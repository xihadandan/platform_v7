package com.wellsoft.pt.ureport.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/25    chenq		2018/9/25		Create
 * </pre>
 */
public class DemoUserDto implements Serializable {


    private String name;

    private String degree;

    private BigDecimal salary;

    private String dept;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
