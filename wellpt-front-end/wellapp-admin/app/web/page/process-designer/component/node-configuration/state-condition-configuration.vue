<template>
  <a-form-model
    class="basic-info"
    :model="stateCondition"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="名称" prop="conditionName">
      <a-input v-model="stateCondition.conditionName" />
    </a-form-model-item>
    <a-form-model-item label="状态设置为" prop="changedState">
      <a-radio-group v-model="stateCondition.changedState">
        <a-radio value="10">启动</a-radio>
        <a-radio value="20">暂停</a-radio>
        <a-radio value="30">完成</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="条件类型">
      <a-radio-group v-model="conditionConfig.type">
        <a-radio value="1">通过其他阶段状态判断</a-radio>
        <a-radio value="2">通过事项到达里程碑判断</a-radio>
        <a-radio value="9">逻辑条件</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-row v-show="conditionConfig.type == '1' || conditionConfig.type == '2'">
      <a-col span="4">
        <a-select v-model="conditionConfig.leftBracket" show-search style="width: 100%" :filter-option="false">
          <a-select-option value="">&nbsp;</a-select-option>
          <a-select-option value="(">(</a-select-option>
        </a-select>
      </a-col>
      <a-col v-show="conditionConfig.type == '1'" span="2" style="text-align: right; padding: 5px 3px">过程</a-col>
      <a-col v-show="conditionConfig.type == '1'" span="7">
        <a-select
          v-model="conditionConfig.processNodeCode"
          show-search
          style="width: 100%"
          :filter-option="filterSelectOption"
          :options="processNodeOptions"
          @change="onProcessNodeChange"
        ></a-select>
      </a-col>
      <a-col v-show="conditionConfig.type == '1'" span="2" style="text-align: right; padding: 5px 3px">状态</a-col>
      <a-col v-show="conditionConfig.type == '1'" span="4">
        <a-select
          v-model="conditionConfig.processNodeState"
          show-search
          style="width: 100%"
          :filter-option="filterSelectOption"
          :options="processNodeStateOptions"
          @change="onProcessNodeStateChange"
        ></a-select>
      </a-col>
      <a-col v-show="conditionConfig.type == '2'" span="2" style="text-align: right; padding: 5px 3px">事项</a-col>
      <a-col v-show="conditionConfig.type == '2'" span="7">
        <a-select
          v-model="conditionConfig.processItemCode"
          show-search
          style="width: 100%"
          :filter-option="filterSelectOption"
          :options="itemCodeOptions"
          @change="onItemCodeChange"
        ></a-select>
      </a-col>
      <a-col span="1"></a-col>
      <a-col span="4">
        <a-select v-model="conditionConfig.rightBracket" show-search style="width: 100%" :filter-option="false">
          <a-select-option value="">&nbsp;</a-select-option>
          <a-select-option value=")">)</a-select-option>
        </a-select>
      </a-col>
    </a-row>
    <a-form-model-item v-show="conditionConfig.type == '9'" label="逻辑条件">
      <a-radio-group v-model="conditionConfig.connector" default-value="&">
        <a-radio value="&">并且</a-radio>
        <a-radio value="|">或者</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <p />
    <a-row>
      <a-col>
        <a-button-group style="margin-bottom: 12px">
          <a-button type="primary" @click="addConditionConfig">添加</a-button>
          <a-button @click="deleteConditionConfig">删除</a-button>
        </a-button-group>
        <a-list bordered :data-source="stateCondition.configs">
          <a-list-item
            class="drag-btn-handler"
            :class="{ selected: item.id == selectedItem.id }"
            slot="renderItem"
            slot-scope="item"
            @click="onConditionConfigClick(item)"
          >
            {{ getItemDisplayName(item) }}
          </a-list-item>
        </a-list>
      </a-col>
    </a-row>
  </a-form-model>
</template>

<script>
import draggable from '@framework/vue/designer/draggable';
import { deepClone, generateId } from '../../designer/utils';
export default {
  mixins: [draggable],
  props: {
    stateCondition: {
      type: Object,
      default() {
        return { id: generateId(), changedState: '10', configs: [] };
      }
    }
  },
  inject: ['designer', 'filterSelectOption'],
  data() {
    if (!this.stateCondition.configs) {
      this.stateCondition.configs = [];
    }
    return {
      conditionConfig: {
        id: '',
        type: '1',
        connector: '&'
      },
      selectedItem: {},
      processNodeOptions: [],
      processNodeStateOptions: [
        { value: '10', label: '已启动' },
        { value: '20', label: '已暂停' },
        { value: '30', label: '已完成' }
      ],
      itemCodeOptions: [],
      rules: {
        conditionName: [{ required: true, message: '名称不能为空！', trigger: 'blur' }]
      }
    };
  },
  created() {
    const _this = this;
    let nodeDataList = _this.designer.processTree.getTreeDataList('node');
    let processNodeOptions = [];
    let processNodeMap = new Map();
    nodeDataList.forEach(node => {
      if (node.data && node.data.configuration) {
        let configuration = node.data.configuration;
        processNodeOptions.push({ value: configuration.code, label: configuration.name });
        processNodeMap.set(configuration.code, configuration.name);
      }
    });
    _this.processNodeOptions = processNodeOptions;
    _this.processNodeMap = processNodeMap;

    let itemDataList = _this.designer.processTree.getTreeDataList('item');
    let itemCodeOptions = [];
    let itemCodeMap = new Map();
    itemDataList.forEach(item => {
      if (item.data && item.data.configuration) {
        let configuration = item.data.configuration;
        itemCodeOptions.push({ value: configuration.itemCode, label: configuration.itemName });
        itemCodeMap.set(configuration.itemCode, configuration.itemName);
      }
    });
    _this.itemCodeOptions = itemCodeOptions;
    _this.itemCodeMap = itemCodeMap;
    // 更新过程节点、业务事项显示名称
    _this.updateItemDisplayName();
  },
  mounted() {
    this.tableDraggable(this.stateCondition.configs, this.$el.querySelector('.ant-list-items'), '.drag-btn-handler');
  },
  methods: {
    updateItemDisplayName() {
      this.stateCondition.configs.forEach(conditionConfig => {
        // 更新过程节点显示名称
        if (conditionConfig.processNodeCode) {
          conditionConfig.processNodeName = this.processNodeMap.get(conditionConfig.processNodeCode) || conditionConfig.processNodeName;
        }
        // 更新业务事项显示名称
        if (conditionConfig.processItemCode) {
          conditionConfig.processItemName = this.itemCodeMap.get(conditionConfig.processItemCode) || conditionConfig.processItemName;
        }
      });
    },
    getItemDisplayName(item) {
      let leftBracket = item.leftBracket || '';
      let processNodeName = item.processNodeName || '';
      let processNodeStateName = item.processNodeStateName || '';
      let rightBracket = item.rightBracket || '';
      let processItemName = item.processItemName || '';
      if (item.type == '1') {
        return leftBracket + processNodeName + ' ' + processNodeStateName + rightBracket;
      } else if (item.type == '2') {
        return leftBracket + processItemName + rightBracket;
      } else {
        return item.connector == '&' ? '并且' : '或者';
      }
    },
    onProcessNodeChange() {
      this.conditionConfig.processNodeName = this.processNodeMap.get(this.conditionConfig.processNodeCode);
    },
    onProcessNodeStateChange() {
      let nodeState = this.processNodeStateOptions.find(item => item.value == this.conditionConfig.processNodeState);
      this.conditionConfig.processNodeStateName = nodeState ? nodeState.label : '';
    },
    onItemCodeChange() {
      this.conditionConfig.processItemName = this.itemCodeMap.get(this.conditionConfig.processItemCode);
    },
    addConditionConfig() {
      let config = deepClone(this.conditionConfig);
      config.id = generateId();
      this.stateCondition.configs.push(config);
    },
    deleteConditionConfig() {
      if (this.selectedItem) {
        let index = this.stateCondition.configs.findIndex(item => item.id == this.selectedItem.id);
        if (index != -1) {
          this.stateCondition.configs.splice(index, 1);
          this.selectedItem = {};
        }
      } else {
        this.$message.warning('请选择要删除的记录！');
      }
    },
    onConditionConfigClick(item) {
      this.selectedItem = item;
      this.conditionConfig = item;
    },
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.stateCondition;
          }
          return null;
        })
        .catch(valid => {
          console.log('valid ', valid);
        });
    }
  }
};
</script>

<style lang="less" scoped>
.selected {
  background-color: #eee;
}
</style>
