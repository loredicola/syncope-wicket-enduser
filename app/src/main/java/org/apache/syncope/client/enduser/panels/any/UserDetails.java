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
package org.apache.syncope.client.enduser.panels.any;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.password.strength.PasswordStrengthBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.password.strength.PasswordStrengthConfig;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.client.enduser.rest.RealmRestClient;
import org.apache.syncope.client.ui.commons.ajax.markup.html.LabelInfo;
import org.apache.syncope.client.ui.commons.markup.html.form.AjaxDropDownChoicePanel;
import org.apache.syncope.client.ui.commons.markup.html.form.AjaxTextFieldPanel;
import org.apache.syncope.client.ui.commons.markup.html.form.FieldPanel;
import org.apache.syncope.client.ui.commons.wizards.any.PasswordPanel;
import org.apache.syncope.client.ui.commons.wizards.any.UserWrapper;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.wicket.PageReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class UserDetails extends Details<UserTO> {

    private static final long serialVersionUID = 6592027822510220463L;

    private static final String PASSWORD_CONTENT_PATH = "body:content";

    private final FieldPanel<String> realm;

    protected final AjaxTextFieldPanel username;

    protected final UserTO userTO;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public UserDetails(
            final String id,
            final UserWrapper wrapper,
            final boolean templateMode,
            final boolean includeStatusPanel,
            final boolean showPasswordManagement,
            final PageReference pageRef) {
        super(id, wrapper, templateMode, includeStatusPanel, pageRef);

        userTO = wrapper.getInnerObject();
        // ------------------------
        // Username
        // ------------------------
        username = new AjaxTextFieldPanel("username", "username", new PropertyModel<>(userTO, "username"), false);

        if (wrapper.getPreviousUserTO() != null && StringUtils.
                compare(wrapper.getPreviousUserTO().getUsername(), wrapper.getInnerObject().getUsername()) != 0) {
            username.showExternAction(new LabelInfo("externalAction", wrapper.getPreviousUserTO().getUsername()));
        }

        if (templateMode) {
            username.enableJexlHelp();
        } else {
            username.addRequiredLabel();
        }
        add(username);
        // ------------------------

        // ------------------------
        // Realm
        // ------------------------
        realm = new AjaxDropDownChoicePanel<>(
                "destinationRealm", "destinationRealm", new PropertyModel<>(userTO, "realm"), false);

        ((AjaxDropDownChoicePanel<String>) realm).setChoices(
                RealmRestClient.list().stream().map(RealmTO::getFullPath).collect(Collectors.toList()));
        add(realm);

    }

    public static class EditUserPasswordPanel extends Panel {

        private static final long serialVersionUID = -8198836979773590078L;

        public EditUserPasswordPanel(
                final String id,
                final UserWrapper wrapper,
                final boolean templateMode) {
            super(id);
            setOutputMarkupId(true);
            add(new Label("warning", new ResourceModel("password.change.warning")));
            add(new PasswordPanel("passwordPanel", wrapper, templateMode, new PasswordStrengthBehavior(
                    new PasswordStrengthConfig()
                            .withDebug(true)
                            .withShowVerdictsInsideProgressBar(true)
                            .withShowProgressBar(true))));
        }

    }
}
