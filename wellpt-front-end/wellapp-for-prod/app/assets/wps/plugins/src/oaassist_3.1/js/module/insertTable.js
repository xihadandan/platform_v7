var data = [
    {
        "name":"zhangsan",
        "age":"18",
        "sex":"nan"
    },
    {
        "name":"lis",
        "age":"19",
        "sex":"nan"
    },
    {
        "name":"wangwu",
        "age":"20",
        "sex":"nan"
    },
    {
        "name":"zhaoliu",
        "age":"21",
        "sex":"nv"
    },
    {
        "name":"sunqi",
        "age":"22",
        "sex":"nv"
    },
];

function insertTable(){
    // 打开模板文件
    var doc = wps.WpsApplication().Documents.Open("C:\\Users\\DELL\\AppData\\Roaming\\kingsoft\\wps\\jsaddons\\oaassist_3.1\\js\\module\\insertTable.docx",false,false);

    // 获取表格对象
    var tables = doc.Tables;
    var table = tables.Item(1);
    // 第一行 为标题，数据从第二行开始填充
    var rowIndex = 2;
    // 遍历数据，填充
    for(var i = 0 ; i < data.length; i++){
        alert("1")
        var item = data[i];
        var colIndex = 1;
        for (var key in item) {
            var range = table.Cell(rowIndex++, colIndex++).Range;
            range.Text = item[key]
        }
        if(i < data.length-1){
            table.Rows.Add();
        }
       
    }
}  

function insertTableFormWeb(doc,bookmark,data){
    bookmark.Range.Select();
    var len = data.length ;
    for(var i=0;i<len;i++){
        var content = data[i] ;
        // 将子表字段里的\u005e转回 ^ ,unicode编码里^是\u005e,\需要转义 lmf 2017-4-26
        // content = content.replace(/\\u005e/g, '^');
        var sel = doc.Application.Selection; 
        sel.Text = content ;
        if(i < len - 1){
            sel.MoveRight(12,1);	
        }else{
            sel.MoveRight(1,1) ; 
        }
    }
}

// function findBookmark(doc,docfield){
//     if(doc && doc.Bookmarks.Exists(docfield)){
//         var sel = doc.Application.Selection;
//         try{
//             sel.GoTo(-1, 0, 0, docfield);	
//         }catch(e){
//             doc.Bookmarks.Item(docfield).Range.Select();
//             try{
//                 sel.MoveRight(1,1) ;
//             }catch(e){}
//         }
//     }
// }