/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 *
 * @author alepaco.com
 * @param <T>
 */
public interface IListController<T extends Serializable>  {
     /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param pageRequest
     * @return 
     */
    @GetMapping
    Page<T> list(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form, Pageable pageRequest);

}
