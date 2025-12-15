package com.wellsoft.pt.dyform.facade.dto;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Description: 单据定义门面
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月28日.1	hongjz		2018年3月28日		Create
 * </pre>
 * @date 2018年3月28日
 */

public interface DyFormFormDefinition extends Serializable {

    /**
     * 获取UUID
     *
     * @return
     */
    public abstract String getUuid();

    /**
     * 获取表名
     *
     * @return
     */
    public abstract String getTableName();

    /**
     * 获取ID
     *
     * @return
     */
    public abstract String getId();

    /**
     * 获取定义名
     *
     * @return
     */
    public abstract String getName();

    /**
     * 设置表单名称
     *
     * @param name
     */
    public abstract void setName(String name);

    /**
     * 获取编号
     *
     * @return
     */
    public abstract String getCode();

    /**
     * 获取备注
     *
     * @return
     */
    public abstract String getRemark();

    /**
     * 获取版本号
     *
     * @return
     */
    public abstract String getVersion();

    /**
     * 获取是否启用签名
     *
     * @return
     */
    public abstract String getEnableSignature();

    /**
     * 获取定义JSON
     *
     * @return
     */
    public abstract String getDefinitionJson();

    /**
     * 获取关系表
     *
     * @return
     */
    public abstract String getRelationTbl();

    /**
     * 获取是否启用
     *
     * @return
     */
    public abstract String getIsUp();

    /**
     * 获取JS二开模块
     *
     * @return
     */
    public abstract String getCustomJsModule();

    /**
     * 获取是否最小版本
     *
     * @return
     */
    public abstract boolean isMinVersion();

    /**
     * 获取字段定义
     *
     * @return
     */
    public abstract List<DyformFieldDefinition> doGetFieldDefintions();

    /**
     * 获取从表定义
     *
     * @return
     */
    public abstract List<DyformSubformFormDefinition> doGetSubformDefinitions();

    /**
     * 获取系统单位ID
     *
     * @return
     */
    public abstract String getSystemUnitId();

    /**
     * 获取系统ID
     *
     * @return
     */
    String getSystem();

    /**
     * 获取表单类型
     *
     * @return
     */
    public abstract String getFormType();

    /**
     * 获取引用的设计器表单UUID
     *
     * @return
     */
    public abstract String getRefDesignerFormUuid();

    /**
     * 获取版本号格式
     *
     * @return
     */
    public DecimalFormat getVersionFormat();

    /**
     * 版本号设置为最小版本
     */
    public void doBindVersionAsMinVersion();

    /**
     * uuid转为json对象
     */
    public void doBindUuid2Json();

    /**
     * 获取存储单据UUID
     *
     * @return
     */
    public String getpFormUuid();

    /**
     * 获取存储单据数据库表名
     *
     * @return
     */
    public String doGetTblNameOfpForm();

    /**
     * 是否有布局控件
     *
     * @return
     */
    public boolean doHasLayout();

    /**
     * 获取字段定义
     *
     * @return
     */
    public abstract List<DyformFieldDefinition> doGetTableFieldDefintions();

    /**
     * 获取子表单字段定义
     *
     * @return
     */
    public abstract List<DyformFieldDefinition> doGetTemplateDefintions();

    /**
     * 获取子表单UUID列表
     *
     * @return
     */
    public abstract List<String> doGetTemplateUuids();

    /**
     * 获取存储单据关系表
     *
     * @return
     */
    public abstract String doGetRelationTblNameOfpForm();

    /**
     * 获取存储单据UUID
     *
     * @return
     */
    public abstract String doGetPFormUuid();

    /**
     * 获取默认手机单据UUID
     *
     * @return
     */
    public abstract String doGetDefaultMFormUuid();

    /**
     * 获取默认显示单据UUID
     *
     * @return
     */
    public abstract String doGetDefaultVFormUuid();

    /**
     * 获取标题表达式
     *
     * @return
     */
    public abstract String doGetTitleExpression();

    /**
     * 获取占位符控件ID列表
     *
     * @return
     */
    public abstract List<String> doGetPlaceholderCtrIds();

    /**
     * 获取FileLibrary控件ID列表
     *
     * @return
     */
    public abstract List<String> doGetFileLibraryIds();

    /**
     * 获取TableView控件ID列表
     *
     * @return
     */
    public abstract List<String> doGetTableViewIds();

}
