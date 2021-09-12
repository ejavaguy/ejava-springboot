package info.ejava.examples.common.web.paging;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class RestTemplateConfig {
    public RestTemplate restTemplateDebug(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.requestFactory(
                        //used to read the streams twice
                        ()->new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .build();

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RestTemplateLoggingFilter());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }
}
