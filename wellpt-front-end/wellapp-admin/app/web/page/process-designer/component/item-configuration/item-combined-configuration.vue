<template>
  <div>
    <div>
      <a-form-model-item label="包含事项"></a-form-model-item>
      <a-table
        rowKey="id"
        size="small"
        :pagination="false"
        :bordered="false"
        :columns="includeItemsTableColumn"
        :locale="locale"
        :data-source="itemDefinition.includeItems"
        :class="['widget-table-include-item-table no-border']"
      >
        <template slot="titleSlot" slot-scope="text, record">
          <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" :disabled="disabled" />
          <span :title="text + ' ' + record.itemCode">{{ text }}</span>
        </template>
        <template slot="frontItemSlot" slot-scope="text, record">
          <a-select
            v-model="record.frontItemCode"
            show-search
            style="width: 100%"
            :options="getFrontItemOptions(record)"
            :filter-option="filterSelectOption"
          ></a-select>
        </template>
      </a-table>
    </div>
    <div>
      <a-form-model-item label="互斥事项"></a-form-model-item>
      <a-table
        rowKey="id"
        :showHeader="false"
        size="small"
        :pagination="false"
        :bordered="false"
        :columns="mutexItemsTableColumn"
        :locale="locale"
        :data-source="itemDefinition.mutexItems"
        :class="['widget-table-mutex-item-table no-border']"
      >
        <template slot="titleSlot" slot-scope="text, record">
          <a-form :colon="false">
            <a-form-item label="互斥组别" :style="{ padding: 0 }">
              <a-input v-model="record.groupName"></a-input>
            </a-form-item>
            <a-form-item label="互斥事项" :style="{ padding: 0 }">
              <a-select v-model="record.itemCodes" mode="multiple" show-search style="width: 100%" :filter-option="filterSelectOption">
                <a-select-option v-for="d in itemDefinition.includeItems" :key="d.id">
                  {{ d.itemName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button type="link" size="small" title="删除" @click="deleteMutexItem(index)">
            <Icon type="pticon iconfont icon-ptkj-shanchu" />
          </a-button>
        </template>
        <template slot="footer">
          <a-button type="link" :style="{ paddingLeft: '7px' }" @click="onAddMutexItemClick">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
        </template>
      </a-table>
    </div>
    <div>
      <a-form-model-item label="关联事项"></a-form-model-item>
      <a-table
        rowKey="id"
        :showHeader="false"
        size="small"
        :pagination="false"
        :bordered="false"
        :columns="relatedItemsTableColumn"
        :locale="locale"
        :data-source="itemDefinition.relatedItems"
        :class="['widget-table-relate-item-table no-border']"
      >
        <template slot="titleSlot" slot-scope="text, record">
          <a-form :colon="false">
            <a-form-item label="关联组别" :style="{ padding: 0 }">
              <a-input v-model="record.groupName"></a-input>
            </a-form-item>
            <a-form-item label="关联事项" :style="{ padding: 0 }">
              <a-select v-model="record.itemCodes" mode="multiple" show-search style="width: 100%" :filter-option="filterSelectOption">
                <a-select-option v-for="d in itemDefinition.includeItems" :key="d.id">
                  {{ d.itemName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button type="link" size="small" title="删除" @click="deleteRelateItem(index)">
            <Icon type="pticon iconfont icon-ptkj-shanchu" />
          </a-button>
        </template>
        <template slot="footer">
          <a-button type="link" :style="{ paddingLeft: '7px' }" @click="onAddRelateItemClick">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script>
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '../../designer/utils';
export default {
  mixins: [draggable],
  props: {
    itemDefinition: Object
  },
  inject: ['itemFlowDesigner', 'filterSelectOption'],
  data() {
    const _this = this;
    if (!_this.itemDefinition.includeItems) {
      _this.$set(_this.itemDefinition, 'includeItems', []);
      _this.$set(_this.itemDefinition, 'mutexItems', []);
      _this.$set(_this.itemDefinition, 'relatedItems', []);
    }
    if (_this.itemDefinition.relateItems && !_this.itemDefinition.relatedItems) {
      _this.itemDefinition.relatedItems = _this.itemDefinition.relateItems;
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      includeItemsTableColumn: [
        { title: '名称', dataIndex: 'itemName', ellipsis: true, scopedSlots: { customRender: 'titleSlot' } },
        { title: '前置事项', dataIndex: 'frontItemCode', scopedSlots: { customRender: 'frontItemSlot' } }
      ],
      mutexItemsTableColumn: [
        { title: '组名', dataIndex: 'groupName', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot' } }
      ],
      relatedItemsTableColumn: [
        { title: '组名', dataIndex: 'groupName', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },
  created() {
    if (this.itemDefinition.itemDefId) {
      this.loadIncludeItems();
    }
  },
  mounted() {
    this.tableDraggable(
      this.itemDefinition.includeItems,
      this.$el.querySelector('.widget-table-include-item-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    loadIncludeItems() {
      const _this = this;
      let itemDefId = _this.itemDefinition.itemDefId;
      let itemCode = _this.itemDefinition.itemCode;
      $axios
        .get(`/proxy/api/biz/item/definition/listIncludeItemDataByItemCode?id=${itemDefId}&itemCode=${itemCode}`)
        .then(({ data: result }) => {
          if (result.data) {
            let includeItems = result.data;
            if (_this.itemDefinition.includeItems.length == 0) {
              _this.addIncludeItems(includeItems, true);
            } else {
              _this.updateIncludeItems(includeItems);
            }
          }
        });
    },
    addIncludeItems(includeItems, tip = false) {
      const _this = this;
      let addedItemNames = [];
      includeItems.forEach(item => {
        addedItemNames.push(item.itemName);
        _this.itemDefinition.includeItems.push({
          id: 'include_item_' + generateId('SF'),
          ...item
        });
      });

      if (tip && addedItemNames.length > 0) {
        _this.$message.success(`自动更新包含的事项：新增[${addedItemNames}]`);
        if (_this.itemFlowDesigner && _this.itemFlowDesigner.mergeItemFlowIfRequired) {
          _this.itemFlowDesigner.mergeItemFlowIfRequired();
        }
      }
    },
    updateIncludeItems(includeItems) {
      const _this = this;
      let itemCodeMap = new Map();
      includeItems.forEach(item => {
        itemCodeMap.set(item.itemCode, item);
      });

      let deleteItems = [];
      _this.itemDefinition.includeItems.forEach(item => {
        if (itemCodeMap.has(item.itemCode)) {
          itemCodeMap.delete(item.itemCode);
        } else {
          deleteItems.push(item);
        }
      });

      // 删除事项
      deleteItems.forEach(item => {
        let index = _this.itemDefinition.includeItems.findIndex(config => config.itemCode == item.itemCode);
        if (index != -1) {
          _this.itemDefinition.includeItems.splice(index, 1);
        }
      });
      // 添加事项
      _this.addIncludeItems(itemCodeMap, false);

      let addedItemNames = [];
      let deletedItemNames = [];
      itemCodeMap.forEach(item => {
        addedItemNames.push(item.itemName);
      });
      deleteItems.forEach(item => {
        deletedItemNames.push(item.itemName);
      });
      if (addedItemNames.length > 0 || deletedItemNames.length > 0) {
        _this.$message.success(`自动更新包含的事项：新增[${addedItemNames}]、删除[${deletedItemNames}]`);
        if (_this.itemFlowDesigner && _this.itemFlowDesigner.mergeItemFlowIfRequired) {
          _this.itemFlowDesigner.mergeItemFlowIfRequired();
        }
      }
    },
    getFrontItemOptions(record) {
      return [
        { label: '', value: '' },
        ...this.itemDefinition.includeItems
          .filter(item => item.itemCode != record.itemCode)
          .map(item => ({ label: item.itemName, value: item.itemCode }))
      ];
    },
    onAddMutexItemClick() {
      this.itemDefinition.mutexItems.push({
        id: generateId('SF'),
        groupName: '',
        itemCodes: []
      });
    },
    // 删除互斥事项
    deleteMutexItem(index) {
      this.itemDefinition.mutexItems.splice(index, 1);
    },
    onAddRelateItemClick() {
      this.itemDefinition.relatedItems.push({
        id: generateId('SF'),
        groupName: '',
        itemCodes: []
      });
    },
    // 删除关联事项
    deleteRelateItem(index) {
      this.itemDefinition.relatedItems.splice(index, 1);
    }
  }
};
</script>

<style></style>
