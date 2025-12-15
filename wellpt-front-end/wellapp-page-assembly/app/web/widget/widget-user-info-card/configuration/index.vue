<template>
  <div>
    <!-- 仅有一个tab时加上class="one-tab",不是请移除 -->
    <a-tabs default-active-key="1" class="one-tab">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              圆角
              <a-tooltip title="设置四个角圆角">
                <a-checkbox v-model="borderRadiusSetFourCorner" @change="onChangeBorderRadiusSetFourCorner" />
              </a-tooltip>
            </template>
            <a-row v-if="!borderRadiusSetFourCorner">
              <a-col :span="12">
                <a-slider v-model="widget.configuration.borderRadius" :min="0" :max="24" />
              </a-col>
              <a-col :span="12">
                <a-input-number v-model="widget.configuration.borderRadius" :min="0" :max="24" style="margin-left: 12px" />
              </a-col>
            </a-row>
            <div v-else style="display: flex; justify-content: space-between">
              <template
                v-for="(corner, i) in [
                  { name: '左上角', key: 'topLeft' },
                  { name: '右上角', key: 'topRight' },
                  { name: '右下角', key: 'bottomRight' },
                  { name: '左下角', key: 'bottomLeft' }
                ]"
              >
                <a-tooltip :title="corner.name" placement="top">
                  <a-input-number style="width: 50px" v-model="widget.configuration.borderRadius[i]" :min="0" :max="10" />
                </a-tooltip>
              </template>
            </div>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              背景颜色
              <a-tooltip title="使用内置颜色变量">
                <a-checkbox v-model="bgColorUseVar" @change="widget.configuration.backgroundColor = undefined" />
              </a-tooltip>
            </template>
            <StyleColorTreeSelect
              v-if="bgColorUseVar"
              :colorConfig="designer.themeSpecifyColorConfig"
              style="width: 100%"
              v-model="widget.configuration.backgroundColor"
              :display-modal="true"
            />
            <ColorPicker v-else v-model="widget.configuration.backgroundColor" :width="150" :allowClear="true" />
          </a-form-model-item>
          <a-form-model-item label="背景图片">
            <ImageLibrary v-model="widget.configuration.backgroundImage" width="100%" />
          </a-form-model-item>
          <a-form-model-item label="背景位置">
            <a-auto-complete
              :data-source="['top', 'center', 'left', 'right']"
              v-model.trim="widget.configuration.backgroundPosition"
              placeholder="请输入背景位置样式"
              allow-clear
            />
          </a-form-model-item>
          <a-form-model-item label="背景重复">
            <a-auto-complete
              :data-source="['no-repeat', 'repeat-x', 'repeat-y', 'repeat', 'space', 'round']"
              v-model.trim="widget.configuration.backgroundRepeat"
              placeholder="请输入背景重复"
              allow-clear
            />
          </a-form-model-item>
          <a-form-model-item label="背景图片尺寸">
            <a-auto-complete
              :data-source="['cover', 'contain', 'inherit', 'unset', 'initial']"
              v-model.trim="widget.configuration.backgroundSize"
              placeholder="请输入背景图片尺寸"
              allow-clear
            />
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import StyleColorTreeSelect from '../../../page/theme-designer/component/design/lib/style-color-tree-select.vue';
import { customFileUploadRequest } from '@framework/vue/utils/function.js';

export default {
  name: 'WidgetUserInfoCardConfiguration',
  props: { widget: Object, designer: Object },
  components: { StyleColorTreeSelect },
  computed: {},
  data() {
    return {
      borderRadiusSetFourCorner: Array.isArray(this.widget.configuration.borderRadius),
      bgColorUseVar: this.widget.configuration.backgroundColor != undefined && this.widget.configuration.backgroundColor.startsWith('--w-'),
      uploading: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    beforeUpload(file) {
      let isJpgOrPng = ['image/gif', 'image/jpeg', 'image/png'].includes(file.type);
      if (!isJpgOrPng) {
        this.$message.error('只允许上传 jpeg、png 或者 gif 图片格式');
      }
      return isJpgOrPng;
    },
    customRequest(e) {
      this.uploading = true;
      customFileUploadRequest(e).then(dbFile => {
        this.uploading = false;
        this.widget.configuration.backgroundImage = `/proxy-repository/repository/file/mongo/download?fileID=${dbFile.fileID}`;
      });
    },
    onChangeBorderRadiusSetFourCorner() {
      if (this.borderRadiusSetFourCorner) {
        this.$set(
          this.widget.configuration,
          'borderRadius',
          Array.from({ length: 4 }, () => {
            return this.widget.configuration.borderRadius || 0;
          })
        );
      } else {
        this.$set(this.widget.configuration, 'borderRadius', this.widget.configuration.borderRadius[0] || 0);
      }
    }
  },
  configuration() {
    return {
      backgroundColor: '--w-primary-color',
      borderRadius: 2,
      backgroundImageType: 'uploadImage',
      backgroundImage: undefined,
      backgroundPosition: undefined,
      backgroundRepeat: undefined,
      backgroundSize: undefined
    };
  }
};
</script>
