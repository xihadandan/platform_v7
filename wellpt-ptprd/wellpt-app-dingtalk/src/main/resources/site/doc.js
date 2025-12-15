[
    {
        "url": "/mobile/pt/dingtalk/start",
        "name": "跳转认证地址",
        "type": "GET|POST",
        "data": {
            "uri": "/passport/user/login/success"
        },
        "result": {
            "http.statusCode": "302",
            "http.Location": "https://oapi.dingtalk.com/connect/oauth2/sns_authorize?appid=dingoatqql5whogyedauzj&response_type=code&scope=snsapi_auth&state=STATE&redirect_uri=http%3A%2F%2Foa.well-soft.com%3A11085%2Fmobile%2Fpt%2Fdingtalk%2Fauth%3Furi%3D%252Fpassport%252Fuser%252Flogin%252Fsuccess"
        }
    },
    {
        "url": "/mobile/pt/dingtalk/auth",
        "name": "钉钉认证地址",
        "type": "GET|POST",
        "data": {
            "uri": "/passport/user/login/success",
            "code": "3132141234"
        },
        "result": {
            "http.statusCode": "302",
            "http.Location": "/passport/user/login/success"
        }
    },
    {
        "url": "/mobile/pt/dingtalk/getJsApiConfig",
        "name": "获取钉钉JSAPI配置",
        "type": "GET|POST",
        "result": {
            "timeStamp": 1594797443,
            "agentId": "771445396",
            "jsticket": "oLzLoZRgkQVvswYcFswrlpCaO7IsaHzXJZrELNIdp9ljsUVqnbiKl6xzGYLUkupdVZp5M5qdb8ec4SEElU58xZ",
            "corpId": "dinge4fcddb96644fd9d35c2f4657eb6378f",
            "signature": "5adbaf2062c0b7072e447f0bedaa20ccb3e05f94",
            "nonceStr": "6Ib4ZcBC"
        }
    },
    {
        "url": "/mobile/pt/dingtalk/getconnect/qrconnect",
        "name": "对应钉钉使用钉钉提供的扫码登录页面。获取免密登录的二维码地址",
        "type": "GET|POST",
        "data": {
            "uri": "/passport/user/login/success"
        },
        "result": "https://oapi.dingtalk.com/connect/qrconnect?appid=dingoatqql5whogyedauzj&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=http%3A%2F%2Foa.well-soft.com%3A11085%2Fmobile%2Fpt%2Fdingtalk%2Fauth%3Furi%3D%252Fpassport%252Fuser%252Flogin%252Fsuccess"
    },
    {
        "url": "/mobile/pt/dingtalk/getconnect/oauth2",
        "name": "对应钉钉支持网站将钉钉登录二维码内嵌到自己页面中。获取免密登录的goto地址",
        "type": "GET|POST",
        "data": {
            "uri": "/passport/user/login/success",
            "loginTmpCode": "13421341234",
        },
        "result": "https://oapi.dingtalk.com/connect/qrconnect?appid=dingoatqql5whogyedauzj&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=http%3A%2F%2Foa.well-soft.com%3A11085%2Fmobile%2Fpt%2Fdingtalk%2Fauth%3Furi%3D%252Fpassport%252Fuser%252Flogin%252Fsuccess"
    },
    {
        "url": "/pt/dingtalk/syncOrg",
        "name": "组织同步接口",
        "type": "GET|POST",
        "result": {
            "success": true,
            "msg": "success",
            "data": null
        }
    },
    {
        "url": "/pt/dingtalk/syncOrgStatue",
        "name": "组织同步状态",
        "type": "GET|POST",
        "result": {
            "success": true,
            "msg": "success",
            "data": {
                "code": 2,
                "msg": "Exception"
            }
        }
    },
    {
        "url": "/pt/dingtalk/registerCallBack",
        "name": "注册业务事件回调接口",
        "type": "GET|POST",
        "data": {
            "call_back_tag": "[\"user_active_org\"]"
        },
        "result": {
            "success": true,
            "msg": "success"
        }
    },
    {
        "url": "/pt/dingtalk/updateCallBack",
        "name": "更新业务事件回调接口",
        "type": "GET|POST",
        "data": {
            "call_back_tag": "[\"user_active_org\"]"
        },
        "result": {
            "success": true,
            "msg": "success"
        }
    },
    {
        "url": "/pt/dingtalk/getCallBack",
        "name": "查询事件回调接口",
        "type": "GET|POST",
        "result": {
            "success": true,
            "msg": "success",
            "data": {
                "errcode": 0,
                "call_back_tag": ["label_conf_del", "user_active_org", "org_dept_create", "org_dept_modify",
                    "label_user_change", "user_leave_org", "label_conf_add", "user_add_org", "user_modify_org",
                    "org_dept_remove", "label_conf_modify"],
                "aes_key": "SsGZ3O54Bou8rCho1yukSmehzHzleO5qPAJYCF5jsig",
                "errmsg": "ok",
                "url": "http://oa.well-soft.com:18085/mobile/pt/dingtalk/callback",
                "token": "6Ib4ZcBC"
            },
        }
    }, {
    "url": "/pt/dingtalk/deleteCallBack",
    "name": "删除事件回调接口",
    "type": "GET|POST",
    "result": {
        "success": true,
        "msg": "success",
        "data": {
            "errcode": 0,
            "errmsg": "ok"
        },
    }
}, {
    "url": "/pt/dingtalk/saveConfig",
    "name": "保存钉钉模块配置",
    "type": "GET|POST",
    "data": {
        "jsonParams": {
            "org.org_version_id": "V0000000439",
            "sns_app_id": "dingoatqql5whogyedauzj",
            "org.org_version_name": "建管-厦门市-行政审批-1.0",
            "agent_id": "771445396",
            "event_call_back_token": "6Ib4ZcBC",
            "org.system_unit_id": "S0000000078",
            "org.system_unit_name": "建管-厦门市",
            "push.mode": "async",
            "org.sync_last_message": "{\"code\":1,\"time\":\"2020-07-14 17:09\"}",
            "app_aes_key": "SsGZ3O54Bou8rCho1yukSmehzHzleO5qPAJYCF5jsig",
            "corp_domain_uri": "http://oa.well-soft.com:11085",
            "app_key": "dingqlg6zwq3oiaepp53",
            "base_uri": "https://oapi.dingtalk.com",
            "sns_app_secret": "Ve7FHvJ6EcyR1EY69J_emyk3n2eJWAas6LTszqHTBMSUFvfZDzd55MiAyZREWNEU",
            "app_secret": "SLytpbwT6oIQ3JpxgRTNmmYkzUW6F_FJCWVYjkiJ_6v6z4AaKhP1aat8FTFWmpHS",
            "corp_id": "dinge4fcddb96644fd9d35c2f4657eb6378f",
            "org.self_business_unit_id": "B0000000225"
        }
    },
    "result": {
        "success": true,
        "msg": "success"
    }
}]