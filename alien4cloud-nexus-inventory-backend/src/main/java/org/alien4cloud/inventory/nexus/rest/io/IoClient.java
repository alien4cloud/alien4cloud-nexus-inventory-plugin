package org.alien4cloud.inventory.nexus.rest.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.IoConfiguration;
import org.alien4cloud.inventory.nexus.rest.RestException;
import org.alien4cloud.inventory.nexus.rest.io.model.ExportRequest;
import org.alien4cloud.inventory.nexus.rest.io.model.ZipRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

@Slf4j
@Component
public class IoClient {

        private static final String URL_PREFIX = "/api/export";

        @Resource
        private IoConfiguration configuration;

        @Resource(name="nexus-http-client")
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

        public void export(String userName, String tokenName, Collection<String> files) throws RestException {
                ExportRequest entity = new ExportRequest();
                entity.setTokenId(tokenName);
                entity.setUserName(userName);
                entity.getListExport().addAll(files);

                try {
                        HttpPost pr = new HttpPost(configuration.getUrl() + URL_PREFIX);
                        pr.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                        pr.setHeader(HttpHeaders.ACCEPT, "application/json");
                        pr.setEntity(new StringEntity(mapper.writeValueAsString(entity)));

                        try (CloseableHttpResponse response = httpClient.execute(pr)) {
                                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                                        throw new RestException("Can't submit export");
                                }
                        } catch (IOException e) {
                                throw new RestException("Can't submit export", e);
                        }
                } catch (JsonProcessingException | UnsupportedEncodingException e) {
                        throw new RestException("Can't submit export", e);
                }
        }

        public HttpEntity download(String filename) throws RestException {
                HttpUriRequest gr = new HttpGet(configuration.getUrl() + URL_PREFIX + "/file/" + filename);

                log.debug("Downloading on IO: {}",gr.getURI());

                try {
                        HttpResponse response = httpClient.execute(gr);
                        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                                throw new RestException("Can't download export");
                        }

                        return response.getEntity();
                } catch (IOException e) {
                        throw new RestException("Can't download export", e);
                }
        }

        public void delete(String filename) throws RestException {
                HttpUriRequest dr = new HttpDelete(configuration.getUrl() + URL_PREFIX  + "/" + filename);

                try (CloseableHttpResponse response = httpClient.execute(dr)) {
                        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                                throw new RestException("Can't delete export");
                        }
                } catch(IOException e) {
                        throw new RestException("Can't delete export", e);
                }
        }
}
