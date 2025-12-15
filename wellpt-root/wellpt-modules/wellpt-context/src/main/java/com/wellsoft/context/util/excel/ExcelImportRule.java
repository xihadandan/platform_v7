package com.wellsoft.context.util.excel;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年05月26日   chenq	 Create
 * </pre>
 */
public class ExcelImportRule implements Serializable {
    private Boolean importLog = false;
    private List<SheetImportRule> sheetImportRules;
    private Boolean strict = false;

    public Boolean getImportLog() {
        return importLog;
    }

    public void setImportLog(Boolean importLog) {
        this.importLog = importLog;
    }


    public List<SheetImportRule> getSheetImportRules() {
        return sheetImportRules;
    }

    public void setSheetImportRules(List<SheetImportRule> sheetImportRules) {
        this.sheetImportRules = sheetImportRules;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }
}
