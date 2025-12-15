package com.wellsoft.pt.ei.dto.mail;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/9/28 19:27
 * @Description:
 */
public class MailTag implements Serializable {
    private static final long serialVersionUID = -5444368001926081992L;

    @FieldType(desc = "主键uuid", required = true)
    private String uuid;
    @FieldType(desc = "用户id", required = true)
    private String userId;
    @FieldType(desc = "标签名称", required = true)
    private String tagName;//
    @FieldType(desc = "标签颜色")
    private String tagColor;
    @FieldType(desc = "排序号", type = ExportFieldTypeEnum.INTEGER)
    private Integer seq;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
