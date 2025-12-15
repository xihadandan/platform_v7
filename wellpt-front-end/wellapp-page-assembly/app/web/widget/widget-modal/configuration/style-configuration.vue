<template>
  <div>
    <div v-show="vAllowSetWidth || vAllowSetHeight">
      <a-form-model-item label="宽度" v-if="vAllowSetWidth">
        <a-row type="flex">
          <a-col flex="auto" style="padding-right: 5px">
            <a-input-number v-model="width" @change="onInputWidth" v-show="widthUnit != 'auto'" />
          </a-col>
          <a-col flex="70px">
            <a-select v-model="widthUnit" @change="onInputWidth" style="width: 100%">
              <a-select-option value="px">px</a-select-option>
              <a-select-option value="%">%</a-select-option>
              <a-select-option value="vw">vw</a-select-option>
              <a-select-option value="auto">自动</a-select-option>
            </a-select>
          </a-col>
        </a-row>
      </a-form-model-item>
      <a-form-model-item label="内容区高度" v-if="vAllowSetHeight">
        <a-row type="flex">
          <a-col flex="auto" style="padding-right: 5px">
            <a-input-number v-model="height" @change="onInputHeight" v-show="heightUnit != 'auto'" />
          </a-col>
          <a-col flex="70px">
            <a-select v-model="heightUnit" @change="onInputHeight" style="width: 100%">
              <a-select-option value="px">px</a-select-option>
              <!-- <a-select-option value="%">%</a-select-option> -->
              <a-select-option value="vh">vh</a-select-option>
              <a-select-option value="auto">自动</a-select-option>
            </a-select>
          </a-col>
        </a-row>
      </a-form-model-item>
    </div>
  </div>
</template>
<script type="text/babel">
import { isNumber } from 'lodash';
export default {
  name: 'ModalStyleConfiguration',
  props: {
    widget: Object,
    setWidthHeight: {
      // 可调整宽、高
      type: [Boolean, Array], // true ：宽高都可调，false 不可设置宽高，[true,false]: 宽可设置、高不可设置，[false,true]: 宽不可设置、高可设置
      default: true,
      validator: function (v) {
        return typeof v === 'boolean' || (Array.isArray(v) && v.length == 2 && typeof v[0] == 'boolean' && typeof v[1] == 'boolean');
      }
    }
  },
  data() {
    let height = undefined,
      heightUnit = 'px';
    if (this.widget.configuration.height) {
      if (this.widget.configuration.height === 'auto') {
        heightUnit = 'auto';
        height = 'auto';
      } else if (isNumber(this.widget.configuration.height)) {
        height = this.widget.configuration.height;
      } else {
        height = parseInt(this.widget.configuration.height);
        heightUnit = this.widget.configuration.height.replace(/[0-9]/g, '');
      }
    } else {
      heightUnit = 'auto';
    }
    let width = undefined,
      widthUnit = 'px';
    if (this.widget.configuration.width) {
      if (this.widget.configuration.width === 'auto') {
        widthUnit = 'auto';
        width = 'auto';
      } else if (isNumber(this.widget.configuration.width)) {
        width = this.widget.configuration.width;
      } else {
        width = parseInt(this.widget.configuration.width);
        widthUnit = this.widget.configuration.width.replace(/[0-9]/g, '');
      }
    } else {
      widthUnit = 'auto';
    }
    return { width, widthUnit, height, heightUnit };
  },

  beforeCreate() {},
  components: {},
  computed: {
    vAllowSetHeight() {
      return typeof this.setWidthHeight === 'boolean' ? this.setWidthHeight : this.setWidthHeight[1];
    },
    vAllowSetWidth() {
      return typeof this.setWidthHeight === 'boolean' ? this.setWidthHeight : this.setWidthHeight[0];
    }
  },
  created() {},
  methods: {
    onInputHeight(e) {
      if (this.heightUnit == 'auto') {
        this.widget.configuration.height = this.heightUnit;
      } else {
        if (this.height) {
          this.widget.configuration.height = this.height == 'auto' ? null : this.height + this.heightUnit;
          if (this.height == 'auto' && e != 'auto') {
            this.height = null;
          }
        } else {
          this.widget.configuration.height = null;
        }
      }
      this.widget.configuration.style.height = this.widget.configuration.height;
    },
    onInputWidth(e) {
      if (this.widthUnit == 'auto') {
        this.widget.configuration.width = this.widthUnit;
      } else {
        if (this.width) {
          this.widget.configuration.width = this.width == 'auto' ? null : this.width + this.widthUnit;
          if (this.width == 'auto' && e != 'auto') {
            this.width = null;
          }
        } else {
          this.widget.configuration.width = null;
        }
      }
      this.widget.configuration.style.width = this.widget.configuration.width;
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
