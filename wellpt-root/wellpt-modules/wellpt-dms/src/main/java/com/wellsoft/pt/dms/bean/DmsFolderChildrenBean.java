package com.wellsoft.pt.dms.bean;

import com.wellsoft.pt.dms.entity.DmsFolderEntity;

import java.io.Serializable;

/**
 * @author yt
 * @title: DmsFolderChildrenBean
 * @date 2020/9/28 18:02
 */
public class DmsFolderChildrenBean implements Serializable {

    private DmsFolderEntity dmsFolder;

    private boolean isParent;//是否父级

    public DmsFolderEntity getDmsFolder() {
        return dmsFolder;
    }

    public void setDmsFolder(DmsFolderEntity dmsFolder) {
        this.dmsFolder = dmsFolder;
    }

    public boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }
}
