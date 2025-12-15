<template>
  <div class="widget-edit-uni-list-buttons">
    <a-table
      rowKey="uuid"
      :pagination="false"
      bordered
      size="small"
      :columns="buttonsTableColumn"
      :data-source="widget.configuration.buttons"
      :locale="locale"
      class="pt-table widget-edit-uni-list-buttons-table"
      style="--pt-table-td-padding: var(--w-padding-2xs)"
    >
      <template slot="textSlot" slot-scope="text, record">
        <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }" />
        <a-input v-model="record.text" size="small" :style="{ width: 'calc(100% - 50px)' }" class="addon-padding-3xs">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :designer="designer" :code="record.uuid" :target="record" v-model="record.text" />
          </template>
        </a-input>
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button
          type="link"
          size="small"
          @click="record.defaultVisible = !record.defaultVisible"
          :title="record.defaultVisible ? '隐藏' : '显示'"
        >
          <Icon :type="record.defaultVisible ? 'pticon iconfont icon-wsbs-xianshi' : 'pticon iconfont icon-wsbs-yincang'"></Icon>
        </a-button>
        <WidgetDesignDrawer :id="'listbuttonConfig' + record.uuid" title="编辑" :designer="designer">
          <a-button type="link" size="small" title="编辑">
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
          </a-button>
          <template slot="content">
            <buttonConfiguration :widget="widget" :designer="designer" :button="record" />
          </template>
        </WidgetDesignDrawer>
        <a-button type="link" size="small" @click="delButtonItem(index)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
        </a-button>
      </template>

      <template slot="footer">
        <WidgetDesignDrawer :id="'listbuttonConfig' + widget.id" title="新增" :designer="designer">
          <a-button type="link" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            新增
          </a-button>
          <template slot="content">
            <buttonConfiguration :widget="widget" :designer="designer" :button="newButton" />
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>
    <a-form-model-item>
      <template slot="label">
        <a-tooltip placement="bottomRight">
          <template slot="title">
            <ul style="padding-inline-start: 20px; margin-block-end: 0px">
              <li>行下按钮超过3个按钮，两个显示，其余放在【更多】按钮里</li>
            </ul>
          </template>
          行下【更多】按钮类型
          <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
        </a-tooltip>
      </template>
      <a-select :options="buttonTypeOptions" v-model="widget.configuration.rowBottomMoreButtonType" :style="{ width: '100%' }"></a-select>
    </a-form-model-item>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import buttonConfiguration from './button-configuration.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'ListButtonsConfiguration',
  inject: ['pageContext'],
  mixins: [draggable],
  props: {
    widget: Object,
    columnIndexOptions: Array,
    designer: Object
  },
  data() {
    let defaultNewButton = {
      checked: false,
      code: '',
      status: '',
      type: 'primary',
      mini: false,
      plain: false,
      group: '',
      position: ['2'],
      text: '',
      icon: {
        className: undefined
      },
      defaultVisible: true,
      eventManger: {}
    };
    return {
      options: {},
      newButton: deepClone(defaultNewButton),
      defaultNewButton,
      locale: {
        emptyText: <span>暂无数据</span>
      },
      buttonsTableColumn: [
        { title: '名称', dataIndex: 'text', scopedSlots: { customRender: 'textSlot' } },
        { title: '操作', dataIndex: 'operation', width: 120, scopedSlots: { customRender: 'operationSlot' } }
      ],
      buttonTypeOptions: [
        { label: '主要按钮', value: 'primary' },
        { label: '默认按钮', value: 'default' },
        { label: '次要按钮', value: 'minor' },
        { label: '警告按钮', value: 'danger' },
        { label: '链接按钮', value: 'link' },
        { label: '文字按钮', value: 'text' }
      ]
    };
  },

  beforeCreate() {},
  components: { buttonConfiguration },
  computed: {},
  created() {
    if (!this.widget.configuration.buttons) {
      this.$set(this.widget.configuration, 'buttons', []);
    }
    if (!this.widget.configuration.rowBottomMoreButtonType) {
      this.$set(this.widget.configuration, 'rowBottomMoreButtonType', 'link');
    }
  },
  methods: {
    onConfirmOk() {
      // this.newButton.uuid = generateId();
      this.widget.configuration.buttons.push(deepClone(this.newButton));
      this.newButton = deepClone(this.defaultNewButton);
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },
    delButtonItem(index) {
      this.widget.configuration.buttons.splice(index, 1);
    }
  },
  beforeMount() {},
  mounted() {
    this.tableDraggable(
      this.widget.configuration.buttons,
      this.$el.querySelector('.widget-edit-uni-list-buttons-table tbody'),
      '.drag-column-handler'
    );
  },
  watch: {
    'widget.configuration.buttons': {
      deep: true,
      handler: v => {
        console.log(v);
      }
    }
  }
};
</script>
