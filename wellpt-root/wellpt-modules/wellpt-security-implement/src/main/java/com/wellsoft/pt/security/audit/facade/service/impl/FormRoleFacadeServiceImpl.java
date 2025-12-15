/*
 * @(#)2020年10月21日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service.impl;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.security.audit.facade.service.FormRoleFacadeService;
import com.wellsoft.pt.security.audit.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 业务角色优化旧数据升级使用
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年10月21日.1	shenhb		2020年10月21日		Create
 * </pre>
 * @date 2020年10月21日
 */
@Service
public class FormRoleFacadeServiceImpl implements FormRoleFacadeService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RoleService roleService;
    @Resource
    private DyFormFacade dyFormFacade;
    @Resource
    private MultiOrgUserService multiOrgUserService;
    @Resource
    private MultiOrgService multiOrgService;
    @Resource
    private MultiOrgGroupFacade multiOrgGroupFacade;

    @Override
    @Transactional
    public void handleDyformRoleUpdate() {
        // 获取DYFORM_CONTROL_ROLE表数据
        List<QueryItem> queryItems = this.roleService.getDao().listQueryItemBySQL("select * from DYFORM_CONTROL_ROLE_TEMP", null, null);
        for (QueryItem queryItem : queryItems) {

            String roleUuid = queryItem.getString("roleUuid");
            String controlName = queryItem.getString("controlName");
            String dyformId = queryItem.getString("dyformId");

            if (StringUtils.isNotBlank(dyformId)) {
                // 获取表数据
                List<QueryItem> allData = dyFormFacade.queryAllDyformDataByFormId(dyformId);
                for (QueryItem allDatum : allData) {
                    String fileName = controlName.toLowerCase();
                    String value = allDatum.getString(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fileName));
                    if (StringUtils.isNotBlank(value)) {
                        try {
                            Set<String> dataIds = Sets.newHashSet();
                            if (value.startsWith("{") && value.endsWith("}")) {
                                HashMap<String, String> map = JsonUtils.gson2Object(value, HashMap.class);
                                dataIds.addAll(map.keySet());
                            } else {
                                dataIds.addAll(Arrays.asList(value.split(",|;")));
                            }
                            if (!dataIds.isEmpty()) {
                                for (String dataId : dataIds) {
                                    if (dataId.startsWith(IdPrefix.USER.getValue())) {
                                        this.multiOrgUserService.addRoleListOfUser(dataId, roleUuid);
                                    } else if (dataId.startsWith(IdPrefix.GROUP.getValue())) {
                                        this.multiOrgGroupFacade.addRoleListOfGroup(dataId, roleUuid);
                                    } else if (MultiOrgApiFacade.isMultiOrgEleNode(dataId)) {
                                        this.multiOrgService.addRoleListOfElement(dataId, roleUuid);
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            logger.error("业务角色优化旧数据升级错误，formData[" + value + "],fileName[" + fileName
                                    + "],dyformId[" + dyformId + "]", ex);
                        }
                    }
                }
            }

        }

    }
}
