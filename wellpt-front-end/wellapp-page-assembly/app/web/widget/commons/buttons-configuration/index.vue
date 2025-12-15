<template>
  <div v-if="button">
    <a-table
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="buttonTableColumn"
      :locale="locale"
      :data-source="button.buttons"
      :class="['buttons-table no-border', !button.enable ? 'hidden-table-content' : '']"
    >
      <template slot="title">
        <template>
          <i class="line" />
          {{ title }}
        </template>
        <a-switch v-model="button.enable" :style="{ float: 'right', 'margin-right': '4px;' }" title="启用按钮" />
      </template>
      <template slot="titleSlot" slot-scope="text, record">
        <Icon
          v-show="button.buttons.length > 1"
          type="pticon iconfont icon-ptkj-tuodong"
          class="drag-btn-handler"
          :style="{ cursor: 'move' }"
          title="拖动排序"
        />
        <a-input v-model="record.title" size="small" :style="{ width: '100px' }">
          <Icon slot="prefix" :type="record.style.icon" v-if="record.style.icon" />
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
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button v-if="record.unDeleted !== true" type="link" size="small" @click="delButton(index)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
        <WidgetDesignDrawer
          :zIndex="zIndex"
          :id="'WidgetTableBtnConfig' + record.id"
          title="编辑按钮"
          :designer="designer"
          :closeOpenDrawer="false"
        >
          <a-button type="link" size="small" title="编辑按钮">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          </a-button>
          <template slot="content">
            <ButtonDetails :button="record" :widget="widget" :designer="designer">
              <template slot="extraVisibleCompleteSelectGroup">
                <slot name="extraVisibleCompleteSelectGroup" />
              </template>
              <template slot="eventUrlHelpSlot">
                <slot name="eventUrlHelpSlot" />
              </template>
              <template slot="eventParamValueHelpSlot">
                <slot name="eventParamValueHelpSlot" />
              </template>
            </ButtonDetails>
          </template>
        </WidgetDesignDrawer>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer
          :zIndex="zIndex"
          :id="'WidgetTableBtnConfig' + widget.id"
          title="添加按钮"
          :designer="designer"
          :closeOpenDrawer="false"
        >
          <a-button type="link" icon="plus" :style="{ paddingLeft: '7px' }" v-show="button.buttons.length < limitButtonCount">
            添加
          </a-button>
          <template slot="content">
            <ButtonDetails ref="addWidgetTableBtnConfig" :button="newButton" :widget="widget" :designer="designer" :key="newButton.id">
              <template slot="extraVisibleCompleteSelectGroup">
                <slot name="extraVisibleCompleteSelectGroup" />
              </template>
              <template slot="eventUrlHelpSlot">
                <slot name="eventUrlHelpSlot" />
              </template>
              <template slot="eventParamValueHelpSlot">
                <slot name="eventParamValueHelpSlot" />
              </template>
            </ButtonDetails>
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
          </template>
        </WidgetDesignDrawer>

        <a-form-model-item label="按钮分组" class="item-lh" v-if="showGroup">
          <a-radio-group size="small" v-model="button.buttonGroup.type" button-style="solid">
            <a-radio-button value="notGroup">不分组</a-radio-button>
            <a-radio-button value="fixedGroup">固定分组</a-radio-button>
            <a-radio-button value="dynamicGroup">动态分组</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="showGroup">
          <div v-show="button.buttonGroup.type === 'dynamicGroup'">
            <a-form-model-item label="分组名称">
              <a-input v-model="button.buttonGroup.dynamicGroupName">
                <template slot="addonAfter">
                  <WI18nInput
                    :widget="widget"
                    :target="button.buttonGroup"
                    :designer="designer"
                    code="dynamicGroupName"
                    v-model="button.buttonGroup.dynamicGroupName"
                  />
                </template>
              </a-input>
            </a-form-model-item>
            <a-form-model-item label="分组按钮数">
              <a-input-number v-model="button.buttonGroup.dynamicGroupBtnThreshold" />
            </a-form-model-item>
          </div>
          <a-table
            v-show="button.buttonGroup.type === 'fixedGroup'"
            :showHeader="false"
            rowKey="id"
            :pagination="false"
            :bordered="false"
            size="small"
            :locale="locale"
            :columns="groupButtonTableColumn"
            :data-source="button.buttonGroup.groups"
          >
            <template slot="titleSlot" slot-scope="text, record">
              <a-form :colon="false">
                <a-form-item label="分组名称" :style="{ padding: 0 }">
                  <a-input v-model="record.name" size="small">
                    <template slot="addonAfter">
                      <WI18nInput :widget="widget" :target="record" :designer="designer" :code="record.id" v-model="record.name" />
                    </template>
                  </a-input>
                </a-form-item>
                <a-form-item label="分组按钮" :style="{ padding: 0 }">
                  <a-select
                    size="small"
                    mode="multiple"
                    v-model="record.buttonIds"
                    style="width: 100%"
                    :options="groupButtonOptions"
                  ></a-select>
                </a-form-item>
                <a-form-item
                  label="分组按钮类型"
                  :style="{ padding: 0 }"
                  :label-col="{ style: { width: '100px' } }"
                  :wrapper-col="{ style: { width: 'calc(100% - 100px)', float: 'right' } }"
                >
                  <a-select size="small" :options="buttonTypeOptions" v-model="record.type" :style="{ width: '100%' }"></a-select>
                </a-form-item>
              </a-form>
            </template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <a-button type="link" size="small" @click="delGroupButton(index)" title="删除">
                <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              </a-button>
            </template>
            <template slot="footer">
              <a-button type="link" @click="addGroupButton" icon="plus">添加</a-button>
            </template>
          </a-table>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import ButtonDetails from './button-details.vue';
import draggable from '@framework/vue/designer/draggable';
import { buttonTypeOptions } from '../constant';

export default {
  name: 'ButtonsConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object,
    button: Object,
    title: String,
    formLayout: Object,
    zIndex: { type: Number, default: 1000 },
    limitButtonCount: {
      type: Number,
      default: 100
    },
    showGroup: {
      type: Boolean,
      default: true
    }
  },
  data() {
    let typeOptions = deepClone(buttonTypeOptions);
    typeOptions.splice(5, 1);
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      buttonTypeOptions: typeOptions,
      newButton: {
        id: undefined,
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
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },

      buttonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 90, scopedSlots: { customRender: 'operationSlot' } }
      ],
      groupButtonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 40, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: { ButtonDetails },
  computed: {
    groupButtonOptions() {
      let options = [];
      for (let i = 0, len = this.button.buttons.length; i < len; i++) {
        options.push({ label: this.button.buttons[i].title, value: this.button.buttons[i].id });
      }
      return options;
    }
  },
  created() {
    if (this.button && Object.keys(this.button).length == 0) {
      this.$set(this.button, 'enable', false);
      this.$set(this.button, 'buttons', []);
      this.$set(this.button, 'buttonGroup', { type: 'notGroup', groups: [], dynamicGroupName: '更多', dynamicGroupBtnThreshold: 3 });
    }
  },
  methods: {
    onConfirmOk() {
      this.$refs.addWidgetTableBtnConfig.validate().then(valid => {
        if (valid) {
          this.button.buttons.push(deepClone(this.newButton));
          this.newButton = deepClone(this.defaultNewBtn);
          this.newButton.id = generateId(9);
          this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
        }
      });
    },

    addGroupButton() {
      this.button.buttonGroup.groups.push({ id: generateId(9), name: '', buttonIds: [] });
    },
    delGroupButton(index) {
      this.button.buttonGroup.groups.splice(index, 1);
    },
    delButton(index) {
      this.button.buttons.splice(index, 1);
    }
  },
  beforeMount() {
    this.defaultNewBtn = deepClone(this.newButton);
  },
  mounted() {
    if (this.button) {
      this.tableDraggable(this.button.buttons, this.$el.querySelector('.buttons-table tbody'), '.drag-btn-handler');
    }
  }
};
</script>
