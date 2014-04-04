/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.Vector;

/**
 *
 * @author michaelglass
 */
public class Message {
        String from, to, subject, body;

    public Message()
    {
        this.from = this.to = this.subject = this.body = "";
    }

    public String getBody() {
        return body;
    }

    public boolean validBody() {
        String status = status();
        return !status.startsWith("ERROR");
    }
    
    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public boolean validFrom() {
        return false;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public boolean validSubject() {
        return false;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public boolean validTo() {
        return false;
    }

    public void setTo(String to) {
        this.to = to;
    }

    private static String phoneNumPattern = "\\d{10}|(?:\\D?\\d{3}\\D ?(?:\\d{7}|(?:\\d{3}\\D\\d{4})))";
    private static String nicknamePattern = "[a-zA-Z]{2,2}[a-zA-Z0-9_-]{1,8}";
    private static String amountPattern = "\\$?\\d{1,4}(\\.\\d{2})?";

    public String status()
    {
        body = body.trim();
        if(body.length() == 0)
            return body;
        String[] atoms = body.split("\\s+");
        String error_str = "ERROR: ";
        boolean multiple_friends = false;
        boolean nickname = false;
        boolean error = false;


        Vector<String> identifiers = new Vector<String>();
        Vector<Integer> amounts = new Vector<Integer>();
        
        for(String atom : atoms)
        {
            atom = atom.trim();

            if(atom.matches(nicknamePattern))
                identifiers.add(atom.toLowerCase());
            else if (atom.matches(phoneNumPattern))
            {
                identifiers.add(atom.replaceAll("\\D", ""));
            }
            else if (atom.matches(amountPattern))
            {
                atom = atom.replaceAll("[^\\d.]", "");
                int ival = 0;
                if(atom.indexOf('.') >= 0)
                {
                    atom = atom.replaceAll("\\.", "");
                    ival = Integer.parseInt(atom);
                }
                else
                {
                    ival = Integer.parseInt(atom) * 100;
                }
                amounts.add(new Integer(ival));
            }
            else if(amounts.size() <= 1 && atom.equalsIgnoreCase("me"))
            {
                identifiers.add(atom);
                multiple_friends = true;
            }
            else
            {
                error = true;
                error_str += "unparsable atom: " + atom + "\n";
                break;
            }
        }

        if(identifiers.size() == 0)
        {
            error = true;
            error_str += "need at least one identifier and amount.\n";
        }
        else if(amounts.size() == identifiers.size() )
        {
            if(multiple_friends)
            {
                error = true;
                error_str += "splitting a bill amongst multiple friends can only accept a \"me\" if you split a single bill between users\n";
            }
        }
        else if (amounts.size() == 1 && identifiers.size() >= 2)
        {
            multiple_friends = true;
        }
        else
        {
            error = true;
            error_str += "syntax error parsing request\n";
        }

        if(error)
            return error_str;

        else if(multiple_friends)
        {
            String rval = "Splitting " + amounts.firstElement() + " between the following friends: "
                    + identifiers.remove(identifiers.size()-1);
            
            for(String ident : identifiers)
            {
                rval += ", " + ident;
            }
            return rval;
        }
        else
        {
            String rval = "";
            for(int i = 0; i < identifiers.size(); i++)
            {
                rval += "Billing " + identifiers.elementAt(i) + " " + amounts.elementAt(i) + "\n";
            }
            return rval;
        }
    }


}
