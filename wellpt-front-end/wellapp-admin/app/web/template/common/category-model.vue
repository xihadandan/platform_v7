<template>
  <div class="category-model-container" :style="{ padding: padding }">
    <div class="flex" v-show="isSearch">
      <div class="f_g_1">
        <a-input-search v-model="searchValue" @change="onChange" allowClear />
      </div>
      <div class="f_s_0" v-if="displayState == 'edit' && allowCreate" style="margin-left: 12px">
        <a-button class="icon-only" @click="onAddClick"><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
      </div>
    </div>
    <Scroll :style="{ height: scrollHeight, marginRight: '-' + paddingArr[1], paddingRight: paddingArr[1] }">
      <a-skeleton active :loading="loading">
        <a-tree
          :tree-data="treeData"
          :default-expanded-keys="[-1]"
          :replaceFields="replaceFields"
          :blockNode="true"
          :class="['ant-tree-directory', 'tree-more-operations']"
          @select="onSelect"
        >
          <template slot="title" slot-scope="scope">
            <Icon
              v-if="showIcon"
              :type="scope.dataRef.icon || defaultIcon"
              :style="{
                color: scope.dataRef.iconColor,
                fontSize: 'var(--w-font-size-base)',
                fontWeight: 'normal',
                verticalAlign: 'middle'
              }"
            />
            <label
              class="title"
              :title="scope[replaceFields.title || 'title']"
              :style="{
                maxWidth: 'calc(100% - 60px)'
              }"
            >
              {{ scope[replaceFields.title || 'title'] }}
            </label>
            <div :style="{ float: 'right' }">
              <a-dropdown v-if="scope.uuid != -1 && displayState == 'edit'">
                <a-button class="tree-ghost-btn" @click.stop="() => {}" size="small" type="text" :style="{ float: 'right' }">
                  <Icon type="iconfont icon-ptkj-gengduocaozuo"></Icon>
                </a-button>
                <a-menu slot="overlay">
                  <a-menu-item @click="onEditClick(scope)">
                    <a-icon type="edit"></a-icon>
                    编辑
                  </a-menu-item>
                  <a-menu-item @click="onDeleteClick(scope)">
                    <a-icon type="delete"></a-icon>
                    删除
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </template>
        </a-tree>
      </a-skeleton>
    </Scroll>
    <a-modal
      :title="modal.title"
      width="700px"
      :visible="modal.visible"
      :destroyOnClose="true"
      @ok="handleCategoryOk"
      @cancel="handleCategoryCancel"
    >
      <a-form-model ref="form" :model="formData" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
        <slot name="formModelItemOfName" :formData="formData">
          <a-form-model-item v-if="hasModelProp('name')" label="名称" prop="name">
            <a-input v-model="formData.name" :maxLength="100">
              <template slot="addonAfter" v-if="applyTo">
                <w-i18n-input :target="formData" code="name" v-model="formData.name" />
              </template>
            </a-input>
          </a-form-model-item>
        </slot>
        <slot name="formModelItemOfId" :formData="formData">
          <a-form-model-item v-if="hasModelProp('id')" label="ID" prop="id">
            <a-input v-model="formData.id" :maxLength="50"></a-input>
          </a-form-model-item>
        </slot>
        <slot name="formModelItemOfCode" :formData="formData">
          <a-form-model-item v-if="hasModelProp('code')" label="编码" prop="code">
            <a-input v-model="formData.code" :maxLength="50"></a-input>
          </a-form-model-item>
        </slot>
        <slot name="formModelItemOfIcon" :formData="formData">
          <a-form-model-item v-if="hasModelProp('icon')" label="图标">
            <WidgetIconLibModal :zIndex="1000" v-model="formData.icon" :onlyIconClass="true">
              <a-button>
                选择图标
                <Icon :type="formData.icon || defaultIcon" />
              </a-button>
            </WidgetIconLibModal>
            <ColorPicker v-model="formData.iconColor"></ColorPicker>
          </a-form-model-item>
        </slot>
        <slot name="formModelItemOfParentUuid" :formData="formData">
          <a-form-model-item v-if="hasModelProp('parentUuid')" label="上级分类">
            <a-tree-select
              v-model="formData.parentUuid"
              style="width: 100%"
              :replaceFields="replaceFields"
              :tree-data="getTreeSelectData(formData.uuid)"
              allow-clear
              show-search
              treeNodeFilterProp="title"
            />
          </a-form-model-item>
        </slot>
        <slot name="formModelItemOfRemark" :formData="formData">
          <a-form-model-item v-if="hasModelProp('remark')" label="备注" prop="remark">
            <a-textarea v-model="formData.remark" />
          </a-form-model-item>
        </slot>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import { deepClone } from '@framework/vue/utils/util';
import { trim } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  props: {
    name: {
      type: String,
      default: '分类'
    },
    // 分类国际化应用
    applyTo: {
      type: String
    },
    allowCreate: {
      type: Boolean,
      default: true
    },
    showIcon: {
      type: Boolean,
      default: true
    },
    defaultIcon: {
      type: String,
      default: 'iconfont icon-ptkj-fenlei'
    },
    loading: Boolean,
    dataSource: [],
    replaceFields: {
      type: Object,
      default() {
        return { title: 'name', key: 'uuid', value: 'uuid' };
      }
    },
    modelData: {
      type: Object,
      default() {
        return {
          name: '',
          code: '',
          icon: '',
          iconColor: '',
          enabled: true,
          sortOrder: 0,
          parentUuid: null,
          remark: '',
          moduleId: null
        };
      }
    },
    modalTypeNames: {
      type: Object,
      default() {
        return { category: '分类' };
      }
    },
    rules: {
      type: Object,
      default() {
        return {
          name: { required: true, message: '不能为空', trigger: 'blur' },
          code: { required: true, message: '不能为空', trigger: 'blur' }
        };
      }
    },
    displayState: {
      type: String,
      default: 'edit' //edit、label
    },
    save: Function,
    delete: Function,
    padding: {
      type: String,
      default: '12px 20px'
    },
    isSearch: {
      type: Boolean,
      default: true
    }
  },
  components: { Modal, WidgetIconLibModal, ColorPicker, WI18nInput },
  data() {
    return {
      searchValue: '',
      treeDataSource: [],
      formData: this.modelData,
      modal: {
        title: '新增分类',
        visible: false
      }
    };
  },
  computed: {
    treeData() {
      return [
        {
          [this.replaceFields.title || 'title']: this.name,
          uuid: -1,
          icon: this.defaultIcon,
          children: this.buildTreeData(deepClone(this.treeDataSource))
        }
      ];
    },
    paddingArr() {
      let paddingArr = ['0px', '0px', '0px', '0px'];
      if (this.padding) {
        let padding = this.padding.split(' ');
        if (padding.length == 1) {
          paddingArr = [padding[0], padding[0], padding[0], padding[0]];
        } else if (padding.length == 2) {
          paddingArr = [padding[0], padding[1], padding[0], padding[1]];
        } else if (padding.length == 3) {
          paddingArr = [padding[0], padding[1], padding[2], padding[1]];
        } else if (padding.length == 4) {
          paddingArr = padding;
        }
      }
      return paddingArr;
    },
    scrollHeight() {
      let height = '100%';
      // if (this.padding) {
      //   height = `${height} - ${this.paddingArr[0]}  - ${this.paddingArr[2]}`;
      // }
      if (this.isSearch) {
        height = `${height} - 32px`;
      }
      return `calc(${height})`;
    }
  },
  watch: {
    dataSource: function (newVal) {
      this.treeDataSource = [...newVal];
    }
  },
  methods: {
    hasModelProp(prop) {
      return this.modelData.hasOwnProperty(prop);
    },
    buildTreeData(dataList) {
      let childNodes = dataList.filter(item => item.parentUuid);
      let movedNodes = [];
      childNodes.forEach(child => {
        let childIndex = dataList.findIndex(item => item.uuid == child.uuid);
        let parentNode = dataList.find(item => item.uuid == child.parentUuid) || movedNodes.find(item => item.uuid == child.parentUuid);
        if (parentNode != null) {
          parentNode.children = parentNode.children || [];
          parentNode.children.push(child);
          if (childIndex != -1) {
            movedNodes = movedNodes.concat(dataList.splice(childIndex, 1));
          }
        }
      });
      return dataList;
    },
    onChange() {
      const _this = this;
      if (!_this.searchValue) {
        _this.treeDataSource = [..._this.dataSource];
        return;
      }

      let nameProp = _this.replaceFields.title || 'title';
      let keyword = trim(_this.searchValue);
      _this.treeDataSource = _this.dataSource.filter(item => item[nameProp] && item[nameProp].indexOf(keyword) != -1);
    },
    onSelect(selectedKeys, e) {
      let keys = selectedKeys.filter(key => key != -1);
      let selectedKeysIncludeChildren = [...keys, ...this.findAllChildKeys(keys)];
      this.$emit('select', keys, selectedKeysIncludeChildren, e);
    },
    findAllChildKeys(keys) {
      if (!keys || keys.length === 0) {
        return [];
      }

      let keyProp = this.replaceFields.key || 'uuid';
      let childKeys = [];
      let extractKeys = treeNodes => {
        treeNodes.forEach(node => {
          childKeys.push(node[keyProp]);
          extractKeys(node.children || []);
        });
      };
      let findNodeKeys = treeNodes => {
        treeNodes.forEach(node => {
          if (keys.includes(node[keyProp])) {
            extractKeys(node.children || []);
          } else {
            findNodeKeys(node.children || []);
          }
        });
      };
      findNodeKeys(this.treeData);
      return childKeys;
    },
    onAddClick() {
      this.formData = deepClone(this.modelData);
      this.modal.title = '新增分类';
      this.modal.visible = true;
      this.$emit('modalAdd', this.modal, this.formData);
    },
    onEditClick(nodeData) {
      this.formData = deepClone(nodeData.dataRef);
      if (this.formData.i18ns) {
        let i18n = {};
        for (let item of this.formData.i18ns) {
          if (i18n[item.locale] == undefined) {
            i18n[item.locale] = {};
          }
          i18n[item.locale][item.code] = item.content;
        }
        this.$set(this.formData, 'i18n', i18n);
      }
      this.modal.title = '编辑分类';
      this.modal.visible = true;
      this.$emit('modalEdit', this.modal, this.formData);
    },
    onDeleteClick(nodeData) {
      const _this = this;
      let typeName = _this.modalTypeNames[nodeData.dataRef.type] || '分类';
      _this.$confirm({
        title: '确认框',
        content: `确定删除${typeName}[${nodeData.dataRef.name}]吗？`,
        onOk() {
          if (_this.delete) {
            _this.delete(nodeData.dataRef);
          } else {
            console.log('没有设置删除处理函数');
          }
        }
      });
    },
    handleCategoryOk() {
      const _this = this;
      _this.$refs.form.validate(valid => {
        if (valid && _this.save) {
          if (_this.formData.i18n && _this.applyTo) {
            let i18ns = [];
            for (let locale in _this.formData.i18n) {
              for (let key in _this.formData.i18n[locale]) {
                if (_this.formData.i18n[locale][key]) {
                  i18ns.push({
                    locale: locale,
                    content: _this.formData.i18n[locale][key],
                    defId: _this.formData.uuid || '',
                    code: key,
                    applyTo: _this.applyTo
                  });
                }
              }
            }
            _this.formData.i18ns = i18ns;
          }
          _this.save(_this.formData, close => {
            _this.modal.visible = close ? false : true;
          });
        }
      });
    },
    handleCategoryCancel() {
      this.modal.visible = false;
    },
    getTreeSelectData(excludeKey) {
      const _this = this;
      let dataList = deepClone(_this.dataSource);
      // 节点本身不可选择
      dataList.forEach(item => {
        if (item.uuid == excludeKey) {
          item.selectable = false;
        }
      });
      return _this.buildTreeData(dataList);
    }
  }
};
</script>

<style lang="less" scoped>
.category-model-container {
  --w-tree-node-content-wrapper-padding: 0 5px 0 0;
}
</style>
