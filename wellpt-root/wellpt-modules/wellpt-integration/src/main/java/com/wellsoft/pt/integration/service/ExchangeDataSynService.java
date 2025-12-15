package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.bean.FtpDataBean;
import com.wellsoft.pt.integration.entity.SysProperties;

import java.util.Collection;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-12-8.1	ruanhg		2014-12-8		Create
 * </pre>
 * @date 2014-12-8
 */
public interface ExchangeDataSynService {

    public static final String TIG_TABLE_DATA = "tig_table_data";

    public static final String TIG_COLUMN_DATA = "tig_column_data";

    public static final String TIG_COLUMN_CLOB = "tig_column_clob";

    public static final String CLOB_EMPTY_FLAG = "nilClob";

    public static final String FILE_EMPTY_FLAG = "nilFile";

    public static final String FILE_SIZE_MAGIC = "filesize:";// 文件传输magic

    public static final long LARGE_FILE_BYTES = 1024 * 1024 * 64; // 64M

    public static final long LARGE_CLOB_BYTES = 1024 * 1024 / 4; // 0.25M

    public static final long LARGE_OBJECT_BYTES = 1024 * 1024 * 8; // 8M

    public static final long DEFAULT_HEARTBEAT_TIME = 1000 * 60 * 5;// 5分钟心跳,TODO与定时器的执行任务相关

    // TODO 关联其他
    public static final String PARAM_REPO = "'REPO_FILE'";

    public static final String PARAM_MSG = "'MSG_MESSAGE_OUTBOX','MSG_MESSAGE_INBOX'";

    public static final String PARAM_ACL = "'ACL_CLASS','ACL_SID','ACL_ENTRY','ACL_SID_MEMBER','ACL_OBJECT_IDENTITY'";

    public static final String PARAM_FLOW = "'WF_DEF_CATEGORY','WF_DEF_OPINION_CATEGORY','WF_DEF_OPINION','WF_FLOW_SCHEMA','WF_FLOW_DEFINITION','WF_FLOW_INSTANCE','WF_TASK_INSTANCE','WF_TASK_COUNTER_SIGN','WF_TASK_IDENTITY','WF_TASK_SUB_FLOW_RELATION','WF_TASK_TIMER','WF_TASK_TIMER_LOG','WF_TASK_TIMER_USER','WF_TASK_TRANSFER'";

    public static final String PARAM_OTHER = PARAM_REPO + "," + PARAM_MSG + "," + PARAM_ACL + "," + PARAM_FLOW;

    public static final String GROUP_REPO = "FILE";

    public static final String GROUP_MSG = "MSG";

    public static final String GROUP_ACL = "ACL";

    public static final String GROUP_FLOW = "FLOW";

    public static final String GROUP_OTHER = "OTHER";

    public boolean beat();

    public boolean synOutData(Map<String, SysProperties> sysPropertiess, String params, String paramGroup)
            throws Exception;

    public boolean synOutData(Map<String, SysProperties> sysPropertiess, String params, boolean withClob,
                              String paramGroup) throws Exception;

    public boolean synInData(Map<String, SysProperties> sysPropertiess, String params, String paramGroup)
            throws Exception;

    public boolean synInData(Map<String, SysProperties> sysPropertiess, String params, boolean withClob,
                             String paramGroup) throws Exception;

    public boolean synBackData(Map<String, SysProperties> sysPropertiessDeprecated, String params) throws Exception;

    public boolean synBackData(Map<String, SysProperties> sysPropertiessDeprecated, String params, boolean withClob)
            throws Exception;

    public boolean synOutDataFeedback(Map<String, SysProperties> sysPropertiess) throws Exception;

    public boolean synInDataFeedback(Map<String, SysProperties> sysPropertiess) throws Exception;

    public void updateCursor() throws Exception;

    boolean clearSynDataAllInOne(Map<String, SysProperties> sysPropertiess) throws Exception;

    boolean deleteTableData(Collection<String> tigUuids) throws Exception;

    /**
     * @param strictSort
     */
    public void setStrictSort(boolean strictSort);

    /**
     * @return the outFtpDataBean
     */
    public FtpDataBean getOutFtpDataBean();

    /**
     * @return the inFtpDataBean
     */
    public FtpDataBean getInFtpDataBean();

    /**
     * @return the dataNum
     */
    public int getDataNum();

    /**
     * @return the direction
     */
    public int getDirection();

    /**
     * @return the dataInQuery
     */
    public String getDataInQuery();

    /**
     * @return the dataOutQuery
     */
    public String getDataOutQuery();

    /**
     * @return the feedInCount
     */
    public String getFeedInCount();

    /**
     * @return the feedOutCount
     */
    public String getFeedOutCount();

    /**
     * @return the feedBackCount
     */
    public String getFeedBackCount();

    /**
     * @return the beatContext
     */
    public Map<String, Object> getBeatContext();

    /**
     * @return the preContext
     */
    public Map<String, String> getPreContext();
}
