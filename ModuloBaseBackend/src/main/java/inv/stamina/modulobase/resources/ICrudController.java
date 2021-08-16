/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.resources;
 
import java.io.Serializable; 

/**
 *
 * @author alepaco.maton
 * @param <R>
 * @param <T>
 * @param <P>
 */
public interface ICrudController<R extends Serializable, T extends Serializable, P> 
        extends IListController<T>, ICreateController<R, T>, IUpdateController<R, T, P>, 
        IDeleteController<P> {
 

  
}
