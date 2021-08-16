/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.security.jwt;

import inv.stamina.modulobase.model.Account;
import inv.stamina.modulobase.util.abm.AccountStatus;
import inv.stamina.modulobase.model.Role;
import inv.stamina.modulobase.model.User;
import inv.stamina.modulobase.service.ParameterService;
import inv.stamina.modulobase.commons.UserStatus;
import inv.stamina.modulobase.repository.IAccountRepository;
import java.io.Serializable;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import inv.stamina.modulobase.repository.IRoleRepository;
import inv.stamina.modulobase.repository.IUserRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Component
public class AuthenticationSecurity implements AuthenticationManager, Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    IUserRepository usuarioRepository; 

    @Autowired
    IAccountRepository accountRepository; 

    @Autowired
    IRoleRepository rolRepository;

    @Autowired
    ParameterService parametroService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public Role validar(String nombreUsuario, String contrasena) throws AuthenticationException {
        try {
            User usuario = usuarioRepository.findByUserNameAndStatusIn(nombreUsuario, Arrays.asList(UserStatus.HABILITADO, UserStatus.BLOQUEADO, UserStatus.INHABILITADO));

            if (usuario == null) {
                throw new BadCredentialsException("El usuario o contraseña ingresados son incorrectos") {
                    private static final long serialVersionUID = 5538299138211283825L;
                };
            }

            if (!passwordEncoder.matches(contrasena, usuario.getPassword())) {
                throw new BadCredentialsException("El usuario o contraseña ingresados son incorrectos") {
                    private static final long serialVersionUID = 5538299138211283825L;
                };
            }

            usuarioRepository.save(usuario);

            return usuario.getRoleId();
        } catch (Exception e) {
            log.error("Error al iniciar sesion con " + nombreUsuario + ", " + e.getMessage(), e);
            throw new BadCredentialsException(e.getMessage()) {
                private static final long serialVersionUID = 5538299138211283825L;
            };
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("authentication " + authentication);
        log.info("authentication " + authentication.getPrincipal());
        log.info("authentication " + authentication.getCredentials());
        log.info("authentication " + authentication.getName());
        log.info("authentication " + authentication.getDetails());
        return authentication;
    }

    public String validarApp(String userName, String password) {
        try {
            Account model = accountRepository.findByUserNameAndStatusIn(userName, Arrays.asList(AccountStatus.ENABLE));

            if (model == null) {
                return "Credenciales invalidas";
            }

            if (!passwordEncoder.matches(password, model.getPassword())) {
                return "Credenciales invalidas";
            }
            
            return "";
        } catch (Exception e) {
            log.error("Error al iniciar sesion con " + userName + ", " + e.getMessage(), e);
            return e.getMessage();
        }
    }

}
