<template>
  <EditWrapper :widget="widget" :index="index" :widgetsOfParent="widgetsOfParent" :designer="designer" :parent="parent">
    <a-form-model
      :class="['widget-form']"
      :layout="widget.configuration.layout"
      :style="{ outline: '1px dotted #e8e8e8' }"
      :colon="widget.configuration.colon"
      :model="form"
      :label-col="widget.configuration.layout === 'horizontal' ? widget.configuration.labelCol : null"
      :wrapper-col="widget.configuration.layout === 'horizontal' ? widget.configuration.wrapperCol : null"
    >
      <draggable
        :list="widget.configuration.widgets"
        v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
        handle=".widget-drag-handler"
        ref="draggable"
        @add="e => onDragAdd(e, widget.configuration.widgets)"
        @paste.native="onDraggablePaste"
      >
        <transition-group name="fade" tag="div" :style="{ minHeight: '50px' }">
          <template v-for="(wgt, i) in widget.configuration.widgets">
            <WDesignItem
              :key="wgt.id"
              :widget="wgt"
              :index="i"
              :widgetsOfParent="widget.configuration.widgets"
              :designer="designer"
              :parent="widget"
            />
          </template>
        </transition-group>
      </draggable>
    </a-form-model>
  </EditWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { createForm } from '../form.js';
import '../css/index.less';
export default {
  name: 'EWidgetForm',
  mixins: [editWgtMixin, draggable],
  data() {
    let formHandler = createForm(this);
    return {
      form: {},
      formHandler,
      formConfiguration: { layout: this.widget.configuration.layout || 'horizontal' },
      formCodeWidgetConfigures: {}
    };
  },
  provide() {
    return {
      form: this.form,
      formCodeWidgetConfigures: this.formCodeWidgetConfigures,
      formConfiguration: this.formConfiguration,
      formHandler: this.formHandler
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    defaultEvents() {
      return [
        { id: 'submitForm', title: '提交表单' },
        { id: 'validateForm', title: '校验表单' },
        { id: 'resetForm', title: '重置表单' }
      ];
    }
  },
  created() {},
  mounted() {},
  methods: {},
  watch: {
    'widget.configuration.layout': {
      handler(v, o) {
        this.formConfiguration.layout = v;
      }
    }
  }
};
</script>
