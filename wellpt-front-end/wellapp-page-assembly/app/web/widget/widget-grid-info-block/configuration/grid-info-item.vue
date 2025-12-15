<template>
  <div>
    <a-form-model ref="form" :model="item">
      <a-form-model-item label="标题">
        <a-input v-model="item.title">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :target="item" :designer="designer" :code="item.id + '_title'" v-model="item.title" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="二级标题">
        <a-input v-model="item.subTitle">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :target="item" :designer="designer" :code="item.id + '_subTitle'" v-model="item.subTitle" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="标题图标">
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
          <IconSetBadge v-model="item.avatar.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="item.avatar.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <BadgeConfiguration :widget="widget" :designer="designer" :configuration="item" />

      <template v-if="item.clickEvent != undefined">
        <a-form-model-item label="点击事件">
          <a-switch v-model="item.clickEvent.enable" />
        </a-form-model-item>

        <WidgetEventHandler
          style="padding: 0px"
          v-if="item.clickEvent.enable"
          :widget="widget"
          :eventModel="item.clickEvent.eventHandler"
          :designer="designer"
          :rule="{
            name: false,
            triggerSelectable: false
          }"
        ></WidgetEventHandler>
      </template>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import BadgeConfiguration from '../../commons/badge-configuration.vue';

export default {
  name: 'GridInfoItem',
  props: {
    item: Object,
    widget: Object,
    designer: Object
  },
  components: { BadgeConfiguration },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    if (this.item.clickEvent == undefined) {
      this.$set(this.item, 'clickEvent', {
        enable: false,
        eventHandler: {}
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
