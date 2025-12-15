package com.wellsoft.context.component.select2;

import com.wellsoft.context.jdbc.support.PagingInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;

/**
 * Description: Select2 ajax查询返回数据
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年03月06日.1	zyguo		2018年3月06日		Create
 * </pre>
 * @date 2018年3月06日
 */
@ApiModel("select2下拉分组数据")
public class Select2GroupData {
    @ApiModelProperty("是否加载更多")
    private boolean more;
    @ApiModelProperty("页码")
    private int pageNo;
    @ApiModelProperty("分组数据项列表")
    private ArrayList<Select2GroupBean> results = new ArrayList<Select2GroupBean>();

    public Select2GroupData() {
    }

    public Select2GroupData(PagingInfo pagingInfo) {
        if (pagingInfo != null) {
            this.pageNo = pagingInfo.getCurrentPage();
            this.more = pagingInfo.getTotalPages() > pagingInfo.getCurrentPage();
        }
    }

    public void addResultData(Select2GroupBean item) {
        if (this.results == null) {
            this.results = new ArrayList<Select2GroupBean>();
        }
        // 组存在需要合并在一起
        boolean isExistedGroup = false;
        for (Select2GroupBean select2GroupBean : results) {
            if (select2GroupBean.getText().equals(item.getText())) {
                isExistedGroup = true;
                select2GroupBean.getChildren().addAll(item.getChildren());
            }
        }
        // 组不存在，添加进来
        if (isExistedGroup == false) {
            this.results.add(item);
        }
    }

    public void addResultData(String groupText, Select2DataBean item) {
        if (this.results == null) {
            this.results = new ArrayList<Select2GroupBean>();
        }
        // 组存在需要合并在一起
        boolean isExistedGroup = false;
        for (Select2GroupBean select2GroupBean : results) {
            if (select2GroupBean.getText().equals(groupText)) {
                isExistedGroup = true;
                select2GroupBean.getChildren().add(item);
            }
        }
        // 组不存在，添加进来
        if (isExistedGroup == false) {
            Select2GroupBean bean = new Select2GroupBean();
            bean.setText(groupText);
            bean.getChildren().add(item);
            this.results.add(bean);
        }

    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * @return the results
     */
    public ArrayList<Select2GroupBean> getResults() {
        return results;
    }

    /**
     * @param results 要设置的results
     */
    public void setResults(ArrayList<Select2GroupBean> results) {
        this.results = results;
    }
}
