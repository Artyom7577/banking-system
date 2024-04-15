package am.greenbank.entities.loan;

import am.greenbank.entities.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "loanTypes")
public class LoanType {
    @Id
    private String id;
    private String name;
    private List<Option> options;
    private boolean available;
}

