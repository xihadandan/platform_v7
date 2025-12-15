package com.wellsoft.pt.api;

import com.wellsoft.pt.api.request.ApiRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/10    chenq		2018/8/10		Create
 * </pre>
 */
public interface WellptApi {

    ApiResponse handleRequest(HttpServletRequest httpServletRequest,
                              HttpServletResponse httpServletResponse,
                              String methodName) throws Exception;

    ApiResponse postMultipartFile(Map<String, String[]> parameters,
                                  Map<String, MultipartFile> files);

    ApiResponse post(ApiRequest request);

    ApiResponse get(Map<String, String[]> parameters);


}
