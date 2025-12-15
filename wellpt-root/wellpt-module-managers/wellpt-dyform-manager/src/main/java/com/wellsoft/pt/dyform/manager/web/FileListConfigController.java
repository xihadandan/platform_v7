package com.wellsoft.pt.dyform.manager.web;

import com.wellsoft.pt.dyform.manager.entity.DyformFileListButtonConfig;
import com.wellsoft.pt.dyform.manager.entity.DyformFileListSourceConfig;
import com.wellsoft.pt.dyform.manager.service.DyformFileListButtonConfigService;
import com.wellsoft.pt.dyform.manager.service.DyformFileListSourceConfigService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 列表式附件配置controller
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/8/29.1        shenhb    2020/8/29           Create
 * </pre>
 * @date 2020/8/29
 */
@RestController
@RequestMapping("fileListConfig")
public class FileListConfigController {

    @Autowired
    private DyformFileListSourceConfigService dyformFileListSourceConfigService;

    @Autowired
    private DyformFileListButtonConfigService dyformFileListButtonConfigService;

    @GetMapping("list")
    public Map<String, Object> getConfig() {
        boolean isAnonymousUser = SpringSecurityUtils.isAnonymousUser();
        Map<String, Object> map = new HashMap<>();
        map.put("isAnonymousUser", isAnonymousUser);
        List<DyformFileListSourceConfig> allSourceConfigList = dyformFileListSourceConfigService.getAllBean();
        List<DyformFileListButtonConfig> allButtonConfigList = dyformFileListButtonConfigService.getAllBean();
        map.put("allSourceConfigList", allSourceConfigList);
        map.put("allButtonConfigList", allButtonConfigList);
        return map;
    }

}
