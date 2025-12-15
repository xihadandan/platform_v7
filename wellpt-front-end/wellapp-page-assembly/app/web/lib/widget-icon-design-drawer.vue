<template>
  <WidgetDesignDrawer
    :id="'widgetMenuTitleIconPrefix' + widget.id"
    title="选择图标"
    :designer="designer"
    :width="640"
    :bodyStyle="{ height: '100%', paddingBottom: '53px' }"
  >
    <!-- <a-badge>
      <a-icon
        v-if="widget.configuration.menuTitleIcon"
        slot="count"
        type="close-circle"
        style="color: #f5222d"
        theme="filled"
        @click.stop="widget.configuration.menuTitleIcon = undefined"
        title="删除图标"
      />
      <a-button size="small" shape="round">
        {{ widget.configuration.menuTitleIcon ? '' : '设置图标' }}
        <Icon :type="widget.configuration.menuTitleIcon || 'pticon iconfont icon-ptkj-shezhi'" />
      </a-button>
    </a-badge> -->
    <slot>
      <IconSetBadge v-model="iconStr"></IconSetBadge>
    </slot>

    <template slot="content">
      <WidgetIconLib v-model="iconStr" ref="iconLib" @change="changeIcon" :onlyIconClass="onlyIconClass" />
    </template>
    <template slot="footer" slot-scope="drawerScope">
      <a-button @click="closeIconDrawer(drawerScope)">取消</a-button>
      <a-button type="primary" @click="confirmIcon(drawerScope)">确定</a-button>
    </template>
  </WidgetDesignDrawer>
</template>

<script>
export default {
  name: 'WidgetIconDesignDrawer',
  props: {
    value: {
      type: [String]
    },
    designer: Object,
    widget: Object,
    onlyIconClass: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      iconStr: this.value
    };
  },
  computed: {
    iconValue() {
      return this.value;
    }
  },
  watch: {
    iconStr: {
      handler(v, o) {
        if (v !== o) {
          this.$emit('input', v);
        }
      }
    }
  },
  methods: {
    changeIcon(configStr) {
      this.iconStr = configStr;
    },
    removeIcon() {
      this.iconStr = undefined;
      this.$emit('input', undefined);
    },
    confirmIcon(e) {
      this.$emit('input', this.iconStr);
      this.closeIconDrawer(e);
    },
    closeIconDrawer(e) {
      e.close();
    }
  }
};
</script>
