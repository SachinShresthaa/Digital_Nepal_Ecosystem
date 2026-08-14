package np.gov.digital.platformgis.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpsCaptureRequest {

    @NotNull(message = "Latitude is required when GPS block is present")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    @NotNull(message = "Longitude is required when GPS block is present")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;

    @NotNull(message = "GPS accuracy (meters) is required")
    private Integer accuracyM;

    private Integer elevationM;

    // Optional manual entry — FLOOD / LANDSLIDE / EARTHQUAKE / FIRE / NONE
    private String riskZone;
}