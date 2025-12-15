/**
 * 文档打开方式
 * params参数结构
 * params:{
 *   protectType: '', 文档保护模式(-1：不启用保护模式，0：只允许对现有内容进行修订，1：只允许添加批注，2：只允许修改窗体域，3：只读)
 *   password: '', 文档保护密码
 * }
 * @param {*} params
 */
function openType(params) {
    if(!params) return;
    var doc = wps.WpsApplication().ActiveDocument;
    // var prop = doc.CustomDocumentProperties;
    var protectType = params.protectType;
    var password = params.password;
    if (doc && params && password && protectType) {
        if([0,1,2,3].indexOf(protectType) !== -1){
            // 保护文档如果之前有被保护，再次保护会出问题，需要先解除保护
            doc.Unprotect();
            if(protectType === 3 && params.readonly === 2) {
                // 禁止保存
                 propAddStorage(doc.DocID, "cannot_save", "true");
            }else{
                doc.Protect(protectType, false, password, false);
            }
        }
    }
}

function setOpenType(params){
    openType(params)
}
