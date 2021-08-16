/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;

import inv.stamina.modulobase.model.Parameter;
import inv.stamina.modulobase.resources.dto.ParameterRequest;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.commons.ParametroID;
import inv.stamina.modulobase.commons.ParameterType;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import inv.stamina.modulobase.repository.IParameterRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Component
public class ParameterValidator extends GlobalValidator {

    @Autowired
    IParameterRepository repository;

    @Autowired
    ParameterService parameterService;

    public void validate(ParameterRequest input, Integer id, Errors errors) {
        if (id != null) {
            Optional<Parameter> model = repository.findById(id);

            if (!model.isPresent()) {
                errors.rejectValue("id", "field.invalido", "Identificador de usuario invalido.");
            } else {
                Parameter temp = repository.findByName(input.getName());

                if (temp != null && !temp.getId().equals(model.get().getId())) {
                    errors.rejectValue("name", "field.invalido", "Ya se encuentra en uso.");
                }
            }
        } else {
            Parameter temp = repository.findByName(input.getName());

            if (temp != null) {
                errors.rejectValue("name", "field.invalido", "Ya se encuentra en uso.");
            }
        }

        if (isBlanck(input.getName())) {
            errors.rejectValue("name", "field.name", "La longitud del nombre debe ser mayor a 0 y menor a 255.");
        }
        
        if (input.getValue()!= null && input.getValue().length()>4000) {
            errors.rejectValue("value", "field.value", "La longitud del nombre debe ser menor a 4000.");
        }
        
        if (!ParameterType.esValido(input.getType())) {
            errors.rejectValue("type", "field.type", "Tipo de valor invalido.");
        }

        if (errors.hasErrors()) {
            return;
        }

        if (!input.getName().matches(parameterService.getParametro(ParametroID.EXPRESION_REGULAR_GENERAL).getValue())) {
            errors.rejectValue("name", "field.name", parameterService.getParametro(ParametroID.MENSAJE_VALIDACION_GENERAL).getValue());
        }        
    }

}
