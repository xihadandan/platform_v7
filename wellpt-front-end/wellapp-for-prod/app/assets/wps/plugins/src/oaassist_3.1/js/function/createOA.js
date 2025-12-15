function insertOAData(treeData) {
    for (var i = 0; i < treeData.length; i++) {
        var data = treeData[i];

        //判断当前节点层级等级
        var searchCode = data.searchCode;
        var arr = searchCode.split("|");

        //根据层级等级不同设置字体属性并插入
        //setAttribute(arr.length,data);

        if (undefined != data.children && data.children.length > 0) {
            insertOAData(data.children);
        } else {
            return;
        }
    }

    //alert("end");
}

function setAttribute(length, data) {
    var doc = wps.WpsApplication().ActiveDocument;
    var range = doc.Content;
    range.InsertParagraphBefore();
    range.InsertBefore(data.name);
    range = doc.Paragraphs.Last.Range;
    switch (length) {
        case 1:
            range.Style = "标题 1";
            range.Font.Name = "宋体";
            range.Font.Bold = true;
            range.ParagraphFormat.Alignment = WdAlignmentTabRelative.wdMargin;
            range.Font.Size = 22;
            break;
        case 2:
            range.Style = "标题 2";
            range.Font.Name = "宋体";
            range.Font.Bold = true;
            range.ParagraphFormat.Alignment = WdAlignmentTabRelative.wdIndent;
            range.Font.Size = 18;
            break;
        case 3:
            range.Style = "标题 3";
            range.Font.Name = "宋体";
            range.Font.Bold = true;
            range.ParagraphFormat.Alignment = WdAlignmentTabRelative.wdIndent;
            range.Font.Size = 16;
            break;
        case 4:
            range.Style = "标题 4";
            range.Font.Name = "宋体";
            range.Font.Bold = true;
            range.ParagraphFormat.Alignment = WdAlignmentTabRelative.wdIndent;
            range.Font.Size = 15;
            break;
        case 5:
            range.Style = "标题 5";
            range.Font.Name = "宋体";
            range.Font.Bold = true;
            range.ParagraphFormat.Alignment = WdAlignmentTabRelative.wdIndent;
            range.Font.Size = 14;
            break;
    }
}

function insertNodes(node) {
    //alert(node);
    var data = JSON.parse(node);
    // alert(data.name);
    var range = wps.WpsApplication().ActiveDocument.Content;
    range.InsertParagraphBefore();
    range.InsertBefore(data.name);
}


function createOA(treeData, docDataParam) {
    //alert(treeData);
    //alert(docIds);
    //弹出右侧流浪器框,并传入所需参数(合成后结构,参考文档)
    var url = GetUrlPath();

    if (url.length != 0) {
        url = url.concat("/leftItem.html");
    } else {
        url = url.concat("./leftItem.html");
    }

    //传递test
    var doc = wps.WpsApplication().ActiveDocument;

    var treeData2 = propGetFromStorage(doc.DocID,"treeData");
    if (treeData2) treeData2.Delete();
    var docDataParam2 = propGetFromStorage(doc.DocID,"docDataParam");
    if (docDataParam2) docDataParam2.Delete();
    var prop1 = prop.Add("treeData", treeData);
    var prop2 = prop.Add("docDataParam", docDataParam);

    //TODO 将合成后的文旦写入wps中
    //insertOAData(JSON.parse(treeData));

    /*var treeData = propGetFromStorage(doc.DocID,"treeData");
     var docDataParam = propGetFromStorage(doc.DocID,"docDataParam");


    //var prop2 = propGetFromStorage(doc.DocID,"abc");
    //alert(prop2.Value);
    //alert(url);
    var tp = wps.CreateTaskPane(url);
    tp.Visible = true;
}



function OnCreateOA(html, title) {
    var url = GetUrlPath();
    if (url.length != 0) {
        url = url.concat("/" + html);
    } else {
        url = url.concat("./" + html);
    }

    wps.ShowDialog(url, title, 800, 582);
    // var ts = wps.CreateTaskPane("http://localhost:8080/wps-oa/");
    // ts.Visible = true
}
