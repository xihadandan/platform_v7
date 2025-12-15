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
      :data-source="button.buttons"
      class="subform-button-table no-border"
    >
      <template slot="title">
        <i class="line" />
        {{ title }}
      </template>
      <template slot="titleSlot" slot-scope="text, record">
        <div>
          <Icon
            title="拖动排序"
            type="pticon iconfont icon-ptkj-tuodong"
            class="drag-btn-handler drag-handler"
            :style="{ cursor: 'move' }"
          ></Icon>
          {{ text }}
          <WI18nInput
            :widget="widget"
            :designer="designer"
            :code="position == 'header' ? position + '_' + record.id : record.id"
            v-model="record.title"
            :target="record"
          />
        </div>
      </template>
      <template slot="visibleSlot" slot-scope="text, record">
        <a-switch size="small" v-model="record.defaultVisible" checked-children="显示" un-checked-children="隐藏" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-popconfirm placement="topRight" @confirm="delButton(index)">
          <template slot="title">是否删除{{ record.title }}按钮</template>
          <a-button type="link" size="small" v-if="!record.default" title="删除">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          </a-button>
        </a-popconfirm>
        <WidgetDesignDrawer :id="'subformBtnConfig' + record.id" title="编辑按钮" :designer="designer">
          <a-button type="link" size="small" @click="e => onClickEditButton(e, record)" title="编辑">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          </a-button>
          <template slot="content">
            <ButtonConfiguration
              :button="record"
              :widget="widget"
              :designer="designer"
              :position="position"
              :columnIndexOptions="columnIndexOptions"
            >
              <template
                slot="extraInfo"
                slot-scope="scope"
                v-if="position == 'rowEnd' && widget.configuration.layout == 'form-tabs' && scope.button.id == 'delRow'"
              >
                <a-form-model-item label="显示为页签关闭">
                  <a-switch v-model="scope.button.displayAsTabClose" />
                </a-form-model-item>
              </template>
            </ButtonConfiguration>
          </template>
        </WidgetDesignDrawer>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer :id="'subformBtnConfig' + widget.id" title="添加按钮" :designer="designer">
          <a-button type="link" size="small" @click="addButton" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
          <template slot="content">
            <ButtonConfiguration
              v-if="button.buttons.length > 0"
              :button="newButton"
              :widget="widget"
              :designer="designer"
              :position="position"
              :columnIndexOptions="columnIndexOptions"
            />
          </template>
        </WidgetDesignDrawer>
        <template v-if="designer.terminalType == 'pc'">
          <a-form-model-item label="按钮分组" class="item-lh">
            <a-radio-group size="small" v-model="button.buttonGroup.type" button-style="solid">
              <a-radio-button value="notGroup">不分组</a-radio-button>
              <a-radio-button value="fixedGroup">固定分组</a-radio-button>
              <a-radio-button value="dynamicGroup">动态分组</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div v-if="button.buttonGroup.type === 'dynamicGroup'">
            <a-form-model-item label="分组名称">
              <a-input v-model="button.buttonGroup.dynamicGroupName">
                <template slot="addonAfter">
                  <WI18nInput
                    :widget="widget"
                    :designer="designer"
                    code="dynamicGroupName"
                    v-model="button.buttonGroup.dynamicGroupName"
                    :target="button.buttonGroup"
                  />
                </template>
              </a-input>
            </a-form-model-item>

            <a-form-model-item>
              <template slot="label">
                当按钮超过
                <a-input-number v-model="button.buttonGroup.dynamicGroupBtnThreshold" :min="1" style="width: 60px" size="small" />
                个时进行分组
              </template>
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
                      <WI18nInput :widget="widget" :designer="designer" :code="record.id" v-model="record.name" :target="record" />
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
                    :getPopupContainer="getPopupContainerByPs()"
                  ></a-select>
                </a-form-item>
              </a-form>
            </template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <a-button type="link" size="small" @click="delGroupButton(index)" title="删除">
                <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              </a-button>
            </template>
            <template slot="footer">
              <a-button type="link" size="small" @click="addGroupButton">
                <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                添加
              </a-button>
            </template>
          </a-table>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import ButtonConfiguration from './button-configuration.vue';
import draggable from '@framework/vue/designer/draggable';

import formCommonMixin from '../../mixin/form-common.mixin';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';

import { debounce } from 'lodash';

export default {
  name: 'WidgetSubformButtonConfiguration',
  mixins: [draggable, formCommonMixin],
  inject: ['pageContext'],
  props: {
    widget: Object,
    designer: Object,
    button: Object,
    title: String,
    position: String,
    columnIndexOptions: Array
  },
  data() {
    return {
      tableKey: generateId(),
      newButton: {},
      locale: {
        emptyText: <span>暂无数据</span>
      },
      formDefinitionOptions: [],
      fetchingFormDefinition: false,
      buttonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '是否显示', dataIndex: 'visible', width: 70, scopedSlots: { customRender: 'visibleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 90, scopedSlots: { customRender: 'operationSlot' }, align: 'right' }
      ],
      groupButtonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 60, scopedSlots: { customRender: 'operationSlot', align: 'right' } }
      ]
    };
  },
  components: { ButtonConfiguration },
  computed: {
    groupButtonOptions() {
      let options = [];
      for (let i = 0, len = this.button.buttons.length; i < len; i++) {
        options.push({ label: this.button.buttons[i].title, value: this.button.buttons[i].id });
      }
      return options;
    }
  },
  methods: {
    onClickEditButton(e, item) {
      if (item.id.startsWith('export_') || item.id.startsWith('import_')) {
        // 导入导出按钮由导出按钮配置负责打开
        this.pageContext.emitEvent(
          `${this.widget.id}:${item.id.startsWith('export_') ? 'openExportButtonConfig' : 'openImportButtonConfig'}`,
          item.id
        );
        e.stopPropagation();
      }
    },
    getPopupContainerByPs,
    iconSelected(icon) {
      this.currentButtonStyleConf.icon = icon;
    },

    addButton() {
      let btn = {
        id: generateId(),
        code: undefined,
        title: undefined,
        defaultVisible: true,
        style: { icon: undefined, textHidden: false, type: 'link' },
        switch: {
          checkedText: undefined,
          UnCheckedText: undefined,
          defaultChecked: true,
          checkedCondition: { code: undefined, operator: 'true', value: undefined }
        },
        eventHandler: { eventParams: [] },
        customEventScript: undefined
      };
      this.newButton = btn;
      this.button.buttons.push(btn);
    },
    addGroupButton() {
      this.button.buttonGroup.groups.push({ id: generateId(), name: '', buttonIds: [] });
    },
    delGroupButton(index) {
      this.button.buttonGroup.groups.splice(index, 1);
    },
    delButton(index) {
      this.button.buttons.splice(index, 1);
    },
    getFormDefinitionOptions: debounce(function (searchValue) {
      var _this = this;
      _this.fetchingFormDefinition = true;
      $axios
        .post('/common/select2/query', {
          serviceName: 'dyFormFacade',
          queryMethod: 'queryAllPforms',
          searchValue,
          pageSize: 20,
          includeSuperAdmin: true
          //  systemUnitId
        })
        .then(({ data }) => {
          if (data.results) {
            _this.formDefinitionOptions.splice(0, _this.formDefinitionOptions.length);
            // _this.formDefinitionOptions.length = 0;
            let exist = false;
            for (let i = 0, len = data.results.length; i < len; i++) {
              _this.formDefinitionOptions.push({
                label: data.results[i].text,
                value: data.results[i].id
              });
              if (data.results[i].id === _this.widget.configuration.formUuid) {
                exist = true;
              }
            }
            if (!exist && _this.widget.configuration.formUuid) {
              _this.formDefinitionOptions.splice(0, 0, {
                label: _this.widget.configuration.formName,
                value: _this.widget.configuration.formUuid
              });
            }
            _this.fetchingFormDefinition = false;
          }
        });
    }, 600)
  },
  beforeMount() {
    if (this.position == 'rowEnd') {
      // 配置修正：
      // 1. 行内按钮增加默认不显示的添加行操作
      let buttons = this.widget.configuration.rowButton.buttons,
        fixButtons = ['appendRow'];
      for (let i = 0, len = buttons.length; i < len; i++) {
        if (buttons[i].id == 'appendRow') {
          fixButtons.splice(fixButtons.indexOf('appendRow'), 1);
        }
      }
      if (fixButtons.includes('appendRow')) {
        buttons.push({
          id: 'appendRow',
          code: 'appendRow',
          title: '添加行',
          default: true,
          defaultVisible: false,
          style: { icon: undefined, type: 'link' },
          eventHandler: { eventParams: [] }
        });
      }
    }
  },
  mounted() {
    this.tableDraggable(this.button.buttons, this.$el.querySelector('.subform-button-table tbody'), '.drag-btn-handler');
    this.getFormDefinitionOptions();
  }
};
</script>
