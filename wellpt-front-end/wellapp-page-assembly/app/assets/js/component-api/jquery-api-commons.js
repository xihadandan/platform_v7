/**
 * 公用api
 */
(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery', 'commons', 'server', 'constant', 'appModal'], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function (jquery, commons, server, constant, appModal) {
    "use strict";

    var CommonApi = function () {
    }

    /**
     * 执行SQL配置
     * @param id  sql id 值
     * @param params 参数
     * @param callback 回调函数
     *  @param async 是否同步，默认同步
     */
    CommonApi.prototype.executeSQL = function (id, params, callback, async) {
        if (id == undefined || id == null || id == '') {
            console.error('调用可执行SQL必须指定id');
            return;
        }
        server.JDS.call(
            {
                service: 'execSqlDefinitionFacadeService.execute',
                data: [id, params],
                async: async === true,
                version: '',
                success: function (result) {
                    console.log('调用可执行SQL[' + id + ']，返回数据', result);
                    if ($.isFunction(callback)) {
                        callback(result.success, result.data);
                    }
                }
            }
        );
    };


    /**
     * 单据转换api
     * @param id  单据转换规则ID
     * @param params
     *              1.报文转单据
     *                      {
                                "jsonBody": {
                                    "name": "test",
                                    "code": "222222222"
                                }
                            }
     *                  2.单据转单据
     *                      {
                            "froms": [
                                    {
                                        "fromObjId": "bot_object_a",  //表单id或者表单uuid
                                        "fromUuid": "c8252f316ba00001de6dada3628017fd"  //表单数据uuid
                                    },
                                    {
                                        "fromObjId": "bot_object_b",  //表单id或者表单uuid
                                        "fromUuid": "c825f5a893e000016a7529d029a01d68"  //表单数据uuid
                                    }
                                ]
                            }
     *
     * @param callback 执行回调函数
     * @param async
     */
    CommonApi.prototype.startBot = function (id, params, callback, async) {
        if (id == undefined || id == null || id == '') {
            console.error('调用单据转换必须指定id');
            return;
        }
        var botParam = {
            ruleId: id
        }
        $.extend(botParam, params);
        server.JDS.call(
            {
                service: 'botFacadeService.startBot',
                data: [botParam],
                async: async === true,
                version: '',
                success: function (result) {
                    console.log('调用单据转换[' + id + ']，返回数据', result);
                    if ($.isFunction(callback)) {
                        callback(result.success, result.data);
                    }
                }
            }
        );
    }


    /**
     * 发送消息
     * @param params    com.wellsoft.pt.message.support.MessageParams
     * @param callback
     * @param async
     */
    CommonApi.prototype.sendMessage = function (params, callback, async) {
        server.JDS.call(
            {
                service: 'messageClientApiFacade.sendByParams',
                data: [params],
                async: async === true,
                version: '',
                success: function (result) {
                    if ($.isFunction(callback)) {
                        callback(result.success, result.data);
                    }
                }
            }
        );
    }

    return CommonApi;

}));