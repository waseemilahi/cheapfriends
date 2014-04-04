package mycheapfriend;

import java.util.ArrayList;


/**
 * Emailinfo class holding all the information parsed from the emails.
 * Include functions for parsing the email and categorizing emails.
 * @author David
 */

public class EmailInfo implements TextMessage{

    public final static String NEW_ACCOUNT_ADDR = "new_account";
    private final static String RERSET_PASS_ADDR = "reset_pass";
    private final static String UNSUBSCRIBE_ADDR = "unsubscribe";
    private final static String RESUBSCRIBE_ADDR = "resubscribe";
    private final static String VALID_PASS_REGEX = "[0-9a-z]{6,6}";
    private final static String REPORT_MESSAGE = "report";
    private final static String ACCEPT_BILL_ADDR = "robot";

    private final static String ACCEPT_BILL_PATTERN = "^(y[a-z]*)?$";
    private final static String PHONE_PATTERN = "\\d{10}|(?:\\D?\\d{3}\\D ?(?:\\d{7}|(?:\\d{3}\\D\\d{4})))";
    private final static String NICKNAME_PATTERN = "[a-zA-Z]{2,2}[a-zA-Z0-9_-]{1,8}";
    private final static String AMOUNT_PATTERN = "\\$?\\d{1,4}(\\.\\d{2})?";
    private final static String FROM_PHONE_PATTERN = "\\d{10,10}";


    private String from,to,subject,date,content;

    private int type = TextMessage.ERROR;
    private int errorType = TextMessage.UNPARSED;

    private long phone, friendPhone;

    private String domain, password, friendNick;

    private ArrayList<Object> billFriends;

   private ArrayList<Integer> billAmounts;

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }

    //need to set To first...
    public void setContent(String content) {
        int signatureLocation = content.indexOf("--");
        if(signatureLocation >= 0)
            content = content.substring(0, signatureLocation);

        this.content = content.toLowerCase().trim();
        this.parseEmail();
        
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFrom(String from) {
        this.from = from.toLowerCase().trim();
        this.parseEmail();
    }

    public void setSubject(String subject) {
        this.subject = subject.toLowerCase().trim();
    }

    public void setTo(String to) {
        this.to = to.toLowerCase().trim();
        this.parseEmail();
    }
    
    public int getType() {
        return this.type;
    }

    public int getErrorType() {
        return this.errorType;
    }

    public long getPhone() {
        return this.phone;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getPassword() {
        return this.password;
    }

    public long getFriendPhone() {
        return this.friendPhone;
    }

    public String getFriendNick() {
        return this.friendNick;
    }

    public int getNumBills() {
        if(billAmounts != null)
            return this.billAmounts.size();
        else
            return 0;
    }

    public Object getBillFriend(int index) {
        if(billFriends != null && index < getNumBills())
            return billFriends.get(index);
        else
            return null;
    }

    public long getBillMoney(int index) {
        if(billAmounts != null && index < getNumBills())
            return billAmounts.get(index).longValue();
        else
            return 0;
    }

    private void parseEmail()
    {
        log("parsing...");
        if(this.to != null && this.from != null && this.content != null)
        {
            //parse from first.  reject anything not from a cellphone.
            int atLocation = from.indexOf('@');
            String parsed_phone = from.substring(0, atLocation).replace("\\D", "");
            boolean fail = true;

            if(parsed_phone.matches(FROM_PHONE_PATTERN))
            {
                try{
                    this.phone = Long.parseLong(parsed_phone);
                    this.domain = from.substring(atLocation+1).toLowerCase();
                    fail = false;
                } catch( Exception e)
                {}
            }
            else
            {
                log("from didn't match regex.");
            }
            if(fail)
            {
                this.type = TextMessage.ERROR;
                this.errorType = TextMessage.INVALID_SENDER;
                return;
            }

            log("from: " + this.phone);


            //parse to next.  for a lot of addresses we don't care about body contents...
            String prefix = this.to.substring(0, this.to.indexOf('@'));
            log("prefix: " + prefix);
            if(prefix.equals(EmailInfo.NEW_ACCOUNT_ADDR))
                this.type = TextMessage.NEW_ACCOUNT;
            else if(prefix.equals(EmailInfo.RERSET_PASS_ADDR))
                this.type = TextMessage.RESET_PASS;
            else if(prefix.equals(EmailInfo.UNSUBSCRIBE_ADDR))
                this.type = TextMessage.UNSUBSCRIBE;
            else if(prefix.equals(EmailInfo.RESUBSCRIBE_ADDR))
                this.type = TextMessage.RESUBSCRIBE;
            else if(prefix.equals(EmailInfo.ACCEPT_BILL_ADDR))
                this.type = TextMessage.ACCEPT_BILL;

            else if(prefix.matches(EmailInfo.VALID_PASS_REGEX))
                this.password = prefix;
            else
            {
                this.type = TextMessage.ERROR;
                this.errorType = TextMessage.UNKNOWN_TYPE;
                return;
            }

            log("set type, didn't fail with TO, parsing contents now");

            //parse content
            switch(this.type) {
                case TextMessage.NEW_ACCOUNT:
                case TextMessage.RESET_PASS:
                case TextMessage.UNSUBSCRIBE:
                case TextMessage.RESUBSCRIBE:
                    return;
                case TextMessage.ACCEPT_BILL:
                    if(content.matches(ACCEPT_BILL_PATTERN))
                    return; //let it set this up in the to:
            }
            if(content.equalsIgnoreCase(REPORT_MESSAGE))
            {
                this.type = TextMessage.REPORT_BILLS;
                this.errorType = TextMessage.NO_ERROR;
                return;
            }



            String[] atoms = content.split("\\s+");

            boolean me_included = false;
            boolean nickname = false;

            this.billFriends = new ArrayList<Object>();
            this.billAmounts = new ArrayList<Integer>();

            for(String atom : atoms)
            {
                atom = atom.trim();
                log("parsing atom \"" + atom + "\"");

                if(atom.matches(NICKNAME_PATTERN))
                {
                    billFriends.add(atom.toLowerCase());
                    log("nickname");
                }
                else if (atom.matches(PHONE_PATTERN))
                {
                    billFriends.add(new Long(atom.replaceAll("\\D", "")));
                    log("phone");
                }
                else if (atom.matches(AMOUNT_PATTERN))
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
                    billAmounts.add(new Integer(ival));
                    log("amount");

                }
                else if(billAmounts.size() <= 1 && atom.equalsIgnoreCase("me"))
                {
                    me_included = true;
                    log("me");

                }
                else
                {
                    this.type = TextMessage.ERROR;
                    this.errorType = TextMessage.LEXICAL_ERROR;
                    log("atom error");
                    return;
                }
            }

            if(billFriends.size() == 0)
            {
                    log("no friends, error");
                    this.type = TextMessage.ERROR;
                    this.errorType = TextMessage.SYNTAX_ERROR;
            }
            else if(billAmounts.size() == 0)
            {
                fail = true;
                if(billFriends.size() == 2)
                {
                    Long num  = null;
                    String nick = null;
                    for(Object o : billFriends)
                        if(o instanceof String)
                            nick = (String) o;
                        else if(o instanceof Long)
                            num = (Long) o;
                    if(num != null && nick != null)
                    {
                        this.friendNick = nick;
                        this.friendPhone = num.longValue();
                        this.type = TextMessage.NEW_FRIEND;
                        this.errorType = TextMessage.NO_ERROR;
                        fail = false;
                    }
                }
                if(fail)
                {
                    this.type = TextMessage.ERROR;
                    this.errorType = TextMessage.SYNTAX_ERROR;
                }
            }
            else if(billAmounts.size() == billFriends.size() )
            {
                if(me_included)
                {
                    //"me" not allowed in billFriendssize
                    this.type = TextMessage.ERROR;
                    this.errorType = TextMessage.SYNTAX_ERROR;
                }
            }
            else if (billAmounts.size() == 1 && billFriends.size() >= 2)
            {
                Integer bill = billAmounts.remove(0);
                int billSize = billFriends.size() + (me_included ? 1 : 0);
                int billValue = bill.intValue();
                int billAmount = billValue / billSize;
                int extra = billValue - (billAmount * billSize);

                for(int i = 0; i < billFriends.size(); i++)
                {
                    int this_bill_amount = billAmount;
                    if(extra > 0)
                    {
                        this_bill_amount++;
                        extra--;
                    }
                    billAmounts.add(new Integer(this_bill_amount));
                }
            }
            else
            {
                this.type = TextMessage.ERROR;
                this.errorType = TextMessage.SYNTAX_ERROR;
            }
            //new bill was successful
            if(this.errorType == TextMessage.UNPARSED)
                this.type = TextMessage.NEW_BILL;

            if(this.type != TextMessage.ERROR)
                this.errorType = TextMessage.NO_ERROR;
        }
        else
            log("from and content and to aren't set");
    }

    public static void log(String log_message)
    {
        System.out.println(log_message);
    }
}

