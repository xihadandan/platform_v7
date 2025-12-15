<template>
  <a-card
    class="pt-card"
    :bordered="false"
    :bodyStyle="{
      height: 'calc(100vh - 246px)',
      overflowY: 'auto'
    }"
  >
    <template slot="title">
      {{ title }}
    </template>
    <MenuConfiguration :menu="form" :widgetSource="widgetSource" :formLayout="formLayout" :eventRule="eventRule" />
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import widgetEventHandler from '@pageAssembly/app/web/widget/commons/widget-event-handler.vue';
import MenuConfiguration from '@pageAssembly/app/web/widget/widget-menu/configuration/menu-configuration.vue';
import { generateId, jsonValue, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'NavDetail',
  inject: ['pageContext'],
  props: { nav: Object, containerWid: String, widgetSource: Array },
  components: { Modal, widgetEventHandler, MenuConfiguration },
  computed: {},
  data() {
    let form = {},
      isNew = form.id == undefined;
    if (this.nav != undefined) {
      form = JSON.parse(JSON.stringify(this.nav));
      // Object.assign(form, this.nav);
      if (form.id == undefined) {
        form.eventHandler.trigger = 'click';
        form.eventHandler.actionType = 'redirectPage';
        form.eventHandler.targetPosition = 'widgetLayout';
        form.eventHandler.containerWid = this.containerWid;
        form.id = generateId(6);
      } else {
        isNew = false;
      }
    }
    return {
      isNew,
      form,
      title: !isNew ? (form.title ? form.title : '') : '新增导航',
      eventHandlerInit: false,
      formLayout: {
        colon: false,
        layout: 'horizontal',
        labelCol: { span: 8 },
        wrapperCol: { span: 16 }
      },
      eventRule: {
        name: false,
        triggerSelectable: false,
        locateNavigation: false
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onRemoveEvent() {
      this.form.eventHandler = {};
    },
    onSetEventHandler() {
      let eventHandler = this.form.eventHandler;
      this.$set(this.form.eventHandler, 'trigger', 'click');
      this.$set(this.form.eventHandler, 'actionType', 'redirectPage');
      this.$set(this.form.eventHandler, 'targetPosition', 'widgetLayout');
      this.$set(this.form.eventHandler, 'containerWid', this.containerWid);
      this.eventHandlerInit = true;
    }
  }
};
</script>
