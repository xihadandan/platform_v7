package com.wellsoft.pt.repository.web;

import com.alibaba.fastjson.util.IOUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.repository.entity.ConvertStatus;
import com.wellsoft.pt.repository.entity.KingGridFileEntity;
import com.wellsoft.pt.repository.service.KingGridFileService;
import com.wellsoft.pt.repository.utils.KingGridConfig;
import com.wellsoft.pt.repository.vo.KingGridParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Description:
 * 金格文件处理
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/8   Create
 * </pre>
 */
@Api(tags = "金格文件转换")
@RestController
@RequestMapping("api/kingGrid/file")
public class KingGridFileController extends BaseController {

    @Autowired
    KingGridFileService kingGridFileService;

    @Autowired
    KingGridConfig kingGridConfig;


    /**
     * 上传文件并发起异步转换
     *
     * @return
     * @author baozh
     * @date 2021/11/8 17:08
     * @params *@params
     */
    @ApiOperation(value = "上传文件转换", notes = "上传文件转换")
    @ApiImplicitParam(name = "type", value = "转换类型(office2ofd:office转为ofd,office2pdf:office转为pdf,pdf2ofd:pdf转为ofd,ofd2pdf:ofd转为pdf,preview:预览)", dataType = "String", required = true)
    @PostMapping("/uploadFile/{type}")
    public ApiResult<KingGridFileEntity> uploadFile(@PathVariable String type, @RequestParam("file") MultipartFile uploadFile, KingGridParamVo paramVo, HttpServletRequest request) throws IOException {
        // 获取文件名
        String fileName = uploadFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String uploadFilePath = kingGridConfig.getUploadFilePath();

        if (StringUtils.isBlank(uploadFilePath)) {
            uploadFilePath = System.getProperty("user.dir") + File.separator + "upload";
        }
        //判断路径是否存在
        File directory = createDirectory(uploadFilePath);
        String targetFilePath = kingGridConfig.getTargetFilePath();
        if (StringUtils.isBlank(targetFilePath)) {
            targetFilePath = System.getProperty("user.dir") + File.separator + "target";
        }
        File targetDirectory = createDirectory(targetFilePath);
        // 用uuid作为文件名，防止生成的临时文件重复
        File tempFile = File.createTempFile(uuid, prefix, directory);
        uploadFile.transferTo(tempFile);
        KingGridFileEntity fileEntity = kingGridFileService.saveFile(fileName, tempFile, type, targetDirectory, paramVo);
        return ApiResult.success(fileEntity);
    }

    private File createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    /**
     * 根据uuid获取文件，如果转化成功则获取文件
     *
     * @return
     * @author baozh
     * @date 2021/11/8 17:08
     * @params *@params
     */
    @ApiOperation(value = "转换文件下载", notes = "转换文件下载")
    @ApiImplicitParam(name = "uuid", value = "转换uuid", dataType = "String", required = true)
    @GetMapping("/download/{uuid}")
    public ApiResult downloadFile(@PathVariable String uuid, HttpServletRequest request, HttpServletResponse response) {
        KingGridFileEntity fileEntity = kingGridFileService.getOne(uuid);
        if (ConvertStatus.SUCCESS != fileEntity.getStatus()) {
            return ApiResult.success(fileEntity);
        }
        InputStream is = null;
        try {
            File file = new File(fileEntity.getTargetFilePath());
            is = new FileInputStream(file);
            FileDownloadUtils.download(request, response, is, fileEntity.getFileName() + "." + fileEntity.getConvertType().getConvertSuffix()); // WorkForm Def
        } catch (IOException e) {

        } finally {
            IOUtils.close(is);

        }
        return ApiResult.success();
    }
}
