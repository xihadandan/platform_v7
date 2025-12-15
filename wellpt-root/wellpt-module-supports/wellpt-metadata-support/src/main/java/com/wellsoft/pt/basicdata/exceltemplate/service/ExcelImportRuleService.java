package com.wellsoft.pt.basicdata.exceltemplate.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.pt.basicdata.exceltemplate.bean.ExcelImportRuleBean;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;

import java.io.InputStream;
import java.util.List;

/**
 * Description: Excel导入规则服务层接口
 *
 * @author zhouyq
 * @date 2013-4-22
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-24.1	zhouyq		2013-4-22		Create
 * </pre>
 */

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-25.1	Administrator		2013-4-25		Create
 * </pre>
 * @date 2013-4-25
 */
public interface ExcelImportRuleService {
    public static final String ACL_SID = "ROLE_EXCEL_IMPORT_RULE";

    /**
     * 获得所有Excel导入规则
     *
     * @return
     */
    public List<ExcelImportRule> findAll();

    /**
     * 通过uuid获取Excel导入规则
     *
     * @param id
     * @return
     */
    public ExcelImportRule get(String uuid);

    /**
     * 通过uuid获取Excel导入规则VO对象
     *
     * @param id
     * @return
     */
    public ExcelImportRuleBean getBeanByUuid(String uuid);

    /**
     * 保存Excel导入规则
     *
     * @param uuid
     * @return
     */
    public void saveBean(ExcelImportRuleBean bean);

    /**
     * 通过UUID删除Excel导入规则
     *
     * @param uuid
     * @return
     */
    public void remove(String uuid);

    public void removeAll(String[] uuids);

    /**
     * Excel导入规则列表查询
     *
     * @param queryInfo
     * @return
     */
    public JqGridQueryData query(JqGridQueryInfo queryInfo);

    /**
     * 列对应列表查询
     *
     * @param queryInfo
     * @return
     */
    public JqGridQueryData queryExcelList(JqGridQueryInfo queryInfo);

    public List getListFromExcel(String code, InputStream is) throws Exception;

    public ExcelImportRule getNameByFileUuid(String fileUuid);

    public List<ExcelImportRule> getExcelImportRule();

    public ExcelImportRule getExcelImportRule(String uuid);

    public ExcelImportRule getExcelImportRuleObj(String code);

    /**
     * 通过excel页签名取得导入的List
     *
     * @param code
     * @param is
     * @param sheetname
     * @return
     * @throws Exception
     */
    public List getListFromExcel(String code, InputStream is, String sheetName) throws Exception;
}
