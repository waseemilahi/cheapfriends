/*
 *  A email poller using Timer service, reads emails every five seconds.
 */

package mycheapfriend;

import java.util.Collection;
import java.util.Iterator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.*;

/**
 *
 * @author David
 */
@Stateless(mappedName="ejb.PollerBean")
public class PollerBean implements PollerRemote {
        
        @Resource
        private SessionContext ctx;
        static private boolean started = false;
        static private boolean created = false;
        public void createTimer() {
            if(created)
                return;
            started = true;
            created = true;
            ctx.getTimerService().createTimer(0, 5000, null);
            System.out.println("Timers set");

        }
        public void startTimer() {
            if(!created) {
                createTimer();
                return;
            }
            if(started)
                return;
            started = true;
            System.out.println("Timers started");
        }
        public void stopTimer() {
            if(!started)
                return;
            started = false;
            System.out.println("Timers stopped");
        }
        public boolean testStarted() {
            return started;
        }
        public boolean testCreated() {
            return created;
        }
    @Timeout
    public void handleTimeout(Timer timer) {
       //call read() everytime the timer expires, process each email in the return arraylist.
        Controller c = new Controller();
        if(!started)
            return;
        try {
            List<EmailInfo> messages = EmailRead.read();
            for(EmailInfo e : messages)
                c.handle(e);
        } catch (Exception ex) {
            Logger.getLogger(PollerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
