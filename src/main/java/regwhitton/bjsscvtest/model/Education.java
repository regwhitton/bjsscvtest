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
 * Represents a part of the education on a Curriculum Vitae
 */
@Entity
@Data
@ToString(exclude = "cv")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Education {

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

    @NotBlank
    @Length(max = 50)
    @Schema(example = "Portmouth Polytechnic")
    String establishment;

    @NotBlank
    @Length(max = 200)
    @Schema(example = "BSc Graphical Design")
    String award;

    @Nullable
    LocalDate awardDate;

    /**
     * Expose the id of the {@link Cv} as a read only derived property in the JSON.
     */
    @Schema(accessMode = READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getCvId() {
        return cv.getId();
    }
}