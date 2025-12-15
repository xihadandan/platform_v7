<template>
  <div class="data-dictionary-detail">
    <Modal
      title="字典扩展属性配置"
      dialogClass="pt-modal"
      :destroyOnClose="true"
      :ok="e => confirmConfigExtAttrs(e)"
      :width="600"
      :maxHeight="500"
      v-if="isEditable"
    >
      <a-button icon="edit" type="link" :style="{ position: 'absolute', top: '12px', left: extAttrLeft }">扩展属性</a-button>
      <template slot="content">
        <DataDictionaryDetailExtAttrs
          ref="extAttrs"
          :dictionaryExtDefinitionJson="dictionaryExtDefinitionJson"
        ></DataDictionaryDetailExtAttrs>
      </template>
    </Modal>
    <a-form-model
      v-if="isEditable"
      :model="dataDictionary"
      :label-col="{ flex: '90px' }"
      :wrapper-col="{ flex: 'auto', style: { marginRight: '28px' } }"
      :rules="rules"
      :colon="false"
      ref="form"
      labelAlign="left"
      class="pt-form pt-form-flex"
    >
      <a-row>
        <a-col span="12">
          <a-form-model-item label="字典名称" prop="name">
            <a-input v-model="dataDictionary.name" :style="moduleSource == 'none' ? 'width:  calc(100% - 84px);margin-right:8px' : ''" />
            <a-tag v-if="moduleSource == 'none'" color="blue" style="margin-top: 8px">通用字典</a-tag>
          </a-form-model-item>
        </a-col>
        <a-col span="12">
          <a-form-model-item label="字典编码" prop="code">
            <a-input v-model="dataDictionary.code" />
          </a-form-model-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col span="12">
          <a-form-model-item label="分类" prop="categoryUuid">
            <a-select v-model="dataDictionary.categoryUuid" show-search :filter-option="filterOption">
              <a-select-option v-for="category in categories" :key="category.uuid">
                {{ category.name }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col span="12">
          <a-form-model-item label="归属" prop="moduleId">
            <a-select
              :disabled="moduleSource != 'all'"
              v-model="dataDictionary.moduleId"
              :options="moduleOptions"
              show-search
              :filter-option="filterOption"
            ></a-select>
          </a-form-model-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col span="24">
          <a-form-model-item label="描述" prop="remark">
            <a-textarea v-model="dataDictionary.remark" :auto-size="{ minRows: 2, maxRows: 6 }" />
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
    <div v-else class="form-diaplay-readonly">
      <a-row>
        <a-col span="12" style="font-weight: bold">
          {{ dataDictionary.name }}
          <a-tag v-if="moduleSource == 'none'" color="blue">通用字典</a-tag>
        </a-col>
        <a-col span="12">字典编码： {{ dataDictionary.code }}</a-col>
      </a-row>
      <a-row>
        <a-col span="12">分类： {{ dataDictionary.categoryName }}</a-col>
        <a-col span="12">归属：{{ dataDictionary.moduleName }}</a-col>
      </a-row>
      <a-row>
        <a-col span="24">描述：{{ dataDictionary.remark }}</a-col>
      </a-row>
    </div>
    <a-row class="data-dictionary-box">
      <a-col span="12">
        <div class="sub-title">字典项</div>
        <a-button block v-if="isEditable" @click="addDictionaryItem" style="margin-bottom: 12px">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加字典项
        </a-button>
        <PerfectScrollbar
          :style="{
            height: 'calc(100vh - 430px)',
            paddingRight: '20px'
          }"
        >
          <a-tree
            ref="itemTree"
            class="ant-tree-directory tree-more-operations"
            :tree-data="treeData"
            show-icon
            default-expand-all
            :expandedKeys.sync="expandedItemTreeKeys"
            :selectedKeys.sync="selectedItemTreeKeys"
            :replaceFields="{ title: 'label' }"
            :draggable="isEditable"
            @select="itemTreeSelected"
            @drop="onItemTreeDrop"
          >
            <template slot="title" slot-scope="scope">
              <span class="title" :title="scope.label">{{ scope.label }}</span>
              <div class="widget-tree-operations">
                <a-button-group class="button-group" v-if="isEditable">
                  <a-button type="link" size="small" title="添加子级" @click.stop="addChildItem(scope.key)">
                    <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  </a-button>
                  <a-button type="link" size="small" title="删除" @click.stop="removeChildItem(scope.key)">
                    <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                  </a-button>
                </a-button-group>
              </div>
            </template>
          </a-tree>
        </PerfectScrollbar>
      </a-col>
      <a-col span="12" class="item-form-container">
        <div class="sub-title w-ellipsis-1" :title="dictionaryItem.label">{{ dictionaryItem.label }}</div>
        <a-form-model
          v-show="selectedItemTreeKeys && selectedItemTreeKeys.length > 0"
          :model="dictionaryItem"
          :label-col="{ span: 7 }"
          :wrapper-col="{ span: 15 }"
          :colon="false"
          labelAlign="left"
          :rules="itemRules"
          ref="itemForm"
          class="pt-form"
          style="--w-pt-form-item-margin-bottom: 4px"
        >
          <a-form-model-item label="字典项" prop="label">
            <a-input v-if="isEditable" v-model="dictionaryItem.label" maxLength="120" placeholder="请输入字典项">
              <template slot="addonAfter">
                <WI18nInput :key="dictionaryItem.key" :target="dictionaryItem" code="label" v-model="dictionaryItem.label" />
              </template>
            </a-input>
            <span v-else>
              {{ dictionaryItem.label }}
            </span>
          </a-form-model-item>
          <a-form-model-item label="字典值" prop="value">
            <a-input v-if="isEditable" v-model="dictionaryItem.value" maxLength="120" placeholder="请输入字典值" />
            <span v-else>
              {{ dictionaryItem.value }}
            </span>
          </a-form-model-item>
          <template v-for="extAttr in dictionaryExtDefinitionJson.extAttrs">
            <a-form-model-item :label="extAttr.attrName" :key="extAttr.id">
              <a-input v-if="isEditable" v-model="dictionaryItem.attrs[extAttr.attrKey]" />
              <span v-else>
                {{ dictionaryItem.attrs[extAttr.attrKey] }}
              </span>
            </a-form-model-item>
          </template>
        </a-form-model>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import DataDictionaryDetailExtAttrs from './data-dictionary-detail-ext-attrs.vue';
import { isEmpty, trim } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  props: {
    dataDictionary: Object,
    categories: {
      type: Array,
      default() {
        return [];
      }
    },
    // 模块来源，'current'当前模块，'all'系统所有模块,‘none’没有模块归属
    moduleSource: {
      type: String,
      default: 'current'
    },
    displayState: {
      type: String,
      default: 'edit' // edit, readonly
    },
    // 扩展属性左偏移位置
    extAttrLeft: {
      type: String,
      default: '80px'
    }
  },
  components: { Modal, DataDictionaryDetailExtAttrs, WI18nInput },
  inject: ['currentModule'],
  data() {
    if (!this.dataDictionary.uuid) {
      this.dataDictionary.moduleId = this.currentModule.id;
    }
    if (!this.dataDictionary.items) {
      this.dataDictionary.items = [];
    }
    return {
      moduleOptions: [],
      rules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }],
        code: [{ required: true, message: '编码必填', trigger: 'blur' }]
      },
      dictionaryItem: {
        label: '',
        value: '',
        attrs: {}
      },
      itemRules: {
        label: [{ required: true, message: '字典项必填', trigger: 'blur' }],
        value: [{ required: true, message: '字典值必填', trigger: 'blur' }]
      },
      dictionaryExtDefinitionJson: JSON.parse(this.dataDictionary.extDefinitionJson || JSON.stringify({ extAttrs: [] })),
      treeData: this.dataDictionary.items,
      expandedItemTreeKeys: [],
      selectedItemTreeKeys: [],
      loading: false
    };
  },
  created() {
    if (this.dataDictionary.uuid) {
      this.loading = true;
      this.loadDictionary();
    }
    if (this.moduleSource == 'current') {
      this.moduleOptions.push({
        label: this.currentModule.name,
        value: this.currentModule.id
      });
    } else if (this.moduleSource == 'all') {
      this.loadModuleOptions();
    }
  },
  computed: {
    isEditable() {
      return this.displayState == 'edit';
    }
  },
  methods: {
    loadDictionary() {
      $axios.get(`/proxy/api/datadict/get/${this.dataDictionary.uuid}`).then(({ data }) => {
        if (data.data) {
          Object.assign(this.dataDictionary, data.data);
          this.dictionaryExtDefinitionJson = JSON.parse(this.dataDictionary.extDefinitionJson || JSON.stringify({ extAttrs: [] }));
          this.treeData = data.data.items;
          this.fillTreeDataKey(this.treeData);
        }
      });
    },
    fillTreeDataKey(treeNodes) {
      treeNodes.forEach(item => {
        if (!item.key) {
          item.key = item.uuid;
        }
        this.fillTreeDataKey(item.children || []);
      });
    },
    loadModuleOptions() {
      let _this = this;
      $axios
        .post('/common/select2/query', {
          serviceName: 'appModuleMgr',
          queryMethod: 'loadSelectData',
          idProperty: 'id',
          includeSuperAdmin: 'true'
        })
        .then(({ data }) => {
          if (data.results) {
            this.moduleOptions.length = 0;
            data.results.forEach(item => {
              this.moduleOptions.push({
                label: item.text,
                value: item.id
              });
            });
          }
        });
    },
    confirmConfigExtAttrs(e) {
      if (this.$refs.extAttrs.validate()) {
        this.$refs.extAttrs.confirm();
        this.dataDictionary.extDefinitionJson = JSON.stringify(this.$refs.extAttrs.confirm());
        e(true);
      }
    },
    itemTreeSelected(selectedKeys) {
      if (selectedKeys.length === 0) {
        this.dictionaryItem = {
          attrs: {}
        };
      } else {
        this.dictionaryItem = this.findDictionaryItemByKey(selectedKeys[0]);
        if (this.dictionaryItem.i18ns) {
          let i18n = {};
          for (let item of this.dictionaryItem.i18ns) {
            if (i18n[item.locale] == undefined) {
              i18n[item.locale] = {};
            }
            i18n[item.locale][item.dataCode] = item.content;
          }
          this.dictionaryItem.i18n = i18n;
        }
        if (this.dictionaryItem.attrs == null) {
          this.dictionaryItem.attrs = {};
        }
      }
      this.selectedItemTreeKeys = selectedKeys;
    },
    onItemTreeDrop(info) {
      const dragKey = info.dragNode.eventKey;
      const dropKey = info.node.eventKey;
      const dropPos = info.node.pos.split('-');
      const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
      const dropToBottom = dropPosition == 1;
      if (dragKey == dropKey) {
        return;
      }
      this.moveItemNode(dragKey, dropKey, info.dropToGap, dropToBottom);
    },
    moveItemNode(fromId, toId, sibling, dropToBottom) {
      const _this = this;
      let treeData = this.treeData;
      // 删除并返回源节点数据
      let deleteFromNode = nodes => {
        let deletedNode = null;
        if (nodes == null) {
          return deletedNode;
        }
        for (let index = 0; index < nodes.length; index++) {
          let node = nodes[index];
          if (node.key == fromId) {
            nodes.splice(index, 1);
            return node;
          }
          // 子节点
          deletedNode = deleteFromNode(node.children);
          if (deletedNode) {
            return deletedNode;
          }
        }
        return deletedNode;
      };
      // 在目标节点插入源节点数据
      let appendFromNode = (fromNode, nodes) => {
        if (nodes == null) {
          return false;
        }
        for (let index = 0; index < nodes.length; index++) {
          let node = nodes[index];
          if (node.key == toId) {
            node.children = node.children || [];
            if (sibling) {
              nodes.splice(dropToBottom ? index + 1 : index, 0, fromNode);
            } else {
              node.children.push(fromNode);
            }
            return true;
          }
          // 子节点
          if (appendFromNode(fromNode, node.children)) {
            return true;
          }
        }
        return false;
      };

      let fromNode = deleteFromNode(treeData);
      if (fromNode) {
        appendFromNode(fromNode, treeData);
      } else {
        console.error('from node not found', fromId);
      }
    },
    addDictionaryItem() {
      this.dictionaryItem = {
        key: generateId(),
        label: '',
        value: '',
        attrs: {},
        children: []
      };
      this.dataDictionary.items.push(this.dictionaryItem);
      this.selectedItemTreeKeys = [this.dictionaryItem.key];
    },
    addChildItem(key) {
      let item = this.findDictionaryItemByKey(key);
      let children = item.children || [];
      item.children = children;

      this.dictionaryItem = {
        key: generateId(),
        label: '子级字典项',
        value: '子级字典值',
        children: []
      };
      children.push(this.dictionaryItem);
      if (!this.expandedItemTreeKeys.includes(key)) {
        this.expandedItemTreeKeys.push(key);
      }
      this.selectedItemTreeKeys = [this.dictionaryItem.key];
    },
    removeChildItem(key) {
      let removeTreeNode = nodes => {
        nodes.forEach((node, index) => {
          if (node.key == key) {
            nodes.splice(index, 1);
            return;
          }
          removeTreeNode(node.children || []);
        });
      };
      removeTreeNode(this.dataDictionary.items);

      if (this.dictionaryItem.key == key) {
        this.dictionaryItem = {
          attrs: {}
        };
      }
      this.selectedItemTreeKeys = this.selectedItemTreeKeys.filter(selected => selected != key);
    },
    findDictionaryItemByKey(key) {
      let item = null;
      let findTreeNode = nodes => {
        if (item) {
          return;
        }
        nodes.forEach(node => {
          if (node.key == key) {
            item = node;
            return;
          }
          findTreeNode(node.children || []);
        });
      };
      findTreeNode(this.dataDictionary.items);

      return item;
    },
    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    submit(callback) {
      const _this = this;
      // 字典项值非空、唯一性验证
      let checkEmpty = function (items, reject) {
        for (let index = 0; index < items.length; index++) {
          let item = items[index];
          if (isEmpty(trim(item.value))) {
            _this.$message.error('字典值必填');
            reject();
            return false;
          }
          let result = checkEmpty(item.children || [], reject);
          if (!result) {
            return result;
          }
        }
        return true;
      };
      let itemValueMap = new Map();
      let checkUnique = function (items, reject) {
        for (let index = 0; index < items.length; index++) {
          let item = items[index];
          let itemValue = trim(item.value);
          if (itemValueMap.has(itemValue)) {
            _this.$message.error(`重复的字典值[${itemValue}]`);
            reject();
            return false;
          }
          itemValueMap.set(itemValue, itemValue);

          if (item.i18n) {
            let i18ns = [];
            for (let locale in item.i18n) {
              for (let key in item.i18n[locale]) {
                if (item.i18n[locale][key]) {
                  i18ns.push({
                    locale: locale,
                    content: item.i18n[locale][key],
                    dataCode: 'label'
                  });
                }
              }
            }
            item.i18ns = i18ns;
          }

          let result = checkUnique(item.children || [], reject);
          if (!result) {
            return result;
          }
        }
        return true;
      };

      let itemPromise = _this.selectedItemTreeKeys && _this.selectedItemTreeKeys.length > 0 ? [_this.$refs.itemForm.validate()] : [];
      Promise.all([_this.$refs.form.validate(), ...itemPromise])
        .then(() => {
          let itemValueRequiredPromise = new Promise((resolve, reject) => {
            let items = _this.dataDictionary.items || [];
            if (checkEmpty(items, reject)) {
              resolve();
            }
          });
          return itemValueRequiredPromise;
        })
        .then(() => {
          let itemValueUniquePromise = new Promise((resolve, reject) => {
            let items = _this.dataDictionary.items || [];
            if (checkUnique(items, reject)) {
              resolve();
            }
          });
          return itemValueUniquePromise;
        })
        .then(() => {
          $axios
            .post(`/proxy/api/datadict/save`, this.dataDictionary)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('保存成功！');
                if (callback) {
                  callback.call(_this, data);
                }
              } else {
                _this.$message.success('保存失败！');
              }
            })
            .catch(({ response }) => {
              if (response.data && response.data.msg) {
                _this.$message.error(response.data.msg);
              } else {
                _this.$message.error('保存失败！');
              }
            });
        });
    }
  }
};
</script>

<style lang="less">
.data-dictionary-detail {
  .form-diaplay-readonly {
    .ant-col {
      margin: 8px 0;
    }
  }
  .item-form-container {
    padding-top: 3.5em;
  }

  .data-dictionary-box {
    border: 1px solid var(--w-border-color-light);
    border-radius: 4px;
    margin-right: 28px;
    margin-top: 4px;
    .sub-title {
      font-weight: bold;
      font-size: var(--w-font-size-lg);
      line-height: 32px;
      color: var(--w-text-color-dark);
      margin-bottom: 4px;
    }
    > div {
      padding: 12px 20px;
      min-height: 400px;

      &:first-child {
        border-right: 1px solid var(--w-border-color-light);
      }
    }
  }

  .ant-tree-directory.tree-more-operations {
    --w-tree-title-offset-width: 60px;
    li .ant-tree-node-content-wrapper {
      --w-tree-node-content-wrapper-display: inline;
    }
    .widget-tree-operations {
      float: right;
      position: relative;

      > div {
        position: absolute;
        right: var(--w-padding-2xs);
      }
    }
  }
}
</style>
