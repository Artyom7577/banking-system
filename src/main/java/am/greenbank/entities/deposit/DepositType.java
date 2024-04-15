package am.greenbank.entities.deposit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

import am.greenbank.entities.Option;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "depositTypes")
public class DepositType {
    @Id
    private String id;
    private String name;
    private List<Option> options;
    private boolean available;
}
