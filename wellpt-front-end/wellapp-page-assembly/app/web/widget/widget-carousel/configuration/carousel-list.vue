<template>
  <div style="margin: 0 20px">
    <a-table size="small" rowKey="id" :columns="columns" :pagination="false" :dataSource="dataSource">
      <template slot="titleSlot" slot-scope="text, record, index">
        <Icon
          title="拖动排序"
          type="pticon iconfont icon-ptkj-tuodong"
          class="drag-handler"
          :style="{ cursor: 'move', marginRight: '4px' }"
        />
        <img :src="record.src" :style="{ maxWidth: '40px', maxHeight: '40px' }" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" @click="setItem(record, index)" title="设置">
          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        </a-button>
        <!-- 删除 -->
        <a-button type="link" size="small" @click="delItem(index, record)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
      </template>
      <template slot="footer">
        <a-button type="link" @click="addItem" icon="plus">添加</a-button>
      </template>
    </a-table>

    <WidgetDesignDrawer id="carousel_info" title="设置" v-model="visible" :designer="designer">
      <template slot="content">
        <carousel-info :formData="currentItem" ref="refItem" />
      </template>
      <template slot="footer">
        <a-button size="small" type="primary" @click.stop="saveItme">确定</a-button>
      </template>
    </WidgetDesignDrawer>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';
import CarouselInfo from './carousel-info.vue';
export default {
  name: 'CarouselList',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object,
    dataSource: Array
  },
  components: {
    CarouselInfo
  },
  data() {
    return {
      columns: [
        { title: '缩略图', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, align: 'center', scopedSlots: { customRender: 'operationSlot' } }
      ],
      currentItem: undefined,
      visible: false,
      createItem: () => {
        return {
          id: generateId(),
          title: '',
          src: undefined,
          size: 'auto',
          style: {
            // background: '#364d79'
          },
          buttonStyle: {
            xAxis: 0,
            xAxisUnit: 'px',
            xAxisDir: 'left',
            yAxis: 0,
            yAxisUnit: 'px',
            yAxisDir: 'top'
          },
          buttonEnable: false,
          buttons: [],
          buttonGroup: {
            type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
            groups: [
              // 固定分组
              // {name:,buttonIds:[]}
            ],
            dynamicGroupName: '更多', //动态分组名称
            dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
          }
        };
      }
    };
  },
  mounted() {
    this.tableDraggable(this.dataSource, this.$el.querySelector('.ant-table-tbody'), '.drag-handler');
  },
  methods: {
    setItem(record, index) {
      this.currentIndex = index;
      this.currentItem = JSON.parse(JSON.stringify(record));
      this.visible = true;
    },
    delItem(index) {
      this.dataSource.splice(index, 1);
    },
    addItem() {
      this.currentItem = this.createItem();
      this.visible = true;
    },
    saveItme() {
      this.$refs.refItem.validate(({ valid, error, data }) => {
        if (valid) {
          const findIndex = this.dataSource.findIndex(item => {
            return item.id === this.currentItem.id;
          });

          if (findIndex === -1) {
            this.dataSource.push(this.currentItem);
          } else {
            this.dataSource.splice(findIndex, 1, this.currentItem);
          }
          this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
        }
      });
    }
  }
};
</script>
