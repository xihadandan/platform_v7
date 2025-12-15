//$(function(){
//	window.onbeforeunload = onbeforeunload_handler;  
//	function onbeforeunload_handler(){  
//		var para = window.location.search;
//		var moduleid = "";
//		var wid = "";
//		var paraArray = para.replace("?","").split("&");
//		for(var i=0;i<paraArray.length;i++){
//			if(paraArray[i].indexOf("moduleid")>-1){
//				moduleid = paraArray[i].split("=")[1];
//			}else if(paraArray[i].indexOf("wid")>-1){
//				wid = paraArray[i].split("=")[1];
//			}
//		}
//		if(moduleid != null && $.trim(moduleid) != ""){
//			$.ajax({
//				type:"post",
//				async:false,
//				data : {"uuid":moduleid},
//				url:ctx+"/cms/cmspage/viewcontent",
//				success:function(result){
//					$(window.opener.document.getElementById(wid)).find("div .active").html(result);
//				}
//			});
//		}
//	}
//});
function returnWindow() {
	if (window.opener == null) {
		window.location.href = ctx;
	}
	var para = window.location.search;
	var moduleid = "";
	var wid = "";
	var para = readSearch();
	moduleid = para.moduleid;
	wid = para.wid;
	var indexLdxDffModuleId = para.indexLdxDffModuleId;
	var pageCurrentPage = para.pageCurrentPage;
	var specilModule = para.specilModule;
	if (moduleid != null && $.trim(moduleid) != "" || indexLdxDffModuleId != null && $.trim(indexLdxDffModuleId)) {
		// var temp =
		// $(window.opener.document.getElementById(wid)).find(".active");
		var reqObj = new Object();
		if (indexLdxDffModuleId != null && $.trim(indexLdxDffModuleId) != "") {
			reqObj.uuid = indexLdxDffModuleId;
			reqObj.count = "000";
			reqObj.pageCurrentPage = pageCurrentPage;
		} else {
			reqObj.uuid = moduleid;
		}
		if (window.navigator.userAgent.indexOf("Chrome") !== -1
				&& (!indexLdxDffModuleId || $.trim(indexLdxDffModuleId) == "")) {
			$(window.opener.document.getElementById(wid)).find("#keySelect").trigger("click");
			if (specilModule != null) {
				var reqObj = new Object();
				reqObj.uuid = specilModule;
				$.ajax({
					type : "post",
					async : false,
					data : reqObj,
					url : ctx + "/cms/cmspage/viewcontent",
					success : function(result) {
						temp.each(function() {
							$(this).parents().find(".dnrw").each(function() {
								var $this = $(this);
								if ($this.attr("moduleid") == specilModule) {
									$this.find(".tab-content").html(result);
								}
							});
						});
					}
				});
			}
		} else {
			$
					.ajax({
						type : "post",
						async : false,
						data : reqObj,
						url : ctx + "/cms/cmspage/viewcontent",
						success : function(result) {
							var temp = $(window.opener.document.getElementById(wid)).find(".tab-pane");
							if (temp.attr("class") == undefined || temp.attr("class") == "") {
								temp = $(window.opener.document.getElementById(wid)).find(".module_btn");
							}
							temp
									.each(function() {
										if (indexLdxDffModuleId != null && $.trim(indexLdxDffModuleId) != "") {
											// (ldx)这个分支是为了返回首页磁贴后再次自动定位打开该磁贴
											if (indexLdxDffModuleId == $(this).attr("moduleid")) {
												$(".mail_write").css("display", "none");
												$(".index_dff").attr("class", "index_dff common-top");
												$(".fun-list").attr("class", "clearfix fun-list fun-expand");
												$(".fun-list").find(".list-item").attr("class", "list-item");
												$(".list-content").html("");
												$(".common2").remove();
												$(".commonSearchModule").remove();
												$(this).parent().parent().hide();
												$(this).parent().parent().attr("class", "list-item list-selected");
												$(this).parent().parent().find(".operate").html(result);
												if (moduleid == "f88650c9-b961-4dc1-ab86-897f8f54b970") {
													$(this)
															.parent()
															.parent()
															.find(".operate")
															.after(
																	"<button type='button' id='messageHiddenId'/ value='123' style='display:none;'></button>");
												}
												/** *添加已阅未阅的图标** */
												$(".page_index .operate .dataTr").each(
														function() {
															if ($(this).attr("class").indexOf("readed") > -1) {
																$(this).find("td").eq(0).html(
																		"" + $(this).find("td").eq(0).html());
															} else if ($(this).attr("class").indexOf("noread") > -1) {
																$(this).find("td").eq(0).html(
																		"" + $(this).find("td").eq(0).html());
															}
														});

												$(this).parent().parent().find(".view_tool2").remove();
												// var searchStr = '<input
												// class="commonModuleKeyWord"
												// type="text"><span
												// class="commonSearchButton"
												// style="font-size:12px;">搜索</span>';
												// var viewButton =
												// temp.parent().parent().find(".operate2").html();
												// var tempStr = '<div
												// class="common2">'+searchStr+viewButton+'</div>';
												// $(this).parent().append(tempStr);
												$(this).parent().parent().show();
												var backgroundColor = $(".list-selected").attr("bc");

												$(".list-selected").find("h4").css("background", backgroundColor);
												$(".list-selected").css("background", "");

												$(this).attr("class", "module_btn module_btn2");
												var totalCount = $(this).parents(".dnrw").find("#pageTotalCount").val();
												// 更新磁贴上面的总数量
												$(this).children(".nums").html(totalCount);
												/** *格式化时间** */
												formDate();
												pageLock("hide");
											}
										} else {
											var class_ = $(this).attr("class");
											if (class_ == "active") {
											} else {
												$(this).html(result);
												window.opener.jsmod(".dnrw .tab-content", true);
											}
										}
									});
							// 刷新特殊模块,针对湖里云OA就是消息模块的刷新
							if (specilModule != null) {
								var reqObj = new Object();
								reqObj.uuid = specilModule;
								$.ajax({
									type : "post",
									async : false,
									data : reqObj,
									url : ctx + "/cms/cmspage/viewcontent",
									success : function(result) {
										temp.each(function() {
											$(this).parents().find(".dnrw").each(function() {
												var $this = $(this);
												if ($this.attr("moduleid") == specilModule) {
													$this.find(".tab-content").html(result);
												}
											});
										});
									}
								});
							}
						}
					});
		}
	} else {
		window.opener.location.href = window.opener.location.href;
	}
}

function refreshWindow(element) {
	// var moduleid = element.parents(".active").attr("moduleid");
	var moduleid = element.parents(".viewContent").parent().attr("moduleid");
	var flag = 0;
	// 内容不是直接放在viewContent父级的情况
	if (moduleid == undefined) {
		moduleid = element.parents(".active").attr("moduleid");
		flag = 1;
	}
	pageLock("show");
	$.ajax({
		type : "post",
		async : false,
		data : {
			"uuid" : moduleid
		},
		url : ctx + "/cms/cmspage/viewcontent",
		success : function(result) {
			// element.parents(".active").html(result);
			try {
				if (flag = 1) {
					element.parents(".active").html(result);
				} else {
					element.parents(".viewContent").parent().html(result);
				}
			} catch (e) {
				console.error(e);
			} finally {
				pageLock("hide");
			}
			jsmod(".dnrw .tab-content", true);
		}
	});
}
