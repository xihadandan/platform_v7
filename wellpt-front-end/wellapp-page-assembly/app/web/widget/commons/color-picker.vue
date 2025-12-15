<template>
  <a-popconfirm
    overlayClassName="color-picker-popconfirm"
    placement="bottomLeft"
    :disabled="disabled"
    :ok-text="$i18n ? $t('orgSelect.okText', '确定') : '确定'"
    :cancel-text="$i18n ? $t('orgSelect.cancelText', '取消') : '确定'"
    @confirm="onConfirmColor"
    @cancel="onCancel"
    :okButtonProps="okButtonProps"
    :cancelButtonProps="cancelButtonProps"
    :getPopupContainer="getPopupContainer()"
  >
    <template slot="title">
      <div class="color-picker">
        <component
          :is="picker"
          v-if="color != undefined"
          v-model="color"
          @input="onChangeColor"
          :palette="palette"
          :presetColors="palette"
          :defaultColors="palette"
          :disable-alpha="disableAlpha"
        ></component>
        <!-- <Sketch v-model="color" v-if="color != undefined" @input="onChangeColor" /> -->
      </div>
      <div style="text-align: right" v-if="pickerChange">
        <a-button type="link" size="small" @click="pickerChangeHandle">{{ pickerChangeText }}</a-button>
      </div>
    </template>
    <template v-if="displayType == 'custom'">
      <slot></slot>
    </template>
    <template v-else>
      <a-input :readOnly="true" :value="colorStyle.color" class="color-picker-input" :style="{ width: vWidth }">
        <span slot="prefix" v-show="colorStyle.color">
          <slot name="prefix">
            <!-- 未设置颜色时不显示色块 -->
            <i class="anticon color-preview-block" :style="colorStyle">
              <svg viewBox="0 0 1024 1024" width="1em" height="1em" fill="currentColor" aria-hidden="true" focusable="false" class="">
                <path d="M864 64H160C107 64 64 107 64 160v704c0 53 43 96 96 96h704c53 0 96-43 96-96V160c0-53-43-96-96-96z"></path>
              </svg>
            </i>
          </slot>
        </span>
        <template slot="addonAfter">
          <slot name="addonAfter"></slot>
        </template>
        <a-icon v-if="allowClear && color" class="clear" type="close-circle" theme="filled" slot="suffix" @click.stop="clearColor" />
      </a-input>
    </template>
  </a-popconfirm>
</template>
<style lang="less">
.color-picker-popconfirm {
  .anticon-exclamation-circle,
  .vc-twitter-triangle-shadow,
  .vc-twitter-triangle {
    display: none;
  }
  .anticon-exclamation-circle {
    display: none;
  }
  .ant-popover-message-title {
    padding: 0px;
  }
  .ant-popover-inner-content {
    padding: 0px 5px 5px 0px;
  }
}
.color-picker-input {
  .color-preview-block {
    outline: 1px solid #c3c3c3;
    border-radius: 1px;
  }
  .clear {
    color: rgba(0, 0, 0, 0.25);
    font-size: 12px;
    opacity: 0;
    cursor: pointer;
  }
  &:hover .clear {
    opacity: 1;
  }
}
.color-picker {
  .vc-sketch,
  .vc-twitter,
  .vc-compact,
  .vc-chrome {
    box-shadow: none;
  }
}
</style>
<script type="text/babel">
import { Sketch, Twitter, Chrome } from 'vue-color';
import tinycolor from 'tinycolor2';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function';

export default {
  name: 'ColorPicker',
  props: {
    value: String | Object, // #fffff  or { h: 150, s: 0.66, v: 0.30 } or { r: 255, g: 0, b: 0 }
    width: String | Number,
    allowClear: { type: Boolean, default: false },
    immediately: {
      type: Boolean,
      default: false
    },
    picker: {
      type: String,
      default: 'Sketch'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    defaultColor: {
      type: String,
      default: undefined
    },
    displayType: {
      type: String,
      default: ''
    },
    pickerChange: {
      type: Boolean,
      default: false
    },
    palette: {
      type: Boolean | Array,
      default: undefined
    },
    popupContainer: Function,
    disableAlpha: {
      type: Boolean,
      default: false
    }
  },
  components: { Sketch, Twitter, Chrome },
  computed: {
    vWidth() {
      if (typeof this.width == 'number') {
        return this.width + 'px';
      }
      return this.width || 'auto';
    },
    pickerChangeText() {
      if (this.picker == 'Sketch') {
        return this.$t('colorPicker.collapse', '收起');
      } else {
        return this.$t('colorPicker.more', '更多');
      }
    }
  },
  watch: {
    value() {
      this.color = this.value || '#488cee';
      this.colorStyle.color = typeof this.value === 'string' || this.value == undefined ? this.value : tinycolor(this.value).toHexString();
      this.onChangeColorEmit();
    }
  },
  data() {
    let colorStr = typeof this.value === 'string' || this.value == undefined ? this.value : tinycolor(this.value).toHexString();
    return {
      okButtonProps: {
        style: this.immediately ? { display: 'none' } : {}
      },
      cancelButtonProps: {
        style: this.immediately ? { display: 'none' } : {}
      },
      color: this.value || '#488cee',
      colorStyle: {
        color: colorStr
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    //FIXME: 滚动页面，弹窗调整top偏移量
  },
  methods: {
    getPopupContainer() {
      if (typeof this.popupContainer == 'function') {
        if (typeof this.popupContainer() == 'function') {
          return this.popupContainer();
        } else {
          return this.popupContainer;
        }
      }
      return getPopupContainerNearestPs(400);
    },
    onCancel() {
      console.log('退出');
    },
    clearColor(e) {
      this.color = '';
      this.colorStyle.color = undefined;
      this.$emit('input', undefined);
      this.$emit('ok', undefined);
    },
    onChangeColor() {
      if (this.immediately) {
        this.onConfirmColor();
      }
    },
    onConfirmColor() {
      let { a, hex, hex8, rgba } = this.color;
      if ((a == undefined && typeof this.color === 'string') || this.color == undefined) {
        // 解决当this.color只有hex值时，确定会提交空值
        let color = this.color || '#488cee';
        a = 1;
        hex = color;
      }
      let output = typeof this.value === 'string' || this.value == undefined ? (a === 1 ? hex : hex8) : rgba;
      this.colorStyle.color = a === 1 ? hex : hex8;
      this.$emit('input', output);
      this.$emit('ok', output);
    },
    onChangeColorEmit() {
      this.$emit('onChangeColor');
    },
    pickerChangeHandle() {
      this.$emit('onPickerChange');
    }
  }
};
</script>
