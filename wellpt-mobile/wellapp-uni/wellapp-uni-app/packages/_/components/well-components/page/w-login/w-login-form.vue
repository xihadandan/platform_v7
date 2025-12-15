<template>
  <view class="w-login-form">
    <!-- 用户名密码、用户名手机号密码登录方式 -->
    <view v-if="currentLoginWay == 'usrPwd' || currentLoginWay == 'usrPhonePwd'" class="login-content">
      <slot name="login-content">
        <view>
          <view class="login-row">
            <text class="title" v-if="!formLabelHidden">{{ $t("loginComponent.acctText", "账号") }}：</text>
            <uni-w-easyinput
              type="text"
              v-model="username"
              :inputBorder="inputBorder"
              prefixIconColor="var(--w-text-color-lighter)"
              :prefixIconSize="20"
              prefixIcon="iconfont icon-filled_zhanghao"
              :placeholder="$t('loginComponent.acctInputPlaceholder', '请输入账号')"
            >
            </uni-w-easyinput>
          </view>
          <view v-if="currentLoginWay == 'usrPhonePwd'" class="login-row">
            <text class="title" v-if="!formLabelHidden">{{ $t("loginComponent.phoneNo", "手机号") }}：</text>
            <uni-w-easyinput
              type="text"
              v-model="mobilePhone"
              :inputBorder="inputBorder"
              :prefixIconSize="20"
              prefixIconColor="var(--w-text-color-lighter)"
              prefixIcon="iconfont icon-filled_shouji"
              :placeholder="$t('loginComponent.phoneNoInputPlaceholder', '请输入手机号码')"
            ></uni-w-easyinput>
          </view>
          <view class="login-row">
            <text class="title" v-if="!formLabelHidden">{{ $t("loginComponent.pwdText", "密码") }}：</text>
            <uni-w-easyinput
              type="password"
              v-model="password"
              :inputBorder="inputBorder"
              :prefixIconSize="20"
              prefixIconColor="var(--w-text-color-lighter)"
              prefixIcon="iconfont icon-filled_mima"
              passwordIcon
              :placeholder="$t('loginComponent.pwdInputPlaceholder', '请输入密码')"
            ></uni-w-easyinput>
          </view>
          <view v-if="showImageVerifyCode" class="login-row image-verify-code">
            <text class="title" v-if="!formLabelHidden">{{ $t("loginComponent.verificationCode", "验证码") }}：</text>
            <uni-w-easyinput
              type="text"
              v-model="imageVerifyCode"
              :inputBorder="inputBorder"
              :prefixIconSize="20"
              prefixIconColor="var(--w-text-color-lighter)"
              prefixIcon=" iconfont icon-filled_yanzhengma"
              :placeholder="$t('loginComponent.verificationCodeInputPlaceholder', '请输入验证码')"
            >
              <template slot="addonAfter">
                <image :src="imageVerifyCodeUrl" class="image" @click="createRandImageVerifyCode"></image>
              </template>
            </uni-w-easyinput>
          </view>
        </view>
        <view class="login-row">
          <uni-w-data-checkbox
            v-model="remeberMe"
            :localdata="[{ text: $t('loginComponent.rememberPwd', '记住密码'), value: true }]"
            :multiple="true"
          ></uni-w-data-checkbox>
        </view>
      </slot>
    </view>
    <!-- 短信验证码登录方式 -->
    <view v-if="currentLoginWay == 'sms'" class="login-content">
      <slot name="login-content-sms">
        <view>
          <view class="login-row">
            <text class="title" v-if="!formLabelHidden">{{ $t("loginComponent.phoneNo", "手机号") }}：</text>
            <uni-w-easyinput
              class="m-input"
              type="text"
              clearable
              v-model="mobilePhone"
              :inputBorder="inputBorder"
              :prefixIconSize="20"
              prefixIconColor="var(--w-text-color-lighter)"
              prefixIcon="iconfont icon-filled_shouji"
              :placeholder="$t('loginComponent.phoneNoInputPlaceholder', '请输入手机号码')"
            ></uni-w-easyinput>
          </view>
          <view class="login-row">
            <text class="title" v-if="!formLabelHidden">{{ $t("loginComponent.verificationCode", "验证码") }}：</text>
            <uni-w-easyinput
              type="text"
              v-model="smsVerifyCode"
              :inputBorder="inputBorder"
              :prefixIconSize="20"
              prefixIconColor="var(--w-text-color-lighter)"
              prefixIcon=" iconfont icon-filled_yanzhengma"
              :placeholder="$t('loginComponent.verificationCodeInputPlaceholder', '请输入验证码')"
            >
              <template slot="addonAfter">
                <view class="send-code-btn" @click="onSendSmsVerifyCodeClick">{{
                  smsVerifyCodeDuration
                    ? smsVerifyCodeDuration + "s"
                    : $t("loginComponent.SendVerificationCode", "发送验证码")
                }}</view>
              </template>
            </uni-w-easyinput>
          </view>
        </view>
      </slot>
    </view>
    <view class="login-btn">
      <slot name="login-btn">
        <view v-if="isUniLoginProvider">
          <template v-for="(provider, key) in providerList">
            <uni-w-button
              v-show="provider.value == currentLoginWay"
              type="primary"
              @click="onUniLoginProviderClick(provider)"
              :key="'button_' + key"
            >
              {{ provider.text }}
            </uni-w-button>
          </template>
        </view>
        <uni-w-button
          v-else
          type="primary"
          block
          size="large"
          :disabled="loginBtnDisabled"
          :loading="loginBtnLoading"
          style="--w-button-border-radius: 8px; --w-button-height-lg: 48px; --w-button-weight: bold"
          @click="onLoginClick"
        >
          {{ $t("loginComponent.login", "登录") }}
        </uni-w-button>
      </slot>
    </view>
    <view class="opration-row">
      <view v-if="showSwitchLoginWay" @tap="onShowSwitchLoginWayClick">
        <w-icon icon="iconfont icon-luojizujian-dengluxiaoyan" :size="14"></w-icon>
        <text>{{ $t("loginComponent.changeLoginType", "登录方式") }}</text>
      </view>
      <view v-if="showLoginSettings" class="login-settings" @click="onLoginSettingsClick">
        <w-icon icon="iconfont icon-ptkj-shezhi" :size="14"></w-icon>
        <text>{{ $t("loginComponent.loginSettings", "登录设置") }}</text>
      </view>
      <view v-if="enableI18nLanguage && languageText" @click="onChangeLocaleClick">
        <w-icon icon="iconfont icon-luojizujian-dangqianyuyan" :size="14"></w-icon>
        <text>
          {{ languageText }}
        </text>
      </view>
    </view>
    <!-- <uni-w-button @click="handleShowToast">显示消息提示框</uni-w-button> -->
  </view>
</template>

<script>
import mixin from "./w-login-form-mixin";
export default {
  mixins: [mixin],
  data() {
    return {};
  },
  methods: {
    handleShowToast() {
      let message = "提示提示,,提11112示提示,,提示提3333示提示,,提示提示5,55提示33,";
      // #ifndef APP-PLUS
      // uni.showToast({
      //   title: message,
      //   duration: 6000 * 30,
      // });
      // this.$globalToast.show({
      //   title: message,
      // });
      // uni.$emit("ptToastShow", {
      //   title: message,
      // });
      uni.$ptToastShow({
        title: message,
      });
      // #endif
      // #ifdef APP-PLUS
      // this.$nToast(message)
      // uni.$nToast(message)
      uni.$emit("nToast", message);

      // #endif
    },
  },
};
</script>

<style lang="scss" scoped>
.w-login-form {
  .login-content {
    text-align: left;
    .login-row {
      margin-bottom: 16px;
    }
    .image-verify-code {
      .image {
        width: 100px;
        height: 40px;
      }
    }
    .send-code-btn {
      color: var(--w-primary-color);
      padding-right: 8px;
    }
  }
  .login-btn {
    margin: 24px 0 0;
  }
  .opration-row {
    margin: 16px 0;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;

    > * {
      color: var(--w-text-color-lighter);
      position: relative;
      padding: 0 16px;

      &::after {
        content: "";
        position: absolute;
        right: 0;
        width: 1px;
        height: 10px;
        background-color: var(--w-border-color-light);
        top: 50%;
        margin-top: -5px;
      }
      &:last-child {
        &::after {
          content: none;
        }
      }
      .w-icon {
        margin-right: var(--w-margin-3xs);
      }
    }
  }
}
</style>
