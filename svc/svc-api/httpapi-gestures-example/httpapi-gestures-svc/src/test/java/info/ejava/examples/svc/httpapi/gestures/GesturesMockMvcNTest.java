package info.ejava.examples.svc.httpapi.gestures;

import info.ejava.examples.svc.httpapi.GesturesApplication;
import info.ejava.examples.svc.httpapi.gestures.api.GesturesAPI;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class is an example unit integration test of a controller class.
 * There is a Spring context that creates components under test that
 * we communicate with directly through a MockMvc.
 */
@SpringBootTest(classes={GesturesApplication.class})
@ActiveProfiles("test")
@Tag("springboot")
@DisplayName("Gestures MockMVC Integration Test")
@Slf4j
@AutoConfigureMockMvc
public class GesturesMockMvcNTest {
    private static final String[] GESTURES_PATH="api/gestures".split("/");
    private static final String[] GESTURE_PATH= "api/gestures/{gestureType}".split("/");;

    private ServerConfig serverConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        serverConfig = new ServerConfig().build();
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURES_PATH)
                .build().toUri();

        mockMvc.perform(delete(url)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void add_new_gesture() throws Exception {
        //given
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");

        //when - adding a new gesture
        ResultActions response = mockMvc.perform(post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .content("hi"))
                .andDo(print());

        //then - it will be accepted and nothing returned
        response.andExpect(status().isCreated());
        response.andExpect(content().string(containsString("")));
        response.andExpect(header()
                .string(HttpHeaders.LOCATION, url.toString()));
    }

    @Test
    public void replace_gesture() throws Exception {
        //given
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");

        //when - we update the first time
        ResultActions response = mockMvc.perform(post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .content("hi"));

        //then -- gesture accepted and nothing returned
        response.andExpect(status().isCreated());
        response.andExpect(content().string(containsString("")));

        //when - an existing value gets updated
        response = mockMvc.perform(post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .content("howdy"))
                .andDo(print());

        //then - it gets accepted and initial gesture back in response
        response.andExpect(status().isOk());
        response.andExpect(content().string(equalTo("hi")));
        response.andExpect(header().doesNotExist(HttpHeaders.LOCATION));
    }

    @Test
    public void get_unknown_gesture_type() throws Exception {
        //given - unknown gesture
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("unknown");

        //when - requesting an unknown gesture
        ResultActions response = mockMvc.perform(get(url)
            .accept(MediaType.TEXT_PLAIN));

        //then - not found will be returned
        response.andExpect(status().isNotFound());
        response.andExpect(content().string(containsString("unknown")));
        response.andExpect(header().doesNotExist(HttpHeaders.LOCATION));
    }

    @Test
    public void get_gesture_without_target() throws Exception {
        //given - we have a known gesture present
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");
        mockMvc.perform(post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .content("howdy"))
                .andExpect(status().isCreated());

        //when - requesting a known gesture
        ResultActions response = mockMvc.perform(get(url)
                .accept(MediaType.TEXT_PLAIN));

        //then - gesture will be returned without target
        response.andExpect(status().isOk());
        response.andExpect(content().string("howdy"));
        response.andExpect(header()
                .string(HttpHeaders.CONTENT_LOCATION,url.toString()));
    }

    @Test
    public void get_gesture_with_target() throws Exception {
        //given - we have a known gesture present
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");
        mockMvc.perform(post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .content("howdy"))
                .andExpect(status().isCreated());

        //when - requesting a known gesture
        ResultActions response = mockMvc.perform(get(url)
                .accept(MediaType.TEXT_PLAIN)
                .queryParam("target","jim"));

        //then - gesture will be returned with target added
        response.andExpect(status().isOk());
        response.andExpect(content().string("howdy, jim"));
        String expectedLocation = UriComponentsBuilder.fromUri(url)
                .queryParam("target","jim").toUriString();
        response.andExpect(header()
                .string(HttpHeaders.CONTENT_LOCATION, expectedLocation));
    }

    @Test
    public void delete_unknown_gesture() throws Exception {
        //given
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("unknown");

        //when - deleting unknown gesture
        ResultActions response = mockMvc.perform(delete(url));

        //then - will receive success with no content
        response.andExpect(status().isNoContent());
    }

    @Test
    public void delete_known_gesture() throws Exception {
        //given - we have a known gesture present
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");
        mockMvc.perform(post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .content("howdy"))
                .andExpect(status().isCreated());

        //when - deleting known gesture
        ResultActions response = mockMvc.perform(delete(url));

        //then - will receive success with no content
        response.andExpect(status().isNoContent());
        //and then the gestureType will be unknown
        mockMvc.perform(get(url)
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_all_gestures() throws Exception {
        //given
        List<String> gestureTypes = Arrays.asList("hello", "goodbye");
        for (String gestureType : gestureTypes) {
            URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                    .path(GesturesAPI.GESTURE_PATH).build(gestureType);
            mockMvc.perform(post(url)
                    .accept(MediaType.TEXT_PLAIN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("aloha"))
                    .andExpect(status().isCreated());
        }

        //when deleting all gestures
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .pathSegment(GESTURES_PATH).build().toUri();
        ResultActions response = mockMvc.perform(delete(url));

        //then - collection was cleared
        response.andExpect(status().isNoContent());
        //and then no gestures left
        for (String gestureType : gestureTypes) {
            url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                    .pathSegment(GESTURE_PATH).build(gestureType);
            mockMvc.perform(get(url)
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isNotFound());
        }
    }
}
