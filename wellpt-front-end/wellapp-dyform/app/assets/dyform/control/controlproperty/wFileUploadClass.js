var WFileUploadClass = function () {
  this.allowUpload = true; //允许上传
  this.allowDownload = true; //允许下载
  this.allowDelete = true; //允许删除
  this.mutiselect = true; //是否多选
  this.toJSON = toJSON;
  this.allowFileNameRepeat = true; //是否允许文件名重复
  this.secDevBtnIdStr = null;
  this.fileSourceIdStr = null;
  this.isShowFileSourceIcon = undefined; //是否显示附件来源图标
  this.isShowFileFormatIcon = undefined; //是否显示附件格式图标
  this.saveFileName2Field = false; //是否保存文件名称 add by zhangyh 20170411
};

WFileUploadClass.prototype = new MainFormFieldClass();
