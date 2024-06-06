package michael.m.marketProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UQ_USER_EMAIL", columnNames = {"email"}),
        @UniqueConstraint(name = "UQ_USER_PHONE", columnNames = {"phone_number"}),
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 24)
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 24)
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "profile_picture")
    private String profilePicture;

    @NotNull
    @Column(name = "phone_number",length = 20)
    @Pattern(regexp = "^\\+\\d{1,15}$",
            message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull
    @Size(min = 2, max = 32)
    private String location;

    @NotNull
    @Email
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    private String email;

    @PrePersist
    private void setEmailToLowerCase() {
        this.email = this.email.toLowerCase();
    }

    @NotNull
    @Size(min = 8, max = 20, message = "Password should have between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$", message = "password must contain at least 1 lowercase letter, 1 uppercase letter, 1 digit and 1 special character")
    private String password;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = {@JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            )},
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            )
    )
    private Set<Role> roles;


    @OneToMany(mappedBy = "userId")
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Set<ProductPost> posts = new HashSet<>();


    @ElementCollection
    @CollectionTable(name = "user_saved_posts", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "post_id")
    private Set<Long> savedPostsIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_is_member_groups", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "group_id")
    private Set<Long> groupIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_is_admin_groups", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "group_id")
    private Set<Long> groupsUserIsAdminOf = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_is_pending_groups", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "group_id")
    private Set<Long> groupsUserIsPendingIn = new HashSet<>();

}