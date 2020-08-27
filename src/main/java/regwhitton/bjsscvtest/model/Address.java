package regwhitton.bjsscvtest.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotBlank
    @Schema(example = "11 George Street")
    String addressLine1;

    @Schema(example = "Grimley")
    String addressLine2;

    @Schema(example = "Cheam")
    String city;

    @Schema(example = "North Ruddles")
    String county;

    @NotBlank
    @Schema(example = "LD99 3JJ")
    String postalCode;
}