var Draft = {};
var Trash = {};
var Outbox = {};
var Inbox = {};
var Supervise = {};

$(function() {
	////草稿箱
	Draft.Add = function() {
		window.open(ctx + "/exchange/draft/add");
	};
	
	Draft.Edit = function() {
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/draft/add?uuid=" + uuid);
	};
	
	Draft.Del = function(uuid) {
		var uuid = ExchangeHelper.getCheckedUUID();
		if(!uuid) return;
		
		if (confirm(global.deleteConfirm)) {
			JDS.call({
				service : 'draftService.delDraft',
				data : [uuid],
				success : function(result) {
					alert(exchange_base.success);
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	};
	
	Draft.Send = function(uuid) {
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		JDS.call({
			service : 'draftService.sendDraft',
			data : [uuid],
			success : function(result) {
				alert(exchange_base.success);
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	};	
	/////废件箱
	//删除操作
	Trash.Del = function() {
		var uuids = ExchangeHelper.getCheckedUUID();
		if(!uuids) return;
		
		if(confirm(exchange_base.confirm_delete)){			
			JDS.call({
				service : 'trashService.delTrash',
				data : [uuids],
				success : function(result) {
					alert(exchange_base.success);
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	};
	
	//清空操作
	Trash.Empty = function() {
		if(confirm(exchange_base.confirm_trash_empty)){
			JDS.call({
				service : 'trashService.emptyTrash',
				data : [],
				success : function(result) {
					alert(exchange_base.success);
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	};
	
	//还原操作
	Trash.Revert = function() {
		var uuids = ExchangeHelper.getCheckedUUID();
		if(!uuids) return;
		
		JDS.call({
			service : 'trashService.revertTrash',
			data : [uuids],
			success : function(result) {
				alert(exchange_base.success);
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	};
	
	/////督办箱
	Supervise.Remind = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/outbox/senddetail?otype=REMIND&uuid=" + uuid);
	};
	
	/////发件箱
	Outbox.Del = function(){
		var uuid = ExchangeHelper.getCheckedUUID();
		if(!uuid) return;
		
		if (confirm(global.deleteConfirm)) {
			JDS.call({
				service : 'outboxService.delOutbox',
				data : [uuid],
				success : function(result) {
					alert(exchange_base.success);
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	};
	//补充发送
	Outbox.Sendadd = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/outbox/sendadd?uuid=" + uuid);
	};
	//撤回
	Outbox.Withdraw = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/outbox/senddetail?otype=WITHDRAW&uuid=" + uuid);
	};
	//办结
	Outbox.Finish = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/outbox/senddetail?otype=FINISH&uuid=" + uuid);
	};
	//催办
	Outbox.Remind = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/outbox/senddetail?otype=REMIND&uuid=" + uuid);
	};
	//撤销
	Outbox.Cancel = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		if (confirm(global.deleteConfirm)) {
			JDS.call({
				service : 'outboxService.cancel',
				data : [uuid],
				success : function(result) {
					alert(exchange_base.success);
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	};
	
	/////收件箱
	//签收
	Inbox.Sign = function(){
		var signHtml = "<div style='padding:10px;'>签名：<input type='text' id='forceSignatureName' name='forceSignatureName' /></div>";
		
		if(confirm(exchange_base.confirm_sign)){
			//强制签名
			if(true){
				var submit = function (v, h, f) {
				    if (f.forceSignatureName == '') {
				        $.jBox.tip("请输入您的姓名。", 'error', { focusId: "forceSignatureName" }); // 关闭设置 yourname 为焦点
				        return false;
				    }
				    SignaturePost(f.forceSignatureName);
				    return true;
				};
				$.jBox(signHtml, { title: "签收签名", submit: submit });
			}else{
				SignaturePost();
			}
		}
	};
	//反馈
	Inbox.Feedback = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/inbox/feedback?uuid=" + uuid);
	};
	//转办
	Inbox.Forward = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/inbox/forward?uuid=" + uuid);
	};
	//转发
	Inbox.Transmit = function(){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		window.open(ctx + "/exchange/draft/add");
	};	
	//签名提交函数
	function SignaturePost(sname){
		var uuid = ExchangeHelper.getCheckedUUID(true);
		if(!uuid) return;
		var bean = $.extend({}, ExchangeDefine.FormDocumentBean);
		bean.opinion = sname;
		bean.ouuids = uuid;
		JDS.call({
			service : "inboxService.receivesign",
			data : [ bean ],
			success : function(result) {
				alert(exchange_base.complete_sign);
				$("#forceSignatureName").val('');
			}
		});
	}
});