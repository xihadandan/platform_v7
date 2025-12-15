package com.wellsoft.pt.basicdata.iexport.service.impl;

import cn.hutool.core.util.ZipUtil;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.protobuf.ByteString;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.basicdata.iexport.acceptor.PrintMongoFileSerializable;
import com.wellsoft.pt.basicdata.iexport.protos.ProtoData;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.ImportIexportService;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Hibernate;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.type.BlobType;
import org.hibernate.type.ClobType;
import org.hibernate.type.NClobType;
import org.hibernate.type.Type;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.Clob;
import java.util.*;

/**
 * @author yt
 * @title: ImportIexportServiceImpl
 * @date 2020/8/13 14:39
 */
@Service
public class ImportIexportServiceImpl extends BaseServiceImpl implements ImportIexportService {

    private static Map<Class, Set<Field>> fieldMap = new HashMap<>();
    private static Map<Class, Set<Field>> ioFieldMap = new HashMap<>();
    private String rootParentTypeUuid = "root" + Separator.UNDERLINE.getValue() + "0";
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private SecurityMetadataSourceService securityMetadataSourceService;
    @Autowired
    private CacheManager cacheManager;
    @Resource
    private List<IexportDataProvider> iexportDataProviders;


    @Override
    @Transactional(readOnly = true)
    public ProtoData.ProtoDataList getExportData(String[] exportIds) {
        Multimap<String, String> parentMap = HashMultimap.create();
        Multimap<String, String> multimap = HashMultimap.create();
        for (String exportId : exportIds) {
            String[] typeUuids = exportId.split(Separator.UNDERLINE.getValue());
            String parentType = typeUuids[0];
            String parentUuid = typeUuids[1];
            String type = typeUuids[2];
            String uuid = typeUuids[3];
            multimap.put(type, uuid);
            parentMap.put(type + Separator.UNDERLINE.getValue() + uuid, parentType + Separator.UNDERLINE.getValue() + parentUuid);

        }
        Set<String> fileIds = new HashSet<>();
        ProtoData.ProtoDataList.Builder builder = ProtoData.ProtoDataList.newBuilder();
        PersistenceContext persistenceContext = ((SessionImplementor) this.dao.getSession()).getPersistenceContext();
        try {
            for (String key : multimap.keySet()) {
                IexportDataProvider iexportDataProvider = IexportDataProviderFactory.getDataProvider(key);
                List<JpaEntity> list = iexportDataProvider.getList(multimap.get(key));
                Map<String, String> treeNameMap = iexportDataProvider.getTreeNameMap(list);
                for (JpaEntity idEntity : list) {
                    idEntity = (JpaEntity) persistenceContext.unproxy(idEntity);
                    ProtoDataBean protoDataBean = new ProtoDataBean(idEntity, key, treeNameMap.get(idEntity.getUuid()));
                    protoDataBean.setParentTypeUuids(parentMap.get(key + Separator.UNDERLINE.getValue() + idEntity.getUuid()));
                    Set<String> fileIdSet = iexportDataProvider.getFileIds(idEntity);
                    if (fileIdSet != null) {
                        protoDataBean.setFileIds(fileIdSet);
                        fileIds.addAll(fileIdSet);
                    }
                    ProtoData.ProtoEntity protoEntity = ProtoDataBeanUtils.toProtoData(protoDataBean);
                    Set<Field> fieldSet = this.getIoFieldSet(idEntity.getClass());
                    if (fieldSet != null) {
                        Map<String, ByteString> byteStringMap = new HashMap<>();
                        for (Field field : fieldSet) {
                            Object obj = field.get(idEntity);
                            if (obj == null) {
                                continue;
                            }
                            if (field.getType() == Clob.class) {
                                obj = ClobUtils.ClobToString((Clob) obj);

                            } else if (field.getType() == Blob.class) {
                                obj = ClobUtils.blobToBytes((Blob) obj);
                            }
                            byteStringMap.put(field.getName(), ByteString.copyFrom(ProtoDataBeanUtils.toByteArray(obj)));
                        }
                        if (byteStringMap.size() > 0) {
                            protoEntity = protoEntity.toBuilder().putAllIoAttribute(byteStringMap).build();
                        }
                    }
                    builder.addProtoEntity(protoEntity);
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        for (String fileId : fileIds) {
            MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
            if (mongoFileEntity == null) {
                continue;
            }
            byte file[] = ProtoDataBeanUtils.toByteArray(mongoFileEntity.getInputstream());
            PrintMongoFileSerializable mongoFileSerializable = new PrintMongoFileSerializable();
            mongoFileSerializable.setFileId(mongoFileEntity.getFileID());
            mongoFileSerializable.setFileName(mongoFileEntity.getFileName());
            mongoFileSerializable.setFileArray(file);
            builder.putAttach(fileId, ByteString.copyFrom(ProtoDataBeanUtils.toByteArray(mongoFileSerializable)));
        }
        ProtoData.ProtoDataList protoDataList = builder.build();

        return protoDataList;
    }

    @Override
    public List<TreeNode> getExportTree(String uuid, String type) {
        List<TreeNode> treeNodes = this.iexportData(uuid, type);
        return treeNodes;
    }

    private TreeNode convertTreeNode(String parentType, String parentUuid, String type, String uuid, String treeName) {
        String parentTypeUuid = parentType + Separator.UNDERLINE.getValue() + parentUuid;
        return this.convertTreeNode(parentTypeUuid, type, uuid, treeName);
    }

    private TreeNode convertTreeNode(String parentTypeUuid, String type, Serializable uuid, String treeName) {
        String id = this.getId(parentTypeUuid, type, uuid);
        TreeNode node = getTreeNode(type, id, treeName);
        return node;
    }

    private TreeNode getTreeNode(String type, String id, String treeName) {
        Map<String, String> treeNodeData = new HashMap<String, String>();
        treeNodeData.put("type", type);
        treeNodeData.put("color", "black");
        TreeNode node = new TreeNode();
        node.setId(id);
        node.setName(treeName);
        node.setChecked(true);
        node.setData(treeNodeData);
        return node;
    }

    private String getId(String parentTypeUuid, String type, Serializable uuid) {
        return parentTypeUuid + Separator.UNDERLINE.getValue() + type + Separator.UNDERLINE.getValue() + uuid;
    }

    private String getId(String parentType, String parentUuid, String type, String uuid) {
        return parentType + Separator.UNDERLINE.getValue() + parentUuid + Separator.UNDERLINE.getValue() + type + Separator.UNDERLINE.getValue() + uuid;
    }

    private List<TreeNode> iexportData(String uuid, String type) {
        IexportDataProvider iexportDataProvider = IexportDataProviderFactory.getDataProvider(type);
        String[] uuids = uuid.split(";");
        List<TreeNode> treeNodes = new ArrayList<>();

//        for (String uid : uuids) {
//            TreeNode node = iexportDataProvider.exportAsTreeNode(uid);
//            if (node != null) {
//                treeNodes.add(node);
//            }
//        }
//
        List<JpaEntity> list = iexportDataProvider.getList(Arrays.asList(uuids));
        Map<String, String> treeNameMap = iexportDataProvider.getTreeNameMap(list);
        Set<String> uuidSet = new HashSet<>();
        Map<String, ProtoDataHql> hqlMap = new HashMap<>();
        Map<String, JpaEntity> parentMap = new HashMap<>();
        Map<JpaEntity, TreeNode> treeNodeMap = new HashMap<>();
        for (JpaEntity idEntity : list) {
            iexportDataProvider.putChildProtoDataHqlParams(idEntity, parentMap, hqlMap);
            TreeNode treeNode = this.convertTreeNode(rootParentTypeUuid, type, idEntity.getUuid(), treeNameMap.get(idEntity.getUuid()));
            treeNodeMap.put(idEntity, treeNode);
            treeNodes.add(treeNode);
            uuidSet.add(treeNode.getId());
        }
        this.addProtoDataBeanChild(treeNodeMap, parentMap, hqlMap, uuidSet);
        return treeNodes;
    }

    private void addProtoDataBeanChild(Map<JpaEntity, TreeNode> treeNodeMap, Map<String, JpaEntity> parentMap, Map<String, ProtoDataHql> hqlMap, Set<String> uuidSet) {
        Map<String, List<JpaEntity>> childMap = new HashMap<>();
        Map<JpaEntity, TreeNode> childTreeMap = new HashMap<>();
        for (String key : hqlMap.keySet()) {
            String[] strs = key.split(Separator.UNDERLINE.getValue());
            String childType = strs[1];
            IexportDataProvider childProvider = IexportDataProviderFactory.getDataProvider(childType);
            Map<String, List<JpaEntity>> mapList = childProvider.getParentMapList(hqlMap.get(key));
            for (String uuidKey : mapList.keySet()) {
                List<JpaEntity> list = mapList.get(uuidKey);
                JpaEntity parentEntity = parentMap.get(uuidKey);
                if (parentEntity == null) {
                    continue;
                }
                TreeNode treeNode = treeNodeMap.get(parentEntity);
                Map<String, String> treeNameMap = childProvider.getTreeNameMap(list);
                Iterator<JpaEntity> iterator = list.iterator();
                while (iterator.hasNext()) {
                    JpaEntity idEntity = iterator.next();
                    String id = this.getId(strs[0], parentEntity.getUuid().toString(), childType, idEntity.getUuid().toString());
                    if (uuidSet.contains(id)) {
                        iterator.remove();
                    } else {
                        TreeNode childrenTreeNode = this.convertTreeNode(strs[0], parentEntity.getUuid().toString(), childType, idEntity.getUuid().toString(), treeNameMap.get(idEntity.getUuid()));
                        childTreeMap.put(idEntity, childrenTreeNode);
                        treeNode.getChildren().add(childrenTreeNode);
                        uuidSet.add(id);
                    }
                }
                if (!childMap.containsKey(childType)) {
                    childMap.put(childType, list);
                } else {
                    childMap.get(childType).addAll(list);
                }
            }
        }
        hqlMap = new HashMap<>();
        parentMap = new HashMap<>();
        for (String key : childMap.keySet()) {
            IexportDataProvider provider = IexportDataProviderFactory.getDataProvider(key);
            for (JpaEntity idEntity : childMap.get(key)) {
                provider.putChildProtoDataHqlParams(idEntity, parentMap, hqlMap);
            }
        }
        if (hqlMap.size() > 0) {
            this.addProtoDataBeanChild(childTreeMap, parentMap, hqlMap, uuidSet);
        }
    }

    private void setIoAttrbute(ProtoData.ProtoEntity protoEntity, ProtoDataBean protoDataBean) throws Exception {
        if (protoEntity.getIoAttributeCount() > 0) {
            Map<String, ByteString> map = protoEntity.getIoAttributeMap();
            Set<Field> fieldSet = this.getIoFieldSet(protoDataBean.getData().getClass());
            for (Field field : fieldSet) {
                ByteString byteStr = map.get(field.getName());
                if (byteStr == null) {
                    continue;
                }
                if (field.getType() == Clob.class) {
                    Clob clob = Hibernate.getLobCreator(this.dao.getSession()).createClob(ProtoDataBeanUtils.toObject(byteStr.toByteArray()).toString());
                    field.set(protoDataBean.getData(), clob);
                } else if (field.getType() == Blob.class) {
                    Blob blob = Hibernate.getLobCreator(this.dao.getSession()).createBlob(byteStr.toByteArray());
                    field.set(protoDataBean.getData(), blob);
                }
            }
        }
    }

    @Override
    @Transactional
    public void importData(InputStream inputstream, String importIds) throws Exception {
        ProtoData.ProtoDataList protoDataList = null;
        try {
            protoDataList = ProtoData.ProtoDataList.parseFrom(inputstream);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("请检查导入文件是否新版定义文件：<br/>" +
                    "1、如果不是新版，请更改当前系统参数iexport.isNewExport的值为false（导入成功后，请将参数值更改回true）！<br/>" +
                    "2、如果是新版，则是定义文件存在参数错误！");
        }
        Map<String, ProtoDataBean> beanMap = new HashMap<>();
        Multimap<String, String> parentMap = HashMultimap.create();
        Set<String> typeUuidSet = new HashSet<>();
        String[] importIdList = importIds.split(";");
        for (String exportId : importIdList) {
            String[] typeUuids = exportId.split(Separator.UNDERLINE.getValue());
            String typeUuid = typeUuids[2] + Separator.UNDERLINE.getValue() + typeUuids[3];
            typeUuidSet.add(typeUuid);
        }
        for (ProtoData.ProtoEntity protoEntity : protoDataList.getProtoEntityList()) {
            ProtoDataBean protoDataBean = ProtoDataBeanUtils.toProtoDataBean(protoEntity);
            this.setIoAttrbute(protoEntity, protoDataBean);
            String typeUuid = protoDataBean.getType() + Separator.UNDERLINE.getValue() + protoDataBean.getData().getUuid();
            beanMap.put(typeUuid, protoDataBean);
            Collection<String> parentTypeUuids = protoDataBean.getParentTypeUuids();
            for (String parentTypeUuid : parentTypeUuids) {
                parentMap.put(parentTypeUuid, typeUuid);
            }
        }
        Map<String, ByteString> attachMap = protoDataList.getAttachMap();
        Map<String, Map<Serializable, ProtoDataBeanTree>> protoDataMap = new HashMap<>();
        Multimap<String, Serializable> uuidMap = HashMultimap.create();
        Multimap<String, JpaEntity> dataMap = HashMultimap.create();
        Set<String> fileIdSet = new HashSet<>();
        int count = 0;
        for (String typeUuid : beanMap.keySet()) {
            if (typeUuidSet.contains(typeUuid)) {
                count++;
                ProtoDataBean protoDataBean = beanMap.get(typeUuid);
                ProtoDataBeanTree protoDataBeanTree = new ProtoDataBeanTree();
                protoDataBeanTree.setProtoDataBean(protoDataBean);

                Collection<String> parentTypeUuids = protoDataBean.getParentTypeUuids();
                for (String parentTypeUuid : parentTypeUuids) {
                    if (!parentTypeUuid.equals(this.rootParentTypeUuid)) {
                        String[] parentTypeUuidStrs = parentTypeUuid.split(Separator.UNDERLINE.getValue());
                        Map<String, Set<JpaEntity>> map = protoDataBeanTree.getParentMap();
                        Set<JpaEntity> set = map.get(parentTypeUuidStrs[0]);
                        if (set == null) {
                            set = new HashSet<>();
                            map.put(parentTypeUuidStrs[0], set);
                        }
                        set.add(beanMap.get(parentTypeUuid).getData());
                    }
                }

                Collection<String> childTypeUuids = parentMap.get(typeUuid);
                for (String chilTypeUuid : childTypeUuids) {
                    String[] chilTypeUuidStr = chilTypeUuid.split(Separator.UNDERLINE.getValue());
                    Map<Serializable, Set<JpaEntity>> map = protoDataBeanTree.getChilderMap();
                    Set<JpaEntity> set = map.get(chilTypeUuidStr[0]);
                    if (set == null) {
                        set = new HashSet<>();
                        map.put(chilTypeUuidStr[0], set);
                    }
                    set.add(beanMap.get(chilTypeUuid).getData());
                }

                Map<Serializable, ProtoDataBeanTree> map = protoDataMap.get(protoDataBean.getType());
                if (map == null) {
                    map = new HashMap<>();
                    protoDataMap.put(protoDataBean.getType(), map);
                }
                map.put(protoDataBean.getData().getUuid(), protoDataBeanTree);
                uuidMap.put(protoDataBean.getType(), protoDataBean.getData().getUuid());
                dataMap.put(protoDataBean.getType(), protoDataBean.getData());
                fileIdSet.addAll(protoDataBean.getFileIds());
                if (count == typeUuidSet.size()) {
                    break;
                }
            }
        }

        for (String type : dataMap.keySet()) {
            IexportDataProvider provider = IexportDataProviderFactory.getDataProvider(type);
            Set<String> set = provider.dataCheck(dataMap.get(type), beanMap);
            if (set != null && set.size() > 0) {
                throw new RuntimeException("存在无定义的资源，且当前系统不存在该资源，无法导入！请确保导入数据中的资源包含定义，或先将资源导入至当前系统！");
            }
        }

        for (String fileId : fileIdSet) {
            ByteString bytes = attachMap.get(fileId);
            if (bytes == null) {
                continue;
            }
            PrintMongoFileSerializable file = (PrintMongoFileSerializable) ProtoDataBeanUtils.toObject(bytes.toByteArray());
            mongoFileService.saveFile(file.getFileId(), file.getFileName(), new ByteArrayInputStream(file.getFileArray()));
        }
        List<BusinessProcessor> businessProcessorList = new ArrayList<>();
        for (String type : protoDataMap.keySet()) {
            IexportDataProvider provider = IexportDataProviderFactory.getDataProvider(type);
            Map<Serializable, ProtoDataBeanTree> map = protoDataMap.get(type);
            BusinessProcessor businessProcessor = provider.saveOrUpdate(map, uuidMap.get(type));
            if (businessProcessor != null) {
                businessProcessorList.add(businessProcessor);
            }
            this.dao.getSession().flush();
        }
        //业务处理
        for (BusinessProcessor businessProcessor : businessProcessorList) {
            businessProcessor.handle(beanMap);
        }
        //处理缓存
        AppCacheUtils.clear();
        securityMetadataSourceService.loadSecurityMetadataSource();
        cacheManager.clearAllCache();
    }

    @Override
    public List<TreeNode> getImportTree(InputStream inputstream) throws Exception {
        ProtoData.ProtoDataList protoDataList = null;
        try {
            protoDataList = ProtoData.ProtoDataList.parseFrom(inputstream);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("请检查导入文件是否新版定义文件：<br/>" +
                    "1、如果不是新版，请更改当前系统参数iexport.isNewExport的值为false（导入成功后，请将参数值更改回true）！<br/>" +
                    "2、如果是新版，则是定义文件存在参数错误！");
        }
        List<TreeNode> treeNodeList = new ArrayList<>();
        Map<String, ProtoDataBean> beanMap = new HashMap<>();
        Multimap<String, Serializable> uuidMap = HashMultimap.create();
        Multimap<String, JpaEntity> dataMap = HashMultimap.create();
        Multimap<String, String> parentMap = HashMultimap.create();
        for (ProtoData.ProtoEntity protoEntity : protoDataList.getProtoEntityList()) {
            ProtoDataBean protoDataBean = ProtoDataBeanUtils.toProtoDataBean(protoEntity);
            this.setIoAttrbute(protoEntity, protoDataBean);
            uuidMap.put(protoDataBean.getType(), protoDataBean.getData().getUuid());
            dataMap.put(protoDataBean.getType(), protoDataBean.getData());
            String key = protoDataBean.getType() + Separator.UNDERLINE.getValue() + protoDataBean.getData().getUuid();
            beanMap.put(key, protoDataBean);
            Collection<String> parentTypeUuids = protoDataBean.getParentTypeUuids();
            for (String parentTypeUuid : parentTypeUuids) {
                parentMap.put(parentTypeUuid, key);
            }
        }
        Map<String, String> colorMap = new HashMap<>();
        for (String type : uuidMap.keySet()) {
            IexportDataProvider dataProvider = IexportDataProviderFactory.getDataProvider(type);
            Collection<Serializable> uuids = uuidMap.get(type);
            List<JpaEntity> list = dataProvider.getList(uuids);
            for (JpaEntity idEntity : list) {
                String key = type + Separator.UNDERLINE.getValue() + idEntity.getUuid();
                ProtoDataBean protoDataBean = beanMap.get(key);
                String color = "green";
                if (!idEntity.getRecVer().equals(protoDataBean.getData().getRecVer())) {
                    color = "orange";//red->orange
                }
                colorMap.put(key, color);
            }
            Collection<JpaEntity> entityList = dataMap.get(type);
            Set<String> checkSet = dataProvider.dataCheck(entityList, beanMap);
            if (checkSet != null) {
                for (String checkUuid : checkSet) {
                    String key = type + Separator.UNDERLINE.getValue() + checkUuid;
                    colorMap.put(key, "red");// grey-red
                }
            }
        }
        Set<String> uuidSet = new HashSet<>();
        List<TreeNode> roots = this.getChild(beanMap, colorMap, rootParentTypeUuid, parentMap.get(rootParentTypeUuid), uuidSet);
        treeNodeList.addAll(roots);
        List<TreeNode> treeNodes = new ArrayList<>(roots);
        while (treeNodes.size() > 0) {
            List<TreeNode> rootList = new ArrayList<>();
            for (TreeNode treeNode : treeNodes) {
                String[] typeUuids = treeNode.getId().split(Separator.UNDERLINE.getValue());
                String typeUuid = typeUuids[2] + Separator.UNDERLINE.getValue() + typeUuids[3];
                List<TreeNode> childs = this.getChild(beanMap, colorMap, typeUuid, parentMap.get(typeUuid), uuidSet);
                treeNode.getChildren().addAll(childs);
                rootList.addAll(childs);
            }
            treeNodes = rootList;
        }

        return treeNodeList;
    }

    private List<TreeNode> getChild(Map<String, ProtoDataBean> beanMap, Map<String, String> colorMap, String parentTypeUuid, Collection<String> typeUuids, Set<String> uuidSet) {
        List<TreeNode> treeNodes = new ArrayList<>();
        for (String uuid : typeUuids) {
            ProtoDataBean protoDataBean = beanMap.get(uuid);
            TreeNode treeNode = this.convertTreeNode(parentTypeUuid, protoDataBean.getType(), protoDataBean.getData().getUuid(), protoDataBean.getTreeName());
            String id = treeNode.getId();
            if (uuidSet.contains(id)) {
                continue;
            } else {
                uuidSet.add(id);
            }
            String color = colorMap.get(uuid);
            if (color != null) {
                ((Map<String, String>) treeNode.getData()).put("color", color);
            }
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    @Override
    @Transactional(readOnly = true)
    public IexportDataDifference getDifference(InputStream inputstream, String uuid) {
        IexportDataDifference dataDifference = new IexportDataDifference();
        ProtoDataBean protoDataBean = null;
        try {
            String[] typeUuids = uuid.split(Separator.UNDERLINE.getValue());
            String typeUuid = typeUuids[2] + Separator.UNDERLINE.getValue() + typeUuids[3];
            ProtoData.ProtoDataList protoDataList = ProtoData.ProtoDataList.parseFrom(inputstream);
            for (ProtoData.ProtoEntity protoEntity : protoDataList.getProtoEntityList()) {
                protoDataBean = ProtoDataBeanUtils.toProtoDataBean(protoEntity);
                String temTypeUuid = protoDataBean.getType() + Separator.UNDERLINE.getValue() + protoDataBean.getData().getUuid();
                if (temTypeUuid.equals(typeUuid)) {
                    this.setIoAttrbute(protoEntity, protoDataBean);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        IexportDataProvider dataProvider = IexportDataProviderFactory.getDataProvider(protoDataBean.getType());
        Collection<Serializable> uuids = new ArrayList<>();
        uuids.add(protoDataBean.getData().getUuid());
        List<JpaEntity> idEntitieList = dataProvider.getList(uuids);
        JpaEntity idEntity = idEntitieList.get(0);
        Map<String, Object> controlData = null;
        Map<String, Object> testData = null;
        try {
            controlData = this.convertMap(protoDataBean.getData());
            testData = this.convertMap(idEntity);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        Set<String> keys = new LinkedHashSet<String>();
        keys.addAll(controlData.keySet());
        keys.addAll(testData.keySet());
        List<IexportDataDifferenceDetail> dataDifferenceDetails = new ArrayList<IexportDataDifferenceDetail>();
        dataDifference.setUuid(uuid);
        dataDifference.setType(protoDataBean.getType());
        dataDifference.setDataDifferenceDetails(dataDifferenceDetails);
        for (String key : keys) {
            Object controlValue = StringUtils.EMPTY;
            Object testValue = StringUtils.EMPTY;
            if (controlData.containsKey(key)) {
                controlValue = this.convertClob(controlData.get(key));
            }
            if (testData.containsKey(key)) {
                testValue = this.convertClob(testData.get(key));
            }
            IexportDataDifferenceDetail detail = new IexportDataDifferenceDetail();
            detail.setFieldName(key);
            detail.setControlValue(ObjectUtils.toString(controlValue));
            detail.setTestValue(ObjectUtils.toString(testValue));
            dataDifferenceDetails.add(detail);
        }

        return dataDifference;
    }

    @Override
    public List<TreeNode> exportDataDefAsTree(List<String> uuids, String type) {
        IexportDataProvider iexportDataProvider = IexportDataProviderFactory.getDataProvider(type);
        List<TreeNode> treeNodes = new ArrayList<>();
        for (String uid : uuids) {
            TreeNode node = iexportDataProvider.exportAsTreeNode(uid);
            if (node != null) {
                treeNodes.add(node);
            }
        }
        return treeNodes;
    }

    @Override
    public List<TreeNode> exportDataDefAsTree(List<String> uuid, List<String> type) {
        List<TreeNode> treeNodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(uuid)) {
            Assert.state(uuid.size() - type.size() == 0, "数据与类型长度不匹配");
            for (int i = 0, len = uuid.size(); i < len; i++) {
                IexportDataProvider iexportDataProvider = IexportDataProviderFactory.getDataProvider(type.get(i));
                TreeNode node = iexportDataProvider.exportAsTreeNode(uuid.get(i));
                if (node != null) {
                    treeNodes.add(node);
                }
            }
        }
        return treeNodes;
    }

    @Override
    public void exportDataDefinition(List<String> typeUuids, HttpServletResponse response) {
        if (CollectionUtils.isNotEmpty(typeUuids)) {
            try {
                // 创建临时导出文件目录
                File directory = new File(new File(System.getProperty("java.io.tmpdir")), "export-definition-" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS"));
                directory.mkdir();
                JSONArray entityFiles = new JSONArray();
                StopWatch stopWatch = new StopWatch("导出定义");
                logger.info("创建导出定义临时文件 => {}", directory.getAbsoluteFile());

                for (String typeUuid : typeUuids) {
                    String[] parts = typeUuid.split(Separator.COLON.getValue());
                    stopWatch.start("导出定义 - " + typeUuid);
                    IexportDataProvider dataProvider = IexportDataProviderFactory.getDataProvider(parts[0]);
                    if (dataProvider != null) {
                        try {
                            IExportEntityStream stream = dataProvider.exportEntityStream(parts[1]);
                            if (stream != null) {
                                String filename = stream.getName();
                                filename = filename.replaceAll("[<>:：\"/\\\\|?*]", "_"); // 替换非法字符
                                filename = filename + "." + stream.getMetadata().getUuid();

                                JSONObject obj = new JSONObject();
                                obj.put("name", stream.getName());
                                obj.put("uuid", stream.getMetadata().getUuid());
                                obj.put("type", stream.getMetadata().getType());
                                // 创建导出定义文件
                                File dataFile = new File(directory.getAbsoluteFile(), filename + ".entity");
                                dataFile.createNewFile();
                                obj.put("filename", dataFile.getName());
                                if (CollectionUtils.isNotEmpty(stream.getFiles())) {
                                    // 创建定义文件关联的附件子目录
                                    File subDirectory = new File(directory.getAbsoluteFile(), stream.getMetadata().getUuid());
                                    subDirectory.mkdir();
                                    JSONArray files = new JSONArray();
                                    for (IExportEntityStream.File f : stream.getFiles()) {
                                        if (f.getInput() != null) {
                                            files.put(f.getName());
                                            File inputFile = new File(subDirectory.getAbsoluteFile(), f.getName());
                                            inputFile.createNewFile();
                                            IOUtils.copy(f.getInput(), new FileOutputStream(inputFile));
                                        }
                                    }
                                    if (files.length() > 0) {// 实体数据附带的附件信息
                                        obj.put("attachment", filename);
                                    }
                                }
                                IOUtils.copy(new ByteArrayInputStream(stream.toJSON().getBytes()), new FileOutputStream(dataFile));
                                entityFiles.put(obj);
                            }
                        } catch (Exception e) {
                            logger.error("导出数据定义异常: {}", Throwables.getStackTraceAsString(e));
                        }

                    }
                    stopWatch.stop();
                }
                if (entityFiles.length() > 0) {// entity文件信息索引
                    File index = new File(directory.getAbsoluteFile(), "index");
                    index.createNewFile();
                    IOUtils.copy(new ByteArrayInputStream(entityFiles.toString().getBytes()), new FileOutputStream(index));
                }


                stopWatch.start("导出定义 - 压缩文件");
                // 压缩导出目录
                File zipFile = ZipUtil.zip(directory);
                stopWatch.stop();
                logger.info("导出定义耗时: {}", stopWatch.prettyPrint());
                InputStream zipInputStream = new FileInputStream(zipFile);
                IOUtils.copyLarge(zipInputStream, response.getOutputStream());
                IOUtils.closeQuietly(zipInputStream);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch (Exception e) {
                logger.error("导出定义异常: {}", Throwables.getStackTraceAsString(e));
            }

        }
    }

    @Override
    public IexportDataProvider getIexportDataProvider(String type) {
        IexportDataProvider dataProvider = ApplicationContextHolder.getBean(type + "IexportDataProvider", IexportDataProvider.class);
        if (dataProvider != null) {
            return dataProvider;
        }
        if (CollectionUtils.isNotEmpty(iexportDataProviders)) {
            for (IexportDataProvider provider : iexportDataProviders) {
                if (provider.getType().equalsIgnoreCase(type)) {
                    return provider;
                }
            }
        }
        return null;
    }


    private Object convertClob(Object object) {
        if (object instanceof Clob) {
            try {
                return ClobUtils.ClobToString((Clob) object);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        }
        return object;
    }

    private Map<String, Object> convertMap(JpaEntity idEntity) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Class clazz = idEntity.getClass();
        Set<Field> fieldNameSet = this.getFieldSet(clazz);
        for (Field field : fieldNameSet) {
            if (field.getType().isAnnotationPresent(Entity.class)) {
                JpaEntity fieldEntity = (JpaEntity) field.get(idEntity);
                map.put(field.getName(), fieldEntity == null ? null : fieldEntity.getUuid());
            } else {
                map.put(field.getName(), field.get(idEntity));
            }
        }
        return map;
    }

    private void addFieldList(Class clazz, Map<String, Field> fieldMap) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        if (clazz.getSuperclass() != null) {
            addFieldList(clazz.getSuperclass(), fieldMap);
        }
    }

    private Set<Field> getFieldSet(Class<? extends JpaEntity> aClass) {
        Set<Field> fieldSet = fieldMap.get(aClass);
        if (fieldSet == null) {
            addFieldSet(aClass);
            fieldSet = fieldMap.get(aClass);
        }
        return fieldSet;
    }

    private Set<Field> getIoFieldSet(Class<? extends JpaEntity> aClass) {
        Set<Field> fieldSet = ioFieldMap.get(aClass);
        if (fieldSet == null && fieldMap.get(aClass) == null) {
            addFieldSet(aClass);
            fieldSet = ioFieldMap.get(aClass);
        }
        return fieldSet;
    }

    private void addFieldSet(Class<? extends JpaEntity> aClass) {
        Set<Field> fieldSet = new HashSet<>();
        Map<String, Field> fieldAllMap = new HashMap<>();
        try {
            this.addFieldList(aClass, fieldAllMap);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        String className = aClass.getName();
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) this.dao.getSession().getSessionFactory();
        SingleTableEntityPersister entityPersister = (SingleTableEntityPersister) sessionFactory.getEntityPersister(className);
        if (entityPersister.getIdentifierColumnNames().length > 1) {
            throw new RuntimeException(className + "." + entityPersister.getIdentifierPropertyName() + ":" + StringUtils.join(entityPersister.getIdentifierColumnNames(), ","));
        }
        fieldSet.add(fieldAllMap.get(entityPersister.getIdentifierPropertyName()));
        Iterable<AttributeDefinition> iterable = entityPersister.getAttributes();
        for (AttributeDefinition attributeDefinition : iterable) {
            Type type = attributeDefinition.getType();
            if (type.isCollectionType()) {
                continue;
            }
            String[] columnNames = entityPersister.getPropertyColumnNames(attributeDefinition.getName());
            if (columnNames.length > 1) {
                throw new RuntimeException(className + "." + attributeDefinition.getName() + ":" + StringUtils.join(columnNames, ","));
            } else {
                Field field = fieldAllMap.get(attributeDefinition.getName());
                if (type instanceof ClobType || type instanceof BlobType || type instanceof NClobType) {
                    Set<Field> ioFieldSet = ioFieldMap.get(aClass);
                    if (ioFieldSet == null) {
                        ioFieldSet = new HashSet<>();
                        ioFieldMap.put(aClass, ioFieldSet);
                    }
                    ioFieldSet.add(field);
                }
                fieldSet.add(field);
            }
        }
        fieldMap.put(aClass, fieldSet);
    }
}
