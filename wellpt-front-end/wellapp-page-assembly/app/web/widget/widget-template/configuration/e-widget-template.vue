<template>
  <EditWrapper
    :widget="widget"
    :showWidgetName="false"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :title="widget.configuration.title"
    :children="widget.configuration.items"
    class="e-widget-template"
  >
    <WidgetTemplate
      v-if="designer.terminalType == 'pc'"
      :widget="widget"
      :index="index"
      :widgetsOfParent="widgetsOfParent"
      :designer="designer"
      :parent="parent"
      style="pointer-events: none"
    />
    <div v-else style="position: relative">
      <div class="mask" style="position: absolute; width: 100%; height: 100%" @click="selectWidget"></div>
      <iframe
        v-if="designer.h5Server != undefined"
        :src="iframeSrc"
        style="width: 100%; height: auto"
        name="template_preview"
        scrolling="no"
        frameborder="0"
      ></iframe>
      <div v-else style="height: 60px; text-align: center; color: #999; line-height: 60px">移动端模板</div>
    </div>
  </EditWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import { getCookie, generateId } from '@framework/vue/utils/util';

export default {
  name: 'EWidgetTemplate',
  mixins: [editWgtMixin],
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      iframeSrc: `${this.designer.h5Server}/#/packages/_/pages/template/preview?templateName=${this.widget.configuration.templateName}`
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    var accessToken = getCookie('jwt');
    this.iframeSrc += '&accessToken=' + accessToken;
  },
  mounted() {},
  methods: {}
};
</script>
