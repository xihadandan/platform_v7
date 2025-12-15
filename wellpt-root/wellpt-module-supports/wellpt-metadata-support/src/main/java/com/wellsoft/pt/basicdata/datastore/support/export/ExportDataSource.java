package com.wellsoft.pt.basicdata.datastore.support.export;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExportDataSource implements DataSource {
    private InputStream is;
    private String contentType;
    private String name;

    public ExportDataSource(InputStream is, String contentType, String name) {
        this.is = is;
        this.contentType = contentType;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() {
        return is;
    }

    ;

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see javax.activation.DataSource#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new RuntimeException("不支持OutputStream");
    }
}
