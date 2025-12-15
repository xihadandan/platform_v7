define(["jquery", "commons", "constant", "server", "mui.AppOutboxMsgViewDevelopment", "appModal", "multiOrg"], function ($, commons, constant, server, AppOutboxMsgViewDevelopment, appModal, unit) {
    JDS = server.JDS;
    var InboxMsgModule = function () {
    	AppOutboxMsgViewDevelopment.apply(this, arguments);
    };
    commons.inherit(InboxMsgModule, AppOutboxMsgViewDevelopment, {
        onClickRow: function (rowNum, row, $element, field) {
            var _self = this;
            var title = "收件消息   " + row['RECEIVED_TIME'].substr(0, 19) + " 来自 " + row['SENDER_NAME'];
            var outboxUuid = row['MESSAGE_OUTBOX_UUID'];
            var buttons = [
               {
            	    id : "forwardMsg",
                    name: "转发",
                    callback: function () {
                        _self.showForwardDialog(row);
                    }
                }
            ];
            var userId = row["SENDER"];
            var userName = row["SENDER_NAME"];
            if(userId !== "system" && userName !== "系统") {
            	// 系统发送不回复
            	buttons.push({ 
            		id : "retractMsg",
            		name: "回复",
            		callback: function () {
            			_self.showReplyDialog(row);
            		}
            	})
            }
            var $dialog = _self.viewMsg(row, buttons, title, outboxUuid, function () {
                _self.readMsgService([row['UUID']]);
            });
        },

        //回复按钮对应的事件
        btnReplyMsg: function (args) {
            var selections = this.getSelections();
            var uuids = this.getSelectedRowIds(selections);
            if (uuids.length == 0) {
                appModal.alert("请选择记录！");
            } else if (uuids.length != 1) {
                appModal.alert("一次只能选择一条记录！");
            } else {
                this.showReplyDialog(selections[0]);
            }
        },

        //展示回复对话框
        showReplyDialog: function (row) {
            var _self = this;
            var userId = row["SENDER"];
            var userName = row["SENDER_NAME"];
            if(userId === "system" || userName === "系统") {
            	return appModal.toast("请勿回复系统");
            }
            var outboxUuid = row['MESSAGE_OUTBOX_UUID'];
            var dialogHtml = this.getReplyDialogHtml(row);
            var $replyDialog = this.SendOutPanel(dialogHtml, "回复消息", outboxUuid, function () {
//                var index = _self.getSelectionIndexes()[0];
               /* _self.widget.$tableElement.find('tr[data-index=' + index + ']').removeClass('unread');*/
                _self.readMsgService([row['UUID']]);
            });
        },

        //转发按钮对应的事件
        btnForwardMsg: function (args) {
            var selections = this.getSelections();
            var uuids = this.getSelectedRowIds(selections);
            if (uuids.length == 0) {
                appModal.alert("请选择记录！");
            } else if (uuids.length != 1) {
                appModal.alert("一次只能选择一条记录！");
            } else {
                this.showForwardDialog(selections[0]);
            }
        },

        //展示转发对话框
        showForwardDialog: function (row) {
            var _self = this;
            var dialogHtml = this.getForwardDialogHtml(row);
            var outboxUuid = row["MESSAGE_OUTBOX_UUID"];
            var $forwardDialog = this.SendOutPanel(dialogHtml, "转发消息", outboxUuid, function () {
                _self.readMsgService([row['UUID']]);
//                var index = _self.getSelectionIndexes()[0];
//                _self.widget.$tableElement.find('tr[data-index=' + index + ']').removeClass('unread');
            });
        },

        //删除按钮对应的事件
        btnDelMsg: function (args) {
            var uuids = this.getSelectedRowIds(this.getSelections());
            if (uuids.length == 0) {
                appModal.alert("请选择记录！");
            } else {
                var _self = this;
                console.log(uuids);
                appModal.confirm("确认要删除吗?", function (result) {
                    if (result) {
                        $.ajax({
                            type: "POST",
                            url: ctx + "/message/content/deleteInboxMessage",
                            data: "uuids=" + uuids,
                            dataType: "text",
                            success: function () {
                                appModal.success("删除成功！");
                                _self.refresh();
                                _self.refreshBadgeNum();
                            }
                        });
                    }
                });
            }
        },


        //标识已读按钮对应的事件
        btnMarkRead: function (args) {
            var uuids = this.getSelectedRowIds(this.getSelections());
            if (uuids.length == 0) {
                appModal.alert("请选择记录！");
            } else {
                var _self = this;
                appModal.confirm("确认标记为已读?", function (result) {
                    if (result) {
                        _self.readMsgService(uuids, function () {
                            _self.refresh();
                            _self.refreshBadgeNum();
                        });
                    }
                });

            }
        },

        /**
         * 已读服务
         * @param uuids
         * @param successCallback
         */
        readMsgService: function (uuids, successCallback) {
            var _self=this;
            $.ajax({
                type: "POST",
                url: ctx + "/message/content/read",
                data: "uuids=" + uuids,
                dataType: "text",
                success: function () {
                    if ($.isFunction(successCallback)) {
                        successCallback();
//                    }else{
//                        //未阅消息列表需要刷新
//                        if (_self.widget.getConfiguration().name == '未阅消息列表') {
//                            _self.refresh();
//                            _self.refreshBadgeNum();
//                        }
                    }

                }
            });
        },


        //标识未读按钮对应的事件
        btnMarkUnread: function (args) {
            var uuids = this.getSelectedRowIds(this.getSelections());
            if (uuids.length == 0) {
                appModal.alert("请选择记录！");
            } else {
                var _self = this;
                appModal.confirm("确认标记为未读?", function (result) {
                    if (result) {
                        $.ajax({
                            type: "POST",
                            url: ctx + "/message/content/unread",
                            data: "uuids=" + uuids,
                            dataType: "text",
                            success: function () {
                                _self.refresh();
                                _self.refreshBadgeNum();
                            }
                        });
                    }
                });
            }
        },


        //收件箱标星按钮对应的事件
        btnMarkStar: function (args) {
            var uuids = this.getSelectedRowIds(this.getSelections());
            if (uuids.length == 0) {
                appModal.alert("请选择记录！");
            } else {
                var _self = this;
                $.ajax({
                    type: "POST",
                    url: ctx + "/message/content/markInboxflag",
                    data: "uuids=" + uuids + "&markflag=1",
                    dataType: "text",
                    success: function () {
                        appModal.success("标星成功！");
                        _self.refresh();
                    }
                });
            }
        },

        //收件箱取消标星按钮对应的事件
        btnCancelMarkStar: function (args) {
            var uuids = this.getSelectedRowIds(this.getSelections());
            if (uuids.length == 0) {
                appModal.alert("请选择记录！");
            } else {
                var _self = this;
                $.ajax({
                    type: "POST",
                    url: ctx + "/message/content/markInboxflag",
                    data: "uuids=" + uuids + "&markflag=0",
                    dataType: "text",
                    success: function () {
                        appModal.success("取消标星成功！");
                        _self.refresh();
                    }
                });
            }
        },


        getReplyDialogHtml: function (row) {
            var newSubject = "Re:" + row['SUBJECT'];
            var newBody = this.getReplybody(row['SENDER_NAME'], row['SUBJECT'], row['BODY'], row['SENT_TIME']);
            var relatedUrl = row['RELATED_URL'];
            var relatedTitle = row['RELATED_TITLE'];
            var userId = row["SENDER"];
            var userName = row["SENDER_NAME"];
            var dialogHtml = this.getDialogHtml(userId, userName, newSubject, newBody, "", relatedUrl, relatedTitle);
            return dialogHtml;
        },


    });

    return InboxMsgModule;
});