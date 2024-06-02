package transfer;

import java.io.Serializable;

public record Request(CommandDto commandDto, UserDto userDto) implements Serializable {
}
