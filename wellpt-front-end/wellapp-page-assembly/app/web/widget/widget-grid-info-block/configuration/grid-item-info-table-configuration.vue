<template>
  <div>
    <a-table
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="columns"
      :data-source="widget.configuration.infoItems"
      :class="['widget-grid-info-item-table', 'no-border']"
    >
      <template slot="titleSlot" slot-scope="text, record">
        <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" />
        <a-input v-model="record.title" style="width: calc(100% - 40px)" allowClear>
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :target="record" :designer="designer" :code="record.id + '_title'" v-model="record.title" />
          </template>
        </a-input>
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button size="small" type="link" @click="widget.configuration.infoItems.splice(index, 1)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
        <WidgetDesignDrawer :id="'WidgetGridInfoBlockEdit' + record.id" title="编辑" :designer="designer">
          <a-button size="small" type="link" title="编辑">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          </a-button>
          <template slot="content">
            <GridInfoItem :item="record" :widget="widget" :designer="designer" />
          </template>
        </WidgetDesignDrawer>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer :id="'WidgetGridInfoBlockAdd' + widget.id" title="添加信息项" :designer="designer">
          <div style="text-align: right">
            <a-button type="link" :style="{ paddingLeft: '7px' }" size="small">
              <Icon type="pticon iconfont icon-ptkj-jiahao" />
              添加
            </a-button>
          </div>
          <template slot="content">
            <GridInfoItem :item="newInfoItem" :widget="widget" :designer="designer" />
          </template>
          <template slot="footer" slot-scope="{ close }">
            <a-button size="small" type="primary" @click.stop="e => onConfirmAddInfoItem(close)">确定</a-button>
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import GridInfoItem from './grid-info-item.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'GridItemInfoTableConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object
  },
  components: { GridInfoItem },
  computed: {},
  data() {
    return {
      newInfoItem: this.getItemConfig(),
      columns: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 90, align: 'right', scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.tableDraggable(
      this.widget.configuration.infoItems,
      this.$el.querySelector('.widget-grid-info-item-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    getItemConfig() {
      return {
        id: generateId(),
        title: undefined,
        subTitle: undefined,
        avatar: {
          icon: 'folder'
        }
      };
    },
    onConfirmAddInfoItem(close) {
      this.widget.configuration.infoItems.push(deepClone(this.newInfoItem));
      this.newInfoItem = deepClone(this.getItemConfig());
      close();
    }
  }
};
</script>
