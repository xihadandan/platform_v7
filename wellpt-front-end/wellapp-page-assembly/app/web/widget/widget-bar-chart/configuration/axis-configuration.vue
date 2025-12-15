<template>
  <div>
    <a-table
      class="widget-chart-axis-table no-border"
      rowKey="id"
      :columns="columnDefine"
      :dataSource="widget.configuration.axis"
      :pagination="false"
      :bordered="false"
      size="small"
    >
      <template slot="nameSlot" slot-scope="text, record, index">
        <a-input v-model="record.name" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <WidgetDesignDrawer :id="'seriesConfig' + record.id" title="编辑坐标系" :designer="designer">
          <a-button size="small" type="link" title="编辑坐标系">
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
          </a-button>
          <template slot="content">
            <AxisItemConfiguration :widget="widget" :designer="designer" :axis="record" :axisIndex="index" />
          </template>
        </WidgetDesignDrawer>
        <a-button size="small" type="link" v-if="index > 0" @click="deleteAxis(index)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
        </a-button>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer :id="'seriesConfig' + widget.id" title="新增坐标系" :designer="designer">
          <a-button type="link" icon="plus" size="small" :style="{ paddingLeft: '7px' }" @click="addNewAxis">新增</a-button>
          <template slot="content">
            <AxisItemConfiguration
              :widget="widget"
              :designer="designer"
              :axis="newAxisItem"
              :axisIndex="widget.configuration.axis.length"
              ref="newAxisItem"
            />
          </template>
          <template slot="footer" slot-scope="{ close }">
            <a-button size="small" type="primary" @click.stop="onConfirmOk(close)">确定</a-button>
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import AxisItemConfiguration from './axis-item-configuration.vue';
export default {
  name: 'AxisConfiguration',
  props: { widget: Object, designer: Object },
  components: { AxisItemConfiguration },
  computed: {},
  data() {
    return {
      columnDefine: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      newAxisItem: this.getDefaultAxisItemConfiguration()
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    getDefaultAxisItemConfiguration() {
      return {
        name: '坐标系 ' + (this.widget.configuration.axis.length + 1),
        grid: {
          id: generateId(9),
          show: false,
          leftUnit: 'px',
          topUnit: 'px',
          rightUnit: 'px',
          bottomUnit: 'px',
          widthUnit: 'auto',
          width: undefined,
          height: undefined,
          heightUnit: 'auto'
        },
        x: {
          type: 'category',
          show: true,
          gridIndex: this.widget.configuration.axis.length,
          axisLabel: {
            color: '#333'
          }
        },
        y: {
          type: 'value',
          show: true,
          gridIndex: this.widget.configuration.axis.length,
          axisLabel: {
            color: '#333'
          }
        }
      };
    },

    deleteAxis(index) {
      this.widget.configuration.axis.splice(index, 1);
      this.adjustGridPositionSize();
    },
    addNewAxis() {
      this.newAxisItem = this.getDefaultAxisItemConfiguration();
    },
    onConfirmOk(close) {
      let _axisItem = JSON.parse(JSON.stringify(this.newAxisItem));
      _axisItem.id = generateId(9);
      this.widget.configuration.axis.push(_axisItem);
      this.newAxisItem = this.getDefaultAxisItemConfiguration();
      this.adjustGridPositionSize();
      close();
    },
    adjustGridPositionSize() {
      /**
       * 当坐标轴多个时候，需要自动调整位置
       * 按一行两个单元格进行排布
       */

      let length = this.widget.configuration.axis.length,
        avgHeight = (100 - 20) / Math.ceil(length / 2) + '%';
      if (length > 1) {
        for (let i = 0; i < length; i++) {
          let axis = this.widget.configuration.axis[i],
            line = Math.floor(i / 2) + 1;

          if (i % 2 == 0) {
            this.$set(axis.grid, 'left', 7);
            this.$set(axis.grid, 'top', (line - 1) * parseInt(avgHeight) + 12 + '%');
            axis.grid.topUnit = '%';
            axis.grid.leftUnit = '%';
            axis.grid.width = '38%';
            axis.grid.widthUnit = '%';
            axis.grid.height = avgHeight;
            axis.grid.heightUnit = '%';
          } else {
            axis.grid.left = undefined;
            this.$set(axis.grid, 'top', (line - 1) * parseInt(avgHeight) + 12 + '%');
            this.$set(axis.grid, 'right', 7);
            axis.grid.rightUnit = '%';
            axis.grid.width = '38%';
            axis.grid.widthUnit = '%';
            axis.grid.height = avgHeight;
            axis.grid.heightUnit = '%';
          }
        }
      } else if (length == 1) {
        let grid = this.widget.configuration.axis[0].grid;
        grid.left = 10;
        grid.leftUnit = '%';
        grid.top = 60;
        grid.topUnit = 'px';
        grid.width = undefined;
        grid.widthUnit = 'auto';
        grid.height = undefined;
        grid.heightUnit = 'auto';
        delete grid.right;
        delete grid.bottom;
      }
    }
  }
};
</script>
