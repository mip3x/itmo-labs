package dto;

import collection.data.User;

import java.io.Serializable;

public class UserDto implements Serializable {
    private final User user;
    private boolean registrationRequired;

    public UserDto(User user, boolean registrationRequired) {
        this.user = user;
        this.registrationRequired = registrationRequired;
    }

    public User getUser() {
        return user;
    }

    public boolean isRegistrationRequired() {
        return registrationRequired;
    }

    public void setRegistrationRequired(boolean registrationRequired) {
        this.registrationRequired = registrationRequired;
    }
}
