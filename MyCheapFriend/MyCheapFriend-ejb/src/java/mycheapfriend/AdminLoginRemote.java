

package mycheapfriend;

import javax.ejb.Remote;

/**
 * Interface for LoginBean.
 * @author Waseem Ilahi
 */
@Remote
public interface AdminLoginRemote {
    public Boolean check_password(long phone, String password);
}
