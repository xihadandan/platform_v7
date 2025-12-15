package com.wellsoft.pt.webmail.support;

import com.wellsoft.pt.multi.org.support.dataprovider.OrgTreeDialogDataProvider;

import javax.mail.Address;
import java.util.List;

/**
 * Description: 抽象邮件目录树对话框数据提供程序
 *
 * @author chenq
 * @date 2020/3/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/3/18    chenq		2020/3/18		Create
 * </pre>
 */
public abstract class AbstractMailContackBookTreeDialogDataProvider implements
        OrgTreeDialogDataProvider {

    /**
     * 解析邮件地址
     *
     * @param address
     * @return
     */
    public abstract List<Address> explainMailAddress(String id) throws Exception;

    /**
     * 匹配通讯录节点ID是否属于该通讯录的解析范畴内
     *
     * @param address
     * @return
     */
    public abstract boolean match(String id);

    public String getLocalId(String id) {

        if (match(id)) {
            //是该通讯录的用户id范围，则去掉前缀返回ID
            return id.substring(id.indexOf("_") + 1);
        }
        //否则则认为是本系统内的用户，则加上其他系统可以识别的前缀后返回
        return "XXXXX_" + id;

    }
}
