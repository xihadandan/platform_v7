package com.wellsoft.pt.dms.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentRequestParam;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;
import com.wellsoft.pt.dms.entity.DmsDataLabelEntity;
import com.wellsoft.pt.dms.service.DmsDataLabelService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: 抽象类的平台级数据标签树节点数据源
 *
 * @author chenq
 * @date 2018/6/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/13    chenq		2018/6/13		Create
 * </pre>
 */
public abstract class AbstractDataLabelTreeDataProvider implements TreeComponentDataProvider {

    /**
     * 由各个业务模块定义属于自己的模块ID，不能随便定义，必须与表格组件内启用标签的归属模块ID一致
     *
     * @return
     */
    abstract String getModuleId();


    /**
     * 定义父节点名称
     *
     * @return
     */
    abstract String getRootTreeNodeName();


    @Override
    public List<TreeType> getNodeTypes() {
        return Lists.newArrayList(TreeType.createTreeType("TAG", "标签"));
    }


    @Override
    public String getFilterHint() {
        return null;
    }


    @Override
    public List<TreeNode> loadTreeData(TreeComponentRequestParam param) {
        TreeNode treeNode = new TreeNode("0", getRootTreeNodeName(), null);
        treeNode.setIsParent(true);
        Map<String, Object> rootData = Maps.newHashMap();
        //可携带模块id参数
        String moduleId = param.getIntfParams().containsKey("moduleId") ? param.getIntfParams().get(
                "moduleId").toString() : getModuleId();

        List<DmsDataLabelEntity> dmsDataLabelEntities = ApplicationContextHolder.getBean(
                DmsDataLabelService.class).queryByUserIdAndModuleId(
                SpringSecurityUtils.getCurrentUserId(), moduleId);

        List<TreeNode> children = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dmsDataLabelEntities)) {
            //int seq = 1;
            for (DmsDataLabelEntity entity : dmsDataLabelEntities) {
                TreeNode child = new TreeNode("" + entity.getUuid(), entity.getLabelName(),
                        null);
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put("uuid", entity.getUuid());
                dataMap.put("labelName", entity.getLabelName());
                dataMap.put("labelColor", entity.getLabelColor());
                dataMap.put("icon", "glyphicon glyphicon-stop");
                dataMap.put("iconStyle", "color:" + entity.getLabelColor() + ";");
                child.setData(dataMap);
                child.setIsParent(false);
                children.add(child);
                child.setChecked(true);
            }
        } else {
            rootData.put("hidden", true);// 如果没有自定义标签，则不展示
        }
        rootData.put("unclickable", true);
        treeNode.setData(rootData);
        treeNode.setChildren(children);
        return Lists.newArrayList(treeNode);
    }

}
