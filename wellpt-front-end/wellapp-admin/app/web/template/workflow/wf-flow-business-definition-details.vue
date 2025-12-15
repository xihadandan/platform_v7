<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="formData" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form">
      <a-tabs>
        <a-tab-pane key="basicInfo" tab="基本信息">
          <a-form-model-item label="名称" prop="name">
            <a-input v-model="formData.name" />
          </a-form-model-item>
          <a-form-model-item label="ID" prop="id">
            <a-input v-model="formData.id" :readOnly="formData.uuid != null" />
          </a-form-model-item>
          <a-form-model-item label="编号" prop="code">
            <a-input v-model="formData.code" />
          </a-form-model-item>
          <a-form-model-item label="使用流程" prop="flowDefId">
            <a-select
              v-model="formData.flowDefId"
              show-search
              style="width: 100%"
              :filter-option="filterSelectOption"
              :options="flowDefOptions"
              @change="handleFlowDefChange"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="监听器" prop="listener">
            <a-select v-model="listeners" mode="multiple" show-search style="width: 100%" :filter-option="filterSelectOption">
              <a-select-option v-for="d in listenerOptions" :key="d.id">
                {{ d.text }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="备注" prop="remark">
            <a-textarea v-model="formData.remark" />
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="stageInfo" tab="阶段列表">
          <a-row>
            <a-col span="8">
              <a-button @click="addStage">新增</a-button>
              <a-button @click="addSubStage">新增子阶段</a-button>
              <a-button @click="deleteStage">删除</a-button>
              <a-tree
                :tree-data="formData.stages"
                :selectable="true"
                :showLine="true"
                :replaceFields="{ title: 'name', key: 'id' }"
                @select="onStageTreeSelect"
              ></a-tree>
            </a-col>
            <a-col span="16">
              <a-form-model-item label="名称">
                <a-input v-model="selectedStage.name" />
              </a-form-model-item>
              <a-form-model-item label="ID">
                <a-input v-model="selectedStage.id" />
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-tab-pane>
        <a-tab-pane key="stateInfo" tab="状态信息">
          <a-form-model-item label="业务状态">
            <a-button @click="addState">新增</a-button>
            <a-button @click="deleteState">删除</a-button>
          </a-form-model-item>
          <div
            v-for="(state, index) in formData.states"
            :key="index"
            @click="onStateClick(state)"
            :class="{ selected: state.id == selectedState.id }"
          >
            <a-form-model-item label="状态类型名称">
              <a-input v-model="state.stateTypeName" />
            </a-form-model-item>
            <a-form-model-item label="状态名称写入字段">
              <a-select v-model="state.stateNameField" show-search style="width: 100%" :filter-option="filterSelectOption">
                <a-select-option v-for="d in flowOptionData.formFields" :key="d.id">
                  {{ d.text }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="状态代码写入字段">
              <a-select v-model="state.stateCodeField" show-search style="width: 100%" :filter-option="filterSelectOption">
                <a-select-option v-for="d in flowOptionData.formFields" :key="d.id">
                  {{ d.text }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="状态列表">
              <a-button @click="addStateConfig(state)">新增</a-button>
              <a-button @click="deleteStateConfig(state)">删除</a-button>
            </a-form-model-item>
            <a-table
              rowKey="id"
              :columns="stateConfigColumns"
              :data-source="state.stateConfigs"
              :row-selection="{ selectedRowKeys: stateConfigSelectedRowKeys, onChange: onStateConfigSelectChange }"
            >
              <template slot="titleSlot" slot-scope="text, record">
                <a-tree-select
                  v-model="record.stageId"
                  :tree-data="formData.stages"
                  :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
                ></a-tree-select>
              </template>
              <template slot="stateNameSlot" slot-scope="text, record">
                <a-input v-model="record.stateNameValue" readOnly @click="onStateNameValueClick(record)" />
              </template>
              <template slot="stateCodeSlot" slot-scope="text, record">
                <a-input v-model="record.stateCodeValue" readOnly @click="onStateCodeValueClick(record)" />
              </template>
              <template slot="triggerTypeSlot" slot-scope="text, record">
                <a-select
                  v-model="record.triggerType"
                  show-search
                  style="width: 100%"
                  :filter-option="filterSelectOption"
                  :options="triggerTypeOptions"
                ></a-select>
              </template>
              <template slot="triggerOptionSlot" slot-scope="text, record">
                <template v-if="record.triggerType == 'TASK_OPERATION'">
                  <a-row>
                    <a-col span="16">
                      <a-select
                        v-model="record.taskIds"
                        mode="multiple"
                        show-search
                        style="width: 100%"
                        :filter-option="filterSelectOption"
                      >
                        <a-select-option v-for="d in flowOptionData.taskIds" :key="d.id">
                          {{ d.text }}
                        </a-select-option>
                      </a-select>
                    </a-col>
                    <a-col span="8">
                      <a-select v-model="record.actionType" show-search style="width: 100%" :filter-option="filterSelectOption">
                        <a-select-option value="Submit">提交</a-select-option>
                        <a-select-option value="Rollback">退回</a-select-option>
                        <a-select-option value="Cancel">撤回</a-select-option>
                      </a-select>
                    </a-col>
                  </a-row>
                </template>
                <template v-if="record.triggerType == 'TASK_BELONG'">
                  <a-select v-model="record.taskIds" mode="multiple" show-search style="width: 100%" :filter-option="filterSelectOption">
                    <a-select-option v-for="d in flowOptionData.taskIds" :key="d.id">
                      {{ d.text }}
                    </a-select-option>
                  </a-select>
                </template>
                <template v-if="record.triggerType == 'DIRECTION_TRANSITION'">
                  <a-select
                    v-model="record.directionIds"
                    mode="multiple"
                    show-search
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                  >
                    <a-select-option v-for="d in flowOptionData.directions" :key="d.id">
                      {{ d.text }}
                    </a-select-option>
                  </a-select>
                </template>
              </template>
            </a-table>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
    <a-modal
      title="状态名称配置"
      width="650px"
      :visible="stateNameValueModalVisible"
      @ok="handleStateNameValueOk"
      @cancel="handleStateNameValueCancel"
    >
      <a-form-model :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
        <a-form-model-item label="状态名称来源">
          <a-radio-group v-model="stateNameType" default-value="constant">
            <a-radio value="constant">固定值</a-radio>
            <a-radio value="freemarker">freemarker表达式</a-radio>
            <a-radio value="groovy">groovy脚本</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="状态名称">
          <a-textarea v-model="stateNameValue"></a-textarea>
          <div v-if="stateNameType == 'freemarker'" class="remark" v-html="freemarkerScriptRemark"></div>
          <div v-if="stateNameType == 'groovy'" class="remark" v-html="groovyScriptRemark"></div>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
    <a-modal
      title="状态代码配置"
      width="650px"
      :visible="stateCodeValueModalVisible"
      @ok="handleStateCodeValueOk"
      @cancel="handleStateCodeValueCancel"
    >
      <a-form-model :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
        <a-form-model-item label="状态代码来源">
          <a-radio-group v-model="stateCodeType" default-value="1">
            <a-radio value="1">固定值</a-radio>
            <a-radio value="2">freemarker表达式</a-radio>
            <a-radio value="3">groovy脚本</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="状态代码">
          <a-textarea v-model="stateCodeValue"></a-textarea>
          <div v-if="stateCodeType == '2'" class="remark" v-html="freemarkerScriptRemark"></div>
          <div v-if="stateCodeType == '3'" class="remark" v-html="groovyScriptRemark"></div>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </a-skeleton>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = {};
    if ($event.meta && $event.meta.definitionJson) {
      formData = JSON.parse($event.meta.definitionJson);
      if ($event.meta.uuid) {
        formData.uuid = $event.meta.uuid;
      }
    }
    let listeners = [];
    if (formData.listener) {
      listeners = formData.listener.split(';');
    }
    if (!formData.stages) {
      formData.stages = [];
    }
    if (!formData.states) {
      formData.states = [];
    } else {
      formData.states.forEach(state => {
        if (!state.id) {
          state.id = generateId();
        }
        if (state.stateConfigs) {
          state.stateConfigs.forEach(stateConfig => {
            if (!stateConfig.id) {
              stateConfig.id = generateId();
            }
            // // 环节ID、流程ID转为数组形式
            // if (stateConfig.taskId) {
            //   stateConfig.taskId = stateConfig.taskId.split(';');
            // } else {
            //   stateConfig.taskId = [];
            // }
            // if (stateConfig.directionId) {
            //   stateConfig.directionId = stateConfig.directionId.split(';');
            // } else {
            //   stateConfig.directionId = [];
            // }
          });
        }
      });
    }
    return {
      loading: formData.uuid != undefined,
      uuid: formData.uuid,
      formData,
      listeners,
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] },
        id: { required: true, message: 'ID必填', trigger: ['blur', 'change'] }
      },
      flowDefOptions: [],
      flowOptionData: { directions: [], formFields: [], taskIds: [] },
      listenerOptions: [],
      selectedStage: { name: '', id: '' },
      stageTreeExpandedKeys: [],
      selectedState: { id: '', stateConfigs: [] },
      stateConfigColumns: [
        { title: '阶段名称', dataIndex: 'stageId', scopedSlots: { customRender: 'titleSlot' } },
        { title: '状态名称', dataIndex: 'stateNameValue', scopedSlots: { customRender: 'stateNameSlot' } },
        { title: '状态代码', dataIndex: 'stateCodeValue', scopedSlots: { customRender: 'stateCodeSlot' } },
        { title: '触发类型', dataIndex: 'triggerType', scopedSlots: { customRender: 'triggerTypeSlot' } },
        { title: '触发选项', dataIndex: 'taskIds', scopedSlots: { customRender: 'triggerOptionSlot' } }
      ],
      stateConfigSelectedRowKeys: [],
      triggerTypeOptions: [
        { label: '流程开始', value: 'FLOW_STARTED' },
        { label: '流程办结', value: 'FLOW_END' },
        { label: '环节操作', value: 'TASK_OPERATION' },
        { label: '环节归属', value: 'TASK_BELONG' },
        { label: '流向流转', value: 'DIRECTION_TRANSITION' }
      ],
      stateNameValueModalVisible: false,
      stateCodeValueModalVisible: false,
      stateNameType: '',
      stateNameValue: '',
      stateCodeType: '',
      stateCodeValue: ''
    };
  },
  computed: {
    freemarkerScriptRemark() {
      return `1、freemarker支持的内置变量：<br/>
        event: 触发类型对应的事件对象，可获取当前流程信息，例如：当前环节id, \${event.taskId}<br/>
        dyFormData: 表单dyFormData对象
        formData: 表单数据对象,可以通过\${formData.表单ID.表单字段}的方式取值，例如：002_\${formData.item_handling_form.project_code};<br/>
        <br/>
        2、其他系统默认的变量：：<br/>
        currentUserName ：当前用户的名称<br/>
        currentLoginName ：当前用户的登录名<br/>
        currentUserId ：当前用户ID<br/>
        currentUserUnitId ：当前用户归属的组织ID<br/>
        currentUserUnitName ：当前用户归属的组织名称<br/>
        currentUserDepartmentId ：当前用户归属的部门ID<br/>
        currentUserDepartmentName ：当前用户归属的部门名称<br/>
        sysdate ：当前时间。\${sysdate?datetime}获取当前完整的时间格式
      `;
    },
    groovyScriptRemark() {
      return `1、groovy支持的内置变量：<br/>
        event: 触发类型对应的事件对象，可获取当前流程信息，例如：当前环节id, event.taskId<br/>
        dyFormData: 表单dyFormData对象<br/>
        formData: 表单数据对象,可以通过formData.表单ID.表单字段的方式取值，例如："003_" + formData.item_handling_form.project_code;<br/>
        <br/>
        currentUserName ：当前用户的名称<br/>
        currentLoginName ：当前用户的登录名<br/>
        currentUserId ：当前用户ID<br/>
        currentUserUnitId ：当前用户归属的组织ID<br/>
        currentUserUnitName ：当前用户归属的组织名称<br/>
        currentUserDepartmentId ：当前用户归属的部门ID<br/>
        currentUserDepartmentName ：当前用户归属的部门名称<br/>
        sysdate ：当前时间
      `;
    }
  },
  watch: {
    listeners: function (newVal) {
      if (newVal) {
        this.formData.listener = newVal.join(';');
      } else {
        this.formData.listener = '';
      }
    }
  },
  mounted() {
    this.loading = false;
    this.handleFlowDefSearch();
    this.handleListenerSearch();
    if (this.formData.flowDefId) {
      this.handleFlowDefChange(this.formData.flowDefId);
    }
  },
  methods: {
    handleFlowDefSearch(value = '') {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'flowSchemeService',
          queryMethod: 'loadSelectData',
          searchValue: value
        })
        .then(({ data }) => {
          if (data.results) {
            let options = [];
            let map = new Map();
            data.results.forEach(item => {
              if (map.has(item.id)) {
                return;
              }
              map.set(item.id, item.id);
              options.push({
                value: item.id,
                label: item.text
              });
            });
            _this.flowDefOptions = options;
          }
        });
    },
    handleFlowDefChange(flowDefId) {
      let _this = this;
      if (!flowDefId) {
        return;
      }
      _this.$axios.get(`/proxy/api/workflow/business/definition/getSelectDataByFlowDefId//${flowDefId}`).then(({ data }) => {
        if (data.data) {
          _this.flowOptionData = data.data;
        }
      });
    },
    handleListenerSearch(value = '') {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'wfFlowBusinessDefinitionFacadeService',
          queryMethod: 'listFlowBusinessListenerSelectData',
          searchValue: value
        })
        .then(({ data }) => {
          if (data.results) {
            _this.listenerOptions = data.results;
          }
        });
    },
    filterSelectOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    addStage() {
      this.formData.stages.push({ name: '新阶段', id: `stage_${generateId()}`, children: [] });
    },
    addSubStage() {
      if (!this.selectedStage.id) {
        this.$message.error('请选择阶段！');
      }
      if (!this.selectedStage.children) {
        this.selectedStage.children = [];
      }
      this.selectedStage.children.push({ name: '子阶段', id: `stage_${generateId()}`, children: [] });
      // this.stageTreeExpandedKeys = [this.selectedStage.id];
    },
    deleteStage() {
      const _this = this;
      if (!_this.selectedStage.id) {
        _this.$message.error('请选择阶段！');
      }
      let doDelete = treeNodes => {
        if (!_this.selectedStage.id) {
          return;
        }
        treeNodes.forEach((node, index) => {
          if (node.id == _this.selectedStage.id) {
            treeNodes.splice(index, 1);
            _this.selectedStage = {};
          }
          if (node.children) {
            doDelete(node.children);
          }
        });
      };
      doDelete(_this.formData.stages);
    },
    onStageTreeSelect(selectedKeys, e) {
      if (e.selected) {
        this.selectedStage = e.selectedNodes[0].componentOptions.propsData.dataRef;
      } else {
        this.selectedStage = {};
      }
    },
    addState() {
      this.formData.states.push({
        id: generateId(),
        stateConfigs: []
      });
    },
    deleteState() {
      const _this = this;
      if (!_this.selectedState.id) {
        _this.$message.error('请选择阶段！');
      }
      this.formData.states.forEach((state, index) => {
        if (state.id == _this.selectedState.id) {
          this.formData.states.splice(index, 1);
        }
      });
    },
    onStateClick(state) {
      this.selectedState = state;
    },
    addStateConfig(state) {
      const _this = this;
      _this.selectedState = state;
      _this.selectedState.stateConfigs.push({
        id: generateId(),
        stateNameType: 'constant',
        stateCodeType: 'constant'
      });
    },
    deleteStateConfig(state) {
      const _this = this;
      _this.selectedState = state;
      if (_this.stateConfigSelectedRowKeys.length == 0) {
        _this.$message.error('请选择记录！');
        return;
      }
      _this.stateConfigSelectedRowKeys.forEach(rowKey => {
        let index = state.stateConfigs.findIndex(item => item.id == rowKey);
        if (index != -1) {
          state.stateConfigs.splice(index, 1);
        }
      });
      _this.stateConfigSelectedRowKeys = [];
    },
    onStateConfigSelectChange(selectedRowKeys) {
      this.stateConfigSelectedRowKeys = selectedRowKeys;
    },
    onStateNameValueClick(record) {
      this.stateNameType = record.stateNameType;
      this.stateNameValue = record.stateNameValue;
      this.stateNameRecord = record;
      this.stateNameValueModalVisible = true;
    },
    handleStateNameValueOk() {
      this.stateNameRecord.stateNameType = this.stateNameType;
      this.stateNameRecord.stateNameValue = this.stateNameValue;
      this.stateNameValueModalVisible = false;
    },
    handleStateNameValueCancel() {
      this.stateNameRecord = null;
      this.stateNameValueModalVisible = false;
    },
    onStateCodeValueClick(record) {
      this.stateCodeType = record.stateCodeType;
      this.stateCodeValue = record.stateCodeValue;
      this.stateCodeRecord = record;
      this.stateCodeValueModalVisible = true;
    },
    handleStateCodeValueOk() {
      this.stateCodeRecord.stateCodeType = this.stateCodeType;
      this.stateCodeRecord.stateCodeValue = this.stateCodeValue;
      this.stateCodeValueModalVisible = false;
    },
    handleStateCodeValueCancel() {
      this.stateCodeRecord = null;
      this.stateCodeValueModalVisible = false;
    },
    save() {
      const _this = this;
      _this.$refs.form.validate().then(valid => {
        if (valid) {
          let formData = deepClone(_this.formData);
          // 环节ID、流程ID转为字符串形式
          // if (formData.states) {
          //   formData.states.forEach(state => {
          //     if (state.stateConfigs) {
          //       state.stateConfigs.forEach(stateConfig => {
          //         if (Array.isArray(stateConfig.taskId)) {
          //           stateConfig.taskId = stateConfig.taskId.join(';');
          //         }
          //         if (Array.isArray(stateConfig.directionId)) {
          //           stateConfig.directionId = stateConfig.directionId.join(';');
          //         }
          //       });
          //     }
          //   });
          // }
          formData.definitionJson = JSON.stringify(formData);
          $axios
            .post('/proxy/api/workflow/business/definition/save', formData)
            .then(({ data }) => {
              if (data.success) {
                _this.$message.success('保存成功！');
                _this.pageContext.emitEvent('ZoDcgRJgKCTRtGkGHKbpfGFJnofKrxtk:closeModal');
                _this.pageContext.emitEvent('XZKNoioHBnLLNUkqFRNibIOnuAFwofpp:refetch');
              } else {
                _this.$message.error(data.msg);
              }
            })
            .catch(({ response }) => {
              _this.$message.error(response.data.msg);
            });
        }
      });
    }
  },
  META: {
    method: {
      save: '保存流程业务定义'
    }
  }
};
</script>

<style lang="less" scoped>
.selected {
  background-color: #eee;
}

.remark {
  line-height: 1.5;
  font-size: 14px;
}
</style>
