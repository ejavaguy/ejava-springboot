package info.ejava.assignments.security.race.security;

import info.ejava.assignments.api.race.client.races.RaceDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.function.Supplier;

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
    public boolean isUsername(String username) {
        return StringUtils.equals(username, getUsername());
    }

    public void assertAuthorityOrOwner(String authority, Supplier<String> ownername) {
        if (null==authority || !hasAuthority(authority)) {
            if (!StringUtils.equals(ownername.get(), getUsername())) {
                throw new AccessDeniedException(
                        String.format("%s is not race owner or have %s authority", getUsername(), authority));
            }
        }
    }

    public void assertOwnerOrRole(String ownername, Supplier<Boolean> hasRole) {
        if (!(StringUtils.equals(ownername, getUsername()) || (hasRole!=null && hasRole.get()))) {
            throw new AccessDeniedException(
                    String.format("%s is not race owner or have required role", getUsername()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean assertAdmin() { return true; }
    @PreAuthorize("hasRole('MGR')")
    public boolean assertMgr() { return true; }
}
