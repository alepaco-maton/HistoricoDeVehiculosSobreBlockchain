/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.validator.ApiException;
import java.io.Serializable; 
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 *
 * @author alepaco.com
 * @param <R>
 * @param <T> 
 */
public interface ICreateController<R extends Serializable, T extends Serializable> {
 
    /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param request
     * @param result
     * @return 
     * @throws inv.stamina.modulobase.validator.ApiException 
     */
    @PostMapping
    ResponseEntity<T> create(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @Valid @RequestBody R request, BindingResult result) throws ApiException, Exception;
    
}
