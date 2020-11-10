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
package org.apache.syncope.client.enduser.rest;

import static org.apache.syncope.client.enduser.rest.BaseRestClient.getService;
import static org.apache.syncope.client.enduser.rest.BaseRestClient.resetClient;

import javax.ws.rs.core.GenericType;
import org.apache.syncope.common.lib.patch.UserPatch;
import org.apache.syncope.common.lib.to.ProvisioningResult;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.rest.api.service.UserSelfService;

public class UserSelfRestClient extends BaseRestClient {

    private static final long serialVersionUID = -1575748964398293968L;

    public static void changePassword(final String password) {
        getService(UserSelfService.class).mustChangePassword(password);
    }

    public static void requestPasswordReset(final String username, final String securityAnswer) {
        getService(UserSelfService.class).requestPasswordReset(username, securityAnswer);
    }

    public ProvisioningResult<UserTO> create(final UserTO userTO, final boolean storePassword) {
        ProvisioningResult<UserTO> result;
        result = getService(UserSelfService.class).create(userTO, storePassword).readEntity(
                new GenericType<ProvisioningResult<UserTO>>() {
        });
        return result;
    }

    public ProvisioningResult<UserTO> update(final String etag, final UserPatch userPatch) {
        ProvisioningResult<UserTO> result;
        synchronized (this) {
            result = getService(etag, UserSelfService.class).update(userPatch).
                    readEntity(new GenericType<ProvisioningResult<UserTO>>() {
                    });
            resetClient(UserSelfService.class);
        }
        return result;
    }
}
