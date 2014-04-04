/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.List;
import javax.annotation.Resource;
import javax.jms.*;
/**
 *
 * @author Shaoqing Niu
 */
public class TextMessage {

    private int type; //defined in Global
    private int errorType; //syntax error type
    private long phone;    //347-244-6971
    private String fromAddress; //address = 347-244-6971@att.com or other address
                            //in case that people don't use cellphone to send msg
                            //if not start with 9 digits, it's a syntax error
    private String toAddress; //pass@cheapfriend.com will check whether password is for the phone

    private long friendPhone; //to set nickname for a friend
    private String friendNick;

    private List<String> billFriend; //to request/settle a bill
    private List<Float> billMoney;

    @Resource(mappedName="jms/FirstMessageFactory")
    private  ConnectionFactory connectionFactory;

    @Resource(mappedName="jms/FirstMessage")
    private  Queue queue;

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }

    public void setErrorType(int errorType){
        this.errorType = errorType;
    }

    public int getErrorType(){
        return errorType;
    }

    public void setPhone(int phone){
        this.phone = phone;
    }

    public long getPhone(){
        return phone;
    }

    public void setFromAddress(String fromAddress){
        this.fromAddress = fromAddress;
    }

    public String getFromAddress(){
        return fromAddress;
    }

    public void setToAddress(String toAddress){
        this.toAddress = toAddress;
    }

    public String getToAddress(){
        return toAddress;
    }

    public void setFriendPhone(long friendPhone){
        this.friendPhone = friendPhone;
    }

    public long getFriendPhone(){
        return friendPhone;
    }

    public void setFriendNick(String friendNick){
        this.friendNick = friendNick;
    }

    public String getFriendNick(){
        return friendNick;
    }

    public void addBillFriend(String billFriend){
        this.billFriend.add(billFriend);
    }

    public String getBillFriend(int index){
        return billFriend.get(index);
    }

    public void addBillMoney(float billMoney){
        this.billMoney.add(billMoney);
    }

    public float getBillMoney(int index){
        return billMoney.get(index);
    }

}
