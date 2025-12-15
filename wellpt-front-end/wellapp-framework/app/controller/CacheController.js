'use strict';

const Controller = require('egg').Controller;

class CacheController extends Controller {
  async deleteKey() {
    let keys = this.ctx.queries.key;
    for (let k of keys) {
      if (k.indexOf('*') != -1) {// 禁止模糊匹配删除key
        continue;
      }
      this.app.redis.del(k);
    }
    this.ctx.body = 'ok'
  }

  async deleteByPattern() {
    let cursor = '0';
    let keysDeleted = 0;
    let pattern = this.ctx.query.pattern;
    do {
      const result = await this.app.redis.scan(cursor, 'MATCH', pattern);
      cursor = result[0];
      const keys = result[1];

      if (keys.length > 0) {
        const delCount = await this.app.redis.del(...keys);
        keysDeleted += delCount;
      }
    } while (cursor !== '0');
    this.ctx.body = 'ok'
  }

  async set() {
    await this.app.redis.set(this.ctx.req.body.key, this.ctx.req.body.value);
    this.ctx.body = 'ok'
  }
}

module.exports = CacheController;
