package org.alien4cloud.inventory.nexus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.NexusConfiguration;
import org.alien4cloud.inventory.nexus.rest.model.ComponentItem;
import org.alien4cloud.inventory.nexus.rest.model.ComponentsRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class RestClient {

    private static final String COMPONENT_REQUEST = "/service/rest/v1/components?repository=";

    @Resource
    private NexusConfiguration configuration;

    @Resource
    private CloseableHttpClient httpClient;

    @Resource
    @Qualifier("json")
    private ObjectMapper mapper;

    public List<ComponentItem> getComponents() throws RestException {
        List<ComponentItem> result = Lists.newArrayList();

        String continuationToken = null;

        do {
            HttpUriRequest request = buildRequest(continuationToken);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RestException("Can't query components");
                }

                HttpEntity entity = response.getEntity();

                ComponentsRequest components = mapper.readValue(response.getEntity().getContent(), ComponentsRequest.class);
                result.addAll(components.getItems());

                continuationToken = components.getContinuationToken();
            } catch (IOException e) {
                throw new RestException("Can't query components", e);
            }
        } while (continuationToken != null);

        return result;
    }

    private HttpUriRequest buildRequest(String continuationToken) {
        String url = configuration.getUrl() + COMPONENT_REQUEST + configuration.getRepository();

        if (continuationToken != null) {
            url += "&continuationToken=";
            url += continuationToken;
        }

        return new HttpGet(url);
    }
}
