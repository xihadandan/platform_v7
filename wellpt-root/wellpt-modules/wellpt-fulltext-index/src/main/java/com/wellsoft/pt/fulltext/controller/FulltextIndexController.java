package com.wellsoft.pt.fulltext.controller;

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.fulltext.index.FulltextDocumentIndex;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.service.FulltextDocumentIndexService;
import com.wellsoft.pt.fulltext.service.FulltextRebuildIndexService;
import com.wellsoft.pt.fulltext.service.FulltextSearchWordService;
import com.wellsoft.pt.fulltext.service.IndexExtDicService;
import com.wellsoft.pt.fulltext.vo.DeleteDocumentIndexVo;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月10日   chenq	 Create
 * </pre>
 */
@Api(tags = "全文检索")
@RestController
@RequestMapping("/api/fulltext/index")
public class FulltextIndexController extends BaseController {

    @Autowired
    IndexExtDicService indexExtDicService;

    @Autowired
    FulltextDocumentIndexService fulltextDocumentIndexService;

    @Autowired
    FulltextRebuildIndexService fulltextRebuildIndexService;

    @Autowired
    FulltextSearchWordService fulltextSearchWordService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;


    @GetMapping("/rebuild")
    public void rebuild() {
        fulltextRebuildIndexService.rebuildIndex();
    }

    /**
     * 保存索引，返回索引ID
     *
     * @return 索引ID
     * @author baozh
     * @date 2021/9/10 16:02
     * @params documentIndex 索引内容
     */
    @PostMapping("/save")
    public ApiResult saveIndex(@Validated @RequestBody FulltextDocumentIndex documentIndex) {
        String indexId = fulltextDocumentIndexService.saveIndex(documentIndex);
        return ApiResult.success("保存成功：" + indexId);
    }

    /**
     * 批量保存
     *
     * @return
     * @author baozh
     * @date 2021/9/14 17:41
     * @params *@params
     */
    @PostMapping("/batchSave/{indexName}")
    public ApiResult batchSaveIndex(@PathVariable("indexName") String indexName, @RequestBody List<FulltextDocumentIndex> documentIndexs) {
        boolean flag = fulltextDocumentIndexService.batchSaveIndex(indexName, documentIndexs);
        return flag ? ApiResult.success("批量保存成功") : ApiResult.fail();
    }

    @PostMapping("/update")
    public ApiResult updateIndex(@Validated @RequestBody FulltextDocumentIndex documentIndex) {
        Boolean flag = fulltextDocumentIndexService.updateIndex(documentIndex);
        return flag ? ApiResult.success("保存成功") : ApiResult.fail();
    }

    @PostMapping("/delete")
    public ApiResult deleteIndex(@RequestBody DeleteDocumentIndexVo indexVo) {
        Boolean flag = fulltextDocumentIndexService.delete(indexVo);
        return flag ? ApiResult.success("删除成功") : ApiResult.fail();

    }

    @PostMapping("/query")
    @ApiOperation(value = "查询索引", notes = "查询索引")
    public ApiResult<QueryData> queryIndex(@RequestBody IndexRequestParams indexRequestParams) {
        QueryData queryData = fulltextDocumentIndexService.query(indexRequestParams);
        addCurrentUserSearchKeyword(indexRequestParams.getKeyword());
        return ApiResult.success(queryData);
    }

    private void addCurrentUserSearchKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return;
        }
        String system = RequestSystemContextPathResolver.system();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        scheduledExecutorService.execute(() -> {
            try {
                RequestSystemContextPathResolver.setSystem(system);
                IgnoreLoginUtils.login(userDetails);
                fulltextSearchWordService.addUserSearchWord(userDetails.getUserId(), StringUtils.trim(keyword));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        });
    }

    @PostMapping("/countCategory")
    @ApiOperation(value = "查询分类数量", notes = "查询分类数量")
    public ApiResult<Map<String, Long>> countCategory(@RequestBody IndexRequestParams indexRequestParams) {
        Map<String, Long> countMap = fulltextDocumentIndexService.countCategory(indexRequestParams);
        return ApiResult.success(countMap);
    }

    /**
     * 异步保存索引
     *
     * @return
     * @author baozh
     * @date 2021/9/10 16:02
     * @params documentIndex 索引内容
     */
    @PostMapping("/asyncSave")
    public ApiResult asynchronousSaveIndex(@Validated @RequestBody FulltextDocumentIndex documentIndex) {
        Future<String> futureStr = fulltextDocumentIndexService.asyncSaveIndex(documentIndex);
        return ApiResult.success("添加成功");
    }

    /**
     * 异步批量保存
     *
     * @return
     * @author baozh
     * @date 2021/9/14 17:41
     * @params *@params
     */
    @PostMapping("/asyncBatchSave/{indexName}")
    public ApiResult asynchronousBatchSaveIndex(@PathVariable("indexName") String indexName, @RequestBody List<FulltextDocumentIndex> documentIndexs) {
        fulltextDocumentIndexService.asyncBatchSaveIndex(indexName, documentIndexs);
        return ApiResult.success("批量添加成功");
    }


    @GetMapping("/getCategorys")
    public ApiResult getCategoryList() {
        List<DataDictionary> dataDictionaryList = fulltextDocumentIndexService.getDataDictionaryList();
        List<Map<String, String>> collect = dataDictionaryList.stream().map(data -> {
            Map<String, String> result = new HashMap<>();
            result.put("name", data.getName());
            result.put("code", data.getCode());
            return result;
        }).collect(Collectors.toList());
        return ApiResult.success(collect);
    }

    @GetMapping("/listHotWord")
    @ApiOperation(value = "获取热门搜索词", notes = "获取热门搜索词")
    public ApiResult<List<String>> listHotWord() {
        List<String> hotWords = fulltextSearchWordService.listHotWordBySystem(RequestSystemContextPathResolver.system());
        return ApiResult.success(hotWords);
    }

    @GetMapping("/listCurrentUserSearchHistory")
    @ApiOperation(value = "获取当前用户搜索历史", notes = "获取当前用户搜索历史")
    public ApiResult<List<String>> listCurrentUserSearchHistory() {
        List<String> historyWords = fulltextSearchWordService.listUserSearchHistory(SpringSecurityUtils.getCurrentUserId(), RequestSystemContextPathResolver.system());
        return ApiResult.success(historyWords);
    }

    @GetMapping("/listCurrentUserCommonSearch")
    @ApiOperation(value = "获取当前用户常用搜索", notes = "获取当前用户常用搜索")
    public ApiResult<List<String>> listCurrentUserCommonSearch() {
        List<String> commonWords = fulltextSearchWordService.listUserCommonSearch(SpringSecurityUtils.getCurrentUserId(), RequestSystemContextPathResolver.system());
        return ApiResult.success(commonWords);
    }

    @GetMapping("/listKeywordLike")
    @ApiOperation(value = "关键字补全", notes = "关键字补全")
    public ApiResult<List<String>> listKeywordLike(@RequestParam("keyword") String keyword) {
        List<String> likeWords = fulltextSearchWordService.listKeywordLike(StringUtils.trim(keyword), RequestSystemContextPathResolver.system());
        return ApiResult.success(likeWords);
    }

}
