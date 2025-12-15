package com.wellsoft.pt.multi.org.facade.service.impl;

import com.wellsoft.pt.multi.org.facade.service.MultiOrgSystemUnitFacadeService;
import com.wellsoft.pt.multi.org.service.MultiOrgSystemUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yt
 * @title: MultiOrgSystemUnitFacadeServiceImpl
 * @date 2020/7/22 09:23
 */
@Service
public class MultiOrgSystemUnitFacadeServiceImpl implements MultiOrgSystemUnitFacadeService {

    @Autowired
    private MultiOrgSystemUnitService multiOrgSystemUnitService;


    @Override
    public long count() {
        return multiOrgSystemUnitService.count();
    }
}
