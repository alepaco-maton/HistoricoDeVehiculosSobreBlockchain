/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author alpaco.maton
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RoleActionPK implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer roleId;
    private Integer actionId;
    
}
