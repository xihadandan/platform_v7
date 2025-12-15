package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.common.ParseUtil;
import com.wellsoft.pt.cg.core.source.TableSource;
import freemarker.template.Configuration;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 生成门面业务服务类
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
public class FacadeServiceByTableModel extends AbstractModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_FACADE_SERVICE;

    @Override
    public int getCode() {
        return MODELCODE;
    }

    @Override
    public void work(Context context) {
        Configuration cfg = initConfig(context);
        freemarkerFormateProcess(context, cfg);
    }

    public void freemarkerFormateProcess(Context context, Configuration cfg) {
        try {
            Set<String> tableSet = ((TableSource) context.getSource()).getTableSet();
            for (String table : tableSet) {
                Map<String, Object> other = new HashMap<String, Object>();
                String className = ParseUtil.tableNameToClass(table);
                className = className.indexOf("Uf") == 0 ? className.replaceFirst("Uf",
                        "") : className;
                other.put("entity", className);
                other.put("tableName", table.toUpperCase());
                Map<String, Object> root = structureData(context, other);
                writeTemplate(context, root, cfg, "base.facade.service.ftl",
                        className + "FacadeService.java",
                        "/facade/service");
                writeTemplate(context, root, cfg, "base.facade.service.impl.ftl",
                        className + "FacadeServiceImpl.java",
                        "/facade/service/impl");
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }


}