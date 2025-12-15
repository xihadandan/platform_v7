package com.wellsoft.distributedlog.jedis;

/**
 * Description: jedis配置
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
public class JedisConfig {

    // redis 节点，例如：192.168.0.1:6379 多个用,或者;号区隔
    private String redisNodes;

    private Integer poolMaxTotal = 50;

    private Integer poolMinIdle = 10;

    private Integer poolMaxIdle = 20;

    private String masterName;

    private String password;

    private int database;

    private Integer timemout = 2000;

    public JedisConfig(String redisNodes, Integer poolMaxTotal, Integer poolMinIdle, Integer poolMaxIdle, String masterName, String password) {
        this.redisNodes = redisNodes;
        if (poolMaxTotal != null) {
            this.poolMaxTotal = poolMaxTotal;
        }
        if (poolMinIdle != null) {
            this.poolMinIdle = poolMinIdle;
        }
        if (poolMaxIdle != null) {
            this.poolMaxIdle = poolMaxIdle;
        }
        this.masterName = masterName;
        this.password = password;
    }

    public String getRedisNodes() {
        return redisNodes;
    }

    public Integer getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public Integer getPoolMinIdle() {
        return poolMinIdle;
    }

    public Integer getPoolMaxIdle() {
        return poolMaxIdle;
    }

    public String getMasterName() {
        return masterName;
    }

    public String getPassword() {
        return password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public Integer getTimemout() {
        return timemout;
    }

    public void setTimemout(Integer timemout) {
        this.timemout = timemout;
    }
}
