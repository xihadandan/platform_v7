"use strict";

import storage from "../storage";

export default {
  /**
   * 文件下载
   */
  downloadFile(file) {
    file.url = `/proxy-repository/repository/file/mongo/download?fileID=${file.fileID}`;
    // #ifdef H5
    this.downloadFileByWeb(file);
    // #endif
    // #ifndef H5
    this.downloadFileOther(file);
    // #endif
  },

  // 通过web方式下载
  /*
  import { saveAs } from 'file-saver';
  //filePath 这里的地址是 uni.downloadFile 中的返回值里的地址
  //finName 下载文件名
  saveAs(filePath,finName)
*/
  downloadFileByWeb(file) {
    var _this = this;
    var hiddenIFrameID = "hiddenDownloader" + Date.now();
    var iframe = document.createElement("iframe");
    iframe.id = hiddenIFrameID;
    iframe.style.display = "none";
    document.body.appendChild(iframe);
    iframe.src = storage.fillAccessResourceUrl(file.url);
  },
  // 下载文件
  downloadFileOther(file, url) {
    uni.showLoading({
      title: `文件${file.fileName || file.name}下载中...`,
    });

    uni.downloadFile({
      url: url || `${storage.fillAccessResourceUrl(file.url)}`,
      success: (res) => {
        console.log("downloadFile success, res is", res);
        uni.hideLoading();
        uni.showToast({ title: "下载成功！" + res.tempFilePath });
        this.saveFileToLocal(res.tempFilePath);
      },
      fail: (err) => {
        console.log("downloadFile fail, err is:", err);
      },
    });
  },
  // 保存文件到本地
  saveFileToLocal(tempFilePath) {
    uni.saveFile({
      tempFilePath,
      success: function (res) {
        console.log(res);
      },
    });
  },
  getFileIcon(filename) {
    if (/\.doc[x]?$/i.test(filename)) {
      //word文档
      return "ant-iconfont file-word";
    }

    if (/\.xls[x]?$|\.csv$/i.test(filename)) {
      //excel文档
      return "ant-iconfont file-excel";
    }

    if (/\.ppt[x]?$/i.test(filename)) {
      //ppt文档
      return "ant-iconfont file-ppt";
    }

    if (/\.pdf$/i.test(filename)) {
      //pdf 文档
      return "ant-iconfont file-pdf";
    }

    if (/\.txt$/i.test(filename)) {
      //文本文档
      return "ant-iconfont file-text";
    }

    if (/\.bpm$|\.png$|\.gif$|\.jpg$|\.jpeg/i.test(filename)) {
      //图片
      return "ant-iconfont file-image";
    }
    if (/\.zip$/i.test(filename)) {
      //压缩文件
      return "ant-iconfont file-zip";
    }
    return "ant-iconfont file";
  },
  getFileSize(size, unit) {
    if (unit == "K" || unit == "KB") {
      return size * 1024;
    }
    if (unit == "M" || unit == "MB") {
      return size * 1024 * 1024;
    }
    if (unit == "G" || unit == "GB") {
      return size * 1024 * 1024 * 1024;
    }
    return size;
  },
};
