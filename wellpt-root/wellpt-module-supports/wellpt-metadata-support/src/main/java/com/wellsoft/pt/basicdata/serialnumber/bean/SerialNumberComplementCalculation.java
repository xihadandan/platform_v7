package com.wellsoft.pt.basicdata.serialnumber.bean;

import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRecord;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRelation;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * @Auther: yt
 * @Date: 2022/5/10 13:50
 * @Description: SerialNumberComplementCalculation
 */
public class SerialNumberComplementCalculation {

    private List<SerialNumberMaintainBean> maintainList;
    private List<SerialNumberRelation> relationList;
    private List<SerialNumberRecord> recordList;
    private Map<String, Set<String>> tableDataUuidMap;

    private List<SerialNumberRecord> delRcordList;
    private List<SerialNumberSupplementBean> supplementBeanList;


    private SerialNumberComplementCalculation(List<SerialNumberMaintainBean> maintainList, List<SerialNumberRelation> relationList,
                                              List<SerialNumberRecord> recordList, Map<String, Set<String>> tableDataUuidMap) {
        this.maintainList = maintainList;
        this.relationList = relationList;
        this.recordList = recordList;
        this.tableDataUuidMap = tableDataUuidMap;
    }

    public static SerialNumberComplementCalculation getInstance(List<SerialNumberMaintainBean> maintainList, List<SerialNumberRelation> relationList,
                                                                List<SerialNumberRecord> recordList, Map<String, Set<String>> tableDataUuidMap) {
        SerialNumberComplementCalculation complementCalculation = new SerialNumberComplementCalculation(maintainList, relationList, recordList, tableDataUuidMap);
        complementCalculation.calculation();
        return complementCalculation;
    }

    public void calculation() {
        //记录可删除流水号使用记录
        delRcordList = new ArrayList<>();
        //关键字可补号
        Map<String, SerialNumberSupplementBean> keyPartPointerMap = new LinkedHashMap<>();
        for (SerialNumberMaintain serialNumberMaintain : maintainList) {
            int pointer = Integer.valueOf(serialNumberMaintain.getPointer());
            List<Integer> pointerList = new ArrayList<>();
            for (int i = 1; i <= pointer; i++) {
                pointerList.add(i);
            }
            SerialNumberSupplementBean supplementBean = new SerialNumberSupplementBean();
            supplementBean.setUuid(serialNumberMaintain.getUuid());
            supplementBean.setKeyPart(serialNumberMaintain.getKeyPart());
            supplementBean.setHeadPart(serialNumberMaintain.getHeadPart());
            supplementBean.setLastPart(serialNumberMaintain.getLastPart());
            supplementBean.setPointer(pointer);
            supplementBean.setPointerList(pointerList);
            keyPartPointerMap.put(serialNumberMaintain.getKeyPart(), supplementBean);
        }
        if (CollectionUtils.isEmpty(recordList)) {
            supplementBeanList = new ArrayList<>(keyPartPointerMap.values());
            return;
        }
        //key:relationUuid val:ObjectName
        Map<String, String> relationUuidTableNameMap = new HashMap<>();
        for (SerialNumberRelation relation : relationList) {
            relationUuidTableNameMap.put(relation.getUuid(), relation.getObjectName());
        }
        for (SerialNumberRecord record : recordList) {
            String objectName = relationUuidTableNameMap.get(record.getRelationUuid());
            String dataUuid = record.getDataUuid();
            Set<String> uuidList = tableDataUuidMap.get(objectName);
            //表数据不存在，记录删除记录
            if (!uuidList.contains(dataUuid)) {
                delRcordList.add(record);
                continue;
            }
            //记录存在 可补号指针移除
            SerialNumberSupplementBean supplementBean = keyPartPointerMap.get(record.getKeyPart());
            if (supplementBean != null && supplementBean.getPointerList().contains(record.getPointer())) {
                supplementBean.getPointerList().remove(record.getPointer());
            }
        }

        supplementBeanList = new ArrayList<>(keyPartPointerMap.values());
    }

    public List<SerialNumberRecord> getDelRcordList() {
        return delRcordList;
    }

    public void setDelRcordList(List<SerialNumberRecord> delRcordList) {
        this.delRcordList = delRcordList;
    }

    public List<SerialNumberSupplementBean> getSupplementBeanList() {
        return supplementBeanList;
    }

    public void setSupplementBeanList(List<SerialNumberSupplementBean> supplementBeanList) {
        this.supplementBeanList = supplementBeanList;
    }
}
