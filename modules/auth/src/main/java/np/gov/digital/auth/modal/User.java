package np.gov.digital.auth.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "User Name is required")
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false,unique = true)
    @NotNull(message = "Email  is required")
    @Email(message = "Email should be valid")
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private UUID provinceId;

    private UUID municipalityId;

    private UUID wardId;

    private Boolean active = true;

    private Boolean locked = false;

    private Integer failedLoginAttempts = 0;

    private LocalDateTime lockUntil;

    private LocalDateTime lastLoginAt;

    private LocalDateTime passwordChangedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



    @PrePersist
    protected void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}