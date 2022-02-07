package de.uksh.medic.mpiproxy.proxy;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public final class FhirProxy {

    private FhirProxy() {
    }

    public static ResponseEntity<String> forward(String content, String target, HttpServletRequest request) {
        RestTemplate rt = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(target + request.getServletPath().replace("/fhir", ""));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        request.getParameterMap().entrySet().forEach(e -> {
            params.addAll(e.getKey(), Arrays.asList(e.getValue()));
        });
        builder.queryParams(params);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", request.getContentType());
        headers.add("Accept", request.getHeader("Accept"));
        ResponseEntity<String> r = rt.exchange(builder.build().encode().toUri(),
                HttpMethod.resolve(request.getMethod()), new HttpEntity<String>(content, headers), String.class);

        return r;
    }

}
