/* reading email from our robot account, username and password are hardcoded now.*/

package mycheapfriend;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *referece: http://forums.sun.com/thread.jspa?threadID=5267916
 * modified for our purpose
 * @author David
 */


public class EmailRead {
    /** Creates a new instance of Main */
    public static ArrayList<EmailInfo> read() throws Exception {
            GmailUtilities gmail= new GmailUtilities();
            gmail.setUserPass("robot@mycheapfriend.com", "K86924");
            gmail.connect();
            gmail.openFolder("INBOX");
            //set all information...
            gmail.setEmailInfo();
            System.out.println("# of new emails is " + gmail.info.size());
            //System.out.println(gmail.info.get(0).getTo());
            /* for(emailinfo a: gmail.getInfo())
                System.out.println(a.getContent()); */
            gmail.closeFolder();
            gmail.disconnect();
            return gmail.info;
    }

}
