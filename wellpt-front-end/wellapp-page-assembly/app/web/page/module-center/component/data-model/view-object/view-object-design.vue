<template>
  <a-layout class="data-model-view-design" style="height: 100%">
    <a-layout-sider theme="light" style="height: calc(100vh - 180px)" :width="224">
      <a-radio-group v-model="pageType" button-style="solid" class="page-type-select">
        <a-radio-button value="object">对象</a-radio-button>
        <a-radio-button value="tool">工具</a-radio-button>
      </a-radio-group>
      <div v-show="pageType == 'object'">
        <!-- 拖拽对象至设计区 -->
        <div style="margin: 12px 0">
          <a-input-search ref="inputSearch" style="width: 100%" v-model.trim="searchKeyword" allow-clear @change="onSearchChange" />
        </div>
        <PerfectScrollbar class="data-model-collpase-sider" style="height: calc(100vh - 310px)">
          <div class="spin-center" v-if="loading">
            <a-spin />
          </div>
          <a-collapse :bordered="false" v-model="collapseActiveKey" expandIconPosition="right" v-else>
            <a-collapse-panel key="table" header="存储对象">
              <ul class="object-ul">
                <li
                  v-for="(opt, i) in vTableObjOptions"
                  :id="opt.value"
                  @dragstart="onDragstart"
                  @drag.stop="onDrag"
                  @dragend="e => onDragend(e, opt, 'data-model-node', 'T')"
                  draggable="true"
                  :title="opt.label + ' ' + opt.value"
                >
                  {{ opt.label }} ( {{ opt.value }} )
                </li>
              </ul>
            </a-collapse-panel>
            <a-collapse-panel key="view" header="视图对象">
              <ul class="object-ul">
                <li
                  v-for="(opt, i) in vViewObjOptions"
                  :id="opt.value"
                  @dragstart="onDragstart"
                  @drag.stop="onDrag"
                  @dragend.stop="e => onDragend(e, opt, 'data-model-node', 'V')"
                  draggable="true"
                  :title="opt.label + ' ' + opt.value"
                >
                  <a-avatar shape="square" class="ant-avatar" :style="{ backgroundColor: 'var(--w-success-color)' }">
                    <a-icon type="codepen" />
                  </a-avatar>
                  <!-- <a-icon type="table" /> -->
                  {{ opt.label }} ( {{ opt.value }} )
                </li>
              </ul>
            </a-collapse-panel>
            <a-collapse-panel key="pt-table" header="内置对象">
              <a-collapse :bordered="false" v-model="collapseEntityActiveKey" style="margin-left: 12px">
                <a-collapse-panel v-for="(groupOption, i) in vEntityObjOptions" :key="groupOption.key" :header="groupOption.title">
                  <ul class="object-ul">
                    <li
                      v-for="(opt, i) in groupOption.options"
                      :id="opt.value"
                      @dragstart="onDragstart"
                      @drag.stop="onDrag"
                      @dragend.stop="e => onDragend(e, opt, 'data-model-node', 'E')"
                      draggable="true"
                      :title="opt.label"
                    >
                      <a-avatar shape="square" class="ant-avatar" :style="{ backgroundColor: 'var(--w-warning-color)' }">
                        <a-icon type="codepen" />
                      </a-avatar>
                      {{ opt.label }}
                    </li>
                  </ul>
                </a-collapse-panel>
              </a-collapse>
            </a-collapse-panel>
            <a-collapse-panel key="other-module-table-view" header="其他模块">
              <a-collapse :bordered="false" v-model="collapseModuleActiveKey" style="margin-left: 12px">
                <a-collapse-panel v-for="(mod, i) in vOtherModuleTableViewObjOptions" :key="mod.id" :header="mod.title">
                  <ul class="object-ul">
                    <template v-for="(t, i) in ['TABLE', 'VIEW']">
                      <li
                        v-for="(opt, i) in mod[t]"
                        :id="opt.value"
                        @dragstart="onDragstart"
                        @drag.stop="onDrag"
                        @dragend.stop="e => onDragend(e, opt, 'data-model-node', t == 'VIEW' ? 'V' : 'T')"
                        draggable="true"
                        :title="opt.label + ' ' + opt.value"
                      >
                        <a-avatar
                          shape="square"
                          class="ant-avatar"
                          :style="{ backgroundColor: t == 'VIEW' ? 'var(--w-success-color)' : 'var(--w-warning-color)' }"
                        >
                          <Icon :type="t == 'VIEW' ? 'codepen' : 'database'" />
                        </a-avatar>
                        {{ opt.label }} ( {{ opt.value }} )
                      </li>
                    </template>
                  </ul>
                </a-collapse-panel>
              </a-collapse>
            </a-collapse-panel>
          </a-collapse>
        </PerfectScrollbar>
      </div>
      <div v-show="pageType == 'tool'">
        <ul class="object-ul tool">
          <li
            @dragstart="onDragstart"
            @drag.stop="onDrag"
            @dragend.stop="e => onDragend(e, { label: '集合', value: 'collect' }, 'data-model-collect', 'V')"
            draggable="true"
          >
            <a-avatar shape="square" class="ant-avatar" :style="{ backgroundColor: 'var(--w-success-color)' }">
              <a-icon type="apartment" />
            </a-avatar>
            集合
          </li>
          <li
            @dragstart="onDragstart"
            @drag.stop="onDrag"
            @dragend.stop="e => onDragend(e, { label: '数据合并', value: 'union' }, 'data-model-collect', 'U')"
            draggable="true"
          >
            <a-avatar shape="square" class="ant-avatar" :style="{ backgroundColor: 'var(--w-danger-color)' }">
              <a-icon type="block" />
            </a-avatar>
            数据合并
          </li>
        </ul>
      </div>
    </a-layout-sider>
    <a-layout>
      <!-- <a-layout-header style="background: #fff; padding: 0; height: 35px; line-height: 35px">
        <div class="tools-bar"> -->
      <!-- <a-button size="small" icon="undo" type="link" @click="onUndo">撤销</a-button>
          <a-button size="small" icon="redo" type="link" @click="onRedo">恢复</a-button>
          <a-button size="small" icon="delete" type="link" @click="onClearGraph">清空</a-button> -->
      <!-- <a-button size="small" icon="code" type="link" @click="onPreviewSQL">SQL 预览</a-button>
        </div>
      </a-layout-header> -->
      <a-layout-content ref="layoutContent" id="testlayout" style="position: relative">
        <div style="position: absolute; right: 20px; top: 20px; z-index: 1">
          <a-button icon="code" @click="onPreviewSQL" title="SQL 预览"></a-button>
        </div>

        <div class="x6ContainerPanel">
          <div id="x6container" @dragover="onDragover"></div>
        </div>

        <a-drawer
          class="data-model-node-set-drawer pt-drawer"
          :title="x6Designer.selectedNode.data.title"
          placement="right"
          :closable="false"
          :width="drawerWidth"
          :visible="x6Designer.showDrawer"
          :destroyOnClose="true"
          :get-container="false"
          :header-style="{ position: 'fixed', zIndex: 5, width: drawerWidth + 'px' }"
          :body-style="{ padding: '56px 20px 70px' }"
          :mask="true"
          :maskClosable="true"
          @close="onCloseDrawer"
        >
          <template>
            <component
              v-if="x6Designer.selectedNode.component"
              :is="x6Designer.selectedNode.component"
              :nodeData="x6Designer.selectedNode.data"
              :graph="graph"
              :key="x6Designer.selectedNode.id"
              :nid="x6Designer.selectedNode.id"
              ref="nodeSet"
            />
            <div class="footer">
              <a-button type="primary" @click="onSave">保存</a-button>
              <a-button @click="onCancelDrawer">取消</a-button>
            </div>
          </template>
        </a-drawer>
        <a-modal v-model="previewSQL" title="SQL" :footer="null" :width="700" class="preview-sql-modal">
          <div class="panel">
            <a-button title="复制SQL" type="link" icon="copy" size="small" class="copy-btn" @click="onCopySQL" />
            <PerfectScrollbar class="sqlStringPanel">
              <pre>{{ SQLstring }}</pre>
            </PerfectScrollbar>
          </div>
        </a-modal>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<style lang="less">
#x6container {
  width: 100%;
  height: 100%;
}
#x6NodeDrawerContainer {
  position: absolute;
}
.data-model-view-design {
  .page-type-select {
    display: flex;
    --w-radio-button-solid-border-radius: 4px;

    > label {
      flex: 1;
      text-align: center;
    }
  }
  .ant-layout-sider-children {
    padding: 20px 12px;
  }
  .data-model-collpase-sider {
    padding-right: 12px;
    margin-right: -12px;
    .ant-collapse-borderless {
      > .ant-collapse-item {
        border-bottom-color: transparent;
        margin-bottom: 0px;
      }
      .ant-collapse-header {
        line-height: 32px;
        padding: 4px 0;
        > i {
          right: 0px;
          left: unset;
          color: var(--w-text-color-light);
        }
      }
      .ant-collapse-content-box {
        padding: 0;
      }
      > .ant-collapse-item:last-child {
        border: unset;
      }
    }
  }
  .x6ContainerPanel {
    width: e('calc(100%)');
    height: 100%;
  }
  .tools-bar {
    text-align: right;
  }
  position: relative;
  .ant-collapse {
    background: unset;
  }
  .ant-layout-content {
    padding: 0px;
  }
  .object-ul {
    padding: 0px;
    margin-bottom: 0px;

    li {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      width: 100%;
      list-style: none;
      margin: 4px 0 4px;
      padding: 8px 10px;
      background-color: #fff;
      cursor: move;
      display: inline-block;
      border: 1px solid var(--w-border-color-light);
      border-radius: 4px;

      .ant-avatar {
        border-radius: 4px;
        height: 18px;
        width: 18px;
        line-height: 18px;

        .ant-avatar-string {
          transform: scale(1) translateX(-50%) !important;
        }
        i {
          font-size: 14px;
        }
      }
    }
    li:hover {
      border: 1px solid var(--w-primary-color);
      color: var(--w-primary-color);
    }
  }

  .object-ul.tool {
    padding-top: 8px;
  }

  .data-model-node-set-drawer {
    .footer {
      position: absolute;
      bottom: 0px;
      width: 100%;
      border-top: 1px solid rgb(232, 232, 232);
      padding: 10px 16px;
      text-align: right;
      left: 0px;
      z-index: 10;
      background: rgb(255, 255, 255);
      border-radius: 0px 0px 4px 4px;
    }
  }
}
.preview-sql-modal {
  .copy-btn {
    position: absolute;
    right: 25px;
    z-index: 2;
    display: none;
  }
  .panel:hover {
    .copy-btn {
      display: block;
    }
  }
  .sqlStringPanel {
    background: #f8f8f8;
    padding: 10px;
    border-radius: 5px;
    height: 450px;
    overflow-y: auto;
  }
}
</style>

<script type="text/babel">
import { copyToClipboard, deepClone } from '@framework/vue/utils/util';

import { Graph, Shape, Markup } from '@antv/x6';
import { register } from '@antv/x6-vue-shape';
import DataModelCollect from './design-component/data-model-collect.vue';
import DataModelNode from './design-component/data-model-node.vue';
import { ports } from './design-component/constant';
import DataModelNodeSet from './design-component/data-model-node-set.vue';
import DataModelLineSet from './design-component/data-model-line-set.vue';
import DataModelCollectSet from './design-component/data-model-collect-set.vue';
import sqlFormatter from '@sqltools/formatter';
import { debounce, groupBy, orderBy } from 'lodash';
register({
  shape: 'data-model-node',
  width: 100,
  height: 100,
  component: DataModelNode
});
register({
  shape: 'data-model-collect',
  width: 100,
  height: 100,
  component: DataModelCollect
});

export default {
  name: 'DataModelViewObjectDesign',
  mixins: [],
  inject: ['x6Designer', 'currentModule'],
  data() {
    return {
      seq: 1,
      previewSQL: false,
      SQLstring: undefined,
      drawerWidth: 500,
      tableObjOptions: [],
      viewObjOptions: [],
      entityObjOptions: [],
      dragoverX: 0,
      dragoverY: 0,
      dragoverNodeId: undefined,
      dragover: false,
      startX: 0,
      startY: 0,
      embedPadding: 10,
      ctrlPressed: false,
      ports,
      graph: undefined,
      modelId: undefined,
      searchKeyword: undefined,
      enableSearch: false,
      collapseActiveKey: ['table'],
      collapseModuleActiveKey: [],
      collapseEntityActiveKey: [],
      history: {
        step: 0,
        json: []
      },
      moduleDataModel: {
        [this.currentModule.id]: { title: this.currentModule.name, id: this.currentModule.id, TABLE: [], VIEW: [], RELATION: [] }
      },
      loading: true,
      loadingOtherModuleDm: true,
      pageType: 'object'
    };
  },
  provide() {},
  beforeCreate() {},
  components: { DataModelNodeSet, DataModelLineSet, DataModelCollectSet },
  computed: {
    vTableObjOptions() {
      if (this.loading) {
        return [];
      }
      let tableObjOptions = this.moduleDataModel[this.currentModule.id].TABLE;
      if (this.searchKeyword == undefined) {
        return tableObjOptions;
      }
      let opt = [];
      for (let i = 0, len = tableObjOptions.length; i < len; i++) {
        if (
          tableObjOptions[i].label.indexOf(this.searchKeyword) != -1 ||
          tableObjOptions[i].value.toLowerCase().indexOf(this.searchKeyword.toLowerCase()) != -1
        ) {
          opt.push(tableObjOptions[i]);
        }
      }
      return opt;
    },

    vOtherModuleTableViewObjOptions() {
      if (this.loadingOtherModuleDm) {
        return [];
      }
      let opt = [];
      for (let id in this.moduleDataModel) {
        if (id != this.currentModule.id) {
          if (this.searchKeyword == undefined) {
            opt.push(this.moduleDataModel[id]);
          } else {
            let tableObjOptions = this.moduleDataModel[id].TABLE,
              viewObjOptions = this.moduleDataModel[id].VIEW;
            let obj = {
              title: this.moduleDataModel[id].title,
              id,
              TABLE: [],
              VIEW: []
            };
            for (let i = 0, len = tableObjOptions.length; i < len; i++) {
              if (
                tableObjOptions[i].label.indexOf(this.searchKeyword) != -1 ||
                tableObjOptions[i].value.toLowerCase().indexOf(this.searchKeyword.toLowerCase()) != -1
              ) {
                obj.TABLE.push(tableObjOptions[i]);
              }
            }
            for (let i = 0, len = viewObjOptions.length; i < len; i++) {
              if (
                viewObjOptions[i].label.indexOf(this.searchKeyword) != -1 ||
                viewObjOptions[i].value.toLowerCase().indexOf(this.searchKeyword.toLowerCase()) != -1
              ) {
                obj.VIEW.push(viewObjOptions[i]);
              }
            }
            if (obj.TABLE.length > 0 || obj.VIEW.length > 0) {
              opt.push(obj);
            }
          }
        }
      }
      return opt;
    },
    vViewObjOptions() {
      if (this.loading) {
        return [];
      }
      let viewObjOptions = this.moduleDataModel[this.currentModule.id].VIEW;
      if (this.searchKeyword == undefined) {
        return viewObjOptions;
      }
      let opt = [];
      for (let i = 0, len = viewObjOptions.length; i < len; i++) {
        if (
          viewObjOptions[i].label.indexOf(this.searchKeyword) != -1 ||
          viewObjOptions[i].value.toLowerCase().indexOf(this.searchKeyword.toLowerCase()) != -1
        ) {
          opt.push(viewObjOptions[i]);
        }
      }
      return opt;
    },

    vEntityObjOptions() {
      if (this.searchKeyword == undefined) {
        return this.entityObjOptions;
      }
      let opt = [];
      for (let i = 0, len = this.entityObjOptions.length; i < len; i++) {
        let item = this.entityObjOptions[i];
        let itemOpts = [];
        for (let j = 0, jlen = item.options.length; j < jlen; j++) {
          let opt = item.options[j];
          if (opt.label.indexOf(this.searchKeyword) != -1 || opt.value.toLowerCase().indexOf(this.searchKeyword.toLowerCase()) != -1) {
            itemOpts.push(opt);
          }
        }
        if (itemOpts.length) {
          opt.push({
            title: item.title,
            key: item.key,
            options: itemOpts
          });
        }
      }
      return opt;
    },
    viewNodeMap() {
      let m = {};
      for (let v of this.viewObjOptions) {
        m[v.data.id] = v.data;
      }
      return m;
    }
  },
  created() {},
  methods: {
    openSearch() {
      this.enableSearch = true;
      this.$nextTick(() => {
        this.$refs.inputSearch.focus();
      });
    },
    onSearchChange(e) {
      if (e.target.value != undefined && e.target.value != '') {
        this.collapseActiveKey.splice(0, this.collapseActiveKey.length);
        this.collapseModuleActiveKey.splice(0, this.collapseModuleActiveKey.length);
        this.collapseEntityActiveKey.splice(0, this.collapseEntityActiveKey.length);

        if (this.vTableObjOptions.length > 0) {
          this.collapseActiveKey.push('table');
        }
        if (this.vViewObjOptions.length > 0) {
          this.collapseActiveKey.push('view');
        }
        if (this.vEntityObjOptions.length > 0) {
          this.collapseActiveKey.push('pt-table');
          for (let i = 0, len = this.vEntityObjOptions.length; i < len; i++) {
            this.collapseEntityActiveKey.push(this.vEntityObjOptions[i].key);
          }
        }
        if (this.vOtherModuleTableViewObjOptions.length > 0) {
          this.collapseActiveKey.push('other-module-table-view');
          for (let i = 0, len = this.vOtherModuleTableViewObjOptions.length; i < len; i++) {
            this.collapseModuleActiveKey.push(this.vOtherModuleTableViewObjOptions[i].id);
          }
        }
      } else {
        this.collapseActiveKey = ['table'];
      }
    },
    pushHistory(json) {
      this.history.step++;
      this.history.json.push(json);
    },
    onCopySQL(e) {
      let _this = this;
      copyToClipboard(this.SQLstring, e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    },
    onUndo() {},
    onRedo() {},
    onClearGraph() {
      this.graph.clearCells();
    },
    onPreviewSQL() {
      this.previewSQL = true;
    },
    onSave() {
      let promise = this.$refs.nodeSet.onSave();
      if (promise != undefined && promise instanceof Promise) {
        promise.then(() => {
          this.onCloseDrawer();
        });
      } else {
        this.onCloseDrawer();
      }
    },
    onCancelDrawer() {
      let nodeSet = this.$refs.nodeSet;
      if (nodeSet.dataChanged != undefined) {
        let changed = nodeSet.dataChanged(),
          _this = this;
        if (changed) {
          this.$confirm({
            title: '提示',
            content: `已${nodeSet.form.timestamp == undefined ? '填写' : '修改'}部分信息, 是否废弃?`,
            okText: '确定',
            onOk() {
              _this.onCloseDrawer();
            },
            onCancel() {}
          });
        } else {
          this.onCloseDrawer();
        }
      } else {
        this.onCloseDrawer();
      }
    },
    onCloseDrawer() {
      this.x6Designer.showDrawer = false;
    },
    onDragstart(e) {
      this.startX = e.offsetX;
      this.startY = e.offsetY;
    },
    onDrag(e) {},
    onDragend(e, option, nodeType, aliasPrefix) {
      let node = {
        shape: nodeType,
        x: this.dragoverX - this.startX,
        y: this.dragoverY - this.startY,
        ports: { ...this.ports }
      };
      console.log(node);
      if (node.x < 0 || node.y < 0) {
        return;
      }

      let _parent = null,
        _node = null;
      if (this.dragoverNodeId != undefined) {
        _parent = this.graph.getCellById(this.dragoverNodeId);
        let parentBox = _parent.getBBox();
        node.x += parentBox.x;
        node.y += parentBox.y;
      }

      // 判断是否在集合节点内
      // let nodes = this.graph.getNodes();
      // let parent = null;
      // if (nodes) {
      //   for (let n of nodes) {
      //     if (n.shape == 'data-model-collect') {
      //       let collectBbox = n.getBBox();

      //       if (
      //         node.x >= collectBbox.x &&
      //         node.x <= collectBbox.x + collectBbox.width &&
      //         node.y >= collectBbox.y &&
      //         node.y <= collectBbox.y + collectBbox.height
      //       ) {
      //         parent = n;
      //         break;
      //       }
      //     }
      //   }
      // }
      // console.log('在里面', parent);

      if (nodeType == 'data-model-node') {
        // let parts = option.value.split('_'),
        //   chars = [];
        // for (let p of parts) {
        //   chars.push(p.charAt(0));
        // }
        node.zIndex = 100;
        node.data = {
          seq: this.seq,
          alias: aliasPrefix + this.seq++,
          timestamp: undefined, // 时间戳用于使数据发生变更
          conditions: [],
          title: option.label,
          id: option.value,
          moduleName: option.moduleName,
          tableName: aliasPrefix === 'E' ? option.value : 'UF_' + option.value.toUpperCase(),
          category: aliasPrefix === 'E' ? 'entity' : 'table',
          columns: []
        };

        if (aliasPrefix == 'V') {
          node.data.type = 'VIEW';
          // node.data.sql =
        }
        _node = this.graph.addNode(node);

        // window.NODE_PARENT.addChild(node); //FIXME: 添加到集合内的移动
      } else if (nodeType == 'data-model-collect') {
        node.width = 500;
        node.height = 500;
        node.zIndex = 1;
        node.data = {
          seq: this.seq,
          title: '未命名' + option.label + this.seq,
          alias: aliasPrefix + this.seq++,
          id: option.value,
          timestamp: undefined,
          conditions: [],
          columns: []
        };
        if (option.value == 'union') {
          node.data.unionType = 'UNION';
        }

        _node = this.graph.addNode(node);
      }

      if (_parent && _node) {
        _parent.embed(_node);
      }
    },
    onDragover(e) {
      this.dragoverX = e.offsetX - this.graph.options.x;
      this.dragoverY = e.offsetY - this.graph.options.y;
      // console.log('offsetX,offsetY', e.offsetX, e.offsetY);
      // console.log(e);
      this.dragoverNodeId = undefined;
      if (e.target.classList.contains('data-model-collect-node')) {
        // 放置到集合内
        this.dragoverNodeId = e.target.getAttribute('id');
      }

      e.preventDefault();
    },
    fetchDataModelObjectOptions(type, module) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/dm/getDataModelsByType`, {
            module: typeof module == 'string' ? [module] : module,
            type: typeof type == 'string' ? [type] : type
          })
          .then(({ data }) => {
            if (data.data) {
              resolve(data.data);
            }
          });
      });
    },
    fetchEntityObjectOptions() {
      let _this = this;
      $tempStorage.getCache(
        'DmExposedTableEntities',
        () => {
          return new Promise((resolve, reject) => {
            $axios.get(`/proxy/api/dm/getDmExposedTableEntities`, { params: {} }).then(({ data }) => {
              if (data.data) {
                resolve(data.data);
              }
            });
          });
        },

        list => {
          _this.entityObjOptions.length = 0;
          let options = [];
          for (let i = 0, len = list.length; i < len; i++) {
            let label = list[i].comment ? list[i].comment + ` ( ${list[i].tableName} )` : list[i].tableName;
            options.push({
              label: label,
              value: list[i].tableName,
              data: {},
              type: 'TABLE'
            });
            // _this.entityObjOptions.push({
            //   label: label,
            //   value: list[i].tableName,
            //   data: {},
            //   type: 'TABLE'
            // });
          }
          options = groupBy(orderBy(options, ['value'], ['asc']), item => {
            if (item.value.startsWith('APP_')) {
              return '应用对象';
            } else if (item.value.startsWith('WF_')) {
              return '流程对象';
            } else if (item.value.startsWith('REPO_')) {
              return '文件对象';
            } else if (item.value.startsWith('AUDIT_')) {
              return '角色权限';
            } else if (item.value.startsWith('ORG_') || item.value.startsWith('BIZ_ORG_')) {
              return '组织';
            } else if (item.value.startsWith('USER_')) {
              return '用户';
            } else if (item.value.startsWith('BIZ_')) {
              return '业务流程对象';
            } else {
              return '其他';
            }
          });

          ['应用对象', '流程对象', '文件对象', '业务流程对象', '角色权限', '组织', '用户', '其他'].forEach((title, i) => {
            _this.entityObjOptions.push({
              key: 'entity_category_' + (i + 1),
              title,
              options: options[title] || []
            });
          });
        }
      );
    },
    nodeClick(e, x, y, node, view) {},
    edgeClick(e, x, y, edge, view) {
      this.x6Designer.select({
        id: edge.id,
        component: 'DataModelLineSet',

        data: { ...edge.getData(), title: '连线', fromId: edge.source.cell, toId: edge.target.cell }
      });
      this.x6Designer.showDrawer = true;
    },
    cellMouseenter(cell) {},
    cellMouseleave(cell) {},

    nodeEmbedding(e) {
      this.ctrlPressed = e.metaKey || e.ctrlKey;
    },
    nodeEmbedded() {
      this.ctrlPressed = false;
      console.log(arguments);
    },
    nodeChangeSize(node, options) {
      if (options.skipParentHandler) {
        return;
      }

      const children = node.getChildren();
      if (children && children.length) {
        node.prop('originSize', node.getSize());
      }
    },

    nodeChangePosition(node, options) {
      let _this = this;
      const parent = node.getParent();
      if (options.skipParentHandler || this.ctrlPressed) {
        return;
      }
      const children = node.getChildren();
      if (children && children.length) {
        node.prop('originPosition', node.getPosition());
      }

      if (parent && parent.isNode()) {
        let originSize = parent.prop('originSize');
        if (originSize == null) {
          originSize = parent.getSize();
          parent.prop('originSize', originSize);
        }

        let originPosition = parent.prop('originPosition');
        if (originPosition == null) {
          originPosition = parent.getPosition();
          parent.prop('originPosition', originPosition);
        }

        let x = originPosition.x;
        let y = originPosition.y;
        let cornerX = originPosition.x + originSize.width;
        let cornerY = originPosition.y + originSize.height;
        let hasChange = false;

        const children = parent.getChildren();
        if (children) {
          children.forEach(child => {
            const bbox = child.getBBox().inflate(_this.embedPadding);
            const corner = bbox.getCorner();

            if (bbox.x < x) {
              x = bbox.x;
              hasChange = true;
            }

            if (bbox.y < y) {
              y = bbox.y;
              hasChange = true;
            }

            if (corner.x > cornerX) {
              cornerX = corner.x;
              hasChange = true;
            }

            if (corner.y > cornerY) {
              cornerY = corner.y;
              hasChange = true;
            }
          });
        }

        if (hasChange) {
          parent.prop(
            {
              position: { x, y },
              size: { width: cornerX - x, height: cornerY - y }
            },
            { skipParentHandler: true }
          );
        }

        if (parent.parent != undefined) {
          _this.nodeChangePosition(parent, options);
        }
      }
    },

    buildGraph() {
      let _this = this;
      const graph = new Graph({
        container: document.getElementById('x6container'),
        height: '100%',
        width: '100%',
        autoResize: false,
        panning: true,
        embedding: {
          // 支持内嵌
          enabled: true,

          findParent({ node }) {
            const bbox = node.getBBox();
            return this.getNodes().filter(node => {
              const targetBBox = node.getBBox();
              return bbox.isIntersectWithRect(targetBBox) && node.shape === 'data-model-collect';
            });
          }
        },
        highlighting: {
          embedding: {
            // 内嵌高亮
            name: 'stroke',
            args: {
              padding: -1,
              attrs: {
                stroke: '#73d13d'
              }
            }
          },
          magnetAdsorbed: {
            name: 'stroke',
            args: {
              attrs: {
                fill: '#5F95FF',
                stroke: '#5F95FF'
              }
            }
          }
        },
        connecting: {
          // 连线
          router: 'manhattan',
          connector: {
            name: 'rounded',
            args: {
              radius: 8
            }
          },
          anchor: 'center',
          connectionPoint: 'anchor',
          allowBlank: false,
          snap: {
            radius: 20
          },
          createEdge() {
            return new Shape.Edge({
              defaultLabel: {
                markup: Markup.getForeignObjectMarkup(),
                attrs: {
                  fo: {
                    width: 120,
                    height: 30,
                    x: 60,
                    y: -15
                  }
                }
              },

              data: {
                lineType: 'CROSS JOIN'
              },
              attrs: {
                line: {
                  stroke: '#A2B1C3',
                  strokeWidth: 2,
                  sourceMarker: {
                    name: 'block',
                    width: 12,
                    height: 8
                  },
                  targetMarker: {
                    name: 'block',
                    width: 12,
                    height: 8
                  }
                }
              }

              // zIndex: 100
            });
          },
          validateConnection({ targetMagnet, sourceCell, targetCell, type }) {
            if (sourceCell.id != targetCell.id) {
              let targetOutgoings = targetCell._model.outgoings,
                targetIncomings = targetCell._model.incomings;

              let _ints = targetIncomings[sourceCell.id];
              let _outs = targetOutgoings[targetCell.id];
              if (_outs != undefined && _ints != undefined) {
                for (let i = 0, len = _outs.length; i < len; i++) {
                  if (_ints.includes(_outs[i])) {
                    _this.triggerMessageInfo('两个对象之间无法重复连续');
                    return false;
                  }
                }
              }
              _ints = targetIncomings[targetCell.id];
              _outs = targetOutgoings[sourceCell.id];
              if (_outs != undefined && _ints != undefined) {
                for (let i = 0, len = _outs.length; i < len; i++) {
                  if (_ints.includes(_outs[i])) {
                    _this.triggerMessageInfo('两个对象之间无法重复连续');
                    return false;
                  }
                }
              }
            }
            return !!targetMagnet;
          }
        },
        // onEdgeLabelRendered: args => {
        //   const { selectors } = args;
        //   console.log('label rendered ', arguments);
        //   const content = selectors.foContent;
        //   content.innerHTML = '<h2>hello</h2>';
        //   // if (content) {
        //   //   ReactDOM.render(<Label />, content)
        //   // }
        // },
        mousewheel: {
          enabled: true,
          zoomAtMousePosition: true,
          modifiers: 'ctrl',
          minScale: 0.5,
          maxScale: 3
        },
        background: {
          color: '#FAFAFA'
        },
        grid: {
          // 网格
          visible: true,
          type: 'dot',
          size: 10,
          args: [
            {
              color: '#E6E6E6', // 主网格线颜色
              thickness: 2 // 主网格线宽度
            }
            // {
            //   color: '#ddd', // 次网格线颜色
            //   thickness: 1, // 次网格线宽度
            //   factor: 4 // 主次网格线间隔
            // }
          ]
        }
      });

      graph.getDesigner = function () {
        return _this.x6Designer;
      };

      _this.x6Designer.graph = graph;

      graph.on('node:click', ({ e, x, y, node, view }) => {
        _this.nodeClick(e, x, y, node, view);
      });
      graph.on('edge:click', ({ e, x, y, edge, view }) => {
        _this.edgeClick(e, x, y, edge, view);
      });

      graph.on('cell:mouseenter', ({ cell }) => {
        _this.cellMouseenter(cell);
        if (cell.shape == 'edge') {
          cell.setTools(['button-remove']);
        }
        // console.log(cell);
      });

      graph.on('cell:mouseleave', ({ cell }) => {
        _this.cellMouseleave(cell);
        if (cell.shape == 'edge') {
          cell.setTools([]);
        }
      });

      graph.on('node:embedding', ({ e }) => {
        _this.nodeEmbedding(e);
      });

      graph.on('node:embedded', () => {
        _this.nodeEmbedded();
      });

      graph.on('node:change:size', ({ node, options }) => {
        _this.nodeChangeSize(node, options);
      });

      graph.on('node:change:position', ({ node, options }) => {
        _this.nodeChangePosition(node, options);
      });

      graph.on('cell:change:data', ({ cell, options }) => {
        _this.graphDataChanged();
      });
      graph.on('cell:removed', ({ cell, options }) => {
        _this.graphDataChanged();
      });
      graph.on('cell:added', ({ cell, options }) => {
        console.log('cell:added', arguments);
        if (cell.shape != 'edge') {
          _this.graphDataChanged();
        }
      });
      graph.on('edge:connected', () => {
        console.log('edge:connected', arguments);
        _this.graphDataChanged();
      });

      return graph;
    },
    triggerMessageInfo: debounce(function (msg) {
      this.$message.info(msg);
    }, 300),
    init(data) {
      if (data.modelJson) {
        this.graph.fromJSON(typeof data.modelJson === 'string' ? JSON.parse(data.modelJson) : data.modelJson);
        let nodes = this.graph.getNodes();
        let maxSeq = this.seq;
        for (let n of nodes) {
          maxSeq = Math.max(n.getData().seq);
        }
        this.seq = maxSeq + 1;
        if (data.sql) {
          this.SQLstring = data.sql;
        }
      } else {
        this.graph.clearCells();
        this.seq = 1;
      }
      this.modelId = data.id;
    },

    graphDataChanged() {
      let _this = this;
      let rootNodes = this.graph.getRootNodes();
      if (rootNodes.length == 0) {
        rootNodes = this.graph.getNodes();
      }
      let root = null,
        rootChains = [];

      // 需要找到顶级节点，从顶级节点开始递归节点关系，拼凑SQL
      if (rootNodes.length > 0) {
        for (let i = 0, len = rootNodes.length; i < len; i++) {
          let r = rootNodes[i],
            rData = r.getData(),
            hasOutgoin = _this.graph.getOutgoingEdges(r) != null, // 该节点由出线
            withoutIncoming = _this.graph.getIncomingEdges(r) == null; // 没有该节点的入线
          // 父节点为空，并且不是连线
          if (r._parent == null && r.shape != 'edge') {
            if ((withoutIncoming && hasOutgoin) || (rData.unionType != undefined && withoutIncoming)) {
              root = r;
              break;
            }
            if (rootChains.includes(r.id)) {
              // 形成闭环的，直接取闭环对象作为顶级节点：如 A -> B -> C -> A ，则 A 作为顶级节点
              root = r;
              break;
            }
            rootChains.push(r.id);
          }
        }
        if (root == null && rootChains.length > 0) {
          root = _this.graph.getCellById(rootChains[0]);
        }
      }

      if (rootNodes.length == 0) {
        _this.$emit('columnChange', []);
        _this.$emit('sqlChange', { sql: '', sqlParameter: [] });
        return;
      }
      console.log('当前顶级: ', root);
      const usedNodeId = [],
        sqlParameter = {};
      let sqlObjTree = { children: [] }; // sql 对象树，提供后端生成sql
      const nodeSQL = function (node, sql, where, backColumns, parent) {
        let nodeData = node.getData();

        if (nodeData.conditionSql) {
          where.push(nodeData.conditionSql);
        }
        if (nodeData.sqlParameter) {
          Object.assign(sqlParameter, nodeData.sqlParameter);
        }
        if (usedNodeId.includes(node.id)) {
          return;
        }
        usedNodeId.push(node.id);
        let _children = node.getChildren();
        let currentSQLObj = undefined;
        if (node.shape == 'data-model-collect' && _children != undefined && _children.length) {
          // 查询视图
          sql.push('(  ');
          let viewsql = [],
            viewwhere = [];

          if (nodeData.unionType != undefined) {
            // FIXME: 合并的列要配置
            let union = [],
              unionColumns = [];
            // 合并输出的列显示
            // for (let uc of nodeData.columns) {
            //   unionColumns.push(uc.alias);
            // }
            currentSQLObj = {
              children: [],
              unionType: undefined
            };
            for (let i = 0, len = _children.length; i < len; i++) {
              let cc = _children[i],
                unionSubSelectsql = ['SELECT'],
                unionwhere = [],
                childColumns = [];
              // 每个合并子查询的列数据来源
              if (nodeData.mergeColumns == undefined) {
                continue;
              }
              let childData = cc.getData(),
                childColumnArr = childData.columns,
                childAlias = childData.alias,
                colAliasMap = {},
                selectCols = [];
              if (!childColumnArr) {
                // 非对象，是连接线等情况时
                continue;
              }
              for (let m = 0, len = childColumnArr.length; m < len; m++) {
                colAliasMap[childColumnArr[m].alias] = childColumnArr[m];
              }

              for (let j = 0, jlen = nodeData.mergeColumns.length; j < jlen; j++) {
                let mc = nodeData.mergeColumns[j];
                if (mc[cc.id] != undefined) {
                  let _col = deepClone(colAliasMap[mc[cc.id]]);
                  _col.alias = mc[cc.id + ':alias'] ? mc[cc.id] + ' AS ' + mc[cc.id + ':alias'] : mc[cc.id];
                  childColumns.push(_col.alias);
                  selectCols.push(_col);
                } else {
                  childColumns.push(' NULL AS ' + nodeData.columns[j].alias);
                  selectCols.push({
                    column: undefined,
                    alias: nodeData.columns[j].alias
                  });
                }
              }

              unionSubSelectsql.push(childColumns.join(', '));
              unionSubSelectsql.push('FROM');

              nodeSQL(cc, unionSubSelectsql, unionwhere, [], currentSQLObj);

              for (let k = 0, len = currentSQLObj.children.length; k < len; k++) {
                if (currentSQLObj.children[k].alias == childAlias) {
                  currentSQLObj.children[k].columns = deepClone(selectCols);
                  break;
                }
              }

              union.push(unionSubSelectsql.join(' '));
            }

            viewsql.push(union.join(` ${nodeData.unionType || 'UNION'} `));
            sql.push(viewsql.join(' '), ')', nodeData.alias);
            currentSQLObj.alias = nodeData.alias;
            currentSQLObj.unionType = nodeData.unionType;
            currentSQLObj.columns = [].concat(nodeData.columns);
            if (parent != undefined) {
              parent.children.push(currentSQLObj);
            }
          } else {
            // 获取字段
            let outColumn = [];
            for (let i = 0, len = nodeData.columns.length; i < len; i++) {
              let c = nodeData.columns[i];
              if (!c.hidden) {
                outColumn.push(_this.graph.getCellById(c.nid).getData().alias + '.' + c.column + ' as ' + c.alias);
              }
            }
            viewsql.push(outColumn.join(', '), 'FROM');

            if (parent != undefined) {
              currentSQLObj = {
                from: undefined,
                alias: nodeData.alias,
                columns: [].concat(nodeData.columns),
                where: [].concat(nodeData.conditions || []),
                children: []
              };
              parent.children.push(currentSQLObj);
            }
            let _rootChild = _children[0];
            for (let i = 0, len = _children.length; i < len; i++) {
              let c = _children[i];
              if (_this.graph.getOutgoingEdges(c) != null && _this.graph.getIncomingEdges(c) == null) {
                _rootChild = c;
              }
            }

            nodeSQL(_rootChild, viewsql, viewwhere, [], currentSQLObj); //FIXME: 获取子查询内部的顶级节点
            // console.log('子查询：', viewsql.join(' '));
            if (viewwhere.length) {
              viewsql.push('WHERE', viewwhere.join(' AND '));
            }
            sql.push(' SELECT ');
            sql.push(viewsql.join(' '));
            sql.push(')', nodeData.alias);
          }
        }
        if (node.shape == 'data-model-node') {
          if (nodeData.type === 'VIEW') {
            sql.push('(', nodeData.sql, ')', nodeData.alias);
            if (nodeData.sqlParameter) {
              Object.assign(sqlParameter, nodeData.sqlParameter);
            }
            if (parent !== undefined) {
              currentSQLObj = {
                from: `( ${nodeData.sql} )`,
                alias: nodeData.alias,
                columns: [],
                subViewSqlObjJson: nodeData.sqlObjJson,
                subView: true,
                where: [].concat(nodeData.conditions || []),
                children: []
              };
              // let _columns = deepClone(nodeData.columns);
              // for (let x = 0, xlen = _columns.length; x < xlen; x++) {
              //   _columns[x].location = `${nodeData.alias}.${_columns[x].alias}`;
              // }
              // currentSQLObj.columns = _columns;
              parent.children.push(currentSQLObj);
            }
          } else if (
            //!sql.includes(nodeData.tableName) &&
            !sql.includes(nodeData.alias)
          ) {
            sql.push(nodeData.tableName, nodeData.alias);
            if (parent !== undefined) {
              currentSQLObj = {
                columns: [],
                from: nodeData.tableName,
                alias: nodeData.alias,
                where: [].concat(nodeData.conditions || []),
                children: []
              };
              parent.children.push(currentSQLObj);
            }
          }
        }

        // 获取该节点的连线
        let edges = _this.graph.getOutgoingEdges(node);
        if (backColumns != undefined) {
          let filterReturnColumns = [];
          for (let c of nodeData.columns) {
            if (c.isReturn !== false) {
              filterReturnColumns.push(c);
            }
          }
          if (currentSQLObj && currentSQLObj.columns.length == 0) {
            currentSQLObj.columns.push(...deepClone(filterReturnColumns));
          }
          backColumns.push(...deepClone(filterReturnColumns)); // 向上反馈列
        }
        if (edges) {
          // 存在与该节点的连接
          for (let i = 0, len = edges.length; i < len; i++) {
            let edge = edges[i],
              edgeData = edge.getData();
            if (!usedNodeId.includes(edge.target.cell)) {
              sql.push(edgeData.lineType); // 连接方式
            }

            if (edgeData.conditionSql) {
              //连线条件
              where.push(edgeData.conditionSql);
              if (currentSQLObj != undefined) {
                currentSQLObj.where.push(...edgeData.conditions);
              }
            }
            if (edgeData.sqlParameter) {
              Object.assign(sqlParameter, edgeData.sqlParameter);
            }

            let targetNode = _this.graph.getCellById(edge.target.cell),
              targetNodeData = targetNode.getData();
            let targetSQLObj = undefined;
            if (!usedNodeId.includes(edge.target.cell)) {
              if (targetNode.shape === 'data-model-collect') {
                // 集合，形成临时视图查询
                let viewsql = [];
                nodeSQL(targetNode, viewsql, [], currentSQLObj);
                sql.push(viewsql.join(' '), edgeData.onConditionSql);
              } else {
                if (targetNodeData.type === 'VIEW') {
                  sql.push('(', targetNodeData.sql, ')', targetNodeData.alias, edgeData.onConditionSql);
                  if (targetNodeData.sqlParameter) {
                    Object.assign(sqlParameter, targetNodeData.sqlParameter);
                  }
                  targetSQLObj = {
                    from: `( ${targetNodeData.sql} )`,
                    alias: targetNodeData.alias,
                    columns: [],
                    subView: true,
                    joinOnCondition: edgeData.onConditionSql,
                    joinType: edgeData.lineType,
                    subViewSqlObjJson: targetNodeData.sqlObjJson,
                    where: [],
                    children: []
                  };

                  // let _columns = deepClone(targetNodeData.columns);
                  // for (let x = 0, xlen = _columns.length; x < xlen; x++) {
                  //   _columns[x].location = `${targetNodeData.alias}.${_columns[x].alias}`;
                  // }
                  // targetSQLObj.columns = _columns;

                  if (currentSQLObj != undefined) {
                    currentSQLObj.children.push(targetSQLObj);
                  } else if (parent != undefined) {
                    parent.children.push(targetSQLObj);
                  }
                } else if (
                  // !sql.includes(targetNodeData.tableName) &&
                  !sql.includes(targetNodeData.alias)
                ) {
                  sql.push(targetNodeData.tableName, targetNodeData.alias, edgeData.onConditionSql);
                  targetSQLObj = {
                    from: targetNodeData.tableName,
                    alias: targetNodeData.alias,
                    joinOnCondition: edgeData.onConditionSql,
                    joinType: edgeData.lineType,
                    columns: [],
                    where: [],
                    children: []
                  };
                  if (currentSQLObj != undefined) {
                    currentSQLObj.children.push(targetSQLObj);
                  } else if (parent != undefined) {
                    parent.children.push(targetSQLObj);
                  }
                }

                if (targetNodeData.conditionSql) {
                  where.push(targetNodeData.conditionSql);
                  targetSQLObj.where.push(...targetNodeData.conditions);
                }
              }
            }

            let targetNodeEdges = _this.graph.getOutgoingEdges(targetNode);
            if (targetNodeEdges) {
              // 递归获取连线的节点sql
              nodeSQL(targetNode, sql, where, backColumns, targetSQLObj);
              if (targetSQLObj && targetSQLObj.columns != undefined) {
                targetSQLObj.columns.push(...deepClone(targetNodeData.columns));
              }
            } else if (backColumns != undefined) {
              if (targetSQLObj && targetSQLObj.columns != undefined) {
                targetSQLObj.columns.push(...deepClone(targetNodeData.columns));
              }
              backColumns.push(...deepClone(targetNodeData.columns));
            }
          }
        }
      };
      if (root != null) {
        let SQL = [];
        const where = [],
          rootColumns = [],
          returnColumns = [];

        nodeSQL(root, SQL, where, rootColumns, sqlObjTree);
        let colSQL = [],
          alias = [];
        console.log('当前返回顶级列: ', rootColumns);
        for (let rc of rootColumns) {
          let location = rc.location;
          if (rc.return === true) {
            if (alias.includes(rc.alias)) {
              // 返回的列存在冲突，则另起别名
              // 字段名称冲突
              let l = location.replace('.', '_');
              location += ' AS ' + l;
              rc.alias = l;
            } else {
              alias.push(rc.alias);
            }
            colSQL.push(location);
            returnColumns.push(rc);
          }
        }
        SQL = ['SELECT', colSQL.join(','), 'FROM'].concat(SQL);

        _this.SQLstring = sqlFormatter.format(SQL.join(' ') + (where.length > 0 ? ' WHERE ' + where.join(' AND ') : ''));
        _this.sqlObj = sqlObjTree.children[0];
        console.info('%c' + _this.SQLstring, 'color:#cfcf0e;font-size: 14px;font-weight:bolder');
        console.info('sqlTreeObj: ', sqlObjTree);
        _this.$emit('columnChange', rootColumns);
        _this.$emit('sqlChange', { sql: _this.SQLstring, sqlParameter, sqlObj: _this.sqlObj });
        // _this.rootColumns = rootColumns;
      }
    },
    // getNodeColumns(node) {
    //   if (node.shape === 'data-model-node') {
    //     node.columns = node.data.columns;
    //   } else if (node.shape === 'data-model-collect') {
    //     if (node.children != undefined) {
    //       let cols = [];
    //       for (let child of node.children) {
    //         this.getNodeColumns(child);
    //         cols = cols.concat(child.columns);
    //       }
    //       node.columns = cols;
    //     }
    //   }
    // },
    fetchModules() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/module/getAllEnabled`)
          .then(({ data }) => {
            if (data.data) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },

    refreshOptions() {
      this.fetchEntityObjectOptions();

      let getDmOptions = moduleId => {
        this.fetchDataModelObjectOptions(undefined, moduleId).then(dataModels => {
          if (dataModels.length) {
            for (let i = 0, len = dataModels.length; i < len; i++) {
              let d = dataModels[i],
                map = this.moduleDataModel[d.module];
              if (moduleId == undefined && d.module == this.currentModule.id) {
                continue;
              }
              if (map && ['TABLE', 'VIEW'].includes(d.type)) {
                map[d.type].push({
                  label: d.name,
                  value: d.id,
                  data: d,
                  type: d.type,
                  moduleName: d.module != this.currentModule.id ? map.title : undefined
                });
              }
            }
          }
          if (moduleId != undefined) {
            this.loading = false;
          } else {
            this.loadingOtherModuleDm = false;
            for (let key in this.moduleDataModel) {
              if (this.moduleDataModel[key].TABLE.length == 0 && this.moduleDataModel[key].VIEW.length == 0) {
                delete this.moduleDataModel[key];
              }
            }
          }
        });
      };
      // 获取当前模块的数据模型
      getDmOptions.call(this, this.currentModule.id);

      // 获取其他模块的数据模型
      this.fetchModules().then(modules => {
        for (let i = 0, len = modules.length; i < len; i++) {
          if (modules[i].id != this.currentModule.id) {
            this.$set(this.moduleDataModel, modules[i].id, {
              title: modules[i].name,
              id: modules[i].id,
              TABLE: [],
              VIEW: [],
              RELATION: []
            });
          }
        }
        getDmOptions.call(this);
      });
    }
  },

  beforeMount() {
    this.refreshOptions();
  },
  mounted() {
    this.graph = this.buildGraph();
    window.GRAPH = this.graph;
    this.$emit('mounted', true);
  }
};
</script>
