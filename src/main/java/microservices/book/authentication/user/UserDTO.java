package microservices.book.authentication.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

    private String alias;
    private String password;

    public String getPassword() {
        return password;
    }

    public String getAlias() {
        return alias;
    }
}
