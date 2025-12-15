package com.wellsoft.pt.cache;

public interface Cache extends org.springframework.cache.Cache {

    public abstract Object getValue(Object key);

    public abstract Boolean exists(Object key);

}