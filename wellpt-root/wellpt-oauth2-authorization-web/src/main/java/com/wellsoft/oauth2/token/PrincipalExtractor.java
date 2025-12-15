package com.wellsoft.oauth2.token;

import java.util.Map;

public interface PrincipalExtractor {

    /**
     * Extract the principal that should be used for the token.
     *
     * @param map the source map
     * @return the extracted principal or {@code null}
     */
    Object extractPrincipal(Map<String, Object> map);

}