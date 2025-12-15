<template>
  <!-- 按钮固定分组 -->
  <a-table
    :showHeader="false"
    rowKey="id"
    :pagination="false"
    :bordered="false"
    size="small"
    :locale="locale"
    :columns="columns"
    :dataSource="dataSource"
    class="table-footer-right"
  >
    <template slot="titleSlot" slot-scope="text, record">
      <a-form :colon="false">
        <a-form-item label="分组名称" :style="{ padding: 0 }">
          <a-input v-model="record.name" size="small" allowClear>
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :target="record" :designer="designer" :code="record.id" v-model="record.name" />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item label="分组按钮" :style="{ padding: 0 }">
          <a-select size="small" mode="multiple" v-model="record.buttonIds" style="width: 100%" :options="groupButtonOptions" />
        </a-form-item>
        <a-form-item
          label="分组按钮类型"
          :style="{ padding: 0 }"
          :label-col="{ style: { width: '100px' } }"
          :wrapper-col="{ style: { width: 'calc(100% - 100px)', float: 'right' } }"
        >
          <a-select size="small" :options="buttonTypeOptions" v-model="record.type" :style="{ width: '100%' }" />
        </a-form-item>
      </a-form>
    </template>
    <template slot="operationSlot" slot-scope="text, record, index">
      <a-button type="link" @click="delItem(index)" title="删除"><Icon type="pticon iconfont icon-ptkj-shanchu"></Icon></a-button>
    </template>
    <template slot="footer">
      <a-button type="link" @click="addItem" icon="plus">添加</a-button>
    </template>
  </a-table>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import { buttonTypeOptions } from '../../commons/constant';

export default {
  name: 'ButtonFixedGroup',
  inject: ['designer', 'widget'],
  props: {
    dataSource: {
      type: Array,
      default: () => []
    },
    buttons: {
      type: Array,
      default: () => []
    }
  },
  data() {
    const typeOptions = buttonTypeOptions.slice(0, 5);
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columns: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 30, scopedSlots: { customRender: 'operationSlot' } }
      ],
      buttonTypeOptions: typeOptions
    };
  },
  computed: {
    groupButtonOptions() {
      let options = [];
      for (let i = 0, len = this.buttons.length; i < len; i++) {
        options.push({ label: this.buttons[i].title, value: this.buttons[i].id });
      }
      return options;
    }
  },
  methods: {
    addItem() {
      this.dataSource.push({
        id: generateId(),
        name: '',
        buttonIds: []
      });
    },
    delItem(index) {
      this.dataSource.splice(index, 1);
    }
  }
};
</script>
