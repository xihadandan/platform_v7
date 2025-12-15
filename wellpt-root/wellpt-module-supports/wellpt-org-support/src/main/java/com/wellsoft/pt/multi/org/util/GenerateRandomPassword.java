package com.wellsoft.pt.multi.org.util;

import com.wellsoft.pt.multi.org.enums.LetterLimitedEnum;

import java.util.Random;

/**
 * 按一定的概率生成一个随机的N位(N>=3)密码，必须由字母数字特殊符号组成，三者缺一不可
 * <ul>
 * <li>数字: 0-9</li>
 * <li>字母: A-Za-z</li>
 * <li>特殊符号: `~!@#$%^&*()-_=+[]{}\|;:'",<.>/?</li>
 * </ul>
 */
public class GenerateRandomPassword {

    private static final byte INDEX_NUMBER = 0;
    private static final byte INDEX_LETTER = 1;
    private static final byte INDEX_SPECIAL_CHAR = 2;

    /**
     * 特殊符号
     */
    private static final char[] SPECIAL_CHARS = {'`', '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_',
            '=', '+', '[', ']', '{', '}', '\\', '|', ';', ':', '\'', '"', ',', '<', '.', '>', '/', '?'};

    /**
     * 按一定的概率生成一个随机的N位(N>=3)密码，必须由字母数字特殊符号组成，三者缺一不可
     *
     * @param len             密码长度,必须大于等于3
     * @param paramGenChances 分别是生成数字、字母、特殊符号的概率
     * @param letterLimited   字母限制 LL01 : 是（必须要有大写、小写 密码中存在字母时，对字母的限制） LL02:否
     * @return 生成的随机密码
     */
    public static char[] generateRandomPassword(final int len, final byte[] paramGenChances, String letterLimited)
            throws IllegalArgumentException {
        Boolean isExistCapitalLetter = false;
        Boolean isExistSmallLetter = false;
        if (len < 3) {
            throw new IllegalArgumentException("len must not smaller than 3, but now is " + len);
        }
        final char[] password = new char[len];
        // 之所以该复制一份是为了使函数不对外产生影响
        final byte[] genChances = paramGenChances.clone();
        final byte[] genNums = new byte[genChances.length];
        for (byte i = 0; i < genChances.length; i++) {
            genNums[i] = 0;
        }
        final Random random = new Random();
        int r;
        for (int i = 0; i < len; i++) {
            adjustGenChance(len, i, genChances, genNums);
            byte index = getPasswordCharType(random, genChances);
            genNums[index]++;
            switch (index) {
                case INDEX_NUMBER:
                    password[i] = (char) ('0' + random.nextInt(10));
                    break;
                case INDEX_LETTER:
                    if (LetterLimitedEnum.Yes.getValue().equals(letterLimited)) {
                        if (!isExistCapitalLetter) {
                            // 强制大写
                            r = random.nextInt(26);
                            password[i] = (char) ('A' + r);
                            isExistCapitalLetter = true;
                        } else if (!isExistSmallLetter) {
                            // 强制小写
                            r = random.nextInt(26) + 26;
                            password[i] = (char) ('a' + r - 26);
                            isExistSmallLetter = true;
                        } else {
                            r = random.nextInt(52);
                            if (r < 26) {
                                password[i] = (char) ('A' + r);
                            } else {
                                password[i] = (char) ('a' + r - 26);
                            }
                        }
                    } else {
                        r = random.nextInt(52);
                        if (r < 26) {
                            password[i] = (char) ('A' + r);
                        } else {
                            password[i] = (char) ('a' + r - 26);
                        }
                    }

                    break;
                case INDEX_SPECIAL_CHAR:
                    r = random.nextInt(SPECIAL_CHARS.length);
                    password[i] = SPECIAL_CHARS[r];
                    break;
                default:
                    password[i] = ' ';
                    break;
            }
        }
        // logChances(genNums);
        return password;
    }

    /**
     * 根据当前需要生成密码字符的位置,动态调整生成概率
     *
     * @param len        待生成的总长度
     * @param index      当前位置
     * @param genChances 分别是生成数字、字母、特殊符号的概率
     * @param genNums    这些类型已经生成过的次数
     */
    private static void adjustGenChance(final int len, final int index, final byte[] genChances, final byte[] genNums) {
        final int leftCount = len - index;
        byte notGenCount = 0;
        for (byte i = 0; i < genChances.length; i++) {
            if (genNums[i] == 0) {
                notGenCount++;
            }
        }
        if (notGenCount > 0 && leftCount < genChances.length && leftCount == notGenCount) {
            for (byte i = 0; i < genChances.length; i++) {
                if (genNums[i] > 0) {
                    genChances[i] = 0;
                }
            }
        }
    }

    /**
     * 获取该密码字符的类型
     *
     * @param random     随机数生成器
     * @param genChances 分别是生成数字、字母、特殊符号的概率
     * @return 密码字符的类型
     */
    private static byte getPasswordCharType(final Random random, final byte[] genChances) {
        int total = 0;
        byte i = 0;
        for (; i < genChances.length; i++) {
            total += genChances[i];
        }
        int r = random.nextInt(total);
        for (i = 0; i < genChances.length; r -= genChances[i], i++) {
            if (r < genChances[i]) {
                break;
            }
        }
        return i;
    }

    public static void main(String[] args) {
        test3();
        // test2();
    }

    /// **
    // * 打印生成密码中各类字符的个数
    // */
    // private static void logChances(byte[] genNums) {
    // StringBuilder sb = new StringBuilder();
    // sb.append("{");
    // for (byte i = 0; i < genNums.length; i++) {
    // sb.append(genNums[i]);
    // if (i != genNums.length - 1) {
    // sb.append(", ");
    // }
    // }
    // sb.append("}");
    // System.out.println(sb.toString());
    // }

    private static void test3() {
        byte[] genChances = {3, 6, 1};
        char[] password = generateRandomPassword(20, genChances, "LL01");
        System.out.println(new String(password));
    }

    // private static void test1() {
    // byte[] genChances = { 5, 4, 1 };
    // for (int i = 3; i < 200; i += 30) {
    // try {
    // char[] password = generateRandomPassword(i, genChances,"LL01");
    // System.out.println(password);
    // } catch (IllegalArgumentException e) {
    // e.printStackTrace();
    // }
    // }
    // System.out.println();
    // }
    //
    // private static void test2() {
    // byte[] genChances = { 2, 5, 3 };
    // for (int i = 3; i < 200; i += 30) {
    // try {
    // char[] password = generateRandomPassword(i, genChances);
    // System.out.println(password);
    // } catch (IllegalArgumentException e) {
    // e.printStackTrace();
    // }
    // }
    // System.out.println();
    // }

}