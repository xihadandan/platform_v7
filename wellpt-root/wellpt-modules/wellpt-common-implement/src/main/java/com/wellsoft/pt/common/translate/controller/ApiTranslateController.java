package com.wellsoft.pt.common.translate.controller;

import com.google.common.collect.Sets;
import com.wellsoft.context.util.encode.EncodeUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.common.translate.service.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月24日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping("/api/translate")
public class ApiTranslateController {

    @Autowired
    TranslateService translateService;

    @GetMapping("/text")
    public ApiResult translateText(@RequestParam String word, @RequestParam String from, @RequestParam String to) {
        String text = translateService.translate(EncodeUtils.urlDecode(word), from, to);
        return ApiResult.success(text);
    }

    @PostMapping("/text")
    public ApiResult batchTranslateText(@RequestBody Map<String, Object> parameters) {
//        List<String> words = (List<String>) parameters.get("word");
//        byte[] bytes = JsonUtils.object2Gson(parameters).getBytes();
//        MD5Digest digest = new MD5Digest();
//        digest.update(bytes, 0, bytes.length);
//        String hex = digest.getHexString();
//        MongoDocumentService mongoDocumentService = ApplicationContextHolder.getBean(MongoDocumentService.class);
//        Map<String, Object> result = mongoDocumentService.getOneAsMapByFieldEq("BATCH_TRANSLATE_COLLECTION", "textHex", hex);
//        if (result != null && result.get("result") != null) {
//            Map<String, String> map = (Map<String, String>) JsonUtils.toBean(result.get("result").toString(), Map.class);
//            return ApiResult.success(map);
//        } else {
//            Map<String, String> map = translateService.translate(Sets.newHashSet(words), parameters.get("from").toString(), parameters.get("to").toString());
//            Map<String, Object> data = Maps.newHashMap();
//            data.put("result", JsonUtils.object2Json(map));
//            data.put("textHex", hex);
//            mongoDocumentService.save("BATCH_TRANSLATE_COLLECTION", data);
//            return ApiResult.success(map);
//        }
        return ApiResult.success(translateService.translate(Sets.newHashSet((List<String>) parameters.get("word")), parameters.get("from").toString(), parameters.get("to").toString()));
    }


//    @PostMapping("/json")
//    public ApiResult batchTranslateText(@RequestBody Map<String, Object> parameters) {
//        String jsonString = (List<String>) parameters.get("json");
//        return ApiResult.success(translateService.translate(Sets.newHashSet(words), parameters.get("from").toString(), parameters.get("to").toString()));
//    }

}
