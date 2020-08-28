package regwhitton.bjsscvtest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

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
    @Schema(example = "Hugh")
    String middleNames;

    @NotBlank
    @Schema(example = "Bloggs")
    String surname;

    @NotBlank
    @Schema(example = "Sanitation Engineer")
    String summary;

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

    @OneToMany(mappedBy = "cv", fetch = LAZY, cascade = ALL)
    @Schema(hidden = true)
    @JsonIgnore
    @NotNull
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "cv", fetch = LAZY, cascade = ALL)
    @Schema(hidden = true)
    @JsonIgnore
    @NotNull
    @Builder.Default
    private List<Employment> employmentHistory = new ArrayList<>();

    @OneToMany(mappedBy = "cv", fetch = LAZY, cascade = ALL)
    @Schema(hidden = true)
    @JsonIgnore
    @NotNull
    @Builder.Default
    private List<Education> education = new ArrayList<>();
}