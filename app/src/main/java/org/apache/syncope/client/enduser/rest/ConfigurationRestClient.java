/*
 *  Copyright (C) 2020 Tirasa (info@tirasa.net)
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.syncope.client.enduser.rest;

import static org.apache.syncope.client.enduser.rest.BaseRestClient.LOG;
import static org.apache.syncope.client.enduser.rest.BaseRestClient.getService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.syncope.common.lib.SyncopeClientException;
import org.apache.syncope.common.lib.to.AttrTO;
import org.apache.syncope.common.rest.api.service.ConfigurationService;

public class ConfigurationRestClient extends BaseRestClient {

    private static final long serialVersionUID = -2942072025958227690L;

    public static String getFormLayout(final String name) {
        try {
            AttrTO confParam = getService(ConfigurationService.class).get(name);
            return CollectionUtils.isNotEmpty(confParam.getValues()) ? confParam.getValues().get(0) : null;
        } catch (SyncopeClientException e) {
            LOG.error("While reading configuration parameter [{}]", name, e);
        }
        return null;
    }
}
