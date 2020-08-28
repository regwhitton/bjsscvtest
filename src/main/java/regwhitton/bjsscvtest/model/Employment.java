package regwhitton.bjsscvtest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static javax.persistence.FetchType.LAZY;

/**
 * Represents an employment on a Curriculum Vitae
 */
@Entity
@Data
@ToString(exclude = "cv")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Employment {

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

    @ManyToOne(fetch = LAZY)
    @Schema(hidden = true)
    @JsonIgnore
    Cv cv;

    @NotNull
    LocalDate fromDate;

    @Nullable
    LocalDate untilDate;

    @NotBlank
    @Length(max = 50)
    @Schema(example = "Manufabaru Plc")
    String company;

    @NotBlank
    @Length(max = 50)
    @Schema(example = "Creative Designer")
    String position;

    @NotBlank
    @Length(max = 5000)
    @Schema(example = "Responsible for designing and creating designs and other graphical stuff.")
    String summary;

    /**
     * Expose the id of the {@link Cv} as a read only derived property in the JSON.
     */
    @Schema(accessMode = READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getCvId() {
        return cv.getId();
    }

}