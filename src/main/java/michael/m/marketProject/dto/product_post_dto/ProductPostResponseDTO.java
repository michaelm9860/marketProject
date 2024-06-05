package michael.m.marketProject.dto.product_post_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductPostResponseDTO {

    private Long id;

    private String productName;

    private String description;

    private List<String> pictures;

    private int price;

    private int originalPrice;

    private String currency;

    private String category;

    private String location;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int savedCount;

    private Long groupId;
}
