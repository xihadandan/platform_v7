/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support;

import com.wellsoft.pt.basicdata.mapper.MapperDataBuilder;
import com.wellsoft.pt.basicdata.mapper.MapperException;
import com.wellsoft.pt.basicdata.mapper.MapperLoader;
import com.wellsoft.pt.basicdata.mapper.support.loader.ClasspathMapperLoader;
import com.wellsoft.pt.basicdata.mapper.support.loader.DatabaseMapperLoader;
import com.wellsoft.pt.basicdata.mapper.support.loader.URLMapperLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.dozer.classmap.MappingFileData;
import org.dozer.loader.xml.MappingStreamReader;
import org.dozer.loader.xml.XMLParserFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wellsoft.pt.basicdata.mapper.support.MapperContants.MAPPING_JSON_SCHEMA;
import static com.wellsoft.pt.basicdata.mapper.support.MapperContants.MAPPING_XML_SCHEMA;

/**
 * Description: 配置文件映射构建
 * 使用：
 * 1、DataMapper.getInstance().addDataMapper("MapperEntity.mapId", ResouceMappingBuilder.getClasspathMapppingBuilder());
 * 2、DataMapper.getInstance().map(src,dest,"MapperLoader.mapId or mapId in MapperLoader.mapId");
 * 建议：
 * 系统初始化时加载一次
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月14日.1	zhongzh		2017年10月14日		Create
 * </pre>
 * @date 2017年10月14日
 */
public class ResouceMapperBuilder implements MapperDataBuilder {

    private static Logger logger = LoggerFactory.getLogger(ResouceMapperBuilder.class);

    private MapperLoader mapperDao;

    /**
     * 如何描述该构造方法
     *
     * @param mapperDao
     */
    public ResouceMapperBuilder(MapperLoader mapperDao) {
        if (mapperDao == null) {
            throw new MapperException("ResouceMappingBuilder mapperDao is not null");
        }
        this.mapperDao = mapperDao;
    }

    public final static MapperDataBuilder getClasspathMapppingBuilder() {
        return new ResouceMapperBuilder(ClasspathMapperLoader.getInstance());
    }

    public final static MapperDataBuilder getDatabaseMapppingBuilder() {
        return new ResouceMapperBuilder(DatabaseMapperLoader.getInstance());
    }

    public final static MapperDataBuilder getURLMapppingBuilder() {
        return new ResouceMapperBuilder(URLMapperLoader.getInstance());
    }

    public static String xmlEscape(String text) {
        if (text == null) {
            return null;
        }

        StringBuffer result = new StringBuffer(text.length());

        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '&':
                    result.append("&amp;");
                    break;
                case '<':
                    result.append("&lt;");
                    break;
                case '\'':
                    result.append("&apos;");
                    break;
                case '\"':
                    result.append("&quot;");
                    break;
                default:
                    result.append(chars[i]);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        String source = "<mapping map-id=\"aa&aa\">  </mapping>";
        Document jsonp = Jsoup.parse(source);
        Elements elements = jsonp.getElementsByAttribute("map-id");
        elements = jsonp.select("mapping[map-id],field[map-id]");
        for (Element element : elements) {
            System.out.println(element.attr("map-id"));
        }
        // System.out.println(elements);
        Pattern charsetPattern = Pattern.compile(" map-id=\".*\"");
        Matcher m = charsetPattern.matcher(source);
        if (m.find()) {
            String charset = m.group();
            source = source.replace(charset, StringEscapeUtils.escapeHtml(charset));
            System.out.println(charset);
        }
        System.out.println(source);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.DefaultMapperBuilder#build(java.lang.String)
     */
    @Override
    public MappingFileData build(String mapId) {
        // 根据mapId在数据库中找最新版本映射关系
        // 根据映射关系构建MappingFileData
        //
        logger.info("Trying to find xml mapping mapId: {}", mapId);
        MappingFileData mappingFileData = null;
        String xmlStream = mapperDao.find(mapId);
        if (xmlStream != null && xmlStream.contains(MAPPING_XML_SCHEMA)) {
            xmlStream = xmlStream.replaceAll("&", "&amp;");// 处理map-id中的特殊字符
            MappingStreamReader streamReader = new MappingStreamReader(XMLParserFactory.getInstance());
            logger.info("Using xmlStream [" + xmlStream + "] to load custom xml mappings");
            mappingFileData = streamReader.read(IOUtils.toInputStream(xmlStream));
            logger.info("Successfully loaded custom xml mappings from Dao: [{}]", mapperDao);
        } else if (xmlStream != null && xmlStream.contains(MAPPING_JSON_SCHEMA)) {
            //
        } else {
            logger.info("mapId[" + mapId + "] custom xml mappings not found : [{}]", mapperDao);
        }
        return mappingFileData;
    }
}
