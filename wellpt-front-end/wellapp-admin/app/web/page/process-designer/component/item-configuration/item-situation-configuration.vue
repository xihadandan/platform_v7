<template>
  <div>
    <a-form-model-item label="办理情形" prop="enabledSituation">
      <a-switch
        v-model="itemDefinition.enabledSituation"
        checked-children="启用"
        un-checked-children="关闭"
        @change="enabledSituationChange"
      />
    </a-form-model-item>
    <a-table
      v-show="itemDefinition.enabledSituation"
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="situationTableColumn"
      :locale="locale"
      :data-source="itemDefinition.situationConfigs"
      :class="['widget-table-button-table no-border']"
    >
      <template slot="titleSlot" slot-scope="text, record">
        <a-row>
          <a-col span="4">
            <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" />
          </a-col>
          <a-col span="18">
            <a-input v-model="record.situationName" size="small" :style="{ width: '180px' }"></a-input>
          </a-col>
        </a-row>
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" title="删除" @click="deleteSituation(index)">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
        </a-button>
        <ProcessDesignDrawer :id="'situation-edit_' + record.id" title="编辑办理情形">
          <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
          <template slot="content">
            <SituationConfiguration :itemDefinition="itemDefinition" :situationConfig="record"></SituationConfiguration>
          </template>
        </ProcessDesignDrawer>
      </template>
      <template slot="footer">
        <ProcessDesignDrawer :id="drawerAddId" title="添加办理情形">
          <a-button type="link" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
          <template slot="content">
            <SituationConfiguration ref="addSituationConfiguration" :itemDefinition="itemDefinition"></SituationConfiguration>
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
          </template>
        </ProcessDesignDrawer>
      </template>
    </a-table>
  </div>
</template>

<script>
import ProcessDesignDrawer from '../process-design-drawer.vue';
import SituationConfiguration from './situation-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
export default {
  mixins: [draggable],
  props: {
    itemDefinition: Object
  },
  components: { ProcessDesignDrawer, SituationConfiguration },
  inject: ['pageContext'],
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      situationTableColumn: [
        { title: '名称', dataIndex: 'situationName', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'situation-add_' + new Date().getTime()
    };
  },
  mounted() {
    this.tableDraggable(
      this.itemDefinition.situationConfigs,
      this.$el.querySelector('.widget-table-button-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    enabledSituationChange() {
      if (this.itemDefinition.enabledSituation) {
        if (this.itemDefinition.situationConfigs == null) {
          this.$set(this.itemDefinition, 'situationConfigs', []);
        }
      }
    },
    deleteSituation(index) {
      this.itemDefinition.situationConfigs.slice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addSituationConfiguration.collect().then(data => {
        if (data) {
          this.itemDefinition.situationConfigs.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
