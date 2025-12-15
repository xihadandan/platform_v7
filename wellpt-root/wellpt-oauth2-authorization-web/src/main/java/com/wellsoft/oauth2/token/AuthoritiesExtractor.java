package com.wellsoft.oauth2.token;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;

public interface AuthoritiesExtractor {

    /**
     * Extract the authorities from the resource server's response.
     *
     * @param map the response
     * @return the extracted authorities
     */
    List<GrantedAuthority> extractAuthorities(Map<String, Object> map);

}