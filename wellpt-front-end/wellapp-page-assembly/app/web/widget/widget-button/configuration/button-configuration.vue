<template>
  <div>
    <a-form-model>
      <a-form-model-item label="按钮名称">
        <a-input v-model="button.title">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :designer="designer" :code="button.id" v-model="widget.title" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="隐藏按钮名称">
        <a-switch v-model="button.textHidden" />
      </a-form-model-item>
      <a-form-model-item label="前置图标">
        <WidgetDesignModal
          title="选择图标"
          :zIndex="1000"
          :width="640"
          dialogClass="pt-modal widget-icon-lib-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          mask
          bodyContainer
        >
          <IconSetBadge v-model="button.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="button.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <a-form-model-item label="后缀图标">
        <WidgetDesignModal
          title="选择图标"
          :zIndex="1000"
          :width="640"
          dialogClass="pt-modal widget-icon-lib-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          mask
          bodyContainer
        >
          <IconSetBadge v-model="button.suffixIcon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="button.suffixIcon" />
          </template>
        </WidgetDesignModal>
        <BehaviorLogConfiguration :widget="widget" :configuration="button" :designer="designer" />
      </a-form-model-item>
      <DefaultVisibleConfiguration
        compact
        :designer="designer"
        :configuration="button"
        :widget="widget"
        hasDyformField
      ></DefaultVisibleConfiguration>
    </a-form-model>
    <WidgetEventHandler :eventModel="button.eventHandler" :designer="designer" :rule="{ name: false }" :widget="widget" />
  </div>
</template>
<script type="text/babel">
export default {
  name: 'ButtonConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    button: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {}
};
</script>
