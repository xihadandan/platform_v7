package com.wellsoft.pt.basicdata.iexport.suport;

import com.google.protobuf.ByteString;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.pt.basicdata.iexport.protos.ProtoData;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author yt
 * @title: ProtoDataBeanUtils
 * @date 2020/8/14 11:42
 */
public class ProtoDataBeanUtils {

    protected static final Logger logger = LoggerFactory.getLogger(ProtoDataBeanUtils.class);


    public static ProtoData.ProtoEntity toProtoData(ProtoDataBean protoDataBean) {
        ProtoData.ProtoEntity.Builder builder = ProtoData.ProtoEntity.newBuilder();
        builder.addAllParentTypeUuids(protoDataBean.getParentTypeUuids());
        builder.setType(protoDataBean.getType());
        builder.setTreeName(protoDataBean.getTreeName());
        builder.setData(ByteString.copyFrom(toByteArray(protoDataBean.getData())));
        if (protoDataBean.getFileIds() != null) {
            builder.addAllFileIds(protoDataBean.getFileIds());
        }
        return builder.build();
    }

    public static ProtoDataBean toProtoDataBean(ProtoData.ProtoEntity protoEntity) {
        JpaEntity idEntity = (JpaEntity) toObject(protoEntity.getData().toByteArray());
        String type = protoEntity.getType();
        String treeName = protoEntity.getTreeName();
        ProtoDataBean protoDataBean = new ProtoDataBean(idEntity, type, treeName);
        protoDataBean.setParentTypeUuids(protoEntity.getParentTypeUuidsList());
        protoDataBean.setFileIds(protoEntity.getFileIdsList());
        return protoDataBean;
    }

    public static byte[] toByteArray(InputStream input) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            bytes = output.toByteArray();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        return bytes;
    }

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        } catch (ClassNotFoundException ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
        return obj;
    }


}
