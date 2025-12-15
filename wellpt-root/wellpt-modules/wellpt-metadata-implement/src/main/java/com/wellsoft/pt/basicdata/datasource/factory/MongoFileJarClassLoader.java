package com.wellsoft.pt.basicdata.datasource.factory;

import cn.hutool.core.util.URLUtil;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月13日   chenq	 Create
 * </pre>
 */
public class MongoFileJarClassLoader extends URLClassLoader {

    private static Logger logger = LoggerFactory.getLogger(MongoFileJarClassLoader.class);

    public MongoFileJarClassLoader() {
        this(new URL[]{});
    }


    public MongoFileJarClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public MongoFileJarClassLoader(URL[] urls) {
        super(urls);
    }

    public MongoFileJarClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public static MongoFileJarClassLoader load(String fileId) throws IOException {
        final MongoFileJarClassLoader loader = new MongoFileJarClassLoader();
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        File jarFile = new File(baseDir + "/" + fileId + ".jar");
        logger.info("mongo file jar temp : {}", jarFile.getAbsoluteFile());
        if (jarFile.exists()) {
            loader.addURL(URLUtil.getURL(jarFile));
        } else {
            MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
            MongoFileEntity fileEntity = mongoFileService.getFile(fileId);
            if (fileEntity != null) {
                OutputStream outputStream = new FileOutputStream(jarFile);
                IOUtils.copy(fileEntity.getInputstream(), outputStream);
                outputStream.flush();
                IOUtils.closeQuietly(outputStream);
                loader.addURL(URLUtil.getURL(new File(baseDir + "/" + fileId + ".jar")));
            }
        }
        return loader;
    }


}
