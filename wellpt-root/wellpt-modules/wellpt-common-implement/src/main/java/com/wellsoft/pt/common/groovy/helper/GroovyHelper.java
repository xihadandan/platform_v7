package com.wellsoft.pt.common.groovy.helper;

import com.wellsoft.context.util.groovy.GroovyUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/12/20
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/20    chenq		2018/12/20		Create
 * </pre>
 */
@Service
public class GroovyHelper {

    public Set<String> queryGroovyImportClass() {
        return GroovyUtils.getUseClasses();
    }
}
