package com.wellsoft.pt.log.support;

import com.wellsoft.context.jdbc.support.PagingInfo;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/1/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/1/14    chenq		2019/1/14		Create
 * </pre>
 */
public class ElasticSearchSysLogParams implements Serializable {

    private static final long serialVersionUID = 3215742007505768572L;


    private Date beginTime;

    private Date endTime;

    private String logLevel;

    private String message;

    private PagingInfo page;

    private Order[] orders;


    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PagingInfo getPage() {
        return page;
    }

    public void setPage(PagingInfo page) {
        this.page = page;
    }


    public Order[] getOrders() {
        return orders;
    }

    public void setOrders(Order[] orders) {
        this.orders = orders;
    }

    public static class Order implements Serializable {

        private static final long serialVersionUID = 1248278439306144577L;

        private String property;

        private String direction;

        public boolean isAsc() {
            return "asc".equalsIgnoreCase(direction);
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }


}
