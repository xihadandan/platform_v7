"use strict";

const getLoginProvider = function (callback) {
  uni.getProvider({
    service: "oauth",
    success: (result) => {
      let providerList = result.provider.map((value) => {
        let providerName = "";
        switch (value) {
          case "weixin":
            providerName = "微信登录";
            break;
          case "qq":
            providerName = "QQ登录";
            break;
          case "sinaweibo":
            providerName = "新浪微博登录";
            break;
          case "xiaomi":
            providerName = "小米登录";
            break;
          case "alipay":
            providerName = "支付宝登录";
            break;
          case "baidu":
            providerName = "百度登录";
            break;
          case "jd":
            providerName = "京东登录";
            break;
          case "toutiao":
            providerName = "头条登录";
            break;
          case "apple":
            providerName = "苹果登录";
            break;
          case "univerify":
            providerName = "一键登录";
            break;
        }
        return {
          text: providerName,
          value: value,
        };
      });

      if (callback) {
        callback.call(this, providerList);
      }
    },
    fail: (error) => {
      console.log("获取登录通道失败", error);
    },
  });
};

// 一键登录
const univerifyLogin = function (provider, univerifyCloudFunctionName, successCallback, failCallback) {
  let providerId = provider.value;
  // 一键登录已在APP onLaunch的时候进行了预登陆，可以显著提高登录速度。登录成功后，预登陆状态会重置
  uni.login({
    provider: providerId,
    success: (res) => {
      let authResult = res.authResult;
      // 登录成功，可以关闭一键登录授权界面了
      uni.closeAuthView();
      // 在得到access_token后，通过callfunction调用云函数
      uniCloud
        .callFunction({
          name: univerifyCloudFunctionName, // 你的云函数名称
          data: {
            access_token: authResult.access_token, // 客户端一键登录接口返回的access_token
            openid: authResult.openid, // 客户端一键登录接口返回的openid
          },
        })
        .then((res) => {
          console.log("res", res);

          if (successCallback) {
            successCallback.call(this, res.phoneNumber);
          }
        })
        .catch((err) => {
          // 处理错误
        });
    },
    fail: (err) => {
      console.log("login fail:", err);

      // 一键登录点击其他登录方式
      if (err.code == "30002") {
        uni.closeAuthView();
      } else if (err.code == 1000) {
        // 未开通
        uni.showModal({
          title: "登录失败",
          content: `${err.errMsg}\n，错误码：${err.code}`,
          confirmText: "开通指南",
          cancelText: "确定",
          success: (res) => {
            if (res.confirm) {
              setTimeout(() => {
                plus.runtime.openWeb("https://ask.dcloud.net.cn/article/37965");
              }, 500);
            }
          },
        });
      } else if (err.code == "30005") {
        // 一键登录预登陆失败
        uni.showModal({
          showCancel: false,
          title: "预登录失败",
          content: err.errMsg,
        });
      } else if (err.code != "30003") {
        // 一键登录用户关闭验证界面
        uni.showModal({
          showCancel: false,
          title: "登录失败",
          content: JSON.stringify(err),
        });
      }

      if (failCallback) {
        failCallback.call(this, err);
      }
    },
    complete: () => {},
  });
};

module.exports = {
  getLoginProvider,
  univerifyLogin,
};
