package michael.m.marketProject.service.product_post_service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import michael.m.marketProject.custom_beans.*;
import michael.m.marketProject.dto.product_post_dto.ProductPostCreateDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostListDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostResponseDTO;
import michael.m.marketProject.dto.product_post_dto.ProductPostUpdateDTO;
import michael.m.marketProject.entity.ProductPost;
import michael.m.marketProject.entity.User;
import michael.m.marketProject.error.*;
import michael.m.marketProject.repository.ProductPostRepository;
import michael.m.marketProject.repository.UserGroupRepository;
import michael.m.marketProject.repository.UserRepository;
import michael.m.marketProject.service.file_storage_service.FileStorageService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductPostServiceImpl implements ProductPostService {

    private final ProductPostRepository productPostRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AdminRoleChecker adminRoleChecker;
    private final GetAuthenticatedRequestingUser getAuthenticatedRequestingUser;
    private final HandlePostDeletionFromSavedOnPostDeletion handlePostDeletionFromSavedOnPostDeletion;
    private final PropertyUpdater propertyUpdater;
    private final UserGroupRepository userGroupRepository;
    private final FileStorageService fileStorageService;
    private final HandleOldPicturesDeletionOnEntityChange handleOldPicturesDeletionOnEntityChange;

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public ProductPostResponseDTO createProductPost(ProductPostCreateDTO dto, Authentication authentication, List<MultipartFile> picturesFiles) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        if (dto.getLocation() == null){
            dto.setLocation(user.getLocation());
        }

        for(MultipartFile file : picturesFiles) {
            String fileName = fileStorageService.storeFile(file);
            dto.getPictures().add(fileName);
        }

        ProductPost productPost = modelMapper.map(dto, ProductPost.class);
        productPost.setUserId(user.getId());

        var saved = productPostRepository.save(productPost);

        return modelMapper.map(saved, ProductPostResponseDTO.class);
    }



    @PreAuthorize("isAuthenticated()")
    @Override
    public ProductPostListDTO getAllProductPosts(Authentication authentication, int pageNum, int pageSize, String sortDir, String... sortBy) {

        try {
            Sort.Direction sort = Sort.Direction.fromString(sortDir);
            var pageable = PageRequest.of(pageNum, pageSize, sort, sortBy);

            Page<ProductPost> pr;
            //get the page result from the repository:
            if (adminRoleChecker.isAdmin(authentication)) {
                pr = productPostRepository.findAll(pageable);
            }else {
                pr = productPostRepository.findAllPublicPosts(pageable);
            }

            if (pageNum > pr.getTotalPages()) {
                throw new PaginationException("Page Number " + pageNum + " Exceeds totalPages " + pr.getTotalPages());
            }

            List<ProductPostResponseDTO> postListDto =
                    pr.getContent().stream()
                            .map(p -> modelMapper.map(p, ProductPostResponseDTO.class))
                            .toList();

            return new ProductPostListDTO(
                    pr.getTotalElements(),
                    pr.getNumber(),
                    pr.getSize(),
                    pr.getTotalPages(),
                    pr.isFirst(),
                    pr.isLast(),
                    postListDto
            );
        } catch (IllegalArgumentException e) {
            throw new PaginationException(e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public ProductPostResponseDTO getProductPostById(Long id, Authentication authentication) {
        ProductPost post = getProductPostEntityOrThrow(id);
        if (post.getGroupId() != null){
            var group = userGroupRepository.findById(post.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("UserGroup", "id", post.getGroupId()));
            if(group.isPrivate()){
                User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
                if (!group.getGroupMembersIds().contains(user.getId())){
                    throw new GroupMembershipException(user.getEmail(), group.getId());
                }
            }
        }
        return modelMapper.map(post, ProductPostResponseDTO.class);
    }


    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public ProductPostResponseDTO updateProductPostById(Long id, ProductPostUpdateDTO dto, Authentication authentication, List<MultipartFile> picturesFiles) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);

        ProductPost post = getProductPostEntityOrThrow(id);

        if (!post.getUserId().equals(user.getId())) {
            throw new EntityOwnershipException(user.getEmail(), "ProductPost", post.getId());
        }

        if (picturesFiles != null && !picturesFiles.isEmpty()){
            handleOldPicturesDeletionOnEntityChange.deleteOldImages(post.getPictures());
            for(MultipartFile file : picturesFiles) {
                String fileName = fileStorageService.storeFile(file);
                dto.getPictures().add(fileName);
            }
        }

        propertyUpdater.updateNonNullProperties(dto, post);


        var saved = productPostRepository.save(post);

        return modelMapper.map(saved, ProductPostResponseDTO.class);

    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public synchronized ProductPostResponseDTO deleteProductPostById(Long id, Authentication authentication) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        ProductPost post = getProductPostEntityOrThrow(id);

        boolean isAdmin = adminRoleChecker.isAdmin(authentication);

        if (!post.getUserId().equals(user.getId()) && !isAdmin) {
            throw new EntityOwnershipException(user.getEmail(), "ProductPost", post.getId());
        }

        handlePostDeletionFromSavedOnPostDeletion.handleUserSavedPostsOnPostDeletion(post.getId());
        handleOldPicturesDeletionOnEntityChange.deleteOldImages(post.getPictures());

        productPostRepository.delete(post);
        return modelMapper.map(post, ProductPostResponseDTO.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public ProductPostResponseDTO saveOrUnsaveProductPostToUserByPostId(Long id, Authentication authentication) {
        int maxRetries = 10;
        int retryCount = 0;

        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        ProductPost post = getProductPostEntityOrThrow(id);

        boolean isPostAlreadySaved = user.getSavedPostsIds().contains(post.getId());

        while (true) {
            try {
                if (isPostAlreadySaved) {
                    user.getSavedPostsIds().remove(post.getId());
                    post.setSavedCount(post.getSavedCount() - 1);
                } else {
                    user.getSavedPostsIds().add(post.getId());
                    post.setSavedCount(post.getSavedCount() + 1);
                }

                productPostRepository.save(post);
                userRepository.save(user);

                return modelMapper.map(post, ProductPostResponseDTO.class);
            } catch (ObjectOptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new TrafficTooHighException();
                }
                try {
                    Thread.sleep((long) Math.pow(2, retryCount) * 10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    @Override
    public ProductPostResponseDTO createProductPostInGroup(ProductPostCreateDTO dto, Long groupId, Authentication authentication, List<MultipartFile> picturesFiles) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        var group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("UserGroup", "id", groupId));
        if (!group.getGroupMembersIds().contains(user.getId())){
            throw new GroupMembershipException(user.getEmail(), group.getId());
        }
        if (dto.getLocation() == null){
            dto.setLocation(user.getLocation());
        }
        for(MultipartFile file : picturesFiles) {
            String fileName = fileStorageService.storeFile(file);
            dto.getPictures().add(fileName);
        }
        ProductPost productPost = modelMapper.map(dto, ProductPost.class);
        productPost.setGroupId(groupId);
        productPost.setUserId(user.getId());
        var saved = productPostRepository.save(productPost);
        return modelMapper.map(saved, ProductPostResponseDTO.class);


    }


    private ProductPost getProductPostEntityOrThrow(Long id) {
        return productPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductPost", "id", id));
    }


}

