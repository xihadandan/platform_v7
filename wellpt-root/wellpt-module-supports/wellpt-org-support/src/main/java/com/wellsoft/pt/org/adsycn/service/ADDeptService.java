package com.wellsoft.pt.org.adsycn.service;

import com.wellsoft.pt.org.adsycn.vo.ADDept;

/**
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-10-13.1  zhengky	2014-10-13	  Create
 * </pre>
 * @date 2014-10-13
 */
public interface ADDeptService {

    /**
     * 添加部门
     *
     * @param adDept
     */
    public void add(ADDept adDept);

    /**
     * 删除部门(子节点为空才能删除)
     *
     * @param adDept
     * @param
     */
    public void delete(ADDept adDept);

    /**
     * 校验部门是否存在AD中
     *
     * @param addept
     * @return
     */
    public boolean checkIsExist(ADDept addept);

    public boolean checkIsExistByPath(String deptPath);

    public void renameDn(ADDept oldadDept, ADDept newadDept);

    public String getDnByAdDept(ADDept adDept);

    /**
     * liyb
     * 实现域名同步
     */
    public void adSync();
}
