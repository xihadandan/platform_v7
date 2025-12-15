package com.wellsoft.pt.jpa.log4tx.spring;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.wellsoft.pt.jpa.log4tx.InvocationNode;
import com.wellsoft.pt.jpa.log4tx.InvocationStatistics;
import com.wellsoft.pt.jpa.log4tx.TxLogUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.util.Properties;

@SuppressWarnings("serial")
public class TransactionLogInterceptor extends org.springframework.transaction.interceptor.TransactionInterceptor {

    public TransactionLogInterceptor() {
        super();
    }

    /**
     * @param ptm
     * @param attributes
     */
    public TransactionLogInterceptor(PlatformTransactionManager ptm, Properties attributes) {
        super(ptm, attributes);
    }

    /**
     * @param ptm
     * @param tas
     */
    public TransactionLogInterceptor(PlatformTransactionManager ptm, TransactionAttributeSource tas) {
        super(ptm, tas);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 事务开始前
        TransactionInfo transactionInfo = currentTransactionInfo();
        // Work out the target class: may be {@code null}.
        // The TransactionAttributeSource should be passed the target class
        // as well as the method, which may be from an interface.
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        // Adapt to TransactionAspectSupport's invokeWithinTransaction...
        MyInvocationCallback invocationCallback = new MyInvocationCallback(invocation, transactionInfo) {
            public Object proceedWithInvocation() throws Throwable {
                // 事务开始后（拦截后的当前事务不会空）
                this.transactionInfo = currentTransactionInfo();
                // 事务切面ID
                String joinpointIdentification = this.transactionInfo.getJoinpointIdentification();
                TransactionStatus transactionStatus = this.transactionInfo.getTransactionStatus();
                TransactionAttribute transactionAttribute = this.transactionInfo.getTransactionAttribute();
                // 记录isRollbackOnly,执行业务方法抛出异常后，值会变化
                this.isRollbackOnly = transactionStatus.isRollbackOnly();
                boolean isNew = this.oldTransactionInfo == null || transactionStatus.isNewTransaction();
                // System.out.println("tx------------------------开始事务[" + isNew + "]");// false时为“重入事务”
                // 获取localNode
                InvocationStatistics invocationStatistics = null;
                InvocationNode localNode, parentNode = TxLogUtils.getInvocationNode();
                if (null == parentNode || null == (localNode = parentNode.findO(joinpointIdentification))) {
                    // parentNode为空，创建一个根节点，否则获取父节点的最后一个节点，判断ID是否相同
                    localNode = parentNode == null ? new InvocationNode() : parentNode.addChildren();
                    localNode.setId(joinpointIdentification);
                    ToStringHelper helper = MoreObjects.toStringHelper("transaction");
                    localNode.setName(helper.add("new", isNew).add("attr", transactionAttribute.toString()).toString());// // false时为“重入事务”
                    Object pStatistics = parentNode == null ? null : parentNode.getData();
                    if (null == pStatistics || pStatistics instanceof InvocationStatistics) {
                        localNode.setData(invocationStatistics = new InvocationStatistics(
                                (InvocationStatistics) pStatistics));
                    } else {
                        localNode.setData(invocationStatistics = new InvocationStatistics());
                    }
                } else {
                    invocationStatistics = (InvocationStatistics) localNode.getData();
                }
                // 记录this.invocationNode传到上层和记录localNode到下层
                TxLogUtils.setInvocationNode(this.invocationNode = localNode);
                try {
                    // 事务操作
                    long start = System.currentTimeMillis();
                    Object ret = invocation.proceed();
                    invocationStatistics.costTime(System.currentTimeMillis() - start);
                    return ret;
                } catch (Throwable throwable) {
                    // 记录异常并重新抛出
                    throw (this.throwable = throwable);
                } finally {
                    // 事务结束前
                    if (null == parentNode) {
                        // 顶层移除
                        TxLogUtils.removeInvocationNode();
                    } else {
                        // 还原invocationNode
                        TxLogUtils.setInvocationNode(parentNode);
                    }
                }
            }
        };
        try {
            return invokeWithinTransaction(invocation.getMethod(), targetClass, invocationCallback);
        } finally {
            // 事务结束后
            InvocationNode treeNode = invocationCallback.getInvocationNode();
            // TransactionInfo newTransactionInfo = invocationCallback.getTransactionInfo();
            // TransactionStatus transactionStatus = newTransactionInfo.getTransactionStatus();
            Throwable throwable;
            // 打印回滚异常
            // System.out.println("tx------------------------事务回滚");
            if ((throwable = invocationCallback.getThrowable()) != null) {
                // throwable.printStackTrace();
                // 记录异常
                InvocationNode parent = treeNode;
                do {
                    parent.setOpen(true);// 展开异常
                    if (parent.getParent() == null) {
                        break;
                    } else {
                        parent = parent.getParent();
                    }
                } while (true);
                if (false == parent.getData() instanceof Throwable) {
                    parent.setData(throwable);
                }
            }
            if (treeNode != null && treeNode.getParent() == null) {
                TxLogUtils.printSpringTxStack(treeNode);
                // System.out.println(JsonUtils.object2Json(treeNode));
            }
        }
    }

    protected abstract class MyInvocationCallback implements InvocationCallback {
        protected Throwable throwable;
        protected boolean isRollbackOnly;
        protected MethodInvocation invocation;
        protected InvocationNode invocationNode;
        protected TransactionInfo transactionInfo;
        protected TransactionInfo oldTransactionInfo;

        /**
         * @param invocation
         * @param oldTransactionInfo
         */
        public MyInvocationCallback(MethodInvocation invocation, TransactionInfo oldTransactionInfo) {
            this.invocation = invocation;
            this.oldTransactionInfo = oldTransactionInfo;
        }

        public TransactionInfo getTransactionInfo() {
            return transactionInfo;
        }

        public boolean isRollbackOnly() {
            return isRollbackOnly;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public InvocationNode getInvocationNode() {
            return invocationNode;
        }
    }

}
