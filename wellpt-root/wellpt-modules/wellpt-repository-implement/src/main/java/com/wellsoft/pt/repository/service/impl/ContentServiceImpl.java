package com.wellsoft.pt.repository.service.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.repository.service.ContentService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilin
 * @ClassName: DefaultContentManager
 * @Description: 利用jcr建立存储word和索引的处理。
 * 每个表建立一个父node，根据id（所有id均为uuid类型）建立对应实体的node，根据filed定义
 * 中的是否索引，如果是索引的字段将根据单独存为一个属性，如果不是索引字段将全部放入一个
 * content字段中，该表中上传的附件也将放入节点中
 */
@Service
public class ContentServiceImpl implements ContentService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#addNode(java.lang.String, java.util.HashMap)
     */
    @Override
    public void addNode(String tableName, HashMap map) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#addNode(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    public void addNode(IdEntity entity) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#deleteNode(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    public void deleteNode(IdEntity entity) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#deleteNode(java.lang.String, java.util.HashMap)
     */
    @Override
    public void deleteNode(String tableName, HashMap map) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#updateNode(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    public void updateNode(IdEntity entity) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#updateNode(java.lang.String, java.util.HashMap)
     */
    @Override
    public void updateNode(String tableName, HashMap map) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#getAllFile(java.lang.String, java.lang.String)
     */
    @Override
    public List<InputStream> getAllFile(String tableName, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#getNodeMap(java.lang.String, java.lang.String)
     */
    @Override
    public Map getNodeMap(String tableName, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#getProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getProperty(String tableName, String id, String property) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.ContentService#getProperties(java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> getProperties(String tableName, String id, String[] properties) {
        // TODO Auto-generated method stub
        return null;
    }

}
