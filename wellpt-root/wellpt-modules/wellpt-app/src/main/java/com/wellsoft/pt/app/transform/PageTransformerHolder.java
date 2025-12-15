package com.wellsoft.pt.app.transform;

import com.wellsoft.pt.app.ui.AbstractPage;
import com.wellsoft.pt.app.ui.Page;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/2
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/2    chenq		2019/8/2		Create
 * </pre>
 */
public class PageTransformerHolder {

    private static ThreadLocal<Page> page = new ThreadLocal<>();


    public static Page page() {
        return page.get();
    }

    public static Page page(Page p) {
        page.set(p);
        return page();
    }

    public static String uuid() {
        if (page.get() instanceof AbstractPage) {
            return ((AbstractPage) page.get()).getUuid();
        }
        return null;
    }

    public static void clear() {
        page.remove();
    }

}
