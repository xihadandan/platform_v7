let getAppId = function () {
  return uni.$axios
    .get("/api/feishu/config/getEnabledAppId")
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

let getUserTokenInfo = function (code) {
  return uni.$axios.post("/api/feishu/getUserTokenInfo", {
    code,
  });
};

let loginByUserTokenInfo = function (tokenInfo) {
  return uni.$axios.post(`/api/feishu/getUserInfo?userAccessToken=${tokenInfo.accessToken}`);
};

let doSsoLogin = function (appId, options) {
  if (!window.h5sdk) {
    return Promise.reject();
  }

  return new Promise((resolve, reject) => {
    // 通过error接口处理API验证失败后的回调
    window.h5sdk.error((err) => {
      reject();
      throw ("h5sdk error:", JSON.stringify(err));
    });
    // 通过ready接口确认环境准备就绪后才能调用API
    window.h5sdk.ready(() => {
      console.log("window.h5sdk.ready");
      console.log("url:", window.location.href);
      options.onReady && options.onReady();
      // 调用JSAPI tt.requestAccess 获取 authorization code
      tt.requestAccess({
        appID: appId,
        scopeList: [],
        // 获取成功后的回调
        success(res) {
          console.log("getAuthCode succeed");
          //authorization code 存储在 res.code
          // 此处通过fetch把code传递给接入方服务端Route: callback，并获得user_info
          // 服务端Route: callback的具体内容请参阅服务端模块server.py的callback()函数
          getUserTokenInfo(res.code)
            .then(({ data: result }) => {
              if (result.code == 0) {
                options.onLogining && options.onLogining();
                loginByUserTokenInfo(result.data)
                  .then(({ data: result }) => {
                    options.onSuccess && options.onSuccess(result.data);
                    resolve();
                  })
                  .catch(() => {
                    options.onFail && options.onFail();
                    reject();
                  });
              } else {
                options.onFail && options.onFail();
                reject();
              }
            })
            .catch(function (e) {
              options.onFail && options.onFail();
              reject();
              console.error(e);
            });
        },
        // 获取失败后的回调
        fail(err) {
          options.onFail && options.onFail();
          reject();
          console.log(`getAuthCode failed, err:`, JSON.stringify(err));
        },
      });
    });
  });
};

export function ssoLogin(options) {
  let loadJssdkPromise = new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = "https://lf1-cdn-tos.bytegoofy.com/goofy/lark/op/h5-js-sdk-1.5.26.js";
    script.onload = () => {
      console.log("Feishu JSJDK loaded and ready");
      resolve();
    };
    script.onerror = () => {
      console.error("Error loading feishu ssoLogin script");
      reject();
    };
    document.head.appendChild(script);
  });
  let loadVConsolePromise = new Promise((resolve, reject) => {
    resolve();
    // const script = document.createElement('script');
    // script.src = 'https://unpkg.com/vconsole/dist/vconsole.min.js';
    // script.onload = () => {
    //   console.log('Feishu VConsole loaded and ready');
    //   var vConsole = new window.VConsole();
    //   resolve();
    // };
    // script.onerror = () => {
    //   console.error('Error loading feishu ssoLogin script');
    //   reject();
    // };
    // document.head.appendChild(script);
  });

  return Promise.all([loadJssdkPromise, loadVConsolePromise])
    .then(() => {
      return getAppId().then((appId) => {
        return doSsoLogin(appId, options);
      });
    })
    .catch(() => {
      return Promise.reject();
    });
}
