package com.wellpt.pt.ant;

/*
 * @(#)2020年1月4日 V1.0
 * 
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.Appendable;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Touchable;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.LineTokenizer;
import org.apache.tools.ant.util.ResourceUtils;

/**
 * Description: 如何描述该类
 *  
 * @author zhongzh
 * @date 2020年1月4日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月4日.1	zhongzh		2020年1月4日		Create
 * </pre>
 *
 */
public class MyCopy extends Copy {

    /** Utilities used for file operations */
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final long MAX_IO_CHUNK_SIZE = 16 * 1024 * 1024; // 16 MB

    static final int BUF_SIZE = 8192;

    /**
     * Actually does the file (and possibly empty directory) copies.
     * This is a good method for subclasses to override.
     */
    protected void doFileOperations() {
        if (fileCopyMap.size() > 0) {
            log("Copying " + fileCopyMap.size() + " file" + (fileCopyMap.size() == 1 ? "" : "s") + " to "
                    + destDir.getAbsolutePath());

            Enumeration e = fileCopyMap.keys();
            while (e.hasMoreElements()) {
                String fromFile = (String) e.nextElement();
                String[] toFiles = (String[]) fileCopyMap.get(fromFile);

                for (int i = 0; i < toFiles.length; i++) {
                    String toFile = toFiles[i];

                    if (fromFile.equals(toFile)) {
                        log("Skipping self-copy of " + fromFile, verbosity);
                        continue;
                    }
                    try {
                        log("Copying " + fromFile + " to " + toFile, verbosity);

                        FilterSetCollection executionFilters = new FilterSetCollection();
                        if (filtering) {
                            executionFilters.addFilterSet(getProject().getGlobalFilterSet());
                        }
                        for (Enumeration filterEnum = getFilterSets().elements(); filterEnum.hasMoreElements();) {
                            executionFilters.addFilterSet((FilterSet) filterEnum.nextElement());
                        }
                        MyCopy.copyFile(new File(fromFile), new File(toFile), executionFilters, getFilterChains(),
                                forceOverwrite, preserveLastModified,
                                /* append: */false, getEncoding(), getOutputEncoding(), getProject(), getForce());
                    } catch (IOException ioe) {
                        String msg = "Failed to copy " + fromFile + " to " + toFile + " due to " + getDueTo(ioe);
                        File targetFile = new File(toFile);
                        if (targetFile.exists() && !targetFile.delete()) {
                            msg += " and I couldn't delete the corrupt " + toFile;
                        }
                        if (failonerror) {
                            throw new BuildException(msg, ioe, getLocation());
                        }
                        log(msg, Project.MSG_ERR);
                    }
                }
            }
        }
        if (includeEmpty) {
            Enumeration e = dirCopyMap.elements();
            int createCount = 0;
            while (e.hasMoreElements()) {
                String[] dirs = (String[]) e.nextElement();
                for (int i = 0; i < dirs.length; i++) {
                    File d = new File(dirs[i]);
                    if (!d.exists()) {
                        if (!d.mkdirs()) {
                            log("Unable to create directory " + d.getAbsolutePath(), Project.MSG_ERR);
                        } else {
                            createCount++;
                        }
                    }
                }
            }
            if (createCount > 0) {
                log("Copied " + dirCopyMap.size() + " empty director" + (dirCopyMap.size() == 1 ? "y" : "ies") + " to "
                        + createCount + " empty director" + (createCount == 1 ? "y" : "ies") + " under "
                        + destDir.getAbsolutePath());
            }
        }
    }

    /**
     * Actually does the resource copies.
     * This is a good method for subclasses to override.
     * @param map a map of source resource to array of destination files.
     * @since Ant 1.7
     */
    protected void doResourceOperations(Map map) {
        if (map.size() > 0) {
            log("Copying " + map.size() + " resource" + (map.size() == 1 ? "" : "s") + " to "
                    + destDir.getAbsolutePath());

            Iterator iter = map.keySet().iterator();
            while (iter.hasNext()) {
                Resource fromResource = (Resource) iter.next();
                String[] toFiles = (String[]) map.get(fromResource);

                for (int i = 0; i < toFiles.length; i++) {
                    String toFile = toFiles[i];

                    try {
                        log("Copying " + fromResource + " to " + toFile, verbosity);

                        FilterSetCollection executionFilters = new FilterSetCollection();
                        if (filtering) {
                            executionFilters.addFilterSet(getProject().getGlobalFilterSet());
                        }
                        for (Enumeration filterEnum = getFilterSets().elements(); filterEnum.hasMoreElements();) {
                            executionFilters.addFilterSet((FilterSet) filterEnum.nextElement());
                        }
                        MyCopy.copyResource(fromResource, new FileResource(destDir, toFile), executionFilters,
                                getFilterChains(), forceOverwrite, preserveLastModified,
                                /* append: */false, getEncoding(), getOutputEncoding(), getProject(), getForce());
                    } catch (IOException ioe) {
                        String msg = "Failed to copy " + fromResource + " to " + toFile + " due to " + getDueTo(ioe);
                        File targetFile = new File(toFile);
                        if (targetFile.exists() && !targetFile.delete()) {
                            msg += " and I couldn't delete the corrupt " + toFile;
                        }
                        if (failonerror) {
                            throw new BuildException(msg, ioe, getLocation());
                        }
                        log(msg, Project.MSG_ERR);
                    }
                }
            }
        }
    }

    /**
     * Returns a reason for failure based on
     * the exception thrown.
     * If the exception is not IOException output the class name,
     * output the message
     * if the exception is MalformedInput add a little note.
     */
    private String getDueTo(Exception ex) {
        boolean baseIOException = ex.getClass() == IOException.class;
        StringBuffer message = new StringBuffer();
        if (!baseIOException || ex.getMessage() == null) {
            message.append(ex.getClass().getName());
        }
        if (ex.getMessage() != null) {
            if (!baseIOException) {
                message.append(" ");
            }
            message.append(ex.getMessage());
        }
        if (ex.getClass().getName().indexOf("MalformedInput") != -1) {
            message.append(LINE_SEPARATOR);
            message.append("This is normally due to the input file containing invalid");
            message.append(LINE_SEPARATOR);
            message.append("bytes for the character encoding used : ");
            message.append((getEncoding() == null ? fileUtils.getDefaultEncoding() : getEncoding()));
            message.append(LINE_SEPARATOR);
        }
        return message.toString();
    }

    public static void copyFile(File sourceFile, File destFile, FilterSetCollection filters, Vector filterChains,
            boolean overwrite, boolean preserveLastModified, boolean append, String inputEncoding,
            String outputEncoding, Project project, boolean force) throws IOException {
        MyCopy.copyResource(new FileResource(sourceFile), new FileResource(destFile), filters, filterChains, overwrite,
                preserveLastModified, append, inputEncoding, outputEncoding, project, force);
    }

    /**
     * Convenience method to copy content from one Resource to another
     * specifying whether token filtering must be used, whether filter chains
     * must be used, whether newer destination files may be overwritten and
     * whether the last modified time of <code>dest</code> file should be made
     * equal to the last modified time of <code>source</code>.
     *
     * @param source the Resource to copy from.
     *                   Must not be <code>null</code>.
     * @param dest   the Resource to copy to.
     *                 Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param filterChains filterChains to apply during the copy.
     * @param overwrite Whether or not the destination Resource should be
     *                  overwritten if it already exists.
     * @param preserveLastModified Whether or not the last modified time of
     *                             the destination Resource should be set to that
     *                             of the source.
     * @param append Whether to append to an Appendable Resource.
     * @param inputEncoding the encoding used to read the files.
     * @param outputEncoding the encoding used to write the files.
     * @param project the project instance.
     * @param force whether read-only taret files will be overwritten
     *
     * @throws IOException if the copying fails.
     *
     * @since Ant 1.8.2
     */
    public static void copyResource(Resource source, Resource dest, FilterSetCollection filters, Vector filterChains,
            boolean overwrite, boolean preserveLastModified, boolean append, String inputEncoding,
            String outputEncoding, Project project, boolean force) throws IOException {
        if (!(overwrite || SelectorUtils.isOutOfDate(source, dest, FileUtils.getFileUtils()
                .getFileTimestampGranularity()))) {
            return;
        }
        final boolean filterSetsAvailable = (filters != null && filters.hasFilters());
        final boolean filterChainsAvailable = (filterChains != null && filterChains.size() > 0);

        File destFile = null;
        if (dest.as(FileProvider.class) != null) {
            destFile = ((FileProvider) dest.as(FileProvider.class)).getFile();
        }
        if (destFile != null && destFile.isFile() && !destFile.canWrite()) {
            if (!force) {
                throw new IOException("can't write to read-only destination " + "file " + destFile);
            } else if (!FILE_UTILS.tryHardToDelete(destFile)) {
                throw new IOException("failed to delete read-only " + "destination file " + destFile);
            }
        }

        if (filterSetsAvailable) {
            BufferedReader in = null;
            BufferedWriter out = null;
            try {
                Reader isr = null;
                if (inputEncoding == null) {
                    isr = new InputStreamReader(source.getInputStream());
                } else if (inputEncoding.equalsIgnoreCase("auto")) {
                    isr = getAutoDetectReader(source);
                } else {
                    isr = new InputStreamReader(source.getInputStream(), inputEncoding);
                }
                in = new BufferedReader(isr);
                OutputStream os = getOutputStream(dest, append, project);
                OutputStreamWriter osw;
                if (outputEncoding == null) {
                    osw = new OutputStreamWriter(os);
                } else {
                    osw = new OutputStreamWriter(os, outputEncoding);
                }
                out = new BufferedWriter(osw);
                if (filterChainsAvailable) {
                    ChainReaderHelper crh = new ChainReaderHelper();
                    crh.setBufferSize(MyCopy.BUF_SIZE);
                    crh.setPrimaryReader(in);
                    crh.setFilterChains(filterChains);
                    crh.setProject(project);
                    Reader rdr = crh.getAssembledReader();
                    in = new BufferedReader(rdr);
                }
                LineTokenizer lineTokenizer = new LineTokenizer();
                lineTokenizer.setIncludeDelims(true);
                String newline = null;
                String line = lineTokenizer.getToken(in);
                while (line != null) {
                    if (line.length() == 0) {
                        // this should not happen, because the lines are
                        // returned with the end of line delimiter
                        out.newLine();
                    } else {
                        newline = filters.replaceTokens(line);
                        out.write(newline);
                    }
                    line = lineTokenizer.getToken(in);
                }
            } finally {
                FileUtils.close(out);
                FileUtils.close(in);
            }
        } else if (filterChainsAvailable || (inputEncoding != null && !inputEncoding.equals(outputEncoding))
                || (inputEncoding == null && outputEncoding != null)) {
            BufferedReader in = null;
            BufferedWriter out = null;
            try {
                Reader isr = null;
                if (inputEncoding == null) {
                    isr = new InputStreamReader(source.getInputStream());
                } else if (inputEncoding.equalsIgnoreCase("auto")) {
                    isr = getAutoDetectReader(source);
                } else {
                    isr = new InputStreamReader(source.getInputStream(), inputEncoding);
                }
                in = new BufferedReader(isr);
                OutputStream os = getOutputStream(dest, append, project);
                OutputStreamWriter osw;
                if (outputEncoding == null) {
                    osw = new OutputStreamWriter(os);
                } else {
                    osw = new OutputStreamWriter(os, outputEncoding);
                }
                out = new BufferedWriter(osw);
                if (filterChainsAvailable) {
                    ChainReaderHelper crh = new ChainReaderHelper();
                    crh.setBufferSize(MyCopy.BUF_SIZE);
                    crh.setPrimaryReader(in);
                    crh.setFilterChains(filterChains);
                    crh.setProject(project);
                    Reader rdr = crh.getAssembledReader();
                    in = new BufferedReader(rdr);
                }
                char[] buffer = new char[MyCopy.BUF_SIZE];
                while (true) {
                    int nRead = in.read(buffer, 0, buffer.length);
                    if (nRead == -1) {
                        break;
                    }
                    out.write(buffer, 0, nRead);
                }
            } finally {
                FileUtils.close(out);
                FileUtils.close(in);
            }
        } else if (source.as(FileProvider.class) != null && destFile != null) {
            File sourceFile = ((FileProvider) source.as(FileProvider.class)).getFile();

            File parent = destFile.getParentFile();
            if (parent != null && !parent.isDirectory() && !destFile.getParentFile().mkdirs()) {
                throw new IOException("failed to create the parent directory" + " for " + destFile);
            }

            FileInputStream in = null;
            FileOutputStream out = null;
            FileChannel srcChannel = null;
            FileChannel destChannel = null;

            try {
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);

                srcChannel = in.getChannel();
                destChannel = out.getChannel();

                long position = 0;
                long count = srcChannel.size();
                while (position < count) {
                    long chunk = Math.min(MAX_IO_CHUNK_SIZE, count - position);
                    position += destChannel.transferFrom(srcChannel, position, chunk);
                }
            } finally {
                FileUtils.close(srcChannel);
                FileUtils.close(destChannel);
                FileUtils.close(out);
                FileUtils.close(in);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = source.getInputStream();
                out = getOutputStream(dest, append, project);

                byte[] buffer = new byte[MyCopy.BUF_SIZE];
                int count = 0;
                do {
                    out.write(buffer, 0, count);
                    count = in.read(buffer, 0, buffer.length);
                } while (count != -1);
            } finally {
                FileUtils.close(out);
                FileUtils.close(in);
            }
        }
        if (preserveLastModified) {
            Touchable t = (Touchable) dest.as(Touchable.class);
            if (t != null) {
                ResourceUtils.setLastModified(t, source.getLastModified());
            }
        }
    }

    /**
     * AutoDetectReader
     * 
     * @param source
     * @return
     * @throws IOException
     * @throws TikaException
     */
    private static Reader getAutoDetectReader(Resource source) {
        try {
            Metadata metadata = new Metadata();
            metadata.add(Metadata.RESOURCE_NAME_KEY, source.getName());
            metadata.add(Metadata.CONTENT_LENGTH, source.getSize() + "");
            System.out.println("[" + source.getName() + "] Detect Success");
            return new AutoDetectReader(source.getInputStream(), metadata);
        } catch (Exception ex) {
            throw new RuntimeException("[" + source.getName() + "] Detect Fail:" + ex.getMessage(), ex);
        }
    }

    private static OutputStream getOutputStream(Resource resource, boolean append, Project project) throws IOException {
        if (append) {
            Appendable a = (Appendable) resource.as(Appendable.class);
            if (a != null) {
                return a.getAppendOutputStream();
            }
            project.log("Appendable OutputStream not available for non-appendable resource " + resource
                    + "; using plain OutputStream", Project.MSG_VERBOSE);
        }
        return resource.getOutputStream();
    }
}
