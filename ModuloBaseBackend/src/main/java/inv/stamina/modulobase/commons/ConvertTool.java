/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

import inv.stamina.modulobase.model.Bitacora;
import inv.stamina.modulobase.model.Label;
import inv.stamina.modulobase.model.Parameter;
import inv.stamina.modulobase.model.Role;
import inv.stamina.modulobase.model.User;
import inv.stamina.modulobase.resources.dto.BitacoraResponse;
import inv.stamina.modulobase.resources.dto.LabelResponse;
import inv.stamina.modulobase.resources.dto.ParameterResponse;
import inv.stamina.modulobase.resources.dto.RoleResponse;
import inv.stamina.modulobase.resources.dto.UserResponse;
import java.text.SimpleDateFormat;

/**
 *
 * @author alepaco.maton
 */
public class ConvertTool {

    public static SimpleDateFormat sdf = new SimpleDateFormat(ParameterType.FORMATO_FECHA_HORA);
    public static SimpleDateFormat sdff = new SimpleDateFormat(ParameterType.FORMATO_FECHA);

    private static final long serialVersionUID = 2052233922867961389L;

    public static UserResponse convert(User model) {
        return new UserResponse(model.getId(), model.getUserName(),
                model.getFullName(),
                model.getRoleId().getId(),
                model.getRoleId().getName(),
                model.getStatus()== UserStatus.HABILITADO ? UserStatus.HABILITADO_VALOR : 
                        model.getStatus()== UserStatus.INHABILITADO ? UserStatus.INHABILITADO_VALOR : 
                                model.getStatus()== UserStatus.BLOQUEADO ? UserStatus.BLOQUEADO_VALOR : "");
    }

    public static RoleResponse convert(Role model) {
        return new RoleResponse(model.getId(), model.getName(), model.getDescription());
    }

    public static BitacoraResponse convert(Bitacora model) {
        return new BitacoraResponse(model.getId(), model.getUserAction(), model.getIpAdress(),
                sdf.format(model.getDateAction()),
                model.getSource(),
                model.getUserName()
        );
    }

    public static ParameterResponse convert(Parameter model) {
        return new ParameterResponse(model.getId(), model.getName(), model.getType(),
                model.getValue(), model.getDescription());
    }

    public static LabelResponse convert(Label model) {
        return new LabelResponse(model.getId(), model.getKey(), model.getValue(), model.getGroupLevel());
    }

}
