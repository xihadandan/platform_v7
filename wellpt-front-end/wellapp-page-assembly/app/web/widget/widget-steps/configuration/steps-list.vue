<template>
  <div style="margin: 0 20px">
    <a-table size="small" rowKey="id" :columns="columns" :pagination="false" :dataSource="dataSource">
      <template slot="titleSlot" slot-scope="text, record">
        <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-handler" :style="{ cursor: 'move' }" />
        <span>{{ text }}</span>
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" title="设置" @click="setItem(record, index)">
          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        </a-button>
        <!-- 删除 -->
        <a-button type="link" size="small" title="删除" @click="delItem(index, record)">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
      </template>
      <template slot="footer">
        <a-button type="link" @click="addItem" icon="plus">添加</a-button>
      </template>
    </a-table>

    <WidgetDesignDrawer id="step_info" title="设置" v-model="visible" :designer="designer">
      <template slot="content">
        <step-info :formData="currentItem" :key="currentItem.id" ref="refItem" />
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
import StepInfo from './step-info.vue';
export default {
  name: 'StepsList',
  mixins: [draggable],
  inject: ['designer'],
  props: {
    dataSource: Array
  },
  components: {
    StepInfo
  },
  data() {
    return {
      columns: [
        { title: '标题', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 90, align: 'center', scopedSlots: { customRender: 'operationSlot' } }
      ],
      currentItem: {},
      visible: false,
      createItem: () => {
        return {
          id: generateId(),
          wtype: 'WidgetStepItem',
          title: '',
          subTitle: '',
          description: '',
          disabled: false,
          status: 'wait',
          style: {
            icon: undefined
          },
          configuration: {
            widgets: [],
            eventHandler: {
              actionType: 'redirectPage',
              pageType: 'page',
              trigger: 'click',
              pageUuid: undefined
            }
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
          this.visible = false;
        }
      });
    }
  }
};
</script>
