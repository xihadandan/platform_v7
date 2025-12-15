package com.wellsoft.pt.cg.core;

/**
 * 相关类型定义
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
public final class Type {

    // 生成文件类型
    public static final int OUTPUTTYPE_ENTITY = 0x0001;// hibernate实体类
    public static final int OUTPUTTYPE_DATABASE_TABLE = 0x0002;// 数据库表
    public static final int OUTPUTTYPE_BASIC_SERVICE = 0x0004;// 基本业务服务类
    public static final int OUTPUTTYPE_VALUE_OBJECT = 0x0008;// 业务数据值对象类
    public static final int OUTPUTTYPE_FACADE_SERVICE = 0x0010;// 门面业务服务类
    public static final int OUTPUTTYPE_BAM = 0x0020;// 后台管理功能文件(jsp+js)
    public static final int OUTPUTTYPE_FRONT_PAGE_VIEW_MAINTAIN = 0x0040;// 前台首页视图单表维护
    public static final int OUTPUTTYPE_NAVIGATION = 0x0080;// 导航
    public static final int OUTPUTTYPE_RESOURCE = 0x0100;// 资源
    public static final int OUTPUTTYPE_DIRECTION_LISTENER = 0x0200;// 流向监听器
    public static final int OUTPUTTYPE_TASK_LISTENER = 0x0400;// 环节监听器
    public static final int OUTPUTTYPE_FLOW_LISTENER = 0x0800;// 流程监听器
    public static final int OUTPUTTYPE_DEV_JS = 0x1000;// 流程二开
    public static final int OUTPUTTYPE_DYFORM_MAINTAIN = 0x2000;// 表单维护

    // 生成方式
    public static final int GENTYPE_TABLE = 0x0001;
    public static final int GENTYPE_ENTITY = 0x0002;
    public static final int GENTYPE_FLOW_DEFINITION = 0x0004;
    public static final int GENTYPE_DYFORM_DEFINITION = 0x0008;


    public static void main(String[] arrs) {
        System.out.println(OUTPUTTYPE_RESOURCE);
    }

}
