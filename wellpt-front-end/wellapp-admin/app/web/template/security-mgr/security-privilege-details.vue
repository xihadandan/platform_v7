<template>
  <div :loading="loading">
    <a-form-model ref="form" :model="formData" :rules="rules" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" class="pt-form">
      <a-tabs default-active-key="1" v-model="activeKey" class="pt-tabs">
        <a-tab-pane key="1" tab="基本信息">
          <a-form-model-item label="名称" prop="name">
            <a-input v-model="formData.name" />
          </a-form-model-item>
          <a-form-model-item label="编号" prop="code">
            <a-input v-model="formData.code" />
          </a-form-model-item>
          <a-form-model-item label="归属" prop="appId">
            <!-- 权限不允许修改归属 -->
            <a-select
              :showSearch="true"
              v-model="formData.appId"
              :options="moduleOptions"
              :filter-option="filterOption"
              :style="{ width: '100%' }"
              disabled
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="备注" prop="remark">
            <a-textarea v-model="formData.remark" />
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="2" tab="系统集成信息">
          <div class="content-box">
            <div class="flex f_x_s" style="margin-bottom: 12px">
              <div class="content-title">选择权限</div>
              <a-input-search v-model="searchValue" @search="onSearchTreeNode" allowClear style="width: 160px" />
            </div>
            <PerfectScrollbar style="max-height: calc(100vh - 220px); margin-right: -20px; padding-right: 12px">
              <div class="spin-center" v-if="resourceLoading">
                <a-spin />
              </div>
              <a-tree
                v-else
                ref="resourceTree"
                v-model="resourceCheckedKeys"
                :tree-data="resourceTreeData"
                class="ant-tree-directory"
                :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
                blockNode
                checkable
                checkStrictly
                :expandedKeys="expandedKeys"
                @check="onCheck"
                show-icon
                @expand="expandNode"
              >
                <template slot="title" slot-scope="node">
                  <div class="title" :title="node.name">
                    <label v-if="node.type == 'appSystem'">系统:</label>
                    <label v-else-if="node.type == 'appModule'">模块:</label>
                    <label v-else-if="node.type == 'appPageDefinition'">页面:</label>

                    <!-- <template v-if="node.children && node.children.length > 0">
                        <a-icon v-if="hasChildCheckedOfNode(node)" type="check-circle" :style="{ color: 'blue' }"></a-icon>
                        <Icon type="folder"></Icon>
                      </template>
                      <Icon v-else type="file"></Icon> -->
                    <span v-if="searchValue && node.name.indexOf(searchValue) > -1">
                      {{ node.name.substr(0, node.name.indexOf(searchValue)) }}
                      <span style="color: #f50">{{ searchValue }}</span>
                      {{ node.name.substr(node.name.indexOf(searchValue) + searchValue.length) }}
                    </span>
                    <span v-else>{{ node.name }}</span>
                  </div>
                  <!-- <a-popover v-model="searchInputVisible" title="搜索" trigger="click" arrowPointAtCenter placement="left">
                        <a-input-search slot="content" />
                        <a-button type="link" icon="search" size="small" style="float: right" v-if="node.type == 'appSystem'"></a-button>
                      </a-popover> -->
                </template>
                <template slot="iconSlot" slot-scope="data">
                  <Icon
                    type="iconfont icon-ptkj-xiangmuguanli"
                    style="color: var(--w-primary-color)"
                    v-if="data.type == 'appSystem' && resourceCheckedKeys.checked.length > 0"
                  />
                  <Icon type="iconfont icon-ptkj-xiangmuguanli" v-else-if="data.type == 'appSystem'" />
                  <Icon type="iconfont icon-ptkj-yemian" v-else-if="data.type == 'appPageDefinition'" />
                  <template v-else-if="data.type == 'appModule'">
                    <Icon
                      type="iconfont icon-ptkj-mokuaiqukuai"
                      v-if="data.type == 'appModule' && hasChildCheckedOfNode(data)"
                      style="color: var(--w-primary-color)"
                    />
                    <Icon type="iconfont icon-ptkj-mokuaiqukuai" v-else="data.type == 'appModule'" />
                  </template>
                  <Icon type="iconfont icon-ptkj-quanxian" v-else />
                </template>
              </a-tree>
            </PerfectScrollbar>
          </div>
        </a-tab-pane>
        <a-tab-pane key="3" tab="资源结果">
          <div class="content-box">
            <PerfectScrollbar style="max-height: calc(100vh - 176px); margin-right: -20px; padding-right: 12px">
              <a-tree
                blockNode
                v-show="resultTreeData.length > 0"
                :tree-data="resultTreeData"
                :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
                class="ant-tree-directory"
              >
                <template slot="title" slot-scope="node">
                  <div class="title" :title="node.name">
                    <a-icon type="iconfont icon-ptkj-yemian" v-if="node.type == 'appPageDefinition'" />
                    <Icon v-else :type="node.icon || 'iconfont icon-ptkj-quanxian'"></Icon>
                    <span>{{ node.name }}</span>
                  </div>
                </template>
              </a-tree>
              <a-empty v-show="resultTreeData.length == 0"></a-empty>
            </PerfectScrollbar>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
    <a-row v-if="!$widgetDrawerContext">
      <a-col offset="11"><a-button type="primary" @click="save">保存</a-button></a-col>
    </a-row>
  </div>
</template>

<script>
import moment from 'moment';
import { compareArrayDifference } from '@framework/vue/utils/util';
const initFormData = () => {
  return {
    name: undefined,
    code: undefined,
    appId: undefined,
    remark: undefined
  };
};

export default {
  inject: ['pageContext'],
  data() {
    return {
      activeKey: '1',
      loading: false,
      resourceLoading: true,
      formData: {
        appId: undefined
      },
      rules: {
        name: { required: true, message: '不能为空', trigger: 'blur' },
        code: { required: true, message: '不能为空', trigger: 'blur' }
      },
      moduleOptions: [],
      resourceTreeData: [],
      originalResourceKeys: [],
      resourceCheckedKeys: {
        checked: [],
        halfChecked: []
      },
      expandedKeys: [],
      searchValue: '',
      searchInputVisible: false,
      $widgetDrawerContext: null
    };
  },
  computed: {
    treeNodeAsList() {
      let list = [];
      let travelTree = (nodes, parentNode, parentKeys) => {
        nodes.forEach(node => {
          if (parentNode) {
            parentKeys.push(parentNode.id);
          }
          node.parentKeys = [...parentKeys];
          list.push(node);
          if (node.children) {
            travelTree(node.children, node, parentKeys);
          }
          if (parentNode) {
            parentKeys.pop();
          }
        });
      };
      travelTree(this.resourceTreeData, null, []);
      return list;
    },
    resultTreeData() {
      let list = [];
      let travelTree = nodes => {
        nodes.forEach(node => {
          if (this.resourceCheckedKeys.checked.includes(node.id)) {
            list.push(node);
          } else {
            if (node.children) {
              travelTree(node.children);
            }
          }
        });
      };
      travelTree(this.resourceTreeData);
      return list;
    }
  },
  created() {
    let $event = this._provided && this._provided.$event;
    this.$widgetDrawerContext = this._provided && this._provided.widgetDrawerContext;
    if (this.$widgetDrawerContext) {
      if ($event && $event.meta) {
        if ($event.meta.uuid) {
          // 编辑状态不显示“保存并添加下一个”
          let buttons = [];
          this.$widgetDrawerContext.widget.configuration.footerButton.buttons.map(item => {
            let button = JSON.parse(JSON.stringify(item));
            if (button.code === 'saveAndNewNextData') {
              button.defaultVisible = false;
            }
            buttons.push(button);
          });
          this.$widgetDrawerContext.setFooterButton(buttons);
        }
      }
    }
    this.loadModuleOptions();
    this.fetchSystemControllableResourceTreeData();
  },
  mounted() {
    this.pageContext.handleEvent('privilege:deleted', () => this.clear());
    let $event = this._provided.$event;
    this.$widgetDrawerContext = this._provided && this._provided.widgetDrawerContext;
    if ($event && $event.meta) {
      if ($event.meta.uuid) {
        this.viewDetails($event);
      } else {
        this.add($event);
      }
    }
  },
  methods: {
    onCheck() {},
    initFormData,
    // 保存并添加下一个
    saveAndNewNextData() {
      this.save(() => {
        this.formData = this.initFormData();
        this.add(this._provided.$event);
      });
    },
    loadModuleOptions() {
      const _this = this;
      _this.moduleOptions.splice(0, _this.moduleOptions.length);

      $axios
        .all([
          $axios.get(`/proxy/api/system/getSystemInfoWithProdVersion`, {
            params: {
              system: this._$SYSTEM_ID
            }
          }),
          $axios.get('/proxy/api/app/module/listModuleUnderSystem', {
            params: {
              system: this._$SYSTEM_ID
            }
          })
        ])
        .then(
          $axios.spread((res1, res2) => {
            if (res1.data.data) {
              _this.moduleOptions.push({
                label: res1.data.data.prodVersion.product.name + `(版本${res1.data.data.prodVersion.version})`,
                value: res1.data.data.prodVersion.versionId
              });

              _this.moduleOptions.push({
                label: res1.data.data.prodVersion.product.name,
                value: res1.data.data.prodVersion.product.id
              });
            }

            if (res2.data.data) {
              _this.moduleOptions.push(...res2.data.data.map(item => ({ label: item.name, value: item.id })));
            }
          })
        )
        .catch(error => {});
    },

    loadPrivilege(privilegeUuid) {
      this.loading = true;
      this.resourceCheckedKeys.checked = [];
      $axios
        .get(`/proxy/api/security/privilege/getPrivilegeBean/${privilegeUuid}`)
        .then(({ data: result }) => {
          this.loading = false;
          if (result.code == 0) {
            this.formData = result.data;
            if (this.formData.otherResources) {
              this.originalResourceKeys = [];
              this.formData.otherResources.forEach(res => {
                this.originalResourceKeys.push(res.resourceUuid);
                this.resourceCheckedKeys.checked.push(res.resourceUuid);
              });
            }
          } else {
            this.formData = {};
            this.$message.error(result.msg || '加载失败');
          }
        })
        .catch(({ response }) => {
          this.loading = false;
          this.formData = {};
          if (response.data && response.data.msg) {
            _this.$message.error(response.data.msg);
          } else {
            _this.$message.error('服务异常！');
          }
        });
    },
    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        (option.componentOptions.children[0] &&
          option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0)
      );
    },
    expandNode(expandedKeys) {
      this.expandedKeys = expandedKeys;
    },
    hasChildCheckedOfNode(node) {
      const _this = this;
      let childChecked = false;
      let children = node.children || [];
      let hasChildChecked = nodes => {
        if (childChecked) {
          return;
        }
        nodes.forEach(child => {
          if (_this.resourceCheckedKeys.checked.includes(child.id)) {
            childChecked = true;
          } else {
            hasChildChecked(child.children);
          }
        });
      };
      hasChildChecked(children);

      return childChecked;
    },
    onSearchTreeNode() {
      const _this = this;
      let dataList = _this.treeNodeAsList;
      if (!_this.searchValue) {
        _this.expandedKeys = [];
        return false;
      }

      let matchNodes = dataList.filter(item => item.name && item.name.indexOf(_this.searchValue) != -1);
      matchNodes.forEach(item => {
        let parentKeys = item.parentKeys || [];
        parentKeys.forEach(key => {
          if (!_this.expandedKeys.includes(key)) {
            _this.expandedKeys.push(key);
          }
        });
      });

      // 第一个匹配的节点移入可见视图内
      if (matchNodes.length > 0) {
        _this.$nextTick(() => {
          let $node = _this.getVueTreeNodeByNodeId(matchNodes[0].id);
          if ($node && $node.$el && $node.$el.scrollIntoView) {
            $node.$el.scrollIntoView();
          }
        });
      }
    },
    getVueTreeNodeByNodeId(nodeId) {
      const _this = this;
      let resourceTree = _this.$refs.resourceTree;
      let node = null;
      let fileVueTreeNode = nodes => {
        if (node) {
          return;
        }
        nodes.forEach(child => {
          if (child.dataRef && child.dataRef.id == nodeId) {
            node = child;
          } else {
            fileVueTreeNode(child.$children || []);
          }
        });
      };
      fileVueTreeNode(resourceTree.$children || []);
      return node;
    },
    add(evt) {
      this.clear();
      this.$tableWidget = evt.$evtWidget;
      this.formData = {
        appId: this._$SYSTEM_ID,
        code: 'PRIVILEGE_' + moment().format('yyyyMMDDHHmmss')
      };
    },
    clear() {
      this.activeKey = '1';
      this.formData = {};
      this.resourceCheckedKeys.checked = [];
      this.expandedKeys = [];
    },
    viewDetails(evt) {
      this.clear();
      this.$tableWidget = evt.$evtWidget;
      this.formData = (evt.meta.uuid && evt.meta) || {};
      this.originalResourceKeys = [];
      if (this.formData.uuid) {
        this.loadPrivilege(this.formData.uuid);
      }
    },

    save(callback) {
      const _this = this;
      _this.$refs.form.validate(valid => {
        if (valid) {
          let formData = {
            privilege: {
              uuid: _this.formData.uuid,
              name: _this.formData.name,
              remark: _this.formData.remark,
              code: _this.formData.code,
              system: _this.formData.system,
              appId: _this.formData.appId || this._$SYSTEM_ID
            }
          };
          formData.privilegeResourceAdded = [];
          formData.privilegeResourceDeleted = [];
          // 新创建的权限归属当前租户系统
          if (formData.privilege.uuid == undefined) {
            formData.privilege.system = this._$SYSTEM_ID;
          }

          // 增量更新
          let result = compareArrayDifference(this.originalResourceKeys || [], this.resourceCheckedKeys.checked);
          for (let key in result) {
            let list = result[key];
            list.forEach(item => {
              let node = _this.resourceTreeNodeMap[item];
              if (node) {
                let typeName = null;
                if (node.type == 'appSystem') {
                  typeName = '系统';
                } else if (node.type == 'appModule') {
                  typeName = '模块';
                } else if (node.type == 'appPageDefinition') {
                  typeName = '页面';
                }
                formData[key == 'from' ? 'privilegeResourceDeleted' : 'privilegeResourceAdded'].push({
                  privilegeUuid: _this.formData.uuid,
                  type: node.type,
                  resourceName: (typeName ? `${typeName}: ` : '') + node.name,
                  resourceUuid: item
                });
              }
            });
          }
          console.log('保存', formData);
          _this.$loading();
          $axios
            .post(`/proxy/api/security/privilege/updatePrivilegeRoleResource`, [formData])
            .then(({ data }) => {
              _this.$loading(false);
              if (data.data) {
                _this.$message.success('保存成功');
                _this.originalResourceKeys = JSON.parse(JSON.stringify(_this.resourceCheckedKeys.checked));
                _this.clear();
                if (typeof callback !== 'function') {
                  _this.loadPrivilege(data.data[0]);
                }
                if (_this.$tableWidget) {
                  _this.$tableWidget.refetch(true);
                }
                $axios.get(`/proxy/api/security/privilege/publishPrivilegeUpdatedEvent/${data.data}`);
                if (_this.$widgetDrawerContext && typeof callback !== 'function') {
                  _this.$widgetDrawerContext.close();
                }
                if (typeof callback === 'function') {
                  callback();
                }
              }
            })
            .catch(error => {
              _this.$loading(false);
            });
        } else {
          this.activeKey = '1';
        }
      });
    },

    fetchSystemControllableResourceTreeData() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/system/queryControllableResourceTree`, {
            params: {
              system: this._$SYSTEM_ID
            }
          })
          .then(({ data }) => {
            console.log('查询系统下的受保护资源树: ', data.data);
            this.resourceLoading = false;
            if (data.data) {
              let treeData = data.data;
              this.resourceTreeData = [treeData]; // treeData && treeData.length == 1 ? treeData[0].children || [] : treeData;
              this.resourceTreeNodeMap = {};
              let cascadeSetNode = (list, parent) => {
                if (list && list.length > 0) {
                  list.forEach(item => {
                    item.parent = parent;
                    this.resourceTreeNodeMap[item.id] = item;
                    item.checkable = !item.nocheck;
                    item.scopedSlots = { icon: 'iconSlot' };
                    cascadeSetNode(item.children, item);
                  });
                }
              };
              cascadeSetNode(this.resourceTreeData, undefined);
            }
            resolve(data.data);
          })
          .catch(error => {});
      });
    }
  },
  META: {
    method: {
      add: '新增权限',
      viewDetails: '查看权限详情',
      save: '保存权限',
      saveAndNewNextData: '保存并添加下一个'
    }
  }
};
</script>

<style scoped lang="less">
.content-box {
  border-radius: 4px;
  border: 1px solid var(--w-border-color-light);
  padding: 12px 20px;
  .content-title {
    font-size: var(--w-font-size-base);
    color: var(--w-text-color-dark);
    font-weight: bold;
    width: 120px;
    line-height: 32px;
  }
}
</style>
