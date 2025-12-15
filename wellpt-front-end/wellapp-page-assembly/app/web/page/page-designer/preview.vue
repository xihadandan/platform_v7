<template>
  <HtmlWrapper title="预览" :class="[isBigScreen ? 'bigscreen' : undefined, 'preview']">
    <div v-if="loading" class="spin-center">
      <a-spin />
    </div>
    <template v-else>
      <a-result title="设计器已退出 , 无法预览" v-if="closeDesigner"></a-result>
      <div v-else-if="widgets.length > 0">
        <div
          :class="['top-menu-bar', hideTopMenuBar ? 'hidden' : undefined]"
          @mouseenter="mouseenterTopMenuBar"
          @mouseleave="hideTopMenuBar = true"
        >
          <div style="display: flex; color: rgb(255, 255, 255); font-weight: bolder; justify-content: space-around; align-items: baseline">
            <label>切换语言</label>
            <a-select
              :getPopupContainer="getPopupContainer"
              :options="i18nOption"
              style="width: 140px"
              size="small"
              v-model="selectLocale"
              @change="onChangeLocale"
            ></a-select>
            <!-- <a-icon type="up" @click="hideTopMenuBar = true" /> -->
          </div>
        </div>
        <WidgetVpage :key="pageKey" :page-id="pageId" :widget="{ items: widgets, style: pageStyle }" :jsModule="jsModule" ref="vPage">
          <UserPreferenceCustomize ref="userPreferenceCustomize" designMode />
        </WidgetVpage>
      </div>
    </template>
  </HtmlWrapper>
</template>
<style lang="less" scoped>
#app.preview {
  min-height: 100vh;
  height: auto;

  .top-menu-bar {
    position: fixed;
    width: 230px;
    top: 0px;
    transform: translateX(-50%);
    z-index: 10000000;
    left: 50%;
    text-align: center;
    background: var(--w-primary-color);
    border-radius: 0px 0px 5px 5px;
    padding: 5px 0px;
    opacity: 1;
    transition: top 0.3s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    &.hidden {
      top: -32px;
      opacity: 0.4;
    }
  }
}
</style>
<script type="text/babel">
import '@installPageWidget';
// import '@dyform/app/web/framework/vue/install';
import { queryString, generateId, asyncLoadLocaleData } from '@framework/vue/utils/util';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
import WidgetVpage from '../../widget/widget-vpage.vue';
import UserPreferenceCustomize from '@pageAssembly/app/web/lib/user-preference-customize.vue';
import { debounce, merge, set } from 'lodash';
export default {
  name: 'PagePreview',
  injects: ['pageContext'],
  props: {
    designer: Object
  },
  data() {
    return {
      i18nOption: [],
      top: 0,
      widgets: [],
      pageId: undefined,
      jsModule: undefined,
      installed: false,
      closeDesigner: false,
      loading: true,
      isBigScreen: false,
      pageStyle: {},
      containerStyle: {},
      pageKey: new Date().getTime(),
      popoverVisible: false,
      hideTopMenuBar: false,
      selectLocale: 'zh_CN' //this.$i18n.locale
    };
  },
  provide() {
    return {
      designMode: false,
      ENVIRONMENT: this.ENVIRONMENT || {},
      previewMode: true
    };
  },
  beforeCreate() {},
  components: { WidgetVpage, UserPreferenceCustomize },
  computed: {},
  created() {
    // FIXME: 设计器先按中文显示，后续要修改
    this.$i18n.locale = 'zh_CN';
  },

  methods: {
    mouseenterTopMenuBar() {
      clearTimeout(this.hideTopMenuBarTimeout);
      this.hideTopMenuBar = false;
      this.hoverTopMenuBar = true;
    },
    reloadI18n(locale) {
      asyncLoadLocaleData(locale).then(results => {
        if (results[0]) {
          merge($app.$children[0].locale, results[0]);
        }
        if (results[1]) {
          this._lazyLoadAppCodeI18nMessagesApplyTo('pt-app-widget').then(() => {
            if (results[1]) {
              $app.$i18n.mergeLocaleMessage(locale, results[1]);
            }
            this.$tempStorage.clear().then(() => {
              this.$refs.vPage._provided.$tempStorage.clear().then(() => {
                this.$i18n.locale = locale;
                this.pageKey = new Date().getTime();
              });
            });
          });
        }
      });
    },
    fetchLocaleOptions() {
      this.$axios
        .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
        .then(({ data }) => {
          if (data.code == 0) {
            for (let d of data.data) {
              this.i18nOption.push({
                label: d.name,
                value: d.locale,
                description: d.remark || d.name,
                transCode: d.translateCode
              });
            }
            if (!this.hoverTopMenuBar) {
              this.hideTopMenuBarTimeout = setTimeout(() => {
                this.hideTopMenuBar = true;
              }, 2000);
            }
          }
        })
        .catch(error => {});
    },
    setWidgetI18ns(widgets) {
      let i18ns = {};
      function findI18nObjects(json, widget) {
        function traverse(obj, belongToWidget) {
          if (typeof obj !== 'object' || obj === null) return;
          if (obj.id && obj.wtype && obj.configuration) {
            belongToWidget = obj;
          }
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item, belongToWidget);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n' && obj[key] != undefined) {
                let i18n = obj[key];
                for (let langCode in i18n) {
                  if (i18ns[langCode] == undefined) {
                    i18ns[langCode] = { Widget: {} };
                  }

                  for (let c in i18n[langCode]) {
                    set(i18ns[langCode].Widget, c, i18n[langCode][c]);
                  }
                }
              } else {
                traverse(obj[key], belongToWidget);
              }
            }
          }
        }
        traverse(json, widget);
      }
      for (let wgt of widgets) {
        findI18nObjects(wgt, wgt);
      }
      console.log('查询 i18ns ', i18ns);
      for (let l in i18ns) {
        this.$i18n.mergeLocaleMessage(l, i18ns[l]);
      }
    },
    getPopupContainer(triggerNode) {
      return triggerNode.parentNode;
    },
    onChangeLocale() {
      this.reloadI18n(this.selectLocale);
    },
    init() {
      let designer = this.designerApp.designer;
      this.jsModule = designer.pageJsModule ? designer.pageJsModule.key : undefined;
      this.pageId = this.designerApp.pageDefinition.id || this.id;
      this.widgets = JSON.parse(JSON.stringify(designer.widgets));
      if (this.designerApp.widgetI18ns != undefined) {
        for (let l in this.designerApp.widgetI18ns) {
          this.$i18n.mergeLocaleMessage(l, this.designerApp.widgetI18ns[l]);
        }
      } else {
      }
      this.setWidgetI18ns(this.widgets);
      // 获取国际化数据
      this.pageStyle = this.designerApp.pageStyle;
      this.pageContext.commitRegisterStateIfNotAbsent(this.pageId, JSON.parse(JSON.stringify(designer.pageVars)));
      this.loading = false;
    },

    autofit() {
      let content = document.querySelector('.widget-vpage');
      let scaleX = window.innerWidth / this.pageStyle.width,
        scaleY = window.innerHeight / this.pageStyle.height,
        scale = Math.min(scaleX, scaleY),
        left = (window.innerWidth - this.pageStyle.width * scale) / 2,
        top = (window.innerHeight - this.pageStyle.height * scale) / 2;
      content.style.transform = `translate(${left}px,${top}px) scale(${scale})`;
      content.style.transformOrigin = 'top left';
      content.style.transition = 'transform 0.2s';
    }
  },
  beforeMount() {
    if (window.opener == null) {
      this.closeDesigner = true;
      this.loading = false;
    }
    this.fetchLocaleOptions();
  },
  mounted() {
    if (!this.closeDesigner && location.pathname.indexOf('designer/preview/') != -1) {
      this.designerApp = window.opener.$app;
      if (this.designerApp.designer.isBigScreenDesign) {
        this.isBigScreen = true;

        import('@pageAssembly/app/web/framework/vue/installBigScreenWidget.js').then(() => {
          this.init();
          this.$nextTick(() => {
            this.autofit();
          });
          let _this = this;
          window.addEventListener('resize', debounce(this.autofit, 300));
        });
      } else {
        import('@installPageWidget').then(() => {
          this.init();
        });
      }
    }
  }
};
</script>
