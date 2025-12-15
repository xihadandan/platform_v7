package com.wellsoft.pt.app.context.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.context.AppContextPropertiesConfigurationSupport;
import com.wellsoft.pt.app.context.AppParserContext;
import com.wellsoft.pt.app.css.FontIcon;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * （作废！！！图标库解析统一由前端脚本解析）
 *
 * @author chenq
 * @date 2019/9/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/3    chenq		2019/9/3		Create
 * </pre>
 */
//@Component
@Deprecated
public class FontIconParser extends AbstractAppContextParser {
    private static final String PT_ICON_PREFIX = "pt.icons.";

    private static final String APP_ICON_PREFIX = "app.icons.";

    private static final String ICON_ID_PREFIX = "id";
    private static final String ICON_NAME_PREFIX = "name";
    private static final String ICON_CLASS_PREFIX = "class";
    private List<FontIcon> fontIcons = Lists.newArrayList();

    @Override
    public void parse(AppContextPropertiesConfigurationSupport configurationSupport, AppParserContext parserContext) {
        Map<String, String> cssValues = extractValues(configurationSupport, PT_ICON_PREFIX, APP_ICON_PREFIX);
        Map<String, FontIcon> fontIconMap = Maps.newLinkedHashMap();
        Set<String> keys = cssValues.keySet();
        for (String k : keys) {
            String[] parts = k.split("\\.");
            String iconType = parts[2];
            if (fontIconMap.containsKey(iconType)) {
                continue;
            } else {
                String iconPrefix = parts[0] + "." + parts[1] + "." + iconType;
                FontIcon fontIcon = new FontIcon();
                fontIcon.setName(cssValues.get(iconPrefix + "." + ICON_NAME_PREFIX));
                fontIcon.setId(cssValues.get(iconPrefix + "." + ICON_ID_PREFIX));
                String classes = cssValues.get(iconPrefix + "." + ICON_CLASS_PREFIX);
                if (StringUtils.isNotBlank(classes)) {
                    String[] all = classes.split(",|;");
                    fontIcon.getClasses().addAll(Arrays.asList(all));
                }
                fontIconMap.put(iconType, fontIcon);
            }

        }
        if (!fontIconMap.isEmpty()) {
            fontIcons = Lists.newArrayList(fontIconMap.values().iterator());
        }

    }

    public List<FontIcon> getFontIcons() {
        return fontIcons;
    }
}
