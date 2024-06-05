package transfer;

import java.io.Serializable;

public class UserDto implements Serializable {
    private final String username;
    private final String password;
    private boolean registrationRequired;

    public UserDto(String username, String password, boolean registrationRequired) {
        this.username = username;
        this.password = password;
        this.registrationRequired = registrationRequired;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRegistrationRequired() {
        return registrationRequired;
    }

    public void setRegistrationRequired(boolean registrationRequired) {
        this.registrationRequired = registrationRequired;
    }
}
