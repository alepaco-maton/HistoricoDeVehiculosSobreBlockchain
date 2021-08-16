/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;
 
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; 
import inv.stamina.modulobase.commons.ConvertTool;
import inv.stamina.modulobase.model.Parameter;
import inv.stamina.modulobase.resources.dto.ParameterRequest;
import inv.stamina.modulobase.resources.dto.ParameterResponse;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.validator.ApiException;
import inv.stamina.modulobase.validator.ParameterValidator;
import lombok.extern.log4j.Log4j2;
import inv.stamina.modulobase.repository.IParameterRepository;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value = "/parameters", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ParameterController extends GenericController implements  
        IListController<ParameterResponse>, 
        IUpdateController<ParameterRequest, ParameterResponse, Integer>{

    private static final long serialVersionUID = 647183600973224741L;

    @Autowired
    private IParameterRepository repository;

    @Autowired
    private ParameterValidator validator;

    @Autowired
    private ParameterService parameterService;

    @Override
    public Page<ParameterResponse> list(String token, String ipClient, String form, Pageable pageRequest) {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("request ", pageRequest)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Page<ParameterResponse> out = repository.findAll(pageRequest).
                    map(model -> ConvertTool.convert(model));

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
  
    @Override
    public ResponseEntity<ParameterResponse> update(String token, String ipClient, String form,
            ParameterRequest request, Integer id, BindingResult result) throws ApiException {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            validator.validate(request, id, result);

            if (result.hasErrors()) {
                throw new ApiException(result, "Errores en la validacion");
            }

            Parameter model = repository.findById(id).get();
            model.setName(request.getName());
            model.setType(request.getType());
            model.setValue(request.getValue());
            model.setDescription(request.getDescription());

            model = repository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, "Se modifico:" + model);

            parameterService.restartParameter();

            ResponseEntity<ParameterResponse> out = ResponseEntity.ok().body(ConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            
            final String message = "Error al filtrar roles, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    message);

            throw e;
        }
    }

}
