/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.commons.AppMovilConvertTool;
import inv.stamina.modulobase.model.Account;
import inv.stamina.modulobase.repository.IAccountRepository;
import inv.stamina.modulobase.resources.dto.AccountRequest;
import inv.stamina.modulobase.resources.dto.AccountResponse;
import inv.stamina.modulobase.util.abm.AccountStatus;
import inv.stamina.modulobase.validator.AccountValidator;
import inv.stamina.modulobase.commons.Actions;
import inv.stamina.modulobase.commons.EmailNotifier;
import inv.stamina.modulobase.commons.ParametroID; 
import inv.stamina.modulobase.commons.StaminaException;
import inv.stamina.modulobase.commons.SymmetricEncoder; 
import inv.stamina.modulobase.resources.dto.VerifyCodeRequest;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.service.InicializationService;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.validator.ApiException; 
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap; 
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController; 

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value = "/accounts", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController extends GenericController 
        implements IListController<AccountResponse> {

    private static final long serialVersionUID = 9081953002167441022L;

    @Autowired
    IAccountRepository repository;

    @Autowired
    AccountValidator validator;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    ParameterService parameterService;

    @Override
    public Page<AccountResponse> list(String token, String ipClient, String form, Pageable pageRequest) {
        try { 
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    //                    new AbstractMap.SimpleEntry<>("nombre ", filterText(nombre)),
                    new AbstractMap.SimpleEntry<>("request ", pageRequest)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Page<AccountResponse> out = repository.findAll(pageRequest)
                    .map(model -> AppMovilConvertTool.convert(model));

            bitacoraService.saveBitacora(token, ipClient, form, Actions.FILTRAR);

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out),
                    new AbstractMap.SimpleEntry<>("content ", out.getContent())).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            final String message = "Error al filtrar roles, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    message);

            throw e;
        }
    }
  
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<AccountResponse> register(
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @Valid @RequestBody AccountRequest request, BindingResult result) throws URISyntaxException, ApiException {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            validator.validate(request, null, result);

            if (result.hasErrors()) {
                bitacoraService.saveBitacoraWithoutToken("app", ipClient, form, getErrors(result));

                throw new ApiException(result, "Errores en la validacion");
            }

            Account model = repository.save(new Account(null,
                    request.getEmail(),
                    request.getUserName(), request.getFullName(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getTypeDevice(), AccountStatus.ENABLE, null));

            bitacoraService.saveBitacoraWithoutToken(request.getUserName(), ipClient, form, Actions.USUARIO_CREAR);

            
            
            
            ResponseEntity<AccountResponse> out = ResponseEntity.created(
                    new URI("/accounts/" + model.getId()))
                    .body(AppMovilConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", request.getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }

            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacoraWithoutToken("app", ipClient, form,
                    mensajeError);

            throw e;
        }
    }

    @PostMapping("/recover/password")
    public ResponseEntity<?> recoverPassword(
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @RequestHeader("email") String email) throws Exception {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("email ", email)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            String error = validator.validate(email);

            if (!isBlanck(error)) {
                bitacoraService.saveBitacoraWithoutToken("app", ipClient, form, error);

                throw new StaminaException(error);
            }

            Account model = repository.findByEmail(email);

            model.setVerifyCode(UUID.randomUUID().toString().substring(0, 8));

            log.info("emai "+(String) parameterService.getParamVal(
                            ParametroID.ServerSMTP.SMTP_FROM));
            log.info("passw "+SymmetricEncoder.AESDecode(
                                    InicializationService.RULES_ENCODE, 
                                    (String) parameterService.getParamVal(
                            ParametroID.ServerSMTP.SMTP_PASSWORD)));
            log.info("template "+((String) parameterService.getParamVal(
                            ParametroID.EMAIL_NOTIFIER_TEMPLATE_MESSAGE)).replace("[name]", model.getFullName()).
                    replace("[code]", model.getVerifyCode()));
            EmailNotifier.sendEmail((String) parameterService.getParamVal(
                    ParametroID.ServerSMTP.SMTP_HOST), ((BigDecimal) parameterService.getParamVal(
                            ParametroID.ServerSMTP.SMTP_PORT)).intValue(), (String) parameterService.getParamVal(
                            ParametroID.ServerSMTP.SMTP_FROM), SymmetricEncoder.AESDecode(
                                    InicializationService.RULES_ENCODE, 
                                    (String) parameterService.getParamVal(
                            ParametroID.ServerSMTP.SMTP_PASSWORD)), email,
                    (String) parameterService.getParamVal(ParametroID.EMAIL_NOTIFIER_SUBJECT),
                    ((String) parameterService.getParamVal(
                            ParametroID.EMAIL_NOTIFIER_TEMPLATE_MESSAGE)).replace("[name]", model.getFullName()).
                    replace("[code]", model.getVerifyCode()));
  
            repository.save(model);
            
            bitacoraService.saveBitacoraWithoutToken(email, ipClient, form, Actions.USUARIO_RESETEAR_CONTRASENA);
            
            ResponseEntity<Object> out = ResponseEntity.ok().build();

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", "app"),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof StaminaException) {
                throw e;
            }

            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacoraWithoutToken("app", ipClient, form,
                    mensajeError);

            throw e;
        }
    }

    @PostMapping("/recover/password/verify/code")
    public ResponseEntity<?> verifyCode(
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @RequestBody VerifyCodeRequest request, BindingResult result) throws ApiException {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            validator.validate(request, result);

            if (result.hasErrors()) {
                bitacoraService.saveBitacora("app", ipClient, form, getErrors(result));

                throw new ApiException(result, "Errores en la validacion");
            }

            Account model = repository.findByEmailAndVerifyCode(request.getEmail(), request.getCode());

            model.setVerifyCode(null);
            model.setPassword(passwordEncoder.encode(request.getPassword()));

            repository.save(model);
            
            bitacoraService.saveBitacoraWithoutToken(model.getUserName(), ipClient, form, Actions.USUARIO_RESETEAR_CONTRASENA);
            
            ResponseEntity<Object> out = ResponseEntity.ok().build();

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", "app"),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }

            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacoraWithoutToken("app", ipClient, form,
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

            Integer idAccount = Integer.parseInt(id);
            Optional<Account> optional = repository.findById(idAccount);

            Account model = optional.get();

            model.setStatus(AccountStatus.DISABLE);
            model = repository.save(model);

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

            Integer idAccount = Integer.parseInt(id);
            Optional<Account> optional = repository.findById(idAccount);

            Account model = optional.get();

            model.setStatus(AccountStatus.ENABLE);
            model = repository.save(model);

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
