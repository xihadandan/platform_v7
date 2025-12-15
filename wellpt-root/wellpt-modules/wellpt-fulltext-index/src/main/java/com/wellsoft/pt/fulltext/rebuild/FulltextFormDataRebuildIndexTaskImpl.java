/*
 * @(#)6/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.rebuild;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.fulltext.entity.FulltextModelEntity;
import com.wellsoft.pt.fulltext.service.FulltextFormDataDocumentIndexService;
import com.wellsoft.pt.fulltext.service.FulltextModelService;
import com.wellsoft.pt.fulltext.support.FulltextRebuildIndexTask;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/20/25.1	    zhulh		6/20/25		    Create
 * </pre>
 * @date 6/20/25
 */
@Service
public class FulltextFormDataRebuildIndexTaskImpl implements FulltextRebuildIndexTask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FulltextFormDataDocumentIndexService fulltextFormDataDocumentIndexService;

    @Autowired
    private FulltextModelService fulltextModelService;

    @Override
    public long indexCount(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        String system = fulltextSetting.getSystem();
        return fulltextFormDataDocumentIndexService.countByFieldEq("system", system);
    }

    @Override
    public void rebuildIndex(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        String system = fulltextSetting.getSystem();
        Map<String, Long> countMap = fulltextFormDataDocumentIndexService.distinctByFieldAndSystem("formUuid", system);
        fulltextFormDataDocumentIndexService.deleteIndexByFieldEq("system", system);

        List<FulltextModelEntity> modelEntities = fulltextModelService.listBySystem(system);
        Set<String> formUuids = Sets.newHashSet(countMap.keySet());
        formUuids.addAll(modelEntities.stream().map(FulltextModelEntity::getViewDataFormUuid).collect(Collectors.toList()));
        Map<String, UserDetails> userDetailsMap = Maps.newHashMap();
        Set<String> rebuildTables = Sets.newHashSet();
        for (String rebuildFromUuid : formUuids) {
            if (StringUtils.isBlank(rebuildFromUuid)) {
                continue;
            }
            DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(rebuildFromUuid);
            String tableName = dyFormFormDefinition.getTableName();
            if (rebuildTables.contains(tableName)) {
                continue;
            }
            Date deadline = getEarliestTime(tableName, system);
            if (deadline == null) {
                continue;
            }
            rebuildTables.add(tableName);

            Calendar fromCalendar = Calendar.getInstance();
            Calendar toCalendar = Calendar.getInstance();
            fromCalendar.add(Calendar.DAY_OF_YEAR, -10);
            Date fromTime = fromCalendar.getTime();
            Date toTime = toCalendar.getTime();
            int loopCount = 0;
            while (fromTime.after(deadline) || loopCount <= 0) {
                loopCount++;
                List<QueryItem> queryItems = listFormDataItem(tableName, system, fromTime, toTime);
                for (QueryItem queryItem : queryItems) {
                    String formUuid = queryItem.get("formUuid").toString();
                    String dataUuid = queryItem.get("uuid").toString();
                    rebuildIndex(formUuid, dataUuid, fulltextSetting, userDetailsMap);
                }
                fromCalendar.add(Calendar.DAY_OF_YEAR, -10);
                toCalendar.setTime(fromTime);
                fromTime = fromCalendar.getTime();
                toTime = toCalendar.getTime();
            }
        }
    }

    private List<QueryItem> listFormDataItem(String tableName, String system, Date fromTime, Date toTime) {
        String[] projection = new String[]{"uuid", "form_uuid"};
        String selection = String.format("system = :system and create_time between :fromTime and :toTime", system);
        Map<String, Object> selectionArgs = Maps.newHashMap();
        selectionArgs.put("system", system);
        selectionArgs.put("fromTime", fromTime);
        selectionArgs.put("toTime", toTime);
        List<QueryItem> queryItems = dyFormFacade.query(tableName, false, projection,
                selection, selectionArgs, null, null,
                "create_time desc", 0, Integer.MAX_VALUE);
        return queryItems;
    }

    private Date getEarliestTime(String tableName, String system) {
        Map<String, Object> selectionArgs = Maps.newHashMap();
        selectionArgs.put("system", system);
        List<QueryItem> queryItems = dyFormFacade.query(tableName, false, new String[]{"create_time"},
                "system = :system", selectionArgs, null, null,
                "create_time desc", 0, 1);
        return CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getDate("createTime") : null;
    }

    @Override
    public void buildIncrementIndex(Date fromTime, FulltextSetting fulltextSetting) {
        String system = fulltextSetting.getSystem();
        Map<String, Long> countMap = fulltextFormDataDocumentIndexService.distinctByFieldAndSystem("formUuid", system);
        Map<String, UserDetails> userDetailsMap = Maps.newHashMap();
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            String formUuid = entry.getKey();
            String[] projection = new String[]{"uuid"};
            String selection = String.format("system = '%s' and modify_time >= to_date('%s', 'YYYY-MM-DD HH24:MI:SS')", system, DateUtils.formatDateTime(fromTime));
            if (DatabaseType.MySQL5.getName().equalsIgnoreCase(Config.getValue("database.type"))) {
                selection = String.format("system = '%s' and modify_time >= STR_TO_DATE('%s', '%Y-%m-%d %H:%i:%s')", system, DateUtils.formatDateTime(fromTime));
            }
            List<QueryItem> queryItems = dyFormFacade.query(formUuid, projection, selection, null,
                    null, null, null, 0, Integer.MAX_VALUE);
            for (QueryItem queryItem : queryItems) {
                String uuid = queryItem.get("uuid").toString();
                rebuildIndex(formUuid, uuid, fulltextSetting, userDetailsMap);
            }
        }
    }

    private void rebuildIndex(String formUuid, String dataUuid, FulltextSetting fulltextSetting, Map<String, UserDetails> userDetailsMap) {
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        Map<String, Object> formData = dyFormData.getFormDataOfMainform();
        String modifier = Objects.toString(formData.get(EnumSystemField.modifier.getName()), fulltextSetting.getCreator());
        try {
            RequestSystemContextPathResolver.setSystem(fulltextSetting.getSystem());
            UserDetails userDetails = userDetailsMap.get(modifier);
            if (userDetails == null) {
                IgnoreLoginUtils.login(fulltextSetting.getTenant(), modifier);
                userDetails = IgnoreLoginUtils.getUserDetails();
                userDetailsMap.put(modifier, userDetails);
            } else {
                IgnoreLoginUtils.login(userDetails);
            }
            fulltextFormDataDocumentIndexService.index(formUuid, dataUuid, dyFormData, fulltextSetting);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    @Override
    public int getOrder() {
        return 30;
    }
}
