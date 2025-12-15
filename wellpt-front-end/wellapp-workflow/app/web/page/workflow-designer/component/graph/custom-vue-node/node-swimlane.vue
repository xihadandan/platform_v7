<template>
  <div class="node-swimlane-container">
    <div class="node-swimlane-icon">
      <i class="iconfont icon-yongdao-01"></i>
    </div>
    <div class="node-swimlane-content">
      <table class="node-swimlane-table">
        <thead>
          <tr v-if="nodeData.layout.length > 1">
            <th v-for="(item, index) in nodeData.columns" :key="index" :style="{ width: Number(item.width) + 'px' }">{{ item.title }}</th>
          </tr>
          <tr
            class="layout-columns-thead"
            v-else-if="nodeData.layout.length === 1 && nodeData.layout.includes(swimlaneLayoutOptions[0]['value'])"
          >
            <th
              v-for="(item, index) in nodeData.columns"
              :key="index"
              :class="[item['dataIndex']]"
              :style="{ width: Number(item.width) + 'px' }"
            >
              {{ item.title }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in computedRows" :key="index">
            <template v-for="(value, key) in item">
              <template v-if="key !== 'height' && key !== 'id'">
                <template v-if="nodeData.layout.length > 1">
                  <td :attr-key="key" :key="key" :style="{ height: Number(item.height) + 'px' }">
                    {{ value }}
                  </td>
                </template>
                <template v-else-if="nodeData.layout.length === 1 && nodeData.layout.includes(swimlaneLayoutOptions[0]['value'])">
                  <td v-if="key !== 'selection'" :attr-key="key" :key="key" :style="{ height: Number(nodeData.columnHeight) + 'px' }">
                    {{ value }}
                  </td>
                </template>
                <template v-else-if="nodeData.layout.length === 1 && nodeData.layout.includes(swimlaneLayoutOptions[1]['value'])">
                  <td
                    :attr-key="key"
                    :key="key"
                    :style="{
                      width: Number(nodeData.rowWidth) + 'px',
                      height: Number(item.height) + 'px'
                    }"
                  >
                    {{ value }}
                  </td>
                </template>
              </template>
            </template>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { swimlaneLayoutOptions } from '../../designer/constant';
export default {
  name: 'NodeSwimlane',
  inject: ['getNode', 'graphInstance'],
  data() {
    let nodeData = {};
    const node = this.getNode();
    if (node) {
      nodeData = node.getData();
    }
    return {
      nodeData,
      swimlaneLayoutOptions
    };
  },
  computed: {
    computedRows() {
      let rows = this.nodeData.rows;
      if (this.nodeData.layout.length === 1) {
        if (this.nodeData.layout.includes(this.swimlaneLayoutOptions[0]['value'])) {
          rows = [this.nodeData.rows[0]];
        } else if (this.nodeData.layout.includes(this.swimlaneLayoutOptions[1]['value'])) {
          rows = this.nodeData.rows.map(item => {
            return {
              id: item.id,
              selection: item.selection,
              title: item.title,
              height: item.height
            };
          });
        }
      }
      return rows;
    },
    swimlanceSize() {
      let NodeSwimlaneWidth = 0;
      this.nodeData.columns.forEach(c => {
        NodeSwimlaneWidth += Number(c.width);
      });

      let NodeSwimlaneHeight = 0;
      this.nodeData.rows.forEach(c => {
        NodeSwimlaneHeight += Number(c.height);
      });
      return {
        width: NodeSwimlaneWidth + 'px',
        height: NodeSwimlaneHeight + 'px'
      };
    }
  }
};
</script>
