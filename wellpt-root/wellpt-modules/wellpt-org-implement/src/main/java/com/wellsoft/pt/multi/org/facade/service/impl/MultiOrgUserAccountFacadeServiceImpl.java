/*
 * @(#)2017年11月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.wellsoft.context.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.context.authentication.encoding.PasswordEncoderFactory;
import com.wellsoft.context.config.SystemParamsUtils;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.excel.ExcelUtils;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.OrgUserDto;
import com.wellsoft.pt.multi.org.bean.OrgUserTableVo;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.dto.EncryptChangePasswordByIdDto;
import com.wellsoft.pt.multi.org.dto.MultiOrgPwdSettingDto;
import com.wellsoft.pt.multi.org.dto.NewAccountDto;
import com.wellsoft.pt.multi.org.dto.PwdErrorDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.enums.IsUserSettingPwdEnum;
import com.wellsoft.pt.multi.org.enums.PwdErrorLockEnum;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgPwdSettingFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.multi.org.utils.ExportAccountUtils;
import com.wellsoft.pt.multi.org.vo.MultiUserRelationAccountVo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;
import com.wellsoft.pt.security.config.service.AppLoginPageConfigService;
import com.wellsoft.pt.security.config.service.MultiUserLoginSettingsService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.service.UserAccountService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.BasePasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月22日.1	zyguo		2017年11月22日		Create
 * </pre>
 * @date 2017年11月22日
 */
@Service("multiOrgUserAccountFacadeService")
public class MultiOrgUserAccountFacadeServiceImpl implements MultiOrgUserAccountFacadeService {
    public static final String DEFAULT_PWD = "0"; // 默认密码MultiOrgUserAccount
    private static final String USER_ID_PATTERN = IdPrefix.USER.getValue() + "0000000000";
    private static final String SUPERADMIN_ID = "admin";
    // 缓存用户关联账户信息<在线用户ID,离线关联用户ID列表>
    public static ConcurrentMap<String, Set<String>> userRelationAccountMap = new ConcurrentHashMap<>();
    public static String ENABLE = "enable";// 开启一人多岗
    @Autowired
    MultiUserLoginSettingsService multiUserLoginSettingsService;
    @Autowired
    MultiUserRelationAccountService multiUserRelationAccountService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IdGeneratorService idGeneratorService;
    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;
    @Autowired
    private MultiOrgUserInfoService multiOrgUserInfoService;
    @Autowired
    private MultiOrgUsrExtAccountService multiOrgUsrExtAccountService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private MultiOrgPwdSettingFacadeService multiOrgPwdSettingFacadeService;
    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private AppLoginPageConfigService appLoginPageConfigService;

    @Autowired
    private MultiOrgSystemUnitService multiOrgSystemUnitService;

    public static String createMd5LoginName(String loginName) {
        String lowerCaseName = loginName.toLowerCase();
        BasePasswordEncoder pwdEncoder = PasswordEncoderFactory.getPasswordEncoder(PasswordAlgorithm.MD5.getValue());
        String md5Str = pwdEncoder.encodePassword(lowerCaseName, lowerCaseName);
        return md5Str;
    }

    public static String createMd5Password(String loginName, String pwd) {
        String lowerCaseName = loginName.toLowerCase();
        BasePasswordEncoder pwdEncoder = PasswordEncoderFactory.getPasswordEncoder(PasswordAlgorithm.MD5.getValue());
        String md5Str = pwdEncoder.encodePassword(pwd, lowerCaseName);
        return md5Str;
    }

    public static void main(String[] args) {
        String ff = PwdUtils.createSm3Password("ZYGUO", "123456");
        System.out.println(ff);
    }

    @Override
    public MultiOrgUserAccount getMultiOrgUserAccountByIdNumber(String idNumber) {
        MultiOrgUserInfo info = multiOrgUserInfoService.getByIdNumber(idNumber);
        if (info == null) {
            return null;
        }
        return multiOrgUserAccountService.getAccountById(info.getUserId());
    }

    // public static String createSm3Password(String loginName, String pwd) {
    // return SM3Util.encrypt(pwd + "{" + loginName.toLowerCase() + "}");
    // }

    @Override
    public MultiOrgUserAccount getMultiOrgUserAccountCertificateSubject(String certificateSubject) {
        MultiOrgUserInfo info = multiOrgUserInfoService.getByCertificateSubject(certificateSubject);
        if (info == null) {
            return null;
        }
        return multiOrgUserAccountService.getAccountById(info.getUserId());
    }

    /**
     * 添加新账号
     * 1，登录名要对大小写不敏感
     * 2,不输入密码的话， 密码默认为0
     */
    @Override
    public MultiOrgUserAccount addUserAccount(OrgUserVo vo, Boolean isRandomPwd) throws UnsupportedEncodingException {

        // 检查参数
        checkOrgUserVo(vo, false);
        // 检查账号名称唯一
        MultiOrgUserAccount account = this.multiOrgUserAccountService.getAccountByLoignName(vo.getLoginName());
        Assert.isNull(account, "该账号已存在，无法新建");
        // 参数没有问题，处理业务逻辑
        MultiOrgUserAccount newAccount = new MultiOrgUserAccount();
        newAccount.setAttrFromOrgUserVo(vo, true);
        newAccount.setUserNamePy(PinyinUtil.getPinYin(newAccount.getUserName()));
        newAccount.setUserNameJp(PinyinUtil.getPinYinHeadChar(newAccount.getUserName()));
        // 登录名转小写
        // 加密密码，采用登录名的小写作为salt
        newAccount.setPassword(PwdUtils.createSm3Password(vo.getLoginName(), vo.getPassword()));

        // 对登录名也进行MD5加密，也是对登录名小写后MD5(用户名加密未修改为国密加密，因为担心会对旧用户产生影响，TODO)
        String md5LoginName = createMd5LoginName(vo.getLoginName());
        newAccount.setMd5LoginName(md5LoginName);
        // 产生一个新用户ID
        String newId = idGeneratorService.generate(MultiOrgUserAccount.class, USER_ID_PATTERN);
        newAccount.setId(newId);
        newAccount.setPwdCreateTime(new Date());

        // 保存账号信息
        this.multiOrgUserAccountService.save(newAccount);
        return newAccount;
    }

    private void checkOrgUserVo(OrgUserVo vo, boolean isModify) {
        Assert.notNull(vo, "参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getLoginName()), "登录名不能为空");
        Assert.isTrue(vo.getType() != null, "类型不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getSystemUnitId()), "归属单位不能为空");
        if (isModify) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getUuid()), "uuid不能为空");
        }
    }

    /**
     * 修改账号信息，
     * 1，登录名不能修改，用户ID不能修改
     * 2，如果有设置新密码，密码才修改
     */
    @Override
    // @IpLog(info = "修改账号信息")
    public MultiOrgUserAccount modifyUserAccount(OrgUserVo vo) {
        // 检查参数
        checkOrgUserVo(vo, false);
        MultiOrgUserAccount oldAccount = this.multiOrgUserAccountService.getOne(vo.getUuid());
        Assert.isTrue(oldAccount != null, "对应的账号不存在");
        // loginName发生变化
        if (!vo.getLoginName().equalsIgnoreCase(oldAccount.getLoginName())) {
            Assert.isTrue(false, "登录名不能修改");
        }
        if (oldAccount.getType() != 1) { // 除了平台管理员， 其他账号归属单位不让修改
            if (!vo.getSystemUnitId().equalsIgnoreCase(oldAccount.getSystemUnitId())) {
                Assert.isTrue(false, "归属单位不能修改");
            }
        }
        // 参数没有问题，处理业务逻辑
        // 更新对应的属性信息
        oldAccount.setAttrFromOrgUserVo(vo, true);
        oldAccount.setUserNamePy(PinyinUtil.getPinYin(oldAccount.getUserName()));
        oldAccount.setUserNameJp(PinyinUtil.getPinYinHeadChar(oldAccount.getUserName()));
        // 重新设置密码了
        if (StringUtils.isNotBlank(vo.getPassword())) {
            // 登录名转小写
            // 加密密码，采用登录名的小写作为salt
            // String md5Pwd = createMd5Password(vo.getLoginName(),
            // vo.getPassword());
            // oldAccount.setPassword(md5Pwd);
            String newPassword = PwdUtils.createSm3Password(vo.getLoginName(), vo.getPassword());
            this.userAccountService.modifyUserPassword(vo.getLoginName(), newPassword, null);
            // 密码验证改为SM3
            oldAccount.setPassword(newPassword);
            oldAccount.setPwdCreateTime(new Date());

        }

        // 保存账号信息
        this.multiOrgUserAccountService.save(oldAccount);

        // 保存个人信息
        return oldAccount;
    }

    @Override
    public boolean changePasswordById(String userId, String oldPassword, String newPassword) {
        MultiOrgUserAccount user = getAccountByUserId(userId);
        String tempOldPassword = PwdUtils.createSm3Password(user.getLoginName(), oldPassword);
        if (StringUtils.equals(user.getPassword(), tempOldPassword) == false) {
            throw new IllegalArgumentException("旧密码输入不正确！");
        }
        user.setPassword(PwdUtils.createSm3Password(user.getLoginName(), newPassword));
        multiOrgUserAccountService.save(user);
        return true;
    }

    @Override
    public EncryptChangePasswordByIdDto encryptChangePasswordById(String userId, String oldPassword,
                                                                  String newPassword) {
        EncryptChangePasswordByIdDto encryptChangePasswordByIdDto = new EncryptChangePasswordByIdDto();
        MultiOrgUserAccount user = getAccountByUserId(userId);
        MultiOrgPwdSettingDto multiOrgPwdSettingDto = multiOrgPwdSettingFacadeService.getMultiOrgPwdSetting();
        if (StringUtils.isBlank(multiOrgPwdSettingDto.getUuid())) {
            throw new BusinessException("密码规则数据为空，无法操作，请联系管理员保存密码规则！");
        }
        try {
            oldPassword = PwdUtils.decodePwdBybase64AndUnicode(oldPassword);
            newPassword = PwdUtils.decodePwdBybase64AndUnicode(newPassword);
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException("解密出错", e);
        }

        String tempOldPassword = PwdUtils.createSm3Password(user.getLoginName(), oldPassword);
        // 超管用户 修改密码-默认密码0 不需要再校验 //密码规则配置有数据才校验
        if (!(SUPERADMIN_ID.equals(user.getLoginName()) && DEFAULT_PWD.equals(oldPassword))
                && StringUtils.equals(user.getPassword(), tempOldPassword) == false) {
            // 账号计入错误次数 ，超过次数，锁定账号
            PwdErrorDto pwdErrorDto = PwdUtils.getPwdErrorDto(user, multiOrgPwdSettingDto, PwdUtils.updatePwdType);
            multiOrgUserAccountService.save(user);
            BeanUtils.copyProperties(pwdErrorDto, encryptChangePasswordByIdDto);
        } else {
            user.setPassword(PwdUtils.createSm3Password(user.getLoginName(), newPassword));
            user.setPwdErrorLock(PwdErrorLockEnum.UnLocked.getValue());
            user.setPwdErrorNum(0);// 错误次数重置为0
            user.setPwdCreateTime(new Date());
            user.setIsUserSettingPwd(IsUserSettingPwdEnum.Yes.getValue());
            multiOrgUserAccountService.save(user);
            encryptChangePasswordByIdDto.setSuccess(Boolean.TRUE);
        }

        return encryptChangePasswordByIdDto;
    }

    @Override
    public List<String> resetAllUserPasswordByUserIds(Collection<String> userIds, String password) {
        Set<MultiOrgUserAccount> userAccounts = new HashSet<MultiOrgUserAccount>();
        if (userIds != null && false == userIds.isEmpty()) {
            for (String userId : userIds) {
                MultiOrgUserAccount user = getAccountByUserId(userId);
                if (user == null) {
                    continue;
                }
                userAccounts.add(user);
            }
        }
        List<String> results = new ArrayList<String>();
        password = StringUtils.isBlank(password) ? DEFAULT_PWD : password;
        for (MultiOrgUserAccount user : userAccounts) {
            user.setPassword(PwdUtils.createSm3Password(user.getLoginName(), password));
            multiOrgUserAccountService.save(user);
            results.add(user.getId());
        }
        return results;
    }

    // @Override 重复代码
    // public List<String> resetAllUserPasswordBySystemUnitId(String
    // systemUnitId, String password) {
    // Set<MultiOrgUserAccount> userAccounts = new
    // HashSet<MultiOrgUserAccount>();
    // if (StringUtils.isNotBlank(systemUnitId)) {
    // userAccounts.addAll(multiOrgUserAccountService.queryAllAccountOfUnit(systemUnitId));
    // }
    // List<String> results = new ArrayList<String>();
    // password = StringUtils.isBlank(password) ? DEFAULT_PWD : password;
    // for (MultiOrgUserAccount user : userAccounts) {
    // user.setPassword(createMd5Password(user.getLoginName(), password));
    // multiOrgUserAccountService.save(user);
    // results.add(user.getId());
    // }
    // return results;
    // }

    // 禁用账号
    @Override
    public boolean forbidAccount(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgUserAccount a = this.multiOrgUserAccountService.getOne(uuid);
        Assert.isTrue(a != null, "对应的账号不存在");
        a.setIsForbidden(1);
        this.multiOrgUserAccountService.save(a);
        return true;
    }

    // 启用账号
    @Override
    public boolean unforbidAccount(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgUserAccount a = this.multiOrgUserAccountService.getOne(uuid);
        Assert.isTrue(a != null, "对应的账号不存在");
        a.setIsForbidden(0);
        this.multiOrgUserAccountService.save(a);
        return true;
    }

    @Override
    @Transactional
    public boolean unforbidAccount(Collection<String> uuids) {
        Assert.isTrue(false == CollectionUtils.isEmpty(uuids), "记录不能为空");
        for (String uuid : uuids) {
            unforbidAccount(uuid);
        }
        return true;
    }

    // 多次密码错误，锁住账号，
    @Override
    public boolean lockAccount(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgUserAccount a = this.multiOrgUserAccountService.getOne(uuid);
        Assert.isTrue(a != null, "对应的账号不存在");
        a.setIsLocked(1);
        this.multiOrgUserAccountService.save(a);
        return true;
    }

    // 解锁账号
    @Override
    public boolean unlockAccount(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgUserAccount a = this.multiOrgUserAccountService.getOne(uuid);
        Assert.isTrue(a != null, "对应的账号不存在");
        a.setIsLocked(0);
        this.multiOrgUserAccountService.save(a);
        return true;
    }

    /**
     * 密码错误锁定-解锁账号
     *
     * @param uuid
     * @return
     */
    @Override
    @Transactional
    public Boolean pwdUnlockAccount(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgUserAccount a = this.multiOrgUserAccountService.getOne(uuid);
        if (a != null) {
            a.setPwdErrorNum(0);
            a.setPwdErrorLock(PwdErrorLockEnum.UnLocked.getValue());
            a.setLastLoginTime(new Date());
            this.multiOrgUserAccountService.save(a);
        }
        return true;
    }

    @Override
    public MultiOrgUserAccount getAccount(String uuid) {
        return this.multiOrgUserAccountService.getOne(uuid);
    }

    // 通过登录名获取账号信息
    @Override
    public MultiOrgUserAccount getUserByLoginNameIgnoreCase(String loginName, String loginNameHashAlgorithmCode) {
        MultiOrgUserAccount q = new MultiOrgUserAccount();
        if (PasswordAlgorithm.MD5.getValue().equals(loginNameHashAlgorithmCode)) {
            // 登录名加密
            q.setMd5LoginName(createMd5LoginName(loginName));
        } else {
            q.setLoginNameLowerCase(loginName.toLowerCase());
        }
        List<MultiOrgUserAccount> objs = this.multiOrgUserAccountService.findByExample(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    /**
     * 支持中文名登录
     *
     * @param userName
     * @return
     * @author baozh
     * @date 2021/11/23 16:52
     */
    @Override
    public List<MultiOrgUserAccount> getUserAccountByLoginNameIgnoreCase(String userName) {
        MultiUserLoginSettingsEntity loginSetting = multiUserLoginSettingsService.getLoginSettingsEntity();
        if (loginSetting == null) {
            return null;
        }
        List<MultiOrgUserAccount> userAccounts = multiOrgUserAccountService
                .getUserAccountByLoginNameIgnoreCase(userName, loginSetting);
        return userAccounts;
    }

    // 通过userID获取账号信息
    @Override
    public MultiOrgUserAccount getAccountByUserId(String userId) {
        return multiOrgUserAccountService.getAccountById(userId);
    }

    @Override
    public List<MultiOrgUserAccount> queryAllAccountOfUnit(String systemUnitId) {
        return this.multiOrgUserAccountService.queryAllAccountOfUnit(systemUnitId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30)
    public void updateLastLoginTime(String userUuid) {
        MultiOrgUserAccount a = this.multiOrgUserAccountService.getOne(userUuid);
        if (a != null) {
            a.setLastLoginTime(new Date());
            this.multiOrgUserAccountService.save(a);
        }

    }

    @Override
    public List<MultiOrgUserAccount> queryAllAdminIdsBySystemUnitId(String unitId) {
        return this.multiOrgUserAccountService.queryAllAdminIdsBySystemUnitId(unitId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public List<String> queryUserIdsLikeName(String name) {
        return this.multiOrgUserAccountService.queryUserIdsLikeName(name);
    }

    @Override
    public List<OrgUserDto> queryUserDtoListByIds(List<String> userIds) {
        return this.multiOrgUserAccountService.queryUserDtoListByIds(userIds);
    }

    @Override
    public List<OrgUserTableVo> query(QueryInfo queryInfo) {
        return multiOrgUserAccountService.query(queryInfo);
    }

    @Override
    public void deleteAccount(MultiOrgUserAccount a) {
        this.multiOrgUserAccountService.delete(a);
    }

    @Override
    public boolean resetAllUserPwd() {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<MultiOrgUserAccount> allAccount = this.queryAllAccountOfUnit(unitId);
        if (CollectionUtils.isNotEmpty(allAccount)) {
            for (MultiOrgUserAccount a : allAccount) {
                this.resetUserPwd(a.getUuid());
            }
        }
        return true;
    }

    @Override
    public String resetAllUserRandomPwd(HttpServletRequest request, HttpServletResponse response) {
        String fileUuid = "";
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<MultiOrgUserAccount> allAccount = this.queryAllAccountOfUnit(unitId);
        // 本地测试用
        // allAccount = queryUsersByUserName(unitId, "lucf");
        MultiOrgPwdSettingDto multiOrgPwdSettingDto = multiOrgPwdSettingFacadeService.getMultiOrgPwdSetting();
        if (StringUtils.isBlank(multiOrgPwdSettingDto.getUuid())) {
            throw new RuntimeException("密码规则数据为空，无法操作，请联系管理员保存密码规则！");
        }
        if (CollectionUtils.isNotEmpty(allAccount)) {
            // map key:loginname value 账号导出对象
            Map<String, NewAccountDto> newAccountMap = Maps.newHashMap();
            for (MultiOrgUserAccount a : allAccount) {
                String randomPwd = PwdUtils.generatePwdByRoleByCkeckPwdRole(multiOrgPwdSettingDto);
                this.resetUserRandomPwd(a.getUuid(), randomPwd);
                NewAccountDto newAccountDto = newAccountMap.get(a.getLoginName());
                newAccountDto = new NewAccountDto();
                newAccountDto.setLoginName(a.getLoginName());
                newAccountDto.setPassword(randomPwd);
                newAccountDto.setUserName(a.getUserName());
                newAccountMap.put(newAccountDto.getLoginName(), newAccountDto);
            }
            // 导入
            List<String[]> dataList = getNewUserAccountDataList(newAccountMap);
            try {
                // ExportAccountUtils.exportPwdAccounts(ExportAccountUtils.pwdTitles, dataList,
                // request, response);
                HSSFWorkbook excel = ExcelUtils.generateExcelBook(dataList, ExportAccountUtils.pwdTitles);
                fileUuid = uploadExcel(excel);
            } catch (Exception e) {
                logger.error("导出用户列表失败：", e);
            }

        }
        return fileUuid;
    }

    /**
     * 上传exel
     *
     * @param excel
     * @return java.lang.String 返回MongoFileEntity ID
     **/
    @Override
    public String uploadExcel(HSSFWorkbook excel) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        excel.write(os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
        String uuid = UUID.randomUUID().toString();
        // 上传处理
        mongoFileService.popAllFilesFromFolder(uuid);
        String fileName = "用户密码_" + DateUtil.getFormatDate(new Date(), "yyyy-MM-dd") + ".xls";
        MongoFileEntity file = mongoFileService.saveFile(fileName, inputStream);
        mongoFileService.pushFileToFolder(uuid, file.getId(), "attach");
        return file.getId();
    }

    @Override
    public boolean resetAllUserDefinedPwd(String userDefinedPwd) throws UnsupportedEncodingException {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<MultiOrgUserAccount> allAccount = this.queryAllAccountOfUnit(unitId);
        MultiOrgPwdSettingDto multiOrgPwdSettingDto = multiOrgPwdSettingFacadeService.getMultiOrgPwdSetting();
        if (CollectionUtils.isNotEmpty(allAccount)) {
            for (MultiOrgUserAccount a : allAccount) {
                this.resetUserDefinedPwd(a.getUuid(), userDefinedPwd);
            }
        }
        return true;
    }

    @Override
    public boolean resetUserPwd(List<String> userUuids) {
        for (String uuid : userUuids) {
            this.resetUserPwd(uuid);
        }
        return true;
    }

    @Override
    public String resetUserRandomPwd(List<String> userUuids) {
        MultiOrgPwdSettingDto multiOrgPwdSettingDto = multiOrgPwdSettingFacadeService.getMultiOrgPwdSetting();
        if (StringUtils.isBlank(multiOrgPwdSettingDto.getUuid())) {
            throw new RuntimeException("密码规则数据为空，无法操作，请联系管理员保存密码规则！");
        }
        String randomPwd = PwdUtils.generatePwdByRoleByCkeckPwdRole(multiOrgPwdSettingDto);
        for (String uuid : userUuids) {
            this.resetUserRandomPwd(uuid, randomPwd);
        }
        return randomPwd;
    }

    @Override
    public boolean resetUserDefinedPwd(List<String> userUuids, String userDefinedPwd)
            throws UnsupportedEncodingException {
        for (String userUuid : userUuids) {
            this.resetUserDefinedPwd(userUuid, userDefinedPwd);
        }
        return true;
    }

    @Override
    public MultiOrgUserAccount getAccountByExtAccount(String externalLoginId, String externalActType) {

        MultiOrgUsrExtAccountEntity extAccountEntity = multiOrgUsrExtAccountService.getByExternal(externalLoginId,
                externalActType);
        if (extAccountEntity != null) {
            return getAccount(extAccountEntity.getAccountUuid());

        }
        return null;
    }

    @Override
    public void bindAccount2Ext(String accountUuid, String username, String remoteType) {
        MultiOrgUsrExtAccountEntity extAccountEntity = new MultiOrgUsrExtAccountEntity();
        extAccountEntity.setAccountUuid(accountUuid);
        extAccountEntity.setExternalAccountId(username);
        extAccountEntity.setExternalAccountType(remoteType);
        this.multiOrgUsrExtAccountService.save(extAccountEntity);
    }

    private boolean resetUserPwd(String userUuid) {
        MultiOrgUserAccount account = this.getAccount(userUuid);
        if (account != null) {
            String sm3Pwd = PwdUtils.createSm3Password(account.getLoginName(), DEFAULT_PWD);
            account.setPassword(sm3Pwd);
            account.setPwdCreateTime(new Date());
            this.multiOrgUserAccountService.save(account);
        }
        return true;
    }

    /**
     * 重置指定用户密码-随机密码
     *
     * @param userUuid
     * @param randomPwd 随机密码
     * @return boolean
     **/
    private boolean resetUserRandomPwd(String userUuid, String randomPwd) {
        MultiOrgUserAccount account = this.getAccount(userUuid);
        if (account != null) {
            String sm3Pwd = PwdUtils.createSm3Password(account.getLoginName(), randomPwd);
            this.setMultiOrgUserAccount(account, sm3Pwd);
            this.multiOrgUserAccountService.save(account);
        }
        return true;
    }

    /**
     * 重置指定用户密码-自定义密码
     *
     * @param userUuid
     * @param userDefinedPwd 用户自定义密码
     * @return boolean
     **/
    private boolean resetUserDefinedPwd(String userUuid, String userDefinedPwd) throws UnsupportedEncodingException {
        MultiOrgUserAccount account = this.getAccount(userUuid);
        if (account != null) {
            String sm3Pwd = PwdUtils.createSm3Password(account.getLoginName(),
                    PwdUtils.decodePwdBybase64AndUnicode(userDefinedPwd));
            this.setMultiOrgUserAccount(account, sm3Pwd);
            this.multiOrgUserAccountService.save(account);
        }
        return true;
    }

    /**
     * 重置密码，相关的字段设值
     *
     * @param account 账号对象
     * @param pwd     加密后的密码
     * @return void
     **/
    private void setMultiOrgUserAccount(MultiOrgUserAccount account, String pwd) {
        account.setPassword(pwd);
        account.setPwdCreateTime(new Date());
        account.setPwdErrorNum(0);
        account.setPwdErrorLock(PwdErrorLockEnum.UnLocked.getValue());
        account.setLastUnLockedTime(new Date());
        account.setIsUserSettingPwd(IsUserSettingPwdEnum.NO.getValue());
    }

    @Override
    public String getUniqueLoginNameByUserName(String userName) {
        return multiOrgUserAccountService.getUniqueLoginNameByUserName(userName);
    }

    @Override
    public List<MultiOrgUserAccount> queryUsersByUserName(String systemUnitId, String userName) {
        MultiOrgUserAccount multiOrgUserAccount = new MultiOrgUserAccount();
        multiOrgUserAccount.setIsLocked(0);
        multiOrgUserAccount.setIsExpired(0);
        multiOrgUserAccount.setIsForbidden(0);
        multiOrgUserAccount.setUserName(userName);
        multiOrgUserAccount.setSystemUnitId(systemUnitId);
        return multiOrgUserAccountService.findByExample(multiOrgUserAccount);
    }

    @Override
    public long countBySystemUnitId(String systemUnitId, Date startTime, Date endTime) {
        return multiOrgUserAccountService.countBySystemUnitId(systemUnitId, startTime, endTime);
    }

    @Override
    public HashMap<String, String> getUnforbiddenUserIdNames(List<String> userIds) {
        HashMap<String, String> values = Maps.newHashMap();
        if (userIds == null || userIds.size() == 0) {
            return values;
        }
        Map<String, Object> params = Maps.newHashMap();
        StringBuilder hqlsb = new StringBuilder(
                "select id,user_name as name from multi_org_user_account where is_forbidden = 0 and ");
        HqlUtils.appendSql("id", params, hqlsb, Sets.newHashSet(userIds));
        List<QueryItem> itemList = this.multiOrgUserAccountService.listQueryItemBySQL(hqlsb.toString(), params, null);
        for (QueryItem i : itemList) {
            values.put(i.getString("id"), i.getString("name"));
        }
        return values;
    }

    @Override
    @Transactional
    public void save(MultiOrgUserAccount user) {
        multiOrgUserAccountService.save(user);
    }

    /**
     * 获取要导出的excel所有数据集合(包含密码)
     *
     * @param newAccountMap
     * @return java.util.List<java.lang.String [ ]>
     **/
    @Override
    public List<String[]> getNewUserAccountDataList(Map<String, NewAccountDto> newAccountMap) {
        List<String[]> dataList = com.google.common.collect.Lists.newArrayList();
        Map<String, MultiOrgUserAccount> userAccountMap = new HashMap<>();
        List<String> loginNames = getLoginNames(newAccountMap);
        List<MultiOrgUserAccount> userAccountList = multiOrgUserAccountService
                .getUserAccountListByloginNames(loginNames);
        for (MultiOrgUserAccount userAccount : userAccountList) {
            userAccountMap.put(userAccount.getId(), userAccount);
        }

        StringBuilder userInfohql = new StringBuilder("from MultiOrgUserInfo where ");
        Map<String, Object> userInfoParam = new HashMap<>();
        HqlUtils.appendSql("userId", userInfoParam, userInfohql, Sets.<Serializable>newHashSet(userAccountMap.keySet()));
        List<MultiOrgUserInfo> userInfoList = multiOrgUserInfoService.listByHQL(userInfohql.toString(), userInfoParam);
        Map<String, MultiOrgUserInfo> userInfoMap = new HashMap<>();
        for (MultiOrgUserInfo userInfo : userInfoList) {
            userInfoMap.put(userInfo.getUserId(), userInfo);
        }
        StringBuilder userTreeHql = new StringBuilder("from MultiOrgUserTreeNode where ");
        Map<String, Object> userTreeParam = new HashMap<>();
        HqlUtils.appendSql("userId", userTreeParam, userTreeHql, Sets.<Serializable>newHashSet(userAccountMap.keySet()));
        List<MultiOrgUserTreeNode> userTreeNodeList = multiOrgUserTreeNodeService.listByHQL(userTreeHql.toString(),
                userTreeParam);
        Multimap<String, String> userJobMap = HashMultimap.create();
        for (MultiOrgUserTreeNode userTreeNode : userTreeNodeList) {
            userJobMap.put(userTreeNode.getUserId(), userTreeNode.getEleId());
        }
        StringBuilder jobTreeHql = new StringBuilder("from MultiOrgTreeNode where ");
        Map<String, Object> jobTreeParam = new HashMap<>();
        HqlUtils.appendSql("eleId", jobTreeParam, jobTreeHql, Sets.<Serializable>newHashSet(userJobMap.values()));
        List<MultiOrgTreeNode> orgTreeNodeList = multiOrgTreeNodeService.listByHQL(jobTreeHql.toString(), jobTreeParam);
        Map<String, String[]> jobIdMap = new HashMap<>();
        Set<String> eleIdSet = new HashSet<>();
        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            String eleIdPath = treeNode.getEleIdPath();
            String[] eleIdPaths = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String[] jobEleIdPath = new String[eleIdPaths.length - 1];
            for (int i = 1; i < eleIdPaths.length; i++) {
                eleIdSet.add(eleIdPaths[i]);
                jobEleIdPath[i - 1] = eleIdPaths[i];
            }
            jobIdMap.put(treeNode.getEleId(), jobEleIdPath);
        }
        Map<String, MultiOrgElement> elementMap = new HashMap<>();
        StringBuilder elementHql = new StringBuilder("from MultiOrgElement where ");
        Map<String, Object> elementParam = new HashMap<>();
        HqlUtils.appendSql("id", elementParam, elementHql, Sets.<Serializable>newHashSet(eleIdSet));
        List<MultiOrgElement> elementList = multiOrgElementService.listByHQL(elementHql.toString(), elementParam);
        for (MultiOrgElement multiOrgElement : elementList) {
            elementMap.put(multiOrgElement.getId(), multiOrgElement);
        }
        for (MultiOrgUserAccount userAccount : userAccountList) {

            String loginName = userAccount.getLoginName();
            String userName = userAccount.getUserName();

            MultiOrgUserInfo userInfo = userInfoMap.get(userAccount.getId());
            String mobile = null;
            String email = null;
            String employeeNumber = null;
            String homePhone = null;
            String officePhone = null;
            String idNumber = null;
            String englishName = null;
            String sex = null;
            if (userInfo != null) {
                mobile = userInfo.getMobilePhone();
                email = userInfo.getMainEmail();
                employeeNumber = userInfo.getEmployeeNumber();
                homePhone = userInfo.getHomePhone();
                officePhone = userInfo.getOfficePhone();
                idNumber = userInfo.getIdNumber();
                englishName = userInfo.getEnglishName();
                if (userInfo.getSex() != null) {
                    sex = userInfo.getSex().equals("1") ? "男" : "女";
                }
            }

            if (userJobMap.containsKey(userAccount.getId())) {
                for (String jobId : userJobMap.get(userAccount.getId())) {
                    String[] eleIdPaths = jobIdMap.get(jobId);
                    if (eleIdPaths == null) {
                        continue;
                    }
                    String[] eleNames = new String[eleIdPaths.length];
                    for (int i = 0; i < eleIdPaths.length; i++) {
                        MultiOrgElement element = elementMap.get(eleIdPaths[i]);
                        if (element != null) {
                            eleNames[i] = element.getName();
                        }
                    }
                    String unitName = eleNames[0];
                    String deptPath = StringUtils.join(eleNames, "/", 1, eleNames.length - 1);
                    ;
                    String jobName = eleNames[eleNames.length - 1];

                    String pwd = newAccountMap.get(userAccount.getLoginName()).getPassword();
                    String[] data = new String[]{unitName, deptPath, jobName, loginName, userName, pwd, sex, mobile,
                            email, employeeNumber, homePhone, officePhone, idNumber, englishName};
                    dataList.add(data);
                }
            } else {

                String pwd = newAccountMap.get(userAccount.getLoginName()).getPassword();
                String[] data = new String[]{null, null, null, loginName, userName, pwd, sex, mobile, email,
                        employeeNumber, homePhone, officePhone, idNumber, englishName};
                dataList.add(data);
            }
        }

        return dataList;
    }

    @Override
    public List<MultiOrgUserAccount> getAccountByUsername(String username) {
        return this.multiOrgUserAccountService.getAccountByUsername(username);
    }

    @Override
    public List<MultiOrgUserAccount> getAccountsByUserIds(List<String> ids) {
        return this.multiOrgUserAccountService.getAccountsByUserIds(ids);
    }

    @Override
    public int countUnforbiddenAccount(String systemUnitId) {
        return this.multiOrgUserAccountService.countUnforbiddenAccount(systemUnitId);
    }

    /**
     * 获取所有的登录名集合
     *
     * @param
     * @return java.util.List<java.lang.String>
     **/
    private List<String> getLoginNames(Map<String, NewAccountDto> newAccountMap) {
        List<String> loginNames = new ArrayList<>();
        for (String key : newAccountMap.keySet()) {
            NewAccountDto newAccountDto = newAccountMap.get(key);
            loginNames.add(newAccountDto.getLoginName());
        }
        return loginNames;
    }

    @Transactional
    @Override
    public String saveRelationAccount(MultiUserRelationAccountVo relationAccountVo, HttpServletRequest request) {
        String relationAccount = relationAccountVo.getRelationAccount();
        // 获取平台系统参数及当前系统和关联账户系统是否开启
        // 默认当前登录账号是主账号
        // 启用为enable
        String relationEnable = SystemParamsUtils.getValue("system.account.relation.enable");
        if (!ENABLE.equals(relationEnable)) {
            return "平台或系统未启用账户关联,请联系管理员";
        }

        String loginPwd = PwdUtils.createSm3Password(relationAccountVo.getRelationAccount(),
                relationAccountVo.getRelationPassword());
        MultiOrgUserAccount account = multiOrgUserAccountService.getAccountByLoignName(relationAccount);
        // 判断是否开启关联
        if (account == null || !account.getSystemUnitId().equals(relationAccountVo.getSystemUnitId())
                || !account.getPassword().equals(loginPwd)) {
            return "用户名或密码错误";
        }

        String currentLoginName = SpringSecurityUtils.getCurrentLoginName();
        String currentSystemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        if (currentLoginName.equals(relationAccount)) {
            throw new WellException("无效绑定！您绑定的是当前登录账号！");
        }
        List<String> relationAccountParams = new ArrayList<>();
        relationAccountParams.add(currentLoginName);
        relationAccountParams.add(relationAccount);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("relationAccountParams", relationAccountParams);
        List<MultiUserRelationAccountEntity> relationAccountEntities = multiUserRelationAccountService.listByHQL(
                " from MultiUserRelationAccountEntity WHERE isValid = 1 and masterAccount in (select masterAccount from MultiUserRelationAccountEntity WHERE isValid = 1 and relationAccount in (:relationAccountParams)) order by createTime ASC",
                paramMap);
        MultiUserRelationAccountEntity masterAccount = null;
        Set<String> relationAccounts = userRelationAccountMap.get(currentUserId);
        if (CollectionUtils.isNotEmpty(relationAccounts) && relationAccounts.contains(account.getId())) {
            throw new WellException("账号已绑定");
        }

        // 校验完成，执行绑定逻辑
        MultiUserRelationAccountEntity currentAccount = null;
        MultiUserRelationAccountEntity relationEntity = null;
        for (MultiUserRelationAccountEntity relationAccountEntity : relationAccountEntities) {
            if (masterAccount == null
                    && relationAccountEntity.getMasterAccount().equals(relationAccountEntity.getRelationAccount())) {
                masterAccount = relationAccountEntity;
            }
            if (account.getId().equals(relationAccountEntity.getRelationAccountId())) {
                relationEntity = relationAccountEntity;
            }
            if (currentUserId.equals(relationAccountEntity.getRelationAccountId())) {
                currentAccount = relationAccountEntity;
            }
        }

        // 保存主账户
        if (masterAccount == null) {
            masterAccount = new MultiUserRelationAccountEntity();
            masterAccount.setRelationAccount(currentLoginName);
            masterAccount.setIsValid(1);
            masterAccount.setMasterAccount(currentLoginName);
            masterAccount.setRelationSystemUnitId(currentSystemUnitId);
            masterAccount.setRelationAccountId(currentUserId);
            multiUserRelationAccountService.save(masterAccount);
        }

        // 将原有有效账号的主账号更新
        for (MultiUserRelationAccountEntity relationAccountEntity : relationAccountEntities) {
            if (!masterAccount.getMasterAccount().equals(relationAccountEntity.getMasterAccount())) {
                relationAccountEntity.setMasterAccount(masterAccount.getMasterAccount());
                multiUserRelationAccountService.update(relationAccountEntity);
            }
        }
        MultiOrgUserAccount currentUserAccount = multiOrgUserAccountService.getAccountById(currentUserId);
        // 更新当前账户
        modifyRelationAccountEntity(masterAccount, currentAccount, currentUserAccount);
        // 更新关联账户
        modifyRelationAccountEntity(masterAccount, relationEntity, account);

        // 重新加载离线账号缓存
        addRelationAccountCache(currentUserId);
        return null;
    }

    /**
     * 更新关联账户关联
     *
     * @param
     * @return
     * @author baozh
     * @date 2022/1/19 8:56
     */
    private void modifyRelationAccountEntity(MultiUserRelationAccountEntity masterAccount,
                                             MultiUserRelationAccountEntity relationEntity, MultiOrgUserAccount userAccount) {
        // 更新关联主账户，如果关联账户不为空，
        // 如果主账号不相等则更新主账户，相等则退出
        if (relationEntity != null) {
            if (!relationEntity.getMasterAccount().equals(relationEntity.getMasterAccount())) {
                relationEntity.setMasterAccount(masterAccount.getMasterAccount());
                multiUserRelationAccountService.update(relationEntity);
            }
            return;
        }
        // 判断账户是否为主账户，主账户在前面已经处理，则不需要处理
        if (!userAccount.getLoginName().equals(masterAccount.getMasterAccount())) {
            relationEntity = new MultiUserRelationAccountEntity();
            relationEntity.setRelationAccount(userAccount.getLoginName());
            relationEntity.setRelationAccountId(userAccount.getId());
            relationEntity.setRelationSystemUnitId(userAccount.getSystemUnitId());
            relationEntity.setIsValid(1);
            relationEntity.setMasterAccount(masterAccount.getMasterAccount());
            multiUserRelationAccountService.save(relationEntity);
        }
    }


    @Override
    public List<MultiUserRelationAccountEntity> getRelationAccounts() {
        String currentAccount = SpringSecurityUtils.getCurrentLoginName();
        return getRelationAccounts(currentAccount);
    }

    private List<MultiUserRelationAccountEntity> getRelationAccounts(String loginName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("relationAccount", loginName);
        return multiUserRelationAccountService.listByHQL(
                " from MultiUserRelationAccountEntity WHERE isValid = 1 and masterAccount in (select masterAccount from MultiUserRelationAccountEntity WHERE isValid = 1 and relationAccount = :relationAccount) order by createTime asc",
                paramMap);
    }

    @Override
    public void addRelationAccountCache(String userId) {
        MultiOrgUserAccount userAccount = multiOrgUserAccountService.getAccountById(userId);
        if (userAccount != null) {
            List<MultiUserRelationAccountEntity> relationAccounts = getRelationAccounts(userAccount.getLoginName());
            Set<String> relationAccountIds = new HashSet<>();
            if (!CollectionUtils.isEmpty(relationAccounts)) {
                relationAccountIds = relationAccounts.stream().map(MultiUserRelationAccountEntity::getRelationAccountId)
                        .collect(Collectors.toSet());
            }
            userRelationAccountMap.put(userId, relationAccountIds);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MultiUserRelationAccountEntity> getRelationAccountsWithLoginAddr() {
        List<MultiUserRelationAccountEntity> relationAccounts = getRelationAccounts();
        // Map<String, MultiUserRelationAccountEntity> collect =
        // relationAccounts.stream().collect(Collectors.toMap(MultiUserRelationAccountEntity::getRelationSystemUnitId,
        // e -> e));
        Set<String> collect = relationAccounts.stream().map(accountEntity -> accountEntity.getRelationSystemUnitId())
                .collect(Collectors.toSet());
        List<String> params = new ArrayList<>(collect);
        if (CollectionUtils.isEmpty(params)) {
            return new ArrayList<>();
        }
        List<AppLoginPageConfigEntity> loginPageConfigs = appLoginPageConfigService.getDao()
                .listByFieldInValues("systemUnitId", params);

        List<MultiOrgSystemUnit> systemUnitEntities = multiOrgSystemUnitService.getDao().listByFieldInValues("id",
                params);

        Map<String, String> systemUnits = systemUnitEntities.stream()
                .collect(Collectors.toMap(MultiOrgSystemUnit::getId, MultiOrgSystemUnit::getName));
        String defaultLoginAddr = "/login";
        Map<String, String> addrMap = new HashMap<>();
        for (AppLoginPageConfigEntity loginPageConfig : loginPageConfigs) {
            if ("1".equals(loginPageConfig.getUnitLoginPageSwitch())) {
                addrMap.put(loginPageConfig.getSystemUnitId(),
                        defaultLoginAddr + "/unit/" + loginPageConfig.getUnitLoginPageUri());
            } else {
                addrMap.put(loginPageConfig.getSystemUnitId(), defaultLoginAddr);
            }
        }
        for (MultiUserRelationAccountEntity relationAccount : relationAccounts) {
            relationAccount.setLoginAddr(addrMap.get(relationAccount.getRelationSystemUnitId()));
            relationAccount.setRelationSystemUnitName(systemUnits.get(relationAccount.getRelationSystemUnitId()));
        }
        return relationAccounts;
    }

    @Transactional
    @Override
    public boolean unboundAccount(String relationAccount) {
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        List<MultiUserRelationAccountEntity> relationAccounts = getRelationAccounts();
        String masterUuid = null;
        for (MultiUserRelationAccountEntity accountEntity : relationAccounts) {
            if (accountEntity.getRelationAccount().equals(accountEntity.getMasterAccount())) {
                masterUuid = accountEntity.getUuid();
            }
        }

        for (MultiUserRelationAccountEntity accountEntity : relationAccounts) {
            if (accountEntity.getRelationAccount().equals(relationAccount)) {
                accountEntity.setIsValid(0);
                multiUserRelationAccountService.update(accountEntity);
                // 如果更新后只剩下主账号，则删除主账号记录
                if (relationAccounts.size() == 2) {
                    multiUserRelationAccountService.delete(masterUuid);
                }
                // 重新加载离线账号缓存
                addRelationAccountCache(currentUserId);
            }
        }
        return true;
    }

    @Override
    public boolean checkRelationAccount(String account) {
        List<MultiUserRelationAccountEntity> relationAccounts = getRelationAccounts();
        List<String> collect = relationAccounts.stream().map(accountEntity -> accountEntity.getRelationAccount())
                .collect(Collectors.toList());
        if (collect.contains(account)) {// 判断当前关联账户是否存在
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean setMasterAccount(String masterAccount) {
        List<MultiUserRelationAccountEntity> relationAccounts = getRelationAccounts();
        List<String> collect = relationAccounts.stream().map(accountEntity -> accountEntity.getRelationAccount())
                .collect(Collectors.toList());
        if (!collect.contains(masterAccount)) {// 判断当前关联账户是否存在
            return false;
        }
        for (MultiUserRelationAccountEntity accountEntity : relationAccounts) {
            accountEntity.setMasterAccount(masterAccount);
            multiUserRelationAccountService.update(accountEntity);
        }
        return true;
    }
}
