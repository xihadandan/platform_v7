/*
 * @(#)Jan 8, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Jan 8, 2018.1	zhulh		Jan 8, 2018		Create
 * </pre>
 * @date Jan 8, 2018
 */
@Component
public class FileActionManagerImpl implements FileActionManager {

    @Autowired
    private List<FileAction> fileActions;

    private Map<String, FileAction> fileActionMap = new HashMap<String, FileAction>();

    @PostConstruct
    private void init() {
        for (FileAction fileAction : fileActions) {
            fileActionMap.put(fileAction.getId(), fileAction);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.action.FileActionManager#getFileAction(java.lang.String)
     */
    @Override
    public FileAction getFileAction(String id) {
        return fileActionMap.get(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.action.FileActionManager#getAllActionIds()
     */
    @Override
    public List<String> getAllActionIds() {
        List<String> actionIds = new ArrayList<String>();
        for (FileAction fileAction : fileActions) {
            actionIds.add(fileAction.getId());
        }
        return actionIds;
    }

}
