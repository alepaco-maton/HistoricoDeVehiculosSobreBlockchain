/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.scheduler;

import inv.stamina.modulobase.service.ParameterService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author alepaco.com
 */
@Log4j2
@Component
public class ScheduledTask {
    
//    @Autowired
//    ParameterService parametroService;

    //@Scheduled(cron = "${cron.expression.limpiar.zip.temporales}")
    public void lsy() {
//        final Path carpetaTemporal;
//
//        final String direccionCarpeta = (String) parametroService.getParametro(ParametroID.CaseFraude.Investigacion.CI_CARPETA_ARCHIVOS_ZIP_TEMPORAL_EXPORTACION).getValor();
//
//        carpetaTemporal = Paths.get(direccionCarpeta)
//                .toAbsolutePath().normalize();
//
//        try {
//            Files.createDirectories(carpetaTemporal);
//
//            carpetaTemporal.toFile().delete();
//
//            logSistemaService.info(Apps.TRAZABILIDAD_SISTEMA,
//                    Acciones.CASO_INVESTIGACION_LIMPIAR_ARCHIVOS_TEMPORALES_ZIP,
//                    "Archivos temporales eliminados. " + direccionCarpeta);
//        } catch (Exception ex) {
//            logSistemaService.warn(Apps.TRAZABILIDAD_SISTEMA,
//                    Acciones.CASO_INVESTIGACION_LIMPIAR_ARCHIVOS_TEMPORALES_ZIP,
//                    "No se pudo obtener el directorio de la carpeta de archivos "
//                    + direccionCarpeta + ", " + ex.getMessage(), ex);
//        }
    }

    @Scheduled(cron = "${cron.expression.crear.secuencial.investigacion}")
    public void crearSecuencialInvestigacion() { 
//        final int gestion = DateUtil.obtenerGestion();
//        casoInvestigacionService.crearSecuencialInvestigacion(gestion);
    }

}
