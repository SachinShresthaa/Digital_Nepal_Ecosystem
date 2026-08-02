package np.gov.digital.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectionRequest {

    @NotBlank
    private String reason;

}