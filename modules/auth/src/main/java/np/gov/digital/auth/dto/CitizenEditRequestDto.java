package np.gov.digital.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CitizenEditRequestDto {

    @NotNull
    private UUID citizenId;

    @NotNull
    private String changePayload;
}