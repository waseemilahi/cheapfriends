/* The Bill Data Entity. */

package mycheapfriend;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author Waseem Ilahi
 */
@Entity
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Boolean paid;

    private Boolean approved;

    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date timeCreated = (Calendar.getInstance()).getTime();

    private long amount;

    @ManyToOne
    private UserObj lender;

    @ManyToOne
    private UserObj borrower;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date date) {
        this.timeCreated = date;
    }

    public UserObj getBorrower() {
        return borrower;
    }

    public void setBorrower(UserObj debtor) {
        this.borrower = debtor;
    }

    public UserObj getLender() {
        return lender;
    }

    public void setLender(UserObj lender) {
        this.lender = lender;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Bill)) {
            return false;
        }
        Bill other = (Bill) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ejb.Bill[id=" + id + "]";
    }



}
