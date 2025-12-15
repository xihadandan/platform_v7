/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgJobRankDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgJobRankService extends JpaService<MultiOrgJobRank, MultiOrgJobRankDao, String> {
    /**
     * 如何描述该方法
     *
     * @param eleId
     * @return
     */
    MultiOrgJobRank getById(String eleId);

    /**
     * 如何描述该方法
     *
     * @param select2QueryInfo
     * @return
     */
    Select2QueryData queryJobRankListForSelect2(Select2QueryInfo select2QueryInfo);

    public long countBySystemUnitIds(List<String> systemUnitIds);

    /**
     * 方法描述
     *
     * @param paramMap
     * @return
     * @author baozh
     * @date 2021/10/22 15:15
     */
    List<MultiOrgJobRank> queryJobRankListByParam(Map<String, Object> paramMap);

    List<Integer> getJobGradeByJobRankId(String... jobRank);

    /**
     * 根据职级获取职等
     *
     * @param jobRank
     * @return
     * @author baozh
     * @date 2021/10/27 9:09
     */
    List<Integer> getJobGradeByJobRankId(String order, String... jobRank);

    List<MultiOrgJobRank> getMultiOrgJobRankByJobRankId(String... jobRank);

    /**
     * 根据职级获取职级信息
     *
     * @param jobRank
     * @return
     * @author baozh
     * @date 2021/11/22 16:16
     */
    List<MultiOrgJobRank> getMultiOrgJobRankByJobRankId(String order, String... jobRank);

    /**
     * 根据职务序列统计
     *
     * @param dutySeqUuid
     * @return
     * @author baozh
     * @date 2021/10/28 17:05
     */
    long countByDutySeqUuid(String dutySeqUuid);

    List<MultiOrgJobRank> getMultiOrgJobRankDetailByDutySeqUuid(String dutySeqUuid);
}
