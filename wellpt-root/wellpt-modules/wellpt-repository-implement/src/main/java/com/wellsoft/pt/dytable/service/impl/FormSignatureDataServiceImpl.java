package com.wellsoft.pt.dytable.service.impl;

import com.wellsoft.pt.dytable.service.FormSignatureDataService;
import com.wellsoft.pt.repository.dao.DbTableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FormSignatureDataServiceImpl implements FormSignatureDataService {

    @Autowired
    DbTableDao dbTableDao;

    @Override
    public List<Map<String, Object>> query(String sql) throws Exception {

        return dbTableDao.query(sql);
    }

}
