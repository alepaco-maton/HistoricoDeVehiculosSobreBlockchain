/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;

/**
 *
 * @author alepaco.maton
 */
public class GlobalValidator {

    public boolean isBlanck(String dato) {
        return dato == null || dato.trim().isEmpty();
    }

}
