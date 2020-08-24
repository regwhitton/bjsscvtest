package regwhitton.bjsscvtest.restapi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    @Schema(description = "Description of the error", example = "an error message")
    String error;
}
