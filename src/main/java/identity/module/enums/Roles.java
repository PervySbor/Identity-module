package identity.module.enums;

import identity.module.exceptions.IncorrectRolesType;

public enum Roles {
    NEW_USER,
    OUT_TRIAL_USER,
    ADMIN,
    BANNED;

    public Roles createRoles(String roleName) throws IncorrectRolesType {
        return switch(roleName){
            case "NEW_USER" -> NEW_USER;
            case "OUT_TRIAL_USER" -> OUT_TRIAL_USER;
            case "ADMIN" -> ADMIN;
            case "BANNED" -> BANNED;
            default -> throw new IncorrectRolesType("Received incorrect role name");
        };
    }
}
