<template>
  <div>
    <ColorSelectConfiguration
      label="背景颜色"
      v-model="backgroundStyle"
      onlyValue
      colorField="backgroundColor"
      radioSize="small"
    ></ColorSelectConfiguration>
    <a-form-model-item label="背景图片" class="item-lh"></a-form-model-item>
    <a-form-model-item>
      <ImageLibrary v-model="backgroundStyle.backgroundImage" width="100%" />
    </a-form-model-item>
    <a-form-model-item label="背景位置">
      <a-select
        showSearch
        :options="[
          { value: 'top', label: 'top' },
          { value: 'center', label: 'center' },
          { value: 'left', label: 'left' },
          { value: 'right', label: 'right' }
        ]"
        v-model.trim="backgroundStyle.backgroundPosition"
        :filter-option="filterOption"
        placeholder="请输入背景位置样式"
        allow-clear
      />
    </a-form-model-item>
    <a-form-model-item label="背景重复">
      <a-select
        showSearch
        :options="[
          { value: 'no-repeat', label: 'no-repeat' },
          { value: 'repeat-x', label: 'repeat-x' },
          { value: 'repeat-y', label: 'repeat-y' },
          { value: 'repeat', label: 'repeat' },
          { value: 'space', label: 'space' },
          { value: 'round', label: 'round' }
        ]"
        v-model.trim="backgroundStyle.backgroundRepeat"
        :filter-option="filterOption"
        placeholder="请输入背景重复"
        allow-clear
      />
    </a-form-model-item>
    <a-form-model-item label="背景尺寸">
      <a-input v-model="backgroundStyle.backgroundSize" :allowClear="true"></a-input>
    </a-form-model-item>
  </div>
</template>
<script type="text/babel">
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';
export default {
  name: 'BackGroundSet',
  props: {
    widget: Object,
    designer: Object,
    backgroundStyle: Object
  },
  components: { ColorSelectConfiguration },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    filterOption(input, option) {
      return (
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        (option.componentOptions.propsData.value && option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0)
      );
    }
  }
};
</script>
