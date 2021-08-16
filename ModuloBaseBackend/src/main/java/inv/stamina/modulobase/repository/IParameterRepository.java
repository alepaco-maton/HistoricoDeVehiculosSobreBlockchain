/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.repository;

import inv.stamina.modulobase.model.Parameter;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author alepaco.maton
 */
@Repository
public interface IParameterRepository extends JpaRepository<Parameter, Integer> {

    Parameter findByName(String name);

    @Query(value = "select p.* "
            + "from MU_PARAMETER p "
            + "where (-1 = ? or UPPER(p.name) like ?) "
            + "and (-1 = ? or UPPER(p.value) like ?) "
            + "and (-1 = ? or UPPER(p.description) like ?) ", nativeQuery = true)
    Page<Parameter> filter(int flag1, String name,
            int flag2, String value,
            int flag3, String description,
            Pageable pageable);

}
