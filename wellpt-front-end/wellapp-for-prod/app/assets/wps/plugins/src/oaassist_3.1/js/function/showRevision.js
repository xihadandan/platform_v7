/**
 * 显示/接收/拒绝修订
 */

var openFlag = "openFlag";
var showFlag = "showFlag";

var bShowRevision = false; //toggleButton

/**
 * web端打开/关闭修订
 * params参数结构
 * params:{
 *   bOpenRevision:'',true:打开/false:关闭
 *   'fileName':'',获取文档接口
 * }
 * @param {*} params
 */
function OnOpenRevisionClickedFromWeb(params) {
    OnOpenRevisionClicked(params)
}

/**
 * web端打开/关闭显示痕迹
 * params参数结构
 * params:{
 *   bShowRevision:'',true:打开/false:关闭
 *   'fileName':'',获取文档接口
 * }
 * @param {*} params
 */
function OnShowRevisionClickedFromWeb(params) {
    OnShowRevisionClicked(params)
}

/**
 * 打开/关闭修订
 * params参数结构
 * params:{
 *   bOpenRevision:'',true:打开/false:关闭
 *   'fileName':'',获取文档接口
 * }
 * @param {*} params
 */
function OnOpenRevisionClicked(params) {
    var fileName = params == null ? null : params.fileName;
    var bOpenRevision,bShowRevision ;

    var wpsApp = wps.WpsApplication();
    if (wpsApp) {
        var activeDoc = wpsApp.ActiveDocument;
        var doc  = activeDoc == null ? openFile(fileName) : activeDoc;
        // var prop = activeDoc.CustomDocumentProperties;
        if (activeDoc) {

            if (params) {
                //web页面调用
                bOpenRevision = params.bOpenRevision;
                bShowRevision = !params.bShowRevision;
            } else {
                //wps内调用
                var flag = propGetFromStorage(doc.DocID,openFlag);
                bOpenRevision = !((flag && flag.Value) || !flag);

            }
            activeDoc.Unprotect()
            activeDoc.TrackRevisions = bOpenRevision; //如果标记对指定文档的修改，则该属性值为True

            var ribbonExt = propGetFromStorage(doc.DocID,"ribbonExt");
            ribbonExt=ribbonExt == null ? getRibbonExt(true) : JSON.parse(ribbonExt.Value);


            //如果关闭修订,关闭显示痕迹并将按钮至灰
            if (!bOpenRevision) {
                ribbonExt.btnShowRevision = {
                    "OnGetEnabled": false,
                    "OnGetVisible": true,
                    "OnGetLabel": "",
                    "GetImage": "icon/ShowRevision.ico"
                }
                bShowRevision = bOpenRevision;
            } else {
                var sflag = propGetFromStorage(doc.DocID,showFlag);
                if (sflag && sflag.Value) {
                    bShowRevision = true;
                } else {
                    bShowRevision =bOpenRevision;
                }
                ribbonExt.btnShowRevision['OnGetEnabled']=bShowRevision
            }

            var activeWindow = wpsApp.ActiveWindow;
            if (activeWindow) {
                var v = activeWindow.View;
                if (v) {
                    v.ShowRevisionsAndComments = bShowRevision; //如果为True，则 WPS 显示使用“修订”功能对文档所作的修订和批注
                    v.RevisionsBalloonShowConnectingLines = bShowRevision; //如果为 True，则 WPS 显示从文本到修订和批注气球之间的连接线
                    wpsApp.CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Disable"); //去掉修改痕迹信息框中的接受修订和拒绝修订勾叉，使其不可用
                }

                if (bShowRevision) {
                    activeWindow.ActivePane.View.RevisionsMode = 2; //2为不支持气球显示。
                }
            }

             propAddStorage(doc.DocID, "ribbonExt", JSON.stringify(ribbonExt));
            // propAdd(prop, openFlag, MsoDocProperties.msoPropertyTypeBoolean, bOpenRevision);
            // propAdd(prop, showFlag, MsoDocProperties.msoPropertyTypeBoolean, bShowRevision);

            propAddStorage(doc.DocID,openFlag,bOpenRevision)
            propAddStorage(doc.DocID,showFlag,bShowRevision)
            if(params) OnShowRevisionClicked(params)
        } else {
            alert("请先打开文档")
        }
    }
    wps.UpdateRibbon("btnOpenRevision");
    wps.UpdateRibbon("btnShowRevision");
}

/**
 * 打开/关闭显示痕迹
 * params参数结构
 * params:{
 *   bShowRevision:'',true:打开/false:关闭
 *   'fileName':'',获取文档接口
 * }
 * @param {*} params
 */
function OnShowRevisionClicked(params) {
    //alert("2")
    var fileName = params == null ? null : params.fileName;
    var bShowRevision;

    var wpsApp = wps.WpsApplication();
    if (wpsApp) {
        var activeDoc = wpsApp.ActiveDocument;
        activeDoc = activeDoc == null ? openFile(fileName) : activeDoc;
        if (activeDoc) {
            // var prop = activeDoc.CustomDocumentProperties;
            //判断是否开启修订
            var oflag = propGetFromStorage(activeDoc.DocID,openFlag);
            //if (oflag && oflag.Value) {
                if (params) {
                    //web页面调用
                    bShowRevision = params.bShowRevision;
                } else {
                    //wps内调用
                    var sflag = propGetFromStorage(activeDoc.DocID,showFlag);
                    if (sflag && sflag.Value) {
                        bShowRevision = false;
                    } else {
                        bShowRevision = true;
                    }
                }

                var activeWindow = wpsApp.ActiveWindow;
                if (activeWindow) {
                    var v = activeWindow.View;
                    if (v) {
                        v.ShowRevisionsAndComments = bShowRevision; //如果为True，则 WPS 显示使用“修订”功能对文档所作的修订和批注
                        v.RevisionsBalloonShowConnectingLines = bShowRevision; //如果为 True，则 WPS 显示从文本到修订和批注气球之间的连接线
                        wpsApp.CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Disable"); //去掉修改痕迹信息框中的接受修订和拒绝修订勾叉，使其不可用
                    }

                    if (bShowRevision) {
                        activeWindow.ActivePane.View.RevisionsMode = 2; //2为不支持气球显示。
                    }
                    // propAdd(prop, showFlag, MsoDocProperties.msoPropertyTypeBoolean, bShowRevision);
                    propAddStorage(activeDoc.DocID,showFlag,bShowRevision)
                }
            //} else {
                //alert("请先开启修订模式")
            //}
        } else {
            alert("请先打开文档")
        }
    }
    wps.UpdateRibbon("btnShowRevision");

}

//接收所有修订
function OnAcceptAllRevisions() {
    var doc = wps.WpsApplication().ActiveDocument;
    if (doc.Revisions.Count >= 1) {
        doc.AcceptAllRevisions();
    }
}

//拒绝所有修订
function OnRejectAllRevisions() {
    var doc = wps.WpsApplication().ActiveDocument;
    if (doc.Revisions.Count >= 1) {
        doc.RejectAllRevisions();
    }
}

function OnGetOpenRevisionLabel() {
    var doc = wps.WpsApplication().ActiveDocument;
    if (doc) {
        // var prop = doc.CustomDocumentProperties;
        var flag = propGetFromStorage(doc.DocID,openFlag);
        if (flag && flag.Value) {
            return "关闭修订";
        } else {
            return "打开修订"
        }
    }
}

function OnGetShowRevisionLabel() {
    var doc = wps.WpsApplication().ActiveDocument;
    if (doc) {
        // var prop = doc.CustomDocumentProperties;
        var flag = propGetFromStorage(doc.DocID,showFlag);
        if (flag && flag.Value) {
            return "关闭显示痕迹";
        } else {
            return "打开显示痕迹"
        }
    }
}

//接受当前审阅人的修订，包括修订痕迹和批注
function OnAcceptRevisions() {
    var doc = wps.WpsApplication().ActiveDocument;
    var isOA;
    if(doc){
         isOA = propGetFromStorage(doc.DocID,"isOA");
         if (isOA&&isOA.Value==="true") {
            var userName = wps.WpsApplication().UserName;
            var revisions = doc.Revisions;
            if (revisions) {
                var count = revisions.Count;
                for (var i = count; i > 0; i--) {
                    var revision = revisions.Item(i);
                    if (revision && revision.Author == userName) {
                        revision.Accept();
                    }
                }
                var comments=doc.Comments;
                if(comments){
                    var count=comments.Count;
                    for (var index = count; index >0; index--) {
                        var comment=comments.Item(index);
                        if (comment && comment.Author == userName) {
                            comment.Delete();
                        }
                    }
                }
            }
        } else {
            alert("非OA文档，无法进行该功能操作！");
        }
    }else{
        alert("WPS当前没有可操作文档！")
    }
}

//拒绝当前审阅人的修订,包括修订痕迹和批注
function OnRejectRevisions() {
    var doc = wps.WpsApplication().ActiveDocument;
    if(doc){
        isOA = propGetFromStorage(doc.DocID,"isOA");
        if (isOA&&isOA.Value==="true") {
            var userName = wps.WpsApplication().UserName;
            if (doc) {
                var revisions = doc.Revisions;
                if (revisions) {
                    var count = revisions.Count;
                    for (var i = count; i > 0; i--) {
                        var revision = revisions.Item(i);
                        if (revision && revision.Author == userName) {
                            revision.Reject();
                        }
                    }
                }
                var comments=doc.Comments;
                if(comments){
                    var count=comments.Count;
                    for (var index = count; index >0; index--) {
                        var comment=comments.Item(index);
                        if (comment && comment.Author == userName) {
                            comment.Delete();
                        }
                    }
                }
            }
       }else{
            alert("非OA文档，无法进行该功能操作！")
       }
   }else{
    alert("WPS当前没有可操作文档！")
   }
}
