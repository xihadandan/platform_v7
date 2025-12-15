package com.wellsoft.pt.mt.wizard;

/**
 * Description: 向导处理过程所需要的数据，如租户信息，界面收集到的配置信息等
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月10日.1	linz		2016年3月10日		Create
 * </pre>
 * @date 2016年3月10日
 */
public class WizardContext {

    /**
     * 租户uuid
     */
    private String uuid;
    /**
     * 租户名称
     **/
    private String name;
    /**
     * 租户编码
     **/
    private String code;
    /**
     * 组织机构
     **/
    private String orgName;
    /**
     * 数据库用户名
     **/
    private String account;
    /**
     * 数据库密码
     **/
    private String password;
    /**
     * DBA账号
     **/
    private String dbaUser;
    /**
     * DBA密码
     **/
    private String dbaPassword;
    /**
     * 批次号
     **/
    private int batchNo;
    /**
     * 租户模板UUID
     */
    private String templeUuid;
    /**
     * 业务模块
     */
    private String moduleUuids;
    /**
     * @default ORCL
     */
    private String jdbcDatabaseName;
    /**
     * 数据库地址
     */
    private String jdbcServer;
    /**
     * 数据库类型
     */
    private String JdbcType;

    private int jdbcPort;

    private String dataType;

    private String repoFileName;

    private String repoFileUuid;

    private String repoFileNames;

    private String repoFileUuids;

    private String upgradeBatchUuid;
    private Boolean isUpdate = false;

    public String getUpgradeBatchUuid() {
        return upgradeBatchUuid;
    }

    public void setUpgradeBatchUuid(String upgradeBatchUuid) {
        this.upgradeBatchUuid = upgradeBatchUuid;
    }

    public String getRepoFileNames() {
        return repoFileNames;
    }

    public void setRepoFileNames(String repoFileNames) {
        this.repoFileNames = repoFileNames;
    }

    public String getRepoFileUuids() {
        return repoFileUuids;
    }

    public void setRepoFileUuids(String repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
    }

    public Boolean getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRepoFileName() {
        return repoFileName;
    }

    public void setRepoFileName(String repoFileName) {
        this.repoFileName = repoFileName;
    }

    public String getRepoFileUuid() {
        return repoFileUuid;
    }

    public void setRepoFileUuid(String repoFileUuid) {
        this.repoFileUuid = repoFileUuid;
    }

    public int getJdbcPort() {
        return jdbcPort;
    }

    public void setJdbcPort(int jdbcPort) {
        this.jdbcPort = jdbcPort;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbaUser() {
        return dbaUser;
    }

    public void setDbaUser(String dbaUser) {
        this.dbaUser = dbaUser;
    }

    public String getDbaPassword() {
        return dbaPassword;
    }

    public void setDbaPassword(String dbaPassword) {
        this.dbaPassword = dbaPassword;
    }

    public int getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(int batchNo) {
        this.batchNo = batchNo;
    }

    public String getTempleUuid() {
        return templeUuid;
    }

    public void setTempleUuid(String templeUuid) {
        this.templeUuid = templeUuid;
    }

    public String getModuleUuids() {
        return moduleUuids;
    }

    public void setModuleUuids(String moduleUuids) {
        this.moduleUuids = moduleUuids;
    }

    public String getJdbcDatabaseName() {
        return jdbcDatabaseName;
    }

    public void setJdbcDatabaseName(String jdbcDatabaseName) {
        this.jdbcDatabaseName = jdbcDatabaseName;
    }

    public String getJdbcServer() {
        return jdbcServer;
    }

    public void setJdbcServer(String jdbcServer) {
        this.jdbcServer = jdbcServer;
    }

    public String getJdbcType() {
        return JdbcType;
    }

    public void setJdbcType(String jdbcType) {
        JdbcType = jdbcType;
    }
}
