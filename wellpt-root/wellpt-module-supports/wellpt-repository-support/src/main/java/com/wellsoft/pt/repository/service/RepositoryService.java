package com.wellsoft.pt.repository.service;

import com.wellsoft.pt.mt.entity.Tenant;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * JCR的仓库管理接口
 *
 * @author lilin
 */
public interface RepositoryService {
    /**
     * 创建 租户的jcr仓库
     *
     * @param tenant
     */
    public void createRepository(Tenant tenant);

    /**
     * 获取 操作用户对应租户的jcr 仓库
     *
     * @return
     */
    public Repository getRepository();

    /**
     * 获取 当前操作用户对应租户的jcr session
     *
     * @return
     */
    public Session getSession();

    /**
     * 获取指定的租户的jcr session
     *
     * @return
     */
    public Session getSession(String tenantKey);


    /**
     * 获取 操作用户对应租户的jcr 根路径
     *
     * @return
     */
    public String getRootPath() throws RepositoryException;

    /**
     * 操作用户对应租户的jcr 仓库退出
     *
     * @return
     */
    public void logout();

    /**
     * 操作用户对应租户的jcr 仓库关闭
     *
     * @return
     */
    public void shutdown();

    /**
     * 租户的jcr 仓库退出
     *
     * @return
     */
    public void logout(Tenant tenant);

    /**
     * 租户的jcr 仓库关闭
     *
     * @return
     */
    public void shutdown(Tenant tenant);

    /**
     * 删除租户的jcr仓库
     *
     * @return
     */
    public void deleteRepository(Tenant tenant);

}
