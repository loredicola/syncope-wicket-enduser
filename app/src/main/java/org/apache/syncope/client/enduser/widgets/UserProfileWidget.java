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
package org.apache.syncope.client.enduser.widgets;

import org.apache.syncope.client.enduser.SyncopeEnduserSession;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

public class UserProfileWidget extends BaseWidget {

    private static final long serialVersionUID = 4437711189800676363L;

    protected final UserTO userTO;

    public UserProfileWidget(final String id) {
        super(id);

        userTO = SyncopeEnduserSession.get().getSelfTO();

        WebMarkupContainer userProfile = new WebMarkupContainer("userProfile");
        userProfile.setOutputMarkupId(true);
        add(userProfile);

        Label welcome = new Label("welcome", userTO.getUsername());
        welcome.setOutputMarkupId(true);
        userProfile.add(welcome);

        addBaseFields(userProfile);
        addExtFields(userProfile);
    }

    public void addBaseFields(final WebMarkupContainer userProfile) {
        Label username = new Label("username", userTO.getUsername());
        username.setOutputMarkupId(true);
        userProfile.add(username);

        Label changePwdDate = new Label("changePwdDate", userTO.getChangePwdDate());
        changePwdDate.setOutputMarkupId(true);
        userProfile.add(changePwdDate);

        Label lastLoginDate = new Label("lastLoginDate", userTO.getLastLoginDate());
        lastLoginDate.setOutputMarkupId(true);
        userProfile.add(lastLoginDate);
    }

    public void addExtFields(final WebMarkupContainer userProfile) {
    }
}
