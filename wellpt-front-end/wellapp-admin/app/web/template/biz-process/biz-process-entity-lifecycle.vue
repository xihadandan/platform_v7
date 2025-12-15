<template>
  <div>
    <a-tabs @change="key => (selectedProcessDefId = key)">
      <a-tab-pane v-for="definition in processDefinitions" :key="definition.id" :tab="definition.name">
        <p />
        <a-row>
          <a-col span="7">
            <a-timeline>
              <a-timeline-item
                v-for="node in definition.nodes"
                :key="node.id"
                @click="onNodeClick(node, definition.id)"
                :color="getSelectedColor(node.id, definition.id)"
              >
                <a>{{ node.name }}</a>
                <div v-if="getNodeStartTime(node.id, definition.id)">{{ getNodeStartTime(node.id, definition.id) }}开始</div>
              </a-timeline-item>
            </a-timeline>
          </a-col>
          <a-col span="17">
            <a-list item-layout="horizontal" :data-source="selectedItemInstanceMap[selectedProcessDefId]">
              <a-list-item slot="renderItem" slot-scope="item, index">
                <a-list-item-meta :description="getItemDescription(item)">
                  <a slot="title" @click="viewItemDetail(item)">{{ item.itemName }}</a>
                </a-list-item-meta>
              </a-list-item>
            </a-list>
          </a-col>
        </a-row>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
export default {
  inject: ['pageContext', 'dyform'],
  data() {
    const _this = this;
    let entityIdField = null;
    if (_this.pageContext.vueInstance && _this.pageContext.vueInstance.widget && _this.pageContext.vueInstance.widget.vars) {
      entityIdField = _this.pageContext.vueInstance.widget.vars.entityIdField;
    }
    return {
      selectedProcessDefId: undefined,
      entityIdField,
      processDefinitions: [],
      nodeInstanceMap: {},
      itemInstanceMap: {},
      selectedItemInstanceMap: {},
      selectedNodeIdMap: {}
    };
  },
  watch: {
    'dyform.formData': {
      deep: true,
      handler(newVal, oldVal) {
        this.loadEntityProcessDefinition();
      }
    }
  },
  created() {
    const _this = this;
    let isDesignMode = !!this._provided.designMode;
    if (isDesignMode) {
      return;
    }

    if (!_this.entityIdField) {
      _this.$message.error('当前页面没有配置业务主体的表单字段编码，无法加载生命周期数据！');
    }
    _this.loadEntityProcessDefinition();
    console.log('dyform', this.dyform);
  },
  methods: {
    getEntityId() {
      let entityIdField = this.dyform.getField(this.entityIdField);
      if (entityIdField == null) {
        return null;
      }
      return entityIdField.getValue();
    },
    loadEntityProcessDefinition() {
      const _this = this;
      let entityId = _this.getEntityId();
      if (!entityId || _this.entityId == entityId) {
        return;
      }

      _this.entityId = entityId;
      let formUuid = _this.dyform.formUuid;
      $axios.get(`/proxy/api/biz/process/entity/listProcessDefinition`, { params: { formUuid, entityId } }).then(({ data: result }) => {
        _this.processDefinitions = result.data || [];
        _this.processDefinitions.forEach((definition, index) => {
          if (index == 0) {
            _this.selectedProcessDefId = definition.id;
          }
          _this.$set(_this.selectedItemInstanceMap, definition.id, []);
          _this.$set(_this.selectedNodeIdMap, definition.id, '');
          _this.loadEntityProcessNodeInstance(definition.id, entityId);
        });
      });
    },
    loadEntityProcessNodeInstance(processDefId, entityId) {
      const _this = this;
      $axios
        .get(`/proxy/api/biz/process/entity/listProcessNodeInstanceByProcessDefId`, { params: { processDefId, entityId } })
        .then(({ data: result }) => {
          console.log(result);
          _this.nodeInstanceMap[processDefId] = result.data || [];
        });
    },
    getNodeStartTime(nodeId, processDefId) {
      let nodeInstances = this.nodeInstanceMap[processDefId] || [];
      let nodeInstance = nodeInstances.find(item => item.id == nodeId);
      return nodeInstance != null ? nodeInstance.startTime : null;
    },
    onNodeClick(node, processDefId) {
      const _this = this;
      _this.selectedNodeIdMap[processDefId] = node.id;
      let itemInstances = _this.itemInstanceMap[node.id];
      if (!itemInstances) {
        let nodeInstances = this.nodeInstanceMap[processDefId] || [];
        let nodeInstance = nodeInstances.find(item => item.id == node.id);
        if (nodeInstance == null) {
          _this.selectedItemInstanceMap[_this.selectedProcessDefId] = [];
        } else {
          _this.loadEntityProcessItemInstance(nodeInstance);
        }
      } else {
        _this.selectedItemInstanceMap[_this.selectedProcessDefId] = itemInstances;
      }
    },
    loadEntityProcessItemInstance(nodeInstance) {
      const _this = this;
      $axios
        .get(`/proxy/api/biz/process/entity/listProcessItemInstanceByProcessNodeInstUuid`, {
          params: { processNodeInstUuid: nodeInstance.uuid }
        })
        .then(({ data: result }) => {
          _this.itemInstanceMap[nodeInstance.id] = result.data || [];
          _this.selectedItemInstanceMap[_this.selectedProcessDefId] = result.data || [];
        });
    },
    getItemDescription(item) {
      let state = item.state;
      let stateName = '';
      if (state == '00') {
        stateName = '草稿';
      } else if (state == '10') {
        stateName = '办理中';
      } else if (state == '20') {
        stateName = '暂停';
      } else if (state == '30') {
        stateName = '已完成';
      } else if (state == '40') {
        stateName = '已取消';
      }
      return `开始时间${item.startTime || item.createTime}, ${item.endTime != null ? '结束时间' + item.endTime : stateName}`;
    },
    viewItemDetail(item) {
      let businessIntegrations = item.businessIntegrations;
      if (businessIntegrations) {
        businessIntegrations.forEach(item => {
          // 流程集成
          if (item.type == '1') {
            window.open(`/workflow/work/view/work?flowInstUuid=${item.bizInstUuid}`);
          }
        });
      } else {
        window.open(`/biz/process/item/instance/view?itemInstUuid=${item.uuid}`);
      }
    },
    getSelectedColor(nodeId, processDefId) {
      return this.selectedNodeIdMap[processDefId] == nodeId ? 'blue' : 'gray';
    }
  }
};
</script>

<style></style>
