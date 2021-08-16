/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.service;

import inv.stamina.modulobase.commons.Actions;
import inv.stamina.modulobase.commons.FileTool;
import inv.stamina.modulobase.commons.ParametroID;
import inv.stamina.modulobase.commons.StaminaException;
import inv.stamina.modulobase.model.AppFile;
import inv.stamina.modulobase.repository.IFileRepository;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author alepaco.com
 */
@Log4j2
@Service
@Component
public class FileService {

    @Autowired
    protected BitacoraService bitacoraService;

    @Autowired
    ParameterService parameterService;

    @Autowired
    IFileRepository repository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public static String fileToString(MultipartFile file) {
        StringBuilder sb = new StringBuilder("(").append(file.getName()).
                append(" - ").append(file.getContentType()).
                append(" - ").append(file.getSize()).
                append(" - ").append(file.getOriginalFilename()).append(")");

        sb.append("]");

        return sb.toString();
    }

    public static String filesToString(MultipartFile[] archivos) {
        StringBuilder sb = new StringBuilder("[");
        for (MultipartFile archivo : archivos) {
            sb.append(fileToString(archivo)).append(", ");
        }

        sb.append("]");

        return sb.toString();
    }

    public AppFile uploadFile(String token, String direccionIp,
            String form, MultipartFile file, String nameFolder, int accountId,
            short type) throws StaminaException {

        final Path folder;

        {
            final String locationFiles = (String) parameterService.getParamVal(
                    ParametroID.LOCATION_FILES);

            folder = Paths.get(locationFiles + nameFolder)
                    .toAbsolutePath().normalize();

            try {
                Files.createDirectories(folder);
            } catch (Exception ex) {
                final String mensaje = "No se pudo crear el directorio de la carpeta de archivos "
                        + folder + ", " + ex.getMessage();

                throw new StaminaException(mensaje);
            }
        }

        try {
            Files.createDirectories(folder);
        } catch (Exception ex) {
            final String mensaje = "No se pudo crear el directorio de la carpeta de archivos "
                    + folder + ", " + ex.getMessage();

            throw new StaminaException(mensaje);
        }

        {
            String nombreArchivo = StringUtils.cleanPath(file.getOriginalFilename());

            try {
                Path targetLocation = folder.resolve(nombreArchivo);

                File archivoTemp = targetLocation.toFile();

                if (archivoTemp.exists()) {
                    String extencion = FileTool.getExtention(archivoTemp.getName());
                    nombreArchivo = FileTool.killExtention(archivoTemp.getName())
                            + "_" + (new Date().getTime()) + extencion;
                    targetLocation = folder.resolve(nombreArchivo);
                }

                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                AppFile model = new AppFile(null, targetLocation.toFile().getAbsolutePath(),
                        accountId, type);

                repository.save(model);

                return model;
            } catch (IOException ex) {
                final String mensajeError = "Error al intentar adjuntar el archivo " + nombreArchivo
                        + ", " + ex.getMessage();

                throw new StaminaException(mensajeError);
            }
        }
    }

    public String uploadFiles(String token, String direccionIp,
            String form, MultipartFile[] files, String nameFolder,
            int accountId, short type) {

        List<AppFile> temps = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                AppFile temp = uploadFile(token, direccionIp, form, file, nameFolder, accountId, type);

                log.info("archivo subido " + temp);

                bitacoraService.saveBitacora(token, direccionIp, form, Actions.UPLOAD_FILE);

                temps.add(temp);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);

                bitacoraService.saveBitacora(token, direccionIp, form, Actions.UPLOAD_FILE + ex.getMessage());

                temps.stream().forEach((temp) -> {
                    File ft = new File(temp.getLocation());
                    ft.delete();
                });

                return ex.getMessage();
            }
        }

        return null;
    }

    public File fetchFile(String userName, String direccionIp,
            String form, int fileId) throws StaminaException {

        AppFile model = repository.getOne(fileId);

        File temp = new File(model.getLocation());

        if (!temp.exists()) {
            bitacoraService.saveBitacoraWithoutToken(userName, direccionIp, form,
                    Actions.FETCH_FILE + "Archivo no encontrado");
            {
                return null;
            }
        }

        return temp;
    }

    public Resource downloadFile(String userName, String direccionIp,
            String form, Integer fileId) throws StaminaException {
        File file = fetchFile(userName, direccionIp, form, fileId);

        if (file == null) {
            return null;
        }

        Path filePath = Paths.get(file.getAbsolutePath())
                .toAbsolutePath().normalize();

        Resource resource;

        try {
            resource = new UrlResource(filePath.toUri());
        } catch (Exception ex) {
            final String mensajeError = " No se pudo obtener la el archivo a descargar " + ex.getMessage();

            bitacoraService.saveBitacoraWithoutToken(userName, direccionIp, form,
                    Actions.FETCH_FILE + mensajeError);

            throw new StaminaException(mensajeError);
        }

        if (resource.exists()) {
            return resource;
        } else {
            return null;
        }
    }

    public void deleteFile(String userName, String direccionIp,
            String form, Integer fileId) throws StaminaException {
        File file = fetchFile(userName, direccionIp, form, fileId);

        if (file != null && file.exists()) {
            file.delete();
        }

        repository.deleteById(fileId);

        bitacoraService.saveBitacoraWithoutToken(userName, direccionIp, form,
                Actions.FETCH_FILE);
    }

}
