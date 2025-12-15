<template>
  <a-form-model layout="vertical" :colon="false">
    <a-form-model-item>
      <template slot="label">
        {{ item.code }}
      </template>
      <template v-if="item.code === '--w-primary-color'">
        <ColorPicker v-model="item.value" width="100%" />
      </template>
      <template v-else>
        <ColorPicker v-model="item.value" v-if="item.const" width="100%">
          <template slot="addonAfter">
            <a-switch v-model="item.const" checked-children="取色" un-checked-children="取色" @change="onChangeSwitch" />
          </template>
        </ColorPicker>
        <a-input v-else v-model="item.value">
          <template slot="addonAfter">
            <a-switch v-model="item.const" checked-children="取色" un-checked-children="取色" @change="onChangeSwitch" />
          </template>
        </a-input>
      </template>
    </a-form-model-item>

    <a-form-model-item label="描述">
      <a-textarea v-model="item.remark"></a-textarea>
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import tinycolor from 'tinycolor2';

export default {
  name: 'ThemeColorProp',
  props: {
    item: Object
  },
  components: { ColorPicker },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    if (this.item.const == undefined) {
      this.$set(this.item, 'const', this.isColor()); // 判断是否是颜色值
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeSwitch(ck) {
      // if (ck) {
      //   let color = tinycolor(this.item.value);
      //   if (!color.isValid()) {
      //     // 转值
      //     this.item.value = document.querySelector(`[value='${this.item.value}']`).lastChild.innerText;
      //   }
      // }
    },
    isColor() {
      if (this.item && this.item.value) {
        return tinycolor(this.item.value).isValid();
      }
      return false;
    }
  }
};
</script>
