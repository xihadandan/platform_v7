<template>
  <div>
    <a-form-model>
      <a-form-model-item label="标题大小">
        <a-input-number v-model="widget.configuration.titleStyle.size" :allowClear="true"></a-input-number>
        px
      </a-form-model-item>
      <a-form-model-item label="标题字重大小">
        <a-radio-group size="small" v-model="widget.configuration.titleStyle.weight" button-style="solid">
          <a-radio-button value="">普通</a-radio-button>
          <a-radio-button value="bold">加粗</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <ColorSelectConfiguration
        label="标题颜色"
        v-model="widget.configuration.titleStyle"
        onlyValue
        colorField="color"
        radioSize="small"
        :popupContainer="popupContainer"
      ></ColorSelectConfiguration>
      <a-form-model-item label="标题超过宽度">
        <a-radio-group size="small" v-model="widget.configuration.titleStyle.titleEllipsis" button-style="solid">
          <a-radio-button value="">不省略</a-radio-button>
          <a-radio-button value="1">显示1行</a-radio-button>
          <a-radio-button value="2">显示2行</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="副标题大小">
        <a-input-number v-model="widget.configuration.titleStyle.subSize" :allowClear="true"></a-input-number>
        px
      </a-form-model-item>
      <ColorSelectConfiguration
        label="副标题颜色"
        v-model="widget.configuration.titleStyle"
        onlyValue
        colorField="subColor"
        radioSize="small"
        :popupContainer="popupContainer"
      ></ColorSelectConfiguration>
      <a-form-model-item label="副标题超过宽度" v-if="!isItem">
        <a-radio-group size="small" v-model="widget.configuration.titleStyle.subTitleEllipsis" button-style="solid">
          <a-radio-button value="">不省略</a-radio-button>
          <a-radio-button value="1">显示1行</a-radio-button>
          <a-radio-button value="2">显示2行</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="副标题外边距">
        <a-input v-model="widget.configuration.titleStyle.subMargin" :allowClear="true"></a-input>
      </a-form-model-item>

      <a-form-model-item label="外边距">
        <a-input v-model="widget.configuration.titleStyle.margin" :allowClear="true"></a-input>
      </a-form-model-item>
      <a-form-model-item label="内边距">
        <a-input v-model="widget.configuration.titleStyle.padding" :allowClear="true"></a-input>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';

export default {
  name: 'TitleStyleConfiguration',
  props: {
    widget: Object,
    designer: Object,
    colorConfig: Object
  },
  components: { ColorSelectConfiguration },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('titleStyle')) {
      this.$set(this.widget.configuration, 'titleStyle', {
        margin: '',
        padding: '',
        size: '',
        color: '',
        subColor: '',
        subSize: '',
        titleEllipsis: '',
        subTitleEllipsis: '',
        weight: '',
        subMargin: ''
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    popupContainer() {
      return document.body;
    }
  }
};
</script>
