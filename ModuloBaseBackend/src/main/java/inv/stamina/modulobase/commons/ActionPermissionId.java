/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

/**
 *
 * @author aleapaco.maton
 */
public class ActionPermissionId {

    public static final int MODULO_ADMINISTRACION = 1;
    public static final int MODULO_APP_MOVIL = 2;

    public class Administration {

        public static final int FORMULARIO_USUARIOS = 100;
        
        public class User {   
            public static final int ACCION_NAVEGACION = 1100;
            public static final int ACCION_CREAR = 1101;
            public static final int ACCION_MODIFICAR = 1102;
            public static final int ACCION_HABILITAR = 1103;
            public static final int ACCION_RESETEAR_CONTRASENA = 1105;
            public static final int ACCION_INHABILITAR = 1106;   
        }
    }
    
    public class AppMovil {

        public static final int FORMULARIO_ACCOUNT = 200;
        
        public class Account {   
            public static final int ACCION_NAVEGACION = 2200;
        }
    }

}
