<template>
  <HtmlWrapper title="登录">
    <a-layout class="widget-design-layout">
      <a-layout-header class="widget-design-header">
        <a-row>
          <a-col :span="12">
            <a-avatar
              shape="square"
              :size="24"
              style="background-color: #fff; color: var(--w-primary-color); margin-right: 8px; margin-top: -4px"
            >
              <Icon type="pticon iconfont icon-ptkj-denglu-toubu" style="font-weight: normal; vertical-align: top; font-size: 16px" />
            </a-avatar>
            <span style="font-weight: bold; font-size: var(--w-font-size-lg)">登录页设置</span>
          </a-col>
          <a-col :span="12" :style="{ textAlign: 'right' }">
            <a-button type="icon" class="icon-only" :title="帮助" ghost><Icon type="pticon iconfont icon-ptkj-bangzhufankui" /></a-button>
            <a-button @click="previewLogin" ghost style="margin-right: 8px">预览</a-button>
            <a-button @click="onRestLoginTemplateConf" ghost style="margin-right: 8px">重置</a-button>
            <a-button @click="onSaveConfig" type="primary" ghost>保存</a-button>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout :hasSider="true">
        <a-layout-sider theme="light" :width="290">
          <div class="widget-design-select-sider">
            <a-tabs tab-position="left" size="small" class="widget-select-tabs" activeKey="1">
              <a-tab-pane key="1">
                <span slot="tab">
                  <Icon type="pticon iconfont icon-ptkj-kapian-01" />
                  <br />
                  模板
                </span>

                <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1">
                  <a-collapse-panel key="1">
                    <template slot="header">
                      <label>模板</label>
                    </template>

                    <PerfectScrollbar style="height: calc(100vh - 130px)">
                      <ul class="template-ul login-template-ul">
                        <li
                          v-for="(tpt, i) in loginTemplates"
                          :key="'loginTpt_' + i"
                          :class="['widget-select-item', tpt.type == loginTemplateSelected ? 'selected' : '']"
                          @click="onSelectLoginTemplate(tpt)"
                        >
                          <img :src="tpt.thumbnail" />
                        </li>
                      </ul>
                    </PerfectScrollbar>
                  </a-collapse-panel>
                </a-collapse>
              </a-tab-pane>
            </a-tabs>
          </div>
        </a-layout-sider>
        <a-layout-content id="design-main" style="padding: 20px; overflow: hidden; height: calc(100vh - 52px)" class="login-design-main">
          <div :style="designMainStyle">
            <component v-if="loginTemplateSelected" :is="loginTemplateSelected" :config="loginTemplateConfig" />
          </div>
        </a-layout-content>
        <a-layout-sider theme="light" :width="rightSiderCollapsed ? 0 : 378">
          <a-icon
            type="double-right"
            :class="['right-collapse', rightSiderCollapsed ? 'collapsed' : '']"
            @click.native.stop="onClickCollapse"
            :title="rightSiderCollapsed ? '点击展开' : '点击收缩'"
          />
          <component v-if="loginTemplateSelected" :is="loginTemplateSelected + 'Config'" :config="loginTemplateConfig" :key="configKey" />
        </a-layout-sider>
      </a-layout>
    </a-layout>
  </HtmlWrapper>
</template>
<style lang="less">
.login-template-ul {
  list-style: none;
  padding: 0px;
  li {
    cursor: pointer !important;
    width: 100% !important;
    margin-right: 0px !important;
    height: 124px !important;
    padding: 12px !important;
    background-color: var(--w-fill-color-light);
    margin: 8px 0px;
    border-radius: var(--w-border-radius-2);
    border: 1px solid var(--w-border-color-light);
    &.selected {
      border: 2px solid var(--w-primary-color) !important;
    }
    > img {
      width: 100%;
      height: 99px;
      object-fit: scale-down;
    }
  }
}
.login-design-config {
  --w-tabs-radio-button-solid-border-radius: 4px;
  .ant-drawer-body {
    padding: 0 0 12px;
  }
  .ant-tabs-bar {
    margin: 0 12px;
  }
  .ant-collapse {
    background: transparent;
    .ant-collapse-header {
      padding: 4px 0px 4px 12px;
      line-height: 32px;
      font-weight: bold;
      color: var(--w-text-color-darker);
      font-size: var(--w-font-size-base);
    }
    .ant-collapse-item:last-child {
      border-bottom: 0;
    }
  }
  .login-bg-image-container {
    &.simple {
      .tree-row:not(:first-child) {
        display: none;
      }
    }
    .tree-row {
      outline: 1px solid #e8e8e8;
      border-radius: 4px;
      margin-bottom: 15px;
      img {
        width: 72px;
        height: 40px;
        object-fit: scale-down;
      }
    }
  }
  .login-page-logo {
    width: 100%;
    height: 150px;
    object-fit: scale-down;
    outline: 1px solid #e8e8e8;
    border-radius: 2px;
    padding: 5px;
    margin-bottom: 10px;
  }

  .transparent-bg {
    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABGdBTUEAALGPC/xhBQAAAFpJREFUWAntljEKADAIA23p6v//qQ+wfUEcCu1yriEgp0FHRJSJcnehmmWm1Dv/lO4HIg1AAAKjTqm03ea88zMCCEDgO4HV5bS757f+7wRoAAIQ4B9gByAAgQ3pfiDmXmAeEwAAAABJRU5ErkJggg==')
      0% 0% / 28px;
  }

  .image-upload-row {
    outline: 1px solid #e8e8e8;
    padding: 5px 15px;
    border-radius: 2px;
    > .ant-col:last-child {
      align-self: center;
      text-align: right;
    }
  }
  .tip-div {
    font-size: var(--w-font-size-sm);
    color: var(--w-text-color-light);
    background-color: var(--w-gray-color-2);
    padding: var(--w-padding-2xs);
    border-radius: 4px;
    line-height: var(--w-line-height);
  }
  .unit-span {
    margin-left: -50px;
    background: var(--w-gray-color-2);
    padding: 0px 5px;
    border-radius: 2px;
    font-size: 12px;
  }
}
</style>
<script type="text/babel">
import '../../assets/css/design.less';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import '../login/component/index';
// import defaultThumbnail from '../../login-widget/login-basic/thumbnail.png';
import { orderBy } from 'lodash';
import { addWindowResizeHandler, generateId } from '@framework/vue/utils/util';
import { customFileUploadRequest } from '@framework/vue/utils/function';

import { toPng, toSvg } from 'html-to-image';

export default {
  name: '',
  props: {},
  components: { Drawer },
  computed: {},
  data() {
    return {
      loginTemplateSelected: undefined,
      loginTemplateConfig: {},
      loginTemplates: [],
      defaultThumbnail: undefined,
      configKey: 'config',
      history: {},
      rightSiderCollapsed: false,
      designMainStyle: { position: 'relative' }
    };
  },
  provide() {
    return {
      designMode: true
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchLoginDefs(this.uuid).then(data => {
      if (data) {
        let defJson = JSON.parse(data.defJson);
        this.system = data.system;
        this.tenant = data.tenant;
        this.loginTemplateSelected = defJson.type;
        this.loginTemplateConfig = defJson.config;
      }
    });
    this.getLoginTemplates();
  },
  mounted() {
    const _self = this;
    this.previewScale();
    addWindowResizeHandler(() => {
      _self.$nextTick(() => {
        _self.previewScale();
      });
    });
  },
  methods: {
    onClickCollapse() {
      this.rightSiderCollapsed = !this.rightSiderCollapsed;
      setTimeout(() => {
        this.previewScale();
      }, 200);
    },
    dataUrlToFile(dataURL, fileName) {
      // 将base64的数据部分提取出来
      const bytes = window.atob(dataURL.split(',')[1]);

      // 创建一个ArrayBuffer，并用它创建一个Blob对象
      const mimeString = dataURL.split(',')[0].match(/:(.*?);/)[1];
      const ab = new ArrayBuffer(bytes.length);
      const ia = new Uint8Array(ab);
      for (let i = 0; i < bytes.length; i++) {
        ia[i] = bytes.charCodeAt(i);
      }
      const blob = new Blob([ab], { type: mimeString });

      // 创建一个File对象
      const file = new File([blob], fileName, { type: mimeString });

      return file;
    },
    onSaveThumbnail() {
      let _this = this;
      setTimeout(() => {
        toPng(document.querySelector('#design-main > div > div'), { pixelRatio: 0.5 })
          .then(function (dataUrl) {
            let file = _this.dataUrlToFile(dataUrl, 'thumbnail.png', '');
            customFileUploadRequest({
              file,
              folder: {
                folderID: _this.uuid,
                purpose: 'thumbnail',
                popFolderFile: true
              }
            }).then(res => {
              console.log(res);
              // var link = document.createElement('a');
              // link.download = 'my-image-name.jpeg';
              // link.href = `/proxy-repository/repository/file/mongo/download?fileID=${res.fileID}`;
              // link.click();
              // _this.$message.success('保存成功');
            });
          })
          .catch(function (error) {
            console.error('oops, something went wrong!', error);
          });
      }, 500); // 延迟执行（等待抽屉关闭后，避免图片包含抽屉)
    },
    onRestLoginTemplateConf() {
      this.configKey = generateId();
      this.loginTemplateConfig =
        window.Vue.options.components[this.loginTemplateSelected + 'Config'].options.methods.generateDefaultConfig();
    },
    previewLogin() {
      let id = generateId();
      sessionStorage.setItem(
        `${id}`,
        JSON.stringify({
          type: this.loginTemplateSelected,
          config: this.loginTemplateConfig
        })
      );
      window.open(`/login-design-preview/${id}`, '_blank');
    },
    onSaveConfig() {
      this.$loading('保存中');
      let submitData = {
        uuid: this.uuid
      };
      submitData.defJson = JSON.stringify({
        type: this.loginTemplateSelected,
        config: this.loginTemplateConfig
      });
      this.$nextTick(() => {
        $axios
          .post(`/proxy/api/system/updateLoginPageDefJson`, submitData)
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('保存成功');
              this.$loading(false);
              this.clearLoginPageCache();
              this.onSaveThumbnail();
            }
          })
          .catch(error => {
            this.$loading(false);
          });
      });
    },

    clearLoginPageCache() {
      if (this.system) {
        $axios
          .get(`/api/cache/deleteByKey`, {
            params: {
              key: `${this.system}:${this.tenant}:systemLoginPagePolicy`
            }
          })
          .then(({ data }) => {})
          .catch(error => {});
      }
    },
    getContainer() {
      return document.body; //this.$el.querySelector('#design-main').querySelector('div');
    },
    onSelectLoginTemplate(item) {
      // 记录变更前历史
      if (this.loginTemplateSelected) {
        this.history[this.loginTemplateSelected] = this.loginTemplateConfig;
      }
      if (this.history[item.type]) {
        this.loginTemplateConfig = this.history[item.type];
      } else {
        this.loginTemplateConfig = window.Vue.options.components[item.type + 'Config'].options.methods.generateDefaultConfig();
        this.history[item.type] = this.loginTemplateConfig;
      }
      this.loginTemplateSelected = item.type;
    },
    getLoginTemplates() {
      let components = window.Vue.options.components,
        list = [];
      for (let k in components) {
        let options = components[k].options;
        if (options && options.name.startsWith('Login') && options.__file.indexOf('login-widget') && options.__file.endsWith('index.vue')) {
          list.push({
            type: options.name,
            thumbnail: options.thumbnail || this.defaultThumbnail,
            order: options.order
          });
        }
      }

      this.loginTemplates = orderBy(
        list,
        [
          o => {
            return parseInt(o.order);
          }
        ],
        ['asc']
      );
    },
    fetchLoginDefs(uuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/system/getAppSystemLoginPageDefinition`, { params: { uuid } })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },
    previewScale() {
      let containerWidth = document.body.querySelector('#design-main').clientWidth - 40;
      let containerHeight = document.body.querySelector('#design-main').clientHeight - 40;
      let windowWidth = 1920;
      let windowHeight = 920;
      let scale = containerWidth / windowWidth;
      let marginLR = (windowWidth - containerWidth) / 2;
      let marginTB = (windowHeight - containerHeight) / 2;

      this.$set(this, 'designMainStyle', {
        position: 'relative',
        transform: `scale(${scale})`,
        width: '1920px',
        height: '920px',
        marginTop: `-${marginTB}px`,
        marginLeft: `-${marginLR}px`
      });
    }
  }
};
</script>
