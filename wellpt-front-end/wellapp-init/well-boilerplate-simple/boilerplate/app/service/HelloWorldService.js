'use strict';

const Service = require('wellapp-framework').Service;

class HelloWorldService extends Service {

    /**
     * 服务调用方式：ctx.service.helloworldService.hello();
     */
    async hello() {
        const { app, ctx } = this;
        console.debug('hello, world');
    }

}

module.exports = HelloWorldService;
