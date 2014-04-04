/* The Session Bean For UserObj Entity. */

package mycheapfriend;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Waseem Ilahi
 */
@Stateless(mappedName="ejb.UserObjFacade")
public class UserObjFacade implements UserObjFacadeRemote {
    @PersistenceContext
    private EntityManager em;

    public void create(UserObj userObj) {
        em.persist(userObj);
        em.flush();
    }

    public void edit(UserObj userObj) {
        em.merge(userObj);
        em.flush();
    }

   public Boolean contains(UserObj userObj){
        return em.contains(userObj);
    }

    public void remove(UserObj userObj) {
        userObj = em.merge(userObj);

        List<Friend> ImTheseFriends = em.createQuery("select object(f) from Friend f where f.friend.phone = "+userObj.getPhone() +"").getResultList();

        for(Friend f: ImTheseFriends)
            em.remove(f);

        List<Friend> fs= userObj.getFriends();
        for(Friend f : fs)
            em.remove(f);

        fs.clear();


        em.remove(em.merge(userObj));
    }
/*
    public UserObj find(UserObj userObj) {
        return em.find(UserObj.class, userObj.getPhone());
    }
*/
    public UserObj find(long phone) {
        UserObj u = em.find(UserObj.class, phone);
        if(u != null)
        try {
            em.refresh(u);
        } catch(EntityNotFoundException ex){
            u = null;
        }

        return u;
    }

    public List<UserObj> findAll() {
        return em.createQuery("select object(o) from UserObj as o").getResultList();
    }

}
