package com.wellsoft.pt.cg.core.generator.impl;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.Model;
import com.wellsoft.pt.cg.core.generator.model.*;
import com.wellsoft.pt.cg.core.source.TableSource;

import java.util.LinkedList;
import java.util.List;

/**
 * 根据实体类生成
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	lmw		2015-7-8		Create
 * </pre>
 * @date 2015-7-8
 */
public class EntityGenerator extends AbstractGenerator {
    public static final int MODEL_DATABASE_TABLE = Type.OUTPUTTYPE_DATABASE_TABLE;// 生成数据库表
    public static final int MODEL_BASIC_SERVICE = Type.OUTPUTTYPE_BASIC_SERVICE;// 生成 基本业务服务类
    public static final int MODEL_VALUE_OBJECT = Type.OUTPUTTYPE_VALUE_OBJECT;// 生成 业务数据值对象类
    public static final int MODEL_FACADE_SERVICE = Type.OUTPUTTYPE_FACADE_SERVICE;// 生成 门面业务服务类
    public static final int MODEL_BAM = Type.OUTPUTTYPE_BAM;// 生成 后台管理功能文件

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
        if ((model & MODEL_DATABASE_TABLE) == MODEL_DATABASE_TABLE) {
            models.add(new DatabaseTableByEntityModel());
        }
        if ((model & MODEL_BASIC_SERVICE) == MODEL_BASIC_SERVICE) {
            models.add(new BaseServiceByEntityModel());
        }
        if ((model & MODEL_VALUE_OBJECT) == MODEL_VALUE_OBJECT) {
            models.add(new ValueObjByEntityModel());
        }
        if ((model & MODEL_FACADE_SERVICE) == MODEL_FACADE_SERVICE) {
            models.add(new FacadeServiceByEntityModel());
        }
        if ((model & MODEL_BAM) == MODEL_BAM) {
            models.add(new BAMByEntityModel());
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
