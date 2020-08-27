package regwhitton.bjsscvtest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * Represents a Curriculum Vitae
 */
@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Cv {

    @Id
    @GeneratedValue
    @Schema(accessMode = READ_ONLY)
    @Null(groups = CreateValidation.class)
    @NotNull(groups = UpdateValidation.class)
    Long id;

    @Version
    @Null(groups = CreateValidation.class)
    @NotNull(groups = UpdateValidation.class)
    @Schema(nullable = true,
            description = "Must not be provided on create. On update must provide the value previously read.")
    Long version;

    @NotBlank
    @Schema(example = "Joseph")
    String firstName;

    @Nullable
    @Schema(example = "Joe")
    String preferredFirstName;

    @Nullable
    @Schema(example = "Fredrick Douglas")
    String middleNames;

    @NotBlank
    @Schema(example = "Bloggs")
    String surname;

    @NotNull
    LocalDate dateOfBirth;

    @Email
    @Schema(example = "joe.bloggs@example.com")
    String email;

    @Nullable
    @Schema(example = "01999 999999")
    String telephone;

    @Embedded
    Address address;
}