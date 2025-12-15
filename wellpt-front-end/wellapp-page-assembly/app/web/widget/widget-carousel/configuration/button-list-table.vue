<template>
  <!-- 按钮列表 -->
  <div>
    <a-table
      rowKey="id"
      size="small"
      :showHeader="showHeader"
      :pagination="false"
      :columns="columns"
      :locale="locale"
      :dataSource="dataSource"
      :class="['no-border table-footer-right', !enableTable ? 'hidden-table-content' : '']"
    >
      <template slot="title">
        <slot name="tableTitle"></slot>
      </template>
      <template slot="titleSlot" slot-scope="text, record">
        <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" title="拖动排序" :style="{ cursor: 'move' }" />
        <slot name="titleSlot" :record="record" :text="text">
          <a-input v-model="record.title" size="small" :style="{ width: '160px' }" allowClear>
            <Icon slot="prefix" :type="record.style.icon" v-if="record.style.icon" />
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :target="record" :designer="designer" :code="record.id" v-model="record.title" />
            </template>
          </a-input>
          <a-tag color="blue" v-if="record.eventHandler != undefined && record.eventHandler.actionTypeName" :style="{ marginRight: '0px' }">
            {{ record.eventHandler.actionTypeName }}
          </a-tag>
          <a-tag
            title="自定义代码"
            color="blue"
            v-if="record.eventHandler != undefined && record.eventHandler.customScript"
            :style="{ marginRight: '0px' }"
          >
            <a-icon type="code" />
          </a-tag>
        </slot>
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <template v-if="detailDisplay === 'drawer'">
          <WidgetDesignDrawer :id="'tableItem' + record.id" title="设置" :designer="designer">
            <a-button type="link" size="small" title="设置">
              <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            </a-button>
            <template slot="content">
              <template v-if="$scopedSlots.buttonInfo">
                <slot name="buttonInfo" :currentItem="record"></slot>
              </template>
            </template>
          </WidgetDesignDrawer>
        </template>
        <a-button type="link" size="small" @click="setItem(record, index)" v-if="detailDisplay === 'modal'" title="设置">
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
    <template v-if="detailDisplay === 'modal'">
      <modal title="设置" v-model="visible" :ok="saveItme" okText="保存" :width="750">
        <template slot="content">
          <template v-if="$scopedSlots.buttonInfo">
            <slot name="buttonInfo" :currentItem="currentItem" :visible="visible" :getButtonInfoVm="getButtonInfoVm"></slot>
          </template>
          <template v-else>
            <button-info ref="refItem" v-if="visible" :formData="currentItem" title="设置" />
          </template>
        </template>
      </modal>
    </template>
  </div>
</template>

<script>
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ButtonInfo from './button-info.vue';

export default {
  name: 'ButtonListTable',
  mixins: [draggable],
  inject: ['designer', 'widget'],
  props: {
    dataSource: {
      type: Array,
      default: () => []
    },
    showHeader: {
      type: Boolean,
      default: true
    },
    createItem: {
      type: Function,
      default: () => {
        return {
          id: generateId(),
          code: undefined,
          title: undefined,
          visible: true,
          displayPosition: undefined,
          style: { icon: undefined, textHidden: false },
          switch: {
            checkedText: undefined,
            UnCheckedText: undefined,
            defaultChecked: true,
            checkedCondition: { code: undefined, operator: 'true', value: undefined }
          },
          eventHandler: { eventParams: [] },
          customEventScript: undefined
        };
      }
    },
    detailDisplay: {
      type: String,
      default: 'modal'
    },
    enableTable: {
      type: Boolean,
      default: false
    }
  },
  components: {
    Modal,
    ButtonInfo
  },
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columns: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, align: 'center', scopedSlots: { customRender: 'operationSlot' } }
      ],
      currentItem: undefined,
      visible: false
    };
  },
  mounted() {
    this.tableDraggable(this.dataSource, this.$el.querySelector('.table-footer-right tbody'), '.drag-btn-handler');
  },
  methods: {
    getButtonInfoVm(vm) {
      this.buttonInfoVm = vm;
    },
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
      this.$emit('add', this.currentItem);
    },
    saveItme(callback) {
      const validate = ({ valid, error, data }) => {
        if (valid) {
          const findIndex = this.dataSource.findIndex(item => {
            return item.id === this.currentItem.id;
          });

          if (findIndex === -1) {
            this.dataSource.push(this.currentItem);
          } else {
            this.dataSource.splice(findIndex, 1, this.currentItem);
          }
          callback(true);
        }
      };
      if (this.$scopedSlots.buttonInfo && this.buttonInfoVm && this.buttonInfoVm.validate) {
        this.buttonInfoVm.validate(validate);
        return;
      }
      this.$refs.refItem.validate(validate);
    }
  }
};
</script>
