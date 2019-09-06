package info.ejava.examples.svc.authn.authcfg;

import info.ejava.examples.svc.authn.AuthConfigExampleApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes={AuthConfigExampleApp.class},
        properties = "test=true")
@AutoConfigureMockMvc
public class AuthConfigMockMvcNTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc anonymous;
    //example manual instantiation
    private MockMvc user;

    @BeforeEach
    public void init() {
        user = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }


    @Nested
    public class when_calling_permit_all {
        private final String uri = "/api/anonymous/hello";
        @Test
        public void anonymous_can_call_get() throws Exception {
            anonymous.perform(MockMvcRequestBuilders.get(uri).queryParam("name","jim"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello, jim :caller=(null)"));
        }
        @Test
        public void anonymous_can_call_post() throws Exception {
            anonymous.perform(MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("jim"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello, jim :caller=(null)"));
        }

        @WithMockUser("user")
        @Test
        public void user_can_call_get() throws Exception {
            user.perform(MockMvcRequestBuilders.get(uri)
                    .queryParam("name","jim"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello, jim :caller=user"));
        }

        @WithMockUser("user")
        @Test
        public void user_can_call_post() throws Exception {
            user.perform(MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("jim"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello, jim :caller=user"));
        }
    }

    @Nested
    public class when_calling_authn {
        private final String uri = "/api/authn/hello";
        @Test
        public void anonymous_cannot_call_get() throws Exception {
            anonymous.perform(MockMvcRequestBuilders.get(uri).queryParam("name","jim"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
        @Test
        public void anonymous_cannot_call_post() throws Exception {
            anonymous.perform(MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("jim"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @WithMockUser("user")
        @Test
        public void user_can_call_get() throws Exception {
            user.perform(MockMvcRequestBuilders.get(uri)
                    .queryParam("name","jim"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello, jim :caller=user"));
        }

        @WithMockUser("user")
        @Test
        public void user_can_call_post() throws Exception {
            user.perform(MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("jim"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello, jim :caller=user"));
        }
    }


    @Nested
    public class when_calling_alt {
        private final String uri = "/api/alt/hello";
        @Test
        public void anonymous_cannot_call_get() throws Exception {
            anonymous.perform(MockMvcRequestBuilders.get(uri).queryParam("name","jim"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
        @Test
        public void anonymous_cannot_call_post() throws Exception {
            anonymous.perform(MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("jim"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @WithMockUser("user")
        @Test
        public void user_can_call_get() throws Exception {
            user.perform(MockMvcRequestBuilders.get(uri)
                    .queryParam("name","jim"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello, jim :caller=user"));
        }

        @WithMockUser("user")
        @Test
        public void user_cannot_call_post() throws Exception {
            user.perform(MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("jim"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
