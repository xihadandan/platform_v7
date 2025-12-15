/*
 * @(#)4/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.pt.app.dingtalk.dto.DingtalkConfigDto;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkConfigFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/25/25.1	    zhulh		4/25/25		    Create
 * </pre>
 * @date 4/25/25
 */
@Api(tags = "钉钉机器人")
@RestController
@RequestMapping("/api/dingtalk/robot")
public class DingtalkRobotController extends BaseController {

    @Autowired
    private DingtalkConfigFacadeService dingtalkConfigFacadeService;

    @IgnoreResultAdvice
    @ApiOperation(value = "消息回调", notes = "消息回调")
    @RequestMapping(value = "/callback/{systemId}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<ApiResult> callback(@PathVariable(name = "systemId") String systemId, HttpServletRequest request) {
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
//        String requestBody = "";
//        try {
//            InputStream is = request.getInputStream();
//            requestBody = IOUtils.toString(is);
//            IOUtils.closeQuietly(is);
//        } catch (Exception e) {
//            logger.error("获取请求体异常", e);
//        }
//        logger.error("接收到钉钉回调消息, systemId:" + systemId + ", timestamp:" + timestamp + ", sign:" + sign + ", requestBody:" + requestBody);
        if (StringUtils.isBlank(timestamp) || StringUtils.isBlank(sign)) {
//            logger.error("钉钉回调消息参数不完整, systemId:" + systemId + ", timestamp:" + timestamp + ", sign:" + sign);
            return new ResponseEntity(ApiResult.success(), HttpStatus.OK);
        }
        try {
            DingtalkConfigDto dingtalkConfigDto = dingtalkConfigFacadeService.getDtoBySystem(systemId);
            String appSecret = dingtalkConfigDto.getClientSecret();
            String stringToSign = timestamp + "\n" + appSecret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String verifySign = new String(Base64.getEncoder().encodeToString(signData));
            if (StringUtils.equals(verifySign, sign)) {
                //               logger.error("钉钉回调消息参数 验证成功, systemId:" + systemId + ", timestamp:" + timestamp + ", sign:" + sign);
                return new ResponseEntity(ApiResult.success(), HttpStatus.OK);
            } else {
                //  logger.error("钉钉回调消息参数 验证失败, systemId:" + systemId + ", timestamp:" + timestamp + ", sign:" + sign);
                return new ResponseEntity(ApiResult.fail(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            // logger.error("钉钉回调消息参数 验证异常, systemId:" + systemId + ", timestamp:" + timestamp + ", sign:" + sign);
            logger.error(e.getMessage(), e);
        }
        // logger.error("钉钉回调消息参数 验证失败, systemId:" + systemId + ", timestamp:" + timestamp + ", sign:" + sign);
        return new ResponseEntity(ApiResult.fail(), HttpStatus.BAD_REQUEST);
    }


    @ApiOperation(value = "获取消息回调token", notes = "获取消息回调token")
    @GetMapping("/getToken")
    public ApiResult<String> getToken() {
        DingtalkConfigDto dingtalkConfigDto = dingtalkConfigFacadeService.getDtoBySystem(RequestSystemContextPathResolver.system());
//        Map<String, Object> body = new HashMap<>();
//        body.put("appId", dingtalkConfigDto.getAgentId());
//        String token = JwtTokenUtil.createToken(body, null);
        return ApiResult.success(dingtalkConfigDto.getUuid().toString());
    }

}
