function AddDoc(info) {
    var docs = wps.WpsApplication().Documents;
    var myDate = currentTime();
    var username = wps.WpsApplication().UserName;
    var filename = username + '_' + myDate;

    docs.Add();
    // // wps.ApiEvent.AddApiEventListener("DocumentBeforeSave", OnDocumentBeforeSave);
    var doc = wps.WpsApplication().ActiveDocument;

    //doc.SaveAs2('新建');
    doc.SaveAs2(filename);
    // alert(filename);

}