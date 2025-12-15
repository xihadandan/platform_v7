package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.integration.bean.FtpDataBean;
import com.wellsoft.pt.integration.bean.SynLogBean;
import com.wellsoft.pt.integration.entity.SynDataLog;
import com.wellsoft.pt.integration.ftp.CommonFtp;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataSynService;
import com.wellsoft.pt.integration.service.SynDataLogService;
import com.wellsoft.pt.integration.service.SynDataStaticsService;
import com.wellsoft.pt.integration.support.SynStaticsUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

import static com.wellsoft.pt.integration.support.ExchangeDataResultTransformer.INSTANCE;

@Service
@Transactional
public class SynDataStaticsServiceImpl extends BaseServiceImpl implements SynDataStaticsService {

    // 默认队列大小
    private static int DEFAULT_QUEUE_SIZE = 36;
    // 平台队列
    public static final ArrayDeque<DataRecord> dataRecords = new ArrayDeque<DataRecord>(DEFAULT_QUEUE_SIZE);
    Map<String, Long> stats = new HashMap<String, Long>();
    Map<Long, String> timers = new HashMap<Long, String>();
    @Autowired
    private SynDataLogService syncDataLogService;
    @Autowired
    private ExchangeDataSynService exchangeDataSynService;
    private boolean stroeFlag = false;

    @Override
    public boolean enable(Boolean stroe) {
        return stroeFlag = (stroe == true);
    }

    @Override
    public boolean isEnable() {
        return stroeFlag;
    }

    @Override
    public SynLogBean logView(String category, Integer limit) {
        return null;
    }

    @Override
    public long get(String category) {
        return 0l;
    }

    @Override
    public void increment(String category) {
    }

    @Override
    public void increment(String category, long increment) {
    }

    // @Override
    public long startTimer(String category) {
        long token = System.currentTimeMillis();
        timers.put(token, category);
        return token;
    }

    // @Override
    public void stopTimer(long token) {
        stopTimer(token, null);
    }

    // @Override
    public void stopTimer(long token, Object data) {
        String category = timers.get(token);
        if (stroeFlag == true && category != null) {
            SynDataLog entity = new SynDataLog(category, System.currentTimeMillis() - token, data);
            syncDataLogService.save(entity);
        }
        timers.remove(token);
    }

    /**
     * 开启一个数据库会话,为空是未平台数据库
     *
     * @param bean bean为空时为平台数据库,否则为指定数据库会话
     * @return
     */
    @Override
    public Session openSession(String bean) {
        if (StringUtils.isBlank(bean)) {
            return dao.getSession();
        }
        return getDao(bean).getSession();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Number> dataInQueue() {
        Map<String, Number> dataInQueue = new HashMap<String, Number>();
        // 平台
        Session session = null;
        String dataInQuery = exchangeDataSynService.getDataInQuery();
        String dataOutQuery = exchangeDataSynService.getDataOutQuery();
        String feedInCount = exchangeDataSynService.getFeedInCount();
        String feedOutCount = exchangeDataSynService.getFeedOutCount();
        String feedBackCount = exchangeDataSynService.getFeedBackCount();
        try {
            // 平台
            session = this.openSession(null);
            StringBuilder sqlB = new StringBuilder("select ");
            // 平台还原
            sqlB.append("(").append(dataInQuery).append(" and t.stable_name in (");
            sqlB.append(ExchangeDataSynService.PARAM_ACL).append(")) as acl_back");
            sqlB.append(",(").append(dataInQuery).append(" and t.stable_name in (");
            sqlB.append(ExchangeDataSynService.PARAM_MSG).append(")) as msg_back");
            sqlB.append(",(").append(dataInQuery).append(" and t.stable_name in (");
            sqlB.append(ExchangeDataSynService.PARAM_FLOW).append(")) as flow_back");
            sqlB.append(",(").append(dataInQuery).append(" and t.stable_name in (");
            sqlB.append(ExchangeDataSynService.PARAM_REPO).append(")) as file_back");
            sqlB.append(",(").append(dataInQuery).append(" and t.stable_name not in (");
            sqlB.append(ExchangeDataSynService.PARAM_OTHER).append(")) as data_back");
            // 待反馈
            sqlB.append(",(").append(feedBackCount).append(") as feed_back");

            // 平台流出
            sqlB.append(",(").append(dataOutQuery).append(" and t.stable_name in (");
            sqlB.append(ExchangeDataSynService.PARAM_ACL).append(")) as acl_out");
            sqlB.append(",(").append(dataOutQuery).append(" and t.stable_name in (");
            sqlB.append(ExchangeDataSynService.PARAM_MSG).append(")) as msg_out");
            sqlB.append(",(").append(dataOutQuery).append(" and t.stable_name in (");
            sqlB.append(ExchangeDataSynService.PARAM_FLOW).append(")) as flow_out");
            sqlB.append(",(").append(dataOutQuery).append(" and t.stable_name in (");
            sqlB.append(ExchangeDataSynService.PARAM_REPO).append(")) as file_out");
            sqlB.append(",(").append(dataOutQuery).append(" and t.stable_name not in (");
            sqlB.append(ExchangeDataSynService.PARAM_OTHER).append(")) as data_out");
            // 反馈流出
            sqlB.append(",(").append(feedOutCount).append(") as feed_out");
            sqlB.append(" from dual");
            Query query = session.createSQLQuery(sqlB.toString()).setResultTransformer(INSTANCE);
            dataInQueue.putAll((Map<String, Number>) query.list().get(0));

            // 前置机流入
            session = this.openSession(ExchangeConfig.NEWAPASIN);
            StringBuilder sqlB2 = new StringBuilder("select ");
            sqlB2.append("(").append(dataInQuery).append(" and t.stable_name in (");
            sqlB2.append(ExchangeDataSynService.PARAM_ACL).append(")) as acl_in");
            sqlB2.append(",(").append(dataInQuery).append(" and t.stable_name in (");
            sqlB2.append(ExchangeDataSynService.PARAM_MSG).append(")) as msg_in");
            sqlB2.append(",(").append(dataInQuery).append(" and t.stable_name in (");
            sqlB2.append(ExchangeDataSynService.PARAM_FLOW).append(")) as flow_in");
            sqlB2.append(",(").append(dataInQuery).append(" and t.stable_name in (");
            sqlB2.append(ExchangeDataSynService.PARAM_REPO).append(")) as file_in");
            sqlB2.append(",(").append(dataInQuery).append(" and t.stable_name not in (");
            sqlB2.append(ExchangeDataSynService.PARAM_OTHER).append(")) as data_in");
            // 反馈流入
            sqlB2.append(",(").append(feedInCount).append(") as feed_in");
            sqlB2.append(" from dual");
            Query query2 = session.createSQLQuery(sqlB2.toString()).setResultTransformer(INSTANCE);
            dataInQueue.putAll((Map<String, Number>) query2.list().get(0));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        return dataInQueue;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Number> dataSpeed() {
        Map<String, Number> dataInSize = new HashMap<String, Number>();
        // 平台
        Session session = null;
        try {
            // 平台
            session = this.openSession(null);
            StringBuilder sqlB = new StringBuilder("with dview as (");
            sqlB.append("select t.*,cast(t.backup_time as date) - cast(t.create_time as date) as bcost/*backup cost*/ from tig_table_data t where t.status = 4");
            sqlB.append(") select (select count(1) from dview tt where /*bcost >= 0 and*/ bcost < 60/86400) as cost0_1");
            sqlB.append(",(select count(1) from dview tt where bcost >= 60/86400 and bcost < 120/86400) as cost1_2");
            sqlB.append(",(select count(1) from dview tt where bcost >= 120/86400 and bcost < 240/86400) as cost2_4");
            sqlB.append(",(select count(1) from dview tt where bcost >= 240/86400 and bcost < 480/86400) as cost4_8");
            sqlB.append(",(select count(1) from dview tt where bcost >= 480/86400 and bcost < 960/86400) as cost8_16");
            sqlB.append(",(select count(1) from dview tt where bcost >= 960/86400 and bcost < 1920/86400) as cost16_32");
            sqlB.append(",(select count(1) from dview tt where bcost >= 1920/86400 and bcost < 3840/86400) as cost32_64");
            sqlB.append(",(select count(1) from dview tt where bcost >= 3840/86400 and bcost < 7680/86400) as cost64_128");
            sqlB.append(",(select count(1) from dview tt where bcost >= 7680/86400) as cost128_0 from dual");
            Query query = session.createSQLQuery(sqlB.toString()).setResultTransformer(INSTANCE);
            dataInSize.putAll((Map<String, Number>) query.list().get(0));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        return dataInSize;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Number> updateRecord() {
        Map<String, Number> dataInSize = new HashMap<String, Number>();
        // 平台
        Session session = null;
        try {// 平台
            session = this.openSession(null);
            StringBuilder sqlB = new StringBuilder("select ");
            sqlB.append("(").append(QUERY_DATA_SIZE).append(") as pt_size");
            sqlB.append(" from dual");
            Query query = session.createSQLQuery(sqlB.toString()).setResultTransformer(INSTANCE);
            dataInSize.putAll((Map<String, Number>) query.list().get(0));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        try {// 前置机流入
            session = this.openSession(ExchangeConfig.NEWAPASIN);
            StringBuilder sqlB2 = new StringBuilder("select ");
            sqlB2.append("(").append(QUERY_DATA_SIZE).append(") as in_size");
            sqlB2.append(" from dual");
            Query query2 = session.createSQLQuery(sqlB2.toString()).setResultTransformer(INSTANCE);
            dataInSize.putAll((Map<String, Number>) query2.list().get(0));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        try {// 前置机流出
            session = this.openSession(ExchangeConfig.NEWAPASOUT);
            StringBuilder sqlB3 = new StringBuilder("select ");
            sqlB3.append("(").append(QUERY_DATA_SIZE).append(") as out_size");
            sqlB3.append(" from dual");
            Query query3 = session.createSQLQuery(sqlB3.toString()).setResultTransformer(INSTANCE);
            dataInSize.putAll((Map<String, Number>) query3.list().get(0));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        int ptSize = SynStaticsUtils.getIntValue(dataInSize.get("pt_size"));
        int inSize = SynStaticsUtils.getIntValue(dataInSize.get("in_size"));
        int outSize = SynStaticsUtils.getIntValue(dataInSize.get("out_size"));
        DataRecord logRecord = new DataRecord(new Date(), ptSize, inSize, outSize);
        while (dataRecords.size() >= DEFAULT_QUEUE_SIZE) {
            dataRecords.poll();
        }
        dataRecords.offer(logRecord);
        return dataInSize;
    }

    @Override
    public Collection<DataRecord> dataRecord() {
        return dataRecords;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.SynDataStaticsService#statistics()
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> statistics() {
        Map<String, Object> allInfo = new HashMap<String, Object>();
        // 网络环境
        allInfo.put(DIRECTION_FIELD, exchangeDataSynService.getDirection());
        // 平台数据库统计信息
        Map<String, Object> info = new HashMap<String, Object>();
        info.put(FILE_SYSTEM_STATUS, false);
        info.put(DATABASE_SESSION_STATUS, true);
        Session session = null;
        try {
            session = this.openSession(null);
            // 表数据量统计
            Query query = session.createSQLQuery(TABLE_DATA_COUNT).setResultTransformer(INSTANCE);
            Map<String, Object> tableDataCount = (Map<String, Object>) query.list().get(0);
            info.put(TABLE_DATA_COUNT_FIELD, tableDataCount);
            // 数据状态分布
            Query query2 = session.createSQLQuery(DATA_STATUS_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> dataStatusGroup = (List<Map<String, Object>>) query2.list();
            info.put(DATA_STATUS_GROUP_FIELD, dataStatusGroup);
            // 大字段数据状态分布
            Query query3 = session.createSQLQuery(CLOB_STATUS_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> clobStatusGroup = (List<Map<String, Object>>) query3.list();
            info.put(CLOB_STATUS_GROUP_FIELD, clobStatusGroup);
            // 失败数据的表分布
            Query query4 = session.createSQLQuery(FAIL_DATA_TABLE_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> failDataTableGroup = (List<Map<String, Object>>) query4.list();
            info.put(FAIL_DATA_TABLE_GROUP_FIELD, failDataTableGroup);
            // 失败大字段的表分布
            Query query5 = session.createSQLQuery(FAIL_CLOB_TABLE_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> failClobTableGroup = (List<Map<String, Object>>) query5.list();
            info.put(FAIL_CLOB_TABLE_GROUP_FIELD, failClobTableGroup);
        } catch (Exception ex) {
            info.put(DATABASE_SESSION_STATUS, false);
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        // 查看本地磁盘空间
        try {
            File dataDir = new File(Config.APP_DATA_DIR);
            info.put(FREE_SPACE, FileUtils.byteCountToDisplaySize(dataDir.getFreeSpace()));
            info.put(TOTAL_SPACE, FileUtils.byteCountToDisplaySize(dataDir.getTotalSpace()));
            info.put(USED_SPACE, FileUtils.byteCountToDisplaySize(dataDir.getTotalSpace() - dataDir.getFreeSpace()));
            info.put(FILE_SYSTEM_STATUS, true);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        allInfo.put(ExchangeConfig.SOURCE_PLATFORM, info);

        // 光闸流出统计信息
        Map<String, Object> info2 = new HashMap<String, Object>();
        info2.put(FILE_SYSTEM_STATUS, false);
        info2.put(DATABASE_SESSION_STATUS, true);
        Session session2 = null;
        try {
            session2 = this.openSession(ExchangeConfig.NEWAPASOUT);
            // 表数据量统计
            Query query = session2.createSQLQuery(TABLE_DATA_COUNT).setResultTransformer(INSTANCE);
            Map<String, Object> tableDataCount = (Map<String, Object>) query.list().get(0);
            info2.put(TABLE_DATA_COUNT_FIELD, tableDataCount);
            // 数据状态分布
            Query query2 = session2.createSQLQuery(DATA_STATUS_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> dataStatusGroup = (List<Map<String, Object>>) query2.list();
            info2.put(DATA_STATUS_GROUP_FIELD, dataStatusGroup);
            // 大字段数据状态分布
            Query query3 = session2.createSQLQuery(CLOB_STATUS_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> clobStatusGroup = (List<Map<String, Object>>) query3.list();
            info2.put(CLOB_STATUS_GROUP_FIELD, clobStatusGroup);
            // 失败数据的表分布
            Query query4 = session2.createSQLQuery(FAIL_DATA_TABLE_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> failDataTableGroup = (List<Map<String, Object>>) query4.list();
            info2.put(FAIL_DATA_TABLE_GROUP_FIELD, failDataTableGroup);
            // 失败大字段的表分布
            Query query5 = session2.createSQLQuery(FAIL_CLOB_TABLE_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> failClobTableGroup = (List<Map<String, Object>>) query5.list();
            info2.put(FAIL_CLOB_TABLE_GROUP_FIELD, failClobTableGroup);
        } catch (Exception ex) {
            info2.put(DATABASE_SESSION_STATUS, false);
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        // 查看流出光闸FTP状态
        FtpDataBean outFtpDataBean = exchangeDataSynService.getOutFtpDataBean();
        CommonFtp outFtp = new CommonFtp(outFtpDataBean.getFtp_host(), outFtpDataBean.getFtp_post(),
                outFtpDataBean.getFtp_user_name(), outFtpDataBean.getFtp_pass_word());
        try {
            if (!outFtp.isConnected()) {
                outFtp.connect();
            }
            FTPFile[] files = outFtp.listFiles(FTP_BACK_DIR);
            if (files == null) {
                info2.put(USED_SPACE, 0);
            } else {
                info2.put(USED_SPACE, files.length);
            }
            info2.put(FILE_SYSTEM_STATUS, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            outFtp.disconnect();
        }
        allInfo.put(ExchangeConfig.NEWAPASOUT, info2);

        // 光闸流入统计信息
        Map<String, Object> info3 = new HashMap<String, Object>();
        info3.put(FILE_SYSTEM_STATUS, false);
        info3.put(DATABASE_SESSION_STATUS, true);
        Session session3 = null;
        try {
            session3 = this.openSession(ExchangeConfig.NEWAPASIN);
            // 表数据量统计
            Query query = session3.createSQLQuery(TABLE_DATA_COUNT).setResultTransformer(INSTANCE);
            Map<String, Object> tableDataCount = (Map<String, Object>) query.list().get(0);
            info3.put(TABLE_DATA_COUNT_FIELD, tableDataCount);
            // 数据状态分布
            Query query2 = session3.createSQLQuery(DATA_STATUS_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> dataStatusGroup = (List<Map<String, Object>>) query2.list();
            info3.put(DATA_STATUS_GROUP_FIELD, dataStatusGroup);
            // 大字段数据状态分布
            Query query3 = session3.createSQLQuery(CLOB_STATUS_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> clobStatusGroup = (List<Map<String, Object>>) query3.list();
            info3.put(CLOB_STATUS_GROUP_FIELD, clobStatusGroup);
            // 失败数据的表分布
            Query query4 = session3.createSQLQuery(FAIL_DATA_TABLE_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> failDataTableGroup = (List<Map<String, Object>>) query4.list();
            info3.put(FAIL_DATA_TABLE_GROUP_FIELD, failDataTableGroup);
            // 失败大字段的表分布
            Query query5 = session3.createSQLQuery(FAIL_CLOB_TABLE_GROUP).setResultTransformer(INSTANCE);
            List<Map<String, Object>> failClobTableGroup = (List<Map<String, Object>>) query5.list();
            info3.put(FAIL_CLOB_TABLE_GROUP_FIELD, failClobTableGroup);

        } catch (Exception ex) {
            info3.put(DATABASE_SESSION_STATUS, false);
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        // 查看流出光闸FTP状态
        FtpDataBean inFtpDataBean = exchangeDataSynService.getInFtpDataBean();
        CommonFtp inFtp = new CommonFtp(inFtpDataBean.getFtp_host(), inFtpDataBean.getFtp_post(),
                inFtpDataBean.getFtp_user_name(), inFtpDataBean.getFtp_pass_word());
        try {
            if (!inFtp.isConnected()) {
                inFtp.connect();
            }
            FTPFile[] files = inFtp.listFiles(FTP_ROOT_DIR);
            if (files == null) {
                info3.put(USED_SPACE, 0);
            } else {
                info3.put(USED_SPACE, files.length);
            }
            info3.put(FILE_SYSTEM_STATUS, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            inFtp.disconnect();
        }
        allInfo.put(ExchangeConfig.NEWAPASIN, info3);

        return allInfo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> queryDataList(String dao, Integer status, String stable, String suuid) {
        Session session = null;
        try {
            session = this.openSession(dao);
            String sqlQuery = StringUtils.isBlank(stable) ? TABLE_DATA_LIST : TABLE_DATA_LIST2;
            if (StringUtils.isNotBlank(suuid)) {
                sqlQuery = TABLE_DATA_LIST3;
            }
            Query query = session.createSQLQuery(sqlQuery).setResultTransformer(INSTANCE);
            if (StringUtils.isNotBlank(suuid)) {
                query.setString("suuid", suuid);
            } else {
                query.setInteger("status", status == null ? 3 : status);// 默认查询失败的数据
                if (StringUtils.isNotBlank(stable)) {
                    query.setString("stable_name", stable);
                }
            }
            query.setMaxResults(MAX_RESULTS);
            return query.list();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> queryClobList(String dao, Integer status, String stable) {
        Session session = null;
        try {
            session = this.openSession(dao);
            String sqlQuery = StringUtils.isBlank(stable) ? TABLE_CLOB_LIST : TABLE_CLOB_LIST2;
            Query query = session.createSQLQuery(sqlQuery).setResultTransformer(INSTANCE);
            query.setInteger("status", status == null ? 3 : status);// 默认查询失败的数据
            if (StringUtils.isNotBlank(stable)) {
                query.setString("stable_name", stable);
            }
            query.setMaxResults(MAX_RESULTS);
            return query.list();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> queryDataDetail(String dao, String tig_owner_uuid) {
        Session session = null;
        try {
            session = this.openSession(dao);
            Query query = session.createSQLQuery(TABLE_DATA_DETAIL).setResultTransformer(INSTANCE);
            query.setString("tig_owner_uuid", tig_owner_uuid);// 默认查询失败的数据
            query.setMaxResults(MAX_RESULTS * 2);
            return query.list();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
        }
        return null;
    }

    @Override
    public void resetData(String dao, Collection<String> uuids, Integer status) {
        Session session = null;
        try {
            session = this.openSession(dao);
            Query query = session.createSQLQuery(RESET_TABLE_DATA).setInteger("statue", status);
            query.setParameterList("uuids", uuids);
            query.executeUpdate();
            logger.info("resetData size" + query.executeUpdate());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("重置状态[" + status + "]失败", ex);
        } finally {
        }
    }

    @Override
    public void resetClob(String dao, Collection<String> uuids, Integer status) {
        Session session = null;
        try {
            session = this.openSession(dao);
            Query query = session.createSQLQuery(RESET_TABLE_CLOB).setInteger("statue", status);
            query.setParameterList("uuids", uuids);
            logger.info("resetClob size" + query.executeUpdate());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("重置状态[" + status + "]失败", ex);
        } finally {
        }
    }

    /**
     * TODO 复用同步服务中清理逻辑
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.SynDataStaticsService#delData(java.lang.String, java.util.Collection)
     */
    @Override
    public void delData(String dao, Collection<String> uuids) {
        try {
            exchangeDataSynService.deleteTableData(uuids);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("删除[" + uuids.size() + "]失败", ex);
        }
    }

    /**
     * TODO 复用同步服务中清理逻辑
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.SynDataStaticsService#delClob(java.lang.String, java.util.Collection)
     */
    @Override
    public void delClob(String dao, Collection<String> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return;
        }
        Session session = null;
        try {
            session = this.openSession(dao);
            Query query = session.createSQLQuery(DEL_TABLE_CLOB).setParameterList("uuids", uuids);
            query.executeUpdate();
            // 清理流出前置机
            session = this.openSession(ExchangeConfig.NEWAPASOUT);
            query = session.createSQLQuery(DEL_TABLE_CLOB).setParameterList("uuids", uuids);
            query.executeUpdate();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("删除[" + uuids.size() + "]失败", ex);
        } finally {
        }
    }

}
