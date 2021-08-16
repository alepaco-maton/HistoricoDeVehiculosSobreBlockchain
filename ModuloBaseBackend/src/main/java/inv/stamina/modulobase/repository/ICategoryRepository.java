/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.repository;

import inv.stamina.modulobase.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author alepaco.maton
 */
@Repository
public interface ICategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findAllByAccountIdAndType(Integer accountId, short type);

    Category findByName(String name);

}
