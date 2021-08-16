/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;

import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import inv.stamina.modulobase.model.Role;
import inv.stamina.modulobase.model.User;
import inv.stamina.modulobase.resources.dto.CambioContrasenaRequest;
import inv.stamina.modulobase.resources.dto.UserRequest;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.commons.ParametroID;
import inv.stamina.modulobase.commons.UserType;
import inv.stamina.modulobase.commons.UserStatus;
import lombok.extern.log4j.Log4j2;
import inv.stamina.modulobase.repository.IRoleRepository;
import inv.stamina.modulobase.repository.IUserRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Component
public class UserValidator extends GlobalValidator {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    IRoleRepository roleRepository;

    @Autowired
    ParameterService parameterService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public void validate(UserRequest input, Integer id, Errors errors) {
        if (isBlanck(input.getFullName())) {
            errors.rejectValue("fullName", "field.fullName", "El número de caracteres del nombre completo debe ser mayor a 0 y menor o igual a 255.");
            return;
        } 
        
        if (isBlanck(input.getUserName())) {
            errors.rejectValue("userName", "field.userName", "El número de caracteres del nombre de usuario debe ser mayor a 0 y menor o igual a 50.");
            return;
        }

        if (!input.getUserName().matches(parameterService.getParametro(ParametroID.EXPRESION_REGULAR_NOMBRE_USUARIO).getValue())) {
            errors.rejectValue("userName", "field.userName", parameterService.getParametro(ParametroID.MENSAJE_VALIDACION_NOMBRE_USUARIO).getValue());
            return;
        }

        if (id != null) {
            Optional<User> model = userRepository.findById(id);

            if (!model.isPresent()) {
                errors.rejectValue("userName", "field.userName", "Identificador de usuario invalido.");
            } else {
                User temp = userRepository.findByUserNameAndStatusIn(input.getUserName(),
                        Arrays.asList(UserStatus.HABILITADO, UserStatus.BLOQUEADO));

                if (temp != null && !temp.getId().equals(model.get().getId())) {
                    errors.rejectValue("nombreUsuario", "field.nombreUsuario", "El nombre de usuario, se encuentra en uso.");
                    return;
                }
            }
        } else {
            User temp = userRepository.findByUserNameAndStatusIn(input.getUserName(),
                    Arrays.asList(UserStatus.HABILITADO, UserStatus.BLOQUEADO));

            if (temp != null) {
                errors.rejectValue("userName", "field.userName", "El nombre de usuario, se encuentra en uso.");
                return;
            }
        }

        if (input.getRoleId() == null) {
            errors.rejectValue("roleId", "field.roleId", "Seleccione un rol.");
            return;
        }

        Optional<Role> model = roleRepository.findById(input.getRoleId());
        if (!model.isPresent()) {
            errors.rejectValue("roleId", "field.roleId", "Rol invalido, seleccione otro rol.");
        }
    }

    public void validate(User model, CambioContrasenaRequest input, Errors errors) {
        if (isBlanck(input.getPasswordOld())) {
            errors.rejectValue("passwordOld", "field.passwordOld", "La contraseña actual es requerido.");
            return;
        }

        if (input.getPasswordOld().length() > 50) {
            errors.rejectValue("passwordOld", "field.passwordOld", "El número de caracteres de la contraseña actual debe ser mayor a 0 y menor o igual a 50.");
            return;
        }

        if (isBlanck(input.getPasswordNew())) {
            errors.rejectValue("passwordNew", "field.passwordNew", "La contraseña nueva es requerida.");
            return;
        }

        if (input.getPasswordNew().length() > 50) {
            errors.rejectValue("passwordNew", "field.passwordNew", "El número de caracteres de la contraseña nueva debe ser mayor a 0 y menor a 50.");
            return;
        }

        if (model == null) {
            errors.rejectValue("passwordNew", "field.passwordNew", "Usuario invalido.");
            return;
        }

        if (UserType.SUPER_USUARIO != model.getType() && UserType.USUARIO_NORMAL != model.getType()) {
            errors.rejectValue("passwordNew", "field.passwordNew", "Tipo de usuario no puede cambiar de contraseña.");
            return;
        }

        if (!passwordEncoder.matches(input.getPasswordOld(), model.getPassword())) {
            errors.rejectValue("passwordOld", "field.passwordOld", "Contraseña actual incorrecta.");
        }
    }

}
