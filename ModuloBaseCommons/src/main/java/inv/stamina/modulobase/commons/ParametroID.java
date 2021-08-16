package inv.stamina.modulobase.commons;

import java.io.Serializable;

/**
 * Esta Clase tiene como finalidad principal identificar los distintos
 * parametros definidos en nuestro proyecto, los cuales son identificados por el
 * ID correspondiente al ID de la BD de la tabla Parametro.
 *
 * @author: alepaco.maton
 *
 * *
 */
public class ParametroID implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Para nuevos Parametros es necesario agregar su respectivo atributo que lo
     * identifique en el sistema, y que es representado por esta clase, el valor
     * del paramtro debe ser el ID que lo identifica en la tabla Parametro.
     *
     */
    public static final int NIVEL_LOG = 0;

    public static final int CONTRASENA_POR_DEFECTO = 10;

    public static final int EXPRESION_REGULAR_CONTRASENA = 13;
    public static final int EXPRESION_REGULAR_NOMBRE_USUARIO = 14;
    public static final int MENSAJE_VALIDACION_NOMBRE_USUARIO = 15;
    public static final int MENSAJE_VALIDACION_CONTRASENA = 16;
    public static final int EXPRESION_REGULAR_GENERAL = 17;
    public static final int MENSAJE_VALIDACION_GENERAL = 18;
    public static final int LOCATION_FILES = 19;
    
    public static final int RELOAD_PARAMETER_ID = 51;
 
    public static final int EMAIL_NOTIFIER_TEMPLATE_MESSAGE = 20;
    public static final int EMAIL_NOTIFIER_SUBJECT = 21;
    public static final int REGEX_VALIDATE_EMAIL = 22;
        
//    //otros valores staticos 
//    public static final int LOCAL = 2;

    public class ServerSocket {

        public static final int SS_MAXIMO_NUMERO_CONEXIONES = 3002;
        public static final int SS_TIEMPO_MAXIMO_ESPERA_PING = 3003;
        public static final int SS_FRECUENCIA_PING = 3004;
        public static final int SS_TIEMPO_MAXIMO_ESPERA_CONEXION = 3005;
        public static final int SS_NUMERO_MAXIMO_INTENTOS_PING = 3006;
        public static final int SS_SERVER_DIRECCION_IP = 3007;
        public static final int SS_SERVER_PUERTO = 3008;

    }
    
    public class ServerSMTP {

        public static final int SMTP_HOST = 4002;
        public static final int SMTP_PORT = 4003;
        public static final int SMTP_FROM = 4004;
        public static final int SMTP_PASSWORD = 4005; 

    }

}
