package regwhitton.bjsscvtest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * Represents a Curriculum Vitae
 */
@Entity
@Data
@Builder(toBuilder = true)
public class Cv {

    @Id
    @GeneratedValue
    @Schema(accessMode = READ_ONLY)
    Long id;

    @NotBlank
    String firstName;

    @Nullable
    String preferredFirstName;

    @NotBlank
    String surname;

}