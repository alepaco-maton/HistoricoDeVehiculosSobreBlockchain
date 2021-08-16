/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

/**
 *
 * @author alepaco.maton
 */
public class UserStatus {

    public static final short HABILITADO = 1;
    public static final short INHABILITADO = 2;
    public static final short BLOQUEADO = 3;

    public static String HABILITADO_VALOR = "Habilitado";
    public static String INHABILITADO_VALOR = "Inhabilitado";
    public static String BLOQUEADO_VALOR = "Bloqueado";

    public static String obtenerValor(short e) {
        if (e == HABILITADO) {
            return HABILITADO_VALOR;
        }
        if (e == INHABILITADO) {
            return INHABILITADO_VALOR;
        }
        if (e == BLOQUEADO) {
            return BLOQUEADO_VALOR;
        }

        return "Desconocido<" + e + ">";
    }

}
