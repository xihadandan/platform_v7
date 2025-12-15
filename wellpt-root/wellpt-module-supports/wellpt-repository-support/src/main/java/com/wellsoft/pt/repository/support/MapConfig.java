package com.wellsoft.pt.repository.support;

import net.sf.json.util.PropertyFilter;

public class MapConfig {
    public PropertyFilter fliter = new PropertyFilter() {
        @Override
        public boolean apply(Object arg0, String arg1, Object arg2) {
            return false;
        }
    };

    public void setMapPropertyFilter(PropertyFilter fliter) {
        this.fliter = fliter;
    }

}
