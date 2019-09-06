package info.ejava.examples.db.validation.contacts.dto;

import javax.validation.GroupSequence;
import javax.validation.Payload;
import javax.validation.groups.Default;

public interface PocValidationGroups {
    public interface Create {};
    public interface Update {};
    public interface CreatePlusDefault extends Create, Default{};
    public interface UpdatePlusDefault extends Update, Default{};

    public interface SimplePlusDefault extends Default {}
    public interface DetailedOnly {}

    @GroupSequence({ SimplePlusDefault.class, DetailedOnly.class })
    public interface DetailOrder {};
}
