package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.constant.UnitParamConstant;
import com.wellsoft.pt.multi.org.dao.OrgDutySeqDao;
import com.wellsoft.pt.multi.org.dto.*;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;
import com.wellsoft.pt.multi.org.entity.OrgDutySeqEntity;
import com.wellsoft.pt.multi.org.entity.OrgJobGradeEntity;
import com.wellsoft.pt.multi.org.service.MultiOrgDutyService;
import com.wellsoft.pt.multi.org.service.MultiOrgJobRankService;
import com.wellsoft.pt.multi.org.service.OrgDutySeqService;
import com.wellsoft.pt.multi.org.service.OrgJobGradeService;
import com.wellsoft.pt.multi.org.vo.OrgDutySeqVo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 * 职务序列ServiceImpl
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/20   Create
 * </pre>
 */
@Service
public class OrgDutySeqServiceImpl extends AbstractJpaServiceImpl<OrgDutySeqEntity, OrgDutySeqDao, String>
        implements OrgDutySeqService {

    @Autowired
    MultiOrgDutyService multiOrgDutyService;
    @Autowired
    OrgJobGradeService orgJobGradeService;
    @Autowired
    private MultiOrgJobRankService multiOrgJobRankService;

    /**
     * 查询职务序列树
     * 1、keyword为空，查询全部序列
     * 2、keyword不为空，查询keyword关联数据及其父序列与子序列
     *
     * @param keyword
     * @param notDutySeqUuid
     * @return
     * @author baozh
     * @date 2021/11/5 9:27
     */
    @Override
    public List<OrgDutySeqTreeDto> queryDutySeqTree(String keyword, String notDutySeqUuid) {
        // 职务序列名称、编号模糊
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotBlank(keyword)) {
            param.put("keyword", "%" + keyword + "%");
        }
        param.put("notDutySeqUuid", notDutySeqUuid);
        return queryDutySeqTreeByParam(param);
    }

    /**
     * 通过 param 查询职务序列树
     *
     * @param param
     * @return
     * @author baozh
     * @date 2021/11/5 9:29
     */
    @Override
    public List<OrgDutySeqTreeDto> queryDutySeqTreeByParam(Map<String, Object> param) {
        return queryDutySeqTreeByParam(param, null, null);
    }

    /**
     * 1、通过 param 查询职务序列列表及其父序列列表与子序列列表
     * 2、通过职务序列uuid列表查询职务序列列表及其父序列列表
     * 将子节点列表加入职务序列列表计算出职务序列树
     *
     * @param param
     * @return 职务序列树
     * @author baozh
     * @date 2021/11/5 9:30
     */
    private List<OrgDutySeqTreeDto> queryDutySeqTreeByParam(Map<String, Object> param, List<OrgDutySeqTreeDto> children,
                                                            List<String> dutySeqUuids) {
        param.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        String notDutySeqUuid = (String) param.remove("notDutySeqUuid");
        List<OrgDutySeqEntity> entities = null;
        if (dutySeqUuids == null || dutySeqUuids.isEmpty()) {
            if (param.containsKey("keyword") && param.get("keyword") != null) {
                List<OrgDutySeqEntity> entityList = listByHQL("from OrgDutySeqEntity where systemUnitId = :systemUnitId and (dutySeqCode like :keyword or dutySeqName like :keyword)", param);
                //递归查询父级
                List<String> parentUuids = entityList.stream()
                        .filter(entity -> !"0".equals(entity.getParentUuid()))
                        .map(entity -> entity.getParentUuid()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(parentUuids)) {
                    entityList.addAll(findParentSeq(parentUuids));
                }
                //递归查询子级
                List<String> deqUuids = entityList.stream()
                        .map(entity -> entity.getUuid()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(deqUuids)) {
                    entityList.addAll(findChildrenSeq(deqUuids));
                }
                //去重
                Set<OrgDutySeqEntity> collect = entityList.stream().collect(Collectors.toSet());
                entities = new ArrayList<>(collect);
            } else {
                entities = listByHQL("from OrgDutySeqEntity where systemUnitId = :systemUnitId ", param);
            }
            //entities = listByNameSQLQuery("queryDutySeqListByParam", param);
        } else {
            entities = listByUuids(dutySeqUuids);
            //递归查询父级
            List<String> parentUuids = entities.stream()
                    .filter(entity -> !"0".equals(entity.getParentUuid()))
                    .map(entity -> entity.getParentUuid()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(parentUuids)) {
                entities.addAll(findParentSeq(parentUuids));
            }
        }

        // 转为处理类
        List<OrgDutySeqTreeDto> all = entities.stream().map(entity -> {
                    OrgDutySeqTreeDto dto = new OrgDutySeqTreeDto();
                    BeanUtils.copyProperties(entity, dto);
                    dto.setChildrenType(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_SEQ);
                    return dto;
                }).sorted(Comparator.comparing(OrgDutySeqTreeDto::getDutySeqCode, Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
        if (children != null) {
            all.addAll(children);
        }
        List<OrgDutySeqTreeDto> collect = all.stream().filter(entity -> "0".equals(entity.getParentUuid()))
                .filter(entity -> !Objects.equals(entity.getUuid(), notDutySeqUuid))
                .peek(entity -> entity.setChildrens(getChildrens(entity, all, 0, notDutySeqUuid)))
                .collect(Collectors.toList());
        return collect;
    }

    //递归查询父级
    private List<OrgDutySeqEntity> findParentSeq(List<String> parentSeqUuids) {
        List<OrgDutySeqEntity> entities = listByUuids(parentSeqUuids);
        List<String> parentUuids = entities.stream()
                .filter(entity -> !"0".equals(entity.getParentUuid()))
                .map(entity -> entity.getParentUuid()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(parentUuids)) {
            entities.addAll(findParentSeq(parentUuids));
        }
        return entities;
    }

    //递归查询子级
    private List<OrgDutySeqEntity> findChildrenSeq(List<String> seqUuids) {
        List<OrgDutySeqEntity> entities = getDao().listByFieldInValues("parentUuid", seqUuids);
        List<String> deqUuids = entities.stream()
                .map(entity -> entity.getUuid()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deqUuids)) {
            entities.addAll(findChildrenSeq(deqUuids));
        }
        return entities;
    }

    /**
     * 递归查找所有序列子序列
     *
     * @param root,all，level
     * @return
     * @author baozh
     * @date 2021/11/5 9:34
     */
    private List<OrgDutySeqTreeDto> getChildrens(OrgDutySeqTreeDto root, List<OrgDutySeqTreeDto> all, int level, String notDutySeqUuid) {
        root.setLevel(level);
        List<OrgDutySeqTreeDto> children = all.stream()
                .filter(entity -> Objects.equals(entity.getParentUuid(), root.getUuid()) && !Objects.equals(entity.getUuid(), notDutySeqUuid))
                .peek(entity -> entity.setChildrens(getChildrens(entity, all, level + 1, notDutySeqUuid)))// 1、找到子序列
                .collect(Collectors.toList());
        return children;
    }

    /**
     * 职务序列视图
     * 1、视图头部信息
     * 2、视图职务职级内容
     * 3、视图纵向职等列表
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/11/5 9:34
     */
    @Override
    public OrgDutyHierarchyViewDto queryDutyHierarchyView() {
        OrgDutyHierarchyViewDto viewDto = new OrgDutyHierarchyViewDto();

        // 1、查询所有职务序列
        List<OrgDutySeqTreeDto> dutySeqDtos = queryDutySeqTree(null, null);
        int maxChildDepth = calculateDepth(dutySeqDtos) + 1;// 字节点最大深度
        viewDto.setChildDepth(maxChildDepth);
        // 2、计算开始行开始列
        cellRange(dutySeqDtos);
        int nextCell = 1;
        for (OrgDutySeqTreeDto dto : dutySeqDtos) {
            nextCell = calculateRowAndCell(maxChildDepth, dto, nextCell);
        }

        viewDto.setOrgDutyHeaderDto(dutySeqDtos);
        List<MultiOrgJobRank> multiOrgJobRanks = multiOrgJobRankService.getDao().listByFieldEqValue("systemUnitId",
                SpringSecurityUtils.getCurrentUserUnitId());
        Map<String, MultiOrgJobRank> orgJobRankMap = new HashMap<>();
        for (MultiOrgJobRank multiOrgJobRank : multiOrgJobRanks) {
            orgJobRankMap.put(multiOrgJobRank.getId(), multiOrgJobRank);
        }
        List<String> jobRandIds = new ArrayList<>();

        // 3、获取body(职务列表)
        List<MultiOrgDuty> multiOrgDuties = multiOrgDutyService
                .queryAllDutyBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<OrgDutyBodyDto> bodyDtos = multiOrgDuties.stream()
                .filter(dto -> StringUtils.isNotBlank(dto.getDutySeqUuid()))// 过滤没有职务序列的数据
                .map(dto -> {
                    List<String> jobRankName = new ArrayList<>();
                    List<Integer> jobGrade = new ArrayList<>();
                    OrgDutyBodyDto bodyDto = new OrgDutyBodyDto(dto.getJobGrade(), dto.getDutySeqUuid());
                    for (String jobRankId : dto.getJobRank().split(",")) {
                        jobRandIds.add(jobRankId);
                        MultiOrgJobRank jobRank = orgJobRankMap.get(jobRankId);
                        jobRankName.add(jobRank.getJobRank());
                        jobGrade.add(jobRank.getJobGrade());
                    }
                    String grade = StringUtils.join(jobGrade, ",");
                    String name = StringUtils.join(jobRankName, ",");
                    bodyDto.setJobGrade(grade == null ? "" : grade);
                    bodyDto.setJobRankName(name == null ? "" : name);
                    bodyDto.setDutyName(dto.getName());
                    return bodyDto;
                }).collect(Collectors.toList());
        //查询未关联职务的职级
        for (String id : orgJobRankMap.keySet()) {
            if (!jobRandIds.contains(id)) {
                MultiOrgJobRank dto = orgJobRankMap.get(id);
                if (StringUtils.isNotBlank(dto.getDutySeqUuid())) {
                    OrgDutyBodyDto bodyDto = new OrgDutyBodyDto(dto.getJobGrade() + "", dto.getDutySeqUuid());
                    bodyDto.setJobRankName(dto.getJobRank());
                    bodyDto.setDutyName("");
                    bodyDtos.add(bodyDto);
                }
            }
        }

        viewDto.setOrgDutyBodyDto(bodyDtos);
        // 4、获取职等列表
        List<OrgJobGradeEntity> gradeEntities = orgJobGradeService.jobGradeList();
        Integer integerTrue = 1;
        List<OrgJobGradeEntity> jobGrades = gradeEntities.stream()
                .filter(grade -> integerTrue.equals(grade.getIsValid()) || integerTrue.equals(grade.getIsException()))
                .peek(grade -> {
                    if (grade.getIsException() == null) {
                        grade.setIsException(0);
                    }
                })
                .collect(Collectors.toList());
        viewDto.setJobGrade(jobGrades);
        return viewDto;
    }


    /**
     * 递归计算开始行，开始列，跨行
     * 1、第一个子列的开始列等于父列的开始列
     * 2、N子列的开始列等于上一子列的开始列+上一列的跨列
     * 3、跨行等于父类的深度-当前子深度
     * 4、开始行等于父类开始行+跨行
     *
     * @param maxDepth
     * @return
     * @author baozh
     * @date 2021/10/28 11:04
     */
    private int calculateRowAndCell(int maxDepth, OrgDutySeqTreeDto dto, int nextCell) {
        dto.setStartCell(nextCell);
        nextCell = nextCell + dto.getCellRange();
        int rowRange = maxDepth - dto.getChildDepth();
        dto.setRowRange(rowRange);
        int childNextCell = dto.getStartCell();
        for (OrgDutySeqTreeDto childDto : dto.getChildrens()) {
            childDto.setStartRow(dto.getStartRow() + rowRange);
            childNextCell = calculateRowAndCell(dto.getChildDepth(), childDto, childNextCell);
        }
        return nextCell;
    }

    /**
     * 递归计算跨列
     * 1、子列表为空则跨2列
     * 2、子列表不为空则为所有子列表跨列加和
     *
     * @param children
     * @return
     * @author baozh
     * @date 2021/10/28 11:26
     */
    private int cellRange(List<OrgDutySeqTreeDto> children) {
        if (children == null || children.isEmpty()) {
            return 2;
        }
        int cellRange = 0;
        for (OrgDutySeqTreeDto childDto : children) {
            int range = cellRange(childDto.getChildrens());
            childDto.setCellRange(range);
            cellRange = range + cellRange;
        }
        return cellRange;
    }

    /**
     * 递归计算深度
     * 1、子节点为空，深度为0
     * 2、子节点不为空，深度为子节点的最大深度+1
     *
     * @param children
     * @return 子节点最大深度
     * @author baozh
     * @date 2021/10/28 10:45
     */
    private int calculateDepth(List<OrgDutySeqTreeDto> children) {
        int maxChildDepth = 0;// 子节点最大深度
        // 计算跨列与子节点深度int
        if (children != null && children.size() > 0) {
            // 子节点最大深度
            for (OrgDutySeqTreeDto childDto : children) {
                int depth = calculateDepth(childDto.getChildrens()) + 1;
                childDto.setChildDepth(depth);
                if (depth > maxChildDepth) {
                    maxChildDepth = depth;
                }
            }
        }
        return maxChildDepth;
    }

    /**
     * 职级树查询三种场景
     * 1、keyword为空时查询全部序列与对应职级
     * 2、keyword不为空，通过keyword查询职级、序列为空，直接返回null
     * 3、keyword不为空，通过keyword查询职级、序列得到序列uuid，通过序列uuid查询对应序列及其父序列
     *
     * @return
     * @author baozh
     * @date 2021/11/5 9:22
     * @params *@params
     */
    @Override
    public List<OrgDutySeqTreeDto> queryJobRankTree(String keyword) {
        // 职务序列名称、职级模糊
        // 1、查询所有职务序列并获取深度
        Map<String, Object> paramMap = new HashMap<>();
        if (StringUtils.isNotBlank(keyword)) {
            paramMap.put("keyword", keyword);
        }
        // 1、查询职级列表
        MultiOrgJobRank paramJobRank = new MultiOrgJobRank();
        paramJobRank.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<MultiOrgJobRank> multiOrgJobRanks = multiOrgJobRankService.queryJobRankListByParam(paramMap);
        // 将职级转化为树形结构返回
        List<OrgDutySeqTreeDto> collect = multiOrgJobRanks.stream().filter(jobRank -> jobRank.getDutySeqUuid() != null)// 处理历史数据
                .map(jobRank -> {
                    OrgDutySeqTreeDto dto = new OrgDutySeqTreeDto();
                    dto.setChildrenType(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_RANK);
                    dto.setId(jobRank.getId());
                    dto.setUuid(jobRank.getUuid());
                    dto.setParentUuid(jobRank.getDutySeqUuid());
                    dto.setDutySeqCode(jobRank.getCode() == null ? "" : jobRank.getCode());
                    dto.setDutySeqName(jobRank.getJobRank());
                    return dto;
                }).sorted(Comparator.comparing(OrgDutySeqTreeDto::getDutySeqCode).reversed()).collect(Collectors.toList());
        List<String> dutySeqUuids = null;
        // 带条件查询时，判断职务序列uuid
        // 如果不带查询条件则查全部职务序列
        if (StringUtils.isNotBlank(keyword)) {
            dutySeqUuids = multiOrgJobRanks.stream().filter(jobRank -> jobRank.getDutySeqUuid() != null)// 处理历史数据
                    .map(jobRank -> jobRank.getDutySeqUuid()).collect(Collectors.toList());
            if (dutySeqUuids == null || dutySeqUuids.isEmpty()) {
                return new ArrayList<>();
            }
        }
        return queryDutySeqTreeByParam(new HashMap<>(), collect, dutySeqUuids);
    }

    /**
     * 下拉显示职务序列
     *
     * @param keyword
     * @return
     * @author baozh
     * @date 2021/11/5 9:58
     */
    @Override
    public List<SelectOptionDto> queryDutySeqSelect(String keyword, String notDutySeqUuid) {
        return toSelectOption(queryDutySeqTree(keyword, notDutySeqUuid));
    }

    private List<SelectOptionDto> toSelectOption(List<OrgDutySeqTreeDto> dutySeqTreeDtos) {
        List<SelectOptionDto> collect = dutySeqTreeDtos.stream().map(dutySeqDto -> {
            SelectOptionDto dto = new SelectOptionDto(dutySeqDto.getUuid(), dutySeqDto.getDutySeqName());
            dto.setData(dutySeqDto);
            dto.setNodeLevel(dutySeqDto.getLevel());
            dto.setChildren(getChildren(dto, dutySeqDto, false));
            return dto;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 下拉显示职级
     *
     * @param keyword
     * @return
     * @author baozh
     * @date 2021/12/1 9:48
     */
    @Override
    public List<SelectOptionDto> queryJobRankSelect(String keyword) {
        //过滤没有职级的职务序列
        List<OrgDutySeqTreeDto> newDutySeqTreeDtos = queryJobRankTree(keyword).stream()
                .peek(dto -> dto.setChildrens(filterChildern(dto)))
                .filter(dto -> !(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_SEQ.equals(dto.getChildrenType()) && dto.getChildrens().size() == 0))
                .collect(Collectors.toList());
        List<SelectOptionDto> collect = newDutySeqTreeDtos.stream().map(dutySeqDto -> {
            String id = dutySeqDto.getId();
            if (StringUtils.isBlank(id)) {
                id = dutySeqDto.getUuid();
            }
            SelectOptionDto dto = new SelectOptionDto(id, dutySeqDto.getDutySeqName());
            dto.setData(dutySeqDto);
            dto.setNodeLevel(dutySeqDto.getLevel());
            dto.setChildren(getChildren(dto, dutySeqDto, true));
            return dto;
        }).collect(Collectors.toList());
        return collect;
    }

    private List<OrgDutySeqTreeDto> filterChildern(OrgDutySeqTreeDto dutySeqTreeDto) {
        if (UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_RANK.equals(dutySeqTreeDto.getChildrenType())) {
            return dutySeqTreeDto.getChildrens();
        }
        List<OrgDutySeqTreeDto> newDutySeqTreeDtos = dutySeqTreeDto.getChildrens().stream()
                .peek(dto -> dto.setChildrens(filterChildern(dto)))
                .filter(dto -> !(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_SEQ.equals(dto.getChildrenType()) && dto.getChildrens().size() == 0))
                .collect(Collectors.toList());
        return newDutySeqTreeDtos;
    }

    /**
     * 下拉显示职务
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/12/1 9:57
     */
    @Override
    public List<SelectOptionDto> queryDutySelect(String dutySeqUuid) {
        return toSelectOption(queryDutyTree(dutySeqUuid));
    }

    @Override
    public List<DutySeqAndjobRankDto> getDutySeqAndjobRankListByRankIds(List<String> jobRankIds) {
        Map<String, Object> values = Maps.newHashMap();
        if (jobRankIds == null || jobRankIds.size() == 0) {
            return Lists.newArrayList();
        }
        values.put("jobRankIds", jobRankIds);
        // QueryItem implements BaseQueryItem
        List<DutySeqAndjobRankDto> queryItems = this.dao.listItemByNameSQLQuery("getDutySeqAndjobRankListByRankIds",
                DutySeqAndjobRankDto.class, values, new PagingInfo(1, Integer.MAX_VALUE));

        return queryItems;
    }


    @Transactional
    @Override
    public String isExceptionJobGrade() {
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        OrgJobGradeEntity entity = new OrgJobGradeEntity();
        entity.setIsValid(0);
        entity.setSystemUnitId(systemUnitId);
        List<OrgJobGradeEntity> jobGradeEntities = orgJobGradeService.listByEntity(entity);
        List<Integer> collect = jobGradeEntities.stream().map(grade -> grade.getJobGrade()).collect(Collectors.toList());
        if (collect == null || collect.isEmpty()) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitId", systemUnitId);
        params.put("grades", collect);
        List<MultiOrgJobRank> jobRanks = multiOrgJobRankService.getDao().listByHQL("FROM MultiOrgJobRank WHERE systemUnitId =:systemUnitId AND jobGrade in (:grades)", params);
        if (jobRanks == null || jobRanks.isEmpty()) {
            return null;
        }
        List<Integer> exceptionGrade = jobRanks.stream().map(jobRank -> jobRank.getJobGrade()).collect(Collectors.toList());
        for (OrgJobGradeEntity gradeEntity : jobGradeEntities) {
            if (exceptionGrade.contains(gradeEntity.getJobGrade())) {
                gradeEntity.setIsException(1);
                orgJobGradeService.update(gradeEntity);
            }
        }
        return "部分职级的职等已超出范围，您可在体系视图中查看异常";
    }


    /**
     * 递归转化为下拉
     *
     * @param optionDto,parentDutySeqDto
     * @return
     * @author baozh
     * @date 2021/11/4 15:24
     */
    private List<SelectOptionDto> getChildren(SelectOptionDto optionDto, OrgDutySeqTreeDto parentDutySeqDto, boolean idAsKey) {
        if (parentDutySeqDto.getChildrens() == null || parentDutySeqDto.getChildrens().isEmpty()) {
            optionDto.setParent(false);
            return null;
        }
        optionDto.setParent(true);
        List<SelectOptionDto> collect = parentDutySeqDto.getChildrens().stream().map(dutySeqDto -> {
            String id = dutySeqDto.getUuid();
            if (idAsKey && StringUtils.isNotBlank(dutySeqDto.getId())) {
                id = dutySeqDto.getId();
            }
            SelectOptionDto dto = new SelectOptionDto(id, dutySeqDto.getDutySeqName());
            dto.setData(dutySeqDto);
            dto.setNodeLevel(dutySeqDto.getLevel());
            dto.setChildren(getChildren(dto, dutySeqDto, idAsKey));
            return dto;
        }).collect(Collectors.toList());
        return collect;

    }

    @Transactional
    @Override
    public String saveDutySeq(OrgDutySeqVo dutySeqVo) {
        if (StringUtils.isBlank(dutySeqVo.getParentUuid())) {
            dutySeqVo.setParentUuid("0");
        } else {
            String message = checkExistDutyRelation(dutySeqVo.getParentUuid(), true);
            if (StringUtils.isNotBlank(message)) {
                return message;
            }
        }

        dutySeqVo.setUuid(null);

        OrgDutySeqEntity one = new OrgDutySeqEntity();
        BeanUtils.copyProperties(dutySeqVo, one);
        one.setTenant(SpringSecurityUtils.getCurrentTenantId());
        save(one);
        return null;
    }

    @Transactional
    @Override
    public String updateDutySeq(OrgDutySeqVo dutySeqVo) {
        OrgDutySeqEntity oldOne = getOne(dutySeqVo.getUuid());
        if (oldOne == null) {
            throw new WellException("职务序列不存在");
        }
        // 如果父类为空则不修改
        if (StringUtils.isBlank(dutySeqVo.getParentUuid())) {
            dutySeqVo.setParentUuid(oldOne.getParentUuid());
        } else {
            String message = checkExistDutyRelation(dutySeqVo.getParentUuid(), true);
            if (StringUtils.isNotBlank(message)) {
                return message;
            }
        }
        BeanUtils.copyProperties(dutySeqVo, oldOne, Entity.BASE_FIELDS);
        oldOne.setTenant(SpringSecurityUtils.getCurrentTenantId());
        save(oldOne);
        return null;
    }

    @Transactional
    @Override
    public String deleteDutySeq(String dutySeqUuid) {
        String message = checkExistDutyRelation(dutySeqUuid, false);
        if (StringUtils.isNotBlank(message)) {
            return message;
        }
        delete(dutySeqUuid);
        return null;
    }

    @Override
    public List<OrgDutySeqTreeDto> queryDutyTree(String dutySeqUuid) {
        List<MultiOrgDuty> multiOrgDuties = null;
        List<MultiOrgJobRank> jobRanks = null;
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        if (StringUtils.isNotBlank(dutySeqUuid)) {
            multiOrgDuties = multiOrgDutyService.getDao().listByFieldEqValue("dutySeqUuid", dutySeqUuid);
            jobRanks = multiOrgJobRankService.getDao().listByFieldEqValue("dutySeqUuid",
                    dutySeqUuid);
        } else {
            multiOrgDuties = multiOrgDutyService.getDao().listByFieldEqValue("systemUnitId", systemUnitId);
            jobRanks = multiOrgJobRankService.getDao().listByFieldEqValue("systemUnitId", systemUnitId);
        }
        HashMap<String, String> rankMap = new HashMap<>();
        for (MultiOrgJobRank jobRank : jobRanks) {
            rankMap.put(jobRank.getId(), jobRank.getJobRank());
        }
        // 将职务转化为树形结构返回
        List<OrgDutySeqTreeDto> dutyDtoNotRankList = multiOrgDuties.stream().filter(duty -> duty.getDutySeqUuid() == null)// 处理历史数据
                .map(duty -> {
                    OrgDutySeqTreeDto dto = new OrgDutySeqTreeDto();
                    dto.setChildrenType(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_DUTY);
                    dto.setUuid(duty.getId());
                    dto.setDutySeqCode(duty.getCode());
                    dto.setDutySeqName(duty.getName());
                    return dto;
                })
                .sorted(Comparator.comparing(OrgDutySeqTreeDto::getDutySeqCode, Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
        // 将职务转化为树形结构返回
        List<OrgDutySeqTreeDto> dutyDtoList = multiOrgDuties.stream().filter(duty -> duty.getDutySeqUuid() != null)// 处理历史数据
                .map(duty -> {
                    OrgDutySeqTreeDto dto = new OrgDutySeqTreeDto();
                    dto.setParentUuid(duty.getDutySeqUuid());
                    dto.setChildrenType(UnitParamConstant.DUTY_SEQ_TREE_CHILDREN_TYPE_DUTY);
                    dto.setUuid(duty.getId());
                    dto.setDutySeqCode(duty.getCode());
                    String[] rankIds = duty.getJobRank().split(",");
                    List<String> ranks = new ArrayList(rankIds.length);
                    for (String rankId : rankIds) {
                        ranks.add(rankMap.get(rankId));
                    }
                    dto.setDutySeqName(duty.getName() + "-" + StringUtils.join(ranks, ","));
                    return dto;
                })
                .sorted(Comparator.comparing(OrgDutySeqTreeDto::getDutySeqCode, Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
        List<OrgDutySeqTreeDto> orgDutySeqTreeDtos = queryDutySeqTreeByParam(new HashMap<>(), dutyDtoList, null);
        orgDutySeqTreeDtos.addAll(dutyDtoNotRankList);
        return orgDutySeqTreeDtos;
    }

    private String checkExistDutyRelation(String dutySeqUuid, boolean saveDutyFlag) {
        // 1、判读是否有职务关联
        if (!saveDutyFlag) {
            long dutyCount = multiOrgDutyService.countByDutySeqUuid(dutySeqUuid);
            if (dutyCount > 0) {
                return "职务序列下存在职务，无法删除。";
            }
        }
        // 2、判断是否有职级关联
        long rankCount = multiOrgJobRankService.countByDutySeqUuid(dutySeqUuid);
        if (rankCount > 0) {
            return "请先修改或删除关联职级";
        }
        return null;
    }

    @Override
    public File exportDutyHierarchyExcelFile(String fileName) {
        OrgDutyHierarchyViewDto view = queryDutyHierarchyView();
        FileOutputStream fileOutputStream = null;
        try {
            HSSFWorkbook wb = createExcel(view);
            File file = new File(fileName);
            fileOutputStream = new FileOutputStream(file);
            wb.write(fileOutputStream);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ex) {
                }
            }
        }
        return null;
    }

    /**
     * 创建excel
     *
     * @param view
     * @return
     * @author baozh
     * @date 2021/10/28 9:20
     */
    private HSSFWorkbook createExcel(OrgDutyHierarchyViewDto view) {
        HSSFWorkbook wb = new HSSFWorkbook();
        // 在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("职务体系");
        // 在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居
        // 设置职等跨行
        sheet.addMergedRegion(new CellRangeAddress(0, view.getChildDepth() - 1, 0, 0));
        HSSFRow titleRow = sheet.createRow(0);
        HSSFCell cell = titleRow.createCell(0);
        cell.setCellValue("职等");
        cell.setCellStyle(style);
        List<OrgDutySeqTreeDto> dutyHeaderDtoList = view.getOrgDutyHeaderDto();
        Map<String, Integer> result = new HashMap<>();
        for (OrgDutySeqTreeDto dto : dutyHeaderDtoList) {
            createHeader(sheet, dto, style, result);
        }
        createBody(sheet, view.getJobGrade(), view.getOrgDutyBodyDto(), view.getChildDepth(), result, style);
        return wb;
    }

    /**
     * 创建excel头部
     *
     * @param sheet,dtoList
     * @return
     * @author baozh
     * @date 2021/10/28 9:20
     */
    private void createHeader(HSSFSheet sheet, OrgDutySeqTreeDto dto, HSSFCellStyle style,
                              Map<String, Integer> result) {
        // System.out.println(dto.getDutySeqName()+" "+ dto.getStartRow()+"
        // "+(dto.getStartRow() + dto.getRowRange() - 1)+" "+dto.getStartCell()+"
        // "+(dto.getStartCell()+dto.getCellRange()-1));
        sheet.addMergedRegion(new CellRangeAddress(dto.getStartRow(), dto.getStartRow() + dto.getRowRange() - 1,
                dto.getStartCell(), dto.getStartCell() + dto.getCellRange() - 1));
        HSSFRow headerRow = sheet.getRow(dto.getStartRow());
        if (headerRow == null) {
            headerRow = sheet.createRow(dto.getStartRow());
        }
        // System.out.println(headerRow.getCell(dto.getStartCell()).getStringCellValue());
        HSSFCell headerCell = headerRow.createCell(dto.getStartCell());
        headerCell.setCellValue(dto.getDutySeqName());
        headerCell.setCellStyle(style);
        for (OrgDutySeqTreeDto childDto : dto.getChildrens()) {
            createHeader(sheet, childDto, style, result);
        }
        if (dto.getChildrens() == null || dto.getChildrens().isEmpty()) {
            HSSFRow row = sheet.getRow(dto.getStartRow() + dto.getRowRange());
            if (row == null) {
                row = sheet.createRow(dto.getStartRow() + dto.getRowRange());
            }
            HSSFCell cell = row.createCell(dto.getStartCell());
            cell.setCellValue("职级");
            cell.setCellStyle(style);
            cell = row.createCell(dto.getStartCell() + 1);
            cell.setCellValue("职务");
            cell.setCellStyle(style);
            result.put(dto.getUuid(), dto.getStartCell());
        }
    }

    // private CellRangeAddress getCellRangeAddress(int startRow,int rowRange,int
    // startCell,int cellRange){
    // return new CellRangeAddress(startRow, startRow + rowRange - 1, startCell,
    // startCell + cellRange - 1);
    // }

    /**
     * 创建excel body
     *
     * @param sheet,grades,dtoList,depth
     * @return
     * @author baozh
     * @date 2021/10/28 9:20
     */
    private void createBody(HSSFSheet sheet, List<OrgJobGradeEntity> grades, List<OrgDutyBodyDto> dtoList, int depth,
                            Map<String, Integer> cellMap, HSSFCellStyle style) {
        Map<String, DutyPositionDto> positionMap = new HashMap<>();
        // 处理数据
        for (OrgDutyBodyDto dto : dtoList) {
            String dutySeqUuid = dto.getDutySeqUuid();
            String[] jobGrade = dto.getJobGrade().split(",");
            String[] jobRankName = dto.getJobRankName().split(",");
            for (int i = 0; i < jobGrade.length; i++) {
                if (!positionMap.containsKey(dutySeqUuid + "_" + Integer.parseInt(jobGrade[i]))) {
                    positionMap.put(dutySeqUuid + "_" + Integer.parseInt(jobGrade[i]),
                            new DutyPositionDto(Integer.parseInt(jobGrade[i]), dutySeqUuid));

                }
                DutyPositionDto positionDto = positionMap.get(dutySeqUuid + "_" + Integer.parseInt(jobGrade[i]));
                positionDto.getJobRank().add(jobRankName[i]);
                // 职务列表
                positionDto.getDutyList().add(dto.getDutyName());
            }
        }

        // 写入职等
        Map<Integer, Integer> rowMap = new HashMap<>();
        for (int i = 0; i < grades.size(); i++) {
            int rowNum = i + depth;
            rowMap.put(grades.get(i).getJobGrade(), rowNum);
            HSSFRow row = sheet.createRow(rowNum);
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(style);
            String name = grades.get(i).getJobGradeName();
            cell.setCellValue(name == null ? (grades.get(i).getJobGrade() + "") : name);
        }
        for (Map.Entry<String, DutyPositionDto> positionDtoEntry : positionMap.entrySet()) {
            DutyPositionDto positionDto = positionDtoEntry.getValue();
            HSSFRow row = sheet.getRow(rowMap.get(positionDto.getGrade()));
            HSSFCell cell = row.createCell(cellMap.get(positionDto.getDutySeqUuid()));
            cell.setCellValue(StringUtils.join(positionDto.getJobRank(), ","));
            cell.setCellStyle(style);

            cell = row.createCell(cellMap.get(positionDto.getDutySeqUuid()) + 1);
            cell.setCellValue(StringUtils.join(positionDto.getDutyList(), ","));
            cell.setCellStyle(style);
        }
    }

}
