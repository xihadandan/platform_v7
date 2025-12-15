<template>
  <div>
    <a-form-model>
      <a-form-model-item label="宽高">
        <a-input-group compact>
          <a-input-number v-model="widget.configuration.avatarStyle.width" :min="40" :max="200" allowClear />
          <a-button>px</a-button>
        </a-input-group>
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
            <a-slider v-model="widget.configuration.avatarStyle.borderRadius" :min="0" :max="widget.configuration.avatarStyle.width / 2" />
          </a-col>
          <a-col :span="12">
            <a-input-group compact>
              <a-input-number
                v-model="widget.configuration.avatarStyle.borderRadius"
                :min="0"
                :max="widget.configuration.avatarStyle.width / 2"
                style="margin-left: 12px"
                allowClear
              />
              <a-button>px</a-button>
            </a-input-group>
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
            <div :key="'setCorner_' + i">
              <a-tooltip :title="corner.name" placement="top">
                <a-input-number
                  allowClear
                  v-model="widget.configuration.avatarStyle.borderRadius[i]"
                  :min="0"
                  :max="widget.configuration.avatarStyle.width"
                />
              </a-tooltip>
            </div>
          </template>
        </div>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          边框颜色
          <a-tooltip title="使用内置颜色变量">
            <a-checkbox v-model="borderColorUseVar" @change="widget.configuration.avatarStyle.borderColor = undefined" />
          </a-tooltip>
        </template>
        <StyleColorTreeSelect
          v-if="borderColorUseVar"
          :colorConfig="colorConfig"
          style="width: 100%"
          v-model="widget.configuration.avatarStyle.borderColor"
          :popupContainer="popupContainer"
        />
        <ColorPicker
          v-else
          v-model="widget.configuration.avatarStyle.borderColor"
          :width="150"
          :allowClear="true"
          :popupContainer="popupContainer"
        />
      </a-form-model-item>
      <a-form-model-item label="边框大小">
        <a-input-number v-model="widget.configuration.avatarStyle.borderWidth" :min="0" :max="10" allowClear />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          背景颜色
          <a-tooltip title="使用内置颜色变量">
            <a-checkbox v-model="bgColorUseVar" @change="widget.configuration.avatarStyle.backgroundColor = undefined" />
          </a-tooltip>
        </template>
        <StyleColorTreeSelect
          v-if="bgColorUseVar"
          :colorConfig="colorConfig"
          style="width: 100%"
          v-model="widget.configuration.avatarStyle.backgroundColor"
          :popupContainer="popupContainer"
        />
        <ColorPicker
          v-else
          v-model="widget.configuration.avatarStyle.backgroundColor"
          :width="150"
          :allowClear="true"
          :popupContainer="popupContainer"
        />
      </a-form-model-item>

      <a-form-model-item label="图标大小">
        <a-row>
          <a-col :span="6"><a-input-number v-model="widget.configuration.avatarStyle.fontSize" :min="12" :max="100" /></a-col>
          <a-col :span="17">
            <div style="display: flex; align-items: center">
              <div>
                图标颜色
                <a-tooltip title="使用内置颜色变量">
                  <a-checkbox v-model="fontColorUseBar" @change="widget.configuration.avatarStyle.color = undefined" />
                </a-tooltip>
              </div>
              <div style="padding-left: 12px; width: 250px">
                <StyleColorTreeSelect
                  v-if="fontColorUseBar"
                  :colorConfig="colorConfig"
                  style="width: 100%"
                  v-model="widget.configuration.avatarStyle.color"
                  :popupContainer="popupContainer"
                />
                <ColorPicker
                  v-else
                  v-model="widget.configuration.avatarStyle.color"
                  :width="150"
                  :allowClear="true"
                  :popupContainer="popupContainer"
                />
              </div>
            </div>
          </a-col>
        </a-row>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import StyleColorTreeSelect from '../../../page/theme-designer/component/design/lib/style-color-tree-select.vue';

export default {
  name: 'TitleAvatarStyleConfiguration',
  props: {
    widget: Object,
    designer: Object,
    colorConfig: Object
  },
  components: { StyleColorTreeSelect },
  computed: {},
  data() {
    return {
      fontColorUseBar:
        this.widget.configuration.avatarStyle.color != undefined && this.widget.configuration.avatarStyle.color.startsWith('--w-'),
      bgColorUseVar:
        this.widget.configuration.avatarStyle.backgroundColor != undefined &&
        this.widget.configuration.avatarStyle.backgroundColor.startsWith('--w-'),
      borderColorUseVar:
        this.widget.configuration.avatarStyle.borderColor != undefined &&
        this.widget.configuration.avatarStyle.borderColor.startsWith('--w-'),
      borderRadiusSetFourCorner: Array.isArray(this.widget.configuration.avatarStyle.borderRadius)
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeBorderRadiusSetFourCorner() {
      if (this.borderRadiusSetFourCorner) {
        this.$set(
          this.widget.configuration.avatarStyle,
          'borderRadius',
          Array.from({ length: 4 }, () => {
            return this.widget.configuration.avatarStyle.borderRadius || 0;
          })
        );
      } else {
        this.$set(this.widget.configuration.avatarStyle, 'borderRadius', this.widget.configuration.avatarStyle.borderRadius[0] || 0);
      }
    },
    popupContainer() {
      return document.body;
    }
  }
};
</script>
