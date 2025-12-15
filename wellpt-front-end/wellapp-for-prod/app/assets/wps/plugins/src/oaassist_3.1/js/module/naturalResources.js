// 获取扩展名
function getExtName(fullName) {
    var index = fullName.lastIndexOf(".");
    if (index == -1) {
        return "";
    } else {
        return fullName.substring(index, fullName.length).toLowerCase();
    }
}
// 是否word文档
function isWord(fullName){
    
    if(!fullName){
        return false ;
    }
    
    var editableDocType =".doc,.docx";
    if(fullName){
        fullName = fullName.toLowerCase();
    }
    var ext = this.getExtName(fullName);
    return editableDocType.indexOf(ext)>-1 ;
}
// 打开文档
function openUrlFile(url){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    var fileName = me.getFileName();
    if(fileName && me.isWord(fileName)){
        me.ocxObj.OpenFromURL(url,null,"Word.Document") ;
        return ;		
    }
    me.ocxObj.OpenFromURL(url) ;
}

// 获取文档名称
function getFileName(){
    var fileName = "" ;
    var me = this ;
    var datas = me["datas"] ;
    if(datas && datas["attrs"] && datas["attrs"]["fileName"]){
        fileName = datas["attrs"]["fileName"] ;
    }
    return fileName ;
}


function setDocuemntAttrs(){  //废黜页面属性
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    
    
    if(me.datas && me.datas.attrs){ 
        var attrs = me.datas.attrs ;  //文档属性
        me.setDocumentRevisions(attrs["showTraces"],attrs["retainedTraces"],attrs["acceptAllRevise"]);
        for(var key in attrs){
            var value = attrs[key] ;
            
            if(!value){
                continue ;
            }
            if(key == "canSaveAs" && (value == "false" && "<%=isSaveAsUser%>"=="0" )){ //是否可以另存为
                //debugger;
                meOcxObj.FileSaveAs = false;
            
                // 只读不另存下关闭菜单栏
                meOcxObj.Menubar = DEBUG;
                meOcxObj.Toolbars = DEBUG;
            }else if(key == "readOnly" && value == "true"){
                me.setReadOnly(true);
                
                // 打印时关闭修订模式
                meOcxObj.FileSave = false;
                if (!ocx_config.funcType == 2) {
                    me.activeDocument.TrackRevisions = DEBUG;
                    me.activeDocument.PrintRevisions = DEBUG;
                }
            }else if(key == "title"){  //文档标题
                meOcxObj.Caption = value;
            }else if(key  == "username"){  //用户名
                meOcxObj.WebUserName = value;
            }
        }
        
        
        //if (ocx_config.funcType == 0 || ocx_config.funcType == 1) {
            //保留痕迹
        //}
    }
    
    var fileName = me.getFileName();
    if(fileName && fileName.indexOf(".pdf") > 0){
        meOcxObj.FileSaveAs = false;
    }
    
    meOcxObj.activate(true);
    meOcxObj.IsUseUTF8URL = true ;
    meOcxObj.IsUseUTF8Data = true ;
    meOcxObj.IsNoCopy = false;
}
function setReadOnly(readOnly){  //设置是否只读  true 只读
    var me = this ;
    var meOcxObj = me.ocxObj ;
    var i;
     try{
         var activeDocument = me.activeDocument ;
         if(activeDocument){
             if(meOcxObj.DocType == 1){ //word
                 //这个密码宁波这边必须要为空，否则印章控件无法使用，建议是否可以配置密码？
                 var protectionType = activeDocument.ProtectionType ;
                 if((protectionType != -1) && !readOnly){
                     meOcxObj.SetReadOnly(false,"");
                 }
                 
                 if((protectionType == -1) && readOnly){
                       meOcxObj.SetReadOnly(true,"",false,2);
                 }
              }else if(meOcxObj.DocType == 2){//excel
                   for(i = 1; i <= activeDocument.Sheets.Count; i++){
                         if(readOnly){
                             activeDocument.Sheets(i).Protect("", true, true, true);
                         }else{
                             activeDocument.Sheets(i).Unprotect("");
                         }
                   }
              }	 
         }
     }catch (err){
         me.showTips({title:'错误', message:err.description, isShow:-1});
     }finally {}
}
function setDocumentRevisions(showTraces,retainedTraces,acceptAllRevise){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    var activeDocument = me.activeDocument ;
    
    try{
        if(activeDocument){
            //显示痕迹
            activeDocument.ShowRevisions  = showTraces == "true";
            //保存痕迹
            activeDocument.TrackRevisions =  retainedTraces == "true" ;
            if(retainedTraces == "true"){
                try{
                    activeDocument.Protect(0,false,'password');
                } catch (err) {
                    me.showTips({title:'错误', message:err.description});
                } 
            }
            //清搞
            if(acceptAllRevise == "true"){
                activeDocument.AcceptAllRevisions();	
            }
        }	
    }catch(e){
        
    }
}
function setInitActiveDocumentSaved(){  //套红，起草，清稿默认就是可以保存
    var me = this ;
    var meOcxObj = me.ocxObj ;
    if(ocx_config.funcType == 1 || ocx_config.funcType == 5 || ocx_config.funcType == 3){
        me.activeDocument.Saved = false ;
    }else{
        me.activeDocument.Saved = true ;
    }
    
    //添加参数，来控制是否是套打之前方法
    if("1" == "<%=isPrintBefore%>"){
        office.beforePrint();
    }
    
}
function uploadFile(){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    /**
    if(ocx_config.funcType == 1 || ocx_config.funcType == 5 || ocx_config.funcType == 3){
        office.ocxObj.ActiveDocument.Saved = false ;
    }*/
    
    if(me.datas && me.datas["upLoadURL"]){
        me.saveUrlFile(me.datas["upLoadURL"],"");	
    }
}
function insertDocumentMarks(array){  //
    var doc = wps.WpsApplication().ActiveDocument;
    var len = array.length ;
    if(len > 0 && doc){
        var files = {} ;
        for(var i=0;i<len;i++){
            var data = array[i] ;
            if(data == null){
                continue;
            }
            
            if(data.type == "insertDocField"){
                if(data.docfield == ""){
                    insertValue(doc,data.value);
                }else{
                    insertText(doc,data.docfield,data.value);//书签	
                }
            }else if(data.type == "gotofield"){
                me.findBookmark(doc,data.docfield);
            }else if(data.type == "insertText"){ 
                me.insertValue(doc,data.value);//光标插入值
            }else if(data.type == "insertFile"){     //这个需要异步处理
                var bookmarkName = data.docfield  ;
                if(!bookmarkName){
                    bookmarkName = "_defaultbookmark" ;
                }
                if(data.url){
                    me.insertFile(doc,bookmarkName,data.url);
                }
                
                //如果是firefox情况下
                if(browser != "IE" && i <len-1) {
                        var newArray = array.slice(i+1,len);   //
                        if(newArray){
                            me.tmpBookmarks = newArray ;
                        }
                        //break ;
                        continue;
                }
            }else if(data.type == "runMacro"){
                me.runMacro(doc,data.value);
            }else if(data.type == "image"){
                me.insertImage(doc,data.value,data["docfield"]);   //这个需要异步处理
                //如果是firefox情况下
                if(browser != "IE" && i <len-1) {
                    var newArray = array.slice(i+1,len);
                        if(newArray){
                            me.tmpBookmarks = newArray ;
                        }
                        //break ;
                        continue;
                }
                
            }else if(data.type == "subtable"){  //处理子表
                me.insertSubTable(doc,data.docfield,data.value);
            }
        }
    }
}


function insertText(doc,name,value){
    var me = this ;
    if(doc && value !=""){
        try{
             var bkmkObj = null ;
             if (browser == "IE") {
                 bkmkObj = doc.BookMarks(name);
             }else{
                 bkmkObj = doc.BookMarks.Item(name);
             }
             
             if(bkmkObj){
                 var sel = doc.Application.Selection;
                 var saverange = bkmkObj.Range ;
                 saverange.Text = value;
                 doc.Bookmarks.Add(name, saverange);
                 try{
                     sel.GoTo(-1, 0, 0, name);	 
                 }catch(e){
                     doc.Bookmarks.Item(name).Range.Select();
                 }
                 sel.MoveRight(1,1) ;
             }
         }catch(err){
             me.showTips({title:'错误', message:err.description, isShow:-1});
         }finally {}
    } 
}
function findBookmark(doc,docfield){
    if(doc && doc.Bookmarks.Exists(docfield)){
        var sel = doc.Application.Selection;
        try{
            sel.GoTo(-1, 0, 0, docfield);	
        }catch(e){
            doc.Bookmarks.Item(docfield).Range.Select();
            try{
                sel.MoveRight(1,1) ;
            }catch(e){}
        }
    }
}
function insertValue(doc,value){
    if(doc){
        var sel = doc.Application.Selection; 
        sel.Text = value ;
        sel.MoveRight(1,1) ; 
    }
}
function insertFile(doc,name,url){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    
    var sel = doc.Application.Selection;
    try{
        //debugger;
        if(name != "_defaultbookmark"){
            //alert(1);
            //doc.BookMarks(name).Select();
            //sel.CopyFormat();
            sel.GoTo(-1, 0, 0, name);
            //doc.Bookmarks.Item(name).Range.Select()
            //sel.PasteFormat();
            
            
            //doc.Bookmarks.Item(name).Select();//name是书签名称
    
            /* var rg = doc.Application.Selection.Range;
            var Style = doc.Application.Selection.Style;//获取书签样式
            rg.InsertFile(url);
            rg.Style = Style; */
            
        }else{
            sel.MoveRight(1,1) ; 
        }
        meOcxObj.AddTemplateFromURL(url);
    }catch(e){}
    
    
    //doc.AcceptAllRevisions();
    if(browser == "IE"){
        doc.AcceptAllRevisions();
    }else{ //firefox异步调用 暂时这样 2016.12.8 taolb
        window.setTimeout(function(){
            doc.AcceptAllRevisions();
        },3000);
    }
}
function insertFiles(doc,files){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    var exist = false ;	
    var sel = doc.Application.Selection;
    for(key in files){
        try{
            var url = files[key] ;
            sel.GoTo(-1, 0, 0, key);
            meOcxObj.AddTemplateFromURL(url);	
        }catch(e){}
        exist = true ;
    }
    
    if(exist){
        doc.AcceptAllRevisions();
    }
}
function runMacro(doc,macro){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    if("fn_Enter" == macro){  //
        me.insertValue(doc,"\r");	
    }else if("oneSpace" == macro){
        me.insertValue(doc," ");	
    }else if("toRight" == macro){
        var sel = doc.Application.Selection;  
        sel.ParagraphFormat.Alignment = 2 ;
    }else if("toLeft" == macro){
        var sel = doc.Application.Selection;  
        sel.ParagraphFormat.Alignment = 0 ;
    }else if("ZwFontGB" == macro){   //正文字体
        me.runZwFontGB();
    }
}
function runZwFontGB(){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    var activeDocument = me.activeDocument;
    if(activeDocument.ProtectionType == -1){
        if(activeDocument.Bookmarks.Exists("正文开始") && activeDocument.Bookmarks.Exists("正文结束")){
            
            
            var sel = activeDocument.Range(activeDocument.Bookmarks.Item("正文开始").Range.Start,activeDocument.Bookmarks.Item("正文结束").Range.End) ;
            sel.ParagraphFormat.LineSpacingRule = 1 ;
            sel.Font.Name = "仿宋_GB2312" ;
            sel.Font.Size = 16 ;
        }
    }
}
function insertImage(doc,imageUrl,field){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    var sel = doc.Application.Selection;
    if(field){
            if("印章"==field){
                sel.GoTo(-1, 0, 0, field);
                meOcxObj.AddPicFromLocal("C:\\\\DSOA\\\\temp\\\\Seal.bmp",false,true,100,100) ;
            }else{
                    
                    sel.InlineShapes.AddPicture(imageUrl,false,true,doc.BookMarks.Item(field).Range);
            }
    }else{
        meOcxObj.AddPicFromURL(imageUrl,false,0,0,1,100,0) ;
    }
    
    //var sel = doc.Application.Selection;
    //sel.MoveRight(1,1) ;
    
}
function insertSubTable(doc,name,value){ //插入子表
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    if(name &&value){
        me.findBookmark(doc,name);  //定位到位置
        var array = value.split("^");
        var len = array.length ;
        for(var i=0;i<len;i++){
            var content = array[i] ;
            // 将子表字段里的\u005e转回 ^ ,unicode编码里^是\u005e,\需要转义 lmf 2017-4-26
            content = content.replace(/\\u005e/g, '^');
            var sel = doc.Application.Selection; 
            sel.Text = content ;
            if(i < len - 1){
                sel.MoveRight(12,1);	
            }else{
                sel.MoveRight(1,1) ; 
            }
        }
    }
    
}
function hideButton(){
    var me = this;
    var meOcxObj = me.ocxObj;
    meOcxObj.FileNew = false ; 
    meOcxObj.FileSaveAs = false;
    meOcxObj.FileSave = false ;
    meOcxObj.FilePrint = false ;
    meOcxObj.FilePrintPreview = false ;
    
}
function toggleBtnDisplay(boolVal) {
    var me = this;
    var meOcxObj = me.ocxObj;
    
    var activeDocument = me.activeDocument;
    
    if(meOcxObj && activeDocument != null && meOcxObj.StatusCode == 0){
        
        meOcxObj.IsShowToolMenu = boolVal;
        meOcxObj.FileNew = boolVal;
        meOcxObj.FileOpen = boolVal;
        meOcxObj.FileClose = boolVal;
        //meOcxObj.FileSaveAs = boolVal;
        meOcxObj.FileProperties = boolVal;
        
        if(me.ocxObj.doctype == 1 || me.ocxObj.doctype == 6) {
            if (meOcxObj.GetOfficeVer() >= 12) {
                try{
                    meOcxObj.SetRibbon("ReviewTrackChangesMenu", boolVal, boolVal);
                    meOcxObj.SetRibbon("ReviewAcceptChangeMenu", boolVal, boolVal);
                    meOcxObj.SetRibbon("ReviewRejectChangeMenu", boolVal, boolVal);	
                }catch(e){}
            } else {
                try{
                    activeDocument.CommandBars("Reviewing").Enabled = boolVal;
                    activeDocument.CommandBars("Track Changes").Enabled = boolVal;
                }catch(e){}
            } 
        }
    }
    
    
    
    if("<%=hideCommonMenu%>" == "1"){
        me.hideButton();
        me.setReadOnly(true);
    }
    
    
}
function setDocumentProperties(config) {
    var me = this;
    var meOcxObj = me.ocxObj;
    meOcxObj.TitleBar = config.showTitle;
    meOcxObj.IsShowFileErrorMsg = config.isShowError;
    meOcxObj.BorderStyle = 1;
    
    
    meOcxObj.AddDocTypePlugin(".pdf","PDF.NtkoDocument","4.0.0.1","officecontrol/ntkooledocall.cab",51,true);
    meOcxObj.AddDocTypePlugin(".tif","TIF.NtkoDocument","4.0.0.1","officecontrol/ntkooledocall.cab",52,true);
    
    
    
}
function initCustomMenu(items) {
    var me = this;
    var meOcxObj = me.ocxObj;
    
    me.menuItems = {
        mainMenu : [{
                title: 'OA功能菜单',
                index: '0',
                event : {},
                subMenu : [{
                        title: '显示痕迹',
                        index: '0',
                        id: 'sub_1',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    },{
                        title: '隐藏痕迹',
                        index: '1',
                        id: 'sub_2',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    },{
                        title: '套红之前',
                        index: '2',
                        id: 'sub_3',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    },{
                        title: '套红之后',
                        index: '3',
                        id: 'sub_4',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    }/*,{
                        title: '查看修改记录',
                        index: '4',
                        id: 'sub_5',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    },{
                        title: '所有审阅者',
                        index: '5',
                        id: 'sub_6',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    },{
                        title: '用户XX环节',
                        index: '6',
                        id: 'sub_7',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    },{
                        title: '用户XX环节',
                        index: '7',
                        id: 'sub_8',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    },{
                        title: '上一条修改',
                        index: '8',
                        id: 'sub_9',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    },{
                        title: '下一条修改',
                        index: '9',
                        id: 'sub_10',
                        isShow: true,
                        isEnable: true,
                        event : {},
                        subMenu : []
                    }*/
                ]
            }
        ]
    };

    if (me.menuItems.mainMenu && me.menuItems.mainMenu.length > 0) {
        var menus = me.menuItems.mainMenu;
        for (var i = 0; i < menus.length; i++) {
            var menu = menus[i];
            
            meOcxObj.AddCustomMenu2(menu.index, menu.title);
            for (var y = 0; y <menu.subMenu.length; y++) {
                var subMenu = menu.subMenu[y];
                
                var menuId = meOcxObj.AddCustomMenuItem2(menu.index, subMenu.index, -1, false, subMenu.title, false, subMenu.id);
            }
        }
    }
}
function showTips(tips) {
    var me = this;
    tips.isShow = tips.isShow | DEBUG;

    if(tips.isShow == 1)
        me.ocxObj.ShowTipMessage(tips.title, tips.message);
        
}
function movePrevRevision(){
     var me = this ;
     var meOcxObj = me.ocxObj ;
     var activeDocument = me.activeDocument ;
     var sel = activeDocument.Application.Selection;
     
     var revTemp = sel.PreviousRevision();
     if(revTemp){
        sel.MoveLeft(1,1);
     }
}
function moveNextRevision(){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    var activeDocument =  me.activeDocument ; 
    var sel = activeDocument.Application.Selection;
     
    var revTemp  = sel.NextRevision();
     if(revTemp){
         sel.MoveRight(1,1);
     }
}
function setRevisions(revision){    //显示还是隐藏痕迹
    //debugger;
    var me = this ;
    var meOcxObj = me.ocxObj ;
    var datas = me["datas"] ;
    if(datas && datas["attrs"] && datas["attrs"]["readOnly"] == "true"){
        meOcxObj.SetReadOnly(false,"");
         me.activeDocument.ShowRevisions = revision ;
        meOcxObj.SetReadOnly(true,"",false,2);
    }else{
         me.activeDocument.ShowRevisions = revision ;
    }
}
function beforePrint(){
    this.printOffice(-603914241);	
}
function afterPrint(){
    this.printOffice(255);
}
function printOffice(rgbInt){
    var me = this ;
    var meOcxObj = me.ocxObj ;
    var doc = me.activeDocument ;
    
    var docReadOnly = false ;
    var datas = me["datas"] ;
    if(datas && datas["attrs"] && datas["attrs"]["readOnly"] == "true"){
        docReadOnly = true ;
    }
    
    if(docReadOnly){
        meOcxObj.SetReadOnly(false,"");
    }
    
    var hideBT = doc.BookMarks("HideBT");						
    if (hideBT) {
        doc.Application.Selection.GoTo(-1, 0, 0, "HideBT");
        if (doc.Application.Selection.Start != doc.Application.Selection.End) {
            doc.Application.Selection.Font.Color = rgbInt ;   //-603914241  ; //显示白色
        }
        doc.Application.Selection.HomeKey(6,0);
    }
    
    var hideLine = doc.BookMarks("HideLine");
    if (hideLine) {
        doc.Application.Selection.GoTo(-1, 0, 0, "HideLine");
        if (doc.Application.Selection.Start != doc.Application.Selection.End) {
            doc.Application.Selection.Font.Color = rgbInt  ;				
        }
        doc.Application.Selection.HomeKey(6,0);
    }
    
    
    if(docReadOnly){
        meOcxObj.SetReadOnly(true,"",false,2);
    }
}