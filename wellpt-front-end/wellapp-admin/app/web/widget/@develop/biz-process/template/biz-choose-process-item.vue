<template>
  <div>
    <a-row>
      <a-col>业务流程名称：{{ processDefinition.name }}</a-col>
    </a-row>
    <a-tabs>
      <a-tab-pane v-for="node in processDefinition.nodes" :key="node.id" :tab="node.name">
        <a-table
          rowKey="id"
          size="small"
          :pagination="false"
          :bordered="false"
          :columns="itemTableColumn"
          :data-source="node.items"
          :rowSelection="rowSelection[node.id]"
          :class="['widget-table-item-table no-border']"
        >
          <template slot="stateSlot" slot-scope="text, record">
            {{ record.currentStateName }}
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
export default {
  props: {
    processDefinition: Object
  },
  data() {
    let rowSelection = {};
    let allItems = [];
    this.processDefinition.nodes.forEach(node => {
      rowSelection[node.id] = {
        selectedRowKeys: [],
        selectedRows: [],
        onChange: (selectedRowKeys, selectedRows) => {
          this.onItemSelectChange(selectedRowKeys, selectedRows, node);
        }
      };
      if (node.items) {
        node.items.forEach(item => {
          allItems.push(item);

          // 组合事项包含的事项
          if (item.includeItems && item.includeItems.length > 0) {
            item.children = item.includeItems;
            item.includeItems.forEach(includeItem => {
              includeItem.parentItem = item;
              allItems.push(includeItem);
            });
          }
        });
      }
    });
    this.allItems = allItems;
    return {
      itemTableColumn: [
        { title: '名称', dataIndex: 'itemName' },
        { title: '当前办理情况', dataIndex: 'currentStateName', scopedSlots: { customRender: 'stateSlot' } }
      ],
      rowSelection
    };
  },
  created() {
    let itemIds = [];
    this.allItems.forEach(item => {
      itemIds.push(item.id);
    });
    $axios
      .post(`/proxy/api/biz/process/item/instance/listItemStates?processDefId=${this.processDefinition.id}`, itemIds)
      .then(({ data }) => {
        if (data.data) {
          let stateMap = data.data || {};
          itemIds.forEach(itemId => {
            let state = stateMap[itemId];
            let item = this.allItems.find(item => item.id == itemId);
            if (state) {
              if (state == '10' || state == '20') {
                this.$set(item, 'currentStateName', '办理中');
              } else if (state == '30') {
                this.$set(item, 'currentStateName', '已办理');
              } else {
                this.$set(item, 'currentStateName', '草稿');
              }
            } else {
              this.$set(item, 'currentStateName', '未办理');
            }
          });
        }
      });
  },
  methods: {
    onItemSelectChange(selectedRowKeys, selectedRows, node) {
      this.rowSelection[node.id].selectedRowKeys = selectedRowKeys;
      this.rowSelection[node.id].selectedRows = selectedRows;
    },
    getSelectedItems() {
      let items = [];
      for (let nodeId in this.rowSelection) {
        let selection = this.rowSelection[nodeId];
        selection.selectedRows.forEach(item => {
          items.push(item);
        });
      }
      return items;
    },
    getSelectedItemIds() {
      let itemIds = [];
      for (let nodeId in this.rowSelection) {
        let selection = this.rowSelection[nodeId];
        selection.selectedRowKeys.forEach(itemId => {
          itemIds.push(itemId);
        });
      }
      return itemIds;
    }
  }
};
</script>

<style></style>
