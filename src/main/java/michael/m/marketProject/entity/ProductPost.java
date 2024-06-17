package michael.m.marketProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<String> pictures = new ArrayList<>();

    @NotNull
    private int price;

    private int originalPrice;

    @NotNull
    private String currency;

    @NotNull
    private String category;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime contentUpdatedAt;

    @NotNull
    @Size(min = 2, max = 32)
    private String location;

    private Long userId;

    private int savedCount = 0;

    private Long groupId;
}
