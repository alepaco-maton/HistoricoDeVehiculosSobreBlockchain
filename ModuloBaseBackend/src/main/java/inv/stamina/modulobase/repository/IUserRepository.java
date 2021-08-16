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
import inv.stamina.modulobase.model.User;

/**
 *
 * @author alepaco.maton
 */
@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {

    User findByUserNameAndStatusIn(String userName, Collection<Short> status); 

    long countByStatusInAndRoleIdId(Collection<Short> status, int roleId);

    @Query(value = "select u.* "
            + "from MU_USER u inner join MU_ROLE r "
            + "on u.role_id = r.id  "
            + "and (-1 = ? or cast(u.id  as varchar) = ?) "
            + "and (-1 = ? or UPPER(u.user_name) like ?) "
            + "and (-1 = ? or UPPER(u.full_name) like ?) "
            + "and (-1 = ? or cast(r.id as varchar) = ?) "
            + "and (-1 = ? or UPPER(r.name) = ?) "
            + "and (-1 = ? or cast(u.type  as varchar) = ?) "
            + "and (-1 = ? or cast(u.status  as varchar) = ?) "
            + "and u.type <> ? ", nativeQuery = true)
    Page<User> filter(
            int flag0, String id,
            int flag1, String userName,
            int flag2, String fullName,
            int flag6, String roleId,
            int flag3, String roleName,
            int flag4, String type,
            int flag5, String status,
            short typeAdmin,
            Pageable pageable);

    User findByIdAndRoleIdIdAndStatus(int id, int roleId, short satus); 
   
    User findByUserName(String userName);

}
