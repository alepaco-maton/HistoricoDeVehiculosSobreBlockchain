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
public class ParameterType {

    public static final String FORMATO_FECHA = "dd/MM/yyyy";
    public static final String FORMATO_FECHA_HORA = "dd/MM/yyyy HH:mm:ss";

    public static final short TIPO_CADENA = 1;
    public static final short TIPO_FECHA = 2;
    public static final short TIPO_NUMERICO = 3;
    public static final short TIPO_BOOLEANO = 4;
    public static final short TIPO_COLOR = 5;
    public static final short TIPO_LISTADO_VALORES_TEXTO = 6;
    public static final short TIPO_LISTADO_VALORES_NUMERICOS = 7;

    public static boolean esValido(short valor) {
        return (valor == TIPO_CADENA || valor == TIPO_FECHA || valor == TIPO_NUMERICO || valor == TIPO_BOOLEANO || valor == TIPO_COLOR || valor == TIPO_LISTADO_VALORES_TEXTO || valor == TIPO_LISTADO_VALORES_NUMERICOS);
    }

    public static String getType(short type) {
        if (type == TIPO_BOOLEANO) {
            return "Verdadero o Falso (" + type + ")";
        }
        if (type == TIPO_CADENA) {
            return "Texto (" + type + ")";
        }
        if (type == TIPO_COLOR) {
            return "Color (" + type + ")";
        }
        if (type == TIPO_FECHA) {
            return "Cadena (" + type + ")";
        }
        if (type == TIPO_LISTADO_VALORES_NUMERICOS) {
            return "Lista de nuemeros (" + type + ")";
        }
        if (type == TIPO_LISTADO_VALORES_TEXTO) {
            return "Lista de textos (" + type + ")";
        }
        if (type == TIPO_NUMERICO) {
            return "Numero (" + type + ")";
        }

        return "Desconocido<" + type + ">";
    }
}
