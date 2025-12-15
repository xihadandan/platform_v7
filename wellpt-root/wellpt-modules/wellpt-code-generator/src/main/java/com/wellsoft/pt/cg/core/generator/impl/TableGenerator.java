package com.wellsoft.pt.cg.core.generator.impl;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.Model;
import com.wellsoft.pt.cg.core.generator.model.*;
import com.wellsoft.pt.cg.core.source.TableSource;

import java.util.LinkedList;
import java.util.List;

/**
 * 根据数据库表生成
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	lmw		2015-7-8		Create
 * </pre>
 * @date 2015-6-18
 */
public class TableGenerator extends AbstractGenerator {
    public static final int MODEL_ENTITY = Type.OUTPUTTYPE_ENTITY;// 生成 hibernate实体类
    public static final int MODEL_BASIC_SERVICE = Type.OUTPUTTYPE_BASIC_SERVICE;// 生成 基本业务服务类
    public static final int MODEL_VALUE_OBJECT = Type.OUTPUTTYPE_VALUE_OBJECT;// 生成 业务数据值对象类
    public static final int MODEL_FACADE_SERVICE = Type.OUTPUTTYPE_FACADE_SERVICE;// 生成 门面业务服务类
    public static final int MODEL_BAM = Type.OUTPUTTYPE_BAM;// 生成 后台管理功能文件
    public static final int MODEL_FRONT_PAGE_VIEW_MAINTAIN = Type.OUTPUTTYPE_FRONT_PAGE_VIEW_MAINTAIN;// 前台首页视图单表维护
    public static final int MODEL_NAVIGATION = Type.OUTPUTTYPE_NAVIGATION;// 导航
    public static final int MODEL_RESOURCE = Type.OUTPUTTYPE_RESOURCE;// 生成 门面业务服务类

    private List<Model> models = new LinkedList<Model>();
    private Context context = null;

    protected void checkContext(Context context) {
        if (context == null) {
            throw new RuntimeException("context is null");
        }
        if (context.getSource() == null) {
            throw new RuntimeException("source is null");
        }
        if (!(context.getSource() instanceof TableSource)) {
            throw new RuntimeException("source is not instanceof TableSource");
        }
    }

    @Override
    public void setModel(int model) {
        models = new LinkedList<Model>();
        if ((model & MODEL_ENTITY) == MODEL_ENTITY) {
            models.add(new EntityByTableModel());
        }
        if ((model & MODEL_BASIC_SERVICE) == MODEL_BASIC_SERVICE) {
            models.add(new BaseServiceByTableModel());
        }
        if ((model & MODEL_VALUE_OBJECT) == MODEL_VALUE_OBJECT) {
            models.add(new ValueObjByTableModel());
        }
        if ((model & MODEL_FACADE_SERVICE) == MODEL_FACADE_SERVICE) {
            models.add(new FacadeServiceByTableModel());
        }
        if ((model & MODEL_BAM) == MODEL_BAM) {
            models.add(new BAMByTableModel());
        }
        if ((model & MODEL_FRONT_PAGE_VIEW_MAINTAIN) == MODEL_FRONT_PAGE_VIEW_MAINTAIN) {
            models.add(new FrontPageViewMaintainByTableModel());
        }
        if ((model & MODEL_NAVIGATION) == MODEL_NAVIGATION) {
            models.add(new NavigationByTableModel());
        }
        if ((model & MODEL_RESOURCE) == MODEL_RESOURCE) {
            models.add(new ResourceByTableModel());
        }
    }

    @Override
    public void init(Context context) {
        this.context = context;
        setModel(context.getModel());
    }

    @Override
    public void start() {
        for (Model model : models) {
            model.work(this.context);
        }
    }
}
