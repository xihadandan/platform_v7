// var btnSaveflag = true; //WPS保存按钮
// var btnSaveAsflag = true; //WPS另存为按钮
// var btnFilePrintflag = true; //WPS打印按钮

// var storage = wps.PluginStorage;
// /**
//  * WPS保存按钮enabled属性设置
//  *  * params参数结构
//  * params:{
//  *   btnFileSaveFlag:,WPS保存按钮是否可用标记，false为不可用，true为可用
//  */
// function OnFileSaveEnabled(params) {

//     if (params) {
//         // alert(JSON.stringify(params));
//         if (params.btnFileSaveFlag) {
//             btnSaveflag = params.btnFileSaveFlag;
//         }
//     }
//     storage.setItem("btnSaveflag", btnSaveflag);
//     return btnSaveflag;
// }
// /**
//  * WPS另存为按钮enabled属性设置
//  *  * params参数结构
//  * params:{
//  *   btnFileSaveAsFlag:'',WPS另存为按钮是否可用标记，false为不可用，true为可用
//  */
// function OnFileSaveAsMenuEnabled(params) {
//     if (params) {

//         if (params.btnFileSaveAsFlag) {
//             btnSaveAsflag = params.btnFileSaveAsFlag;
//         }
//     }

//     storage.setItem("btnFileSaveAsFlag", btnSaveAsflag);
//     return btnSaveAsflag;
// }

// /**
//  * WPS打印按钮enabled属性设置
//  *  * params参数结构
//  * params:{
//  *   btnFilePrintflag:'',WPS打印按钮是否可用标记，false为不可用，true为可用
//  */
// function OnFilePrintMenuEnabled(params) {
//     if (params) {

//         if (params.btnFilePrintflag) {
//             btnFilePrintflag = params.btnFilePrintflag;
//         }
//     }
//     storage.setItem("btnFilePrintflag", btnFilePrintflag);
//     return btnFilePrintflag;
// }