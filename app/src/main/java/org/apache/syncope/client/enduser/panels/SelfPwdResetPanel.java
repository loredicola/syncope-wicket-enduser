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

import org.apache.syncope.client.enduser.rest.UserSelfRestClient;
import org.apache.syncope.client.enduser.SyncopeEnduserApplication;
import org.apache.syncope.client.enduser.SyncopeEnduserSession;
import org.apache.syncope.client.enduser.pages.BasePage;
import org.apache.syncope.client.enduser.pages.Login;

import org.apache.syncope.client.ui.commons.Constants;
import org.apache.syncope.client.enduser.pages.SelfResult;
import org.apache.syncope.client.enduser.panels.captcha.CaptchaPanel;
import org.apache.syncope.common.lib.SyncopeClientException;
import org.apache.syncope.common.lib.to.SecurityQuestionTO;
import org.apache.syncope.common.lib.types.ClientExceptionType;
import org.apache.syncope.common.rest.api.service.SecurityQuestionService;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.IEventSource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfPwdResetPanel extends Panel implements IEventSource {

    private static final long serialVersionUID = -2841210052053545578L;

    private static final Logger LOG = LoggerFactory.getLogger(SelfPwdResetPanel.class);

    private String usernameText;

    private String securityAnswerText;

    private final TextField<String> securityQuestion;

    private final CaptchaPanel<Void> captcha;

    public SelfPwdResetPanel(final String id, final PageReference pageRef) {
        super(id);

        boolean isSecurityQuestionEnabled =
                SyncopeEnduserSession.get().getPlatformInfo().isPwdResetRequiringSecurityQuestions();

        TextField<String> username =
                new TextField<>("username", new PropertyModel<>(this, "usernameText"), String.class);
        username.add(new AjaxFormComponentUpdatingBehavior(Constants.ON_BLUR) {

            private static final long serialVersionUID = -1107858522700306810L;

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                if (isSecurityQuestionEnabled) {
                    loadSecurityQuestion(pageRef, target);
                }
            }
        });
        username.setRequired(true);
        add(username);

        Label sqLabel = new Label("securityQuestionLabel", new ResourceModel("securityQuestion", "securityQuestion"));
        sqLabel.setOutputMarkupPlaceholderTag(true);
        sqLabel.setVisible(isSecurityQuestionEnabled);
        add(sqLabel);

        securityQuestion =
                new TextField<>("securityQuestion", new PropertyModel<>(Model.of(), "content"), String.class);
        securityQuestion.setOutputMarkupId(true);
        securityQuestion.setEnabled(false);
        securityQuestion.setOutputMarkupPlaceholderTag(true);
        securityQuestion.setVisible(isSecurityQuestionEnabled);
        add(securityQuestion);

        Label notLoading = new Label("not.loading", new ResourceModel("not.loading", "not.loading"));
        notLoading.setOutputMarkupPlaceholderTag(true);
        notLoading.setVisible(isSecurityQuestionEnabled);
        add(notLoading);

        AjaxLink<Void> reloadLink = new AjaxLink<Void>("reloadLink") {

            private static final long serialVersionUID = -817438685948164787L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                loadSecurityQuestion(pageRef, target);
            }
        };
        reloadLink.setOutputMarkupPlaceholderTag(true);
        reloadLink.setVisible(isSecurityQuestionEnabled);
        add(reloadLink);

        Label saLabel = new Label("securityAnswerLabel", new ResourceModel("securityAnswer", "securityAnswer"));
        saLabel.setOutputMarkupPlaceholderTag(true);
        saLabel.setVisible(isSecurityQuestionEnabled);
        add(saLabel);

        TextField<String> securityAnswer =
                new TextField<>("securityAnswer", new PropertyModel<>(this, "securityAnswerText"), String.class);
        securityAnswer.add(new AjaxFormComponentUpdatingBehavior(Constants.ON_CHANGE) {

            private static final long serialVersionUID = -1107858522700306810L;

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                // do nothing
            }
        });
        securityAnswer.setRequired(isSecurityQuestionEnabled);
        securityAnswer.setOutputMarkupPlaceholderTag(true);
        securityAnswer.setVisible(isSecurityQuestionEnabled);
        add(securityAnswer);

        captcha = new CaptchaPanel<>("captchaPanel");
        captcha.setOutputMarkupPlaceholderTag(true);
        captcha.setVisible(SyncopeEnduserApplication.get().isCaptchaEnabled());
        add(captcha);

        AjaxButton submitButton = new AjaxButton("submit") {

            private static final long serialVersionUID = 4284361595033427185L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target) {
                boolean checked = true;
                if (SyncopeEnduserApplication.get().isCaptchaEnabled()) {
                    checked = captcha.check();
                }
                if (!checked) {
                    SyncopeEnduserSession.get().error(getString(Constants.CAPTCHA_ERROR));
                    ((BasePage) pageRef.getPage()).getNotificationPanel().refresh(target);
                } else {
                    PageParameters parameters = new PageParameters();
                    try {
                        UserSelfRestClient.requestPasswordReset(usernameText, securityAnswerText);
                        parameters.add(Constants.STATUS, Constants.OPERATION_SUCCEEDED);
                        parameters.add(Constants.NOTIFICATION_TITLE_PARAM, getString("self.pwd.reset.success"));
                        parameters.add(Constants.NOTIFICATION_MSG_PARAM, getString("self.pwd.reset.success.msg"));
                    } catch (SyncopeClientException sce) {
                        if (ClientExceptionType.NotFound.equals(sce.getType())) {
                            parameters.add(Constants.STATUS, Constants.OPERATION_SUCCEEDED);
                            parameters.add(Constants.NOTIFICATION_TITLE_PARAM, getString("self.pwd.reset.success"));
                            parameters.add(Constants.NOTIFICATION_MSG_PARAM, getString("self.pwd.reset.success.msg"));
                        } else {
                            parameters.add(Constants.STATUS, Constants.OPERATION_ERROR);
                            parameters.add(Constants.NOTIFICATION_TITLE_PARAM, getString("self.pwd.reset.error"));
                            parameters.add(Constants.NOTIFICATION_MSG_PARAM, getString("self.pwd.reset.error.msg"));
                            LOG.error("Unable to reset password of [{}]", usernameText, sce);
                            SyncopeEnduserSession.get().onException(sce);
                        }
                        ((BasePage) pageRef.getPage()).getNotificationPanel().refresh(target);
                    }
                    parameters.add(Constants.LANDING_PAGE, Login.class.getName());
                    setResponsePage(SelfResult.class, parameters);
                }
            }

        };
        submitButton.setOutputMarkupId(true);
        submitButton.setDefaultFormProcessing(false);
        add(submitButton);

        Button cancel = new Button("cancel") {

            private static final long serialVersionUID = 3669569969172391336L;

            @Override
            public void onSubmit() {
                setResponsePage(getApplication().getHomePage());
            }

        };
        cancel.setOutputMarkupId(true);
        cancel.setDefaultFormProcessing(false);
        add(cancel);
    }

    protected void loadSecurityQuestion(final PageReference pageRef, final AjaxRequestTarget target) {
        try {
            SecurityQuestionTO securityQuestionTO = SyncopeEnduserSession.get().getService(
                    SecurityQuestionService.class).readByUser(usernameText);
            // set security question field model
            securityQuestion.setModel(Model.of(securityQuestionTO.getContent()));
            target.add(securityQuestion);
        } catch (Exception e) {
            LOG.error("Unable to get security question for [{}]", usernameText, e);
            SyncopeEnduserSession.get().onException(e);
            ((BasePage) pageRef.getPage()).getNotificationPanel().refresh(target);
        }
    }
}
