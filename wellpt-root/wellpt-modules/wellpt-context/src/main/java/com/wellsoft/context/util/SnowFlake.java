package com.wellsoft.context.util;

import com.wellsoft.context.config.Config;

public class SnowFlake {

    /**
     * 起始的时间戳(2023.1.1 0:0:0.000)
     */
    private static final long START_STMP = 1672502400000L;
    /**
     * 数据中心标识占用的位数
     */
    private static final long DATACENTER_BIT = 5;
    /**
     * 机器标识占用的位数
     */
    private static final long MACHINE_BIT = 5;
    /**
     * 序列号占用的位数
     */
    private static final long SEQUENCE_BIT = 12;
    /**
     * 支持的最大数据中心标识id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static final long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    /**
     * 支持的最大机器id，结果是31
     */
    private static final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    /**
     * 支持的序列号id，结果是4095 (0b111111111111=0xfff=4095)
     */
    private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    /**
     * 机器ID向左移12位
     */
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    /**
     * 数据中心标识id向左移17位(12+5)
     */
    private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    static SnowFlake DEFAULT = null;

    static {
        DEFAULT = new SnowFlake(Config.getSnfDcid(), Config.getSnfMid());
    }

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;
    /**
     * 机器标识ID(0~31)
     */
    private long machineId;
    /**
     * 毫秒内序列号(0~4095)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间截
     */
    private long lastStmp = -1L;

    /**
     * 构造函数
     *
     * @param datacenterId 数据中心ID(0~31)
     * @param machineId    机器标识ID(0~31)
     */
    public SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenterId can't be greater than %d or less than 0", MAX_DATACENTER_NUM));
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(
                    String.format("machineId can't be greater than %d or less than 0", MAX_MACHINE_NUM));
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public static long getId() {
        return DEFAULT.nextId();
    }

    public static long getTinyId() {
        return DEFAULT.nextShortId();
    }

    public static long getId(long datacenterId, long machineId) {
        return new SnowFlake(datacenterId, machineId).nextId();
    }

    public static long getTinyId(long datacenterId, long machineId) {
        return new SnowFlake(datacenterId, machineId).nextShortId();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(SnowFlake.getId(0, 0));
        System.out.println(SnowFlake.getTinyId(0, 0));

    }

    /**
     * 产生下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (currStmp < lastStmp) {

            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds, lastTimestamp: %d , currentTimestamp: %d", lastStmp - currStmp, lastStmp, currStmp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (currStmp == lastStmp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                // 阻塞到下一个毫秒,获得新的时间戳
                currStmp = getNextMill();
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastStmp = currStmp;

        // 移位并通过或运算拼到一起组成64位的ID
        return (currStmp - START_STMP) << TIMESTMP_LEFT // 时间戳部分
                | datacenterId << DATACENTER_LEFT // 数据中心部分
                | machineId << MACHINE_LEFT // 机器标识部分
                | sequence; // 序列号部分
    }

    public synchronized long nextShortId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastStmp) {
            throw new RuntimeException("时钟回拨！");
        }

        if (timestamp == lastStmp) {
            sequence = (sequence + 1) & 0xFF; // 8位掩码
            if (sequence == 0) {
                timestamp = getNextMill();
            }
        } else {
            sequence = 0;
        }

        lastStmp = timestamp;
        return ((timestamp & 0xFFFFF) << 13)  // 20位时间戳
                | (machineId << 8)            // 5位机器ID
                | sequence;                  // 8位序列号
    }


    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @return 当前时间戳
     */
    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long getNewstmp() {
        return System.currentTimeMillis();
    }


}