/*
 * @(#)3/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.web;

import com.wellsoft.pt.license.generator.LicenseGenerator;
import com.wellsoft.pt.license.support.LicenseManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 3/29/24.1	zhulh		3/29/24		Create
 * </pre>
 * @date 3/29/24
 */
@Controller
@RequestMapping("/license/verify")
public class LicenseVerifyController {

    /**
     * 对内容签名
     *
     * @param content
     * @return
     */
    @PostMapping(value = "/signature")
    public @ResponseBody
    Map<String, Object> verify(@RequestParam(name = "content", required = true) String content) {
        LicenseGenerator licenseGenerator = new LicenseGenerator();
        LicenseManager licenseManager = licenseGenerator.getLicenseManager();
        Map<String, Object> map = new HashMap<>();
        map.put("signature", licenseManager.sign(content));
        return map;
    }

}
