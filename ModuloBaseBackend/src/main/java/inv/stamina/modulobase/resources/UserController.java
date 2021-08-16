/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap; 
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; 
import inv.stamina.modulobase.commons.Actions;
import inv.stamina.modulobase.commons.ConvertTool;
import inv.stamina.modulobase.commons.ParametroID;
import inv.stamina.modulobase.commons.UserType;
import inv.stamina.modulobase.model.User;
import inv.stamina.modulobase.resources.dto.UserRequest;
import inv.stamina.modulobase.resources.dto.UserResponse;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.commons.UserStatus;
import inv.stamina.modulobase.validator.ApiException;
import inv.stamina.modulobase.validator.UserValidator;
import lombok.extern.log4j.Log4j2;
import inv.stamina.modulobase.repository.IRoleRepository; 

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController extends GenericController implements 
        IListController<UserResponse>,
        ICreateController<UserRequest, UserResponse>,
        IUpdateController<UserRequest, UserResponse, Integer> {

    private static final long serialVersionUID = 1981251085693864444L;

    @Autowired
    protected UserValidator validator;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private ParameterService parameterService;

    @Override
    public Page<UserResponse> list(String token, String ipClient, String form, Pageable pageRequest) {
        try {
            final String id = this.httpServletRequest.getParameter("id");
            final String nombreCompleto = this.httpServletRequest.getParameter("nombreCompleto");
            final String nombreUsuario = this.httpServletRequest.getParameter("nombreUsuario");
            final String rolId = this.httpServletRequest.getParameter("rolId");
            final String rolNombre = this.httpServletRequest.getParameter("rolNombre");
            final String tipo = this.httpServletRequest.getParameter("tipo");
            final String estado = this.httpServletRequest.getParameter("estado");

            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("id ", filterText(id)),
                    new AbstractMap.SimpleEntry<>("nombreCompleto ", filterText(nombreCompleto)),
                    new AbstractMap.SimpleEntry<>("nombreUsuario ", filterText(nombreUsuario)),
                    new AbstractMap.SimpleEntry<>("rolId ", filterText(rolId)),
                    new AbstractMap.SimpleEntry<>("rolNombre ", filterText(rolNombre)),
                    new AbstractMap.SimpleEntry<>("tipo ", filterText(tipo)),
                    new AbstractMap.SimpleEntry<>("estado ", filterText(estado)),
                    new AbstractMap.SimpleEntry<>("request ", pageRequest)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            String tipoTemporal = "";

            if (tipo != null && !tipo.isEmpty()) {
                if (UserType.USUARIO_NORMAL_VALOR.equals(tipo)) {
                    tipoTemporal = String.valueOf(UserType.USUARIO_NORMAL);
                }
            }

            String estadoTemporal = "";

            if (estado != null && !estado.isEmpty()) {
                if (UserStatus.HABILITADO_VALOR.equals(estado)) {
                    estadoTemporal = String.valueOf(UserStatus.HABILITADO);
                } else if (UserStatus.INHABILITADO_VALOR.equals(estado)) {
                    estadoTemporal = String.valueOf(UserStatus.INHABILITADO);
                } else if (UserStatus.BLOQUEADO_VALOR.equals(estado)) {
                    estadoTemporal = String.valueOf(UserStatus.BLOQUEADO);
                }
            }

            Page<UserResponse> out = userRepository.filter(((id == null || id.trim().isEmpty()) ? -1 : 0),
                    ((id == null || id.trim().isEmpty()) ? "" : id.trim()),
                    ((nombreUsuario == null || nombreUsuario.isEmpty()) ? -1 : 0),
                    ((nombreUsuario == null || nombreUsuario.trim().isEmpty()) ? "" : "%" + nombreUsuario.trim().toUpperCase() + "%"),
                    ((nombreCompleto == null || nombreCompleto.isEmpty()) ? -1 : 0),
                    ((nombreCompleto == null || nombreCompleto.trim().isEmpty()) ? "" : "%" + nombreCompleto.trim().toUpperCase() + "%"),
                    ((rolId == null || rolId.isEmpty()) ? -1 : 0),
                    ((rolId == null || rolId.trim().isEmpty()) ? "" : rolNombre.trim().toUpperCase()),
                    ((rolNombre == null || rolNombre.isEmpty()) ? -1 : 0),
                    ((rolNombre == null || rolNombre.trim().isEmpty()) ? "" : rolNombre.trim().toUpperCase()),
                    ((tipo == null || tipo.isEmpty()) ? -1 : 0),
                    tipoTemporal,
                    ((estado == null || estado.isEmpty()) ? -1 : 0),
                    estadoTemporal,
                    UserType.SUPER_USUARIO, pageRequest)
                    .map(model -> ConvertTool.convert(model));

            bitacoraService.saveBitacora(token, ipClient, form, Actions.FILTRAR);

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out),
                    new AbstractMap.SimpleEntry<>("content ", out.getContent())).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            final String mensajeError = "Error al filtrar usuarios, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }

    @Override
    public ResponseEntity<UserResponse> create(String token, String ipClient, String form,
            UserRequest request, BindingResult result) throws URISyntaxException, ApiException {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            validator.validate(request, null, result);

            if (result.hasErrors()) {
                bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_CREAR);

                throw new ApiException(result, "Errores en la validacion");
            }

            User model = userRepository.save(new User(null, request.getUserName(), request.getFullName(),
                    (String) parameterService.getParamVal(ParametroID.CONTRASENA_POR_DEFECTO),
                    UserType.USUARIO_NORMAL,
                    roleRepository.findById(request.getRoleId()).get(),
                    UserStatus.HABILITADO));

            bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_CREAR);

            ResponseEntity<UserResponse> out = ResponseEntity.created(new URI("/usuarios/" + model.getId()))
                    .body(ConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            
            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }

    @Override
    public ResponseEntity<UserResponse> update(String token, String ipClient, String form,
            UserRequest request, Integer id, BindingResult result) throws ApiException {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("id ", id),
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            validator.validate(request, id, result);

            if (result.hasErrors()) {
                bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_MODIFICAR);

                throw new ApiException(result, "Errores en la validacion");
            }

            User model = userRepository.findById(id).get();
            model.setRoleId(roleRepository.findById(request.getRoleId()).get());
            model.setFullName(request.getFullName());
            model.setUserName(request.getUserName());

            model = userRepository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_MODIFICAR);

            ResponseEntity<UserResponse> out = ResponseEntity.ok().body(ConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            
            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }
 
    @PutMapping("/disable")
    ResponseEntity<?> disable(@RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @RequestBody String id) { 

        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("id ", id)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Integer idusuario = Integer.parseInt(id);
            Optional<User> optional = userRepository.findById(idusuario);

            User model = optional.get();

            model.setStatus(UserStatus.INHABILITADO);
            model = userRepository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_INHABILITAR);

            ResponseEntity<Object> out = ResponseEntity.ok().build();

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }

    @PutMapping("/enable")
    ResponseEntity<?> enable(@RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @RequestBody String id) {

        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("id ", id)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Integer idusuario = Integer.parseInt(id);
            Optional<User> optional = userRepository.findById(idusuario);

            User model = optional.get();

            model.setStatus(UserStatus.HABILITADO);
            model = userRepository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_HABILITAR);

            ResponseEntity<Object> out = ResponseEntity.ok().build();

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }

}
