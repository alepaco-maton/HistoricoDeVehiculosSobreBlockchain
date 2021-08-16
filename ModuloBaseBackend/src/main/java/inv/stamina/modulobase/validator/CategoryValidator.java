/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;
 
import inv.stamina.modulobase.model.Category;
import inv.stamina.modulobase.repository.ICategoryRepository;
import inv.stamina.modulobase.resources.dto.CategoryRequest;
import inv.stamina.modulobase.service.ParameterService;  
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Component
public class CategoryValidator extends GlobalValidator {

    @Autowired
    ICategoryRepository repository;

    @Autowired
    ParameterService parameterService;

    public void validate(CategoryRequest input, Integer id, Errors errors) {
        if (isBlanck(input.getName())) {
            errors.rejectValue("name", "field.name", "El nombre es requerido");
            return;
        }
  
        if (id != null) {
            Optional<Category> model = repository.findById(id);

            if (!model.isPresent()) {
                errors.rejectValue("name", "field.name", "Identificador de usuario invalido.");
            } else {
                Category temp = repository.findByName(input.getName());

                if (temp != null && !temp.getId().equals(model.get().getId())) {
                    errors.rejectValue("name", "field.name", "El nombre, se encuentra en uso.");
                }
            }
        } else {
            Category temp = repository.findByName(input.getName());

            if (temp != null) {
                errors.rejectValue("name", "field.name", "El nombre, se encuentra en uso.");
            }
        }
    }

}
