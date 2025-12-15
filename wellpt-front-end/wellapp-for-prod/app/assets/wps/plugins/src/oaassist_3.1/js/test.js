
function TestWPSAPI()
{
    TestApplication();
}

function TestApplication()
{
    var wps = wps.WpsApplication();
    if (!wps.hasOwnProperty("Caption"))
        alert("access failed: WpsApplication().Caption");
    if (!wps.hasOwnProperty("Name"))
        alert("access failed: WpsApplication().Name");
    if (!wps.hasOwnProperty("UserName"))
        alert("access failed: WpsApplication().UserName");
    if (!wps.hasOwnProperty("Visible"))
        alert("access failed: WpsApplication().Visible");
    wps.Activate();
    if (!wps.hasOwnProperty("Documents"))
        alert("access failed: WpsApplication().Documents");
    TestDocuments(wps.Documents);
    if (!wps.hasOwnProperty("Windows"))
        alert("access failed: WpsApplication().Windows");
    TestWindows(wps.Windows);
    if (!wps.hasOwnProperty("ActiveDocument"))
        alert("access failed: WpsApplication().ActiveDocument");
    TestDocument(wps.ActiveDocument);
    if (!wps.hasOwnProperty("ActiveWindow"))
        alert("access failed: WpsApplication().ActiveWindow");
    TestWindow(wps.ActiveWindow);
    wps.Quit();
    if (!wps.hasOwnProperty("Dialogs"))
        alert("access failed: WpsApplication().Dialogs");
    wps.FileDialog();
    if (!wps.hasOwnProperty("WordBasic"))
        alert("access failed: WpsApplication().WordBasic");
    if (!wps.hasOwnProperty("Selection"))
        alert("access failed: WpsApplication().Selection");
    TestWindow(wps.NewWindow());
}

var bTestDocuments = false;
function TestDocuments(docs)
{
    if (!docs.hasOwnProperty("Count"))
        alert("failed: Documents.Count");

    if (bTestDocuments)
        return;
    bTestDocuments = true;

    //TestDocument(docs.Open(""));
    TestDocument(docs.Add());
    TestDocument(docs.Item(1));
}

var bTestDocument = false;
function TestDocument(doc)
{
    if (!doc.hasOwnProperty("ActiveWindow"))
        alert("access failed: Document.ActiveWindow");
    if (!doc.hasOwnProperty("Bookmarks"))
        alert("access failed: Document.Bookmarks");
    doc.AcceptAllRevisions();
    doc.Activate();
    //doc.Close();
    if (!doc.hasOwnProperty("Content"))
        alert("access failed: Document.Content");
    if (!doc.hasOwnProperty("DocID"))
        alert("access failed: Document.DocID");
    doc.ExportAsFixedFormat("t22", 0);
    if (!doc.hasOwnProperty("FullName"))
        alert("access failed: Document.FullName");
    if (!doc.hasOwnProperty("Name"))
        alert("access failed: Document.Name");
    doc.PrintOut();
    doc.PrintPreview();
    doc.Protect(0);
    if (!doc.hasOwnProperty("ReadOnlyRecommended"))
        alert("access failed: Document.ReadOnlyRecommended");
    doc.RejectAllRevisions();
    doc.Save();
    doc.SaveAs();
    doc.SaveAs2();
    doc.SaveCopyAs();
    if (!doc.hasOwnProperty("Saved"))
        alert("access failed: Document.Saved");
    if (!doc.hasOwnProperty("SaveFormat"))
        alert("access failed: Document.SaveFormat");
    if (!doc.hasOwnProperty("TrackRevisions"))
        alert("access failed: Document.TrackRevisions");
    if (!doc.hasOwnProperty("Type"))
        alert("access failed: Document.Type");

    if (bTestDocument)
        return;
    bTestDocument = true;

    TestWindows(doc.Windows);
    TestRevisions(doc.Revisions);
    TestFormFields(doc.FormFields);
    TestTables(doc.Tables);
    TestContent(doc.Content)
    TestCustomDocumentProperties(doc.CustomDocumentProperties);
}

var bTestWindows = false;
function TestWindows(wdws)
{
    if (!wdws.hasOwnProperty("Count"))
        alert("access failed: Windows.Count");
    if (bTestWindows)
        return;
    bTestWindows = true;
    TestWindow(wdws.Item(1));
}

var bTestWindow = false;
function TestWindow(wdw)
{
    wdw.Activate();
    wdw.Document.Activate();
    if (!wdw.hasOwnProperty("Caption"))
        alert("access failed: Windows.Caption");

    if (bTestWindow)
        return;
    bTestWindow = true;

    TestView(wdw.View);
    TestView(wdw.ActivePane);
    TestSelection(wdw.Selection);
}

function TestView(view)
{

}

function TestPane(pane)
{

}

function TestRevisions(revisions)
{
    revisions.Item(1);
}

function TestFormFields(formFields)
{
    formFields.Select();
}

function TestTables(tbls)
{
    tbls.Item(1);
}

function TestContent(ctt)
{
    ctt.InsertParagraphBefore();
}

function TestCustomDocumentProperties(prs)
{
    prs.Item(1);
}

function TestSelection(sel)
{
    
}