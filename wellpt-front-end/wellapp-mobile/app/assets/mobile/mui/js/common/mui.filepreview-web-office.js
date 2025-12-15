(function(factory) {
	// 配置requirejs
	requirejs.config({
		paths : {
			server : ctx + "/resources/pt/js/server",
			imageViewer : ctx + "/mobile/mui/js/mui.imageViewer",
			floatbutton : ctx + "/mobile/mui/js/common/mui.floatbutton"
		}
	});
	if (typeof define === "function" && define.amd) {
		// AMD. Register as an anonymous module.
		define([ "mui", "server"], factory);
	} else {
		// Browser globals
		factory(mui);
	}
})(function($, server) {
	var FileViewer = {};
	// Office 后缀
	var powerPointExts = FileViewer.powerPointExts = [ "odp", "pot", "potm", "potx", "pps", "ppsm", "ppsx", "ppt",
			"pptm", "pptx" ];
	var wordExts = FileViewer.wordExts = [ "doc", "docm", "docx", "dot", "dotm", "dotx", "odt" ];
	var excelExts = FileViewer.excelExts = [ "ods", "xls", "xlsb", "xlsm", "xlsx" ];
	var pdfExts = FileViewer.pdfExts = [ "pdf" ];
	var txtExts = FileViewer.txtExts = [ "txt" ];
	var excelUrl = server.SystemParams.getValue("office.x.excel.m.url",
		"http://reading1.leedarson.com/x/_layouts/xlviewerinternal.aspx?WOPISrc=");
	var powerPointUrl = server.SystemParams.getValue("office.p.powerpoint.m.url",
		"http://reading1.leedarson.com/p/PowerPointFrame.aspx?WOPISrc=");
	var wordViewerUrl = server.SystemParams.getValue("office.wv.word.viewer.m.url",
		"http://reading1.leedarson.com/wv/wordviewerframe.aspx?WOPISrc=");
	var txtViewerUrl = ctx + "/repository/file/mongo/content2?fileID=";
	var requestServerUrl = window.location.origin;
	var isMatchExts = FileViewer.isMatchExts = function(ext, exts) {
		ext = ext || "";
		ext = ext.toLowerCase();
		for (var i = 0; i < exts.length; i++) {
			if (ext === exts[i]) {
				return true;
			}
		}
		return false;
	}
	FileViewer.preview = function(fileObj) {
		var fileUrl = null;
		if (typeof fileObj === "string") { // url预览
			fileUrl = fileObj;
		} else if ($.isPlainObject(fileObj) && fileObj.fileId) {
			var fileName = fileObj.fileName, fileExt = fileObj.fileExt;
			if (!fileExt && fileName && fileName.lastIndexOf(".") > 0) {
				fileExt = fileObj.fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
			}
			var WOPISrc = requestServerUrl + "/wopi/files/" + fileObj.fileId + "?access_token=xxx&wdMobileHost=2&wdTtime=" + (new Date()).getTime();
			if (isMatchExts(fileExt, excelExts)) { // excel
				fileUrl = excelUrl + WOPISrc;
			} else if (isMatchExts(fileExt, powerPointExts)) { // powerpoint
				fileUrl = powerPointUrl + WOPISrc;
			} else if (isMatchExts(fileExt, wordExts)) { // word
				fileUrl = wordViewerUrl + WOPISrc;
			} else if (isMatchExts(fileExt, pdfExts)) { // pdf
				fileUrl = wordViewerUrl + WOPISrc + "&embed=1&PdfMode=1";
			} else if (isMatchExts(fileExt, txtExts)) { // txt
				fileUrl = txtViewerUrl + fileObj.fileId + "&preview=true";
			}
		}
		if (fileUrl && requestServerUrl) {
			requirejs(["floatbutton"], function(floatButton) {
				var wrapperIframe = document.createElement("div");
				wrapperIframe.classList.add("filepreview");
				wrapperIframe.innerHTML = '<iframe class="filepreview-body" src="" marginheight="0px" marginwidth="0px" width="100%" height="100%">立达信文件预览 </iframe><!--<div class="filepreview-close"><i class="icon mui-icon mui-icon-close"></i></div>-->';
				document.body.appendChild(wrapperIframe);
				$(wrapperIframe).on("tap", ".filepreview-close", function(event) {
					// document.body.removeChild(wrapperIframe);
				});
				FileViewer.btn = new floatButton({
					hold : false,
					className : "filepreview-operation",
					close : function() {
						document.body.removeChild(wrapperIframe);
					},
					btns : [{
						icon : "mui-icon mui-icon-reload",
						text : "模式切换",
						info : "切换PC/Mobile浏览模式<br>PC端模式,信息展示更全,但速度慢;<br>Mobile模式部分信息可能不展示,但速度快;<br>pdf不支持手机端模式,txt没有模式区别.",
						callback : function(event){
							var fileUrl = $(".filepreview-body", wrapperIframe)[0].src;
							if(fileUrl.indexOf("&embed=1") > 0){
								fileUrl = fileUrl.replace("&embed=1", "");
							}else {
								fileUrl = fileUrl + "&embed=1";
							}
							$(".filepreview-body", wrapperIframe)[0].src = fileUrl;
						}
					}]
				});
				$(".filepreview-body", wrapperIframe)[0].src = fileUrl;
			})
		} else if(fileObj.image && fileObj.fileUrl) {
			require(["imageViewer"], function() {// 需要时才加载
				var imageViewer = FileViewer.imageViewer;
				if(imageViewer == null || typeof imageViewer === "undefined"){
					imageViewer = FileViewer.imageViewer = $.defaultImageViewer || $.imageViewer("img[id]");
				}
				var wrapperImg = $("img[id=\"" + fileObj.fileId + "\"]")[0];
				if(wrapperImg == null || typeof wrapperImg === "undefined"){
					var wrapperImg = document.createElement("img");
					wrapperImg.id = fileObj.fileId;
					wrapperImg.style.display = "none";
					wrapperImg.classList.add("mui-image");
					wrapperImg.classList.add("filepriview-item");
					document.body.appendChild(wrapperImg);
				}
				wrapperImg.src = fileObj.fileUrl + "&preview=true";
				imageViewer.disposeImage(true);
				imageViewer.findAllImage(); // 重新搜索图片
				$.trigger(wrapperImg, "tap");
			});
		} else {
			return $.toast("附件无法预览");
		}
	}
	FileViewer.dispose = function() {
		var dispose = false;
		if(FileViewer.btn && FileViewer.btn.close) {
			FileViewer.btn.close();// 单实例
			FileViewer.btn = null;
			dispose = true;
		};
		if(FileViewer.imageViewer && FileViewer.imageViewer.close) {
			FileViewer.imageViewer.close();
			FileViewer.imageViewer = null;
			dispose = true;;
		};
		$("img.filepriview-item").each(function() {
			var self = this;
			self.parentNode.removeChild(self);
		})
		return dispose;
	}
	$.FileViewer = FileViewer;
	return FileViewer;
})
