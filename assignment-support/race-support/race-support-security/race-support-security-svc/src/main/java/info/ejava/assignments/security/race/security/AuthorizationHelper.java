package info.ejava.assignments.security.race.security;

import info.ejava.assignments.api.race.client.races.RaceDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class AuthorizationHelper {
    public String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : null;
    }
    public boolean hasAuthority(String authority) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (principal instanceof UserDetails) ? ((UserDetails) principal).getAuthorities().stream()
                        .anyMatch(a->a.getAuthority().equals(authority)) : false;
    }
    public void isOwnerOrAuthority(java.util.function.Supplier<String> ownername, String authority) {
        if (null==authority || !hasAuthority(authority)) {
            if (!StringUtils.equals(ownername.get(), getUsername())) {
                throw new AccessDeniedException(
                        String.format("%s is not race owner or have %s authority", getUsername(), authority));
            }
        }
    }
}
