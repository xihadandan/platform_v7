package com.wellsoft.pt.basicdata.cleanup;

import com.wellsoft.context.component.tree.TreeNode;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang.BooleanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月23日   chenq	 Create
 * </pre>
 */
public interface DataCleanupProvider {

    String getType();

    ExpectCleanupResult cleanup(Params params);

    ExpectCleanupResult expectCleanupRows(Params params);


    public static class ExpectCleanupResult implements Serializable {

        private static final long serialVersionUID = 2522149868542197715L;

        private long total;


        private List<TreeNode> dataList;

        public ExpectCleanupResult() {
        }

        public ExpectCleanupResult(long total) {
            this.total = total;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public List<TreeNode> getDataList() {
            return dataList;
        }

        public void setDataList(List<TreeNode> dataList) {
            this.dataList = dataList;
        }

        public static ExpectCleanupResult total(long total) {
            return new ExpectCleanupResult(total);
        }
    }

    public static class Params extends CaseInsensitiveMap<String, Object> {

        private static final long serialVersionUID = -6587696363619854269L;

        public static Params build(String[] keys, Object[] values) {
            Params params = new Params();
            if (keys.length == values.length) {
                for (int i = 0, len = keys.length; i < len; i++) {
                    params.put(keys[i], values[i]);
                }
            }
            return params;
        }

        public Boolean optBoolean(String key) {
            if (this.containsKey(key) && this.get(key) != null) {
                BooleanUtils.toBoolean(this.get(key).toString(), "true", "false");
            }
            return false;
        }

        public Integer optInt(String key) {
            if (this.containsKey(key) && this.get(key) != null) {
                return Integer.parseInt(this.get(key).toString());
            }
            return null;
        }

        public String optString(String key) {
            if (this.containsKey(key) && this.get(key) != null) {
                return this.get(key).toString();
            }
            return null;
        }

    }

}
