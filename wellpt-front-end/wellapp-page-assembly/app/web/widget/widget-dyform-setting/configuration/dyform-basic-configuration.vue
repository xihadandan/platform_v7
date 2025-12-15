<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-form-model-item label="名称" v-if="editRule.title == undefined || editRule.title.hidden !== true">
        <a-input v-model="widget.title" @change="onChangeTitle" />
      </a-form-model-item>
      <slot name="selectForm">
        <a-form-model-item label="状态表单">
          <a-switch v-model="widget.configuration.enableStateForm" />
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <span
              :class="widget.configuration.formUuid ? 'ant-btn ant-btn-link' : ''"
              @click="redirectFormDesign(widget.configuration.formUuid)"
              :title="widget.configuration.formUuid ? '打开表单' : ''"
            >
              {{ widget.configuration.enableStateForm ? '新建表单' : '表单' }}
            </span>
          </template>
          <DyformDefinitionSelect
            v-model="widget.configuration.formUuid"
            :displayModal="true"
            @change="(e, opt) => onSelectChangeStateForm(e, opt, 'formUuid')"
          />
        </a-form-model-item>
        <div v-show="widget.configuration.enableStateForm">
          <a-form-model-item>
            <template slot="label">
              <span
                :class="widget.configuration.editStateFormUuid ? 'ant-btn ant-btn-link' : ''"
                @click="redirectFormDesign(widget.configuration.editStateFormUuid)"
                :title="widget.configuration.editStateFormUuid ? '打开表单' : ''"
              >
                编辑表单
              </span>
            </template>
            <DyformDefinitionSelect
              v-model="widget.configuration.editStateFormUuid"
              :displayModal="true"
              @change="(e, opt) => onSelectChangeStateForm(e, opt, 'editStateFormUuid')"
            />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <span
                :class="widget.configuration.labelStateFormUuid ? 'ant-btn ant-btn-link' : ''"
                @click="redirectFormDesign(widget.configuration.labelStateFormUuid)"
                :title="widget.configuration.labelStateFormUuid ? '打开表单' : ''"
              >
                查阅表单
              </span>
            </template>
            <DyformDefinitionSelect
              v-model="widget.configuration.labelStateFormUuid"
              :displayModal="true"
              @change="(e, opt) => onSelectChangeStateForm(e, opt, 'labelStateFormUuid')"
            />
          </a-form-model-item>
        </div>
        <a-form-model-item>
          <template slot="label">
            <a-space>
              优先使用请求数据的表单
              <a-popover>
                <template slot="content">开启时，当请求参数中存在表单定义UUID参数(formUuid)时，优先使用请求参数的表单定义UUID</template>
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-popover>
            </a-space>
          </template>
          <a-switch v-model="widget.configuration.useRequestForm" />
        </a-form-model-item>
      </slot>
      <a-form-model-item label="JS模块">
        <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetDyformSetDevelopment" />
      </a-form-model-item>
      <a-form-model-item label="字段设置">
        <WidgetDesignDrawer :id="'dyformFieldConfigureDrawer' + widget.id" title="字段设置" :designer="designer" :zIndex="1001">
          <a-button type="link" size="small" title="设置">
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
            设置
          </a-button>
          <template slot="content">
            <FieldConfiguration :widget="widget" :designer="designer" />
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
      <a-form-model-item v-if="widget.forceRender !== false">
        <template slot="label">
          数据绑定
          <a-checkbox v-model="widget.configuration.enableDataBinding" />
        </template>
        <WidgetDesignDrawer
          v-if="widget.configuration.enableDataBinding"
          :id="'dyformFieldDataBindingConfigureDrawer' + widget.id"
          title="数据绑定"
          :designer="designer"
          :zIndex="1001"
        >
          <a-button type="link" size="small" title="设置">
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
            绑定
          </a-button>
          <template slot="content">
            <DyformDataBindingConfiguration :widget="widget" :designer="designer" />
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
      <a-form-model-item label="标题栏">
        <a-switch v-model="widget.configuration.titleVisible" />
      </a-form-model-item>
      <div v-show="widget.configuration.titleVisible">
        <a-form-model-item label="新建时">
          <a-input v-model="widget.configuration.title">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" code="title" v-model="widget.configuration.title" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="编辑时">
          <a-input v-model="widget.configuration.editStateTitle">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" code="editStateTitle" v-model="widget.configuration.editStateTitle" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="查阅时">
          <a-input v-model="widget.configuration.labelStateTitle">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" code="labelStateTitle" v-model="widget.configuration.labelStateTitle" />
            </template>
          </a-input>
        </a-form-model-item>

        <a-form-model-item label="标题图标">
          <WidgetDesignDrawer
            :id="'formTitleIconDrawer' + widget.id"
            title="选择图标"
            :designer="designer"
            :zIndex="20000"
            :width="640"
            :bodyStyle="{ height: '100%' }"
          >
            <IconSetBadge v-model="widget.configuration.titleIcon"></IconSetBadge>
            <template slot="content">
              <WidgetIconLib v-model="widget.configuration.titleIcon" />
            </template>
          </WidgetDesignDrawer>
        </a-form-model-item>
      </div>
      <a-form-model-item label="表单操作">
        <a-radio-group size="small" v-model="widget.configuration.buttonPosition" button-style="solid">
          <a-radio-button v-for="(item, i) in buttonPositionOptions" :value="item.value" :key="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <div v-show="widget.configuration.buttonPosition != 'disable'" style="padding: 0 12px">
        <a-table
          :showHeader="false"
          :bordered="false"
          size="small"
          rowKey="id"
          :pagination="false"
          :columns="buttonTableColumn"
          :locale="locale"
          :data-source="widget.configuration.button.buttons"
          class="widget-dyform-setting-button-table no-border"
        >
          <template slot="titleSlot" slot-scope="text, record, index">
            <!-- <i class="line" />
                {{ title }}
                <a-input size="small" v-model="record.title">
                  <span slot="addonAfter">
                    <a-icon :type="record.visibleType === 'visible' ? 'eye' : 'eye-invisible'" />

                  </span>
                </a-input> -->
            <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" />
            <a-input v-model="record.title" size="small" :style="{ width: '150px' }">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" :target="record" :code="record.id" v-model="record.title" />
              </template>
            </a-input>
          </template>
          <template slot="operationSlot" slot-scope="text, record, index">
            <WidgetDesignDrawer :zIndex="1000" :id="'formButtonConfigurationDrawer' + record.id" title="操作设置" :designer="designer">
              <a-button type="link" size="small" title="设置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
              <template slot="content">
                <ButtonConfiguration :button="record" :designer="designer" :editRule="editRule" :widget="widget" />
              </template>
            </WidgetDesignDrawer>
            <a-button
              type="link"
              size="small"
              title="删除"
              @click="widget.configuration.button.buttons.splice(index, 1)"
              v-if="allowDelete(record.id)"
            >
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
          </template>
          <template slot="footer">
            <WidgetDesignDrawer :zIndex="1000" :id="'WidgetDyformSettingBtnConfig' + widget.id" title="添加按钮" :designer="designer">
              <a-button type="link" :style="{ paddingLeft: '7px' }">
                <Icon type="pticon iconfont icon-ptkj-jiahao" />
                添加按钮
              </a-button>
              <template slot="content">
                <ButtonConfiguration :button="newButton" :widget="widget" :designer="designer" />
              </template>
              <template slot="footer">
                <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
              </template>
            </WidgetDesignDrawer>

            <a-form-model-item label="按钮分组" class="item-lh">
              <a-radio-group size="small" v-model="widget.configuration.button.buttonGroup.type" button-style="solid">
                <a-radio-button v-for="(item, i) in buttonGroupTypeOptions" :value="item.value" :key="item.value">
                  {{ item.label }}
                </a-radio-button>
              </a-radio-group>
            </a-form-model-item>

            <div v-show="widget.configuration.button.buttonGroup.type == 'fixGroup'">
              <a-table
                :showHeader="false"
                :bordered="false"
                size="small"
                rowKey="id"
                :locale="{ emptyText: '暂无数据' }"
                :pagination="false"
                :columns="buttonGroupTableColumn"
                :data-source="widget.configuration.button.buttonGroup.groups"
                class="btn-group-table"
              >
                <template slot="titleSlot" slot-scope="text, record, index">
                  <a-input-group compact>
                    <Icon
                      title="拖动排序"
                      type="pticon iconfont icon-ptkj-tuodong"
                      class="drag-btn-handler"
                      :style="{ cursor: 'move', 'margin-right': '5px', 'vertical-align': 'middle' }"
                    />
                    <a-input style="width: 60px" v-model="record.title" size="small" placeholder="名称" />
                    <a-select
                      placeholder="请选择按钮"
                      style="width: 170px"
                      mode="multiple"
                      v-model="record.buttonIds"
                      :options="vButtonOptions"
                      size="small"
                    />
                  </a-input-group>
                </template>
                <template slot="operationSlot" slot-scope="text, record, index">
                  <a-button type="link" size="small" title="删除" @click="widget.configuration.button.buttonGroup.groups.splice(index, 1)">
                    <Icon type="pticon iconfont icon-ptkj-shanchu" />
                  </a-button>
                </template>
                <template slot="footer">
                  <a-button @click="addButtonGroup" type="link" size="small">
                    <Icon type="pticon iconfont icon-ptkj-jiahao" />
                    添加
                  </a-button>
                </template>
                <!-- <template slot="operationSlot" slot-scope="text, record">

                    </template> -->
              </a-table>
            </div>
            <div v-show="widget.configuration.button.buttonGroup.type == 'dynamicGroup'">
              <a-form-model-item label="分组名称">
                <a-input v-model="widget.configuration.button.buttonGroup.dynamicGroupName">
                  <template slot="addonAfter">
                    <WI18nInput :widget="widget" :designer="designer" :target="buttonGroup" code="dynamicGroupName" />
                  </template>
                </a-input>
              </a-form-model-item>
              <a-form-model-item label="分组阈值">
                <a-input-number v-model="widget.configuration.dynamicGroupBtnThreshold" />
              </a-form-model-item>
            </div>
            <a-form-model-item label="对齐方式" v-show="widget.configuration.buttonPosition === 'bottom'">
              <a-radio-group size="small" v-model="widget.configuration.button.buttonAlign" button-style="solid">
                <a-radio-button v-for="(item, i) in alignTypeOptions" :value="item.value" :key="item.value">
                  {{ item.label }}
                </a-radio-button>
              </a-radio-group>
            </a-form-model-item>
          </template>
        </a-table>
      </div>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import ButtonConfiguration from './button-configuration.vue';
import FieldConfiguration from './field-configuration.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';
import DyformDefinitionSelect from '../../commons/dyform-definition-select.vue';
import DyformDataBindingConfiguration from './dyform-data-binding-configuration.vue';
export default {
  name: 'DyformBasicConfiguration',
  mixins: [draggable],
  inject: ['appId'],
  props: {
    widget: Object,
    designer: Object,
    editRule: {
      type: Object,
      default: {}
    }
  },
  provide() {
    return {
      formFieldOptions: this.formFieldOptions
    };
  },
  data() {
    let dyformDefinitionOption = [];
    if (this.widget.configuration.formUuid && this.widget.configuration.formName) {
      dyformDefinitionOption.push({ label: this.widget.configuration.formName, value: this.widget.configuration.formUuid });
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      newButton: {
        id: generateId(),
        code: undefined,
        title: undefined,
        role: [],
        visibleType: 'visible', // visible / visible-condition / hidden
        visibleCondition: {
          formStateConditions: [],
          userRoleConditions: [],
          enableDefineCondition: false,
          defineCondition: {
            operator: 'and',
            cons: []
          }
        },
        style: { icon: undefined, textHidden: false, type: 'default' },
        eventHandler: [],
        remark: ''
      },
      buttonPositionOptions: [
        { label: '关闭', value: 'disable' },
        { label: '头部操作', value: 'top' },
        { label: '底部操作', value: 'bottom' }
      ],
      buttonGroupTypeOptions: [
        { label: '不分组', value: 'notGroup' },
        { label: '固定分组', value: 'fixGroup' },
        { label: '动态分组', value: 'dynamicGroup' }
      ],
      alignTypeOptions: [
        { label: '左对齐', value: 'left' },
        { label: '居中', value: 'center' },
        { label: '右对齐', value: 'right' }
      ],
      dyformDefinitionMap: {},
      dyformDefinitionOption,
      selectedButtonRowKeys: [],
      selectedButtonRows: [],
      selectedButtonGroupRowKeys: [],
      selectedButtonGroupRows: [],
      buttonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      buttonGroupTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', scopedSlots: { customRender: 'operationSlot' } }
      ],
      formFieldOptions: []
    };
  },

  beforeCreate() {},
  components: { ButtonConfiguration, FieldConfiguration, DyformDefinitionSelect, DyformDataBindingConfiguration },
  computed: {
    vButtonOptions() {
      let options = [];
      for (let i = 0, len = this.widget.configuration.button.buttons.length; i < len; i++) {
        options.push({ label: this.widget.configuration.button.buttons[i].title, value: this.widget.configuration.button.buttons[i].id });
      }
      return options;
    }
  },
  created() {},
  methods: {
    allowDelete(id) {
      if (this.editRule == undefined || Object.keys(this.editRule).length == 0) {
        return true;
      }
      return (
        this.editRule.button == undefined ||
        this.editRule.button[id] == undefined ||
        (this.editRule.button && this.editRule.button[id] && this.editRule.button[id].deleteHidden !== true)
      );
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    redirectFormDesign(uuid) {
      if (uuid) {
        window.open(`/dyform-designer/index?uuid=${uuid}`, '_blank');
      }
    },
    onChangeTitle(e) {
      this.designer.widgetTreeMap[this.widget.id].title = e.target.value;
    },
    onConfirmOk() {
      this.widget.configuration.button.buttons.push(deepClone(this.newButton));
      this.newButton = deepClone(this.defaultNewBtn);
      this.newButton.id = generateId();
      this.designer.emitEvent('closeDrawer:' + this.designer.unForceRenderDrawerVisibleKey);
    },

    addButtonGroup() {
      this.widget.configuration.button.buttonGroup.groups.push({
        id: generateId(),
        title: '',
        buttonIds: []
      });
    },
    onSelectChangeStateForm(v, opt, key) {
      let nameField = key == 'formUuid' ? 'formName' : key == 'labelStateFormUuid' ? 'labelStateFormName' : 'editStateFormName';
      let ruleField =
        key == 'formUuid' ? 'formElementRules' : key == 'labelStateFormUuid' ? 'labelStateFormElementRules' : 'editStateFormElementRules';
      this.widget.configuration[nameField] = null;
      this.widget.configuration[ruleField].splice(0, this.widget.configuration[ruleField].length);
      if (v) {
        this.widget.configuration[nameField] = opt.name;
        this.fetchDyformDefinitionJSON(v).then(def => {
          this.updateFormFieldOptions();
          this.designer.emitEvent(`${this.widget.id}:DyformSettingChange`, {
            state: key == 'formUuid' ? 'create' : key == 'labelStateFormUuid' ? 'label' : 'edit',
            json: JSON.parse(def.definitionVjson)
          });
        });
      }
    },

    fetchDyformDefinitionJSON(formUuid) {
      return new Promise((resolve, reject) => {
        if (this.dyformDefinitionMap[formUuid] && this.dyformDefinitionMap[formUuid].definitionVjson) {
          resolve(this.dyformDefinitionMap[formUuid]);
        } else {
          DyformDefinitionSelect.methods.fetchDyformDefinitionJSON(formUuid).then(data => {
            if (this.dyformDefinitionMap[formUuid] == undefined) {
              this.dyformDefinitionMap[formUuid] = data;
            }
            this.dyformDefinitionMap[formUuid].definitionVjson = data.definitionVjson;
            resolve(data);
          });
        }
      });
    },
    updateFormFieldOptions() {
      this.formFieldOptions.splice(0, this.formFieldOptions.length);
      if (this.widget.configuration.formUuid) {
        let uuids = [this.widget.configuration.formUuid];
        if (this.widget.configuration.enableStateForm) {
          if (this.widget.configuration.labelStateFormUuid) {
            uuids.push(this.widget.configuration.labelStateFormUuid);
          }
          if (this.widget.configuration.editStateFormUuid) {
            uuids.push(this.widget.configuration.editStateFormUuid);
          }
        }
        let codes = [];
        uuids.forEach(id => {
          if (this.dyformDefinitionMap[id]) {
            let vjson = JSON.parse(this.dyformDefinitionMap[id].definitionVjson);
            if (vjson.fields && vjson.fields.length) {
              vjson.fields.forEach(f => {
                if (!codes.includes(f.configuration.code)) {
                  this.formFieldOptions.push({
                    label: f.configuration.name,
                    value: f.configuration.code,
                    dbDataType: f.configuration.dbDataType
                  });
                  codes.push(f.configuration.code);
                }
              });
            }
          }
        });
      }
    }
  },
  beforeMount() {
    this.defaultNewBtn = deepClone(this.newButton);
    if (this.widget.configuration.formUuid) {
      let promise = [this.fetchDyformDefinitionJSON(this.widget.configuration.formUuid)];
      if (this.widget.configuration.enableStateForm) {
        if (this.widget.configuration.labelStateFormUuid) {
          promise.push(this.fetchDyformDefinitionJSON(this.widget.configuration.labelStateFormUuid));
        }
        if (this.widget.configuration.editStateFormUuid) {
          promise.push(this.fetchDyformDefinitionJSON(this.widget.configuration.editStateFormUuid));
        }
      }
      Promise.all(promise).then(res => {
        this.updateFormFieldOptions();
      });
    }
  },
  mounted() {
    this.tableDraggable(
      this.widget.configuration.button.buttons,
      this.$el.querySelector('.widget-dyform-setting-button-table tbody'),
      '.drag-btn-handler'
    );
    this.tableDraggable(
      this.widget.configuration.button.buttonGroup.groups,
      this.$el.querySelector('.btn-group-table tbody'),
      '.drag-btn-handler'
    );
  },
  watch: {
    'widget.configuration.formUuid': {
      handler(v) {
        this.updateFormFieldOptions();
      }
    },
    'widget.configuration.editStateFormUuid': {
      handler(v) {
        this.updateFormFieldOptions();
      }
    },
    'widget.configuration.labelStateFormUuid': {
      handler(v) {
        this.updateFormFieldOptions();
      }
    }
  }
};
</script>
