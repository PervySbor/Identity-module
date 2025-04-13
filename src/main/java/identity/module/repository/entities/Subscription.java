package identity.module.repository.entities;

import identity.module.enums.SubscriptionType;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Objects;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name="subscriptions")
public class Subscription {

    @Id
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="expire_at")
    private Timestamp expireAt;

    @Column(name="created_at")
    private Timestamp createdAt;

    @Column(name="subscription_type")
    @Enumerated(STRING)
    private SubscriptionType subscriptionType;

    public Subscription() {}

    public Subscription(User user, SubscriptionType subscriptionType){
        this.user = user;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.createdAt);
        cal.add(Calendar.DAY_OF_MONTH, subscriptionType.getLength());
        this.expireAt = new Timestamp(cal.getTimeInMillis());
        this.subscriptionType = subscriptionType;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "user=" + user +
                ", expireAt=" + expireAt +
                ", createdAt=" + createdAt +
                ", subscriptionType=" + subscriptionType +
                '}';
    }

    //WARNING!!! For test assertions only
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(user, that.user) && subscriptionType == that.subscriptionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, expireAt, createdAt, subscriptionType);
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getExpireAt() {
        return expireAt;
    }

    public User getUser() {
        return user;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpireAt(Timestamp expireAt) {
        this.expireAt = expireAt;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
