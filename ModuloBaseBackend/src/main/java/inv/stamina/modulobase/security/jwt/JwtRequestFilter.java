
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import inv.stamina.modulobase.model.Action;
import inv.stamina.modulobase.model.Role;
import inv.stamina.modulobase.model.RoleAction;
import inv.stamina.modulobase.resources.LabelController;
import inv.stamina.modulobase.service.ParameterService;
import lombok.extern.log4j.Log4j2;
import inv.stamina.modulobase.repository.IActionRepository;
import inv.stamina.modulobase.repository.IRoleActionRepository;
import inv.stamina.modulobase.repository.IRoleRepository;

/**
 *
 * @author alepaco.maton
 */
@Component
@Log4j2
public class JwtRequestFilter extends OncePerRequestFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private IRoleRepository rolRepository;

    @Autowired
    private IRoleActionRepository rolAccionRepository;

    @Autowired
    private IActionRepository accionRepository;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    ParameterService parametroService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private void peticionesOptionsCors(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        Enumeration<String> headersEnum = ((HttpServletRequest) request).getHeaders("Access-Control-Request-Headers");
        StringBuilder headers = new StringBuilder();
        String delim = "";
        while (headersEnum.hasMoreElements()) {
            headers.append(delim).append(headersEnum.nextElement());
            delim = ", ";
        }
        response.setHeader("Access-Control-Allow-Headers", headers.toString());
    }

    private void tokenInvalido(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpServletResponse httpResp = (HttpServletResponse) response;
        httpResp.setHeader("Access-Control-Allow-Origin", "*");
        httpResp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        httpResp.setHeader("Access-Control-Max-Age", "3600");
        Enumeration<String> headersEnum = ((HttpServletRequest) request).getHeaders("Access-Control-Request-Headers");
        StringBuilder headers = new StringBuilder();
        String delim = "";
        while (headersEnum.hasMoreElements()) {
            headers.append(delim).append(headersEnum.nextElement());
            delim = ", ";
        }
        httpResp.setHeader("Access-Control-Allow-Headers", headers.toString());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter res = response.getWriter();
        res.append("Unauthorized");
//        res.close();
    }

    private void sinPermiso(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpServletResponse httpResp = (HttpServletResponse) response;
        httpResp.setHeader("Access-Control-Allow-Origin", "*");
        httpResp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        httpResp.setHeader("Access-Control-Max-Age", "3600");
        Enumeration<String> headersEnum = ((HttpServletRequest) request).getHeaders("Access-Control-Request-Headers");
        StringBuilder headers = new StringBuilder();
        String delim = "";
        while (headersEnum.hasMoreElements()) {
            headers.append(delim).append(headersEnum.nextElement());
            delim = ", ";
        }
        httpResp.setHeader("Access-Control-Allow-Headers", headers.toString());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter res = response.getWriter();
        res.append("Forbidden");
//        res.close();
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
            FilterChain chain) throws IOException, ServletException {

        try {
            log.info("URL " + request.getRequestURL());

            if (request.getMethod().equals("OPTIONS")) {
                peticionesOptionsCors(request, response);
                return;
            }

//            if (request.getRequestURI().contains("/files")) { 
//                chain.doFilter(request, response);
//                return;
//            }

            if (!request.getRequestURI().equals(request.getContextPath() + JwtAuthenticationController.METODO_AUTENTICACION)
                    && !request.getRequestURI().equals(request.getContextPath() + JwtAuthenticationController.METODO_VERSION)
                    && !request.getRequestURI().equals(request.getContextPath() + LabelController.RESOURCE_BY_LLAVE)
                    && !request.getRequestURI().equals(request.getContextPath() + "/accounts/register")
                    && !request.getRequestURI().contains(request.getContextPath() + "/files")
                    && !request.getRequestURI().contains("files")
                    && !request.getRequestURI().equals(request.getContextPath() + "/big/data/device/info")
                    && !request.getRequestURI().equals(request.getContextPath() + "/accounts/recover/password")
                    && !request.getRequestURI().equals(request.getContextPath() + "/accounts/recover/password/verify/code")
                    && !request.getRequestURI().equals(request.getContextPath() + LabelController.RESOURCE_BY_GRUPO)) {
 
                final String requestTokenHeader = request.getHeader(JwtTokenUtil.KEY_TOKEN);

                String rolNombre = null;

                if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                    log.info("Token invalido " + requestTokenHeader);
                    tokenInvalido(request, response);
                    return;
                }

                try {
                    rolNombre = jwtTokenUtil.getRolNombreFromToken(requestTokenHeader);

                    log.info("rolNombre " + rolNombre);

                    if (jwtTokenUtil.isTokenExpired(requestTokenHeader)) {
                        log.info("Token expirado ");
                        tokenInvalido(request, response);
                        return;
                    }
                } catch (Exception e) {
                    log.error("Token invalido, " + e.getMessage(), e);
                    tokenInvalido(request, response);
                    return;
                }

                if ("appMovil".equals(rolNombre)) {
                    UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(rolNombre);
                    UsernamePasswordAuthenticationToken temp
                            = new UsernamePasswordAuthenticationToken(userDetails,
                                    null, userDetails.getAuthorities());
                    temp.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(temp);

                    chain.doFilter(request, response);
                    return;
                }

                Role rol = rolRepository.findByNameAndStatusTrue(rolNombre);

                log.info("usario web " + rol);

                if (rol == null) {
                    tokenInvalido(request, response);
                    return;
                }

                boolean flag = true;

                exito:
                for (RoleAction rolAccion : rolAccionRepository.findAllByRoleId(rol.getId())) {
                    Action accion = accionRepository.findById(rolAccion.getId().getActionId()).get();
                    String[] urls = accion.getUrl().split(",");
                    String[] metodos = accion.getMethod().split(",");
                    int size = urls.length;

                    for (int i = 0; i < size; i++) {
                        String uri = request.getContextPath() + urls[i];
                        if (request.getRequestURI().startsWith(uri) && metodos[i].equals(request.getMethod().toUpperCase())) {
                            flag = false;
                            break exito;
                        }

                    }
                }

                if (flag) {
                    log.info("No tiene permiso al recurso, " + request.getRequestURL());
                    sinPermiso(request, response);
                    return;
                }

                UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(rolNombre);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
 
            chain.doFilter(request, response);
        } catch (Exception eg) {
            log.error("Token invalido, " + eg.getMessage(), eg);
            tokenInvalido(request, response);
        }
    }

}
