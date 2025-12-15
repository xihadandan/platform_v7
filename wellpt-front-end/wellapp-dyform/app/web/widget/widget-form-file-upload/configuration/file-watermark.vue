<template>
  <a-form-model-item v-if="widget.configuration.type !== 'picture'">
    <template slot="label">
      <slot name="itemLabel">附件水印</slot>
      <a-checkbox v-model="widget.configuration.fileWatermarkConfig.enabled" />
    </template>
    <WidgetDesignDrawer
      v-if="widget.configuration.fileWatermarkConfig.enabled"
      :id="'widgetFormFileuploadWatermarkSet' + widget.id"
      title="水印设置"
      :width="640"
      :bodyStyle="{ height: '100%' }"
      :designer="designer"
    >
      <template slot="content">
        <a-form-model>
          <Scroll :style="bodyStyle">
            <a-alert message="支持 word、ppt、excel、pdf 文档类文件以及 png、jpg 、jpeg 图片类水印" type="info" show-icon />
            <a-form-model-item label="水印生效于">
              <a-checkbox-group
                v-model="widget.configuration.fileWatermarkConfig.effectScope"
                :options="watermarkEffectOptions"
              ></a-checkbox-group>
            </a-form-model-item>
            <DataConditionalControlConfiguration
              :widget="widget"
              :configuration="widget.configuration.fileWatermarkConfig"
              configCode="effectConditionControl"
              controlDescription="水印生效"
              :designer="designer"
            />
            <a-form-model-item label="水印类型">
              <a-radio-group v-model="widget.configuration.fileWatermarkConfig.type" button-style="solid" size="small">
                <a-radio-button value="text">文字</a-radio-button>
                <a-radio-button value="picture">图片</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <div v-show="widget.configuration.fileWatermarkConfig.type === 'text'">
              <a-form-model-item label="内容">
                <FormulaEditor
                  :widget="widget"
                  :bind-to-configuration="widget.configuration.fileWatermarkConfig"
                  configKey="textFormula"
                  :variableDelimiters="['<%= ', ' %>']"
                  ref="formulaEditor"
                  mode="plaintext"
                />
                <div>
                  <template v-for="(txt, i) in ['严禁复制', '保密', '绝密', '传阅', '紧急', '样本', '原件', '草稿']">
                    <a-tag :key="'wtxt_' + i" @click="clickAppendWaterSimpleTextToEditor(txt)">{{ txt }}</a-tag>
                  </template>
                </div>
              </a-form-model-item>
              <!-- <a-form-model-item label="字体">
                <a-select :options="fontFamilyOptions" v-model="widget.configuration.fileWatermarkConfig.fontFamily" allow-clear />
              </a-form-model-item> -->
              <a-form-model-item label="字号">
                <a-auto-complete
                  :data-source="['36', '40', '44', '48', '54', '60', '64', '68', '72', '80']"
                  v-model.trim="widget.configuration.fileWatermarkConfig.fontSize"
                  allow-clear
                  @change="onChangeFontSize"
                />
              </a-form-model-item>
              <a-form-model-item label="颜色">
                <ColorPicker v-model="widget.configuration.fileWatermarkConfig.fontColor" :disableAlpha="true" />
              </a-form-model-item>
            </div>
            <a-form-model-item label="图片" v-if="widget.configuration.fileWatermarkConfig.type === 'picture'">
              <ImageLibrary
                v-model="widget.configuration.fileWatermarkConfig.pictureSrc"
                :allowSelectType="['mongoImages']"
                :includeFileSuffix="['.jpg', '.png', '.jpeg']"
                @input="onInputImage"
              />
            </a-form-model-item>
            <a-form-model-item label="版式">
              <a-radio-group v-model="widget.configuration.fileWatermarkConfig.layout" button-style="solid" size="small">
                <a-radio-button value="horizontal">水平</a-radio-button>
                <a-radio-button value="diagonal">倾斜</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="水平对齐">
              <a-radio-group v-model="widget.configuration.fileWatermarkConfig.horizontalAlign" button-style="solid" size="small">
                <a-radio-button value="left">居左</a-radio-button>
                <a-radio-button value="center">居中</a-radio-button>
                <a-radio-button value="right">居右</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="垂直对齐">
              <a-radio-group v-model="widget.configuration.fileWatermarkConfig.verticalAlign" button-style="solid" size="small">
                <a-radio-button value="top">居上</a-radio-button>
                <a-radio-button value="center">居中</a-radio-button>
                <a-radio-button value="bottom">居下</a-radio-button>
              </a-radio-group>
            </a-form-model-item>

            <a-form-model-item label="缩放" v-show="widget.configuration.fileWatermarkConfig.type === 'picture'">
              <a-space>
                <span>
                  <a-input-number v-model="widget.configuration.fileWatermarkConfig.scale" :min="0" :max="500" />
                  %
                </span>
              </a-space>
            </a-form-model-item>
            <a-form-model-item label="透明度">
              <a-space>
                <span>
                  <a-input-number v-model="widget.configuration.fileWatermarkConfig.opacity" :min="0" :max="100" />
                  %
                </span>
              </a-space>
            </a-form-model-item>
          </Scroll>
        </a-form-model>
      </template>
      <a-button icon="setting" size="small">水印设置</a-button>
    </WidgetDesignDrawer>
  </a-form-model-item>
</template>
<style lang="less" scoped>
.widget-design-drawer .ant-drawer-content-wrapper .ant-drawer-body .ant-form {
  ::v-deep .ant-form-item-label {
    width: 20%;
    -webkit-box-flex: 0;
    -webkit-flex: 0 0 auto;
    flex: 0 0 auto;
    display: block;
    text-align: left;
    float: left;
  }
  ::v-deep .ant-form-item-control-wrapper {
    width: 80%;
    -webkit-box-flex: 0;
    -webkit-flex: 0 0 auto;
    flex: 0 0 auto;
    display: block;
    float: left;
  }
}
</style>
<script type="text/babel">
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import { queryString } from '@framework/vue/utils/util';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import FormulaEditor from '@pageAssembly/app/web/widget/commons/formula-editor.vue';
export default {
  name: 'FileWatermark',
  props: {
    widget: Object,
    designer: Object,
    bodyStyle: {
      type: Object,
      default: () => ({ height: 'calc(100vh - 180px)', paddingRight: '10px' })
    }
  },
  components: { ImageLibrary, FormulaEditor },
  computed: {
    watermarkEffectOptions() {
      return this.widget.configuration.fileWatermarkConfig
        ? [
            {
              label: '上传',
              value: 'upload',
              disabled:
                this.widget.configuration.fileWatermarkConfig.effectScope.includes('preview') ||
                this.widget.configuration.fileWatermarkConfig.effectScope.includes('download')
            },
            {
              label: '仅下载',
              value: 'download',
              disabled: this.widget.configuration.fileWatermarkConfig.effectScope.includes('upload')
            },
            {
              label: '仅预览',
              value: 'preview',
              disabled: this.widget.configuration.fileWatermarkConfig.effectScope.includes('upload')
            }
          ]
        : [];
    }
  },
  data() {
    return {
      fontFamilyOptions: [
        { label: '微软雅黑', value: 'Microsoft YaHei' },
        { label: '宋体', value: 'SimSun' },
        { label: '黑体', value: 'SimHei' },
        { label: '楷体', value: 'KaiTi' },
        { label: 'Arial', value: 'Arial' },
        { label: 'Times New Roman', value: 'Times New Roman' },
        { label: 'Courier New', value: 'Courier New' }
      ],
      watermarkList: []
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('fileWatermarkConfig')) {
      this.$set(this.widget.configuration, 'fileWatermarkConfig', {
        enabled: false,
        effectScope: ['upload'], // upload \ download \ preview
        type: 'text',
        verticalAlign: 'center',
        horizontalAlign: 'center',
        opacity: 100,
        layout: 'diagonal',
        fontColor: '#727272',
        // fontFamily: 'Microsoft YaHei',
        fontSize: 68,
        scale: 100
      });
    }
  },
  beforeMount() {
    this.initWatermarkList();
  },
  mounted() {},
  methods: {
    clickAppendWaterSimpleTextToEditor(txt) {
      this.$refs.formulaEditor.insertSimpleEditorCode(txt);
    },
    onInputImage() {
      let fileWatermarkConfig = this.widget.configuration.fileWatermarkConfig;
      fileWatermarkConfig.imageFileId = undefined;
      fileWatermarkConfig.imageBase64 = undefined;
      if (fileWatermarkConfig.pictureSrc) {
        if (fileWatermarkConfig.pictureSrc.startsWith('/proxy-repository/repository/file')) {
          fileWatermarkConfig.imageFileId = queryString(
            fileWatermarkConfig.pictureSrc.substring(fileWatermarkConfig.pictureSrc.indexOf('?') + 1)
          ).fileID;
        } else if (fileWatermarkConfig.pictureSrc.startsWith('url("data:image')) {
          // base64
          fileWatermarkConfig.imageBase64 = fileWatermarkConfig.pictureSrc.substring(5, this.value.length - 2);
        }
      }
    },
    onChangeFontSize() {
      if (
        this.widget.configuration.fileWatermarkConfig.fontSize != undefined &&
        !/^-?\d+(\.\d+)?$/.test(this.widget.configuration.fileWatermarkConfig.fontSize)
      ) {
        this.$message.error('请输入数字');
        this.widget.configuration.fileWatermarkConfig.fontSize = undefined;
      }
    },
    initWatermarkList() {}
  }
};
</script>
