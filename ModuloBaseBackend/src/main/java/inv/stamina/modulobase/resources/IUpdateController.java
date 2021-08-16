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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 *
 * @author alepaco.com
 * @param <R>
 * @param <T>
 * @param <P>
 */
public interface IUpdateController<R extends Serializable, T extends Serializable, P> {
     
    /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param request
     * @param id
     * @param result
     * @return 
     * @throws inv.stamina.modulobase.validator.ApiException 
     */
    @PutMapping("/{id}")
    ResponseEntity<T> update(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @Valid @RequestBody R request,
            @PathVariable @Min(value = 1, message = "Identificador invalido, no puede ser menor a 1")
            @Max(value = 999999999, message = "Identificador invalido") P id,
            BindingResult result) throws ApiException;
    
}
