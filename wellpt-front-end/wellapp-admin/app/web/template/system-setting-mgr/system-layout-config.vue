<template>
  <div class="system-layout-config-container">
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <div class="sub-title">导航风格</div>
    <div style="margin-bottom: 20px; width: 620px">
      <MenuLayoutSelect :config="setting.layoutConf" :siderToTop="siderToTop" />
    </div>
    <a-form class="system-layout-setting-form" :colon="false">
      <!-- 菜单在头部情况下,支持菜单栏独立展示 -->
      <a-form-item label="独立菜单栏" v-if="setting.layoutConf.menuPosition == 'header'">
        <a-switch v-model="setting.layoutConf.topMenuBar" @change="onChangeTopMenuBar"></a-switch>
      </a-form-item>

      <template
        v-if="
          setting.layoutConf.menuPosition == 'sider' || (setting.layoutConf.menuPosition == 'header' && setting.layoutConf.appMenusToSider)
        "
      >
        <a-form-item label="左侧导航置顶">
          <a-switch v-model="siderToTop" @change="changeSiderToTop"></a-switch>
        </a-form-item>
      </template>

      <a-form-item>
        <template #label>
          {{ setting.layoutConf.topMenuBar && setting.layoutConf.menuPosition == 'header' ? '横幅和菜单固定' : '横幅固定' }}
        </template>
        <a-switch v-model="setting.layoutConf.headerFixed"></a-switch>
      </a-form-item>
      <template
        v-if="
          setting.layoutConf.menuPosition == 'sider' || (setting.layoutConf.menuPosition == 'header' && setting.layoutConf.appMenusToSider)
        "
      >
        <a-form-item label="显示左导航折叠按钮">
          <a-switch v-model="setting.layoutConf.menuCollapseBtnVisible"></a-switch>
          <!-- <a-radio-group v-model="setting.layoutConf.menuCollapseBtnDisplayType" button-style="solid">
            <a-radio-button value="hidden">不显示</a-radio-button>
            <a-radio-button value="buttonDisplay">显示按钮</a-radio-button>
            <a-radio-button value="topDisplay">显示在导航顶部</a-radio-button>
            <a-radio-button value="bottomDisplay">显示在导航底部</a-radio-button>
          </a-radio-group> -->
        </a-form-item>
        <a-form-item label="左导航默认状态">
          <a-radio-group v-model="setting.layoutConf.siderMenuCollapseType" button-style="solid">
            <a-radio-button value="unCollapsed">展开</a-radio-button>
            <a-radio-button value="collapsed">折叠</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="左导航风格">
          <a-radio-group v-model="setting.layoutConf.siderBgColorType" button-style="solid">
            <a-radio-button value="light">浅色</a-radio-button>
            <a-radio-button value="dark">深色</a-radio-button>
            <a-radio-button value="primary-color">主题色</a-radio-button>
          </a-radio-group>
        </a-form-item>
      </template>
      <a-form-item label="横幅风格">
        <a-radio-group v-model="setting.layoutConf.headerBgColorType" button-style="solid">
          <a-radio-button value="light">浅色</a-radio-button>
          <a-radio-button value="primary-color">主题色</a-radio-button>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="logo显示位置" v-if="setting.layoutConf.layoutType == 'siderTopMiddleBottom'">
        <a-radio-group v-model="setting.layoutConf.logoPosition" button-style="solid">
          <a-radio-button value="header">横幅</a-radio-button>
          <a-radio-button value="sider">左导航</a-radio-button>
        </a-radio-group>
      </a-form-item>

      <a-form-item v-if="setting.layoutConf.menuPosition == 'header'">
        <template #label>
          {{ setting.layoutConf.topMenuBar ? '菜单水平位置' : '导航水平位置' }}
        </template>
        <a-radio-group v-model="setting.layoutConf.horizontalAlign" button-style="solid">
          <a-radio-button value="left">居左</a-radio-button>
          <a-radio-button value="center">居中</a-radio-button>
          <a-radio-button value="right">居右</a-radio-button>
        </a-radio-group>
      </a-form-item>
      <a-form-item v-if="setting.layoutConf.menuPosition == 'header'">
        <template #label>
          {{ setting.layoutConf.topMenuBar ? '菜单交互' : '导航交互' }}
        </template>
        <a-radio-group v-model="setting.layoutConf.horizontalExpandType" button-style="solid">
          <a-radio-button value="topDrawer">全部展开</a-radio-button>
          <a-radio-button value="dropdown">单列展开</a-radio-button>
        </a-radio-group>
      </a-form-item>
      <a-form-item>
        <template slot="label">用户个性化设置</template>
        <a-switch v-model="setting.userLayoutDefinable"></a-switch>
      </a-form-item>

      <div class="sub-title">导航标签页</div>
      <a-form-item label="在动态导航标签页中打开页面">
        <a-switch v-model="setting.layoutConf.pageToTabs"></a-switch>
      </a-form-item>

      <div class="sub-title">页面底部</div>
      <a-form-item label="页面底部页脚显示">
        <a-radio-group v-model="setting.layoutConf.footerDisplayType" button-style="solid">
          <a-radio-button value="hidden">不显示</a-radio-button>
          <a-radio-button value="indexShow">仅首页显示</a-radio-button>
          <a-radio-button value="visible">全部页面显示</a-radio-button>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </div>
</template>
<style lang="less">
.system-layout-config-container {
  .sub-title {
    font-weight: bold;
    font-size: var(--w-font-size-lg);
    line-height: 32px;
    color: var(--w-text-color-dark);
    margin-bottom: 24px;
  }
  .system-layout-setting-form {
    .ant-form-item {
      display: flex;
      margin-bottom: 10px;

      > .ant-form-item-label {
        min-width: 220px;
        text-align: left;
        color: var(--w-text-color-dark);
      }
    }
  }
}
</style>
<script type="text/babel">
import MenuLayoutSelect from '@pageAssembly/app/web/page/product-center/component/manager/menu-layout-select.vue';
import ProductPageSetting from '@pageAssembly/app/web/page/product-center/component/manager/product-page-setting.vue';
import { getElSpacingForTarget } from '@framework/vue/utils/util';

export default {
  name: 'SystemLayoutConfig',
  props: {},
  components: { MenuLayoutSelect },
  computed: {},
  data() {
    return {
      siderToTop: false,
      loading: true,
      setting: {
        userLayoutDefinable: false,
        layoutConf: {
          layoutType: 'siderTopMiddleBottom', // 布局类型:  topMiddleSiderBottom \ siderTopMiddleBottom \ topMiddleBottom
          menuPosition: 'sider', // 导航位置: sider \ header
          appMenusToSider: false, // 模块的导航项渲染到侧边区域
          headerFixed: true, // 顶部固定
          menuCollapseBtnVisible: false,
          menuCollapseBtnDisplayType: 'hidden',
          siderMenuCollapseType: 'unCollapsed',
          topMenuBar: false,
          pageToTabs: true,
          horizontalAlign: 'left',
          footerDisplayType: 'indexShow',
          logoPosition: 'sider',
          siderBgColorType: 'dark',
          headerBgColorType: 'light',
          horizontalExpandType: 'dropdown'
        }
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    if (this._$SYSTEM_ID) {
      this.loadData();
    } else {
      this.loading = false;
    }
    let $parent = this.$root.$el.classList.contains('preview') ? this.$root.$el : this.$el.closest('.widget-vpage');
    if ($parent) {
      const { maxHeight, totalBottom, totalNextSibling } = getElSpacingForTarget(this.$el, $parent);
      this.$el.style.cssText = `overflow-y:auto; height:${maxHeight}px`;
    }
  },
  methods: {
    loadData() {
      this.fetchSystemPageSetting().then(d => {
        if (d == null) {
          // 初始化数据
          $axios
            .post(`/proxy/api/system/saveAppSystemPageSetting`, {
              layoutConf: JSON.stringify(ProductPageSetting.methods.defaultLayoutConf()),
              tenant: this._$USER.tenantId,
              system: this._$SYSTEM_ID
            })
            .then(() => {
              this.loadData();
            });
          return;
        }
        this.setting.uuid = d.uuid;
        this.setting.userLayoutDefinable = d.userLayoutDefinable;
        for (let key in d.layoutConf) {
          this.$set(this.setting.layoutConf, key, d.layoutConf[key]);
          if (key == 'menuCollapseBtnDisplayType' && !d.layoutConf.hasOwnProperty('menuCollapseBtnVisible')) {
            this.$set(this.setting.layoutConf, 'menuCollapseBtnVisible', d.layoutConf[key] !== 'hidden');
          }
        }
        this.loading = false;
      });
    },
    saveLayoutConf() {
      this.$loading();
      $axios
        .post(`/proxy/api/system/updateAppSystemPageSettingLayoutConf`, {
          layoutConf: JSON.stringify(this.setting.layoutConf),
          userLayoutDefinable: this.setting.userLayoutDefinable,
          uuid: this.setting.uuid
        })
        .then(({ data }) => {
          this.$loading(false);
          this.$message.success('保存成功');
          $axios
            .get(`/api/cache/deleteByKey`, {
              params: {
                key: `TENANT_SYSTEM_INFO:${this._$USER.tenantId}:${this._$SYSTEM_ID}`
              }
            })
            .then(({ data }) => {})
            .catch(error => {});
        })
        .catch(error => {
          this.$loading(false);
          this.$message.success('保存失败');
        });
    },
    changeSiderToTop(checked) {
      this.setting.layoutConf.layoutType = checked ? 'siderTopMiddleBottom' : 'topMiddleSiderBottom';
      if (!checked) {
        // 布局变更，logo位置也要随之变化
        this.setting.layoutConf.logoPosition = 'header';
      }
    },
    onChangeTopMenuBar(checked) {
      if (this.setting.layoutConf.appMenusToSider && checked) {
        this.setting.layoutConf.layoutType = 'topMiddleSiderBottom';
      }
    },
    fetchSystemPageSetting() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/system/getTenantSystemPageSetting`, {
            params: {
              system: this._$SYSTEM_ID
            }
          })
          .then(({ data }) => {
            if (data.data) {
              data.data.layoutConf = JSON.parse(data.data.layoutConf);
              resolve(data.data);
            } else {
              resolve(null);
            }
          })
          .catch(error => {});
      });
    }
  },
  watch: {
    'setting.layoutConf': {
      deep: true,
      handler(v) {
        for (let key in this.setting.layoutConf) {
          if (key == 'layoutType') {
            this.siderToTop = this.setting.layoutConf.layoutType == 'siderTopMiddleBottom';
          }
        }
      }
    }
  },
  META: {
    method: {
      saveLayoutConf: '保存导航布局配置'
    }
  }
};
</script>
