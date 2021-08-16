/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.service;

import inv.stamina.modulobase.model.Bitacora;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import java.sql.Timestamp;
import java.util.Calendar;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import inv.stamina.modulobase.repository.IBitacoraRepository;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author alepaco.com
 */
@Log4j2
@Getter
@Setter
@Service
@Component
public class BitacoraService {

    @Autowired
    IBitacoraRepository respository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public void saveBitacora(String token, String direccionIp, String form,
            String accion) {
        saveBitacoraWithoutToken(jwtTokenUtil.getUsernameFromToken(token),
                direccionIp, form, accion);
    }

    public void saveBitacoraWithoutToken(String userName, String direccionIp,
            String form, String accion) {
        respository.save(new Bitacora(null, accion, direccionIp,
                new Timestamp(Calendar.getInstance().getTimeInMillis()),
                form, userName));
    }

}
