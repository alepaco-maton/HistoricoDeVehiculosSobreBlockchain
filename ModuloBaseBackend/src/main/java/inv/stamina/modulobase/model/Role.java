package inv.stamina.modulobase.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author alepaco.maton
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "MU_ROLE")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final int SUPER_ADMINISTRADOR = 1;

    @Id
    @SequenceGenerator(name = "SEQ_MU_ROLE", sequenceName = "SEQ_MU_ROLE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MU_ROLE")
    private Integer id;
    private String name;
    private String description;
    private boolean status;

}
