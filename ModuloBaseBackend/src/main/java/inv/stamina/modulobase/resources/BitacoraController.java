/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.resources.dto.BitacoraResponse;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.commons.ConvertTool;
import inv.stamina.modulobase.service.ParameterService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import inv.stamina.modulobase.repository.IBitacoraRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value="/bitacoras", produces={MediaType.APPLICATION_JSON_VALUE})
public class BitacoraController extends GenericController {

    private static final long serialVersionUID = 5418078144608698561L;

    @Autowired
    IBitacoraRepository respository;

    @Autowired
    ParameterService parameterService;

    /**
	 * 
	 * @param token
	 * @param ipClient
	 * @param form
	 * @param fechaIniStr
	 * @param fechaFinStr
	 * @param fecha
	 * @param accion
	 * @param direccionIp
	 * @param formulario
	 * @param usuario
	 * @param pageRequest
	 */
	@GetMapping
    Page<BitacoraResponse> list(
            @RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @Param("fecha") String fecha,
            @Param("accion") String accion,
            @Param("direccionIp") String direccionIp,
            @Param("formulario") String formulario,
            @Param("usuario") String usuario,
            Pageable pageRequest) {
        try {
            if (ipClient == null || ipClient.isEmpty() || ipClient.equals("127.0.0.1") || ipClient.equals("localhost")) {
                ipClient = httpServletRequest.getRemoteAddr();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("-------------------REQUEST----------------------\n").
                    append("token ").append(token).append(", \n").
                    append("form ").append(form).append(", \n").
                    append("ipClient ").append(ipClient).append(", \n").
                    append("url ").append(httpServletRequest.getRequestURL()).append(", \n").
                    append("fecha ").append(fecha).append(", \n").
                    append("accion ").append(accion).append(", \n").
                    append("direccionIp ").append(direccionIp).append(", \n").
                    append("formulario ").append(formulario).append(", \n").
                    append("usuario ").append(usuario).append(", \n").
                    append("fecha ").append(fecha).append(", \n").
                    append("fecha ").append(fecha).append(", \n").
                    append("------------------------------------------------\n");

            log.info(sb.toString());

            int fechaBool = (fecha == null || fecha.trim().isEmpty()) ? -1 : 0;
            int accionBool = (accion == null || accion.trim().isEmpty()) ? -1 : 0;
            int direccionIpBool = (direccionIp == null || direccionIp.trim().isEmpty()) ? -1 : 0;
            int formularioBool = (formulario == null || formulario.trim().isEmpty()) ? -1 : 0;
            int usuarioBool = (usuario == null || usuario.trim().isEmpty()) ? -1 : 0;

            Page<BitacoraResponse> out = respository.filter(
                    fechaBool, (fechaBool == -1) ? "" : ("%" + fecha + "%").toUpperCase(),
                    accionBool, (accionBool == -1) ? "" : ("%" + accion + "%").toUpperCase(),
                    direccionIpBool, (direccionIpBool == -1) ? "" : ("%" + direccionIp + "%").toUpperCase(),
                    formularioBool, (formularioBool == -1) ? "" : ("%" + formulario + "%").toUpperCase(),
                    usuarioBool, (usuarioBool == -1) ? "" : ("%" + usuario + "%").toUpperCase(),
                    pageRequest).map(b -> ConvertTool.convert(b));

            sb = new StringBuilder();
            sb.append("-------------------RESPONSE----------------------\n").
                    append("token ").append(token).append(", \n").
                    append("DATA ").append(out).append(", \n").
                    append("CONTENT ").append(out.getContent()).append(", \n").
                    append("------------------------------------------------\n");

            log.info(sb.toString());

            return out;
        } catch (Exception ex) {
            log.error("error al paginar bitacoras, " + ex.getMessage(), ex);
            Page<BitacoraResponse> out = respository.findByIdAndUserAction(-1, "-1", pageRequest).map(b -> ConvertTool.convert(b));
            StringBuilder sb = new StringBuilder();
            sb.append("-------------------RESPONSE----------------------\n").
                    append("token ").append(token).append(", \n").
                    append("DATA ").append(out).append(", \n").
                    append("CONTENT ").append(out.getContent()).append(", \n").
                    append("------------------------------------------------\n");

            log.info(sb.toString());

            return out;
        }
    }

}
