/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

import inv.stamina.modulobase.model.Account;
import inv.stamina.modulobase.model.Category;
import inv.stamina.modulobase.model.PictogramShortcut;
import inv.stamina.modulobase.resources.dto.AccountResponse;
import inv.stamina.modulobase.resources.dto.CategoryResponse;
import inv.stamina.modulobase.resources.dto.PictogramShortcutResponse;

/**
 *
 * @author alepaco.maton
 */
public class AppMovilConvertTool {

    public static PictogramShortcutResponse convert(PictogramShortcut model) {
        return new PictogramShortcutResponse(model.getId(), model.getTitle(),
                model.getDescription(), model.getType(), 
                model.getFileId(), model.getCategoryId());
    }

    public static AccountResponse convert(Account model) {
        return new AccountResponse(model.getId(), model.getEmail(),
                model.getUserName(), model.getFullName(),
                model.getTypeDevice(), model.getStatus());
    }

    public static CategoryResponse convert(Category model) {
        return new CategoryResponse(model.getId(), model.getName(), model.getDescription(),
                model.getType(), model.getAccountId());
    }

}
