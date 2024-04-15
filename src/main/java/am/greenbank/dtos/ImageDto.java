package am.greenbank.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    @Id
    private String id;

    private String name;

    private String type;

    private byte[] imageData;
}
