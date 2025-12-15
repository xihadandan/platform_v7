function setDocumentRevisions(showTraces,retainedTraces,acceptAllRevise){  //设置痕迹、保留痕迹和是否接受修改（清稿）
   
}


function insertDocumentMarks(param){  //
    var doc = wps.WpsApplication().ActiveDocument;
    var array = param.dataFromWeb;
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
                findBookmark(doc,data.docfield);
            }else if(data.type == "insertText"){ 
                insertValue(doc,data.value);//光标插入值
            }else if(data.type == "insertFile"){     //这个需要异步处理
                var bookmarkName = data.docfield  ;
                if(!bookmarkName){
                    bookmarkName = "_defaultbookmark" ;
                }
                if(data.url){
                    insertFile(doc,bookmarkName,data.url);
                }
               
            }else if(data.type == "runMacro"){
                runMacro(doc,data.value);
            }else if(data.type == "image"){
                insertImage(doc,data.value,data["docfield"]);   //这个需要异步处理
            
            }else if(data.type == "subtable"){  //处理子表
                insertSubTable(doc,data.docfield,data.value);
            }
        }
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

function insertText(doc,name,value){ //在指定书签位置插入内容
    if(doc && value !=""){
        try{
             var bkmkObj = doc.Bookmarks.Item(name);
             
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
             alert("insertText err!");
         }finally {}
    } 
}

function insertValue(doc,value){   //在光标位置插入书签
    if(doc){
        var sel = doc.Application.Selection; 
        sel.Text = value ;
        sel.MoveRight(1,1) ; 
    }
}

function findBookmark(doc,docfield){  //定位书签位置
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

function insertFile(doc,name,url){ //书签位置插入url文件内容
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    
    var sel = doc.Application.Selection;
    try{
        //debugger;
        if(name != "_defaultbookmark"){
            //alert(1);
            //doc.Bookmarks(name).Select();
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

function insertFile(doc,url){ //光标位置插入url文件内容
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

function insertImage(doc,field,url){ //书签位置，插入图片
    var me = this ;
    var meOcxObj = me.ocxObj ;
    
    var sel = doc.Application.Selection;
    if(field){
            if("印章"==field){
                sel.GoTo(-1, 0, 0, field);
                meOcxObj.AddPicFromLocal("C:\\\\DSOA\\\\temp\\\\Seal.bmp",false,true,100,100) ;
            }else{
                    
                    sel.InlineShapes.AddPicture(imageUrl,false,true,doc.Bookmarks.Item(field).Range);
            }
    }else{
        meOcxObj.AddPicFromURL(imageUrl,false,0,0,1,100,0) ;
    }
    
    //var sel = doc.Application.Selection;
    //sel.MoveRight(1,1) ;
}

function insertImage(doc,url){ //光标位置，插入图片
}

function insertSubTable(doc,name,array){  //指定位置，插入子表数据
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

