package am.greenbank.responses;

import am.greenbank.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse implements Value {
    private String refreshToken;
    private String accessToken;
    private UserDto userDto;
}
