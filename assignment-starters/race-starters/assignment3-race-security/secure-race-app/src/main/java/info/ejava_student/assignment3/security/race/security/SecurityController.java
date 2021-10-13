package info.ejava_student.assignment3.security.race.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SecurityController {
    public static final String WHO_AM_I_PATH = "/api/whoAmI";
    public static final String AUTHORITIES_PATH = "/api/authorities";

    @RequestMapping(path=WHO_AM_I_PATH,
            method = RequestMethod.GET,
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> whoAmIGet(/*...?*/) {
        return null;
    }

    @RequestMapping(path=WHO_AM_I_PATH,
            method = RequestMethod.POST,
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> whoAmIPost(/*...?*/) {
        return null;
    }

    @RequestMapping(path=AUTHORITIES_PATH,
            method = RequestMethod.GET,
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> hasAuthority(
            String authority/*, ...*/) {
        return null;
    }
}
