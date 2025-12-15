package com.wellsoft.pt.integration.service;

import com.sunsharing.collagen.filex.Attachment;
import com.wellsoft.pt.integration.entity.GuangDunReceive;
import com.wellsoft.pt.integration.request.DXRequest;

import java.util.List;

/**
 * Description: 集美总线
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-18.1	wbx		2013-11-18		Create
 * </pre>
 * @date 2013-11-18
 */
public interface ZongXianExchangeService {

    public boolean dataArrived(String taskId, String dataPath, String msg);

    public boolean submit(DXRequest dXRequest);

    /**
     * 调用光盾发送数据
     *
     * @param toId        目标id
     * @param baseInfo    发送数据主体信息
     * @param attachments 发送附件信息
     * @param dataId      发送数据id
     * @param isRepeatEnd 是否重发(true/false:是/否 )
     * @return
     */
    public boolean submit(String toId, byte[] baseInfo, List<Attachment> attachments, String dataId, boolean isRepeatEnd);

    void dataArrivedAsynchronous();

    void sendDataAsynchronousJob();

    public void dataArrivedBuss(byte[] baseInfo, List<Attachment> attachments, String taskId);

    public void updateGuangDunReceiveAndPoint(String dataId);

    public String getTanentId(String unitId);

    public void dxDecryptedAndPrepareStreamingData(DXRequest request) throws Exception;

    public Boolean repeatSend(String dataId);

    public GuangDunReceive getGuangDunReceiveData();

    public List<Attachment> getGuangDunFiles(String dataId);

    public void saveGuangDunFile(List<Attachment> attachments, String dataId);

    public void saveGuangDunReceive(String taskId, byte[] baseInfo, List<Attachment> attachments);

    public void updateGuangDunSendAndPoint(String dataId, String taskId, boolean isRepeatEnd);

    public boolean saveGuangDunSend(String toId, byte[] baseInfo, List<Attachment> attachments, String typeId);

    /**
     * 反馈机制任务
     */
    public void feedBackJob();

    /**
     * 重发任务
     */
    public void repeatDateJob();

    public boolean feedBackSubmit(String toId, byte[] baseInfo, List<Attachment> attachments, String dataId);

    public void updatePointSaveDataId(String saveDataId);

    public void updatePointSendDataId(String sendDataId);

    public void updatePointReceiveDataId(String receiveDataId);

    public void updatePointErrDataIdAndStatus(String errDataId, Integer status);

    public void updateReceiveIsBack(String dataId, Integer isBack);

    public void updateReceiveStatus(String dataId, Integer status);

    public void updateSendIsBack(String dataId, Integer isBack);
}
