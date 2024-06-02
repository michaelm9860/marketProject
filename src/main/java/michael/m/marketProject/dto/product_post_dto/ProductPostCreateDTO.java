package michael.m.marketProject.dto.product_post_dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductPostCreateDTO {

    @NotNull
    @Size(min = 1, max = 32)
    private String productName;

    @NotNull
    @Column(length = 512)
    private String description;

    @NotNull
    private List<String> pictures;

    @NotNull
    private int price;

    @NotNull
    @Size(min = 1, max = 32, message = "Category must be between 1 and 32 characters")
    private String category;

    @Size(min = 2, max = 32)
    private String location;

//    private Long groupId;
}
