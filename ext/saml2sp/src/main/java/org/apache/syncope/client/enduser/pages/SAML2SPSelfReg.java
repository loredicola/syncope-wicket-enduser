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
package org.apache.syncope.client.enduser.pages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.syncope.common.lib.to.AttrTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.ext.saml2lsp.agent.Constants;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SAML2SPSelfReg extends WebPage {

    private static final Logger LOG = LoggerFactory.getLogger(SAML2SPSelfReg.class);

    private static final long serialVersionUID = -4330637558823990359L;

    private static final String SAML_ACCESS_ERROR = "SAML 2.0 error - while getting user attributes";

    private static final ObjectMapper MAPPER =
            new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    public SAML2SPSelfReg(final PageParameters parameters) {
        super(parameters);

        PageParameters params = new PageParameters();
        try {
            UserTO newUser = new UserTO();
            for (AttrTO attr : MAPPER.readValue(((ServletWebRequest) getRequest()).getContainerRequest().
                    getSession().getAttribute(Constants.SAML2SP_USER_ATTRS).toString(), AttrTO[].class)) {

                if ("username".equals(attr.getSchema())) {
                    newUser.setUsername(attr.getValues().get(0));
                } else {
                    newUser.getPlainAttrs().add(attr);
                }
            }

            params.add("newUser", MAPPER.writeValueAsString(newUser));
        } catch (Exception e) {
            LOG.error("While extracting user attributes", e);

            params.add("errorMessage", SAML_ACCESS_ERROR);
        }
        setResponsePage(getApplication().getHomePage(), params);
    }
}
