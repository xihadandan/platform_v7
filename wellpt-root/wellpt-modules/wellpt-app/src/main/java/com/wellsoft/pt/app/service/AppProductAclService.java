package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppProductAclDao;
import com.wellsoft.pt.app.entity.AppProductAclEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年08月02日   chenq	 Create
 * </pre>
 */
public interface AppProductAclService extends JpaService<AppProductAclEntity, AppProductAclDao, Long> {

    void deleteProdAcl(String prodId, String type);

    void addProductAcl(List<AppProductAclEntity> list);

    void saveProdAcl(List<AppProductAclEntity> list, String prodId);

    List<AppProductAclEntity> getProdAcl(String prodId);
}
