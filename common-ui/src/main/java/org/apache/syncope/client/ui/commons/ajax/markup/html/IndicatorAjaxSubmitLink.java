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
package org.apache.syncope.client.ui.commons.ajax.markup.html;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;

/**
 * An {@link AjaxSubmitLink} not showing veil.
 */
public abstract class IndicatorAjaxSubmitLink extends AjaxSubmitLink implements IAjaxIndicatorAware {

    private static final long serialVersionUID = 2199328860134082968L;

    public IndicatorAjaxSubmitLink(final String id) {
        super(id);
    }

    public IndicatorAjaxSubmitLink(final String id, final Form<?> form) {
        super(id, form);
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return StringUtils.EMPTY;
    }

}
