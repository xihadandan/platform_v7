const events = require('events');
const path = require('path');
const fs = require('fs');
const cluster = require('cluster');
class AppBootHook {
  constructor(app) {
    this.app = app;
  }

  async serverDidReady() {
    const { app } = this;
    const _this = this;
    if (app.config.env !== 'local' && app.redis != null && cluster.isWorker && cluster.worker.id == 1) {
      this.initLoginConf();

      // redis 订阅后端通信消息
      app.subRedis.subscribe('wellappLoginPageSetting', (err, count) => {
        if (err) {
          console.error('subscribe redis channel = %s failed , error : %s', 'wellappLoginPageSetting', err.message);
        } else {
          console.log('subscribe redis channel = %s success', 'wellappLoginPageSetting');
        }
      });
      app.subRedis.on('message', async (channel, msg) => {
        try {
          if (channel === 'wellappLoginPageSetting') {
            app.logger.info('redis on message -> channel: %s , msg: %s', channel, msg);
            let message = JSON.parse(msg);
            const ctx = await app.createAnonymousContext();
            let results = await ctx.service.appLoginConfService.getLoginPageConfig(message.systemUnitId, true);
            _this.writeLoginConfImage2FileSystem(results.data);
          }
        } catch (error) {
          app.logger.error('消息订阅发送异常：', error);
        }
      });
    }
  }

  async initLoginConf() {
    const { app } = this;
    const _this = this;
    try {
      // 加载登录页到redis
      const ctx = await app.createAnonymousContext();
      let results = await ctx.service.appLoginConfService.getAllLoginPageSettings();
      if (results) {
        for (let i = 0, len = results.length; i < len; i++) {
          _this.writeLoginConfImage2FileSystem(results[i]);
          // (function (data) {
          //   _this.writeLoginConfImage2FileSystem(data);
          // })(results[i]);
        }
      }
    } catch (error) {
      setTimeout(() => {
        _this.initLoginConf();
      }, 3000);
    }
  }

  async writeLoginConfImage2FileSystem(loginConfig) {
    const { app } = this;
    let _jsonString = JSON.stringify(loginConfig);
    app.redis.set(app.redis.keyWrapper(`wellapp:loginPageSetting:${loginConfig.systemUnitId}`), _jsonString);
    if (loginConfig.unitLoginPageUri) {
      app.redis.set(app.redis.keyWrapper(`wellapp:loginPageSetting:${loginConfig.unitLoginPageUri}`), _jsonString);
    }

    // 保存图片到本地
    if (loginConfig.pageBackgroundImageBase64) {
      fs.writeFile(
        path.join(app.baseDir, './app/public', `login_page_bg_${loginConfig.systemUnitId}.png`),
        Buffer.from(loginConfig.pageBackgroundImageBase64, 'base64'),
        () => {}
      );
    }
    if (loginConfig.pageLogoBase64) {
      fs.writeFile(
        path.join(app.baseDir, './app/public', `login_page_logo_${loginConfig.systemUnitId}.png`),
        Buffer.from(loginConfig.pageLogoBase64, 'base64'),
        () => {}
      );
    }
  }
}

module.exports = AppBootHook;
