'use strict';
(function (factory, global) {
    if (typeof define === "function" && define.amd) {
        // AMD. Register as an anonymous module.
        define("ClientFactory", [], function () {
            return factory(global);
        });
    } else {
        // Browser globals
        global.ClientFactory = factory(global);
    }
})(function (global) {
    var guid = 0;

    function MessageClient(options) {
        if (typeof options.success === "function" && typeof options.error === "function") {
            // success和error必填
        } else {
            return options.error("缺少参数[success,error]");
        }
        var self = this;
        var messages = {};
        var origin = options.origin;
        var win = (options.win || global);

        // 接收消息
        function receiveMessage(event) {
            var result, message;
            if (event.origin === origin && (result = event.data)) {
                if (result.id && (message = messages[result.id])) {
                    if (typeof message[result.type] === "function") {
                        message[result.type].call(self, result.ret);
                        delete messages[result.id];
                    }
                }
            }
        }

        // 发送消息
        function postMessage(message) {
            message.id = "id-" + (guid++);
            options.poster.postMessage(message, origin);
            return new Promise(function (resolve, reject) {
                message.reject = reject;
                message.resolve = resolve;
                messages[message.id] = message;
            })
        }

        win.addEventListener("message", receiveMessage, false);
        // 发送初始化消息
        postMessage({
            action: "init",
        }).then(function (actions) {
            if (actions && actions.length) {
                for (var i = 0; i < actions.length; i++) {
                    var action = actions[i];
                    // self[action] = (function(action) {
                    MessageClient.prototype[action] = (function (action) {
                        return function () {
                            var message = {
                                action: action,
                                args: Array.prototype.slice.apply(arguments),
                            }
                            return postMessage(message);
                        }
                    })(action);
                }
                options.success(self);
            } else {
                options.error("服务端未初始化[router]");
            }
        }).catch(options.error);
    }

    var clients = {};

    function ClientFactory(options) {
        return new Promise(function (resolve, reject) {
            if (clients[options.origin]) {
                return resolve(clients[options.origin]);
            } else {
                options.success = function (client) {
                    clients[options.origin] = client;
                    resolve(client);
                }
                options.error = reject;
                setTimeout(function () {
                    if (null == clients[options.origin]) {
                        reject("[timeout]服务端未初始化");
                    }
                }, options.timeout || 3000);
                return new MessageClient(options);
            }
        })
    }

    ClientFactory.loadScript = function Util_loadScript(src, callback) {
        var script = document.createElement('script');
        var loaded = false;
        script.setAttribute('src', src);
        script.setAttribute('type', "text/javascript");
        if (callback) {
            script.onload = function () {
                if (!loaded) {
                    callback();
                }
                loaded = true;
            };
        }
        document.getElementsByTagName('head')[0].appendChild(script);
    };
    ClientFactory.loadStyle = function Util_loadStyle(src, callback) {
        var script = document.createElement('link');
        var loaded = false;
        script.setAttribute('href', src);
        script.setAttribute('type', "text/css");
        script.setAttribute('rel', "stylesheet");
        if (callback) {
            script.onload = function () {
                if (!loaded) {
                    callback();
                }
                loaded = true;
            };
        }
        document.getElementsByTagName('head')[0].appendChild(script);
    };
    return ClientFactory;
}, window);

(function (factory, global) {
    if (typeof define === "function" && define.amd) {
        // AMD. Register as an anonymous module.
        define("ClientFactory2", ["ClientFactory"], function (ClientFactory) {
            return factory(ClientFactory);
        });
    } else {
        // Browser globals
        global.ClientFactory2 = factory(global.ClientFactory);
    }
})(function (ClientFactory) {
    function ClientFactory2(options) {

    };
    var apis = {
        enableOpenFile: function (enable) {

        },
        enablePrint: function (enable) {

        },
        enableDownload: function (enable) {

        },
        enableViewBookmark: function (enable) {

        }
    };
    ClientFactory2.loadFileApi = function Util_loadFileApi(fileApi) {

    };
    ClientFactory2.ready = function Util_ready(callback) {
        var callback2 = function (event) {
            callback(ClientFactory);
        }
        if (document.readyState === 'interactive' || document.readyState === 'complete') {
            callback2()
        } else {
            document.addEventListener('DOMContentLoaded', callback2, true);
        }
    };
    return ClientFactory2;
});
