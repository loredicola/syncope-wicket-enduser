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

import org.apache.syncope.client.enduser.SyncopeEnduserApplication;
import org.apache.syncope.client.enduser.SyncopeEnduserSession;
import org.apache.syncope.client.enduser.layout.UserFormLayoutInfo;
import org.apache.syncope.client.enduser.panels.UserFormPanel;
import org.apache.syncope.common.rest.api.service.SyncopeService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class EditUser extends BasePage {

    private static final long serialVersionUID = -1100228004207271270L;

    public EditUser(final PageParameters parameters) {
        super(parameters);

        WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        contentWrapper.add(content);

        UserFormPanel editUserPanel = new UserFormPanel(
                "editUserPanel",
                SyncopeEnduserSession.get().getSelfTO(),
                SyncopeEnduserSession.get().getSelfTO(),
                SyncopeEnduserSession.get().getService(SyncopeService.class).platform().getUserClasses(),
                buildFormLayout(),
                getPageReference());
        editUserPanel.setOutputMarkupId(true);
        content.add(editUserPanel);
    }

    private UserFormLayoutInfo buildFormLayout() {
        UserFormLayoutInfo customlayoutInfo = SyncopeEnduserApplication.get().getCustomFormLayout();
        return customlayoutInfo != null ? customlayoutInfo : new UserFormLayoutInfo();
    }
}
