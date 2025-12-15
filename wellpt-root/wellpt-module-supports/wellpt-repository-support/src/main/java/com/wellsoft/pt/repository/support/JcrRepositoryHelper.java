package com.wellsoft.pt.repository.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.repository.service.RepositoryService;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author lilin
 * @ClassName: JCRRepositoryHelper
 * @Description: jcr仓库管理类
 */
public class JcrRepositoryHelper {
    private static RepositoryService getRepService() {
        return (RepositoryService) ApplicationContextHolder.getBean("RepositoryService");
    }

    public synchronized static Repository getRepository() {
        RepositoryService service = getRepService();
        return service.getRepository();
    }

    public synchronized static void shutdown() {
        RepositoryService service = getRepService();
        service.shutdown();
    }

    public synchronized static Session getSystemSession() {
        RepositoryService service = getRepService();
        return service.getSession();
    }

    public synchronized static Session getSystemSession(String tenantKey) {
        RepositoryService service = getRepService();
        return service.getSession(tenantKey);
    }

    public synchronized static String getRootPath() throws RepositoryException {
        RepositoryService service = getRepService();
        return service.getRootPath();
    }

    // public synchronized static Repository getRepository()
    // throws javax.jcr.RepositoryException {
    // logger.debug("getRepository()");
    //
    // if (repository == null) {
    // String repConfig = Configuration.CLASS_DIR + File.separator
    // + "jcr/repository-db.xml";
    //
    // // home路径为${rep.home}
    // // 的路径，这里可以进行多租户的路径定义，不可能将数据全部放置到数据库中，index数据只能放到文件中
    // String repHome = Configuration.APP_DIR + "/repository";
    // WorkspaceConfig wc = null;
    // // repository = new TransientRepository();
    // try {
    // // 重新构造xml 将datasource属性从多租户中获取，租户和配置信息，进行建立相关数据
    // // public static RepositoryConfig create(InputSource xml, String
    // // home)
    // RepositoryConfig config = RepositoryConfig.create(repConfig,
    // repHome);
    // wc = config
    // .getWorkspaceConfig(config.getDefaultWorkspaceName());
    // repository = RepositoryImpl.create(config);
    //
    // } catch (ConfigurationException e) {
    // logger.error(e.getMessage(), e);
    // throw e;
    // } catch (javax.jcr.RepositoryException e) {
    // logger.error(e.getMessage(), e);
    // throw e;
    // }
    // try {
    // systemSession = repository.login(new SimpleCredentials(
    // "username", "password".toCharArray()));
    // registerCustomNodeTypes(systemSession);
    // Node rootNode = systemSession.getRootNode();
    // Node contentNode = null;
    // try {
    // contentNode = rootNode
    // .getNode(JcrConstants.FOLDER_NODE_NAME);
    // } catch (javax.jcr.PathNotFoundException e) {
    //
    // }
    // if (contentNode == null) {
    // rootNode.addNode(JcrConstants.FOLDER_NODE_NAME);
    // }
    // } catch (ParseException e) {
    // e.printStackTrace();
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // logger.debug("getRepository: " + repository);
    // }
    //
    // return repository;
    // }

    /**
     * Close repository and free the lock
     */
    // public synchronized static void shutdown() {
    // logger.debug("shutdownRepository()");
    //
    // logout();
    //
    // systemSession = null;
    // ((RepositoryImpl) repository).shutdown();
    // repository = null;
    // logger.debug("shutdownRepository: void");
    // }
    //
    // public synchronized static void logout() {
    // if (systemSession != null && systemSession.isLive()) {
    // for (String lt : systemSession.getLockTokens()) {
    // logger.debug("Remove LockToken: {}", lt);
    // systemSession.removeLockToken(lt);
    // }
    // systemSession.logout();
    // }
    // }
    //
    // public synchronized static Session getSystemSession()
    // throws RepositoryException {
    // logger.debug("getSystemSession()");
    //
    // if (systemSession == null) {
    // getRepository();
    // return systemSession;
    // }
    // return systemSession;
    // }

    // @SuppressWarnings("unchecked")
    // private synchronized static void registerCustomNodeTypes(Session session)
    // throws InvalidNodeTypeDefinitionException, NodeTypeExistsException,
    // UnsupportedRepositoryOperationException, ParseException,
    // RepositoryException, IOException {
    // String repConfig = Configuration.CLASS_DIR + File.separator + "jcr"
    // + File.separator + Configuration.CUSTOMNODE_CONFIG;
    // InputStream is = new FileInputStream(repConfig);
    // Reader cnd = new InputStreamReader(is);
    // NodeType[] nodeTypes = CndImporter.registerNodeTypes(cnd, session);
    // }

    // public synchronized static String getRootPath() throws
    // RepositoryException {
    //
    // // Initializes Repository and SystemSession
    // getRepository();
    // Session systemSession = getSystemSession();
    // String rootPath = systemSession.getRootNode().getPath();
    // // JCRSessionManager.getInstance().putSystemSession(systemSession);
    // return rootPath;
    // }
}
