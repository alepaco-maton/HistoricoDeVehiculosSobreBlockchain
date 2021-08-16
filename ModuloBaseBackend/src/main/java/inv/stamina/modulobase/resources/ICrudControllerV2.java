/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.validator.ApiException;
import java.io.Serializable;
import java.net.URISyntaxException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 *
 * @author alepaco.maton
 * @param <R>
 * @param <T>
 */
public interface ICrudControllerV2<R extends Serializable, T extends Serializable, P> {

    /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param pageRequest
     */
    @GetMapping
    Page<T> list(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form, Pageable pageRequest);

    /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param id
     */
    @GetMapping("/{id}")
    ResponseEntity<T> get(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form, @PathVariable P id) throws NoHandlerFoundException;

    /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param request
     * @param result
     */
    @PostMapping
    ResponseEntity<T> create(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @Valid @RequestBody R request, BindingResult result) throws URISyntaxException, ApiException;

    /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param request
     * @param id
     * @param result
     */
    @PutMapping("/{id}")
    ResponseEntity<T> update(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @Valid @RequestBody R request,
            @PathVariable @Min(value = 1, message = "Identificador invalido, no puede ser menor a 1")
            @Max(value = 999999999, message = "Identificador invalido") P id,
            BindingResult result) throws ApiException;

    /**
     *
     * @param token
     * @param ipClient
     * @param form
     * @param id
     */
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @PathVariable @Min(value = 1, message = "Identificador invalido, no puede ser menor a 1")
            @Max(value = 999999999, message = "Identificador invalido") P id) throws NoHandlerFoundException, Exception;

}
