<template>
  <div>
    <div
      :style="{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: '7px'
      }"
    >
      <!-- <a-space>
        <Drawer
          title="添加视图属性"
          :width="400"
          :container="getDrawerContainer"
          :wrapStyle="{ position: 'absolute' }"
          okText="保存"
          :ok="confirmAddViewColumn"
          :destroyOnClose="true"
        >
          <a-button size="small" icon="plus">添加</a-button>
          <template slot="content"></template>
        </Drawer>
      </a-space> -->
      <a-input-search v-model.trim="searchKeyword" allow-clear style="width: 300px" placeholder="按显示名称、编码、描述搜索" />
    </div>

    <a-table
      rowKey="location"
      :pagination="false"
      :bordered="false"
      :columns="columnDefine"
      :data-source="vViewColumns"
      class="widget-table-col-table no-border pt-table"
      :scroll="{ y: 600 }"
    >
      <template slot="seqSlot" slot-scope="text, record, index">
        {{ index + 1 }}
      </template>

      <template slot="titleSlot" slot-scope="text, record">
        <a-input v-model="record.title" @change="onChangeViewPropName(record)" />
      </template>
      <template slot="dataTypeSlot" slot-scope="text, record">
        <template v-if="text == 'varchar'">字符</template>
        <template v-else-if="text == 'number'">数字</template>
        <template v-else-if="text == 'timestamp'">日期时间</template>
        <template v-else-if="text == 'clob'">大字段</template>
      </template>
      <template slot="fullTitleSlot" slot-scope="text, record">
        <a-tag color="blue" :title="text" class="w-ellipsis" style="max-width: 100%">{{ text }}</a-tag>
      </template>
      <template slot="hiddenSlot" slot-scope="text, record, index">
        <a-switch v-model="record.hidden" size="small" />
      </template>
    </a-table>
  </div>
</template>

<style lang="less"></style>

<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';

export default {
  name: 'DataModelViewObjectProp',
  mixins: [],
  inject: ['x6Designer', 'getViewObjectDesign'],
  props: {
    viewColumns: Array
  },
  data() {
    return {
      searchKeyword: undefined,
      rows: [],
      columnDefine: [
        { title: '序号', dataIndex: 'seq', scopedSlots: { customRender: 'seqSlot' }, width: 60, align: 'center' },
        { title: '显示名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' }, width: 200 },
        { title: '类型', dataIndex: 'dataType', scopedSlots: { customRender: 'dataTypeSlot' }, width: 200 },
        { title: '编码', dataIndex: 'alias' },
        { title: '源属性', dataIndex: 'fullTitle', scopedSlots: { customRender: 'fullTitleSlot' }, width: 300 },
        { title: '隐藏', dataIndex: 'hidden', scopedSlots: { customRender: 'hiddenSlot' }, width: 150 },
        { title: '描述', dataIndex: 'remark' }
      ]
    };
  },

  watch: {},
  beforeCreate() {},
  components: { Drawer },
  computed: {
    vViewColumns() {
      let cols = [];
      for (let i = 0, len = this.viewColumns.length; i < len; i++) {
        if (this.viewColumns[i].return !== true) {
          continue;
        }
        if (
          this.searchKeyword == undefined ||
          this.searchKeyword == '' ||
          this.viewColumns[i].alias.toLowerCase().indexOf(this.searchKeyword.toLowerCase()) != -1 ||
          (this.viewColumns[i].remark != undefined && this.viewColumns[i].remark.indexOf(this.searchKeyword) != -1) ||
          (this.viewColumns[i].title != undefined && this.viewColumns[i].title.indexOf(this.searchKeyword) != -1)
        ) {
          cols.push(this.viewColumns[i]);
        }
      }
      return cols;
    }
  },
  created() {},
  methods: {
    onChangeViewPropName(item) {
      this.$emit('viewObjectPropChange', item);
    },
    getDrawerContainer() {
      return document.querySelector('#data-model-detail-body').querySelector('.ant-card-body');
    }
  },
  beforeMount() {},
  mounted() {},
  watch: {}
};
</script>
