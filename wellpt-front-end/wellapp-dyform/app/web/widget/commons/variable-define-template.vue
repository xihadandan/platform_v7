<template>
  <div class="variable-define-template">
    <span v-if="editable && triggerType == 'custom'" v-on:[eventName]="onTrigger">
      <slot name="trigger"></slot>
    </span>
    <a-input :value="valueStringWatched" :readOnly="true" v-show="!triggerType">
      <Icon v-if="editable" slot="addonAfter" type="iconfont icon-ptkj-shezhi" title="编辑" @click.stop="modalVisible = true" />
    </a-input>
    <a-modal
      :title="title"
      :bodyStyle="{ maxHeight: maxHeight, 'overflow-y': 'auto' }"
      :width="900"
      :visible="modalVisible"
      :mask="true"
      :centered="false"
      :maskClosable="false"
      :destroyOnClose="true"
      @cancel="onConfirmCancel"
      @ok="onConfirmOk"
      :closable="true"
      class="pt-modal"
      ref="modalEle"
    >
      <slot name="modalTop"></slot>
      <div v-if="showSnapshot && valueExpression" class="pt-tip-block" style="margin-bottom: 12px">
        {{ valueExpression }}
      </div>
      <a-row type="flex" :style="{ 'flex-flow': 'row nowrap' }" :gutter="0" class="variable-define-row">
        <a-col flex="376px" style="border-right: 1px solid var(--w-border-color-light)">
          <div class="variable-tree-container">
            <div class="flex f_x_s f_y_c">
              <div class="col-title">选项</div>
              <a-input-search :style="{ width: '200px' }" @change="onSearchChange" />
            </div>
            <a-tree
              :tree-data="treeData"
              :load-data="loadTreeData"
              @select="onSelectTreeNode"
              :expanded-keys="expandedKeys"
              :auto-expand-parent="autoExpandParent"
              @expand="onExpand"
            >
              <template slot="title" slot-scope="{ title }">
                <span v-if="title.indexOf(searchValue) > -1">
                  {{ title.substr(0, title.indexOf(searchValue)) }}
                  <span style="color: #f50">{{ searchValue }}</span>
                  {{ title.substr(title.indexOf(searchValue) + searchValue.length) }}
                </span>
                <span v-else>{{ title }}</span>
              </template>
            </a-tree>
          </div>
        </a-col>
        <a-col flex="auto">
          <div class="col-title">
            点击下面区域可输入文本
            <a-button style="float: right" type="link" @click="onClearAll" v-show="showClear" title="清空">
              <Icon type="iconfont icon-ptkj-dacha" />
            </a-button>
          </div>
          <div class="variable-textarea-container" @click.stop="onClickVarTextarea">
            <div
              class="variable-textarea"
              @click.stop="onClickVarTextarea"
              @mouseenter="focusTextarea = true"
              @mouseleave="focusTextarea = false"
            >
              <div class="variable-tag-rendered">
                <ul>
                  <template v-for="(tag, i) in valueTags">
                    <template v-if="tag.edit">
                      <li :key="i" class="tag" :data-index="i">
                        <span contenteditable="true" @blur="e => onBlurEditTagSpan(e, i)" class="focus-edit-span"></span>
                      </li>
                    </template>
                    <template v-else>
                      <li :key="i" class="tag" :data-index="i" @click.stop="onClickTagSpan(tag)">
                        <span>
                          <a-icon v-if="tag.edit != undefined" type="edit" />
                          {{ tag.label }}
                          <a-icon type="close" @click.stop="onCloseTag(i)" />
                        </span>
                      </li>
                    </template>
                  </template>
                </ul>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>
<style lang="less" scoped>
.variable-define-template {
  > .ant-input-group-wrapper {
    top: -2px;
  }
}
.variable-define-row {
  border: 1px solid var(--w-border-color-light);
  border-radius: var(--w-border-radius-2);
  > .ant-col {
    &:first-child {
      border-right: 1px solid var(--w-border-color-light);
    }
    padding: var(--w-padding-2xs) var(--w-padding-xs);

    .col-title {
      line-height: 32px;
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-darker);
      font-weight: bold;
    }
  }
}

.variable-tree-container {
  float: left;
  width: 370px;
  .ant-tree {
    height: 410px;
    overflow-y: auto;
    overflow-x: hidden;
  }
  .ant-tree li .ant-tree-node-content-wrapper {
    width: 370px;
    word-break: break-all;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.variable-textarea-container {
  height: 410px;
  overflow-y: auto;
  width: 100%;

  .variable-textarea {
    position: relative;
    width: 100%;
    display: inline-block;
    box-sizing: border-box;
    outline: none;
    min-height: 32px;
    cursor: text;
    zoom: 1;

    .clearAll {
      top: 8px;
      position: absolute;
      right: 13px;
    }

    .variable-tag-rendered {
      display: block;
      margin-right: 11px;
      line-height: 30px;
      height: auto;
      margin-left: 5px;

      ul {
        margin: 0;
        padding: 0;
        list-style: none;

        li {
          margin-top: var(--w-padding-2xs);
          margin-right: var(--w-padding-2xs);
          line-height: 1;
          float: left;
          max-width: 99%;
          overflow: hidden;
          color: var(--w-text-color-dark);
          cursor: default;

          span {
            line-height: 22px;
            background-color: #fafafa;
            border: 1px solid var(--w-border-color-base);
            border-radius: var(--w-border-radius-2);
            display: inline-block;
            min-width: 2px;
            padding-left: var(--w-padding-2xs);
            padding-right: var(--w-padding-2xs);

            > i {
              font-size: 10px;
            }
          }

          span:focus {
            background-color: unset;
            border: none;
            outline: none;
            box-shadow: none;
          }
        }
      }
    }
  }
}
</style>
<script type="text/babel">
import { debounce } from 'lodash';
export default {
  name: 'VariableDefineTemplate',
  mixins: [],
  props: {
    value: Object,
    editable: {
      type: Boolean,
      default: true
    },
    enableSysVar: {
      // 启用系统参数变量
      type: Boolean,
      default: true
    },
    // 变量树
    variableTreeData: Array,
    // 显示表达式快照
    showSnapshot: {
      type: Boolean,
      default: false
    },
    // 异步加载变量数据
    loadTreeData: Function,
    // 自定义触发事件
    triggerType: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: '设置'
    },
    trigger: {
      type: String,
      default: 'click'
    },
    height: {
      type: String,
      default: ''
    },
    valueExpressionIsLable: {
      type: Boolean,
      default: false
    },
    valueStringIsLable: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      modalVisible: false,
      sysVarTreeData: [],
      defalutValueTags: [],
      defaultValueString: '',
      valueTags: [],
      valueString: '',
      insertIndex: -1,
      insertPosition: null,
      expandedKeys: [],
      autoExpandParent: false,
      searchValue: '',
      focusTextarea: false,
      valueStringWatched: ''
    };
  },

  beforeCreate() {},
  components: {},
  watch: {
    valueString: {
      immediate: true,
      handler(value) {
        let valueString = '';
        if (value) {
          if (this.valueStringIsLable) {
            this.valueTags.map(item => {
              valueString += item.label;
            });
          } else {
            valueString = value;
          }
        }
        this.valueStringWatched = valueString;
      }
    }
  },
  computed: {
    showClear() {
      return this.valueTags.length > 0;
    },
    treeData() {
      let data = [].concat(this.sysVarTreeData);
      if (this.variableTreeData != undefined && this.variableTreeData.length > 0) {
        data = data.concat(this.variableTreeData);
      }
      return data;
    },
    treeDataList() {
      return this.toList(this.treeData);
    },
    valueExpression() {
      let v = [];
      for (let i = 0, len = this.valueTags.length; i < len; i++) {
        if (this.valueExpressionIsLable) {
          v.push('${' + this.valueTags[i]['label'] + '}');
        } else {
          v.push(this.valueTags[i].value);
        }
      }
      return v.join('');
    },
    eventName() {
      return this.trigger;
    },
    maxHeight() {
      if (this.height && this.height.indexOf('px') > -1) {
        return this.height;
      }
      return '600px';
    }
  },
  created() {
    if (this.value != null) {
      this.update(this.value);
    }
  },
  methods: {
    onClearAll() {
      this.valueTags = [];
      this.valueString = '';
    },
    onExpand(expandedKeys) {
      this.expandedKeys = expandedKeys;
      this.autoExpandParent = false;
    },

    getParentKey(key, tree) {
      let parentKey;
      for (let i = 0; i < tree.length; i++) {
        let node = tree[i];
        if (node.children) {
          if (node.children.some(item => item.key === key)) {
            parentKey = node.key;
          } else if (this.getParentKey(key, node.children)) {
            parentKey = this.getParentKey(key, node.children);
          }
        }
      }
      return parentKey;
    },
    toList(data) {
      let dataList = [];
      for (let i = 0; i < data.length; i++) {
        const node = data[i];
        const key = node.key;
        dataList.push({ key, title: key });
        if (node.children) {
          dataList = dataList.concat(this.toList(node.children));
        }
      }
      return dataList;
    },
    onSearchChange: debounce(function (e) {
      let value = e.target.value,
        _this = this,
        expandedKeys = [];
      if (value.trim().length > 0) {
        expandedKeys = this.treeDataList
          .map(item => {
            if (item.title.indexOf(value) > -1) {
              return _this.getParentKey(item.key, this.treeData);
            }
            return null;
          })
          .filter((item, i, self) => item && self.indexOf(item) === i);
      }
      Object.assign(this, {
        expandedKeys,
        searchValue: value,
        autoExpandParent: value && expandedKeys.length > 0
      });
    }, 600),

    onConfirmCancel() {
      this.valueTags = JSON.parse(JSON.stringify(this.defalutValueTags));
      this.valueString = this.defaultValueString;
      this.modalVisible = false;
    },
    onConfirmOk() {
      let v = [];
      for (let i = 0, len = this.valueTags.length; i < len; i++) {
        v.push(this.valueTags[i].value);
      }
      v = v.join('');
      this.modalVisible = false;
      this.valueString = v;
      this.defalutValueTags = JSON.parse(JSON.stringify(this.valueTags));
      this.defaultValueString = v;

      this.$emit('input', {
        variables: this.valueTags,
        value: v
      });
    },
    onClickTagSpan(tag) {
      if (tag.hasOwnProperty('edit')) {
        tag.edit = true;
        this.$nextTick(function () {
          let editSpanEle = this.$refs.modalEle.$el.querySelector('.focus-edit-span');
          editSpanEle.innerText = tag.label;
          editSpanEle.focus();
          // 光标后移
          let range = window.getSelection();
          range.selectAllChildren(editSpanEle);
          range.collapseToEnd();
        });
      }
    },
    onBlurEditTagSpan(e, i) {
      let text = e.target.innerText.trim();
      if (text.length) {
        this.valueTags[i].label = text;
        this.valueTags[i].value = text;
        this.valueTags[i].edit = false;
        if (this.showSnapshot) {
          this.valueTags = [...this.valueTags];
        }
      } else {
        this.valueTags.splice(i, 1); // 移除
      }
    },
    onClickVarTextarea(e) {
      if (e.target.nodeName !== 'DIV') {
        return;
      }
      // 定位tag位置
      let tags = document.querySelectorAll('.tag'),
        position = null,
        index = null;

      if (tags.length === 0) {
        index = -1;
        position = 'After';
      }

      for (let i = 0, len = tags.length; i < len; i++) {
        let tag = tags[i],
          tag_x = tag.offsetLeft,
          tag_y = tag.offsetTop,
          offsetX = e.offsetX,
          offsetY = e.offsetY;

        if (offsetY >= tag_y && offsetY <= tag_y + tag.clientHeight) {
          if (offsetX < tag_x) {
            position = 'Before';
            index = parseInt(tag.dataset.index);
            break;
          } else {
            position = 'After';
            if (tag_x + tag.clientWidth + 20 >= offsetX) {
              index = parseInt(tag.dataset.index);
              break;
            } else {
              // 行末的点击
              index = parseInt(tag.dataset.index);
            }
          }
        }
      }

      if (index != null) {
        if (index === tags.length - 1 && position === 'After') {
          this.valueTags.push({ label: '', edit: true });
        } else {
          this.valueTags.splice(position === 'Before' ? index : index + 1, 0, {
            label: '',
            edit: true
          });
        }

        this.$nextTick(function () {
          this.$refs.modalEle.$el.querySelector('.focus-edit-span').focus();
        });
      }
      this.insertIndex = index || 0;
      this.insertPosition = position || 'After';
    },
    onSelectTreeNode(selectedKeys, e) {
      if (this.insertIndex === -1 || (this.insertIndex == this.valueTags.length - 1 && this.insertPosition === 'After')) {
        this.valueTags.push({
          label: e.node.dataRef.title,
          value: e.node.value
        });
      } else {
        this.valueTags.splice(this.insertPosition === 'Before' ? this.insertIndex : this.insertIndex + 1, 0, {
          label: e.node.dataRef.title,
          value: e.node.value
        });
      }
      this.insertIndex = -1;
    },
    fetchSystemVariables() {
      let _this = this;
      $axios.get('/proxy/api/basicdata/system/param/query', {}).then(({ data }) => {
        if (data.code === 0) {
          let systemVarNode = {
            title: '系统变量',
            key: 'systemVar',
            value: 'systemVar',
            selectable: false,
            children: []
          };
          for (let i = 0, len = data.data.length; i < len; i++) {
            let key = data.data[i].key;
            systemVarNode.children.push({
              title: key,
              key,
              value: '${sys.' + key + '}'
            });
          }
          _this.sysVarTreeData.push(systemVarNode);
        }
      });
    },

    onCloseTag(i) {
      this.valueTags.splice(i, 1);
    },

    update(value) {
      if (value != null) {
        let _value = JSON.parse(JSON.stringify(value));
        this.valueTags = _value.variables || [];
        this.valueString = _value.value;
        this.defalutValueTags = JSON.parse(JSON.stringify(_value.variables || []));
        this.defaultValueString = _value.value;
        // 从字符串解析值标签
        if (this.defaultValueString && !_value.variables) {
          this.valueTags = this.defalutValueTags = this.parseValueTags(this.defaultValueString, value.replaceLabel);
        }
      }
    },

    parseValueTags(value, replaceLabel = {}) {
      let part = '';
      let startFlag = '${';
      let endFlag = '}';
      let variables = [];
      var addVariable = (label, value, edit) => {
        let newLabel = label;
        if (newLabel && replaceLabel) {
          if (replaceLabel.search) {
            newLabel = newLabel.replace(replaceLabel.search, replaceLabel.replace);
          } else if (replaceLabel[value]) {
            newLabel = replaceLabel[value]['label'];
          }
        }
        variables.push({ label: newLabel, value, edit });
      };
      for (let index = 0; index < value.length; index++) {
        let letter = value[index];
        part += letter;
        if (part.endsWith(startFlag)) {
          if (part != startFlag) {
            // 前缀常量
            let prefixVar = part.substring(0, part.length - 2);
            addVariable(prefixVar, prefixVar, false);
            // 变量开始
            part = part.substring(part.length - 2);
          }
        } else if (part.endsWith(endFlag)) {
          if (part.startsWith(startFlag)) {
            addVariable(part.substring(2, part.length - 1), part);
          } else {
            addVariable(part, part, false);
          }
          part = '';
        } else if (index == value.length - 1 && part) {
          // 解析结束
          addVariable(part, part, false);
        }
      }
      return variables;
    },
    onTrigger() {
      this.modalVisible = true;
    }
  },
  beforeMount() {
    if (this.enableSysVar) {
      // 获取系统参数变量
      this.fetchSystemVariables();
    }
  },
  mounted() {}
};
</script>
