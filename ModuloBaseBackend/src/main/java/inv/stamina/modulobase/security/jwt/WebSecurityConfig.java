/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.security.jwt;

import inv.stamina.modulobase.resources.LabelController;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author alepaco.maton
 */
@Configuration
@EnableWebSecurity	
@EnableScheduling // para los crones
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return new AuthenticationSecurity();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().
                antMatchers(HttpMethod.OPTIONS, "/**").permitAll().
                antMatchers(LabelController.RESOURCE_BY_LLAVE).permitAll().
                antMatchers(LabelController.RESOURCE_BY_GRUPO).permitAll().
                antMatchers("/accounts/register").permitAll().
                antMatchers("/files").permitAll().
                antMatchers(HttpMethod.GET, "/**").permitAll().
                antMatchers("/files/").permitAll().
                antMatchers("/big/data/device/info").permitAll().
                antMatchers("/accounts/recover/password").permitAll().
                antMatchers("/accounts/recover/password/verify/code").permitAll().
                antMatchers(JwtAuthenticationController.METODO_AUTENTICACION).permitAll().
                antMatchers(JwtAuthenticationController.METODO_VERSION).permitAll().
                anyRequest().authenticated();

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
