/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;

import inv.stamina.modulobase.model.AppFile;
import inv.stamina.modulobase.model.Category;
import inv.stamina.modulobase.model.PictogramShortcut;
import inv.stamina.modulobase.repository.ICategoryRepository;
import inv.stamina.modulobase.repository.IFileRepository;
import inv.stamina.modulobase.repository.IPictogramShortcutRepository;
import inv.stamina.modulobase.resources.dto.PictogramShortcutRequest;
import inv.stamina.modulobase.util.abm.CategoryType;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Component
public class PictogramShortcutValidator extends GlobalValidator {

    @Autowired
    IFileRepository fileRepository;

    @Autowired
    IPictogramShortcutRepository repository;

    @Autowired
    ICategoryRepository categoryRepository;

    public String validate(PictogramShortcutRequest input, Integer id) {
        if (isBlanck(input.getTitle())) {
            return "El número de caracteres del nombre de usuario debe ser mayor a 0 y menor o igual a 50.";
        }

        if (input.getCategoryId() == null) {
            return "El número de caracteres del nombre de usuario debe ser mayor a 0 y menor o igual a 50.";
        }

        if (input.getType() != CategoryType.CATEGORY_PICTOGRAM
                && input.getType() != CategoryType.CATEGORY_SHORTCUT) {
            return "El número de caracteres del nombre de usuario debe ser mayor a 0 y menor o igual a 50.";
        }

        if (input.getFileId() != null) {
            Optional<AppFile> optional = fileRepository.findById(input.getFileId());

            if (!optional.isPresent()) {
                return "El número de caracteres del nombre de usuario debe ser mayor a 0 y menor o igual a 50.";
            }
        }

        {
            Optional<Category> optional = categoryRepository.findById(input.getCategoryId());

            if (!optional.isPresent()) {
                return "El número de caracteres del nombre de usuario debe ser mayor a 0 y menor o igual a 50.";
            }
        }

        if (id != null) {
            Optional<PictogramShortcut> model = repository.findById(id);

            if (!model.isPresent()) {
                return "Identificador de usuario invalido.";
            } else {
                PictogramShortcut temp = repository.findByTitle(input.getTitle());

                if (temp != null && !temp.getId().equals(model.get().getId())) {
                    return "El nombre de usuario, se encuentra en uso.";
                }
            }
        } else {
            PictogramShortcut temp = repository.findByTitle(input.getTitle());

            if (temp != null) {
                return "El nombre de usuario, se encuentra en uso.";
            }
        }

        return "";
    }

    public void validate(PictogramShortcutRequest request, Integer id, BindingResult errors) {
        String error = validate(request, id);
        
        if (!isBlanck(error)) {
            errors.rejectValue("title", "field.title", error);
        }
    }

}
