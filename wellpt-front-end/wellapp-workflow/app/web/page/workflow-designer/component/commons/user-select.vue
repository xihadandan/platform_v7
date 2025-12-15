<template>
  <div class="org-select-component">
    <div :class="['org-select-input', 'user-select-input']" @click="clickOpenModal">
      <a-icon :type="visible ? 'folder-open' : 'folder'" class="input-suffix-icon" @click.stop="openModal" />
      <a-icon
        v-if="valueNodes && valueNodes.length > 0"
        type="close-circle"
        theme="filled"
        class="input-suffix-icon close"
        :style="{
          right: showOpenIcon ? '27px' : '2px'
        }"
        @click.stop="clearAllInput"
      />

      <a-skeleton active :loading="valueLoading" :paragraph="{ rows: 1, width: '95%' }" :title="false">
        <div v-if="displayType == 'merge'">
          <div v-if="hasText">{{ text }}</div>
          <template v-for="(node, i) in mergeValueNodes">
            <a-tag :key="'tag_merge' + i" :title="node" color="blue" v-if="node && i !== 'optionf'">
              <Icon :type="tpyeIcon[i]" />
              {{ textOverflow(node) }}
            </a-tag>
          </template>
          <template v-if="mergeValueNodes.optionf">
            <div :style="{ marginTop: hasText ? 'var(--w-margin-3xs)' : '' }">{{ text }}过滤条件</div>
            <a-tag :key="'tag_merge_optionf'" :title="mergeValueNodes.optionf" color="var(--w-primary-color-1)">
              <Icon :type="tpyeIcon.optionf" />
              {{ textOverflow(mergeValueNodes.optionf) }}
            </a-tag>
          </template>
        </div>
        <div v-else>
          <a-tag
            v-for="node in valueNodes"
            :key="'tag_' + node.value"
            :title="titleSet(node.argValue, node.value)"
            color="var(--w-primary-color-1)"
          >
            <Icon v-if="iconSet(node.argValue)" :type="orgElementIcon[iconSet(node.argValue)]" />
            {{ textOverflow(titleSet(node.argValue, node.value)) }}
          </a-tag>
        </div>
      </a-skeleton>
    </div>
    <a-modal
      class="wf-users-select-modal"
      :mask="false"
      :maskClosable="false"
      :title="title"
      :visible="visible"
      :okText="okText"
      cancelText="取消"
      :width="800"
      :bodyStyle="{ padding: '12px 20px', height: '600px', 'overflow-y': 'auto' }"
      @cancel="onCancelModal"
      @ok="onOkModal"
      ref="modal"
    >
      <template v-if="formData">
        <a-form-model
          ref="form"
          :model="formData"
          :colon="false"
          labelAlign="left"
          :label-col="{ flex: '100px' }"
          :wrapper-col="{ flex: 'auto' }"
        >
          <a-form-model-item prop="unit" label="组织机构" v-if="types.indexOf('unit') > -1">
            <org-select
              :orgVersionId="orgVersionId"
              :orgVersionIds="orgVersionIds"
              v-model="formData.unit"
              v-if="orgVersionId"
              :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
              @change="changeOrg"
            />
          </a-form-model-item>
          <a-form-model-item prop="field" label="表单字段" v-if="types.indexOf('field') > -1">
            <dyform-fields-tree-select v-model="formData.field" :formData="formData" @change="changeFields"></dyform-fields-tree-select>
          </a-form-model-item>
          <a-form-model-item prop="task" label="环节" v-if="types.indexOf('task') > -1">
            <node-task-select mode="multiple" v-model="formData.task" @change="changeTask"></node-task-select>
          </a-form-model-item>
          <a-form-model-item prop="direction" label="流向" v-if="types.indexOf('direction') > -1"></a-form-model-item>
          <template v-if="types.indexOf('option') > -1">
            <a-form-model-item prop="option1" label="人员选项">
              <a-row>
                <a-col span="12" v-for="litem in handlerList" :key="litem.type">
                  <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
                    <a-checkbox :checked="formData.option.indexOf(item.value) > -1" :value="item" @change="onChangeOptions">
                      {{ item.name }}
                    </a-checkbox>
                  </div>
                </a-col>
              </a-row>
            </a-form-model-item>
            <a-form-model-item prop="option2" label="人员过滤">
              <a-row>
                <a-col span="12" v-for="litem in handlerFilterList" :key="litem.type">
                  <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
                    <a-checkbox :checked="formData.option.indexOf(item.value) > -1" :value="item" @change="onChangeOptions">
                      {{ item.name }}
                    </a-checkbox>
                  </div>
                </a-col>
              </a-row>
            </a-form-model-item>
          </template>
          <a-form-model-item prop="custom" label="自定义" v-if="types.indexOf('custom') > -1">
            <user-select-custom
              v-model="formData.custom"
              :data="selectedNodes.custom.data"
              :orgVersionId="orgVersionId"
              :orgVersionIds="orgVersionIds"
              @change="onChangCustom"
            ></user-select-custom>
          </a-form-model-item>
        </a-form-model>
      </template>
      <template slot="footer">
        <a-button type="primary" @click="onOkModal">{{ okText }}</a-button>
        <a-button type="danger" @click="clearAll">清空</a-button>
        <a-button @click="onCancelModal">取消</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { filter, map, each, findIndex, isInteger, some } from 'lodash';
import { Empty } from 'ant-design-vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { deepClone } from '@framework/vue/utils/util';
import { handlerList, handlerFilterList } from '../designer/constant';
import DyformFieldsTreeSelect from './dyform-fields-tree-select.vue';
import NodeTaskSelect from './node-task-select';
import UserSelectCustom from './user-select-custom.vue';

export default {
  name: 'UserSelect',
  inject: ['designer', 'workFlowData'],
  components: {
    OrgSelect,
    DyformFieldsTreeSelect,
    NodeTaskSelect,
    UserSelectCustom
  },
  props: {
    title: {
      type: String,
      default: function () {
        return '选择人员';
      }
    },
    text: {
      type: String,
      default: '办理人'
    },
    value: {
      type: Array
    },
    displayType: {
      type: String,
      default: '' //默认单个显示，merge：按种类合并
    },
    types: {
      // unit组织1/field字段2/option选项8/direction/task环节
      type: String,
      default: 'unit'
    },
    separator: {
      // 分隔符
      type: String,
      default: ';'
    },
    showOpenIcon: {
      type: Boolean,
      default: true
    },
    enableCache: {
      type: Boolean,
      default: false
    },
    orgVersionId: {
      type: String
    },
    okText: {
      type: String,
      default: '确定'
    },
    textLength: {
      type: [Boolean, Number],
      default: false
    }
  },
  data() {
    let valueNodes = this.value;
    let selectedNodes = {};
    let mergeValueNodes = {};
    let formData = {};
    each(this.types.split('/'), item => {
      if (item) {
        let type = this.getTypeByCode(item);
        let data = filter(valueNodes, { type: type });
        let value = map(data, 'value');
        selectedNodes[item] = {
          type: type,
          data
        };
        formData[item] = value;
        this.mergeValueNodesSet(item, data);
      }
    });
    mergeValueNodes = deepClone(this.mergeValueNodes);
    let orgElementIcon = window.localStorage.getItem(`orgElementIcon`);
    orgElementIcon = orgElementIcon ? JSON.parse(orgElementIcon) : { user: 'user' };
    return {
      options: [],
      visible: false,
      orgElementIcon,
      selectedNodes, // 存所有值
      valueNodes, // 存显示组件值
      mergeValueNodes, // 存merge时显示组件值,
      valueLoading: false,
      formData, // 存输入组件值
      handlerList,
      handlerFilterList,
      tpyeIcon: {},
      fields: {},
      nodes: {}
    };
  },
  filters: {},
  computed: {
    hasText() {
      return some(this.types.split('/'), (item, index) => {
        return this.mergeValueNodes[item];
      });
    },
    orgVersionIds() {
      return this.workFlowData.property.orgVersionIds || [];
    }
  },
  beforeCreate() {},
  created() {
    this.fetchOrgElementModel();
    this.getAllfields();
    this.getAllNodes();
  },
  beforeMount() {},
  mounted() {},
  methods: {
    textOverflow(text) {
      if (text === null || text === undefined) {
        text = '';
      }
      if (text.indexOf('|') > -1) {
        let _text = text.split('|');
        text = _text[0];
      }
      if (this.textLength) {
        let length = isInteger(this.textLength) ? this.textLength : 10;
        if (text.length > length) {
          // 超过30个字长度，省略后续字处理
          return text.substr(0, length) + '...';
        }
      }
      return text;
    },
    // 根据类型，获取对应type值
    getTypeByCode(code) {
      if (code == 'unit') {
        return 1;
      } else if (code == 'field') {
        return 2;
      } else if (code == 'option') {
        return 8;
      } else if (code == 'task' && this.types.indexOf('unit') > -1) {
        return 4;
      } else if (code == 'custom') {
        return 16;
      } else if (code == 'direction') {
        // return "16";
      }
      return 32;
    },
    titleSet(text, value) {
      if (text === null || text === undefined) {
        text = '';
        if (value) {
          text = this.getNodeText(value);
        }
      }
      if (text.indexOf('|') > -1) {
        let _text = text.split('|');
        return _text[0];
      }
      return text;
    },
    iconSet(text) {
      if (text === null || text === undefined) {
        text = '';
      }
      if (text.indexOf('|') > -1) {
        let _text = text.split('|');
        return _text[1];
      }
      return '';
    },
    getNodeText(value) {
      let text = '';
      some(this.handlerList, item => {
        let hasIndex = findIndex(item.list, { value: value });
        if (hasIndex > -1) {
          text = item.list[hasIndex].name;
        }
        return text;
      });
      if (!text) {
        some(this.handlerFilterList, item => {
          let hasIndex = findIndex(item.list, { value: value });
          if (hasIndex > -1) {
            text = item.list[hasIndex].name;
          }
          return text;
        });
      }
      if (!text) {
        let fields = this.fields;
        if (!this.fields) {
          fields = this.getAllfields();
        }
        if (fields[value]) {
          text = fields[value];
        }
      }
      if (!text) {
        let nodes = this.nodes;
        if (!this.nodes) {
          nodes = this.getAllNodes();
        }
        if (nodes[value]) {
          text = nodes[value];
        }
      }
      return text;
    },
    getAllfields() {
      this.fields = {};
      if (this.designer.formFieldDefinition && this.designer.formDefinition) {
        let formDefinition = JSON.parse(this.designer.formDefinition.definitionVjson);
        each(formDefinition.fields, item => {
          this.fields[item.configuration.code] = item.configuration.name;
        });
        each(formDefinition.subforms, item => {
          each(item.configuration.columns, citem => {
            this.fields[item.configuration.formUuid + ':' + citem.dataIndex] = citem.title;
          });
        });
      }
      return this.fields;
    },
    getAllNodes() {
      this.nodes = {};
      if (this.graph && this.graph.instance) {
        each(this.graph.instance.tasksData, item => {
          this.nodes[item.id] = item.name;
        });
      }
      return this.nodes;
    },
    // 获取组织机构图标
    fetchOrgElementModel() {
      let _this = this;
      if (!_this.orgElementIcon) {
        let orgElementIcon = window.localStorage.getItem(`orgElementIcon`);
        _this.orgElementIcon = orgElementIcon ? JSON.parse(orgElementIcon) : { user: 'user' };
      }
      let request = () => {
        return new Promise((resolve, reject) => {
          $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            }
          });
        });
      };
      let callback = results => {
        for (let i = 0, len = results.length; i < len; i++) {
          if (results[i].enable) {
            _this.orgElementIcon[results[i].id] = results[i].icon;
          }
          window.localStorage.setItem(`orgElementIcon`, JSON.stringify(_this.orgElementIcon));
        }
      };
      if (this.enableCache) {
        this.$tempStorage.getCache(
          'getAllOrgElementModels',
          () => {
            return request();
          },
          results => {
            callback(results);
          }
        );
      } else {
        request().then(results => {
          callback(results);
        });
      }
    },
    clickOpenModal(e) {
      if (
        (e.target.classList.contains('org-select-input') || e.target.nodeName === 'DIV') &&
        !this.readonly &&
        !this.disable &&
        !this.textonly
      ) {
        this.openModal();
      }
    },
    openModal() {
      let _this = this;
      this.visible = true;
    },
    clearAllInput() {
      this.clearAll();

      this.emitValueChange();
    },
    clearAll() {
      this.valueNodes.splice(0, this.valueNodes.length);
      each(this.selectedNodes, (item, index) => {
        item.data = [];
        this.formData[index] = [];
      });
      this.mergeValueNodes = {};
    },
    emitValueChange() {
      this.$emit('input', this.valueNodes);
      this.$emit('change', {
        valueNodes: this.valueNodes,
        selectedNodes: this.selectedNodes
      });
    },
    onCancelModal() {
      let _this = this;
      this.visible = false;
      _this.$emit('cancel');
    },
    onOkModal() {
      this.valueNodes.splice(0, this.valueNodes.length);
      this.mergeValueNodes = {};
      each(this.selectedNodes, (item, index) => {
        this.valueNodes = this.valueNodes.concat(item.data);
        this.mergeValueNodesSet(index, item.data);
      }); // 最终结果值
      this.visible = false;
      this.emitValueChange();
    },
    mergeValueNodesSet(code, data) {
      if (!this.mergeValueNodes) {
        this.mergeValueNodes = {};
      }
      if (code == 'option') {
        let option = [],
          optionf = [];
        each(data, (item, index) => {
          let index0 = findIndex(handlerFilterList[0].list, { value: item.value });
          let index1 = findIndex(handlerFilterList[1].list, { value: item.value });
          if (index0 > -1 || index1 > -1) {
            optionf.push(this.titleSet(item.argValue, item.value));
          } else {
            option.push(this.titleSet(item.argValue, item.value));
          }
        });
        this.mergeValueNodes.option = option.join(';');
        this.mergeValueNodes.optionf = optionf.join(';');
      } else {
        this.mergeValueNodes[code] = map(data, citem => {
          return this.titleSet(citem.argValue, citem.value);
        }).join(';');
      }
    },
    // 选项变化事件
    onChangeOptions(e) {
      let item = e.target.value;
      let hasIndex = findIndex(this.selectedNodes.option.data, { value: item.value });
      let _hasIndex = this.formData.option.indexOf(item.value);
      if (e.target.checked) {
        if (hasIndex == -1) {
          this.selectedNodes.option.data.push({
            value: item.value,
            argValue: item.name,
            type: this.selectedNodes.option.type
          });
        }
        if (_hasIndex == -1) {
          this.formData.option.push(item.value);
        }
      } else {
        if (hasIndex > -1) {
          this.selectedNodes.option.data.splice(hasIndex, 1);
        }
        if (_hasIndex > -1) {
          this.formData.option.splice(_hasIndex, 1);
        }
      }
    },
    // 组织机构变化事件
    changeOrg({ value, label, nodes }) {
      this.selectedNodes.unit.data = map(nodes, item => {
        return {
          value: item.key,
          argValue: item.title + '|' + item.type,
          type: this.selectedNodes.unit.type
        };
      });
    },
    // 表单字段
    changeFields({ value, label, extra }) {
      this.selectedNodes.field.data = map(extra.allCheckedNodes, item => {
        let data = item.node.data.props;
        return {
          value: data.value,
          argValue: data.title,
          type: this.selectedNodes.field.type
        };
      });
    },
    // 环节字段
    changeTask(value, option) {
      this.selectedNodes.task.data = map(option, item => {
        let data = item.data.props;
        return {
          value: data.value,
          argValue: data.title,
          type: this.selectedNodes.task.type
        };
      });
    },
    onChangCustom({ value, label }) {
      this.selectedNodes.custom.data.splice(0, this.selectedNodes.custom.data.length);
      this.selectedNodes.custom.data.push({
        value: value,
        argValue: label,
        type: this.selectedNodes.custom.type
      });
    },
    changeValue(v) {
      this.valueNodes = deepClone(v);
      each(this.types.split('/'), item => {
        if (item) {
          let type = this.getTypeByCode(item);
          let data = filter(this.valueNodes, { type: type });
          let value = map(data, 'value');
          this.selectedNodes[item] = {
            type: type,
            data
          };
          this.formData[item] = value;
          this.mergeValueNodesSet(item, data);
        }
      });
    }
  },

  watch: {
    value: {
      deep: true,
      handler(v) {
        this.changeValue(v);
      }
    }
  }
};
</script>
<style lang="less">
.wf-users-select-modal {
  .ant-row {
    margin-bottom: 4px;
    display: flex;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;
    }

    .checked-div {
      line-height: var(--w-line-height);
      margin-top: 10px;
    }
  }
  .org-select-component {
    padding-top: 4px;
  }
}
.org-select-component {
  .user-select-input {
    .ant-tag {
      white-space: normal;
    }
  }
}
</style>
