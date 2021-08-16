/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

/**
 *
 * @author alepaco.com
 */
public class StaminaException extends RuntimeException {
    
    private static final long serialVersionUID = 7416858242595203185L;

    public StaminaException(String message) {
        super(message);
    }
}
