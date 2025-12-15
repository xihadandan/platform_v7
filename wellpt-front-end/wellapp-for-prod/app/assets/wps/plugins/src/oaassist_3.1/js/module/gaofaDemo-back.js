/**
 * 获取高发demo右侧窗口
 */
function gaofaDemo() {
    var url = getHtmlURL("gaofaDemo.html");
    var tp = wps.CreateTaskPane(url);
    tp.Visible = true;
    wps.tp = tp;
}

var flag = true;

/**
 * 控制插件按钮
 */
function controlButton() {
    flag = !flag;

    var _ribbon = getRibbonExt(flag);
    var doc = wps.WpsApplication().ActiveDocument;

     propAddStorage(doc.DocID, "ribbonExt", JSON.stringify(_ribbon));
    wps.UpdateRibbon();
}

//点击文档校对按钮后在右侧嵌入web页面（存在则不创建，不存在就出创建）
function OnDocCheck() {
    var doc = wps.WpsApplication().ActiveDocument;
    if (doc) {
        // var prop = doc.CustomDocumentProperties;
        // 先清除doc自带的
        var currentTaskPane = propGetFromStorage(doc.DocID,"currentTaskPaneId");
        if (currentTaskPane) {
            currentTaskPane.Delete();
        }
        var storage = wps.PluginStorage;
        var activeTaskPaneId = storage.getItem("activeTaskPaneId");
        var tp;

        if (propGetFromStorage(doc.DocID,"currentTaskPaneId")) {
            alert("当前活动窗口：" + propGetFromStorage(doc.DocID,"currentTaskPaneId").Value);
            tp = wps.GetTaskPane(propGetFromStorage(doc.DocID,"currentTaskPaneId").Value); //根据taskpane的ID来取到之前创建的taskpane
            tp.Visible = true;

        } else {
            if (activeTaskPaneId) { //针对打开多个文档的场合
                tp = wps.GetTaskPane(activeTaskPaneId);
                // tp.Visible = (!tp.Visible);
                tp.Visible = true;
                // alert("已存在的活动窗口：" + tp.ID);
                // doc.CustomDocumentProperties.Add("currentTaskPaneId", false, 4, tp.ID);
            } else {
                var url = GetUrlPath();
                if (url.length != 0) {
                    url = url.concat("/gaofaDocCheckDemo.html");
                } else {
                    url = url.concat("./gaofaDocCheckDemo.html");
                }

                tp = wps.CreateTaskPane(url); //创建右侧的浏览器
                //设置 Width 属性小于最小宽度的值，该应用程序会自动重新分配 Width 属性设置为最小宽度。
                // tp.Width = 100;
                // tp.Height = 400;
                tp.Visible = true;
                // alert("正在创建的活动窗口：" + tp.ID);
                storage.setItem("activeTaskPaneId", tp.ID);
                propAddStorage(doc.DocID,"currentTaskPaneId",tp.ID);
                // doc.CustomDocumentProperties.Add("currentTaskPaneId", false, 4, tp.ID);
            }

        }
    }

}

//错词定位
function findErrorWord(item) {

    var s = wps.WpsApplication().ActiveWindow.Selection;
    var f = s.Find;
    var isFind = true;
    if (f) {
        while (isFind) {
            f.Wrap = 0; //到达搜索范围的开始或结尾时，停止执行查找操作。
            f.Text = item; //设置要查找的文本
            f.Forward = true; //往前搜索
            f.MatchCase = false; //相当于“查找和替换”对话框中的“区分大小写”复选框，false为不区分大小写
            f.MatchByte = true; //搜索过程中不区分全角和半角字母或字符
            f.MatchWildcards = false; //查找的文字不包含特殊搜索操作符
            f.MatchWholeWord = false; //相当于“查找和替换”对话框中的“全字匹配”复选框,false为非全字匹配
            f.MatchFuzzy = false; //执行特定搜索
            f.Replacement.Text = ""; //将文本替换为""
            f.Replacement.Style = "";
            f.Replacement.Highlight = 9999999;
            f.Style = ""; //返回或设置指定对象的样式
            f.Highlight = 9999999; //指定文本的突出显示颜色

            f.Execute(); //运行指定的查找操作,如果查找成功，则返回 True
            if (f.Found == true) {
                alert("找到错词")

                isFind = true;
                s.Font.ColorIndex = WdColorIndex.wdRed; //将所选文本的格式设置为红色。
                // s.Text = "你好"; //替换查找后的文字
            } else {
                isFind = false;
            }
        }
    }
}
//修正错词()
function replaceErrorWord(item, item2) {

    var s = wps.WpsApplication().ActiveWindow.Selection;
    var f = s.Find;
    var isFind = true;
    item = item + '';
    item2 = item2 + '';
    if (f) {
        while (isFind) {
            f.Wrap = 0; //到达搜索范围的开始或结尾时，停止执行查找操作。
            f.Text = item; //设置要查找的文本
            f.Forward = true; //往前搜索
            f.MatchCase = false; //相当于“查找和替换”对话框中的“区分大小写”复选框，false为不区分大小写
            f.MatchByte = true; //搜索过程中不区分全角和半角字母或字符
            f.MatchWildcards = false; //查找的文字不包含特殊搜索操作符
            f.MatchWholeWord = false; //相当于“查找和替换”对话框中的“全字匹配”复选框,false为非全字匹配
            f.MatchFuzzy = false; //执行特定搜索
            f.Replacement.Text = ""; //将文本替换为""
            f.Replacement.Style = "";
            f.Replacement.Highlight = 9999999;
            f.Style = ""; //返回或设置指定对象的样式
            f.Highlight = 9999999; //指定文本的突出显示颜色

            f.Execute(); //运行指定的查找操作,如果查找成功，则返回 True
            if (f.Found == true) {
                alert("修改错词")
                isFind = true;
                s.Font.ColorIndex = WdColorIndex.wdRed; //将所选文本的格式设置为红色。
                s.Text = item2; //替换查找后的文字
            } else {
                isFind = false;
            }
        }
    }
}

//一键排版
function OnComposeTypeClicked(data, judgeMembers, haschiefFlag, pic) {
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;

    var shapes = doc.Sections.Item(1).Footers.Item(WdHeaderFooterIndex.wdHeaderFooterPrimary).Range.InlineShapes;
    if (shapes.Count > 0) {
        shapes.Item(1).Select();
        var s = wpsApp.ActiveWindow.Selection
        s.InlineShapes = pic; //替换模板中页脚处的图片
    }

    var bookmarks = doc.Bookmarks;
    if (bookmarks.Exists("courtName")) {
        var bookmark = bookmarks.Item("courtName"); //法院名称
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection

        bookmark.Range.Text = data.courtName;
        bookmark.Range.Style = "正文";
        bookmark.Range.Font.Name = "方正小标宋简体";
        bookmark.Range.Font.Size = 30;
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphCenter; //居中对齐
    }

    if (bookmarks.Exists("verdictName")) { //文书名称
        var bookmark = bookmarks.Item("verdictName");
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        bookmark.Range.Text = data.verdictName;
        bookmark.Range.Style = "正文";
        bookmark.Range.Font.Name = "方正小标宋简体";
        bookmark.Range.Font.Size = 36;
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphCenter //居中对齐

    }

    var blankParaIndex = 0;
    var paraCount = doc.Paragraphs.Count;
    for (var i = 1; i <= paraCount; i++) {
        var paragraph = doc.Paragraphs.Item(i);
        paragraph.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        var tmpContent = s.Text;
        var text = tmpContent.trim('g');
        if (text.endWith("人民法院")) {
            blankParaIndex = i + 2; //文书名称下面的空行段落
            break;
        }

    }
    //文书名称下面的空行
    var paragraph3 = doc.Paragraphs.Item(blankParaIndex);
    paragraph3.Range.Select();
    var s = wpsApp.ActiveWindow.Selection
    var tmpContent = s.Text;
    var text = tmpContent.trim('g');
    if (text == "") {
        s.Style = "";
        s.Font.Name = "仿宋";
        s.Font.Size = 16;
    }


    if (bookmarks.Exists("caseNo")) { //案号
        var bookmark = bookmarks.Item("caseNo");
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        bookmark.Range.Text = data.caseNo;
        bookmark.Range.Style = "";
        bookmark.Range.Font.Name = "仿宋";
        bookmark.Range.Font.Size = 16;
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphRight //右对齐
        s.ParagraphFormat.CharacterUnitRightIndent = 2; //右缩进二个汉字空格至行末端

    }
    if (bookmarks.Exists("fileContent")) { //正文
        var bookmark = bookmarks.Item("fileContent");
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        bookmark.Range.Text = data.fileContent;
        bookmark.Range.Style = "";
        bookmark.Range.Font.Name = "仿宋";
        bookmark.Range.Font.Size = 16;
        bookmark.Range.Font.SizeBi = 16;
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphJustify //完全两端对齐
        s.ParagraphFormat.CharacterUnitFirstLineIndent = 2 //首行缩进2字符
    }
    if (bookmarks.Exists("chiefJudge")) { //审判长
        var bookmark = bookmarks.Item("chiefJudge");
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        bookmark.Range.Text = data.chiefJudge;
        bookmark.Range.Font.Name = "仿宋";
        bookmark.Range.Font.Size = 16;
        bookmark.Range.Font.Spacing = 11.338492; //字符间距设置为11.338492 磅相当于2个汉字。
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphRight //右对齐

    }
    if (bookmarks.Exists("signDate")) { //落款日期
        var bookmark = bookmarks.Item("signDate");
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        s.InsertAfter("    ");
        bookmark.Range.Text = data.signDate;
        bookmark.Range.Font.Name = "仿宋";
        bookmark.Range.Font.Size = 16;
        bookmark.Range.Font.Spacing = 0; //字符间距设置为0
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphRight //右对齐
        s.ParagraphFormat.CharacterUnitRightIndent = 6.2; //右缩进4个汉字空格至行末端
    }

    if (bookmarks.Exists("secretary")) { //书记员
        var bookmark = bookmarks.Item("secretary");
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        s.InsertAfter(" ");
        bookmark.Range.Text = data.secretary;
        bookmark.Range.Font.Spacing = 11.338492; //字符间距设置为 11.338492 磅，相当于2个汉字。
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphRight //右对齐
            // s.ParagraphFormat.CharacterUnitRightIndent = 1; //右缩进二个汉字空格至行末端

    }
    if (bookmarks.Exists("chiefJudgeName")) { //审判长姓名
        var bookmark = bookmarks.Item("chiefJudgeName");
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        s.InsertAfter(" "); //姓名之后空二个汉字空格至行末端
        bookmark.Range.Text = data.chiefJudgeName;
        bookmark.Range.Font.Name = "仿宋";
        bookmark.Range.Font.Size = 16;
        bookmark.Range.Font.Spacing = 3.133849; //字符间距设置为0磅。
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphRight //右对齐
        s.ParagraphFormat.CharacterUnitRightIndent = 6.2; //右缩进4个汉字空格至行末端

        setJudgeMember(bookmarks, judgeMembers, haschiefFlag)

    } else {
        // 无审判长的场合
        setJudgeMember(bookmarks, judgeMembers, haschiefFlag);

    }

    if (bookmarks.Exists("secretaryName")) { //书记员姓名
        var bookmark = bookmarks.Item("secretaryName");
        bookmark.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        bookmark.Range.Text = data.secretaryName;
        bookmark.Range.Font.Name = "仿宋";
        bookmark.Range.Font.Size = 16;
        bookmark.Range.Font.Spacing = 3.133849; //字符间距设置为0磅。
        s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphRight //右对齐
        if (haschiefFlag == 1) {
            s.ParagraphFormat.CharacterUnitRightIndent = 6.2; //右缩进二个汉字空格至行末端
        }

    }
}
//拆解法院文书
function splitCourtFile(info) {
    var wpsApp = wps.WpsApplication();
    var doc;

    if (info) {
        var fileName = info.fileName;
        var path = wps.OAAssist.DownloadFile(fileName);
        doc = wps.Documents.Open(path, false, false);
    } else {
        doc = wpsApp.ActiveDocument;
    }

    var paraCount = doc.Paragraphs.Count;
    var data = {};
    var judgeMembers = {};

    //正文到文章末尾内容排版
    var contentStart = 0; //正文开始位置
    var contentEnd; //标记正文结尾的位置
    var indexSecretary; //标记书记员的位置
    var memberJudgeCount = 0; //审判员的个数
    var haschiefFlag = 0; //1代表有审判长，0代表无审判长
    for (var i = 1; i <= paraCount; i++) {
        var paragraph = doc.Paragraphs.Item(i);
        paragraph.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        var tmpContent = s.Text;
        var text = tmpContent.trim('g');

        var paraId = i;
        var tempParaId = -1;
        if (text == "") {
            continue;
        } else {
            // 拆分：法院名称
            if (text.endWith("人民法院")) {
                data["courtName"] = text;
                continue;
            }

            // 拆分：文书名称
            if (text.endWith("判决书") || text.endWith("裁定书") || text.endWith("调解书")) {
                data["verdictName"] = text;
                continue;
            }

            // 拆分：文书名称(当前段落25个字以内， 以"号"结尾 )
            if (text.length < 25 && text.endWith("号")) {
                data["caseNo"] = text;
                contentStart = i + 1;

                continue;
            }
            // 拆分：落款日期(当前段落13个字以内，包含年月日，并且以"日"结尾 )
            if (text.length < 13 && text.indexOf("年") != -1 && text.indexOf("月") != -1 && text.endWith("日")) {
                data["signDate"] = text;

                continue;
            }
            // 拆分：落款名
            if ((text.indexOf("审判长") != -1) || (text.indexOf("审判员") != -1) || (text.indexOf("书记员") != -1)) {

                if ((text.indexOf("审判长") != -1)) { //当有审判长的场合，获取审判长的位置

                    contentEnd = i;
                    haschiefFlag = 1;
                    var result = text.split("审判长");

                    data["chiefJudge"] = "审判长";
                    var chiefJudgeName;
                    //若审判长与审判员同处一段，需要再对result[1]进行拆解，以获取审判长姓名
                    if (result[1].indexOf("审判员")) {
                        chiefJudgeName = result[1].split("审判员")[0];
                    } else {
                        chiefJudgeName = result[1];
                    }
                    data["chiefJudgeName"] = chiefJudgeName;

                    if (text.indexOf("审判员") != -1) {
                        var result = text.split("审判员");
                        var len = result.length
                        tempParaId = i;
                        for (var k = 1; k < len; k++) {
                            memberJudgeCount++;

                            judgeMembers["memberJudge" + memberJudgeCount] = "审判员";
                            judgeMembers["judgeMemberName" + memberJudgeCount] = result[k];

                        }
                    }

                } else if ((text.indexOf("审判长") == -1) && (text.indexOf("审判员") != -1) && memberJudgeCount == 0) { //当无审判长，有几个审判员的场合，获取第一次出现审判员的位置
                    if (haschiefFlag == 0) {
                        contentEnd = i;
                    }

                    var result = text.split("审判员");
                    var len = result.length
                    tempParaId = i;
                    for (var k = 1; k < len; k++) {
                        memberJudgeCount++;

                        judgeMembers["memberJudge" + memberJudgeCount] = "审判员";
                        judgeMembers["judgeMemberName" + memberJudgeCount] = result[k];

                    }

                } else if ((text.indexOf("审判员") != -1) && memberJudgeCount > 0 && tempParaId != paraId) {
                    memberJudgeCount++;
                    var result = text.split("审判员");

                    judgeMembers["memberJudge" + memberJudgeCount] = "审判员";
                    judgeMembers["judgeMemberName" + memberJudgeCount] = result[1];

                }

                if ((text.indexOf("书记员") != -1)) {
                    indexSecretary = i;
                    var result = text.split("书记员");

                    data["secretary"] = "书记员";
                    data["secretaryName"] = result[1] + "";
                    break;
                }
            }


        }
    }

    data["judgeMembers"] = judgeMembers;

    //获取页脚中的图片
    var shapes = doc.Sections.Item(1).Footers.Item(WdHeaderFooterIndex.wdHeaderFooterPrimary).Range.InlineShapes;
    var pic;
    if (shapes.Count > 0) {
        pic = shapes.Item(1);
    }

    //正文排版
    var paragraph = doc.Paragraphs.Item(contentStart);
    var contentParaLength = contentEnd - contentStart - 1;
    paragraph.Range.Select();
    var s = wpsApp.ActiveWindow.Selection
        //将选定内容向下扩展几段,选中正文
    s.MoveDown(WdUnits.wdParagraph, contentParaLength, WdMovementType.wdExtend);
    var filecontent = s.Text;
    //删除正文开头和结尾处的所有换行符
    filecontent = filecontent.replace(/\r+$/, "");
    filecontent = filecontent.replace(/^\r+/, "");

    data["fileContent"] = filecontent;

    var selection = wpsApp.ActiveWindow.Selection; //选取全文
    var templateFile;
    if (haschiefFlag == 0) { //无审判长
        // templateFile = "http://dev.wpseco.cn/wps-oa/" + "template/getFileData/" + 35
        templateFile = "http://dev.wpseco.cn/wps-oa/" + "template/getFileData/" + 46
    } else { //有审判长(2个审判员)
        templateFile = "http://dev.wpseco.cn/wps-oa/" + "template/getFileData/" + 41
            // templateFile = "http://dev.wpseco.cn/wps-oa/" + "template/getFileData/" + 43
    }

    selection.WholeStory();
    selection.Delete();
    selection.InsertFile(templateFile);
    OnComposeTypeClicked(data, judgeMembers, haschiefFlag, pic);
}
//给审判员排版
function setJudgeMember(bookmarks, judgeMembers, haschiefFlag) {
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    for (var key in judgeMembers) {
        // alert("key:" + key + "值：" + JSON.stringify(judgeMembers[key]));

        if (key.startWith("memberJudge")) {
            if (bookmarks.Exists(key)) { //审判员
                // alert("审判员key:" + key);
                var bookmark = bookmarks.Item(key);
                bookmark.Range.Select();
                var s = wpsApp.ActiveWindow.Selection
                bookmark.Range.Style = "";
                bookmark.Range.Text = judgeMembers[key];
                bookmark.Range.Font.Name = "仿宋";
                bookmark.Range.Font.Size = 16;
                bookmark.Range.Font.Spacing = 11.338492; //字符间距设置为 11.338492 磅相当于2个汉字。
                s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphRight //右对齐

            }
        }
        if (key.startWith("judgeMemberName")) {
            if (bookmarks.Exists(key)) { //审判员姓名
                var bookmark = bookmarks.Item(key);
                bookmark.Range.Select();
                var s = wpsApp.ActiveWindow.Selection
                bookmark.Range.Style = "";
                bookmark.Range.Text = judgeMembers[key];
                bookmark.Range.Font.Name = "仿宋";
                bookmark.Range.Font.Size = 16;
                bookmark.Range.Font.Spacing = 3.133849; //字符间距设置为一个汉字。
                s.ParagraphFormat.Alignment = WdParagraphAlignment.wdAlignParagraphRight //右对齐
                if (haschiefFlag == 1) {
                    s.ParagraphFormat.CharacterUnitRightIndent = 6.2; //右缩进二个汉字空格至行末端
                }

            }
        }
    }
}
