package regwhitton.bjsscvtest.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotBlank
    @Size(max = 100)
    @Schema(example = "11 George Street")
    String addressLine1;

    @Size(max = 100)
    @Schema(example = "Grimley")
    String addressLine2;

    @Size(max = 50)
    @Schema(example = "Cheam")
    String city;

    @Size(max = 50)
    @Schema(example = "North Ruddles")
    String county;

    @Size(max = 30)
    @NotBlank
    @Schema(example = "LD99 3JJ")
    String postalCode;
}