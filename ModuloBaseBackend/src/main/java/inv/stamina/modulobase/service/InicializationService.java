/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import inv.stamina.modulobase.commons.ActionPermissionId; 
import inv.stamina.modulobase.commons.ParametroID;
import inv.stamina.modulobase.commons.ParameterType;
import inv.stamina.modulobase.commons.SymmetricEncoder;
import inv.stamina.modulobase.commons.UserType;
import inv.stamina.modulobase.model.Action;
import inv.stamina.modulobase.model.Label;
import inv.stamina.modulobase.model.Form;
import inv.stamina.modulobase.model.Parameter;
import inv.stamina.modulobase.model.Role;
import inv.stamina.modulobase.model.RoleAction;
import inv.stamina.modulobase.model.User;
import inv.stamina.modulobase.commons.UserStatus;
import inv.stamina.modulobase.model.RoleActionPK;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import lombok.Getter;
import inv.stamina.modulobase.repository.IActionRepository;
import inv.stamina.modulobase.repository.ILabelRepository;
import inv.stamina.modulobase.repository.IFormRepository;
import inv.stamina.modulobase.repository.IParameterRepository;
import inv.stamina.modulobase.repository.IRoleActionRepository;
import inv.stamina.modulobase.repository.IRoleRepository;
import inv.stamina.modulobase.repository.IUserRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Service
@Component
@Order(1)
@Scope("singleton")
public class InicializationService {

    public static final String RULES_ENCODE = "alepaco.maton 2021";
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private IActionRepository accionRepository;

    @Autowired
    private ILabelRepository etiquetaRepository;

    @Autowired
    private IFormRepository formularioRepository;

    @Autowired
    private IParameterRepository parametroRepository;

    @Autowired
    private IRoleRepository rolRepository;

    @Autowired
    private IRoleActionRepository rolAccionRepository;

    @Autowired
    private IUserRepository usuarioRepository;

    @Getter
    ExecutorService executor;

    public int ordenModulo = 1;
    public int seqModuloId = 0;
    public int intervaloModulo = 100;
//    public int seqFormularioId = 1;

    public void etiquetasPlantilla() {
        if (etiquetaRepository.findByGroupLevelAndKey("login", "login.titulo") == null) {
            log.warn(etiquetaRepository.save(new Label(null, "login.titulo", "Modulo Base", "login", true)));
        }

        final String grupo = "plantilla";

        HashMap<String, String> etiquetas = new HashMap<>();

        etiquetas.put("pie.pagina", "Copyright © 2020 : Tigo - Todos los derechos reservados.");
        etiquetas.put("dialogo.confirmacion.titulo", "Confirmación");
        etiquetas.put("dialogo.btn.confirmacion.si", "Si");
        etiquetas.put("dialogo.btn.confirmacion.no", "No");
        etiquetas.put("tbl.sin.registros", "Sin registro");
        etiquetas.put("btn.nuevo", "Nuevo");
        etiquetas.put("btn.editar", "Editar");
        etiquetas.put("btn.excel", "Excel");
        etiquetas.put("btn.guardar", "Guardar");
        etiquetas.put("btn.actualizar", "Actualizar");
        etiquetas.put("btn.eliminar", "Eliminar");
        etiquetas.put("btn.cancelar", "Cancelar");

//        etiquetas.put("","");
        etiquetas.entrySet().stream().forEach((entry) -> {
            String llave = entry.getKey();
            String value = entry.getValue();
            if (etiquetaRepository.findByGroupLevelAndKey(grupo, llave) == null) {
                log.warn(etiquetaRepository.save(new Label(null, llave, value, grupo, true)));
            }
        });
    }

    public void etiquetasUsuario() {
        final String grupo = "usuario";

        HashMap<String, String> etiquetas = new HashMap<>();

        etiquetas.put("titulo", "Gestión de Usuarios");
        etiquetas.put("btn.habilitar", "Habilitar");
        etiquetas.put("btn.inhabilitar", "Inhabilitar");
        etiquetas.put("tbl.id", "Id");
        etiquetas.put("tbl.usuario", "Usuario");
        etiquetas.put("tbl.nombre.completo", "Nombre completo");
        etiquetas.put("tbl.rol", "Rol");
        etiquetas.put("tbl.estado", "Estado");
        etiquetas.put("crud.header", "Nuevo/Editar Usuario");
        etiquetas.put("crud.nombre.usuario", "Nombre de usuario :*");
        etiquetas.put("crud.nombre.completo", "Nombre completo :*");
        etiquetas.put("crud.rol", "Rol :*");
        etiquetas.put("crud.rol.placeholder", "Seleccione un rol");
//        etiquetas.put("", "");

        etiquetas.entrySet().stream().forEach((entry) -> {
            String llave = entry.getKey();
            String value = entry.getValue();
            if (etiquetaRepository.findByGroupLevelAndKey(grupo, llave) == null) {
                log.warn(etiquetaRepository.save(new Label(null, llave, value, grupo, true)));
            }
        });
    }

    public void etiquetasEtiqueta() {
        final String grupo = "etiqueta";

        HashMap<String, String> etiquetas = new HashMap<>();

        etiquetas.put("titulo", "Gestión de Etiquetas");
        etiquetas.put("crud.header", "Editar Etiqueta");
        etiquetas.put("tbl.llave", "Llave");
        etiquetas.put("tbl.valor", "Valor");
        etiquetas.put("tbl.grupo", "Grupo");
        etiquetas.put("crud.llave", "Llave :*");
        etiquetas.put("crud.valor", "Valor :*");
        etiquetas.put("crud.grupo", "Grupo :*");
//        etiquetas.put("", "");

        etiquetas.entrySet().stream().forEach((entry) -> {
            String llave = entry.getKey();
            String value = entry.getValue();
            if (etiquetaRepository.findByGroupLevelAndKey(grupo, llave) == null) {
                log.warn(etiquetaRepository.save(new Label(null, llave, value, grupo, true)));
            }
        });
    }

    public void etiquetasPerfil() {
        final String grupo = "perfil";

        HashMap<String, String> etiquetas = new HashMap<>();

        etiquetas.put("titulo", "Cambio de Contraseña");
        etiquetas.put("crud.header", "Cambio de contraseña");
        etiquetas.put("crud.contrasena.actual", "Contraseña actual :*");
        etiquetas.put("crud.nueva.contrasena", "Nueva contraseña :*");
        etiquetas.put("crud.confirmacion.nueva.contrasena", "Confirmar nueva contraseña :*");
//        etiquetas.put("", "");

        etiquetas.entrySet().stream().forEach((entry) -> {
            String llave = entry.getKey();
            String value = entry.getValue();
            if (etiquetaRepository.findByGroupLevelAndKey(grupo, llave) == null) {
                log.warn(etiquetaRepository.save(new Label(null, llave, value, grupo, true)));
            }
        });
    }

    public void etiquetasRol() {
        final String grupo = "rol";

        HashMap<String, String> etiquetas = new HashMap<>();

        etiquetas.put("titulo", "Gestión de Roles");
        etiquetas.put("crud.header", "Nuevo/Editar Rol");
        etiquetas.put("tbl.id", "Id");
        etiquetas.put("tbl.nombre", "Rol");
        etiquetas.put("tbl.descripcion", "Descripción");
        etiquetas.put("crud.nombre", "Rol :*");
        etiquetas.put("crud.descripcion", "Descripción :");
        etiquetas.put("crud.sub.header.permiso", "Permisos del rol : ");
        etiquetas.put("crud.sub.header.modulo", "Módulos");
        etiquetas.put("btn.permiso", "Permisos");
        etiquetas.put("lbl.seleccionar.todos", "Seleccionar todos");
//        etiquetas.put("", "");

        etiquetas.entrySet().stream().forEach((entry) -> {
            String llave = entry.getKey();
            String value = entry.getValue();
            if (etiquetaRepository.findByGroupLevelAndKey(grupo, llave) == null) {
                log.warn(etiquetaRepository.save(new Label(null, llave, value, grupo, true)));
            }
        });
    }

    public void etiquetasBitacora() {
        final String grupo = "bitacora";

        HashMap<String, String> etiquetas = new HashMap<>();

        etiquetas.put("titulo", "Bitácora");
        etiquetas.put("crud.header", "Detalles de Bitácora");
        etiquetas.put("header.filtros", "Filtros");
        etiquetas.put("tbl.fecha", "Fecha");
        etiquetas.put("tbl.accion", "Acción");
        etiquetas.put("tbl.formulario", "Origen");
        etiquetas.put("tbl.usuario", "Usuario");
        etiquetas.put("tbl.direccion.ip", "Dirección IP");
//        etiquetas.put("", "");

        etiquetas.entrySet().stream().forEach((entry) -> {
            String llave = entry.getKey();
            String value = entry.getValue();
            if (etiquetaRepository.findByGroupLevelAndKey(grupo, llave) == null) {
                log.warn(etiquetaRepository.save(new Label(null, llave, value, grupo, true)));
            }
        });
    }
 
    public void etiquetasParametro() {
        final String grupo = "parametro";

        HashMap<String, String> etiquetas = new HashMap<>();

        etiquetas.put("titulo", "Gestión de Parámetros");
        etiquetas.put("crud.header", "Editar Parámetro"); 
        etiquetas.put("tbl.nombre", "Nombre");
        etiquetas.put("tbl.valor", "Valor");
        etiquetas.put("tbl.descripcion", "Descripción");
        etiquetas.put("crud.nombre", "Nombre :*"); 
        etiquetas.put("crud.valor", "Valor :*");
        etiquetas.put("crud.descripcion", "Descripción :"); 
//        etiquetas.put("", "");

        etiquetas.entrySet().stream().forEach((entry) -> {
            String llave = entry.getKey();
            String value = entry.getValue();
            if (etiquetaRepository.findByGroupLevelAndKey(grupo, llave) == null) {
                log.warn(etiquetaRepository.save(new Label(null, llave, value, grupo, true)));
            }
        });
    }

    public void accionesGestionUsuario() {
        int seqAccionId = 1100;

        if (!accionRepository.findById(ActionPermissionId.Administration.User.ACCION_NAVEGACION).isPresent()) {
            log.warn(accionRepository.save(new Action(ActionPermissionId.Administration.User.ACCION_NAVEGACION,
                    ActionPermissionId.Administration.FORMULARIO_USUARIOS,
                    "Navegación", "/users,/users/", "GET,GET")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR,
                ActionPermissionId.Administration.User.ACCION_NAVEGACION) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR,
                    ActionPermissionId.Administration.User.ACCION_NAVEGACION))));
        }

        seqAccionId++;
        if (!accionRepository.findById(ActionPermissionId.Administration.User.ACCION_CREAR).isPresent()) {
            log.warn(accionRepository.save(new Action(ActionPermissionId.Administration.User.ACCION_CREAR,
                    ActionPermissionId.Administration.FORMULARIO_USUARIOS,
                    "Crear", "/users,/roles", "POST,GET")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR,
                ActionPermissionId.Administration.User.ACCION_CREAR) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR,
                    ActionPermissionId.Administration.User.ACCION_CREAR))));
        }

        seqAccionId++;
        if (!accionRepository.findById(ActionPermissionId.Administration.User.ACCION_MODIFICAR).isPresent()) {
            log.warn(accionRepository.save(new Action(ActionPermissionId.Administration.User.ACCION_MODIFICAR,
                    ActionPermissionId.Administration.FORMULARIO_USUARIOS,
                    "Modificar", "/users,/roles", "PUT,GET")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR,
                ActionPermissionId.Administration.User.ACCION_MODIFICAR) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR,
                    ActionPermissionId.Administration.User.ACCION_MODIFICAR))));
        }

        seqAccionId++;
        if (!accionRepository.findById(ActionPermissionId.Administration.User.ACCION_HABILITAR).isPresent()) {
            log.warn(accionRepository.save(new Action(ActionPermissionId.Administration.User.ACCION_HABILITAR,
                    ActionPermissionId.Administration.FORMULARIO_USUARIOS,
                    "Habilitar", "/users/enable", "PUT")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR,
                ActionPermissionId.Administration.User.ACCION_HABILITAR) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR,
                    ActionPermissionId.Administration.User.ACCION_HABILITAR))));
        }

        seqAccionId++;// se salto el 1104 problemas en el desarrollo arreglar en un futuro
        seqAccionId++;
//        if (!accionRepository.findById(ActionPermissionId.Administration.User.ACCION_RESETEAR_CONTRASENA).isPresent()) {
//            log.warn(accionRepository.save(new Action(ActionPermissionId.Administration.User.ACCION_RESETEAR_CONTRASENA,
//                    ActionPermissionId.Administration.FORMULARIO_USUARIOS,
//                    "Resetear contraseña", "/users/password/reset", "GET")));
//        }
//
//        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR,
//                ActionPermissionId.Administration.User.ACCION_RESETEAR_CONTRASENA) == null) {
//            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR,
//                    ActionPermissionId.Administration.User.ACCION_RESETEAR_CONTRASENA))));
//        }

        seqAccionId++;
        if (!accionRepository.findById(ActionPermissionId.Administration.User.ACCION_INHABILITAR).isPresent()) {
            log.warn(accionRepository.save(new Action(ActionPermissionId.Administration.User.ACCION_INHABILITAR,
                    ActionPermissionId.Administration.FORMULARIO_USUARIOS,
                    "Inhabilitar", "/users/disable", "PUT")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR,
                ActionPermissionId.Administration.User.ACCION_INHABILITAR) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR,
                    ActionPermissionId.Administration.User.ACCION_INHABILITAR))));
        }
    }

    public void accionesGestionRol(int formularioId) {
        int seqAccionId = 1200;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Navegación", "/roles,/roles/", "GET,GET")));
        }
        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }

        seqAccionId++;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Crear", "/roles", "POST")));
        }
        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }

        seqAccionId++;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Modificar", "/roles", "PUT")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }

        seqAccionId++;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Eliminar", "/roles", "DELETE")));
        }
        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }

        seqAccionId++;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Navegación de permisos", "/permissions,/permissions/rol/", "GET,GET")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }

        seqAccionId++;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Modificación de permisos", "/permissions", "POST")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }

        seqAccionId++;

//        if (!accionRepository.findById(seqAccionId).isPresent()) {
//            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Navegación de permisos de tipos de parametros", "/permissions/parametros,/permisos/tipos/parametros/rol/", "GET,GET")));
//        }

//        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
//            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
//        }

        seqAccionId++;

//        if (!accionRepository.findById(seqAccionId).isPresent()) {
//            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Modificación de permisos de tipos de parametros ", "/permissions/tipos/parametros", "POST")));
//        }
//        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
//            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
//        }
    }

    public void accionesBitacora(int formularioId) {
        int seqAccionId = 1400;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Navegación", "/bitacoras", "GET")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }
    }

    public void accionesGestionParametro(int formularioId) {
        int seqAccionId = 1500;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Navegación", "/parameters,/parameters/,/tipos/parametros,/permisos/tipos/parametros/rol/", "GET,GET,GET,GET")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }

        seqAccionId++;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Modificar", "/parameters", "PUT")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }
    }

    public void accionesGestionEtiqueta(int formularioId) {
        int seqAccionId = 1600;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Navegación", "/labels,/labels/", "GET,GET")));
        }
        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }

        seqAccionId++;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Modificar", "/labels", "PUT")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }
    }

    public void accionesPerfil(int formularioId) {
        int seqAccionId = 1701; // arreglar con un calculo numerico

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formularioId, "Cambiar contraseña", "/profiles/changed/password", "POST")));
        }

        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }
    }

    public void parametrosSistema() {
//        if (!parametroRepository.findById(ParametroID.NIVEL_LOG).isPresent()) {
//            log.warn(parametroRepository.save(new Parameter(
//                    ParametroID.NIVEL_LOG,
//                    "NIVEL_LOG", ParameterType.TIPO_NUMERICO,
//                    "1",
//                    "Nivel de log del sistema.")));
//        }

        if (!parametroRepository.findById(ParametroID.CONTRASENA_POR_DEFECTO).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.CONTRASENA_POR_DEFECTO, "CONTRASENA_POR_DEFECTO", ParameterType.TIPO_CADENA, "$2y$11$d4DkunoigPZztvcDf99lfeuEsszGE1nuvdcGVddT6kgQNToEE3pMS", "Cantidad de intentos fallidos que pude tener el usuario que está intentando ingresar al sistema antes de que sea  bloqueado por autenticación fallida.")));
        }

//        if (!parametroRepository.findById(ParametroID.NUMERO_INTENTOS_AUTENTICACION).isPresent()) {
//            log.warn(parametroRepository.save(new Parameter(ParametroID.NUMERO_INTENTOS_AUTENTICACION, "NUMERO_INTENTOS_AUTENTICACION", ParameterType.TIPO_NUMERICO, "3", "Cantidad de intentos fallidos que pude tener el usuario que está intentando ingresar al sistema antes de que sea  bloqueado por autenticación fallida.")));
//        }

//        if (!parametroRepository.findById(ParametroID.TIEMPO_BLOQUEO_AUTENTICACION).isPresent()) {
//            log.warn(parametroRepository.save(new Parameter(ParametroID.TIEMPO_BLOQUEO_AUTENTICACION, "TIEMPO_BLOQUEO_AUTENTICACION", ParameterType.TIPO_NUMERICO, "5", "Tiempo en minutos que debe  esperar el usuario bloqueado (autenticación fallida), para intentar ingresar al sistema nuevamente.")));
//        }

        if (!parametroRepository.findById(ParametroID.EXPRESION_REGULAR_CONTRASENA).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.EXPRESION_REGULAR_CONTRASENA, "EXPRESION_REGULAR_CONTRASENA", ParameterType.TIPO_CADENA, "^[a-zA-Z0-9_@%*.]+$", "Expresion regular para validar que la contraseña cumpla con los requerimientos minimos de seguridad")));
        }

        if (!parametroRepository.findById(ParametroID.EXPRESION_REGULAR_NOMBRE_USUARIO).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.EXPRESION_REGULAR_NOMBRE_USUARIO, "EXPRESION_REGULAR_NOMBRE_USUARIO", ParameterType.TIPO_CADENA, "^[a-zA-Z0-9_\\- \\ñ\\Ñ\\á\\é\\í\\ó\\ú\\Á\\É\\Í\\Ó\\Ú,.()/*.#]+$", "Expresion regular para validar que el nombre de usuario cumpla con los requerimientos minimos de seguridad")));
        }

        if (!parametroRepository.findById(ParametroID.MENSAJE_VALIDACION_NOMBRE_USUARIO).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.MENSAJE_VALIDACION_NOMBRE_USUARIO, "MENSAJE_VALIDACION_NOMBRE_USUARIO", ParameterType.TIPO_CADENA, "No se aceptan caracteres especiales.", "Mensaje de error para la validacion de el nomnbre de usuario")));
        }

        if (!parametroRepository.findById(ParametroID.MENSAJE_VALIDACION_CONTRASENA).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.MENSAJE_VALIDACION_CONTRASENA, "MENSAJE_VALIDACION_CONTRASENA", ParameterType.TIPO_CADENA, "Solo se permiten letras, numeros y los caracteres ''_'' ''@'' con longitud minima de 2", "Mensaje de error para la validacion de la contraseña")));
        }

        if (!parametroRepository.findById(ParametroID.EXPRESION_REGULAR_GENERAL).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.EXPRESION_REGULAR_GENERAL, "EXPRESION_REGULAR_GENERAL", ParameterType.TIPO_CADENA, "^[a-zA-Z0-9_\\- \\ñ\\Ñ\\á\\é\\í\\ó\\ú\\Á\\É\\Í\\Ó\\Ú,.()/*.#]+$", "Expresion regular para la validacion de texto")));
        }

        if (!parametroRepository.findById(ParametroID.MENSAJE_VALIDACION_GENERAL).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.MENSAJE_VALIDACION_GENERAL, "MENSAJE_VALIDACION_GENERAL", ParameterType.TIPO_CADENA, "No se aceptan caracteres especiales.", "Mensaje de error para la validacion de texto")));
        }

        if (!parametroRepository.findById(ParametroID.LOCATION_FILES).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.LOCATION_FILES, 
                    "LOCATION_FILES", ParameterType.TIPO_CADENA, 
                     "E:\\Alex Paco\\Personal\\tempfiles\\beethoven\\", "Mensaje de error para la validacion de texto")));
        }
 
        if (!parametroRepository.findById(ParametroID.RELOAD_PARAMETER_ID).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.RELOAD_PARAMETER_ID, "RELOAD_PARAMETER_ID", ParameterType.TIPO_BOOLEANO, "true", "Parametro para volver a cargar los parametros del sistema")));
        }

        if (!parametroRepository.findById(ParametroID.EMAIL_NOTIFIER_TEMPLATE_MESSAGE).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.EMAIL_NOTIFIER_TEMPLATE_MESSAGE, 
                    "EMAIL_NOTIFIER_TEMPLATE_MESSAGE", ParameterType.TIPO_CADENA, 
                    "Estimado [name], \nle compartimos su codigo de verificacion : [code] \n" +
                            "para poder cambiar su contrasena " +
                            "desde tu App Movil. \nSaludos.", 
                    "Template para las notifiaciones para el envio del codigo de verificacion")));
        }

        if (!parametroRepository.findById(ParametroID.EMAIL_NOTIFIER_SUBJECT).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.EMAIL_NOTIFIER_SUBJECT, 
                    "EMAIL_NOTIFIER_SUBJECT", ParameterType.TIPO_CADENA, "App Movil - Codigo de verificacion", 
                    "Asunto de la notifiacion del codigo de verificacion")));
        }

        if (!parametroRepository.findById(ParametroID.REGEX_VALIDATE_EMAIL).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(ParametroID.REGEX_VALIDATE_EMAIL, 
                    "REGEX_VALIDATE_EMAIL", ParameterType.TIPO_CADENA, "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", 
                    "Expresion regular para validar si un correo electronico es valido o no")));
        }
        
//        if (!parametroRepository.findById(ParametroID.DIRECCION_REPORTES_JRXML).isPresent()) {
//            log.warn(parametroRepository.save(new Parameter(ParametroID.DIRECCION_REPORTES_JRXML,
//                    "DIRECCION_REPORTES_JRXML", ParameterType.TIPO_CADENA,
//                    "E:\\proyectos\\FraudeCaseManagement\\CaseFarudeManagement\\Reportes\\",
//                    "Direccion de los jasper jrxml")));
//        }

//        if (!parametroRepository.findById(ParametroID.BITACORA_LOG_SISTEMA_DIAS_ATRAS).isPresent()) {
//            log.warn(parametroRepository.save(new Parameter(ParametroID.BITACORA_LOG_SISTEMA_DIAS_ATRAS,
//                    "BITACORA_LOG_SISTEMA_DIAS_ATRAS", ParameterType.TIPO_NUMERICO,
//                    "30", "Cantidad de dias atras para visulizar en la bitacora y logs del sistema")));
//        }
    }

    public void parametrosServerSocket() {
        if (!parametroRepository.findById(ParametroID.ServerSocket.SS_FRECUENCIA_PING).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSocket.SS_FRECUENCIA_PING,
                    "SS_FRECUENCIA_PING", ParameterType.TIPO_NUMERICO,
                    "60000",
                    "Es la frecuencia de tiempo en que hara la verificacion de las conexiones mediante un PING, en milisegundos.")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSocket.SS_MAXIMO_NUMERO_CONEXIONES).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSocket.SS_MAXIMO_NUMERO_CONEXIONES,
                    "SS_MAXIMO_NUMERO_CONEXIONES", ParameterType.TIPO_NUMERICO,
                    "5",
                    "Es el numero de conexiones maximo que atendera el servidor socket.")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSocket.SS_NUMERO_MAXIMO_INTENTOS_PING).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSocket.SS_NUMERO_MAXIMO_INTENTOS_PING,
                    "SS_NUMERO_MAXIMO_INTENTOS_PING", ParameterType.TIPO_NUMERICO,
                    "5",
                    "Es el numero maximo de intentos que hara el servidor sokcet para verificar las conexiones.")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSocket.SS_TIEMPO_MAXIMO_ESPERA_CONEXION).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSocket.SS_TIEMPO_MAXIMO_ESPERA_CONEXION,
                    "SS_TIEMPO_MAXIMO_ESPERA_CONEXION", ParameterType.TIPO_NUMERICO,
                    "60000",
                    "El tiempo maximo de espera de una conexion, en milisegundos.")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSocket.SS_TIEMPO_MAXIMO_ESPERA_PING).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSocket.SS_TIEMPO_MAXIMO_ESPERA_PING,
                    "SS_TIEMPO_MAXIMO_ESPERA_PING", ParameterType.TIPO_NUMERICO,
                    "5000",
                    "El tiempo maximo de espera de la respuesta de un ping, en milisegundos.")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSocket.SS_SERVER_DIRECCION_IP).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSocket.SS_SERVER_DIRECCION_IP,
                    "SS_SERVER_DIRECCION_IP", ParameterType.TIPO_CADENA,
                    "localhost",
                    "Direccion Ip del servidor socket.")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSocket.SS_SERVER_PUERTO).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSocket.SS_SERVER_PUERTO,
                    "SS_SERVER_PUERTO", ParameterType.TIPO_NUMERICO,
                    "8051",
                    "Puerto de conexion hacia el servidor socket.")));
        }

    }

    public void parametrosServerSMTP() {
        if (!parametroRepository.findById(ParametroID.ServerSMTP.SMTP_HOST).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSMTP.SMTP_HOST,
                    "SMTP_HOST", ParameterType.TIPO_CADENA,
                    "smtp.gmail.com",
                    "Direccion del servidor SMTP")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSMTP.SMTP_PORT).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSMTP.SMTP_PORT,
                    "SMTP_PORT", ParameterType.TIPO_NUMERICO,
                    "587",
                    "Puerto del servidor SMTP")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSMTP.SMTP_FROM).isPresent()) {
            log.warn(parametroRepository.save(new Parameter(
                    ParametroID.ServerSMTP.SMTP_FROM,
                    "SMTP_FROM", ParameterType.TIPO_CADENA,
                    "alepaco.maton@gmail.com",
                    "Direccion de la empresa para el envio de correos electronicos")));
        }

        if (!parametroRepository.findById(ParametroID.ServerSMTP.SMTP_PASSWORD).isPresent()) {
            try {
                log.warn(parametroRepository.save(new Parameter(
                        ParametroID.ServerSMTP.SMTP_PASSWORD,
                        "SMTP_PASSWORD", ParameterType.TIPO_CADENA,
                        SymmetricEncoder.AESEncode(RULES_ENCODE, "alepaco.maton#123"),
                        "Contrasena del email de la empresa para notifiacionesp or correo electronico")));
            } catch (Exception ex) {
                log.error("Error "+ex.getMessage(), ex);
            }
        }

    }
    
    /**
     * Estructura Modulo Base
     */
    public void etiquetasModuloBase() {
        etiquetasPlantilla();
        etiquetasUsuario();
        etiquetasEtiqueta();
        etiquetasPerfil();
        etiquetasRol();
        etiquetasBitacora(); 
        etiquetasParametro();
    }

    public void modulosFormulariosModuloBase() {
        log.warn("***********************modulosFormulariosModuloBase**************************");

        seqModuloId++;
        int seqFormularioId = seqModuloId * intervaloModulo;

        if (!formularioRepository.findById(ActionPermissionId.MODULO_ADMINISTRACION).isPresent()) {
            log.warn(formularioRepository.save(new Form(ActionPermissionId.MODULO_ADMINISTRACION,
                    "Administración", 1, null, "/administracion", "fa fa-plus")));
        }

        if (!formularioRepository.findById(ActionPermissionId.Administration.FORMULARIO_USUARIOS).isPresent()) {
            log.warn(formularioRepository.save(new Form(seqFormularioId, "Usuarios", 3,
                    ActionPermissionId.MODULO_ADMINISTRACION, "/usuario", "fa fa-user-plus")));
        }

        accionesGestionUsuario();

        seqFormularioId++;

        if (!formularioRepository.findById(seqFormularioId).isPresent()) {
            log.warn(formularioRepository.save(new Form(seqFormularioId, "Roles", 2, seqModuloId, "/rol", "fa fa-list-alt")));
        }

        accionesGestionRol(seqFormularioId);

        seqFormularioId++;

        if (!formularioRepository.findById(seqFormularioId).isPresent()) {
//            log.warn(formularioRepository.save(new Form(seqFormularioId, "Grupos", 4, seqModuloId, "/grupo", "fa fa fa-group")));
        }

//        accionesGestionGrupo(seqFormularioId);
        seqFormularioId++;

        if (!formularioRepository.findById(seqFormularioId).isPresent()) {
            log.warn(formularioRepository.save(new Form(seqFormularioId, "Bitácora", 7, seqModuloId, "/bitacora", "fa fa-fw fa-desktop")));
        }

        accionesBitacora(seqFormularioId);

        seqFormularioId++;

        if (!formularioRepository.findById(seqFormularioId).isPresent()) {
            log.warn(formularioRepository.save(new Form(seqFormularioId, "Parámetros", 5, seqModuloId, "/parametro", "fa fa-table")));
        }

        accionesGestionParametro(seqFormularioId);

        seqFormularioId++;

        if (!formularioRepository.findById(seqFormularioId).isPresent()) {
            log.warn(formularioRepository.save(new Form(seqFormularioId, "Etiquetas", 6, seqModuloId, "/etiqueta", "fa fa-text-width")));
        }

        accionesGestionEtiqueta(seqFormularioId);

        seqFormularioId = 110; // arriglar con un calculo numerico

        if (!formularioRepository.findById(seqFormularioId).isPresent()) {
            log.warn(formularioRepository.save(new Form(seqFormularioId, "Perfil", 1, seqModuloId, "/perfil", "fa fa-user")));
        }

        accionesPerfil(seqFormularioId);

        seqFormularioId++;

        if (!formularioRepository.findById(seqFormularioId).isPresent()) {
//            log.warn(formularioRepository.save(new Form(seqFormularioId, "Logs del sistema", 8, seqModuloId, "/logs/sistema", "fa fa-bug")));
        }

//        accionesLogSistema(seqFormularioId);
        seqFormularioId++;

        if (!formularioRepository.findById(seqFormularioId).isPresent()) {
//            log.warn(formularioRepository.save(new Form(seqFormularioId, "Monitoreo", 9, seqModuloId, "/monitoreo", "fa fa-terminal")));
        }

//        accionesMonitoreo(seqFormularioId);
        seqFormularioId++;
    }

    public void parametrosModuloBase() {
        parametrosSistema();
        parametrosServerSMTP();
    }

    /**
     * Estructura de Versiones
     */
    public void moduloBase() {
        etiquetasModuloBase();

        modulosFormulariosModuloBase();
    }

    @PostConstruct
    public void init() {
        log.info("InicializacionService inicializado");

        Optional<Role> optional = rolRepository.findById(Role.SUPER_ADMINISTRADOR);

        Role rolSuperAdministrador;
        if (optional.isPresent()) {
            rolSuperAdministrador = optional.get();
        } else {
            rolSuperAdministrador = new Role(Role.SUPER_ADMINISTRADOR, "Super Administrador", "Super Administrador", true);
            log.warn(rolSuperAdministrador);
            jdbcTemplate.execute("INSERT INTO MU_ROLE VALUES (" + Role.SUPER_ADMINISTRADOR + ", 'Super Administrador', 'Super Administrador', true)");
        }

        if (!usuarioRepository.findById(User.SUPER_ADMINISTRADOR_ID).isPresent()) {
            log.warn(new User(User.SUPER_ADMINISTRADOR_ID, "admin", "admin", "$2a$11$fzI3VdQYtXoaKr2n7kwc9OyP9A16MKdCDY4LfM/22Q29QybVEbJCS",
                    UserType.SUPER_USUARIO, rolSuperAdministrador,
                    UserStatus.HABILITADO));
            jdbcTemplate.execute("INSERT INTO mu_user VALUES (" + User.SUPER_ADMINISTRADOR_ID
                    + ", 'admin', 'admin', '$2a$11$fzI3VdQYtXoaKr2n7kwc9OyP9A16MKdCDY4LfM/22Q29QybVEbJCS', "
                    + UserType.SUPER_USUARIO + ", " + Role.SUPER_ADMINISTRADOR + ", " + UserStatus.HABILITADO + ")");

        }

        parametrosModuloBase();

//        parametrosServerSocket();

        executor = Executors.newFixedThreadPool(50);

        executor.submit(() -> {
            // no se le manda el rolId porque siempre se tendra un solo usuario = super usuario, 
            // por lo cual tendra un solo rol = super administrador
            // los tipos de parametros siempre seran quemados ya que no cambiaran TipoParametro
            try {
                moduloBase();
                AppMovil();
            } catch (Exception aa) {
                log.error("Exception moduloBase()" + aa.getMessage(), aa);
            }
        });
    }

    private void AppMovil() {
        etiquetasAppMovil();

        modulosFormulariosAppMovil();
    }

    private void etiquetasAppMovil() {
        etiquetasAccount();
    }

    private void modulosFormulariosAppMovil() {
        log.warn("***********************modulosFormulariosModuloBase**************************");

        seqModuloId++;

        if (!formularioRepository.findById(ActionPermissionId.MODULO_APP_MOVIL).isPresent()) {
            log.warn(formularioRepository.save(new Form(ActionPermissionId.MODULO_APP_MOVIL,
                    "App Movil", 2, null, "/app/movil", "fa fa-mobile")));
        }

        if (!formularioRepository.findById(ActionPermissionId.AppMovil.FORMULARIO_ACCOUNT).isPresent()) {
            log.warn(formularioRepository.save(new Form(ActionPermissionId.AppMovil.FORMULARIO_ACCOUNT, "Cuentas", 1,
                    ActionPermissionId.MODULO_APP_MOVIL, "/account", "fa fa-address-card")));
        }

        actionsManagerAccount(ActionPermissionId.AppMovil.FORMULARIO_ACCOUNT);
    }

    private void etiquetasAccount() {
        final String grupo = "account";

        HashMap<String, String> etiquetas = new HashMap<>();

        etiquetas.put("title", "Gestión de Cuentas");
        etiquetas.put("tbl.id", "Id");
        etiquetas.put("tbl.user.name", "Usuario");
        etiquetas.put("tbl.full.name", "Nombre completo");
        etiquetas.put("tbl.type.device", "Dispositivo");
        etiquetas.put("tbl.status", "Estado");
//        etiquetas.put("", "");

        etiquetas.entrySet().stream().forEach((entry) -> {
            String llave = entry.getKey();
            String value = entry.getValue();
            if (etiquetaRepository.findByGroupLevelAndKey(grupo, llave) == null) {
                log.warn(etiquetaRepository.save(new Label(null, llave, value, grupo, true)));
            }
        });
    }

    private void actionsManagerAccount(int formId) {
        int seqAccionId = 2100;

        if (!accionRepository.findById(seqAccionId).isPresent()) {
            log.warn(accionRepository.save(new Action(seqAccionId, formId, 
                    "Navegación", "/accounts,/accounts/", "GET,GET")));
        }
        if (rolAccionRepository.findByRoleIdAndActionId(Role.SUPER_ADMINISTRADOR, seqAccionId) == null) {
            log.warn(rolAccionRepository.save(new RoleAction(new RoleActionPK(Role.SUPER_ADMINISTRADOR, seqAccionId))));
        }
        
        seqAccionId++;
    }

}
