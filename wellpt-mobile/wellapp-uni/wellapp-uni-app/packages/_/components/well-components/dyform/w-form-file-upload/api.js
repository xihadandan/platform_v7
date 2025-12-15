// 请求附件来源
export const fetchFileSource = () => {
  return new Promise((resolve, reject) => {
    uni.$axios
      .post("/json/data/services", {
        serviceName: "dyformFileListSourceConfigService",
        methodName: "getAllBean",
        args: JSON.stringify([]),
        validate: false,
      })
      .then((res) => {
        const { data } = res;
        if (data.code == 0 && data.data) {
          resolve(data.data);
        } else {
          reject(res);
        }
      });
  });
};

export const loadLogicFileByFiles = (files) => {
  return uni.$axios
    .get(`/repository/file/mongo/getNonioFile?fileID=${files.map((file) => file.fileID).join(";")}`)
    .then(({ data: result }) => {
      if (result.data && result.data.length) {
        return result.data;
      }
      return files;
    })
    .catch((error) => {
      return Promise.reject(error.message);
    });
};

// 请求文件预览服务器地址
export const fetchPreviewServer = () => {
  return new Promise((resolve, reject) => {
    let previewServer = uni.getStorageSync("previewServer");
    if (previewServer) {
      resolve(previewServer);
    }
    const key = "document.preview.path";
    const getTimestamp = new Date().getTime();
    uni.request({
      url: "/basicdata/system/param/get?key=" + key + "&timestamp=" + getTimestamp,
      method: "GET",
      contentType: "application/json",
      dataType: "json",
      success: (success) => {
        previewServer = success.data.data;
        uni.setStorageSync("previewServer", previewServer);
        resolve(previewServer);
      },
    });
  });
};

// 请求表单标题
export const fetchDyformTitle = (formData) => {
  return new Promise((resolve, reject) => {
    uni.$axios.post(`/api/dyform/data/getDyformTitle`, formData).then((res) => {
      const { data } = res;
      if (data.code == 0) {
        resolve(data.data);
      } else {
        reject(res);
      }
    });
  });
};

export default {
  fetchFileSource,
  loadLogicFileByFiles,
  fetchPreviewServer,
  fetchDyformTitle,
};
