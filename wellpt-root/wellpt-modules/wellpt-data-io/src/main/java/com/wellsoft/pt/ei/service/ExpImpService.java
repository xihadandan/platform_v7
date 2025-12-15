package com.wellsoft.pt.ei.service;

import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.processor.utils.AttachFieldDesc;

import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2021/9/22 15:21
 * @Description:
 */
public interface ExpImpService<D, T> {

    default int order() {
        return 1;
    }

    default AttachFieldDesc getAttachFieldDesc(D d, T t) {
        return null;
    }

    default boolean isPage() {
        return true;
    }

    default String expFileName(D d) {
        return null;
    }

    /**
     * 数据Class
     *
     * @return
     */
    Class dataClass();

    /**
     * 文件路径
     *
     * @return
     */
    String filePath();

    /**
     * 文件名称
     *
     * @return
     */
    String fileName();

    /**
     * 数据类型
     *
     * @return
     */
    String dataChildType();

    /**
     * 总数
     *
     * @param systemUnitId
     * @return
     */
    long total(String systemUnitId);

    /**
     * 全部数据
     *
     * @param systemUnitId
     * @return
     */
    List<T> queryAll(String systemUnitId);

    /**
     * 分页数据
     *
     * @param systemUnitId
     * @param currentPage
     * @param pageSize
     * @return
     */
    default List<T> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        return null;
    }

    /**
     * 转为输出对象
     *
     * @param t
     * @return
     */
    D toData(T t);

    /**
     * 获取主键uuid
     *
     * @param t
     * @return
     */
    String getUuid(T t);

    /**
     * 获取主键uuid
     *
     * @param d
     * @return
     */
    String getDataUuid(D d);

    /**
     * 获取id
     *
     * @param t
     * @return
     */
    String getId(T t);

    /**
     * 获取id
     *
     * @param d
     * @return
     */
    String getDataId(D d);


    /**
     * 根据依赖情况 决定是否保存数据 或抛出异常
     * 如依赖的数据是在本次导入文件内的 需要滞后在 update 方法里去保存
     * 无论是否保存 都需要添加数据到 dependentDataMap
     *
     * @param d
     * @param systemUnitId
     * @param replace
     * @param dependentDataMap key:导入前 uuid 或 id  val:导入后 uuid 或 id
     * @return
     */
    ImportEntity<T, D> save(D d, String systemUnitId, boolean replace, Map<String, String> dependentDataMap);

    /**
     * 保存
     *
     * @param d
     * @param systemUnitId
     * @param replace      是否替换
     * @param versionId    组织版本Id
     * @return
     */
    default ImportEntity<T, D> save(D d, String systemUnitId, boolean replace, String versionId) {
        return null;
    }

    /**
     * 导入执行完成后，再执行更新
     *
     * @param t
     */
    default void update(T t, D d, Map<String, String> dependentDataMap) {

    }

    /**
     * 导入执行完成后，再执行更新
     *
     * @param t
     */
    default void update(T t, D d, String versionId, String versionName) {

    }

}
