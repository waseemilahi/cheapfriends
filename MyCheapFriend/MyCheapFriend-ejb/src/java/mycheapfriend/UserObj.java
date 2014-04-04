/* The User Object Entity. */

package mycheapfriend;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Waseem Ilahi
 */
@Entity
public class UserObj implements Serializable {

    @OneToMany(mappedBy = "borrower", fetch=FetchType.EAGER, cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private List<Bill> debts;

    @OneToMany(mappedBy = "lender", fetch=FetchType.EAGER, cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private List<Bill> assets;

    @OneToMany(mappedBy = "parent", fetch=FetchType.EAGER, cascade={CascadeType.ALL})
    private List<Friend> friends;

    private static final long serialVersionUID = 1L;

    @Id
    private long phone;

    private String password;

    private String salt;

    private boolean active;

    private boolean unsubscribe;

    private boolean disabled;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    private String email_domain;

    public UserObj() {

        this.password = null;
        this.salt = null;
        this.active = false;
        this.unsubscribe = false;
        this.disabled = false;
        this.email_domain = null;
    }

     public UserObj(long phone) {
        this.phone = phone;
        this.password = null;
        this.salt = null;
        this.active = false;
        this.disabled = false;
        this.unsubscribe = false;
        this.email_domain = null;
    }

    public UserObj(long phone,String domain) {

        this.phone = phone;
        this.active = false;
        this.disabled = false;
        this.unsubscribe = false;
        this.email_domain = domain;
    }

    public UserObj(long phone,String domain, String password) {
        this.phone = phone;
        this.password = password;
        this.salt = "";
        this.active = true;
        this.unsubscribe = false;
        this.email_domain = domain;
    }

    /* Given a Long or a String, find the long id of the friend/user in the table.*/
    public long getFriendId(Object obj){
        
         if(obj instanceof String ){
             for(Friend f : this.friends)
                 if((f.getNickname()).equals((String)obj))
                     return f.getFriend().getPhone();
         }
         else if(obj instanceof Long){
             Long id = (Long)obj;
             return id.longValue();
         }
        return 0;         
    }

    public UserObj getFriend(String nickname){
        for(Friend f: this.friends)
            if((f.getNickname()).equals(nickname))
                return f.getFriend();

        return null;
    }
    
    public boolean hasFriend(long phone) {
        for(Friend f : this.friends)
            if(f.getFriend().phone == phone)
                return true;
        return false;
    }

    public void addFriend(UserObj newUser,String nickname){

        Friend newFriend = new Friend();
        newFriend.setFriend(newUser);
        newFriend.setParent(this);
        newFriend.setNickname(nickname);
        this.getFriends().add(newFriend);
    }

    public String getNickname(UserObj friend)
    {
        if(friends == null)
            return null;
        for(Friend f: friends)
        {
            if(f.getFriend().equals(friend))
                return f.getNickname();
        }
        return null;
    }

    /**
     * note this bill is not persisted
     *
     * @param friend
     * @param amount
     */
     public void loanTo(UserObj friend , long amount){
        Bill loan = new Bill();
        loan.setAmount(amount);
        loan.setApproved(false);
        loan.setPaid(false);


        loan.setLender(this);
        loan.setBorrower(friend);

        (this.getAssets()).add(loan);

    }

     public void borrowFrom(UserObj friend, long amount){
        friend.loanTo(this, amount);
     }

    public List<Bill> getDebts() {
        return debts;
    }

    public void setDebts(List<Bill> debts) {
        this.debts = debts;
    }

    public List<Bill> getAssets() {
        return assets;
    }

    public void setAssets(List<Bill> loans) {
        this.assets = loans;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmail() {
        if(email_domain != null)
            return phone + "@" + email_domain;
        else
            return null;
    }

    public String getEmail_domain() {
        return email_domain;
    }

    public void setEmail_domain(String email_domain) {
        this.email_domain = email_domain;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isUnsubscribe() {
        return unsubscribe;
    }

    public void setUnsubscribe(boolean unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (phone != 0 ? phone : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserObj)) {
            return false;
        }
        UserObj other = (UserObj) object;
        if(this.phone == 0 || other.phone == 0)
                return false;
        else
            return this.phone == other.phone;
    }

    @Override
    public String toString() {
        return "ejb.UserObj[phone=" + phone + "]";
    }

}
