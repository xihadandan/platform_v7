/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgOptionDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgOption;
import com.wellsoft.pt.multi.org.service.MultiOrgOptionService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeAllUserProvider;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogDataProvider;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogProvider;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogType;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
@Service
public class MultiOrgOptionServiceImpl extends AbstractJpaServiceImpl<MultiOrgOption, MultiOrgOptionDao, String>
        implements MultiOrgOptionService {

    private Map<String, OrgTreeDialogType> privoders2 = Maps.newHashMap();

    @PostConstruct
    private void init() {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Map<String, OrgTreeDialogType> privoders = applicationContext.getBeansOfType(OrgTreeDialogType.class);
        for (OrgTreeDialogType privoder : privoders.values()) {
            privoders2.put(privoder.getType(), privoder);
        }
    }

    @Override
    public MultiOrgOption getById(String optId) {
        MultiOrgOption q = new MultiOrgOption();
        q.setId(optId);
        List<MultiOrgOption> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String searchValue = queryInfo.getSearchValue();
        return new Select2QueryData(this.dao.queryMultiOrgOptionsByLikeIdCodeName(searchValue),
                queryInfo.getOtherParams("idProperty", "id"), "name");
    }

    @Override
    public Select2QueryData loadSelectDataNoId(Select2QueryInfo queryInfo) {
        String searchValue = queryInfo.getSearchValue();
        Collection<MultiOrgOption> collection = this.dao.queryMultiOrgOptionsByLikeIdCodeName(searchValue);
        String excludeId = queryInfo.getOtherParams("excludeId");
        Set<String> idSet = new HashSet<>();
        if (StringUtils.isNotBlank(excludeId)) {
            for (String id : excludeId.split(",")) {
                idSet.add(id.toLowerCase());
            }
        }
        collection = collection.stream().filter(multiOrgOption -> !idSet.contains(multiOrgOption.getId().toLowerCase())).collect(Collectors.toList());
        return new Select2QueryData(collection,
                queryInfo.getOtherParams("idProperty", "id"), "name");
    }

    @Override
    public List<MultiOrgOption> getOrgOptionsByIds(String[] ids) {
        return this.dao.getOrgOptionsByIds(ids);
    }

    // 获取指定系统单位的所有的启用的组织选择项
    @Override
    public List<MultiOrgOption> queryOrgOptionListBySystemUnitId(String systemUnitId, boolean onlyShow) {
        MultiOrgOption q = new MultiOrgOption();
        q.setSystemUnitId(systemUnitId);
        q.setIsEnable(1);
        if (true == onlyShow) {
            q.setIsShow(1);
        }
        List<MultiOrgOption> list = this.dao.listByEntity(q);
        for (MultiOrgOption entity : list) {
            entity.setAttach(getOptionStyle(entity.getId()));
        }
        return list;
    }

    @Override
    public String getOptionStyle(String id) {
        OrgTreeDialogType privoder = privoders2.get(id);
        List<String> styles = Lists.newArrayList();
        if (privoder instanceof OrgTreeAllUserProvider) {
            // FIXME OrgTreeAllUserProvider接口还未做完备性测试，先只启用MyUnit，MyCompany。与前端保持
            if (StringUtils.equals(privoder.getType(), "MyUnit") || StringUtils.equals(privoder.getType(), "MyCompany")) {
                styles.add("list");
            }
        }
        if (privoder instanceof OrgTreeDialogProvider || privoder instanceof OrgTreeDialogDataProvider) {
            styles.add("tree");
        }
        return StringUtils.join(styles, Separator.SEMICOLON.getValue());
    }

}
