package michael.m.marketProject.dto.product_post_dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductPostUpdateDTO {

    @Size(max = 512)
    private String description;

    private int price;

    @Size(min = 2, max = 32)
    private String location;

    //TODO: Update to allow profile pic update
}
