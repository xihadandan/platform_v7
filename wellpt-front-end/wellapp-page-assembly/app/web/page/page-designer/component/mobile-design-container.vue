<template>
  <mPhoneUi>
    <template slot="default" v-if="!loading">
      <draggable
        :list="designer.widgets"
        v-bind="{ group: 'dragGroup', ghostClass: 'ghost' }"
        handle=".widget-drag-handler"
        @start="onDragStart"
        @end="onDragEnd"
        @add="onDragAdd"
        @choose="onDragChoose"
        :move="onDragMove"
        @change="onDragChange"
        @sort="onDragSort"
        @unchoose="onDragUnchoose"
        @update="onDragUpdate"
      >
        <transition-group
          name="fade"
          tag="div"
          :class="['widget-drop-panel']"
          @click.native.stop="onClickDropPanel"
          :style="{ position: 'relative', background: 'transparent', width: 'calc(100% - 1px)', height: 'auto', minHeight: '500px' }"
        >
          <WDesignItem
            :key="widget.id"
            :widget="widget"
            v-for="(widget, i) in designer.widgets"
            :index="i"
            :widgetsOfParent="designer.widgets"
            :designer="designer"
          />
        </transition-group>
      </draggable>
    </template>
  </mPhoneUi>
</template>
<style lang="less"></style>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import mPhoneUi from './m-phone-ui.vue';
export default {
  name: 'MobileDesignContainer',
  mixins: [draggable],
  inject: ['pageContext', 'draggableConfig', 'designWidgetTypes'],
  props: {
    designer: Object,
    loading: {
      type: Boolean,
      default: false
    }
  },
  components: { mPhoneUi },
  computed: {},
  data() {
    return { bKey: 'widgetBuildPanel' + new Date().getTime() };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onClickDropPanel(e) {
      this.designer.clearSelected();
    }
  },

  watch: {}
};
</script>
