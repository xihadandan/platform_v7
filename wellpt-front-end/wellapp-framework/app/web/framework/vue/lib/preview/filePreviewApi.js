import createClientCommonApi from '@pageAssembly/app/web/framework/vue/clientCommonApi';
import SparkMD5 from 'spark-md5';
import { deepClone } from '@framework/vue/utils/util';
const clientCommonApi = createClientCommonApi();

var pdfReg = new RegExp('(pdf)$', 'i');
var ofdReg = new RegExp('(ofd)$', 'i');
var wmvReg = new RegExp('(wmv)$', 'i');
// var txtReg = new RegExp('(txt)$', 'i');
// var tifReg = new RegExp('(tif|tiff)$', 'i');
var zipReg = new RegExp('(zip|rar|7z)$', 'i');
var imgReg = new RegExp('(gif|jpg|jpeg|png|bmp|tif|tiff)$', 'i');
var excelReg = new RegExp('(ods|xls|xlsb|xlsm|xlsx)$', 'i');
var wordReg = new RegExp('(doc|docm|docx|dot|dotm|dotx|odt)$', 'i');
var pptReg = new RegExp('(odp|pot|potm|potx|pps|ppsm|ppsx|ppt|pptm|pptx)$', 'i');

const openWindow = function (url, target) {
  if (typeof target === 'string' && target === '_self') {
    window.open(url, target);
    return
  }
  var iframe = target;
  if (typeof target === 'string') {
    iframe = document.getElementById(target);
  }
  if (target && target.tagName && 'IFRAME' === target.tagName.toUpperCase()) {
    target.src = url;
  } else {
    clientCommonApi.getSystemParamValue('pt.preview.open.mode', function (openMode) {
      if ('window' == openMode) {
        window.open(
          url,
          '_blank',
          ' left=0,top=0,width=' +
          (screen.availWidth - 20) +
          ',height=' +
          (screen.availHeight - 80) +
          ',scrollbars,resizable=yes,toolbar=no'
        );
      } else {
        window.open(url, '_blank');
      }
    });
  }
};

const FilePreviewAdapter = function () {
  //获取参数
  let _this = this;
  this.readyParams = [false, false, false];
  // 获取预览参数
  clientCommonApi.getSystemParamValue('app.filepreview.serve', function (serv) {
    _this.filePreviewType = 'null' === serv ? null : serv;
    _this.readyParams[0] = true;
  });
  clientCommonApi.getSystemParamValue('app.filepreview.type', function (type) {
    _this.filePreviewType = 'null' === type ? null : type;
    _this.readyParams[1] = true;
  });
  clientCommonApi.getSystemParamValue('document.preview.path', function (type) {
    _this.documentPreviewPath = 'null' === type ? null : type;
    _this.readyParams[2] = true;
  });
};
FilePreviewAdapter.prototype.preview = function (WOPISrc, options) {
  let _this = this;
  if (this.readyParams[0] && this.readyParams[1] && this.readyParams[2]) {
    options = options || {};
    if (typeof options.callback == 'function') {
      options.callback();
    }
    if ('msOfficeOnline' === this.filePreviewType) {
      $axios
        .get(WOPISrc, {
          params: Object.assign(
            {
              ignoreSha256: 'true'
            },
            options.wopiOptions
          )
        })
        .then(function (result) {
          console.log(result);
          // console.log(fileInfo);
          var url = filePreviewServer;
          var fileName = fileInfo.BaseFileName;
          if (wordReg.test(fileName)) {
            url += '/wv/wordviewerframe.aspx?WOPISrc=';
          } else if (excelReg.test(fileName)) {
            url += '/x/_layouts/xlviewerinternal.aspx?WOPISrc=';
          } else if (pptReg.test(fileName)) {
            url += '/p/PowerPointFrame.aspx?WOPISrc=';
          } else {
            console.error('附件类型不支持预览');
          }
          var winOpts = url + encodeURIComponent(WOPISrc);
          openWindow(winOpts, options.target);
        });
    } else if ('yozodcs' === this.filePreviewType) {
      $axios
        .get(WOPISrc, {
          params: Object.assign(
            {
              ignoreSha256: 'true'
            },
            options.wopiOptions
          )
        })
        .then(function (res) {
          console.log(res);
          // 0  : 文档格式到高清html的转换。先对与1，高清显示更多的细节，比如word中的图片。
          // 1  : 文档格式到html的转换
          // 14 : pdf文档格式到html的转换
          // 17 : tif文件转成html
          // 19 : 压缩文件到html的转换(模版)
          // 20 : PDF文件到html的转换(模版)
          // 21 : OFD文件到html的转换
          // 23 : 图片到html的转换
          // 45 : 视频到mp4的转换
          // 61 : 文档格式到高清html canvas的转换。针对office文件，兼容性更好
          console.log(result);

          var convertType = 0; //
          var fileName = fileInfo.BaseFileName;
          if (pdfReg.test(fileName)) {
            convertType = 20;
          } else if (ofdReg.test(fileName)) {
            convertType = 21;
          }
          // else if (tifReg.test(fileName)) {
          //   convertType = 17;
          // }
          else if (imgReg.test(fileName)) {
            convertType = 23;
          } else if (wmvReg.test(fileName)) {
            convertType = 45;
          } else if (zipReg.test(fileName)) {
            convertType = 19;
          }
          (function doConvert(convertType, password) {
            window.appModal && appModal.showMask('附件转换中...', null, 60 * 60 * 1000);
            $axios
              .get('/wopi/files/getYozoPreviewUrl', {
                params: {
                  fileName: fileName,
                  fileUrl: WOPISrc.replace(fileInfo.SHA256, fileInfo.SHA256 + '/contents'),
                  convertType: convertType
                }
              })
              .then(function (response) {
                console.log(response);
                let result = eval('(' + response.data.data + ')');
                if (0 == result.errorcode) {
                  var vToken = result.data;
                  openWindow(vToken.viewUrl, options.target);
                } else if (4 == result.errorcode) {
                  var password = prompt('请输入密码');
                  if ($.trim(password).length) {
                    doConvert(convertType, password);
                  }
                } else {
                  console.error(result.data.message);
                }
              });
          })(convertType);
        });
    } else {
      var previewServer = this.filePreviewServer || this.documentPreviewPath;
      var url = previewServer + '/document/online/viewer?WOPISrc=';
      var winOpts = url + encodeURIComponent(WOPISrc);
      if (options.urlParamString != undefined) {
        if (typeof options.urlParamString == 'function') {
          winOpts += '&' + options.urlParamString();
        } else if (typeof options.urlParamString == 'string') {
          winOpts += '&' + options.urlParamString;
        }
      }
      openWindow(winOpts, options.target);
    }
  } else {
    setTimeout(function () {
      _this.preview(WOPISrc, options);
    }, 1000);
  }
};
let previewer = null;

export const preview = function (WOPISrc, options) {
  if (previewer == null) {
    previewer = new FilePreviewAdapter();
  }
  previewer.preview(WOPISrc, options);
};

export const getFileIcon = function (filename) {
  if (/\.doc[x]?$/i.test(filename)) {
    //word文档
    return 'file-word';
  }

  if (/\.xls[x]?$|\.csv$/i.test(filename)) {
    //excel文档
    return 'file-excel';
  }

  if (/\.ppt[x]?$/i.test(filename)) {
    //ppt文档
    return 'file-ppt';
  }

  if (/\.pdf$/i.test(filename)) {
    //pdf 文档
    return 'file-pdf';
  }

  if (/\.txt$/i.test(filename)) {
    //文本文档
    return 'file-text';
  }

  if (/\.bpm$|\.png$|\.gif$|\.jpg$|\.jpeg/i.test(filename)) {
    //图片
    return 'file-image';
  }
  if (/\.zip$/i.test(filename)) {
    //压缩文件
    return 'file-zip';
  }
  return 'file';
}

export const getFileSize = function (size, unit) {
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
}


let maxChunkSize = null;
let getChunkSize = () => {
  if (maxChunkSize) {
    return maxChunkSize;
  }
  // 切片每次上传的块最大值
  var __size = (function (name) {
    var arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
    return arr ? decodeURIComponent(arr[2]) : null;
  })('fileupload.maxChunkSize');

  maxChunkSize = __size ? parseInt(__size) : undefined;
  return maxChunkSize;
}
let computeFileMD5 = (file, callback) => {
  if (file.md5) {
    return callback();
  }

  let chunkSize = getChunkSize(),
    chunks = Math.ceil(file.size / chunkSize),
    currentChunk = 0,
    spark = new SparkMD5.ArrayBuffer(),
    fileReader = new FileReader();

  fileReader.onload = function (e) {
    spark.append(e.target.result);
    currentChunk++;

    if (currentChunk < chunks) {
      loadNext();
    } else {
      let md5Str = spark.end();
      console.debug('计算文件hash值: ', md5Str); // Compute hash
      file.md5 = md5Str;
      callback();
    }
  };

  fileReader.onerror = function () {
    console.warn('读取文件失败');
  };

  function loadNext() {
    var start = currentChunk * chunkSize,
      end = start + chunkSize >= file.size ? file.size : start + chunkSize;

    fileReader.readAsArrayBuffer(blobSlice(file, start, end));
  }

  loadNext();
}
let blobSlice = (file, start, end) => {
  var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
  return blobSlice.call(file, start, end);
}
let getUploadBody = (xhr) => {
  let text = xhr.responseText || xhr.response;
  if (!text) {
    return text;
  }

  try {
    return JSON.parse(text);
  } catch (e) {
    return text;
  }
}
let getUploadError = (option, xhr) => {
  let msg = 'cannot ' + option.method + ' ' + option.action + ' ' + xhr.status + '\'';
  let err = new Error(msg);
  err.status = xhr.status;
  err.method = option.method;
  err.url = option.action;
  return err;
}
let doUploadFile = (requestOption, formData, progressCallback, successCallback, errorCallback) => {
  let headers = requestOption.headers || {};
  let source = requestOption.cancelToken || $axios.CancelToken.source();
  requestOption.cancelToken = source;
  requestOption.onCreateCancelToken && requestOption.onCreateCancelToken(source, requestOption);

  $axios
    .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
      headers: headers,
      cancelToken: source.token,
      onUploadProgress(e) {
        progressCallback && progressCallback.call(this, e);
      }
    }).then(data => {
      if (data.status < 200 || data.status >= 300) {
        // requestOption.onError(getUploadError(requestOption, data.request), getUploadBody(data.request));
        errorCallback && errorCallback.call(this, getUploadError(requestOption, data.request), getUploadBody(data.request));
      } else {
        //  requestOption.onSuccess && requestOption.onSuccess(data.data, data.request);
        successCallback && successCallback.call(this, data.data, data.request);
      }
    }).catch(error => {
      if ($axios.isCancel(error)) {
        console.log('Request canceled', error.message);
      } else {
        // requestOption.onError && requestOption.onError(error.request);
        errorCallback && errorCallback.call(this, getUploadError(requestOption, error.request));
      }
    });
}
let uploadFile = requestOption => {
  let formData = new FormData();
  formData.append(requestOption.filename, requestOption.file);

  // 请求前回调
  requestOption.onBeforeRequest && requestOption.onBeforeRequest(formData, requestOption);

  doUploadFile(requestOption, formData, e => {
    if (e.total > 0) {
      e.percent = e.loaded / e.total * 100;
    }
    requestOption.onProgress && requestOption.onProgress(e);
  }, (data, xhr) => {
    requestOption.onSuccess && requestOption.onSuccess(data, xhr);
  }, (error, data) => {
    requestOption.onError && requestOption.onError(error, data);
  })
}
let chunkUploadFile = requestOption => {
  computeFileMD5(requestOption.file, () => {
    getFileChunkInfo(requestOption.file, requestOption.chunkSize).then(fileChunkInfo => {
      doChunkUploadFile(fileChunkInfo, requestOption);
    }).catch(error => {
      requestOption.onError && requestOption.onError(getUploadError(requestOption, error.request));
    })
  });
}
let ChunkFile = function (file, fileChunkInfo) {
  this.file = file;
  this.fileChunkInfo = fileChunkInfo;
  this.init();
}
ChunkFile.prototype.init = function () {
  const _this = this;
  let fileSize = _this.file.size;
  let fileChunkInfo = _this.fileChunkInfo;
  let chunkSize = fileChunkInfo.chunkSize;
  let md5FileStored = fileChunkInfo.md5FileStored;
  let chunkIndexList = fileChunkInfo.chunkIndexList;
  // let chunkStoredCnt = chunkIndexList.length;
  let start = 0;
  let chunkIndex = -1;
  let storedChunkIndexMap = {};
  let maxChunkIndex = parseInt(fileSize / chunkSize);
  if (fileSize % chunkSize == 0) {
    maxChunkIndex--;
  }
  if (md5FileStored) {
    start = fileSize - 2;
    chunkIndex = maxChunkIndex;
  } else {
    for (let index = 0; index < chunkIndexList.length; index++) {
      // 存在中间缺失的分块
      if (chunkIndexList[index] == index) {
        start = start + chunkSize >= fileSize ? fileSize : start + chunkSize;
        chunkIndex = index;
      } else {
        break;
      }
    }
    chunkIndexList.forEach(cIndex => {
      storedChunkIndexMap[cIndex] = cIndex;
    });
    // if (chunkStoredCnt > 0) {
    //   // 断点续传，计算从哪个位置开始
    //   while (chunkStoredCnt-- > 0) {
    //     start = start + chunkSize >= fileSize ? fileSize : start + chunkSize;
    //   }
    // }
  }

  _this.start = start;
  _this.position = start;
  _this.chunkIndex = chunkIndex;
  _this.maxChunkIndex = maxChunkIndex;
  _this.storedChunkIndexMap = storedChunkIndexMap;
}
ChunkFile.prototype.nextChunk = function () {
  const _this = this;
  let fileSize = _this.file.size;
  let fileName = _this.file.name;
  let start = 0;
  let end = 0;
  if (_this.fileChunkInfo.md5FileStored) {
    start = _this.start;
    end = fileSize;
  } else {
    let chunkSize = _this.fileChunkInfo.chunkSize;
    let chunkIndex = _this.nextChunkIndex();
    start = chunkIndex * chunkSize;
    end = (chunkIndex + 1) * chunkSize;
    end = end >= fileSize ? fileSize : end;
  }
  // 分块上传的数据
  let chunkFile = blobSlice(_this.file, start, end);
  _this.start = start;
  _this.position = end;
  return new File([chunkFile], fileName);
}
ChunkFile.prototype.nextChunkIndex = function () {
  const _this = this;
  let storedChunkIndexMap = _this.storedChunkIndexMap;
  let chunkIndex = _this.chunkIndex;
  chunkIndex++;
  while (storedChunkIndexMap[chunkIndex] == chunkIndex) {
    chunkIndex++;
  }

  if (chunkIndex > _this.maxChunkIndex) {
    chunkIndex = _this.maxChunkIndex;
  }

  _this.chunkIndex = chunkIndex;
  return _this.chunkIndex;
}

let doChunkUploadFile = (fileChunkInfo, requestOption) => {
  let file = requestOption.file;
  let fileSize = file.size;
  let chunkSize = requestOption.chunkSize;

  let chunkFile = new ChunkFile(file, fileChunkInfo);
  let formData = new FormData();
  formData.set('file', chunkFile.nextChunk());
  formData.set('frontUUID', file.uid);
  formData.set('md5', file.md5);
  formData.set('localFileSourceIcon', '');
  formData.set('size', fileSize);
  formData.set('chunkSize', chunkSize);
  formData.set('chunkIndex', chunkFile.chunkIndex);
  formData.set('chunkCount', chunkFile.maxChunkIndex + 1);
  let headers = requestOption.headers = requestOption.headers || {};
  headers['Content-Range'] = `bytes ${chunkFile.start}-${chunkFile.position - 1}/${fileSize}`;

  // 请求前回调
  requestOption.onBeforeRequest && requestOption.onBeforeRequest(formData, requestOption, fileChunkInfo);

  let progressCallback = (e) => {
    if (!requestOption.verify) {
      e.percent = (chunkFile.start + (e.loaded || 0)) / fileSize * 100;
      requestOption.onProgress && requestOption.onProgress(e);
    }
  }
  let successCallback = (data, xhr) => {
    if (data && data.success) {
      if (data.data === 'continue') {
        // 分块上传的数据
        formData.set('file', chunkFile.nextChunk());
        formData.set('chunkIndex', chunkFile.chunkIndex);
        formData.set('chunkCount', chunkFile.maxChunkIndex + 1);
        headers['Content-Range'] = `bytes ${chunkFile.start}-${chunkFile.position - 1}/${fileSize}`;
        doUploadFile(requestOption, formData, progressCallback, successCallback, errorCallback);
      } else {
        requestOption.onSuccess && requestOption.onSuccess(data, xhr);
      }
    } else {
      requestOption.onError && requestOption.onError(getUploadError(requestOption, xhr), data);
    }
  }
  let errorCallback = (error, data) => {
    // 上传失败，验证分片上传的完整性重试一次上传
    if (!requestOption.verify) {
      requestOption.verify = true;
      console.log("verify upload", requestOption);
      getFileChunkInfo(requestOption.file, requestOption.chunkSize).then(fileChunkInfo => {
        if (fileChunkInfo.chunkIndexList.length != chunkFile.maxChunkIndex + 1) {
          doChunkUploadFile(fileChunkInfo, requestOption);
        } else {
          requestOption.onError && requestOption.onError(error, data);
        }
      }).catch(verifyError => {
        requestOption.onError && requestOption.onError(error, data);
      })
    } else {
      requestOption.onError && requestOption.onError(error, data);
    }
  }
  doUploadFile(requestOption, formData, progressCallback, successCallback, errorCallback);
}
let getFileChunkInfo = (file, chunkSize) => {
  return $axios.get("/proxy/repository/file/mongo/getFileChunkInfo", {
    params: {
      md5: file.md5,
      chunkSize
    }
  }).then(({ data: result }) => {
    if (result.success) {
      // 可能存在文件名不同，文件内容相同的情况，分块进行去重
      let chunkIndexList = result.data.chunkIndexList.length ? Array.from(new Set(result.data.chunkIndexList)) : [];
      return { md5FileStored: result.data.hasMd5FileFlag, chunkIndexList, chunkSize }
    } else {
      throw new Error(result.msg);
    }
  })
};
export const upload = function (requestOption) {
  // 独立请求头
  if (requestOption.headers) {
    requestOption.headers = deepClone(requestOption.headers);
  } else {
    requestOption.headers = {};
  }

  let chunkSize = getChunkSize();
  if (chunkSize) {
    let file = requestOption.file;
    let chunkUpload = file.size > chunkSize;
    requestOption.chunkSize = chunkSize;
    if (chunkUpload) {
      chunkUploadFile(requestOption);
    } else {
      uploadFile(requestOption);
    }
  } else {
    uploadFile(requestOption);
  }
}
