<template>
  <div>
    <a-form-model-item label="是否分发事项" prop="dispenseItem">
      <a-switch v-model="itemDefinition.dispenseItem" checked-children="是" un-checked-children="否" @change="onDispenseItemChange" />
      <a-icon type="settings" />
    </a-form-model-item>
    <a-table
      v-show="itemDefinition.dispenseItem"
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="dispenseFormTableColumn"
      :locale="locale"
      :data-source="itemDefinition.dispenseFormConfigs"
      :class="['widget-table-button-table no-border']"
    >
      <template slot="titleSlot" slot-scope="text, record">
        <a-row>
          <a-col span="4">
            <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" />
          </a-col>
          <a-col span="18">
            <a-input v-model="record.itemName" size="small" :style="{ width: '180px' }" readOnly></a-input>
          </a-col>
        </a-row>
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <ProcessDesignDrawer :id="'dispense-form-edit_' + record.id" title="编辑事项分发">
          <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
          <template slot="content">
            <DispenseFormConfiguration :itemDefinition="itemDefinition" :dispenseFormConfig="record"></DispenseFormConfiguration>
          </template>
        </ProcessDesignDrawer>
      </template>
    </a-table>
    <a-form-model-item v-show="itemDefinition.dispenseItem" label="分发事项办理显示位置" prop="dispenseItemPlaceHolder">
      <a-select
        v-model="itemDefinition.dispenseItemPlaceHolder"
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formBlockOptions"
      ></a-select>
    </a-form-model-item>
  </div>
</template>

<script>
import ProcessDesignDrawer from '../process-design-drawer.vue';
import DispenseFormConfiguration from './dispense-form-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '../../designer/utils';
export default {
  mixins: [draggable],
  props: {
    itemDefinition: Object,
    formBlockOptions: Array
  },
  components: { ProcessDesignDrawer, DispenseFormConfiguration },
  inject: ['pageContext', 'filterSelectOption'],
  data() {
    // 旧数据兼容处理
    if (this.itemDefinition.dispenseFormConfigs) {
      this.itemDefinition.dispenseFormConfigs.forEach(item => {
        if (!item.hasOwnProperty('id')) {
          item.id = generateId();
        }
      });
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      dispenseFormTableColumn: [
        { title: '名称', dataIndex: 'itemName', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'dispense-form-add_' + new Date().getTime()
    };
  },
  mounted() {
    this.tableDraggable(
      this.itemDefinition.dispenseFormConfigs,
      this.$el.querySelector('.widget-table-button-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    onDispenseItemChange() {
      if (this.itemDefinition.dispenseItem) {
        this.loadDispenseFormConfigs();
      }
    },
    loadDispenseFormConfigs() {
      const _this = this;
      let itemDefId = _this.itemDefinition.itemDefId;
      let itemCode = _this.itemDefinition.itemCode;
      $axios.get(`/proxy/api/biz/item/definition/listIncludeItemDataByItemCode?id=${itemDefId}&itemCode=${itemCode}`).then(({ data }) => {
        if (data.data) {
          let includeItems = data.data;
          if (_this.itemDefinition.dispenseFormConfigs.length == 0) {
            _this.addDispenseFormConfigs(includeItems);
          } else {
            _this.updateDispenseFormConfigs(includeItems);
          }
        }
      });
    },
    addDispenseFormConfigs(includeItems) {
      includeItems.forEach(item => {
        this.itemDefinition.dispenseFormConfigs.push({
          id: generateId(),
          itemDefName: item.itemDefName,
          itemDefId: item.itemDefId,
          itemType: item.itemType,
          itemName: item.itemName,
          itemCode: item.itemCode,
          type: '1', // 使用主表单
          botId: null,
          botName: null,
          formUuid: null,
          formName: null,
          entityNameField: null,
          entityIdField: null,
          timeLimitField: null
        });
      });
    },
    updateDispenseFormConfigs(includeItems) {
      const _this = this;
      let itemCodeMap = new Map();
      includeItems.forEach(item => {
        itemCodeMap.set(item.itemCode, item);
      });

      let deleteItems = [];
      _this.itemDefinition.dispenseFormConfigs.forEach(item => {
        if (itemCodeMap.has(item.itemCode)) {
          itemCodeMap.delete(item.itemCode);
        } else {
          deleteItems.push(item);
        }
      });

      // 删除事项
      deleteItems.forEach(item => {
        let index = _this.itemDefinition.dispenseFormConfigs.findIndex(config => config.id == item.id);
        if (index != -1) {
          _this.itemDefinition.dispenseFormConfigs.splice(index, 1);
        }
      });
      // 添加事项
      _this.addDispenseFormConfigs(itemCodeMap);
    },
    onConfirmOk() {
      this.$refs.addDispenseFormConfiguration.collect().then(data => {
        if (data) {
          this.itemDefinition.dispenseFormConfigs.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
