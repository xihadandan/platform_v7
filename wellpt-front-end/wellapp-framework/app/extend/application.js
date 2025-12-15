'use strict';

const NEDB = Symbol('Application#nedb');
const Nedb = require('../../lib/nedb');
const Timing = require('../../lib/timing');
// const ROUTER = Symbol('EggCore#router');
// const Router = require('@eggjs/router').EggRouter;

module.exports = {
  get nedb() {
    if (!this[NEDB]) {
      this[NEDB] = new Nedb();
    }
    return this[NEDB];
  },

  get newTiming() {
    return new Timing();
  },

  /**
   * 后端服务地址
   */
  get backendURL() {
    if (!this[BACKENDURL]) {
      // 实际情况肯定更复杂
      this[BACKENDURL] = this.config.backendURL;
    }
    return this[BACKENDURL];
  },

  // get router() {
  //   if (this[ROUTER]) {
  //     return this[ROUTER];
  //   }
  //   const router = this[ROUTER] = new Router({ sensitive: true }, this);
  //   router.prefix('/well');
  //    this.beforeStart(() => {
  //     this.use(router.middleware());
  //   });
  //   return router;
  // }
};
