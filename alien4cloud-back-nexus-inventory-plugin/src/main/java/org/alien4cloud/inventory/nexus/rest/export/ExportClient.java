package org.alien4cloud.inventory.nexus.rest.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.ExportConfiguration;
import org.alien4cloud.inventory.nexus.rest.RestException;
import org.alien4cloud.inventory.nexus.rest.export.model.ZipRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class ExportClient {

        private static final String URL_PREFIX = "/api/export";

        @Resource
        private ExportConfiguration configuration;

        @Resource
        private CloseableHttpClient httpClient;

        @Resource
        @Qualifier("json")
        private ObjectMapper mapper;

        public ZipRequest get(String userName) throws RestException {
                HttpUriRequest request = new HttpGet(configuration.getUrl() + URL_PREFIX + "/" + userName);

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                        ZipRequest zips = mapper.readValue(response.getEntity().getContent(), ZipRequest.class);
                        return zips;
                } catch (IOException e) {
                        throw new RestException("Can't query exports", e);
                }
        }
}
