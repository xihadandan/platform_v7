package com.wellsoft.pt.app.context;

import com.wellsoft.pt.app.context.config.ContainerComponentRegistry;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;

/**
 * Description: theme-pt.properties等配置文件
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月10日.1	wujx		2016年8月10日		Create
 * </pre>
 * @date 2016年8月10日
 */
public interface AppContextConfigurer {

    void addProperties(PropertiesRegistry propertiesRegistry);

    void addContainerComponent(ContainerComponentRegistry containerComponentRegistry);

}
