package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.OrgDutySeqDao;
import com.wellsoft.pt.multi.org.dto.DutySeqAndjobRankDto;
import com.wellsoft.pt.multi.org.dto.OrgDutyHierarchyViewDto;
import com.wellsoft.pt.multi.org.dto.OrgDutySeqTreeDto;
import com.wellsoft.pt.multi.org.dto.SelectOptionDto;
import com.wellsoft.pt.multi.org.entity.OrgDutySeqEntity;
import com.wellsoft.pt.multi.org.vo.OrgDutySeqVo;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface OrgDutySeqService extends JpaService<OrgDutySeqEntity, OrgDutySeqDao, String> {

    /**
     * 查询职务序列树形结构
     *
     * @param param
     * @return
     * @author baozh
     * @date 2021/10/22 15:03
     */
    List<OrgDutySeqTreeDto> queryDutySeqTreeByParam(Map<String, Object> param);

    /**
     * 查询职务序列树形结构
     *
     * @param keyword
     * @param notDutySeqUuid 排除在外的职务序列UUID
     * @return
     * @author baozh
     * @date 2021/10/20 17:00
     */
    List<OrgDutySeqTreeDto> queryDutySeqTree(String keyword, String notDutySeqUuid);


    /**
     * 职务体系视图
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/10/21 18:35
     */
    OrgDutyHierarchyViewDto queryDutyHierarchyView();

    /**
     * 查询职级序列树形结构
     *
     * @param keyword
     * @return
     * @author baozh
     * @date 2021/10/20 17:00
     */
    List<OrgDutySeqTreeDto> queryJobRankTree(String keyword);

    /**
     * 下拉职务序列
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/11/4 15:03
     */

    List<SelectOptionDto> queryDutySeqSelect(String keyword, String notDutySeqUuid);

    /**
     * 下拉职级
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/11/4 15:03
     */

    List<SelectOptionDto> queryJobRankSelect(String keyword);

    /**
     * 职务序列更新
     *
     * @param dutySeqVo
     * @return
     * @author baozh
     * @date 2021/10/25 17:42
     */
    String updateDutySeq(OrgDutySeqVo dutySeqVo);

    /**
     * 方法描述
     *
     * @param dutySeqVo
     * @return
     * @author baozh
     * @date 2021/10/27 17:12
     */
    String saveDutySeq(OrgDutySeqVo dutySeqVo);

    /**
     * 导出职务体系excel文件
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/10/27 17:13
     */
    File exportDutyHierarchyExcelFile(String fileName);

    /**
     * 职务体系删除
     *
     * @param dutySeqUuid
     * @return
     * @author baozh
     * @date 2021/10/28 16:52
     */
    String deleteDutySeq(String dutySeqUuid);

    /**
     * 查询职务树形结构
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/11/30 15:35
     */
    List<OrgDutySeqTreeDto> queryDutyTree(String dutySeqUuid);

    /**
     * 下拉职务
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/11/4 15:03
     */

    List<SelectOptionDto> queryDutySelect(String dutySeqUuid);

    /**
     * 获取职务序列编号和职级ID对应关系列表
     *
     * @param jobRankIds
     * @return java.util.List<com.wellsoft.pt.multi.org.dto.DutySeqAndjobRankDto>
     **/
    List<DutySeqAndjobRankDto> getDutySeqAndjobRankListByRankIds(List<String> jobRankIds);

    /**
     * 返回是否有异常职等
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/12/7 9:23
     */
    String isExceptionJobGrade();

 }
