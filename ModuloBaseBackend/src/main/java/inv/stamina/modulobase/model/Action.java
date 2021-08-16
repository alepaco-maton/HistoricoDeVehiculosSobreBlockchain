package inv.stamina.modulobase.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author alepaco.maton
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "MU_ACTION")
public class Action implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private Integer id;
    private Integer formId;
    private String name;
    private String url;
    private String method;

}
