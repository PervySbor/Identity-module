package identity.module.enums;

public enum SubscriptionType {

    TRIAL(7), //no checks here
    WEEK(7),
    MONTH(30),
    YEAR(365),
    ETERNITY(Integer.MAX_VALUE);

    SubscriptionType(Integer length){
        this.length = length;
    }

    private final Integer length;

    public Integer getLength() {
        return length;
    }
}
