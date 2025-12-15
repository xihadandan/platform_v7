// --------------------------  通用常量  ---------------------------
// var pluginStorage = window.wps.PluginStorage; //插件内存取数据的对象
var URL = "http://dev.wpseco.cn/wps-oa"
var OA_DOOR_BASE_URL="http://dev.wpseco.cn"//oa助手门户网站基础路径

var leftTaskPaneFlag = {
    currentTaskPaneId: "currentTaskPaneId",
    leftFlag: "leftFlag",
    activeTaskPaneId: "activeTaskPaneId"
}
var htmlURL = {
    leftItem: "leftItem.html",
    decomposeDoc: "decomposeDoc.html",
    docCreate: "docCreate.html",
    templateAddEdit: "templateAddEdit.html",
    templateList: "templateList.html",
    packInfo: "packInfo.html",
    yunpact: "yunpact.html"
}

//OA门户网站用接口
var OA_DOOR={
    uploadURL:OA_DOOR_BASE_URL +"/file/upload",//新文档保存接口
    previewURL:OA_DOOR_BASE_URL+"/file/preview/",//文档预览接口
    downloadURL:OA_DOOR_BASE_URL+"/file/download/",//文档下载接口
    templatePath: URL + "/template/paging", //正文模板列表接口
    templateBaseURL: URL + "/template/getFileData/", //指定正文模板基础接口
    redHeadsPath: URL + "/redHead/paging",     //默认红头模板列表获取路径
    getRedHeadPath: URL + "/redHead/getFileData/", //默认获取红头文件路径
    bookmarkPath: OA_DOOR_BASE_URL + "/bookmark/getAllBookmark", //书签列表接口
    setDocStatus:OA_DOOR_BASE_URL + "/file/setDocStatus/",
    checkOAClosed: OA_DOOR_BASE_URL + "/file/checkOAClosed/" //检测OA页面是否关闭
}

//后台获取数据用接口常量
var BASE_INTERFACE = {
    allRedHeadPath: URL + "/redHead/paging", //红头模板列表接口
    allTemplatePath: URL + "/template/paging", //正文模板列表接口
    redHeadBaseURL: URL + "/redHead/getFileData/", //指定红头模板基础接口
    templateBaseURL: URL + "/template/getFileData/", //指定正文模板基础接口
    redHeadPreviewURL: URL + "/redHead/preview/", //红头模板的预览接口
    bookmarkPath: URL + "/bookmark/getAllBookmark", //书签列表接口

}
// --------------------------  通用方法  ---------------------------

// 把这个去掉   选择书签是正常的！！！
// 去掉字符串中所有空格(包括中间空格,需要设置第2个参数为:g)
// String.prototype.trim = function(is_global) {
//     var result;
//     result = this.replace(/(^\s+)|(\s+$)/g, "");
//     if (is_global.toLowerCase() == "g") {
//         result = result.replace(/\s/g, "");
//     }
//     return result;
// }

//去除字符串左边空格
String.prototype.ltrim = function() {　　
    return this.replace(/(^\s*)/g, "");　　
}　　

//去除字符串右边空格
String.prototype.rtrim = function() {　　
    return this.replace(/(\s*$)/g, "");　　
}

//扩展js string endwith,startwith方法
String.prototype.endWith = function(str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substring(this.length - str.length) == str)
        return true;
    else
        return false;
    return true;
}

String.prototype.startWith = function(str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substr(0, str.length) == str)
        return true;
    else
        return false;
    return true;
}

//UTF-16转UTF-8
function utf16ToUtf8(s) {
    if (!s) {
        return;
    }
    var i, code, ret = [],
        len = s.length;
    for (i = 0; i < len; i++) {
        code = s.charCodeAt(i);
        if (code > 0x0 && code <= 0x7f) {
            //单字节
            //UTF-16 0000 - 007F
            //UTF-8  0xxxxxxx
            ret.push(s.charAt(i));
        } else if (code >= 0x80 && code <= 0x7ff) {
            //双字节
            //UTF-16 0080 - 07FF
            //UTF-8  110xxxxx 10xxxxxx
            ret.push(
                //110xxxxx
                String.fromCharCode(0xc0 | ((code >> 6) & 0x1f)),
                //10xxxxxx
                String.fromCharCode(0x80 | (code & 0x3f))
            );
        } else if (code >= 0x800 && code <= 0xffff) {
            //三字节
            //UTF-16 0800 - FFFF
            //UTF-8  1110xxxx 10xxxxxx 10xxxxxx
            ret.push(
                //1110xxxx
                String.fromCharCode(0xe0 | ((code >> 12) & 0xf)),
                //10xxxxxx
                String.fromCharCode(0x80 | ((code >> 6) & 0x3f)),
                //10xxxxxx
                String.fromCharCode(0x80 | (code & 0x3f))
            );
        }
    }

    return ret.join('');

}

//UTF-8转UTF-16
function utf8ToUtf16(s) {
    if (!s) {
        return;
    }

    var i, codes, bytes, ret = [],
        len = s.length;
    for (i = 0; i < len; i++) {
        codes = [];
        codes.push(s.charCodeAt(i));
        if (((codes[0] >> 7) & 0xff) == 0x0) {
            //单字节  0xxxxxxx
            ret.push(s.charAt(i));
        } else if (((codes[0] >> 5) & 0xff) == 0x6) {
            //双字节  110xxxxx 10xxxxxx
            codes.push(s.charCodeAt(++i));
            bytes = [];
            bytes.push(codes[0] & 0x1f);
            bytes.push(codes[1] & 0x3f);
            ret.push(String.fromCharCode((bytes[0] << 6) | bytes[1]));
        } else if (((codes[0] >> 4) & 0xff) == 0xe) {
            //三字节  1110xxxx 10xxxxxx 10xxxxxx
            codes.push(s.charCodeAt(++i));
            codes.push(s.charCodeAt(++i));
            bytes = [];
            bytes.push((codes[0] << 4) | ((codes[1] >> 2) & 0xf));
            bytes.push(((codes[1] & 0x3) << 6) | (codes[2] & 0x3f));
            ret.push(String.fromCharCode((bytes[0] << 8) | bytes[1]));
        }
    }
    return ret.join('');

}

function currentTime() {
    var now = new Date();

    var year = now.getFullYear(); //年
    var month = now.getMonth() + 1; //月
    var day = now.getDate(); //日

    var hh = now.getHours(); //时
    var mm = now.getMinutes(); //分

    var clock = year + "";

    if (month < 10)
        clock += "0";

    clock += month + "";

    if (day < 10)
        clock += "0";

    clock += day + "";

    if (hh < 10)
        clock += "0";

    clock += hh + "";
    if (mm < 10) clock += '0';
    clock += mm;
    return (clock);
}

var chinese = ['〇', '一', '二', '三', '四', '五', '六', '七', '八', '九'];

function getChineseYear() {
    var today = new Date();
    var y = today.getFullYear().toString();
    var result = "";
    for (var i = 0; i < y.length; i++) {
        result += chinese[y.charAt(i)];
    }
    result += "年";
    return result;
}

function getChineseMonth() {
    var today = new Date();
    var result = "";
    var m = (today.getMonth() + 1).toString();

    if (m.length == 2) {
        if (m.charAt(0) == "1") {
            result += ("十" + chinese[m.charAt(1)] + "月");
        }
    } else {
        result += (chinese[m.charAt(0)] + "月");
    }
    return result;
}

function getChineseDay() {
    var today = new Date();
    var d = today.getDate().toString();
    var result = "";

    if (d.length == 2) {
        if (d.charAt(0) == "1") {
            result += ("十" + chinese[d.charAt(1)] + "日");
        } else {
            result += (chinese[d.charAt(0)] + "十" + chinese[d.charAt(1)] + "日");
        }

    } else {
        result += (chinese[d.charAt(0)] + "日");
    }
    return result;
}

/**
 *
 * @param {*} path 原路径
 * @param {*} temName 要替换的文件名
 */
function replaceFileName(path, temName) {
    var start = path.lastIndexOf("/");
    if (start === -1) {
        start = path.lastIndexOf("\\");
    }
    var end = path.lastIndexOf(".");
    var regex = path.slice(start + 1, end);
    return path.replace(regex, temName);
}

/**
 *
 * @param {*} prop prop对象
 * @param {*} key prop中对应的key值
 */
function propDel(prop, key) {
    var _value = propGetFromStorage(doc.DocID,key);
    if (_value) {
        _value.Delete();
    }
}

/**
 *
 * @param {*} prop prop对象
 * @param {*} key prop中对应的key值
 * @param {*} value 对应key要存入的值
 */
function propAddDefault(prop, key, value) {
    propAdd(prop, key, MsoDocProperties.msoPropertyTypeString, value);
}

/**
 *
 * @param {*} prop prop对象
 * @param {*} key prop中对应的key值
 * @param {*} type 存储数据类型
 * @param {*} value 对应key要存入的值
 */
function propAdd(prop, key, type, value) {
    var _value = prop.Item(key);
    if (_value) {
        _value.Delete();
    }
    prop.Add(key, false, type, value);
}

function propAddStorage(docId,key,value){
    var storage = wps.PluginStorage;
    var data = storage.getItem(docId);
    var json = data ? JSON.parse(data) : {};
    json[key] = value;
    storage.setItem(docId,JSON.stringify(json))
}

function propGetFromStorage(docId,key){
    var storage = wps.PluginStorage;
    var data = storage.getItem(docId);
    if(!data){
        return ""
    }else {
        var json = JSON.parse(data);

        return json[key] === undefined ? undefined : {"Value":json[key]}
    }
}
/**
 * 判断文档集合中是否已经打开对象文档
 * @param {*} docs Documents 对象
 * @param {*} path 文档对象的绝对路径
 */
function isOpen(docs, path) {
    path = path.replace(/\//g, "\\");
    var count = docs.Count;
    var item;
    if (count >= 1) {
        for (var index = 1; index <= count; index++) {
            item = docs.Item(index);
            // alert("FullName:" + item.FullName);
            if (item.FullName === path) {
                return true;
            }
        }
        return false;
    }
}

/**
 * 获取文件路径前缀
 */
function GetUrlPath() {
    var url = document.location.toString();
    if (url.indexOf("/") != -1) {
        url = url.substring(0, url.lastIndexOf("/"));
    }
    return url;
}

/**
 * 获取文件路径
 * @param {*} html 文件全称
 */
function getHtmlURL(html) {
    //弹出辅助窗格框
    var url = GetUrlPath();

    if (url.length != 0) {
        url = url.concat("/" + html);
    } else {
        url = url.concat("./" + html);
    }
    return url;
}

/**
 * wps内弹出web页面
 * @param {*} html 文件名
 * @param {*} title 窗口标题
 * @param {*} hight 窗口高
 * @param {*} width 窗口宽
 */
function OnShowDialog(html, title, hight, width) {
    var ActiveDoc = wps.WpsApplication().ActiveDocument;
    if(ActiveDoc){
        var url = getHtmlURL(html);
        wps.ShowDialog(url, title, hight, width);
    }else{
        alert("WPS当前没有可操作文档！")
    }
}

/**
 * 打开服务器上的文件
 * @param {*} fileUrl 文件url路径
 */
function openFile(params) {
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;
    var fileUrl = params.fileName;
    var doc;
    if (fileUrl) {
        var path = wps.OAAssist.DownloadFile(fileUrl);
        doc = wps.WpsApplication().Documents.Open(path, false, false);
    } else {
        if (activeDoc && (activeDoc.Name.indexOf("文档") != -1 || activeDoc.Name.indexOf("文字文稿1") != -1)) {
            doc = activeDoc;
        } else {
            doc = wpsApp.Documents.Add();
        }
         // 暂时用这个路径   用户 公共  公共文档
         // 如果日后版本更新了，这里也要修改，有获得temp路径的方法   wps.Env.GetTempPath()
        if(wps.Env&&wps.Env.GetTempPath){
            if (params.newFileName) {
                doc.SaveAs2(wps.Env.GetTempPath()+"/" + params.newFileName);
            } else if(doc.FullName.indexOf('C:') == -1) {
                doc.SaveAs2(wps.Env.GetTempPath() +"/"+ currentTime());
            }
        }else{
            if (params.newFileName) {
                doc.SaveAs2("C:/Users/Public/Documents/" + params.newFileName);
            } else if(doc.FullName.indexOf('C:') == -1) {
                doc.SaveAs2("C:/Users/Public/Documents/" + currentTime());
            }
        }
    }
    return doc;
}

/**
 * 文档不落地打开服务器上的文件
 * @param {*} fileUrl 文件url路径
 */
function OnlineEditOpenFile(fileUrl) {
    var doc;
    if (fileUrl) {
        //下载文档不落地（16版WPS的925后支持）
        wps.WpsApplication().Documents.OpenFromUrl(fileUrl,"","OnDownFail");
        doc = wps.WpsApplication().ActiveDocument;
    } else {
        if(wps.WpsApplication().ActiveDocument&&wps.WpsApplication().ActiveDocument.Name.indexOf("文档")!=-1){
              doc = wps.WpsApplication().ActiveDocument;
        }else{
            doc = wps.WpsApplication().Documents.Add();
        }
    }
    return doc;
}

/**
 * 删除文件函数
 * @param {*} name
 */
function deleteFile(name) {
    var fso = new ActiveXObject("Scripting.FileSystemObject");
    if (fso.FileExists(name)) {
        fso.DeleteFile(name);
    } else {
        return false;
    }
}

/**
 * 解析返回response的参数
 * @param {*} resp
 * @return {*} body
 */
function handleResultBody(resp) {
    var result = "";
    if (resp.Body) {
        result = new Base64().decode(resp.Body);
		//转utf-16编码，防止接收后台数据转base64后中文乱码
        result = utf8ToUtf16(result);
    }
    // return JSON.parse(result);
    return result;
}

//清掉文档之前的CustomDocumentProperties里所参数
function deleteCustom(docId){
    var storage = wps.PluginStorage;
    storage.removeItem(docId)

    // var doc = wps.WpsApplication().ActiveDocument;
    // var data = ["ribbonExt","isOA","isOnlineFile","close","protectType","password","notCopy",
    // "suffix","showReviseButton","info","buttonGroups","closeDocLister",
    // "closeDoc","notSavedCloseDoc","bOpenRevision","bShowRevision","openFlag","showFlag","disableBtns","cannot_save","notSave"];
    // data.forEach(function(it) {
    //     var item = propGetFromStorage(doc.DocID,it);
    //     if(item){
    //         item.Delete();
    //     }
    // })
}


/**
 * 判断WPS中的文件个数是否为0，若为0则关闭WPS函数
 * @param {*} name
 */
function closeWpsIfNoDocument() {
    var wpsApp = wps.WpsApplication();
    var docs = wpsApp.Documents;

    if (!docs || docs.Count == 0) {

        wps.ApiEvent.Cancel = true;
        wpsApp.Quit();
    }
}

function OnDownFail(resp) {
    alert("文档下载失败");
}

function activeTab(){
    // 默认显示OA助手页签
    wps.ExecFunc("wps.ribbonUI.ActivateTab('WPSExtOfficeTab')");
}

function ZSYRedHead(){
    var wpsApp = wps.WpsApplication();
    var s = wpsApp.ActiveWindow.Selection;
    s.WholeStory();
    s.Cut();
    s.InsertFile("D:\\金山文档\\7、中石油\\测试公司会议纪要模版.doc");
    var doc = wpsApp.ActiveDocument;
    var shapes = doc.Shapes;
    if (shapes.Count > 0) {
        shapes.Item(1).Select();
    }
    s.GoTo(3,2,1)
    s.Paste();
    var ts = doc.Tables;
    if(ts.Count>0){
        ts.Item(1).Select();
        s.Cut();
    }
    var pagecount = doc.BuiltInDocumentProperties.Item(WdBuiltInProperty.wdPropertyPages); //获取文档页数
    s.GoTo(WdGoToItem.wdGoToPage,WdGoToDirection.wdGoToFirst, pagecount.Value)
    s.Paste();
}