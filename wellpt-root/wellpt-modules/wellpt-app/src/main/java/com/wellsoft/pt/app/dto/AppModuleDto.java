package com.wellsoft.pt.app.dto;

import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.entity.AppTagEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月31日   chenq	 Create
 * </pre>
 */
public class AppModuleDto extends AppModule {
    private static final long serialVersionUID = -6537569459390617875L;

    private List<AppTagEntity> tags;

    public List<AppTagEntity> getTags() {
        return tags;
    }

    public void setTags(List<AppTagEntity> tags) {
        this.tags = tags;
    }
}
