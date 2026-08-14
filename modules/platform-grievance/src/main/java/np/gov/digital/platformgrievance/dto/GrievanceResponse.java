package np.gov.digital.platformgrievance.dto;

import lombok.*;
import np.gov.digital.platformgrievance.enums.GrievanceCategory;
import np.gov.digital.platformgrievance.enums.GrievanceStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrievanceResponse {
    private UUID id;
    private UUID citizenId;
    private GrievanceCategory category;
    private String trackingCode;
    private GrievanceStatus status;
    private Instant filedAt;
    private Instant slaDueAt;
    private String message;
}
