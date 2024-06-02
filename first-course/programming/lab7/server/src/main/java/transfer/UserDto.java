package transfer;

import java.io.Serializable;

public class UserDto implements Serializable {
    private final String username;
    private final String password;

    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
