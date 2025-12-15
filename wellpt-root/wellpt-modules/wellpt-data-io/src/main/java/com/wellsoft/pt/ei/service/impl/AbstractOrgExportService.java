package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.org.OrgNodeInfoData;
import com.wellsoft.pt.ei.service.ExpImpService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/29.1	liuyz		2021/9/29		Create
 * </pre>
 * @date 2021/9/29
 */
public abstract class AbstractOrgExportService<D, T> implements ExpImpService<D, T> {
    @Override
    public String filePath() {
        return DataExportConstants.DATA_TYPE_ORG;
    }

    /**
     * 判断节点是否存在
     *
     * @param list        组织树节点列表，按照层级从浅到深
     * @param type        节点类型
     * @param name        节点名称
     * @param eleNamePath 节点中文路径
     * @return
     */
    public OrgNodeInfoData existSameNode(List<OrgNodeInfoData> list, String type, String name, String eleNamePath) {
        for (OrgNodeInfoData data : list) {
            if (data.getType().equals(type) && data.getName().equals(name) && data.getEleNamePath().equals(eleNamePath)) {
                return data;
            }
        }
        return null;
    }
}
