package com.wellsoft.context.util.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.google.gson.Gson;
import com.wellsoft.context.config.service.SystemParamsFacadeService;
import com.wellsoft.context.util.ApplicationContextHolder;

/**
 * Description: 导入监听服务样例
 *
 * @author chenq
 * @date 2019/12/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/9    chenq		2019/12/9		Create
 * </pre>
 */
public class ExampleExcelImportListener extends
        AbstractEasyExcelImportListener<ExampleExcelImportDo> {


    private SystemParamsFacadeService paramsFacadeService;

    public ExampleExcelImportListener() {
        //其他服务实例类通过构造函数获取
        paramsFacadeService = ApplicationContextHolder.getBean(SystemParamsFacadeService.class);
    }

    @Override
    public ExcelRowDataAnalysedResult dataAnalysed(ExampleExcelImportDo exampleExcelImportDto,
                                                   int rowIndex, AnalysisContext analysisContext) {
        System.out.println(new Gson().toJson(exampleExcelImportDto));

        //TODO:业务处理

        return new ExcelRowDataAnalysedResult(rowIndex % 2 == 0,
                rowIndex % 2 == 0 ? "" : "测试异常");//成功返回
    }

    @Override
    public String name() {
        return "自定义导入服务监听样例";
    }
}
