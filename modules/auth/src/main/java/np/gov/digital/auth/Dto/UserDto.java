package np.gov.digital.auth.Dto;

import jakarta.persistence.*;
import lombok.*;
import np.gov.digital.auth.modal.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

    private UUID id;


    private String username;


    private String email;


    private String password;

    private Boolean active = true;

    private Boolean locked = false;

    private Integer failedLoginAttempts = 0;

    private LocalDateTime lockUntil;

    private LocalDateTime lastLoginAt;

    private LocalDateTime passwordChangedAt;

    private UserRole role;

    private UUID provinceId;

    private UUID municipalityId;

    private UUID wardId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
