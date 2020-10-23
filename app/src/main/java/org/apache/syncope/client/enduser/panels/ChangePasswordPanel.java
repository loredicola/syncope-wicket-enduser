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
package org.apache.syncope.client.enduser.panels;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.password.strength.PasswordStrengthBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.password.strength.PasswordStrengthConfig;
import org.apache.syncope.client.ui.commons.markup.html.form.AjaxPasswordFieldPanel;
import org.apache.syncope.client.ui.commons.panels.NotificationPanel;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ChangePasswordPanel extends Panel {

    protected static final Logger LOG = LoggerFactory.getLogger(ChangePasswordPanel.class);

    private static final long serialVersionUID = -8937593602426944714L;

    protected final AjaxPasswordFieldPanel passwordField;

    protected final AjaxPasswordFieldPanel confirmPasswordField;

    public ChangePasswordPanel(final String id, final NotificationPanel notificationPanel) {
        super(id);
        final StatelessForm<Void> form = new StatelessForm<>("changePassword");
        form.setOutputMarkupId(true);
        add(form);

        passwordField = new AjaxPasswordFieldPanel(
                "password",
                getString("password"),
                new Model<>(),
                false,
                new PasswordStrengthBehavior(
                        new PasswordStrengthConfig()
                                .withDebug(true)
                                .withShowVerdictsInsideProgressBar(true)
                                .withShowProgressBar(true)));
        passwordField.setRequired(true);
        passwordField.setMarkupId("password");
        passwordField.setPlaceholder("password");

        ((PasswordTextField) passwordField.getField()).setResetPassword(true);
        form.add(passwordField);

        confirmPasswordField = new AjaxPasswordFieldPanel("confirmPassword", "confirmPassword", new Model<>());
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setMarkupId("confirmPassword");
        confirmPasswordField.setPlaceholder("confirmPassword");
        ((PasswordTextField) confirmPasswordField.getField()).setResetPassword(true);
        form.add(confirmPasswordField);

        form.add(new EqualPasswordInputValidator(passwordField.getField(), confirmPasswordField.getField()));

        AjaxButton submitButton = new AjaxButton("submit", new Model<>(getString("submit"))) {

            private static final long serialVersionUID = 429178684321093953L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target) {
                doSubmit(target, passwordField);
            }

            @Override
            protected void onError(final AjaxRequestTarget target) {
                notificationPanel.refresh(target);
            }
        };
        form.add(submitButton);
        form.setDefaultButton(submitButton);

        Button cancel = new Button("cancel") {

            private static final long serialVersionUID = 3669569969172391336L;

            @Override
            public void onSubmit() {
                doCancel();
            }
        };
        cancel.setOutputMarkupId(true);
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);
    }

    public AjaxPasswordFieldPanel getPasswordField() {
        return passwordField;
    }

    public AjaxPasswordFieldPanel getConfirmPasswordField() {
        return confirmPasswordField;
    }

    protected abstract void doSubmit(AjaxRequestTarget target, AjaxPasswordFieldPanel passwordField);

    protected abstract void doCancel();

    protected abstract UserTO getLoggedUser();
}
