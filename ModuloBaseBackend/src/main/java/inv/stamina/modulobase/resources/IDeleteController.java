/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.security.jwt.JwtTokenUtil; 
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 *
 * @author alepaco.com
 * @param <P>
 */
public interface IDeleteController<P> {

    /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param id
     * @return 
     * @throws org.springframework.web.servlet.NoHandlerFoundException 
     */
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @PathVariable @Min(value = 1, message = "Identificador invalido, no puede ser menor a 1")
            @Max(value = 999999999, message = "Identificador invalido") P id) throws NoHandlerFoundException, Exception;

}
