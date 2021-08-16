/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.security.jwt;

import inv.stamina.modulobase.commons.Actions;
import inv.stamina.modulobase.model.Action;
import inv.stamina.modulobase.model.Form;
import inv.stamina.modulobase.model.Role;
import inv.stamina.modulobase.model.RoleAction;
import inv.stamina.modulobase.resources.GenericController;
import inv.stamina.modulobase.resources.dto.jwt.ActionResponse;
import inv.stamina.modulobase.resources.dto.jwt.FormResponse;
import inv.stamina.modulobase.resources.dto.jwt.ModuleResponse;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.resources.dto.jwt.ExceptionResponse;
import inv.stamina.modulobase.resources.dto.jwt.AuteticacionResponse;
import inv.stamina.modulobase.resources.dto.jwt.AuteticationRequest;
import inv.stamina.modulobase.commons.PrivilegeType;
import inv.stamina.modulobase.model.Account;
import inv.stamina.modulobase.repository.IAccountRepository;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import inv.stamina.modulobase.repository.IActionRepository;
import inv.stamina.modulobase.repository.IFormRepository;
import inv.stamina.modulobase.repository.IRoleActionRepository;
import inv.stamina.modulobase.util.abm.AccountStatus;
import java.util.Arrays;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
public class JwtAuthenticationController extends GenericController implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String METODO_VERSION = "/version";
    public static final String METODO_AUTENTICACION = "/autentication";

    @Autowired
    private AuthenticationSecurity authenticationManager;

    @Autowired
    private IFormRepository formRepository;

    @Autowired
    private IRoleActionRepository roleActionRepository;

    @Autowired
    private IActionRepository actionRepository;

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    ParameterService parameterService;

    @RequestMapping(value = METODO_VERSION, method = RequestMethod.GET)
    public ResponseEntity<?> version() {
        HashMap<String, String> map = new HashMap();
        map.put("Version", "Version 1.0");

        return ResponseEntity.ok(map.toString());
    }

    @RequestMapping(value = METODO_AUTENTICACION, method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @Valid @RequestBody AuteticationRequest authenticationRequest, BindingResult result) {

        try {
            log.info("validando autenticacion "+authenticationRequest);
            
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("request ", authenticationRequest)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            if (!isBlanck(authenticationRequest.getSource()) && "appMovil".equals(authenticationRequest.getSource())) {
                String msg = authenticationManager.validarApp(authenticationRequest.getUserName(),
                        authenticationRequest.getPassword());

                if (!msg.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ExceptionResponse(
                            HttpStatus.BAD_REQUEST, "El usuario o contraseña ingresados son incorrectos",
                            "El usuario o contraseña ingresados son incorrectos"));
                }

                final String token = jwtTokenUtil.generateToken(authenticationRequest.getUserName(), "appMovil");

                bitacoraService.saveBitacora("Bearer " + token, ipClient, form,
                        Actions.AUTENTICACION);

                Account model = accountRepository.findByUserNameAndStatusIn(authenticationRequest.getUserName(), Arrays.asList(AccountStatus.ENABLE));

                ResponseEntity<AuteticacionResponse> out = ResponseEntity.ok(new AuteticacionResponse(
                        token, 0, "",
                        null, model.getEmail()));

                printResponse(Stream.of(
                        new AbstractMap.SimpleEntry<>("trazabilidad ", authenticationRequest.getUserName()),
                        new AbstractMap.SimpleEntry<>("response ", out)).
                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

                return out;
            }

            Role rol = null;

            try {
                rol = authenticationManager.validar(authenticationRequest.getUserName(),
                        authenticationRequest.getPassword());
            } catch (BadCredentialsException | InsufficientAuthenticationException | AccountStatusException e) {
                bitacoraService.saveBitacora(null, ipClient, form, Actions.AUTENTICACION);

                return ResponseEntity.badRequest().body(new ExceptionResponse(
                        HttpStatus.BAD_REQUEST, "Error procesamiento", e.getMessage()));
            }

            final String token = jwtTokenUtil.generateToken(authenticationRequest.getUserName(), rol.getName());

            List<ModuleResponse> modulos = new ArrayList<>();
            List<Integer> formularios = new ArrayList<>();

            for (RoleAction rolAccion : roleActionRepository.findAllByRoleId(rol.getId())) {
                if (1701 == rolAccion.getId().getActionId() && rol.getId() != Role.SUPER_ADMINISTRADOR) {
                    continue;
                }

                Action accion = actionRepository.getOne(rolAccion.getId().getActionId());

                Optional<Form> optionalForm = formRepository.findById(accion.getFormId());

                Form formulario = optionalForm.get();

                if (!formularios.stream().anyMatch(id -> id.equals(accion.getFormId()))) {
                    formularios.add(accion.getFormId());

                    Optional<Form> optionalMod = formRepository.findById(formulario.getModuleId());

                    Form modulo = optionalMod.get();

                    List<ActionResponse> tempAcciones = new ArrayList<>();
                    tempAcciones.add(new ActionResponse(accion.getId(), accion.getName(), PrivilegeType.PERMISO_TIPO_ACCION));

                    FormResponse tempFormulario = new FormResponse(formulario.getId(),
                            formulario.getName(), formulario.getPosition(),
                            PrivilegeType.PERMISO_TIPO_FORMULARIO, formulario.getUrl(), formulario.getIcono(), tempAcciones);

                    Optional<ModuleResponse> optionalModuloRespo = modulos.stream().
                            filter(m -> m.getId() == formulario.getModuleId()).findFirst();

                    ModuleResponse moduloResponse;

                    if (optionalModuloRespo.isPresent()) {
                        moduloResponse = optionalModuloRespo.get();
                    } else {
                        moduloResponse = new ModuleResponse(modulo.getId(), modulo.getName(), modulo.getPosition(),
                                PrivilegeType.PERMISO_TIPO_MODULO, modulo.getUrl(), modulo.getIcono(), new ArrayList<>());

                        modulos.add(moduloResponse);
                    }
                    moduloResponse.getForms().add(tempFormulario);

                } else {
                    ModuleResponse moduloResponse = modulos.stream().
                            filter(m -> m.getId() == formulario.getModuleId()).findFirst().get();

                    Optional<FormResponse> optionalFormRes = moduloResponse.getForms().
                            stream().filter(f -> f.getId() == formulario.getId()).findFirst();
                    optionalFormRes.get().getActions().add(new ActionResponse(accion.getId(), accion.getName(),
                            PrivilegeType.PERMISO_TIPO_ACCION));
                }
//                }
            }

            Collections.sort(modulos);

            modulos.forEach(m
                    -> {
                m.sortFormularios();
            }
            );

            bitacoraService.saveBitacora("Bearer " + token, ipClient, form,
                    Actions.AUTENTICACION);

            ResponseEntity<AuteticacionResponse> out = ResponseEntity.ok(new AuteticacionResponse(
                    token, rol.getId(), rol.getName(),
                    modulos, ""));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", authenticationRequest.getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            final String mensajeError = "Error al cambiar la contraseña, " + e.getMessage();

            bitacoraService.saveBitacora(null, ipClient, form,
                    mensajeError);

            throw e;
        }
    }

}
