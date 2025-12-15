package com.wellsoft.pt.bm.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.bm.bean.SelfPublicityBean;
import com.wellsoft.pt.bm.entity.AdvisoryComplaints;
import com.wellsoft.pt.bm.entity.RegisterApply;
import com.wellsoft.pt.repository.entity.FileEntity;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 商事事务业务类
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-5.1	wangbx		2013-12-5		Create
 * </pre>
 * @date 2013-12-5
 */
public interface CommercialBusinessService {

    public String doReply(AdvisoryComplaints object);

    public AdvisoryComplaints getAdviceByUuid(String uuid);

    public String doSelfPublicVerify(SelfPublicityBean bean);

    public SelfPublicityBean getSelfPublicBeanByUuid(String uuid);

    public RegisterApply getRegisterByUuid(String uuid);

    public String doRegisterReply(RegisterApply obj);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public FileEntity getPublicityAttach(String uuid);

    public Map<String, Object> getCommercialRegister();

    /**
     * 如何描述该方法
     *
     * @param uuids
     * @return
     */
    public File generateZipFile(Collection<String> uuids) throws Exception;

    /**
     * 分页获取数据
     *
     * @param string
     * @param values
     * @param pagingInfo
     * @return
     */
    public List<QueryItem> queryQueryItem(String hql, Map<String, Object> values, PagingInfo pagingInfo);

    /**
     * 通过hql语句查询数量
     *
     * @param string
     * @param values
     * @return
     */
    public Long findCountByHql(String string, Map<String, Object> values);

}
