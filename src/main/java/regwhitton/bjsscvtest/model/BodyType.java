package regwhitton.bjsscvtest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;

@Data
public class BodyType {
    @Schema(description = "My value", example = "100.5")
    @DecimalMax("100.5")
    BigDecimal value;
}