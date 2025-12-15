(function(global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) : typeof define === 'function' && define.amd ? define([ 'exports' ], factory)
			: (factory((global._wps = {})));
}(this, (function(exports) {
	'use strict';
	// 消息监听
	var saveTimeout = 1000 * 3;
	window.addEventListener("message", function(event) {
		// debugger
		// console.log(event);//
		var data = event.data;
		if (typeof data === "string") {
			data = JSON.parse(data);
		}
		var fName = data.fName;
		var fArgs = JSON.parse(data.fArgs);
		var obj = document.getElementById("webwps_id");
		var scb = function(Application) {
			exports.Application = Application;
			exports[fName].apply(exports, fArgs);
			// alert("初始化成功")
			console.log("初始化成功");
		}, fcb = function() {
			// console.log("初始化失败");
			alert("初始化失败");
			// typeof wpsCallback.fail === "function" && wpsCallback.fail();
		};
		var i = 10;
		var app = obj.Application;
		if (!app) {
			// 为了兼容
			var ctx = window.ctx || "";
			obj.setAttribute('data', obj.getAttribute("data-newfile"));
			function checkInterval() {
				app = obj.Application;
				if (app && app.IsLoad()) {
					// typeof wpsCallback.success === "function" &&
					// wpsCallback.success();
					return scb(app);
				} else if ((i--) > 0) {
					intervalId = setTimeout(checkInterval, 1000);
				} else {
					return fcb();
				}
			}
			var intervalId = setTimeout(checkInterval, 1000);
		} else {
			return scb(app);
		}
	});
	// 退出时做清理
	window.onbeforeunload = function(event) {
		if (wpsCallback && wpsCallback.onbeforeunload) {
			wpsCallback.onbeforeunload.apply(this, event);
		}
		// 跨浏览器时，会关闭其他浏览器WPS(单个浏览器的所有WPS窗体关闭时，会影响其他浏览器的WPS，一起关闭)
		exports.Application && exports.Application.Quit();
	};

	function _inArray(suffix, array) {
		for (var i = 0; i < array.length; i++) {
			if (suffix.indexOf(array[i]) != -1)
				return true;
		}
		return false;
	}

	/**
	 * 定义一个变量 用于apiGroup 因为不支持直接输入中文
	 * 
	 * @apiDefine doc 文档相关
	 */
	/**
	 * 定义一个变量 用于apiGroup 因为不支持直接输入中文
	 * 
	 * @apiDefine et 表格相关
	 */
	/**
	 * 定义一个变量 用于apiGroup 因为不支持直接输入中文
	 * 
	 * @apiDefine ppt PPT相关
	 */

	/**
	 * @apiDeprecated 新的直接使用OpenDoc()
	 * @api _wps.newDoc(params) 新建文档
	 * @apiVersion 3.1.0
	 * @apiName newDoc
	 * @apiDescription 新建文档，直接调用newDoc即可，传参参考下面说明
	 * @apiExample {js} 新建文档 _wps.newDoc({ "uploadPath":
	 *             "http://dev.wpseco.cn/file/uploadFromWps", //保存文档接口
	 *             "strBookmarkDataPath": strBookmarkDataPath,//书签列表接口
	 *             "templatePath": templatePath,//正文模板列表接口 "buttonGroups":
	 *             "btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate" })
	 * @apiGroup doc
	 * 
	 * @apiParam {Object} params 请求的JSON，具体参数说明看下面数据
	 * @apiParam {string} params.uploadPath 文档保存路径(必传)
	 * @apiParam {string} params.strBookmarkDataPath 书签列表
	 *           (可不传，可以在OA助手config.js中配置)
	 * @apiParam {string} params.templatePath 模板列表 (可不传，可以在OA助手config.js中配置)
	 * @apiParam {string} params.buttonGroups 自定义按钮组 （可不传，不传显示所有按钮）
	 */
	function newDoc(params) {
		openDoc.apply(this, arguments);
	}

	/**
	 * @api _wps.openDoc(params) 打开指定文档
	 * @apiVersion 3.1.0
	 * @apiName openDoc
	 * @apiDescription 打开指定文档，可以通过参数控制打开文档格式，格式转换保存，禁止复制粘贴等，参照下面实例，也可一起配合调用。
	 * @apiExample {js} 新建文档 _wps.openDoc({ "docId": "文档ID", "uploadPath":
	 *             "保存文档接口", //"fileName": "不传fileName即为新建空文档", });
	 * @apiExample {js} 打文指定文档 _wps.openDoc({ "docId": "文档ID", "uploadPath":
	 *             "保存文档接口", "fileName": "获取服务器文档接口", });
	 * @apiExample {js} 保护模式打开 _wps.openDoc({ "docId": "文档ID", "uploadPath":
	 *             "保存文档接口", "fileName": "获取服务器文档接口", "openType": { //文档打开方式
	 *             ，不传正常打开 "protectType": 3,
	 *             //文档保护类型，-1：不启用保护模式，0：只允许对现有内容进行修订，1：只允许添加批注，2：只允许修改窗体域，3：只读
	 *             "password": "123456" } });
	 * @apiExample {js} 控制痕迹打开 _wps.openDoc({ "docId": "文档ID", "uploadPath":
	 *             "保存文档接口", "fileName": "获取服务器文档接口", "userName":
	 *             "用于更改显示修改人的用户名", "revisionCtrl":{ //痕迹控制 ，不传正常打开
	 *             "bOpenRevision":true, //true(打开)false(关闭)修订
	 *             "bShowRevision":true //true(显示)/false(关闭)痕迹 } });
	 * @apiExample {js} 保存文档转为pdf、UOT、UOF等 _wps.openDoc({ "docId": "文档ID",
	 *             "uploadPath": "保存文档接口", "fileName": "获取服务器文档接口", "suffix":
	 *             ".pdf|.uot" //可传多个，用“|”分割，保存时会按照所传的值转成对应的格式文档并上传 });
	 * @apiGroup doc
	 * 
	 * @apiParam {Object} params 请求的JSON，具体参数说明看下面数据
	 * @apiParam {String} params.docId 文档ID
	 * @apiParam {String} params.uploadPath 保存文档接口
	 * @apiParam {String} params.fileName 获取服务器文档接口（不传即为新建空文档）
	 * @apiParam {String} params.suffix
	 *           ".pdf|.uot"，可传多个，用“|”分割，保存时会按照所传的值转成对应的格式文档并上传
	 * @apiParam {String} params.userName 用于更改显示修改人的用户名
	 * @apiParam {string} params.strBookmarkDataPath 书签列表
	 *           (可不传，可以在OA助手config.js中配置)
	 * @apiParam {string} params.templatePath 模板列表 (可不传，可以在OA助手config.js中配置)
	 * @apiParam {string} params.buttonGroups 自定义按钮组 （可不传，不传显示所有按钮）
	 * @apiParam {String} params.revisionCtrl 痕迹控制 ，不传正常打开
	 * @apiParam {String} params.revisionCtrl.bOpenRevision true(打开)false(关闭)修订
	 * @apiParam {String} params.revisionCtrl.bShowRevision true(显示)/false(关闭)痕迹
	 * @apiParam {String} params.openType 文档打开方式 ，不传正常打开
	 * @apiParam {String} params.openType.protectType
	 *           文档保护类型，-1：不启用保护模式，0：只允许对现有内容进行修订，1：只允许添加批注，2：只允许修改窗体域，3：只读
	 * @apiParam {String} params.openType.password 密码
	 */
	var openDoc = function openDoc(params, fucns) {
		var self = this;
		var app = self.Application, ret;
		var fileName = params.fileName;
		var openType = params["openType"] || params["_openType"], protectTypes = [ 0, 1, 2, 3 ];
		var isReadonly = !!(openType && protectTypes.indexOf(openType.protectType) > -1);
		if (fileName && (fileName.indexOf("http") === 0 || fileName.indexOf("ftp") === 0)) {
			ret = app.openDocumentRemote_s(fileName, isReadonly);
		} else if (fileName && (fileName.indexOf("/") > -1 || fileName.indexOf("\\") > -1)) {
			ret = app.openDocument(fileName, isReadonly);
		} else if (params.newFileName) {
			ret = app.createDocument(params.newFileName);
		} else {
			ret = app.createDocument("doc");
		}
		// console.log(ret);
		var revisionCtrl = params.revisionCtrl || {};
		// console.log(isReadonly + "-" + revisionCtrl.bOpenRevision);
		params.userName && app.setUserName(params.userName);
		app.enableRevision(!!revisionCtrl.bOpenRevision);
		// console.log("openType:" + JSON.stringify(openType));
		if (isReadonly === true) {
			app.enableProtect(true);
			app.enableCut(false);
			app.enableCopy(false);
		} else {
			app.enableProtect(false);
			app.enableCut(true);
			app.enableCopy(true);
		}
		if (params.uploadPath && isReadonly === false) {
			var ret = app.registerEvent("DIID_ApplicationEvents4", "DocumentBeforeSave", "DocumentBeforeSaveCallBack");
			// console.log("DocumentBeforeSave:" + ret);
			// GUID貌似只能为DIID_ApplicationEvents4
			var currentActiveDocument = app.ActiveDocument, lastSaveTimeStamp;
			window.DocumentBeforeSaveCallBack = function DocumentBeforeSaveCallBack(Doc, SaveAsUI, Cancel) {
				// console.log("另存为" + SaveAsUI.GetValue());
				if (currentActiveDocument !== Doc) {
					return;// 非当前编辑实例
				}
				Cancel.SetValue(true);// 不触发界面另存为
				if (lastSaveTimeStamp > 0 && ((new Date()).getTime() - lastSaveTimeStamp <= saveTimeout) && confirm("频繁保存！")) {
					// console.log("频繁保存！")
				} else if (confirm("请确认保存并提交到OA？")) {
					// 当前文档才保存(会跨浏览器和窗口)
					lastSaveTimeStamp = (new Date()).getTime();// 重新获取时间，排除确认时间
					ret = app.saveURL_s(params.uploadPath, params.newFileName || "新建.doc");
				}
			};
		}
		if (fucns && Array.isArray(fucns) && fucns.length > 0) {
			for (var index = 0; index < fucns.length; index++) {
				var fucn = fucns[index];
				for ( var key in fucn) {
					if (key === "OpenDoc") {
						alert("文档已打开!");
						continue;
					}
					exports[key] && exports[key].apply(exports, [ fucn[key] ]);
				}
			}
		}
	};

	/**
	 * @api _wps.onlineEditDoc(params) 在线编辑文档（文档不落地）
	 * @apiVersion 3.1.0
	 * @apiName onlineEditDoc
	 * @apiDescription 从OA系统打开一个指定文档进行编辑时，采用文档不落地的方式打开文档，可以保证文档的安全性，并且不会在本地留有文件的缓存数据
	 * @apiExample {js} 文档不落地 _wps.onlineEditDoc({ "docId": docId, //文档ID
	 *             "uploadPath": url + this.getPath("file/upload?id=" + docId),
	 *             //保存文档接口 "fileName": url + this.getPath("file/download/" +
	 *             docId), //根据文档id获取服务器文档接口 "strBookmarkDataPath": url +
	 *             this.getPath("bookmark/getAllBookmark"),//书签列表接口
	 *             "templatePath": url +
	 *             this.getPath("template/paging"),//正文模板列表接口 "buttonGroups":
	 *             "btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate",
	 *             "userName": '王五'//用户名 })
	 * @apiGroup doc
	 * 
	 * @apiParam {Object} params 请求的JSON，具体参数说明看下面数据
	 * @apiParam {string} params.docId 文档ID
	 * @apiParam {string} params.uploadPath 文档保存路径(必传)
	 * @apiParam {string} params.fileName 文档下载路径(必传)
	 * @apiParam {string} params.strBookmarkDataPath 书签列表
	 *           (可不传，可以在OA助手config.js中配置)
	 * @apiParam {string} params.templatePath 模板列表 (可不传，可以在OA助手config.js中配置)
	 * @apiParam {string} params.buttonGroups 自定义按钮组 （可不传，不传显示所有按钮）
	 */
	var onlineEditDoc = function onlineEditDoc(params) {
		openDoc.apply(this, arguments);
	};

	/**
	 * @api _wps.insertRedHeadDocFromWeb(params,templateURL,replaceBookMark) 套红头
	 * @apiVersion 3.1.0
	 * @apiName insertRedHeadDocFromWeb
	 * @apiDescription 1.从WPS的OA助手菜单上点击【套红头】，在弹出的红头列表对话框中选择一个指定的红头模板，点击【选择模板】按钮，就会将红头文件插入到当前文档的顶部
	 *                 <br?>
	 *                 2.从OA系统界面点击【套红头】，其中，红头模板通过参数templateURL传入，模板中的书签通过参数replaceBookMark传入，若模板中存在对应书签，则会将当前文档的正文插入到书签所在位置；若不存在对应书签，则会弹出提示信息
	 *                 (支持多次点击套后按钮，和重复套红头)
	 * @apiExample {js} 套红头 _wps.insertRedHeadDocFromWeb({ "docId": fileId,
	 *             //文档ID "uploadPath": url + dispatch.getPath("file/upload?id=" +
	 *             fileId), //保存文档接口 "fileName": url +
	 *             dispatch.getPath("file/download/" + fileId),
	 *             //根据文档id获取服务器文档接口 "buttonGroups": "btnInsertRedHeader", },url +
	 *             dispatch.getPath("file/download/" +
	 *             templateId),"Content");//红头模板中填充正文的位置书签名
	 * @apiGroup doc
	 * 
	 * @apiParam {Object} params 请求的JSON，具体参数说明看下面数据
	 * @apiParam {string} params.docId 文档ID
	 * @apiParam {string} params.uploadPath 文档保存路径(必传)
	 * @apiParam {string} params.fileName 文档下载路径(必传)
	 * @apiParam {string} params.buttonGroups 自定义按钮组 （可不传，不传显示所有按钮）
	 * @apiParam {string} templateURL 红头文件的获取路径
	 * @apiParam {string} replaceBookMark 正文你换的书签名
	 */
	var insertRedHeadDocFromWeb = function insertRedHeadDocFromWeb(params, templateURL, replaceBookMark) {
		var self = this;
		var app = self.Application;
		openDoc.apply(self, [ params ]);
		if (templateURL && replaceBookMark) {
			// 第一次点击套红头按钮
			function firstInsertRedHead(strFile, bookMarks, bookmark) {
				var wpsApp = self.Application;
				var activeDoc = wpsApp.ActiveDocument;
				var selection = wpsApp.ActiveWindow.Selection;
				selection.WholeStory(); // 选取全文
				bookMarks.Add("quanwen", selection.Range)
				selection.Cut();
				activeDoc.TrackRevisions = false; // 准备以非批注的模式插入红头文件
				selection.InsertFile(strFile);
				if (bookMarks.Exists(bookmark)) {
					var bookmark1 = bookMarks.Item(bookmark);
					bookmark1.Range.Select(); // 获取指定书签位置
					var s = activeDoc.ActiveWindow.Selection;
					s.Paste();
					// 取消WPS关闭时的提示信息 wps_enum.js WdAlertLevel
					self.Application.DisplayAlerts = 0;
				} else {
					alert("套红头失败，您选择的红头模板没有对应书签：" + bookmark);
				}
			}
			/**
			 * 从OA-web端点击套红头
			 * 
			 * @param {*}
			 *            params 'templateURL':'',获取模板接口 'replaceBookMark':'',标签
			 */
			var wpsApp = self.Application;
			var activeDoc = wpsApp.ActiveDocument;
			if (activeDoc) {
				var bookMarks = activeDoc.Bookmarks;
				if (!bookMarks.Item("quanwen")) { // 当前文档没有"quanwen"书签时候表示第一次套红
					// WPS上第一次点击套红头按钮
					firstInsertRedHead(templateURL, bookMarks, replaceBookMark);
				} else {
					alert("web端重复选择红头模板套红时，请在WPS-OA助手上操作")
				}
			}
			// 取消WPS关闭时的提示信息 wps_enum.js WdAlertLevel
			self.Application.DisplayAlerts = 0;
		} else {
			alert("套红头失败，您选择的红头模板没有正文书签！")
		}
	};

	/**
	 * @api _wps.fillTemplate(params) 填充模板
	 * @apiVersion 3.1.0
	 * @apiName fillTemplate
	 * @apiDescription
	 *          <P>
	 *          说明：dataFromWeb 和 dataFromServer
	 *          都传的话，dataFromServer中数据会覆盖dataFromWeb中的数据。填充模板的逻辑也是先打开文档，再根据数据源，匹配书签一一插入数据。
	 *          填充的数据格式：name: 书签的名称 ， text：填充的内容， type: 填充内容的格式，（link :
	 *          从链接获取内容，比如说直接下载文档，text 或者不填都为文本，pic: 图片地址,table：填充表格数据）
	 * @apiExample {js} 填充模板 _wps.openDoc({ "docId": docId, //文档ID "fileName":
	 *             "http://dev.wpseco.cn/wps-oa/template/getFileData/98",
	 *             "uploadPath": uploadPath + docId, //保存文档接口 }, [ {
	 *             "fillTemplate": { "dataFromWeb": [{"name": "FirstTitle",
	 *             "text": "web"}, { "name": "TopTitle1", "text": "军参谋-web" },
	 *             {"name": "TopTitle2", "text": "通信-web"}, { "name":
	 *             "ContentTitle", "text": "空军内部使用办文助手-web" }, {"name":
	 *             "Company", "text": "空军参谋部-web"}, { "name": "Contactor",
	 *             "text": "李四-web" }, {"name": "Telephone", "text": "26874"}, {
	 *             "name": "Content", "text": fileName, "type": "link" },
	 *             {"name": "table_index", "text": ["zhangsan", "18", "nan",
	 *             "lis", "19", "nan", "wangwu", "20", "nan", "zhaoliu", "21",
	 *             "nv", "sunqi", "22", "nv"], "type": "table" }], //
	 *             "dataFromServer":
	 *             "http://dev.wpseco.cn/wps-oa/document/getData" //后台获取数据的接口, } } ]) }
	 * @apiGroup doc
	 * 
	 * @apiParam {Object} fillTemplate 填充模板传递的数据
	 * @apiParam {string} params.dataFromWeb 从web页面传递的数据
	 * @apiParam {string} params.dataFromServer 从服务器获取的数据
	 */
	var fillTemplate = function fillTemplate(params) {
		var self = this;
		var dataFromWeb = params.dataFromWeb;
		var dataFromServer = params.dataFromServer;
		// 获取模板
		var template = self.Application.ActiveDocument;
		function insertTableFormWeb(doc, bookmark, data) {
			bookmark.Range.Select();
			var len = data.length;
			for (var i = 0; i < len; i++) {
				var content = data[i];
				// 将子表字段里的\u005e转回 ^ ,unicode编码里^是\u005e,\需要转义 lmf 2017-4-26
				// content = content.replace(/\\u005e/g, '^');
				var sel = doc.Application.Selection;
				sel.Text = content;
				if (i < len - 1) {
					sel.MoveRight(12, 1);
				} else {
					sel.MoveRight(1, 1);
				}
			}
		}
		// 获取文档内容
		function fillData(data, template) {
			data.forEach(function(it) {
				var bookmark = template.Bookmarks.Item(it.name);
				if (bookmark) {
					var type = it.type
					if (!type || type === "text") {
						bookmark.Range.Text = it.text;
					} else if (type === "link") {
						bookmark.Range.InsertFile(it.text)
					} else if (type === "pic") {
						var pic = bookmark.Range.InlineShapes.AddPicture(it.text);
						pic.LockAspectRatio = 0;
						if (it.picHeight) {
							pic.Height = it.picHeight; // 设定图片高度
						}
						if (it.picWidth) {
							pic.Width = it.picWidth; // 设定图片宽度
						}
					} else if (type === "table") { // 表格中插入数据
						insertTableFormWeb(template, bookmark, it.text)
					}
				}
			})
		}
		// 不支持dataFromServer
		dataFromServer = false;
		if (dataFromServer) {
			$.ajax({
				url : dataFromServer,
				async : false,
				method : "post",
				dataType : 'json',
				success : function(res) {
					res.push.apply(res, dataFromWeb);
					fillData(res, template);
				}
			});
		} else {
			fillData(dataFromWeb, template);
		}
	};

	/**
	 * @api _wps.openET(params) 打开表格文档
	 * @apiVersion 2.0.0
	 * @apiName openET
	 * @apiDescription 打开表格类文档
	 * @apiExample {js} 新建文档 _wps.openET({
	 *             "uploadPath":"http://dev.wpseco.cn/wps-oa/document/saveFile?docId=4257",
	 *             "fileName":"http://dev.wpseco.cn/wps-oa/document/getFileData/4257",
	 *             "buttonGroups":
	 *             "btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate", })
	 * @apiGroup et
	 * 
	 * @apiParam {Object} params 请求的JSON，具体参数说明看下面数据
	 * @apiParam {string} params.uploadPath 文档保存路径(必传)
	 * @apiParam {string} params.fileName 获取服务器文档接口
	 * @apiParam {string} params.buttonGroups 自定义按钮组 （可不传，不传显示所有按钮）
	 */
	var openET = function openET(params) {
		var self = this;
		var app = self.Application, ret;
		var fileName = params.fileName;
		var openType = params["openType"] || params["_openType"], protectTypes = [ 0, 1, 2, 3 ];
		var isReadonly = !!(openType && protectTypes.indexOf(openType.protectType) > -1);
		if (fileName && (fileName.indexOf("http") === 0 || fileName.indexOf("ftp") === 0)) {
			ret = app.openDocumentRemote_s(fileName, isReadonly);
		} else if (fileName && (fileName.indexOf("/") > -1 || fileName.indexOf("\\") > -1)) {
			ret = app.openDocument(fileName, isReadonly);
		} else {
			ret = app.Workbooks.Add();
		}
		params.userName && app.put_UserName(params.userName);
		if (isReadonly === true) {
			app.enableCut(false);
			app.enableCopy(false);
		} else {
			app.enableCut(true);
			app.enableCopy(true);
		}
		if (params.uploadPath && isReadonly === false) {
			var ret = app.registerEvent("DIID_AppEvents", "WorkbookBeforeSave", "WorkbookBeforeSaveCallBack");
			// console.log("WorkbookBeforeSave:" + ret);
			var currentActiveWorkbook = app.get_ActiveWorkbook(), lastSaveTimeStamp;
			window.WorkbookBeforeSaveCallBack = function WorkbookBeforeSaveCallBack(Doc, SaveAsUI, Cancel) {
				// console.log("另存为" + SaveAsUI);
				if (currentActiveWorkbook !== Doc) {
					return;// 非当前编辑实例
				}
				Cancel.SetValue(true);// 不触发界面另存为
				if (lastSaveTimeStamp > 0 && ((new Date()).getTime() - lastSaveTimeStamp <= saveTimeout) && confirm("频繁保存！")) {
					// console.log("频繁保存！")
				} else if (confirm("请确认保存并提交到OA？")) {
					// 当前文档才保存(会跨浏览器和窗口)
					lastSaveTimeStamp = (new Date()).getTime();// 重新获取时间，排除确认时间
					ret = app.saveURL_s(params.uploadPath, params.newFileName || "新建.xls");
				}
			};
		}
	}

	/**
	 * @api _wps.openWpp(params) 打开PPT文档
	 * @apiVersion 2.0.0
	 * @apiName openWpp
	 * @apiDescription 打开PPT文档
	 * @apiExample {js} 新建文档 _wps.openWpp({
	 *             "uploadPath":"http://dev.wpseco.cn/wps-oa/document/saveFile?docId=4257",
	 *             "fileName":"http://dev.wpseco.cn/wps-oa/document/getFileData/4257",
	 *             "buttonGroups":
	 *             "btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate", })
	 * @apiGroup ppt
	 * 
	 * @apiParam {Object} params 请求的JSON，具体参数说明看下面数据
	 * @apiParam {string} params.uploadPath 文档保存路径(必传)
	 * @apiParam {string} params.fileName 获取服务器文档接口
	 * @apiParam {string} params.buttonGroups 自定义按钮组 （可不传，不传显示所有按钮）
	 */
	var openWpp = function openWpp(params) {
		var self = this;
		var app = self.Application, ret;
		var fileName = params.fileName;
		var openType = params["openType"] || params["_openType"], protectTypes = [ 0, 1, 2, 3 ];
		var isReadonly = !!(openType && protectTypes.indexOf(openType.protectType) > -1);
		// console.log("isReadonly:" + isReadonly);
		if (fileName && (fileName.indexOf("http") === 0 || fileName.indexOf("ftp") === 0)) {
			ret = app.openDocumentRemote_s(fileName, isReadonly);
		} else if (fileName && (fileName.indexOf("/") > -1 || fileName.indexOf("\\") > -1)) {
			ret = app.openDocument(fileName, isReadonly);
		} else {
			ret = app.Presentations.Add();
			if (app.ActivePresentation && app.ActivePresentation.Slides) {
				var slides = app.ActivePresentation.Slides;
				slides.Add(1, 1); // wpp_enum.js PpSlideLayout
			}
		}
		// params.userName && app.put_UserName(params.userName);
		if (isReadonly === true) {
			app.enableCut(false);
			app.enableCopy(false);
		} else {
			app.enableCut(true);
			app.enableCopy(true);
		}
		if (params.uploadPath && isReadonly === false) {
			// console.log("params.uploadPath:" + params.uploadPath);
			var ret = app.registerEvent("IID_EApplication", "PresentationBeforeSave", "PresentationBeforeSaveCallBack");
			// console.log("WorkbookBeforeSave:" + ret);
			var currentActivePresentation = app.get_ActivePresentation(), lastSaveTimeStamp;
			window.PresentationBeforeSaveCallBack = function PresentationBeforeSaveCallBack(Pres, Cancel) {
				// console.log("取消" + Cancel.GetValue());
				if (currentActivePresentation !== Pres) {
					return;// 非当前编辑实例
				}
				Cancel.SetValue(true);// 不触发界面另存为
				if (lastSaveTimeStamp > 0 && ((new Date()).getTime() - lastSaveTimeStamp <= saveTimeout)) {
					console.log("频繁保存！");// 会连续触发，所以不提示
				} else if (confirm("请确认保存并提交到OA？")) {
					// 当前文档才保存(会跨浏览器和窗口)
					lastSaveTimeStamp = (new Date()).getTime();// 重新获取时间，排除确认时间
					ret = app.saveURL_s(params.uploadPath, params.newFileName || "新建.ppt");
				}
			};
		}
	}
	exports.newDoc = newDoc;
	exports.openDoc = openDoc;
	exports.onlineEditDoc = onlineEditDoc;
	exports.insertRedHeadDocFromWeb = insertRedHeadDocFromWeb;
	exports.fillTemplate = fillTemplate;
	exports.openET = openET;
	exports.openWpp = openWpp;
	Object.defineProperty(exports, '__esModule', {
		value : true
	});
	// 签章使用
	// 选择书签
	// 转pdf
	// 定制按钮组合
	// 打开关闭修订
	// 打印
	// 文档校对
})));