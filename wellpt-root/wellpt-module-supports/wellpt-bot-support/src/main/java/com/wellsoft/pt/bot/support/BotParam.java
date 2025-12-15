package com.wellsoft.pt.bot.support;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Description:单据转换参数
 *
 * @author chenq
 * @date 2018/9/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/18    chenq		2018/9/18		Create
 * </pre>
 */
public class BotParam implements Serializable {


    private static final long serialVersionUID = 4314892511442790291L;
    private String ruleId;//单据转换规则ID
    private Set<BotFromParam> froms = Sets.newLinkedHashSet();//来源对象数据
    private Map<String, Object> jsonBody;//json报文
    private String targetUuid;//目标单据实例：单据转换更新到目标单据

    /**
     * 构造方法
     */
    public BotParam() {

    }

    /**
     * 构造方法
     *
     * @param ruleId 单据转换规则ID
     * @param froms  来源数据参数
     */
    public BotParam(String ruleId,
                    Set<BotFromParam> froms) {
        this.ruleId = ruleId;
        this.froms = froms;
    }

    /**
     * 构造方法
     *
     * @param ruleId   单据转换规则ID
     * @param jsonBody json数据
     */
    public BotParam(String ruleId, Map<String, Object> jsonBody) {
        this.ruleId = ruleId;
        this.jsonBody = jsonBody;
    }

    /**
     * 获取单据转换规则ID
     *
     * @return
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * 设置单据转换规则ID
     *
     * @param ruleId
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * 获取来源数据参数
     *
     * @return
     */
    public Set<BotFromParam> getFroms() {
        return froms;
    }

    /**
     * 设置来源数据参数
     *
     * @param froms
     */
    public void setFroms(Set<BotFromParam> froms) {
        this.froms = froms;
    }

    /**
     * 获取json数据
     *
     * @return
     */
    public Map<String, Object> getJsonBody() {
        return jsonBody;
    }

    /**
     * 设置json数据
     *
     * @param jsonBody
     */
    public void setJsonBody(Map<String, Object> jsonBody) {
        this.jsonBody = jsonBody;
    }

    /**
     * 获取目标数据UUID
     *
     * @return
     */
    public String getTargetUuid() {
        return targetUuid;
    }

    /**
     * 设置获取目标数据UUID
     *
     * @param targetUuid
     */
    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    /**
     * Description:来源数据参数
     *
     * @author chenq
     * @date 2018/9/18
     *
     * <pre>
     * 修改记录:
     * 修改后版本	    修改人		修改日期			修改内容
     * 2018/9/18    chenq		2018/9/18		Create
     * </pre>
     */
    public static class BotFromParam implements Serializable {

        private String fromUuid;//来源数据uuid

        private String fromObjId;//来源单据id：表单ID或者数据库表名

        private Object fromObjData;//来源单据数据

        private String version = "1.0";

        /**
         * 构造方法
         */
        public BotFromParam() {
        }

        /**
         * 构造方法
         *
         * @param fromUuid  来源数据uuid
         * @param fromObjId 来源单据id：表单ID或者数据库表名
         */
        public BotFromParam(String fromUuid, String fromObjId) {
            this.fromUuid = fromUuid;
            this.fromObjId = fromObjId;
        }

        /**
         * 构造方法
         *
         * @param fromUuid    来源数据uuid
         * @param fromObjId   来源单据id：表单ID或者数据库表名
         * @param fromObjData 来源单据数据
         */
        public BotFromParam(String fromUuid, String fromObjId, Object fromObjData) {
            this.fromUuid = fromUuid;
            this.fromObjId = fromObjId;
            this.fromObjData = fromObjData;
        }

        /**
         * 获取来源数据uuid
         *
         * @return
         */
        public String getFromUuid() {
            return fromUuid;
        }

        /**
         * 设置来源数据uuid
         *
         * @param fromUuid
         */
        public void setFromUuid(String fromUuid) {
            this.fromUuid = fromUuid;
        }

        /**
         * 获取来源单据id：表单ID或者数据库表名
         *
         * @return
         */
        public String getFromObjId() {
            return fromObjId;
        }

        /**
         * 设置来源单据id：表单ID或者数据库表名
         *
         * @param fromObjId
         */
        public void setFromObjId(String fromObjId) {
            this.fromObjId = fromObjId;
        }

        /**
         * 获取来源单据数据
         *
         * @return
         */
        public Object getFromObjData() {
            return fromObjData;
        }

        /**
         * 设置来源单据数据
         *
         * @param fromObjData
         */
        public void setFromObjData(Object fromObjData) {
            this.fromObjData = fromObjData;
        }

        /**
         * 获取版本号
         *
         * @return
         */
        public String getVersion() {
            return version;
        }

        /**
         * 设置版本号
         *
         * @param version
         */
        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public int hashCode() {
            return (this.fromUuid + this.fromObjId).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BotFromParam) {
                if (obj != null) {
                    return ((BotFromParam) obj).getFromObjId().equals(
                            this.getFromObjId()) && ((BotFromParam) obj).getFromUuid().equals(
                            this.getFromUuid()) && ((BotFromParam) obj).getVersion().equals(
                            this.getVersion());
                }

            }
            return false;
        }
    }


}
