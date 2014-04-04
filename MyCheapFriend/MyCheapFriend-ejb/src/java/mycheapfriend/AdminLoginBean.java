/*
 * The checking valid login session bean.
 */

package mycheapfriend;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Waseem Ilahi
 */
@Stateful(mappedName="ejb.AdminLoginBean")
public class AdminLoginBean implements AdminLoginRemote {
    
    @EJB (mappedName="ejb.UserObjFacade")
    UserObjFacadeRemote userObjFacade;


    public Boolean check_password(long phone, String password){
        
        UserObj user = userObjFacade.find(phone);
       
        if((user.getPassword()).equals(password)){
                return Boolean.TRUE;
        }
        else{
             return Boolean.FALSE;
        }
    }
      
}
