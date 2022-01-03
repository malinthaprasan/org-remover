/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.apimgt.rest.api.sample.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.am.choreo.extensions.cleanup.service.OrganizationCleanupManager;
import org.wso2.carbon.apimgt.cleanup.service.OrganizationPurge;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegistrationServiceImpl {

    private static final Log log = LogFactory.getLog(RegistrationServiceImpl.class);

    @GET
    public Response purge(@QueryParam("organizationId") String organizationId) {

        if ("c1b2cfb2-e965-4d28-b36d-b34f162ecc30".equals(organizationId) ||
                "783c6c4d-8b9b-4190-b70a-e717ab1ee739".equals(organizationId) ||
                "5659b6b7-1063-41ed-8e39-d91857699255".equals(organizationId)) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity("Are you kidding! Choreo Org is not allowed to remove!").
                    build();
        }

        Map<String, String> result = new LinkedHashMap<>();
        Set<OrganizationPurge> orgPurgeList = OrganizationCleanupManager.getOrganizationPurgeServiceList();
        boolean isStartedTenantFlow = false;
        try {
            PrivilegedCarbonContext.startTenantFlow();
            isStartedTenantFlow = true;
            PrivilegedCarbonContext.getThreadLocalCarbonContext().
                    setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, true);
            log.info("Thread :" + Thread.currentThread().getName() + " will start to execute "
                    + "organization " + organizationId + " cleanup task.");

            for (OrganizationPurge orgPurgeService : orgPurgeList) {
                LinkedHashMap<String, String> orgPurgeServiceResult = orgPurgeService.purge(organizationId);
                result.putAll(orgPurgeServiceResult);
            }
        } finally {
            if (isStartedTenantFlow) {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
        return Response.status(Response.Status.OK).
                entity(result).
                build();
    }

}
