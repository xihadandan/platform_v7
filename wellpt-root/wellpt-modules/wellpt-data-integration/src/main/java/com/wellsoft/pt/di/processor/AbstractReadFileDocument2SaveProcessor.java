package com.wellsoft.pt.di.processor;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.service.DiConfigService;
import com.wellsoft.pt.di.util.CamelContextUtils;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/29
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/29    chenq		2019/10/29		Create
 * </pre>
 */
public abstract class AbstractReadFileDocument2SaveProcessor extends
        AbstractDIProcessor<GenericFile> {

    public static final String NODES_RESPONSE_KEY = "READ_XML_NODES_RESPONSE";

    @Override
    void action(GenericFile file) throws Exception {
        Document document = null;
        SAXReader reader = new SAXReader();
        GenericFile body = null;
        try {
            document = reader.read((File) file.getFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            List<Node> nodes = getDocuments(document);
            if (CollectionUtils.isNotEmpty(nodes)) {
                IgnoreLoginUtils.login(Config.DEFAULT_TENANT, getSystemUnitId());
                //保存数据节点
                ReadNodesResponse response = saveNodes(getDocuments(document));
                response.setTotal(nodes.size());
                addProperties(NODES_RESPONSE_KEY, response);
            }
        } catch (Exception e) {
            logger.error("读取文档为xml节点进行数据保存异常：", e);
        } finally {
            IgnoreLoginUtils.logout();
        }

    }


    /**
     * 读取文件到MongoDB
     *
     * @param parentDir
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    protected MongoFileEntity readFile2MongoDB(String parentDir,
                                               String fileName) throws FileNotFoundException {
        String filePath = EXCHANGE.get().getIn().getHeader(Exchange.FILE_PARENT).toString();
        String fileuri = "file://" + filePath + "/" + parentDir + "?fileName=" + fileName + "&move=../.success/${date:now:yyyy-MM-dd}/" + parentDir;
        ConsumerTemplate consumerTemplate = CamelContextUtils.consumer();
        Exchange exchange = consumerTemplate.receive(fileuri, 20000L);
        if (exchange == null) {
            throw new FileNotFoundException(fileuri);
        }

        if (exchange == null || exchange.getIn() == null) {
            throw new FileNotFoundException(fileuri);
        }
        GenericFile body = (GenericFile) exchange.getIn().getBody();
        File file = (File) body.getBody();
        FileInputStream fileInputStream = new FileInputStream(file);
        MongoFileEntity fileEntity = ApplicationContextHolder.getBean(
                MongoFileService.class).saveFile(fileName, fileInputStream);

        IOUtils.closeQuietly(fileInputStream);
        consumerTemplate.doneUoW(exchange);
        return fileEntity;
    }


    /**
     * 保存数据节点
     *
     * @param nodeList
     */
    protected abstract ReadNodesResponse saveNodes(List<Node> nodeList);

    /**
     * 获取文档节点列表
     *
     * @param document
     * @return
     */
    protected List<Node> getDocuments(Document document) {
        try {
            return document.selectNodes("/root/document");
        } catch (Exception e) {
            logger.error("获取文档节点列表异常：", e);
        }
        return Collections.EMPTY_LIST;
    }


    protected String getSystemUnitId() {
        try {

            String confUuid = getProperty(DiConstant.DI_UUID_PROPERTY_NAME, String.class);
            DiConfigEntity diConfigEntity = ApplicationContextHolder.getBean(
                    DiConfigService.class).getOne(confUuid);
            OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
            OrgUserVo adminUser = orgApiFacade.getUnitAdmin(
                    diConfigEntity.getSystemUnitId());
            String userId = adminUser != null ? adminUser.getId() : MultiOrgUserAccount.PT_ACCOUNT_ID;
            if (StringUtils.isBlank(userId)) {
                throw new RuntimeException("[" + diConfigEntity.getSystemUnitId() + "]单位无系统管理员");
            }
            return userId;
        } catch (Exception e) {
            logger.error("获取归属的系统单位ID异常：", e);
            throw new RuntimeException(e);
        }
    }


    public class ReadNodesResponse implements Serializable {
        private int success = 0;

        private int total = 0;

        public ReadNodesResponse(int success) {
            this.success = success;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

}
