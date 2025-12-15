package com.wellsoft.pt.app.css;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
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
public class FontIcon implements Serializable {

    private String id;

    private String name;

    private List<String> classes = Lists.newArrayList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }


}
