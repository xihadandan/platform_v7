// --------------------------  通用常量  ---------------------------
// var URL = "http://xtbgnw.gdgov.cn/OA/instance-web/minstone";// 生产环境
// var ATTACH_URL = "http://xtbgnw.gdgov.cn/OA/AttachmentTool/minstone"; // 生产环境
// URL = "http://xtbgyy.digitalgd.com.cn/OA/instance-web/minstone"; // 预发布环境
// ATTACH_URL = "http://xtbgyy.digitalgd.com.cn/OA/AttachmentTool/minstone"; // 预发布环境 
// URL = "http://localhost:8080/instance-web/minstone"; // 开发环境 
// ATTACH_URL = "http://172.16.2.73:8080/AttachmentTool/minstone"; // 开发环境


// var INTERFACESET = {
//     TEMP_FINDALL: URL + "/oaTemplate/findAll", //查询所有文档
//     TEMP_OPENPATH: URL + "/oaTemplate/getFileData", //打开模板url
//     SAVE_DOC: URL + "/oaDocument/saveDoc", //保存/修改文档信息
//     PLACE_FILE: URL + "/oaDocument/placeFile" //文档归档接口
// }

//数字广东OA用常量
var SZGD_URL = {
    // redHeadPath: URL + "/redHead/paging", //红头模板列表接口
    // templatePath: URL + "/template/paging", //正文模板列表接口
    // redHeadBaseURL: URL + "/redHead/getFileData/", //指定红头模板基础接口
    // templateBaseURL: URL + "/template/getFileData/", //指定正文模板基础接口
    // redHeadPreviewURL: URL + "/redHead/preview/", //红头模板的预览接口
    // bookmarkPath: URL + "/bookmark/getAllBookmark", //书签列表接口

    downLoadFileUrl: "/AttachmentTool/minstone/attachment/downloadFile?", // 文件下载
    unLockDocUrl: "/instance-web/minstone/wfDocBody/unLockDoc?",// 解锁
    copyUrl: "/instance-web/minstone/wfDocBody/copyDoc?",// 备份正文
    lockDocUrl: "/instance-web/minstone/wfDocBody/getLockInfo?" // 正文锁
}


var LEFTTASKPANE_FLAG = {
    CURRENT_TASKPANEID: "currentTaskPaneId",
    LEFT_FLAG: "leftFlag",
    ACTIVE_TASKPANEID: "activeTaskPaneId",
    TASKPANEID: "taskPaneId",
    OA_TASKPANEID: "oaTaskPaneId", //OA流程右边框ID taskPaneId
}
var HTML_URL = {
    LEFTITEM: "leftItem.html",
    DECOMPOSEDOC: "decomposeDoc.html",
    DOCCREATE: "docCreate.html",
    TEMPLATE_ADDEDIT: "templateAddEdit.html",
    TEMPLATE_LIST: "templateList.html",
    PACT_INFO: "packInfo.html",
    PACT_YUN: "yunpact.html",
    OA_TASKPANE: "oaTaskPane.html", //OA流程右边框
}

//web页面传递的参数的key值
var PARAM_KEY = {
    DOC_ID: "docId", //存储文档id的key值
    UPLOAD_PATH: "uploadPath", //存储文档上传路径的key值
    FILE_NAME: "fileName", //打开文档接口的key值
    TEMPLATE_URL: "templateURL", //获取红头模板接口的key值
    STATE_KEY: "docState", //存储文档状态的key值
    PROPS_KEY: "props", //存储文档信息的key值
    USERNAME: "username" //存储用户名的key值
}

// ========================oa-demo start========================
//常用字符串
var CONSTANT = {

}

// ========================oa-demo end========================


// --------------------------  通用方法  ---------------------------

//获取CustomDocumentProperties对象
function getProp() { //自定义文档属性窗口
    var doc = wps.WppApplication().ActivePresentation;
    return doc.CustomDocumentProperties
}

// 去掉字符串中所有空格(包括中间空格,需要设置第2个参数为:g)
function Trim(str, is_global) {
    var result;
    result = str.replace(/(^\s+)|(\s+$)/g, "");
    if (is_global.toLowerCase() == "g") {
        result = result.replace(/\s/g, "");
    }
    return result;
}

//去除字符串左边空格
String.prototype.ltrim = function () {
    return this.replace(/(^\s*)/g, "");
}

//去除字符串右边空格
String.prototype.rtrim = function () {
    return this.replace(/(\s*$)/g, "");
}

//扩展js string endwith,startwith方法
String.prototype.endWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substring(this.length - str.length) == str)
        return true;
    else
        return false;
    return true;
}

String.prototype.startWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length) {
        return false;
    }
    if (this.substr(0, str.length) == str) {
        return true;
    } else {
        return false;
    }
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

//若要显示:当前日期加时间(如:200906121200)
function CurentTime() {
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
    var _value = prop.Item(key);
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
    propAdd(prop, key, 4, value);
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

/**
 * 判断文档集合中是否已经打开对象文档
 * @param {*} docs Presentations 对象
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
 * 获取文件路径后缀名
 */
function GetFilePathSuffix(path) {
    return path.substring(path.lastIndexOf(".") + 1, path.length);
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
    var url = getHtmlURL(html);

    wps.ShowDialog(url, title, hight, width);
}

/**
 * 打开服务器上的文件
 * @param {*} fileUrl 文件url路径
 */
function openFile(fileUrl) {
    var doc;
    if (fileUrl) { //打开指定文档
        var path = wps.OAAssist.DownloadFile(fileUrl);

        doc = wps.WppApplication().Presentations.Open(path);
    } else { //打开新文档
        var activeDoc = wps.WppApplication().ActivePresentation;
        if (activeDoc) {
            doc = activeDoc.Name !== "工作簿1" ? wps.WppApplication().Presentations.Add() : activeDoc;
        } else {
            doc = wps.WppApplication().Presentations.Add();
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
    }
    return result;
}

/**
 * 判断WPS中的文件个数是否为0，若为0则关闭WPS函数
 * @param {*} name 
 */
function closeWpsIfNoDocument() {
    var wpsApp = wps.WppApplication();
    var docs = wpsApp.Presentations;
    if (docs && docs.Count == 0) {
        //wps.ApiEvent.Cancel = true;
        wpsApp.Quit();
    }
}

function OnUploadSuccess(response) {
    alert("保存成功");
}

function OnUploadSuccessAndClose(response) {
    alert("保存成功，文档即将关闭。");
    if (wps.WppApplication().Presentations.Count > 0 && wps.WppApplication().ActivePresentation.Saved) {
        wps.WppApplication().ActivePresentation.Close();
    }
    // closeWpsIfNoDocument();
}

function OnUploadFail(response) {
    alert("上传失败");
    wps.ApiEvent.Cancel = true;
    wps.WppApplication().ActivePresentation.Saved = false;
}