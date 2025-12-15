var events = require('events');
const cluster = require('cluster');

class AppBootHook {
  constructor(app) {
    this.app = app;
    this._clearPageCache = (_app, pattern) => {
      _app.redis.keys(pattern || 'webapp:page:*', (err, keys) => {
        if (keys.length) {
          _app.redis.del(keys);
        }
      });
    };
  }

  async serverDidReady() {
    const { app, _clearPageCache } = this;
    if (app.config.env === 'prod' && app.redis) {
      // 清除页面缓存
      _clearPageCache(app);
      app.appEmitter.addListener('event.UserLoginSuccess', function (user) {
        _clearPageCache(app, `webapp:page:${user.userId}*`);
      });

      app.appEmitter.addListener('event.UserPreferencesUpdate', function (user, params) {
        _clearPageCache(app, `webapp:page:${user.userId}*`);
      });
    }

    if (app.redis != null && cluster.isWorker && cluster.worker.id == 1) {
      // 主题包编译为缓存内容
      const tryCompile = async () => {
        try {
          await app.createAnonymousContext().service.themeService.publishThemePackAsChunkContent();
        } catch (error) {
        }
        content = await app.redis.get('THEME_DESIGN_CSS_CONTENT');
        if (!content) {
          setTimeout(() => {
            tryCompile()
          }, 5000)
        }
      }
      let content = await app.redis.get('THEME_DESIGN_CSS_CONTENT');
      if (!content) {
        tryCompile();
      }

    }

  }
}

module.exports = AppBootHook;
