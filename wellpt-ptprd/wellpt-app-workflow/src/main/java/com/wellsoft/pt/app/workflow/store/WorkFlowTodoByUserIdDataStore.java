package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 工作流程_待办_指定用户
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/4/2.1	    zenghw		2022/4/2		    Create
 * </pre>
 * @date 2022/4/2
 */
@Component
public class WorkFlowTodoByUserIdDataStore extends WorkFlowTodoDataStore {

    private String handoverUserId;

    @Override
    public String getQueryName() {
        return "工作流程_待办_指定用户";
    }

    @Override
    protected List<String> getSids() {
        List<String> sids = new ArrayList<String>();
        sids.add(this.handoverUserId);
        return sids;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {

        this.handoverUserId = (String) context.getQueryParams().get("handoverUserId");
        return super.query(context);
    }

}
