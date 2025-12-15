package com.wellsoft.pt.message.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author yt
 * @title: UserPerDto
 * @date 2020/5/20 8:46 下午
 */
@SuppressWarnings("serial")
public class UserPerDto implements Serializable {

    private Integer mainSwitch;

    private List<UserPerClassifyDto> userPerClassifys;

    public Integer getMainSwitch() {
        return mainSwitch;
    }

    public void setMainSwitch(Integer mainSwitch) {
        this.mainSwitch = mainSwitch;
    }

    public List<UserPerClassifyDto> getUserPerClassifys() {
        return userPerClassifys;
    }

    public void setUserPerClassifys(List<UserPerClassifyDto> userPerClassifys) {
        this.userPerClassifys = userPerClassifys;
    }
}
