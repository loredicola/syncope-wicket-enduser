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

import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.client.enduser.SyncopeEnduserApplication;
import org.apache.syncope.client.enduser.SyncopeEnduserSession;
import org.apache.syncope.client.enduser.init.ClassPathScanImplementationLookup;
import org.apache.syncope.client.enduser.init.EnduserInitializer;
import org.apache.syncope.client.enduser.wicket.markup.head.MetaHeaderItem;
import org.apache.syncope.client.ui.commons.BaseSession;
import org.apache.syncope.client.ui.commons.Constants;
import org.apache.syncope.client.ui.commons.pages.Logout;
import org.apache.syncope.client.enduser.panels.Sidebar;
import org.apache.syncope.client.ui.commons.pages.BaseWebPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class BasePage extends BaseWebPage {

    private static final long serialVersionUID = 1571997737305598502L;

    protected static final HeaderItem META_IE_EDGE = new MetaHeaderItem("X-UA-Compatible", "IE=edge");

    protected final Sidebar sidebar;

    protected final WebMarkupContainer contentWrapper;

    protected final WebMarkupContainer footer;

    protected final AjaxLink<Void> collapse;

    public BasePage() {
        this(null);
    }

    public BasePage(final PageParameters parameters) {
        super(parameters);

        ClassPathScanImplementationLookup lookup = (ClassPathScanImplementationLookup) SyncopeEnduserApplication.get().
                getServletContext().getAttribute(EnduserInitializer.CLASSPATH_LOOKUP);

        Serializable leftMenuCollapse = SyncopeEnduserSession.get().getAttribute(Constants.MENU_COLLAPSE);
        if ((leftMenuCollapse instanceof Boolean) && ((Boolean) leftMenuCollapse)) {
            body.add(new AttributeAppender("class", " sidebar-collapse"));
        }

        // header, footer
        body.add(new Label("username", SyncopeEnduserSession.get().isAuthenticated()
                ? SyncopeEnduserSession.get().getSelfTO().getUsername() : StringUtils.EMPTY));

        // sidebar
        sidebar = new Sidebar("sidebar", getPageReference(), lookup.getExtPageClasses());
        sidebar.setOutputMarkupPlaceholderTag(true);
        body.add(sidebar);

        // contentWrapper
        contentWrapper = new WebMarkupContainer("contentWrapper");
        contentWrapper.setOutputMarkupPlaceholderTag(true);
        body.add(contentWrapper);

        // footer
        footer = new WebMarkupContainer("footer");
        footer.setOutputMarkupPlaceholderTag(true);
        body.add(footer);

        // collapse
        collapse = new AjaxLink<Void>("collapse") {

            private static final long serialVersionUID = -7978723352517770644L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                Session.get().setAttribute(Constants.MENU_COLLAPSE,
                        Session.get().getAttribute(Constants.MENU_COLLAPSE) == null
                        ? true
                        : !(Boolean) Session.get().getAttribute(Constants.MENU_COLLAPSE));
            }
        };

        collapse.setOutputMarkupPlaceholderTag(true);
        body.add(collapse);

        body.add(new Label("domain", BaseSession.class.cast(Session.get()).getDomain()));

        @SuppressWarnings("unchecked")
        final Class<? extends WebPage> beforeLogout = (Class<? extends WebPage>) Session.get().
                getAttribute(Constants.BEFORE_LOGOUT_PAGE);
        if (beforeLogout == null) {
            body.add(new BookmarkablePageLink<>("logout", Logout.class));
        } else {
            body.add(new AjaxLink<Page>("logout") {

                private static final long serialVersionUID = -7978723352517770644L;

                @Override
                protected void updateAjaxAttributes(final AjaxRequestAttributes attributes) {
                    super.updateAjaxAttributes(attributes);

                    AjaxCallListener ajaxCallListener = new AjaxCallListener();
                    ajaxCallListener.onPrecondition("return confirm('" + getString("confirmGlobalLogout") + "');");
                    attributes.getAjaxCallListeners().add(ajaxCallListener);
                }

                @Override
                public void onClick(final AjaxRequestTarget target) {
                    setResponsePage(beforeLogout);
                }
            });
        }
    }

    protected void disableSidebar() {
        sidebar.setVisible(false);
        collapse.setVisible(false);
        contentWrapper.add(new AttributeModifier("style", "margin-left: 0px"));
        footer.add(new AttributeModifier("style", "margin-left: 0px"));
    }

    protected void setDomain(final PageParameters parameters) {
        if (parameters != null && !parameters.get("domain").isEmpty()) {
            BaseSession.class.cast(Session.get()).setDomain(parameters.get("domain").toString());
        }
    }
}
