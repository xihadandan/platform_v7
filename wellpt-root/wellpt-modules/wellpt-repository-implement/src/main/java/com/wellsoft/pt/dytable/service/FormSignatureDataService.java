package com.wellsoft.pt.dytable.service;

import java.util.List;
import java.util.Map;

public interface FormSignatureDataService {
    public List<Map<String, Object>> query(String sql) throws Exception;
}
