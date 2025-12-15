package com.wellsoft.pt.mail.support;

import java.util.List;

/**
 * Description: 邮件分页类
 *
 * @author wuzq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1	wuzq		2013-2-7		Create
 * </pre>
 * @date 2013-3-1
 */
public class MailPage {


    protected List result;
    protected long totalCount;
    protected int pageNo;
    private long totalPage;
    private long noread;

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getNoread() {
        return noread;
    }

    public void setNoread(long noread) {
        this.noread = noread;
    }


}
