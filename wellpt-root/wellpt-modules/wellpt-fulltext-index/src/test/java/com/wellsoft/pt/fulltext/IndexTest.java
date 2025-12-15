package com.wellsoft.pt.fulltext;


/***
 * es6 升级  es7
 * @author baozh
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.wellsoft.pt.fulltext.index.WellFlowDocumentIndex;
import com.wellsoft.pt.fulltext.index.WordAttachmentIndex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;***/

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月08日   chenq	 Create
 * </pre>
 */
public class IndexTest {

    /**
     * es6 升级  es7
     *  @author baozh
     * * private String host = "localhost";
    private int port = 9300;
    private ElasticsearchTemplate template;


     @Before public void init() {
     try {
     template = new ElasticsearchTemplate(TransportClient
     .builder()
     .build()
     .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port)));
     } catch (Exception e) {

     throw new RuntimeException(e);
     }
     }

     @Test public void testCreateIndexMapping() {
     Assert.assertTrue(template.deleteIndex(WellFlowDocumentIndex.class));
     Assert.assertTrue(template.createIndex(WellFlowDocumentIndex.class));
     Assert.assertTrue(template.putMapping(WellFlowDocumentIndex.class));
     }

     @Test public void testCreateAttachmentIndexMapping() {
     //        Assert.assertTrue(template.deleteIndex(WordAttachmentIndex.class));
     Assert.assertTrue(template.createIndex(WordAttachmentIndex.class));
     Assert.assertTrue(template.putMapping(WordAttachmentIndex.class));
     }

     class Data implements Serializable {
     private List<WellFlowDocumentIndex> indices;

     public List<WellFlowDocumentIndex> getIndices() {
     return indices;
     }

     public void setIndices(List<WellFlowDocumentIndex> indices) {
     this.indices = indices;
     }
     }

     @Test public void testIndexDocument() throws Exception {
     String string = IOUtils.toString(ClassLoader.getSystemResource("test_document.json").openStream());
     Data data = new Gson().fromJson(string, Data.class);
     if (data.getIndices() != null) {
     List<IndexQuery> indexQueries = Lists.newArrayList();
     for (WellFlowDocumentIndex index : data.getIndices()) {
     IndexQueryBuilder indexQueryBuilder = new IndexQueryBuilder();
     indexQueryBuilder.withObject(index);
     indexQueryBuilder.withId(System.currentTimeMillis() + RandomUtils.nextInt(1000000, 9000000) + "");
     indexQueries.add(indexQueryBuilder.build());
     }
     template.bulkIndex(indexQueries);
     }

     }***/
}
