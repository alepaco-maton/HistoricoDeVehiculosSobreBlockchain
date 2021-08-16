/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources.dto.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author alepaco.maton
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(exclude = "password")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuteticationRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    @NotNull(message = "Introdusca el nombre de usuario")
    @Size(min = 1,max = 50,message = "Nombre de usuario invalido, debe tener almenos 1 caracter y no mas de 50 caracteres.")
    private String userName;
    @NotNull(message = "Introdusca la constraseña")
    @Size(min = 1,max = 100,message = "Contraseña invalida, debe ser tener almenos un caracter y no mas de 100 caracteres.")
    private String password;
    
    private String source;

}
