<template>
  <a-card title="对象" class="module-page-resource-card" :bordered="false">
    <a-row type="flex" style="margin: 12px 0 16px" v-show="!pleaseSelectExport">
      <a-col flex="auto" style="margin-right: 12px">
        <a-input-search placeholder="请输入关键字" @search="onSearch" allow-clear @change="onSearchInputChange" @click.stop="() => {}" />
      </a-col>
      <a-col flex="44px">
        <a-dropdown :trigger="['click']">
          <a-button class="icon-only" title="更多新建操作"><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
          <a-menu slot="overlay" @click="handleMenuAddClick">
            <a-menu-item key="CreateDataModel">新建存储对象</a-menu-item>
            <a-menu-item key="CreateDataModelView">新建视图对象</a-menu-item>
            <a-menu-item key="ImportModel">导入对象</a-menu-item>
          </a-menu>
        </a-dropdown>
      </a-col>
      <a-col flex="32px">
        <a-dropdown :trigger="['click']">
          <a-button class="icon-only" title="更多操作"><Icon type="pticon iconfont icon-ptkj-gengduocaozuo"></Icon></a-button>
          <a-menu slot="overlay" @click="handleMenuClick">
            <a-menu-item key="refresh">刷新</a-menu-item>
            <a-sub-menu key="export" title="导出">
              <a-menu-item key="exportAll">全部导出</a-menu-item>
              <a-menu-item key="exportSelected">选择导出</a-menu-item>
            </a-sub-menu>
            <a-menu-item key="dragSort">{{ dragSort ? '关闭' : '开启' }}排序</a-menu-item>
          </a-menu>
        </a-dropdown>
      </a-col>
    </a-row>
    <a-row type="flex" v-show="pleaseSelectExport" style="margin: 12px 0 16px">
      <a-col flex="80px" style="line-height: 32px; padding-left: 18px">
        <a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">全选</a-checkbox>
      </a-col>
      <a-col flex="auto" style="text-align: right">
        <a-button :disabled="exportSelectedKeys.length == 0" @click="beginExportSelected" style="margin-right: 8px">
          <Icon type="pticon iconfont icon-luojizujian-yemiantiaozhuan"></Icon>
          导出选择
        </a-button>
        <a-button @click="quitExportSelect">
          <Icon type="pticon iconfont icon-ptkj-dacha"></Icon>
          退出
        </a-button>
      </a-col>
    </a-row>
    <a-spin :spinning="resourceLoading" style="min-height: 150px">
      <PerfectScrollbar style="height: calc(100vh - 190px); padding-right: 20px; margin-right: -20px">
        <div class="module-res-container" @copy.stop="onCopy">
          <template v-for="(data, i) in dataset">
            <a-row type="flex" class="level-one" :style="{ paddingBottom: '5px' }">
              <a-col flex="100%">
                <a-row type="flex" class="res-item-row btn-ghost-container">
                  <a-col flex="auto" class="res-item-col" @click.native.stop="onClickGroupRow(data.key)">
                    <img
                      class="svg-iconfont"
                      :src="groupOpened.includes(data.key) ? '/static/svg/folder-open.svg' : '/static/svg/folder-close.svg'"
                    />
                    <span :title="data.title" style="width: auto">{{ data.title }}</span>
                    <label style="color: #999; padding-left: 5px">
                      {{ data.children && data.children.length > 0 ? `(${data.children.length})` : '' }}
                    </label>
                  </a-col>
                </a-row>
              </a-col>
              <a-col flex="100%" v-show="groupOpened.includes(data.key)" class="sub-res-item-col">
                <draggable
                  v-model="data.children"
                  :group="{ name: 'res_' + data.key, pull: false }"
                  animation="300"
                  @end="e => dragEnd(e, data.children)"
                  handle=".dm-drag-btn-handler"
                >
                  <template v-for="(p, i) in data.children">
                    <a-row
                      type="flex"
                      v-show="searchWord == undefined || p.name.indexOf(searchWord) != -1"
                      :data-uuid="p.uuid"
                      :class="['res-item-row btn-ghost-container', 'level-two', selectKey == p.uuid ? 'selected btn-hover' : '']"
                      :key="p.uuid"
                      @click.native.stop="selectResource(p, p.uuid, data.key)"
                    >
                      <a-col flex="auto" class="res-item-col">
                        <Icon :type="iconType(data.key)" />
                        <span :title="p.name">
                          <template v-if="searchWord != undefined && p.name.indexOf(searchWord) > -1">
                            {{ p.name.substr(0, p.name.indexOf(searchWord)) }}
                            <span style="color: #f50">{{ searchWord }}</span>
                            {{ p.name.substr(p.name.indexOf(searchWord) + searchWord.length) }}
                          </template>
                          <template v-else>{{ p.name }}</template>
                        </span>
                      </a-col>
                      <a-col flex="110px" class="col-operate">
                        <a-checkbox
                          style="height: 24px"
                          v-if="pleaseSelectExport"
                          :checked="exportSelectedKeys.includes(p.uuid)"
                          @click.stop="() => {}"
                          @change="e => onChangeExportSelect(e, p.uuid)"
                        />
                        <a-dropdown :trigger="['click']" v-else>
                          <a-button ghost type="link" size="small" @click.stop="() => {}" title="更多操作">
                            <Icon :type="p._LOADING ? 'loading' : 'pticon iconfont icon-ptkj-gengduocaozuo'"></Icon>
                          </a-button>
                          <a-menu slot="overlay" @click="e => onResItemClick(e, p, data.children, i)">
                            <a-menu-item key="copy">复制</a-menu-item>
                            <a-menu-item key="export">导出</a-menu-item>
                            <a-menu-item key="delete">删除</a-menu-item>
                          </a-menu>
                        </a-dropdown>
                        <span
                          v-show="dragSort"
                          class="dm-drag-btn-handler ant-btn ant-btn-link ant-btn-sm ant-btn-background-ghost icon-only"
                          @click.stop="() => {}"
                          title="拖动排序"
                        >
                          <Icon type="pticon iconfont icon-ptkj-tuodong" />
                        </span>
                      </a-col>
                    </a-row>
                  </template>
                </draggable>
              </a-col>
            </a-row>
          </template>
        </div>
      </PerfectScrollbar>
    </a-spin>
    <ExportDef :uuid="exportUuid" :type="exportType" ref="exportDef" title="导出对象" />
    <ImportDef ref="importDef" title="导入对象" filterType="dataModel" />
    <Modal
      :title="'复制: ' + copyDataModel.fromName + ' ( ' + copyDataModel.fromId + ')'"
      v-model="copyDataModel.visible"
      :ok="onConfirmCopyDataModel"
    >
      <template slot="content">
        <a-form-model
          :model="copyDataModel"
          :rules="copyRules"
          ref="copyForm"
          :label-col="{ span: 2 }"
          :wrapper-col="{ span: 21 }"
          :colon="false"
        >
          <a-form-model-item prop="name" label="名称">
            <a-input v-model="copyDataModel.name"></a-input>
          </a-form-model-item>
          <a-form-model-item prop="id" label="ID">
            <a-input :maxLength="24" v-model="copyDataModel.id" @change="e => onInputId2CaseFormate(e, 'toUpperCase')"></a-input>
          </a-form-model-item>
        </a-form-model>
      </template>
    </Modal>
    <Modal :title="createResourceTitle" :ok="e => confirmCreate(e)" :width="800" :maxHeight="600" v-model="modalVisible">
      <template slot="content">
        <component
          class="pt-form"
          v-if="createResourceType != undefined"
          :is="createResourceType"
          ref="createResource"
          :key="createResourceKey"
          :type="dataModelType"
        />
      </template>
    </Modal>
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
import CreateDataModel from './create-data-model.vue';
import { orderBy, debounce } from 'lodash';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
export default {
  name: 'ModuleDataModelResource',
  inject: ['currentModule'],
  props: {},
  components: {
    ExportDef,
    ImportDef,
    draggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable'),
    CreateDataModel,
    Modal
  },
  computed: {
    allKeys() {
      let keys = [];
      for (let d of this.dataset) {
        for (let c of d.children) {
          keys.push(c.uuid);
        }
      }
      return keys;
    },
    resSeqMap() {
      let map = {};
      for (let g of this.resSeq) {
        map[g.resUuid] = g.seq;
      }
      return map;
    }
  },
  data() {
    return {
      exportDefVisible: false,
      exportUuid: undefined,
      exportType: 'dataModel',
      dragSort: false,
      createResourceType: undefined,
      modalVisible: false,
      createResourceTitle: '',
      dataModelType: 'TABLE',
      loading: 0,
      selectKey: undefined,
      resSeq: [],
      resourceLoading: true,
      searchWord: undefined,
      createResourceKey: 'CreateDmResourceKey_0',
      dataset: [
        { title: '存储对象', key: 'TABLE', children: [] },
        { title: '视图对象', key: 'VIEW', children: [] }
      ],
      groupOpened: ['TABLE', 'VIEW'],
      pleaseSelectExport: false,
      exportSelectedKeys: [],
      indeterminate: false,
      checkAll: false,
      copyDataModel: {
        name: undefined,
        id: undefined,
        visible: false,
        fromId: undefined
      },
      copyRules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }],
        id: [
          { required: true, message: 'ID必填', trigger: 'blur' },
          { pattern: /^\w+$/, message: 'ID只允许包含字母、数字以及下划线', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.validateIdExist }
        ]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.refresh();
  },
  mounted() {},
  methods: {
    onConfirmCopyDataModel(e) {
      let _this = this;
      this.$refs.copyForm.validate(pass => {
        if (pass) {
          _this.$loading('复制中...');
          $axios
            .get(`/proxy/api/dm/getDetails`, { params: { uuid: _this.copyDataModel.uuid } })
            .then(({ data }) => {
              if (data.code == 0) {
                let fromSource = data.data;
                fromSource.uuid = undefined;
                fromSource.name = _this.copyDataModel.name;
                fromSource.id = _this.copyDataModel.id;
                $axios
                  .post(`/proxy/api/dm/save`, fromSource)
                  .then(({ data }) => {
                    _this.$loading(false);
                    if (data.code != 0) {
                      _this.$error({
                        title: '复制失败',
                        content: _this.$createElement('div', {
                          domProps: {
                            innerHTML: Array.from(new Set(data.msg.split('\n'))).join('<br>')
                          }
                        })
                      });
                      console.error(data);
                    } else {
                      e(true);
                      _this.$message.success('复制成功');
                      _this.dataset[fromSource.type == 'TABLE' ? 0 : 1].children.push({
                        uuid: data.data,
                        name: fromSource.name,
                        id: fromSource.id,
                        type: fromSource.type
                      });
                      _this.updateSeq(_this.dataset[fromSource.type == 'TABLE' ? 0 : 1].children);
                      //TODO: 复制数据
                      // _this.$confirm({
                      //   title: '是否复制数据',
                      //   onOk() {},
                      //   onCancel() {}
                      // });
                    }
                  })
                  .catch(() => {
                    _this.$message.error('复制失败');
                    _this.$loading(false);
                  });
              } else {
                _this.$loading(false);
              }
            })
            .catch(() => {
              _this.$loading(false);
            });
        }
      });
    },
    getCopyName(sources, originName) {
      let names = [],
        name = originName + ' - 副本';
      for (let p of sources) {
        names.push(p.name);
      }
      let num = 0;
      while (names.includes(name)) {
        name = originName + ` - 副本(${++num})`;
      }
      return name;
    },
    validateIdExist: debounce(function (rule, value, callback) {
      $axios.get(`/proxy/api/dm/exist/${value}`, {}).then(({ data }) => {
        callback(data.data === false ? undefined : 'ID已存在');
      });
    }, 300),

    onInputId2CaseFormate(e, caseType) {
      if (this.copyDataModel.id != undefined) {
        if (caseType === 'toUpperCase' || caseType === 'toLowerCase') {
          // 自动转大写
          this.copyDataModel.id = this.copyDataModel.id[caseType]();
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        }
      }
    },
    onCheckAllChange(e) {
      this.checkAll = e.target.checked;
      this.exportSelectedKeys.splice(0, this.exportSelectedKeys.length);
      if (e.target.checked) {
        this.exportSelectedKeys.push(...this.allKeys);
      }
      this.indeterminate = false;
    },
    quitExportSelect() {
      this.checkAll = false;
      this.indeterminate = false;
      this.pleaseSelectExport = false;
      this.exportSelectedKeys.splice(0, this.exportSelectedKeys.length);
    },
    onChangeExportSelect(e, key) {
      if (e.target.checked) {
        this.exportSelectedKeys.push(key);
      } else {
        this.exportSelectedKeys.splice(this.exportSelectedKeys.indexOf(key), 1);
      }
      this.indeterminate = this.exportSelectedKeys.length && this.exportSelectedKeys.length < this.allKeys.length;
      this.checkAll = this.exportSelectedKeys.length && this.exportSelectedKeys.length == this.allKeys.length;
    },
    beginExportSelected() {
      this.exportUuid = JSON.parse(JSON.stringify(this.exportSelectedKeys));
      this.exportType = 'dataModel';
      this.$refs.exportDef.show();
    },
    refresh: debounce(function () {
      this.resourceLoading = true;
      this.fetchDataModels();
    }, 300),
    onSearchInputChange(e) {
      if (e.target.value == '') {
        this.searchWord = undefined;
      }
    },
    onSearch(v) {
      this.searchWord = v;
      for (let r of this.dataset) {
        if (r.children != undefined) {
          for (let c of r.children) {
            if (c.name.indexOf(this.searchWord) != -1) {
              this.openGroupRow(r.key);
              break;
            }
          }
        }
      }
    },
    fetchDataModels() {
      $axios
        .all([
          $axios.post('/proxy/api/dm/getDataModelsByType', { type: ['TABLE', 'VIEW'], module: [this.currentModule.id] }),
          $axios.get('/proxy/api/app/module/res/seq/list', { params: { moduleId: this.currentModule.id, type: 'dataModel' } })
        ])
        .then(
          $axios.spread((dataModelResponse, resSeqResponse) => {
            let dataModels = dataModelResponse.data.data;
            this.resSeq = resSeqResponse.data.data;
            this.resourceLoading = false;
            if (dataModels) {
              this.dataset[0].children = [];
              this.dataset[1].children = [];
              for (let d of dataModels) {
                d._SEQ = this.resSeqMap[d.uuid];
                if (d.type == 'TABLE') {
                  this.dataset[0].children.push(d);
                } else if (d.type == 'VIEW') {
                  this.dataset[1].children.push(d);
                }
              }
              // 排序
              for (let i = 0, len = this.dataset.length; i < len; i++) {
                this.dataset[i].children = orderBy(this.dataset[i].children, ['_SEQ'], ['asc']);
              }
            }
          })
        );
    },
    deleteDataModel(res, siblings, index) {
      this.$set(res, '_LOADING', true);
      let afterDelete = () => {
        siblings.splice(index, 1);
        this.$message.success('删除成功');
        if (this.selectKey == res.uuid) {
          this.$emit('select', undefined, undefined);
        }
      };
      $axios
        .get(`/proxy/api/dm/${res.type == 'TABLE' ? 'drop' : 'delete'}/${res.type == 'TABLE' ? res.id : res.uuid}`)
        .then(({ data }) => {
          if (data.code == 0) {
            afterDelete.call(this);
          } else {
            this.$message.error('删除数据模型对象失败: ' + data.msg);
          }
          this.$set(res, '_LOADING', false);
        })
        .catch(() => {
          this.$set(res, '_LOADING', false);
        });
    },
    onResItemClick({ item, key }, res, siblings, index) {
      if (key == 'delete') {
        this.$confirm({
          title: '提示',
          content: `确认要删除 [ ${res.name} ] 吗?`,
          onOk: () => {
            this.deleteDataModel(res, siblings, index);
          },
          onCancel: () => {}
        });
      } else if (key == 'copy') {
        this.openCopyDataModelModal(res.uuid, res.name, siblings, res.id);
      } else if (key == 'export') {
        if (res.type == 'TABLE') {
          this.$set(res, '_LOADING', true);
          this.$refs.exportDef.createExportRequest(this.exportType, res.uuid, `数据模型: ${res.name}`).then(() => {
            this.$set(res, '_LOADING', false);
          });
        } else {
          this.exportUuid = res.uuid;
          this.$refs.exportDef.show();
        }
      }
    },
    openCopyDataModelModal(uuid, name, siblings, id) {
      this.copyDataModel.uuid = uuid;
      this.copyDataModel.fromName = name;
      this.copyDataModel.name = this.getCopyName(siblings, name);
      this.copyDataModel.visible = true;
      this.copyDataModel.fromId = id;
      this.copyDataModel.id = undefined;
    },
    iconType(key) {
      if (key == 'TABLE') {
        return '';
      }
      return 'codepen';
    },
    openGroupRow(key) {
      let idx = this.groupOpened.indexOf(key);
      if (idx != -1) {
        this.groupOpened.push(key);
      }
    },
    onClickGroupRow(key) {
      let idx = this.groupOpened.indexOf(key);
      if (idx != -1) {
        this.groupOpened.splice(key, 1);
      } else {
        this.groupOpened.push(key);
      }
    },
    updateSeq(list, type) {
      let seq = [];
      for (let i = 0, len = list.length; i < len; i++) {
        seq.push({
          seq: i + 1,
          resUuid: list[i].uuid,
          moduleId: this.currentModule.id,
          type: 'dataModel'
        });
      }
      if (seq.length) $axios.post('/proxy/api/app/module/res/seq/save', seq).then(() => {});
    },
    dragEnd(e, list) {
      this.updateSeq(list);
    },
    handleExportMenuClick(e) {
      if (e.key == 'exportSelected') {
        this.pleaseSelectExport = true;
      } else if (e.key == 'exportAll') {
        this.exportUuid = JSON.parse(JSON.stringify(this.allKeys));
        this.exportType = 'dataModel';
        this.$refs.exportDef.show();
      }
    },
    handleMenuAddClick(e) {
      if (e.key == 'ImportModel') {
        this.$refs.importDef.show();
      } else {
        this.openCreateResourceModal({
          title: e.item.$el.innerText.trim(),
          type: e.key == 'CreateDataModelView' ? 'CreateDataModel' : e.key
        });
      }
    },
    handleMenuClick(e) {
      if (e.key == 'refresh') {
        this.refresh();
      } else if (e.key == 'exportAll' || e.key == 'exportSelected') {
        this.handleExportMenuClick(e);
      } else if (e.key == 'dragSort') {
        this.dragSort = !this.dragSort;
      }
    },
    openCreateResourceModal({ title, type }) {
      this.modalVisible = true;
      this.createResourceTitle = title;
      this.createResourceType = type;
      this.createResourceKey = 'CreateDmResourceKey_' + new Date().getTime();
      this.dataModelType = title == '新建存储对象' ? 'TABLE' : 'VIEW';
    },
    selectResource(data, key, type) {
      console.log('选择资源: ', arguments);
      this.selectKey = key;
      this.$emit('select', 'DataModelDetail', data);
    },
    onCopy() {
      if (this.selectKey != undefined) {
        for (let i = 0, len = this.dataset.length; i < len; i++) {
          let children = this.dataset[i].children;
          for (let j = 0, jlen = children.length; j < jlen; j++) {
            let res = children[j];
            if (res.uuid == this.selectKey) {
              this.openCopyDataModelModal(res.uuid, res.name, children, res.id);
              return;
            }
          }
        }
      }
    },
    confirmCreate(e) {
      let _this = this;
      this.$refs.createResource.save(data => {
        if (data && data.uuid) {
          e(true);
          let idx = _this.dataModelType == 'TABLE' ? 0 : 1;
          _this.dataset[idx].children.push({
            uuid: data.uuid,
            name: data.name,
            id: data.id,
            type: _this.dataModelType
          });
          _this.updateSeq(_this.dataset[idx].children);
          _this.openGroupRow(_this.dataModelType);
        }
      });
    }
  }
};
</script>
