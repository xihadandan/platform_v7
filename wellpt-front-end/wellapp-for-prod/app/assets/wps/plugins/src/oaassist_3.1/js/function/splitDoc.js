function OnsplitDoc() {

    var url = GetUrlPath();
    if (url.length != 0) {
        url = url.concat("/splitRules.html");
    } else {
        url = url.concat("./splitRules.html");
    }
    wps.ShowDialog(url, "自定义拆解规则", 500, 400, false);
    //弹出一个对话框，在对话框中显示url中指定的html文件的内容，url为html文件的路径参数

}

function splitDoc(params) {
    alert(JSON.stringify(params));
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    var questionNumber;
    var question;
    var testoption;
    var answer;
    var rightAnswer;

    if (params) {
        questionNumber = params.questionNumber;
        question = params.question;
        testoption = params.testoption;
        answer = params.answer;
        rightAnswer = params.rightAnswer;
    }

    var paraCount = doc.Paragraphs.Count;
    var questionNumbers = [];
    var questions = [];
    var testoptions = [];;
    var answers = [];
    var rightAnswers = [];
    var oneQuestion = {};
    var allQuestion = [];

    var curoption = "";
    var optionindex = 0;
    var curquestionNumber = 0;
    var beforequestionIndex = 0;
    var distance = 0;
    for (var i = 1; i <= paraCount; i++) {
        var paragraph = doc.Paragraphs.Item(i);
        var parID = i;

        paragraph.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        var filecontent = s.Text;
        var text = filecontent.trim("g");
        if (text == "") { //若为空白段落，则继续遍历下一段
            if (beforequestionIndex != 0) {
                distance += 1;
            }
            continue;
        } else {
            var num = parseInt(questionNumber);

            if (num > 0) {

                // if (num <= questionNumbersTemp.length) {

                // }

            }
            //获取题号
            var questionNumbersTemp = getQuestionNumber(paragraph);

            if (questionNumbersTemp) {
                curquestionNumber = questionNumbersTemp;
                questionNumbers.push(questionNumbersTemp);

                // if (distance == 0) {
                oneQuestion["questionNo"] = questionNumbersTemp;
                // } else if ((i - beforequestionIndex) == distance) {
                // oneQuestion["questionNo"] = questionNumbersTemp;
                // }

                beforequestionIndex = i;
                distance = 0;
                // }

                // if (question) {
                //获取题目
                var questionTemp = getQuestion(paragraph, curquestionNumber);
                if (questionTemp.length > 0) {
                    questions.push(questionTemp);

                    oneQuestion["question"] = questionTemp;
                }
                // }

                continue;
            }
            // if (testoption) {
            //获取选项
            var testoptionTemp = getTestoption(paragraph);
            if (testoptionTemp.length > 0) {
                var tempoptionindex = i;
                distance += 1;
                if (optionindex != (tempoptionindex - 1)) {
                    if (curoption.length > 0) {
                        testoptions.push(curoption);
                        // distance += 1;
                        if ((i - beforequestionIndex) == distance) {
                            oneQuestion["options"] = curoption;
                        }
                    }
                    optionindex = tempoptionindex;
                    curoption = testoptionTemp;

                } else {
                    curoption += testoptionTemp;

                }

                continue;
            }
            // }
            // if (answer) {
            //获取答案
            var answerTemp = getAnswer(paragraph);
            if (answerTemp.length > 0) {
                answers.push(answerTemp);
                distance += 1;
                if ((i - beforequestionIndex) == distance) {
                    oneQuestion["answer"] = answerTemp;
                }

                continue;

            }
            // }

            // if (rightAnswer) {
            //获取正确答案
            var rightAnswerTemp = getRightAnswer(paragraph);
            if (rightAnswerTemp.length > 0) {
                rightAnswers.push(rightAnswerTemp);
                distance += 1;
                if ((i - beforequestionIndex) == distance) {
                    oneQuestion["rightAnswer"] = rightAnswerTemp;
                }

                // continue;
            }

            // }
            allQuestion.push(oneQuestion);

        }
    }


    alert("题号：" + JSON.stringify(questionNumbers));
    alert("题目：" + JSON.stringify(questions));
    alert("选项：" + JSON.stringify(testoptions));
    alert("答案：" + JSON.stringify(answers));
    alert("正确答案：" + JSON.stringify(rightAnswers));
    alert("试题：" + JSON.stringify(allQuestion));

}


//从指定段落获取题号
function getQuestionNumber(paragraph) {

    var questionNumber = "";
    var words = paragraph.Range.Words;
    var wordsCount = words.Count;
    if (wordsCount > 0) {
        for (var i = 1; i <= wordsCount; i++) {
            var num = words.Item(i).Text.replace(/\r+$/, "");
            num = num.trim("g");
            if (checkNum(num)) {
                questionNumber = num;
                break;
            } else {
                continue;
            }
        }
    }

    return questionNumber;

}

//从指定段落获取题目内容
function getQuestion(paragraph, questionNumbersTemp) {

    var questions = "";
    var paraText = paragraph.Range.Text.replace(/\r+$/, "");
    var text = paraText.trim("g");
    if (text != "" && text.startWith(questionNumbersTemp)) {
        questions = text;
    }

    return questions;
}

//从指定段落获取题目选项
function getTestoption(paragraph) {

    var testoption = "";
    var paraText = paragraph.Range.Text.replace(/\r+$/, "");
    var text = paraText.trim("g");
    if (text != "") {
        var reg = /^[A-Za-z]/;
        if (reg.test(text)) { //判断是否以字母开头
            // testoption.push(text);
            testoption = text;
        }

    }

    return testoption;
}

//从指定段落获取答案
function getAnswer(paragraph) {

    var answer = "";
    var paraText = paragraph.Range.Text.replace(/\r+$/, "");
    var text = paraText.trim("g");
    if (text != "" && text.startWith("答案")) {
        // answer.push(text);
        answer = text;
    }

    return answer;
}

//从指定段落获取正确答案
function getRightAnswer(paragraph) {

    var rightanswer = "";
    var paraText = paragraph.Range.Text.replace(/\r+$/, "");
    var text = paraText.trim("g");
    if (text != "" && text.startWith("正确答案")) {
        // rightanswer.push(text);
        rightanswer = text;
    }

    return rightanswer;
}
/**
 * 拆解公文(其中包含带下划线的文字，加粗的文字，带颜色的文字，以及拆分关键字)
 * params:{
 * fileName:获取文件的接口
 * keyWords：待拆分的关键字
 * }
 */
function splitDocDemo(params) {
    var wpsApp = wps.WpsApplication();
    var doc;
    var keyWords;

    if (params) {
        var filename = params.fileName;
        var path = wps.OAAssist.DownloadFile(filename);
        doc = wpsApp.Documents.Open(path, false, false);
    } else {
        doc = wpsApp.ActiveDocument;
    }

    // var prop = doc.CustomDocumentProperties;
    for (var key in params) {
        if (key === "keyWords") {
            keyWords = params[key];
        } else {
             propAddStorage(doc.DocID, key, params[key]);
        }
    }
    if (keyWords == null) {
        keyWords = ["法院", "调解书", "审判员"];
    }

    var paraCount = doc.Paragraphs.Count;

    var underlineWords = [];
    var underlineWordsJson = {};
    var boldWords = [];
    var boldWordsJson = {};
    var hasColorWords = [];
    var hasColorWordsJson = {};
    var contents = [];
    var contentsJson = {};

    for (var i = 1; i <= paraCount; i++) {
        var paragraph = doc.Paragraphs.Item(i);
        var parID = "第" + i + "段";
        paragraph.Range.Select();
        var s = wpsApp.ActiveWindow.Selection
        var filecontent = s.Text;
        var text = filecontent.trim("g");
        if (text == "") { //若为空白段落，则继续遍历下一段
            continue;
        } else {
            //获取带下划线的文字
            var underlineWordsTemp = getUndelineWords(paragraph);
            if (underlineWordsTemp.length > 0) {
                underlineWords.push(underlineWordsTemp)
                underlineWordsJson[parID] = underlineWordsTemp
            }
            //获取加粗的文字
            var boldWordsTemp = getBoldWords(paragraph);
            if (boldWordsTemp.length > 0) {
                boldWords.push(boldWordsTemp)
                boldWordsJson[parID] = boldWordsTemp;
            }
            //获取有特殊颜色或背景色的文字
            var hasColorWordsTemp = getColorWords(paragraph);
            if (hasColorWordsTemp.length > 0) {
                hasColorWords.push(hasColorWordsTemp)
                hasColorWordsJson[parID] = hasColorWordsTemp;
            }
            //根据关键字获取相关文字内容
            var contentsTemp = getContentsByKeyWords(paragraph, keyWords);
            if (contentsTemp.length > 0) {
                contents.push(contentsTemp);
                contentsJson[parID] = contentsTemp;
            }


        }
    }
    alert("下划线文字：" + JSON.stringify(underlineWordsJson));
    alert("加粗文字：" + JSON.stringify(boldWordsJson));
    alert("带颜色文字：" + JSON.stringify(hasColorWordsJson));
    alert("关键文字内容：" + JSON.stringify(contentsJson));

}

//从指定段落获取加粗的文字
function getBoldWords(paragraph) {
    var boldWords = [];
    var sentencesCount = paragraph.Range.Sentences.Count;
    if (sentencesCount > 0) {
        for (var i = 1; i <= sentencesCount; i++) {
            var boldVal = paragraph.Range.Sentences.Item(i).Bold;
            if (boldVal) { //true为加粗，false为非加粗
                boldWords.push(paragraph.Range.Sentences.Item(i).Text.replace(/\r+$/, ""));
            }
        }
    }

    return boldWords;

}
//从指定段落获取带下划线的文字
function getUndelineWords(paragraph) {

    var undelineWords = [];
    var sentences = paragraph.Range.Sentences;
    var sentencesCount = sentences.Count;
    if (sentencesCount > 0) {
        for (var i = 1; i <= sentencesCount; i++) {
            var undelineVal = sentences.Item(i).Underline;
            if (undelineVal != WdUnderline.wdUnderlineNone) {

                undelineWords.push(sentences.Item(i).Text.replace(/\r+$/, ""));
            }
        }
    }

    return undelineWords;

}

//从指定段落获取特殊颜色或背景色的文字
function getColorWords(paragraph) {
    var colorWords = [];
    var sentencesCount = paragraph.Range.Sentences.Count;
    if (sentencesCount > 0) {
        for (var i = 1; i <= sentencesCount; i++) {
            var bgColorVal = paragraph.Range.Sentences.Item(i).HighlightColorIndex;
            var fontColorVal = paragraph.Range.Sentences.Item(i).Font.ColorIndex;
            if (bgColorVal != WdColorIndex.wdNoHighlight || (fontColorVal != WdColorIndex.wdAuto && fontColorVal != WdColorIndex.wdBlack &&
                    fontColorVal != WdColorIndex.wdNoHighlight && fontColorVal != WdColorIndex.wdByAuthor)) { //字体有颜色
                colorWords.push(paragraph.Range.Sentences.Item(i).Text.replace(/\r+$/, ""));
            }
        }
    }

    return colorWords;

}

//根据关键字获取相应文本内容
function getContentsByKeyWords(paragraph, params) {
    var contents = [];
    var wpsApp = wps.WpsApplication();
    paragraph.Range.Select();
    var s = wpsApp.ActiveWindow.Selection
    var tmpContent = s.Text;
    var text = tmpContent.trim("g");

    for (var i = 0; i < params.length; i++) {
        if (text == "") {
            break;
        } else {
            if (text.endWith(params[i])) {
                contents.push(text);
                break;
            }
        }
    }
    return contents;

}
