package info.ejava.examples.db.validation.contacts.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This constraint verifies whether the upper case value for the field is a valid
 * postal state abbreviation as of 1963
 *
 * @see https://about.usps.com/who-we-are/postal-history/state-abbreviations.htm
 */
public class PostalStateAbbreviationValidator implements ConstraintValidator<PostalStateAbbreviation,String> {
    public static final Set<String> STATES = Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NB", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "PR", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY").stream().collect(Collectors.toSet());
    private boolean normalize;

    @Override
    public void initialize(PostalStateAbbreviation annotation) {
        this.normalize = annotation.normalize();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value==null) {
            return true; //use @NotNull to enforce non-null
        }

        return normalize ? STATES.contains(value.toUpperCase()) : STATES.contains(value);
    }
}
