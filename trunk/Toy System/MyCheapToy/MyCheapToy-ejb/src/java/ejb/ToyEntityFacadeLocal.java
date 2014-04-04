/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ejb;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Waseem Ilahi
 */
@Local
public interface ToyEntityFacadeLocal {

    void create(ToyEntity toyEntity);

    void edit(ToyEntity toyEntity);

    void remove(ToyEntity toyEntity);

    ToyEntity find(Object id);

    List<ToyEntity> findAll();

}
