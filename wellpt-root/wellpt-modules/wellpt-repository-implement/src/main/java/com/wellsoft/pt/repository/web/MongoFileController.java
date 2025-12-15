package com.wellsoft.pt.repository.web;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.mime.CommonUtils;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.encode.JsonBinder;
import com.wellsoft.context.util.encode.Md5PasswordEncoderUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.file.ZipUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.dytable.service.FormSignatureDataService;
import com.wellsoft.pt.repository.convert.FileConvertService;
import com.wellsoft.pt.repository.convert.util.DocumentWatermarkUtils;
import com.wellsoft.pt.repository.dto.FileChunkInfoResponseDto;
import com.wellsoft.pt.repository.entity.FileUpload;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.WatermarkStyle;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.repository.support.ImageResizeTool;
import com.wellsoft.pt.repository.support.RepoUtils;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

@Controller
@RequestMapping(path = {"/repository/file/mongo"})
public class MongoFileController extends BaseController {
    public static final String FILE_UPLOAD_PATH = Config.APP_DATA_DIR + File.separator + "upload_tmp_file"
            + File.separator;
    public final static String pathSeparator = File.separator;
    public final static String appDataDir = Config.APP_DATA_DIR + pathSeparator + "downloadallfiles" + pathSeparator;
    private static final int KB = 10240;
    @Autowired
    FormSignatureDataService formSignatureDataService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    @Qualifier("suwellConvertService")
    private FileConvertService fileConvertService;
    @Autowired
    @Qualifier("spireConvertService")
    private FileConvertService spireFileConvertService;

    /**
     * 对文件用MD5算法生成消息摘要
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String digestAsMD5(File file) throws Exception {
        MessageDigest m = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
        FileChannel ch = fis.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        m.update(byteBuffer);
        byte s[] = m.digest();
        IOUtils.closeQuietly(fis);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            sb.append(Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6));
        }
        return sb.toString();
    }

    @PostConstruct
    public void init() throws ServletException {
        File uploadFolder = new File(FILE_UPLOAD_PATH);
        if (uploadFolder.mkdirs()) {
            //
        }
    }

    @RequestMapping(value = "/upgrade", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage upgrade(@RequestPart("file") MultipartFile multifile,
                                 @RequestParam(value = "fileId") String fileId, @RequestParam(value = "fileName") String fileName) {
        InputStream inputStream = null;
        final ResultMessage resultMessage = new ResultMessage();
        try {
            inputStream = multifile.getInputStream();
            final MongoFileEntity entity = mongoFileService.saveFile(fileId, fileName, inputStream);
            resultMessage.setData(entity.getMd5());
            return resultMessage;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new ResultMessage(e.getMessage(), false);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @RequestMapping(value = "/getNonioFilesFromFolder")
    public void getNonioFilesFromFolder(HttpServletResponse response,
                                        @RequestParam(value = "folderID") String folderID, @RequestParam(value = "purpose") String purpose)
            throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        List<LogicFileInfo> data = null;
        try {
            data = mongoFileService.getNonioFilesFromFolder(folderID, purpose);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
            return;
        }
        resultMessage.setSuccess(true);
        resultMessage.setData(data);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
    }

    /**
     * 获取文件信息
     *
     * @param response
     * @param fileID
     * @throws IOException
     */
    @RequestMapping(value = "/getNonioFiles")
    public void getNonioFiles(HttpServletResponse response, @RequestParam(value = "fileID") String fileID)
            throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        List<LogicFileInfo> data = new ArrayList<LogicFileInfo>();
        try {
            List<String> fileIDs = Arrays.asList(StringUtils.split(fileID, Separator.SEMICOLON.getValue()));
            Iterator<String> it = fileIDs.iterator();
            while (it.hasNext()) {
                MongoFileEntity mongoFileEntity = mongoFileService.getFile(it.next());
                if (mongoFileEntity != null) {
                    LogicFileInfo logicFileInfo = mongoFileEntity.getLogicFileInfo();
                    // 设置MD5值
                    if (StringUtils.isBlank(logicFileInfo.getDigestAlgorithm()) && StringUtils.isBlank(logicFileInfo.getDigestValue())) {
                        logicFileInfo.setDigestAlgorithm("MD5");
                        logicFileInfo.setDigestValue(mongoFileEntity.getMd5());
                    }
                    data.add(logicFileInfo);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
            return;
        }
        resultMessage.setSuccess(true);
        resultMessage.setData(data);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
    }

    /**
     * 文件上传
     */
    @RequestMapping(value = "/saveFilesByFileIds", method = RequestMethod.POST)
    public void saveFilesByFileIds(HttpServletResponse response, @RequestParam(value = "fileIds[]") String[] fileIds,
                                   @RequestParam(value = "fileSourceIcon", defaultValue = "", required = false) String fileSourceIcon,
                                   @RequestParam(value = "signUploadFile", defaultValue = "false", required = false) boolean signUploadFile)
            throws IOException {

        ResultMessage resultMessage = new ResultMessage();
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();

        for (String fileId : fileIds) {
            MongoFileEntity file = mongoFileService.getFile(fileId);
            if (file == null) {
                resultMessage.setSuccess(false);
                resultMessage.setData(uploadFiles);
                resultMessage.setMsg(new StringBuilder().append("上传失败，文件id错误"));
                response.setContentType("text/html; charset=utf-8");
                response.setStatus(HttpStatus.OK.value());
                response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
                return;
            }

            String fileName = file.getFileName();
            String contentType = fileName.substring(fileName.lastIndexOf(".") + 1);
            try {
                FileUpload fileUpload = saveFileCore(signUploadFile, fileName, file.getInputstream(), contentType,
                        file.getLength(), fileSourceIcon, null);
                uploadFiles.add(fileUpload);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        resultMessage.setSuccess(true);
        resultMessage.setData(uploadFiles);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
    }

    /**
     * 根据md5获取对应文件块后端存储情况并保存文件
     */
    @GetMapping(value = "/getFileChunkInfoAndSave")
    public void getFileChunkInfoAndSave(HttpServletResponse response, @RequestParam(value = "md5") String md5,
                                        @RequestParam(value = "chunkSize") int chunkSize, @RequestParam(value = "fileName") String fileName,
                                        @RequestParam(value = "fileSourceIcon") String fileSourceIcon) throws IOException {
        FileChunkInfoResponseDto fileChunkInfo = mongoFileService.getFileChunkInfo(md5, chunkSize);
        //		if (fileChunkInfo.isHasMd5FileFlag()) {
        //			List<FileUpload> fileUploadList = mongoFileService.saveFileByMd5(md5, fileName, fileSourceIcon);
        //			fileChunkInfo.setUploadFiles(fileUploadList);
        //		}
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setSuccess(true);
        resultMessage.setData(fileChunkInfo);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage));
    }

    /**
     * 根据md5获取对应文件块后端存储情况
     */
    @GetMapping(value = "/getFileChunkInfo")
    public void getFileChunkInfo(HttpServletResponse response, @RequestParam(value = "md5") String md5,
                                 @RequestParam(value = "chunkSize") int chunkSize) throws IOException {
        FileChunkInfoResponseDto fileChunkInfo = mongoFileService.getFileChunkInfo(md5, chunkSize);
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setSuccess(true);
        resultMessage.setData(fileChunkInfo);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage));
    }

    /**
     * 文件上传
     */
    @RequestMapping(value = "/savefilesChunk", method = RequestMethod.POST)
    public void savefilesChunk(MultipartHttpServletRequest multipartRequest, HttpServletResponse response,
                               @RequestParam(value = "signUploadFile", defaultValue = "false", required = false) boolean signUploadFile,
                               @RequestParam(value = "fun", required = false) String fun,
                               @RequestParam(required = false, defaultValue = "false") boolean anonymous) throws IOException {
        ResultMessage resultMessage = new ResultMessage();

        String contentRange = multipartRequest.getHeader("Content-Range");
        if (contentRange == null) {
            //完整文件，直接保存
            this.saveFiles(multipartRequest, response, signUploadFile, fun, null, anonymous);
        } else {
            String chunkSize = multipartRequest.getParameter("chunkSize");
            String source = multipartRequest.getParameter("source");
            String localFileSourceIcon = multipartRequest.getParameter("localFileSourceIcon");
            String md5 = multipartRequest.getParameter("md5");

            String[] contentRangeSplit = contentRange.substring("bytes ".length()).split("/");
            String[] numberRange = contentRangeSplit[0].split("-");
            String total = contentRangeSplit[1];
            int chunkSizeInt = Integer.parseInt(chunkSize);
            int chunkIndex = (Integer.parseInt(numberRange[0])) / chunkSizeInt;
            signUploadFile = false;

            GridFSDBFile fileByMd5 = this.mongoFileService.findFileByMd5(md5);
            String fileName = UUID.randomUUID().toString();
            Iterator<String> iterator2 = multipartRequest.getFileNames();
            while (iterator2.hasNext()) {
                String fieldName = iterator2.next();
                MultipartFile multifile = multipartRequest.getFile(fieldName);
                fileName = multifile.getOriginalFilename();
            }

            if (fileByMd5 == null) {
                List<FileUpload> uploadFiles = new ArrayList<FileUpload>();
                try {
                    Iterator<String> iterator = multipartRequest.getFileNames();
                    while (iterator.hasNext()) {
                        String fieldName = iterator.next();
                        MultipartFile multifile = multipartRequest.getFile(fieldName);
                        fileName = multifile.getOriginalFilename();
                        InputStream multifileIS = multifile.getInputStream();
                        String contentType = multifile.getContentType();
                        long size = multifile.getSize();
                        FileUpload fileUpload = new FileUpload();
                        if (signUploadFile) {
                            //
                        } else {
                            mongoFileService.saveChunkFile(fileName, md5, chunkIndex, chunkSizeInt, size, multifileIS);
                        }
                        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                        fileUpload.setUserId(userDetails.getUserId());
                        fileUpload.setUserName(userDetails.getUserName());
                        fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
                        fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
                        fileUpload.setFilename(fileName);
                        fileUpload.setContentType(contentType);
                        fileUpload.setFileSize(size);
                        fileUpload.setCreator(userDetails.getUserId());
                        fileUpload.setCreateTime(new Date());
                        fileUpload.setUploadTime(new Date());
                        uploadFiles.add(fileUpload);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    resultMessage.setSuccess(false);
                    resultMessage.setMsg(new StringBuilder(e.getMessage()));
                    response.setContentType("text/html; charset=utf-8");
                    response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
                    response.getWriter().write(
                            this.getFun(fun, JsonBinder.buildNormalBinder().toJson(resultMessage).toString()));
                    return;
                }
            }

            if (Integer.toString(Integer.parseInt(numberRange[1]) + 1).equals(total)) {
                //最后一个分块
                if (fileByMd5 == null) {
                    List<GridFSDBFile> gridFSDBChunkFiles = Lists.newArrayList(this.mongoFileService.findFileByMetadataMd5(md5));
                    if ((chunkIndex + 1) != gridFSDBChunkFiles.size()) {
                        // 可能存在文件内容相同，但是文件不同的情况，但是md5值是一样的，要剔除非此文件名的chunk文件
                        Iterator<GridFSDBFile> gridFSDBFileIterator = gridFSDBChunkFiles.iterator();
                        while (gridFSDBFileIterator.hasNext()) {
                            if (!gridFSDBFileIterator.next().getFilename().equals(fileName)) {
                                gridFSDBFileIterator.remove();
                            }
                        }
                        if ((chunkIndex + 1) != gridFSDBChunkFiles.size()) {
                            //没有将所有的切块上传
                            throw new BusinessException("上传失败，请重新上传！");
                        }
                    }

                    // 系统不存在该文件
                    //                int chunkTotal = (Integer.parseInt(total) + chunkSizeInt - 1) / chunkSizeInt;
                    File uploadDir = new File(Config.UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    String tempUuid = UUID.randomUUID().toString();
                    File tempFile = new File(uploadDir, tempUuid);
                    FileOutputStream fos = new FileOutputStream(tempFile);

                    for (GridFSDBFile gridFSDBFile : gridFSDBChunkFiles) {
                        InputStream gridFSDBFileInputStream = gridFSDBFile.getInputStream();
                        IOUtils.copyLarge(gridFSDBFileInputStream, fos);
                        gridFSDBFileInputStream.close();
                    }
                    IOUtils.closeQuietly(fos);
                    try {
                        List<FileUpload> fileUploads = saveFilesFromChunk(fileName, tempFile.length(),
                                new FileInputStream(tempFile), response, signUploadFile, localFileSourceIcon, source);
                        if (CollectionUtils.isNotEmpty(fileUploads)) {
                            for (GridFSDBFile gridFSDBFile : gridFSDBChunkFiles) {
                                this.mongoFileService.deleteByGridFSDBFile(gridFSDBFile);
                            }
                        }
                    } finally {
                        tempFile.delete();
                    }
                } else {
                    List<FileUpload> fileUploadList = mongoFileService
                            .saveFileByMd5(md5, fileName, localFileSourceIcon);
                    resultMessage.setSuccess(true);
                    resultMessage.setData(fileUploadList);
                    if (anonymous && CollectionUtils.isNotEmpty(fileUploadList)) {
                        /**
                         * 文件允许匿名访问，则发放匿名地址加密token，浏览器可以通过文件地址后加上 guest-uri-token 进行匿名访问
                         * 例如: http://域名/proxy-repository/repository/file/mongo/download?guest-uri-token=xxxxxx
                         */
                        response.setHeader("guest-uri-token"
                                , JwtTokenUtil.jwtBuilder().setSubject("/repository/file/mongo/download?fileID=" + fileUploadList.get(0).getFileID())
                                        .compact());
                    }
                    response.setContentType("text/html; charset=utf-8");
                    response.setStatus(HttpStatus.OK.value());
                    response.getWriter().write(this.getFun(fun, JsonBinder.buildNormalBinder().toJson(resultMessage)));
                }
            } else {
                resultMessage.setSuccess(true);
                resultMessage.setData("continue");
                response.setContentType("text/html; charset=utf-8");
                response.setStatus(HttpStatus.OK.value());
                response.getWriter().write(this.getFun(fun, JsonBinder.buildNormalBinder().toJson(resultMessage)));
            }
        }
    }

    public List<FileUpload> saveFilesFromChunk(String fileName, long fileSize, InputStream inputStream,
                                               HttpServletResponse response, boolean signUploadFile, String localFileSourceIcon, String source)
            throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();

        try {
            String contentType = fileName.substring(fileName.lastIndexOf(".") + 1);
            FileUpload fileUpload = saveFileCore(signUploadFile, fileName, inputStream, contentType, fileSize,
                    localFileSourceIcon, source);
            uploadFiles.add(fileUpload);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
            return null;
        }
        resultMessage.setSuccess(true);
        resultMessage.setData(uploadFiles);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());

        return uploadFiles;
    }

    @GetMapping("/makeWatermark/{fileID}")
    public @ResponseBody
    ApiResult<Boolean> makeFileWatermark(@RequestParam String watermarkText, @PathVariable String fileID) {
        WatermarkStyle style = new WatermarkStyle(watermarkText);
        this.makeFileWatermark(style, fileID);
        return ApiResult.success(true);
    }


    @PostMapping("/makeWatermark/{fileID}")
    public @ResponseBody
    ApiResult<Boolean> makeFileWatermark(@RequestBody WatermarkStyle watermarkStyle, @PathVariable String fileID) {
        if ((WatermarkStyle.WatermarkType.text.equals(watermarkStyle.getType()) && StringUtils.isBlank(watermarkStyle.getText())) || (
                WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType()) && StringUtils.isBlank(watermarkStyle.getImageFileId())
        )) {
            return ApiResult.success(true);
        }
        MongoFileEntity file = mongoFileService.getFile(fileID);
        if (file != null) {
            File tempFile = null;
            try {
                int dotIndex = file.getFileName().lastIndexOf(".");
                String tempFileName = file.getFileName().substring(0, dotIndex) + "_(水印)_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + file.getFileName().substring(dotIndex);
                tempFile = new File(new File(System.getProperty("java.io.tmpdir")), tempFileName);
                tempFile.createNewFile();
                logger.info("创建临时水印文件: {}", tempFile.getAbsolutePath());
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                if (file.getFileName().endsWith(".doc") || file.getFileName().endsWith(".docx") || file.getFileName().endsWith(".wps")) {
                    DocumentWatermarkUtils.addDocWatermark(file.getInputstream(), fileOutputStream, watermarkStyle);
                    IOUtils.closeQuietly(fileOutputStream);
                    mongoFileService.updateFileInputstream(fileID, file.getFileName(), file.getContentType(), new FileInputStream(tempFile));
                } else if (file.getFileName().endsWith(".ppt") || file.getFileName().endsWith(".pptx")) {
                    DocumentWatermarkUtils.addPptWatermark(file.getInputstream(), fileOutputStream, watermarkStyle);
                    IOUtils.closeQuietly(fileOutputStream);
                    mongoFileService.updateFileInputstream(fileID, file.getFileName(), file.getContentType(), new FileInputStream(tempFile));
                } else if (file.getFileName().endsWith(".xls") || file.getFileName().endsWith(".xlsx")) {
                    DocumentWatermarkUtils.addExcelWatermark(file.getInputstream(), fileOutputStream, watermarkStyle);
                    IOUtils.closeQuietly(fileOutputStream);
                    mongoFileService.updateFileInputstream(fileID, file.getFileName(), file.getContentType(), new FileInputStream(tempFile));
                } else if (file.getFileName().endsWith(".pdf")) {
                    DocumentWatermarkUtils.addPdfBoxWatermark(file.getInputstream(), fileOutputStream, watermarkStyle);
                    IOUtils.closeQuietly(fileOutputStream);
                    mongoFileService.updateFileInputstream(fileID, file.getFileName(), file.getContentType(), new FileInputStream(tempFile));
                } else if (file.getFileName().endsWith(".png") || file.getFileName().endsWith(".jpg") || file.getFileName().endsWith(".jpeg") || "image/jpeg".equalsIgnoreCase(file.getContentType())) {
                    // 创建临时Graphics获取文本尺寸
                    if (WatermarkStyle.WatermarkType.text.equals(watermarkStyle.getType())) {
                        BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = temp.createGraphics();
                        Font font = DocumentWatermarkUtils.jFont.deriveFont(watermarkStyle.getFontSize() == null ? 24f : (float) watermarkStyle.getFontSize());// new Font(watermarkStyle.getFontFamily(), Font.PLAIN, watermarkStyle.getFontSize());
                        g.setFont(font);
                        FontMetrics metrics = g.getFontMetrics();
                        int padding = 0;
                        int width = metrics.stringWidth(watermarkStyle.getText()) + padding * 2; // 增加边距
                        int height = metrics.getHeight() + padding * 2;
                        g.dispose();
                        // 创建实际水印图片
                        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        g = image.createGraphics();

                        // 设置抗锯齿
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        // 绘制文字
                        g.setFont(font);
                        g.setColor(watermarkStyle.getColor());
                        g.drawString(watermarkStyle.getText(), padding, metrics.getAscent());
                        g.dispose();
                        try {
                            if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
                                image = DocumentWatermarkUtils.rotateImage(image, -45);
                            }
                            Thumbnails.of(file.getInputstream())
                                    .scale(1d)
                                    .watermark(DocumentWatermarkUtils.convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign())
                                            , image, watermarkStyle.getOpacity() == null ? 1f : watermarkStyle.getOpacity().floatValue())
                                    .outputQuality(1)
                                    .toFile(tempFile);
                        } catch (Exception e) {
                            logger.error("图片加水印失败", e);
                        }
                    } else {
                        try {
                            MongoFileEntity pic = mongoFileService.getFile(watermarkStyle.getImageFileId());
                            if (pic == null) {
                                return ApiResult.success(true);
                            }
                            BufferedImage image = ImageIO.read(pic.getInputstream());
                            if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
                                image = DocumentWatermarkUtils.rotateImage(image, -45);
                            }
                            Thumbnails.of(file.getInputstream())
                                    .scale(1d)
                                    .watermark(DocumentWatermarkUtils.convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign())
                                            , image, watermarkStyle.getOpacity() == null ? 1f : watermarkStyle.getOpacity().floatValue())
                                    .outputQuality(1)
                                    .toFile(tempFile);
                        } catch (Exception e) {
                            logger.error("图片加水印失败", e);
                        }
                    }
                    mongoFileService.updateFileInputstream(fileID, file.getFileName(), file.getContentType(), new FileInputStream(tempFile));
                }
            } catch (Exception e) {
                logger.error("文件水印失败: ", e);
                return ApiResult.fail("文件制作水印失败: " + Throwables.getStackTraceAsString(e));

            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
        return ApiResult.success(true);

    }

    /**
     * 文件上传
     *
     * @param request
     * @param fileUpload
     * @return
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping(value = "/savefiles", method = RequestMethod.POST)
    public void saveFiles(MultipartHttpServletRequest multipartRequest, HttpServletResponse response,
                          @RequestParam(value = "signUploadFile", defaultValue = "false", required = false) boolean signUploadFile,
                          @RequestParam(value = "fun", required = false) String fun,
                          @ApiParam(value = "替换文件名称，分号隔开", required = true) @RequestParam(value = "fileNameStr", required = false) String fileNameStr,
                          @RequestParam(required = false, defaultValue = "false") boolean anonymous) throws IOException {

        ResultMessage resultMessage = new ResultMessage();
        // boolean signUploadFile = Config.SIGN_UPLOAD_FILE;
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();
        String source = multipartRequest.getParameter("source");
        String newVer = multipartRequest.getParameter("bsMode");
        String origUuid = multipartRequest.getParameter("origUuid");
        String localFileSourceIcon = multipartRequest.getParameter("localFileSourceIcon");
        if (StringUtils.isBlank(localFileSourceIcon)) {
            localFileSourceIcon = "";
        }

        try {
            Iterator<String> iterator = multipartRequest.getFileNames();
            fileNameStr = StringUtils.isBlank(fileNameStr) ? StringUtils.EMPTY : fileNameStr;
            String[] fileNames = StringUtils.split(fileNameStr, Separator.SEMICOLON.getValue());
            int fileNameIndex = 0;
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                MultipartFile multifile = multipartRequest.getFile(fieldName);
                String filename = multifile.getOriginalFilename();
                if (ArrayUtils.isNotEmpty(fileNames) && fileNames.length >= fileNameIndex + 1 && StringUtils.isNotBlank(fileNames[fileNameIndex])) {
                    filename = fileNames[fileNameIndex];
                }
                InputStream multifileIS = multifile.getInputStream();
                String contentType = multifile.getContentType();
                long size = multifile.getSize();
                if (size <= 0 && RepoUtils.isOffice(filename)) {
                    throw new RuntimeException("附件大小为0，不允许上传空附件");
                }
                FileUpload fileUpload = null;
                if (StringUtils.isNoneBlank(origUuid)) {
                    fileUpload = saveFileVersion(origUuid, filename, multifileIS, contentType, size, newVer, source);
                } else {
                    fileUpload = saveFileCore(signUploadFile, filename, multifileIS, contentType, size,
                            localFileSourceIcon, source);
                }
                uploadFiles.add(fileUpload);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(
                    this.getFun(fun, JsonBinder.buildNormalBinder().toJson(resultMessage).toString()));
            return;
        }
        // res.setContentType("text/html; charset=utf-8");
        // res.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
        resultMessage.setSuccess(true);
        resultMessage.setData(uploadFiles);
        if (anonymous && CollectionUtils.isNotEmpty(uploadFiles)) {
            /**
             * 文件允许匿名访问，则发放匿名地址加密token，浏览器可以通过文件地址后加上 guest-uri-token 进行匿名访问
             * 例如: http://域名/proxy-repository/repository/file/mongo/download?guest-uri-token=xxxxxx
             */
            response.setHeader("guest-uri-token"
                    , JwtTokenUtil.jwtBuilder().setSubject("/repository/file/mongo/download?fileID=" + uploadFiles.get(0).getFileID())
                            .compact());
        }
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(this.getFun(fun, JsonBinder.buildNormalBinder().toJson(resultMessage).toString()));
    }

    private String getFun(String fun, String result) {
        if (StringUtils.isNotBlank(fun)) {
            fun = HtmlUtils.htmlUnescape(fun);
            fun = fun.replace("{data}", result);
            return fun;
        }
        return result;
    }

    /**
     * 如何描述该方法
     *
     * @param origUuid
     * @param filename
     * @param multifileIS
     * @param contentType
     * @param size
     * @param bsMode
     * @param source
     * @return
     */
    private FileUpload saveFileVersion(String origUuid, String filename, InputStream multifileIS, String contentType,
                                       long size, String bsMode, String source) {
        FileUpload fileUpload = new FileUpload();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        MongoFileEntity file = mongoFileService.saveFile(origUuid, StringUtils.equals(bsMode, "true"), filename,
                multifileIS, source);
        IOUtils.closeQuietly(multifileIS);
        fileUpload.setFileID(file.getId());
        // 保存文件上传记录
        fileUpload.setFilename(filename);
        fileUpload.setFileSize(size);
        fileUpload.setContentType(contentType);
        fileUpload.setUserId(userDetails.getUserId());
        fileUpload.setUserName(userDetails.getUserName());
        fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
        fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
        fileUpload.setCreator(userDetails.getUserId());
        fileUpload.setCreateTime(new Date());
        fileUpload.setUploadTime(new Date());
        fileUpload.setOrigUuid(file.getLogicFileInfo().getOrigUuid());
        return fileUpload;
    }

    private FileUpload saveFileCore(boolean signUploadFile, String filename, InputStream multifileIS,
                                    String contentType, long size, String localFileSourceIcon, String source) throws Exception {
        FileUpload fileUpload = new FileUpload();
        if (signUploadFile) {
            File uploadDir = new File(Config.UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String tempUuid = UUID.randomUUID().toString();
            File tempFile = new File(uploadDir, tempUuid);
            FileOutputStream fos = new FileOutputStream(tempFile);

            IOUtils.copyLarge(multifileIS, fos);
            IOUtils.closeQuietly(fos);

            // 计算MD5消息摘要
            String digestValue = digestAsMD5(tempFile);
            fileUpload.setDigestAlgorithm("MD5");
            fileUpload.setDigestValue(digestValue);
            fileUpload.setSignatureValue("");
            fileUpload.setCertificate("");
            fileUpload.setSignUploadFile(true);

            FileInputStream fis = new FileInputStream(tempFile);

            MongoFileEntity file = this.mongoFileService.saveFile(UuidUtils.createUuid(), filename, fis,
                    fileUpload.getDigestValue(), fileUpload.getDigestAlgorithm(), fileUpload.getSignatureValue(),
                    fileUpload.getCertificate(), localFileSourceIcon, source);
            fileUpload.setFileID(file.getId());
            fileUpload.setOrigUuid(file.getLogicFileInfo().getOrigUuid());
            fileUpload.setContentType(file.getContentType());
            IOUtils.closeQuietly(fis);

            tempFile.delete();
        } else {
            MongoFileEntity file = this.mongoFileService.saveFile(UuidUtils.createUuid(), filename, multifileIS,
                    localFileSourceIcon, source);
            fileUpload.setFileID(file.getId());
            fileUpload.setContentType(file.getContentType());
        }

        IOUtils.closeQuietly(multifileIS);

        // 保存文件上传记录
        //UserDetails userDetails = SpringSecurityUtils.getCurrentUser();

        fileUpload.setUserId(SpringSecurityUtils.getCurrentUserId());
        fileUpload.setUserName(SpringSecurityUtils.getCurrentUserName());
        fileUpload.setDepartmentId(SpringSecurityUtils.getCurrentUserDepartmentId());
        fileUpload.setDepartmentName(SpringSecurityUtils.getCurrentUserDepartmentName());

        fileUpload.setFilename(filename);
        fileUpload.setContentType(contentType);
        fileUpload.setFileSize(size);
        fileUpload.setCreator(SpringSecurityUtils.getCurrentUserId());
        fileUpload.setCreateTime(new Date());
        fileUpload.setUploadTime(new Date());
        return fileUpload;
    }

    @RequestMapping(value = "/saveTemps", method = RequestMethod.POST)
    public void saveTemps(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
            throws IOException {
        // 保存到临时文件(浏览器无法获取本地路径，所以先上传)
        String fileID = multipartRequest.getParameter("fileID");
        ResultMessage resultMessage = new ResultMessage();
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();
        try {
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            Iterator<String> iterator = multipartRequest.getFileNames();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                MultipartFile multifile = multipartRequest.getFile(fieldName);
                FileUpload fileUpload = new FileUpload();
                String filename = multifile.getOriginalFilename();
                fileID = StringUtils.isBlank(fileID) ? UuidUtils.createUuid() : fileID;
                File tempFile = new File(FILE_UPLOAD_PATH + fileID + ".dat");
                multifile.transferTo(tempFile);
                fileUpload.setFileID(fileID);
                // 保存文件上传记录
                fileUpload.setFilename(filename);
                fileUpload.setFileSize(multifile.getSize());
                fileUpload.setContentType(multifile.getContentType());
                fileUpload.setUserId(userDetails.getUserId());
                fileUpload.setUserName(userDetails.getUserName());
                fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
                fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
                uploadFiles.add(fileUpload);
            }
            resultMessage.setSuccess(true);
            resultMessage.setData(uploadFiles);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            resultMessage.setSuccess(false);
            resultMessage.setData(ex.getMessage());
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
        }
        response.setContentType("text/html; charset=utf-8");
        response.getWriter().write(JsonUtils.object2Json(resultMessage));
    }

    @ResponseBody
    @RequestMapping("saveAsOfd")
    public ResultMessage saveAsOfd(@RequestParam("fileID") String fileID, HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File tempFile = null, tempOfdFile = null;
        String force = request.getParameter("force");
        ResultMessage resultMessage = new ResultMessage();
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        try {
            String ofdFileId = RepoUtils.getOfdFileId(fileID);
            MongoFileEntity ofdFileEntity, originFileEntity = mongoFileService.getFile(fileID);
            if (StringUtils.equals(force, "true")) {
                // 重新生成,强制转换
            } else if ((ofdFileEntity = mongoFileService.getFile(ofdFileId)) != null) {
                originFileEntity = ofdFileEntity;
            }
            String originFilename = originFileEntity.getLogicFileInfo().getFileName();
            if (false == RepoUtils.isOfdOrPdf(originFilename)) {
                // ofd 转换方式
                if (RepoUtils.isOfdConvertMethod()) {
                    inputStream = originFileEntity.getInputstream();
                    int lastIndex = originFilename.lastIndexOf(".");
                    String ofdFilename = (lastIndex > 0 ? originFilename.substring(0, lastIndex) : originFilename) + ".ofd";
                    String tempFileName = "[" + originFileEntity.getId() + "]" + originFilename;
                    // 写本地
                    tempFile = new File(FILE_UPLOAD_PATH + tempFileName);
                    IOUtils.copyLarge(inputStream, outputStream = new FileOutputStream(tempFile));
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                    // 转换输出
                    String tempOfdFileName = "[" + ofdFileId + "]" + ofdFilename;
                    tempOfdFile = new File(FILE_UPLOAD_PATH + tempOfdFileName);
                    outputStream = new FileOutputStream(tempOfdFile);
                    Map<String, String> metas = Maps.newHashMap();
                    fileConvertService.officeToOFD(tempFile, outputStream, metas);
                    IOUtils.closeQuietly(outputStream);
                    // 保存附件
                    inputStream = new FileInputStream(tempOfdFile);
                    ofdFileEntity = mongoFileService.saveFile(ofdFileId, ofdFilename, inputStream);
                    //  pdf转换方式
                } else {
                    inputStream = originFileEntity.getInputstream();
                    int lastIndex = originFilename.lastIndexOf(".");
                    String pdfFilename = (lastIndex > 0 ? originFilename.substring(0, lastIndex) : originFilename) + ".pdf";
                    String tempFileName = "[" + originFileEntity.getId() + "]" + originFilename;
                    // 写本地
                    tempFile = new File(FILE_UPLOAD_PATH + tempFileName);
                    IOUtils.copyLarge(inputStream, outputStream = new FileOutputStream(tempFile));
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                    // 转换输出
                    String tempPdfFileName = "[" + ofdFileId + "]" + pdfFilename;
                    spireFileConvertService.officeToPDF(tempFile, new FileOutputStream(new File(FILE_UPLOAD_PATH + tempPdfFileName)), null);
                    tempOfdFile = new File(FILE_UPLOAD_PATH + tempPdfFileName);
                    // 保存附件
                    inputStream = new FileInputStream(tempOfdFile);
                    ofdFileEntity = mongoFileService.saveFile(ofdFileId, pdfFilename, inputStream);
                }
            } else {
                ofdFileEntity = originFileEntity;
            }
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileID(ofdFileEntity.getId());
            fileUpload.setFilename(ofdFileEntity.getFileName());
            fileUpload.setFileSize(ofdFileEntity.getLength());
            fileUpload.setContentType(ofdFileEntity.getContentType());
            fileUpload.setUserId(userDetails.getUserId());
            fileUpload.setUserName(userDetails.getUserName());
            fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
            fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
            uploadFiles.add(fileUpload);
            resultMessage.setSuccess(true);
            resultMessage.setData(uploadFiles);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(ex.getMessage()));
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            FileUtils.deleteQuietly(tempFile);
            FileUtils.deleteQuietly(tempOfdFile);
        }
        return resultMessage;
    }

    /**
     * @param fileName
     * @param blockSize
     * @param blockIndex
     * @return
     * @Title: getTempFileName
     * @Description: 生成临时文件名称
     */
    private String getTempFileName(String fileName, String blockSize, String blockIndex) {
        StringBuilder name = new StringBuilder("");
        name.append(fileName).append("-block-").append(blockIndex).append("-of-").append(blockSize).append(".tmp");
        return name.toString();
    }

    public void printInfo(HttpServletResponse response, String info, String type) {
        Writer writer = null;
        try {
            if (type.equals("1")) {
                // 打印XML数据
                // 设置浏览器输出流对象，页面类型XML
                response.setContentType("text/xml;charset=UTF-8");
                // 以下两句为取消在本地的缓存
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                // ServletOutputStream out = response.getOutputStream();
                writer = response.getWriter();
                writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                writer.write(info);
                writer.close();
            } else if (type.equals("2")) {
                // 打印JSON数据
                // 设置浏览器输出流对象，页面类型XML
                response.setContentType("text/xml;charset=UTF-8");
                // 以下两句为取消在本地的缓存
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                // ServletOutputStream out = response.getOutputStream();
                writer = response.getWriter();
                writer.write(info);
                writer.close();
            } else if (type.equals("3")) {
                response.setContentType("text/html;charset=GBK");
                PrintWriter out = response.getWriter();
                out.println(info);
                out.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param is
     * @param filename
     * @param postSize
     * @return
     * @throws IOException
     * @throws Exception
     * @Title: printOut
     * @Description: 写临时文件
     */
    private boolean printOut(BufferedInputStream is, String filename, int postSize) throws IOException, Exception {
        FileOutputStream os = null;
        boolean res = false;
        FileLock lock = null;
        try {
            // System.out.println(FILE_UPLOAD_PATH + filename);

            File f1 = new File(FILE_UPLOAD_PATH + filename);
            if (f1.exists())
                f1.delete();
            f1.createNewFile();
            os = new FileOutputStream(f1, true);
            lock = os.getChannel().tryLock();
            byte buf[] = new byte[KB];
            int count = 0;
            int total = 0;
            // 计算上传超时时间，最少为10k/s
            int timeout = (postSize / 1024 / 10) * 1000 + 1;
            // 上传空文件失败加上改行
            timeout = timeout < 5000 ? 5000 : timeout;
            // 记录上传时间
            int timeCount = 0;
            // 开始接收文件
            while (total < postSize && timeCount < timeout) {
                count = is.read(buf);
                if (count > 0) {
                    os.write(buf, 0, count);
                    total += count;
                }
                Thread.sleep(5);
                timeCount += 10;
            }
            buf = null;
            // System.out.println("文件接收完毕，读取数据总长度为：" + total);
            if (total == postSize) {
                res = true;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != lock) {
                try {
                    lock.release();
                } catch (Exception ex) {
                    // ignore
                    logger.error(ex.getMessage(), ex);
                }
            }
            IOUtils.closeQuietly(os);
            // System.gc();
        }
        return res;

    }

    /**
     * @param filename
     * @param blockSize
     * @return
     * @throws IOException
     * @throws Exception
     * @Title: mergeBlock
     * @Description: 合并临时文件
     */
    private synchronized boolean mergeBlock(String filename, String blockSize) throws IOException, Exception {
        FileOutputStream os = null;
        FileInputStream in = null;
        String tempFileName = "";
        byte buf[] = new byte[1024 * 1024];
        int count = 0;
        boolean res = true;
        try {
            File tempFile = null;
            for (int i = 1; i <= Integer.parseInt(blockSize); i++) {
                tempFileName = getTempFileName(filename, blockSize, String.valueOf(i));
                tempFile = new File(FILE_UPLOAD_PATH + tempFileName);
                if (!tempFile.exists() || !tempFile.renameTo(tempFile)) {
                    res = false;
                    tempFile = null;
                    break;
                }
                tempFileName = null;
            }
            if (res) {
                File f = new File(FILE_UPLOAD_PATH + filename);
                if (f.exists())
                    f.delete();
                f.createNewFile();
                os = new FileOutputStream(f, true);
                for (int i = 1; i <= Integer.parseInt(blockSize); i++) {
                    tempFileName = getTempFileName(filename, blockSize, String.valueOf(i));
                    tempFile = new File(FILE_UPLOAD_PATH + tempFileName);
                    in = new FileInputStream(tempFile);
                    count = in.read(buf);
                    while (count > 0) {
                        os.write(buf, 0, count);
                        count = in.read(buf);
                    }
                    in.close();
                    in = null;
                    tempFile.delete();
                    tempFile = null;
                    tempFileName = null;
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            res = false;
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(in);
            // System.gc();
        }
        return res;
    }

    /**
     * 文件上传
     *
     * @param request
     * @param fileUpload
     * @return
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping(value = "/saveOcxFiles", method = RequestMethod.POST)
    public void saveOcxFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream DATA = request.getInputStream();
        BufferedInputStream is = new BufferedInputStream(DATA);
        try {
            /*
             * System.out.println("文件上传开始"); //遍历请求的所有头部信息
             * System.out.println("--------遍历请求的所有头部信息-----------"); Enumeration
             * names = request.getHeaderNames(); while(names.hasMoreElements())
             * { String name = names.nextElement().toString(); String headers =
             * request.getHeader(name); System.out.println(name);
             * System.out.println(headers); }
             * System.out.println("-----------------------------");
             * //System.out.println("服务器目录:" + FILE_UPLOAD_PATH );
             */

            String fileName = request.getHeader("fileName");
            if (fileName == null || fileName.equals(""))
                return;
            // System.out.println("上传文件名解码前：" + fileName);

            String tempFileName = URLDecoder.decode(fileName, "UTF-8");
            // System.out.println("上传文件名解码后1：" + fileName);
            // System.out.println(tempFileName);

            // tempFileName = new String(tempFileName.getBytes(), "GBK");
            // System.out.println("上传文件名解码后2：" + tempFileName);

            String locFileName = tempFileName;
            String blockSize = request.getHeader("blockSize");
            String md5String = request.getHeader("MD5");
            String blockIndex = request.getHeader("blockIndex");
            if (Integer.parseInt(blockSize) > 1) {
                tempFileName = getTempFileName(tempFileName, blockSize, blockIndex);
            }
            // 开始读输入流，写上传文件
            // printOut(is, tempFileName, request.getContentLength());
            MongoFileEntity fileEntity = mongoFileService.saveFile(fileName, DATA);
            boolean isFinish = false;
            if (Integer.parseInt(blockSize) > 1)
                if (mergeBlock(locFileName, blockSize))
                    isFinish = true;
                else
                    response.getWriter().println("tempFile ");
            else
                isFinish = true;

            // 上传结束
            if (isFinish) {
                // MD5验证文件是否完整。
                if (checkFile(locFileName, md5String)) {
                    // success
                    // System.out.println("返回文件上传成功");
                    response.getWriter().println("{success:1, fileID:\"" + fileEntity.getFileID() + "\"}");
                } else {
                    // fail
                    // System.out.println("返回文件上传失败");
                    response.getWriter().println("success:0");
                }
            }
            // System.out.println("文件上传结束");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (is != null)
                is.close();
            if (DATA != null)
                DATA.close();
        }
    }

    /**
     * @param fileName
     * @param clientMD5String
     * @return
     * @Title: checkFile
     * @Description: MD5验证文件
     */
    private boolean checkFile(String fileName, String clientMD5String) throws Exception {
        boolean res = false;
        res = true;
        /*
         * clientMD5String = (null == clientMD5String)?"":clientMD5String;
         *
         * if (clientMD5String.equalsIgnoreCase(CommonUtils.getInstance().
         * getFileMD5String(FILE_UPLOAD_PATH+fileName)))
         */
        return res;
    }

    /**
     * 文件上传
     *
     * @param request
     * @param fileUpload
     * @return
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping(value = "/saveTextBody", method = RequestMethod.POST)
    public void saveTextBody(MultipartHttpServletRequest multipartRequest, HttpServletResponse response,
                             @RequestParam(value = "signUploadFile", defaultValue = "false", required = false) boolean signUploadFile)
            throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        // boolean signUploadFile = Config.SIGN_UPLOAD_FILE;
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();

        try {
            Iterator<String> iterator = multipartRequest.getFileNames();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                MultipartFile multifile = multipartRequest.getFile(fieldName);
                String filename = multifile.getOriginalFilename();
                String fileID = filename.split("\\.")[0];
                InputStream multifileIS = multifile.getInputStream();
                String contentType = multifile.getContentType();
                long size = multifile.getSize();
                FileUpload fileUpload = new FileUpload();
                if (signUploadFile) {
                    File uploadDir = new File(Config.UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    String tempUuid = UUID.randomUUID().toString();
                    File tempFile = new File(uploadDir, tempUuid);
                    FileOutputStream fos = new FileOutputStream(tempFile);

                    IOUtils.copyLarge(multifileIS, fos);
                    IOUtils.closeQuietly(fos);

                    // 计算MD5消息摘要
                    String digestValue = digestAsMD5(tempFile);
                    fileUpload.setDigestAlgorithm("MD5");
                    fileUpload.setDigestValue(digestValue);
                    fileUpload.setSignatureValue("");
                    fileUpload.setCertificate("");
                    fileUpload.setSignUploadFile(true);

                    FileInputStream fis = new FileInputStream(tempFile);

                    MongoFileEntity file = this.mongoFileService.saveFile(fileID, filename, fis,
                            fileUpload.getDigestValue(), fileUpload.getDigestAlgorithm(),
                            fileUpload.getSignatureValue(), fileUpload.getCertificate());
                    fileUpload.setFileID(file.getId());
                    IOUtils.closeQuietly(fis);
                    tempFile.delete();
                } else {
                    MongoFileEntity file = this.mongoFileService
                            .saveFile(UuidUtils.createUuid(), filename, multifileIS);
                    fileUpload.setFileID(file.getId());

                }

                IOUtils.closeQuietly(multifileIS);

                // 保存文件上传记录
                UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                fileUpload.setUserId(userDetails.getUserId());
                fileUpload.setUserName(userDetails.getUserName());
                fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
                fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
                fileUpload.setFilename(filename);
                fileUpload.setContentType(contentType);
                fileUpload.setFileSize(size);

                uploadFiles.add(fileUpload);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
            return;
        }
        // res.setContentType("text/html; charset=utf-8");
        // res.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
        resultMessage.setSuccess(true);
        resultMessage.setData(uploadFiles);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
    }

    /**
     * 更新签名
     *
     * @param response
     * @param folderID
     * @param jsonData 字段也fileID的对应关系
     * @throws IOException
     */
    @RequestMapping(value = "/updateSignature")
    public void updateSignature(HttpServletResponse response, @RequestParam(value = "fileID") String fileID,
                                @RequestParam(value = "signatureData") String signatureData) throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        SignatureBean signature = JsonBinder.buildNormalBinder().fromJson(signatureData, SignatureBean.class);
        try {
            this.mongoFileService.updateSignature(fileID, signature.getDigestValue(), signature.getDigestAlgorithm(),
                    signature.getSignatureValue(), signature.getCertificate());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
            return;
        }
        // res.setContentType("text/html; charset=utf-8");
        // res.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
        resultMessage.setSuccess(true);

        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
    }

    /**
     * 文件推入文件夹
     *
     * @param response
     * @param folderID
     * @param jsonData 字段也fileID的对应关系
     * @throws IOException
     */
    @RequestMapping(value = "/pushFilesToFolder")
    public void pushFilesToFolder(HttpServletResponse response, @RequestParam(value = "folderID") String folderID,
                                  String jsonData) throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, String[]> fieldFileIDs = JsonBinder.buildNormalBinder().fromJson(jsonData, Map.class);
        try {
            Iterator<Entry<String, String[]>> it = fieldFileIDs.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String[]> entry = it.next();
                String field = entry.getKey();
                String[] fileIDs = entry.getValue();
                List<String> fileIDList = Arrays.asList(fileIDs);
                this.mongoFileService.pushFilesToFolder(folderID, fileIDList, field);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
            return;
        }
        // res.setContentType("text/html; charset=utf-8");
        // res.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
        resultMessage.setSuccess(true);

        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
    }

    /**
     * 上传文件并推送入文件夹
     *
     * @param request
     * @param fileUpload
     * @return
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping(value = "/saveFilesAndPushToFolder", method = RequestMethod.POST)
    public void saveFilesAndPushToFolder(MultipartHttpServletRequest multipartRequest, HttpServletResponse response,
                                         @RequestParam("folderID") String folderID,
                                         @RequestParam(value = "purpose", required = false) String purpose,
                                         @RequestParam(value = "popFolderFile", required = false) Boolean popFolderFile,
                                         @RequestParam(value = "signUploadFile", defaultValue = "false", required = false) boolean signUploadFile)
            throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        // boolean signUploadFile = Config.SIGN_UPLOAD_FILE;
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();
        if ("".equals(folderID) || "undefined".equals(folderID)) {
            folderID = UUID.randomUUID().toString();
        }
        if (BooleanUtils.isTrue(popFolderFile)) {
            mongoFileService.popAllFilesFromFolder(folderID, purpose);
        }
        try {
            Iterator<String> iterator = multipartRequest.getFileNames();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                MultipartFile multifile = multipartRequest.getFile(fieldName);
                String filename = multifile.getOriginalFilename();
                InputStream multifileIS = multifile.getInputStream();
                String contentType = multifile.getContentType();
                long size = multifile.getSize();
                FileUpload fileUpload = new FileUpload();
                if (signUploadFile) {
                    File uploadDir = new File(Config.UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    String tempUuid = UUID.randomUUID().toString();
                    File tempFile = new File(uploadDir, tempUuid);
                    FileOutputStream fos = new FileOutputStream(tempFile);

                    IOUtils.copyLarge(multifileIS, fos);
                    IOUtils.closeQuietly(fos);

                    // 计算MD5消息摘要
                    String digestValue = digestAsMD5(tempFile);
                    fileUpload.setDigestAlgorithm("MD5");
                    fileUpload.setDigestValue(digestValue);
                    fileUpload.setSignUploadFile(true);

                    FileInputStream fis = new FileInputStream(tempFile);

                    MongoFileEntity file = this.mongoFileService.saveFile(UuidUtils.createUuid(), filename, fis,
                            fileUpload.getDigestValue(), fileUpload.getDigestAlgorithm(),
                            fileUpload.getSignatureValue(), fileUpload.getCertificate());
                    this.mongoFileService.pushFileToFolder(folderID, file.getId(), purpose);
                    fileUpload.setFileID(file.getId());
                    IOUtils.closeQuietly(fis);
                    tempFile.delete();
                } else {

                    MongoFileEntity file = this.mongoFileService
                            .saveFile(UuidUtils.createUuid(), filename, multifileIS);
                    this.mongoFileService.pushFileToFolder(folderID, file.getId(), purpose);
                    fileUpload.setFileID(file.getId());

                }

                IOUtils.closeQuietly(multifileIS);

                // 保存文件上传记录
                UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                fileUpload.setUserId(userDetails.getUserId());
                fileUpload.setUserName(userDetails.getUserName());
                fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
                fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
                fileUpload.setFolderID(folderID);
                fileUpload.setFilename(filename);
                fileUpload.setContentType(contentType);
                fileUpload.setFileSize(size);

                uploadFiles.add(fileUpload);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
            return;
        }
        // res.setContentType("text/html; charset=utf-8");
        // res.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
        resultMessage.setSuccess(true);
        resultMessage.setData(uploadFiles);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
    }

    @RequestMapping("download")
    public void download(@RequestParam(value = "fileID", required = false) String fileID,
                         @RequestParam(value = "preview", required = false) String preview, HttpServletResponse res,
                         @RequestParam(value = "folderUuid", required = false) String folderUuid,
                         @RequestParam(value = "purpose", required = false) String purpose,
                         @RequestParam(value = "watermark", required = false) String watermark,
                         HttpServletRequest request) throws IOException {
        /*
         * if(fileID.startsWith(DytableConfig.BODY_FILEID_PREFIX)){ fileID =
         * fileID.substring(0, fileID.lastIndexOf(".")); }
         */
        MongoFileEntity file = null;


        if (StringUtils.isBlank(fileID) && StringUtils.isNotBlank(folderUuid)) {
            List<MongoFileEntity> fileList = mongoFileService.getFilesFromFolder(folderUuid, purpose);
            if (CollectionUtils.isNotEmpty(fileList)) {
                file = fileList.get(0);
            }
        } else if (StringUtils.isNotBlank(fileID)) {
            file = mongoFileService.getFile(fileID);

        }

        if (file == null) {
            return;
        }
        String oFileName = file.getLogicFileInfo().getFileName();
        if (preview != null && "true".equals(preview) && file.getLength() > 400000) {// 预览
            // long fileSize = file.getLength();
            String previewPath = Config.APP_DATA_DIR + "/" + "preview";
            File dir = new File(previewPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = file.getId() + oFileName;
            File preFile = new File(previewPath + "/" + fileName);
            InputStream is = null;
            if (!preFile.exists()) {
                try {
                    is = file.getInputstream();
                    ImageResizeTool.createFixedBoundImg(is, preFile, 0.5f);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }

            is = new FileInputStream(preFile);
            FileDownloadUtils.download(request, res, is, oFileName);
            is.close();
        } else {
            InputStream is = file.getInputstream();
            if (StringUtils.isNotBlank(watermark)) {
                File tempFile = null;
                try {
                    WatermarkStyle watermarkStyle = JsonUtils.gson2Object(watermark, WatermarkStyle.class);
                    if ((WatermarkStyle.WatermarkType.text.equals(watermarkStyle.getType()) && StringUtils.isNotBlank(watermarkStyle.getText())) || (
                            WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType()) && StringUtils.isNotBlank(watermarkStyle.getImageFileId())
                    )) {
                        String tempFileName = file.getFileName().substring(0, file.getFileName().lastIndexOf(".")) + "_(水印)_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + file.getFileName().substring(file.getFileName().lastIndexOf("."));
                        tempFile = new File(new File(System.getProperty("java.io.tmpdir")), tempFileName);
                        tempFile.createNewFile();
                        logger.info("创建临时水印文件: {}", tempFile.getAbsolutePath());
                        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                        boolean watermarked = false;
                        if (file.getFileName().endsWith(".doc") || file.getFileName().endsWith(".docx") || file.getFileName().endsWith(".wps")) {
                            DocumentWatermarkUtils.addDocWatermark(file.getInputstream(), fileOutputStream, watermarkStyle);
                            watermarked = true;
                        } else if (file.getFileName().endsWith(".ppt") || file.getFileName().endsWith(".pptx")) {
                            DocumentWatermarkUtils.addPptWatermark(file.getInputstream(), fileOutputStream, watermarkStyle);
                            watermarked = true;
                        } else if (file.getFileName().endsWith(".xls") || file.getFileName().endsWith(".xlsx")) {
                            DocumentWatermarkUtils.addExcelWatermark(file.getInputstream(), fileOutputStream, watermarkStyle);
                            watermarked = true;
                        } else if (file.getFileName().endsWith(".pdf")) {
                            DocumentWatermarkUtils.addPdfBoxWatermark(file.getInputstream(), fileOutputStream, watermarkStyle);
                            watermarked = true;
                        } else if (file.getFileName().endsWith(".png") || file.getFileName().endsWith(".jpg") || file.getFileName().endsWith(".jpeg") || "image/jpeg".equalsIgnoreCase(file.getContentType())) { // 创建临时Graphics获取文本尺寸
                            if (WatermarkStyle.WatermarkType.text.equals(watermarkStyle.getType())) {
                                BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g = temp.createGraphics();
                                Font font = DocumentWatermarkUtils.jFont.deriveFont(watermarkStyle.getFontSize() == null ? 24f : (float) watermarkStyle.getFontSize());// new Font(watermarkStyle.getFontFamily(), Font.PLAIN, watermarkStyle.getFontSize());
                                g.setFont(font);
                                FontMetrics metrics = g.getFontMetrics();
                                int padding = 0;
                                int width = metrics.stringWidth(watermarkStyle.getText()) + padding * 2; // 增加边距
                                int height = metrics.getHeight() + padding * 2;
                                g.dispose();
                                // 创建实际水印图片
                                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                                g = image.createGraphics();

                                // 设置抗锯齿
                                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                                // 绘制文字
                                g.setFont(font);
                                g.setColor(watermarkStyle.getColor());
                                g.drawString(watermarkStyle.getText(), padding, metrics.getAscent());
                                g.dispose();
                                try {
                                    if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
                                        image = DocumentWatermarkUtils.rotateImage(image, -45);
                                    }
                                    Thumbnails.of(file.getInputstream())
                                            .scale(1d)
                                            .watermark(DocumentWatermarkUtils.convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign())
                                                    , image, watermarkStyle.getOpacity() == null ? 1f : watermarkStyle.getOpacity().floatValue())
                                            .outputQuality(1)
                                            .toFile(tempFile);
                                } catch (Exception e) {
                                    logger.error("图片加水印失败", e);
                                }
                            } else {
                                try {
                                    MongoFileEntity pic = mongoFileService.getFile(watermarkStyle.getImageFileId());
                                    if (pic != null) {
                                        BufferedImage image = ImageIO.read(pic.getInputstream());
                                        if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
                                            image = DocumentWatermarkUtils.rotateImage(image, -45);
                                        }
                                        Thumbnails.of(file.getInputstream())
                                                .scale(1d)
                                                .watermark(DocumentWatermarkUtils.convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign())
                                                        , image, watermarkStyle.getOpacity() == null ? 1f : watermarkStyle.getOpacity().floatValue())
                                                .outputQuality(1)
                                                .toFile(tempFile);
                                    }

                                } catch (Exception e) {
                                    logger.error("图片加水印失败", e);
                                }
                            }
                            watermarked = true;
                        }
                        if (watermarked) {
                            IOUtils.closeQuietly(fileOutputStream);
                            FileDownloadUtils.download(request, res, new FileInputStream(tempFile), oFileName);
                            is.close();
                            return;
                        }
                    }

                } catch (Exception e) {
                    logger.error("下载水印文件失败: ", e);
                } finally {
                    FileUtils.deleteQuietly(tempFile);
                }

            }

            FileDownloadUtils.download(request, res, is, oFileName);
            is.close();
        }

    }


    @GetMapping("/downloadVideoSegment")
    public void downloadVideoSegment(HttpServletResponse response, HttpServletRequest request, @RequestParam("fileID") String fileID) throws Exception {
        InputStream inputStream = null;
        OutputStream outputStream = response.getOutputStream();
        try {
            long skip = -1;
            long length = -1;
            MongoFileEntity fileEntity = mongoFileService.getFile(fileID);
            response.setHeader("Content-Type", fileEntity.getContentType());
            long fileLength = fileEntity.getLength();
            long end = fileLength - 1;
            String range = request.getHeader("Range");
            if (range != null && range.length() > 0) {
                int idx = range.indexOf("-");
                skip = Long.parseLong(range.substring(6, idx));
                if ((idx + 1) < range.length()) {
                    end = Long.parseLong(range.substring(idx + 1));
                }
                length = end - skip + 1;
            }

            if (range == null || range.length() <= 0) {//bytes=32523-32523
                response.setHeader("Content-Length", "" + fileLength);
                response.setStatus(200);
            } else {
                response.setHeader("Content-Length", "" + length);
                response.setHeader("Content-Range", "bytes " + skip + "-" + end + "/" + fileLength);
                response.setStatus(206);
            }
            System.out.println("bytes " + skip + "-" + end + "/" + fileLength);
            inputStream = fileEntity.getInputstream();
            if (skip > 0) {
                inputStream.skip(skip);
            }
            byte[] bs = new byte[1024];
            int len;
            while ((len = inputStream.read(bs)) != -1) {
                if (length > 0) {
                    if (length > len) {
                        outputStream.write(bs, 0, len);
                        outputStream.flush();
                        length -= len;
                    } else {
                        outputStream.write(bs, 0, (int) length);
                        outputStream.flush();
                        break;
                    }
                } else {
                    outputStream.write(bs, 0, len);
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    @RequestMapping("download4ocx")
    public void download4ocx(@RequestParam("fileID") String fileID, HttpServletResponse response,
                             HttpServletRequest request) throws IOException {

        // String url = null;
        String userName = SpringSecurityUtils.getCurrentUserName();
        if (StringUtils.isBlank(fileID)) {
            logger.error("[" + userName + "]MongoFileController.download4ocx文件下载:参数fileId为空");
            response.getWriter().println("success:0");
            return;
        }
        MongoFileEntity file = mongoFileService.getFile(fileID);
        String fileName = file.getFileName();
        InputStream is = file.getInputstream();// 负责读取数据
        long fileLength = file.getLength();// 记录文件大小

        // System.out.println("文件下载开始");
        /*
         * url = request.getParameter("url"); url = new
         * String(url.getBytes("ISO-8859-1"), "GBK"); //System.out.println(url);
         * //若没有附件的下载url地址则退出 if(url ==null | url.equals("")) return;
         *
         * if (url != null) { if (url.indexOf("/") == 0) { url =
         * url.substring(1, url.length()); }
         *
         * int index; //获取文件名 index = StringUtils.lastIndexOf(url, "/");
         * fileName = url.substring(index + 1, url.length());
         *
         * //获取文档UNID url = url.substring(0, index); index =
         * StringUtils.lastIndexOf(url.toLowerCase(), "/$file"); String docId =
         * url.substring(index - 32, index);
         *
         * //获取数据库全路径 url = url.substring(0, index - 32); index =
         * StringUtils.lastIndexOf(url.toLowerCase(), ".nsf"); String dbPath =
         * url.substring(0, index + 4);
         *
         * Session session = null; Database db = null; Document doc = null;
         *
         * NotesThread.sinitThread(); try { session =
         * NotesFactory.createSession(); if (session != null) { db =
         * session.getDatabase("", dbPath); if (db != null) { doc =
         * db.getDocumentByUNID(docId); if (doc != null) { EmbeddedObject eo =
         * doc.getAttachment(fileName); if (eo != null) {
         * //System.out.println("获取附件流"); is = eo.getInputStream();
         * //System.out.println("计算文件的MD5"); response.setHeader("MD5-String",
         * CommonUtils.getInstance().getFileMD5String( eo.getInputStream()));
         * //System.out.println("开始计算长度"); fileLength = eo.getFileSize();
         * //System.out.println(fileLength); } } } } } catch (NotesException e)
         * { e.printStackTrace(); } catch (Exception ex) { ex.printStackTrace();
         * } finally { try{ if(doc!=null)doc.recycle();
         * if(db!=null)db.recycle(); if (session!=null)session.recycle(); }catch
         * (NotesException e) { e.printStackTrace(); } }
         * NotesThread.stermThread(); }
         */
        String getMD5String = request.getHeader("method");
        getMD5String = getMD5String == null ? "" : getMD5String;
        try {
            if (getMD5String.equalsIgnoreCase("getMD5String")) {
                response.getWriter().write("fileMD5String:" + CommonUtils.getInstance().getFileMD5String(is));
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }

        downloadFile4Ocx(response, request, fileName, is, fileLength);

        is.close();
    }

    @RequestMapping("download4bodytemplate")
    public void download4BodyTemplate(HttpServletResponse response, HttpServletRequest request) {
        String folderUuid = "wellpt-common-fileupload-icon";
        String purpose = "body-template";
        List<MongoFileEntity> files = this.mongoFileService.getFilesFromFolder(folderUuid, purpose);
        String templateFilePath = "/resources/pt/js/fileupload/ocx/$WTemplate.doc";

        String root = request.getServletContext().getRealPath("/");
        templateFilePath = root + templateFilePath;
        FileInputStream fis = null;
        try {

            if (CollectionUtils.isEmpty(files)) {
                File file = new File(templateFilePath);
                fis = new FileInputStream(file);

                MongoFileEntity fe = this.mongoFileService.saveFile(file.getName(), fis);
                this.mongoFileService.pushFileToFolder(folderUuid, fe.getFileID(), purpose);
                download4ocx(fe.getFileID(), response, request);

            } else {
                MongoFileEntity file = files.get(0);
                download4ocx(file.getFileID(), response, request);
            }
        } catch (FileNotFoundException e) {
            logger.error("找不到正文模板文件{}\n" + e.getMessage(), templateFilePath);
        } catch (IOException e) {
            logger.error("正文模板下载失败{}\n" + e.getMessage(), templateFilePath);
        } finally {
            if (fis != null) {
                IOUtils.closeQuietly(fis);
            }
        }

    }

    private void downloadFile4Ocx(HttpServletResponse response, HttpServletRequest request, String fileName,
                                  InputStream is, long fileLength) {
        logger.info("------>开始下载附件: " + fileName);
        logger.info("------>用户是[" + SpringSecurityUtils.getCurrentUserName() + "]");

        long pastLength = 0;// 记录已下载文件大小
        int rangeSwitch = 0;// 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
        long toLength = 0;// 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
        long contentLength = 0;// 客户端当前请求的字节总量

        String rangeBytes = "";// 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
        OutputStream os = null;// 写出数据
        OutputStream out = null;// 缓冲
        byte b[] = new byte[1024];// 暂存容器
        /*
         * try { response.setHeader("MD5-String",CommonUtils.getInstance().
         * getFileMD5String(is)); } catch (Exception e1) {
         * logger.error(e1.getMessage(), e1); }
         */
        if (request.getHeader("Range") != null) {// 客户端请求的下载的文件块的开始字节
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
            rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
            if (rangeBytes.indexOf('-') == rangeBytes.length() - 1) {// bytes=969998336-
                rangeSwitch = 1;
                rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                pastLength = Long.parseLong(rangeBytes.trim());
                contentLength = fileLength - pastLength + 1;// 客户端请求的是 969998336
                // 之后的字节
            } else {// bytes=1275856879-1275877358
                rangeSwitch = 2;
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                pastLength = Long.parseLong(temp0.trim());// bytes=1275856879-1275877358，从第
                // 1275856879
                // 个字节开始下载
                toLength = Long.parseLong(temp2);// bytes=1275856879-1275877358，到第
                // 1275877358 个字节结束
                contentLength = toLength - pastLength + 1;// 客户端请求的是
                // 1275856879-1275877358
                // 之间的字节
            }
        } else {// 从开始进行下载
            contentLength = fileLength;// 客户端要求全文下载
        }

        /**
         * 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。 响应的格式是:
         * Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
         * ServletActionContext.getResponse().setHeader("Content-Length", new
         * Long(file.length() - p).toString());
         */
        // response.reset();// 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
        response.setHeader("Accept-Ranges", "bytes");// 如果是第一次下,还没有断点续传,状态是默认的
        // 200,无需显式设置;响应的格式是:HTTP/1.1
        // 200 OK
        if (pastLength != 0) {
            // 不是从最开始下载,
            // 响应的格式是:
            // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
            logger.info("----------------------------不是从开始进行下载！服务器即将开始断点续传...");
            switch (rangeSwitch) {
                case 1: {// 针对 bytes=27000- 的请求
                    String contentRange = new StringBuilder("bytes ").append(new Long(pastLength).toString()).append("-")
                            .append(new Long(fileLength - 1).toString()).append

                                    ("/").append(new Long(fileLength).toString()).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                case 2: {// 针对 bytes=27000-39000 的请求
                    String contentRange = rangeBytes + "/" + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            // 是从开始下载
            logger.info("----------------------------是从开始进行下载！");
        }

        // System.out.println("开始下载");

        try {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentType(CommonUtils.getContentType(fileName));// set
            // the
            // MIME
            // type.

            // response.setHeader("Content-Length",
            // String.valueOf(contentLength));
            response.setHeader("File-Length", String.valueOf(fileLength));
            os = response.getOutputStream();
            out = new BufferedOutputStream(os);

            // System.out.println(rangeSwitch);
            try {
                switch (rangeSwitch) {
                    case 0:
                    case 1: {// 针对 bytes=27000- 的请求
                        if (is.skip(pastLength) < 0) {
                            logger.info("no data skiped");
                            // 形如 bytes=969998336- 的客户端请求，跳过969998336 个字节
                        }
                        int n = 0;
                        while ((n = is.read(b, 0, 1024)) != -1) {
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    case 2: {// 针对 bytes=27000-39000 的请求
                        if (is.skip(pastLength - 1) < 0) {
                            logger.info("no data skiped");
                            // 形如 bytes=1275856879-1275877358 的客户端请求，找到第 1275856879
                            // 个字节
                        }
                        int n = 0;
                        long readLength = 0;// 记录已读字节数
                        while (readLength <= contentLength - 1024) {// 大部分字节在这里读取
                            n = is.read(b, 0, 1024);
                            readLength += 1024;
                            out.write(b, 0, n);
                        }
                        if (readLength <= contentLength) {// 余下的不足 1024 个字节在这里读取
                            n = is.read(b, 0, (int) (contentLength - readLength));
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                out.flush();

                // System.out.println("下载结束");
            } catch (IOException ie) {
                logger.error(ie.getMessage(), ie);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        logger.info("------>下载附件: [" + fileName + "]完成 ");
    }

    @RequestMapping("downAllFiles4ocx")
    @ResponseBody
    public void downAllFiles4ocx(@RequestParam("fileIDs") String fileIDs, HttpServletResponse res,
                                 HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {

        String getMD5String = request.getHeader("method");
        getMD5String = getMD5String == null ? "" : getMD5String;

        File file = zipFiles(fileIDs, getMD5String);
        InputStream is = new FileInputStream(file);
        String fileName = file.getName();
        long fileLength = file.length();// 记录文件大小
        // try {
        // logger.error(CommonUtils.getInstance().getFileMD5String(is));
        // is.close();
        // is = null;
        // } catch (Exception e) {
        // logger.error(e.getMessage(), e);
        // return;
        // }
        // is = new FileInputStream(file);

        try {
            if (getMD5String.equalsIgnoreCase("getMD5String")) {
                response.getWriter().write("fileMD5String:" + CommonUtils.getInstance().getFileMD5String(is));
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }

        downloadFile4Ocx(response, request, fileName, is, fileLength);

        try {
            is.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        // FileDownloadUtils.download4Ocx(request, res, is, fileName,
        // file.length());
        //
        // IOUtils.closeQuietly(is);

        /*
         * List<MongoFileEntity> mongoFiles =
         * mongoFileService.getFilesFromFolder( folderID, purpose);
         * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         * if (mongoFiles != null) { for (MongoFileEntity mfe : mongoFiles) {
         * String edittime = sdf.format(mfe.getUploadDate());
         * files.add(mfe.getFileName() + ";" + mfe.getId() + ";" + edittime); }
         * }
         */
        // Config.APP_DATA_DIR

    }

    /**
     * @param fileIDs:file's id string in mongodb
     * @return File object
     * @function: zip files and return File object
     */
    private File zipFiles(String fileIDs, String getMD5String) {
        JSONArray filesArray = null;
        try {
            filesArray = new JSONArray(fileIDs);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        // String folderName = DyFormFacade.createUuid();
        String folderName = Md5PasswordEncoderUtils.encodePassword(fileIDs, "");

        String childFolderName = folderName;
        // modify by wujx 20160822 begin
        // 取第一个文件名称作为压缩文件名称，为避免重复冲突，把该文件夹先放到文件夹名为UUID中
        if (filesArray.length() > 0) {
            try {
                JSONObject obj = filesArray.getJSONObject(0);
                String fileId = obj.getString("fileID");
                MongoFileEntity mongoFileEntity = this.mongoFileService.getFile(fileId);
                childFolderName = mongoFileEntity.getFileName();
                childFolderName = childFolderName.substring(0, childFolderName.lastIndexOf("."));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String zipDir = appDataDir + folderName + pathSeparator + childFolderName;
        String zipFileName = zipDir + ".zip";
        // modify by wujx 20160822 end
        File file = new File(zipFileName);
        // 文件不存在，或者第一次请求（MD5请求）时，重新打包。避免同一次下载，dll多次请求，多次打包会出现MD5不一致情况
        if (!file.exists() || getMD5String.equalsIgnoreCase("getMD5String")) {
            File dir = new File(zipDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            for (int i = 0; i < filesArray.length(); i++) {
                try {
                    JSONObject obj = filesArray.getJSONObject(i);
                    String fileId = obj.getString("fileID");
                    writeToDir(dir, this.mongoFileService.getFile(fileId));
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            try {
                ZipUtils.zipFolder(zipDir, zipFileName);
                FileUtils.deleteDirectory(dir);

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return file;
    }

    /**
     * @param fileIDs:file's id string in mongodb
     * @return File object
     * @function: zip files and return File object
     */
    private File zipFilesRepackage(String fileIDs) {
        JSONArray filesArray = null;
        try {
            filesArray = new JSONArray(fileIDs);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        // String folderName = DyFormFacade.createUuid();
        String folderName = Md5PasswordEncoderUtils.encodePassword(fileIDs, "");
        String zipDir = appDataDir + folderName;
        File dir = new File(zipDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (int i = 0; i < filesArray.length(); i++) {
            try {
                JSONObject obj = filesArray.getJSONObject(i);
                String fileId = obj.getString("fileID");
                writeToDir(dir, this.mongoFileService.getFile(fileId));
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        String zipFileName = appDataDir + folderName + ".zip";
        try {
            ZipUtils.zipFolder(zipDir, zipFileName);
            FileUtils.deleteDirectory(dir);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        File file = new File(zipFileName);
        return file;
    }

    @RequestMapping("/downloadBody/{fileID}")
    public void downloadBody(@PathVariable("fileID") String fileID, HttpServletResponse res, HttpServletRequest request) {
        /*
         * if(fileID.startsWith(DytableConfig.BODY_FILEID_PREFIX)){ fileID =
         * fileID.substring(0, fileID.lastIndexOf(".")); }
         */
        MongoFileEntity file = mongoFileService.getFile(fileID);
        String oFileName = file.getLogicFileInfo().getFileName();
        FileDownloadUtils.download(request, res, file.getInputstream(), oFileName);

    }

    @RequestMapping("downloadSWF")
    public void downloadSWF(@RequestParam("fileID") String fileID, HttpServletResponse res, HttpServletRequest request) {
        MongoFileEntity file = null;
        try {
            file = mongoFileService.getReplicaOfSWF(fileID);
            String oFileName = file.getLogicFileInfo().getFileName();
            FileDownloadUtils.download(request, res, file.getInputstream(), oFileName);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping("downloadall")
    public void downLoadAll(@RequestParam("folderID") String folderID, @RequestParam("purpose") String purpose)
            throws FileNotFoundException {

        mongoFileService.getFilesFromFolder(folderID, purpose);

    }

    @RequestMapping(value = "downLoadFiles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> downLoadFiles(@RequestParam("folderID") String folderID, @RequestParam("purpose") String purpose)
            throws FileNotFoundException {

        List<String> files = new ArrayList<String>();

        List<MongoFileEntity> mongoFiles = mongoFileService.getFilesFromFolder(folderID, purpose);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (mongoFiles != null) {
            for (MongoFileEntity mfe : mongoFiles) {
                String oFileName = mfe.getLogicFileInfo().getFileName();
                String edittime = sdf.format(mfe.getUploadDate());
                files.add(oFileName + ";" + mfe.getId() + ";" + edittime);
            }
        }

        return files;
    }

    @ResponseBody
    @RequestMapping("downAllFiles")
    @ApiOperation(value = "打包下载fileIDs", notes = "打包下载fileIDs")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileIDs", value = "附加ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "fileName", value = "文件名", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "includeFolder", value = "压缩包包含文件夹", paramType = "query", dataType = "String", required = false, defaultValue = "true")})
    public void downAllFiles(@RequestParam("fileIDs") String fileIDs,
                             @RequestParam(value = "fileName", defaultValue = "") String reqFileName,
                             @RequestParam(value = "includeFolder", defaultValue = "true") String includeFolder, HttpServletResponse res,
                             HttpServletRequest request) throws FileNotFoundException {
        // System.out.println();
        JSONArray filesArray = null;
        try {
            filesArray = new JSONArray(fileIDs);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        String folderName = UuidUtils.createUuid();
        String childFolderName = folderName;
        // modify by wujx 20160816 begin
        // 取第一个文件名称作为压缩文件名称，为避免重复冲突，把该文件夹先放到文件夹名为UUID中
        if (StringUtils.isNotBlank(reqFileName)) {
            childFolderName = reqFileName;
        } else if (filesArray.length() > 0) {
            try {
                JSONObject obj = filesArray.getJSONObject(0);
                String fileId = obj.getString("fileID");
                MongoFileEntity mongoFileEntity = this.mongoFileService.getFile(fileId);
                childFolderName = mongoFileEntity.getFileName();
                childFolderName = childFolderName.substring(0, childFolderName.lastIndexOf("."));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // modify by wujx 20160816 end

        String zipDir = appDataDir + folderName + pathSeparator + childFolderName;
        File dir = new File(zipDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (int i = 0; i < filesArray.length(); i++) {
            try {
                JSONObject obj = filesArray.getJSONObject(i);
                String fileId = obj.getString("fileID");
                writeToDir(dir, this.mongoFileService.getFile(fileId));
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        String zipFileName = zipDir + ".zip";
        try {
            ZipUtils.zipFolder(zipDir, zipFileName, BooleanUtils.toBoolean(includeFolder));
            // FileUtils.deleteDirectory(dir);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        File file = new File(zipFileName);

        InputStream is = new FileInputStream(file);
        String fileName = file.getName();

        FileDownloadUtils.download(request, res, is, fileName);

        IOUtils.closeQuietly(is);

        /*
         * List<MongoFileEntity> mongoFiles =
         * mongoFileService.getFilesFromFolder( folderID, purpose);
         * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         * if (mongoFiles != null) { for (MongoFileEntity mfe : mongoFiles) {
         * String edittime = sdf.format(mfe.getUploadDate());
         * files.add(mfe.getFileName() + ";" + mfe.getId() + ";" + edittime); }
         * }
         */
        // Config.APP_DATA_DIR

    }

    private void writeToDir(File destFolder, MongoFileEntity fileEntity) {
        File file = new File(destFolder, fileEntity.getFileName());
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = fileEntity.getInputstream();
            int it = 1;
            int idx = fileEntity.getFileName().lastIndexOf(".");
            while (file.exists() && idx > 0) {
                String name = fileEntity.getFileName().substring(0, idx);
                String ext = fileEntity.getFileName().substring(idx);
                file = new File(destFolder, name + "(" + (it++) + ")" + ext);
            }
            file.createNewFile();
            fos = new FileOutputStream(file);
            IOUtils.copyLarge(is, fos);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
            if (is != null) {
                IOUtils.closeQuietly(is);
            }

        }

    }

    @RequestMapping("popFileFromFolder")
    @ResponseBody
    public ResultMessage deleteFromFolder(@RequestParam("fileID") String fileID,
                                          @RequestParam("folderID") String folderID) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            this.mongoFileService.popFileFromFolder(folderID, fileID);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    @RequestMapping("deleteFile")
    @ResponseBody
    public ResultMessage delete(@RequestParam("fileID") String fileID) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            this.mongoFileService.destroyFile(fileID);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    /**
     * 保存文件和push到文件夹在同一个事务
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("testInOneTra")
    @ResponseBody
    public ResultMessage testInOneTra() throws IOException {
        ResultMessage resultMessage = new ResultMessage();

        this.mongoFileService.testInOneTra();

        return resultMessage;
    }

    @RequestMapping("updateFile")
    @ResponseBody
    public ResultMessage updateFile4OneFileId() throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        String fileId = this.mongoFileService.createFolderID();
        String foderId = this.mongoFileService.createFolderID();
        File file1 = new File("c:/3.txt");
        MongoFileEntity fileEntitye = this.mongoFileService.saveFile(fileId, "test.txt", new FileInputStream(file1));
        System.out.println(fileEntitye.getFileID().equals(fileId));
        this.mongoFileService.pushFileToFolder(foderId, fileId, "test");
        File file2 = new File("c:/4.txt");
        fileEntitye = this.mongoFileService.saveFile(fileId, "test2.txt", new FileInputStream(file2));
        System.out.println(fileEntitye.getFileID().equals(fileId));
        this.mongoFileService.pushFileToFolder(foderId, fileId, "test");
        return resultMessage;
    }

    @RequestMapping("file2Folders")
    @ResponseBody
    public ResultMessage file2Folders() throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        String fileId = this.mongoFileService.createFolderID();
        File file1 = new File("c:/3.txt");
        MongoFileEntity fileEntitye = this.mongoFileService.saveFile(fileId, "test.txt", new FileInputStream(file1));
        String foderId = this.mongoFileService.createFolderID();
        String foderId2 = this.mongoFileService.createFolderID();
        this.mongoFileService.pushFileToFolder(foderId, fileId, "test");
        this.mongoFileService.pushFileToFolder(foderId2, fileId, "test");
        return resultMessage;
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public void file(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam(value = "path", required = true) String path) throws IOException, ServletException {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(request.getRealPath("/") + "/" + URLDecoder.decode(path, "utf-8"));
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            // String ext = filename.substring(filename.lastIndexOf(".") +
            // 1).toUpperCase();

            // 以流的形式下载文件。
            /*
             * InputStream fis = new BufferedInputStream(new
             * FileInputStream(file)); byte[] buffer = new
             * byte[fis.available()]; fis.read(buffer); fis.close(); //
             * 清空response response.reset(); // 设置response的Header
             * response.addHeader("Content-Disposition", "attachment;filename="
             * + filename); response.addHeader("Content-Length", "" +
             * file.length()); OutputStream toClient = new
             * BufferedOutputStream(response.getOutputStream());
             * response.setContentType("application/octet-stream");
             * toClient.write(buffer); toClient.flush(); toClient.close();
             */
            FileDownloadUtils.download(request, response, new FileInputStream(file), filename);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void deleteFile(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(value = "id", required = true) String fileID) {
        mongoFileService.destroyFile(fileID);
    }

    /**
     * 删除没有用的mongo原文件
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("destroyUselessPysicalFile")
    @ResponseBody
    public ResultMessage destroyUselessPysicalFile() throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        String synbeforedays = Config.getValue("mongodb.server.gc.synbeforedays");
        List<GridFSDBFile> fsdbFiles = this.mongoFileService.findProtoFiles(synbeforedays);
        if (fsdbFiles == null || fsdbFiles.size() == 0) {
            resultMessage.setData("no files in mongodb");
            return resultMessage;
        }

        long uselesscount = 0l;
        long usefulcount = 0l;
        long uselessSize = 0l;
        long usefulSize = 0l;
        long totalcount = 0;
        List<String> physicalFileIds = new ArrayList<String>();
        Map<String, GridFSDBFile> physicalInfos = new HashMap<String, GridFSDBFile>();
        for (GridFSDBFile fsdbFile : fsdbFiles) {
            String physicalFileId = fsdbFile.getId().toString();
            physicalFileIds.add(physicalFileId);
            physicalInfos.put(physicalFileId, fsdbFile);
            totalcount++;
            if (physicalFileIds.size() <= 60 && totalcount != fsdbFiles.size()) {
                continue;
            }

            List<LogicFileInfo> logicFileInfos = this.mongoFileService.getFilesByPhysicalFileId(physicalFileIds);
            for (String physicalFileIdx : physicalFileIds) {
                fsdbFile = physicalInfos.get(physicalFileIdx);
                boolean isUsed = isUsedOfPhysicalFile(physicalFileIdx, logicFileInfos);
                if (isUsed) {
                    usefulcount++;
                    usefulSize += fsdbFile.getLength();
                    logger.info("used file:" + physicalFileIdx + "(" + fsdbFile.getFilename() + ") fulcount: "
                            + usefulcount + "space:" + usefulSize + "  total:" + totalcount);
                } else {
                    // this.mongoFileService.destroyProtoFile(fsdbFile.getId().toString());
                    uselesscount++;
                    uselessSize += fsdbFile.getLength();
                    logger.info("------->find a useless file:" + physicalFileIdx + "(" + fsdbFile.getFilename()
                            + ") lesscount:" + uselesscount + " space:" + uselessSize + "  total:" + totalcount);
                }
            }

            physicalFileIds.clear();
            physicalInfos.clear();
        }
        logger.info("no useless file any more ^-^ it's cleaned count:" + uselesscount + "  size:" + uselessSize);
        resultMessage.setData("yes , it's cleaned count:" + uselesscount + "  size:" + uselessSize);
        return resultMessage;
    }

    private boolean isUsedOfPhysicalFile(String physicalFileId, List<LogicFileInfo> logicFileInfos) {
        if (logicFileInfos == null || logicFileInfos.size() == 0) {
            return false;
        }
        for (LogicFileInfo lfile : logicFileInfos) {
            if (physicalFileId.equals(lfile.getPhysicalFileId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除没有用的mongo原文件
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("destroyUselessLogicFile")
    @ResponseBody
    public ResultMessage destroyUselessLogicFile() throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        long emptycount = 0l;
        long entitycount = 0l;

        long totalcount = 0;
        int first = 0;
        while (true) {
            List<QueryItem> items = this.mongoFileService.getFilesByPage(first, 100);

            for (QueryItem item : items) {
                // String fileID = item.getString("uuid");
                String key = QueryItem.getKey("PHYSICAL_FILE_ID");
                String PHYSICAL_FILE_ID = (String) item.get(key);
                MongoFileEntity file = this.mongoFileService.findProtoFile(PHYSICAL_FILE_ID);
                if (file == null) {
                    emptycount++;
                } else {
                    entitycount++;
                }
                totalcount++;
                first++;

            }
            logger.info("yes , it's cleaned count emptycount=" + emptycount + " totalcount=" + totalcount
                    + " entitycount" + entitycount);
            if (items.size() < 100) {
                break;
            }
        }

        resultMessage.setData("yes , it's cleaned count emptycount=" + emptycount + " totalcount=" + totalcount
                + " entitycount" + entitycount);
        return resultMessage;
    }

    /**
     * text/html的意思是将文件的content-type设置为text/html的形式，浏览器在获取到这种文件时会自动调用html的解析器对文件进行相应的处理。
     * text/plain的意思是将文件设置为纯文本的形式，浏览器在获取到这种文件时并不会对其进行处理。
     *
     * @param fileID
     * @param preview
     * @param res
     * @param request
     * @param model
     * @throws IOException
     * @throws TikaException
     */
    @RequestMapping(value = "/content2", produces = {MediaType.TEXT_HTML_VALUE})
    public void content2(@RequestParam("fileID") String fileID,
                         @RequestParam(value = "preview", required = false) String preview, HttpServletResponse res,
                         HttpServletRequest request) throws IOException, TikaException {
        MongoFileEntity file = mongoFileService.getFile(fileID);
        InputStream is = file.getInputstream();
        AutoDetectReader reader = null;
        try {
            Metadata metadata = new Metadata();
            metadata.add(Metadata.CONTENT_TYPE, file.getContentType());
            metadata.add(Metadata.RESOURCE_NAME_KEY, file.getFileName());
            // metadata.add(Metadata.CONTENT_LENGTH, String.valueOf(file.getFileSize()));
            reader = new AutoDetectReader(is, metadata);
            // IOUtils.copy(is, res.getOutputStream());
            IOUtils.copy(reader, res.getOutputStream());
            // res.setCharacterEncoding(String.valueOf(reader.getCharset()));
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(reader);
        }
    }

    @GetMapping("/getFilesFromFolder")
    public @ResponseBody
    HashSet<HashMap<String, String>> getFilesFromFolder(@RequestParam String folder,
                                                        @RequestParam String purpose,
                                                        @RequestParam(required = false) Boolean distinct,
                                                        @RequestParam(required = false) Boolean filterFileNotFound) {
        HashSet<HashMap<String, String>> fileuuids = Sets.newHashSet();
        List<LogicFileInfo> logicFileInfos = mongoFileService.getNonioFilesFromFolder(folder, purpose);
        if (CollectionUtils.isEmpty(logicFileInfos)) {
            return fileuuids;
        }
        Set<String> physicalFileIds = Sets.newHashSet();

        Collections.sort(logicFileInfos, new Comparator<LogicFileInfo>() {
            @Override
            public int compare(LogicFileInfo o1, LogicFileInfo o2) {
                return o2.getModifyTime().compareTo(o1.getModifyTime());
            }
        });
        for (LogicFileInfo f : logicFileInfos) {
            if (BooleanUtils.isTrue(distinct)) {
                if (!physicalFileIds.add(f.getFileName() + f.getPhysicalFileId())) {
                    continue;
                }
            }
            if (BooleanUtils.isTrue(filterFileNotFound) && !mongoFileService.existFile(f.getPhysicalFileId())) {
                continue;
            }

            HashMap<String, String> map = Maps.newHashMap();
            map.put("fileid", f.getUuid());
            map.put("filename", f.getFileName());
            map.put("physicalID", f.getPhysicalFileId());
            fileuuids.add(map);
        }
        return fileuuids;
    }


    @GetMapping("/checkFileHeadMagicByte")
    public @ResponseBody
    Boolean strictCheckFileHeadMagicByte(@RequestParam String fileID) {
        MongoFileEntity fileEntity = mongoFileService.getFile(fileID);
        if (fileEntity != null) {
            int index = fileEntity.getFileName().lastIndexOf(Separator.DOT.getValue());
            if (index > -1) {
                String ext = fileEntity.getFileName().substring(index + 1).toLowerCase();
                List<int[]> signatures = FileSignatures.getFileSignatures().get(ext);
                if (signatures != null) {
                    // 计算需要读取的字节长度（取最长的签名长度）
                    int maxSignatureLength = signatures.stream()
                            .mapToInt(signature -> signature.length)
                            .max()
                            .orElse(0);

                    byte[] headerBytes = new byte[maxSignatureLength];
                    try {
                        fileEntity.getInputstream().read(headerBytes);
                        StringBuilder hexString = new StringBuilder();
                        for (byte b : headerBytes) {
                            hexString.append(String.format("%02X", b));
                        }
                        String headerHex = hexString.toString();
                        // 检查是否匹配签名
                        for (int[] signature : signatures) {
                            // 将签名转换为十六进制字符串
                            StringBuilder signatureHex = new StringBuilder();
                            for (int b : signature) {
                                signatureHex.append(String.format("%02X", b));
                            }
                            // 检查文件头部是否以签名开头
                            if (headerHex.startsWith(signatureHex.toString())) {
                                return true;
                            }
                        }
                        return false;
                    } catch (Exception e) {
                        logger.error("文件头签名比较异常", e);
                    }
                }
            }
        }

        return true;
    }

    public interface PURPOSE_Constance {
        public final String SIGNATURE = "signature_";
    }


}
