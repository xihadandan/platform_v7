package com.wellsoft.pt.org.support.renderer;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractSelectiveDatasDataStoreRenderer;
import com.wellsoft.pt.org.entity.OrgElementPathEntity;
import com.wellsoft.pt.org.service.OrgElementPathService;
import com.wellsoft.pt.org.service.OrgUserService;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年10月26日   chenq	 Create
 * </pre>
 */
@Component
public class OrgElementDataStoreRenderer extends AbstractSelectiveDatasDataStoreRenderer {
    private static final String SELECTIVE_ALL_ELE_KEY = "ORG_ALL_ELE";
    private static final String SHOW_TYPE_KEY = "showType";
    @Autowired
    OrgElementPathService orgElementPathService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    OrgUserService orgUserService;

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        Boolean displayPath = !param.containsKey(SHOW_TYPE_KEY) || "0".equals(param.getString(SHOW_TYPE_KEY));
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "";
        }
        List<String> names = Lists.newArrayList();
        String[] values = value.toString().split(",|;");
        for (String v : values) {
            boolean pathQuery = v.indexOf(Separator.SLASH.getValue()) > 0;
            boolean isUser = v.startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue());
            if (pathQuery) {
                String[] parts = v.split(Separator.SLASH.getValue());
                isUser = parts[parts.length - 1].startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue());
            }
            if (isUser) {
                // 查询返回用户
                if (!pathQuery) {
                    Map<String, String> map = userInfoService.getUserNamesByUserIds(Lists.newArrayList(v));
                    if (map.containsKey(v)) {
                        names.add(map.get(v));
                    }
                }

            } else {
                OrgElementPathEntity pathEntity = pathQuery ? orgElementPathService.getByIdPathAndOrgVersionUuid(v, null) : orgElementPathService.getByOrgElementIdAndOrgVersionUuid(v, null);
                if (pathEntity != null) {
                    if (displayPath) {
                        names.add(pathEntity.getCnPath());
                    } else {
                        String[] strs = pathEntity.getCnPath().split(Separator.SLASH.getValue());
                        names.add(strs[strs.length - 1]);
                    }
                }
            }

        }
        return StringUtils.join(names, ";");
    }

    @Override
    public String getConfigKey(RendererParam param) {
        return SELECTIVE_ALL_ELE_KEY;
    }

    @Override
    public String getType() {
        return "orgElementRenderer";
    }

    @Override
    public String getName() {
        return "组织元素渲染器";
    }
}
