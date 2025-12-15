export const rowBtnSimple = [
  { "btnType": "1", "btnShowType": "edit", "id": "onClickRename", "code": "onClickRename", "buttonName": "重命名", "defaultFlag": true, "style": {}, "customEvent": {} },
  { "btnType": "1", "btnShowType": "edit", "id": "onClickDelFile", "code": "onClickDelFile", "buttonName": "删除", "defaultFlag": true, "style": {}, "customEvent": {} },
  { "btnType": "1", "btnShowType": "edit", "id": "onClickMoveUp", "code": "onClickMoveUp", "buttonName": "上移", "defaultFlag": true, "style": {}, "customEvent": {} },
  { "btnType": "1", "btnShowType": "edit", "id": "onClickMoveDown", "code": "onClickMoveDown", "buttonName": "下移", "defaultFlag": true, "style": {}, "customEvent": {} }
]

// 高级视图
export const headerBtnAdvanced = [
  { btnType: '1', btnShowType: 'edit', id: 'onAdvancedAddFile', code: 'onAdvancedAddFile', buttonName: '添加附件', defaultFlag: true, style: { icon: undefined }, customEvent: {} },
  { btnType: '1', btnShowType: 'edit', id: 'onPasteFile', code: 'onPasteFile', buttonName: '粘贴附件', defaultFlag: true, style: { icon: undefined }, customEvent: {} },
  { btnType: '1', btnShowType: 'edit', id: 'onAdvancedBatchDel', code: 'onAdvancedBatchDel', buttonName: '批量删除', defaultFlag: true, style: { icon: undefined }, customEvent: {} },
  { btnType: '1', btnShowType: 'show', id: 'onAdvancedBatchDownload', code: 'onAdvancedBatchDownload', buttonName: '批量下载', defaultFlag: true, style: { icon: undefined }, customEvent: {} },
]

export const rowBtnAdvanced = [{ "btnType": "1", "btnShowType": "edit", "id": "onClickEdit", "code": "onClickEdit", "buttonName": "编辑", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "edit", "id": "onClickReplace", "code": "onClickReplace", "buttonName": "替换", "defaultFlag": false, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "edit", "id": "onClickRename", "code": "onClickRename", "buttonName": "重命名", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "edit", "id": "onClickDelFile", "code": "onClickDelFile", "buttonName": "删除", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "show", "id": "onClickPreviewFile", "code": "onClickPreviewFile", "buttonName": "预览", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "show", "id": "onClickDownloadFile", "code": "onClickDownloadFile", "buttonName": "下载", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "show", "id": "onClickSaveAs", "code": "onClickSaveAs", "buttonName": "另存为", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "show", "id": "onClickCopyFilename", "code": "onClickCopyFilename", "buttonName": "复制名称", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "show", "id": "onClickLookUp", "code": "onClickLookUp", "buttonName": "查阅", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "edit", "id": "onClickSeal", "code": "onClickSeal", "buttonName": "盖章", "defaultFlag": false, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "show", "id": "onClickShowHistory", "code": "onClickShowHistory", "buttonName": "历史记录", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "edit", "id": "onClickMoveUp", "code": "onClickMoveUp", "buttonName": "上移", "defaultFlag": true, "style": {}, "customEvent": {} }, { "btnType": "1", "btnShowType": "edit", "id": "onClickMoveDown", "code": "onClickMoveDown", "buttonName": "下移", "defaultFlag": true, "style": {}, "customEvent": {} }]

export const advancedFileListTypeOptions = [
  {
    label: '列表视图',
    value: 'listView'
  },
  {
    label: '表格视图',
    value: 'tableView'
  },
  {
    label: '图标视图',
    value: 'iconView'
  }
];


export const UPLOAD_STATUS = { UPLOADING: 'uploading', DONE: 'done', ERROR: 'error', REMOVED: 'removed' };

export const fileList = [
  {
    "uid": "vc-upload-1695692330682-4",
    "name": "福建省统一身份认证概要设计.docx",
    "status": "done",
    "hovered": false,
    "showProgress": false,
    "dbFile": {
      "attach": null,
      "uuid": null,
      "recVer": null,
      "creator": "U0000000059",
      "createTime": 1695692608692,
      "modifier": null,
      "modifyTime": null,
      "userId": "U0000000059",
      "userName": "系统管理员",
      "departmentId": "D0000000115",
      "departmentName": "人事部",
      "businessTypeId": null,
      "businessTypeName": null,
      "moduleName": null,
      "nodeName": null,
      "folderID": null,
      "filename": "福建省统一身份认证概要设计.docx",
      "fileID": "97266784712785920",
      "purpose": null,
      "contentType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      "fileSize": 202099,
      "uploadTime": 1695692608692,
      "signUploadFile": false,
      "digestValue": null,
      "digestAlgorithm": null,
      "certificate": null,
      "signatureValue": null,
      "origUuid": null,
      "createTimeStr": "2023-09-26 09:43"
    },
    "formatSize": "197.36KB",
    "buttonControl": {
      "canPreview": false
    },
    "icon": "file-word",
    "url": "/proxy-repository/repository/file/mongo/download?fileID=97266784712785920"
  },
  {
    "uid": "vc-upload-1695692330682-6",
    "name": "平台手机uni-app开发手册-0403.docx",
    "percent": 100,
    "status": "error",
    "hovered": false,
    "showProgress": false,
    "dbFile": {
      "attach": null,
      "uuid": null,
      "recVer": null,
      "creator": "U0000000059",
      "createTime": 1695692645813,
      "modifier": null,
      "modifyTime": null,
      "userId": "U0000000059",
      "userName": "系统管理员",
      "departmentId": "D0000000115",
      "departmentName": "人事部",
      "businessTypeId": null,
      "businessTypeName": null,
      "moduleName": null,
      "nodeName": null,
      "folderID": null,
      "filename": "平台手机uni-app开发手册-0403.docx",
      "fileID": "97266939558100992",
      "purpose": null,
      "contentType": "docx",
      "fileSize": 7174684,
      "uploadTime": 1695692645813,
      "signUploadFile": false,
      "digestValue": null,
      "digestAlgorithm": null,
      "certificate": null,
      "signatureValue": null,
      "origUuid": null,
      "createTimeStr": "2023-09-26 09:44"
    },
    "formatSize": "6.84MB",
    "buttonControl": {
      "canPreview": false
    },
    "icon": "file-word",
    "url": "/proxy-repository/repository/file/mongo/download?fileID=97266939558100992"
  },
  {
    "uid": "vc-upload-1695692330682-611",
    "name": "平台手机uni-app开发手册-0403.docx",
    "percent": 50,
    "status": "uploading",
    "hovered": false,
    "showProgress": true,
    "dbFile": {
      "attach": null,
      "uuid": null,
      "recVer": null,
      "creator": "U0000000059",
      "createTime": 1695692645813,
      "modifier": null,
      "modifyTime": null,
      "userId": "U0000000059",
      "userName": "系统管理员",
      "departmentId": "D0000000115",
      "departmentName": "人事部",
      "businessTypeId": null,
      "businessTypeName": null,
      "moduleName": null,
      "nodeName": null,
      "folderID": null,
      "filename": "平台手机uni-app开发手册-0403.docx",
      "fileID": "97266939558100992",
      "purpose": null,
      "contentType": "docx",
      "fileSize": 7174684,
      "uploadTime": 1695692645813,
      "signUploadFile": false,
      "digestValue": null,
      "digestAlgorithm": null,
      "certificate": null,
      "signatureValue": null,
      "origUuid": null,
      "createTimeStr": "2023-09-26 09:44"
    },
    "formatSize": "6.84MB",
    "buttonControl": {
      "canPreview": false
    },
    "icon": "file-word",
    "url": "/proxy-repository/repository/file/mongo/download?fileID=97266939558100992"
  }
]
