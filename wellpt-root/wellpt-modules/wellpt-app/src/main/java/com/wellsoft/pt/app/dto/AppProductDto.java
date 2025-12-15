package com.wellsoft.pt.app.dto;

import com.wellsoft.pt.app.entity.AppProdVersionEntity;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.entity.AppProductAclEntity;
import com.wellsoft.pt.app.entity.AppTagEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月28日   chenq	 Create
 * </pre>
 */
public class AppProductDto extends AppProduct {
    private static final long serialVersionUID = 5759390507816573139L;

    // 最新发布版本
    private AppProdVersionEntity latestVersion;

    private List<AppTagEntity> tags;

    private List<AppProductAclEntity> aclList;

    public List<AppTagEntity> getTags() {
        return tags;
    }

    public void setTags(List<AppTagEntity> tags) {
        this.tags = tags;
    }

    public AppProdVersionEntity getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(AppProdVersionEntity latestVersion) {
        this.latestVersion = latestVersion;
    }

    public List<AppProductAclEntity> getAclList() {
        return aclList;
    }

    public void setAclList(List<AppProductAclEntity> aclList) {
        this.aclList = aclList;
    }
}
