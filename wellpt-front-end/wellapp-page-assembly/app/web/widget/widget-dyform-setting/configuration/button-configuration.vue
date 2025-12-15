<template>
  <a-form-model>
    <a-form-model-item label="按钮名称">
      <a-input v-model="button.title">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" v-model="button.title" :designer="designer" :target="button" :code="button.id" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="按钮编码">
      <a-input v-model="button.code" />
    </a-form-model-item>
    <!-- <a-form-model-item label="角色可访问">
      <RoleSelect v-model="button.role" />
    </a-form-model-item> -->
    <a-form-model-item label="按钮类型">
      <a-select :options="buttonTypeOptions" v-model="button.style.type" :style="{ width: '100%' }"></a-select>
    </a-form-model-item>
    <a-form-model-item label="事件处理">
      <a-table
        :bordered="false"
        :showHeader="false"
        size="small"
        :locale="locale"
        rowKey="id"
        :pagination="false"
        :columns="buttonEventTableColumn"
        :data-source="button.eventHandler"
        class="btn-event-handler-table no-border"
      >
        <template slot="titleSlot" slot-scope="text, record, index">
          <Icon title="拖动排序" type="iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" />
          <a-input v-model="record.name" size="small" :style="{ width: '200px' }" />
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <WidgetDesignModal :zIndex="1000" title="事件设置" :designer="designer" :maxHeight="600">
            <a-button size="small" type="link" title="事件设置"><Icon type="iconfont icon-ptkj-shezhi" /></a-button>
            <div slot="content">
              <WidgetEventHandler
                :eventModel="record"
                :designer="designer"
                :widget="widget"
                :rule="
                  editRule && editRule.button && editRule.button[button.id] && editRule.button[button.id].eventHandlerRule
                    ? editRule.button[button.id].eventHandlerRule
                    : {}
                "
              >
                <template #extraOptions="{ eventConfig }">
                  <EventHandlerCopyFormDataConfiguration
                    :eventConfig="eventConfig"
                    :sourceColumns="[]"
                    :widget="widget"
                  ></EventHandlerCopyFormDataConfiguration>
                </template>
              </WidgetEventHandler>
            </div>
          </WidgetDesignModal>
          <a-button size="small" type="link" @click="deleteEventHandler(index)" v-if="allowDelete" title="删除">
            <Icon type="iconfont icon-ptkj-shanchu" />
          </a-button>
        </template>
        <template slot="footer">
          <WidgetDesignModal :zIndex="1000" title="事件设置" :designer="designer" :ok="() => {}" ref="eventHandlerModal">
            <a-button type="link" icon="plus" :style="{ paddingLeft: '7px' }">添加</a-button>
            <template slot="content">
              <WidgetEventHandler :eventModel="newEventHandler" :designer="designer" :widget="widget">
                <template #extraOptions="{ eventConfig }">
                  <EventHandlerCopyFormDataConfiguration
                    :eventConfig="eventConfig"
                    :sourceColumns="[]"
                    :widget="widget"
                  ></EventHandlerCopyFormDataConfiguration>
                </template>
              </WidgetEventHandler>
            </template>
            <template slot="footer">
              <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
            </template>
          </WidgetDesignModal>
        </template>
      </a-table>
    </a-form-model-item>
    <!-- <a-button-group class="table-header-operation" size="small">
      <a-button @click="addEvent" icon="plus">新增</a-button>
    </a-button-group> -->

    <div v-show="button.style.type != 'switch'">
      <a-form-model-item label="按钮图标">
        <WidgetDesignModal
          :zIndex="1000"
          title="选择图标"
          :designer="designer"
          dialogClass="pt-modal widget-icon-lib-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          :width="640"
          mask
          bodyContainer
        >
          <IconSetBadge v-model="button.style.icon"></IconSetBadge>

          <template slot="content">
            <WidgetIconLib v-model="button.style.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <a-form-model-item label="按钮形状">
        <a-select :style="{ width: '100%' }" :options="buttonShapeOptions" v-model="button.style.shape" allowClear></a-select>
      </a-form-model-item>

      <a-form-model-item label="隐藏文本">
        <a-switch v-model="button.style.textHidden" />
      </a-form-model-item>
    </div>
    <a-form-model-item label="可见性">
      <a-radio-group v-model="button.visibleType" button-style="solid">
        <a-radio-button v-for="(item, i) in visibleTypeOptions" :value="item.value" :key="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <div v-show="button.visibleType === 'visible-condition'">
      <a-form-model-item label="关联表单状态">
        <a-checkbox-group v-model="button.visibleCondition.formStateConditions" :options="formStateOptions" />
      </a-form-model-item>
      <a-form-model-item label="关联角色">
        <a-select mode="multiple" show-search v-model="button.visibleCondition.userRoleConditions" :options="roleOptions" allowClear />
      </a-form-model-item>

      <a-form-model-item label="自定义条件">
        <a-switch v-model="button.visibleCondition.enableDefineCondition" />
      </a-form-model-item>
      <div v-show="button.visibleCondition.enableDefineCondition">
        <a-form-model-item :label-col="{ style: { width: '100%' } }" :wrapper-col="{ style: { width: '100%' } }">
          <template slot="label">
            满足
            <a-select
              size="small"
              :options="[
                { label: '全部', value: 'and' },
                { label: '任一', value: 'or' }
              ]"
              style="width: 65px"
              v-model="button.visibleCondition.defineCondition.operator"
            />
            条件时显示
            <a-button
              size="small"
              icon="plus"
              type="link"
              @click="
                () => {
                  button.visibleCondition.defineCondition.cons.push({
                    code: undefined,
                    value: undefined,
                    operator: '=='
                  });
                }
              "
            >
              添加条件
            </a-button>
          </template>

          <div
            style="padding: 0px 6px; background: #fff; margin-bottom: 5px"
            v-if="button.visibleCondition.defineCondition.cons.length > 0"
          >
            <div style="outline: 1px solid #e8e8e8; padding: 6px 5px 1px 5px; border-radius: 2px">
              <template v-for="(item, i) in button.visibleCondition.defineCondition.cons">
                <a-row type="flex" :key="'requireCon_' + i" style="margin-bottom: 5px">
                  <a-col flex="calc(100% - 30px)">
                    <a-input-group compact>
                      <a-select
                        v-if="formFieldOptions.length > 0"
                        v-model="item.code"
                        :allowClear="true"
                        :showSearch="true"
                        :filterOption="filterSelectOption"
                        :style="{ width: 'calc(50% - 50px)' }"
                      >
                        <a-select-opt-group>
                          <span slot="label">
                            <a-icon type="code" />
                            表单数据
                          </span>
                          <a-select-option v-for="opt in formFieldOptions" :key="'_FORM_DATA_.' + opt.value" :title="opt.label">
                            {{ opt.label }}
                            <a-tag>{{ opt.value }}</a-tag>
                          </a-select-option>
                          <a-select-option :key="'_FORM_DATA_.creator'" title="数据创建人">
                            创建人
                            <a-tag>creator</a-tag>
                          </a-select-option>
                        </a-select-opt-group>
                      </a-select>
                      <a-input v-else v-model="item.code" :style="{ width: 'calc(50% - 100px)' }" />
                      <a-select :options="operatorOptions" v-model="item.operator" :style="{ width: '100px' }" />
                      <a-select
                        v-if="!['true', 'false'].includes(item.operator)"
                        v-model="item.valueType"
                        style="width: 70px"
                        @change="item.value = undefined"
                      >
                        <a-select-option value="constant">常量</a-select-option>
                        <a-select-option value="variable">变量</a-select-option>
                      </a-select>
                      <template v-if="!['true', 'false'].includes(item.operator)">
                        <a-input
                          v-if="item.valueType !== 'variable'"
                          v-model="item.value"
                          :style="{ width: 'calc(50% - 50px - 70px)' }"
                          class="design-constant-variable-input"
                        ></a-input>
                        <a-select
                          v-else
                          v-model="item.value"
                          :style="{ width: 'calc(50% - 50px - 70px)' }"
                          :dropdownMatchSelectWidth="false"
                          :show-search="true"
                          :filterOption="filterSelectOption"
                        >
                          <a-select-opt-group>
                            <span slot="label">
                              <a-icon type="code" />
                              表单数据
                            </span>
                            <a-select-option
                              v-for="opt in formFieldOptions"
                              :key="'_FORM_DATA_.' + opt.value"
                              readonly
                              :value="'_FORM_DATA_.' + opt.value"
                              :title="opt.label"
                            >
                              {{ opt.label }}
                            </a-select-option>
                            <a-select-option :key="'_FORM_DATA_.creator'" title="数据创建人">
                              创建人
                              <a-tag>creator</a-tag>
                            </a-select-option>
                          </a-select-opt-group>
                          <a-select-opt-group>
                            <span slot="label">
                              <a-icon type="code" />
                              用户数据
                            </span>
                            <a-select-option
                              v-for="opt in userVariableOptions"
                              :key="opt.value"
                              readonly
                              :value="opt.value"
                              :title="opt.label"
                            >
                              {{ opt.label }}
                            </a-select-option>
                          </a-select-opt-group>

                          <a-select-opt-group>
                            <span slot="label">
                              <a-icon type="code" />
                              日期时间
                            </span>
                            <a-select-option
                              v-for="opt in timeDataVariableOptions"
                              :key="opt.value"
                              readonly
                              :value="opt.value"
                              :title="opt.label"
                            >
                              {{ opt.label }}
                            </a-select-option>
                          </a-select-opt-group>
                          <a-select-opt-group>
                            <span slot="label">
                              <a-icon type="code" />
                              工作流数据
                            </span>
                            <a-select-option
                              v-for="opt in flowDataVariableOptions"
                              :key="opt.value"
                              readonly
                              :value="opt.value"
                              :title="opt.label"
                            >
                              {{ opt.label }}
                            </a-select-option>
                          </a-select-opt-group>
                        </a-select>
                      </template>
                    </a-input-group>
                  </a-col>
                  <a-col flex="30px">
                    <a-button type="link" size="small" @click="button.visibleCondition.defineCondition.cons.splice(i, 1)" title="删除">
                      <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    </a-button>
                  </a-col>
                </a-row>
              </template>
            </div>
          </div>
        </a-form-model-item>
      </div>
    </div>
    <BehaviorLogConfiguration
      :widget="widget"
      :configuration="button"
      :designer="designer"
      :extFormulaVariableOptions="extFormulaVariableOptions"
    />
    <a-form-model-item label="描述">
      <a-textarea v-model="button.remark" />
    </a-form-model-item>
  </a-form-model>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import {
  buttonTypeOptions,
  buttonShapeOptions,
  userVariableOptions,
  flowDataVariableOptions,
  timeDataVariableOptions
} from '../../commons/constant';
import draggable from '@framework/vue/designer/draggable';
import { filterSelectOption } from '@framework/vue/utils/function';
import EventHandlerCopyFormDataConfiguration from '../../commons/event-handler-copy-form-data-configuration.vue';

export default {
  name: 'WidgetDyformSettingButtonConfiguration',
  mixins: [draggable],
  inject: ['appId', 'formFieldOptions'],
  props: {
    widget: Object,
    button: Object,
    designer: Object,
    editRule: Object
  },
  data() {
    return {
      options: {},
      buttonTypeOptions,
      buttonShapeOptions,
      selectedButtonEventRowKeys: [],
      selectedButtonEventRows: [],
      visibleTypeOptions: [
        { label: '显示', value: 'visible' },
        { label: '满足条件显示', value: 'visible-condition' },
        { label: '隐藏', value: 'hidden' }
      ],
      formStateOptions: [
        { label: '新建', value: 'createForm' },
        { label: '编辑', value: 'edit' },
        { label: '查阅', value: 'label' }
      ],
      roleOptions: [],
      buttonEventTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      newEventHandler: {
        id: undefined,
        eventParams: []
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },
      fieldVarOptions: [],
      operatorOptions: [
        { label: '等于', value: '==' },
        { label: '不等于', value: '!=' },
        { label: '为真', value: 'true' },
        { label: '为假', value: 'false' },
        { label: '包含于', value: 'in' },
        { label: '不包含于', value: 'not in' },
        { label: '包含', value: 'contain' },
        { label: '不包含', value: 'not contain' }
      ],
      userVariableOptions,
      timeDataVariableOptions,
      flowDataVariableOptions
    };
  },

  beforeCreate() {},
  components: { EventHandlerCopyFormDataConfiguration },
  computed: {
    extFormulaVariableOptions() {
      let groups = [];
      let colDataOptions = {
        title: '表单字段',
        options: []
      };
      for (let i of this.formFieldOptions) {
        colDataOptions.options.push({
          label: i.label,
          value: `_FORM_DATA_.${i.value}`
        });
      }
      groups.push(colDataOptions);

      return groups;
    },

    allowDelete() {
      if (this.editRule == undefined || Object.keys(this.editRule).length == 0) {
        return true;
      }
      return (
        this.editRule.button == undefined ||
        this.editRule.button[this.button.id] == undefined ||
        (this.editRule.button && this.editRule.button[this.button.id] && this.editRule.button[this.button.id].deleteHidden !== true)
      );
    }
  },
  created() {
    if (this.button.role == undefined) {
      this.$set(this.button, 'role', []);
    }
    if (this.button.visibleCondition && this.button.visibleCondition.defineCondition && this.button.visibleCondition.defineCondition.cons) {
      for (let c of this.button.visibleCondition.defineCondition.cons) {
        if (/^[_a-z]+(_[a-z]+)*$/.test(c.code)) {
          // 转换旧版定义
          c.code = `_FORM_DATA_.${c.code}`;
        }
      }
    }
  },
  methods: {
    filterSelectOption,

    onConfirmOk() {
      this.newEventHandler.id = generateId();
      this.button.eventHandler.push(deepClone(this.newEventHandler));
      this.newEventHandler = deepClone(this.defaultNewEventHandler);
      this.$refs.eventHandlerModal.visible = false;
    },
    fetchRoles() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/security/role/queryAppRoles`, { params: { appId: this.appId } })
          .then(({ data }) => {
            if (data.data) {
              for (let i = 0, len = data.data.length; i < len; i++) {
                this.roleOptions.push({
                  label: data.data[i].name,
                  value: data.data[i].id
                });
              }
            }
          })
          .catch(error => {});
      });
    },
    deleteEventHandler(index) {
      this.button.eventHandler.splice(index, 1);
    }
  },
  beforeMount() {
    this.defaultNewEventHandler = deepClone(this.newEventHandler);
    this.fetchRoles();
  },
  mounted() {
    this.tableDraggable(this.button.eventHandler, this.$el.querySelector('.btn-event-handler-table tbody'), '.drag-btn-handler');
  }
};
</script>
