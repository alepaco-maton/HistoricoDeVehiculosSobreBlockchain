/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.commons.Actions; 
import inv.stamina.modulobase.service.FileService;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream; 
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author alepaco.com
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value = "/files", produces = {MediaType.APPLICATION_JSON_VALUE})
public class FileController extends GenericController {

    private static final long serialVersionUID = -4600125266089747654L;

    @Autowired
    private FileService service;

    @GetMapping
    public ResponseEntity<Resource> descargar(
            @RequestParam int fileId 
            ) throws RuntimeException {
        String ipClient = getIpAdress("");
        String token = "Sin token";
        String form = "desconocido";
 
        printRequest(Stream.of(
                new AbstractMap.SimpleEntry<>("token ", token),
             //   new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                new AbstractMap.SimpleEntry<>("form ", form),
                new AbstractMap.SimpleEntry<>("fileId ", fileId)).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        Resource resource = service.downloadFile(token, ipClient,
                form, fileId);

        if (resource == null) {
            bitacoraService.saveBitacoraWithoutToken(token, ipClient, form, Actions.FETCH_FILE);
            throw new RuntimeException("No se encontro el adjunto.");
        }

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = this.httpServletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception ex) {
            log.info("No se pudo determinar el tipo del archivo.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        printResponse(Stream.of( 
                new AbstractMap.SimpleEntry<>("fileName ", resource.getFilename()),
                new AbstractMap.SimpleEntry<>("contentType ", contentType)).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
 

    @GetMapping("/{fileId}/{name}")
    public ResponseEntity<Resource> descargar2( 
            @PathVariable int fileId,
            @PathVariable String name 
            ) throws RuntimeException {
        String ipClient = getIpAdress("");
        String token = "Sin token";
        String form = "desconocido";
 
        printRequest(Stream.of(
                new AbstractMap.SimpleEntry<>("token ", token),
             //   new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                new AbstractMap.SimpleEntry<>("form ", form),
                new AbstractMap.SimpleEntry<>("fileId ", fileId)).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        Resource resource = service.downloadFile(token, ipClient,
                form, fileId);

        if (resource == null) {
            bitacoraService.saveBitacoraWithoutToken(token, ipClient, form, Actions.FETCH_FILE);
            throw new RuntimeException("No se encontro el adjunto.");
        }

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = this.httpServletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception ex) {
            log.info("No se pudo determinar el tipo del archivo.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        printResponse(Stream.of( 
                new AbstractMap.SimpleEntry<>("fileName ", resource.getFilename()),
                new AbstractMap.SimpleEntry<>("contentType ", contentType)).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/{fileId}/abkbakjbajkb.jpeg")
    public ResponseEntity<Resource> descargar3( 
            @PathVariable int fileId,
            @PathVariable String name 
            ) throws RuntimeException {
        String ipClient = getIpAdress("");
        String token = "Sin token";
        String form = "desconocido";
 
        printRequest(Stream.of(
                new AbstractMap.SimpleEntry<>("token ", token),
             //   new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                new AbstractMap.SimpleEntry<>("form ", form),
                new AbstractMap.SimpleEntry<>("fileId ", fileId)).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        Resource resource = service.downloadFile(token, ipClient,
                form, fileId);

        if (resource == null) {
            bitacoraService.saveBitacoraWithoutToken(token, ipClient, form, Actions.FETCH_FILE);
            throw new RuntimeException("No se encontro el adjunto.");
        }

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = this.httpServletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception ex) {
            log.info("No se pudo determinar el tipo del archivo.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        printResponse(Stream.of( 
                new AbstractMap.SimpleEntry<>("fileName ", resource.getFilename()),
                new AbstractMap.SimpleEntry<>("contentType ", contentType)).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
 

    
}
