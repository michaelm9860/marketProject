package michael.m.marketProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 32)
    private String productName;

    @NotNull
    @Column(length = 512)
    private String description;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> pictures;

    @NotNull
    private int price;

    @NotNull
    private String category;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotNull
    @Size(min = 2, max = 32)
    private String location;

    //    @Column(name = "user_id")
    private Long userId;

    //    @Column(name = "saved_count")
    private int savedCount = 0;

    //    @Column(name = "group_id")
    private Long groupId;
}
