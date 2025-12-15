package com.wellsoft.pt.multi.org.event;

import com.google.common.collect.Lists;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/31    chenq		2019/7/31		Create
 * </pre>
 */
public class AccountUserEvent extends ApplicationEvent {

    private static final long serialVersionUID = -7706004484945828874L;


    /**
     * 如何描述该构造方法
     *
     * @param source
     */
    public AccountUserEvent(AccountUserEventSource source) {
        super(source);
    }


    public static class AccountUserEventSource implements Serializable {

        private static final String ADD_OPERATION = "add";
        private static final String DELETE_OPERATION = "delete";

        private static final long serialVersionUID = -6534940256759576838L;

        private String operation;
        private List<MultiOrgUserAccount> accounts = Lists.newArrayList();

        public static AccountUserEventSource source() {
            return new AccountUserEventSource();
        }

        public List<MultiOrgUserAccount> getAccounts() {
            return accounts;
        }

        public AccountUserEventSource delete() {
            this.operation = DELETE_OPERATION;
            return this;
        }

        public AccountUserEventSource save() {
            this.operation = ADD_OPERATION;
            return this;
        }

        public AccountUserEventSource accounts(List<MultiOrgUserAccount> data) {
            this.accounts.addAll(data);
            return this;
        }


        public boolean isDelete() {
            return DELETE_OPERATION.equalsIgnoreCase(this.operation);
        }

        public boolean isAdd() {
            return ADD_OPERATION.equalsIgnoreCase(this.operation);
        }
    }

}
