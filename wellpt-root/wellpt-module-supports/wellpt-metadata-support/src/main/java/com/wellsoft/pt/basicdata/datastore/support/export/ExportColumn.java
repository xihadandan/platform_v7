package com.wellsoft.pt.basicdata.datastore.support.export;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;

public class ExportColumn {
    private String columnIndex;
    private String title;
    private RendererParam renderer;

    public String getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(String columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RendererParam getRenderer() {
        return renderer;
    }

    public void setRenderer(RendererParam renderer) {
        this.renderer = renderer;
    }

}
