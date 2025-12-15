<template>
  <div class="theme-style-panel theme-picture-panel">
    <a-page-header title="图片">
      <div slot="subTitle">平台表单图片</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认  Default">
            <a-upload
              list-type="picture-card"
              :file-list="fileList"
              :customRequest="customImageRequest"
              :showUploadList="showUploadListPicture"
            >
              <div>
                <a-icon type="plus" />
                <div class="ant-upload-text">点击添加</div>
              </div>
            </a-upload>
          </a-descriptions-item>
          <a-descriptions-item label="悬停  Hover">
            <a-upload
              list-type="picture-card"
              :file-list="fileList"
              :customRequest="customImageRequest"
              :showUploadList="showUploadListPicture"
              class="theme-picture-hover"
            >
              <div>
                <a-icon type="plus" />
                <div class="ant-upload-text">点击添加</div>
              </div>
            </a-upload>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 错误提示 -->
      <a-card :class="[selectedKey == 'errorStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('errorStyle')">
        <template #title>
          错误提示
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('errorStyle')" />
        </template>
        <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="错误提示 Error">
              <a-form-model-item prop="testPicture" required class="full-line80">
                <a-upload
                  list-type="picture-card"
                  :file-list="fileList"
                  :customRequest="customImageRequest"
                  :showUploadList="showUploadListPicture"
                >
                  <div>
                    <a-icon type="plus" />
                    <div class="ant-upload-text">点击添加</div>
                  </div>
                </a-upload>
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item :label="null"></a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
      <!-- 已上传 -->
      <a-card :class="[selectedKey == 'itemStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('itemStyle')">
        <template #title>
          已上传
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('itemStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认  Default">
            <a-upload
              list-type="picture-card"
              :default-file-list="defaultFileList"
              :customRequest="e => customRequestToDefault(e, 'uploaded')"
            >
              <div>
                <a-icon type="plus" />
                <div class="ant-upload-text">点击添加</div>
              </div>
            </a-upload>
          </a-descriptions-item>
          <a-descriptions-item label="悬停  Hover">
            <a-upload
              list-type="picture-card"
              :default-file-list="defaultFileList"
              :customRequest="customRequestToDefault"
              class="theme-picture-item-hover"
            >
              <div>
                <a-icon type="plus" />
                <div class="ant-upload-text">点击添加</div>
              </div>
            </a-upload>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';
import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'ThemePicture',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '图片',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      wPictureBorderColor: '--w-border-color-darker',
      wPictureBorderWidth: '--w-border-width-base',
      wPictureBorderStyle: '--w-border-style-dashed',
      wPictureBorderRadius: '--w-border-radius-2',
      wPictureTextColor: '--w-text-color-light',
      wPictureTextSize: '--w-font-size-base',
      wPictureTextWeight: '--w-font-weight-regular',
      wPictureIconSize: '--w-font-size-base',
      wPictureIconColor: '--w-text-color-light',
      wPictureSelectBg: '--w-fill-color-base',
      wPictureLrPadding: '--w-padding-2xs',
      wPictureSelectHeight: '--w-height-sm',

      wPictureBorderColorHover: '--w-primary-color'
    },
    errorStyle: {
      wPictureErrorBorderColor: '--w-danger-color',
      wPictureErrorTextColor: '--w-danger-color',
      wPictureErrorTextSize: '--w-font-size-base',
      wPictureErrorTextWeight: '--w-font-weight-regular',
      wPictureErrorIconSize: '--w-font-size-base',
      wPictureErrorIconColor: '--w-gray-color-11',
      wPictureErrorBorderColorHover: '--w-danger-color'
    },
    itemStyle: {
      wPictureItemBorderColor: '--w-border-color-darker',
      wPictureItemBorderWidth: '--w-border-width-base',
      wPictureItemBorderStyle: '--w-border-style-base',
      wPictureItemBorderRadius: '--w-border-radius-2',
      wPictureItemLrPadding: '--w-padding-3xs',
      wPictureItemMaskColor: '--w-gray-color-13'
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'errorStyle', 'itemStyle'];
      for (let prop of keys) {
        if (this.config[prop]) {
          for (let key in this.config[prop]) {
            if (this.config[prop][key] != undefined) {
              if (key.toLowerCase().indexOf('boxshadow') != -1) {
                // 阴影样式处理
                colorVars['--' + kebabCase(key)] = this.config[prop][key].startsWith('inset ')
                  ? 'inset var(' + this.config[prop][key].split('inset ')[1] + ')'
                  : `var(${this.config[prop][key]})`;
              } else {
                colorVars['--' + kebabCase(key)] = this.config[prop][key].startsWith('--w-')
                  ? `var(${this.config[prop][key]})`
                  : this.config[prop][key];
              }
            }
          }
        }
      }

      let vars = { ...colorVars, ...this.basicStyleCssVars };
      console.log('colorVars', colorVars);
      // vars['--w-border-color-base'] = '#d9d9d9';
      // console.log('输入框样式变量: ', colorVars,this.basicStyleCssVars);
      return vars;
    },
    showUploadListPicture() {
      let showUploadList = { showPreviewIcon: false, showDownloadIcon: false, showRemoveIcon: false };
      return showUploadList;
    }
  },
  data() {
    return {
      selectedKey: 'basicStyle',
      testForm: { testPicture: undefined },
      testRules: {
        testPicture: [
          {
            required: true,
            message: (
              <span class="ant-upload-picture-error">
                <a-icon type="warning" /> 错误提示文案
              </span>
            )
          }
        ]
      },
      fileList: [],
      defaultFileList: [
        {
          uid: '-1',
          name: '-1',
          status: 'done',
          url: '/proxy/org/user/view/photo/user/U0000000059'
        }
      ]
    };
  },
  mounted() {
    this.$emit('select', this.config.basicStyle);
    this.$refs.testForm.validate();
  },
  methods: {
    customRequestToDefault(e, type) {
      this.file2Base64(e).then(res => {
        res.options.onSuccess();
        this.defaultFileList.push(res);
      });
    },
    customImageRequest(e) {
      this.file2Base64(e).then(res => {
        this.fileList.push(res);
      });
    },
    file2Base64(e) {
      return new Promise(resolve => {
        let render = new FileReader();
        render.readAsDataURL(e.file);

        render.onload = event => {
          let src = event.target.result;
          resolve({
            uid: generateId(),
            name: e.file.name,
            status: 'done',
            url: src,
            options: e
          });
        };
      });
    },
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemePictureProp',
          errorStyle: 'ThemePictureError',
          itemStyle: 'ThemePictureItem'
        };
        this.selectedKey = key;
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    }
  }
};
</script>
