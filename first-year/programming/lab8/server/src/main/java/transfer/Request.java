package transfer;

import dto.CommandDto;
import dto.UserDto;

import java.io.Serializable;

public record Request(CommandDto commandDto, UserDto userDto) implements Serializable {
}
