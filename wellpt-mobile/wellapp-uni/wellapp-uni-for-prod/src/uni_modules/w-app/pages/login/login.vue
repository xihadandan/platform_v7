<template>
  <view class="content">
    <w-login
      logoImg="/static/images/wellinfo_logo_vertical_white.png"
      logoTitle="一体化办公助手"
      :system="system"
    ></w-login>
    <w-upgrade-center></w-upgrade-center>
  </view>
</template>

<script>
export default {
  data() {
    return { system: undefined };
  },
  onLoad(options) {
    // 由地址参数获取登录的系统标志
    if (options.system != undefined) {
      this.system = options.system;
    }
  },
};
</script>

<style lang="scss" scoped>
.content {
  padding: 0;

  ::v-deep .w-login {
    .login-header {
      padding-top: 80px;
      background: linear-gradient(#488cee -200%, #ffffff 50%);

      .title {
        margin-top: 15px;
        color: #000;
      }
    }

    .login-content {
      padding: 0px 12px;
    }

    .login-btn {
      margin-top: 5px;
    }

    .btn-row {
      margin-top: 25px;
      padding: 10px;
    }
  }
}
</style>
