'use strict';
(function (factory, global) {
    if (typeof define === "function" && define.amd) {
        // AMD. Register as an anonymous module.
        define("MessageServer", [], function () {
            return factory(global);
        });
    } else {
        // Browser globals
        global.MessageServer = factory(global);
    }
})(function (global) {
    /**
     * origin:不同的应用情景，不同的origin
     */
    function MessageServer(options) {
        var self = this;
        var routers = {};
        var origin = options.origin;

        function receiveMessage(event) {
            var message = event.data;
            if (event.origin === origin && message) {
                var fn, action = message.action;
                if (action && (fn = routers[action])) {
                    var source = event.source;
                    try {
                        var ret = fn.apply(self, message.args);
                        source.postMessage({
                            ret: ret,
                            type: "resolve",
                            id: message.id
                        }, event.origin);
                    } catch (ex) {
                        source.postMessage({
                            ret: ex,
                            type: "reject",
                            id: message.id
                        }, event.origin);
                    }
                }
            }
        }

        var win = (options.win || global);
        win.addEventListener("message", receiveMessage, false);

        /**
         * { objRouters:{ router:fn,删除fn可以传noop } }
         */
        function router(objRouters) {
            var self = this;
            for (var key in objRouters) {
                if (routers[key]) {
                    throw new Error("重复定义[" + key + "]")
                } else if (typeof objRouters[key] === "function") {
                    routers[key] = objRouters[key];
                }
            }
        }

        /**
         * 系统定义功能
         */
        router({
            init: function () {
                return Object.keys(routers);
            },
            main: function () {
                return options.script;
            }
        });
        self.router = router;
    }

    return MessageServer;
}, window);

(function (factory) {
    if (typeof define === "function" && define.amd) {
        require(["MessageServer"], factory)
    } else {
        factory(window.MessageServer);
    }
})(function (MessageServer) {
    var server = new MessageServer({
        origin: "http://192.168.20.43:8082",
        script: "http://192.168.20.43:8082/resources/utils/pdf-custom.js",
    })
    server.router({
        getUserDetails: function () {
            return SpringSecurityUtils.getUserDetails();
        },
        getFileInfo: function (fileId) {
            return;
        },
        getFilePermission: function (fileId) {
            return {
                open: false,
                print: true,
                download: false,
            }
        },
        getPrintHistory: function (fileId) {
            return [{
                username: "张三",
                printcount: "1",
                printdate: "2021-06-19",
            }, {
                username: "李四",
                printcount: "2",
                printdate: "2021-08-10",
            }]
        },
        onafterprint: function (fileId) {
            alert(fileId);
        },
        savePrintReason: function (fileId, reason) {

        }
    });
});
