/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;

import inv.stamina.modulobase.commons.AppMovilConvertTool;
import inv.stamina.modulobase.model.PictogramShortcut;
import inv.stamina.modulobase.repository.IPictogramShortcutRepository;
import inv.stamina.modulobase.resources.dto.PictogramShortcutRequest;
import inv.stamina.modulobase.resources.dto.PictogramShortcutResponse;
import inv.stamina.modulobase.validator.PictogramShortcutValidator;
import inv.stamina.modulobase.commons.Actions;
import inv.stamina.modulobase.commons.StaminaException;
import inv.stamina.modulobase.model.Account;
import inv.stamina.modulobase.model.AppFile;
import inv.stamina.modulobase.repository.IAccountRepository;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.service.FileService; 
import inv.stamina.modulobase.util.abm.AccountStatus;
import inv.stamina.modulobase.util.abm.FileType;
import inv.stamina.modulobase.util.abm.PictogramShortcutType;
import inv.stamina.modulobase.validator.ApiException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream; 
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@RestController
@CrossOrigin
@RequestMapping(value = "/pictograms/shortcuts", produces = {MediaType.APPLICATION_JSON_VALUE})
public class PictogramShortcutController extends GenericController {
    
    private static final long serialVersionUID = 9081953002167441022L;

    @Autowired
    IPictogramShortcutRepository repository;

    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    PictogramShortcutValidator validator;

    @Autowired
    FileService fileService;
 
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<PictogramShortcutResponse> createShortcut(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form, 
            @RequestBody PictogramShortcutRequest request, BindingResult result) throws URISyntaxException, ApiException {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            validator.validate(request, null, result);

            if (result.hasErrors()) {
                bitacoraService.saveBitacora(token, ipClient, form, getErrors(result));

                throw new ApiException(result, "Errores en la validacion");
            }

            PictogramShortcut model = repository.save(new PictogramShortcut(null,
                    request.getTitle(), request.getDescription(), request.getType(),
                    request.getFileId(), request.getCategoryId()));

            bitacoraService.saveBitacora(token, ipClient, form, Actions.ROL_CREAR);

            ResponseEntity<PictogramShortcutResponse> out = ResponseEntity.created(
                    new URI("/roles/" + model.getId()))
                    .body(AppMovilConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }

            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }
     
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PictogramShortcutResponse> updatePictorgamShortcut(
            @RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form, 
            @PathVariable("id") Integer id, 
            @RequestBody PictogramShortcutRequest request, BindingResult result) throws ApiException {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("id ", id),
                    new AbstractMap.SimpleEntry<>("request ", request)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            validator.validate(request, id, result);

            if (result.hasErrors()) {
                bitacoraService.saveBitacora(token, ipClient, form, getErrors(result));

                throw new ApiException(result, "Errores en la validacion");
            }

            PictogramShortcut model = repository.findById(id).get();

            model.setTitle(request.getTitle());
            model.setDescription(request.getDescription());
            model.setType(request.getType());
            model.setFileId(request.getFileId());
            model.setCategoryId(request.getCategoryId());

            model = repository.save(model);

            bitacoraService.saveBitacora(token, ipClient, form, Actions.USUARIO_MODIFICAR);

            ResponseEntity<PictogramShortcutResponse> out = ResponseEntity.ok().
                    body(AppMovilConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }

            final String mensajeError = "Error al modificar un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }
    
    @PostMapping(value = "/file", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPictogram(
            @RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @RequestHeader("file") MultipartFile file,
            @RequestHeader("title") String title,
            @RequestHeader("description") String description,
            @RequestHeader("categoryId") Integer categoryId) throws Exception {

        Integer tempId = null;

        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("title ", title),
                    new AbstractMap.SimpleEntry<>("description ", (description == null) ? "" : description),
                    new AbstractMap.SimpleEntry<>("categoryId ", categoryId),
                    new AbstractMap.SimpleEntry<>("file ", FileService.fileToString(file)),
                    new AbstractMap.SimpleEntry<>("form ", form)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            PictogramShortcutRequest request = new PictogramShortcutRequest(title, description,
                    PictogramShortcutType.PICTOGRAM, null, categoryId);

            String error = validator.validate(request, null);

            if (!isBlanck(error)) {
                bitacoraService.saveBitacora(token, ipClient, form, error);

                throw new StaminaException(error);
            }

            Account account = accountRepository.findByUserNameAndStatusIn(
                    jwtTokenUtil.getUsernameFromToken(token), Arrays.asList(AccountStatus.ENABLE));

            final String pictogramFolder = "/" + account.getId() + "/pictograms/";

            AppFile temp = fileService.uploadFile(getUserName(), ipClient,
                    form, file, pictogramFolder, account.getId(), FileType.PICTOGRAM);

            tempId = temp.getId();

            request.setFileId(tempId);

            PictogramShortcut model = repository.save(new PictogramShortcut(null,
                    request.getTitle(), request.getDescription(), request.getType(),
                    request.getFileId(), request.getCategoryId()));

            bitacoraService.saveBitacora(token, ipClient, form, Actions.ROL_CREAR);

            ResponseEntity<PictogramShortcutResponse> out = ResponseEntity.created(
                    new URI("/roles/" + model.getId()))
                    .body(AppMovilConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (e instanceof StaminaException) {
                throw e;
            }

            if (tempId != null) {
                fileService.deleteFile(getUserName(), ipClient, form, tempId);
            }

            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw new RuntimeException("Error en el proceso, comuniquese con el administrador. "
                    + e.getMessage());
        }
    }

    @PutMapping(value = "/file/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updatePictogram(
            @RequestHeader(value = JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(value = JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(value = JwtTokenUtil.FORM) String form,
            @RequestHeader("file") MultipartFile file,
            @RequestHeader("title") String title,
            @PathVariable("id") Integer id,
            @RequestHeader("description") String description, 
            @RequestHeader("categoryId") Integer categoryId) throws RuntimeException {

        Integer tempId = null;

        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("title ", title),
                    new AbstractMap.SimpleEntry<>("id ", id),
                    new AbstractMap.SimpleEntry<>("description ", (description == null) ? "" : description),
                    new AbstractMap.SimpleEntry<>("categoryId ", categoryId), 
                    new AbstractMap.SimpleEntry<>("file ", FileService.fileToString(file)),
                    new AbstractMap.SimpleEntry<>("form ", form)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            PictogramShortcutRequest request = new PictogramShortcutRequest(title, description,
                    PictogramShortcutType.PICTOGRAM, null, categoryId);

            String error = validator.validate(request, id);

            if (!isBlanck(error)) {
                bitacoraService.saveBitacora(token, ipClient, form, error);

                throw new StaminaException(error);
            }

            Account account = accountRepository.findByUserNameAndStatusIn(
                    jwtTokenUtil.getUsernameFromToken(token), Arrays.asList(AccountStatus.ENABLE));

            {
                final String pictogramFolder = "/" + account.getId() + "/pictograms/";

                AppFile temp = fileService.uploadFile(getUserName(), ipClient,
                        form, file, pictogramFolder, account.getId(), FileType.PICTOGRAM);

                tempId = temp.getId();

                request.setFileId(tempId);
            }

            PictogramShortcut model = repository.getOne(id);
            
            Integer tempFileId = model.getFileId();
            
            model.setCategoryId(request.getCategoryId());
            model.setDescription(request.getDescription());
            model.setTitle(request.getTitle());
            model.setType(request.getType());
            model.setFileId(request.getFileId());

            repository.save(model);
            repository.flush();

            if (tempFileId != null) {
                fileService.deleteFile(getUserName(), ipClient, form, tempFileId);
            }
            
            bitacoraService.saveBitacora(token, ipClient, form, Actions.ROL_MODIFICAR);

            ResponseEntity<PictogramShortcutResponse> out = ResponseEntity.ok()
                    .body(AppMovilConvertTool.convert(model));

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            if (tempId != null) {
                fileService.deleteFile(getUserName(), ipClient, form, tempId);
            }
            
            if (e instanceof StaminaException) {
                throw e;
            }

            final String mensajeError = "Error al crear un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw new RuntimeException("Error en el proceso, comuniquese con el administrador. "
                    + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@RequestHeader(JwtTokenUtil.KEY_TOKEN) String token,
            @RequestHeader(JwtTokenUtil.IP_CLIENT) String ipClient,
            @RequestHeader(JwtTokenUtil.FORM) String form,
            @PathVariable Integer id) throws NoHandlerFoundException, Exception {
        try {
            ipClient = getIpAdress(ipClient);

            printRequest(Stream.of(
                    new AbstractMap.SimpleEntry<>("token ", token),
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("ipClient ", ipClient),
                    new AbstractMap.SimpleEntry<>("form ", form),
                    new AbstractMap.SimpleEntry<>("id ", id)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Optional<PictogramShortcut> temp = repository.findById(id);

            if (!temp.isPresent()) {
                throw new NoHandlerFoundException("DELETE", "/" + id, HttpHeaders.EMPTY);
            }

            PictogramShortcut model = temp.get();
            repository.delete(model);
            repository.flush();

            if (model.getFileId() != null) {
                fileService.deleteFile(getUserName(), ipClient, form, model.getFileId());
            }

            bitacoraService.saveBitacora(token, ipClient, form, Actions.ROL_ELIMINAR);

            ResponseEntity<Object> out = ResponseEntity.ok().build();

            printResponse(Stream.of(
                    new AbstractMap.SimpleEntry<>("trazabilidad ", getUserName()),
                    new AbstractMap.SimpleEntry<>("response ", out)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            return out;
        } catch (Exception e) {
            final String mensajeError = "Error al eliminar un rol, " + e.getMessage();

            bitacoraService.saveBitacora(token, ipClient, form,
                    mensajeError);

            throw e;
        }
    }
    
}
