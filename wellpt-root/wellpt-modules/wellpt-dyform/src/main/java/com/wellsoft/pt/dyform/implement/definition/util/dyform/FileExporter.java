/*
 * @(#)2013-2-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.util.dyform;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-8.1	zhulh		2013-2-8		Create
 * </pre>
 * @date 2013-2-8
 */
class FileExporter implements Exporter {
    private final FileWriter writer;

    public FileExporter(String outputFile) throws IOException {
        this.writer = new FileWriter(outputFile);
    }

    @Override
    public boolean acceptsImportScripts() {
        return false;
    }

    @Override
    public void export(String string) throws Exception {
        writer.write(string + '\n');
    }

    @Override
    public void release() throws Exception {
        writer.flush();
        writer.close();
    }
}