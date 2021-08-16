/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import inv.stamina.modulobase.model.Label;

/**
 *
 * @author alepaco.maton
 */
@Repository
public interface ILabelRepository extends JpaRepository<Label, Integer> {

    Page<Label> findAllByStatusTrue(Pageable pagination);

    List<Label> findAllByKeyIn(Collection<String> llaves);

    List<Label> findAllByGroupLevelIn(Collection<String> GroupLevels);

    Label findByGroupLevelAndKey(String GroupLevel, String llave);
    
    @Query(value = "select u.* "
            + "from MU_LABEL u "
            + "where  "
            + "(-1 = ? or UPPER(u.key) like ?) "
            + "and (-1 = ? or UPPER(u.value) like ?) "
            + "and (-1 = ? or UPPER(u.group_level) like ?) ", nativeQuery = true)
    Page<Label> filter(int flag1, String key,
    					int flag2,String value,
			    		int flag3,String groupLevel,
			    		Pageable pageable);

}
