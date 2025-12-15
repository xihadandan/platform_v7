(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery'], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {

    var Constant = {
        /**
         * 通过key值，找到对应常量中的VALUE
         *
         * @param constantArray
         *            常量，对象数组
         * @param keyValue
         *            标识值
         * @param keyField
         *            标识域
         * @param valueField
         *            真实值域
         * @returns
         */
        getValueByKey: function (constantArray, key, keyField, valueField) {
            if (!($.isArray(constantArray))) {
                return;
            }
            keyField = keyField || "key";
            valueField = valueField || "value";
            for (var i = 0; i < constantArray.length; i++) {
                var constant = constantArray[i];
                if (constant[keyField] === key) {
                    return constant[valueField];
                }
            }
            return null;
        },
        EVENT_TYPE: [{
            key: "CLICK",
            value: "click",
            name: "单击"
        }, {
            key: "MOUSE_OVER",
            value: "mouseover",
            name: "鼠标移进"
        } /*, { 
            key: "DOM_LOADED",
            value: "domloaded",
            name: "DOM初始化"
        }*/],/*domloaded只有面板头部事件有实现*/
        WIDGET_COLOR: [{
            key: "INVERSE",
            value: "inverse",
            name: "黑色"
        }, {
            key: "DEFAULT",
            value: "default",
            name: "灰色"
        }, {
            key: "PRIMARY",
            value: "primary",
            name: "蓝色"
        }, {
            key: "SUCCESS",
            value: "success",
            name: "绿色"
        }, {
            key: "INFO",
            value: "info",
            name: "浅蓝"
        }, {
            key: "WARNING",
            value: "warning",
            name: "橙色"
        }, {
            key: "DANGER",
            value: "danger",
            name: "红色"
        }],
        WIDGET_SIZE: [{
            key: "LG",
            value: "lg",
            name: "较大"
        }, {
            key: "SM",
            value: "sm",
            name: "较小"
        }, {
            key: "XS",
            value: "xs",
            name: "特别小"
        }],
        // 平台版本
        PT_VERSION: "5.3.1",
        // 组件版本
        WIDGET_VERSION: "5.3.1",
        // 组件的自定义事件
        WIDGET_EVENT: {
            // 页面容器创建完成, ui.wPage
            PageContainerCreationComplete: "PageContainerCreationComplete",
            // 组件创建完成事件
            WidgetCreated: "WidgetCreated",
            // 导航菜单选中事件，未支持
            MenuItemSelect: "MenuItemSelect",
            // 左导航项选择事件, ui.wLeftSidebar
            LeftSidebarItemClick: "LeftSidebarItemClick",
            Change: "Change",
            Refresh: "Refresh",
            WindowResultRefresh: "WindowResultRefresh",// 窗口返回结果刷新
            // 刷新微章
            BadgeRefresh: "BadgeRefresh",
            LeftSideBarRefresh: "LeftSidebarRefresh",//刷新全部左导航
            LeftSideBarRefreshDynamicItem: "LeftSideBarRefreshDynamicItem",//仅刷新动态左导航
            // 数据项点击
            ItemClick: "ItemClick",
            // 视图变更事件, ui.wBootstrapTable
            BootstrapTableChange: "BootstrapTableChange",
            // 视图刷新事件, ui.wBootstrapTable
            BootstrapTableRefresh: "BootstrapTableRefresh",
            // 锚点选择
            HashSelection : "HashSelection"
        },
        APP_TYPE: [{
            value: 0,
            name: "产品",
            englishName: "PRODUCT"
        }, {
            value: 1,
            name: "系统",
            englishName: "SYSTEM",
            keyField: "sysId"
        }, {
            value: 2,
            name: "模块",
            englishName: "MODULE",
            keyField: "moduleId"
        }, {
            value: 3,
            name: "应用",
            englishName: "APPLICATION",
            keyField: "appId"
        }, {
            value: 4,
            name: "功能",
            englishName: "FUNCTION",
            keyField: "functionId"
        }],
        APP_FUNCTION_CATEGORY: {
            URL: "URL",
            CONTROLLER: "Controller",
            FACADE_SERVICE: "FacedeService",
            APP_WIDGET_DEFINITION: "appWidgetDefinition",
            DATA_DICTIONARY: "dataDictionary"
        },
        TARGET_POSITION: {
            SELF: "_self",
            BLANK: "_blank",
            TARGET_WIDGET: "_targetWidget",
            DIALOG: "_dialog",
        },
        // 分隔符
        Separator: {
            Comma: ",", // 逗号
            Slash: "/", // 斜杠
            Dot: ".", // 点号
            Semicolon: ';'// 分号
        },
        // 当前应用的发起者路径标识
        KEY_APP_INITIATOR_PATH: "app_initiator_path"
    };
    window.Constant = Constant;
    return Constant;
}));