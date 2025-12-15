<template>
  <div class="search-page-config">
    <a-button type="primary" @click="openDrawer">
      <a-icon type="setting" />
      自定义
    </a-button>
    <drawer v-model="visible" title="搜索页面设置" :width="500" :container="getContainer" :mask="true" wrapClassName="search-page-drawer">
      <template slot="content">
        <a-form-model v-if="visible" :colon="false" layout="vertical" class="pt-form">
          <a-form-model-item>
            <div slot="label" class="search-page-config-label">
              <span>LOGO</span>
              <a-switch v-model="config.enabledLogo" />
            </div>
            <ImageLibrary
              v-model="config.logo"
              width="100%"
              :height="150"
              :limitSize="limitSize"
              :acceptType="acceptTypes"
              :acceptTip="acceptTip"
            />
            <div class="tip-div">支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 800px*800px</div>
          </a-form-model-item>
          <a-form-model-item>
            <div slot="label" class="search-page-config-label">
              <span>
                标题
                <!-- <WI18nInput :target="config" v-model="config.title" code="fullText.title" :htmlEditor="true" /> -->
              </span>
              <a-switch v-model="config.enabledTitle" />
            </div>
            <QuillEditor v-model="config.title" class="search-page-config-quill" />
            <!-- <QuillEditor
              min-height="unset"
              @input="onInputTitle"
              v-model="config.title"
              :hiddenButtons="['image', 'underline', 'minusIndent', 'addIndent', 'align', 'ordered', 'list', 'source', 'fullscreen']"
            /> -->
          </a-form-model-item>
          <div class="search-page-config-title">页面背景</div>
          <a-form-model-item label="背景颜色">
            <StyleColorTreeSelect :colorConfig="colorConfig" v-if="colorFetched" style="width: 100%" v-model="config.backgroundColor" />
          </a-form-model-item>
          <a-form-model-item label="背景图片">
            <div :class="['search-page-bgimage-container', config.enableBgCarousel ? 'carousel' : 'simple']">
              <DraggableTreeList
                v-model="config.backgroundImage"
                :maxLevel="1"
                :draggable="config.enableBgCarousel"
                dragButton
                dragButtonPosition="end"
                :showLeaveIcon="false"
                :titleWidth="config.enableBgCarousel ? 240 : 433"
              >
                <template slot="title" slot-scope="scope">
                  <ImageLibrary
                    v-model="config.backgroundImage[scope.index]"
                    :width="72"
                    :height="40"
                    boxClass="flex f_y_c f_x_s"
                    :boxStyle="{ width: config.enableBgCarousel ? '236px' : '428px' }"
                    :limitSize="limitSize"
                    :acceptType="acceptTypes"
                    :acceptTip="acceptTip"
                  >
                    <template slot="button-before">
                      <!-- 用于解决点击该组件会默认触发第一个按钮事件 -->
                      <a-button type="link" @click.stop="() => {}" size="small" style="display: none"></a-button>
                    </template>
                  </ImageLibrary>
                </template>
                <template slot="operation" slot-scope="scope">
                  <a-button
                    size="small"
                    v-show="config.enableBgCarousel && config.backgroundImage.length > 1"
                    title="删除菜单"
                    type="link"
                    @click.stop="scope.items.splice(scope.index, 1)"
                  >
                    <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    删除
                  </a-button>
                </template>
              </DraggableTreeList>
              <a-button size="small" v-show="config.enableBgCarousel" type="link" icon="upload">添加轮播背景</a-button>
              <div class="tip-div">支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 800px*800px</div>
            </div>
          </a-form-model-item>
          <a-form-model-item label="背景位置">
            <a-select :options="backgroundPositionOptions" v-model="config.backgroundPosition" />
          </a-form-model-item>
          <a-form-model-item label="背景重复">
            <a-select :options="backgroundRepeatOptions" v-model="config.backgroundRepeat" />
          </a-form-model-item>
        </a-form-model>
      </template>
      <template slot="footer">
        <a-button type="primary" @click="handleConfirm">确定</a-button>
        <a-button @click="closeDrawer">取消</a-button>
      </template>
    </drawer>
  </div>
</template>

<script>
import { backgroundPositionOptions, backgroundRepeatOptions, acceptTypes, acceptTip, limitSize } from '../SearchSetting';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';
import StyleColorTreeSelect from '@pageAssembly/app/web/page/theme-designer/component/design/lib/style-color-tree-select.vue';
import DraggableTreeList from '@pageAssembly/app/web/widget/commons/draggable-tree-list';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'SearchPageConfig',
  props: {
    value: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Drawer,
    ImageLibrary,
    QuillEditor,
    DraggableTreeList,
    StyleColorTreeSelect,
    WI18nInput
  },
  data() {
    const config = JSON.parse(JSON.stringify(this.value));
    return {
      backgroundPositionOptions,
      backgroundRepeatOptions,
      visible: false,
      acceptTypes,
      acceptTip,
      limitSize,
      colorFetched: false,
      colorConfig: {},
      config
    };
  },
  created() {
    this.fetchThemeSpecify();
  },
  methods: {
    fetchThemeSpecify() {
      $axios
        .get(`/proxy/api/theme/specify/getEnabled`, { params: {} })
        .then(({ data }) => {
          console.log('获取主题规范', data);
          if (data.code == 0) {
            let specifyDefJson = JSON.parse(data.data.defJson);
            this.colorConfig = specifyDefJson.colorConfig;
            this.colorFetched = true;
          }
        })
        .catch(error => {});
    },
    onInputTitle() {},
    handleConfirm() {
      this.$emit('input', this.config);
      this.closeDrawer();
    },
    getContainer() {
      return document.body;
    },
    openDrawer() {
      this.visible = true;
    },
    closeDrawer() {
      this.visible = false;
    }
  }
};
</script>
