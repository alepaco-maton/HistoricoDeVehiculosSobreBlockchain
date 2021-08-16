/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources.dto.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author alepaco.maton
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class ModuleResponse implements Serializable, Comparable<ModuleResponse> {

    private int id;
    private String name;
    private int position;
    private int type;
    private String url;
    private String icono;

    private List<FormResponse> forms = new ArrayList<>();

    public void sortFormularios() {
        Collections.sort(forms);
    }
    
    @Override
    public int compareTo(ModuleResponse o) {
        return Integer.compare(this.getPosition(), o.getPosition());
    }

}
