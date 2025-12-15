<template>
  <div>
    <a-table
      :key="tableKey"
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="buttonTableColumn"
      :data-source="buttons"
      class="upload-button-table no-border"
    >
      <template slot="title">
        <i class="line" />
        {{ title }}
      </template>
      <template slot="titleSlot" slot-scope="text">
        <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" />
        {{ text }}
      </template>
      <template slot="btnShowTypeSlot" slot-scope="text, record">
        <span :class="btnShowType[record.btnShowType].className">{{ btnShowType[record.btnShowType].text }}</span>
      </template>
      <template slot="visibleSlot" slot-scope="text, record">
        <a-switch size="small" v-model="record.defaultFlag" checked-children="显示" un-checked-children="隐藏" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <div v-if="record.btnType === '0'">
          <Icon type="pticon iconfont icon-ptkj-shanchu" @click="delButton(index)" title="删除" />
          <WidgetDesignDrawer :id="'uploadBtnConfig' + record.id" title="编辑按钮" :designer="designer">
            <Icon type="pticon iconfont icon-ptkj-shezhi" title="编辑按钮" />
            <template slot="content">
              <UploadAddButton :button="record" :widget="widget" :designer="designer" />
            </template>
          </WidgetDesignDrawer>
        </div>
        <div v-else style="min-width: 38px" />
      </template>
      <template slot="footer" v-if="allowAddButton">
        <WidgetDesignDrawer :id="'uploadBtnConfig' + widget.id" title="添加按钮" :designer="designer">
          <a-button type="link" @click="addButton" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao" />
            添加
          </a-button>
          <template slot="content">
            <UploadAddButton v-if="buttons.length > 0" :button="newButton" :widget="widget" :designer="designer" />
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>
  </div>
</template>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'UploadButtonConfiguration',
  mixins: [draggable],
  props: {
    designer: Object,
    widget: Object,
    buttons: Array,
    title: String,
    allowAddButton: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      tableKey: generateId(),
      newButton: {},
      btnShowType: {
        show: { text: '显示类', className: 'upload-btn-show' },
        edit: { text: '编辑类', className: 'upload-btn-edit' }
      },
      buttonTableColumn: [
        { title: '名称', dataIndex: 'buttonName', width: 150, scopedSlots: { customRender: 'titleSlot' } },
        { title: '是否编辑', dataIndex: 'btnShowType', width: 65, scopedSlots: { customRender: 'btnShowTypeSlot' } },
        { title: '是否显示', dataIndex: 'defaultFlag', width: 65, scopedSlots: { customRender: 'visibleSlot' } },
        { title: '操作', dataIndex: 'operation', scopedSlots: { customRender: 'operationSlot' }, align: 'right' }
      ]
    };
  },
  created() {},
  methods: {
    addButton() {
      let btn = {
        id: generateId(),
        btnType: '0',
        btnShowType: 'edit',
        code: '',
        buttonName: '',
        defaultFlag: true,
        style: { icon: undefined },
        customEvent: {} // 自定义脚本事件 : click  ...
      };
      this.newButton = btn;
      // this.buttons.push(btn);
      this.$emit('addButton', btn);
    },
    delButton(index) {
      this.buttons.splice(index, 1);
    }
  },
  mounted() {
    this.tableDraggable(this.buttons, this.$el.querySelector('.upload-button-table tbody'), '.drag-btn-handler');
  }
};
</script>

<style lang="less" scoped>
.upload-btn-show,
.upload-btn-edit,
.upload-btn-outer {
  padding: 2px 5px;
  border-width: 1px;
  text-align: center;
  border-style: solid;
  border-radius: 4px;
  font-size: 12px;
}
.upload-btn-show {
  color: #4bb633;
  border-color: rgba(165, 218, 153, 1);
  background: rgba(237, 248, 234, 1);
}
.upload-btn-edit {
  color: #666666;
  border-color: rgba(221, 221, 221, 1);
  background: rgba(246, 246, 246, 1);
}
.upload-btn-outer {
  color: #488cee;
  border-color: rgba(163, 197, 246, 1);
  background: rgba(236, 243, 253, 1);
}
</style>
