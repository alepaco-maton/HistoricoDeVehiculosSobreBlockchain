/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.repository;

import inv.stamina.modulobase.model.Bitacora; 
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
public interface IBitacoraRepository extends JpaRepository<Bitacora, Long> {

    @Query(value = "select * "
            + "from MU_BITACORA u "
            + "where (-1 = ? or (to_char(u.date_action, 'DD/MM/YYYY HH24:MI:SS') LIKE ?)) "
            + "and (-1 = ? or UPPER(user_action) like ?) "
            + "and (-1 = ? or UPPER(ip_adress) like ?) "
            + "and (-1 = ? or UPPER(source) like ?) "
            + "and (-1 = ? or UPPER(user_name) like ?)", nativeQuery = true)
    Page<Bitacora> filter(int dateBool, String date,
            int actionBool, String userAction, int ipAdressBool, String ipAdress, int formBool, String form,
            int userNameBool, String userName, Pageable pageable);

    Page<Bitacora> findByIdAndUserAction(long id, String userAction, Pageable pageable);

}
