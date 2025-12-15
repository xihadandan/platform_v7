<template>
  <a-tabs class="login-config-tabs">
    <a-tab-pane key="1" tab="页面整体">
      <PerfectScrollbar style="height: calc(100vh - 110px)">
        <a-collapse default-active-key="bg" :bordered="false" expandIconPosition="right" accordion>
          <a-collapse-panel key="bg" header="背景">
            <a-form-model :colon="false" class="pt-form">
              <a-form-model-item label="背景颜色">
                <StyleColorTreeSelect :colorConfig="colorConfig" v-if="colorFetched" style="width: 100%" v-model="config.backgroundColor" />
              </a-form-model-item>
              <a-form-model-item label="背景图片类型">
                <a-radio-group v-model="bgImageType" button-style="solid" @change="onChangeBgImageType">
                  <a-radio-button value="simple">单屏背景</a-radio-button>
                  <a-radio-button value="carousel">轮播背景</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="背景图片">
                <div :class="['login-bg-image-container', config.backgroundCarousel.enable ? 'carousel' : 'simple']">
                  <DraggableTreeList
                    v-model="config.backgroundImage"
                    :maxLevel="1"
                    :draggable="config.backgroundCarousel.enable"
                    dragButton
                    dragButtonPosition="end"
                    :showLeaveIcon="false"
                    :titleWidth="config.backgroundCarousel.enable ? 240 : 320"
                  >
                    <template slot="title" slot-scope="scope">
                      <ImageLibrary
                        v-model="config.backgroundImage[scope.index]"
                        :width="72"
                        :height="40"
                        boxClass="flex f_y_c f_x_s"
                        :boxStyle="{ width: config.backgroundCarousel.enable ? '236px' : '316px' }"
                        :limitSize="limitSize"
                        :acceptType="acceptTypes"
                        :acceptTip="acceptTip"
                      >
                        <template slot="button-before">
                          <!-- 用于解决点击该组件会默认触发第一个按钮事件 -->
                          <a-button type="link" @click.stop="() => {}" size="small" style="display: none"></a-button>
                        </template>
                      </ImageLibrary>
                      <!-- <img :src="scope.item" class="transparent-bg" /> -->
                    </template>
                    <template slot="operation" slot-scope="scope">
                      <a-button
                        size="small"
                        v-show="config.backgroundCarousel.enable && config.backgroundImage.length > 1"
                        title="删除菜单"
                        type="link"
                        @click.stop="scope.items.splice(scope.index, 1)"
                      >
                        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                        删除
                      </a-button>
                      <!-- <a-button size="small" type="link" @click="replaceBgImage(scope.index)">
                        <Icon type="pticon iconfont icon-ptkj-shangchuan"></Icon>
                        上传
                      </a-button> -->
                    </template>
                  </DraggableTreeList>
                  <a-button size="small" v-show="config.backgroundCarousel.enable" type="link" icon="upload" @click="addReplaceBgImage">
                    添加轮播背景
                  </a-button>
                  <!-- <a-upload
                    v-show="config.backgroundCarousel.enable"
                    name="file"
                    :file-list="[]"
                    ref="bgUpload"
                    :show-upload-list="false"
                    :before-upload="e => beforeUpload(e, 50)"
                    :customRequest="e => customRequest(e, 'login_background', url => afterUploadBgImage(url))"
                  >
                    <a-button size="small" type="link" icon="upload" @click="replaceBgImageIndex = undefined" ref="bgImageUploadButton">
                      添加轮播背景
                    </a-button>
                  </a-upload> -->
                  <div class="tip-div">支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 800px*800px</div>
                </div>
              </a-form-model-item>
              <a-form-model-item label="轮播频率" v-if="config.backgroundCarousel.enable">
                <a-input-number v-model="config.backgroundCarousel.duration" :min="3" />
                <label style="margin-left: -50px; background: #e8e8e8; padding: 0px 5px; border-radius: 2px; font-size: 12px">秒</label>
              </a-form-model-item>
            </a-form-model>
          </a-collapse-panel>
          <a-collapse-panel key="logo" header="logo">
            <a-form-model :colon="false" class="pt-form">
              <a-form-model-item :label="null">
                <ImageLibrary
                  v-model="config.logo"
                  width="100%"
                  :height="150"
                  :limitSize="limitSize"
                  :acceptType="acceptTypes"
                  :acceptTip="acceptTip"
                />
                <!-- <img :src="config.logo" v-if="config.logo" class="login-page-logo transparent-bg" />
                <a-upload
                  name="file"
                  :file-list="[]"
                  :show-upload-list="false"
                  :before-upload="e => beforeUpload(e, 50)"
                  :customRequest="e => customRequest(e, 'login_background', url => setTargetUrl(config, 'logo', url))"
                >
                  <a-button size="small" type="link" icon="upload">上传</a-button>
                  <a-button size="small" type="link" icon="delete" @click.stop="config.logo = undefined" v-if="config.logo">删除</a-button>
                </a-upload> -->
                <div class="tip-div">支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 800px*800px</div>
              </a-form-model-item>
              <a-form-model-item label="logo布局尺寸">
                宽
                <a-input-number v-model="config.logoWidth" />
                <label class="unit-span">px</label>
                <span style="margin-left: 50px">
                  高
                  <a-input-number v-model="config.logoHeight" />
                  <label class="unit-span">px</label>
                </span>
              </a-form-model-item>
              <a-form-model-item label="logo间距">
                <span>
                  上
                  <a-input-number v-model="config.logoMargin[0]" />
                  <label class="unit-span">px</label>
                </span>
                <span style="margin-left: 50px">
                  下
                  <a-input-number v-model="config.logoMargin[2]" />
                  <label class="unit-span">px</label>
                </span>
                <br />
                <span>
                  左
                  <a-input-number v-model="config.logoMargin[3]" />
                  <label class="unit-span">px</label>
                </span>
                <span style="margin-left: 50px">
                  右
                  <a-input-number v-model="config.logoMargin[1]" />
                  <label class="unit-span">px</label>
                </span>
              </a-form-model-item>
              <a-form-model-item label="logo水平对齐方式">
                <a-radio-group v-model="config.logoHAlign" button-style="solid">
                  <a-radio-button value="left">居左</a-radio-button>
                  <a-radio-button value="center">居中</a-radio-button>
                  <a-radio-button value="right">居右</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="logo垂直对齐方式">
                <a-radio-group v-model="config.logoVAlign" button-style="solid">
                  <a-radio-button value="top">顶部</a-radio-button>
                  <a-radio-button value="center">居中</a-radio-button>
                  <a-radio-button value="bottom">底部</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </a-form-model>
          </a-collapse-panel>
          <a-collapse-panel key="title" header="系统标题">
            <a-form-model-item>
              <template slot="label">
                标题
                <WI18nInput :target="config" v-model="config.title" code="loginComponent.systemTitle" :htmlEditor="true" />
              </template>
              <QuillEditor v-model="config.title" />
            </a-form-model-item>
            <a-form-model-item label="标题布局间距">
              <span>
                上
                <a-input-number v-model="config.titleMargin[0]" />
                <label class="unit-span">px</label>
              </span>
              <span style="margin-left: 50px">
                下
                <a-input-number v-model="config.titleMargin[2]" />
                <label class="unit-span">px</label>
              </span>
              <br />
              <span>
                左
                <a-input-number v-model="config.titleMargin[3]" />
                <label class="unit-span">px</label>
              </span>
              <span style="margin-left: 50px">
                右
                <a-input-number v-model="config.titleMargin[1]" />
                <label class="unit-span">px</label>
              </span>
            </a-form-model-item>
            <a-form-model-item label="标题垂直对齐方式">
              <a-radio-group v-model="config.titleVAlign" button-style="solid">
                <a-radio-button value="top">顶部</a-radio-button>
                <a-radio-button value="center">居中</a-radio-button>
                <a-radio-button value="bottom">底部</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
          </a-collapse-panel>
          <a-collapse-panel key="footer" header="页脚设置">
            <a-form-model-item>
              <template slot="label">
                内容
                <WI18nInput :target="config" v-model="config.footer" code="loginComponent.footerContent" :htmlEditor="true" />
              </template>
              <QuillEditor v-model="config.footer" />
            </a-form-model-item>
          </a-collapse-panel>
        </a-collapse>
      </PerfectScrollbar>
    </a-tab-pane>
    <a-tab-pane key="2" tab="登录窗口">
      <PerfectScrollbar style="height: calc(100vh - 110px)">
        <a-collapse default-active-key="primaryColor" :bordered="false" expandIconPosition="right" accordion>
          <a-collapse-panel key="primaryColor" header="窗口主题色">
            <a-form-model :colon="false" class="pt-form">
              <a-form-model-item>
                <template #label>
                  <label style="color: #a3a3a3; font-size: 12px">登录窗口中输入框、按钮等组件的主题色</label>
                </template>
                <ColorPicker immediately v-model="config.loginWin.primaryColor" width="100%" />
              </a-form-model-item>
            </a-form-model>
          </a-collapse-panel>
          <a-collapse-panel key="winBgColor" header="窗口背景色">
            <a-form-model :colon="false" class="pt-form">
              <a-form-model-item>
                <template #label>
                  <label style="color: #a3a3a3; font-size: 12px">整个登录窗口的背景颜色</label>
                </template>
                <ColorPicker immediately v-model="config.loginWin.backgroundColor" width="100%" />
              </a-form-model-item>
            </a-form-model>
          </a-collapse-panel>
          <a-collapse-panel key="winBgImage" header="背景图片">
            <a-form-model :colon="false" class="pt-form">
              <a-form-model-item>
                <template #label>
                  <label style="color: #a3a3a3; font-size: 12px">登录窗口的背景图片</label>
                </template>
                <ImageLibrary
                  v-model="config.loginWin.backgroundImage"
                  width="100%"
                  :height="150"
                  :limitSize="limitSize"
                  :acceptType="acceptTypes"
                  :acceptTip="acceptTip"
                />
                <!-- <a-row type="flex" class="image-upload-row">
                  <a-col flex="auto">
                    <img
                      :src="config.loginWin.backgroundImage"
                      v-if="config.loginWin.backgroundImage"
                      style="width: 150px; height: 150px; object-fit: scale-down"
                      class="transparent-bg"
                    />
                  </a-col>
                  <a-col flex="140px">
                    <a-upload
                      name="file"
                      :file-list="[]"
                      :show-upload-list="false"
                      :before-upload="e => beforeUpload(e, 50)"
                      :customRequest="
                        e => customRequest(e, 'login_background', url => setTargetUrl(config.loginWin, 'backgroundImage', url))
                      "
                    >
                      <a-button
                        size="small"
                        type="link"
                        icon="delete"
                        @click.stop="config.loginWin.backgroundImage = undefined"
                        v-if="config.loginWin.backgroundImage"
                      >
                        删除
                      </a-button>
                      <a-button size="small" type="link" icon="upload">上传</a-button>
                    </a-upload>
                  </a-col>
                </a-row> -->
              </a-form-model-item>
            </a-form-model>
          </a-collapse-panel>
          <a-collapse-panel key="titleBar" header="标题栏">
            <a-form-model :colon="false" class="pt-form">
              <a-form-model-item label="登录窗口标题栏">
                <a-switch v-model="config.loginWin.titleBar.enable" checked-children="开启" un-checked-children="关闭" />
              </a-form-model-item>
              <div v-show="config.loginWin.titleBar.enable">
                <a-form-model-item label="布局">
                  <a-radio-group size="small" v-model="config.loginWin.titleBar.layout" button-style="solid">
                    <a-radio-button value="horizontal">左右布局</a-radio-button>
                    <a-radio-button value="vertical">上下布局</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
                <a-form-model-item label="布局间距">
                  <a-input-number v-model="config.loginWin.titleBar.gutter" :min="0" />
                  <label style="margin-left: -50px; background: #e8e8e8; padding: 0px 5px; border-radius: 2px; font-size: 12px">px</label>
                </a-form-model-item>
                <a-form-model-item label="logo">
                  <a-switch v-model="config.loginWin.titleBar.logo.enable" checked-children="开启" un-checked-children="关闭" />
                </a-form-model-item>
                <div v-show="config.loginWin.titleBar.logo.enable">
                  <a-form-model-item label="logo布局尺寸">
                    宽
                    <a-input-number v-model="config.loginWin.titleBar.logo.width" />
                    <label style="margin-left: -50px; background: #e8e8e8; padding: 0px 5px; border-radius: 2px; font-size: 12px">px</label>
                    <span style="margin-left: 50px">
                      高
                      <a-input-number v-model="config.loginWin.titleBar.logo.height" />
                      <label style="margin-left: -50px; background: #e8e8e8; padding: 0px 5px; border-radius: 2px; font-size: 12px">
                        px
                      </label>
                    </span>
                  </a-form-model-item>
                  <a-form-model-item label="上传logo">
                    <ImageLibrary
                      v-model="config.loginWin.titleBar.logo.src"
                      width="100%"
                      :height="150"
                      :limitSize="limitSize"
                      :acceptType="acceptTypes"
                      :acceptTip="acceptTip"
                    />
                    <!-- <img
                      :src="config.loginWin.titleBar.logo.src"
                      v-if="config.loginWin.titleBar.logo.src"
                      class="login-page-logo transparent-bg"
                    />
                    <a-upload
                      name="file"
                      :file-list="[]"
                      :show-upload-list="false"
                      :before-upload="e => beforeUpload(e, 50)"
                      :customRequest="
                        e => customRequest(e, 'login_background', url => setTargetUrl(config.loginWin.titleBar.logo, 'src', url))
                      "
                    >
                      <a-button size="small" type="link" icon="upload">上传</a-button>
                      <a-button
                        size="small"
                        type="link"
                        icon="delete"
                        @click.stop="config.loginWin.titleBar.logo.src = undefined"
                        v-if="config.loginWin.titleBar.logo.src"
                      >
                        删除
                      </a-button>
                    </a-upload> -->
                    <div class="tip-div">支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 800px*800px</div>
                  </a-form-model-item>
                </div>
                <a-form-model-item label="窗口标题">
                  <a-switch v-model="config.loginWin.titleBar.title.enable" checked-children="开启" un-checked-children="关闭" />
                </a-form-model-item>
                <div v-show="config.loginWin.titleBar.title.enable">
                  <a-form-model-item label="标题布局尺寸">
                    <span>
                      宽
                      <a-input-number v-model="config.loginWin.titleBar.title.width" />
                      <label style="margin-left: -50px; background: #e8e8e8; padding: 0px 5px; border-radius: 2px; font-size: 12px">
                        px
                      </label>
                    </span>

                    <span style="margin-left: 50px">
                      高
                      <a-input-number v-model="config.loginWin.titleBar.title.height" />
                      <label style="margin-left: -50px; background: #e8e8e8; padding: 0px 5px; border-radius: 2px; font-size: 12px">
                        px
                      </label>
                    </span>
                  </a-form-model-item>
                  <a-form-model-item>
                    <template slot="label">
                      标题
                      <WI18nInput
                        :target="config.loginWin.titleBar.title"
                        v-model="config.loginWin.titleBar.title.content"
                        code="loginComponent.loginWinTitle"
                        :htmlEditor="true"
                      />
                    </template>
                    <QuillEditor v-model="config.loginWin.titleBar.title.content" />
                  </a-form-model-item>
                </div>
              </div>
            </a-form-model>
          </a-collapse-panel>
          <a-collapse-panel key="i18nBar" header="国际化语言栏">
            <a-form-model-item label="开启国际化语言选择">
              <a-switch v-model="config.loginWin.enableI18nLanguage" checked-children="开启" un-checked-children="关闭" />
            </a-form-model-item>
          </a-collapse-panel>
        </a-collapse>
      </PerfectScrollbar>
    </a-tab-pane>
  </a-tabs>
</template>
<style lang="less">
.login-config-tabs {
  > .ant-tabs-top-bar {
    .ant-tabs-tab-prev,
    .ant-tabs-tab-next {
      display: none !important;
    }
    .ant-tabs-nav-container-scrolling {
      padding: 0px !important;
    }
    .ant-tabs-nav {
      width: 100%;
      > div {
        display: flex;
        > div {
          text-align: center;
          flex: 1;
          margin-right: 0px !important;
        }
      }
    }
  }
}
</style>
<script type="text/babel">
import configMixin from './config.mixin';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  mixins: [configMixin],
  name: 'LoginBasicConfig',
  components: { WI18nInput },
  computed: {},
  data() {
    return {
      bgImageType: this.config.backgroundCarousel.enable ? 'carousel' : 'simple',
      replaceBgImageIndex: undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    replaceBgImage(index) {
      this.$refs.bgImageUploadButton.$el.click();
      this.replaceBgImageIndex = index;
    },

    afterUploadBgImage(url) {
      if (this.replaceBgImageIndex == undefined) {
        this.config.backgroundImage.push(url);
      } else {
        this.config.backgroundImage.splice(this.replaceBgImageIndex, 1, url);
        this.replaceBgImageIndex = undefined;
      }
    },

    onChangeBgImageType() {
      this.config.backgroundCarousel.enable = this.bgImageType == 'carousel';
    },
    generateDefaultConfig() {
      return {
        backgroundColor: '--w-primary-color',
        backgroundImage: ['/static/images/login/login_page_bg_1.png'],
        backgroundCarousel: {
          enable: true,
          duration: 3
        },
        logo: '/static/images/login/logo_wellinfo.png',
        logoWidth: 200,
        logoHeight: 65,
        logoMargin: [15, 0, 0, 15],
        title: undefined,
        titleMargin: [0, 0, 0, 0],
        footer: undefined,
        loginWin: {
          primaryColor: '#488cee',
          backgroundColor: '#fff',
          backgroundImage: undefined,
          titleBar: {
            enable: false,
            layout: 'horizontal',
            gutter: 16,
            logo: {
              enable: false,
              width: undefined,
              height: undefined,
              src: undefined
            },
            title: {
              enable: false,
              width: undefined,
              height: undefined,
              content: undefined
            }
          }
        } // 登录窗口设置
      };
    }
  }
};
</script>
