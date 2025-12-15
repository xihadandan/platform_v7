package com.wellsoft.pt.repository.vo;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期        修改内容
 * 2021/9/22.1    linzc       2021/9/22     Create
 * </pre>
 */
public class PreviewResult {
    private String code;
    private String message;
    private String url;
    private String down;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
    }
}
