/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.acceptor;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.basicdata.iexport.visitor.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public abstract class IexportData implements Acceptor {
    protected static final Logger logger = LoggerFactory.getLogger(IexportData.class);

    private IexportData parent;

    private List<IexportData> children = new ArrayList<IexportData>(0);

    // true依赖优先导入，false根结点优化导入
    private boolean priority = true;

    public abstract String getUuid();

    // 数据ID，唯一标识
    public final String getId() {
        return getUuid() + Separator.UNDERLINE.getValue() + getType();
    }

    public abstract String getName();

    public abstract String getType();

    public abstract Integer getRecVer();

    public Map<String, Object> getRowData() {
        Map<String, Object> values = IexportDataResultSetUtils.getIexportRowData(getUuid(), getType());
        return values;
    }

    public abstract InputStream getInputStream() throws IOException;

    public IexportDataRecord getRecord() {
        return IexportDataRecordUtils.getRecord(this);
    }

    public abstract List<IexportData> getDependencies();

    public IexportMetaData getMetaData() {
        return IexportDataProviderFactory.getDataProvider(getType()).getMetaData();
    }

    /**
     * @return the parent
     */
    public IexportData getParent() {
        return parent;
    }

    public List<IexportData> getChildren() {
        return children;
    }

    public void setChildren(List<IexportData> children) {
        this.children = children;
    }

    /**
     * @return the priority
     */
    public boolean isPriority() {
        return priority;
    }

    /**
     * @param priority 要设置的priority
     */
    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Acceptor#accept(com.wellsoft.pt.basicdata.iexport.visitor.Visitor)
     */
    @Override
    public void accept(Visitor visitor) {
        accept(visitor, null);
    }

    public void accept(Visitor visitor, Set<String> importIdSet) {
        visitor.visit(this);
        if (children != null) {
            for (IexportData iexportData : children) {
                if (iexportData == null || IexportType.ErrorData.equals(iexportData.getType())) {
                    continue;
                }
                if (importIdSet == null || importIdSet.contains(iexportData.getUuid())) {
                    iexportData.parent = this;
                    iexportData.accept(visitor, importIdSet);
                }
            }
        }
    }

}
