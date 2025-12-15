package com.wellsoft.pt.app.support;

import java.io.Serializable;

public class Authenticatable implements Serializable {

    private boolean authenticated = true;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
