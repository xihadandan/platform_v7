<template>
  <div style="width: 100%; overflow-x: hidden">
    <div style="margin-bottom: 8px">
      <div style="display: flex; align-items: flex-start">
        <!-- <a-checkbox :indeterminate="selectWgtIndeterminate" :checked="selectWgtCheckAll" @change="onCheckAllSelectWgtChange">
          选择组件
        </a-checkbox> -->
        <a-checkable-tag
          :style="{
            margin: '0px 0px 5px 0px',
            backgroundColor: selectWgtCheckAll ? 'var(--w-primary-color)' : 'var(--w-primary-color-1)',
            color: selectWgtCheckAll ? '#fff' : 'var(--w-primary-color)'
          }"
          :checked="selectWgtCheckAll"
          @change="onCheckAllSelectWgtChange"
        >
          <a-icon
            :type="
              selectWgtCheckAll || (selectedWgtIds.length > 0 && selectedWgtIds.length == selectWgtOptions.length)
                ? 'check-square'
                : 'minus-square'
            "
            theme="filled"
          />
          全部
        </a-checkable-tag>
        <a-divider type="vertical" style="height: 26px" />
        <div style="display: flex; width: calc(100% - 100px); flex-wrap: wrap">
          <template v-for="tag in selectWgtOptions">
            <a-checkable-tag
              style="margin-bottom: 5px"
              :key="tag.value"
              :checked="selectedWgtIds.indexOf(tag.value) > -1"
              @change="checked => handleWgtSelectChange(tag, checked)"
            >
              {{ tag.widget.title || tag.widget.name }}
            </a-checkable-tag>
          </template>
        </div>
      </div>
      <a-button size="small" type="link" icon="delete" style="float: right; margin-bottom: 8px" @click="resetForm">重置所有条件</a-button>
    </div>
    <a-table :columns="columns" :data-source="devSourceFilter" rowKey="uuid" :pagination="pagination" @change="handleTableChange">
      <template slot="widgetSlot" slot-scope="text, record">
        <a-button size="small" type="link" @click.stop="selectWidget(record.widget.id)">
          {{ record.widget.title || record.widget.name }}
        </a-button>
      </template>
      <template slot="elementNameSlot" slot-scope="text, record">
        <div>
          <a-tag v-if="record.elementTypeName">{{ record.elementTypeName }}</a-tag>
          {{ text || '-' }}
        </div>
      </template>
      <template slot="triggerNameSlot" slot-scope="text, record">
        {{ record.triggerName || '-' }}
      </template>
      <template slot="actionObjectSlot" slot-scope="text, record">
        <template v-if="text.codeEditor">
          <WidgetCodeEditor @save="e => onChangeCode(e, record)" :value="record.actionSource[text.sourcePath]" :hideError="true">
            <a-button icon="code" type="link" size="small">{{ text.name }}</a-button>
          </WidgetCodeEditor>
        </template>
        <template v-else-if="record.actionType == 'template'">
          <div>
            <Modal title="查看源码" width="1000px">
              <template slot="content">
                <div>
                  <div class="spin-center" v-show="loadingCodeSource">
                    <a-spin />
                  </div>
                  <pre v-show="!loadingCodeSource">{{ codeContent }}</pre>
                  <a-empty description="查无代码" v-show="!loadingCodeSource && codeContent == ''" />
                </div>
              </template>
              <a-button
                type="link"
                size="small"
                @click="viewCode(record.actionSource[record.actionObject.sourcePath], 'vue')"
                style="white-space: pre-wrap; word-break: break-all; text-align: left"
              >
                {{ record.actionSource[record.actionObject.sourcePath] }}
              </a-button>
            </Modal>
          </div>
        </template>
        <template v-else-if="record.actionType == 'jsFunction'">
          <div>
            {{ text.name }}
            <Modal title="查看源码" width="900px">
              <template slot="content">
                <div>
                  <div class="spin-center" v-show="loadingCodeSource">
                    <a-spin />
                  </div>
                  <pre v-html="codeContent" v-show="!loadingCodeSource"></pre>
                  <a-empty description="查无代码" v-show="!loadingCodeSource && codeContent == ''" />
                </div>
              </template>
              <a-button
                type="link"
                size="small"
                @click="viewCode(record.actionSource[record.actionObject.sourcePath], 'js')"
                style="white-space: pre-wrap; word-break: break-all; text-align: left"
              >
                {{ record.actionSource[record.actionObject.sourcePath] }}
              </a-button>
            </Modal>
          </div>
        </template>
        <template v-else-if="record.actionType == 'jsModule'">
          <div v-for="(module, i) in record.actionSource">
            {{ module.label }}
            <Modal title="查看源码" width="900px">
              <template slot="content">
                <div>
                  <div class="spin-center" v-show="loadingCodeSource">
                    <a-spin />
                  </div>
                  <pre v-html="codeContent" v-show="!loadingCodeSource"></pre>
                  <a-empty description="查无代码" v-show="!loadingCodeSource && codeContent == ''" />
                </div>
              </template>
              <a-button
                type="link"
                size="small"
                @click="viewCode(module.key, 'js')"
                style="white-space: pre-wrap; word-break: break-all; text-align: left"
              >
                {{ module.key }}
              </a-button>
            </Modal>
          </div>
        </template>
        <template v-else-if="record.actionType == 'workflow'">
          <a-button
            size="small"
            type="link"
            @click="openUrl('/workflow-designer/index?id=' + record.actionSource[record.actionObject.sourcePath])"
          >
            {{ text.name }}
          </a-button>
        </template>
        <template v-else-if="record.actionType == 'widgetEvent'">
          <a-button size="small" type="link" @click.stop="selectWidget(record.actionSource[record.actionObject.sourcePath])">
            {{ text.refWidget.title }}
          </a-button>
        </template>
        <template v-else-if="record.actionType == 'dataManager'">
          <a-button size="small" type="link" @click.stop="selectWidget(record.actionSource[record.actionObject.sourcePath])">
            {{ text.name }}
          </a-button>
        </template>
        <template v-else-if="record.actionType == 'redirectPage'">
          <a-button
            v-if="record.actionObject.sourcePath == 'pageId'"
            size="small"
            type="link"
            @click="openUrl('/page-designer/index?id=' + record.actionSource[record.actionObject.sourcePath])"
          >
            {{ text.name }}
          </a-button>
          <a v-else>{{ text.name }}</a>
        </template>
        <template v-else>
          {{ text }}
          <!-- {{ text.actionObject.name }} -->
        </template>
      </template>

      <div
        slot="filterActionTypeFilterDropdown"
        slot-scope="{ setSelectedKeys, selectedKeys, confirm, clearFilters, column }"
        style="padding: 8px"
      >
        <a-button
          size="small"
          style="position: absolute; right: 0px; top: 0px"
          type="link"
          @click="filterActionTypeNames.splice(0, filterActionTypeNames.length)"
        >
          重置
        </a-button>
        <div style="display: flex; width: 400px; flex-wrap: wrap; padding-right: 36px">
          <template v-for="(name, i) in actionTypeNames">
            <a-checkable-tag
              style="margin-bottom: 5px"
              :key="'name-' + i"
              @change="checked => handleSelectActNameChange(name, checked)"
              :checked="filterActionTypeNames.indexOf(name) > -1"
            >
              {{ name }}
            </a-checkable-tag>
          </template>
        </div>
      </div>

      <a-icon
        type="filter"
        :theme="filterActionTypeNames.length > 0 ? 'filled' : undefined"
        slot="filterActionTypeIcon"
        :style="{
          color: filterActionTypeNames.length > 0 ? 'var(--w-primary-color)' : undefined
        }"
      />
    </a-table>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'WidgetEvtDevView',
  props: {
    designer: Object
  },
  components: { Drawer, Modal },
  computed: {
    devSourceFilter() {
      let rows = [];
      for (let r of this.devSource) {
        if (
          (this.selectedWgtIds.length == 0 ||
            this.selectedWgtIds.includes(r.widget.id) ||
            (r.actionObject.refWidget != undefined && this.selectedWgtIds.includes(r.actionObject.refWidget.id))) &&
          (this.filterActionTypeNames.length == 0 || this.filterActionTypeNames.includes(r.actionTypeName))
        ) {
          rows.push(r);
        }
      }
      return rows;
    }
  },
  data() {
    return {
      devSource: [],
      pagination: { current: 1, pageSize: 8 },
      actionTypeNames: [],
      filterActionTypeNames: [],
      actionTypeIndeterminate: false,
      selectActionTypeCheckAll: false,
      selectWgtOptions: [],
      selectedWgtIds: [],
      selectWgtIndeterminate: false,
      selectWgtCheckAll: true,
      codeContent: undefined,
      loadingCodeSource: false,
      columns: [
        {
          title: '组件',
          width: 150,
          dataIndex: 'widget',
          key: 'widget',
          scopedSlots: {
            customRender: 'widgetSlot'
            // filterDropdown: 'filterWgtFilterDropdown', filterIcon: 'filterWgtIcon'
          }
        },
        { title: '元素', width: 200, dataIndex: 'elementName', scopedSlots: { customRender: 'elementNameSlot' } },
        { title: '触发方式', width: 150, dataIndex: 'triggerName', scopedSlots: { customRender: 'triggerNameSlot' } },
        { title: '执行对象 / 组件', dataIndex: 'actionObject', key: 'actionObject', scopedSlots: { customRender: 'actionObjectSlot' } },
        {
          title: '执行动作',
          width: 150,
          dataIndex: 'actionTypeName',
          key: 'actionTypeName',
          scopedSlots: { filterDropdown: 'filterActionTypeFilterDropdown', filterIcon: 'filterActionTypeIcon' }
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    if (this.designer.widgets.length != 0) {
      //解析各个组件配置的功能元素、以及生成后端保存的定义信息
      // elementName: btn.title, // 元素名称
      // elementTypeName: '按钮', // 元素类型名称
      // actionType: e.actionType, // 动作类型
      // actionSource: e, // 动作事件源
      // actionTypeName: undefined, // 动作类型名称
      // actionObject: {
      //   // 执行对象
      //   type: 'jsFunction',
      //   name: undefined,
      //   sourcePath: undefined // 对象路径，用于从事件源解析对象值，可以在管理端同步修改对应值
      // }
      this.selectWgtOptions.splice(0, this.selectWgtOptions.length);
      this.selectedWgtIds.splice(0, this.selectedWgtIds.length);
      let seq = 1;
      let actionTypeNames = new Set();
      let wgtIds = [];
      let explain = wgt => {
        if (wgt) {
          let type = `${wgt.wtype}Configuration`;
          if (window.Vue.options.components[type] && window.Vue.options.components[type].options.methods.getWidgetActionElements) {
            let result = window.Vue.options.components[type].options.methods.getWidgetActionElements(wgt, this.designer);
            if (result.length > 0) {
              if (!wgtIds.includes(wgt.id)) {
                wgtIds.push(wgt.id);
                this.selectWgtOptions.push({
                  value: wgt.id,
                  label: wgt.title,
                  widget: wgt
                });
              }
              for (let element of result) {
                let s = { widget: wgt, ...element };
                let { actionSource, actionType, actionObject } = s;
                if (actionType == 'jsFunction') {
                  // 执行脚本
                  if (actionObject.sourcePath == 'jsFunction') {
                    let result = this.explainJsFunctionPath(actionSource[actionObject.sourcePath]);
                    if (result) {
                      actionObject.name = result.name + (result.method ? '.' + result.method : '');
                    }
                  } else if (actionObject.sourcePath == 'customScript') {
                    actionObject.name = '自定义代码';
                  }
                  s.actionTypeName = 'javaScript 脚本';
                } else if (actionType == 'jsModule') {
                  s.actionTypeName = 'javaScript 脚本';
                } else if (actionType == 'dataManager') {
                  // 数据管理
                  let dmsId = actionSource[actionObject.sourcePath],
                    script = actionSource.action;
                  s.actionObject.name = this.designer.widgetIdMap[dmsId].title;
                  s.actionObject.refWidget = this.designer.widgetIdMap[dmsId];
                  let result = this.explainJsFunctionPath(script);
                  if (script) {
                    s.actionTypeName = result.method;
                  }

                  if (!wgtIds.includes(s.actionObject.refWidget.id)) {
                    wgtIds.push(s.actionObject.refWidget.id);
                    this.selectWgtOptions.push({
                      widget: s.actionObject.refWidget,
                      value: s.actionObject.refWidget.id,
                      label: s.actionObject.refWidget.title
                    });
                  }
                } else if (actionType == 'workflow') {
                  // 工作流
                  s.actionTypeName = '发起流程';
                } else if (actionType == 'redirectPage') {
                  // 页面跳转
                  s.actionTypeName = '打开页面';
                } else if (actionType == 'widgetEvent') {
                  // 组件事件
                  let wid = actionSource[actionObject.sourcePath];
                  let wevents = this.designer.widgetDefaultEvents[wid];
                  if (wevents) {
                    for (let i = 0, len = wevents.length; i < len; i++) {
                      if (wevents[i].id == actionSource.eventId) {
                        s.actionTypeName = wevents[i].title;
                        break;
                      }
                    }
                  } else {
                    s.actionTypeName = '组件事件';
                    let widget = this.designer.widgetIdMap[wid];
                    let defineEvents = widget.configuration.defineEvents;
                    if (defineEvents) {
                      for (let d of defineEvents) {
                        if (d.id == actionSource.eventId) {
                          s.actionTypeName = d.title;
                          break;
                        }
                      }
                    }
                  }

                  s.actionObject.name = this.designer.widgetIdMap[wid].title;
                  s.actionObject.refWidget = this.designer.widgetIdMap[wid];
                  if (!wgtIds.includes(s.actionObject.refWidget.id)) {
                    wgtIds.push(s.actionObject.refWidget.id);
                    this.selectWgtOptions.push({
                      widget: s.actionObject.refWidget,
                      value: s.actionObject.refWidget.id,
                      label: s.actionObject.refWidget.title
                    });
                  }
                } else if (actionType == 'template') {
                  s.actionTypeName = '模板渲染';
                }
                s.uuid = seq++;
                this.devSource.push(s);
                actionTypeNames.add(s.actionTypeName || s.actionType);
              }
            }
          }
        }
      };
      for (let id in this.designer.widgetIdMap) {
        explain(this.designer.widgetIdMap[id]);
      }

      console.log('devSource', this.devSource);
      this.actionTypeNames = Array.from(actionTypeNames);
      // this.filterActionTypeNames.push(...this.actionTypeNames);
      this.selectWgtCheckAll = true;
      this.selectActionTypeCheckAll = true;
      // if (this.selectedWgtIds.length > 0) {
      //   this.selectWgtCheckAll = true;
      // }
    }
    // document.querySelector('#design-main').style.zIndex = 1;
  },
  methods: {
    resetForm() {
      this.selectedWgtIds.splice(0, this.selectedWgtIds.length);
      this.selectWgtCheckAll = true;
      this.filterActionTypeNames.splice(0, this.filterActionTypeNames.length);
    },
    handleTableChange(pagination) {
      this.pagination.current = pagination.current;
    },
    onCheckAllSelectWgtChange(e) {
      if (!this.selectWgtCheckAll) {
        this.selectWgtCheckAll = true;
        this.selectedWgtIds.splice(0, this.selectedWgtIds.length);
      } else {
        this.selectWgtCheckAll = true;
      }

      // this.selectedWgtIds.splice(0, this.selectedWgtIds.length);
      // this.selectWgtIndeterminate = false;
      // if (e.target.checked) {
      //   for (let s of this.selectWgtOptions) {
      //     this.selectedWgtIds.push(s.value);
      //   }
      // }

      // this.selectWgtCheckAll = e.target.checked;
    },
    onCheckAllActionTypeChange(e) {
      this.filterActionTypeNames.splice(0, this.filterActionTypeNames.length);
      this.actionTypeIndeterminate = false;
      if (e.target.checked) {
        for (let s of this.actionTypeNames) {
          this.filterActionTypeNames.push(s);
        }
      }
      this.selectActionTypeCheckAll = e.target.checked;
    },
    handleSelectActNameChange(name) {
      let i = this.filterActionTypeNames.indexOf(name);
      if (i == -1) {
        this.filterActionTypeNames.push(name);
      } else {
        this.filterActionTypeNames.splice(i, 1);
      }
      this.actionTypeIndeterminate =
        this.filterActionTypeNames.length > 0 && this.filterActionTypeNames.length < this.actionTypeNames.length;
      this.selectActionTypeCheckAll =
        this.filterActionTypeNames.length > 0 && this.filterActionTypeNames.length == this.actionTypeNames.length;
    },
    handleWgtSelectChange(item) {
      let i = this.selectedWgtIds.indexOf(item.value);
      if (i == -1) {
        this.selectedWgtIds.push(item.value);
      } else {
        this.selectedWgtIds.splice(i, 1);
      }
      this.selectWgtCheckAll = this.selectedWgtIds.length == 0;
      // this.selectWgtIndeterminate = this.selectedWgtIds.length > 0 && this.selectedWgtIds.length < this.selectWgtOptions.length;
      // this.selectWgtCheckAll = this.selectedWgtIds.length > 0 && this.selectedWgtIds.length == this.selectWgtOptions.length;
    },
    onChangeCode(e, item) {
      // console.log(arguments);
      item.actionSource[item.actionObject.sourcePath] = e;
    },
    explainJsFunctionPath(jsPath) {
      let parts = jsPath.split('.');
      let script = this.__developScript[parts[0]];
      if (script) {
        let meta = script.default.prototype.META;
        if (parts.length > 1) {
          for (let h in meta.hook) {
            if (h == parts[1]) {
              return {
                name: meta.name,
                method: meta.hook[h]
              };
            }
          }
        }
      }
      return undefined;
    },

    selectWidget(id) {
      this.designer.selectedByID(id);
      // document.querySelector('#design-main').style.zIndex = 1000;
    },
    openUrl(url) {
      window.open(url, '_blank');
    },
    drawerContainer() {
      return this.$el;
    },
    viewCode(fileName, fileType = 'js') {
      let parts = fileName.split('.');
      let file = fileType == 'js' ? fileName.split('.')[0] : fileName;
      this.loadingCodeSource = true;
      this.codeContent = '';
      this.$axios
        .get(`/web/resource/viewCodeSource`, {
          params: {
            fileName: file,
            fileType,
            methodName: parts.length == 2 ? parts[1] : undefined
          }
        })
        .then(({ data }) => {
          if (data) {
            let content = data.content;
            if (data.line) {
              // 定义到函数块
              let lines = content.split('\n');
              lines[data.line[0] - 1] = `<div style="color: #000;font-weight: bold;background: #e5ffec;border-radius: 4px;padding:5px;">${
                lines[data.line[0] - 1]
              }`;
              lines[data.line[1] - 1] = `${lines[data.line[1] - 1]}</div>`;

              content = lines.join('\n');
            }
            this.codeContent = content;
            if (fileType == 'js' && fileName.indexOf('.') != -1) {
            }
          }
          this.loadingCodeSource = false;
        })
        .catch(error => {});
    }
  },
  beforeDestroy() {
    // document.querySelector('#design-main').style.zIndex = 1;
  },
  watch: {}
};
</script>
