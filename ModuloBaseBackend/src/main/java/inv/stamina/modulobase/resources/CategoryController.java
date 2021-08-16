/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.commons.AppMovilConvertTool;
import inv.stamina.modulobase.model.Category;
import inv.stamina.modulobase.repository.ICategoryRepository;
import inv.stamina.modulobase.repository.IPictogramShortcutRepository;
import inv.stamina.modulobase.resources.dto.CategoryRequest;
import inv.stamina.modulobase.resources.dto.CategoryResponse;
import inv.stamina.modulobase.validator.CategoryValidator;
import inv.stamina.modulobase.commons.Actions;
import inv.stamina.modulobase.model.Account;
import inv.stamina.modulobase.repository.IAccountRepository;
import inv.stamina.modulobase.resources.dto.CategoryPSResponse;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.util.abm.AccountStatus;
import inv.stamina.modulobase.validator.ApiException; 
import java.net.URI; 
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value = "/categories", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CategoryController extends GenericController implements 
        ICreateController<CategoryRequest, CategoryResponse>, 
        IUpdateController<CategoryRequest, CategoryResponse, Integer>, 
        IDeleteController<Integer> {

    private static final long serialVersionUID = 9081953002167441022L;

    @Autowired
    ICategoryRepository repository;

    @Autowired
    IPictogramShortcutRepository pictogramShortcutRepository;

    @Autowired
    CategoryValidator validator;

    @Autowired
    IAccountRepository accountRepository;
 
    @GetMapping("/by/type")
    public List<CategoryResponse> listCategoriesByType(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form, Pageable pageRequest) {
        try {
            final short type = Short.parseShort(this.httpServletRequest.getHeader("type"));

            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form), 
                    new AbstractMap.SimpleEntry<>("request ", pageRequest)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Account account = accountRepository.findByUserNameAndStatusIn(getUserName(), Arrays.asList(AccountStatus.ENABLE));

            List<CategoryResponse> out = repository.findAllByAccountIdAndType(account.getId(), type).stream()
                    .map(model -> AppMovilConvertTool.convert(model)).collect(Collectors.toList());

            bitacoraService.saveBitacora(token, ipClient, form, Actions.FILTRAR);

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("content ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            final String message = "Error al filtrar roles, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    message);

            throw e;
        }
    }
 
    @Override
    public ResponseEntity<CategoryResponse> create(String token, String ipClient, String form,
            CategoryRequest request, BindingResult result) throws ApiException, Exception {
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
                bitacoraService.saveBitacora(token, ipClient, form, getErrors(result));

                throw new ApiException(result, "Errores en la validacion");
            }

            Account acccount = accountRepository.findByUserNameAndStatusIn(getUserName(), Arrays.asList(AccountStatus.ENABLE));

            Category model = repository.save(new Category(null,
                    request.getName(), request.getDescription(),
                    request.getType(), acccount.getId()));

            bitacoraService.saveBitacora(token, ipClient, form, Actions.ROL_CREAR);

            ResponseEntity<CategoryResponse> out = ResponseEntity.created(
                    new URI("/roles/" + model.getId()))
                    .body(AppMovilConvertTool.convert(model));

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
    public ResponseEntity<CategoryResponse> update(String token, String ipClient, String form,
            CategoryRequest request, Integer id, BindingResult result) throws ApiException {
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
                bitacoraService.saveBitacora(token, ipClient, form, getErrors(result));

                throw new ApiException(result, "Errores en la validacion");
            }

            Account acccount = accountRepository.findByUserNameAndStatusIn(getUserName(), Arrays.asList(AccountStatus.ENABLE));

            Category model = repository.findById(id).get();

            model.setName(request.getName());
            model.setDescription(request.getDescription());
            model.setType(request.getType());
            model.setAccountId(acccount.getId());

            model = repository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_MODIFICAR);

            ResponseEntity<CategoryResponse> out = ResponseEntity.ok().
                    body(AppMovilConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }

            final String mensajeError = "Error al modificar un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }

    @Override
    public ResponseEntity<?> delete(String token, String ipClient, String form,
            Integer id) throws NoHandlerFoundException, Exception {

        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("id ", id)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Optional<Category> temp = repository.findById(id);

            if (!temp.isPresent()) {
                throw new NoHandlerFoundException("DELETE", "/{id}" + id, HttpHeaders.EMPTY);
            }

            Category model = temp.get();

            pictogramShortcutRepository.findAllByCategoryId(model.getId()).
                    forEach(ps -> {
                        pictogramShortcutRepository.delete(ps);
                    });

            repository.delete(model);

            bitacoraService.saveBitacora(token, ipClient, form, Actions.ROL_ELIMINAR);

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

            final String mensajeError = "Error al eliminar un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }

    @GetMapping("/pictograms/shortcuts")
    List<CategoryPSResponse> listCategoriesPictogramsShortcuts(
            @RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @RequestHeader(value = "type") String type) {

        try {
            if (ipClient == null || ipClient.isEmpty() || ipClient.equals("127.0.0.1") || ipClient.equals("localhost")) {
                ipClient = httpServletRequest.getRemoteAddr();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("-------------------REQUEST----------------------\n").
                    append("token ").append(token).append(", \n").
                    append("form ").append(form).append(", \n").
                    append("ipClient ").append(ipClient).append(", \n").
                    append("type ").append(type).append(", \n").
                    append("url ").append(httpServletRequest.getRequestURL()).append(", \n").
                    append("metodo ").append(httpServletRequest.getMethod()).append(", \n").
                    append("------------------------------------------------\n");

            log.info(sb.toString());

            Account model = accountRepository.findByUserNameAndStatusIn(
                    jwtTokenUtil.getUsernameFromToken(token), Arrays.asList(AccountStatus.ENABLE));

            //CategoryType.CATEGORY_PICTOGRAM
            List<CategoryPSResponse> categories = repository.findAllByAccountIdAndType(model.getId(),
                    Short.valueOf(type)).stream().map(category -> {
                return new CategoryPSResponse(category.getId(),
                        category.getName(), category.getDescription(), category.getType(),
                        pictogramShortcutRepository.findAllByCategoryId(category.getId()).stream().
                        map(s -> AppMovilConvertTool.convert(s)).
                        collect(Collectors.toList()));
            }).collect(Collectors.toList());

            bitacoraService.saveBitacora(token, ipClient, form, Actions.FILTRAR);

            sb = new StringBuilder();
            sb.append("-------------------RESPONSE----------------------\n").
                    append("token ").append(token).append(", \n").
                    append("DATA ").append(categories).append(", \n").
                    append("------------------------------------------------\n");

            log.info(sb.toString());

            return categories;
        } catch (Exception e) {
            final String message = "Error al filtrar roles, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    message);

            throw e;
        }
    }

}
