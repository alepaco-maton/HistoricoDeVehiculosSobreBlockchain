/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import inv.stamina.modulobase.model.Action;
import inv.stamina.modulobase.model.Form;
import inv.stamina.modulobase.model.RoleAction;
import inv.stamina.modulobase.resources.dto.PermissionRequest;
import inv.stamina.modulobase.resources.dto.jwt.ActionResponse;
import inv.stamina.modulobase.resources.dto.jwt.FormResponse;
import inv.stamina.modulobase.resources.dto.jwt.ModuleResponse;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.service.ParameterService; 
import inv.stamina.modulobase.commons.PrivilegeType;
import inv.stamina.modulobase.model.RoleActionPK;
import inv.stamina.modulobase.validator.ApiException;
import lombok.extern.log4j.Log4j2;
import inv.stamina.modulobase.repository.IActionRepository;
import inv.stamina.modulobase.repository.IFormRepository;
import inv.stamina.modulobase.repository.IRoleActionRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@Service
@CrossOrigin
@RequestMapping(value = "/permissions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class PermissionController extends GenericController {
    private static final long serialVersionUID = 508350232277107075L;

    @Autowired
    IFormRepository formRepository;

    @Autowired
    IActionRepository actionRepository;

    @Autowired
    IRoleActionRepository roleActionRepository;

    @Autowired
    ParameterService parameterService;

    @GetMapping
    List<ModuleResponse> getTemplatePermissions(@RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form) {
        if (ipClient == null || ipClient.isEmpty() || ipClient.equals("127.0.0.1") || ipClient.equals("localhost")) {
            ipClient = httpServletRequest.getRemoteAddr();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------REQUEST----------------------\n").
                append("token ").append(token).append(", \n").
                append("form ").append(form).append(", \n").
                append("ipClient ").append(ipClient).append(", \n").
                append("url ").append(httpServletRequest.getRequestURL()).append(", \n").
                append("metodo ").append(httpServletRequest.getMethod()).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        List<ModuleResponse> modulos = new ArrayList<>();

        for (Form modulo : formRepository.findByModuleIdIsNull(Sort.by(Sort.Direction.ASC, "position"))) {
            List<FormResponse> formularios = new ArrayList<>();

            for (Form formulario : formRepository.findByModuleId(modulo.getId(), Sort.by(Sort.Direction.ASC, "position"))) {
                List<ActionResponse> acciones = new ArrayList<>();

                for (Action accion : actionRepository.findByFormId(formulario.getId())) {

                    acciones.add(new ActionResponse(accion.getId(), accion.getName(), PrivilegeType.PERMISO_TIPO_ACCION));
                }

                formularios.add(new FormResponse(formulario.getId(), formulario.getName(),
                        formulario.getPosition(), PrivilegeType.PERMISO_TIPO_FORMULARIO,
                        formulario.getUrl(), formulario.getIcono(), acciones));
            }

            modulos.add(new ModuleResponse(modulo.getId(), modulo.getName(), modulo.getPosition(),
                    PrivilegeType.PERMISO_TIPO_MODULO, modulo.getUrl(), modulo.getIcono(),
                    formularios));
        }

        sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(token).append(", \n").
                append("DATA ").append(modulos).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return modulos;
    }

    @GetMapping("/role/{roleId}")
    List<ActionResponse> getTemplatePermissions(@RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form, @PathVariable @Min(value = 1, message = "Identificador invalido, no puede ser menor a 1")
            @Max(value = 999999999, message = "Identificador invalido") int roleId) {
        if (ipClient == null || ipClient.isEmpty() || ipClient.equals("127.0.0.1") || ipClient.equals("localhost")) {
            ipClient = httpServletRequest.getRemoteAddr();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------REQUEST----------------------\n").
                append("token ").append(token).append(", \n").
                append("form ").append(form).append(", \n").
                append("ipClient ").append(ipClient).append(", \n").
                append("url ").append(httpServletRequest.getRequestURL()).append(", \n").
                append("metodo ").append(httpServletRequest.getMethod()).append(", \n").
                append("rolId ").append(roleId).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        List<ActionResponse> acciones = new ArrayList<>();

        for (RoleAction rolAccion : roleActionRepository.findAllByRoleId(roleId)) {
            Optional<Action> optinal = actionRepository.findById(rolAccion.getId().getActionId());

            if (optinal.isPresent()) {
                Action accion = optinal.get();
                acciones.add(new ActionResponse(accion.getId(), accion.getName(), PrivilegeType.PERMISO_TIPO_ACCION));
            }
        }

        sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(token).append(", \n").
                append("DATA ").append(acciones).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return acciones;
    }

    @PostMapping
    ResponseEntity<?> savePermissions(@RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @Valid @RequestBody PermissionRequest request) throws URISyntaxException, ApiException {
        if (ipClient == null || ipClient.isEmpty() || ipClient.equals("127.0.0.1") || ipClient.equals("localhost")) {
            ipClient = httpServletRequest.getRemoteAddr();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------REQUEST----------------------\n").
                append("token ").append(token).append(", \n").
                append("form ").append(form).append(", \n").
                append("ipClient ").append(ipClient).append(", \n").
                append("url ").append(httpServletRequest.getRequestURL()).append(", \n").
                append("metodo ").append(httpServletRequest.getMethod()).append(", \n").
                append("request ").append(request).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        roleActionRepository.findAllByRoleId(request.getRoleId()).stream().forEach(ra -> {
            roleActionRepository.delete(ra);
        });

        List<RoleAction> rolAcciones = new ArrayList<>();

        request.getActions().forEach((accionId) -> {
            rolAcciones.add(roleActionRepository.save(new RoleAction(new RoleActionPK(request.getRoleId(), accionId))));
        });

        bitacoraService.saveBitacora(token, ipClient, form, "Actualizando permisos del rol " + request.getRoleId());

        ResponseEntity<Object> out = ResponseEntity.ok().build();

        sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(token).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }


}
