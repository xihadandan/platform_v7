let doLogin = function (authCode, options) {
  options.onLogining && options.onLogining();
  uni.$axios
    .post(`/api/dingtalk/auth?code=${authCode}&sso=true`)
    .then(({ data: result }) => {
      options.onSuccess && options.onSuccess(result.data);
    })
    .catch((error) => {
      options.onFail && options.onFail();
    });
};

let doSsoLogin = function (corpId, options) {
  if (!window.dd) {
    return Promise.reject();
  }

  options.onReady && options.onReady();
  dd.getAuthCode({
    corpId,
    success: (res) => {
      const { code } = res;
      doLogin(code, options);
    },
    fail: (err) => {
      options.onFail && options.onFail();
    },
    complete: () => {},
  });
};

let getCorpId = function () {
  return uni.$axios
    .get("/api/dingtalk/config/getEnabledCorpId")
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

export function ssoLogin(options) {
  let loadJssdkPromise = new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = "https://g.alicdn.com/dingding/dingtalk-jsapi/3.0.25/dingtalk.open.js";
    script.onload = () => {
      console.log("Dingtalk JSAPI loaded and ready");
      resolve();
    };
    script.onerror = () => {
      console.error("Error loading dingtalk ssoLogin script");
      reject();
    };
    document.head.appendChild(script);
  });

  loadJssdkPromise.then(() => {
    getCorpId()
      .then((corpId) => {
        doSsoLogin(corpId, options);
      })
      .catch(() => {
        options.onFail && options.onFail();
      });
  });
}
