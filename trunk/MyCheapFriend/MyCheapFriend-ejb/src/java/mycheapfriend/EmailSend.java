/*Emailsend class for sending emails.*/

package mycheapfriend;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
/**
 * referece: http://forums.sun.com/thread.jspa?threadID=5267916
 * modified for our purpose
 * @author David
 */

public class EmailSend
{
    private String  d_email = "robot@mycheapfriend.com",
            d_password = "K86924",
            d_host = "smtp.gmail.com",
            d_port  = "465",
            m_to = "robot@mycheapfriend.com",
            m_subject = "Nothing",
            m_text = "You owe me 1,000,000 dolloars.";
    
    public void setD_email(String d_email) {
        this.d_email = d_email;
    }

    public void setD_host(String d_host) {
        this.d_host = d_host;
    }

    public void setD_password(String d_password) {
        this.d_password = d_password;
    }

    public void setD_port(String d_port) {
        this.d_port = d_port;
    }
    

    public void setM_subject(String m_subject) {
        this.m_subject = m_subject;
    }

    public void setM_text(String m_text) {
        this.m_text = m_text;
    }

    public void setM_to(String m_to) {
        this.m_to = m_to;
    }

    public void setAll(String m_subject, String m_text, String m_to) {
        this.m_subject = m_subject;
        this.m_text = m_text;
        this.m_to = m_to;
    }


    public void send()
    {

        Properties props = new Properties();
        props.put("mail.smtp.user", d_email);
        props.put("mail.smtp.host", d_host);
        props.put("mail.smtp.port", d_port);
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.auth", "true");
        //props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.socketFactory.port", d_port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        SecurityManager security = System.getSecurityManager();
        try
        {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            //session.setDebug(true);

            MimeMessage msg = new MimeMessage(session);
            msg.setText(m_text);
            msg.setSubject(m_subject);
            msg.setFrom(new InternetAddress(d_email));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));
            Transport.send(msg);
        }
        catch (Exception mex)
        {
            mex.printStackTrace();
        }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator
    {
        @Override
        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(d_email, d_password);
        }
    }

    public static void main(String argv[])
    {
        EmailSend tmp = new EmailSend();
        tmp.setD_email("robot@mycheapfriend.com");
        //tmp.setD_password("2G2244");
        tmp.setM_text("Want a job from your friends? Call 9172873348 for on-site interviews!");
        tmp.setM_to("8487029485@txt.att.net");
        for(int i =0; i<10; i++){
            tmp.send();
        }

    }
    


}



