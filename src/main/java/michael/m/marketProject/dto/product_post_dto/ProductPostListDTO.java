package michael.m.marketProject.dto.product_post_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPostListDTO {
    private long totalPosts;
    private int pageNum;
    private int pageSize;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private Collection<ProductPostResponseDTO> posts;
}