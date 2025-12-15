package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.bean.SynLogBean;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SynDataStaticsService {

    public static final int MAX_RESULTS = 512;

    public static final String FREE_SPACE = "free_space";

    public static final String USED_SPACE = "used_space";

    public static final String TOTAL_SPACE = "total_space";

    public static final String DIRECTION_FIELD = "direction";

    public static final String RULE_BEAN_FIELD = "rule_bean_field";

    public static final String TABLE_DATA_COUNT_FIELD = "table_data_count";

    public static final String DATA_STATUS_GROUP_FIELD = "data_status_group";

    public static final String CLOB_STATUS_GROUP_FIELD = "clob_status_group";

    public static final String FAIL_DATA_TABLE_GROUP_FIELD = "fail_data_table_group";

    public static final String FAIL_CLOB_TABLE_GROUP_FIELD = "fail_clob_table_group";

    public static final String FTP_ROOT_DIR = "\\";

    public static final String FTP_BACK_DIR = "/unimas_back/";

    public static final String FILE_SYSTEM_STATUS = "file_system_status";

    public static final String DATABASE_SESSION_STATUS = "database_session_status";

    public static final String QUERY_DATA_SIZE = "select ((select count(1) from tig_table_data) + (select count(1) from tig_column_data) + (select count(1) from tig_column_clob)) as data_size from dual";

    public static final String DEL_TABLE_DATA = "delete from tig_table_data where uuid in (:uuids)";

    public static final String QUERY_TABLE_DATA = "select * from tig_table_data where uuid in (:uuids)";

    public static final String DEL_COLUMN_DATA = "delete from tig_column_data where tig_owner_uuid in (:uuids)";

    public static final String DEL_COLUMN_DATA2 = "delete from tig_column_clob t where t.tig_owner_uuid in (:batchIds) and t.tig_column_name = 'tig_table_data'";

    public static final String QUERY_TABLE_CLOB = "select * from tig_column_clob where tig_owner_uuid || ';' || tig_column_name in (:uuids)";

    public static final String DEL_TABLE_CLOB = "delete from tig_column_clob where tig_owner_uuid || ';' || tig_column_name in (:uuids)";

    public static final String RESET_TABLE_DATA = "update tig_table_data t set t.syn_time=sysdate,t.status = :statue,t.remark = null where t.uuid in (:uuids)";

    public static final String RESET_TABLE_CLOB = "update tig_column_clob t set t.syn_time=sysdate,t.data_status = :statue where t.tig_owner_uuid || ';' || t.tig_column_name in (:uuids)";

    public static final String TABLE_DATA_LIST = "select t.stable_name,t.suuid,t.direction,t.action,t.cloum_num,t.status,t.create_time,t.syn_time,t.backup_time,t.uuid from tig_table_data t where t.status = :status order by t.create_time asc";

    public static final String TABLE_DATA_LIST2 = "select t.stable_name,t.suuid,t.direction,t.action,t.cloum_num,t.status,t.create_time,t.syn_time,t.backup_time,t.uuid from tig_table_data t where t.status = :status and t.stable_name=upper(:stable_name) order by t.create_time asc";

    public static final String TABLE_DATA_LIST3 = "select t.stable_name,t.suuid,t.direction,t.action,t.cloum_num,t.status,t.create_time,t.syn_time,t.backup_time,t.uuid from tig_table_data t where t.suuid = :suuid order by t.create_time asc";

    public static final String TABLE_CLOB_LIST = "select t.stable_name,t.data_uuid as suuid,t.direction,2 as action,1 as cloum_num,t.data_status as status,t.create_time,t.syn_time,t.backup_time,t.tig_owner_uuid,t.tig_column_name,(t.tig_owner_uuid || ';' || t.tig_column_name) as uuid from tig_column_clob t where t.data_status = :status order by t.create_time asc";

    public static final String TABLE_CLOB_LIST2 = "select t.stable_name,t.data_uuid as suuid,t.direction,2 as action,1 as cloum_num,t.data_status as status,t.create_time,t.syn_time,t.backup_time,t.tig_owner_uuid,t.tig_column_name,(t.tig_owner_uuid || ';' || t.tig_column_name) as uuid from tig_column_clob t where t.data_status = :status and t.stable_name = upper(:stable_name) order by t.create_time asc";

    public static final String TABLE_DATA_DETAIL = "select t.* from tig_column_data t where t.tig_owner_uuid = :tig_owner_uuid";

    public static final String TABLE_DATA_COUNT = "select (select count(1) from tig_table_data) as count_table_data,(select count(1) from tig_column_data) as count_column_data,(select count(1) from tig_column_clob) as count_column_clob from dual";

    public static final String DATA_STATUS_GROUP = "select t.status,count(1) as ctotal from tig_table_data t group by t.status order by ctotal desc";

    public static final String CLOB_STATUS_GROUP = "select t.data_status,count(1) as ctotal from tig_column_clob t group by t.data_status order by ctotal desc";

    public static final String FAIL_DATA_TABLE_GROUP = "select t.stable_name,count(1) as ctotal from tig_table_data t where t.status = 3 group by t.stable_name order by ctotal desc";

    public static final String FAIL_CLOB_TABLE_GROUP = "select t.stable_name,count(1) as ctotal from tig_column_clob t where t.data_status = 3 group by t.stable_name order by ctotal desc";

    public boolean enable(Boolean stroe);

    public boolean isEnable();

    public SynLogBean logView(String category, Integer limit);

    public long get(String category);

    public void increment(String category);

    public void increment(String category, long increment);

    //public long startTimer(String category);

    //public void stopTimer(long token);

    //public void stopTimer(long token, Object data);

    public Session openSession(String bean);

    /**
     * 统计队列中的数据量,TODO ：复用同步实现方法中的取数逻辑
     *
     * @return
     */
    public Map<String, Number> dataInQueue();

    /**
     * 统计同步健康状态
     *
     * @return
     */
    public Map<String, Object> statistics();

    public List<Map<String, Object>> queryDataList(String dao, Integer status, String stable, String suuid);

    public List<Map<String, Object>> queryClobList(String dao, Integer status, String stable);

    public List<Map<String, Object>> queryDataDetail(String dao, String tig_owner_uuid);

    public void resetData(String dao, Collection<String> uuids, Integer status);

    public void resetClob(String dao, Collection<String> uuids, Integer status);

    public void delData(String dao, Collection<String> uuids);

    public void delClob(String dao, Collection<String> uuids);

    public Map<String, Number> dataSpeed();

    public Map<String, Number> updateRecord();

    public Collection<DataRecord> dataRecord();

    public static class DataRecord implements Serializable {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        private Date time;

        private Integer ptSize;
        private Integer inSize;
        private Integer outSize;

        /**
         * 如何描述该构造方法
         */
        public DataRecord() {

        }

        /**
         * 如何描述该构造方法
         *
         * @param time
         * @param ptSize
         * @param inSize
         * @param outSize
         */
        public DataRecord(Date time, Integer ptSize, Integer inSize, Integer outSize) {
            this.time = time;
            this.ptSize = ptSize;
            this.inSize = inSize;
            this.outSize = outSize;
        }

        /**
         * @return the time
         */
        public final Date getTime() {
            return time;
        }

        /**
         * @param time 要设置的time
         */
        public final void setTime(Date time) {
            this.time = time;
        }

        /**
         * @return the ptSize
         */
        public final Integer getPtSize() {
            return ptSize;
        }

        /**
         * @param ptSize 要设置的ptSize
         */
        public final void setPtSize(Integer ptSize) {
            this.ptSize = ptSize;
        }

        /**
         * @return the inSize
         */
        public final Integer getInSize() {
            return inSize;
        }

        /**
         * @param inSize 要设置的inSize
         */
        public final void setInSize(Integer inSize) {
            this.inSize = inSize;
        }

        /**
         * @return the outSize
         */
        public final Integer getOutSize() {
            return outSize;
        }

        /**
         * @param outSize 要设置的outSize
         */
        public final void setOutSize(Integer outSize) {
            this.outSize = outSize;
        }

    }

}
