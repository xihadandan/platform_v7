package com.wellsoft.pt.repository.service;

import com.wellsoft.context.jdbc.entity.IdEntity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilin
 * @ClassName: ContentManager
 * @Description: jcr的管理封装
 */
public interface ContentService {

    public void addNode(String tableName, HashMap map);

    public void addNode(IdEntity entity);

    public void deleteNode(IdEntity entity);

    public void deleteNode(String tableName, HashMap map);

    public void updateNode(IdEntity entity);

    public void updateNode(String tableName, HashMap map);

    public List<InputStream> getAllFile(String tableName, String id);

    public Map getNodeMap(String tableName, String id);

    public String getProperty(String tableName, String id, String property);

    public List<String> getProperties(String tableName, String id, String[] properties);
}
