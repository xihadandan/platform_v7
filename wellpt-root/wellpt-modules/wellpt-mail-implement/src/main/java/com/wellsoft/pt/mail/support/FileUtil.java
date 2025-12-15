package com.wellsoft.pt.mail.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.repository.entity.FileUpload;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.*;
import java.util.List;

/**
 * Description: smartupload 文件工具类
 *
 * @author wuzq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1	wuzq		2013-2-7		Create
 * </pre>
 * @date 2013-3-1
 */

public class FileUtil {

    public static final int StreamFlushBufferSzie = 100;//buffer size= 1K

    /**
     * 文件上传方法.
     * @param su
     * @param pageContext
     * @return
     * @throws Exception
     */
	/*
	public static String fileUpload(SmartUpload su, PageContext pageContext) throws Exception {
	com.wellsoft.pt.mail.support.File suFile = null;
	int fileCount = 0;
	StringBuffer sb = new StringBuffer();
	FileInputStream in = null;
	try {
		//获取传递过来的参数
		String fileMid = su.getRequest().getParameter("fileMid");
		String attachment = su.getRequest().getParameter("attachment");
		//String passId = su.getRequest().getParameter("pass_id");

		//fileMid = String.valueOf(GenerateUnique.getKey());

		String fileExt = "";
		int fileSize = 0;
		//String AllowedExtensions = ",jpg,jpeg,gif,";//允许上传的文件类型
		String AllowedExtensions = "";//允许上传的文件类型
		double maxFileSize = 30 * 1024;//单文件最大大小，单位KB
		//校验文件类型和大小
		for (int i = 0; i < su.getFiles().getCount(); i++) {
			suFile = su.getFiles().getFile(i);
			if (suFile.isMissing())
				continue;
			//校验文件大小
			fileSize = suFile.getSize() / 1024;//字节转换成KB
			if (suFile.getSize() == 0)
				throw new Exception("大小不能为空");
			if (fileSize == 0)
				fileSize = 1;
			if (maxFileSize < fileSize)
				throw new Exception("单个上传相片的容量不能超过[" + maxFileSize + "KB]");

			//校验文件类型
			if (suFile.getFileExt() == null || "".equals(suFile.getFileExt())) {
				fileExt = ",,";
			} else {
				fileExt = "," + suFile.getFileExt().toLowerCase() + ",";
			}
			if (!"".equals(AllowedExtensions) && AllowedExtensions.indexOf(fileExt) == -1) {
				throw new Exception("您上传的文件[" + suFile.getFileName() + "]的类型为系统禁止上传的文件类型，不能上传！");
			}
			fileCount++;
		}
		if (fileCount == 0)
			throw new Exception("请选择上传的文件");
		String filename = suFile.getFileName().replaceAll("#", "").replaceAll("|", "").replaceAll("@", "")
				.replaceAll(";", "").replaceAll(",", "");
		//准备保存文件
		//            String filePath=(FileUtil.filePath.equals("/")?FileUtil.filePath:FileUtil.filePath+"/")+fileMid+"/";//这里填写项目中存放上传文件的物理路径
		String filePath = MailUtil.getMailUpload();

		//            File ff=new File(filePath);
		//            if(!ff.exists()){
		//            	ff.mkdir();
		//            }
		if (null != attachment && attachment.indexOf(filename) >= 0) {
			return "exist";
		}
		MailFileService mailFileService = ApplicationContextHolder.getBean(MailFileService.class);
		MailFileBean bean = mailFileService.getMailFiles(filename, String.valueOf(suFile.getSize()));
		if (null != bean) {
			sb.append(bean.getFileName());
			sb.append("|" + bean.getFileMid());
			sb.append("#|#successed");
			return sb.toString();
		} else {
			//            FileService fileService = ApplicationContextHolder.getBean(FileService.class);
			MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
			filePath = filePath + fileMid + "/";
			for (int i = 0; i < su.getFiles().getCount(); i++) {
				suFile = su.getFiles().getFile(i);
				java.io.File f = new java.io.File(filePath);
				if (!f.exists()) {
					f.mkdir();
				}
				suFile.saveAs(filePath + filename, SmartUpload.SAVE_PHYSICAL);//保存文件
				java.io.File file = new java.io.File(filePath + filename);
				//                FileInputStream in= new FileInputStream(suFile.getFilePathName()+suFile.getFieldName());
				in = new FileInputStream(file);
				fileMid = String.valueOf(GenerateUnique.getKey());
				//TODO
				//                fileService.uploadFile("mail", fileMid,in, filename);
				FileEntity fe = new FileEntity();
				fe.setFile(in);
				fe.setFilename(filename);
				fe.setSize(suFile.getSize());
				fileService.uploadTempFile(fe, fileMid);
				//                MailTempFile mtf=new MailTempFile();
				//                mtf.setFileMid(fileMid);
				//                mtf.setFileName(filename);
				//                mtf.setCreateTime(new Date());
				//                mtf.setFileSize(String.valueOf(suFile.getSize()));
				//                mtf.setFileType(getFileType(suFile.getFileExt()));
				//                mailFileService.saveMailTempFile(mtf);
			}
			sb.append(filename);
			sb.append("|" + (fileMid == null || "null".equals(fileMid) ? "" : fileMid));
			sb.append("#|#successed");
			return sb.toString();
		}
	} finally {
		if (null != in) {
			in = null;
		}
	}
	}*/
    protected static final Log log = LogFactory.getLog(FileUtil.class);

    /**
     * 保存文件
     */
    public static boolean saveFileFromInputStream(java.io.InputStream stream, String path, String filename) {
        java.io.FileOutputStream fs = null;

        try {
            File f = new File(path);
            if (!f.exists())
                f.mkdir();
            String filePath = path.endsWith("/") == true ? path : path + "/";
            java.io.File file = new java.io.File(filePath + filename);
            fs = new java.io.FileOutputStream(file);
            byte[] buffer = new byte[1024 * 1024];
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = stream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
                fs.flush();
            }
            fs.close();
            stream.close();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (null != fs) {
                fs = null;
            }
            if (null != stream) {
                stream = null;
            }
        }
    }

    /**
     * 指定文件路径，得到文件字节流<p>
     *
     * @param path 文件路径
     * @return 文件流
     * @throws IOException
     */
    public static ByteArrayOutputStream downloadFileEx(String filePathAndName) throws Exception {
        File file = new File(filePathAndName);
        if (!file.exists())
            throw new Exception("指定文件" + filePathAndName + "不存在");

        FileInputStream fis = new FileInputStream(filePathAndName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        int i;
        while ((i = bis.read()) != -1) {
            bos.write(i);
        }
        bos.flush(); // 提交文件流，很关键
        bis.close();
        return baos;
    }

    /**
     * 文件下载
     *
     * @param object      FileInputStream或ByteArrayOutputStream
     * @param FileName    文件名
     * @param contenttype 下载类型
     * @throws Exception
     */
    public static void downLoadFile(HttpServletResponse response, String filePath, String fileName) throws Exception {
        filePath = filePath.endsWith("/") ? filePath : filePath + "/";
        //    	fileName=new String(fileName.getBytes("ISO8859_1"),"utf-8");
        System.out.println("filename==" + fileName);
        //    	System.out.println("filename1=="+new String(fileName.getBytes("utf-8"),"ISO8859_1"));
        //    	System.out.println("filename1=="+new String(fileName.getBytes("gbk"),"ISO8859_1"));
        //    	System.out.println("filename1=="+new String(fileName.getBytes("ISO8859_1"),"GBK"));
        //    	System.out.println("filename1=="+new String(fileName.getBytes("ISO8859_1")));
        //    	System.out.println("filename1=="+new String(fileName.getBytes("utf-8")));
        String filePathAndName = filePath + fileName;
        Object object = downloadFileEx(filePathAndName); // 获得文件流
        fileName = StringUtils.replaceChars(fileName, "\r\n", "");
        //        FacesContext ctx = FacesContext.getCurrentInstance();
        //        if (!ctx.getResponseComplete()) {
        //            String contentType = contenttype;
        // 获得Excel文件流(可能是输出流，也可能是输入流)
        // 调用CommonUtil类下的方法，将Object转换成ByteArrayOutputStream
        ByteArrayOutputStream baos = castToBAOStream(object);
        //            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext()
        //                    .getResponse();
        //            response.setContentType(contentType);
        // 生成默认文件名
        // defaultFileName = 文档类型代码.业务申报书 + BriefBizTaskVO.bizEventNumber
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes()));
        response.setContentType("application/octet-stream;charset=utf-8");
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
        //            ctx.responseComplete();
        //            saveView();
        //        }
    }

    /**
     * 将FileInputStream或ByteArrayOutputStream统一转换成ByteArrayOutputStream
     *
     * @param obj        需要转换的FileInputStream或ByteArrayOutputStream 不支持其他类型
     * @param bufferSize 转换中使用的buffer尺寸
     * @return 转换后的ByteArrayOutputStream
     * <p>
     * 说明： bufferSzie 拥护下载是长操作，不能直接将所有字节全部读出后再统一写入，所以需要确定Buffer尺寸
     */
    public static ByteArrayOutputStream castToBAOStream(Object obj, int bufferSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (obj.getClass().isInstance(baos)) {
            return (ByteArrayOutputStream) obj;
        } else {
            FileInputStream fis = (FileInputStream) obj;
            try {
                BufferedInputStream bis = new BufferedInputStream(fis);
                baos = new ByteArrayOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(baos);
                int ch;
                int i = 0;
                while ((ch = bis.read()) != -1) {
                    bos.write(ch);
                    if (i++ == bufferSize) {
                        bos.flush();
                        i = 0;
                    }
                }
                bos.flush(); //提交文件流，很关键
                bis.close();
                return baos;
            } catch (ClassCastException e) {
                log.error("Stream object not a ByteArrayOutputStream or a FileInputStream:" + e.getCause()
                        + e.getMessage(), e);
                return null;
            } catch (Exception e) {
                log.error("baos is null:" + e.getCause() + e.getMessage(), e);
                return null;
            }
        }
    }

    /**
     * 将FileInputStream或ByteArrayOutputStream统一转换成ByteArrayOutputStream
     *
     * @param obj 需要转换的FileInputStream或ByteArrayOutputStream 不支持其他类型
     * @return 转换后的ByteArrayOutputStream
     * <p>
     * 说明： 该方法省略bufferSzi参数，使用缺省的StreamFlushBufferSzie；
     */
    public static ByteArrayOutputStream castToBAOStream(Object obj) {
        return castToBAOStream(obj, StreamFlushBufferSzie);

    }

    /**
     * 获得文件大小
     *
     * @param fileAndPath
     * @return
     */
    public static String getFileSize(String fileAndPath) {
        File f = new File(fileAndPath);
        if (f.length() < 1024) {
            return f.length() + "Byte";
        }
        if (f.length() >= 1024 && f.length() < 1024 * 1024) {
            return convert((double) f.length() / 1024) + "K";
        }
        if (f.length() >= 1024 * 1024) {
            return convert((double) f.length() / (1024 * 1024)) + "M";
        }
        return "";
    }

    /**
     * 获得文件大小
     *
     * @param f
     * @return
     */
    public static String getFileSize(File f) {
        if (f.length() < 1024) {
            return f.length() + "Byte";
        }
        if (f.length() >= 1024 && f.length() < 1024 * 1024) {
            return convert((double) f.length() / 1024) + "K";
        }
        if (f.length() >= 1024 * 1024) {
            return convert((double) f.length() / (1024 * 1024)) + "M";
        }
        return "";
    }

    /**
     * 获得文件大小
     *
     * @param fileAndPath
     * @return
     */
    public static String getFileSize2(String fileSize) {
        int f = Integer.parseInt(fileSize);
        if (f < 1024) {
            return f + "B";
        }
        if (f >= 1024 && f < 1024 * 1024) {
            return convert((double) f / 1024) + "K";
        }
        if (f >= 1024 * 1024) {
            return convert((double) f / (1024 * 1024)) + "M";
        }
        return "";
    }

    /**
     * 文件大小四舍五入
     *
     * @param value
     * @return
     */
    private static double convert(double value) {
        long l1 = Math.round(value * 100); //四舍五入
        double ret = l1 / 100.0; //注意：使用   100.0   而不是   100
        return ret;
    }

    /**
     * 邮件附件前台展示(Mongo)
     */
    public static List<Object> showFileWithMongo(List<Object> dbFilesList, String fileId) {
        MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
        String fileName = mongoFileService.getFile(fileId).getFileName();
        FileUpload fu = new FileUpload();
        fu.setFileID(fileId);
        fu.setFilename(fileName);
        dbFilesList.add(fu);
        return dbFilesList;
    }
}
