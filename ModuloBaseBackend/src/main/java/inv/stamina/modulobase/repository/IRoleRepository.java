/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import inv.stamina.modulobase.model.Role;

/**
 *
 * @author alepaco.maton
 */
@Repository
public interface IRoleRepository extends JpaRepository<Role, Integer> {

    Role findByNameAndStatusTrue(String nombre);

    Page<Role> findAllByIdNotAndStatusTrue(int id, Pageable pageable);

    @Query(value = "select r.* "
            + "from MU_ROLE r "
            + "where "
            + "(-1 = ? or UPPER(r.name) like ?) "
            + "and (-1 = ? or UPPER(r.description) like ?) "
            + "and r.status = true and r.id not in (?) ", nativeQuery = true)
    Page<Role> filter(int flag1, String name,
            int flag2, String description, int idroladministrador,
            Pageable pageable);

}
