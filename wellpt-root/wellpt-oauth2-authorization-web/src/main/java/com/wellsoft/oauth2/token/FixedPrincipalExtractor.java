package com.wellsoft.oauth2.token;

import java.util.Map;

public class FixedPrincipalExtractor implements PrincipalExtractor {

    private static final String[] PRINCIPAL_KEYS = new String[]{"user", "username",
            "userid", "user_id", "login", "id", "name", "nickname"};

    private PrincipalSourceEnum from = PrincipalSourceEnum.DEFAULT_OAUTH2_PRINCIPAL;

    public FixedPrincipalExtractor() {
    }

    public FixedPrincipalExtractor(PrincipalSourceEnum from) {
        this.from = from;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        for (String key : PRINCIPAL_KEYS) {
            if (map.containsKey(key)) {
                return new FixedPrincipal(map.get(key).toString(), from);
            }
        }
        return null;
    }
}