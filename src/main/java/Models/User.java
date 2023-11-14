package Models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class User {

    private long id;
    private UUID userUuid;
    private String userName;
    private String userEmail;
    private String userPassword;
}
