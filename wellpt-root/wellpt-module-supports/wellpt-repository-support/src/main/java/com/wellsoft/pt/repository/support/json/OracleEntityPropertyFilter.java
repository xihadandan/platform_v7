package com.wellsoft.pt.repository.support.json;

import com.wellsoft.pt.repository.entity.FileInFolder;
import com.wellsoft.pt.repository.entity.Folder;
import com.wellsoft.pt.repository.entity.FolderOperateLog;
import net.sf.json.util.PropertyFilter;

public class OracleEntityPropertyFilter implements PropertyFilter {

    @Override
    public boolean apply(Object source, String propertyName, Object value) {
        if (source instanceof Folder && (propertyName.equals("files") || propertyName.equals("logs"))) {
            return true;
        } else if (source instanceof FileInFolder && propertyName.equals("folder")) {
            return true;
        } else if (source instanceof FolderOperateLog && propertyName.equals("folder")) {
            return true;
        }
        return false;
    }
}
