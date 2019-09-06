package info.ejava.examples.svc.authz.authorities.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/whoAmI")
public class WhoAmIController {
    @GetMapping(produces={MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> getCallerInfo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails user) {

        List<?> values = (user!=null) ?
                Arrays.asList(user.getUsername(), user.getAuthorities()) :
                Arrays.asList("null");
        String text = StringUtils.join(values);

        ResponseEntity<String> response = ResponseEntity.ok(text);
        return response;
    }
}


