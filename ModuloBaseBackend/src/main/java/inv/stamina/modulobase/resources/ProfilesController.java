/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.commons.Actions;
import inv.stamina.modulobase.commons.StaminaException;
import java.util.Arrays;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import inv.stamina.modulobase.model.User;
import inv.stamina.modulobase.resources.dto.CambioContrasenaRequest;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.commons.UserStatus;
import inv.stamina.modulobase.validator.ApiException;
import inv.stamina.modulobase.validator.UserValidator;
import java.util.AbstractMap; 
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import inv.stamina.modulobase.repository.IUserRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value = "/profiles", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ProfilesController extends GenericController {

    private static final long serialVersionUID = -6548357999333579666L;

    @Autowired
    protected IUserRepository repository;

    @Autowired
    protected UserValidator validator;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/changed/password")
    public ResponseEntity<?> changedPassword(@RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @Valid @RequestBody CambioContrasenaRequest request,
            BindingResult result) throws ApiException {

        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            User model = repository.findByUserNameAndStatusIn(this.jwtTokenUtil.getUsernameFromToken(token),
                    Arrays.asList(UserStatus.HABILITADO, UserStatus.BLOQUEADO));

            validator.validate(model, request, result);

            if (result.hasErrors()) {
                bitacoraService.saveBitacora(token, ipClient, form,
                        Actions.PERFIL_CAMBIAR_CONTRASENA);

                throw new ApiException(result, "Errores en la validacion");
            }

            model.setPassword(this.passwordEncoder.encode(request.getPasswordNew()));

            repository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form,
                    Actions.PERFIL_CAMBIAR_CONTRASENA);

            ResponseEntity<Object> out = ResponseEntity.ok().build();

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            
            final String message = "Error al cambiar la contraseña, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form, message);

            throw e;
        }
    }

}
