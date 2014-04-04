/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import javax.ejb.Remote;

/**
 *
 * @author Waseem Ilahi
 */
@Remote
public interface AdminLoginRemote {
    public Boolean check_password(long phone, String password);
}
