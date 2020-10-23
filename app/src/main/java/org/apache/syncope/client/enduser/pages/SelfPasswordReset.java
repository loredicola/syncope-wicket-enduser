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

import org.apache.syncope.client.enduser.panels.SelfPwdResetPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SelfPasswordReset extends BasePage {

    private static final long serialVersionUID = 164651008547631054L;

    private final SelfPwdResetPanel pwdResetPanel;

    public SelfPasswordReset(final PageParameters parameters) {
        super(parameters);

        setDomain(parameters);
        disableSidebar();

        WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        contentWrapper.add(content);

        Form<?> form = new Form<>("selfPwdResetForm");
        content.add(form);

        pwdResetPanel = new SelfPwdResetPanel("selfPwdResetPanel", getPageReference());
        pwdResetPanel.setOutputMarkupId(true);

        form.add(pwdResetPanel);
    }
}
