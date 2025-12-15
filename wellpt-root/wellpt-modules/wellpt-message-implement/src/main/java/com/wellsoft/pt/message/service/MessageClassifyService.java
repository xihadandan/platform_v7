package com.wellsoft.pt.message.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.MessageClassifyDao;
import com.wellsoft.pt.message.dto.MessageClassifyDto;
import com.wellsoft.pt.message.entity.MessageClassify;

import java.util.List;

/**
 * @author yt
 * @title: MessageClassifyService
 * @date 2020/5/18 9:06 下午
 */
public interface MessageClassifyService extends JpaService<MessageClassify, MessageClassifyDao, String> {


    public MessageClassify getByNameNoEqUuid(String name, String uuid);

    /**
     * 启用，禁用
     *
     * @param uuid
     * @param isEnable
     */
    public void enableFlg(String uuid, int isEnable);

    /**
     * 保存或更新
     *
     * @param classify
     */
    public void saveOrupdateClassify(MessageClassify classify);

    /**
     * 删除
     *
     * @param uuids
     */
    public void delClassifys(List<String> uuids);

    /**
     * 查询分类
     *
     * @return
     */
    public List<MessageClassify> queryList(String name, String systemUnitId);

    /**
     * 前端查询分类
     *
     * @param name
     * @return
     */
    public List<MessageClassifyDto> facadeQueryList(String name);

    /**
     * 设置弹窗查询分类
     *
     * @return
     */
    public List<MessageClassify> facadeWinQueryList();

}
