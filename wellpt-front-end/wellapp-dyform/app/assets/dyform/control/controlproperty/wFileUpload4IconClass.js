var WFileUpload4IconClass = function () {
  this.allowUpload = true; //允许上传
  this.allowDownload = true; //允许下载
  this.allowDelete = true; //允许删除
  this.allowPreview = true; //允许删除
  this.mutiselect = true; //是否多选
  this.toJSON = toJSON;
};

WFileUpload4IconClass.prototype = new MainFormFieldClass();
