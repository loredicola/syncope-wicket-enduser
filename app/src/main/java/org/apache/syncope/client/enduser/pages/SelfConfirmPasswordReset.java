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

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.password.strength.PasswordStrengthBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.password.strength.PasswordStrengthConfig;
import org.apache.syncope.client.enduser.SyncopeEnduserSession;
import org.apache.syncope.client.ui.commons.Constants;
import org.apache.syncope.client.ui.commons.markup.html.form.AjaxPasswordFieldPanel;
import org.apache.syncope.client.ui.commons.markup.html.form.FieldPanel;
import org.apache.syncope.common.lib.SyncopeClientException;
import org.apache.syncope.common.rest.api.service.UserSelfService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SelfConfirmPasswordReset extends BasePage {

    private static final long serialVersionUID = -2166782304542750726L;

    public SelfConfirmPasswordReset(final PageParameters parameters) {
        super(parameters);

        setDomain(parameters);
        disableSidebar();

        if (parameters == null || parameters.get("token").isEmpty()) {
            LOG.error("No token parameter found in the request url");
            parameters.add("errorMessage", getString("self.confirm.pwd.reset.error.empty"));
            setResponsePage(getApplication().getHomePage(), parameters);
        }

        WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        contentWrapper.add(content);

        Form<?> form = new StatelessForm<>("selfConfirmPwdResetForm");
        form.setOutputMarkupId(true);
        content.add(form);

        AjaxPasswordFieldPanel passwordField = new AjaxPasswordFieldPanel(
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
        ((PasswordTextField) passwordField.getField()).setResetPassword(false);
        form.add(passwordField);

        FieldPanel<String> confirmPasswordField = new AjaxPasswordFieldPanel(
                "confirmPassword", "confirm-password", new Model<>(), false, null);
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setMarkupId("confirmPassword");
        confirmPasswordField.setPlaceholder(getString("confirm-password"));
        ((PasswordTextField) confirmPasswordField.getField()).setResetPassword(false);
        form.add(confirmPasswordField);

        form.add(new EqualPasswordInputValidator(passwordField.getField(), confirmPasswordField.getField()));

        AjaxButton submit = new AjaxButton("submit", new Model<>(getString("submit"))) {

            private static final long serialVersionUID = 509325877101838812L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target) {
                PageParameters params = new PageParameters();
                try {
                    SyncopeEnduserSession.get().getService(UserSelfService.class).confirmPasswordReset(
                            parameters.get("token").toString(), passwordField.getDefaultModelObjectAsString());
                    params.add(Constants.STATUS, Constants.OPERATION_SUCCEEDED);
                    params.add(Constants.NOTIFICATION_TITLE_PARAM, getString("self.confirm.pwd.reset.success"));
                    params.add(Constants.NOTIFICATION_MSG_PARAM, getString("self.confirm.pwd.reset.success.msg"));
                    SyncopeEnduserSession.get().success(getString(Constants.OPERATION_SUCCEEDED));
                } catch (SyncopeClientException sce) {
                    LOG.error("Unable to complete the 'Password Reset Confirmation' process", sce);
                    params.add(Constants.STATUS, Constants.OPERATION_ERROR);
                    params.add(Constants.NOTIFICATION_TITLE_PARAM, getString("self.confirm.pwd.reset.error"));
                    params.add(Constants.NOTIFICATION_MSG_PARAM, getString("self.confirm.pwd.reset.error.msg"));
                    SyncopeEnduserSession.get().onException(sce);
                    ((BasePage) getPageReference().getPage()).getNotificationPanel().refresh(target);
                }
                parameters.add(Constants.LANDING_PAGE, Login.class.getName());
                setResponsePage(SelfResult.class, params);
            }

            @Override
            protected void onError(final AjaxRequestTarget target) {
                notificationPanel.refresh(target);
            }
        };
        form.setDefaultButton(submit);
        form.add(submit);

        Button cancel = new Button("cancel") {

            private static final long serialVersionUID = 3669569969172391336L;

            @Override
            public void onSubmit() {
                setResponsePage(getApplication().getHomePage());
            }
        };
        cancel.setOutputMarkupId(true);
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);
    }
}
