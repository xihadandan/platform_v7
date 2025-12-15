package com.wellsoft.pt.basicdata.datanode.service;

import com.wellsoft.pt.basicdata.datanode.DataNode;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/21    chenq		2019/10/21		Create
 * </pre>
 */
public abstract class AbstractDataNodeCrudService {


    /**
     * 移动数据节点到其他节点的后面
     *
     * @param uuid     待移动的数据节点UUID
     * @param thatUuid 其他数据节点UUID
     * @return
     */
    public abstract void moveDNodeAfterThatNode(String uuid, String thatUuid);


    /**
     * 添加数据节点到其他节点的后面
     *
     * @param dataNode
     * @param thatUuid
     * @return
     */
    public abstract Serializable addDNodeAfterThatNode(DataNode dataNode, String thatUuid, String parentUuid);


    public abstract String name();

}
