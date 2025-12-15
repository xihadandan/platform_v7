<script>
import { mapMutations } from "vuex";
import { storage, utils } from "wellapp-uni-framework";
// #ifndef APP-PLUS
import "@/packages/_/style/index.scss";
// #endif

export default {
  onLaunch: function () {
    console.log("App Launch");
    let uniIdToken = storage.getAccessToken(); // uni.getStorageSync("uni_id_token");
    if (uniIdToken) {
      // uni.getStorageSync("username")
      this.login(storage.getUsername());
    }
    this.onOrgShow();
    this.onActionSheetShow();
    this.onptPopupShow();
    this.onPtToastShow();

    uni.$nToast = this.$nToast;
    uni.$on("nToast", (options) => {
      this.$nToast(options);
    });
    this.asyncLoadLocale(this.$i18n.locale).then((message) => {
      this.$i18n.mergeLocaleMessage(this.$i18n.locale, message);
    });
    utils.setI18n(this.$i18n);
  },
  onShow: function () {
    console.log("App Show");
  },
  onHide: function () {
    console.log("App Hide");
  },

  methods: {
    ...mapMutations(["login", "setUniverifyErrorMsg", "setHideUniverify"]),
    asyncLoadLocale(locale) {
      return new Promise((resolve, reject) => {
        const data = require(`@locale/${locale}.json`);
        resolve(data);
      });
    },
    // 组织机构选择
    onOrgShow() {
      // 监听事件
      uni.$on("showMyPopup", (params, options) => {
        this.$orgSelectPopup.show(params, options);
      });
    },
    // 从底部向上弹出操作菜单
    onActionSheetShow() {
      // 监听事件
      uni.$on("ptActionSheetShow", (options) => {
        // #ifndef H5
        this.$ptActionSheet.show(options);
        // #endif
        // #ifdef H5
        this.$globalActionSheet.show(options);
        // #endif
      });
    },
    // 全局弹框
    onptPopupShow() {
      // 监听事件
      uni.$on("ptPopupShow", (options) => {
        // #ifndef H5
        this.$ptPopup.show(options);
        // #endif
        // #ifdef H5
        this.$globalPopup.show(options);
        // #endif
      });
    },

    onPtToastShow() {
      uni.$ptToastShow = (options) => {
        // #ifndef H5
        this.$nToast(options);
        // #endif
        // #ifdef H5
        this.$globalToast.show(options);
        // #endif
      };
      uni.$on("ptToastShow", (options) => {
        // #ifndef H5
        this.$nToast(options);
        // #endif
        // #ifdef H5
        this.$globalToast.show(options);
        // #endif
      });
    },
  },
};
</script>

<style lang="scss">
@import "@/packages/_/components/uview-ui/index.scss";
// @import "/static/css/pt/ant-iconfont/iconfont.css";
// @import "/static/css/pt/iconfont/iconfont.css";
// @import "@/uni_modules/uni-scss/index.scss";
/* #ifndef APP-PLUS-NVUE */
/* uni.css - 通用组件、模板样式库，可以当作一套ui库应用 */
@import "./common/uni.css";

/* H5 兼容 pc 所需 */
/* #ifdef H5 */
@media screen and (min-width: 768px) {
  body {
    overflow-y: scroll;
  }
}

/* 顶栏通栏样式 */
/* .uni-top-window {
	    left: 0;
	    right: 0;
	} */

uni-page-body {
  min-height: 100% !important;
  height: auto !important;
  font-size: 14px;
}

.uni-top-window uni-tabbar .uni-tabbar {
  background-color: #fff !important;
}

.uni-app--showleftwindow .hideOnPc {
  display: none !important;
}

/* #endif */

/* #endif*/
page {
  background-color: #f5f6f8;
}

button.primary {
  background-color: #0faeff;
}

/* #ifdef APP-PLUS */
@import "@/packages/_/style/index.scss";
/* #endif */
</style>
