<template>
  <div class="widget-data-manager-view">
    <template v-if="designMode && designer && designer.terminalType == 'mobile'">
      <widgetUniListView :widget="widget.configuration.WidgetTable" hiddenButtons :widgetsOfParent="widgetsOfParent"></widgetUniListView>
    </template>
    <template v-else>
      <template v-if="widget.configuration.type == 'table'">
        <WidgetTable
          ref="table"
          :widget="widget.configuration.WidgetTable"
          :parent="parent"
          :widgetsOfParent="widgetsOfParent"
          :designer="designer"
          @rowClick="onTableRowClick"
          @tbodyRendered="onTbodyRendered"
          :customRowStyle="customRowStyle"
          :customCellStyle="customCellStyle"
        >
          <template slot="designBeforeTableHeaderSlot" v-if="designMode">
            <div
              v-if="
                widget.configuration.WidgetTable.configuration != undefined &&
                widget.configuration.WidgetTable.configuration.enableBeforeTableHeaderWidget &&
                widget.configuration.WidgetTable.configuration.beforeTableHeaderWidgets != undefined &&
                widget.configuration.WidgetTable.configuration.beforeTableHeaderWidgets
              "
              style="position: sticky; z-index: 3"
              :class="
                widget.configuration.WidgetTable.configuration.enableBeforeTableHeaderWidget &&
                (widget.configuration.WidgetTable.configuration.beforeTableHeaderWidgets == undefined ||
                  widget.configuration.WidgetTable.configuration.beforeTableHeaderWidgets.length == 0)
                  ? 'zone-draggable'
                  : undefined
              "
            >
              <draggable
                :list="widget.configuration.WidgetTable.configuration.beforeTableHeaderWidgets"
                v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
                handle=".widget-drag-handler"
                :style="{ width: '100%', 'min-height': '30px', outline: '1px dotted #e8e8e8' }"
                :move="onDragMove"
                @add="e => onDragAdd(e, widget.configuration.WidgetTable.configuration.beforeTableHeaderWidgets)"
              >
                <template v-for="(wgt, index) in widget.configuration.WidgetTable.configuration.beforeTableHeaderWidgets">
                  <WDesignItem
                    :widget="wgt"
                    :key="wgt.id"
                    :index="index"
                    :widgetsOfParent="widget.configuration.WidgetTable.configuration.beforeTableHeaderWidgets"
                    :designer="designer"
                    :parent="widget.configuration.WidgetTable"
                    :ref="'item_label_' + index"
                    :dragGroup="draggableConfig.dragGroup"
                  />
                </template>
              </draggable>
            </div>
          </template>
          <template
            slot="firstColumnPrefixSlot"
            slot-scope="{ record, index }"
            v-if="
              widget.configuration.collectConfig.enable ||
              widget.configuration.attentionConfig.enable ||
              widget.configuration.topConfig.enable
            "
          >
            <div style="text-align: inherit; min-width: 51px; width: 100%">
              <template v-if="record.__$$TOP">
                <a-icon type="pushpin" theme="twoTone" :title="$t('WidgetDataManagerView.column.pushpin', '置顶中')" />
              </template>
              <template v-if="record.__$$COLLECT">
                <a-icon
                  type="star"
                  theme="filled"
                  style="color: rgb(255, 193, 7)"
                  :title="$t('WidgetDataManagerView.column.collect', '已收藏')"
                />
              </template>

              <template v-if="record.__$$ATTENT">
                <a-icon
                  type="heart"
                  theme="filled"
                  style="color: rgb(255, 85, 0)"
                  :title="$t('WidgetDataManagerView.column.focus', '已关注')"
                />
              </template>
            </div>
          </template>
          <template slot="firstColumnSuffixSlot" slot-scope="{ record, index }" v-if="widget.configuration.tagConfig.enable">
            <div v-if="record.__$$TAGS != undefined">
              <a-tag
                class="dm-label"
                v-for="(tag, i) in record.__$$TAGS"
                :key="index + '_tag_' + i"
                :color="tag.color ? tag.color : null"
                :closable="true"
                @close="closeTrTag(record, tag)"
              >
                {{ tag.title }}
              </a-tag>
            </div>
          </template>
        </WidgetTable>
      </template>
      <template v-else-if="widget.configuration.type == 'nav-table'">
        <a-layout style="background: #fff" :hasSider="true">
          <a-layout-sider theme="light">
            <PerfectScrollbar style="height: 500px">
              <a-tree
                style="width: 190px"
                :show-line="true"
                :show-icon="false"
                default-expand-all
                class="ant-tree-directory"
                :treeData="dataSource.nav"
                @select="onNavTreeSelect"
                v-if="widget.configuration.nav.type == 'treeNav'"
                ref="tree"
              ></a-tree>

              <a-menu
                ref="menu"
                v-else-if="widget.configuration.nav.type == 'menuNav'"
                mode="inline"
                style="width: 190px"
                @click="onClickMenu"
              >
                <a-menu-item v-for="(menu, i) in dataSource.nav" :key="menu.key">{{ menu.title }}</a-menu-item>
              </a-menu>
            </PerfectScrollbar>
          </a-layout-sider>
          <a-layout-content>
            <WidgetTable
              ref="table"
              :widget="widget.configuration.WidgetTable"
              :parent="parent"
              :widgetsOfParent="widgetsOfParent"
              :designer="designer"
              @rowClick="onTableRowClick"
              @tbodyRendered="onTbodyRendered"
            />
          </a-layout-content>
        </a-layout>
      </template>
    </template>
    <a-modal
      v-model="classifyModalVisbile"
      :title="$t('WidgetDataManagerView.categoryTo', '分类至')"
      @ok="handleClassifyOk"
      :getContainer="getModalContainer"
    >
      <PerfectScrollbar style="height: 300px">
        <a-tree
          style="width: 450px"
          :multiple="widget.configuration.classifyConfig.type == 'ONE_TO_MANY'"
          :show-line="true"
          :show-icon="false"
          default-expand-all
          class="ant-tree-directory"
          @select="onClassifyTreeSelect"
          :treeData="dataSource.classify"
        ></a-tree>
      </PerfectScrollbar>
    </a-modal>
    <a-modal
      v-model="tagModalVisbile"
      :title="$t('WidgetDataManagerView.selectTag', '选择标签')"
      @ok="handleTagOk"
      :getContainer="getModalContainer"
    >
      <template v-for="(tag, i) in dataSource.tag">
        <a-tag
          :color="tag.nData[widget.configuration.tagConfig.colorField] || null"
          :key="tag.key"
          @click="onClickTag(tag)"
          :style="selectedTagKeys.includes(tag.key) ? 'box-shadow: rgb(120 110 110) 5px 4px 3px;margin-bottom:7px' : 'margin-bottom:7px'"
        >
          {{ tag.title }}
        </a-tag>
      </template>
      <a-input-group compact v-if="showNewTagInput" class="color-input-group" style="display: inline-block; width: auto">
        <a-input size="small" v-model="newTag.title" style="width: 100px">
          <template slot="addonBefore" v-if="tagColorful">
            <swatches
              v-model="newTag.color"
              :swatches="colors"
              show-fallback
              fallback-input-type="color"
              fallback-ok-class="ant-btn ant-btn-primary ant-btn-sm color-select-ok-button"
              :fallback-ok-text="$t('WidgetDataManagerView.colorOkText', '确定')"
              popover-x="left"
              class="tag-color-select"
              :swatch-style="{ width: '18px', height: '18px', borderRadius: '4px', marginBottom: '6px' }"
              :trigger-style="{ width: '18px', height: '18px', borderRadius: '4px' }"
              :wrapper-style="{ paddingLeft: '0px' }"
            ></swatches>
          </template>
          <!-- <template slot="addonAfter">
            <a-icon type="check" @click="onNewTagOk" />
            <a-icon type="close" @click="showNewTagInput = false" />
          </template> -->
        </a-input>
        <a-button icon="check" size="small" @click="onNewTagOk"></a-button>
        <a-button icon="close" size="small" @click="onCancleNewTag"></a-button>
      </a-input-group>

      <a-tag v-else style="background: #fff; border-style: dashed" @click="showNewTagInput = true">
        <a-icon type="plus" />
        {{ $t('WidgetDataManagerView.newTag', '新建标签') }}
      </a-tag>
    </a-modal>

    <WidgetModal
      v-if="widget.configuration.WidgetModal"
      :widget="widget.configuration.WidgetModal"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :designer="designer"
    ></WidgetModal>
  </div>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import './css/index.less';
import 'vue-swatches/dist/vue-swatches.css';
import widgetUniListView from '../mobile/widget-uni-list-view/widget-uni-list-view.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'WidgetDataManagerView',
  mixins: [widgetMixin, draggable],
  props: {
    navs: Array
  },
  data() {
    return {
      dataUuidMark: {},
      dataUuidTag: {},
      markTypes: ['READ', 'ATTENTION', 'COLLECT', 'TOP'],
      dataSource: {
        classify: [],
        nav: this.navs || [],
        tag: []
      },
      nodeDataSource: {
        classify: {},
        nav: {},
        tag: {}
      },
      selectedTagKeys: [],
      classifyModalVisbile: false,
      tagModalVisbile: false,
      showNewTagInput: false,
      newTag: {
        color: '#0958d9',
        title: undefined
      },
      colors: [
        '#000000',
        '#cf1322', // 中国红
        '#fadb14', // 日出黄
        '#5b8c00', // 青柠绿
        '#13c2c2', //明青
        '#0958d9' // 科技蓝
      ]
    };
  },
  beforeCreate() {},
  components: { swatches: () => import('vue-swatches'), widgetUniListView },
  computed: {
    tableDataModelUuid() {
      return this.widget.configuration.WidgetTable.configuration.dataModelUuid;
    },

    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [{ id: 'refetchTable', title: '重新加载表格数据' }];
    },
    tagColorful() {
      return this.widget.configuration.tagConfig.enable && this.widget.configuration.tagConfig.colorField != undefined;
    }
  },
  created() {
    let widgetTableConfig = this.widget.configuration.WidgetTable.configuration,
      dataSourceParams = {};
    // 如果是开启了标记数据的功能，则添加展示相关标记的表格列
    if (!EASY_ENV_IS_NODE && !this.designMode) {
      if (this.widget.configuration.topConfig.enable) {
        dataSourceParams.LEFT_JOIN_TOP_MARK = true;
      }

      // 数据访问权限参数
      if (this.widget.configuration.authConfig.enable) {
        dataSourceParams.ACL = this.widget.configuration.authConfig.allowAccess;
        dataSourceParams.ACL_ROLES = this.widget.configuration.authConfig.allowAccessRoles;
        // dataSourceParams.CURRENT_USER = true;
      }

      widgetTableConfig.dataSourceParams = dataSourceParams;
    }
  },
  methods: {
    // 重取数据
    refetchTable() {
      this.$refs.table.refetch.apply(this.$refs.table, arguments);
    },
    onTbodyRendered({ rows }) {
      this.reRenderTrMarks();
      this.reRenderTrTags();
    },
    reRenderTrMarks() {
      this.fetchDataRelaMarker(this.$refs.table.rows, () => {
        this.updateTableRowsAsMarkStyle(this.$refs.table.rows);
      });
    },
    onTableRowClick({ row, index }) {},
    updateTableTags(rows) {
      for (let [index, r] of rows.entries()) {
        if (this.dataUuidTag[r.UUID] !== undefined) {
          let tags = this.dataUuidTag[r.UUID];
          let tagOptions = [];
          for (let t of tags) {
            let node = this.nodeDataSource.tag[t];
            if (node) {
              tagOptions.push({
                title: node.title,
                key: node.key,
                color: node.nData[this.widget.configuration.tagConfig.colorField]
              });
            }
          }
          this.$set(r, '__$$TAGS', tagOptions);
        } else {
          this.$set(r, '__$$TAGS', []);
        }
      }
    },
    updateTableRowsAsMarkStyle(rows) {
      for (let [index, r] of rows.entries()) {
        if (
          this.widget.configuration.readConfig.enable &&
          (this.dataUuidMark[r.UUID] == undefined || !this.dataUuidMark[r.UUID].includes('READ'))
        ) {
          // 不包含已读标记，则更新行样式
          this.$refs.table.setRowStyle(index, { 'font-weight': 'bolder' });
        }
        if (this.widget.configuration.collectConfig.enable) {
          this.$set(r, '__$$COLLECT', this.dataUuidMark[r.UUID] != undefined && this.dataUuidMark[r.UUID].includes('COLLECT'));
        }
        if (this.widget.configuration.attentionConfig.enable) {
          this.$set(r, '__$$ATTENT', this.dataUuidMark[r.UUID] != undefined && this.dataUuidMark[r.UUID].includes('ATTENTION'));
        }
        if (this.widget.configuration.topConfig.enable) {
          this.$set(r, '__$$TOP', this.dataUuidMark[r.UUID] != undefined && this.dataUuidMark[r.UUID].includes('TOP'));
        }
      }
    },
    fetchDataTagRela(rows, callback) {
      let dataUuids = [];
      for (let r of rows) {
        dataUuids.push(r.UUID);
      }
      if (dataUuids.length > 0) {
        $axios
          .post(`/proxy/api/dm/getDataRelaData/${this.tableDataModelUuid}`, {
            dataUuids,
            type: 'ONE_TO_MANY',
            relaId: this.widget.configuration.tagConfig.dataModelId
          })
          .then(({ data }) => {
            if (data.code == 0) {
              this.dataUuidTag = {};
              let results = data.data;
              if (results && results.length > 0) {
                for (let rs of results) {
                  if (this.dataUuidTag[rs.DATA_UUID] == undefined) {
                    this.dataUuidTag[rs.DATA_UUID] = [];
                  }
                  this.dataUuidTag[rs.DATA_UUID].push(rs.RELA_DATA_UUID);
                }
              }

              if (typeof callback == 'function') {
                callback.call(this);
              }
            }
          });
      }
    },
    fetchDataRelaMarker(rows, callback) {
      let _this = this;
      let dataUuids = [];
      for (let r of rows) {
        dataUuids.push(r.UUID);
      }
      let types = [];
      if (this.widget.configuration.readConfig.enable) {
        types.push('READ');
      }
      if (this.widget.configuration.attentionConfig.enable) {
        types.push('ATTENTION');
      }
      if (this.widget.configuration.collectConfig.enable) {
        types.push('COLLECT');
      }
      if (this.widget.configuration.topConfig.enable) {
        types.push('TOP');
      }
      if (types.length > 0 && dataUuids.length > 0) {
        $axios.post(`/proxy/api/dm/getDataRelaMarker/${this.tableDataModelUuid}`, { dataUuids, types }).then(({ data }) => {
          if (data.code == 0) {
            _this.dataUuidMark = {};
            let results = data.data;
            if (results && results.length > 0) {
              for (let rs of results) {
                if (_this.dataUuidMark[rs.DATA_UUID] == undefined) {
                  _this.dataUuidMark[rs.DATA_UUID] = [];
                }
                _this.dataUuidMark[rs.DATA_UUID].push(_this.markTypes[parseInt(rs.TYPE)]);
              }
            }

            if (typeof callback == 'function') {
              callback.call(_this);
            }
          }
        });
      }
    },

    updateDataRelaMarker(dataUuids, type, callback, deleted = false) {
      if (this.tableDataModelUuid != undefined) {
        $axios
          .post(`/proxy/api/dm/${deleted ? 'deleteDataRelaMarker' : 'updateDataRelaMarker'}/${this.tableDataModelUuid}`, {
            dataUuids,
            type
          })
          .then(({ data }) => {
            if (typeof callback == 'function') {
              callback.call(this);
            }
          });
      }
    },
    onClassifyTreeSelect(selectedKeys) {
      this.classifySelectedKeys = selectedKeys;
    },
    onClickMenu({ key }) {
      this.buildNavCriterions([key]);
      this.$refs.table.pagination.current = 1;
      this.refreshTable();
    },
    onNavTreeSelect(selectedKeys, { selected, selectedNodes, node, event }) {
      this.buildNavCriterions(selectedKeys);
      this.$refs.table.pagination.current = 1;
      this.refreshTable();
    },
    buildNavCriterions(keys) {
      this.navCriterions = [];
      if (keys && keys.length == 1) {
        let condition = `R.RELA_DATA_UUID = ${keys[0]}`;
        // let type = this.widget.configuration.classifyConfig.type == 'ONE_TO_ONE' ? 0 : 1;
        if (this.widget.configuration.nav.parentField != undefined && this.widget.configuration.nav.cascadeChildQuery) {
          // 关联查询子节点的数据，获取子节点
          let ids = [keys[0]];
          let getChildrenKey = _children => {
            if (_children) {
              for (let c of _children) {
                ids.push(c.key);
                if (c.children) {
                  getChildrenKey(c.children);
                }
              }
            }
          };
          getChildrenKey(this.nodeDataSource.nav[keys[0]].children);
          if (ids.length > 1) {
            condition = `R.RELA_DATA_UUID IN ( ${ids.join(' , ')} )`;
          }
        }

        this.navCriterions.push({
          sql: ` SELECT 1 FROM UF_${this.widget.configuration.WidgetTable.configuration.dataModelId}_DL R
        WHERE R.DATA_UUID = this_.UUID AND ${condition} AND R.RELA_ID='${this.widget.configuration.nav.dataModelId}'`,
          type: 'exists'
        });
      }
    },

    refreshTable() {
      let params = {
        criterions: [].concat(this.navCriterions || [])
      };
      this.$refs.table.fetch(params);
    },

    loadDataModelData(dataModelUuid, params, callback) {
      $axios.post(`/proxy/api/dm/loadData/${dataModelUuid}`, { pagingInfo: null, params }).then(({ data }) => {
        if (data.code == 0 && data.data.data.length) {
          let result = data.data.data;
          if (typeof callback == 'function') {
            callback.call(this, result);
          }
        }
      });
    },

    buildDataByDataModel(config, key, params = {}) {
      let _this = this,
        { dataModelUuid, primaryField, parentField, titleField } = config;
      primaryField = primaryField || 'UUID';
      this.loadDataModelData(dataModelUuid, params, result => {
        // 开始构建树形
        let treeData = [],
          nodeMap = {};
        for (let row of result) {
          let node = {
            key: row[primaryField],
            title: row[titleField],
            pKey: row[parentField],
            nData: row,
            children: []
          };

          nodeMap[node.key] = node;
          if (config.type !== 'treeNav' && key != 'classify') {
            treeData.push(node);
            continue;
          }
          if (parentField == undefined || node.pKey == undefined) {
            treeData.push(node);
          }
        }
        // 构建父级层次树
        if (parentField != undefined && (key == 'classify' || config.type === 'treeNav')) {
          for (let row of result) {
            if (row[parentField] != undefined && nodeMap[row[parentField]]) {
              nodeMap[row[parentField]].children.push(nodeMap[row[primaryField]]);
            }
          }
        }
        this.dataSource[key] = treeData;
        this.nodeDataSource[key] = nodeMap;
      });
    },

    markData(e, type, callback, deleted = false) {
      let dataUuids = e.meta.selectedRowKeys ? e.meta.selectedRowKeys : [e.meta.UUID],
        _this = this;
      if (dataUuids.length == 0) {
        this.$message.info(this.$t('WidgetDataManagerView.message.pleaseSelectRow', '请选择行'));
        return;
      }
      this.updateDataRelaMarker(
        dataUuids,
        type,
        () => {
          callback.call(_this, dataUuids);
        },
        deleted
      );
    },
    setAsRead(e) {
      // 已读，则添加已读数据的标记关系
      if (this.widget.configuration.readConfig.enable)
        this.markData(e, 'READ', dataUuids => {
          for (let u of dataUuids) {
            this.$refs.table.clearRowStyleByRowKey(u);
          }
        });
    },
    cancelRead(e) {
      if (this.widget.configuration.readConfig.enable)
        this.markData(
          e,
          'READ',
          dataUuids => {
            for (let u of dataUuids) {
              this.$refs.table.clearRowStyleByRowKey(u);
            }
          },
          true
        );
    },
    setAsAttent(e) {
      // 添加关注
      if (this.widget.configuration.attentionConfig.enable)
        this.markData(e, 'ATTENTION', dataUuids => {
          let rows = this.$refs.table.getRowsByKeys(dataUuids);
          for (let r of rows) {
            this.$set(r, '__$$ATTENT', true);
          }
        });
    },
    cancelAttent(e) {
      if (this.widget.configuration.attentionConfig.enable)
        this.markData(
          e,
          'ATTENTION',
          dataUuids => {
            let rows = this.$refs.table.getRowsByKeys(dataUuids);
            for (let r of rows) {
              this.$set(r, '__$$ATTENT', false);
            }
          },
          true
        );
    },
    setAsCollect(e) {
      if (this.widget.configuration.collectConfig.enable)
        this.markData(e, 'COLLECT', dataUuids => {
          let rows = this.$refs.table.getRowsByKeys(dataUuids);
          for (let r of rows) {
            this.$set(r, '__$$COLLECT', true);
          }
        });
    },
    cancelCollect(e) {
      if (this.widget.configuration.collectConfig.enable)
        this.markData(
          e,
          'COLLECT',
          dataUuids => {
            let rows = this.$refs.table.getRowsByKeys(dataUuids);
            for (let r of rows) {
              this.$set(r, '__$$COLLECT', false);
            }
          },
          true
        );
    },
    setAsTop(e) {
      if (this.widget.configuration.topConfig.enable)
        this.markData(e, 'TOP', dataUuids => {
          this.refreshTable();
        });
    },
    cancelTop(e) {
      if (this.widget.configuration.topConfig.enable)
        this.markData(
          e,
          'TOP',
          dataUuids => {
            this.refreshTable();
          },
          true
        );
    },
    setAsClassify(e) {
      if (this.widget.configuration.classifyConfig.enable) {
        this.rowKeys = e.meta.selectedRowKeys ? e.meta.selectedRowKeys : [e.meta.UUID];
        if (this.rowKeys.length == 0) {
          this.$message.info(this.$t('WidgetDataManagerView.message.pleaseSelectRow', '请选择行'));
          return;
        }
        this.classifyModalVisbile = true;
      }
    },
    cancelClassify(e) {
      if (
        this.widget.configuration.classifyConfig.enable &&
        this.widget.configuration.classifyConfig.dataModelId &&
        this.tableDataModelUuid
      ) {
        let rowKeys = e.meta.selectedRowKeys ? e.meta.selectedRowKeys : [e.meta.UUID];
        if (this.rowKeys.length == 0) {
          this.$message.info(this.$t('WidgetDataManagerView.message.pleaseSelectRow', '请选择行'));
          return;
        }
        if (rowKeys.length) {
          $axios
            .post(`/proxy/api/dm/deleteDataRelaData/${this.tableDataModelUuid}`, {
              type: this.widget.configuration.classifyConfig.type,
              dataUuids: this.rowKeys,
              relaId: this.widget.configuration.classifyConfig.dataModelId
            })
            .then(({ data }) => {
              if (data.code == 0) {
                this.refreshTable();
              } else {
                console.error(data);
                this.$message.error(this.$t('WidgetDataManagerView.message.serverError', '服务异常'));
              }
            });
        }
      }
    },
    handleClassifyOk() {
      if (this.classifySelectedKeys && this.classifySelectedKeys.length && this.rowKeys && this.rowKeys.length) {
        $axios
          .post(`/proxy/api/dm/updateDataRelaData/${this.tableDataModelUuid}`, {
            type: this.widget.configuration.classifyConfig.type,
            override: this.widget.configuration.classifyConfig.override,
            dataUuids: this.rowKeys,
            relaId: this.widget.configuration.classifyConfig.dataModelId,
            relaDataUuids: this.classifySelectedKeys
          })
          .then(({ data }) => {
            if (data.code == 0) {
              this.classifyModalVisbile = false;
              this.refreshTable();
              this.classifySelectedKeys = [];
            } else {
              console.error(data);
              this.$message.error(this.$t('WidgetDataManagerView.message.serverError', '服务异常'));
            }
          });
      }
    },
    onCloseTag(tag) {
      let i = this.selectedTagKeys.indexOf(tag.key);
      if (i != -1) {
        this.selectedTagKeys.splice(i, 1);
      }
    },
    onClickTag(tag) {
      let i = this.selectedTagKeys.indexOf(tag.key);
      if (i != -1) {
        this.selectedTagKeys.splice(i, 1);
      } else {
        this.selectedTagKeys.push(tag.key);
      }
    },
    setAsTag(e) {
      if (this.widget.configuration.tagConfig.enable) {
        this.rowKeys = e.meta.selectedRowKeys ? e.meta.selectedRowKeys : [e.meta.UUID];
        if (this.rowKeys && this.rowKeys.length == 0) {
          this.$message.info(this.$t('WidgetDataManagerView.message.pleaseSelectRow', '请选择行'));
          return;
        }
        this.tagModalVisbile = true;
      }
    },
    handleTagOk() {
      if (this.selectedTagKeys && this.selectedTagKeys.length == 0) {
        this.$message.info(this.$t('WidgetDataManagerView.message.pleaseSelectTag', '请选择标签'));
        return;
      }
      if (this.selectedTagKeys && this.selectedTagKeys.length && this.rowKeys && this.rowKeys.length) {
        $axios
          .post(`/proxy/api/dm/updateDataRelaData/${this.tableDataModelUuid}`, {
            type: 'ONE_TO_MANY',
            override: false,
            dataUuids: this.rowKeys,
            relaId: this.widget.configuration.tagConfig.dataModelId,
            relaDataUuids: this.selectedTagKeys
          })
          .then(({ data }) => {
            if (data.code == 0) {
              this.tagModalVisbile = false;
              // this.refreshTable();
              this.selectedTagKeys = [];
              this.reRenderTrTags();
            } else {
              console.error(data);
              this.$message.error(this.$t('WidgetDataManagerView.message.serverError', '服务异常'));
            }
          });
      }
    },
    reRenderTrTags() {
      if (this.widget.configuration.tagConfig.enable && this.widget.configuration.tagConfig.dataModelUuid) {
        this.fetchDataTagRela(this.$refs.table.rows, () => {
          this.updateTableTags(this.$refs.table.rows);
        });
      }
    },
    deleteDataRelaData(dataUuids, relaId, type, relaDataUuids, callback) {
      $axios
        .post(`/proxy/api/dm/deleteDataRelaData/${this.tableDataModelUuid}`, {
          type: type,
          dataUuids: dataUuids,
          relaId: relaId,
          relaDataUuids
        })
        .then(({ data }) => {
          if (data.code == 0) {
            callback.call(this);
          } else {
            console.error(data);
            this.$message.error(this.$t('WidgetDataManagerView.message.serverError', '服务异常'));
          }
        });
    },
    cancelTag(e) {
      if (this.widget.configuration.tagConfig.enable && this.widget.configuration.tagConfig.dataModelId && this.tableDataModelUuid) {
        let rowKeys = e.meta.selectedRowKeys ? e.meta.selectedRowKeys : [e.meta.UUID];
        if (rowKeys.length) {
          this.deleteDataRelaData(this.rowKeys, this.widget.configuration.tagConfig.dataModelId, 'ONE_TO_MANY', undefined, () => {
            this.refreshTable();
            this.reRenderTrTags();
          });
        }
      }
    },
    getModalContainer() {
      return this.$el;
    },
    onNewTagOk() {
      let { titleField, colorField, dataModelId } = this.widget.configuration.tagConfig;
      let formData = {
        table: `UF_${dataModelId}`,
        props: [
          {
            code: titleField,
            value: this.newTag.title
          }
        ]
      };
      if (this.tagColorful) {
        formData.props.push({ code: colorField, value: this.newTag.color });
      }

      $axios.post(`/proxy/api/dm/saveOrUpdateModelData`, formData).then(({ data }) => {
        if (data.code === 0) {
          // 提交成功
          this.$message.success(this.$t('WidgetDataManagerView.message.saveTagSuccess', '标签保存成功'));
          let _tag = { key: data.data, title: this.newTag.title, nData: {} };
          if (this.tagColorful) {
            _tag.nData[colorField] = this.newTag.color;
          }
          this.showNewTagInput = false;
          this.dataSource.tag.push(_tag);
          this.nodeDataSource.tag[_tag.key] = _tag;
          this.newTag.title = undefined;
          this.newTag.color = '#0958d9';
        }
      });
    },
    onCancleNewTag() {
      this.showNewTagInput = false;
      this.newTag.title = undefined;
      this.newTag.color = '#0958d9';
    },
    createData() {},
    editData() {},
    deleteData() {},
    customRowStyle(style, record, index, table) {},
    customCellStyle(style, col, record, index) {
      style.position = 'relative';
    },
    closeTrTag(row, tag) {
      this.deleteDataRelaData([row.UUID], this.widget.configuration.tagConfig.dataModelId, 'ONE_TO_MANY', [tag.key], () => {});
    }
  },

  beforeUpdate() {},
  beforeMount() {
    if (this.widget.configuration.type == 'nav-table' && this.widget.configuration.nav.dataModelUuid) {
      this.buildDataByDataModel(this.widget.configuration.nav, 'nav');
    }
    if (this.widget.configuration.classifyConfig.enable && this.widget.configuration.classifyConfig.dataModelUuid) {
      this.buildDataByDataModel(this.widget.configuration.classifyConfig, 'classify');
    }
    if (this.widget.configuration.tagConfig.enable && this.widget.configuration.tagConfig.dataModelUuid) {
      this.buildDataByDataModel(this.widget.configuration.tagConfig, 'tag', { CURRENT_USER: true });
    }
    [
      'setAsRead',
      'cancelRead',
      'setAsAttent',
      'cancelAttent',
      'setAsCollect',
      'cancelCollect',
      'setAsTop',
      'cancelTop',
      'setAsClassify',
      'cancelClassify',
      'setAsTag',
      'cancelTag'
    ].forEach(id => {
      let _this = this;
      this.pageContext.handleEvent(`${this.widget.id}:${id}`, function () {
        _this[id].apply(_this, arguments);
      });
    });
  },
  mounted() {
    let _this = this;

    if (_this.widget.configuration.readConfig.enable) {
      this.pageContext.handleCrossTabEvent('dyform:mounted', function (data) {
        let { dataUuid, formUuid, userId } = data;
        if (userId == _this._$USER.userId && dataUuid != undefined) {
          _this.$refs.table.rows.forEach((row, index) => {
            if (row.UUID == dataUuid && (_this.dataUuidMark[row.UUID] == undefined || !_this.dataUuidMark[row.UUID].includes('READ'))) {
              _this.updateDataRelaMarker([row.UUID], 'READ', () => {
                _this.$refs.table.clearRowStyle(index);
              });
            }
          });
        }
      });
    }
  }
};
</script>
