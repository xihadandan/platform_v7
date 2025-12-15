<!-- 主题规范 -->
<template>
  <HtmlWrapper title="主题规范">
    <a-card style="width: 100%" class="theme-specify-container">
      <template slot="title">
        {{ currentSpecify.version | versionTitle }}
      </template>
      <template slot="extra">
        <a-button type="primary" icon="save" @click="onSave(currentSpecify, refreshvVersion)">保存</a-button>
        <Drawer title="版本记录" v-if="!loading" ref="drawer">
          <a-button type="link" icon="history">版本记录</a-button>
          <template slot="content">
            <ThemeSpecifyVersion :specify="currentSpecify" ref="version" @onEditVersionDetails="onEditVersionDetails" />
          </template>
          <template slot="footer">
            <a-button icon="plus" type="primary" @click="createNewVersion">创建新版本</a-button>
            <!-- <a-button icon="import">导入</a-button> -->

            <a-button type="primary" @click="onSaveVersion">保存</a-button>
            <a-button @click="onEditVersionDetails">配置</a-button>
          </template>
        </Drawer>
      </template>
      <a-layout :hasSider="true">
        <a-layout-sider theme="light">
          <a-menu mode="inline" style="width: 195px" :defaultOpenKeys="['globalStyle']" v-model="selectedMenuKey" @select="onSelectMenu">
            <a-sub-menu key="globalStyle" title="基础样式">
              <a-menu-item key="ThemeColorSpecify" configKey="colorConfig">色彩</a-menu-item>
              <a-menu-item key="ThemeFontSpecify" configKey="fontConfig">字体</a-menu-item>
              <a-menu-item key="ThemeBorderSpecify" configKey="borderConfig">边框</a-menu-item>
              <a-menu-item key="ThemeRadiusSpecify" configKey="radiusConfig">圆角</a-menu-item>
              <a-menu-item key="ThemeSpaceSpecify" configKey="spaceConfig">间距</a-menu-item>
              <a-menu-item key="ThemeHeightSpecify" configKey="heightConfig">高度</a-menu-item>
              <a-menu-item key="ThemeShadowSpecify" configKey="shadowConfig">阴影</a-menu-item>
            </a-sub-menu>
          </a-menu>
        </a-layout-sider>
        <a-layout-content>
          <PerfectScrollbar class="layout-content-scroll">
            <a-skeleton active :loading="loading">
              <component :is="selectedMenuKey[0]" :config="specifyConfig[configKey]" />
              <a-back-top :target="backTopTarget" style="right: 30px" />
            </a-skeleton>
          </PerfectScrollbar>
        </a-layout-content>
      </a-layout>
    </a-card>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import './css/index.less';
import ThemeFontSpecify from './component/theme-font-specify.vue';
import ThemeColorSpecify from './component/theme-color-specify.vue';
import ThemeBorderSpecify from './component/theme-border-specify.vue';
import ThemeHeightSpecify from './component/theme-height-specify.vue';
import ThemeSpaceSpecify from './component/theme-space-specify.vue';
import ThemeRadiusSpecify from './component/theme-radius-specify.vue';
import ThemeShadowSpecify from './component/theme-shadow-specify.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import ThemeSpecifyVersion from './component/theme-specify-version.vue';
import { defaultThemeSpecify } from './constant';

export default {
  name: 'ThemeSpecification',
  components: {
    ThemeFontSpecify,
    ThemeColorSpecify,
    ThemeBorderSpecify,
    ThemeHeightSpecify,
    ThemeSpaceSpecify,
    ThemeRadiusSpecify,
    ThemeShadowSpecify,
    Drawer,
    ThemeSpecifyVersion
  },
  computed: {},
  filters: {
    versionTitle(v) {
      return '主题规范 ' + (v != undefined ? parseFloat(v).toFixed(1) : '');
    }
  },
  data() {
    return {
      selectedMenuKey: ['ThemeColorSpecify'],
      configKey: 'colorConfig',
      loading: true,
      specifyConfig: defaultThemeSpecify,
      currentSpecify: {}
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getEnabledSpecify().then(data => {
      if (data != null) {
        this.loading = false;
        this.currentSpecify = data;
        this.specifyConfig = JSON.parse(data.defJson);
        console.log('主题规范定义: ', this.specifyConfig);
        this.title = '主题规范 ' + parseFloat(data.version).toFixed(1);
      } else {
        // 生成默认规范
        this.saveSpecify({ version: 1, enabled: true, defJson: JSON.stringify(defaultThemeSpecify) }).then(() => {
          this.getEnabledSpecify().then(specify => {
            this.loading = false;
            this.currentSpecify = specify;
            this.specifyConfig = JSON.parse(specify.defJson);
            console.log('主题规范定义: ', this.specifyConfig);
            this.title = '主题规范 ' + parseFloat(specify.version).toFixed(1);
          });
        });
      }
    });
  },
  mounted() {},
  methods: {
    getEnabledSpecify() {
      return new Promise((resolve, reject) => {
        $axios.get('/proxy/api/theme/specify/getEnabled', {}).then(({ data }) => {
          resolve(data.data);
        });
      });
    },
    saveSpecify(formData) {
      return new Promise((resolve, reject) => {
        $axios.post('/proxy/api/theme/specify/save', formData).then(({ data }) => {
          if (data.code == 0) {
            resolve(data.data);
          }
        });
      });
    },
    onSave(formData, callback, cancleCallback) {
      let _this = this;
      this.$confirm({
        title: '提示',
        content: '【主题规范 ' + parseFloat(formData.version ? formData.version : 1).toFixed(1) + '】确认要保存吗?',
        onOk() {
          _this.$loading('保存中');
          if (!formData.newVersion) {
            formData.defJson = JSON.stringify(_this.specifyConfig);
          }
          if (formData.version == undefined) {
            formData.version = 1;
          }
          _this.saveSpecify(formData).then(uuid => {
            if (uuid) {
              _this.$message.success('保存成功');
              if (!formData.newVersion) {
                _this.currentSpecify.version = formData.version;
              }

              if (typeof callback == 'function') {
                callback.call(_this, uuid);
              }
            }
            _this.$loading(false);
          });
        },
        onCancel() {
          if (typeof cancleCallback == 'function') {
            cancleCallback.call(_this);
          }
        }
      });
    },
    backTopTarget() {
      return this.$el.querySelector('.layout-content-scroll');
    },
    onSelectMenu({ item, key, selectedKeys }) {
      this.selectedMenuKey = selectedKeys;
      this.configKey = item.$el.getAttribute('configKey');
    },
    createNewVersion() {
      this.$refs.version.switchNewVersionEdit();
    },
    onSaveVersion(currentSpecify) {
      //点击版本的保存按钮时，不会点击配置,不触发onEditVersionDetails
      let event = currentSpecify && currentSpecify.type === 'click' ? currentSpecify : undefined;
      if (!currentSpecify || currentSpecify.type === 'click') {
        currentSpecify = this.$refs.version.currentSpecify;
      }
      this.onSave(
        currentSpecify,
        uuid => {
          // 保存成功后，刷新版本列表
          if (currentSpecify.newVersion) {
            this.$refs.version.refresh(uuid, event);
          } else {
            this.$refs.version.refresh('', event);
          }
        },
        () => {
          // 如果是旧版本或者点击版本的保存，不会点击配置
          if (!currentSpecify.newVersion || event) {
            this.onEditVersionDetails({ isChange: true });
          }
        }
      );
    },
    refreshvVersion(uuid) {
      this.$refs.version.refresh('', true);
    },
    onEditVersionDetails(arg) {
      let { uuid, version, defJson, remark, enabled, newVersion } = this.$refs.version.currentSpecify;
      if (arg && arg.isChange) {
        // 直接切换，不需要保存
        this.currentSpecify.uuid = uuid;
        this.currentSpecify.version = version;
        this.currentSpecify.defJson = defJson;
        this.currentSpecify.remark = remark;
        this.currentSpecify.enabled = enabled;
        this.specifyConfig = JSON.parse(defJson);
        if (!arg.visible) {
          this.$refs.drawer.visible = false;
        }
      } else {
        // 新版本，先保存新版本，再展现新版本配置
        // 旧版本，先保存配置页版本的配置，再展现选中版本
        // 保存成功后会再次进入该方法，直接切换
        if (newVersion) {
          this.onSaveVersion();
        } else {
          // 同个版本，就不需要保存配置页版本配置了
          if (this.currentSpecify.uuid != uuid) {
            this.onSaveVersion(this.currentSpecify);
          } else {
            this.onEditVersionDetails({ isChange: true });
          }
        }
      }
    }
  }
};
</script>
