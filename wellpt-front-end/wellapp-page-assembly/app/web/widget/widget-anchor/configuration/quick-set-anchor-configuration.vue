<template>
  <div>
    <a-alert message="请选择要添加锚点的布局和组件" showIcon type="info" />

    <a-form-model-item label="布局" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
      <a-tag v-for="(opt, i) in containerWgtVar.selectedOptions" :key="opt.value" closable @close="onCloseTag(opt, containerWgtVar, i)">
        {{ opt.label }}
      </a-tag>
      <a-select
        v-model="containerWgtVar.selected"
        size="small"
        :options="filterContainerWgtOptions"
        v-show="containerWgtVar.showSelect"
        :style="{ width: '120px' }"
        @change="(v, opt) => onSelectWidgetType(v, opt, containerWgtVar)"
      ></a-select>
      <a-icon
        type="plus-square"
        v-show="filterContainerWgtOptions.length > 0 && !containerWgtVar.showSelect"
        @click.stop="
          () => {
            containerWgtVar.showSelect = true;
          }
        "
      />
    </a-form-model-item>
    <a-form-model-item label="组件" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
      <a-tag v-for="(opt, i) in basicWgtVar.selectedOptions" :key="opt.value" closable @close="onCloseTag(opt, basicWgtVar, i)">
        {{ opt.label }}
      </a-tag>
      <a-select
        v-model="basicWgtVar.selected"
        size="small"
        :options="filterBasicWgtOptions"
        v-show="basicWgtVar.showSelect"
        :style="{ width: '120px' }"
        @change="(v, opt) => onSelectWidgetType(v, opt, basicWgtVar)"
      ></a-select>
      <a-icon
        type="plus-square"
        v-show="filterBasicWgtOptions.length > 0 && !basicWgtVar.showSelect"
        @click.stop="
          () => {
            basicWgtVar.showSelect = true;
          }
        "
      />
    </a-form-model-item>
    <a-form-model :model="anchorModel" ref="anchorRef">
      <a-table rowKey="id" :pagination="false" :border="false" :data-source="anchors" :columns="anchorColumns">
        <template slot="hrefSlot" slot-scope="text, record, index">
          <a-select v-model="record.href" :options="anchorScopeWidgetOptions.selectOptions" :style="{ width: '138px' }"></a-select>
        </template>
        <template slot="titleSlot" slot-scope="text, record, index">
          {{ changeTitle(record.title, `title${index}`) }}
          <a-form-model-item :prop="`title${index}`" :rules="anchorRules.title[0]">
            <a-input v-model="record.title" @change="e => changeTitle(e.target.value, `title${index}`)">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :target="record" :designer="designer" :code="record.id" v-model="record.title" />
              </template>
            </a-input>
          </a-form-model-item>
        </template>
        <template slot="parentIdSlot" slot-scope="text, record, index">
          <a-select
            v-model="record.parentId"
            :options="filterRowParentAnchorOptions(record)"
            :style="{ width: '138px' }"
            allowClear
            @change="(v, opt) => onSelectParentAnchor(v, opt, record)"
          />
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button type="link" size="small" @click.stop="onDeleteAnchor(index)" title="删除">
            <Icon type="pticon iconfont icon-ptkj-shanchu" />
          </a-button>
        </template>
      </a-table>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import { deepClone, generateId } from '@framework/vue/utils/util';
import { difference } from 'lodash';
export default {
  name: 'QuickSetAnchorConfiguration',
  inject: ['widgetMeta'],
  props: {
    designer: Object,
    widget: Object,
    anchorScopeWidgetOptions: Object
  },
  data() {
    return {
      anchors: [],
      links: [],
      containerWgtVar: {
        showSelect: false,
        selectedOptions: [],
        selected: undefined
      },
      basicWgtVar: {
        showSelect: false,
        selectedOptions: [],
        selected: undefined
      },
      selectedWtypes: [],
      anchorColumns: [
        {
          title: '关联锚点元素',
          dataIndex: 'href',
          width: 170,
          scopedSlots: { customRender: 'hrefSlot' }
        },
        {
          title: '锚点名称',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titleSlot' }
        },
        {
          title: '父级锚点',
          dataIndex: 'parentId',
          width: 170,
          scopedSlots: { customRender: 'parentIdSlot' }
        },
        {
          title: '',
          width: 73,
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      anchorModel: {},
      anchorRules: { title: [{ required: true, message: '必填', trigger: 'change' }] }
    };
  },
  computed: {
    anchorIdRowMap() {
      let map = {};
      for (let i = 0, len = this.anchors.length; i < len; i++) {
        map[this.anchors[i].id] = this.anchors[i];
      }
      return map;
    },

    rowHrefs() {
      let keys = [];
      for (let i = 0, len = this.anchors.length; i < len; i++) {
        keys.push(this.anchors[i].href);
      }
      return keys;
    },
    containerWgtTagOptions() {
      let options = [];
      for (const key in this.widgetMeta) {
        if (['WidgetFormLayout', 'WidgetTab'].includes(key)) {
          options.push({
            label: this.widgetMeta[key].name,
            value: this.widgetMeta[key].wtype
          });
        }
      }
      return options;
    },
    filterContainerWgtOptions() {
      let opts = this.containerWgtTagOptions.filter(o => !this.selectedWtypes.includes(o.value));
      return opts;
    },
    containerWtypes() {
      let wtypes = [];
      for (let i = 0, len = this.containerWgtTagOptions.length; i < len; i++) {
        wtypes.push(this.containerWgtTagOptions[i].value);
      }
      return wtypes;
    },
    basicWgtTagOptions() {
      const keys = ['WidgetSubform', 'WidgetTable', 'WidgetFormFileUpload', 'WidgetImage'];
      let options = new Array(4);
      for (const key in this.widgetMeta) {
        if (keys.includes(key)) {
          const _index = keys.findIndex(item => item === key);
          options[_index] = {
            label: this.widgetMeta[key].name,
            value: this.widgetMeta[key].wtype
          };
        }
      }
      return options;
    },
    filterBasicWgtOptions() {
      let opts = this.basicWgtTagOptions.filter(o => !this.selectedWtypes.includes(o.value));
      return opts;
    },
    basicWtypes() {
      let wtypes = [];
      for (let i = 0, len = this.basicWgtTagOptions.length; i < len; i++) {
        wtypes.push(this.basicWgtTagOptions[i].value);
      }
      return wtypes;
    }
  },
  beforeMount() {
    if (this.widget.configuration.anchors.length === 0) {
      // 默认带出表单布局、从表、表格、文件上传的锚点组件
      for (let i = 0, len = this.containerWgtTagOptions.length; i < len; i++) {
        let o = this.containerWgtTagOptions[i];
        if (['WidgetFormLayout'].includes(o.value)) {
          this.containerWgtVar.selectedOptions.push(deepClone(o));
          this.selectedWtypes.push(o.value);
        }
      }

      for (let i = 0, len = this.basicWgtTagOptions.length; i < len; i++) {
        let o = this.basicWgtTagOptions[i];
        if (['WidgetSubform', 'WidgetTable', 'WidgetFormFileUpload'].includes(o.value)) {
          this.basicWgtVar.selectedOptions.push(deepClone(o));
          this.selectedWtypes.push(o.value);
        }
      }
      this.updateRows();
    } else {
      // 已经存在锚点数据的情况下：
      // 1. 锚点数据转为表格行数据，并且初始化已选的 tag 选项
      // 2. 新拖拉到锚点域内的组件，生成锚点
      let wtypeIds = deepClone(this.anchorScopeWidgetOptions.wtypeIds);
      let cascadeAdd = ans => {
        if (ans.anchors) {
          for (let i = 0, len = ans.anchors.length; i < len; i++) {
            let suban = ans.anchors[i];
            this.anchors.push({
              id: suban.id,
              href: suban.href,
              title: suban.label,
              parentId: ans.id
            });

            if (this.designer.widgetTreeMap[suban.href]) {
              let wtype = this.designer.widgetTreeMap[suban.href].wtype;
              // 移除已经添加的组件
              wtypeIds[wtype].splice(wtypeIds[wtype].indexOf(suban.href), 1);
              this.initSelectionTags(suban.href);
            }

            cascadeAdd.call(this, suban);
          }
        }
      };

      for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
        let ans = this.widget.configuration.anchors[i];
        this.anchors.push({
          id: ans.id,
          href: ans.href,
          title: ans.label,
          parentId: undefined
        });
        if (this.designer.widgetTreeMap[ans.href]) {
          let wtype = this.designer.widgetTreeMap[ans.href].wtype;
          // 移除已经添加的组件
          wtypeIds[wtype].splice(wtypeIds[wtype].indexOf(ans.href), 1);
          this.initSelectionTags(ans.href);
        }
        cascadeAdd.call(this, ans);
      }
      for (let t in wtypeIds) {
        if (wtypeIds[t].length && !this.selectedWtypes.includes(t)) {
          this.initSelectionTags(wtypeIds[t][0]);
        }
      }
      this.updateRows();
    }
  },
  methods: {
    changeTitle(value, prop) {
      this.$set(this.anchorModel, prop, value);
    },
    onConfirmOk() {
      this.$refs.anchorRef.validate(valid => {
        if (valid) {
          // 构建锚点数据
          let result = [],
            anchors = deepClone(this.anchors);
          let cascadeChildren = node => {
            for (let i = 0; i < anchors.length; i++) {
              let an = anchors[i];
              if (an.parentId == node.id) {
                let n = { id: an.id, pid: node.id, level: node.level + 1, label: an.title, href: an.href, anchors: [] };
                if (an.i18n != undefined) {
                  n.i18n = deepClone(an.i18n);
                }
                node.anchors.push(n);
                cascadeChildren.call(this, n);
                anchors.splice(i, 1);
                i--;
              }
            }
          };
          for (let i = 0; i < anchors.length; i++) {
            let anchor = anchors[i];
            if (anchor.parentId == null) {
              let node = {
                id: anchor.id,
                pid: undefined,
                level: 1,
                label: anchor.title,
                href: anchor.href,
                anchors: []
              };
              if (anchor.i18n != undefined) {
                node.i18n = deepClone(anchor.i18n);
              }
              result.push(node);
              cascadeChildren.call(this, node);
              anchors.splice(i, 1);
              i--;
            }
          }

          this.widget.configuration.anchors = result;
          this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
        }
      });
    },
    filterRowParentAnchorOptions(record) {
      let options = [];
      for (let i = 0, len = this.anchors.length; i < len; i++) {
        if (this.anchors[i].id !== record.id) {
          options.push({
            label: this.anchors[i].title,
            value: this.anchors[i].id
          });
        }
      }
      return options;
    },

    onSelectWidgetType(value, opt, varOption) {
      if (value) {
        this.selectedWtypes.push(value);
        varOption.showSelect = false;
        varOption.selectedOptions.push({
          label: opt.componentOptions.children[0].text,
          value
        });
        varOption.selected = undefined;
        this.updateRows();
      }
    },
    onCloseTag(delTag, varObj, i) {
      varObj.selectedOptions.splice(i, 1);
      // 删除行
      for (let i = 0; i < this.anchors.length; i++) {
        let wtype = this.designer.widgetTreeMap[this.anchors[i].href].wtype;
        if (wtype === delTag.value) {
          this.anchors.splice(i, 1);
          i--;
        }
      }
      this.selectedWtypes.splice(this.selectedWtypes.indexOf(delTag.value), 1);
    },
    updateRows() {
      // 获取页面的所有组件
      for (let i = 0, len = this.selectedWtypes.length; i < len; i++) {
        let ids = this.anchorScopeWidgetOptions.wtypeIds[this.selectedWtypes[i]];
        if (ids) {
          ids = difference(ids, this.rowHrefs);
          for (let j = 0, jlen = ids.length; j < jlen; j++) {
            this.anchors.push({
              id: generateId(),
              href: ids[j],
              title: this.designer.widgetTreeMap[ids[j]].title,
              parentId: undefined
            });
          }
        }
      }
    },
    onDeleteAnchor(index) {
      this.anchors.splice(index, 1);
    },
    initSelectionTags(id) {
      let wtype = this.designer.widgetTreeMap[id].wtype;
      let opt = this.containerWtypes.includes(wtype) ? 'containerWgtTagOptions' : 'basicWgtTagOptions';
      let varObj = this.containerWtypes.includes(wtype) ? 'containerWgtVar' : 'basicWgtVar';
      for (let i = 0, len = this[opt].length; i < len; i++) {
        let o = this[opt][i];
        const keys = ['WidgetFormLayout', 'WidgetSubform', 'WidgetTable', 'WidgetFormFileUpload'];
        if (keys.includes(o.value) && !this.selectedWtypes.includes(o.value)) {
          this[varObj].selectedOptions.push(deepClone(o));
          this.selectedWtypes.push(o.value);
        }
      }
    },
    onSelectParentAnchor(v, opt, record) {
      // 判断是否形成闭环;
      let ids = [],
        anchorMap = {},
        circle = false;
      for (let i = 0, len = this.anchors.length; i < len; i++) {
        anchorMap[this.anchors[i].id] = this.anchors[i];
      }
      if (record.parentId) {
        let parent = anchorMap[record.parentId];
        while (parent != null) {
          if (ids.includes(parent.id)) {
            circle = true;
            break;
          }
          ids.push(parent.id);
          if (parent.parentId != null) {
            parent = anchorMap[parent.parentId];
          } else {
            parent = null;
          }
        }
        if (circle) {
          this.$message.error('锚点存在闭环情况');
          record.parentId = undefined;
        }
      }
    }
  }
};
</script>
