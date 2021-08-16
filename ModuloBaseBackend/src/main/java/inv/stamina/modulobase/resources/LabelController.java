/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.model.Label;
import inv.stamina.modulobase.resources.dto.LabelRequest;
import inv.stamina.modulobase.resources.dto.LabelResponse;
import inv.stamina.modulobase.commons.ConvertTool; 
import inv.stamina.modulobase.validator.ApiException;
import inv.stamina.modulobase.validator.LabelValidator;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import inv.stamina.modulobase.repository.ILabelRepository; 
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Getter
@Setter
@RestController
@CrossOrigin
@RequestMapping(value = LabelController.RESOURCE, produces = {MediaType.APPLICATION_JSON_VALUE})
public class LabelController extends GenericController implements 
        IListController<LabelResponse>, 
        IUpdateController<LabelRequest, LabelResponse, Integer>{

    public static final String RESOURCE = "/labels";
    public static final String RESOURCE_BY_LLAVE = RESOURCE + "/by/key";
    public static final String RESOURCE_BY_GRUPO = RESOURCE + "/by/group";

    private static final long serialVersionUID = -7116684103790631404L;

    @Autowired
    ILabelRepository repository;

    @Autowired
    private LabelValidator validator;

    @PostMapping("/by/key")
    List<LabelResponse> findAllByKeyIn(@Valid @RequestBody List<String> request) {
        try {
            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            List<LabelResponse> out = repository.findAllByKeyIn(request).stream().map(model -> ConvertTool.convert(model)).collect(Collectors.toList());

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            log.error("Error: " + e.getMessage(), e);

            throw e;
        }
    }

    @PostMapping("/by/group")
    List<LabelResponse> findAllByGroup(@Valid @RequestBody List<String> request) {
        try {
            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            List<LabelResponse> out = repository.findAllByGroupLevelIn(request).stream().map(model -> ConvertTool.convert(model)).collect(Collectors.toList());

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            log.error("Error: " + e.getMessage(), e);

            throw e;
        }
    }

    @Override
    public Page<LabelResponse> list(String token, String ipClient, String form, Pageable pageRequest) {
        final String llave = this.httpServletRequest.getParameter("llave");
        final String grupo = this.httpServletRequest.getParameter("grupo");
        final String valor = this.httpServletRequest.getParameter("valor");

        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("llave ", llave),
                    new AbstractMap.SimpleEntry<>("grupo ", grupo),
                    new AbstractMap.SimpleEntry<>("valor ", valor)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Page<LabelResponse> out = repository.filter(((llave == null || llave.isEmpty()) ? -1 : 0), ((llave == null || llave.trim().isEmpty()) ? "" : "%" + llave.trim().toUpperCase() + "%"),
                    ((valor == null || valor.isEmpty()) ? -1 : 0), ((valor == null || valor.trim().isEmpty()) ? "" : "%" + valor.trim().toUpperCase() + "%"),
                    ((grupo == null || grupo.isEmpty()) ? -1 : 0), ((grupo == null || grupo.trim().isEmpty()) ? "" : "%" + grupo.trim().toUpperCase() + "%"),
                    pageRequest)
                    .map(model -> ConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            log.error("Error: " + e.getMessage(), e);

            throw e;
        }
    }

    @Override
    public ResponseEntity<LabelResponse> update(String token, String ipClient, String form,
            LabelRequest request, Integer id, BindingResult result) throws ApiException {
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
                throw new ApiException(result, "Errores en la validacion");
            }

            Label model = repository.findById(id).get();

            model.setValue(request.getValue());

            model = repository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, "Se modifico:" + model);

            ResponseEntity<LabelResponse> out = ResponseEntity.ok().body(ConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            
            log.error("Error: " + e.getMessage(), e);

            throw e;
        }
    }

}
