package michael.m.marketProject.service.product_post_service;

import michael.m.marketProject.dto.product_post_dto.ProductPostCreateDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostListDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostResponseDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostUpdateDTO;
import org.springframework.security.core.Authentication;

public interface ProductPostService {
    ProductPostResponseDTO createProductPost(ProductPostCreateDTO dto, Authentication authentication);

    ProductPostListDTO getAllProductPosts(Authentication authentication, int pageNum, int pageSize, String sortDir, String... sortBy);
    ProductPostResponseDTO getProductPostById(Long id, Authentication authentication);

    ProductPostResponseDTO updateProductPostById(Long id, ProductPostUpdateDTO dto, Authentication authentication);
    ProductPostResponseDTO deleteProductPostById(Long id, Authentication authentication);

    ProductPostResponseDTO saveOrUnsaveProductPostToUserByPostId(Long id, Authentication authentication);

    ProductPostResponseDTO createProductPostInGroup(ProductPostCreateDTO dto, Long groupId, Authentication authentication);

}

