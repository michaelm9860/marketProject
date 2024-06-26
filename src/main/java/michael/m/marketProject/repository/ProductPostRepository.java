package michael.m.marketProject.repository;

import michael.m.marketProject.entity.ProductPost;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPostRepository extends JpaRepository<ProductPost, Long> {
    List<ProductPost> findProductPostsByUserId(Long userId);

    @Query(value = "SELECT p.* FROM product_post p LEFT JOIN user_group g ON p.group_id = g.id WHERE g.is_private = false OR p.group_id IS NULL",
            countQuery = "SELECT COUNT(*) FROM product_post p LEFT JOIN user_group g ON p.group_id = g.id WHERE g.is_private = false OR p.group_id IS NULL",
            nativeQuery = true)
    Page<ProductPost> findAllPublicPosts(Pageable pageable);


}