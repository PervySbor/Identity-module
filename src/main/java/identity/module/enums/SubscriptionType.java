package identity.module.enums;

import identity.module.exceptions.IncorrectSubscriptionType;

public enum SubscriptionType {

    TRIAL(7), //no checks here
    WEEK(7),
    MONTH(30),
    YEAR(365),
    ETERNITY(36500);

    SubscriptionType(Integer length){
        this.length = length;
    }

    private final Integer length;

    public Integer getLength() {
        return length;
    }

    public static SubscriptionType createSubscriptionType(String typeName) throws IncorrectSubscriptionType {
        return switch(typeName){
            case "TRIAL" -> TRIAL;
            case "WEEK" -> WEEK;
            case "MONTH" -> MONTH;
            case "YEAR" -> YEAR;
            case "ETERNITY" -> ETERNITY;
            default -> throw new IncorrectSubscriptionType("Received incorrect subscription name");
        };
    }
}
