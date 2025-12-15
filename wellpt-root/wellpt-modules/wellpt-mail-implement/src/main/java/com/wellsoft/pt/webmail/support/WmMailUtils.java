/*
 * @(#)2016年6月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.support;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.mail.MessageUtils;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.mail.support.MailAuthenticator;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoStatus;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;
import com.wellsoft.pt.webmail.enums.WmMailboxInfoStatusEnum;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Description: 邮件utils
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月4日.1	zhulh		2016年6月4日		Create
 * </pre>
 * @date 2016年6月4日
 */
public class WmMailUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WmMailUtils.class);

    /**
     * 如何描述该方法
     *
     * @param wmMailUser
     * @return
     */
    public static Session getSendSession(WmMailUserEntity wmMailUser) {
        String mailAddress = wmMailUser.getMailAddress();
        String mailPassword = wmMailUser.getMailPassword();
        String smtpServer = wmMailUser.getSmtpServer();
        Integer smtpPort = wmMailUser.getSmtpPort();
        boolean isEnableSSL = BooleanUtils.isTrue(wmMailUser.getIsSmtpSsl());// 是否启用SSL发送

        // 创建Session实例对象
        MailAuthenticator sa = new MailAuthenticator(mailAddress, decode(mailPassword));
        // 初始化连接邮件服务器的会话信息
        Properties properties = new Properties();
        properties.put(isEnableSSL ? "mail.smtps.host" : "mail.smtp.host", smtpServer); // smtp服务器
        properties.put(isEnableSSL ? "mail.smtps.auth" : "mail.smtp.auth", "true"); // 是否smtp认证
        properties.put(isEnableSSL ? "mail.smtps.port" : "mail.smtp.port", smtpPort); // 设置smtp端口
        properties.put(isEnableSSL ? "mail.smtps.connectiontimeout" : "mail.smtp.connectiontimeout", "3000");// 设置连接超时时间

        properties.put("mail.transport.protocol", isEnableSSL ? "smtps" : "smtp"); // 发邮件协议

        Session session = Session.getInstance(properties, sa);
        return session;
    }

    public static Transport getTransport(Session session, WmMailUserEntity wmMailUser) {
        boolean isEnableSSL = BooleanUtils.isTrue(wmMailUser.getIsSmtpSsl());// 是否启用SSL发送
        try {
            return session.getTransport(
                    new URLName(isEnableSSL ? "smtps" : "smtp",
                            wmMailUser.getSmtpServer(),
                            wmMailUser.getSmtpPort(),
                            null, wmMailUser.getMailAddress(), wmMailUser.getMailPassword()));
        } catch (Exception e) {
            LOGGER.error("获取邮件transport异常：", e);
        }
        return null;
    }

    /**
     * @param mailUser
     * @return
     * @throws NoSuchProviderException
     */
    public static Store getPop3Store(WmMailUserEntity mailUser) throws Exception {
        String mailAddress = mailUser.getMailAddress();
        String password = decode(mailUser.getMailPassword());
        String pop3Server = mailUser.getPop3Server();
        String pop3Port = mailUser.getPop3Port() + "";
        boolean isEnableSSL = BooleanUtils.isTrue(mailUser.getIsPopSsl());
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", isEnableSSL ? "pop3s" : "pop3"); // 协议
        props.setProperty(isEnableSSL ? "mail.pop3s.port" : "mail.pop3.port", pop3Port); // 端口
        props.setProperty(isEnableSSL ? "mail.pop3s.host" : "mail.pop3.host",
                pop3Server); // pop3服务器
        props.setProperty(isEnableSSL ? "mail.pop3s.connectiontimeout" : "mail.pop3.connectiontimeout", "3000");// 设置连接超时时间

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        Store store = session.getStore(isEnableSSL ? "pop3s" : "pop3");
        store.connect(mailAddress, password);
        return store;
    }

    /**
     * @param store
     * @param readWrite
     * @return
     * @throws Exception
     */
    public static POP3Folder getPop3Folder(Store store, int readWrite) throws Exception {
        // 获得收件箱
        POP3Folder folder = (POP3Folder) store.getFolder("INBOX");
        /*
         * Folder.READ_ONLY：只读权限 Folder.READ_WRITE：可读可写（可以修改邮件的状态）
         */
        folder.open(readWrite); // 打开收件箱
        return folder;
    }

    public static Store getImapStore(WmMailUserEntity mailUser) throws Exception {
        String mailAddress = mailUser.getMailAddress();
        String password = decode(mailUser.getMailPassword());
        String imapServer = mailUser.getImapServer();
        String imap3Port = mailUser.getImapPort() + "";
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap"); // 协议
        props.setProperty("mail.imap.port", imap3Port); // 端口
        props.setProperty("mail.imap.host", imapServer); // pop3服务器
        props.setProperty("mail.imap.connectiontimeout", "3000");
//        props.setProperty("mail.imap.timeout","3000");
        //props.setProperty("mail.imap.auth.plain.disable", "true");

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");
        store.connect(mailAddress,
                password/*JamesUser.hashPassword(mailUser.getMailAddress(), password, "MD5")*/);

        return store;
    }


    /**
     * @param store
     * @param readWrite
     * @return
     * @throws Exception
     */
    public static IMAPFolder getImapFolder(Store store, int readWrite) throws Exception {
        // 获得收件箱
        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");
        /*
         * Folder.READ_ONLY：只读权限 Folder.READ_WRITE：可读可写（可以修改邮件的状态）
         */
        folder.open(readWrite); // 打开收件箱
        return folder;
    }

    public static void close(Transport transport) {
        if (transport != null) {
            // 释放资源
            try {
                transport.close();
            } catch (MessagingException e) {
            }
        }
    }

    public static void close(Store store, Folder folder, boolean flag) {
        if (folder != null) {
            // 释放资源
            try {
                folder.close(flag);
            } catch (MessagingException e) {
            }
        }
        if (store != null) {
            try {
                store.close();
            } catch (MessagingException e) {
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param mailAddresses
     * @return
     */
    public static Address[] getAddresses(String mailAddresses) throws Exception {
        if (StringUtils.isBlank(mailAddresses)) {
            return new Address[0];
        }
        Set<Address> addressesSet = new LinkedHashSet<Address>();
        String[] addresses = StringUtils.split(mailAddresses, Separator.SEMICOLON.getValue());
        List<String> orgIds = new ArrayList<String>();
        for (int index = 0; index < addresses.length; index++) {
            String address = addresses[index];
            // 邮件地址
            if (address.indexOf(WmWebmailConstants.MAIL_SEPARATOR) != -1) {
                String name = extractUserNameAsString(address);
                addressesSet.add(getMailAddress(name, address));
            } /*else if (address.startsWith(MailConstant.CONTACT_BOOK_ID_PREFIX)
                    || address.startsWith(
                    MailConstant.CONTACT_BOOK_GRP_ID_PREFIX)) {// 通讯录ID或者通讯录分组ID
                addressesSet.addAll(getContactBookMailAddress(Lists.newArrayList(address)));
            }*/ else if (address.startsWith(IdPrefix.ORG_VERSION.getValue())) {
                orgIds.add(address);
            } else if (address.startsWith(IdPrefix.USER.getValue())) {
                // 用户
                orgIds.add(address);
            } else if (address.startsWith(IdPrefix.GROUP.getValue())) {
                // 群组
                orgIds.add(address);
            } else if (address.startsWith(IdPrefix.DUTY.getValue())) {
                // 职务
                orgIds.add(address);
            } else if (MultiOrgElement.isValidElementId(address)) {
                // 组织节点
                orgIds.add(address);
            } else {
                //扩展通讯录的节点地址解析
                List<Address> extendsAddress = ApplicationContextHolder.getBean(
                        ExtendsMailContactBookHolder.class).getMailAddress(address);
                if (CollectionUtils.isEmpty(extendsAddress)) {
                    throw new RuntimeException("无效的邮件地址[" + address + "]");
                }
                addressesSet.addAll(extendsAddress);
            }
        }
        addressesSet.addAll(getUserAddressesSet(orgIds));
        return addressesSet.toArray(new Address[0]);
    }


    /**
     * 如何描述该方法
     *
     * @throws AddressException
     */
    private static List<Address> getUserAddressesSet(List<String> orgIds) throws Exception {
        List<Address> addresses = new ArrayList<Address>();
        WmMailUserService wmMailUserService = ApplicationContextHolder.getBean(
                WmMailUserService.class);
        List<WmMailUserEntity> mailAddresses = wmMailUserService.getMailAddressByOrgIds(orgIds);
        if (CollectionUtils.isNotEmpty(mailAddresses)) {
            for (WmMailUserEntity mailAddress : mailAddresses) {
                if (!mailAddress.getIsInnerUser()) {// 不包括绑定的外部其他邮箱账号
                    continue;
                }
                addresses
                        .addAll(Arrays.asList(getMailAddress(mailAddress.getUserName(),
                                mailAddress.getMailAddress())));
            }
        }
        return addresses;
    }

    /**
     * 如何描述该方法
     *
     * @return
     * @throws AddressException
     */
    public static Address getMailAddress(String userName, String mailAddress) throws Exception {
        String tmpAddress = mailAddress;
        if (tmpAddress.indexOf("<") != -1 && tmpAddress.endsWith(">")) {
            int start = tmpAddress.indexOf("<") + 1;
            if (tmpAddress.indexOf("><") != -1) {
                // 旧数据问题处理
                int end = tmpAddress.indexOf("><");
                tmpAddress = tmpAddress.substring(start, end);
            } else {
                tmpAddress = tmpAddress.substring(start, tmpAddress.length() - 1);
            }
        }
        return new InternetAddress(MimeUtility.encodeText(userName) + " <" + tmpAddress + ">");
    }


    /**
     * @param wmMailbox
     * @return
     * @throws MessagingException
     */
    public static Multipart getContent(WmMailboxInfo wmMailbox) throws Exception {
        if (hasAttachments(wmMailbox)) {
            return getMultiContent(wmMailbox);
        }
        return getHtmlContent(wmMailbox);
    }

    /**
     * 加密
     *
     * @param pass
     * @return
     */
    public static String encode(String pass) {
        byte[] t = new StringBuilder(pass).reverse().toString().getBytes();
        return (new BASE64Encoder()).encode(t);
    }

    /**
     * 解密
     *
     * @param pass
     * @return
     */
    public static String decode(String pass) {
        try {
            StringBuilder tBuilder = new StringBuilder(
                    new String(new BASE64Decoder().decodeBuffer(pass)));
            return new String(tBuilder.reverse());
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 如何描述该方法
     *
     * @param wmMailbox
     * @return
     */
    public static boolean hasAttachments(WmMailboxInfo wmMailbox) {
        return StringUtils.isNotBlank(wmMailbox.getRepoFileUuids());
    }

    /**
     * 如何描述该方法
     *
     * @param wmMailbox
     * @return
     */
    public static Multipart getMultiContent(WmMailboxInfo wmMailbox) throws Exception {
        MimeMultipart mailContent = new MimeMultipart("mixed");

        // 1、内容
        MimeBodyPart contentPart = new MimeBodyPart();
        MimeMultipart contentMultipart = new MimeMultipart("related");

        // HTML内容
        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(wmMailbox.getContent(), "text/html; charset=utf-8");
        contentMultipart.addBodyPart(htmlBodyPart);
        contentPart.setContent(contentMultipart);
        mailContent.addBodyPart(contentPart);

        // 2、附件
        // String repoFileNames = wmMailbox.getRepoFileNames();
        String repoFileUuids = wmMailbox.getRepoFileUuids();
        if (StringUtils.isNotBlank(repoFileUuids)) {
            MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                    MongoFileService.class);
            // String[] fileNames = StringUtils.split(repoFileNames,
            // Separator.SEMICOLON.getValue());
            String[] fileUuids = StringUtils.split(repoFileUuids, Separator.SEMICOLON.getValue());
            for (int index = 0; index < fileUuids.length; index++) {
                String fileId = fileUuids[index];
                // String fileName = fileNames[index];
                MongoFileEntity fileEntity = mongoFileService.getFile(fileId);
                MimeBodyPart filePart = new MimeBodyPart();
                DataHandler dh = new DataHandler(new MongoFileDataSource(fileEntity));
                filePart.setFileName(MimeUtility.encodeText(fileEntity.getFileName()));
                filePart.setDataHandler(dh);
                filePart.setHeader(WmWebmailConstants.HEADER_FILE_ID, fileId);
                mailContent.addBodyPart(filePart);
            }
        }
        return mailContent;
    }

    /**
     * 如何描述该方法
     *
     * @param wmMailbox
     * @return
     */
    public static Multipart getHtmlContent(WmMailboxInfo wmMailbox) throws Exception {
        MimeMultipart contentMultipart = new MimeMultipart("related");
        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(wmMailbox.getContent(), "text/html; charset=utf-8");
        contentMultipart.addBodyPart(htmlBodyPart);
        return contentMultipart;
    }

    /**
     * 如何描述该方法
     *
     * @param folder
     * @param message
     * @return
     * @throws MessagingException
     * @throws Exception
     */
    public static WmMailboxInfo message2MailboxInfo(Message message) throws Exception {
        // 发送人名称
        String fromUserName = getMailReceiveUserName(message.getFrom());
        // 邮箱地址
        String fromMailAddress = getMailReceiveAddress(message.getFrom());
        // 接收人名称，多个以分号隔开
        String toUserName = getMailReceiveUserName(message.getRecipients(RecipientType.TO));
        // 接收人邮箱地址/部门、职位、用户、群组id
        String toMailAddress = getMailReceiveAddress(message.getRecipients(RecipientType.TO));
        // 抄送人名称，多个以分号隔开
        String ccUserName = getMailReceiveUserName(message.getRecipients(RecipientType.CC));
        // 抄送人邮箱地址/部门、职位、用户、群组id
        String ccMailAddress = getMailReceiveAddress(message.getRecipients(RecipientType.CC));
        // 密送人名称，多个以分号隔开
        String bccUserName = getMailReceiveUserName(message.getRecipients(RecipientType.BCC));
        // 密送人邮箱地址/部门、职位、用户、群组id
        String bccMailAddress = getMailReceiveAddress(message.getRecipients(RecipientType.BCC));
        // 主题
        String subject = message.getSubject();
        // 发送时间
        Date sendTime = message.getSentDate();
        // 邮件大小
        Integer mailSize = message.getSize();
        // 邮件文本内容
        StringBuilder sb = new StringBuilder();
        MessageUtils.fetchMessageContentText(message, sb);
        String content = sb.toString();
        // mogodb附件名称，多个以分隔开
        List<MongoFileEntity> mongoFileEntities = getAttachments(message);
        String repoFileNames = getRepoFileNames(mongoFileEntities);
        // mogodb附件UUID，多个以分隔开
        String repoFileUuids = getRepoFileUuids(mongoFileEntities);

        String[] priority = message.getHeader(WmWebmailConstants.HEADER_PRIORITY);
        String[] readReceipt = message.getHeader(WmWebmailConstants.HEADER_REQUIRE_READ_RECEIPT);

        WmMailboxInfo wmMailbox = new WmMailboxInfo();
        wmMailbox.setFromUserName(fromUserName);
        wmMailbox.setFromMailAddress(fromMailAddress);
        wmMailbox.setToUserName(toUserName);
        wmMailbox.setToMailAddress(toMailAddress);
        wmMailbox.setCcUserName(ccUserName);
        wmMailbox.setCcMailAddress(ccMailAddress);
        wmMailbox.setBccUserName(bccUserName);
        wmMailbox.setBccMailAddress(bccMailAddress);

        wmMailbox.setSubject(subject);
        wmMailbox.setSendTime(sendTime);
        wmMailbox.setMailSize((long) mailSize);
        wmMailbox.setContent(content);
        wmMailbox.setRepoFileNames(repoFileNames);
        wmMailbox.setRepoFileUuids(repoFileUuids);

        if (ArrayUtils.isNotEmpty(priority)) {
            wmMailbox.setPriority(Integer.valueOf(MimeUtility.decodeText(priority[0])));
        }
        if (ArrayUtils.isNotEmpty(readReceipt)) {
            wmMailbox.setReadReceiptStatus(1);
        }
        return wmMailbox;
    }

    /**
     * 获取UID
     *
     * @param folder
     * @param message
     * @return
     * @throws MessagingException
     */
    public static String getUID(Folder folder, Message message) throws MessagingException {
        if (folder instanceof IMAPFolder) {
            return String.valueOf(((IMAPFolder) folder).getUID(message));
        } else {
            return ((POP3Folder) folder).getUID(message);
        }
    }

    /**
     * @param message
     * @return
     * @throws MessagingException
     */
    private static String getMailReceiveUserName(Address[] addresses) throws Exception {
        if (addresses == null) {
            return null;
        }
        List<String> userNames = new ArrayList<String>();
        for (Address address : addresses) {
            userNames.add(extractUserName(address));
        }
        return StringUtils.join(userNames, Separator.SEMICOLON.getValue());
    }

    /**
     * @param message
     * @return
     * @throws MessagingException
     */
    private static String getMailReceiveAddress(Address[] addresses) throws MessagingException {
        if (addresses == null) {
            return null;
        }
        List<String> mailAddresses = new ArrayList<String>();
        for (Address address : addresses) {
            mailAddresses.add(extractAddress(address));
        }
        return StringUtils.join(mailAddresses, Separator.SEMICOLON.getValue());
    }

    /**
     * 从邮件地址提取用户名
     *
     * @param address
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String extractUserName(Address address) {
        String addr = address.toString();
        if (addr.startsWith("\"")) {
            addr = addr.replaceAll("\"", "");
        }
        if (addr.indexOf("<") != -1 && addr.endsWith(">")) {
            int end = addr.indexOf("<");
            try {
                return MimeUtility.decodeText(StringUtils.trim(addr.substring(0, end)));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return addr;
    }

    public static String extractUserName(String address) {
        String userName = null;
        if (address.indexOf("<") != -1 && address.endsWith(">")) {
            int end = address.indexOf("<");
            userName = StringUtils.trim(address.substring(0, end));
        } else {
            int end = address.indexOf(WmWebmailConstants.MAIL_SEPARATOR);
            userName = StringUtils.trim(address.substring(0, end));
        }
        return userName;
    }

    /**
     * @param address
     * @return
     */
    public static String extractAddress(Address address) {
        String addr = address.toString();
        if (addr.startsWith("\"")) {
            addr = addr.replaceAll("\"", "");
        }
        if (addr.indexOf("<") != -1 && addr.endsWith(">")) {
            int start = addr.indexOf("<") + 1;
            return addr.substring(start, addr.length() - 1);
        }
        return addr;
    }

    public static String extractAddress(String address) {
        String addr = address;
        if (addr.startsWith("\"")) {
            addr = addr.replaceAll("\"", "");
        }
        if (addr.indexOf("<") != -1 && addr.endsWith(">")) {
            int start = addr.indexOf("<") + 1;
            return addr.substring(start, addr.length() - 1);
        }
        return addr;
    }

    public static Address getAddress(String address) throws Exception {
        String userName = null;
        if (address.indexOf("<") != -1 && address.endsWith(">")) {
            int end = address.indexOf("<");
            userName = StringUtils.trim(address.substring(0, end));
        } else {
            int end = address.indexOf(WmWebmailConstants.MAIL_SEPARATOR);
            userName = StringUtils.trim(address.substring(0, end));
        }
        return getMailAddress(userName, address);
    }

    /**
     * @param mailAddress
     * @return
     */
    public static String extractUserNameAsString(String mailAddress) {
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WmMailUserService wmMailUserService = ApplicationContextHolder.getBean(
                WmMailUserService.class);
        List<String> addresses = Arrays.asList(
                StringUtils.split(mailAddress, Separator.SEMICOLON.getValue()));
        List<String> userNames = new ArrayList<String>();
        for (int index = 0; index < addresses.size(); index++) {
            String address = addresses.get(index);
            // 收件人本身就是个邮件地址
            if (address.indexOf(WmWebmailConstants.MAIL_SEPARATOR) != -1) {
                if (address.indexOf("<") != -1 && address.endsWith(">")) {
                    int end = address.indexOf("<");
                    userNames.add(StringUtils.trim(address.substring(0, end)));
                } else {
                    WmMailUserEntity example = new WmMailUserEntity();
                    example.setMailAddress(mailAddress);
                    List<WmMailUserEntity> wmMailUsers = wmMailUserService.findByExample(example);
                    if (wmMailUsers.isEmpty()) {
                        int end = address.indexOf(WmWebmailConstants.MAIL_SEPARATOR);
                        userNames.add(StringUtils.trim(address.substring(0, end)));
                    } else {
                        userNames.add(wmMailUsers.get(0).getUserName());
                    }
                }
                // 收件人是对应的多版本组织元素
            } else if (address.startsWith(IdPrefix.ORG_VERSION.getValue())) { // 多版本组织
                String[] idAndVersion = address.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                String eleId = idAndVersion[1];
                String name = orgApiFacade.getNameByOrgEleId(eleId);
                userNames.add(name);
            } else if (address.startsWith(IdPrefix.JOB.getValue())
                    || address.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                // 职位
                MultiOrgElement orgElement = orgApiFacade.getOrgElementById(address);
                if (orgElement == null) {
                    userNames.add(address);
                } else {
                    userNames.add(orgElement.getName());
                }
            } else if (address.startsWith(IdPrefix.USER.getValue())) {
                // 用户
                String user = orgApiFacade.getUserNameById(address);
                if (user == null) {
                    userNames.add(address);
                } else {
                    userNames.add(user);
                }
            } else {
                throw new RuntimeException("无效的邮件地址[" + address + "]!");
            }
        }
        return StringUtils.join(userNames, Separator.SEMICOLON.getValue());
    }

    /**
     * @param repoFileUuids
     * @return
     */
    public static String extractFileNameAsString(String fileIds) {
        MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                MongoFileService.class);
        String[] ids = StringUtils.split(fileIds, Separator.SEMICOLON.getValue());
        List<String> fileNames = new ArrayList<String>();
        for (String fileId : ids) {
            MongoFileEntity fileEntity = mongoFileService.getFile(fileId);
            if (fileEntity == null) {
                fileNames.add(fileId);
            } else {
                fileNames.add(fileEntity.getFileName());
            }
        }
        return StringUtils.join(fileNames, Separator.SEMICOLON.getValue());
    }

    /**
     * 获取邮件附件
     *
     * @param msg
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static List<MongoFileEntity> getAttachments(
            Message msg) throws MessagingException, IOException {
        MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                MongoFileService.class);
        List<BodyPart> attachments = MessageUtils.getMessageAttachment(msg.getContent());
        Iterator<BodyPart> iterator = attachments.iterator();
        List<MongoFileEntity> mongoFileEntities = new ArrayList<MongoFileEntity>();
        while (iterator.hasNext()) {
            BodyPart bodyPart = iterator.next();
            if (bodyPart != null) {
                String[] values = bodyPart.getHeader(WmWebmailConstants.HEADER_FILE_ID);
                if (values != null) {
                    MongoFileEntity mongoFileEntity = mongoFileService.getFile(values[0]);
                    if (mongoFileEntity != null) {
                        mongoFileEntities.add(mongoFileEntity);
                        continue;
                    }
                }
                MongoFileEntity mongoFileEntity = getMongoFileByDataHandler(mongoFileService, bodyPart);
                if (mongoFileEntity != null) {
                    mongoFileEntities.add(mongoFileEntity);
                }
            }
        }
        return mongoFileEntities;
    }

    private static MongoFileEntity getMongoFileByDataHandler(MongoFileService mongoFileService, BodyPart bodyPart) throws MessagingException, IOException {
        String filename = MessageUtils.decodeText(bodyPart.getFileName());
        // String fileID = UUID.randomUUID().toString();
        InputStream is = null;
        try {
            is = bodyPart.getDataHandler().getInputStream();
            String[] fileid = bodyPart.getHeader(WmWebmailConstants.HEADER_FILE_ID);
            MongoFileEntity mongoFileEntity = null;
            if (ArrayUtils.isNotEmpty(fileid)) {
                mongoFileEntity = mongoFileService.saveFile(fileid[0], filename, is);
            } else if (StringUtils.isNotBlank(filename)) {
                mongoFileEntity = mongoFileService.saveFile(filename, is);
            }
            if (mongoFileEntity == null) {
                return null;
            }
            String fileID = mongoFileEntity.getFileID();
            mongoFileService.pushFileToFolder(fileID, fileID, null);
            return mongoFileEntity;
        } catch (IOException e) {
            throw e;
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
        }
    }


    /**
     * @param mongoFileEntities
     * @return
     */
    private static String getRepoFileNames(List<MongoFileEntity> mongoFileEntities) {
        List<String> fileNames = new ArrayList<String>();
        for (MongoFileEntity mongoFileEntity : mongoFileEntities) {
            fileNames.add(mongoFileEntity.getFileName());
        }
        return StringUtils.join(fileNames, Separator.SEMICOLON.getValue());
    }

    /**
     * @param mongoFileEntities
     * @return
     */
    private static String getRepoFileUuids(List<MongoFileEntity> mongoFileEntities) {
        List<String> fileUuids = new ArrayList<String>();
        for (MongoFileEntity mongoFileEntity : mongoFileEntities) {
            fileUuids.add(mongoFileEntity.getFileID());
        }
        return StringUtils.join(fileUuids, Separator.SEMICOLON.getValue());
    }

    public static MimeMessage copyMessage(MimeMessage src, Session session) throws Exception {
        MimeMessage message = new MimeMessage(session);
        // 设置主题
        message.setSubject(src.getSubject());
        // 设置发送人
        message.setFrom(src.getFrom()[0]);
        // 设置收件人
        message.setRecipients(RecipientType.TO, src.getRecipients(RecipientType.TO));
        // 设置抄送人
        message.setRecipients(RecipientType.CC, src.getRecipients(RecipientType.CC));
        // 设置密送人
        message.setRecipients(RecipientType.BCC, src.getRecipients(RecipientType.BCC));
        // 设置发送时间
        message.setSentDate(src.getSentDate());
        // 设置回复人(收件人回复此邮件时,默认收件人)
        message.setReplyTo(src.getReplyTo());
        // 设置优先级(1:紧急 3:普通 5:低)
        if (ArrayUtils.isNotEmpty(src.getHeader(WmWebmailConstants.HEADER_PRIORITY))) {
            message.setHeader(WmWebmailConstants.HEADER_PRIORITY,
                    src.getHeader(WmWebmailConstants.HEADER_PRIORITY)[0]);
        }
        // 设置来源信id
        if (ArrayUtils.isNotEmpty(src.getHeader(WmWebmailConstants.HEADER_FROM_MAIL_ID))) {
            message.setHeader(WmWebmailConstants.HEADER_FROM_MAIL_ID,
                    src.getHeader(WmWebmailConstants.HEADER_FROM_MAIL_ID)[0]);
        }
        // 要求阅读回执(收件人阅读邮件时会提示回复发件人,表明邮件已收到,并已阅读)
        if (ArrayUtils.isNotEmpty(src.getHeader(WmWebmailConstants.HEADER_REQUIRE_READ_RECEIPT))) {
            message.setHeader(WmWebmailConstants.HEADER_REQUIRE_READ_RECEIPT,
                    src.getHeader(WmWebmailConstants.HEADER_REQUIRE_READ_RECEIPT)[0]);
        }
        // 设置内容
        message.setContent((Multipart) src.getContent());
        // 保存邮件内容修改
        message.saveChanges();
        return message;
    }

    /**
     * 失败提醒邮件
     *
     * @param wmMailboxInfoUser
     * @param orginalMail
     * @param infoStatusList
     * @return
     */
    public static String failureReport(WmMailboxInfoUser wmMailboxInfoUser, WmMailboxInfo orginalMail, List<WmMailboxInfoStatus> infoStatusList) {
        List<Integer> statusError = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        List<String> statusStrError = new ArrayList<>();
        if (wmMailboxInfoUser.getSendStatus() == 2) {
            nameList.add("");
            statusStrError.add(wmMailboxInfoUser.getFailMsg());
        } else {
            for (WmMailboxInfoStatus infoStatus : infoStatusList) {
                if (infoStatus.getStatus() > WmMailboxInfoStatusEnum.PostedToMailboxService.ordinal()) {
                    if (!statusError.contains(infoStatus.getStatus())) {
                        statusError.add(infoStatus.getStatus());
                    }
                    if (StringUtils.isNotBlank(infoStatus.getMailName()) && !nameList.contains(infoStatus.getMailName())) {
                        nameList.add(infoStatus.getMailName());
                    }
                }
            }
            //3：地址不存在，4：未开启公网邮箱，5：无效邮件地址，6：邮件服务异常
            for (Integer status : statusError) {
                statusStrError.add(WmMailboxInfoStatusEnum.getName(status));
            }
        }
        StringBuilder sb = failContent(StringUtils.join(nameList, Separator.SEMICOLON.getValue()), StringUtils.join(statusStrError, Separator.SEMICOLON.getValue()), wmMailboxInfoUser, orginalMail);
        return sb.toString();
    }

    private static StringBuilder failContent(String toUserName, String errorMsg, WmMailboxInfoUser wmMailboxInfoUser, WmMailboxInfo orginalMail) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br>");
        sb.append("<div style=\"font-size: 12px;font-family: Arial Narrow;padding:2px 0 2px 0;\">------------------&nbsp;投递失败报告&nbsp;------------------</div>");
        sb.append(
                "<div style=\"font-size: 12px;background:#efefef;padding:8px;margin-bottom:20px;\">");
        sb.append("<div><b>主题:</b>&nbsp;" + (StringUtils.isNotEmpty(orginalMail.getSubject()) ? orginalMail.getSubject() : "") + "</div>");
        sb.append("<div><b>收件人:</b>&nbsp;" + (StringUtils.isNotEmpty(toUserName) ? toUserName : "") + "</div>");
        sb.append("<div><b>发送时间:</b>&nbsp;"
                + (orginalMail.getSendTime() != null ? DateFormatUtils.format(
                orginalMail.getSendTime(), "yyyy年MM月dd日(E) ahh:mm") : "") + "</div>");
        sb.append("<div><b>失败原因:</b>&nbsp;" + (StringUtils.isNotEmpty(errorMsg) ? errorMsg : "") + "</div>");
        sb.append("<div><b>原始邮件:</b>&nbsp;<a href=\"/web/app/pt-app/pt-webmail/pt-webmail-openmail.html?pageUuid=9a6037a5-52fe-4d82-86c0-1d068a6c0b51&mailboxUuid=" + wmMailboxInfoUser.getUuid() + "\">" + orginalMail.getSubject() + "</a></div>");
        sb.append("<div><b>投递失败原因说明</b></div>");
        sb.append("<div><p>(1) 收件人地址不存在：收件人地址或格式错误，导致邮件无法送达，需要核实收件地址。</p>");
        sb.append("<p>(2) 未开启公网邮箱：系统未开启公网邮箱，请联系管理员处理。</p>");
        sb.append("<p>(3) 无效邮件地址：收件人的邮箱地址是否真实存在，是否邮箱处于被冻结与被禁用的状态等。</p>");
        sb.append("<p>(4) 邮件服务异常：请联系管理员处理</span></p>");
        sb.append("<p>(5) 未找到具体收件人：选择的收件人节点经解析处理后，未找到具体收件人</span></p>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append(wrapContent(wmMailboxInfoUser, orginalMail));
        return sb;
    }

    /**
     * 如何描述该方法
     *
     * @param content
     * @return
     */
    public static String wrapContent(WmMailboxInfoUser wmMailboxInfoUser, WmMailboxInfo orginalMail) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br>");
        sb.append(
                "<div style=\"font-size: 12px;font-family: Arial Narrow;padding:2px 0 2px 0;\">------------------&nbsp;原始邮件&nbsp;------------------</div>");
        sb.append(
                "<div style=\"font-size: 12px;background:#efefef;padding:8px;margin-bottom:20px;\">");
        sb.append("<div><b>发件人:</b>&nbsp;" + (StringUtils.isNotEmpty(
                orginalMail.getFromUserName()) ? orginalMail.getFromUserName() : "") + "</div>");
        sb.append("<div><b>发送时间:</b>&nbsp;"
                + (orginalMail.getSendTime() != null ? DateFormatUtils.format(
                orginalMail.getSendTime(), "yyyy年MM月dd日(E) ahh:mm") : "") + "</div>");
        sb.append("<div><b>收件人:</b>&nbsp;" + (StringUtils.isNotEmpty(
                orginalMail.getToUserName()) ? orginalMail.getToUserName() : "") + "</div>");
        if (StringUtils.isNotEmpty(orginalMail.getCcUserName())) {
            sb.append("<div><b>抄送:</b>&nbsp;" + orginalMail.getCcUserName() + "</div>");
        }
        sb.append("<div><b>主题:</b>&nbsp;" + (StringUtils.isNotEmpty(
                orginalMail.getSubject()) ? orginalMail.getSubject() : "") + "</div>");
        sb.append("</div>");
        if (wmMailboxInfoUser.getRevokeStatus() != null && wmMailboxInfoUser.getRevokeStatus() == 1) {
            sb.append("该邮件已经被发送者撤回");
        } else {
            sb.append(orginalMail.getContent());
        }
        return sb.toString();
    }
}
