package com.wellsoft.context.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮件消息处理类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月23日.1	zhulh		2015年12月23日		Create
 * </pre>
 * @date 2015年12月23日
 */
public class MessageUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);

    /**
     * 解码文本信息
     *
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decodeText(String text) throws UnsupportedEncodingException {
        if (text != null && (text.indexOf("=?") != -1)) {
            text = MimeUtility.decodeText(text);
        }
        return text;
    }

    /**
     * 获取邮件附件列表体
     *
     * @param content
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static List<BodyPart> getMessageAttachment(Object content) throws MessagingException, IOException {
        // System.setProperty(
        // "mail.mime.multipart.ignoreexistingboundaryparameter", "true");
        // List<BodyPart> bodyParts = new ArrayList<BodyPart>();
        // if (content instanceof String) {
        // return bodyParts;
        // }
        // Part part = null;
        // if (content instanceof Part) {
        // part = (Part) content;
        // if (part.isMimeType("multipart/*")) {
        // Multipart multipart = (Multipart) part.getContent();
        // for (int index = 0; index < multipart.getCount(); index++) {
        // BodyPart bodyPart = multipart.getBodyPart(index);
        // String dp = bodyPart.getDisposition();
        // if (Part.ATTACHMENT.equals(dp) || Part.INLINE.equals(dp)) {
        // bodyParts.add(bodyPart);
        // }
        // }
        // } else if (part.isMimeType("message/rfc822")) {
        // bodyParts.addAll(getMessageAttachment(part.getContent()));
        // }
        // }

        List<BodyPart> bodyParts = new ArrayList<BodyPart>();
        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int index = 0; index < multipart.getCount(); index++) {
                BodyPart bodyPart = multipart.getBodyPart(index);
                String dp = bodyPart.getDisposition();
                if (Part.ATTACHMENT.equals(dp) || Part.INLINE.equals(dp)) {
                    bodyParts.add(bodyPart);
                } else if (bodyPart.isMimeType("multipart/*")) {
                    bodyParts.addAll(getMessageAttachment(bodyPart.getContent()));
                } else if (bodyPart.isMimeType("message/rfc822")) {
                    bodyParts.addAll(getMessageAttachment(bodyPart.getContent()));
                } else if (bodyPart.isMimeType("image/*")) {
                    bodyParts.add(bodyPart);
                } else if (bodyPart.isMimeType("application/*")) {
                    bodyParts.add(bodyPart);
                }
            }
        }

        return bodyParts;
    }

    /**
     * 获取邮件文件内容
     *
     * @param content
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    @Deprecated
    public static String getMessageContentText(Object content) throws IOException, MessagingException {
        // if (content instanceof Part) {
        // return fetchContentText((Part) content);
        // }
        String contentText = "";
        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    contentText = bodyPart.getContent().toString();
                } else if (bodyPart.isMimeType("text/html")) {
                    contentText = bodyPart.getContent().toString();
                } else if (bodyPart.isMimeType("multipart/*")) {
                    contentText = getMessageContentText(bodyPart.getContent());
                } else if (bodyPart.isMimeType("message/rfc822")) {
                    contentText = getMessageContentText(bodyPart.getContent());
                }
            }
        } else {
            contentText = content.toString();
        }

        return contentText;
    }

    /**
     * 提取邮件文件内容到StringBuilder
     *
     * @param part
     * @param content
     * @throws MessagingException
     * @throws IOException
     */
    public static void fetchMessageContentText(Part part, StringBuilder content) throws MessagingException, IOException {
        // 如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/html") && !isContainTextAttach) {
            content.append(converErrorMsg(part.getContent().toString()));
        }
        if (part.isMimeType("text/plain") && !isContainTextAttach) {
            content.append(converErrorMsg(part.getContent().toString()));
        } else if (part.isMimeType("message/rfc822")) {
            fetchMessageContentText((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            //邮件正文若包含 text/plain和text/html,则排除第一个text/plain
            if (partCount == 2) {
                if (multipart.getBodyPart(0).isMimeType("text/plain")) {
                    multipart.removeBodyPart(0);
                    partCount = multipart.getCount();
                }
            }
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                fetchMessageContentText(bodyPart, content);
            }
        }
    }

    public static String converErrorMsg(String content) {
        content = content.replaceFirst("Hi. This is the James mail server at (.*).\nI'm afraid I wasn't able to deliver your message to the following addresses.\nThis is a permanent error; I've given up. Sorry it didn't work out.  Below\nI include the list of recipients and the reason why I was unable to deliver\nyour message.",
                "抱歉：您的邮件投递失败，请检查收件地址是否有误；或联系系统管理员处理");
        content = content.replaceFirst("\n\nFailed recipient\\(s\\):", "\n\n投递失败邮件地址：");
        content = content.replaceFirst("\n\nError message:\n(.*)\n\n", "\n\n原邮件内容\n\n");
        return content;
    }

    /**
     * 提取邮件Part内容
     *
     * @param p
     * @return
     */
    public static String fetchContentText(Part p) {
        String text = "";
        if (p == null)
            return text;

        try {
            if (!p.isMimeType("text/rfc822-headers") && p.isMimeType("text/*")) {
                try {
                    Object pContent;
                    try {
                        pContent = p.getContent();

                    } catch (UnsupportedEncodingException e) {
                        LOG.error(e.getMessage(), e);
                        pContent = "Message has an illegal encoding. " + e.getLocalizedMessage();
                    }
                    if (pContent != null) {
                        text = pContent.toString();
                    } else {
                        text = "Illegal content";
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            } else if (p.isMimeType("multipart/*")) {
                try {
                    Multipart mp = (Multipart) p.getContent();
                    int count = mp.getCount();
                    for (int i = 0; i < count; i++) {
                        text = fetchContentText(mp.getBodyPart(i));
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            } else if (p.isMimeType("message/rfc822")) {
                text = fetchContentText((Part) p.getContent());
            } else {
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return text;
    }

    @SuppressWarnings("rawtypes")
    @Deprecated
    public ArrayList fetchParts(Part p, ArrayList parts) throws Exception {
        if (p == null)
            return null;

        if (!p.isMimeType("text/rfc822-headers") && p.isMimeType("text/*")) {
            try {
                Object pContent = null;
                try {
                    pContent = p.getContent();
                } catch (UnsupportedEncodingException e) {
                    LOG.error(e.getMessage(), e);
                    pContent = "Message has an illegal encoding. " + e.getLocalizedMessage();
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        } else if (p.isMimeType("multipart/*")) {
            try {
                Multipart mp = (Multipart) p.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++) {
                    fetchParts(mp.getBodyPart(i), parts);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        } else if (p.isMimeType("message/rfc822")) {
            fetchParts((Part) p.getContent(), parts);
        } else {
            try {
                // attatch
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return parts;
    }

}
