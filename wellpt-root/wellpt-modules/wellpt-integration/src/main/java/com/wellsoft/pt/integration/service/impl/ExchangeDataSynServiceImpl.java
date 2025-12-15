package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.integration.bean.FtpDataBean;
import com.wellsoft.pt.integration.entity.SysProperties;
import com.wellsoft.pt.integration.ftp.CommonFtp;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataConfigService;
import com.wellsoft.pt.integration.service.ExchangeDataSynService;
import com.wellsoft.pt.integration.support.SynOfflineEvent;
import com.wellsoft.pt.integration.support.SynOnlineEvent;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import java.io.*;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.wellsoft.pt.integration.support.ExchangeDataResultTransformer.INSTANCE;

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
@Service
@Transactional
public class ExchangeDataSynServiceImpl extends BaseServiceImpl implements ExchangeDataSynService {

    protected int interval = 30;

    // @Autowired //这个类注解一直有问题，不知什么原因，所以直接调用exchangeDataConfigService --zyguo
    // private ExchangedataApiFacade exchangedataApiFacade;
    protected int dataNum = 50;
    protected int clearInterval = 300;
    protected String feedInQuery;
    protected String feedOutQuery;
    protected String feedInCount;
    protected String feedOutCount;
    protected String feedBackCount;
    protected Boolean checkUnimas;
    protected String queryUnimasQueue;
    protected String deleteUnimasQueue;
    protected String dataInQuery;
    protected String dataOutQuery;
    protected FtpDataBean outFtpDataBean = new FtpDataBean();
    protected FtpDataBean inFtpDataBean = new FtpDataBean();
    protected int direction = 0;// 1内/2外
    // heartbeat
    protected long timeout = DEFAULT_HEARTBEAT_TIME;
    protected long lastBeat;// 最后心跳时间
    protected long lastFeed;// 最后反馈时间
    protected long currCursor;// 清理游标(谁生成的数据,谁清理)
    protected long feedCursor;// 反馈清理游标(确认清理游标)
    protected boolean offline = true;// 默认离线
    protected String sqlData, sqlData2, sqlClob;
    protected String queryMaxOrderId, queryBeat, insertBeat, updateBeat, deleteBeat;
    // 顺序上下文
    protected boolean strictSort = true; // 默认启用严格顺序
    protected Queue<String> lossQueue = new ArrayBlockingQueue<String>(1024);
    protected Map<String, String> preContext = new ConcurrentHashMap<String, String>();
    // protected String tableDataInsertSql;
    // protected String columnDataInsertSql;
    // protected String columnClobInsertSql;
    File exportFolder = null;
    Map<String, Object> beatContext = new HashMap<String, Object>();// 心跳
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private ExchangeDataConfigService exchangeDataConfigService;

    public static String getClobSuuid(String tig_owner_uuid, String tig_column_name) {
        return tig_owner_uuid + "_" + tig_column_name;
    }

    public static boolean isLargeData(long objectLength) throws SQLException {
        return objectLength > LARGE_OBJECT_BYTES;
    }

    public static boolean isLargeClob(Clob clob) throws SQLException {
        return clob != null && clob.length() > LARGE_CLOB_BYTES;
    }

    public static boolean isLargeFile(File file) {
        return file != null && file.length() > LARGE_FILE_BYTES;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Integer fff = 4;
        System.out.println(fff.toString());

        // ---------------------------------
        int direction = 2;
        StringBuffer param = new StringBuffer(" and o.stable_name not in (");
        param.append(ExchangeDataSynService.PARAM_MSG).append(",");
        param.append(ExchangeDataSynService.PARAM_ACL).append(",");
        param.append(ExchangeDataSynService.PARAM_REPO).append(",");
        param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
        String params = param.toString();

        // first with as cte1
        StringBuilder sqlB = new StringBuilder(
                "with cte1 as (select oo.tig_owner_uuid,count(1) as ctn from tig_column_data oo group by oo.tig_owner_uuid)");
        // append second with as cte2
        sqlB.append(",cte2 as (select o.* from tig_table_data o where o.status = 2 and o.direction = ");
        sqlB.append(direction == 1 ? 2 : 1);
        if (StringUtils.isBlank(params) == false) {// 带约束
            sqlB.append(params);
        }
        sqlB.append(") select b.* from cte2 b left join cte1 a on a.tig_owner_uuid = b.uuid where a.ctn = b.cloum_num or b.action = 3 order by b.status asc,b.order_id asc");
        System.out.println(sqlB);

        System.out.println("---------------------------------------------------------------");
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        Random ran = new Random();
        for (int i = 0; i < 500; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("uuid1", UUID.randomUUID().toString());
            data.put("uuid2", UUID.randomUUID().toString());
            data.put("uuid3", UUID.randomUUID().toString());
            data.put("uuid4", UUID.randomUUID().toString());
            data.put("String1", UUID.randomUUID().toString());
            data.put("String2", UUID.randomUUID().toString());
            data.put("String3", UUID.randomUUID().toString());
            data.put("String4", UUID.randomUUID().toString());
            data.put("Date1", new Date());
            data.put("Date2", new Date());
            data.put("Date3", new Date());
            data.put("Date4", new Date());
            data.put("Number1", ran.nextInt());
            data.put("Number2", ran.nextInt());
            data.put("Number3", ran.nextDouble());
            data.put("Number4", ran.nextDouble());
            data.put("Number5", ran.nextFloat());
            data.put("Number6", ran.nextFloat());
            data.put("null1", null);
            data.put("null2", null);
            data.put("null3", null);
            data.put("null4", null);
            datas.add(data);
        }
        BASE64Encoder enc = new BASE64Encoder();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream);
        objectOutputStream.writeObject(datas);
        objectOutputStream.close();
        gzipOutputStream.close();
        String str = null;
        str = enc.encode(byteArrayOutputStream.toByteArray());// java codec
        System.out.println(str.length());
        byteArrayOutputStream.close();

        // str = "";
        BASE64Decoder dec = new BASE64Decoder();
        byte[] bit = null;
        bit = dec.decodeBuffer(str);// java codec
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bit);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        System.out.println(obj);

        //
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(byteArrayOutputStream2);
        objectOutputStream2.writeObject(datas);
        objectOutputStream2.close();
        str = null;
        str = enc.encode(byteArrayOutputStream2.toByteArray());// java codec
        System.out.println(str.length());
        byteArrayOutputStream2.close();

    }

    @PostConstruct
    public void init() {
        try {
            Map<String, SysProperties> sysPropertiess = exchangeDataConfigService.getAllSysProperties("SYSPROPERTIES");

            if (MapUtils.isEmpty(sysPropertiess)) {
                return;
            }

            // 同步中间隔时间再同步(S)
            if (sysPropertiess.get("syn_fail_agin_interval").getProValue() != null) {
                interval = Integer.parseInt(sysPropertiess.get("syn_fail_agin_interval").getProValue());
            }
            // 每个同步周期同步条数
            if (sysPropertiess.get("syn_zq_data_num").getProValue() != null) {
                dataNum = Integer.parseInt(sysPropertiess.get("syn_zq_data_num").getProValue());
            }
            // 清理间隔时间(S)
            if (sysPropertiess.get("syn_clear_table_interval").getProValue() != null) {
                clearInterval = Integer.parseInt(sysPropertiess.get("syn_clear_table_interval").getProValue());
            }
            // 同步链路超时时间(S)
            if (sysPropertiess.get("syn_offline_timeout").getProValue() != null) {
                long ptimeout = Long.parseLong(sysPropertiess.get("syn_offline_timeout").getProValue()) * 1000;
                if (ptimeout > timeout) {
                    timeout = ptimeout;
                }
            }
            String xt_wl = sysPropertiess.get("xt_wl").getProValue();// 内网/外网
            // in/out

            /***********************获取同步的信息****************************/
            // 流出
            int out_ftp_post = 21;
            String out_ftp_host = "";
            String out_ftp_user_name = "";
            String out_ftp_pass_word = "";
            // 流入
            int in_ftp_post = 21;
            String in_ftp_host = "";
            String in_ftp_user_name = "";
            String in_ftp_pass_word = "";
            if ("in".equals(xt_wl)) {// 当前是内网
                direction = 1;
                // 内网流出
                out_ftp_host = sysPropertiess.get("qzj_ftp_nw_out_host").getProValue();
                out_ftp_user_name = sysPropertiess.get("qzj_ftp_nw_out_username").getProValue();
                out_ftp_pass_word = sysPropertiess.get("qzj_ftp_nw_out_password").getProValue();
                out_ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_nw_out_post").getProValue());
                // 内网流入
                in_ftp_host = sysPropertiess.get("qzj_ftp_nw_in_host").getProValue();
                in_ftp_user_name = sysPropertiess.get("qzj_ftp_nw_in_username").getProValue();
                in_ftp_pass_word = sysPropertiess.get("qzj_ftp_nw_in_password").getProValue();
                in_ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_nw_in_post").getProValue());
            } else if ("out".equals(xt_wl)) {// 当前是外网
                direction = 2;
                // 外网流出
                out_ftp_host = sysPropertiess.get("qzj_ftp_ww_out_host").getProValue();
                out_ftp_user_name = sysPropertiess.get("qzj_ftp_ww_out_username").getProValue();
                out_ftp_pass_word = sysPropertiess.get("qzj_ftp_ww_out_password").getProValue();
                out_ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_ww_out_post").getProValue());
                // 外网流入
                in_ftp_host = sysPropertiess.get("qzj_ftp_ww_in_host").getProValue();
                in_ftp_user_name = sysPropertiess.get("qzj_ftp_ww_in_username").getProValue();
                in_ftp_pass_word = sysPropertiess.get("qzj_ftp_ww_in_password").getProValue();
                in_ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_ww_in_post").getProValue());

            }
            // 反馈数据提取
            feedInQuery = "select list.* from (select * from tig_table_data d where (d.status=6 or d.status=7) and d.direction = "
                    + (direction == 1 ? 2 : 1)
                    + " order by d.status asc,d.order_id asc) list where rownum<="
                    + (dataNum * 10);
            // 反馈数据提取
            feedOutQuery = "select * from tig_table_data o where o.syn_time is null and (o.status=6 or o.status=7) and o.direction = "
                    + direction + " order by o.order_id asc";
            // 流入队列
            dataInQuery = "select count(1) from tig_table_data t where t.status = 2 and t.direction = "
                    + (direction == 1 ? 2 : 1);
            // 流出队列
            dataOutQuery = "select count(1) from tig_table_data t where t.status = 1 and t.direction = " + direction;

            // 取前置机反馈数据量
            feedInCount = "select count(1) from tig_column_clob t where t.data_status = -12 and t.direction = "
                    + (direction == 1 ? 2 : 1);
            // 取系统待同步反馈数据量
            feedOutCount = "select count(1) from tig_table_data o where o.syn_time is null and (o.status=6 or o.status=7) and o.direction = "
                    + direction;
            // 取系统待反馈数据量
            feedBackCount = "select count(1) from tig_table_data o where o.status=2 and o.direction = " + direction;

            // 确认清理游标
            queryMaxOrderId = "select max(t.order_id) as order_id from tig_table_data t where t.status = 4 and t.direction = "
                    + direction + " and t.create_time < cast((sysdate - " + clearInterval + "/86400) as TIMESTAMP)";
            // 查询心跳
            queryBeat = "select create_time,feedback_time,composite_key,stable_name from tig_column_clob t where t.tig_owner_uuid = 'heartbeat' and t.tig_column_name = 'heartbeat'";
            // 插入心跳
            insertBeat = "insert into tig_column_clob (tig_owner_uuid,tig_column_name,create_time,feedback_time,composite_key,stable_name) values ('heartbeat','heartbeat',?,?,?,?)";
            // 更新心跳
            updateBeat = "update tig_column_clob t set t.create_time = ?, t.feedback_time = ?,t.composite_key = ?,t.stable_name = ? where t.tig_owner_uuid = 'heartbeat' and t.tig_column_name = 'heartbeat'";
            // 删除心跳
            deleteBeat = "delete from tig_column_clob t where t.tig_owner_uuid = 'heartbeat' and t.tig_column_name = 'heartbeat'";
            // 重发发送端已经发送的数据
            sqlData = "update tig_table_data t set t.status = 1 where t.status = 2 and t.direction = " + direction;
            // 重发发送端已经发送的数据
            sqlData2 = "update tig_table_data t set t.status = 1 where t.remark = ? and t.direction = " + direction;
            // 重发发送端已经发送的大字段
            sqlClob = "update tig_column_clob t set t.data_status = 1 where t.data_status = 2 and t.direction = "
                    + direction;

            exportFolder = new File(Config.APP_DATA_DIR + File.separator + "mongofilesyn" + File.separator + "export"
                    + File.separator);
            exportFolder.mkdirs();

            outFtpDataBean.setFtp_host(out_ftp_host);
            outFtpDataBean.setFtp_pass_word(out_ftp_pass_word);
            outFtpDataBean.setFtp_post(out_ftp_post);
            outFtpDataBean.setFtp_user_name(out_ftp_user_name);

            inFtpDataBean.setFtp_host(in_ftp_host);
            inFtpDataBean.setFtp_pass_word(in_ftp_pass_word);
            inFtpDataBean.setFtp_post(in_ftp_post);
            inFtpDataBean.setFtp_user_name(in_ftp_user_name);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * online true、offline false
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynService#beat()
     */
    @Override
    public synchronized boolean beat() {
        boolean oStateOffline = offline;
        // 发送端心跳时间(写回到反馈字段),反馈时间,当前时间
        String preBatchId = null;
        Date beatDate = null, feedDate = new Date(lastFeed), currDate = new Date();
        {// 查询心跳数据
            Session sessionIn = null;
            try {
                sessionIn = this.getDao(ExchangeConfig.NEWAPASIN).getSession();
                Query queryInHeart = sessionIn.createSQLQuery(queryBeat);
                List<Map<String, Object>> list = queryInHeart.setResultTransformer(INSTANCE).list();
                if (list != null && list.size() > 0) {
                    Map<String, Object> data = list.get(0);
                    beatDate = (Date) data.get("create_time");
                    feedDate = (Date) data.get("feedback_time");
                    preBatchId = (String) data.get("composite_key");
                    String maxClearCursor = (String) data.get("stable_name");
                    if (StringUtils.isNumeric(maxClearCursor)) {
                        feedCursor = Long.parseLong(maxClearCursor);
                    }
                }
            } catch (Exception ex) {
                logger.error("hearbeat query in error:", ex);
            } finally {
            }
        }

        {// 写心跳数据
            Session sessionOut = null;
            try {
                sessionOut = this.getDao(ExchangeConfig.NEWAPASOUT).getSession();
                sessionOut.createSQLQuery(deleteBeat).executeUpdate();// 先删除后插入,防止接收端无数据导致无效更新
                // Query updateOutHeart = sessionOut.createSQLQuery(updateBeat);
                // updateOutHeart.setTimestamp(0, currDate);// 心跳时间
                // updateOutHeart.setTimestamp(1, beatDate);// 发送端心跳时间->反馈时间
                // updateOutHeart.setString(2, lossQueue.poll());// 携带重发批次
                // updateOutHeart.setString(3, currCursor + "");// 清理游标
                // if (updateOutHeart.executeUpdate() <= 0) {
                Query insertOutHeart = sessionOut.createSQLQuery(insertBeat);
                insertOutHeart.setTimestamp(0, currDate);// 心跳时间
                insertOutHeart.setTimestamp(1, beatDate);// 发送端心跳时间->反馈时间
                insertOutHeart.setString(2, lossQueue.poll());// 携带重发批次
                insertOutHeart.setString(3, currCursor + "");// 清理游标
                insertOutHeart.executeUpdate();
                // }
            } catch (Exception ex) {
                logger.error("hearbeat send out error:", ex);
            } finally {
            }
        }

        {// 分析心跳
            lastBeat = currDate.getTime();
            if (feedDate != null) {
                lastFeed = feedDate.getTime();
            }
            offline = lastBeat - lastFeed > timeout;// 更新心跳状态
        }

        if ((oStateOffline || StringUtils.isNotBlank(preBatchId)) && !offline) {
            Session session = null;
            try {
                session = this.dao.getSession();
                if (oStateOffline) {
                    // 链路恢复,有序重发“发送中”的数据
                    Query updateData = session.createSQLQuery(sqlData);
                    long dataSize = updateData.executeUpdate();
                    Query updateClob = session.createSQLQuery(sqlClob);
                    long clobSize = updateClob.executeUpdate();
                    logger.error("resend data size:" + dataSize + ",clob size:" + clobSize);
                } else if (StringUtils.isNotBlank(preBatchId)) {
                    // 重发前置批次
                    Query preBatchUpdate = session.createSQLQuery(sqlData2);
                    preBatchUpdate.setString(0, preBatchId);
                    preBatchUpdate.executeUpdate();
                    logger.error("resend batchId : " + preBatchId);
                }
            } finally {
            }
        }

        {// 发布事件
            if (oStateOffline && !offline) { // 上线事件
                SynOnlineEvent event = new SynOnlineEvent(getDirection());
                ApplicationContextHolder.getApplicationContext().publishEvent(event);
            } else if (!oStateOffline && offline) {// 离线事件
                SynOfflineEvent event = new SynOfflineEvent(getDirection());
                ApplicationContextHolder.getApplicationContext().publishEvent(event);
            }
        }
        // 反馈时间超时（默认5分钟没有心跳则离线）
        if (oStateOffline != offline) {
            logger.error("heartbeat offline : " + offline + ",feedDate : " + feedDate + ",clearCur:" + feedCursor);
        }
        beatContext.put("beatDate", beatDate);
        beatContext.put("feedDate", feedDate);
        beatContext.put("currDate", currDate);
        beatContext.put("offline", offline);
        beatContext.put("currCursor", currCursor);
        beatContext.put("feedCursor", feedCursor);
        return !offline;
    }

    public boolean unimasOffline() {
        return offline;
    }

    @Override
    public boolean synOutData(Map<String, SysProperties> sysPropertiess, String params, String paramGroup)
            throws Exception {
        return synOutData(sysPropertiess, params, false, paramGroup);
    }

    @Override
    public boolean synOutData(Map<String, SysProperties> sysPropertiessDeprecated, String params, boolean withClob,
                              String paramGroup) throws Exception {
        /**
         * 数据同步到前置机
         * 1、提取系统数据
         * 2、变更数据到前置机
         * 3、修改系统数据状态
         */
        boolean fullFetch = false;
        boolean recordPreBatchId = false;
        try {
            // 测试期间先注释掉
            if (unimasOffline())/* 通道不可用 */ {
                return false;
            }
            Map<String, File> fileMap = new HashMap<String, File>();
            Date synTime = new Date();// 同步时间
            String batchId = UUID.randomUUID().toString();// 批次ID,记录在tig_table_data.remark字段
            /***********************1、提取系统数据 先提取表数据再提取字段数据****************************/
            List<String> addDataUuids = new ArrayList<String>(); // 本次新增的UUID集合
            // List<String> updateDataUuids = new ArrayList<String>(); //
            // 本次更新的UUID集合
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> CLOBList = new ArrayList<Map<String, Object>>();
            // ?long ri_token = 0;
            Session session = this.dao.getSession();
            try {
                // ?ri_token = statics.startTimer(SynDataLog.READ_IN);
                if (withClob == true) {
                    // 流出Clob提取
                    StringBuilder sqlB = new StringBuilder();
                    sqlB.append("select list.* from (select * from tig_column_clob t where (t.data_status=1 or (t.data_status=2");
                    sqlB.append(" and t.syn_time < cast((sysdate - ").append(interval).append("/86400) as TIMESTAMP)");
                    sqlB.append(")) and t.direction=").append(direction);
                    sqlB.append(") list where rownum<=").append(dataNum / 2);// clob走ftp,同步速度尽量慢
                    CLOBList = session.createSQLQuery(sqlB.toString()).setResultTransformer(INSTANCE).list();
                    if (CLOBList.isEmpty()) {
                        return false;
                    }
                    fullFetch = CLOBList.size() == dataNum / 2;
                } else {
                    // 提取同步数据
                    StringBuilder sqlB = new StringBuilder();
                    // 一天秒：86400 = 24*60*60
                    sqlB.append("select list.* from (select * from tig_table_data o where (o.status=1 or (o.status=2 ");
                    sqlB.append(" and o.syn_time < cast((sysdate - ").append(interval).append("/86400) as TIMESTAMP)");
                    sqlB.append(")) and o.direction = ").append(direction);
                    if (StringUtils.isNotBlank(params)) {
                        sqlB.append(params);
                    }
                    sqlB.append(" order by o.order_id asc) list where rownum<=").append(dataNum * 5);// 单个批次同步50条记录
                    dataList = session.createSQLQuery(sqlB.toString()).setResultTransformer(INSTANCE).list();
                    if (dataList.isEmpty()) {
                        return false;
                    }
                    fullFetch = dataList.size() == dataNum * 5;
                    // 提取字段数据
                    // ?long ria_token = 0;
                    try {
                        // ?ria_token = statics.startTimer(SynDataLog.READ_IN +
                        // SynDataLog.SUB_ALL);
                        for (Map<String, Object> dataItem : dataList) {
                            String uuid = (String) dataItem.get("uuid");
                            // 当REPO_FILE插入时，删除更新不做处理，mongo里更新即是插入及删除，删除由mongo的垃圾回收来处理
                            String tableName = (String) dataItem.get("stable_name");
                            if (tableName != null && tableName.toUpperCase().equals(ExchangeConfig.FILETABLE)
                                    && ((Number) dataItem.get("action")).intValue() == 1) {
                                String suuid = (String) dataItem.get("suuid");
                                fileMap.put(suuid, this.exportFileZip(suuid));
                            }
                        }
                    } finally {
                        // ?statics.stopTimer(ria_token, null);
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                // 因为这次session 是通过 getSession,获取的，所以会自动管理
                // 不需要手动关闭了,统一用session1就可以了, 同理下面所有的session.close 方法全部删除 --zyguo
                // session1.close();// ,在取完字段信息后关闭
                // ?statics.stopTimer(ri_token, null);
            }

            /****************************3、变更数据到前置机 先插入字段数据再插入表数据*****************************/
            // ?long wo_token = 0;
            Session sessionOut = this.getDao(ExchangeConfig.NEWAPASOUT).getSession();
            try {
                // ?long woc_token = 0;
                try {
                    // ?woc_token = statics.startTimer(SynDataLog.WRITE_OUT +
                    // SynDataLog.SUB_CLOB);
                    // 大字段插入
                    for (Map<String, Object> dataItem : CLOBList) {
                        String tig_owner_uuid = dataItem.get("tig_owner_uuid").toString();
                        String tig_column_name = dataItem.get("tig_column_name").toString();
                        String suuid = getClobSuuid(tig_owner_uuid, tig_column_name);
                        Clob clob = (Clob) dataItem.get("data_clob");
                        // Clob上传FTP,如果内容超过0.5M
                        boolean largeClob = false;
                        if (largeClob = isLargeClob(clob)) {
                            // 同步FTP上传CLOB内容
                            uploadFileToFtp(suuid, exportClobZip(suuid, clob), outFtpDataBean);
                        }

                        String querySql2 = "delete from tig_column_clob o where o.tig_owner_uuid=:tig_owner_uuid and o.tig_column_name=:tig_column_name";
                        Query query = sessionOut.createSQLQuery(querySql2);
                        query.setString("tig_owner_uuid", tig_owner_uuid);
                        query.setString("tig_column_name", tig_column_name);
                        query.executeUpdate();
                        String insertSql = "insert into tig_column_clob "
                                + "(tig_owner_uuid,tig_column_name,data_uuid,composite_key,data_clob,data_status,direction,stable_name,syn_time,create_time) "
                                + "values (?,?,?,?,?,?,?,?,?,?)";
                        SQLQuery sqlquery = sessionOut.createSQLQuery(insertSql);
                        sqlquery.setString(0, (String) dataItem.get("tig_owner_uuid"));
                        sqlquery.setString(1, (String) dataItem.get("tig_column_name"));
                        sqlquery.setString(2, (String) dataItem.get("data_uuid"));
                        sqlquery.setString(3, (String) dataItem.get("composite_key"));
                        // data_clob
                        String clobText = null;
                        if (clob == null || clob.length() <= 0) {
                            clobText = CLOB_EMPTY_FLAG;
                        } else if (largeClob) {
                            clobText = suuid;
                        } else {
                            clobText = IOUtils.toString(clob.getCharacterStream());
                        }
                        sqlquery.setText(4, clobText);
                        // data_status
                        sqlquery.setInteger(5, 2);
                        // direction
                        sqlquery.setInteger(6, ((Number) dataItem.get("direction")).intValue());
                        sqlquery.setString(7, (String) dataItem.get("stable_name"));
                        sqlquery.setTimestamp(8, synTime);
                        sqlquery.setTimestamp(9, (Date) dataItem.get("create_time"));
                        // 大字段是否为空标志位
                        sqlquery.executeUpdate();
                    }
                } finally {
                    // ?statics.stopTimer(woc_token, null);
                }

                // 插入数据
                // ?long wol_token = 0;
                List<Map<String, Object>> dataSynDataList = new ArrayList<Map<String, Object>>();
                ObjectOutputStream dataObjectOutputStream = null;
                ByteArrayOutputStream dataByteArrayOutputStream = null;
                try {
                    // ?wol_token = statics.startTimer(SynDataLog.WRITE_OUT +
                    // SynDataLog.SUB_LIST);
                    for (Map<String, Object> dataItem : dataList) {
                        String stable_name = (String) dataItem.get("stable_name");
                        String uuid = (String) dataItem.get("uuid");
                        String remark = (String) dataItem.get("remark");// 同步批次标识
                        int action = ((Number) dataItem.get("action")).intValue();
                        dataItem.put("status", 2);// 修改状态为同步中
                        dataItem.put("remark", batchId);// 添加同步批次
                        /******比较数据是否已经处理过*********/
                        if (StringUtils.isNotBlank(remark)) {
                            String querySql2 = "delete from tig_column_clob o where o.tig_owner_uuid=:buid and o.tig_column_name = 'tig_table_data'";// 查询是否存在数据
                            Query query = sessionOut.createSQLQuery(querySql2);
                            query.setString("buid", remark).executeUpdate();
                        }
                        dataSynDataList.add(dataItem);
                        addDataUuids.add(uuid);
                        // 文件夹 先上传附件到ftp再插入数据
                        if (stable_name != null && stable_name.toUpperCase().equals(ExchangeConfig.FILETABLE)
                                && action == 1) {
                            String suuid = (String) dataItem.get("suuid");
                            File zipFile = fileMap.get(suuid);
                            if (zipFile != null && zipFile.length() > 0) {
                                dataItem.put("cloum_num", new Long(zipFile.length())); // 附件存在标志位
                                uploadFileToFtp(suuid, zipFile, outFtpDataBean);
                            }
                        }
                    }
                    if (!addDataUuids.isEmpty()) {
                        // 取本次需要同步的字段信息
                        String colOutQuery = "select t.* from tig_column_data t where t.tig_owner_uuid in (:uuids)";
                        Query colQuery = session.createSQLQuery(colOutQuery).setParameterList("uuids", addDataUuids);
                        List<Map<String, Object>> columDataList = colQuery.setResultTransformer(INSTANCE).list();
                        long dataLength = 0;
                        for (Map<String, Object> columnData : columDataList) {
                            Object dataValue = null;
                            if ("CLOB".equals(columnData.get("tig_data_type"))
                                    && (dataValue = columnData.get("data_clob")) != null) {
                                // load lazy clob
                                columnData.put("data_clob", new SerialClob((Clob) dataValue));
                                dataLength += ((Clob) dataValue).length();
                            } else if ("BLOB".equals(columnData.get("tig_data_type"))
                                    && (dataValue = columnData.get("data_blob")) != null) {
                                // load lazy blob
                                columnData.put("data_blob", new SerialBlob((Blob) dataValue));
                                dataLength += ((Blob) dataValue).length();
                            }
                        }
                        logger.info("----------------batch:" + batchId + ",size:" + columDataList.size());
                        // Base64序列化批次数据
                        Map<String, Object> batchData = new HashMap<String, Object>();
                        batchData.put(TIG_TABLE_DATA, dataSynDataList);
                        batchData.put(TIG_COLUMN_DATA, columDataList);
                        String encodeData = null;
                        if (isLargeData(dataLength)) {
                            // 同步FTP上传Object stream
                            File dataObjectFile = exportObjectZip(batchId, batchData); // TODO
                            // 大文件事物调整？
                            uploadFileToFtp(batchId, dataObjectFile, outFtpDataBean);
                            encodeData = FILE_SIZE_MAGIC + dataObjectFile.length(); // 文件大小，用于文件完整性校验
                        } else {
                            dataByteArrayOutputStream = new ByteArrayOutputStream();
                            dataObjectOutputStream = new ObjectOutputStream(new GZIPOutputStream(
                                    dataByteArrayOutputStream));
                            dataObjectOutputStream.writeObject(batchData);
                            // flush buffer
                            IOUtils.closeQuietly(dataObjectOutputStream);
                            BASE64Encoder enc = new BASE64Encoder();
                            encodeData = enc.encode(dataByteArrayOutputStream.toByteArray());// Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
                        }
                        String insertSql = "insert into tig_column_clob "
                                + "(tig_owner_uuid,tig_column_name,data_uuid,data_clob,data_status,direction,stable_name,syn_time,create_time,composite_key)"
                                + "values (?,?,?,?,?,?,?,?,?,?)";
                        SQLQuery sqlquery = sessionOut.createSQLQuery(insertSql);
                        sqlquery.setString(0, batchId);
                        sqlquery.setString(1, TIG_TABLE_DATA);// tig_table_data
                        sqlquery.setString(2, String.valueOf(addDataUuids.size()));// dataLists.size
                        // ->
                        // data_uuid
                        // data_clob
                        sqlquery.setText(3, encodeData);
                        // data_status
                        sqlquery.setInteger(4, -2);//
                        // direction
                        sqlquery.setInteger(5, getDirection());
                        // stable_name
                        sqlquery.setString(6, paramGroup);
                        sqlquery.setTimestamp(7, synTime);
                        sqlquery.setTimestamp(8, synTime);
                        // 写入上一个批次,维护前后置关系
                        sqlquery.setString(9, preContext.get(paramGroup));
                        sqlquery.executeUpdate();
                        recordPreBatchId = true;
                    }
                } finally {
                    IOUtils.closeQuietly(dataObjectOutputStream);
                    IOUtils.closeQuietly(dataByteArrayOutputStream);
                    // ?statics.stopTimer(wol_token, null);
                }
            } catch (Exception e) {
                throw e;
            } finally {
                // ?statics.stopTimer(wo_token, null);
            }

            /****************************4、修改系统数据状态*****************************/
            // ?long is_token = 0;
            try {
                // ?is_token = statics.startTimer(SynDataLog.IN_STATE);
                if (!addDataUuids.isEmpty()) {
                    for (String addUUid : addDataUuids) {
                        SQLQuery sqlquery = session
                                .createSQLQuery("update tig_table_data o set o.remark=?,o.syn_time=sysdate,o.status=2 where o.uuid = ?");
                        sqlquery.setString(0, batchId);
                        // sqlquery.setTimestamp("syn_time", synTime); //
                        // 修改同步时间,防止短时间内多次同步
                        sqlquery.setString(1, addUUid);
                        sqlquery.executeUpdate();
                    }
                }
                for (Map<String, Object> dataItem : CLOBList) {// 更新同步时间
                    String tig_owner_uuid = dataItem.get("tig_owner_uuid").toString();
                    String tig_column_name = dataItem.get("tig_column_name").toString();
                    String updateSql = "update tig_column_clob o set o.syn_time=sysdate,o.data_status=2 where o.tig_owner_uuid=:tig_owner_uuid and o.tig_column_name=:tig_column_name";
                    SQLQuery sqlquery = session.createSQLQuery(updateSql);
                    sqlquery.setString("tig_owner_uuid", tig_owner_uuid);
                    sqlquery.setString("tig_column_name", tig_column_name);
                    // sqlquery.setTimestamp("syn_time", synTime);//
                    // 修改同步时间,防止短时间内多次同步
                    sqlquery.executeUpdate();
                }
            } catch (Exception e) {
                throw e;
            } finally {
                // ?statics.stopTimer(is_token, null);
            }
            if (recordPreBatchId) { // 成功写入前置机,记录当前批次
                preContext.put(paramGroup, batchId);
            }
        } catch (Exception e) {
            logger.error("**********************synOutData " + e.getMessage(), e);
            throw e;
        }
        return fullFetch;
    }

    @Override
    public boolean synOutDataFeedback(Map<String, SysProperties> sysPropertiess) throws Exception {
        /**
         * 数据同步到前置机
         * 1、提取系统数据
         * 2、变更数据到前置机
         * 3、修改系统数据状态
         */
        boolean fullFetch = false;
        try {
            Date synTime = new Date();// 同步时间
            String batchId = UUID.randomUUID().toString();// 批次ID,记录在tig_table_data.remark字段
            /***********************2、提取系统数据 先提取表数据再提取字段数据****************************/
            /***********************2、提取系统数据 先提取表数据再提取字段数据****************************/
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            List<String> addDataUuids = new ArrayList<String>(); // 本次新增的UUID集合
            // List<String> updateDataUuids = new ArrayList<String>(); //
            // 本次更新的UUID集合
            Session session = this.dao.getSession();
            // ?long ir_token = 0;
            try {
                // ?ir_token = statics.startTimer(SynDataLog.IN_READ);
                // 提取反馈数据
                Query query = session.createSQLQuery(feedOutQuery).setMaxResults(dataNum * 10);
                dataList = query.setResultTransformer(INSTANCE).list();
                if (dataList.isEmpty()) {
                    return fullFetch;
                }
                fullFetch = dataList.size() == dataNum * 10;
            } catch (Exception e) {
                throw e;
            } finally {
                // ?statics.stopTimer(ir_token, null);
            }

            /****************************3、变更数据到前置机 先插入字段数据再插入表数据*****************************/
            Session sessionOut = this.getDao(ExchangeConfig.NEWAPASOUT).getSession();
            List<Map<String, Object>> synDataList = new ArrayList<Map<String, Object>>();
            ObjectOutputStream objectOutputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            // ?long ow_token = 0;
            try {
                // ?ow_token = statics.startTimer(SynDataLog.OUT_WRITE);
                // 插入数据
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = (String) dataItem.get("uuid");
                    /******比较数据是否已经处理过*********/
                    String remark = (String) dataItem.get("remark");// 同步批次标识
                    dataItem.put("remark", batchId);// 添加同步批次
                    if (StringUtils.isNotBlank(remark)) {
                        String querySql2 = "delete from tig_column_clob o where o.tig_owner_uuid=:buid and o.tig_column_name = 'tig_table_data'";// 查询是否存在数据
                        Query query = sessionOut.createSQLQuery(querySql2);
                        query.setString("buid", remark).executeUpdate();
                    }
                    synDataList.add(dataItem);
                    addDataUuids.add(uuid);
                }
                if (!addDataUuids.isEmpty()) {
                    logger.info("----------------feedBack batch:" + batchId + ",size:" + synDataList.size());
                    // Base64序列化批次数据
                    Map<String, Object> batchData = new HashMap<String, Object>();
                    batchData.put(TIG_TABLE_DATA, synDataList);
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(batchData);
                    BASE64Encoder enc = new BASE64Encoder();
                    String encodeData = enc.encode(byteArrayOutputStream.toByteArray());// Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
                    String insertSql = "insert into tig_column_clob "
                            + "(tig_owner_uuid,tig_column_name,data_uuid,data_clob,data_status,direction,stable_name,syn_time,create_time)"
                            + "values (?,?,?,?,?,?,?,?,?)";
                    SQLQuery sqlquery = sessionOut.createSQLQuery(insertSql);
                    sqlquery.setString(0, batchId);
                    sqlquery.setString(1, TIG_TABLE_DATA);// tig_column_name
                    sqlquery.setString(2, String.valueOf(addDataUuids.size()));// dataLists.size
                    // ->
                    // data_uuid
                    // data_clob
                    sqlquery.setText(3, encodeData);
                    // data_status
                    sqlquery.setInteger(4, -12);//
                    // direction
                    sqlquery.setInteger(5, getDirection());
                    // stable_name
                    sqlquery.setString(6, "data_table_feedback");
                    sqlquery.setTimestamp(7, synTime);
                    sqlquery.setTimestamp(8, synTime);
                    sqlquery.executeUpdate();
                    //
                }
            } catch (Exception e) {
                throw e;
            } finally {
                IOUtils.closeQuietly(objectOutputStream);
                IOUtils.closeQuietly(byteArrayOutputStream);
                // ?statics.stopTimer(ow_token, null);
            }
            /****************************4、修改系统数据同步时间*****************************/
            try {
                if (!addDataUuids.isEmpty()) {
                    SQLQuery sqlquery = session
                            .createSQLQuery("update tig_table_data o set o.remark=:remark,o.syn_time=:syn_time where o.uuid in (:uuids)");
                    sqlquery.setString("remark", batchId);
                    sqlquery.setTimestamp("syn_time", synTime); // 修改同步时间,防止短时间内多次同步
                    sqlquery.setParameterList("uuids", addDataUuids);
                    sqlquery.executeUpdate();
                }
            } catch (Exception e) {
                throw e;
            } finally {
            }
        } catch (Exception e) {
            logger.error("**********************synOutDataFeedback " + e.getMessage(), e);
            throw e;
        }
        return fullFetch;
    }

    @Override
    public boolean synInData(Map<String, SysProperties> sysPropertiess, String params, String paramGroup)
            throws Exception {
        return synInData(sysPropertiess, params, false, paramGroup);
    }

    @Override
    public boolean synInData(Map<String, SysProperties> sysPropertiessDeprecated, String params, boolean withClob,
                             String paramGroup) throws Exception {
        /**
         * 提取前置机的数据
         * 1、提取前置机数据(同步中，同步成功/同步失败(握手))
         * 2、修改流入前置机状态（避免多少提取）
         * 3、修改流出前置机状态（清理数据使用）
         */
        boolean fullFetch = false;
        try {
            Date synTime = new Date();// 同步时间
            String batchId = null, preBatchId = null;
            /***********************2、提取前置机数据(同步中，同步成功/同步失败(握手))****************************/
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> columDataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> CLOBList = new ArrayList<Map<String, Object>>();
            Session sessionIn = this.getDao(ExchangeConfig.NEWAPASIN).getSession();
            // ?long ro_token = 0;
            InputStream dataInputStream = null;
            ObjectInputStream objectInputStream = null;
            try {
                // ?ro_token = statics.startTimer(SynDataLog.READ_OUT);
                if (withClob == true) /* 大字段单独处理 */ {
                    // 提取大字段
                    StringBuilder sqlB = new StringBuilder(
                            "select list.* from (select * from tig_column_clob t where length(t.data_clob) > 0 and t.data_status=2 and t.direction = ");
                    sqlB.append(direction == 1 ? 2 : 1).append(") list where rownum<=").append(dataNum);
                    CLOBList = sessionIn.createSQLQuery(sqlB.toString()).setResultTransformer(INSTANCE).list();
                    if (CLOBList.isEmpty()) {
                        return fullFetch;
                    }
                    fullFetch = CLOBList.size() == dataNum;
                } else {
                    // first with as cte1
                    StringBuilder sqlB = new StringBuilder(
                            "select t.tig_owner_uuid,t.tig_column_name,t.data_clob,composite_key from tig_column_clob t where length(t.data_clob) > 0 and t.data_status = -2 and t.direction = ");
                    sqlB.append(direction == 1 ? 2 : 1);
                    if (StringUtils.isNotBlank(paramGroup)) {// 带约束
                        sqlB.append(" and t.stable_name = '").append(paramGroup).append("'");// paramGroup
                    }
                    sqlB.append(" order by t.create_time asc");
                    // 提取同步中及反馈的数据
                    Query query = sessionIn.createSQLQuery(sqlB.toString()).setMaxResults(1);// 每次取一个批次
                    List<Map<String, Object>> batchDatas = query.setResultTransformer(INSTANCE).list();
                    if (batchDatas.isEmpty()) {
                        return fullFetch;
                    }
                    fullFetch = batchDatas.size() == 1;
                    Map<String, Object> batchData = batchDatas.get(0);
                    batchId = (String) batchData.get("tig_owner_uuid");
                    preBatchId = (String) batchData.get("composite_key");
                    if (strictSort && StringUtils.isNotBlank(preBatchId)) { //
                        String preBatchSql = "select count(1) as count_ from tig_column_clob o where o.tig_owner_uuid=?";
                        Query preBatchQuery = sessionIn.createSQLQuery(preBatchSql);
                        preBatchQuery.setString(0, preBatchId);
                        boolean notExist = ((Number) preBatchQuery.uniqueResult()).intValue() <= 0;
                        if (notExist && lossQueue.offer(preBatchId)) {
                            // 通过心跳写反馈数据通知重发(重发数据量不大)
                            logger.error("batchId : [" + batchId + "] waiting for preBatchId : " + preBatchId);
                            // return false; // FIXME 等待时间多长
                        }
                    }
                    Clob clob = (Clob) batchData.get("data_clob");
                    // debug info
                    // logger.info("batchId:" + batchId + ",clob length:" +
                    // clob.length());
                    String serData = IOUtils.toString(clob.getCharacterStream());
                    if (serData.startsWith(FILE_SIZE_MAGIC)) {
                        int retryCount = 0;
                        File zipFile = null;
                        long objectLength = Long.parseLong(serData.replace(FILE_SIZE_MAGIC, ""));
                        do {
                            zipFile = downFileFromFtp(batchId, inFtpDataBean);// 下载附件
                            if (zipFile == null || zipFile.length() <= 0) {
                                // 附件小于16m时,8s重试一次;大于16m时,按附件大小等待,1M附件传输时间500ms,100M附件等待50s(根据关闸负载调整)
                                long sleepTime = Math.max(8 * 1000, (objectLength / 1024 / 1024) * 500);
                                logger.error("batchId : [" + batchId + "] waiting batchFile sleep ms : "
                                        + (sleepTime / 1000) + ",retryCount:" + retryCount);
                                Thread.sleep(sleepTime);// 附件小于16m至少等待8秒是为了保障小附件重试有效，比如1m附件重试等待16*1*0.5s可能还未到，但16*8附件就可能到了。
                            }
                            // 重试16次
                        } while (zipFile == null && (retryCount++) < 16);
                        if (zipFile == null || zipFile.length() != objectLength) {
                            // （附件不完整，或者重试无效） 重新发送
                            if (lossQueue.offer(batchId)) {
                                logger.error("batchId : [" + batchId + "] waiting for batchFile : " + batchId);
                            }
                        } else {
                            dataInputStream = new FileInputStream(zipFile);
                            objectInputStream = new ObjectInputStream(new GZIPInputStream(dataInputStream));
                            Map<String, Object> desData = desData = (Map<String, Object>) objectInputStream
                                    .readObject();
                            dataList = (List<Map<String, Object>>) desData.get(TIG_TABLE_DATA);
                            columDataList = (List<Map<String, Object>>) desData.get(TIG_COLUMN_DATA);
                        }
                    } else {
                        BASE64Decoder dec = new BASE64Decoder();
                        dataInputStream = new ByteArrayInputStream(dec.decodeBuffer(serData));// Base64.decodeBase64(serData)
                        objectInputStream = new ObjectInputStream(new GZIPInputStream(dataInputStream));
                        Map<String, Object> desData = desData = (Map<String, Object>) objectInputStream.readObject();
                        dataList = (List<Map<String, Object>>) desData.get(TIG_TABLE_DATA);
                        columDataList = (List<Map<String, Object>>) desData.get(TIG_COLUMN_DATA);
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                IOUtils.closeQuietly(objectInputStream);
                IOUtils.closeQuietly(dataInputStream);
                // ?statics.stopTimer(ro_token, null);
            }

            /***********************3、变更数据到系统*****************************/
            Session session = this.dao.getSession();
            // ?long wi_token = 0;
            try {
                // ?wi_token = statics.startTimer(SynDataLog.WRITE_IN);
                // 字段表插入
                try {
                    for (Map<String, Object> dataItem : columDataList) {
                        String tig_owner_uuid = (String) dataItem.get("tig_owner_uuid");
                        String tig_column_name = (String) dataItem.get("tig_column_name");
                        String querySql2 = "select count(1) as count_ from tig_column_data o where o.tig_owner_uuid=:tig_owner_uuid and o.tig_column_name=:tig_column_name";
                        Query query = session.createSQLQuery(querySql2);
                        query.setString("tig_owner_uuid", tig_owner_uuid);
                        query.setString("tig_column_name", tig_column_name);
                        boolean notExist = ((Number) query.uniqueResult()).intValue() <= 0;
                        if (notExist) {
                            String insertSql = "insert into tig_column_data "
                                    + "(,tig_owner_uuid,tig_column_name,tig_data_type,data_time,data_float,data_number,data_clob,data_blob,data_char,data_date,data_varchar_2) "
                                    + "values "
                                    + "(,:tig_owner_uuid,:tig_column_name,:tig_data_type,:data_time,:data_float,:data_number,:data_clob,:data_blob,:data_char,:data_date,:data_varchar_2) ";
                            for (String columKey : dataItem.keySet()) {
                                if (dataItem.get(columKey) == null) {
                                    insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                                }
                            }
                            insertSql = insertSql.replace("(,", "(");
                            SQLQuery sqlquery = session.createSQLQuery(insertSql);
                            sqlquery.setString("tig_owner_uuid", (String) dataItem.get("tig_owner_uuid"));
                            sqlquery.setString("tig_column_name", (String) dataItem.get("tig_column_name"));
                            sqlquery.setString("tig_data_type", (String) dataItem.get("tig_data_type"));
                            Object obj = null;
                            if ((obj = dataItem.get("data_time")) != null) {
                                sqlquery.setTimestamp("data_time", (Date) obj);
                            }
                            if ((obj = dataItem.get("data_float")) != null) {
                                sqlquery.setFloat("data_float", ((Number) obj).floatValue());
                            }
                            if ((obj = dataItem.get("data_number")) != null) {
                                sqlquery.setDouble("data_number", ((Number) obj).doubleValue());
                            }
                            if ((obj = dataItem.get("data_clob")) != null) {
                                Clob clob = (Clob) obj;
                                sqlquery.setText("data_clob", IOUtils.toString(clob.getCharacterStream()));
                            }
                            if ((obj = dataItem.get("data_blob")) != null) {
                                Blob blob = (Blob) obj;
                                sqlquery.setBinary("data_blob", IOUtils.toByteArray(blob.getBinaryStream()));
                            }
                            if ((obj = dataItem.get("data_char")) != null) {
                                sqlquery.setString("data_char", (String) obj);
                            }
                            if ((obj = dataItem.get("data_date")) != null) {
                                sqlquery.setDate("data_date", (Date) obj);
                            }
                            if ((obj = dataItem.get("data_varchar_2")) != null) {
                                sqlquery.setString("data_varchar_2", (String) obj);
                            }
                            sqlquery.executeUpdate();
                        }
                    }
                } finally {
                    // ?statics.stopTimer(wi_token, null);
                }
                // ?long wic_token = 0;
                try {
                    // ?wic_token = statics.startTimer(SynDataLog.WRITE_IN +
                    // SynDataLog.SUB_CLOB);
                    // 大字段的处理
                    for (Map<String, Object> dataItem : CLOBList) {
                        String tig_owner_uuid = (String) dataItem.get("tig_owner_uuid");
                        String tig_column_name = (String) dataItem.get("tig_column_name");
                        String clobQuSql = "select count(1) as count_ from tig_column_clob t where t.tig_owner_uuid=:tig_owner_uuid and t.tig_column_name=:tig_column_name";
                        Query query = session.createSQLQuery(clobQuSql);
                        query.setString("tig_owner_uuid", tig_owner_uuid);
                        query.setString("tig_column_name", tig_column_name);
                        int zcount = ((Number) query.uniqueResult()).intValue();
                        if (zcount == 0) {
                            String insertSql = "insert into tig_column_clob "
                                    + "(tig_owner_uuid,tig_column_name,data_uuid,composite_key,data_clob,data_status,direction,stable_name,syn_time,create_time) "
                                    + "values (?,?,?,?,?,?,?,?,?,?)";
                            SQLQuery sqlquery = session.createSQLQuery(insertSql);
                            sqlquery.setString(0, (String) dataItem.get("tig_owner_uuid"));
                            sqlquery.setString(1, (String) dataItem.get("tig_column_name"));
                            sqlquery.setString(2, (String) dataItem.get("data_uuid"));
                            sqlquery.setString(3, (String) dataItem.get("composite_key"));
                            // data_clob
                            Clob clob = (Clob) dataItem.get("data_clob");
                            sqlquery.setText(4, clob == null ? null : IOUtils.toString(clob.getCharacterStream()));
                            // data_status
                            sqlquery.setInteger(5, 2);
                            // direction
                            sqlquery.setInteger(6, ((Number) dataItem.get("direction")).intValue());
                            sqlquery.setString(7, (String) dataItem.get("stable_name"));
                            sqlquery.setTimestamp(8, synTime);
                            sqlquery.setTimestamp(9, (Date) dataItem.get("create_time"));
                            sqlquery.executeUpdate();
                        } else {// 已处理过的数据，可能失败或光闸截断重传的数据，清空反馈数据的同步时间，使其再同步（更新成功才落地，此时一定成功过）
                            Query query2 = session
                                    .createSQLQuery("select count(1) from tig_table_data t where t.suuid=:tig_owner_uuid and t.feedback=:tig_column_name and t.status = 4");
                            query2.setString("tig_owner_uuid", tig_owner_uuid);
                            query2.setString("tig_column_name", tig_column_name);
                            int fcount = ((Number) query2.uniqueResult()).intValue();
                            if (fcount == 0) {// 不成功，重置状态使其再还原
                                SQLQuery sqlquery = session
                                        .createSQLQuery("update tig_column_clob l set data_status = 2,syn_time=sysdate where l.tig_owner_uuid=:tig_owner_uuid and l.tig_column_name=:tig_column_name");// ,backup_time=:backup_time
                                sqlquery.setString("tig_owner_uuid", tig_owner_uuid);
                                sqlquery.setString("tig_column_name", tig_column_name);
                                // sqlquery.setTimestamp("backup_time",
                                // synTime);
                                sqlquery.executeUpdate();
                            } else { // 已经成功还原过,触发反馈数据
                                SQLQuery sqlquery = session
                                        .createSQLQuery("update tig_table_data t set t.syn_time=null where t.suuid=:tig_owner_uuid and t.feedback=:tig_column_name and t.status = 4");
                                sqlquery.setString("tig_owner_uuid", tig_owner_uuid);
                                sqlquery.setString("tig_column_name", tig_column_name);
                                // sqlquery.setTimestamp("syn_time", synTime);
                                sqlquery.executeUpdate();
                            }
                        }
                    }
                } finally {
                    // ?statics.stopTimer(wic_token, null);
                }
                // 插入数据
                // ?long wil_token = 0;
                try {
                    // ?wil_token = statics.startTimer(SynDataLog.WRITE_IN +
                    // SynDataLog.SUB_LIST);
                    for (Map<String, Object> dataItem : dataList) {
                        String uuid = (String) dataItem.get("uuid");
                        /******比较数据是否已经处理过*********/
                        String querySql2 = "select count(1) as count_ from tig_table_data o where o.uuid=:uuid";// 查询是否存在数据
                        Query query = session.createSQLQuery(querySql2).setString("uuid", uuid);
                        boolean notExist = ((Number) query.uniqueResult()).intValue() <= 0;
                        if (notExist) {
                            // 插入数据
                            String insertSql = "insert into tig_table_data "
                                    + "(,uuid,create_time,order_id,action,feedback,direction,remark,stable_name,status,suuid,syn_time,composite_key,cloum_num) "
                                    + "values "
                                    + "(,:uuid,:create_time,:order_id,:action,:feedback,:direction,:remark,:stable_name,:status,:suuid,:syn_time,:composite_key,:cloum_num)";
                            for (String dataKey : dataItem.keySet()) {
                                if (dataItem.get(dataKey) == null && !dataKey.equals("syn_time")) {
                                    insertSql = insertSql.replace("," + dataKey, "").replace(",:" + dataKey, "");
                                }
                            }
                            insertSql = insertSql.replace("(,", "(");
                            SQLQuery sqlquery = session.createSQLQuery(insertSql);
                            sqlquery.setString("uuid", (String) dataItem.get("uuid"));
                            sqlquery.setString("suuid", (String) dataItem.get("suuid"));
                            sqlquery.setString("stable_name", (String) dataItem.get("stable_name"));
                            sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                            Object obj = null;
                            if ((obj = dataItem.get("order_id")) != null) {
                                sqlquery.setInteger("order_id", ((Number) obj).intValue());
                            }
                            if ((obj = dataItem.get("action")) != null) {
                                sqlquery.setInteger("action", ((Number) obj).intValue());
                            }
                            if ((obj = dataItem.get("feedback")) != null) {
                                sqlquery.setString("feedback", (String) obj);
                            }
                            if ((obj = dataItem.get("direction")) != null) {
                                sqlquery.setInteger("direction", ((Number) obj).intValue());
                            }
                            if ((obj = dataItem.get("remark")) != null) {
                                sqlquery.setString("remark", (String) obj);
                            }
                            if ((obj = dataItem.get("status")) != null) {
                                sqlquery.setInteger("status", ((Number) obj).intValue());
                            }
                            if ((obj = dataItem.get("composite_key")) != null) {
                                sqlquery.setString("composite_key", (String) obj);
                            }
                            if ((obj = dataItem.get("cloum_num")) != null) {
                                sqlquery.setLong("cloum_num", ((Number) obj).longValue());
                            }
                            sqlquery.setTimestamp("syn_time", synTime);// (Date)
                            // obj//
                            // 记录从光闸取下来的时间
                            sqlquery.executeUpdate();
                        } else {// 已处理过的数据，可能失败或光闸截断重传的数据，清空反馈数据的同步时间，使其再同步
                            // 判断是否还原成功
                            Query query2 = session
                                    .createSQLQuery("select count(1) as count_ from tig_table_data t where t.suuid=:suuid and t.status=6 and t.feedback = '4'");
                            int fcount = ((Number) query2.setString("suuid", uuid).uniqueResult()).intValue();
                            if (fcount == 0) {// 不成功或还未还原，重新还原
                                Query query3 = session
                                        .createSQLQuery("update tig_table_data t set t.status=2,syn_time=sysdate where t.uuid=:uuid");
                                query3.setString("uuid", uuid).executeUpdate();
                            } else { // 已经成功还原过,触发反馈数据
                                Query query3 = session
                                        .createSQLQuery("update tig_table_data t set t.syn_time=null where t.suuid=:suuid and t.status=6 and t.feedback = '4'");
                                query3.setString("suuid", uuid).executeUpdate();
                            }
                        }
                    }
                } finally {
                    // ?statics.stopTimer(wil_token, null);
                }
            } catch (Exception e) {
                throw e;
            } finally {
            }
            /***********************4、修改流入前置机状态（避免多次提取）*****************************/
            // ?long os_token = 0;
            try {
                // ?os_token = statics.startTimer(SynDataLog.OUT_STATE);
                // 流出为2时更新状态为还原状态
                if (StringUtils.isNotBlank(batchId)) {
                    Query query = sessionIn
                            .createSQLQuery("update tig_column_clob t set t.data_status=-4 where t.tig_owner_uuid=:buid and t.tig_column_name= 'tig_table_data'");
                    query.setString("buid", batchId).executeUpdate();
                }
                for (Map<String, Object> dataItem : CLOBList) {// 更新同步时间
                    String tig_owner_uuid = (String) dataItem.get("tig_owner_uuid");
                    String tig_column_name = (String) dataItem.get("tig_column_name");
                    Query query = sessionIn
                            .createSQLQuery("update tig_column_clob o set o.data_status=4 where o.tig_owner_uuid=:tig_owner_uuid and o.tig_column_name=:tig_column_name");
                    query.setString("tig_owner_uuid", tig_owner_uuid).setString("tig_column_name", tig_column_name);
                    query.executeUpdate();
                }
            } catch (Exception e) {
                throw e;
            } finally {
                // ?statics.stopTimer(os_token, null);
            }
        } catch (Exception e) {
            logger.error("**********************synInData " + e.getMessage(), e);
            throw e;
        }
        return fullFetch;
    }

    @Override
    public boolean synBackData(Map<String, SysProperties> sysPropertiessDeprecated, String params) throws Exception {
        return synBackData(sysPropertiessDeprecated, params, false);
    }

    @Override
    public boolean synBackData(Map<String, SysProperties> sysPropertiessDeprecated, String params, boolean withClob)
            throws Exception {
        /**
         * 提取前置机的数据
         * 1、提取前置机数据(同步中，同步成功/同步失败(握手))
         * 2、修改流入前置机状态（避免多少提取）
         * 3、修改流出前置机状态（清理数据使用）
         */
        boolean fullFetch = false;
        try {
            Date synTime = new Date();// 同步时间
            /***********************1、提取同步数据(同步中，同步成功/同步失败(握手))****************************/
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> CLOBList = new ArrayList<Map<String, Object>>();
            Session session = dao.getSession();
            try {
                if (withClob == true) /* 大字段单独处理 */ {
                    // 提取大字段
                    StringBuilder sqlB = new StringBuilder(
                            "select list.* from (select * from tig_column_clob t where (t.data_status=2 ");
                    // 在1/2个重发周期内interval尝试还原32次,即8小时为一个重发周期,在4个小时内尝试32次,7.5分钟还原一次
                    sqlB.append(" or (t.data_status=5 and t.backup_time < cast((sysdate - ")
                            .append(interval / (2 * 32)).append("/86400) as TIMESTAMP))");
                    sqlB.append(") and t.direction = ").append(direction == 1 ? 2 : 1);
                    sqlB.append(") list where rownum<=").append(dataNum);
                    CLOBList = session.createSQLQuery(sqlB.toString()).setResultTransformer(INSTANCE).list();
                    if (CLOBList.isEmpty()) {
                        return fullFetch;
                    }
                    fullFetch = CLOBList.size() == dataNum;
                } else {
                    // attention：字段完整性由流入synInData方法保障
                    StringBuilder sqlB = new StringBuilder(
                            "select list.* from (select * from tig_table_data o where (o.status=2 ");
                    // 在1/2个重发周期内interval尝试还原32次,即8小时为一个重发周期,在4个小时内尝试32次,7.5分钟还原一次
                    sqlB.append(" or (o.status=5 and o.backup_time < cast((sysdate - ").append(interval / (2 * 32))
                            .append("/86400) as TIMESTAMP))");
                    sqlB.append(") and o.direction = ").append(direction == 1 ? 2 : 1);
                    if (StringUtils.isBlank(params) == false) {// 带约束
                        sqlB.append(params);
                    }
                    sqlB.append(" order by o.order_id asc) list where rownum<=").append(dataNum);
                    // 提取同步中及反馈的数据
                    dataList = session.createSQLQuery(sqlB.toString()).setResultTransformer(INSTANCE).list();
                    if (dataList.isEmpty()) {
                        return fullFetch;
                    }
                    fullFetch = dataList.size() == dataNum;
                }
            } catch (Exception e) {
                throw e;
            } finally {
            }

            /***********************2、变更数据到系统*****************************/
            // 字段表插入
            try {
                // 插入数据
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = (String) dataItem.get("uuid");

                    /*
                     * Query fkquery = session3.createSQLQuery(
                     * "select count(1) from tig_table_data t where t.suuid=:uuid and t.status = 6"
                     * ); fkquery.setString("uuid", uuid); boolean firstCover =
                     * ((Number) fkquery.uniqueResult()).intValue() <= 0; //
                     * 查看是否已经还原过,且还原失败等待主数据
                     */
                    // 业务字段表
                    String subQuerySql = "select t.* from tig_column_data t where t.tig_owner_uuid = :uuid";
                    Query query = session.createSQLQuery(subQuerySql);
                    query.setResultTransformer(INSTANCE);
                    List<Map<String, Object>> columDataFormat = query.setString("uuid", uuid).list();
                    Integer feedback = null;
                    try {
                        // 4还原成功、3还原失败、5在1/2个重发周期内interval尝试还原32次,即8小时为一个重发周期,在4个小时内尝试32次,7.5分钟还原一次
                        feedback = backData(session, dataItem, columDataFormat) ? 4 : 5;// 还原数据
                    } catch (Exception e) {

                        feedback = 3;
                    }
                    Object synInDateTime = dataItem.get("syn_time");
                    if (feedback == 5 && synInDateTime != null
                            && ((Date) synInDateTime).getTime() + (interval / 2) * 1000 < System.currentTimeMillis()) {
                        // 尝试还原超时,超过时间((Date) backupTime).getTime() + (interval
                        // / 2) * 1000 算超时
                        feedback = 3;
                    }
                    // 更新结果
                    Query query2 = session
                            .createSQLQuery("update tig_table_data set status=:status,backup_time=sysdate where uuid=:uuid");
                    query2.setString("uuid", uuid);
                    query2.setInteger("status", feedback);
                    // query2.setTimestamp("backup_time", synTime);
                    query2.executeUpdate();
                    if (dataItem.get("backup_time") == null) {
                        // 插入反馈数据
                        SQLQuery fksqlquery = session.createSQLQuery("insert into tig_table_data "
                                + "(uuid,status,suuid,feedback,direction,order_id,create_time) values "
                                + "(sys_guid(),6,:uuid,:feedback," + direction + ",syn_order_id.NEXTVAL,:create_time)");
                        fksqlquery.setString("uuid", uuid);
                        fksqlquery.setTimestamp("create_time", synTime);
                        fksqlquery.setString("feedback", feedback.toString());
                        fksqlquery.executeUpdate();
                    } else {
                        Object foodback = dataItem.get("status");
                        String updataSql = "update tig_table_data t set t.feedback = :feedback,t.syn_time = null where t.suuid=:suuid and t.status = 6";
                        // 反馈状态有更新(重置remark,使重新打包)
                        if (foodback == null || ((Number) foodback).intValue() != feedback) {
                            updataSql = "update tig_table_data t set t.feedback = :feedback,t.syn_time = null,t.remark=null where t.suuid=:suuid and t.status = 6";
                        }
                        Query fkSqlInsert = session.createSQLQuery(updataSql);
                        fkSqlInsert.setString("suuid", uuid);
                        fkSqlInsert.setString("feedback", feedback.toString());// feedback作为反馈状态
                        fkSqlInsert.executeUpdate();
                    }
                }
                // 大字段的处理
                for (Map<String, Object> dataItem : CLOBList) {
                    String tig_owner_uuid = (String) dataItem.get("tig_owner_uuid");
                    String tig_column_name = (String) dataItem.get("tig_column_name");
                    /*
                     * Query fkquery = session3.createSQLQuery(
                     * "select count(1) from tig_table_data t where t.suuid=:tig_owner_uuid and t.feedback=:tig_column_name"
                     * ); fkquery.setString("tig_owner_uuid", tig_owner_uuid);
                     * fkquery.setString("tig_column_name", tig_column_name);
                     * boolean firstCover = ((Number)
                     * fkquery.uniqueResult()).intValue() <= 0; //
                     * 查看是否已经还原过,且还原失败等待主数据
                     */
                    Integer feedstatus = null;
                    try {
                        // 4还原成功、3还原失败、5在1/2个重发周期内interval尝试还原32次,即8小时为一个重发周期,在4个小时内尝试32次,7.5分钟还原一次
                        feedstatus = backDataCLOB(session, dataItem) ? 4 : 5;
                    } catch (Exception e) {
                        feedstatus = 3;
                    }
                    Object synInDateTime = dataItem.get("syn_time");
                    if (feedstatus == 5 && synInDateTime != null
                            && ((Date) synInDateTime).getTime() + (interval / 2) * 1000 < System.currentTimeMillis()) {
                        // 尝试还原超时,超过时间((Date) backupTime).getTime() + (interval
                        // / 2) * 1000 算超时
                        feedstatus = 3;
                    }
                    // 更新结果
                    Query query2 = session
                            .createSQLQuery("update tig_column_clob l set l.data_status = :data_status,l.backup_time = sysdate where l.tig_owner_uuid=:tig_owner_uuid and l.tig_column_name=:tig_column_name");
                    // query2.setTimestamp("syn_time", synTime);//
                    // attention:还原端的syn_time会多次写，用于记录最后一次还原时间
                    // query2.setTimestamp("backup_time", synTime);
                    query2.setInteger("data_status", feedstatus);
                    query2.setString("tig_owner_uuid", tig_owner_uuid);
                    query2.setString("tig_column_name", tig_column_name);
                    query2.executeUpdate();
                    if (dataItem.get("backup_time") == null) {
                        // 插入反馈数据
                        SQLQuery fkSqlInsert = session.createSQLQuery("insert into tig_table_data "
                                + "(uuid,status,suuid,feedback,direction,order_id,cloum_num,create_time) values "
                                + "(sys_guid(),7,:uuid,:feedback," + direction
                                + ",syn_order_id.NEXTVAL,:cloum_num,:create_time)");
                        fkSqlInsert.setString("uuid", tig_owner_uuid);
                        fkSqlInsert.setInteger("cloum_num", feedstatus);// cloum_num作为反馈状态
                        fkSqlInsert.setTimestamp("create_time", synTime);
                        fkSqlInsert.setString("feedback", tig_column_name);
                        fkSqlInsert.executeUpdate();
                    } else {
                        Object foodback = dataItem.get("data_status");
                        String updataSql = "update tig_table_data t set t.cloum_num = :cloum_num,t.syn_time = null where suuid=:tig_owner_uuid and t.feedback=:tig_column_name";
                        // 反馈状态有更新(重置remark,使重新打包)
                        if (foodback == null || ((Number) foodback).intValue() != feedstatus) {
                            updataSql = "update tig_table_data t set t.cloum_num = :cloum_num,t.syn_time = null,t.remark=null where suuid=:tig_owner_uuid and t.feedback=:tig_column_name";
                        }
                        Query fkSqlInsert = session.createSQLQuery(updataSql);
                        fkSqlInsert.setInteger("cloum_num", feedstatus);// cloum_num作为反馈状态
                        fkSqlInsert.setString("tig_owner_uuid", tig_owner_uuid);
                        fkSqlInsert.setString("tig_column_name", tig_column_name);
                        fkSqlInsert.executeUpdate();
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
            }
        } catch (Exception e) {
            logger.error("**********************synInData " + e.getMessage(), e);
            throw e;
        }
        return fullFetch;
    }

    @Override
    public boolean synInDataFeedback(Map<String, SysProperties> sysPropertiess) throws Exception {
        /**
         * 提取前置机的数据
         * 1、提取前置机数据(同步中，同步成功/同步失败(握手))
         * 2、变更数据到系统,还原数据
         * 3、修改流入前置机状态（避免多少提取）
         * 4、修改流出前置机状态（清理数据使用）
         */
        boolean fullFetch = false;
        try {
            /***********************2、提取前置机数据(同步中，同步成功/同步失败(握手))****************************/
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            List<String> dataUuids = new ArrayList<String>();
            Date synDate = new Date();
            String batchId = null;
            ObjectInputStream objectInputStream = null;
            ByteArrayInputStream byteArrayInputStream = null;
            Session session2 = this.getDao(ExchangeConfig.NEWAPASIN).getSession();
            // ?long or_token = 0;
            try {
                StringBuilder sqlB = new StringBuilder(
                        "select t.tig_owner_uuid,t.tig_column_name,t.data_clob from tig_column_clob t where length(t.data_clob) > 0 and t.data_status = -12 and t.direction = ");
                sqlB.append(direction == 1 ? 2 : 1);
                sqlB.append(" order by t.create_time asc");
                // 提取同步中及反馈的数据
                Query query = session2.createSQLQuery(sqlB.toString()).setMaxResults(1);// 每次取一个批次
                List<Map<String, Object>> batchDatas = query.setResultTransformer(INSTANCE).list();
                if (batchDatas.isEmpty()) {
                    return fullFetch;
                }
                fullFetch = batchDatas.size() == 1;
                Map<String, Object> batchData = batchDatas.get(0);
                Clob clob = (Clob) batchData.get("data_clob");
                batchId = (String) batchData.get("tig_owner_uuid");
                if (clob == null || clob.length() <= 0) {
                    logger.info("feedBack batchId:" + batchId + ",clob length:0,waiting...");
                    // 解决光闸大字段二次入库
                    return fullFetch;
                }
                String serData = IOUtils.toString(clob.getCharacterStream());
                BASE64Decoder dec = new BASE64Decoder();
                byteArrayInputStream = new ByteArrayInputStream(dec.decodeBuffer(serData));// Base64.decodeBase64(serData)
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Map<String, Object> desData = (Map<String, Object>) objectInputStream.readObject();
                dataList = (List<Map<String, Object>>) desData.get(TIG_TABLE_DATA);
            } catch (Exception e) {
                throw e;
            } finally {
                // ?statics.stopTimer(or_token, null);
            }

            /***********************3、变更数据到系统*****************************/
            Session session = this.dao.getSession();
            // ?long iw_token = 0;
            try {
                // ?iw_token = statics.startTimer(SynDataLog.IN_WRITE);
                // 插入数据
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = (String) dataItem.get("uuid");
                    String suuid = (String) dataItem.get("suuid");
                    String feedback = (String) dataItem.get("feedback");
                    dataUuids.add(uuid);
                    Object obj = dataItem.get("status");
                    int status = obj == null ? 0 : ((Number) obj).intValue();
                    /******比较数据是否已经处理过*********/
                    String querySql2 = "select count(1) as count_ from tig_table_data o where o.uuid=:uuid";// 查询是否存在数据
                    Query query = session.createSQLQuery(querySql2).setString("uuid", uuid);
                    boolean notExist = ((Number) query.uniqueResult()).intValue() <= 0;
                    if (notExist) {
                        // 插入数据
                        String insertSql = "insert into tig_table_data "
                                + "(,uuid,create_time,order_id,action,feedback,direction,remark,stable_name,status,suuid,syn_time,composite_key,cloum_num) "
                                + "values "
                                + "(,:uuid,:create_time,:order_id,:action,:feedback,:direction,:remark,:stable_name,:status,:suuid,:syn_time,:composite_key,:cloum_num)";
                        for (String dataKey : dataItem.keySet()) {
                            if (dataItem.get(dataKey) == null) {
                                insertSql = insertSql.replace("," + dataKey, "").replace(",:" + dataKey, "");
                            }
                        }
                        insertSql = insertSql.replace("(,", "(");
                        SQLQuery sqlquery = session.createSQLQuery(insertSql);
                        sqlquery.setString("uuid", (String) dataItem.get("uuid"));
                        sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                        if ((obj = dataItem.get("order_id")) != null) {
                            sqlquery.setInteger("order_id", ((Number) obj).intValue());
                        }
                        if ((obj = dataItem.get("action")) != null) {
                            sqlquery.setInteger("action", ((Number) obj).intValue());
                        }
                        if ((obj = dataItem.get("feedback")) != null) {
                            sqlquery.setString("feedback", (String) obj);
                        }
                        if ((obj = dataItem.get("direction")) != null) {
                            sqlquery.setInteger("direction", ((Number) obj).intValue());
                        }
                        if ((obj = dataItem.get("remark")) != null) {
                            sqlquery.setString("remark", (String) obj);
                        }
                        if ((obj = dataItem.get("stable_name")) != null) {
                            sqlquery.setString("stable_name", (String) obj);
                        }
                        if ((obj = dataItem.get("status")) != null) {
                            sqlquery.setInteger("status", status);
                        }
                        if ((obj = dataItem.get("suuid")) != null) {
                            sqlquery.setString("suuid", (String) obj);
                        }
                        if ((obj = dataItem.get("syn_time")) != null) {
                            sqlquery.setTimestamp("syn_time", (Date) dataItem.get("syn_time"));
                        }
                        if ((obj = dataItem.get("composite_key")) != null) {
                            sqlquery.setString("composite_key", (String) obj);
                        }
                        if ((obj = dataItem.get("cloum_num")) != null) {
                            sqlquery.setInteger("cloum_num", ((Number) obj).intValue());
                        }
                        sqlquery.executeUpdate();
                        SQLQuery upSqlquery = null;
                        if (status == 7) {// 解析大字段反馈的数据
                            upSqlquery = session
                                    .createSQLQuery("update tig_column_clob l set l.data_status=:data_status,l.backup_time=:backup_time,l.feedback_time=:feedback_time where l.tig_owner_uuid=:suuid and l.tig_column_name=:feedback");
                            obj = dataItem.get("cloum_num"); // cloum_num大字段反馈状态
                            upSqlquery.setInteger("data_status", obj == null ? 3 : ((Number) obj).intValue()); // 默认3标识失败
                            upSqlquery.setString("feedback", feedback);
                        } else {// 解析反馈的数据
                            upSqlquery = session
                                    .createSQLQuery("update tig_table_data l set l.status=:feedback,l.backup_time=:backup_time,l.feedback_time=:feedback_time where l.uuid=:suuid");
                            upSqlquery.setInteger("feedback", Integer.parseInt(feedback));

                        }
                        upSqlquery.setString("suuid", suuid);
                        upSqlquery.setTimestamp("feedback_time", synDate);
                        upSqlquery.setTimestamp("backup_time", (Date) dataItem.get("create_time"));
                        upSqlquery.executeUpdate();
                    } else {// 已处理过的数据，可能失败或光闸截断重传的数据，清空反馈数据的同步时间，使其再同步
                        // 6.7时反馈数据解析成功过，需修改流入前置机的状态，修改流出前置机说明数据的状态
                        SQLQuery upSqlquery = null;
                        if (status == 7) {
                            upSqlquery = session
                                    .createSQLQuery("update tig_column_clob l set l.data_status=:data_status where l.tig_owner_uuid=:suuid and l.tig_column_name=:feedback");
                            obj = dataItem.get("cloum_num");// cloum_num大字段反馈状态
                            upSqlquery.setInteger("data_status", obj == null ? 3 : ((Number) obj).intValue());
                            upSqlquery.setString("feedback", feedback);
                        } else {
                            upSqlquery = session
                                    .createSQLQuery("update tig_table_data l set l.status=:feedback where l.uuid=:suuid");
                            upSqlquery.setInteger("feedback", Integer.parseInt(feedback));
                        }
                        upSqlquery.setString("suuid", suuid);
                        upSqlquery.executeUpdate();
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                // ?statics.stopTimer(iw_token, null);
            }
            /***********************4、修改流入前置机状态（避免多次提取）*****************************/
            Session sessionIn = this.getDao(ExchangeConfig.NEWAPASIN).getSession();
            // ?long ow_token = 0;
            try {
                // ?ow_token = statics.startTimer(SynDataLog.OUT_WRITE);
                if (StringUtils.isNotBlank(batchId)) {
                    Query query = sessionIn
                            .createSQLQuery("update tig_column_clob t set t.data_status=-14 where t.tig_owner_uuid=:buid and t.tig_column_name= 'tig_table_data'");
                    query.setString("buid", batchId).executeUpdate();
                }
            } catch (Exception e) {
                throw e;
            } finally {
                // ?statics.stopTimer(ow_token, null);
            }
        } catch (Exception e) {
            logger.error("**********************synInDataFeedback " + e.getMessage(), e);
            throw e;
        }
        return fullFetch;
    }

    /**
     * 还原数据:backDataPlaceHolder
     *
     * @param session
     * @param dataItem
     * @param columDataFormat
     * @throws Exception
     */
    public boolean backData(Session session, Map<String, Object> dataItem, List<Map<String, Object>> columDataFormat)
            throws Exception {
        String uuid = (String) dataItem.get("uuid");
        int action = ((Number) dataItem.get("action")).intValue();
        String suuid = (String) dataItem.get("suuid");
        String tableName = (String) dataItem.get("stable_name");
        try {
            if (action == 3) {// 删除
                String backDelSql = "";
                String compositeKey = (String) dataItem.get("composite_key");
                if (StringUtils.isNotBlank(compositeKey)) {// 联合主键（多对多关系表）
                    String[] compositeKeyArr = compositeKey.split(";");
                    String[] compositeValueArr = suuid.split(";");
                    String where = compositeKeyArr[0] + "='" + compositeValueArr[0] + "'";
                    for (int i = 1; i < compositeKeyArr.length; i++) {
                        where += " and " + compositeKeyArr[i] + "='" + compositeValueArr[i] + "'";
                    }
                    backDelSql = "delete from " + tableName + " where " + where;
                } else {
                    backDelSql = "delete from " + tableName + " where uuid = '" + suuid + "'";
                }
                session.createSQLQuery(backDelSql).executeUpdate();
                // 删除还原数据为删除时触发器生成的记录,删除没有触发条件
                String delTigData = "delete from tig_table_data d where d.action=3 and d.suuid=:suuid and d.stable_name=:stable_name and d.direction=:direction";
                Query delTigQuery = session.createSQLQuery(delTigData);
                delTigQuery.setInteger("direction", direction).setString("stable_name", tableName);
                delTigQuery.setString("suuid", suuid).executeUpdate();
            } else {// 更新插入
                // 处理字段数据
                if (columDataFormat != null && !columDataFormat.isEmpty()) {
                    // 还原附件(可重发)
                    if (action == 1 && tableName.toUpperCase().equals(ExchangeConfig.FILETABLE)) {
                        File zipFile = this.downFileFromFtp(suuid, inFtpDataBean);// 下载附件
                        Object fileExist = dataItem.get("cloum_num");
                        if (fileExist != null
                                && ((Number) fileExist).longValue() != (zipFile == null ? 0 : zipFile.length())) {
                            return false;// 附件还未到达,稍后重试
                        } else if (zipFile != null) {
                            // 当REPO_FILE插入时,根据suuid下载附件
                            Map<String, Object> physicalColumn = null;
                            Map<String, Object> fileValues = new HashMap<String, Object>();
                            for (Map<String, Object> columMap : columDataFormat) {
                                if (((String) columMap.get("tig_data_type")).equals("VARCHAR2")) {
                                    Object objVal = columMap.get("data_varchar_2");
                                    String objKey = (String) columMap.get("tig_column_name");
                                    fileValues.put(objKey, objVal);
                                    if (objKey != null && "PHYSICAL_FILE_ID".equals(objKey)) {
                                        // link physical column
                                        physicalColumn = columMap;
                                    }
                                }
                            }
                            MongoFileEntity entity = this.importFileZip(zipFile, fileValues);
                            if (physicalColumn != null && entity != null && entity.getPhysicalID() != null) {
                                // md5的附件流已存在,更新(重写)PhysicalID(考虑到操作数据库一次,提高性能)
                                physicalColumn.put("data_varchar_2", entity.getPhysicalID());
                            }
                        }
                    }
                    String columns = "";
                    String insertColumn = "";
                    String updateColumn = "";
                    List<Object> values = new ArrayList<Object>();
                    List<String> types = new ArrayList<String>();
                    for (Map<String, Object> columMap : columDataFormat) {
                        String columnName = (String) columMap.get("tig_column_name");
                        String columnType = (String) columMap.get("tig_data_type");
                        // 命中率高的放在前面
                        if (columnName.equals(ExchangeConfig.ISSYNBACKFIELD)) {
                            // attention:字段名都是大写
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(UUID.randomUUID().toString());
                            types.add("VARCHAR2");
                        } else if (columnType.equals("VARCHAR2")) {
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(columMap.get("data_varchar_2"));
                            types.add("VARCHAR2");
                        } else if (columnType.equals("TIMESTAMP(6)") && columMap.get("data_time") != null) {
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(columMap.get("data_time"));
                            types.add("TIMESTAMP(6)");
                        } else if (columnType.equals("NUMBER") && columMap.get("data_number") != null) {
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(columMap.get("data_number"));
                            types.add("NUMBER");
                        } else if (columnType.equals("FLOAT") && columMap.get("data_float") != null) {
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(columMap.get("data_float"));
                            types.add("FLOAT");
                        } else if (columnType.equals("CLOB") && columMap.get("data_clob") != null) {
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(columMap.get("data_clob"));
                            types.add("CLOB");
                        } else if (columnType.equals("BLOB") && columMap.get("data_blob") != null) {
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(columMap.get("data_blob"));
                            types.add("BLOB");
                        } else if (columnType.equals("CHAR") && columMap.get("data_char") != null) {
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(columMap.get("data_char"));
                            types.add("CHAR");
                        } else if (columnType.equals("DATE") && columMap.get("data_date") != null) {
                            columns += "," + columnName;
                            insertColumn += ",?";
                            updateColumn += "," + columnName + "=?";
                            values.add(columMap.get("data_date"));
                            types.add("DATE");
                        }
                    }
                    columns = columns.replaceFirst(",", "");
                    insertColumn = insertColumn.replaceFirst(",", "");
                    updateColumn = updateColumn.replaceFirst(",", "");
                    if (!columns.contains(ExchangeConfig.ISSYNBACKFIELD)) {
                        if (StringUtils.isBlank(columns)) {
                            columns += ExchangeConfig.ISSYNBACKFIELD;
                        } else {
                            columns += "," + ExchangeConfig.ISSYNBACKFIELD;
                        }
                        if (StringUtils.isBlank(insertColumn)) {
                            insertColumn += "?";
                        } else {
                            insertColumn += ",?";
                        }
                        if (StringUtils.isBlank(updateColumn)) {
                            updateColumn += ExchangeConfig.ISSYNBACKFIELD + "=?";
                        } else {
                            updateColumn += "," + ExchangeConfig.ISSYNBACKFIELD + "=?";
                        }
                        values.add(UUID.randomUUID().toString());
                        types.add("VARCHAR2");
                    }
                    // 插入更新数据
                    // 查看数据是否存在
                    String compositeKey = (String) dataItem.get("composite_key");
                    String where = "";
                    if (StringUtils.isNotBlank(compositeKey)) {// 联合主键（多对多关系表）
                        String[] compositeKeyArr = compositeKey.split(";");
                        String[] compositeValueArr = suuid.split(";");
                        where = compositeKeyArr[0] + "='" + compositeValueArr[0] + "'";
                        for (int i = 1; i < compositeKeyArr.length; i++) {
                            where += " and " + compositeKeyArr[i] + "='" + compositeValueArr[i] + "'";
                        }
                    } else {
                        where = " uuid = '" + suuid + "'";
                    }

                    String sqlStr = "select count(1) as count_ from " + tableName + " where " + where;
                    Query cQuery = session.createSQLQuery(sqlStr);
                    int count_ = ((Number) cQuery.uniqueResult()).intValue();
                    String sql = "";
                    if (action == 1 && count_ <= 0) {// 插入,存在数据时不做处理
                        sql = "insert into " + tableName + "(" + columns + ") values (" + insertColumn + ")";
                    } else if ((action == 2 && count_ <= 0)) {
                        return false;
                        // throw new RuntimeException("更新UUID：" + uuid +
                        // "数据失败,需要更新的数据还未到达:" + suuid);
                    } else {// 默认更新
                        sql = "update " + tableName + " set " + updateColumn + " where " + where;
                    }
                    SQLQuery dataSqlquery = session.createSQLQuery(sql);
                    for (int i = 0; i < types.size(); i++) {
                        String type = types.get(i);
                        Object obj = values.get(i);
                        // 命中率高的放在前面
                        if (type.equals("VARCHAR2")) {
                            dataSqlquery.setString(i, (String) obj);
                        } else if (type.equals("TIMESTAMP(6)")) {
                            dataSqlquery.setTimestamp(i, (Date) obj);
                        } else if (type.equals("NUMBER")) {
                            dataSqlquery.setDouble(i, ((Number) obj).doubleValue());
                        } else if (type.equals("FLOAT")) {
                            dataSqlquery.setFloat(i, ((Number) obj).floatValue());
                        } else if (type.equals("CLOB")) {
                            Clob clob = (Clob) obj;
                            dataSqlquery.setText(i, IOUtils.toString(clob.getCharacterStream()));
                        } else if (type.equals("BLOB")) {
                            Blob blob = (Blob) obj;
                            dataSqlquery.setBinary(i, IOUtils.toByteArray(blob.getBinaryStream()));
                        } else if (type.equals("CHAR")) {
                            dataSqlquery.setString(i, (String) obj);
                        } else if (type.equals("DATE")) {
                            dataSqlquery.setTimestamp(i, (Date) obj);
                        }
                    }
                    dataSqlquery.executeUpdate();
                }
            }
        } catch (Exception e) {
            logger.error("**********************backData " + uuid + " " + e.getMessage(), e);
            throw e;
        }
        return true;
    }

    /**
     * 还原数据:placeHolder
     *
     * @param dataItem
     * @param columDataFormat
     * @param ftpDataBean
     * @return
     * @throws Exception
     */
    public boolean backDataCLOB(Session session, Map<String, Object> dataItem) throws Exception {
        String tig_owner_uuid = (String) dataItem.get("tig_owner_uuid");
        String tig_column_name = (String) dataItem.get("tig_column_name");
        String suuid = getClobSuuid(tig_owner_uuid, tig_column_name);
        String tableName = (String) dataItem.get("stable_name");
        String dataUuid = (String) dataItem.get("data_uuid");
        String compositeKey = (String) dataItem.get("composite_key");
        try {
            String where = "";
            if (StringUtils.isNotBlank(compositeKey)) {// 联合主键（多对多关系表）
                String[] compositeKeyArr = compositeKey.split(";");
                String[] compositeValueArr = dataUuid.split(";");
                where = compositeKeyArr[0] + "='" + compositeValueArr[0] + "'";
                for (int i = 1; i < compositeKeyArr.length; i++) {
                    where += " and " + compositeKeyArr[i] + "='" + compositeValueArr[i] + "'";
                }
            } else {
                where = " uuid = '" + dataUuid + "'";
            }
            String querySql = "select count(1) as count_ from " + tableName + " where " + where;
            Query cQuery = session.createSQLQuery(querySql);
            int count_ = ((Number) cQuery.uniqueResult()).intValue();
            if (count_ == 0) {
                return false;// 主表记录还没到达
            }
            Clob clob = (Clob) dataItem.get("data_clob");
            String dataVal = clob == null ? "" : IOUtils.toString(clob.getCharacterStream());
            if (dataVal.equals(CLOB_EMPTY_FLAG)) {
                dataVal = null;
            } else if (dataVal.equals(suuid)) {
                File zipFile = this.downFileFromFtp(suuid, inFtpDataBean);// 下载附件
                if (zipFile == null) {
                    return false;// FTP内容未到达,等待稍后重试
                }
                dataVal = IOUtils.toString(zipFile.toURI()); // FTP内容替换大字段内容
            } else {
                // dataVal = dataVal;
            }
            String sql = "update " + tableName + " set " + tig_column_name + "=?," + ExchangeConfig.ISSYNBACKFIELD
                    + "=? where " + where;
            SQLQuery dataSqlquery = session.createSQLQuery(sql);
            dataSqlquery.setText(0, dataVal);
            dataSqlquery.setString(1, UUID.randomUUID().toString());
            dataSqlquery.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error(
                    "**********************backData " + tig_owner_uuid + " " + tig_column_name + " " + e.getMessage(),
                    e);
            throw e;
        }
    }

    public void updateCursor() throws Exception {
        Session ptSession = null;
        try {
            ptSession = this.dao.getSession();
            Query curQuery = ptSession.createSQLQuery(queryMaxOrderId);
            Number maxClearCursor = (Number) curQuery.uniqueResult();
            if (maxClearCursor != null) {
                currCursor = maxClearCursor.longValue();
            }
        } catch (Exception e) {
            logger.error("**********************updateCursor :", e);
            throw e;
        } finally {
        }
    }

    /**
     * 备份交换成功的数据，同时清理数据
     */
    @Override
    public boolean clearSynDataAllInOne(Map<String, SysProperties> sysPropertiess) throws Exception {
        if (unimasOffline()) {
            return false;// 离线则不清数据,保存反馈数据
        }
        boolean fullFetch = false;
        List<String> tigUuids = new ArrayList<String>();
        /*********************删除平台数据********************************/
        Session ptSession = null;
        try {
            ptSession = this.dao.getSession();
            // 查询要删除的数据
            StringBuilder sqlB = new StringBuilder("select t.uuid from tig_table_data t where t.status = 4");
            // sqlB.append(" and t.create_time < cast((sysdate - ");
            // sqlB.append(clearInterval).append("/86400) as TIMESTAMP)");//
            // 平台数据保留长3倍光闸数据的时间,平台保留1.5天
            sqlB.append(" and ((t.direction = ? and t.order_id <= ?) or (t.direction = ? and t.order_id <= ?))");
            Query query = ptSession.createSQLQuery(sqlB.toString());
            query.setInteger(0, getDirection());// 清理当前应用生成的数据
            query.setLong(1, currCursor);
            query.setInteger(2, getDirection() == 1 ? 2 : 1);// 清理接收到的数据
            query.setLong(3, feedCursor);
            query.setMaxResults(dataNum * 10);// 单次清理100条记录
            List<Map<String, Object>> dataList = query.setResultTransformer(INSTANCE).list();
            fullFetch = dataList.size() == dataNum * 10;
            for (Map<String, Object> dataItem : dataList) {
                String uuid = (String) dataItem.get("uuid");
                tigUuids.add(uuid);
            }
            // 清理数据
            deleteTableData(tigUuids);
        } catch (Exception e) {
            logger.error("**********************clearSynDataAllInOne :", e);
            throw e;
        } finally {
        }
        return fullFetch;
    }

    public boolean deleteTableData(Collection<String> tigUuids) throws Exception {
        if (tigUuids == null || tigUuids.isEmpty()) {
            return false;
        }
        Set<String> batchUuids = new HashSet<String>();
        List<String> delTigUuids = new ArrayList<String>();
        List<Map<String, Object>> clobLists = new ArrayList<Map<String, Object>>();
        try {
            /*********************删除平台数据********************************/
            Session ptSession = null;
            try {
                ptSession = dao.getSession();
                // 查询要删除的数据(包括反馈数据)
                String selDataSql = "select t.uuid,t.suuid,t.remark,t.stable_name from tig_table_data t where t.uuid in (:uuids) or (t.status in (6,7) and t.suuid in (:suuids))";
                Query queryDeleteData = ptSession.createSQLQuery(selDataSql);
                queryDeleteData.setParameterList("uuids", tigUuids);
                queryDeleteData.setParameterList("suuids", tigUuids);
                List<Map<String, Object>> dataList = queryDeleteData.setResultTransformer(INSTANCE).list();
                for (Map<String, Object> dataItem : dataList) {
                    delTigUuids.add((String) dataItem.get("uuid"));
                    String suuid = (String) dataItem.get("suuid");
                    String remark = (String) dataItem.get("remark");
                    String tableName = (String) dataItem.get("stable_name"); // 附件反馈成功
                    if (tableName != null && tableName.toUpperCase().equals(ExchangeConfig.FILETABLE)) {
                        delFileFromFtp(suuid, outFtpDataBean);// 清理流出
                    }
                    if (StringUtils.isNotBlank(remark)) {
                        delFileFromFtp(remark, outFtpDataBean);// 清理流出
                        batchUuids.add(remark);
                    }
                }

                // 清理字段数据
                String delSubSql = "delete from tig_column_data t where t.tig_owner_uuid in (:uuids)";
                ptSession.createSQLQuery(delSubSql).setParameterList("uuids", delTigUuids).executeUpdate();
                // 清理记录信息(包括反馈数据)
                String delSql = "delete from tig_table_data t where t.uuid in (:uuids)";
                ptSession.createSQLQuery(delSql).setParameterList("uuids", delTigUuids).executeUpdate();

                // 大字段表的处理
                String selClobSql = "select t.tig_owner_uuid,t.tig_column_name from tig_column_clob t where t.data_status=4 and t.tig_owner_uuid in (:uuids)";
                Query queryDeleteClob = ptSession.createSQLQuery(selClobSql);
                queryDeleteClob.setParameterList("uuids", delTigUuids);
                clobLists = queryDeleteClob.setResultTransformer(INSTANCE).list();
                for (Map<String, Object> clobItem : clobLists) {
                    String tig_owner_uuid = (String) clobItem.get("tig_owner_uuid");
                    String tig_column_name = (String) clobItem.get("tig_column_name");
                    String delClobSql = "delete from tig_column_clob t where t.tig_owner_uuid=:tig_owner_uuid and t.tig_column_name=:tig_column_name";
                    Query delClobQuery = ptSession.createSQLQuery(delClobSql);
                    delClobQuery.setString("tig_owner_uuid", tig_owner_uuid);
                    delClobQuery.setString("tig_column_name", tig_column_name);
                    delClobQuery.executeUpdate();
                    // 删除FTP的文件
                    delFileFromFtp(getClobSuuid(tig_owner_uuid, tig_column_name), outFtpDataBean);// 清理流出
                }
            } catch (Exception e) {
                throw e;
            } finally {
            }
        } catch (Exception e) {
            logger.error("**********************clearSynTablePT:", e);
            throw e;
        }
        try {
            /*********************删除前置机数据,光闸与平台数据保持一直*******************************/
            Session oSession = null;
            try {
                oSession = getDao(ExchangeConfig.NEWAPASOUT).getSession();
                // 清理字段数据
                String delSubSql = "delete from tig_column_data t where t.tig_owner_uuid in (:uuids)";
                oSession.createSQLQuery(delSubSql).setParameterList("uuids", delTigUuids).executeUpdate();
                // 清理记录信息
                String delSql = "delete from tig_table_data t where t.uuid in (:uuids)";
                oSession.createSQLQuery(delSql).setParameterList("uuids", delTigUuids).executeUpdate();

                // 前置库清理
                if (!batchUuids.isEmpty()) {
                    String delBatchSql = "delete from tig_column_clob t where t.tig_owner_uuid in (:uuids) and t.tig_column_name='tig_table_data'";
                    Query delBatchClob = oSession.createSQLQuery(delBatchSql);
                    delBatchClob.setParameterList("uuids", batchUuids);
                    delBatchClob.executeUpdate();
                }

                // 大字段表的处理
                for (Map<String, Object> clobItem : clobLists) {
                    String tig_owner_uuid = (String) clobItem.get("tig_owner_uuid");
                    String tig_column_name = (String) clobItem.get("tig_column_name");
                    String delClobSql = "delete from tig_column_clob t where t.tig_owner_uuid=:tig_owner_uuid and t.tig_column_name=:tig_column_name";
                    Query cuery = oSession.createSQLQuery(delClobSql);
                    cuery.setString("tig_owner_uuid", tig_owner_uuid);
                    cuery.setString("tig_column_name", tig_column_name);
                    cuery.executeUpdate();
                }
            } catch (Exception e) {
                throw e;
            } finally {
            }
        } catch (Exception e) {
            logger.error("**********************deleteTableDataQZJ :", e);
            throw e;
        }
        return true;
    }

    public File exportFileZip(String suuid) throws Exception {
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(suuid);
        if (mongoFileEntity == null) {
            logger.error("exportFileZip noData suuid : " + suuid);
            return null;
        }
        InputStream is = null;
        OutputStream fos = null;
        File file = new File(exportFolder, suuid + ".zip");
        if (!file.exists()) {
            try {
                file.createNewFile();
                is = mongoFileEntity.getInputstream();
                fos = new GZIPOutputStream(new FileOutputStream(file));
                IOUtils.copyLarge(is, fos);
            } catch (Exception e) {
                if (file != null && file.exists()) {
                    IOUtils.closeQuietly(fos);
                    IOUtils.closeQuietly(is);
                    file.delete();
                }
                throw e;
            } finally {
                IOUtils.closeQuietly(fos);
                IOUtils.closeQuietly(is);
            }
        }
        return file.length() > 0 ? file : null;
    }

    public File exportClobZip(String suuid, Clob clob) throws Exception {
        if (clob == null || clob.length() <= 0) {
            logger.error("exportClobZip noData suuid : " + suuid);
            return null;
        }
        Reader clobReader = null;
        Writer fileWriter = null;
        File file = new File(exportFolder, suuid + ".zip");
        if (!file.exists()) {
            try {
                // file.createNewFile();
                clobReader = clob.getCharacterStream();
                fileWriter = new FileWriter(file);
                IOUtils.copyLarge(clobReader, fileWriter);
            } catch (Exception e) {
                if (file != null && file.exists()) {
                    IOUtils.closeQuietly(clobReader);
                    IOUtils.closeQuietly(fileWriter);
                    file.delete();
                }
                throw e;
            } finally {
                IOUtils.closeQuietly(clobReader);
                IOUtils.closeQuietly(fileWriter);
            }
        }
        return file.length() > 0 ? file : null;
    }

    public File exportObjectZip(String batchId, Object batchData) throws Exception {
        FileOutputStream fileOutputStream = null;
        GZIPOutputStream gzipOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        File file = new File(exportFolder, batchId + ".zip");
        if (!file.exists()) {
            try {
                fileOutputStream = new FileOutputStream(file);
                gzipOutputStream = new GZIPOutputStream(fileOutputStream);
                objectOutputStream = new ObjectOutputStream(gzipOutputStream);
                // write objectData stream to file
                objectOutputStream.writeObject(batchData);
            } catch (Exception e) {
                if (file != null && file.exists()) {
                    IOUtils.closeQuietly(objectOutputStream);
                    IOUtils.closeQuietly(gzipOutputStream);
                    IOUtils.closeQuietly(fileOutputStream);
                    file.delete();
                }
                throw e;
            } finally {
                IOUtils.closeQuietly(objectOutputStream);
                IOUtils.closeQuietly(gzipOutputStream);
                IOUtils.closeQuietly(fileOutputStream);
            }
        }
        return file.length() > 0 ? file : null;
    }

    public MongoFileEntity importFileZip(File zipFile, Map<String, Object> values) throws Exception {
        if (zipFile != null) {
            InputStream inputStream = null;
            try {
                String tenantId = SpringSecurityUtils.getCurrentTenantId();
                inputStream = new GZIPInputStream(new FileInputStream(zipFile));
                String fileID = values.get("PHYSICAL_FILE_ID") == null ? "" : values.get("PHYSICAL_FILE_ID").toString();
                String fileName = values.get("FILE_NAME") == null ? "" : values.get("FILE_NAME").toString();
                String contentType = values.get("CONTENT_TYPE") == null ? "" : values.get("CONTENT_TYPE").toString();
                return mongoFileService.savePhysicalFileWithoutVerifyMD5(tenantId, fileID, fileName, contentType,
                        inputStream);
            } catch (IOException e) {
                throw e;
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
        return null;
    }

    public void uploadFileToFtp(String suuid, File zipFile, FtpDataBean ftpDataBean) throws Exception {
        // 附件的处理
        if (zipFile != null) {
            CommonFtp myFtp = new CommonFtp(ftpDataBean.getFtp_host(), ftpDataBean.getFtp_post(),
                    ftpDataBean.getFtp_user_name(), ftpDataBean.getFtp_pass_word());
            String fileName = suuid + ".zip";
            try {
                if (!myFtp.isConnected()) {
                    myFtp.connect();
                }
                // 文件名改成操作记录uuid
                if (isLargeFile(zipFile)) {
                    FTPFile[] files = myFtp.listFiles(fileName);
                    if (files != null && files.length > 0) {
                        return;// 大文件已经存在则不上传,防止事务超时
                    }
                    files = myFtp.listFiles("\\unimas_back\\" + fileName);
                    if (files != null && files.length > 0) {
                        return;
                    }
                }
                myFtp.deleteFile("\\", fileName);
                myFtp.uploadFile(zipFile, "\\");// 上传
                myFtp.rename("\\", fileName + ".oa");// 上传完成后修改文件名,去掉.oa后缀，保证同步成功（只有.zip与.rar支持同步）
            } catch (Exception e) {
                myFtp.deleteFile("\\", fileName + ".oa");
                throw e;
            } finally {
                myFtp.disconnect();
            }
        }
    }

    public File downFileFromFtp(String suuid, FtpDataBean ftpDataBean) throws Exception {
        CommonFtp myFtp = new CommonFtp(ftpDataBean.getFtp_host(), ftpDataBean.getFtp_post(),
                ftpDataBean.getFtp_user_name(), ftpDataBean.getFtp_pass_word());
        File downFile = null;
        try {
            if (!myFtp.isConnected()) {
                myFtp.connect();
            }
            String zuuid = suuid + ".zip";
            downFile = myFtp.downFile("\\", zuuid);
            if (downFile != null) {
                myFtp.deleteFile("\\", zuuid);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            myFtp.disconnect();
        }
        return downFile;
    }

    public boolean delFileFromFtp(String suuid, FtpDataBean ftpDataBean) throws Exception {
        boolean del = false;
        if (StringUtils.isBlank(suuid))/* dead code(关闸会删除流出的附件) */ {
            CommonFtp myFtp = new CommonFtp(ftpDataBean.getFtp_host(), ftpDataBean.getFtp_post(),
                    ftpDataBean.getFtp_user_name(), ftpDataBean.getFtp_pass_word());
            try {
                if (!myFtp.isConnected()) {
                    myFtp.connect();
                }
                // 判断附件是否存在
                del = myFtp.deleteFile("\\unimas_back\\", suuid + ".zip");// 删除unimas_back目录的备份文件,TODO
                // soft
                // coding
                // unimas_back
            } catch (Exception e) {
                throw e;
            } finally {
                myFtp.disconnect();
            }
        }
        return del;
    }

    /**
     * @param strictSort 要设置的strictSort
     */
    public void setStrictSort(boolean strictSort) {
        this.strictSort = strictSort;
    }

    /**
     * @return the outFtpDataBean
     */
    public FtpDataBean getOutFtpDataBean() {
        return outFtpDataBean;
    }

    /**
     * @return the inFtpDataBean
     */
    public FtpDataBean getInFtpDataBean() {
        return inFtpDataBean;
    }

    /**
     * @return the dataNum
     */
    public int getDataNum() {
        return dataNum;
    }

    /**
     * @return the direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     * @return the dataInQuery
     */
    public String getDataInQuery() {
        return dataInQuery;
    }

    /**
     * @return the dataOutQuery
     */
    public String getDataOutQuery() {
        return dataOutQuery;
    }

    /**
     * @return the feedInCount
     */
    public String getFeedInCount() {
        return feedInCount;
    }

    /**
     * @return the feedOutCount
     */
    public String getFeedOutCount() {
        return feedOutCount;
    }

    /**
     * @return the feedBackCount
     */
    public String getFeedBackCount() {
        return feedBackCount;
    }

    /**
     * @return the beatContext
     */
    @Override
    public Map<String, Object> getBeatContext() {
        return beatContext;
    }

    /**
     * @return the preContext
     */
    @Override
    public Map<String, String> getPreContext() {
        return preContext;
    }
}