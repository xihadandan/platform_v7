var backendServer = getBackendUrl();
var fileServiceURL = {
  saveFiles: '/proxy-repository/repository/file/mongo/savefiles', // 上传的文件地址
  savefilesChunk: '/proxy-repository/repository/file/mongo/savefilesChunk', // 上传的文件地址
  getFileChunkInfoAndSave: '/proxy-repository/repository/file/mongo/getFileChunkInfoAndSave', // 根据md5获取对应文件块后端存储情况
  getFileChunkInfo: '/proxy-repository/repository/file/mongo/getFileChunkInfo', // 根据md5获取对应文件块后端存储情况
  saveFilesByFileIds: '/repository/file/mongo/saveFilesByFileIds', // 上传的文件地址
  saveTemps: '/proxy-repository/repository/file/mongo/saveTemps', // 上传的临时文件地址
  downFile: '/proxy-repository/repository/file/mongo/download?fileID=', // 下载文件地址
  downAllFiles: '/proxy-repository/repository/file/mongo/downAllFiles?fileIDs=', // 下载文件地址
  downAllFiles4ocx: '/repository/file/mongo/downAllFiles4ocx?fileIDs=', // 下载文件地址

  downloadBody: '/proxy-repository/repository/file/mongo/downloadBody/', // 正文下载地址
  deleteFile: '/proxy-repository/repository/file/mongo/deleteFile?fileID=', // 删除文件地址
  updateSignature: '/proxy-repository/repository/file/mongo/updateSignature', // 更新签名
  viewFile: '/proxy-repository/repository/file/mongo/downloadSWF?fileID=', // 查看
  downloadOcx: '/repository/file/mongo/download4ocx?fileID=',
  excelbody: '/static/js/pt/js/fileupload/ocx/$WTemplate.xls',
  wordbody: '/static/js/pt/js/fileupload/ocx/$WTemplate.doc',
  wordbodymongo: '/proxy-repository/repository/file/mongo/download4bodytemplate' // 正文模板(存储在mongodb中)
};

// 图标上传控件风格
iconFileControlStyle = {
  OnlyIcon: 0, // 只有图标
  OnlyBody: 1, // 只有正文
  IconAndBody: 2
  // 有图标也有正文
};

BODY_FILEID_PREFIX = 'BODY_FILEID_';

/**
 * 格式化文件大小, 输出成带单位的字符串
 *
 * @method formatSize
 * @grammar Base.formatSize( size ) => String
 * @grammar Base.formatSize( size, pointLength ) => String
 * @grammar Base.formatSize( size, pointLength, units ) =>
 *          String
 * @param {Number}
 *            size 文件大小
 * @param {Number}
 *            [pointLength=2] 精确到的小数点数。
 * @param {Array}
 *            [units=[ 'B', 'K', 'M', 'G', 'TB' ]]
 *            单位数组。从字节，到千字节，一直往上指定。如果单位数组里面只指定了到了K(千字节)，同时文件大小大于M,
 *            此方法的输出将还是显示成多少K.
 */
function formatSize(size, pointLength, units) {
  var unit;
  units = units || ['B', 'KB', 'MB', 'GB', 'TB'];
  while ((unit = units.shift()) && size > 1024) {
    size = size / 1024;
  }
  return (unit === 'B' ? size : size.toFixed(pointLength || 2)) + unit;
}
