<template>
  <div>
    <a-form-model>
      <a-form-model-item label="文字颜色">
        <ColorPicker v-model="textStyle.color" width="100%" />
      </a-form-model-item>
      <a-form-model-item label="字体大小">
        <a-input-number v-model="textStyle.fontSize" :min="11" />
      </a-form-model-item>
      <a-form-model-item label="字体">
        <a-select mode="tags" style="width: 100%" :options="fontFamilyOptions" v-model="fontFamily" @change="onChangeFontFamily"></a-select>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';

export default {
  name: 'TextStyleConfiguration',
  props: { widget: Object, designer: Object, textStyle: Object },
  components: { ColorPicker },
  computed: {},
  data() {
    return {
      fontFamily: this.textStyle.fontFamily != undefined ? this.textStyle.fontFamily.split(',') : undefined,
      fontFamilyOptions: [
        {
          label: '宋体',
          value: '宋体'
        },
        {
          label: '微软雅黑',
          value: 'Microsoft YaHei'
        },
        {
          label: 'sans-serif',
          value: 'sans-serif'
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeFontFamily() {
      this.textStyle.fontFamily = this.fontFamily.join(',');
    }
  }
};
</script>
