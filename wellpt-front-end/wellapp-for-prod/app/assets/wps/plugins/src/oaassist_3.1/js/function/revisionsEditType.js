/**
 * 修订类型
 * params参数结构
 * params:{
 *   
 * }
 * @param {*} params 
 */
function revisionsEditType(params) {
    var fileName = params == null ? null : params.fileName;
    var wpsApp = wps.WpsApplication();
    var activeDoc = wpsApp.ActiveDocument;
        activeDoc = activeDoc == null ? openFile(fileName) : activeDoc;
    var editType = params.revisionsEditType;
    if(editType && editType =="1"){
        activeDoc.TrackRevisions =true;//修订的方式编辑文档。
    }else{
        activeDoc.TrackRevisions =false;//非修订的方式编辑文档。
    }

}