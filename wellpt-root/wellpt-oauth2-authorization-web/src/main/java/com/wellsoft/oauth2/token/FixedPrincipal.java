package com.wellsoft.oauth2.token;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/12    chenq		2019/10/12		Create
 * </pre>
 */
public class FixedPrincipal implements Serializable {

    private String username;

    private String nickname;

    private PrincipalSourceEnum from;

    public FixedPrincipal() {
    }

    public FixedPrincipal(String username, PrincipalSourceEnum from) {
        this.username = username;
        this.from = from;
    }

    public FixedPrincipal(String username, PrincipalSourceEnum from, String nickname) {
        this.username = username;
        this.from = from;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PrincipalSourceEnum getFrom() {
        return from;
    }

    public void setFrom(PrincipalSourceEnum from) {
        this.from = from;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
