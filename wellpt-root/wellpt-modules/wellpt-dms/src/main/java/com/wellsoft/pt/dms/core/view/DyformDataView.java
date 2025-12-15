package com.wellsoft.pt.dms.core.view;

import com.wellsoft.pt.dms.file.view.FileDataView;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/27
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/27    chenq		2019/5/27		Create
 * </pre>
 */
@Component
public class DyformDataView implements DataView, FileDataView {
    @Override
    public String getName() {
        return "表单视图展示";
    }

    @Override
    public String getType() {
        return "dyformDataView";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

    }

    @Override
    public int getOrder() {
        return 60;
    }
}
