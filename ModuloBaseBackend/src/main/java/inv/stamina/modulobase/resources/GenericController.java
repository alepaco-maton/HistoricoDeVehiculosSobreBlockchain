/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.repository.IUserRepository;
import java.io.Serializable;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.service.BitacoraService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Getter
@Setter
public class GenericController implements Serializable {

    private static final long serialVersionUID = 2546243156433770443L;

    @Autowired
    protected BitacoraService bitacoraService;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    protected HttpServletRequest httpServletRequest;

    @Autowired
    protected IUserRepository userRepository; 
    
    protected String getErrors(BindingResult result) {
        StringBuilder sb = new StringBuilder("[");

        result.getAllErrors().forEach((error) -> {
            sb.append(error.getDefaultMessage()).append(" - ");
        });

        return sb.append("]").toString();
    }

    protected String getIpAdress(String ipClient) {
        if (ipClient == null || ipClient.isEmpty() || ipClient.equals("127.0.0.1") || ipClient.equals("localhost")) {
            return httpServletRequest.getRemoteAddr();
        }

        return ipClient;
    }

    protected void printRequest(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------REQUEST----------------------\n").
                append("url ").append(httpServletRequest.getRequestURL()).append(", \n").
                append("metodo ").append(httpServletRequest.getMethod()).append(", \n");

        data.entrySet().stream().forEach((entry) -> {
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append(key).append(value).append(", \n");
        });

        sb.append("------------------------------------------------\n");

        log.info(sb.toString());
    }

    protected void printResponse(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();

        sb.append("-------------------RESPONSE----------------------\n");

        data.entrySet().stream().forEach((entry) -> {
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append(key).append(value).append(", \n");
        });

        sb.append("------------------------------------------------\n");

        log.info(sb.toString());
    }

    protected String getRole() {
        return jwtTokenUtil.getRolNombreFromToken(this.httpServletRequest.getHeader(JwtTokenUtil.KEY_TOKEN));
    }

    protected String getUserName() {
        return jwtTokenUtil.getUsernameFromToken(this.httpServletRequest.getHeader(JwtTokenUtil.KEY_TOKEN));
    }
    
    protected String getFullName() {
        String nombre = jwtTokenUtil.getUsernameFromToken(this.httpServletRequest.getHeader(JwtTokenUtil.KEY_TOKEN));
        return userRepository.findByUserName(nombre).getFullName();
    }

    protected boolean isBlanck(String text) {
        return (text == null || text.trim().isEmpty());
    }

    protected String filterText(String text) {
        return (isBlanck(text) ? "" : text);
    }

    protected String filterTextQueryUpperLike(String text) {
        return (isBlanck(text) ? "" : "%" + text.trim().toUpperCase() + "%");
    }
    
    protected String filterTextQueryUpper(String text) {
        return (isBlanck(text) ? "" : text.trim().toUpperCase());
    }
    
    protected String filterTextQuery(String text) {
        return (isBlanck(text) ? "" : text.trim());
    }

    protected int queryfilterText(String text) {
        return isBlanck(text) ? -1 : 0;
    }

}
