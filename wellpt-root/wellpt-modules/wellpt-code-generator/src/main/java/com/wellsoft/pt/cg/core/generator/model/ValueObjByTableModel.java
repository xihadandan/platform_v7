package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.common.ParseUtil;
import com.wellsoft.pt.cg.core.source.TableSource;
import freemarker.template.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 业务数据值对象类
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-7.1	lmw		2015-7-7		Create
 * </pre>
 * @date 2015-6-18
 */
public class ValueObjByTableModel extends ValueObjModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_VALUE_OBJECT;

    @Override
    public int getCode() {
        return MODELCODE;
    }


    @Override
    protected void freemarkerFormateProcess(Context context, Configuration config) {
        TableSource source = (TableSource) context.getSource();
        Configuration cfg = initConfig(context);
        for (Map.Entry<String, List<TableSource.Column>> entry : source.getTables().entrySet()) {
            // 构建数据
            Map<String, Object> root = structureData(context, entry);

            // java文件生成
            genJava(root, context, cfg);
        }
    }

    /**
     * 构建数据
     *
     * @param context
     * @return
     */
    protected Map<String, Object> structureData(Context context,
                                                Map.Entry<String, List<TableSource.Column>> entry) {
        Map<String, Object> root = super.structureData(context, null);

        String className = ParseUtil.tableNameToClass(entry.getKey());
        root.put("className", className + "Bean");
        root.put("entity", className);
        return root;
    }

}