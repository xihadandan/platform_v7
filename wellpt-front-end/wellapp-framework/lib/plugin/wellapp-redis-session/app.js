'use strict';

const assert = require('assert');
const lodash = require('lodash');

module.exports = app => {
  const name = app.config.wellappRedisSession.name;
  const redis = name ? app.redis.get(name) : app.redis;
  assert(redis, `redis instance [${name}] not exists`);

  app.sessionStore = {
    async get(key, maxAge, options) {
      const res = await redis.get(key);
      if (!res) return null;
      let _session = JSON.parse(res);
      if (_session.passport && _session.passport.user) {
        // this.setOnlineSession(_session.passport.user, key);
      }
      return _session;
    },

    async set(key, value, maxAge, options) {
      let ctx = options.ctx;
      let ip =
        ctx.req.headers['x-forwarded-for'] ||
        ctx.req.connection.remoteAddress ||
        ctx.req.socket.remoteAddress ||
        (ctx.req.connection.socket ? ctx.req.connection.socket.remoteAddress : null);
      if (ip === '::1') {
        ip = '127.0.0.1';
      } else if (ip.startsWith('::ffff:')) {
        ip = ip.substring(7); // 处理 ::ffff:127.0.0.1 这种情况
      }

      if (value.passport && value.passport.user && options && options.ctx && options.ctx.AUTHENTICATE_PASSPORT) {
        ctx.logger.info('用户[%s]登录，ip[%s]', value.passport.user.loginName, ctx.req.connection.remoteAddress);
        // let forbidMultiDeviceLogin = app.config.wellappRedisSession.forbidMultiDeviceLogin === true;
        // if (value.passport.user.isAllowMultiDeviceLogin === false) {
        //   forbidMultiDeviceLogin = true;
        // }

        // if (forbidMultiDeviceLogin) {
        //   // 不允许多设备登录的情况
        //   let members = await redis.smembers('session:ip:' + value.passport.user.loginName);
        //   for (let i = 0, len = members.length; i < len; i++) {
        //     let parts = members[i].split('__');
        //     if (parts[1] !== ip) {
        //       redis.del(parts[0]);
        //       redis.srem('session:ip:' + value.passport.user.loginName, members[i]); // 移除session ip
        //       redis.set('session:kick:ip' + parts[0], ip, 'EX', 60 * 60 * 24); // 标记为下线
        //     }
        //   }
        // }
        // redis.sadd('session:ip:' + value.passport.user.loginName, key + '__' + ip); // 添加session ip
        if (value.passport.user.loginTime == undefined) {
          value.passport.user.loginTime = new Date().getTime();
          this.setOnlineSession(value.passport.user, key, ctx);
        }

        // // 登录用户存在的所有会话key
        // redis.sadd(`loginUser:session:${value.passport.user.loginName}`, key, 'EX', 60 * 60 * 24);
        // 登录用户十分钟在线检测状态缓存
      }

      if (maxAge !== 'session') {
        await redis.set(key, JSON.stringify(value), 'PX', maxAge);
      } else {
        await redis.set(key, JSON.stringify(value), 'EX', 60 * 60 * 24); // 浏览器会话情况下，默认1天时效
      }
    },

    async setOnlineSession(user, key, ctx) {
      // 登录用户十分钟在线检测状态缓存
      redis.set(`session:online:${user.loginName}`, '1', 'EX', 60 * (ctx.app.config.userOfflineTime || 10));
      redis.set(
        `session:key:${key}`,
        JSON.stringify(
          lodash.pick(user, ['loginName', 'userId', 'userName', 'userUuid', 'userSystemOrgDetails', 'loginTime', 'roles', 'userNamePy'])
        ),
        'EX',
        60 * 3
        //  60 * (ctx.app.config.sessionKeyKeepaliveMinutes || 15)
      ); // 指定分钟内没有活动会被认为失效session，用户不在线情况，（实际session对应的缓存并没有失效），此处缓存时用于处理在线用户的数据
    },

    async destroy(key) {
      // let user = await redis.get('session:key:' + key);
      // if (user) {
      //   user = JSON.parse(user);
      //   await redis.srem(`loginUser:session:${user.loginName}`, key);
      //   await redis.del('session:key:' + key);
      // }
      await redis.del('session:key:' + key);
      await redis.del(key);
    }
  };
};
