<template>
  <div>
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      labelAlign="left"
      :wrapper-col="{ style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" :allowClobDataType="true" />

          <FieldLengthInput :widget="widget" v-show="!widget.configuration.dbDataType" />

          <!-- 默认值 -->
          <FieldDefaultValue :configuration="widget.configuration" :selectVariable="true" />

          <a-form-model-item label="分隔符">
            <a-select v-model="widget.configuration.separator" :getPopupContainer="getPopupContainerByPs()">
              <a-select-option value=";">分号;</a-select-option>
              <a-select-option value=",">逗号,</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="指定组织">
            <a-select
              v-model="widget.configuration.orgUuid"
              allowClear
              :options="orgOptions"
              showSearch
              :filterOption="filterOption"
              :getPopupContainer="getPopupContainerByPs()"
              :dropdownClassName="getDropdownClassName()"
              @change="onChangeOrgSelect"
            />
          </a-form-model-item>
          <a-form-model-item v-if="widget.configuration.orgUuid != undefined && widget.configuration.bizOrgUuids != undefined">
            <template slot="label">指定业务组织</template>
            <a-select
              v-model="widget.configuration.bizOrgUuids"
              allowClear
              :options="bizOrgOptions"
              showSearch
              :filterOption="filterOption"
              :getPopupContainer="getPopupContainerByPs()"
              :dropdownClassName="getDropdownClassName()"
              @change="onChangeBizOrgSelect"
              mode="multiple"
            />
            <a-checkbox v-model="widget.configuration.onlyShowBizOrg" @change="onChangeOnlyShowBizOrg">仅展示业务组织</a-checkbox>
          </a-form-model-item>

          <a-form-model-item class="item-lh">
            <template slot="label">
              组织树节点过滤
              <a-tooltip placement="top" :overlayStyle="{ maxWidth: '300px' }">
                <template slot="title">
                  <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                    <li>逻辑处理判断，先显示节点，后隐藏节点</li>
                    <li>显示节点不配置，按默认组织树显示</li>
                    <li>显示节点有配置，选中节点显示在树的第一层</li>
                    <li>显示节点按函数判断，当前节点的函数返回true，该节点显示在树的第一层</li>
                    <li>隐藏节点有配置，该节点及其下级节点均不显示</li>
                    <li>隐藏节点按函数判断，当前节点的函数返回true，该节点及其下级节点均不显示</li>
                    <li>若隐藏节点的下级节点是已配置的显示节点，该下级节点显示在树的第一层</li>
                    <li>若组织节点树未找到匹配数据，则当前树不变</li>
                  </ul>
                </template>
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-tooltip>
            </template>
          </a-form-model-item>
          <filterNodeConfiguraiton :widget="widget" ref="showFilterNodeRef" :typeOptions="checkTypeOptions"></filterNodeConfiguraiton>
          <filterNodeConfiguraiton
            :widget="widget"
            type="hide"
            label="隐藏节点"
            ref="hideFilterNodeRef"
            :typeOptions="checkTypeOptions"
          ></filterNodeConfiguraiton>
          <a-table
            :showHeader="false"
            class="org-type-table no-border"
            rowKey="id"
            :pagination="false"
            size="small"
            :bordered="false"
            :data-source="widget.configuration.orgSelectTypes"
            :columns="orgSelectTypeColumns"
          >
            <template slot="title">可选组织选择项</template>
            <template slot="labelSlot" slot-scope="text, record, index">
              <a-row type="flex">
                <a-col flex="20px">
                  <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-handler" :style="{ cursor: 'move' }" />
                </a-col>
                <a-col flex="auto">
                  <a-input v-model="record.label" size="small">
                    <template slot="suffix">
                      <a-switch v-model="record.enable" size="small" v-if="!record.id.startsWith('customize-org-type-')" />
                      <template v-else>
                        <a-popover placement="left">
                          <template slot="title">
                            <label>
                              服务类型编码
                              <label style="color: red">*</label>
                            </label>
                          </template>
                          <template slot="content">
                            <a-input v-model="record.value" />
                            <div>
                              <a-icon type="question-circle" />
                              自定义组织接口服务需要继承于com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider,
                              其中服务类型编码即接口需要实现的type
                            </div>
                          </template>
                          <a-button size="small" type="link" icon="code"></a-button>
                        </a-popover>
                        <a-button
                          size="small"
                          type="link"
                          icon="delete"
                          @click="widget.configuration.orgSelectTypes.splice(index, 1)"
                        ></a-button>
                      </template>
                    </template>
                  </a-input>
                </a-col>
              </a-row>
            </template>

            <template slot="footer">
              <a-button type="link" icon="plus" :style="{ paddingLeft: '7px' }" @click.stop="addCustomOrgOption">自定义组织选择项</a-button>
            </template>
          </a-table>
          <a-form-model-item
            label="打开弹窗重新加载数据"
            :label-col="{ span: 10 }"
            :wrapper-col="{ span: 13, style: { textAlign: 'right' } }"
          >
            <a-switch v-model="widget.configuration.refetchDataOnOpenModal" />
          </a-form-model-item>

          <a-collapse :bordered="false" expandIconPosition="right">
            <a-collapse-panel key="component_mode_properties" header="组件模式属性">
              <a-form-model-item label="默认状态" class="item-lh">
                <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
                  <a-radio-button value="edit">可编辑</a-radio-button>
                  <a-radio-button value="unedit">不可编辑</a-radio-button>
                  <a-radio-button value="hidden">隐藏</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="edit_mode_properties" header="编辑模式属性">
              <a-form-model-item>
                <template #label>
                  <FormItemTooltip
                    label="显示值字段"
                    text="当选项选择后，显示值将一并更新至该字段。可配置隐藏字段，用于数据提交、存储。"
                  ></FormItemTooltip>
                </template>
                <a-select
                  allowClear
                  :options="inputFieldOptions"
                  v-model="widget.configuration.displayValueField"
                  :getPopupContainer="getPopupContainerByPs()"
                  :dropdownClassName="getDropdownClassName()"
                ></a-select>
              </a-form-model-item>
              <a-form-model-item label="是否多选" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                <a-switch v-model="widget.configuration.multiSelect" />
              </a-form-model-item>
              <a-form-model-item label="返回值格式">
                <a-radio-group size="small" v-model="widget.configuration.isPathValue" button-style="solid">
                  <a-radio-button :value="true">完整格式</a-radio-button>
                  <a-radio-button :value="false">仅组织ID</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="选中节点显示">
                <a-radio-group size="small" v-model="widget.configuration.choosenTitleDisplay" button-style="solid">
                  <a-radio-button value="title">名称</a-radio-button>
                  <a-radio-button value="shortTitle">简称</a-radio-button>
                  <a-radio-button value="titlePath">层级名称</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="可选节点类型">
                <a-select
                  mode="tags"
                  style="width: 100%"
                  v-model="widget.configuration.checkableTypes"
                  :showArrow="true"
                  :getPopupContainer="getPopupContainerByPs()"
                  :dropdownClassName="getDropdownClassName()"
                >
                  <a-select-option v-for="(opt, i) in checkTypeOptions" :value="opt.value" :key="'checktyp_' + i">
                    {{ opt.label }}
                  </a-select-option>
                </a-select>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="un_edit_mode_properties" header="不可编辑模式属性">
              <a-form-model-item label="不可编辑状态" class="item-lh">
                <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
                  <a-radio-button value="label">纯文本</a-radio-button>
                  <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="other_properties" header="其他属性">
              <a-form-model-item label="应用于">
                <FieldApplySelect v-model="widget.configuration.applyToDatas" />
              </a-form-model-item>
              <a-form-model-item label="字段映射">
                <WidgetDesignDrawer :id="'fieldMappingConfigure' + widget.id" title="字段映射" :designer="designer">
                  <a-button size="small">添加字段映射</a-button>
                  <template slot="content">
                    <OrgFieldMapping :widget="widget" ref="fieldMapping" :designer="designer" />
                  </template>
                </WidgetDesignDrawer>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="component_style" header="组件样式">
              <a-form-model-item label="弹窗标题">
                <a-input v-model="widget.configuration.modalTitle">
                  <template slot="addonAfter">
                    <WI18nInput :widget="widget" :designer="designer" code="modalTitle" v-model="widget.configuration.modalTitle" />
                  </template>
                </a-input>
              </a-form-model-item>
              <a-form-model-item label="输入框风格">
                <a-select v-model="widget.configuration.inputDisplayStyle" :getPopupContainer="getPopupContainerByPs()">
                  <a-select-option value="IconLabel">标签</a-select-option>
                  <a-select-option value="GroupIconLabel">标签分组</a-select-option>
                  <a-select-option value="Label">
                    纯文本(以{{ widget.configuration.separator == ';' ? '分号;' : '逗号,' }}分隔)
                  </a-select-option>
                </a-select>
              </a-form-model-item>
              <a-form-model-item
                v-if="designer.terminalType == 'mobile'"
                label="显示边框"
                class="item-lh"
                :wrapper-col="{ style: { textAlign: 'right' } }"
              >
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.bordered" />
              </a-form-model-item>
            </a-collapse-panel>
          </a-collapse>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration :widget="widget" :unique="true"></ValidateRuleConfiguration>
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '@framework/vue/utils/util';
import OrgFieldMapping from './org-field-mapping.vue';
import filterNodeConfiguraiton from './filterNodeConfiguration.vue';

export default {
  name: 'WidgetFormOrgSelectConfiguration',
  mixins: [formConfigureMixin, draggable],
  inject: ['appId'],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      orgOptions: [],
      bizOrgOptions: [],
      checkTypeOptions: [],
      orgSelectTypeColumns: [
        {
          title: '名称',
          dataIndex: 'label',
          scopedSlots: { customRender: 'labelSlot' }
        }
        // {
        //   title: '操作',
        //   dataIndex: 'operation',
        //   width: 70,
        //   align: 'right',
        //   scopedSlots: { customRender: 'operationSlot' }
        // }
      ],
      rules: {
        name: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="字段名称必填" />,
          trigger: ['blur', 'change'],
          whitespace: true
        },
        code: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="字段编码必填" />,
          trigger: ['blur', 'change'],
          whitespace: true
        }
      }
    };
  },

  beforeCreate() {},
  components: { OrgFieldMapping, filterNodeConfiguraiton },
  computed: {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { bordered: false });
    }
    if (!this.widget.configuration.hasOwnProperty('filterNode')) {
      this.$set(this.widget.configuration, 'filterNode', {
        showType: undefined,
        showData: '',
        showFunction: undefined,
        hideType: undefined,
        hideData: '',
        hideFunction: undefined,
        hideNoUserNode: false
      });
    }
  },
  methods: {
    onChangeOrgUuid() {
      if (this.$refs.showFilterNodeRef && this.$refs.hideFilterNodeRef) {
        this.$refs.showFilterNodeRef.onChangeOrgUuid();
        this.$refs.hideFilterNodeRef.onChangeOrgUuid();
      }
    },
    onChangeOnlyShowBizOrg() {
      this.onChangeOrgUuid();
    },
    onChangeBizOrgSelect() {
      this.$set(this.widget.configuration, 'bizOrgIds', []);
      if (this.widget.configuration.bizOrgUuids != undefined) {
        for (let u of this.widget.configuration.bizOrgUuids) {
          this.widget.configuration.bizOrgIds.push(this.bizOrgMap[u].id);
        }
      }
      this.$nextTick(() => {
        this.onChangeOrgUuid();
      });
    },
    onChangeOrgSelect() {
      if (this.widget.configuration.bizOrgUuids == undefined) {
        this.$set(this.widget.configuration, 'bizOrgUuids', []);
      } else {
        this.widget.configuration.bizOrgUuids.splice(0, this.widget.configuration.bizOrgUuids.length);
      }
      this.bizOrgOptions.splice(0, this.bizOrgOptions.length);
      this.$set(this.widget.configuration, 'orgId', undefined);
      if (this.widget.configuration.orgUuid != undefined) {
        if (this.orgMap && this.orgMap[this.widget.configuration.orgUuid]) {
          this.widget.configuration.orgId = this.orgMap[this.widget.configuration.orgUuid].id;
        }
        this.getBizOrgOptions(this.widget.configuration.orgUuid).then(list => {
          this.bizOrgMap = {};
          if (list) {
            for (let item of list) {
              this.bizOrgMap[item.uuid] = item;
              this.bizOrgOptions.push({
                label: item.name,
                value: item.uuid
              });
            }
          }
        });
      }
      this.onChangeOrgUuid();
    },
    initBizOrgOptions() {
      this.bizOrgOptions.splice(0, this.bizOrgOptions.length);
      if (this.widget.configuration.orgUuid != undefined) {
        this.getBizOrgOptions(this.widget.configuration.orgUuid).then(list => {
          this.bizOrgMap = {};
          if (list) {
            for (let item of list) {
              this.bizOrgMap[item.uuid] = item;
              this.bizOrgOptions.push({
                label: item.name,
                value: item.uuid
              });
            }
          }
        });
      }
    },
    getBizOrgOptions(orgUuid) {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/org/biz/getValidBizOrg`, {
            params: {
              orgUuid
            }
          })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    addCustomOrgOption() {
      this.widget.configuration.orgSelectTypes.push({
        id: 'customize-org-type-' + generateId(6),
        value: undefined,
        label: undefined
      });
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    fetchModuleSystemOrgOptions(appId) {
      this.orgOptions.splice(0, this.orgOptions.length);
      if (appId != undefined) {
        $axios
          .get(`/proxy/api/app/module/getModuleRelaSystems`, {
            params: {
              moduleId: appId
            }
          })
          .then(({ data }) => {
            if (data.data) {
              this.fetchOrgOptions(data.data);
            }
          })
          .catch(error => {});
      }
    },
    fetchOrgOptions(systems) {
      let _this = this;
      this.orgMap = {};
      $axios
        .post(`/proxy/api/org/organization/queryEnableOrgUnderSystems`, systems)
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            for (let i = 0, len = data.data.length; i < len; i++) {
              this.orgMap[data.data[i].uuid] = data.data[i];
              if (this.widget.configuration.orgUuid == data.data[i].uuid && this.widget.configuration.orgId == undefined) {
                this.widget.configuration.orgId = data.data[i].id;
              }
              this.orgOptions.push({
                label: data.data[i].name,
                value: data.data[i].uuid
              });
            }
          }
        })
        .catch(() => {
          _this.$message.error('组织服务异常');
        });
    },
    fetchOrgElementModel() {
      let _this = this;
      $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          let opt = [],
            models = {};
          for (let i = 0, len = data.data.length; i < len; i++) {
            if (data.data[i].enable) {
              models[data.data[i].id] = data.data[i];
            }
          }
          if (models.unit) {
            opt.push({ label: models.unit.name, value: 'unit' });
            delete models.unit;
          }
          if (models.dept) {
            opt.push({ label: models.dept.name, value: 'dept' });
            delete models.dept;
          }
          if (models.job) {
            opt.push({ label: models.job.name, value: 'job' });
            delete models.job;
          }
          opt.push({ label: '用户', value: 'user' });
          opt.push({ label: '群组', value: 'group' });
          opt.push({ label: '职务', value: 'duty' });
          for (let k in models) {
            opt.push({ label: models[k].name, value: k });
          }
          opt.push({ label: '业务角色', value: 'bizRole' });

          _this.checkTypeOptions = opt;
        }
      });
    }
  },
  beforeMount() {
    this.fetchModuleSystemOrgOptions(this.appId);
    this.designer.handleEvent('appId:change', appId => {
      this.fetchModuleSystemOrgOptions(appId);
    });
    this.fetchOrgElementModel();
    this.initBizOrgOptions();
  },
  mounted() {
    if (!this.widget.configuration.hasOwnProperty('isPathValue')) {
      this.widget.configuration.isPathValue = false;
    }
    this.tableDraggable(this.widget.configuration.orgSelectTypes, this.$el.querySelector('.org-type-table tbody'), '.drag-handler');
  }
};
</script>
