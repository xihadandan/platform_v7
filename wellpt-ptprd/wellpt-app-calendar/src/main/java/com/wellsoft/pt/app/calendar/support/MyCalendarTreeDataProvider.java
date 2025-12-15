/*
 * @(#)Apr 13, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEntity;
import com.wellsoft.pt.app.calendar.facade.service.CalendarFacade;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentRequestParam;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Apr 13, 2018.1	zhulh		Apr 13, 2018		Create
 * </pre>
 * @date Apr 13, 2018
 */
@Component
public class MyCalendarTreeDataProvider implements TreeComponentDataProvider {

    @Autowired
    private CalendarFacade calendarFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getName()
     */
    @Override
    public String getName() {
        return "日程管理_我的日历本";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getNodeTypes()
     */
    @Override
    public List<TreeType> getNodeTypes() {
        return Lists.newArrayList(TreeType.createTreeType("Calendar", "日历本"));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#loadTreeData(java.util.Map)
     */
    @Override
    public List<TreeNode> loadTreeData(TreeComponentRequestParam arg0) {
        List<MyCalendarEntity> list = calendarFacade.queryAppMyCalendarListAndCheckDefaultCalendar();
        List<TreeNode> children = Lists.newArrayList();
        TreeNode myAllNode = new TreeNode("", "全部", null);
        myAllNode.setType("myAll");
        Map<String, Object> d = Maps.newHashMap();
        d.put("icon", "hide");
        d.put("type", "myAll");
        d.put("uuid", System.currentTimeMillis());
        myAllNode.setData(d);
        children.add(myAllNode);
        if (CollectionUtils.isNotEmpty(list)) {
            for (MyCalendarEntity entity : list) {
                TreeNode child = new TreeNode(entity.getUuid(), entity.getCalendarName(), null);
                child.setType("myCalendar");
                Map<String, Object> data = Maps.newHashMap();
                data.put("icon", "iconfont icon-ptkj-bianji pull-right");//glyphicon glyphicon-pencil pull-right
                data.put("calendar", entity);
                data.put("type", "myCalendar");
                child.setData(data);
                children.add(child);
            }
        }
        return children;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getFilterHint()
     */
    @Override
    public String getFilterHint() {
        // TODO Auto-generated method stub
        return null;
    }

}
