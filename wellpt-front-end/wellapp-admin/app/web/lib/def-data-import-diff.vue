<template>
  <div>
    <a-row v-if="diffNodes.length > 0">
      <a-col span="6">
        <a-list bordered :data-source="diffNodes">
          <a-list-item slot="renderItem" slot-scope="item" @click="onItemClick(item)">
            {{ item.name }}
          </a-list-item>
        </a-list>
      </a-col>
      <a-col span="18">
        <a-table
          style="height: 330px; overflow: auto"
          rowKey="fieldName"
          :columns="dataDifferenceColumns"
          :data-source="dataDifferenceDetails"
          :pagination="{ pageSize: 1000 }"
        >
          <template slot="fieldNameSlot" slot-scope="text, record">
            <span :style="{ color: record.isDifference ? conflictColor : '' }">
              {{ record.fieldName }}
            </span>
          </template>
          <template slot="controlValueSlot" slot-scope="text, record">
            <span :style="{ color: record.isDifference ? conflictColor : '' }">
              {{ record.controlValue }}
            </span>
          </template>
          <template slot="testValueSlot" slot-scope="text, record">
            <span :style="{ color: record.isDifference ? conflictColor : '' }">
              {{ record.testValue }}
            </span>
          </template>
          <template slot="footer"></template>
        </a-table>
      </a-col>
    </a-row>
    <div v-else class="no-diff">没有差异数据！</div>
  </div>
</template>

<script>
export default {
  props: {
    uploadFile: Object,
    treeData: Array
  },
  data() {
    return {
      conflictColor: 'orange',
      diffNodes: [],
      dataDifferenceColumns: [
        { title: '字段', dataIndex: 'fieldName', width: '100px', scopedSlots: { customRender: 'fieldNameSlot' } },
        { title: '已上传数据', dataIndex: 'controlValue', width: '200px', scopedSlots: { customRender: 'controlValueSlot' } },
        { title: '系统数据', dataIndex: 'testValue', width: '200px', scopedSlots: { customRender: 'testValueSlot' } }
      ],
      dataDifferenceDetails: []
    };
  },
  created() {
    let diffNodes = [];
    this.extractDiffNodes(this.treeData || [], diffNodes);
    this.diffNodes = diffNodes;
  },
  methods: {
    extractDiffNodes(nodes, diffNodes) {
      nodes.forEach(node => {
        if (node.data && node.data.color == 'orange') {
          diffNodes.push(node);
        }
        this.extractDiffNodes(node.children || [], diffNodes);
      });
    },
    onItemClick(item) {
      $axios
        .post('/json/data/services', {
          serviceName: 'iexportService',
          methodName: 'getDifference',
          args: JSON.stringify([this.uploadFile.fileID, item.id, item.data.type])
        })
        .then(({ data }) => {
          if (data.data) {
            this.dataDifferenceDetails = data.data.dataDifferenceDetails || [];
            this.dataDifferenceDetails.sort((a, b) => {
              if (a.isDifference) {
                return -1;
              } else if (b.isDifference) {
                return 1;
              }
              return 0;
            });
          }
        });
    }
  }
};
</script>

<style lang="less" scoped>
.no-diff {
  margin-top: calc(12%);
  text-align: center;
  font-size: 20px;
  color: #52c41a;
}
</style>
