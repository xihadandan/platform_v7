package com.wellsoft.pt.org.service;

public interface HuoQuLeaderService {

    /**
     * 获取上级领导姓名
     *
     * @param userId
     * @return
     */
    public String getSuperLeadName(String userId);

    /**
     * 获取职位汇报对象
     *
     * @param userId
     * @return
     */
    public String getReportLeaderName(String userId);

    /**
     * 获取所有领导姓名
     *
     * @param userId
     * @return
     */
    public String getAllLeadName(String userId);
}
