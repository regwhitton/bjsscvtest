package regwhitton.bjsscvtest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ReturnType {
    @Schema(description = "My value", example = "20.5")
    @DecimalMax("20.5")
    BigDecimal value;
}