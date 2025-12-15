package com.wellsoft.pt.basicdata.iexport.suport;

import org.apache.commons.compress.utils.Lists;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月09日   chenq	 Create
 * </pre>
 */
public class NodeFolder implements Serializable {

    public NodeFolder() {
    }

    public NodeFolder(String name) {
        this.name = name;
    }

    private List<NodeFile> files = Lists.newArrayList();

    private List<NodeFolder> folders = Lists.newArrayList();


    public List<NodeFile> getFiles() {
        return files;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFiles(List<NodeFile> files) {
        this.files = files;
    }

    public static class NodeFile implements Serializable {
        private String name;
        private InputStream inputStream;

        public NodeFile() {
        }

        public NodeFile(String name, InputStream inputStream) {
            this.name = name;
            this.inputStream = inputStream;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }

    public List<NodeFolder> getFolders() {
        return folders;
    }

    public void setFolders(List<NodeFolder> folders) {
        this.folders = folders;
    }
}
