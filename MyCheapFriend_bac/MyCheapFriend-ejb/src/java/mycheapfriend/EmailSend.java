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
/* Test main program for email sending */
    public static void main(String[] args)
    {
        EmailSend test = new EmailSend();
        test.setM_subject("testing again");
        test.setM_to("robot@mycheapfriend.com");
        test.setM_text("Done testing");
        test.send();

    }

    private class SMTPAuthenticator extends javax.mail.Authenticator
    {
        @Override
        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(d_email, d_password);
        }
    }
}


