package info.ejava.examples.db.validation.contacts.svc;

import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.dto.PocValidationGroups;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.util.Optional;

public interface PocService {
    /**
     * Creates a new point of contact for a person.
     * @param personDTO person contact info
     * @return personDTO created, with ID
     */
    @NotNull
    /*
    @Validated here augments the parameter validation to use
    the specified groups.
     */
    @Validated(PocValidationGroups.CreatePlusDefault.class)
    public PersonPocDTO createPOC(
            @NotNull
            @Valid
            @Named("person") PersonPocDTO personDTO);

    @NotNull
    public Optional<PersonPocDTO> getPOC(
            @NotNull
            @Pattern(regexp = "[0-9]+", message = "must be a valid number")
            @Named("id") String id);

    public void updatePOC(
            @NotNull @Named("id") String id,
            @NotNull @Named("person") PersonPocDTO personDTO);

    public long deletePOC(@NotNull @Named("id") String id);

    public long deleteAllPOCs();

    @NotNull
    Page<PersonPocDTO> findPOCsMatchingAll(
            @NotNull @Named("probe") PersonPocDTO probe,
            @NotNull @Named("pageable") Pageable pageable);

    /**
     * Used to trigger an internal validation error.
     */
    PersonPocDTO positiveOrZero(@PositiveOrZero int value);
}
