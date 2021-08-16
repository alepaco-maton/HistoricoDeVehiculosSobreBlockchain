/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.resources.dto.RoleResponse;
import inv.stamina.modulobase.resources.dto.RoleRequest;
import inv.stamina.modulobase.commons.Actions;
import inv.stamina.modulobase.model.Role;
import inv.stamina.modulobase.commons.ConvertTool;
import inv.stamina.modulobase.commons.StaminaException;
import inv.stamina.modulobase.util.abm.RoleStatus; 
import inv.stamina.modulobase.validator.ApiException;
import inv.stamina.modulobase.validator.RoleValidator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap; 
import java.util.Map; 
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;
import inv.stamina.modulobase.repository.IRoleRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value = "/roles", produces = {MediaType.APPLICATION_JSON_VALUE})
public class RoleController extends GenericController implements ICrudController<RoleRequest, RoleResponse, Integer> {

    private static final long serialVersionUID = 9081953002167441022L;

    @Autowired
    IRoleRepository repository;

    @Autowired
    RoleValidator validator;

    @Override
    public Page<RoleResponse> list(String token, String ipClient, String form, Pageable pageRequest) {
        try {
            final String nombre = this.httpServletRequest.getParameter("nombre");
            final String descripcion = this.httpServletRequest.getParameter("descripcion");

            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("nombre ", filterText(nombre)),
                    new AbstractMap.SimpleEntry<>("descripcion ", filterText(descripcion)),
                    new AbstractMap.SimpleEntry<>("request ", pageRequest)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Page<RoleResponse> out = repository.filter(((nombre == null || nombre.isEmpty()) ? -1 : 0),
                    ((nombre == null || nombre.trim().isEmpty()) ? "" : "%" + nombre.trim().toUpperCase() + "%"),
                    ((descripcion == null || descripcion.isEmpty()) ? -1 : 0),
                    ((descripcion == null || descripcion.trim().isEmpty()) ? "" : "%" + descripcion.trim().toUpperCase() + "%"),
                    Role.SUPER_ADMINISTRADOR, pageRequest)
                    .map(model -> ConvertTool.convert(model));

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
 
    @Override
    public ResponseEntity<RoleResponse> create(String token, String ipClient, String form,
            RoleRequest request, BindingResult result) throws URISyntaxException, ApiException {

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

            Role model = repository.save(new Role(null, request.getName(), request.getDescription(), RoleStatus.ENABLE));

            bitacoraService.saveBitacora(token, ipClient, form, Actions.ROL_CREAR);

            ResponseEntity<RoleResponse> out = ResponseEntity.created(
                    new URI("/roles/" + model.getId()))
                    .body(ConvertTool.convert(model));

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
    public ResponseEntity<RoleResponse> update(String token, String ipClient, String form,
            RoleRequest request, Integer id, BindingResult result) throws ApiException {
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

            Role model = repository.findById(id).get();

            model.setName(request.getName());
            model.setDescription(request.getDescription());

            model = repository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_MODIFICAR);

            ResponseEntity<RoleResponse> out = ResponseEntity.ok().body(ConvertTool.convert(model));

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

            
            validator.validate(id);
            
            Role model = repository.getOne(id);

            model.setStatus(RoleStatus.DISABLE);

            repository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, Actions.ROL_ELIMINAR);

            ResponseEntity<Object> out = ResponseEntity.ok().build();

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof StaminaException) {
                bitacoraService.saveBitacoraWithoutToken(token, ipClient, form, Actions.ROL_ELIMINAR);
                throw e;
            }
            
            final String mensajeError = "Error al eliminar un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }

}
