/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Waseem Ilahi
 */
@Stateless
public class ToyEntityFacade implements ToyEntityFacadeLocal {
    @PersistenceContext
    private EntityManager em;

    public void create(ToyEntity toyEntity) {
        em.persist(toyEntity);
    }

    public void edit(ToyEntity toyEntity) {
        em.merge(toyEntity);
    }

    public void remove(ToyEntity toyEntity) {
       em.remove(em.merge(toyEntity));
    }

    public ToyEntity find(Object id) {
        return em.find(ToyEntity.class, id);
    }

    public List<ToyEntity> findAll() {
        return em.createQuery("select object(o) from ToyEntity as o").getResultList();
    }

}
