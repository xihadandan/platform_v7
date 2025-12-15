/*
 * @(#)2014-3-18 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-18.1	zhulh		2014-3-18		Create
 * </pre>
 * @date 2014-3-18
 */
public class ProjectContastants {
    //所属分类
    public static final Map<String, String> ssflMap = new HashMap<String, String>();
    //是否开发区
    public static final Map<String, String> sfkaqMap = new HashMap<String, String>();
    //项目统计表示
    public static final Map<String, String> xmtjMap = new HashMap<String, String>();
    //建筑性质
    public static final Map<String, String> jzxzMap = new HashMap<String, String>();
    //建筑等级
    public static final Map<String, String> jzdjMap = new HashMap<String, String>();
    //项目性质
    public static final Map<String, String> xmxzMap = new HashMap<String, String>();
    //是否保密
    public static final Map<String, String> sfbmMap = new HashMap<String, String>();
    //敏感性质
    public static final Map<String, String> mgxzMap = new HashMap<String, String>();
    //资金来源
    public static final Map<String, String> zjlyMap = new HashMap<String, String>();
    //协调项目
    public static final Map<String, String> xtxmMap = new HashMap<String, String>();
    //重点工程
    public static final Map<String, String> zdgcMap = new HashMap<String, String>();
    //重点跟踪
    public static final Map<String, String> zdgzMap = new HashMap<String, String>();
    //行政分区
    public static final String DOMDISTRICT_TYPE = "XZSP_ADMINISTRATIVE_AREAS";
    public static final Map<String, String> xzfqMap = new HashMap<String, String>();
    //土地供应方式
    public static final Map<String, String> tdgyfsMap = new HashMap<String, String>();
    //建设单位||设计单位
    public static final Map<String, String> jsdwMap = new HashMap<String, String>();

    static {
        //旧：0建设类 1财政 2消防 9其他
        //新：1建设类项目 2非建设类项目 3财政类项目 4其他
        ssflMap.put("0", getJsonData("1", "建设类项目"));
        ssflMap.put("1", getJsonData("3", "财政类项目"));
        ssflMap.put("9", getJsonData("4", "其他"));
    }

    static {
        //旧：0建设类 1财政 2消防 9其他
        //新：1建设类项目 2非建设类项目 3财政类项目 4其他
        sfkaqMap.put("0", getJsonData("0", "否"));
        sfkaqMap.put("1", getJsonData("1", "是"));
    }

    static {
        //旧：0建设类 1财政 2消防 9其他
        //新：1建设类项目 2非建设类项目 3财政类项目 4其他
        xmtjMap.put("0", getJsonData("0", "无效"));
        xmtjMap.put("1", getJsonData("1", "有效"));
    }

    static {
        //旧：0未知 1改建 2扩建 3新建
        //新：{"1":"新建","2":"改建","3":"扩建","4":"未知"}
        jzxzMap.put("0", getJsonData("4", "未知"));
        jzxzMap.put("1", getJsonData("2", "改建"));
        jzxzMap.put("2", getJsonData("3", "扩建"));
        jzxzMap.put("3", getJsonData("1", "新建"));
    }

    static {
        //旧：0未知 1特级 2一级 3二级 4三级
        //新：1一级 2二级 3三级 4特级 5未知
        jzdjMap.put("0", getJsonData("5", "未知"));
        jzdjMap.put("1", getJsonData("4", "特级"));
        jzdjMap.put("2", getJsonData("1", "一级"));
        jzdjMap.put("3", getJsonData("2", "二级"));
        jzdjMap.put("4", getJsonData("3", "三级"));
    }

    static {
        //旧 ：0未知 1商品房 2工业项目 3基础设施 4居住建筑 5装修 6公共建筑 7其它
        //新： 1商品房 2工业项目 3基础设施 4居住建筑 5装修 6公共建筑 7市政道路 8其它 9未知
        xmxzMap.put("0", getJsonData("9", "未知"));
        xmxzMap.put("1", getJsonData("1", "商品房"));
        xmxzMap.put("2", getJsonData("2", "工业项目"));
        xmxzMap.put("3", getJsonData("3", "基础设施"));
        xmxzMap.put("4", getJsonData("4", "居住建筑"));
        xmxzMap.put("5", getJsonData("5", "装修"));
        xmxzMap.put("6", getJsonData("6", "公共建筑"));
        xmxzMap.put("7", getJsonData("8", "其它"));
    }

    static {
        //旧 ：0未知 1保密 2非保密
        //新：{"1":"保密","2":"非保密","3":"未知"}
        sfbmMap.put("0", getJsonData("3", "未知"));
        sfbmMap.put("1", getJsonData("1", "保密"));
        sfbmMap.put("2", getJsonData("2", "非保密"));
    }

    static {
        //旧 ：0未知 1敏感 2非敏感
        //新：1敏感 2非敏感 3未知
        mgxzMap.put("0", getJsonData("3", "未知"));
        mgxzMap.put("1", getJsonData("1", "敏感"));
        mgxzMap.put("2", getJsonData("2", "非敏感"));
    }

    static {
        //旧 ：0未知 1财政 2非财政
        //新：{"1":"财政","2":"非财政","3":"未知"}
        zjlyMap.put("0", getJsonData("3", "未知"));
        zjlyMap.put("1", getJsonData("1", "财政"));
        zjlyMap.put("2", getJsonData("2", "非财政"));
    }

    static {
        //旧 ：0未知 1协调项目 2非协调项目
        //新：1协调项目 2非协调项目 3未知
        xtxmMap.put("0", getJsonData("3", "未知"));
        xtxmMap.put("1", getJsonData("1", "协调项目"));
        xtxmMap.put("2", getJsonData("2", "非协调项目"));
    }

    static {
        //旧 ：0未知 1重点工程 2非重点工程
        //新：{"1":"重点工程","2":"非重点工程","3":"未知"}
        zdgcMap.put("0", getJsonData("3", "未知"));
        zdgcMap.put("1", getJsonData("1", "重点工程"));
        zdgcMap.put("2", getJsonData("2", "非重点工程"));
    }

    static {
        //旧 ：0未知 1重点跟踪 2非重点跟踪
        //新：1重点跟踪 2非重点跟踪 3未知
        zdgzMap.put("0", getJsonData("3", "未知"));
        zdgzMap.put("1", getJsonData("1", "重点跟踪"));
        zdgzMap.put("2", getJsonData("2", "非重点跟踪"));
    }

    static {
        //旧 ：0未知 350203思明区 350205海沧区 350206湖里区 350211集美区 350212同安区 350213翔安区
        //新：350200福建省厦门市 350203思明区 350205海沧区 350206湖里区 350211集美区 350212同安区 350213翔安区
        xzfqMap.put("350200", getJsonData("350200", "福建省厦门市"));
        xzfqMap.put("000000", getJsonData("350200", "福建省厦门市"));
        xzfqMap.put("350203", getJsonData("350203", "思明区"));
        xzfqMap.put("350205", getJsonData("350205", "海沧区"));
        xzfqMap.put("350206", getJsonData("350206", "湖里区"));
        xzfqMap.put("350211", getJsonData("350211", "集美区"));
        xzfqMap.put("350212", getJsonData("350212", "同安区"));
        xzfqMap.put("350213", getJsonData("350213", "翔安区"));
    }

    static {
        //旧 ：0未知 1划拨土地 2不涉及农用地 3涉及农用地
        //新：1划拨土地 2不涉及农用地 3涉及农用地 4未知
        tdgyfsMap.put("0", getJsonData("4", "未知"));
        tdgyfsMap.put("1", getJsonData("1", "划拨土地"));
        tdgyfsMap.put("2", getJsonData("2", "不涉及农用地"));
        tdgyfsMap.put("3", getJsonData("3", "涉及农用地"));
    }

    static {
        jsdwMap.put("01", "设计单位");
        jsdwMap.put("02", "施工单位");
        jsdwMap.put("03", "建设单位");
        jsdwMap.put("04", "施工分包单位");
    }

    public static String getJsonData(String key, String value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
        return jsonObject.toString();
    }
}
