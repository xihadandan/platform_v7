<template>
  <div>
    <a-table
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="defineEventsTableColumn"
      :locale="locale"
      :data-source="itemDefinition.defineEvents"
      :class="['widget-table-define-event-table no-border']"
    >
      <template slot="titleSlot" slot-scope="text">
        <span>{{ text }}</span>
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button v-if="!record.builtIn" type="link" size="small" title="删除" @click="deleteDefineEvent(index)">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
        </a-button>
        <ProcessDesignDrawer v-if="!record.builtIn" :id="'define-event-edit_' + record.id" title="编辑自定义事件定义">
          <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
          <template slot="content">
            <DefineEventConfiguration :defineEvent="record"></DefineEventConfiguration>
          </template>
        </ProcessDesignDrawer>
      </template>
      <template slot="footer">
        <ProcessDesignDrawer :id="drawerAddId" title="添加自定义事件定义">
          <a-button type="link" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
          <template slot="content">
            <DefineEventConfiguration ref="addDefineEventConfiguration"></DefineEventConfiguration>
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
          </template>
        </ProcessDesignDrawer>
      </template>
    </a-table>
    <a-form-model-item v-if="hasCustomEvent" label="事件发布">
      <ItemEventPublishConfiguration :closeOpenDrawer="closeOpenDrawer" :itemDefinition="itemDefinition"></ItemEventPublishConfiguration>
    </a-form-model-item>
  </div>
</template>

<script>
import ProcessDesignDrawer from '../process-design-drawer.vue';
import DefineEventConfiguration from './define-event-configuration.vue';
import ItemEventPublishConfiguration from './item-event-publish-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
export default {
  mixins: [draggable],
  props: {
    processDefinition: Object,
    itemDefinition: Object
  },
  components: { ProcessDesignDrawer, DefineEventConfiguration, ItemEventPublishConfiguration },
  inject: ['pageContext'],
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      defineEventsTableColumn: [
        { title: '名称', dataIndex: 'name', ellipsis: true, scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'define-event-add_' + new Date().getTime()
    };
  },
  computed: {
    hasCustomEvent() {
      let defineEvents = this.itemDefinition.defineEvents || [];
      let defineEvent = defineEvents.find(item => item.builtIn === false);
      return defineEvent != null;
    }
  },
  created() {},
  mounted() {
    this.tableDraggable(
      this.itemDefinition.defineEvents,
      this.$el.querySelector('.widget-table-define-event-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    deleteDefineEvent(index) {
      this.itemDefinition.defineEvents.splice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addDefineEventConfiguration.collect().then(data => {
        if (data) {
          this.itemDefinition.defineEvents.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
