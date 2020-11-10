package org.alien4cloud.inventory.nexus.importclaim;

import alien4cloud.dao.ESGenericSearchDAO;
import alien4cloud.dao.model.GetMultipleDataResult;
import alien4cloud.exception.IndexingServiceException;
import org.alien4cloud.inventory.nexus.importclaim.model.ImportClaim;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ImportDao extends ESGenericSearchDAO {

    @PostConstruct
    public void init() {
        try {
            getMappingBuilder().initialize("org.alien4cloud.inventory.nexus.importclaim.model");
        } catch (IntrospectionException | IOException e) {
            throw new IndexingServiceException("Could not initialize elastic search mapping builder", e);
        }
        initIndices("importclaim", null, ImportClaim.class);
        initCompleted();
    }

    public List<ImportClaim> getImportClaims(String user) {
       Map<String,String[]> filters = new HashMap<String,String[]>();
       String[] users = {user};
       filters.put("user", users);
       GetMultipleDataResult<ImportClaim> result = search (ImportClaim.class, null, filters, 10000);
       return Arrays.asList(result.getData());
    }

    public ImportClaim getImportClaimForFile (String fileName) {
       Map<String,String[]> filter = new HashMap<String,String[]>();
       filter.put("fileName", new String[]{fileName});
       GetMultipleDataResult<ImportClaim> result = find (ImportClaim.class, filter, 1);
       if (result.getTotalResults() == 0) {
         return null;
       } else {
         return result.getData()[0];
       }
    }
}
