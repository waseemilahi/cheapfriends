
package mycheapfriend;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This class implements the business logic of processing parsed text messages. Only one public function is provided in this class and it deals with all kinds of messages.
 * <p>After receiving a parsed text message, form errors will be checked at first. If there is any form error, the function will send a notice back specifying the errors. There are generally four kinds of form errors.
 * <ul>
 * <li>Invalid sender: The message is not sent from a cell phone.
 * <li>Lexical error: Some elements in the content are illegal.
 * <li>Syntax error: The combination of elements is illegal.
 * <li>Other error: illegal messages duing to undefined errors.
 * </ul>
 * <p>If there is no form error, the funcion will check whether the message sender has authorization for the functionality he wants to use. There are generally three kinds of users, including active users, inactive users, and strangers.
 * <ul>
 * <li>Active users: people who have registered by sending any text message to new_account@mycheapfriend.com. Active users have authorization to use any functionality implemented so far.
 * <li>Inactive users: people who haven't registered but exist in our database. Inactive users can use public functionalities such creating an account, unsubscribing, accepting a bill and so on.
 * <li>Strangers: people who are not in our database. Strangers can only create new accounts. But they may also become inactive users after sending any message free of form errors to mycheapfriend.com.
 * </ul>
 * <p>If there is no form error and the authorization meets requirement, the user may use the functionality. 
 * <p>Functionalities implemented include "create a new account", "reset password", "unsubscribe", "resubscribe", "add a friend", "request a bill", "accept a bill" and "report".
 * <p>When applying a functionality, semantic errors might be found. For example, the user may want to send a bill to some one named "Jack", but it turns out that the user doesn't have a friend with a nickname "Jack". In this case, a notice will be sent back to the user.
 * <p>*Any of the three kinds of users might be blocked by an administrator and blocked users can not use any functionality.
 * @author Shaoqing Niu
 */
public class Controller{

    public static final String[] POSSIBLE_DOMAINS = {"txt.att.net", "messaging.sprintpcs.com", "tmomail.net", "vtext.com"};
    //["airtelkk.com", "alertas.personal.com.ar", "bplmobile.com", "cingularme.com", "clarotorpedo.com.br", "comcel.com.co", "cwemail.com", 
	//"email.uscc.net", "emtelworld.net", "fido.ca", "ideasclaro-ca.com", "ivctext.com", "iwspcs.net", "mas.aw", "message.alltel.com", 
	//"messaging.nextel.com", "messaging.sprintpcs.com", "mmst5.tracfone.com", "mobile.celloneusa.com", "mobipcs.net", "movistar.com.co", 
	//"msg.acsalaska.com", "msg.gci.net", "msg.globalstarusa.com", "msg.iridium.com", "msg.koodomobile.com", "msg.telus.com", "myboostmobile.com", 
	//"mymetropcs.com", "nextel.net.ar", "orange.pl", "page.att.net", "pcs.rogers.com", "qwestmp.com", "rek2.com.mx", "slinteractive.com.au",
	//"sms.airtelmontana.com", "sms.co.za", "sms.ctimovil.com.ar", "sms.lmt.lv", "sms.mobitel.lk", "sms.movistar.net.ar", "sms.mymeteor.ie", 
	//"sms.sasktel.com", "sms.spicenepal.com", "sms.t-mobile.at", "sms.thumbcellular.com", "sms.tigo.com.co", "sms.vodafone.it", "sms.ycc.ru", 
	//"t-mobile.uk.net", "tachyonsms.co.uk", "text.aql.com", "text.mtsmobility.com", "text.plusgsm.pl", "tmomail.net", "tms.suncom.com", 
	//"txt.att.net", "txt.bell.ca", "utext.com", "vmobile.ca", "vmobl.com", "voda.co.za", "vtext.com"]
    InitialContext context;
    UserObjFacadeRemote userObjFacade;

    /**
     * Handles a parsed message.
     * @param tm: a parsed message
     */
    public void handle(TextMessage tm) {
        log("new message from " + tm.getFrom());
        log("message: " + ((EmailInfo)tm).getContent());
        
        try {
            context = new InitialContext();
            userObjFacade = (UserObjFacadeRemote) context.lookup("ejb.UserObjFacade");
        } catch (NamingException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            replyReport("Internal error, try back later (SORRY!!)", tm.getFrom());
            return;
        }
		
	//handle syntax errors
	if( isError(tm) == true)return;

        //initialize common vars
        UserObj user = userObjFacade.find(tm.getPhone());
        boolean newUser = user == null;
        boolean userAuthenticated = isAuthenticated(user, newUser,tm);

        log("sender phone:" + tm.getPhone());
		
	//always create a new account if the sender is not a user
        if(isNewUser(user,newUser,tm) == false)return;
        if(newUser)
            user = userObjFacade.find(tm.getPhone());

        //all people can use the any of the four functionalities
	if(isGeneralFunctionality(user,newUser,tm) == true)return;
		
        //a non-user wants to use premium functionalities
        if(newUser) {
            replyUnregisteredUser(tm.getFrom());
            return;
        }
        //a user wants to accept a bill
        if(tm.getType() == TextMessage.ACCEPT_BILL)
        {
            acceptBill(user);
            return;
        }
        //inactive user, or acitve user who sends to a wrong address when using premium functionalities
        else if(!userAuthenticated) {
            replyWrongPasswordUser(user.getPassword(), tm.getFrom());
            return;
        }

        //active user wants to use premium functions
	if(isPremiumFunctionality(user,tm) == true)return;		
    }
	
    /**
     * Checks if the user wants to use one of the premium functionalities on the system.
     * if yes then it calls the appropriate methods to handle that.
     * @param user: the user object that needs to be set.
     * @param tm: The text message that needs to be tested.
     */
    private boolean isPremiumFunctionality(UserObj user, TextMessage tm)
    {
        switch(tm.getType()){
            case TextMessage.NEW_FRIEND:
                newFriend(user, tm.getFriendPhone(), tm.getFriendNick());
                return true;
            case TextMessage.NEW_BILL:
                newBill(user, tm);
                return true;
            case TextMessage.REPORT_BILLS:
                reportBills(user);
                return true;
        }
	return false;
    }
	
    /**
     * Checks if the user wants to use one of the four basic functionalities on the system.
     * if yes then it calls the appropriate methods to handle that.
     * @param user: the user object that needs to be set.
     * @param newUser: the boolean that tells us whether the user is new of not
     * @param tm: The text message that needs to be tested.
     */
    private boolean isGeneralFunctionality(UserObj user, boolean newUser, TextMessage tm)
    {
        switch(tm.getType()){
            case TextMessage.NEW_ACCOUNT:
            case TextMessage.RESET_PASS:
                newAccountOrReset(user, newUser);
                return true;
            case TextMessage.UNSUBSCRIBE:
                unsubscribe(user);
                return true;
            case TextMessage.RESUBSCRIBE:
                resubscribe(user);
                return true;
        }
	return false;
    }
	
    /**
     * Checks if the user is new user or not.
     * @param user: the user object that needs to be set.
     * @param newUser: the boolean that tells us whether the user is new of not
     * @param tm: The text message that needs to be tested.
     */
    private boolean isNewUser(UserObj user, boolean newUser, TextMessage tm)
    {
	if(newUser) 
	{
            log("sender is a new user");
            //create inactive user
            user = new UserObj(tm.getPhone(), tm.getDomain());
            userObjFacade.create(user);
            return true;
	}
	else
	{
            //the sender is an inactive user
            if(user.getEmail_domain() == null)
            {
		user.setEmail_domain(tm.getDomain());
		userObjFacade.edit(user);
            }
            //the sender is disabled by administrator
            if(user.isDisabled())
		return false;
            //the sender is unsubscribed and wants to resubscribe now
            if(user.isUnsubscribe())
            {
		log("Sender is unsubscribed");
		if(tm.getType() == TextMessage.RESUBSCRIBE)
                    resubscribe(user);
                    return false;
            }
	}
	
	return true;
    }
	
    /**
     * Checks if the user is an authorized user or not.
     * @param user: the user object that needs to be set.
     * @param newUser: the boolean that tells us whether the user is new of not
     * @param tm: The text message that needs to be tested.
     */
    private boolean isAuthenticated(UserObj user, boolean newUser, TextMessage tm)
    {
    	if(!newUser && user.isActive())
            if(tm.getPassword() != null)
                if(user.getPassword().equalsIgnoreCase(tm.getPassword()))
                    return true;
	return false;
    }
	
    /**
     * Checks if the message has error(s) or not and if it does, returns true.
     * @param tm: The text message that needs to be tested.
     */
    private boolean isError(TextMessage tm)
    {
        if(tm.getType() == TextMessage.ERROR)
        {
            log("error: " + getErrorMessage(tm.getErrorType()));
            replyReport(getErrorMessage(tm.getErrorType()), tm.getFrom());
            return true;
        }
        else return false;
    }

    /**
     * Creates a new account or resets the password for an user.
     * @param newUser: determines what message to reply to the user
     */
    private void newAccountOrReset(UserObj user, boolean newUser)
    {
        log("new account or reset");
        user.setActive(true);
        user.setPassword(PasswordGenerator.generatePassword());
        user.setUnsubscribe(false);
        userObjFacade.edit(user);
        log("sending password to " + user.getEmail());
        if(newUser)
            this.replyNewUser(user.getPassword(), user.getEmail());
        else
            this.replyResetPassword(user.getPassword(), user.getEmail());

    }

    /**
     * Unsubscribes a user so that he/she won't receive messages from mycheapfriend.com.
     */
    private void unsubscribe(UserObj user)
    {
        log("unsubscribe");
        user.setUnsubscribe(true);
        userObjFacade.edit(user);
        this.replyUnsubscribe(user.getEmail());
    }

    /**
     * Resubscribes a user so that he/she can receive messages from mycheapfriend.com.
     */
    private void resubscribe(UserObj user)
    {
        log("resubscribe");
        user.setUnsubscribe(false);
        userObjFacade.edit(user);
        this.replyResubscribe(user.getEmail());
    }

    /**
     * Adds a friend for a user and sets the firend's nickname.
     * @param friendPhone: the phone number of the user's friend
     * @param friendNick: the nickname of the friend
     */
    private void newFriend(UserObj user, long friendPhone, String friendNick)
    {
        log("new friend");
        String email = user.getEmail();
        //only one way
        setFriendNickName(friendPhone, friendNick, user);
        this.replyAddFriend(friendPhone, friendNick, email);
    }

    /**
     * Requests a bill to the user's friends.
     * @param tm: the parsed message that contains the information of the request
     */
    private void newBill(UserObj user, TextMessage tm)
    {
        log("new bill");
        ArrayList<UserObj> toBeBilled = new ArrayList<UserObj>();

        //make sure all of the friends make sense.
        for(int i = 0; i < tm.getNumBills(); i++){
            UserObj friendUser = this.identifierToUserObj(user, tm.getBillFriend(i));
            //a nickname is used but the user doesn't have a friend with the nickname
            //so the message has a semantic error
            if(friendUser == null) {
                this.replyIdentifierWrong(tm.getBillFriend(i), user.getEmail());
                return;
            }
            else
                toBeBilled.add(friendUser);

        }
        for(int i = 0; i < tm.getNumBills(); i++) {
            UserObj friendUser = toBeBilled.get(i);
            user.loanTo(friendUser, tm.getBillMoney(i));
            userObjFacade.edit(user);

            if(friendUser.isUnsubscribe()){
                this.replyFriendUnsubscribed(readableFriend(user, friendUser), user.getEmail());
            }
            else {
                String money = readableAmount(tm.getBillMoney(i));

                this.replyBillRequest(money, readableFriend(user, friendUser), user.getEmail(), 0);

                ArrayList<String> emailsToTry = new ArrayList<String>();
                String newAddress;
                if((newAddress = friendUser.getEmail()) != null)
                    emailsToTry.add(newAddress);
                else //we don't have the receiver's domain, so we try all domains
                    for(String domain:POSSIBLE_DOMAINS)
                        emailsToTry.add(friendUser.getPhone() + "@" + domain);

                for(String address: emailsToTry)
                    this.replyBillRequest(money , readableFriend(friendUser, user) , address, 1);
            }
        }
    }

    /**
     * Reports bills to the user.
     */
    private void reportBills(UserObj user)
    {
        log("report bills");
        HashMap<UserObj, Long> nets = new HashMap<UserObj, Long>();
        HashMap<UserObj, String> nicknames = new HashMap<UserObj, String>();

        String text = "";
        String newline = "\r\n";
        List<Bill> assets = user.getAssets();
        for(Bill asset: assets)
        {
            if(asset.getApproved() && !asset.getPaid())
            {
                UserObj borrower = asset.getBorrower();
                long amount = asset.getAmount();
                Long oldAmount = nets.get(borrower);
                long oldVal = (oldAmount == null)? 0 : oldAmount.longValue();
                Long newAmount = new Long(oldVal + amount);
                nets.put(borrower, newAmount);
            }
        }

        List<Bill> debts = user.getDebts();
        for(Bill debt: debts)
        {
            if(debt.getApproved() && !debt.getPaid())
            {
                UserObj lender = debt.getLender();
                long amount = debt.getAmount();
                Long oldAmount = nets.get(lender);
                long oldVal = (oldAmount == null)? 0 : oldAmount.longValue();
                Long newAmount = new Long(oldVal - amount);
                nets.put(lender, newAmount);
            }
        }
        ArrayList<UserObj> owesUser = new ArrayList<UserObj>();
        ArrayList<UserObj> userOwes = new ArrayList<UserObj>();
        Set<UserObj> users = nets.keySet();

        List<Friend> friends = user.getFriends();


        //populate nicknames hash
        for(Friend f : friends)
        {
            UserObj friend = f.getFriend();
            if(nets.containsKey(friend))
                nicknames.put(friend, f.getNickname());
        }

        for(UserObj u : users)
        {
            if(nets.get(u).longValue() > 0)
                owesUser.add(u);
            else if (nets.get(u).longValue() < 0)
                userOwes.add(u);

        }

        if(!owesUser.isEmpty())
        {
            text += "They owe you: ";
            for(UserObj u : owesUser)
            {

                String money = readableAmount( nets.get(u).longValue() );

                String identifier = readableFriend(nicknames.get(u), u.getPhone());
                text += identifier + "-$" + money +", ";
            }
        }
        if(!userOwes.isEmpty())
        {
            if(text.length() > 0)
                text += ". "+ newline;

            text += "You owe: ";
            for(UserObj u : userOwes)
            {

                String money = readableAmount( nets.get(u).longValue() );
                String identifier = readableFriend(nicknames.get(u), u.getPhone());

                text += identifier + "-$" + money +", ";
            }
        }
        if(userOwes.isEmpty() && owesUser.isEmpty())
            text = "no current debts or loans";

        replyReport(text, user.getEmail());
    }

    /**
     * Accepts a bill.
     */
    private void acceptBill(UserObj user)
    {
        log("accept bill");
        List<Bill> debts = user.getDebts();
        Bill most_recent_bill = null;
        Date most_recent = null;
        log("traversing " + debts.size() + " debts.");
        //get the most recent debt
        for(Bill b : debts)
        {
            Date next_date = b.getTimeCreated();
            if( most_recent == null || next_date.after(most_recent))
            {
                most_recent = next_date;
                most_recent_bill = b;
            }
        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);

        if(most_recent == null)
        {
            log("no recent date");
        }
        else
          log("bbbill date:" + most_recent + ", calendar date" + c.getTime());

        //the most recent bill is not approved yet and it is not out of date
        if(most_recent != null && !most_recent_bill.getApproved() && most_recent.after(c.getTime()))
        {
            log("1");
            most_recent_bill.setApproved(true);
            UserObj lender = most_recent_bill.getLender();
            //the lender is unsubscribed
            if(lender.isUnsubscribe())
                this.replyFriendUnsubscribed(readableFriend(user, lender), user.getEmail());
            //else calculate all bills not setteled
            log("2");

            long originalBalance = most_recent_bill.getAmount();
            long recentBillBalance = originalBalance;
            long newBalance = 0;

            List<Bill> assets = user.getAssets();
            Bill theirLastBill = null;
            for(Bill b : assets)
                if(b.getApproved() && !b.getPaid() && b.getBorrower().equals(lender))
                {
                    long newAmount = b.getAmount();
                    if(recentBillBalance > 0)
                    {
                        theirLastBill = b;
						//if he loaned me $x, any previous bills up to $x are settled
                        if(recentBillBalance >= newAmount) 
                            b.setPaid(true);
                        
                        recentBillBalance -= newAmount;
                    }
                    newBalance += newAmount;
                }

            log("3");

            if(recentBillBalance <= 0)
                most_recent_bill.setPaid(true);
            if(recentBillBalance < 0)
                theirLastBill.setAmount(Math.abs(recentBillBalance));
            if(recentBillBalance > 0)
                most_recent_bill.setAmount(recentBillBalance);
            
            userObjFacade.edit(user);
            log(newBalance + "");

            for(Bill b : debts)
                if(b.getApproved() && !b.getPaid() && b.getLender().equals(lender) && b.getId() != most_recent_bill.getId())
                    newBalance -= b.getAmount();
            log(newBalance + "");

            newBalance -= originalBalance;

            log(newBalance + "");
            replyAcceptBill(most_recent_bill, newBalance, originalBalance);
        }
        else
        {
            log("7");
			//perhaps also allow debtor to refersh bill...
            replyBillTooOld(user);
        }
        log("8");
    }

    /**
     * Checks whether the amount of money is readable.
     * @param val
     * @return
     */
    private String readableAmount(long val)
    {
        String money;
        if(val % 100 == 0)
            money = ""+ (val / 100);
        else
            money = (new BigDecimal(BigInteger.valueOf(val), 2)).toPlainString();
        return money;
    }

    /**
     * Gets the identifier of a user, probably a friend of a user.
     * @param user
     * @param friend
     * @return
     */
    private String readableFriend(UserObj user, UserObj friend)
    {
        return readableFriend(user.getNickname(friend), friend.getPhone());
    }

    /**
     * Gets the identifier of a user, probably a friend of a user.
     * @param user
     * @param friend
     * @return
     */
    private String readableFriend(String nickname, long phone)
    {
        if(nickname == null)
            return "@ "+phone;
        else
            return nickname;
    }

    /**
     * Set nickname for a friend of a user.
     * @param phone: phone number of the friend
     * @param nickname: nickname for friend
     * @param user: the user who wants to add a friend
     */
    private void setFriendNickName(long phone, String nickname, UserObj user){

            UserObj friendUser = userObjFacade.find(phone);
            Boolean found = false;

            //no such user, then create one
            if(friendUser == null){

                friendUser = new UserObj(phone);
                user.addFriend(friendUser, nickname);

                userObjFacade.create(friendUser);
                userObjFacade.edit(user);
            }
            else{

                Friend newFriend = new Friend();
                newFriend.setParent(user);
                newFriend.setFriend(friendUser);
                //check the friend list and update the nickname if exists
                for(Friend f : user.getFriends()){
                    if(f.getFriend().getPhone() == phone){
                        f.setNickname(nickname);
                        found = true;
                        break;
                    }
                }
                //not in the friend list, so add to the list
                if(found == false){
                    newFriend.setNickname(nickname);
                    user.getFriends().add(newFriend);
                }

                userObjFacade.edit(user);

            }
    }
    /**
     * Replys a message if an unregistered user wants to use premium function.
     * @param address
     */
    private void replyUnregisteredUser(String address){
        String text = "You are not registered yet. ";
        text += "Please text "+EmailInfo.NEW_ACCOUNT_ADDR+"@mycheapfriend.com to register.";
        replyReport(text, address);
    }
    /**
     * Replys a message if a user sends to a wrong address.
     * @param password
     * @param address
     */
    private void replyWrongPasswordUser(String password, String address){
        String text = "Please text your own address. ";
        text += "Your unique address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        replyReport(text, address);
    }
    /**
     * Replys a message if people wants to create an account.
     * @param password
     * @param address
     */
    private void replyNewUser(String password, String address){
        String text = "Welcome to MyCheapFriend! ";
        text += "Your unique address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        replyReport(text, address);
    }

    private void replyResetPassword(String password, String address){
        String text = "Your new address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        replyReport(text, address);
    }

    private void replyUnsubscribe(String address) {

        String text = "You have unsubscribed from mycheapfriend now. ";
        replyReport( text, address);
    }

    private void replyResubscribe(String address) {

        String text = "You have subscribed from mycheapfriend now. ";
        replyReport( text, address);
    }

    private void replyAddFriend(long phone, String nick, String address){
        String text = "Your have set your friend "+phone+"'s nickname to "+nick;
        replyReport(text, address);
    }

    private void replyIdentifierWrong(Object id, String address){

        String text = "Your don't have a friend with identifier"+id;
        replyReport(text, address);
    }

    private void replyFriendUnsubscribed(String id, String address)
    {
        replyReport("Your friend " + id + " has unsubscribed from mycheapfriend", address);
    }

    private void replyBillRequest(String money, String id, String address, int type){
        String text = "";
        if(type == 0){
            text = "You have sent a bill of " + money + " to your friend " + id + ".";
        }
        else if(type == 1){
            text = "Your friend " + id + " says you owe " + money + " to them.  Send a 'y' to robot@mycheapfriend.com to confirm.";
        }
        else ;
        replyReport( text, address);
    }

    private void replyReport(String message, String address){
        log("sending: " + message);
        log("to: "+ address);
        EmailSend emailSend = new EmailSend();

        emailSend.setAll("", message, address);
        emailSend.send();
    }

    private void replyAcceptBill(Bill b, long newBalance, long originalBalance)
    {
        log("replying after accepting bill");
        UserObj borrower = b.getBorrower();
        UserObj lender = b.getLender();

        String readableAmount = readableAmount(originalBalance);
        String readableNewBalance = readableAmount(Math.abs(newBalance));
        String borrowerText = "You have confirmed that your friend " + readableFriend(borrower, lender) + " loaned you $" + readableAmount + ".";
        String lenderText = "Your friend " + readableFriend(lender, borrower) + " has confirmed that you paid them " + readableAmount +".";

		//based on the value of the variable "newBalance" the two text messages
		//are created to be sent to both parties.
        if(newBalance > 0)
        {
            borrowerText += "  They now owe you $" + readableNewBalance +".";
            lenderText += "  You now owe them $" + readableNewBalance +".";
        }
        else if(newBalance < 0)
        {
            lenderText += "  They now owe you $" + readableNewBalance+".";
            borrowerText += "  You now owe them $" + readableNewBalance+".";
        }
        else //new_balance == 0
        {
            String suffix = " You guys have now settled all of your debts.";
            lenderText +=  suffix;
            borrowerText += suffix;
        }

        this.replyReport(lenderText, lender.getEmail());
        this.replyReport(borrowerText, borrower.getEmail());
    }


    private void replyBillTooOld(UserObj u)
    {
        replyReport("You tried to accept a bill, but you don't have any pending bills from the last 24 hours to accept", u.getEmail());
    }

    /**
     * getErrorMessage(int) is used to handle syntax error in a message.
     * @param errorType
     * @return
     */
    private String getErrorMessage(int errorType){

        switch(errorType) {
            case TextMessage.INVALID_SENDER:
                return "Please use a cell phone to send the message.";
            case TextMessage.LEXICAL_ERROR:
            case TextMessage.SYNTAX_ERROR:
                return "Please check the format of your message.";
            default: //undefined error
                return "Please check your message.";
        }
    }

    private UserObj identifierToUserObj(UserObj user, Object id)
    {
        if(id instanceof String)
            return user.getFriend((String)id);
        else
        {
            long phone = ((Long)id).longValue();
            UserObj friendUser = userObjFacade.find(phone);
            if(friendUser == null)
            {
                friendUser = new UserObj(phone);
                userObjFacade.create(friendUser);
            }
            return friendUser;
        }
    }

    private void log(String message)
    {
        System.out.println(message);
    }

}