/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.repository;

import inv.stamina.modulobase.model.RoleAction;
import inv.stamina.modulobase.model.RoleActionPK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author alepaco.maton
 */
@Repository
public interface IRoleActionRepository extends JpaRepository<RoleAction, RoleActionPK> {
    
    @Query(value = "select * from MU_ROLE_ACTION u where u.ROLE_ID = ? ",nativeQuery = true)
    List<RoleAction> findAllByRoleId(int roleId);

    @Query(value = "select * from MU_ROLE_ACTION u where u.ROLE_ID = ? and u.ACTION_ID = ? ",nativeQuery = true)
    RoleAction findByRoleIdAndActionId(int roleId, int actionId);
    
}
