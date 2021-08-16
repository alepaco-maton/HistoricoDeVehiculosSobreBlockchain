/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.annotation.Version;

/**
 *
 * @author alepaco.maton
 */
@ToString(exclude = "password")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "MU_USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUPER_ADMINISTRADOR_ID = 1;

    @Id
    @SequenceGenerator(name = "SEQ_MU_USER", sequenceName = "SEQ_MU_USER", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MU_USER")
    private Integer id;
    private String userName;
    private String fullName;
    private String password;
    private short type;
    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private Role roleId;
    private short status;

}
