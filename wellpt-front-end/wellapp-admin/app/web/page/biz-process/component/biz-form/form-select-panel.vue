<template>
  <div class="widget-design-select-sider">
    <a-icon
      type="double-left"
      :class="['left-collapse', collapse ? 'collapsed' : '']"
      @click.stop="onClickCollapse"
      :title="collapse ? '点击展开' : '点击收缩'"
    />
    <a-tabs
      ref="tab"
      v-model="activeKey"
      tab-position="left"
      size="small"
      class="widget-select-tabs"
      defaultActiveKey="process"
      @tabClick="onTabClick"
    >
      <a-tab-pane key="form">
        <span slot="tab">
          <Icon type="iconfont icon-a-icleftbuju"></Icon>
          <br />
          表单
        </span>
        <BizForm :processAssembleJson="processAssembleJson"></BizForm>
      </a-tab-pane>
      <a-tab-pane key="template" v-if="false">
        <span slot="tab">
          <Icon type="iconfont icon-a-icleftzujian"></Icon>
          <br />
          模板
        </span>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import BizForm from './form.vue';
import draggable from 'vuedraggable';
export default {
  name: 'FormSelectPanel',
  props: {
    processAssembleJson: Object
  },
  components: { draggable, BizForm },
  data() {
    let activeKey = 'form';
    return {
      activeKey,
      collapse: false
    };
  },
  computed: {},
  watch: {},
  created() {
    const _this = this;
  },
  mounted() {
    this.defaultSiderStyle = {
      flex: this.$el.parentElement.parentElement.style.flex,
      width: this.$el.parentElement.parentElement.style.width
    };
  },
  methods: {
    onTabClick(key) {
      this.collapse = false;
      this.collapseOrExpand();
    },
    collapseOrExpand(width) {
      let style = this.$el.parentElement.parentElement.style;
      if (this.collapse) {
        style.flex = '0 0 ' + width + 'px';
        style.width = width + 'px';
        style.minWidth = style.width;
      } else {
        style.flex = this.defaultSiderStyle.flex;
        style.width = this.defaultSiderStyle.width;
        style.minWidth = this.defaultSiderStyle.width;
      }
    },
    onClickCollapse(e) {
      this.collapse = !this.collapse;

      this.collapseOrExpand(
        64 //_target.offsetWidth
      );
    }
  }
};
</script>

<style lang="less" scoped></style>
