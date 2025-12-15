<template>
  <view :style="theme" style="padding: 20px; flex: 1">
    <uni-forms ref="form" :rules="rules" :modelValue="server">
      <uni-forms-item label="服务器地址" name="host" labelWidth="85">
        <input class="uni-input-border" v-model="server.host" placeholder="请输入" />
      </uni-forms-item>

      <uni-forms-item label="服务器端口" name="port" labelWidth="85">
        <input class="uni-input-border" v-model="server.port" placeholder="请输入" type="number" />
      </uni-forms-item>

      <uni-forms-item label="启用SSL" name="enableSSL" labelWidth="85">
        <switch :checked="server.enableSSL" @change="onEnableSLLSwitchChange" />
      </uni-forms-item>
      <view class="uni-button-group pointer">
        <button class="uni-button uni-button-full" type="primary" @click="submitForm">确认</button>
      </view>
    </uni-forms>
  </view>
</template>

<script>
import { trim } from "lodash";
import { storage } from "wellapp-uni-framework";
export default {
  data() {
    return {
      server: {
        host: "",
        port: "",
        enableSSL: false,
      },
      rules: {
        host: {
          rules: [
            {
              required: true,
              errorMessage: "请输入服务器地址",
            },
          ],
        },
        port: {
          rules: [
            {
              required: true,
              errorMessage: "请输入服务器端口",
            },
          ],
        },
        enableSSL: {
          rules: [],
        },
      },
    };
  },
  onLoad: function () {
    var serverConfig = uni.getStorageSync("server_config");
    if (typeof serverConfig == "object") {
      this.server = serverConfig;
    }
  },
  methods: {
    onEnableSLLSwitchChange: function (e) {
      this.server.enableSSL = e.detail.value;
    },
    submitForm() {
      var _self = this;
      _self.$refs["form"]
        .validate()
        .then((res) => {
          // console.log('success', res);
          _self.save(res);
        })
        .catch((err) => {
          console.log("err", err);
        });
    },
    save() {
      var _self = this;
      uni.setStorageSync("server_config", _self.server);
      var host = trim(_self.server.host);
      var port = trim(_self.server.port);
      var enableSSL = _self.server.enableSSL;
      var wellappBackendUrl = "";
      if (enableSSL) {
        wellappBackendUrl = "https://" + host + ":" + port;
      } else {
        wellappBackendUrl = "http://" + host + ":" + port;
      }
      storage.setWellappBackendUrl(wellappBackendUrl);
      // uni.setStorageSync("wellapp_backend_url", wellappBackendUrl);
      uni.showToast({
        title: "保存成功！",
      });
    },
  },
};
</script>

<style>
/* 标题栏 */
.uni-header {
  padding: 0 15px;
  display: flex;
  height: 55px;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px #f5f5f5 solid;
}

.uni-title {
  margin-right: 10px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.uni-group {
  display: flex;
  align-items: center;
  justify-content: center;
  word-break: keep-all;
}

/* 容器 */
.uni-container {
  padding: 15px;
  box-sizing: border-box;
}

/* 按钮样式 */
.uni-button-group {
  margin-top: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pointer {
  cursor: pointer;
}

.uni-input-border,
.uni-textarea-border {
  width: 100%;
  font-size: 14px;
  color: #666;
  border: 1px #e5e5e5 solid;
  border-radius: 5px;
  box-sizing: border-box;
}

.uni-input-border {
  padding: 0 10px;
  height: 35px;
}

.uni-icon-password-eye {
  position: absolute;
  right: 8px;
  top: 6px;
  font-family: uniicons;
  font-size: 20px;
  font-weight: normal;
  font-style: normal;
  width: 24px;
  height: 24px;
  line-height: 24px;
  color: #999999;
}

.uni-eye-active {
  color: #007aff;
}

.uni-button {
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 4px;
  line-height: 1;
  margin: 0;
  box-sizing: border-box;
  overflow: initial;
}

.uni-button-full {
  width: 100%;
}
</style>
