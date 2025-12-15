package com.wellsoft.context.util.enumtool;

import com.wellsoft.context.annotation.EnumClass;
import com.wellsoft.context.component.tree.TreeNode;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 枚举服务类</br>
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月25日.1	Asus		2015年12月25日		Create
 * </pre>
 * @date 2015年12月25日
 */
@Service
@Transactional
public class EnumManageService {
    private Logger logger = LoggerFactory.getLogger(EnumManageService.class);

    /**
     * 获取所有枚举
     *
     * @param o
     * @return 所有枚举
     */
    @SuppressWarnings("rawtypes")
    public List getAllEnums(Object o) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        Map<String, JSONObject> map = EnumManage.getEnumsMap();
        TreeNode treeNode = new TreeNode();
        for (String enumClassName : map.keySet()) {
            JSONObject enumJson = map.get(enumClassName);
            TreeNode child = new TreeNode();
            child.setId(enumJson.getString("objectName"));
            child.setName(enumJson.getString("objectRemarkName"));
            child.setData(enumJson.getString("objectName"));
            treeNodes.add(child);
        }
        return treeNodes;
    }

    /**
     * 获取所有加了{@link EnumClass}注解的枚举转化的JSON字符串
     *
     * @return JSON字符串
     * @see EnumManage#getEnumJs()
     */
    public String getEnums() {
        try {
            return EnumManage.getInstance().getEnumJs();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "{}";
        }
    }
}
