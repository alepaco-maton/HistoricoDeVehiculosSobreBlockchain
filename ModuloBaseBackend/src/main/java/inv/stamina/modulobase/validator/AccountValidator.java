/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;

import inv.stamina.modulobase.commons.ParametroID;
import inv.stamina.modulobase.model.Account;
import inv.stamina.modulobase.repository.IAccountRepository;
import inv.stamina.modulobase.resources.dto.AccountRequest;
import inv.stamina.modulobase.resources.dto.VerifyCodeRequest;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.util.abm.AccountStatus;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Component
public class AccountValidator extends GlobalValidator {

    @Autowired
    IAccountRepository repository;

    @Autowired
    ParameterService parameterService;

    public void validate(AccountRequest input, Integer id, Errors errors) {
        if (isBlanck(input.getEmail())) {
            errors.rejectValue("email", "field.email", "Email es requerido.");
            return;
        }

        if (isBlanck(input.getUserName())) {
            errors.rejectValue("userName", "field.userName", "El nombre de usuario es requerido.");
            return;
        }

        if (input.getUserName().length() > 50) {
            errors.rejectValue("userName", "field.userName", "El número de caracteres del nombre de usuario debe ser mayor a 0 y menor o igual a 50.");
            return;
        }

        if (isBlanck(input.getFullName())) {
            errors.rejectValue("fullName", "field.fullName", "Nombre completo es requerido.");
            return;
        }

        if (input.getFullName().length() > 255) {
            errors.rejectValue("fullName", "field.fullName", "El número de caracteres del nombre completo debe ser mayor a 0 y menor o igual a 255.");
            return;
        }

        if (!input.getUserName().matches(parameterService.getParametro(ParametroID.EXPRESION_REGULAR_NOMBRE_USUARIO).getValue())) {
            errors.rejectValue("userName", "field.userName", parameterService.getParametro(ParametroID.MENSAJE_VALIDACION_NOMBRE_USUARIO).getValue());
            return;
        }

        if (id != null) {
            Optional<Account> model = repository.findById(id);

            if (!model.isPresent()) {
                errors.rejectValue("userName", "field.userName", "Identificador de usuario invalido.");
            } else {
                Account temp = repository.findByUserNameAndStatusIn(
                        input.getUserName(), Arrays.asList(AccountStatus.ENABLE));

                if (temp != null && !temp.getId().equals(model.get().getId())) {
                    errors.rejectValue("userName", "field.userName", "El nombre de usuario, se encuentra en uso.");
                    return;
                }
            }
        } else {
            Account account = repository.findByUserNameAndStatusIn(
                    input.getUserName(), Arrays.asList(AccountStatus.ENABLE));

            if (account != null) {
                errors.rejectValue("userName", "field.userName", "El nombre de usuario, se encuentra en uso.");
                return;
            }
        }

        if (id != null) {
            Optional<Account> model = repository.findById(id);

            if (!model.isPresent()) {
                errors.rejectValue("email", "field.email", "Email invalido.");
            } else {
                Account temp = repository.findByEmail(input.getEmail());

                if (temp != null && !temp.getId().equals(model.get().getId())) {
                    errors.rejectValue("email", "field.email", "Cuenta de email ya registrado.");
                    return;
                }
            }
        } else {
            Account account = repository.findByUserNameAndStatusIn(
                    input.getUserName(), Arrays.asList(AccountStatus.ENABLE));

            if (account != null) {
                errors.rejectValue("email", "field.email", "Cuenta de email ya registrado.");
                return;
            }
        }

        {
            Pattern pattern = Pattern
                    .compile((String) parameterService.getParamVal(ParametroID.REGEX_VALIDATE_EMAIL));

            Matcher mather = pattern.matcher(input.getEmail());

            if (!mather.find()) {
                errors.rejectValue("email", "field.email", "Email invalido.");
                return;
            }
        }

    }

    public String validate(String email) {
        if (isBlanck(email)) {
            return "Correo electronico es requerido.";
        }

        Account model = repository.findByEmail(email);

        if (model == null) {
            return "Cuenta no registrada.";
        }

        return "";
    }

    public void validate(VerifyCodeRequest request, BindingResult errors) {
        if (isBlanck(request.getPassword())) {
            errors.rejectValue("password", "field.password", "Contrasena es requerido.");
            return;
        }

        if (isBlanck(request.getEmail())) {
            errors.rejectValue("email", "field.email", "Correo electronico es requerido.");
            return;
        }

        if (isBlanck(request.getCode())) {
            errors.rejectValue("code", "field.code", "Codigo es requerido.");
            return;
        }

        Account model = repository.findByEmailAndVerifyCode(request.getEmail(), request.getCode());

        if (model == null) {
            errors.rejectValue("code", "field.code", "Codigo invalido.");
        }
    }

}
