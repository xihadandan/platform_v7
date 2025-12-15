function OnCreateTaskPane(){
    var docId = doc.DocID;
    var currrentOpenedTPs = localStorage.getItem('openedTaskpaneIds') ? JSON.parse(localStorage.getItem('openedTaskpaneIds')) : [];
    if (currrentOpenedTPs.indexOf(docId) === -1) {
        currrentOpenedTPs.push(docId);
        localStorage.setItem('openedTaskpaneIds', JSON.stringify(currrentOpenedTPs));
    }

    var taskPaneID = localStorage.getItem("g_taskPaneID");
    if (!taskPaneID) {
        // var src = getHtmlURL("demo/index.html");
        var mrTP = wps.CreateTaskPane("http://dev.wpseco.cn/oaassist_3.1/demo/index.html", '外部页面');
        mrTP.DockPosition = MsoCTPDockPosition.msoCTPDockPositionRight;
        mrTP.Visible = true;
        mrTP.Width = 400;
        localStorage.setItem("g_taskPaneID", mrTP.ID);
        
        return true;
    } else {
        var mrTP = wps.GetTaskPane(taskPaneID);
        mrTP.Visible = true;
    }
}