package org.alien4cloud.inventory.nexus.rest;

import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.NexusConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class RestClient {

    private static final String COMPONENT_REQUEST = "/service/rest/v1/components?repository=";

    @Resource
    private NexusConfiguration configuration;

    @Resource
    private CloseableHttpClient httpClient;

    public void getComponents() throws RestException {
        String continuationToken = null;

        HttpUriRequest request = buildRequest(continuationToken);

        try(CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK) {
                throw new RestException("Can't query components");
            }
            log.info("response={}",response);
        } catch (IOException e) {
            throw new RestException("Can't query components",e);
        }
    }

    private HttpUriRequest buildRequest(String continuationToken) {
        String url = configuration.getUrl() + COMPONENT_REQUEST + configuration.getRepository();

        if (continuationToken != null) {
            url += "continuationToken=";
            url += continuationToken;
        }

        return new HttpGet(url);
    }
}
