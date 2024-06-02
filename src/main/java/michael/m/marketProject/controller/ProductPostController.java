package michael.m.marketProject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import michael.m.marketProject.dto.product_post_dto.ProductPostCreateDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostListDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostResponseDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostUpdateDTO;
import michael.m.marketProject.service.product_post_service.ProductPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductPostController {

    private final ProductPostService productPostService;

    @GetMapping
    public ResponseEntity<ProductPostListDTO> getAllProductPosts(
            Authentication authentication,
            @RequestParam(value = "pageNum", required = false, defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String... sortBy
    ){
        return ResponseEntity.ok(productPostService.getAllProductPosts(authentication, pageNum, pageSize, sortDir, sortBy));
    }

    @PostMapping
    public ResponseEntity<ProductPostResponseDTO> createProductPost(
            Authentication authentication,
            @RequestPart("productPost") @Valid ProductPostCreateDTO dto,
            @RequestPart("picturesFiles") List<MultipartFile> picturesFiles,
            UriComponentsBuilder uriBuilder){

        var res = productPostService.createProductPost(dto, authentication, picturesFiles);

        var uri = uriBuilder.path("/api/v1/products/{id}").buildAndExpand(res.getId()).toUri();
        return ResponseEntity.created(uri).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductPostResponseDTO> getProductPostById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(productPostService.getProductPostById(id, authentication));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductPostResponseDTO> updateProductPostById(
            Authentication authentication,
            @PathVariable Long id,
            @RequestPart("updateDetails") @Valid ProductPostUpdateDTO dto,
            @RequestPart("picturesFiles") List<MultipartFile> picturesFiles){
        return ResponseEntity.ok(productPostService.updateProductPostById(id, dto ,authentication, picturesFiles));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<ProductPostResponseDTO> saveOrUnsaveProductPostToUserByPostId(@PathVariable Long id, Authentication authentication) {
        ProductPostResponseDTO responseDto = productPostService.saveOrUnsaveProductPostToUserByPostId(id, authentication);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductPostResponseDTO> deleteProductPostById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(productPostService.deleteProductPostById(id, authentication));
    }

    @PostMapping("/group/{groupId}")
    public ResponseEntity<ProductPostResponseDTO> createProductPostInGroup(
            Authentication authentication,
            @PathVariable Long groupId,
            @RequestPart("productPost") @Valid ProductPostCreateDTO dto,
            @RequestPart("picturesFiles") List<MultipartFile> picturesFiles,
            UriComponentsBuilder uriBuilder){

        var res = productPostService.createProductPostInGroup(dto, groupId, authentication, picturesFiles);

        var uri = uriBuilder.path("/api/v1/products/{id}").buildAndExpand(res.getId()).toUri();
        return ResponseEntity.created(uri).body(res);
    }

}
