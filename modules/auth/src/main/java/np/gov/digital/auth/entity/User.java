package np.gov.digital.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import np.gov.digital.auth.enums.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private UUID wardId;

    private UUID municipalityId;

    private UUID provinceId;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Integer failedAttempts = 0;

    private LocalDateTime lockTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}