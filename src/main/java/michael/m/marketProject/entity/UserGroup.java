package michael.m.marketProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 32)
    private String name;

    @Column(length = 512)
    private String description = "";

    @NotNull
    @ElementCollection
    @CollectionTable(name = "group_members", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "member_id")
    private Set<Long> groupMembersIds = new HashSet<>();

    @NotNull
    @ElementCollection
    @CollectionTable(name = "group_admins", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "admin_id")
    private Set<Long> groupAdminsIds = new HashSet<>();

    private String image;

    @OneToMany(mappedBy = "groupId")
    private Set<ProductPost> groupPosts = new HashSet<>();


    private boolean isPrivate;

    @NotNull
    @ElementCollection
    @CollectionTable(name = "group_pending_members", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "pending_user_id")
    private Set<Long> pendingMembersIds = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

}