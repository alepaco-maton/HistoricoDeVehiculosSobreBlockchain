/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;

import inv.stamina.modulobase.model.Role;
import inv.stamina.modulobase.resources.dto.RoleRequest;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.commons.ParametroID;
import inv.stamina.modulobase.commons.StaminaException;
import inv.stamina.modulobase.commons.UserStatus;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import inv.stamina.modulobase.repository.IRoleRepository;
import inv.stamina.modulobase.repository.IUserRepository; 
import java.util.Arrays;
import org.springframework.http.HttpHeaders; 
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Component
public class RoleValidator extends GlobalValidator {

    @Autowired
    IRoleRepository repository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    ParameterService parameterService;

    public void validate(RoleRequest input, Integer id, Errors errors) {
        if (isBlanck(input.getName())) {
            errors.rejectValue("name", "field.name", "El nombre del es requerido.");
            return;
        }

        if (input.getName().length() > 50) {
            errors.rejectValue("name", "field.name", "El número de caracteres del nombre debe ser mayor a 0 y menor o igual a 50.");
            return;
        }

        if (!input.getName().matches(parameterService.getParametro(ParametroID.EXPRESION_REGULAR_GENERAL).getValue())) {
            errors.rejectValue("name", "field.name", parameterService.getParametro(ParametroID.MENSAJE_VALIDACION_GENERAL).getValue());
            return;
        }

        if (input.getDescription() != null && input.getDescription().length() > 200) {
            errors.rejectValue("description", "field.description", "El número de caracteres de la descripción debe ser mayor a 0 y menor o igual a 200.");
            return;
        }

        if (id != null) {
            Optional<Role> model = repository.findById(id);

            if (!model.isPresent()) {
                errors.rejectValue("id", "field.invalido", "Identificador de usuario invalido.");
            } else {
                Role temp = repository.findByNameAndStatusTrue(input.getName());

                if (temp != null && !temp.getId().equals(model.get().getId())) {
                    errors.rejectValue("name", "field.invalido", "El nombre del rol, se encuentra en uso.");
                }
            }
        } else {
            Role temp = repository.findByNameAndStatusTrue(input.getName());

            if (temp != null) {
                errors.rejectValue("name", "field.invalido", "El nombre del rol, se encuentra en uso.");
            }
        }
    }

    public void  validate(Integer id) throws NoHandlerFoundException {
        Optional<Role> temp = repository.findById(id);

        if (!temp.isPresent()) {
            throw new NoHandlerFoundException("DELETE", "/{id}" + id, HttpHeaders.EMPTY);
        }
        
        long numeroUsuariosConRol = userRepository.countByStatusInAndRoleIdId(Arrays.asList(UserStatus.HABILITADO, UserStatus.BLOQUEADO), id);

        if (numeroUsuariosConRol > 0) {
            throw new StaminaException("No puede ser eliminado porque esta en uso por almenos un usuario");
        }
    }

}
