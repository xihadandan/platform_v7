'use strict';
const QRCode = require('qrcode');
const Controller = require('wellapp-framework').Controller;

class MobileAppController extends Controller {
  async qrcode() {
    const { ctx, app } = this;
    let response = ctx.response;
    let content = ctx.request.URL.toString().replace('qrcode', 'download');
    let buf = await QRCode.toBuffer(content, {
      type: 'png',
      width: 127
    });
    response.type = 'image/png';
    response.set('Expires', 0);
    response.set('prama', 'no-cache');
    response.set('coche-control', 'no-cache');
    ctx.body = buf;
  }

  async download() {
    const { ctx, app } = this;
    try {
      let helper = ctx.helper;
      let os = helper.mobileDetect.os();
      if (os === 'AndroidOS') {
        ctx.redirect(helper.staticPrefix() + '/mobile/android/wellim.apk');
        return;
      } else if (os === 'iOS' || os === 'iPadOS') {
        ctx.redirect('https://apps.apple.com/cn/app/%E5%A8%81%E5%B0%94%E4%BF%A1%E6%81%AF%E5%8A%9E%E5%85%AC%E5%8A%A9%E6%89%8B/id1458704320');
        return;
      }
      ctx.redirect(helper.staticPrefix() + '/mobile/mobile.zip');
    } catch (e) {
      app.logger.error('获取文件异常：%s', e);
    }
  }


  async setUniappTempDesignData() {
    const { ctx, app } = this;
    await ctx.app.redis.set(ctx.req.body.id, ctx.req.body.json, 'EX', 60 * 60);  // 只保存 1 小时
    ctx.body = 'ok';
  }

  async getUniappTempDesignData() {
    const { ctx, app } = this;
    if (ctx.query.jsonid) {
      let json = await app.redis.get(ctx.query.jsonid);
      ctx.body = json;
    } else {
      ctx.body = null;
    }

  }
}

module.exports = MobileAppController;
