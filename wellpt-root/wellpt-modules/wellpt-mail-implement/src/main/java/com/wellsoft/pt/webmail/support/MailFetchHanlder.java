package com.wellsoft.pt.webmail.support;

import com.sun.mail.pop3.POP3Folder;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.service.WmMailUseCapacityService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoUserService;
import com.wellsoft.pt.webmail.service.WmMailboxService;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;

/**
 * Description: 邮件抓取
 *
 * @author chenq
 * @date 2018/6/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/4    chenq		2018/6/4		Create
 * </pre>
 */
@Component
public class MailFetchHanlder {

    public static final String MULTIPART_CONTENT_TYPE_PREFIX = "multipart";
    public static final String TEXT_CONTENT_TYPE_PREFIX = "text";
    public static final String APPLICATION_CONTENT_TYPE_PREFIX = "application";
    public static final String MESSAGE_CONTENT_TYPE_PREFIX = "message";
    private static final String TEST_FILE_PATH = "D:/下载区/mail";
    @Autowired
    MongoFileService mongoFileService;
    @Autowired
    WmMailboxService wmMailboxService;
    @Autowired
    WmWebmailService wmWebmailService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isDebug = false;
    private Long connectTimeout = 10000L;
    @Autowired
    private WmMailboxInfoUserService wmMailboxInfoUserService;

    @Autowired
    private WmMailUseCapacityService wmMailUseCapacityService;

    @Autowired
    private WmMailboxInfoService wmMailboxInfoService;

    private static String decodeWord(String text) {
        try {
            if (text.indexOf("=?") != -1 && text.indexOf("?=") != -1) {
                if (text.indexOf("\r") == -1) {//没有换行的情况下直接转
                    return MimeUtility.decodeText(text);
                }
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                        text.getBytes());
                BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayInputStream));
                String r = "";
                String l = null;
                int start1 = text.indexOf(63, 2);
                String charset = text.substring(2, start1);
                String encoding = text.substring(start1 + 1, text.indexOf(63, start1 + 1));
                String ce = String.format("=?%s?%s?", charset, encoding);
                boolean flag = false;
                StringBuilder decodeLine = new StringBuilder("");
                while ((l = br.readLine()) != null) {
                    if (StringUtils.isNotBlank(l)) {
                        l = l.trim();
                        if (l.startsWith(ce)) {
                            flag = true;//分行格式化后进行拼接
                            decodeLine.append(MimeUtility.decodeText(l));
                        } else {
                            flag = false;
                        }
                        r += l.substring(l.indexOf(ce) + ce.length(), l.length() - 2);
                    }
                }
                return flag ? decodeLine.toString() : MimeUtility.decodeWord(
                        String.format("%s%s?=", ce, r));
            }

        } catch (Exception e) {
        }
        return toGBK(text);
    }

    private static String toGBK(String str) {
        if (str == null) return null;
        String retStr = str;
        byte b[];
        try {
            b = str.getBytes(Charsets.ISO_8859_1);

            for (int i = 0; i < b.length; i++) {
                byte b1 = b[i];
                if (b1 == 63)
                    break;    //1
                else if (b1 > 0)
                    continue;//2
                else if (b1 < 0) {        //不可能为0，0为字符串结束符
                    retStr = new String(b,
                            "GB18030");
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
        }
        return retStr;
    }

    /**
     * 按收件起始时间开始，根据pop3协议抓取邮件内容
     *
     * @param mailUserEntity
     * @param beginTime
     * @return
     */
    public int fetchMailsUsingPop3Protocol(WmMailUserEntity mailUserEntity, Date beginTime) {
        int success = 0;
        Store store = null;
        POP3Folder folder = null;
        try {
            if (mailUserEntity != null) {
                store = getPop3Store(mailUserEntity);
                folder = getPop3Foler(store, Folder.READ_ONLY);
                Message[] messages = folder.getMessages();
                FetchProfile profile = new FetchProfile();
                profile.add(UIDFolder.FetchProfileItem.UID);
                folder.fetch(messages, profile);
                for (Message message : messages) {
                    String pid = folder.getUID(message);
                    if (wmMailboxInfoUserService.isExistMailPid(pid, mailUserEntity.getUserId(), mailUserEntity.getMailAddress())) {
                        continue;
                    }
                    Date sentDate = message.getSentDate();
                    if (beginTime != null && sentDate.before(beginTime)) {
                        continue;
                    }
                    //占用收件箱的空间
                    if (wmMailUseCapacityService.updateUseCapacity((long) message.getSize(), mailUserEntity.getUserId(), mailUserEntity.getSystemUnitId(), mailUserEntity.getMailAddress()) == 0) {
                        throw new RuntimeException("邮件空间不足");
                    }
                    WmMailboxInfo mailboxInfo = this.convert2Mailbox(message);
                    wmMailboxInfoService.save(mailboxInfo);
                    WmMailboxInfoUser infoUser = new WmMailboxInfoUser();
                    infoUser.setUserId(mailUserEntity.getUserId());
                    infoUser.setUserName(mailUserEntity.getUserName());
                    infoUser.setMailAddress(mailUserEntity.getMailAddress());
                    infoUser.setSystemUnitId(mailUserEntity.getSystemUnitId());
                    infoUser.setMailboxName(mailUserEntity.getMailAddress());
                    infoUser.setIsRead(Integer.valueOf(WmWebmailConstants.FLAG_UNREAD));
                    infoUser.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
                    infoUser.setMailInfoUuid(mailboxInfo.getUuid());
                    infoUser.setReadReceiptStatus(mailboxInfo.getReadReceiptStatus());
                    infoUser.setPid(pid);
                    wmMailboxInfoUserService.save(infoUser);
                    success++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            WmMailUtils.close(store, folder, false);
        }
        return success;
    }

    private WmMailboxInfo convert2Mailbox(Message message) throws Exception {
        WmMailboxInfo mailbox = this.mailboxBaseInfoSet(message);//设置基本邮件信息
        if (TEXT_CONTENT_TYPE_PREFIX.equals(
                new ContentType(message.getContentType()).getPrimaryType())) {//纯文本或者纯HTML
            mailbox.setContent(IOUtils.toString(message.getInputStream(),
                    getCharset(message.getContentType(), message.getContent().toString())));
        } else if (MULTIPART_CONTENT_TYPE_PREFIX.equals(
                new ContentType(message.getContentType()).getPrimaryType())) {//复合型，需要解析其中附件等
            MimeMultipart part = new MimeMultipart(message.getDataHandler().getDataSource());
            explainMessagePart(part, message, mailbox);
        }
        return mailbox;
    }

    private Charset getCharset(String contentType, String content) {
        try {
            if (StringUtils.isNotBlank(contentType)) {
                ContentType type = new ContentType(contentType);
                String charset = type.getParameter("charset");
                if (StringUtils.isBlank(charset) && StringUtils.isNotBlank(content)) {
                    byte[] bytes = content.getBytes();
                    if (bytes[0] == 63) {
                        return Charset.forName("GBK");
                    }
                }
                return StringUtils.isNotBlank(charset) ? Charset.forName(charset) : Charsets.UTF_8;
            }


        } catch (Exception e) {
            logger.error("解析邮件编码{}，异常：", contentType, e);
        }

        return Charsets.UTF_8;

    }

    private WmMailboxInfo mailboxBaseInfoSet(Message message) throws Exception {
        WmMailboxInfo box = new WmMailboxInfo();
        box.setSubject(decodeWord(message.getHeader("Subject")[0]));
        box.setMailSize((long) message.getSize());
        Address fromAddress = message.getFrom()[0];
        box.setFromMailAddress(decodeWord(WmMailUtils.extractAddress(fromAddress)));
        box.setFromUserName(decodeWord(WmMailUtils.extractUserName(fromAddress)));
        box.setSendTime(message.getSentDate());
        String[] priority = message.getHeader(WmWebmailConstants.HEADER_PRIORITY);
        String[] readReceipt = message.getHeader(WmWebmailConstants.HEADER_REQUIRE_READ_RECEIPT);
        if (ArrayUtils.isNotEmpty(priority)) {
            box.setPriority(Integer.valueOf(MimeUtility.decodeText(priority[0])));
        }
        if (ArrayUtils.isNotEmpty(readReceipt)) {
            box.setReadReceiptStatus(1);
        }
        Address[] toAddress = message.getRecipients(Message.RecipientType.TO);
        Address[] bccAddress = message.getRecipients(Message.RecipientType.BCC);
        Address[] ccAddress = message.getRecipients(Message.RecipientType.CC);
        if (ArrayUtils.isNotEmpty(toAddress)) {
            box.setToUserName("");
            box.setToMailAddress("");
            for (Address to : toAddress) {
                box.setToMailAddress(box.getToMailAddress() + (StringUtils.isNotBlank(
                        box.getToMailAddress()) ? Separator.SEMICOLON.getValue() : "") + decodeWord(
                        WmMailUtils.extractAddress(
                                to)));
                box.setToUserName(box.getToUserName() + (StringUtils.isNotBlank(
                        box.getToUserName()) ? Separator.SEMICOLON.getValue() : "") + decodeWord(
                        WmMailUtils.extractUserName(
                                to)));
            }
        }

        if (ArrayUtils.isNotEmpty(bccAddress)) {
            box.setBccUserName("");
            box.setBccMailAddress("");
            for (Address bcc : bccAddress) {
                box.setBccMailAddress(box.getBccMailAddress() + (StringUtils.isNotBlank(
                        box.getBccMailAddress()) ? Separator.SEMICOLON.getValue() : "") + decodeWord(
                        WmMailUtils.extractAddress(
                                bcc)));
                box.setBccUserName(box.getBccUserName() + (StringUtils.isNotBlank(
                        box.getBccUserName()) ? Separator.SEMICOLON.getValue() : "") + decodeWord(
                        WmMailUtils.extractUserName(
                                bcc)));
            }
        }
        if (ArrayUtils.isNotEmpty(ccAddress)) {
            box.setCcUserName("");
            box.setCcMailAddress("");
            for (Address cc : ccAddress) {
                box.setCcMailAddress(box.getCcMailAddress() + (StringUtils.isNotBlank(
                        box.getCcMailAddress()) ? Separator.SEMICOLON.getValue() : "") + WmMailUtils.extractAddress(
                        cc));
                box.setCcUserName(box.getCcUserName() + (StringUtils.isNotBlank(
                        box.getCcUserName()) ? Separator.SEMICOLON.getValue() : "") + decodeWord(
                        WmMailUtils.extractUserName(
                                cc)));
            }
        }
        box.setRepoFileUuids("");
        box.setRepoFileNames("");
        return box;
    }

    private void outputLocalFile(String content, InputStream in, int messageNumber,
                                 String fileName) {
        FileOutputStream out = null;
        try {
            File file = new File(
                    String.format("%s/%s_%s", TEST_FILE_PATH, messageNumber, fileName));
            out = new FileOutputStream(file);
            if (in != null) {
                IOUtils.copy(in, out);
            } else {
                IOUtils.write(content, out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private String title(String subject) throws Exception {
        return StringUtils.isNotBlank(subject) ? subject.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]",
                "") : "NULL";
    }


    private void explainMessagePart(MimeMultipart mimeMultipart, Message message,
                                    WmMailboxInfo mailbox) throws Exception {
        int pcnt = mimeMultipart.getCount();
        for (int p = 0; p < pcnt; p++) {
            Part part = mimeMultipart.getBodyPart(p);
            ContentType contentType = new ContentType(part.getContentType());
            String disposition = part.getDisposition();
            if (Part.ATTACHMENT.equals(disposition) || Part.INLINE.equals(
                    disposition) || APPLICATION_CONTENT_TYPE_PREFIX.equals(
                    contentType.getPrimaryType())
                    || MESSAGE_CONTENT_TYPE_PREFIX.equals(contentType.getPrimaryType())) {
                //保存附件

                String fileName = part.getFileName();
                fileName = StringUtils.isNotBlank(fileName) ? MimeUtility.decodeText(
                        fileName) : "NULL";

                //上传附件
                MongoFileEntity mongoFileEntity = mongoFileService.saveFile(fileName,
                        part.getInputStream());
                mailbox.setRepoFileUuids(
                        mailbox.getRepoFileUuids() + (StringUtils.isNotBlank(
                                mailbox.getRepoFileUuids()) ? Separator.SEMICOLON.getValue() : "") + mongoFileEntity.getFileID());
                mailbox.setRepoFileNames(
                        mailbox.getRepoFileNames() + (StringUtils.isNotBlank(
                                mailbox.getRepoFileNames()) ? Separator.SEMICOLON.getValue() : "") + mongoFileEntity.getFileID());

                if (isDebug) {
                    outputLocalFile(null, part.getInputStream(), message.getMessageNumber(),
                            title(message.getSubject()) + "_" + fileName);
                }

                continue;
            }
            if (MULTIPART_CONTENT_TYPE_PREFIX.equals(contentType.getPrimaryType())) {
                int cnt = part.getSize();
                for (int i = 0; i < cnt; i++) {
                    explainMessagePart(
                            new MimeMultipart(part.getDataHandler().getDataSource()),
                            message, mailbox);
                }

            } else if (TEXT_CONTENT_TYPE_PREFIX.equals(contentType.getPrimaryType())) {
                if (isDebug) {
                    outputLocalFile(part.getContent().toString(), null, message.getMessageNumber(),
                            title(message.getSubject()) + ".html");
                }
                //有的邮件会反馈text/plain与text/html两部分，text/html优先
                if (StringUtils.isNotBlank(mailbox.getContent())
                        && part.isMimeType("text/plain")) {
                    return;
                }
                mailbox.setContent(IOUtils.toString(part.getInputStream(),
                        getCharset(part.getContentType(), part.getContent().toString())));


            } else if ("image".equals(contentType.getPrimaryType())) {
                //邮件内容关联的图片
                String imgName = contentType.getParameter("name");
                //上传附件
                MongoFileEntity mongoFileEntity = mongoFileService.saveFile(imgName,
                        part.getInputStream());
                String mongoFilePath = "src=\"/repository/file/mongo/download?fileID=" + mongoFileEntity.getFileID() + "\"";
                mailbox.setContent(
                        mailbox.getContent().replaceAll("src=\"cid:" + imgName + "\"",
                                mongoFilePath).replaceAll(
                                "src=\"cid:" + imgName.substring(0, imgName.indexOf(".")) + "\"",
                                mongoFilePath)
                );


            }
        }

    }


    private int getTotalMailMsgCount(Folder foler) throws Exception {
        int totalCnt = foler.getMessageCount();
        System.out.println(" ######## 邮件总数：" + totalCnt + " ######## ");
        return totalCnt;
    }


    private POP3Folder getPop3Foler(Store store, int readOnly) throws Exception {
        POP3Folder folder = (POP3Folder) store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    protected Folder getImapFoler(Store store, int readOnly) throws Exception {
        return WmMailUtils.getImapFolder(store, readOnly);
    }

    protected Store getPop3Store(WmMailUserEntity userEntity) throws Exception {
        // 创建Session实例对象
        Session session = Session.getInstance(pop3Properties(userEntity));
        session.setDebug(false);
        Store store = session.getStore(
                BooleanUtils.isTrue(userEntity.getIsPopSsl()) ? "pop3s" : "pop3");
        store.connect(userEntity.getMailAddress(),
                WmMailUtils.decode(userEntity.getMailPassword()));
        return store;
    }


    private Properties pop3Properties(WmMailUserEntity userEntity) {
        String server = userEntity.getPop3Server();//"pop.qq.com"
        String port = userEntity.getPop3Port() + "";//"995";
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        boolean isSSL = BooleanUtils.isTrue(userEntity.getIsPopSsl());
        props.setProperty("mail.store.protocol", isSSL ? "pop3s" : "pop3"); // 协议
        props.setProperty(isSSL ? "mail.pop3s.port" : "mail.pop3.port",
                port); // 端口
        props.setProperty(isSSL ? "mail.pop3s.host" : "mail.pop3.host",
                server); // pop3服务器
        props.setProperty(isSSL ? "mail.pop3s.connectiontimeout" : "mail.pop3.connectiontimeout",
                connectTimeout.toString());//超时间
        return props;
    }


    private Properties imap3Properties() {
        String server = "imap.qq.com";
        String port = "993";
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps"); // 协议
        props.setProperty("mail.imaps.port",
                port); // 端口
        props.setProperty("mail.imaps.host",
                server); // pop3服务器
        props.setProperty("mail.imaps.connectiontimeout", "10000");
        return props;
    }


}