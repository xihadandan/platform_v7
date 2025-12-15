package com.wellsoft.pt.multi.org.util;

import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.sm.SM3Util;
import com.wellsoft.pt.multi.org.dto.MultiOrgPwdSettingDto;
import com.wellsoft.pt.multi.org.dto.PwdErrorDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.enums.IsPwdErrorLockEnum;
import com.wellsoft.pt.multi.org.enums.LetterAskEnum;
import com.wellsoft.pt.multi.org.enums.LetterLimitedEnum;
import com.wellsoft.pt.multi.org.enums.PwdErrorLockEnum;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Random;

/**
 * Description: 密码校验工具类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/29.1	    zenghw		2021/3/29		    Create
 * </pre>
 * @date 2021/3/29
 */
public class PwdUtils {

    public static final Integer updatePwdType = 1;
    public static final Integer loginPwdType = 2;

    private static final String letterMatches = ".*([A-Z]|[a-z])+.*";
    private static final String numberMatches = ".*([0-9])+.*";
    private static final String specialCharMatches = ".*([`~!@#$^&*()_=|{}':;',\\\\[\\\\].<>《》/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？ ])+.*";
    private static final String chineseMatches = ".*([\u4e00-\u9fa5])+.*";

    /**
     * 创建SM3加密密码
     *
     * @param loginName
     * @param pwd
     * @return java.lang.String
     **/
    public static String createSm3Password(String loginName, String pwd) {
        return SM3Util.encrypt(pwd + "{" + loginName.toLowerCase() + "}");
    }

    /**
     * 解码密码
     * base64+unicode
     *
     * @param oldPassword
     * @return java.lang.String
     **/
    public static String decodePwdBybase64AndUnicode(String oldPassword) throws UnsupportedEncodingException {
        String base64Pwd = new String(org.apache.commons.codec.binary.Base64.decodeBase64(oldPassword));
        String pwd = URLDecoder.decode(base64Pwd, "UTF-8");
        return pwd;
    }

    /**
     * 根据密码规则，随机生成密码字符串
     * 生成的密码包含三种：字母数字特殊符号组成
     *
     * @param multiOrgPwdSettingDto
     * @return java.lang.String
     **/
    public static String generatePwdByRole(MultiOrgPwdSettingDto multiOrgPwdSettingDto) {
        byte[] genChances = {3, 6, 1};
        final Random random = new Random();
        // int randNumber =rand.nextInt(MAX - MIN + 1) + MIN;
        // randNumber 将被赋值为一个 MIN和 MAX 范围内的随机数
        Integer length = random.nextInt(multiOrgPwdSettingDto.getMaxLength() - multiOrgPwdSettingDto.getMinLength() + 1)
                + multiOrgPwdSettingDto.getMinLength();

        char[] pwds = GenerateRandomPassword.generateRandomPassword(length, genChances,
                multiOrgPwdSettingDto.getLetterLimited());
        String newPwd = new String(pwds);
        return newPwd;
    }

    /**
     * 根据密码规则，随机生成密码字符串(经过严格校验密码规则)
     * 生成的密码包含三种：字母数字特殊符号组成
     *
     * @param multiOrgPwdSettingDto
     * @return java.lang.String
     **/
    public static String generatePwdByRoleByCkeckPwdRole(MultiOrgPwdSettingDto multiOrgPwdSettingDto) {
        Boolean isNotOk = true;
        String pwdStr = "";
        while (isNotOk) {
            pwdStr = generatePwdByRole(multiOrgPwdSettingDto);
            if (ckeckPwdRole(pwdStr, multiOrgPwdSettingDto)) {
                isNotOk = false;
            }
        }
        return pwdStr;

    }

    /**
     * 校验密码是否符合密码规则
     *
     * @param pwd                   未加密密码字符串
     * @param multiOrgPwdSettingDto 密码规则
     * @return java.lang.Boolean 通过为true
     **/
    public static Boolean ckeckPwdRole(String pwd, MultiOrgPwdSettingDto multiOrgPwdSettingDto) {
        Boolean isSuccess = true;

        // 字符要求
        LetterAskEnum letterAsk = LetterAskEnum.getByValue(multiOrgPwdSettingDto.getLetterAsk());
        switch (letterAsk) {
            case LEAST1:
                if (includeNumber(pwd) < 1) {
                    return false;
                }
                break;
            case LEAST2:
                if (includeNumber(pwd) < 2) {
                    return false;
                }
                break;
            case INCLUDE3:
                if (includeNumber(pwd) != 3) {
                    return false;
                }
                break;
            default:
                break;
        }
        // 字母限制 开了才检验 且字符串存在字母
        if (LetterLimitedEnum.Yes.getValue().equals(multiOrgPwdSettingDto.getLetterLimited())
                && pwd.matches(letterMatches)) {
            if (pwd.matches(".*([A-Z])+.*") && pwd.matches(".*([a-z])+.*")) {
                // 字母必须要有大写、小写
            } else {
                return false;
            }
        }
        // 长度要求
        if (pwd.length() < multiOrgPwdSettingDto.getMinLength()
                || pwd.length() > multiOrgPwdSettingDto.getMaxLength()) {
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * 返回密码错误输出对象
     * 账号计入错误次数 ，超过次数，锁定账号
     *
     * @param user                  多组织用户账号
     * @param multiOrgPwdSettingDto 密码规配置
     * @return com.wellsoft.pt.multi.org.dto.PwdErrorDto
     **/
    public static PwdErrorDto getPwdErrorDto(MultiOrgUserAccount user, MultiOrgPwdSettingDto multiOrgPwdSettingDto,
                                             Integer pwdType) {
        PwdErrorDto pwdErrorDto = new PwdErrorDto();
        // 账号计入错误次数 ，超过次数，锁定账号
        if (StringUtils.isNotBlank(multiOrgPwdSettingDto.getUuid())) {
            if (user.getPwdErrorNum() == null) {
                user.setPwdErrorNum(0);
            }
            user.setPwdErrorNum(user.getPwdErrorNum() + 1);
            if (IsPwdErrorLockEnum.Yes.getValue().equals(multiOrgPwdSettingDto.getIsPwdErrorLock())) {
                StringBuilder message = new StringBuilder();
                if (PwdUtils.updatePwdType.equals(pwdType)) {
                } else {
                    message.append("用户名或密码错误！\n");
                }
                if (multiOrgPwdSettingDto.getMoreTheanErrorNumber() > user.getPwdErrorNum()) {
                    message.append("账号" + user.getLoginName() + "的密码已输入错误" + user.getPwdErrorNum() + "次，");
                    message.append("如果输入错误" + multiOrgPwdSettingDto.getMoreTheanErrorNumber() + "次，账号将被锁定"
                            + multiOrgPwdSettingDto.getAccountLockMinute() + "分钟！");
                } else {
                    Date nowDate = new Date();
                    user.setLastUnLockedTime(
                            DateUtil.getNextMinute(nowDate, multiOrgPwdSettingDto.getAccountLockMinute()));
                    user.setLastLockedTime(nowDate);
                    user.setPwdErrorLock(PwdErrorLockEnum.Locked.getValue());
                    user.setLastUnLockedTime(
                            DateUtil.getNextMinute(nowDate, multiOrgPwdSettingDto.getAccountLockMinute()));
                    message.append("账号" + user.getLoginName() + "的密码已输入错误" + user.getPwdErrorNum() + "次，账号被锁定！\n");
                    message.append("锁定期间无法使用，" + DateUtils.formatDateTimeMin(user.getLastUnLockedTime()) + "将自动解锁！\n");
                    if (PwdUtils.updatePwdType.equals(pwdType)) {
                        message.append("为了您的账号安全，当前登录将被强制退出！\n");
                    }
                    message.append("isLocked:" + user.getId());
                    pwdErrorDto.setLocked(Boolean.TRUE);
                }
                pwdErrorDto.setMessage(message.toString());
            } else {
                pwdErrorDto.setMessage(closePwdErrorLockErrorStr(pwdType));
            }
        } else {
            // 没有密码配置
            pwdErrorDto.setMessage(closePwdErrorLockErrorStr(pwdType));
        }
        return pwdErrorDto;
    }

    /**
     * 【密码规则管理】中的【密码输入错误时账号锁定】关闭时：提示内容
     * 不同的入口，提示文案不同，根据密码类型区分
     *
     * @param pwdType 密码类型
     * @return java.lang.String
     **/
    private static String closePwdErrorLockErrorStr(Integer pwdType) {
        String str = "";
        if (PwdUtils.updatePwdType.equals(pwdType)) {
            str = "旧密码错误！";
        } else {
            str = "用户名或密码错误！";
        }
        return str;
    }

    /**
     * 判断包含 字母，数字，特殊字符几种
     *
     * @param pwd 未加密密码字符串
     * @return java.lang.Integer
     **/
    private static Integer includeNumber(String pwd) {
        // 字母判断
        Integer include = 0;
        if (pwd.matches(letterMatches)) {
            ++include;
        }
        // 数字判断
        if (pwd.matches(numberMatches)) {
            ++include;
        }
        // 特殊字符
        if (pwd.matches(specialCharMatches)) {
            ++include;
        }
        return include;
    }

    /**
     * 检查密码是否包含中文
     *
     * @param pwd
     * @return java.lang.Boolean
     **/
    public static Boolean checkChinese(String pwd) {
        if (pwd.matches(chineseMatches)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 字符串转换为Ascii
     *
     * @param value
     * @return
     */
    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    public static void main(String[] args) throws Exception {
        String reslut = "123456";
        if (reslut.matches(specialCharMatches)) {
            System.out.println(reslut);
        }

        // String specialCharacter =
        // "[_`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
        // String word = "[a-zA-Z]";
        // String number = "[0-9]";
        //
        // StringBuilder regexBuilder = new StringBuilder();
        // regexBuilder.append("^");
        // regexBuilder.append("[");
        // regexBuilder.append("(").append(word).append("&").append(number).append(")");
        // regexBuilder.append("|");
        // regexBuilder.append("(").append(word).append("&").append(specialCharacter).append(")");
        // regexBuilder.append("|");
        // regexBuilder.append("(").append(number).append("&").append(specialCharacter).append(")");
        // regexBuilder.append("|");
        // regexBuilder.append("(").append(word).append("&").append(number).append("&").append(specialCharacter)
        // .append(")");
        // regexBuilder.append("]");
        // regexBuilder.append("{6,20}");
        // regexBuilder.append("$");
        //
        // String input = "z34";
        //
        // String regex = regexBuilder.toString();
        // Pattern p = Pattern.compile(regex);
        // Matcher m = p.matcher(input); // 获取 matcher 对象
        //
        // System.out.println(m.matches());

    }
}
