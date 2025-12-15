/**
 * 获取文件名和后缀
 * @param {String} name
 */
export const get_file_ext = (name) => {
  const last_len = name.lastIndexOf(".");
  const len = name.length;
  return {
    name: name.substring(0, last_len),
    ext: name.substring(last_len + 1, len),
  };
};

/**
 * 获取扩展名
 * @param {Array} fileExtname
 */
export const get_extname = (fileExtname) => {
  if (!Array.isArray(fileExtname)) {
    if (fileExtname) {
      let extname = fileExtname.replace(/(\[|\])/g, "");
      return extname.split(",");
    }
  } else {
    return fileExtname;
  }
  return [];
};

/**
 * 获取文件和检测是否可选
 */
export const get_files_and_is_max = (res, _extname, vm) => {
  let filePaths = [];
  let files = [];
  if (!_extname || _extname.length === 0) {
    return {
      filePaths,
      files,
    };
  }
  res.tempFiles.forEach((v) => {
    let fileFullName = get_file_ext(v.name);
    const extname = fileFullName.ext.toLowerCase();
    if (_extname.indexOf(extname) !== -1) {
      files.push(v);
      filePaths.push(v.path);
    }
  });
  if (files.length !== res.tempFiles.length) {
    uni.showToast({
      title: vm.$i18n.$t(vm, "WidgetFormFileUpload.message.invalidFileFormat", {
        allLength: res.tempFiles.length,
        errorLength: res.tempFiles.length - files.length,
      }), //,`当前选择了${res.tempFiles.length}个文件 ，${res.tempFiles.length - files.length} 个文件格式不正确`
      icon: "none",
      duration: 5000,
    });
  }

  return {
    filePaths,
    files,
  };
};

/**
 * 获取图片信息
 * @param {Object} filepath
 */
export const get_file_info = (filepath) => {
  return new Promise((resolve, reject) => {
    uni.getImageInfo({
      src: filepath,
      success(res) {
        resolve(res);
      },
      fail(err) {
        reject(err);
      },
    });
  });
};
/**
 * 获取封装数据
 */
export const get_file_data = async (files, type = "image") => {
  // 最终需要上传数据库的数据
  let fileFullName = get_file_ext(files.name);
  const extname = fileFullName.ext.toLowerCase();
  let filedata = {
    name: files.name,
    uuid: files.uuid,
    extname: extname || "",
    cloudPath: files.cloudPath,
    fileType: files.fileType,
    thumbTempFilePath: files.thumbTempFilePath,
    url: files.path || files.path,
    size: files.size, //单位是字节
    image: {},
    path: files.path,
    video: {},
  };
  if (type === "image") {
    const imageinfo = await get_file_info(files.path);
    delete filedata.video;
    filedata.image.width = imageinfo.width;
    filedata.image.height = imageinfo.height;
    filedata.image.location = imageinfo.path;
  } else {
    delete filedata.image;
  }
  return filedata;
};
export const getFileIcon = function (filename) {
  let icon = {
    icon: "ant-iconfont file",
    bgColor: "var(--w-primary-color)",
  };
  if (/\.doc[x]?$/i.test(filename)) {
    //word文档
    icon.icon = "ant-iconfont file-word";
  } else if (/\.xls[x]?$|\.csv$/i.test(filename)) {
    //excel文档
    icon.icon = "ant-iconfont file-excel";
    icon.bgColor = "var(--w-success-color)";
  } else if (/\.ppt[x]?$/i.test(filename)) {
    //ppt文档
    icon.icon = "ant-iconfont file-ppt";
    icon.bgColor = "var(--w-warning-color)";
  } else if (/\.pdf$/i.test(filename)) {
    //pdf 文档
    icon.icon = "ant-iconfont file-pdf";
    icon.bgColor = "var(--w-danger-color)";
  } else if (/\.txt$/i.test(filename)) {
    //文本文档
    icon.icon = "ant-iconfont file-text";
    icon.bgColor = "var(--w-success-color)";
  } else if (/\.bpm$|\.png$|\.gif$|\.jpg$|\.jpeg/i.test(filename)) {
    //图片
    icon.icon = "ant-iconfont file-image";
  } else if (/\.zip$/i.test(filename)) {
    //压缩文件
    icon.icon = "ant-iconfont file-zip";
  }
  return icon;
};

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
};
export const formatSize = function (size, pointLength, units) {
  var unit;
  units = units || ["B", "KB", "MB", "GB", "TB"];
  while ((unit = units.shift()) && size > 1024) {
    size = size / 1024;
  }
  return (unit === "B" ? size : size.toFixed(pointLength || 2)) + unit;
};
