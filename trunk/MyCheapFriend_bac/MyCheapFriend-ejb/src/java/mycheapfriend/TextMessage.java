/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.List;
/**
 *
 * @author Shaoqing Niu
 */
public interface TextMessage {

    /**
     * Message types
     */

    /** type set if error is true */
    public final static int ERROR = 0;
    /** new bill type */
    public final static int NEW_BILL = 1;
    /** new friend type */
    public final static int NEW_FRIEND = 2;

    /** bill report type */
    public final static int REPORT_BILLS = 3;

    /** new account url called */
    public final static int NEW_ACCOUNT = 4;
    /** reset pass url called */
    public final static int RESET_PASS = 5;
    /** unsubscribed url called */
    public final static int UNSUBSCRIBE = 6;
    /** resubscribe url called */
    public final static int RESUBSCRIBE = 7;
    /** accept bill*/
    public final static int ACCEPT_BILL = 8;



    /** no error code, (no error!) */
    public final static int NO_ERROR = 0;
    /** lexical error found */
    public final static int LEXICAL_ERROR = 1;
    /** syntax error */
    public final static int SYNTAX_ERROR = 2;
    /** other unknown error */
    public final static int UNKNOWN_TYPE = 3;
    /** email sent not phone number */
    public final static int INVALID_SENDER = 4;
    /** email not parsed errror    */
    public final static int UNPARSED = 5;
    /**
     * 
     * @return type of message (0 for error)
     */
    public int getType();

    /**
     *
     * @return type of error message (0 for no error)
     */
    public int getErrorType();

    /**
     * @return the phone number from the caller.  check for error before calling this
     */
    public long getPhone();

    /**
     * 
     * @return gets the from address of the email.
     */
    public String getFrom();

    /**
     *
     * @return gets the domain of the phone number.  check for error before calling this
     */
    public String getDomain();


    /**
     *
     * @return returns password parsed from to address.  only set for NEW_BILL and NEW_FRIEND
     */
    public String getPassword();

    /**
     * if this is NEW_FRIEND returns friend's phone
     * @return friend's new phone#
     */
    public long getFriendPhone();
    /**
     * if this is NEW_FRIEND returns friend's nickname
     * @return friend's new nickname
     */
    public String getFriendNick();

    /**
     * returns the number of bills in this message
     * @return number of bills
     */
    public int getNumBills();
    /**
     * returns the index-th friend
     * @param index
     * @return the friend.  Can be of type String or Long
     */
    public Object getBillFriend(int index);

    /**
     * returns what the index-th friend owes
     * @param index
     * @return what the index-th friend owes
     */
    public long getBillMoney(int index);

}
