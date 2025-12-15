<template>
  <div class="widget-file-manager">
    <a-spin :tip="maskTip" :spinning="mask">
      <template v-if="widget.configuration.type == 'table'">
        <WidgetTable
          ref="table"
          :widget="widget.configuration.WidgetTable"
          :parent="parent"
          :widgetsOfParent="widgetsOfParent"
          :designer="designer"
          @beforeLoadData="onTableBeforeLoadData"
          @tbodyRendered="onTableBodyRendered"
          @rowClick="onTableRowClick"
          @onSelectRowChanged="onSelectRowChanged"
        >
          <template slot="afterTableHeader" v-if="widget.configuration.showBreadcrumbNav">
            <FileBreadcrumbNav ref="breadcrumbNav" :initItems="[rootFolder]" @click="item => openFolder(item, false)"></FileBreadcrumbNav>
          </template>
        </WidgetTable>
      </template>
      <template v-else>
        <a-layout style="background: #fff" :hasSider="true">
          <a-layout-sider
            ref="leftSider"
            v-model="collapsed"
            :collapsible="true"
            :trigger="null"
            :collapsedWidth="0"
            theme="light"
            class="left-sider"
          >
            <a-icon
              type="double-left"
              :class="['left-collapse', collapsed ? 'collapsed' : '']"
              @click.stop="() => (collapsed = !collapsed)"
              :title="collapsed ? $t('WidgetFileManager.expand', '展开') : $t('WidgetFileManager.collapse', '收起')"
            />
            <a-input-search
              v-if="widget.configuration.showSearch"
              allowClear
              @search="onFolderTreeSearch"
              style="margin-top: 12px; padding: 0 8px"
            />
            <PerfectScrollbar style="height: 100%">
              <a-skeleton active :loading="loading">
                <a-tree
                  :load-data="onLoadData"
                  :tree-data="treeData"
                  :replaceFields="replaceFields"
                  :defaultExpandedKeys="defaultExpandedKeys"
                  :expandedKeys.sync="expandedKeys"
                  :loadedKeys="loadedKeys"
                  :blockNode="true"
                  :showIcon="true"
                  :draggable="treeDraggable"
                  @drop="onFolderTreeDrop"
                  @select="onFolderTreeSelect"
                >
                  <a-icon slot="switcherIcon" type="down" />
                  <template slot="title" slot-scope="node">
                    <Icon v-if="node.id == 'recycleBin'" :type="configuration.recycleBinIcon || 'delete'"></Icon>
                    <Icon v-else-if="node.isLeaf" type="ant-iconfont file-text"></Icon>
                    <!-- <a-icon v-else-if="node.expanded && !node.isLeaf" type="folder-open"></a-icon>
                    <a-icon v-else type="folder"></a-icon> -->
                    <img v-else-if="node.expanded && !node.isLeaf" class="svg-iconfont" :src="'/static/svg/folder-open.svg'" />
                    <img v-else class="svg-iconfont" :src="'/static/svg/folder-close.svg'" />
                    <span>{{ node.name }}</span>
                  </template>
                </a-tree>
                <a-empty
                  v-if="searchValue && !treeData.length"
                  :description="$t('WidgetFileManager.message.noMatchData', '无匹配数据')"
                ></a-empty>
              </a-skeleton>
            </PerfectScrollbar>
          </a-layout-sider>
          <a-layout-content>
            <WidgetTable
              ref="table"
              :widget="widget.configuration.WidgetTable"
              :parent="parent"
              :widgetsOfParent="widgetsOfParent"
              :designer="designer"
              @beforeLoadData="onTableBeforeLoadData"
              @tbodyRendered="onTableBodyRendered"
              @rowClick="onTableRowClick"
              @onSelectRowChanged="onSelectRowChanged"
              class="right-table"
            >
              <template slot="afterTableHeader" v-if="widget.configuration.showBreadcrumbNav">
                <FileBreadcrumbNav
                  ref="breadcrumbNav"
                  :initItems="[rootFolder]"
                  @click="item => openFolder(item, false)"
                  class="breadcrumb-nav"
                ></FileBreadcrumbNav>
              </template>
            </WidgetTable>
          </a-layout-content>
        </a-layout>
      </template>
    </a-spin>
    <a-upload
      v-show="false"
      ref="fileUpload"
      name="file"
      :multiple="true"
      action="/repository/file/mongo/savefiles"
      :fileList="fileList"
      :customRequest="customRequest"
      @change="onFileChange"
      :showUploadList="false"
    >
      <a-button>
        <a-icon type="upload" class="btn-file-upload" />
        Click to Upload
      </a-button>
    </a-upload>
  </div>
</template>

<script type="text/babel">
import { deepClone } from '@framework/vue/utils/util';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { upload } from '@framework/vue/lib/preview/filePreviewApi';
import { trim, isEmpty } from 'lodash';
import FileUploadList from './file-upload-list.vue';
import FileBreadcrumbNav from './file-breadcrumb-nav.vue';
import FileManager from './FileManager.js';
export default {
  name: 'WidgetFileManager',
  components: { FileBreadcrumbNav },
  mixins: [widgetMixin],
  inject: ['locale'],
  data() {
    let configuration = this.widget.configuration;
    let rootFolder = {
      uuid: configuration.belongToFolderUuid,
      name: configuration.belongToFolderName
    };
    return {
      loading: false,
      mask: false,
      maskTip: '',
      collapsed: false,
      fileManager: new FileManager(this),
      libraryUuid: configuration.belongToFolderUuid,
      rootFolder,
      selectedFolder: {
        ...rootFolder
      },
      treeData: [],
      replaceFields: { title: 'name', key: 'id', value: 'id' },
      defaultExpandedKeys: [rootFolder.uuid],
      expandedKeys: [],
      loadedKeys: [],
      treeDraggable: false,
      fileList: [],
      searchValue: ''
    };
  },
  computed: {
    defaultEvents() {
      return [
        { id: 'createFolder', title: '新建文件夹' },
        { id: 'uploadFile', title: '上传文件' },
        { id: 'createDocument', title: '新建文档' },
        { id: 'viewFile', title: '查看文件' },
        { id: 'download', title: '下载' },
        { id: 'delete', title: '删除' },
        { id: 'restore', title: '恢复' },
        { id: 'rename', title: '重命名' },
        { id: 'copy', title: '复制' },
        { id: 'move', title: '移动' },
        { id: 'viewAttributes', title: '查看属性' },
        { id: 'fulltextQuery', title: '全文检索' }
      ];
    }
  },
  beforeMount() {
    const _this = this;
    if (_this.libraryUuid) {
      _this.initFolderTree();
      _this.fileManager.getFolderActions(_this.libraryUuid).then(fileActions => {
        _this.treeDraggable = fileActions.isAllowMoveFolder();
        // 更新列表操作权限
        _this.updateTableDataPermissionOfCreate(fileActions);
      });
    }
  },
  mounted() {},
  methods: {
    getRootFolder() {
      return this.rootFolder;
    },
    initFolderTree() {
      this.asyncLoadTree().then(({ data: result }) => {
        if (result.data) {
          let treeData = this.convertAsyncTreeData(result.data);
          if (this.configuration.enabledRecycleBin) {
            treeData.push({
              id: 'recycleBin',
              name: this.configuration.recycleBinName || '回收站',
              isParent: false,
              children: [],
              isLeaf: true
            });
          }
          this.treeData = treeData;
          // 默认展开第一个节点
          if (this.treeData.length) {
            this.expandedKeys = [this.treeData[0].id, 'recycleBin'];
          }
        }
      });
    },
    asyncLoadTree(parentFolderUuid, keyword) {
      return $axios({
        method: 'POST',
        url: '/proxy/api/dms/file/manager/load/folder/tree',
        params: {
          belongToFolderUuid: this.libraryUuid,
          parentFolderUuid: parentFolderUuid,
          showRootFolder: true,
          checkIsParent: true, // 异步加载，检测是否父节点
          // withoutPermission: true
          keyword
        },
        headers: { 'content-type': 'application/x-www-form-urlencoded' }
      });
    },
    onFolderTreeSearch(value) {
      const _this = this;
      let keyword = trim(value || '');
      _this.searchValue = keyword;
      if (isEmpty(keyword)) {
        if (_this.keepTreeData) {
          _this.treeData = [..._this.keepTreeData];
          _this.expandedKeys = _this.keepExpandedKeys;
          delete _this.keepTreeData;
        }
      } else {
        if (!_this.keepTreeData) {
          _this.keepTreeData = deepClone(_this.treeData);
          _this.keepExpandedKeys = [..._this.expandedKeys];
        }
        _this.loading = true;
        _this
          .asyncLoadTree(null, keyword)
          .then(({ data: result }) => {
            if (result.data) {
              _this.treeData = _this.convertAsyncTreeData(result.data);
            }
          })
          .finally(() => {
            _this.loading = false;
          });
      }
    },
    convertAsyncTreeData(treeData) {
      let traverseTree = nodes => {
        if (!nodes || !nodes.length) {
          return;
        }
        nodes.forEach(node => {
          node.isLeaf = !node.isParent;
          if (node.isParent && node.children && !node.children.length) {
            delete node.children;
          }
          traverseTree(node.children);
        });
      };

      traverseTree(treeData);
      return treeData;
    },
    setBreadcrumbNav(items) {
      if (this.$refs.breadcrumbNav) {
        this.$refs.breadcrumbNav.setItems(items);
      }
    },
    addBreadcrumbNav(item) {
      if (this.$refs.breadcrumbNav) {
        this.$refs.breadcrumbNav.addItem(item);
      }
    },
    showMask(tip) {
      this.mask = true;
      this.maskTip = tip;
    },
    hideMask() {
      this.mask = false;
    },
    onLoadData(treeNode, existsChildren = []) {
      const _this = this;
      return new Promise(resolve => {
        if (treeNode.dataRef.children && treeNode.dataRef.children.length) {
          _this.loadedKeys.push(treeNode.dataRef.id);
          resolve();
          return;
        }

        _this.asyncLoadTree(treeNode.dataRef.id).then(({ data: result }) => {
          if (result.data) {
            let children = _this.convertAsyncTreeData(result.data);
            // 合并已经存在的子节点的子节点
            if (_this.treeData && _this.treeData.length) {
              children.forEach(child => {
                let childData = _this.findTreeNodeById(child.id, existsChildren);
                if (childData && childData.children && childData.children.length) {
                  child.children = childData.children;
                }
              });
            }
            treeNode.dataRef.children = children;
          }
          _this.treeData = [..._this.treeData];
          _this.loadedKeys.push(treeNode.dataRef.id);
          resolve();
        });
      });
    },
    onFolderTreeDrop(info) {
      const dragKey = info.dragNode.eventKey;
      const dropKey = info.node.eventKey;
      const dropPos = info.node.pos.split('-');
      const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
      const dropToBottom = dropPosition == 1;
      // console.log('dropPos,dropPosition', dropPos, info.dropPosition, dropToBottom);
      // console.log('dragKey, dropKey', dragKey, dropKey);
      // console.log('info.dropToGap', info.dropToGap);
      if (dropKey == this.libraryUuid) {
        this.$message.error(this.$t('WidgetFileManager.message.unableToMoveRoot', '不能将节点拖入根节点！'));
        return;
      }
      if (dragKey == this.libraryUuid) {
        this.$message.error(this.$t('WidgetFileManager.message.unableToMoveRootToOther', '不能将根节点拖入其他节点！'));
        return;
      }
      if (dragKey == dropKey) {
        return;
      }
      this.moveItemNode(dragKey, dropKey, info.dropToGap, dropToBottom, dropPos);
      // 更新树
      this.treeData = [...this.treeData];
    },
    moveItemNode(fromId, toId, sibling, dropToBottom, dropPos) {
      const _this = this;
      let treeData = this.treeData;
      // 删除并返回源节点数据
      let deleteFromNode = (nodes, parentNode) => {
        let deletedNode = null;
        if (nodes == null) {
          return deletedNode;
        }
        for (let index = 0; index < nodes.length; index++) {
          let node = nodes[index];
          if (node.id == fromId) {
            nodes.splice(index, 1);
            // 父节点变为子节点
            if (parentNode && parentNode.children && !parentNode.children.length) {
              parentNode.isLeaf = true;
              parentNode.isParent = false;
            }
            return node;
          }
          // 子节点
          deletedNode = deleteFromNode(node.children, node);
          if (deletedNode) {
            return deletedNode;
          }
        }
        return deletedNode;
      };

      // 在目标节点插入源节点数据
      let appendFromNode = (fromNode, nodes, parentNode) => {
        if (nodes == null) {
          return false;
        }
        for (let index = 0; index < nodes.length; index++) {
          let node = nodes[index];
          if (node.id == toId) {
            node.children = node.children || [];
            if (sibling) {
              nodes.splice(dropToBottom ? index + 1 : index, 0, fromNode);
              // 更新目录编号
              _this.updateFolderCodes(nodes, dropPos).then(() => {
                // 移动夹
                if (parentNode) {
                  _this.fileManager.moveFileTo(
                    [{ uuid: fromNode.id, name: fromNode.name, contentType: 'application/folder' }],
                    parentNode.id
                  );
                }
              });
            } else {
              if (node.children.length) {
                node.children.push(fromNode);
                // 子节点变为父节点
                node.isLeaf = false;
                node.isParent = true;
                // 更新目录编号
                _this.updateFolderCodes(node.children, dropPos).then(() => {
                  // 移动夹
                  _this.fileManager.moveFileTo([{ uuid: fromNode.id, name: fromNode.name, contentType: 'application/folder' }], node.id);
                });
              } else {
                _this
                  .onLoadData({
                    dataRef: node
                  })
                  .then(() => {
                    node.children.push(fromNode);
                    // 子节点变为父节点
                    node.isLeaf = false;
                    node.isParent = true;
                    // 更新目录编号
                    _this.updateFolderCodes(node.children, dropPos).then(() => {
                      // 移动夹
                      _this.fileManager.moveFileTo(
                        [{ uuid: fromNode.id, name: fromNode.name, contentType: 'application/folder' }],
                        node.id
                      );
                    });
                  });
              }
            }
            return true;
          }
          // 子节点
          if (appendFromNode(fromNode, node.children, node)) {
            return true;
          }
        }
        return false;
      };

      let fromNode = deleteFromNode(treeData);
      if (fromNode) {
        appendFromNode(fromNode, treeData, null);
      } else {
        console.error('from node not found', fromId);
      }
    },
    updateFolderCodes(nodes, dropPos) {
      let folderCodeMap = {};
      nodes.forEach((node, index) => {
        let nodePos = [...dropPos];
        let indexString = index + '';
        if (nodePos.length >= indexString.length) {
          nodePos.length = nodePos.length - indexString.length;
        } else {
          nodePos = [];
        }
        folderCodeMap[node.id] = nodePos.join('') + indexString;
      });

      return $axios.post('/proxy/api/dms/file/manager/updateFolderCodes', folderCodeMap);
    },
    onFolderTreeSelect(selectedKeys, e) {
      const _this = this;
      _this.selectedFolder = {
        uuid: e.node.dataRef.id,
        name: e.node.dataRef.name
      };
      if (_this.$refs.table) {
        _this.$refs.table.refetch(true);
        setTimeout(() => {
          _this.updateTableColumnIfRequired(_this.$refs.table);
        }, 100);
      }

      // 设置面包屑导航
      _this.setBreadcrumbNav(_this.getFolderPathsOfTreeDataByFolder(_this.selectedFolder));

      // 更新列表操作权限
      _this.updateTableDataPermissionOfCreate();
    },
    updateTableColumnIfRequired($tableWidget) {
      const _this = this;
      if (!_this.configuration.enabledRecycleBin) {
        return;
      }
      if (!_this.tableColumns) {
        let columns = $tableWidget.widget.configuration.columns;
        let recycleBinTableDisplayColumns = _this.configuration.recycleBinTableDisplayColumns || [];
        _this.tableColumns = deepClone(columns);
        _this.recycleBinColumns = deepClone(columns);
        _this.recycleBinColumns.forEach(column => {
          if (recycleBinTableDisplayColumns.includes(column.dataIndex)) {
            column.hidden = false;
          } else {
            column.hidden = true;
          }
        });
        _this.recycleBinColumns.sort(
          (a, b) => recycleBinTableDisplayColumns.indexOf(a.dataIndex) - recycleBinTableDisplayColumns.indexOf(b.dataIndex)
        );
        _this.tableRowButton = $tableWidget.widget.configuration.rowButton;
        _this.tableRowEndButton = $tableWidget.rowEndButton;
      }

      if (_this.isSelectRecycleBin()) {
        $tableWidget.widget.configuration.columns = _this.recycleBinColumns;
        $tableWidget.widget.configuration.rowButton = { buttons: [], enabled: false };
        $tableWidget.rowEndButton = { buttons: [] };
      } else {
        $tableWidget.widget.configuration.columns = _this.tableColumns;
        $tableWidget.widget.configuration.rowButton = _this.tableRowButton || { buttons: [], enabled: false };
        $tableWidget.rowEndButton = _this.tableRowEndButton || { buttons: [] };
      }
    },
    isSelectRecycleBin() {
      return this.selectedFolder.uuid == 'recycleBin';
    },
    getFolderPathsOfTreeDataByFolder(folder) {
      let folderDatas = [];

      let parentDatas = [];
      let fetchFolderDatas = (parentNode, nodes) => {
        if (parentNode) {
          parentDatas.push({ uuid: parentNode.id, name: parentNode.name });
        }
        nodes.forEach(node => {
          if (node.id == folder.uuid) {
            folderDatas = [...parentDatas];
          }
          if (node.children) {
            fetchFolderDatas(node, node.children);
          }
        });
        if (parentNode) {
          parentDatas.pop();
        }
      };
      fetchFolderDatas(null, this.treeData);
      return folderDatas.concat([folder]);
    },
    findTreeNodeById(id, treeData) {
      let treeNode = null;
      let findTreeNode = nodes => {
        if (treeNode) {
          return;
        }
        nodes.forEach(node => {
          if (node.id == id) {
            treeNode = node;
            return;
          }
          findTreeNode(node.children || []);
        });
      };
      findTreeNode(treeData || this.treeData);

      return treeNode;
    },
    /**
     * 重新加载树
     */
    reloadTree() {
      this.loadedKeys = [];
      this.initFolderTree();
    },
    /**
     * 重新加载树节点
     */
    reloadTreeNode(treeNode) {
      const _this = this;
      let selectedTreeNode = _this.findTreeNodeById(treeNode.id, _this.treeData);
      if (selectedTreeNode) {
        let existsChildren = deepClone(selectedTreeNode.children);
        delete selectedTreeNode.children;
        _this.onLoadData(
          {
            dataRef: selectedTreeNode
          },
          existsChildren
        );
      }
    },
    /**
     * 在当前节点附加树节点
     */
    appendTreeNode(treeNode) {
      const _this = this;
      let selectedFolder = _this.getSelectedFolder();
      let selectedTreeNode = _this.findTreeNodeById(selectedFolder.uuid, _this.treeData);
      if (selectedTreeNode) {
        selectedTreeNode.children = selectedTreeNode.children || [];
        selectedTreeNode.children.push({ isLeaf: true, ...treeNode });
        selectedTreeNode.isLeaf = false;
        selectedTreeNode.isParent = true;
        if (!_this.expandedKeys.includes(selectedTreeNode.id)) {
          _this.expandedKeys.push(selectedTreeNode.id);
        }
        _this.treeData = [..._this.treeData];
      }
    },
    /**
     * 更新树节点
     */
    updateTreeNode(treeNode) {
      const _this = this;
      let toUpdateTreeNode = _this.findTreeNodeById(treeNode.id, _this.treeData);
      if (toUpdateTreeNode) {
        toUpdateTreeNode.name = treeNode.name;
        _this.treeData = [..._this.treeData];
      }
    },
    /**
     * 根据节点ID删除树节点
     */
    deleteTreeNodeByIds(treeNodeIds) {
      const _this = this;
      let hasDeleted = false;

      let deleteTreeNodeById = (id, nodes) => {
        if (!nodes) {
          return;
        }
        let nodeIndex = nodes.findIndex(node => node.id == id);
        if (nodeIndex != -1) {
          nodes.splice(nodeIndex, 1);
          hasDeleted = true;
        }
        nodes.forEach(node => {
          deleteTreeNodeById(id, node.children);
        });
      };

      treeNodeIds.forEach(id => {
        deleteTreeNodeById(id, this.treeData);
      });

      if (hasDeleted) {
        this.treeData = [...this.treeData];
      }
    },
    openFolder(folder, addBreadcrumb = true) {
      const _this = this;
      _this.setSelectedFolder(folder);
      if (_this.$refs.table) {
        _this.$refs.table.refetch(true);
      }
      // 添加面包屑导航
      if (addBreadcrumb) {
        _this.addBreadcrumbNav(folder);
      }

      // 更新列表操作权限
      _this.updateTableDataPermissionOfCreate();
    },
    setSelectedFolder(folder) {
      this.selectedFolder = folder;
    },
    getSelectedFolder() {
      return this.selectedFolder;
    },
    updateTableDataPermissionOfCreate(fileActions) {
      const _this = this;
      if (_this.$refs.table) {
        if (fileActions) {
          _this.$refs.table.$data.__TABLE__.dataPermission = fileActions.getDataPermissionOfCreate();
        } else {
          _this.fileManager.getFolderActions(_this.selectedFolder.uuid).then(fileActions => {
            _this.$refs.table.$data.__TABLE__.dataPermission = fileActions.getDataPermissionOfCreate();
          });
        }
      }
    },
    onTableBeforeLoadData(params) {
      const _this = this;
      let dataSourceParams = null;
      _this.$refs.table.deleteDataSourceParams('recycleBin', 'listFileMode');
      let selectedFolder = _this.getSelectedFolder();
      // 回收站
      if (selectedFolder.uuid == 'recycleBin') {
        dataSourceParams = { recycleBin: true, folderUuid: this.rootFolder.uuid, listFileMode: 'listAllFolderAndFiles' };
      } else {
        dataSourceParams = { folderUuid: selectedFolder.uuid };
        if (_this.configuration.listFileModeType == 'custom') {
          dataSourceParams.listFileMode = _this.configuration.listFileMode;
        }
      }
      _this.$refs.table.addDataSourceParams(dataSourceParams);
    },
    onTableBodyRendered({ rows = [] }) {
      this.fileManager.getFileActions(rows);
    },
    onTableRowClick() {},
    onSelectRowChanged({ selectedRows }) {
      const _this = this;
      _this.fileManager.getFolderActions(_this.selectedFolder.uuid).then(fileActions => {
        let dataPermissionOfCreate = fileActions.getDataPermissionOfCreate();
        if (selectedRows && selectedRows.length) {
          _this.fileManager.getFilesDataPermission(selectedRows).then(dataPermissionOfSelectedRows => {
            let dataPermission = dataPermissionOfSelectedRows;
            // 选择回收站只能进行恢复操作
            if (_this.isSelectRecycleBin()) {
              dataPermission = dataPermission.includes('restore')
                ? selectedRows.length == 1
                  ? ['restore', 'viewAttributes']
                  : ['restore']
                : [];
            } else {
              dataPermission = dataPermission.filter(permission => !['restoreFile', 'restore'].includes(permission));
            }
            _this.$refs.table.$data.__TABLE__.dataPermission = dataPermissionOfCreate.concat(dataPermission);
          });
        } else {
          _this.$refs.table.$data.__TABLE__.dataPermission = dataPermissionOfCreate;
        }
      });
    },
    /**
     * 刷新
     */
    refresh(options = {}) {
      const _this = this;
      // 添加树节点
      if (options.appendTreeNode) {
        _this.appendTreeNode(options.appendTreeNode);
      }
      // 更新树节点
      if (options.updateTreeNode) {
        _this.updateTreeNode(options.updateTreeNode);
      }
      // 删除的树节点
      if (options.deletedTreeNodeIds && options.deletedTreeNodeIds.length) {
        _this.deleteTreeNodeByIds(options.deletedTreeNodeIds);
        // 删除表格选中的数据
        if (options.deletedTreeNodeIds.length == _this.$refs.table.selectedRows.length) {
          _this.onSelectRowChanged({ selectedRows: [] });
        }
      }
      // 重新加载树节点
      if (options.reloadTreeNode) {
        _this.reloadTreeNode(options.reloadTreeNode);
      }
      // 重新加载树
      if (options.reloadTree) {
        _this.reloadTree();
      }
      if (_this.$refs.table) {
        _this.$refs.table.selectedRowKeys = [];
        _this.$refs.table.selectedRows = [];
        _this.$refs.table.refetch(true);
        _this.onSelectRowChanged({ selectedRows: [] });
      }
    },
    handleCrossTabEvent() {
      const _this = this;
      _this.pageContext.handleCrossTabEvent(`filemanager:detail:change:${_this.configuration.WidgetDyformSetting.id}`, options => {
        if (options && options.message) {
          _this.$message.success(options.message);
        }
        _this.refresh();
      });
    },
    /**
     * 创建夹
     */
    createFolder($evt) {
      this.fileManager.createFolder($evt);
    },
    /**
     * 上传文件
     */
    uploadFile($evt) {
      this.$refs.fileUpload.$el.querySelector('.btn-file-upload').click();
    },
    customRequest(requestOption) {
      upload(requestOption);
    },
    onFileChange(info) {
      const _this = this;
      if (info.file.status === 'done') {
        info.fileList.forEach(file => {
          let data = (file.response && file.response.data) || [];
          if (data.length > 0) {
            file.uuid = data[0].fileID;
            file.fileID = data[0].fileID;
          }
        });
        _this.fileList = info.fileList;
        _this.uploadFileId();
      } else if (info.file.status === 'uploading') {
        let uploadingFile = _this.fileList.find(file => file.uid == info.file.uid);
        if (uploadingFile == null) {
          _this.fileList.push(info.file);
        }
        _this.showFileUploadNotification();
      } else if (info.file.status == 'error') {
        _this.$message.error(this.$t('WidgetFormFileUpload.validateError.error', '存在上传异常的附件,请确认后继续'));
      }
    },
    showFileUploadNotification() {
      const _this = this;
      _this.$notification.open({
        key: 'file-upload-notification',
        message: this.$t('WidgetFileManager.fileUpload', '文件上传'),
        description: <FileUploadList fileList={_this.fileList}></FileUploadList>,
        duration: null,
        placement: 'bottomRight'
      });
    },
    uploadFileId() {
      const _this = this;
      let selectedFolder = _this.getSelectedFolder();
      let fileIds = _this.fileList
        .filter(file => {
          if (file.status == 'done' && file.fileID && !file.uploadFileId) {
            // 标记上传文件ID
            file.uploadFileId = true;
            return true;
          }
          return false;
        })
        .map(file => file.fileID);
      $axios
        .post(`/proxy/api/dms/file/uploadFileId?fileIds=${fileIds.join(';')}&folderUuid=${selectedFolder.uuid}`)
        .then(({ data: result }) => {
          if (result.data) {
            _this.refresh();
            let toUploadList = _this.fileList.filter(file => file.status != 'done' || !file.uploadFileId);
            // 没有待上传附件，5秒后关闭提示
            if (!toUploadList.length) {
              setTimeout(() => {
                _this.fileList.length = 0;
                _this.$notification.close('file-upload-notification');
              }, 5000);
            }
          } else {
            _this.$message.error(result.msg || _this.$t('WidgetFileManager.message.uploadFailedToFolder', '上传失败，无法将文件放入夹！'));
          }
        })
        .catch(({ response }) => {
          _this.$message.error(
            (response && response.data && response.data.msg) || _this.$t('WidgetFileManager.message.serverError', '服务异常！')
          );
        });
    },
    /**
     * 新建文档
     */
    createDocument($evt) {
      const _this = this;
      _this.fileManager.createDocument($evt);
      _this.handleCrossTabEvent();
    },
    /**
     * 查看文件
     */
    viewFile($evt) {
      const _this = this;
      if (_this.isSelectRecycleBin()) {
        return;
      }
      _this.fileManager.viewFile($evt && $evt.meta, $evt);
      _this.handleCrossTabEvent();
    },
    /**
     * 下载文件
     */
    download($evt) {
      this.fileManager.download($evt && $evt.meta && $evt.meta.selectedRows);
    },
    /**
     * 删除
     */
    delete($evt) {
      this.fileManager.delete($evt && $evt.meta && $evt.meta.selectedRows);
    },
    /**
     * 恢复
     */
    restore($evt) {
      this.fileManager.restore($evt && $evt.meta && $evt.meta.selectedRows);
    },
    /**
     * 重命名
     */
    rename($evt) {
      this.fileManager.rename($evt && $evt.meta && $evt.meta.selectedRows);
    },
    /**
     * 复制
     */
    copy($evt) {
      this.fileManager.copy($evt && $evt.meta && $evt.meta.selectedRows);
    },
    /**
     * 移动
     */
    move($evt) {
      this.fileManager.move($evt && $evt.meta && $evt.meta.selectedRows);
    },
    /**
     * 查看属性
     */
    viewAttributes($evt) {
      this.fileManager.viewAttributes($evt && $evt.meta && $evt.meta.selectedRows);
    },
    /**
     * 全文检索
     */
    fulltextQuery($evt) {
      this.fileManager.fulltextQuery($evt);
    }
  },
  // 组件元数据
  META: {
    // 对设计器暴露的方法集合
    method: {
      createFolder: '新建文件夹',
      uploadFile: '上传',
      createDocument: '新建文档',
      viewFile: '查看文件',
      download: '下载',
      delete: '删除',
      restore: '恢复',
      rename: '重命名',
      copy: '复制',
      move: '移动',
      viewAttributes: '查看属性',
      fulltextQuery: '全文检索'
    }
  }
};
</script>

<style lang="less" scoped>
.widget-file-manager {
  .left-sider {
    border: 1px solid var(--w-border-color-base);
    position: relative;

    :hover {
      .left-collapse {
        opacity: 1;
      }
    }
    .left-collapse {
      position: absolute;
      top: 35%;
      right: -18px;
      z-index: 2;
      margin-left: -1px;
      border-radius: 0px 4px 4px 0px;
      height: 48px;
      width: 18px;
      line-height: 48px;
      text-align: center;
      margin-left: -1px;
      background-color: var(--w-fill-color-base);
      color: var(--w-text-color-dark);
      border: 1px solid var(--w-border-color-light);
      border-left: 0;
      font-size: 10px;
      opacity: 0;
      transition: left 0.15s linear, opacity 0.3s linear;
      cursor: pointer;
    }
    .left-collapse.collapsed {
      transform: rotate(180deg);
      left: 0px;
      opacity: 1;
      border-left: 1px solid var(--w-border-color-light);
      border-right: 0;
      border-radius: 4px 0px 0px 4px;
    }
  }
  .right-table {
    margin-left: 16px;
    .breadcrumb-nav {
      margin-bottom: 6px;
      font-weight: bold;
    }
  }
}
</style>
