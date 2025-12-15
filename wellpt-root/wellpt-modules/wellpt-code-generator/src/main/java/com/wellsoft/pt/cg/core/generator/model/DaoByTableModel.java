package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.generator.common.ParseUtil;
import com.wellsoft.pt.cg.core.source.TableSource;
import freemarker.template.Configuration;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/1
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/1    chenq		2018/8/1		Create
 * </pre>
 */
public class DaoByTableModel extends EntityByTableModel {

    @Override
    public int getCode() {
        return 0;
    }


    @Override
    public void freemarkerFormateProcess(Context context, Configuration cfg) {
        try {

            Set<String> tableSet = ((TableSource) context.getSource()).getTableSet();
            for (String table : tableSet) {
                Map<String, Object> other = new HashMap<String, Object>();
                String className = ParseUtil.tableNameToClass(table);
                className = className.indexOf("Uf") == 0 ? className.replaceFirst("Uf",
                        "") : className;
                other.put("entity", className);
                other.put("tableName", table);
                Map<String, Object> root = structureData(context, other);

                writeTemplate(context, root, cfg, "base.dao.ftl",
                        className + "Dao.java",
                        "/dao");
                writeTemplate(context, root, cfg, "base.dao.impl.ftl",
                        className + "DaoImpl.java",
                        "/dao/impl");

            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }
}
