package am.greenbank.entities.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verificationNumbers")
public class VerificationNumber {
    @Id
    private String id;
    @DBRef
    private User user;

    private String number;

    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;
}
