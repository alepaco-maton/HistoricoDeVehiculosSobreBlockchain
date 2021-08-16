/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.service.BigDataService;
import lombok.extern.log4j.Log4j2;
import java.util.Map;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@Service
@CrossOrigin
@RequestMapping(value = "/big/data", produces = {MediaType.APPLICATION_JSON_VALUE})
public class BigDataController extends GenericController {

    private static final long serialVersionUID = 1L;

    @Autowired
    BigDataService bigDataService;
    
    @PostMapping("/device/info")
    void deviceInfo(
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @RequestHeader(value = "collectionName") String collectionName,
            @RequestBody Map<String, Object> request) {
        try {
            if (ipClient == null || ipClient.isEmpty() || ipClient.equals("127.0.0.1") || ipClient.equals("localhost")) {
                ipClient = httpServletRequest.getRemoteAddr();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("-------------------REQUEST----------------------\n").
                    append("form ").append(form).append(", \n").
                    append("ipClient ").append(ipClient).append(", \n").
                    append("url ").append(httpServletRequest.getRequestURL()).append(", \n").
                    append("metodo ").append(httpServletRequest.getMethod()).append(", \n").
                    append("request ").append(request).append(", \n").
                    append("------------------------------------------------\n");

            log.info(sb.toString());

            bigDataService.deviceInfo(ipClient, form, collectionName, request);
        } catch (Exception e) {
            log.error("Error " + e.getMessage(), e);
        }
    }

}
