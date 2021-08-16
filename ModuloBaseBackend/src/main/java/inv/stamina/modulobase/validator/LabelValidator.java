/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;

import inv.stamina.modulobase.resources.dto.LabelRequest;
import lombok.extern.log4j.Log4j2; 
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors; 
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Getter
@Setter
@Component
public class LabelValidator extends GlobalValidator {

    public void validate(LabelRequest input, Integer id, Errors errors) {
    }

}
