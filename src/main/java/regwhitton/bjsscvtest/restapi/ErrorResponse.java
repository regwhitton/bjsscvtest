package regwhitton.bjsscvtest.restapi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Description of the error")
    String error;
}
