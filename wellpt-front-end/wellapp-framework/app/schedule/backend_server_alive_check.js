'use strict';

module.exports = {
  schedule: {
    disable: true,
    interval: '3s', //
    type: 'all', // 指定所有的 worker 都需要执行
  },
  async task(ctx) {
    try {
      const result = await ctx.curl(ctx.app.config.backendURL + '/ops/check', {
        method: 'POST', contentType: 'json', dataType: 'text', timeout: 1000,
      });
      if (ctx.app.backendServerIsOK !== undefined && !ctx.app.backendServerIsOK && result.data === 'ok') {
        ctx.curl(ctx.app.config.backendURL + '/webapp/syncAppComponent', {
          method: 'POST', contentType: 'json', dataType: 'json',
          data: ctx.app.registerAppComponent,
        });
        ctx.app.backendServerIsOK = true;
      }
    } catch (error) {
      ctx.app.logger.error('后端服务不可用！');
      ctx.app.backendServerIsOK = false;
    }
  },
};
