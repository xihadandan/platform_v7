<template>
  <a-tabs class="product-page-setting" tabPosition="left">
    <a-tab-pane key="loginPage">
      <span slot="tab">
        <Icon type="pticon iconfont icon-ptkj-denglu-toubu" />
        登录页面
      </span>
      <PerfectScrollbar style="height: calc(100vh - 250px)">
        <ProdVersionLogin :prod-version-uuid="uuid" :prod-id="prodId" />
      </PerfectScrollbar>
    </a-tab-pane>
    <a-tab-pane key="layoutSet">
      <span slot="tab">
        <Icon type="pticon iconfont icon-ptkj-zhagebuju" />
        导航布局
      </span>
      <div class="product-nav-setting">
        <a-alert
          message="已初始化默认导航布局配置, 保存后生效"
          banner
          type="info"
          v-if="remindSaveLayoutConf"
          style="margin-top: var(--w-padding-xs); margin-right: 20px"
        />
        <a-tabs defaultActiveKey="pc" class="setting-pane-tabs">
          <a-tab-pane v-for="tab in navTabs" :key="tab.key" :tab="tab.title" :forceRender="tab.key == 'pc' ? true : false">
            <PerfectScrollbar style="height: calc(100vh - 340px)">
              <div v-if="tab.key == 'pc'">
                <div class="sub-title">导航风格</div>
                <div style="margin-bottom: 20px; width: 620px">
                  <MenuLayoutSelect :config="setting.layoutConf" :siderToTop="siderToTop" />
                </div>
                <a-form class="setting-form" :colon="false">
                  <!-- 菜单在头部情况下,支持菜单栏独立展示 -->
                  <a-form-item label="独立菜单栏" v-if="setting.layoutConf.menuPosition == 'header'">
                    <a-switch v-model="setting.layoutConf.topMenuBar" @change="onChangeTopMenuBar"></a-switch>
                  </a-form-item>

                  <template
                    v-if="
                      setting.layoutConf.menuPosition == 'sider' ||
                      (setting.layoutConf.menuPosition == 'header' && setting.layoutConf.appMenusToSider)
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
                      setting.layoutConf.menuPosition == 'sider' ||
                      (setting.layoutConf.menuPosition == 'header' && setting.layoutConf.appMenusToSider)
                    "
                  >
                    <a-form-item label="显示左导航折叠按钮">
                      <a-switch v-model="setting.layoutConf.menuCollapseBtnVisible"></a-switch>
                      <!-- <a-radio-group v-model="setting.layoutConf.menuCollapseBtnDisplayType" button-style="solid">
                        <a-radio-button value="hidden">不显示</a-radio-button>
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
              <div v-else>
                <a-empty :image="simpleImage" />
              </div>
            </PerfectScrollbar>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-tab-pane>
    <a-tab-pane key="prodPage" :forceRender="true">
      <span slot="tab">
        <Icon type="pticon iconfont icon-szgy-zhuye" />
        系统首页
      </span>
      <PerfectScrollbar style="height: calc(100vh - 250px)">
        <ProductPageList
          :prod-id="prodId"
          :version-id="versionId"
          @pageListChange="onPageListChange"
          :versionUuid="uuid"
          :setting="setting"
        />
      </PerfectScrollbar>
    </a-tab-pane>
    <a-tab-pane key="themeSet">
      <span slot="tab">
        <Icon type="pticon iconfont icon-ptkj-zhutifengge" />
        主题风格
      </span>
      <PerfectScrollbar style="height: calc(100vh - 250px)">
        <ProductPageTheme :version-id="versionId" :versionUuid="uuid" :setting="setting" :pages="pages" v-if="pageLoaded" />
      </PerfectScrollbar>
    </a-tab-pane>
    <a-tab-pane key="urlSet">
      <span slot="tab">
        <Icon type="pticon iconfont icon-ptkj-tijiaofabufasong" />
        访问设置
      </span>
      <a-tabs class="setting-pane-tabs">
        <a-tab-pane v-for="tab in deviceTabs" :key="tab.key" :tab="tab.title">
          <PerfectScrollbar style="height: calc(100vh - 315px)">
            <div class="sub-title">默认访问地址</div>
            <a-form class="setting-form" :colon="false" :wrapper-col="{ style: { width: '100%' } }">
              <a-form-item label="默认访问地址">
                <a-button type="link" @click="clickToOpenUrl(`/sys/${prodId}/index`)">
                  {{ getLocationOrigin() }}/sys/{{ prodId }}/index
                </a-button>
                <a-button
                  type="link"
                  icon="copy"
                  title="复制"
                  @click="e => onClickCopy(e, `${getLocationOrigin()}/sys/${prodId}/index`)"
                ></a-button>
                <!-- <div style="display: flex; width: 100%">
                  <a-select v-model="setting[tab.key == 'pc' ? 'pcIndexUrl' : 'mobileIndexUrl']" style="width: 246px">
                    <a-select-option
                      v-for="(opt, j) in tab.key == 'pc' ? pcPageOptions : mobilePageOptions"
                      :value="opt.url"
                      :key="'defaultUrl_' + i + '_' + j"
                    >
                      {{ opt.label }}
                    </a-select-option>
                  </a-select>
                  <a-input
                    style="margin-left: 15px; width: 550px"
                    v-model="setting[tab.key == 'pc' ? 'pcIndexUrl' : 'mobileIndexUrl']"
                    :readOnly="true"
                    v-if="setting[tab.key == 'pc' ? 'pcIndexUrl' : 'mobileIndexUrl']"
                  >
                    <template slot="addonBefore">
                      {{ getLocationOrigin() }}
                    </template>
                    <a-button
                      size="small"
                      type="link"
                      icon="copy"
                      slot="suffix"
                      @click="e => onClickCopy(e, getLocationOrigin() + setting[tab.key == 'pc' ? 'pcIndexUrl' : 'mobileIndexUrl'])"
                    />
                  </a-input>
                  <a-button
                    type="link"
                    icon="link"
                    @click="clickToOpenUrl(getLocationOrigin() + setting[tab.key == 'pc' ? 'pcIndexUrl' : 'mobileIndexUrl'])"
                  >
                    前往访问
                  </a-button>
                </div> -->
              </a-form-item>
            </a-form>

            <!-- <div class="sub-title">
              <label>匿名访问</label>
              <a-switch :checked="setting.deviceAnon.indexOf('1') == i" @change="e => onChangeDeviceAnon(e, i)" />
            </div>
            <div v-show="setting.deviceAnon.indexOf('1') == i" class="anon-url-panel">
              <template v-for="(url, j) in anonUrls">
                <a-row type="flex" :key="'anonUrl_' + j" v-if="url.deviceType == tab.key.toUpperCase()" class="row-item">
                  <a-col flex="auto">
                    <a-form class="setting-form" :colon="false" :wrapper-col="{ style: { width: '100%' } }">
                      <a-form-item label="类型">
                        <a-radio-group size="small" v-model="url.type" button-style="solid">
                          <a-radio-button value="PAGE">指定页面</a-radio-button>
                          <a-radio-button value="URL">地址</a-radio-button>
                        </a-radio-group>
                      </a-form-item>
                      <a-form-item label="指定页面" v-if="url.type === 'PAGE'">
                        <a-select
                          v-model="url.pageId"
                          style="width: 246px"
                          :options="tab.key == 'pc' ? pcPageOptions : mobilePageOptions"
                        ></a-select>
                      </a-form-item>
                      <a-form-item label="地址" v-if="url.type === 'URL'">
                        <a-input style="width: 550px" v-model="url.url">
                          <template slot="addonBefore">{{ getLocationOrigin() }}/</template>
                        </a-input>
                      </a-form-item>
                    </a-form>
                  </a-col>
                  <a-col flex="70px">
                    <a-button type="link" size="small" icon="delete" @click="removeAnonUrl(j)">删除</a-button>
                  </a-col>
                </a-row>
              </template>
              <a-button icon="plus" @click="addAnonUrl(tab.key)" :block="true">添加</a-button>
            </div> -->
          </PerfectScrollbar>
        </a-tab-pane>
      </a-tabs>
    </a-tab-pane>
    <a-tab-pane key="otherSet">
      <span slot="tab">
        <Icon type="pticon iconfont icon-ptkj-shezhi" />
        其他设置
      </span>
      <a-tabs defaultActiveKey="pc" class="setting-pane-tabs">
        <a-tab-pane v-for="tab in settingTabs" :key="tab.key" :tab="tab.title" :forceRender="tab.key == 'pc' ? true : false">
          <PerfectScrollbar style="height: calc(100vh - 340px)">
            <div v-if="tab.key == 'pc'">
              <a-form class="setting-form" :colon="false">
                <a-form-item label="系统显示名称">
                  <a-input v-model="setting.title" style="width: 400px" />
                </a-form-item>
                <a-form-item label="系统icon" :wrapper-col="{ style: { width: '100%' } }">
                  <a-upload
                    name="thumbnail"
                    list-type="picture-card"
                    :file-list="[]"
                    :show-upload-list="false"
                    :before-upload="e => beforeUpload(e, 'systemFavicon')"
                    :customRequest="e => customRequest(e, 'systemFavicon')"
                    style="float: left; width: auto; position: relative; z-index: 1"
                  >
                    <img v-if="setting.icon" :src="setting.icon" alt="avatar" style="width: 66px; height: 66px; object-fit: scale-down" />
                    <div v-else>
                      <a-icon :type="uploading ? 'loading' : 'plus'" />
                      <div class="ant-upload-text">点击上传</div>
                    </div>
                    <a-button
                      style="position: absolute; right: 10px; top: 0px"
                      v-show="setting.icon != undefined"
                      type="link"
                      size="small"
                      @click.stop="removeIcon"
                      title="删除"
                    >
                      <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    </a-button>
                  </a-upload>
                  <div style="padding-top: 70px; color: #999999; font-size: 12px">
                    ( 在浏览器标签页头部显示的icon, 建议格式: .icon 或者 .png , 大小: 66x66
                    <label v-if="naturalIconSize != undefined && naturalIconSize.length == 2">
                      ,
                      <label style="color: red">当前实际大小: {{ naturalIconSize[0] }}x{{ naturalIconSize[1] }}</label>
                    </label>
                    )
                  </div>
                </a-form-item>

                <a-form-item label="系统地图">
                  <a-switch v-model="setting.mapEnabled" />
                </a-form-item>
              </a-form>
            </div>
            <div v-else>
              <a-empty :image="simpleImage" />
            </div>
          </PerfectScrollbar>
        </a-tab-pane>
      </a-tabs>
    </a-tab-pane>
  </a-tabs>
</template>
<script type="text/babel">
import ProductPageList from './product-page-list.vue';
import ProdVersionLogin from './product-version-login.vue';
import MenuLayoutSelect from './menu-layout-select.vue';
import ProductPageTheme from './product-page-theme.vue';
import { copyToClipboard } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { debounce, orderBy } from 'lodash';
import { Empty } from 'ant-design-vue';

export default {
  name: 'ProductPageSetting',
  inject: ['pageContext'],
  props: {
    uuid: String,
    prodId: String,
    versionId: String,
    setting: Object,
    anonUrls: Array
  },
  components: { ProductPageList, MenuLayoutSelect, Modal, ProdVersionLogin, ProductPageTheme },
  computed: {
    layoutHasSider() {
      return (
        this.setting &&
        this.setting.layoutConf &&
        this.setting.layoutConf.layoutType &&
        this.setting.layoutConf.layoutType.toLowerCase().indexOf('sider') != -1
      );
    },
    pcSelectedThemeClass() {}
  },

  data() {
    return {
      uploading: false,
      naturalIconSize: undefined,
      deviceTabs: [
        { key: 'pc', title: '桌面端' },
        { key: 'mobile', title: '移动端' }
      ],
      navTabs: [
        { key: 'pc', title: '桌面端' }
        // { key: 'mobile', title: '移动端' }
      ],
      settingTabs: [
        { key: 'pc', title: '桌面端' }
        // { key: 'mobile', title: '移动端' }
      ],
      pages: { pc: [], mobile: [] },
      pcPageOptions: [],
      mobilePageOptions: [],
      siderToTop: true,
      pageLoaded: false,
      remindSaveLayoutConf: false,
      simpleImage: Empty.PRESENTED_IMAGE_SIMPLE
    };
  },
  beforeCreate() {},
  created() {
    if (this.setting.theme == undefined) {
      this.$set(this.setting, 'theme', { pc: { theme: [], defaultTheme: undefined }, mobile: { theme: [], defaultTheme: undefined } });
    }
  },
  beforeMount() {
    this.fetchVersionSetting().then(data => {
      this.setting.prodVersionUuid = this.uuid;
      let _this = this;
      this.pageContext.handleEvent(`${this.setting.prodVersionUuid}:saveVersionSuccess`, function () {
        _this.remindSaveLayoutConf = false;
      });
      if (data) {
        this.setting.title = data.title;
        this.setting.icon = data.icon;
        this.$set(
          this.setting,
          'theme',
          data.theme
            ? JSON.parse(data.theme)
            : { pc: { theme: [], defaultTheme: undefined }, mobile: { theme: [], defaultTheme: undefined } }
        );

        this.setting.mapEnabled = data.mapEnabled;
        this.setting.loginConf = data.loginConf ? JSON.parse(data.loginConf) : {};
        if (data.layoutConf == null) {
          this.remindSaveLayoutConf = true;
          this.setting.layoutConf = this.defaultLayoutConf();
        } else {
          this.setting.layoutConf = JSON.parse(data.layoutConf);
          if (!this.setting.layoutConf.hasOwnProperty('menuCollapseBtnVisible')) {
            this.$set(this.setting.layoutConf, 'menuCollapseBtnVisible', this.setting.layoutConf.menuCollapseBtnDisplayType !== 'hidden');
          }
        }
        this.setting.deviceAnon = data.deviceAnon ? data.deviceAnon : '00';
        this.setting.uuid = data.uuid;
        this.setting.pcIndexUrl = data.pcIndexUrl;
        this.setting.mobileIndexUrl = data.mobileIndexUrl;
        this.siderToTop = this.setting.layoutConf.layoutType == 'siderTopMiddleBottom';
      } else {
        this.setting.theme = {
          pc: { theme: [], defaultTheme: undefined },
          mobile: { theme: [], defaultTheme: undefined }
        };
        // 默认配置
        this.$set(this.setting, 'layoutConf', this.defaultLayoutConf());
      }
    });
  },
  mounted() {},
  methods: {
    defaultLayoutConf() {
      return {
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
      };
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
    addAnonUrl(type) {
      this.anonUrls.push({
        deviceType: type.toUpperCase(),
        type: 'PAGE',
        pageId: undefined,
        url: undefined
      });
    },
    removeAnonUrl(i) {
      this.anonUrls.splice(i, 1);
    },
    onChangeDeviceAnon(ck, i) {
      let parts = this.setting.deviceAnon.split('');
      parts[i] = ck ? '1' : '0';
      this.setting.deviceAnon = parts.join('');
    },
    clickToOpenUrl(url) {
      window.open(url, '_blank');
    },
    onClickCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    },
    getLocationOrigin() {
      return location.origin;
    },
    onPageListChange(data) {
      let { pc, mobile } = data;
      this.pcPageOptions = [];
      this.mobilePageOptions = [];
      this.pages.mobile = mobile;
      this.pages.pc = pc;
      this.pageLoaded = true;
      if (pc) {
        for (let p of pc) {
          this.pcPageOptions.push({
            label: p.name,
            value: p.id,
            data: p,
            url: `/webapp/${this.prodId}/${this.uuid}/${p.id}`
            // url: `/web/prod/${this.uuid}/${p.id}`
          });
        }
      }
      if (mobile) {
        for (let p of mobile) {
          this.mobilePageOptions.push({
            label: p.name,
            value: p.id,
            data: p,
            //FIXME: 手机端产品地址修正
            url: `/webapp/${this.prodId}/${this.uuid}/${p.id}`
          });
        }
      }
    },
    customRequest(options, key) {
      this.uploading = true;
      this.naturalIconSize = undefined;
      let file = options.file,
        fileSize = file.size,
        fileName = file.name,
        formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);
      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      formData.set('file', file);
      $axios
        .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
          headers: headers
        })
        .then(({ data }) => {
          this.uploading = false;
          if (data.code == 0 && data.data) {
            options.onSuccess();
            this.setting.icon = `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`;
            let imgDom = new Image();
            imgDom.src = this.setting.icon;
            let _this = this;
            imgDom.onload = () => {
              _this.naturalIconSize = [imgDom.naturalWidth, imgDom.naturalHeight];
            };
          }
        });
    },
    removeIcon() {
      this.naturalIconSize = undefined;
      this.setting.icon = undefined;
    },
    beforeUpload(file) {
      console.log('上传的文件信息: ', file);
      let isJpgOrPng = ['image/png', 'image/x-icon'].includes(file.type);
      if (!isJpgOrPng) {
        this.$message.error('只允许上传 png 或者 x-icon 图片格式');
      }
      return isJpgOrPng;
    },
    fetchVersionSetting() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/getSetting`, { params: { versionUuid: this.uuid } })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    }
  },

  watch: {
    // setting: {
    //   deep: true,
    //   handler(v) {
    //     console.log('版本设置定义变更: ', v);
    //   }
    // },
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
  }
};
</script>
