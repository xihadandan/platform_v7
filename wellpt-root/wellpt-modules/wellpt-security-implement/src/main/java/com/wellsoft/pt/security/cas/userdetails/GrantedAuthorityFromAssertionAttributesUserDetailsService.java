/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wellsoft.pt.security.cas.userdetails;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Populates the {@link org.springframework.security.core.GrantedAuthority}s for a user by reading a list of attributes that were returned as
 * part of the CAS response.  Each attribute is read and each value of the attribute is turned into a GrantedAuthority.  If the attribute has no
 * value then its not added.
 *
 * @author Scott Battaglia
 * @since 3.0
 */
public final class GrantedAuthorityFromAssertionAttributesUserDetailsService
        extends AbstractCasAssertionUserDetailsService {

    private UserDetailsService userDetailsService;

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected UserDetails loadUserDetails(final Assertion assertion) {
        return getUserDetailsService().loadUserByUsername(assertion.getPrincipal().getName());
    }

}
