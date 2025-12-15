/*
 * @(#)2018年10月10日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author {Svn璐﹀彿}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年10月10日.1	{Svn璐﹀彿}		2018年10月10日		Create
 * </pre>
 * @date 2018年10月10日
 */
public class ImportNode implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1711617498371269681L;
    private MultiOrgVersion ver; // 节点对应的版本
    private String unitName; // 单位名称
    private String deptPath; // 部门路径
    private String jobName; // 职位名称
    private String dutyName; // 职务名称
    private String typeName; // 类型名称
    private String code; // 编码
    //
    private String nodeName; // 节点名称
    private String nodeType; // 节点类型
    private String eleId; // 节点ID
    private String idPath; // 节点ID 全路径
    private String namePath; // 节点名称 全路径

    public ImportNode() {

    }

    public ImportNode(MultiOrgVersion ver, String namePath) {
        this.ver = ver;
        this.namePath = namePath;
        this.nodeName = this.computeNodeName();
        String[] names = namePath.split("/");
        // 第一个是组织版本名称，所以 length > 1
        if (names.length > 1) {
            this.unitName = names[1];
            if (names.length == 2) { // 只有单位
                this.nodeType = IdPrefix.BUSINESS_UNIT.getValue();
                this.typeName = "单位";
            } else { // 存在部门路径，是个部门节点
                this.nodeType = IdPrefix.DEPARTMENT.getValue();
                this.typeName = "部门";
                this.deptPath = StringUtils.join(names, "/", 2, names.length);
            }
        } else {
            throw new RuntimeException("数据异常,namePath=" + namePath);
        }

    }

    public ImportNode(MultiOrgVersion ver, Row row) {
        Cell unitCell = row.getCell(0);
        Cell deptPathCell = row.getCell(1);
        Cell jobNameCell = row.getCell(2);
        Cell dutyNameCell = row.getCell(3);
        Cell codeCell = row.getCell(4);
        Cell typeCell = row.getCell(5);
        this.ver = ver;
        this.unitName = unitCell.getStringCellValue();
        // 统一格式，
        String deptPath = deptPathCell == null ? null : deptPathCell.getStringCellValue();
        if (deptPath != null && deptPath.startsWith("/")) {
            deptPath = deptPath.substring(1);
        }
        this.deptPath = deptPath;
        this.jobName = jobNameCell == null ? null : jobNameCell.getStringCellValue();
        this.dutyName = dutyNameCell == null ? null : dutyNameCell.getStringCellValue();
        DecimalFormat df = new DecimalFormat("0");
        try {
            this.code = codeCell == null ? null : codeCell.getStringCellValue();
        } catch (Exception e) {
            this.code = codeCell == null ? null : df.format(codeCell.getNumericCellValue());
        }
        this.typeName = typeCell == null ? null : typeCell.getStringCellValue();
        // 根据 row 的值，计算节点的一些基本属性【类型，名称, 名称全路径】
        this.nodeType = this.computeNodeType();
        // 计算名称全路径
        this.namePath = this.computeNamePath();
        this.nodeName = this.computeNodeName();
    }

    public static String getNodeTypeFromName(String name) {
        if ("部门".equals(name)) {
            return IdPrefix.DEPARTMENT.getValue();
        } else if ("职位".equals(name)) {
            return IdPrefix.JOB.getValue();
        } else if ("单位".equals(name)) {
            return IdPrefix.BUSINESS_UNIT.getValue();
        } else if ("节点".equals(name)) {
            return IdPrefix.ORG.getValue();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String tt = "/dfasf";
        String t2 = "aaaa";
        System.out.println(tt.split("/").length);
        System.out.println(t2.split("/").length);
        String[] a = new String[]{"1", "2", "fdsaf", "3"};
        String as = StringUtils.join(a, "/", 2, 4);
        System.out.println("as=" + as + "----");

    }

    // 先判断是否职位节点，在判断是否部门节点，在判断是否单位节点，如果都不是，则默认为组织节点
    private String computeNodeType() {
        // 判断是否职位节点,
        if (StringUtils.isNotBlank(getJobName())) {
            return IdPrefix.JOB.getValue();
        } else {
            // 部门路径不为空，说明是个部门节点
            if (StringUtils.isNotBlank(deptPath)) {
                // 判断是否有设置 节点字段来调整节点类型，如果有，以设置为准
                if (StringUtils.isBlank(typeName)) {
                    return IdPrefix.DEPARTMENT.getValue();
                } else {
                    return getNodeTypeFromName(typeName);
                }
            } else {
                // 职位为空，部门为空，说明是个单位节点
                // 同样判断是否有设置节点字段来调整类型
                if (StringUtils.isBlank(typeName)) {
                    return IdPrefix.BUSINESS_UNIT.getValue();
                } else {
                    return getNodeTypeFromName(typeName);
                }
            }
        }
    }

    // 计算节点名称全路径
    private String computeNamePath() {
        List<String> path = Lists.newArrayList();
        path.add(ver.getName());
        path.add(unitName);
        if (StringUtils.isNotBlank(deptPath)) {
            path.add(deptPath);
        }
        if (StringUtils.isNotBlank(jobName)) {
            path.add(jobName);
        }
        return StringUtils.join(path, "/");
    }

    // 计算节点名称
    private String computeNodeName() {
        if (StringUtils.isNotBlank(this.namePath)) {
            String[] names = this.namePath.split("/");
            // 第一个名称是组织版本名称，不能算是节点名称，所以 length > 1
            if (names.length > 1) {
                // 最后一个字段就是节点名称
                return names[names.length - 1];
            }
        }
        return null;
    }

    // 获取上级节点名称全路径
    public String getParentNamePath() {
        if (StringUtils.isNotBlank(this.namePath)) {
            int pos = this.namePath.lastIndexOf("/");
            if (pos > 0) {
                return this.namePath.substring(0, pos);
            }
        }
        return null;
    }

    // 获取上级节点名称全路径
    public String getParentIdPath() {
        if (StringUtils.isNotBlank(this.idPath)) {
            int pos = this.idPath.lastIndexOf("_");
            if (pos > 0) {
                return this.idPath.substring(0, pos);
            }
        }
        return null;
    }

    /**
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @param unitName 要设置的unitName
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * @return the deptPath
     */
    public String getDeptPath() {
        return deptPath;
    }

    /**
     * @param deptPath 要设置的deptPath
     */
    public void setDeptPath(String deptPath) {
        this.deptPath = deptPath;
    }

    /**
     * @return the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName 要设置的jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return the dutyName
     */
    public String getDutyName() {
        return dutyName;
    }

    /**
     * @param dutyName 要设置的dutyName
     */
    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName 要设置的typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName 要设置的nodeName
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the nodeType
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType 要设置的nodeType
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return the eleId
     */
    public String getEleId() {
        return eleId;
    }

    /**
     * @param eleId 要设置的eleId
     */
    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    /**
     * @return the idPath
     */
    public String getIdPath() {
        return idPath;
    }

    /**
     * @param idPath 要设置的idPath
     */
    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    /**
     * @return the namePath
     */
    public String getNamePath() {
        return namePath;
    }

    /**
     * @param namePath 要设置的namePath
     */
    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }

    public String toString() {
        return this.namePath + "," + this.nodeType;
    }
}
