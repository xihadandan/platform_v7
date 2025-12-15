import { isEmpty } from "lodash";

let getAppInfo = function () {
  return uni.$axios
    .get("/api/weixin/config/getEnabledAppInfo")
    .then(({ data: result }) => {
      if (result.data) {
        return Promise.resolve(result.data);
      } else {
        return Promise.reject();
      }
    })
    .catch((error) => {
      return Promise.reject();
    });
};

let extractAuthCode = function (url) {
  if (isEmpty(url) || url.indexOf("code=") === -1) {
    return null;
  }

  let code = "";
  let sourcePart = url.substring(url.indexOf("code="));
  code = sourcePart.split("=")[1];
  if (code && code.includes("&")) {
    code = code.substring(0, code.indexOf("&"));
  }
  return code;
};

let doLogin = function (authCode, options) {
  options.onLogining && options.onLogining();
  uni.$axios
    .post(`/api/weixin/auth?code=${authCode}&sso=true`)
    .then(({ data: result }) => {
      options.onSuccess && options.onSuccess(result.data);
    })
    .catch((error) => {
      options.onFail && options.onFail();
    });
};

let doSsoLogin = function (corpId, appId, options) {
  let redirectUri = window.location.href;
  if (redirectUri.includes("wework_cfm_code=")) {
    uni.showToast({
      icon: "none",
      title: options.$widget.$t("loginComponent.sso.weixinDomainFailed", "企业微信单点登录需使用应用可信域名"),
    });
    return;
  }
  let code = extractAuthCode(window.location.href);
  if (isEmpty(code)) {
    let url = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corpId}&redirect_uri=${encodeURIComponent(
      redirectUri
    )}&response_type=code&scope=snsapi_base&state=weixin_login&agentid=${appId}&#wechat_redirect`;
    window.location.href = url;
  } else {
    doLogin(code, options);
  }
};

export function ssoLogin(options) {
  getAppInfo()
    .then((appInfo) => {
      doSsoLogin(appInfo.corpId, appInfo.appId, options);
    })
    .catch(() => {
      options.onFail && options.onFail();
    });
}
