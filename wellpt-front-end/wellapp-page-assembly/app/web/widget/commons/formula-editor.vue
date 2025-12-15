<template>
  <div class="formula-content-editor">
    <div class="toolbar">
      <div class="title"></div>
      <div class="right-corner-operation">
        <a-button size="small" @click="onZoom" type="link" icon="fullscreen">公式编辑</a-button>
        <a-button size="small" type="link" @click="clearEditor(simpleEditor)">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
          清空
        </a-button>
      </div>
    </div>
    <div class="editor-wrapper">
      <textarea ref="editor"></textarea>
      <div v-show="simpleEditorErrorMsg != undefined" class="editor-error-msg">
        <a-icon type="close-circle" theme="filled" style="color: red" />
        {{ simpleEditorErrorMsg }}
      </div>
    </div>
    <a-modal
      ref="zoomModal"
      v-model="zoomOut"
      title="编辑"
      :destroyOnClose="true"
      dialogClass="pt-modal"
      wrapClassName="formula-content-editor"
      width="900px"
      @ok="onConfirmEditAdvancedFormula"
    >
      <div>
        <div class="editor-wrapper">
          <textarea ref="zoomModalEditor"></textarea>
          <a-button size="small" type="link" @click="clearEditor(advancedEditor)" class="textarea-clear-button">清空</a-button>
          <div v-show="advancedEditorErrorMsg != undefined" class="editor-error-msg">
            <a-icon type="close-circle" theme="filled" style="color: red" />
            {{ advancedEditorErrorMsg }}
          </div>
        </div>
        <a-alert message="可选择以下内置变量插入编辑器" type="info" :banner="true" style="margin: 8px 0px" />

        <!-- 参数区 -->
        <div class="predefined-params-function-container">
          <a-card size="small" title="内置变量">
            <template slot="extra">
              <a-input-search placeholder="搜索" size="small" allow-clear v-model.trim="searchVariableKeyword" style="width: 150px" />
            </template>

            <ul
              role="listbox"
              style="height: 150px"
              class="ant-select-dropdown-menu ant-select-dropdown-menu-vertical ant-select-dropdown-menu-root"
            >
              <template v-if="supportVariableTypes.includes('dyform') && underDyformScope && fieldVarOptions.length > 0">
                <li class="ant-select-dropdown-menu-item-group">
                  <div class="ant-select-dropdown-menu-item-group-title" @click="expandCollapseGroup('formFields')">
                    <a-icon
                      :type="collapsedGroupKeys.includes('formFields') ? 'folder' : 'folder-open'"
                      theme="filled"
                      style="color: #ffca28"
                    />

                    表单字段
                  </div>
                  <ul
                    class="ant-select-dropdown-menu-item-group-list"
                    v-show="!!searchVariableKeyword || !collapsedGroupKeys.includes('formFields')"
                  >
                    <li
                      v-for="(item, i) in fieldVarOptions"
                      v-show="
                        searchVariableKeyword == undefined || item.label.toUpperCase().indexOf(searchVariableKeyword.toUpperCase()) > -1
                      "
                      role="option"
                      class="ant-select-dropdown-menu-item"
                      :value="item.value"
                      :key="'dyform-field-' + i"
                      @click.stop="onSelectPredefinedItem(item, 'variable')"
                    >
                      {{ item.label }}
                    </li>
                  </ul>
                </li>
              </template>

              <li class="ant-select-dropdown-menu-item-group" v-if="supportVariableTypes.includes('orgUser')">
                <div class="ant-select-dropdown-menu-item-group-title" @click="expandCollapseGroup('userDatas')">
                  <a-icon
                    :type="collapsedGroupKeys.includes('userDatas') ? 'folder' : 'folder-open'"
                    theme="filled"
                    style="color: #ffca28"
                  />

                  用户数据
                </div>
                <ul
                  class="ant-select-dropdown-menu-item-group-list"
                  v-show="!!searchVariableKeyword || !collapsedGroupKeys.includes('userDatas')"
                >
                  <li
                    v-for="(item, i) in userDataOptions"
                    v-show="
                      searchVariableKeyword == undefined || item.label.toUpperCase().indexOf(searchVariableKeyword.toUpperCase()) > -1
                    "
                    role="option"
                    class="ant-select-dropdown-menu-item"
                    :value="item.value"
                    :key="'user-field-' + i"
                    @click.stop="onSelectPredefinedItem(item, 'variable')"
                  >
                    {{ item.label }}
                  </li>
                </ul>
              </li>

              <li class="ant-select-dropdown-menu-item-group" v-if="supportVariableTypes.includes('workflow')">
                <div class="ant-select-dropdown-menu-item-group-title" @click="expandCollapseGroup('flowDatas')">
                  <a-icon
                    :type="collapsedGroupKeys.includes('flowDatas') ? 'folder' : 'folder-open'"
                    theme="filled"
                    style="color: #ffca28"
                  />
                  工作流数据
                </div>
                <ul
                  class="ant-select-dropdown-menu-item-group-list"
                  v-show="!!searchVariableKeyword || !collapsedGroupKeys.includes('flowDatas')"
                >
                  <li
                    v-for="(item, i) in flowDataOptions"
                    v-show="
                      searchVariableKeyword == undefined || item.label.toUpperCase().indexOf(searchVariableKeyword.toUpperCase()) > -1
                    "
                    role="option"
                    class="ant-select-dropdown-menu-item"
                    :value="item.value"
                    :key="'flow-field-' + i"
                    @click.stop="onSelectPredefinedItem(item, 'variable')"
                  >
                    {{ item.label }}
                  </li>
                </ul>
              </li>

              <li class="ant-select-dropdown-menu-item-group" v-if="supportVariableTypes.includes('dateTime')">
                <div class="ant-select-dropdown-menu-item-group-title" @click="expandCollapseGroup('dataTime')">
                  <a-icon
                    :type="collapsedGroupKeys.includes('dataTime') ? 'folder' : 'folder-open'"
                    theme="filled"
                    style="color: #ffca28"
                  />
                  日期时间
                </div>
                <ul
                  class="ant-select-dropdown-menu-item-group-list"
                  v-show="!!searchVariableKeyword || !collapsedGroupKeys.includes('dataTime')"
                >
                  <li
                    v-for="(item, i) in timeDataOptions"
                    v-show="
                      searchVariableKeyword == undefined || item.label.toUpperCase().indexOf(searchVariableKeyword.toUpperCase()) > -1
                    "
                    role="option"
                    class="ant-select-dropdown-menu-item"
                    :value="item.value"
                    :key="'time-field-' + i"
                    @click.stop="onSelectPredefinedItem(item, 'variable')"
                  >
                    {{ item.label }}
                  </li>
                </ul>
              </li>

              <template v-if="extVariableOptions != undefined && extVariableOptions.length > 0">
                <!-- [{ title:, options:[{label, value}]}] -->
                <template v-for="(group, g) in extVariableOptions">
                  <li class="ant-select-dropdown-menu-item-group">
                    <div class="ant-select-dropdown-menu-item-group-title" @click="expandCollapseGroup('extVariableGrp_' + g)">
                      <a-icon
                        :type="collapsedGroupKeys.includes('extVariableGrp_' + g) ? 'folder' : 'folder-open'"
                        theme="filled"
                        style="color: #ffca28"
                      />
                      {{ group.title }}
                    </div>
                    <ul
                      class="ant-select-dropdown-menu-item-group-list"
                      v-show="!!searchVariableKeyword || !collapsedGroupKeys.includes('extVariableGrp_' + g)"
                    >
                      <li
                        v-for="(item, i) in group.options"
                        v-show="
                          searchVariableKeyword == undefined || item.label.toUpperCase().indexOf(searchVariableKeyword.toUpperCase()) > -1
                        "
                        role="option"
                        class="ant-select-dropdown-menu-item"
                        :value="item.value"
                        :key="'ext-var-group-' + g + '-field-' + i"
                        @click.stop="onSelectPredefinedItem(item, 'variable')"
                      >
                        {{ item.label }}
                      </li>
                    </ul>
                  </li>
                </template>
              </template>
            </ul>
          </a-card>
          <a-card size="small" title="函数列表" v-if="enableFormulaFunction">
            <template slot="extra">
              <a-input-search placeholder="搜索" size="small" allow-clear v-model.trim="searchFunctionKeyword" style="width: 150px" />
            </template>
            <ul
              role="listbox"
              style="height: 150px"
              class="ant-select-dropdown-menu ant-select-dropdown-menu-vertical ant-select-dropdown-menu-root"
            >
              <li class="ant-select-dropdown-menu-item-group" v-for="(categoryFunc, c) in categoryFunctions" :key="'categoryFunc_' + c">
                <div
                  class="ant-select-dropdown-menu-item-group-title"
                  :title="categoryFunc.category.description || categoryFunc.category.name"
                  @click="expandCollapseGroup(categoryFunc.category.name)"
                >
                  <a-icon
                    :type="collapsedGroupKeys.includes(categoryFunc.category.name) ? 'folder' : 'folder-open'"
                    theme="filled"
                    style="color: #ffca28"
                  />

                  {{ categoryFunc.category.name }}
                </div>
                <ul
                  class="ant-select-dropdown-menu-item-group-list"
                  v-show="!!searchFunctionKeyword || !collapsedGroupKeys.includes(categoryFunc.category.name)"
                >
                  <li
                    v-for="(item, i) in categoryFunc.functions"
                    v-show="
                      searchFunctionKeyword == undefined ||
                      item.meta.name.toUpperCase().indexOf(searchFunctionKeyword.toUpperCase()) > -1 ||
                      item.key.toUpperCase().indexOf(searchFunctionKeyword.toUpperCase()) > -1
                    "
                    role="option"
                    class="ant-select-dropdown-menu-item"
                    :value="item.key"
                    :key="'func-field-' + c + '-' + i"
                    @click.stop="onSelectPredefinedItem(item, 'function')"
                    @mouseenter="onMouseenterItem(item, 'function')"
                  >
                    {{ item.meta.name }}
                  </li>
                </ul>
              </li>
            </ul>
          </a-card>
          <a-card size="small" v-if="enableFormulaFunction">
            <template slot="title">
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              {{ predefineItemInfo.key ? predefineItemInfo.title : '说明' }}
            </template>
            <div style="height: 150px; overflow-y: auto">
              <div v-show="predefineItemInfo.key != undefined">
                <div style="padding-bottom: 6px">{{ predefineItemInfo.description }}</div>
                <div style="padding-bottom: 6px" v-if="predefineItemInfo.example != undefined">
                  <div>示例:</div>
                  <div style="padding-left: 8px">
                    <template v-if="Array.isArray(predefineItemInfo.example)">
                      <div v-for="(example, e) in predefineItemInfo.example" :key="'example_' + e">
                        {{ example }}
                      </div>
                    </template>
                    <template v-else>{{ predefineItemInfo.example }}</template>
                  </div>
                </div>
                <div style="padding-bottom: 6px" v-show="predefineItemInfo.args && predefineItemInfo.args.length > 0">
                  参数:
                  <div style="padding-left: 8px">
                    <div v-for="item in predefineItemInfo.args">
                      {{ formatePredefineFuncArgType(item) }}
                    </div>
                  </div>
                </div>
                <div style="padding-bottom: 6px" v-if="predefineItemInfo.returnType != undefined">
                  返回值:
                  <div style="padding-left: 8px">{{ formatePredefineFuncReturnType(predefineItemInfo.returnType) }}</div>
                </div>
              </div>
            </div>
          </a-card>
        </div>
      </div>
    </a-modal>
  </div>
</template>
<style lang="less">
.formula-content-editor {
  text-align: left;
  .toolbar {
    height: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    border-radius: var(--w-input-border-radius) var(--w-input-border-radius) 0px 0px;
    padding: 0px 10px;
    font-weight: bold;
    border-left: var(--w-input-border-width) var(--w-input-border-style) var(--w-input-border-color);
    border-right: var(--w-input-border-width) var(--w-input-border-style) var(--w-input-border-color);
    border-top: var(--w-input-border-width) var(--w-input-border-style) var(--w-input-border-color);
    .title {
      font-size: 14px;
      font-weight: 500;
    }
    .right-corner-operation {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      cursor: pointer;
    }
  }
  .editor-wrapper {
    position: relative;
    .textarea-clear-button {
      position: absolute;
      right: 2px;
      top: 3px;
      background-color: #fff;
    }
    .editor-error-msg {
      background-color: #fff0f0;
      color: red;
      position: absolute;
      right: 3px;
      padding: 2px 5px;
      bottom: 3px;
      line-height: normal;
    }
  }
  .ant-select-dropdown-menu-item-group-title {
    cursor: pointer;
  }

  .predefined-params-function-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    > div {
      flex: 1;
      margin-right: 12px;
      &:last-child {
        margin-right: unset !important;
      }
    }
  }

  /* 修改默认选中文本颜色 */
  .CodeMirror-selected {
    background: var(--w-primary-color-2);
  }

  .CodeMirror {
    color: rgba(0, 0, 0, 0.65);
    font-family: inherit;
    font-weight: inherit;
    line-height: 23px;
    border: var(--w-input-border-width) var(--w-input-border-style) var(--w-input-border-color);
    border-radius: 0px 0px var(--w-input-border-radius) var(--w-input-border-radius);
    .CodeMirror-cursor {
      margin-left: 1px; /* 与前一个字符的距离 */
    }
    /* 修改 placeholder 文本样式 */
    &.CodeMirror-empty {
      color: rgba(0, 0, 0, 0.45) !important;
    }
    .cm-predefined-variable {
      color: rgba(0, 0, 0, 0.65);
      padding: 2px;
      margin-right: 2px;
      line-height: 20px;
      background: #fafafa;
      font-size: 12px;
      border-radius: 2px;
      border: 1px solid #d9d9d9;
      // background: var(--w-primary-color-1);
      //  color: var(--w-primary-color);
      // outline: 1px solid var(--w-primary-color-3);
    }

    .cm-predefined-function {
      color: var(--w-primary-color);
      font-weight: bolder;
      padding: 1px;
      margin-right: 1px;
      line-height: 20px;
      text-decoration: underline;
    }
  }
}
</style>
<script type="text/babel">
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/darcula.css';
import { cloneDeep, debounce } from 'lodash';
import { FormulaFunctionLibrary } from '@framework/vue/formula/JsFormulaEngine';

export default {
  name: 'FormulaEditor',
  inject: ['pageParams', 'dyform', 'designer'],
  props: {
    placeholder: {
      type: String,
      default: '请输入内容'
    },
    widget: Object,
    bindToConfiguration: {
      type: Object,
      required: true
    },
    configKey: {
      type: String,
      default: 'formula'
    },
    height: {
      type: String,
      default: '100px'
    },
    variableDelimiters: Array, // 变量分隔符
    enableFormulaFunction: {
      type: Boolean,
      default: true
    },
    supportVariableTypes: {
      type: Array,
      default: ['dyform', 'orgUser', 'workflow', 'dateTime']
    },
    mode: {
      type: String,
      default: 'javascript'
    },
    extVariableOptions: Array,
    extFormulaFunctionLibrary: [Object, Array]
  },
  components: {},
  computed: {
    underDyformScope() {
      return this.dyform != undefined;
    },
    // 表单字段
    fieldVarOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos) {
        // 表单设计
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          opt.push({
            label: info.name,
            value: `_FORM_DATA_.${info.code}`
          });
        }
      }
      return opt;
    }
  },
  data() {
    const formulaFunctionLibrary = this.enableFormulaFunction ? new FormulaFunctionLibrary() : undefined;
    return {
      collapsedGroupKeys: [],
      editor: null,
      formulaFunctionLibrary,
      marks: [],
      editorContent: '',
      zoomOut: false,
      searchVariableKeyword: undefined,
      simpleEditor: undefined,
      advancedEditor: undefined,
      userDataOptions: [
        { label: '用户名', value: '_USER_.userName' },
        { label: '用户ID', value: '_USER_.userId' },
        { label: '登录账号', value: '_USER_.loginName' },
        { label: '用户包含角色ID集合', value: '_USER_.roles' },
        { label: '用户主部门ID', value: '_USER_.mainDeptId' },
        { label: '用户主部门ID路径', value: '_USER_.mainDeptIdPath' },
        { label: '用户主部门名称', value: '_USER_.mainDeptName' },
        { label: '用户主部门名称路径', value: '_USER_.mainDeptNamePath' },
        { label: '用户主职位ID', value: '_USER_.mainJobId' },
        { label: '用户主职位ID路径', value: '_USER_.mainJobIdPath' },
        { label: '用户主职位名称', value: '_USER_.mainJobName' },
        { label: '用户主职位名称路径', value: '_USER_.mainJobNamePath' },
        { label: '用户单位ID', value: '_USER_.unitId' },
        { label: '用户单位名称', value: '_USER_.unitName' },
        { label: '用户职位ID集合', value: '_USER_.jobIds' },
        { label: '用户职位名称集合', value: '_USER_.jobNames' },
        { label: '用户部门ID集合', value: '_USER_.deptIds' },
        { label: '用户部门名称集合', value: '_USER_.deptNames' }
      ],
      timeDataOptions: [
        { label: '当前日期时间戳', value: '_DATETIME_.currentTimestamp' },
        { label: `当前日期格式数值(YYYYMMDD)`, value: '_DATETIME_.currentDateString' },
        { label: `当前日期时间格式数值(YYYYMMDDHHmmss)`, value: '_DATETIME_.currentFullDateTimeString' },
        { label: '当前星期几(1~7)', value: '_DATETIME_.currentWeekDay' },
        { label: '当前月份(1~12)', value: '_DATETIME_.currentMonth' },
        { label: '当前季度(1~4)', value: '_DATETIME_.currentQuarter' },
        { label: '当前年份', value: '_DATETIME_.currentYear' },
        { label: '当前日(1~31)', value: '_DATETIME_.currentDay' },
        { label: '当前小时(0~23)', value: '_DATETIME_.currentHour' }
      ],
      flowDataOptions: [
        { label: '当前环节ID', value: '_WORKFLOW_.taskId' },
        { label: '当前环节名称', value: '_WORKFLOW_.taskName' },
        { label: '当前流程ID', value: '_WORKFLOW_.flowDefId' },
        { label: '当前流程版本号', value: '_WORKFLOW_.version' }
      ],
      categoryFunctions: [],
      predefineItemInfo: {
        title: undefined,
        description: undefined,
        example: undefined,
        args: undefined,
        returnType: undefined
      },
      dataType: { string: '字符串', date: '日期', number: '数字', boolean: '布尔值', object: '对象', array: '数组' },
      advancedEditorErrorMsg: undefined,
      simpleEditorErrorMsg: undefined
    };
  },
  beforeCreate() {},
  created() {
    if (this.bindToConfiguration != undefined && this.configKey != undefined) {
      if (!this.bindToConfiguration.hasOwnProperty(this.configKey)) {
        this.$set(this.bindToConfiguration, this.configKey, {
          value: undefined,
          marks: [] // 内置变量、函数的标记配置
        });
      }
    }
    if (this.formulaFunctionLibrary != undefined) {
      this.categoryFunctions = this.formulaFunctionLibrary.groupCategoryFunctions();
    }
    if (this.extFormulaFunctionLibrary != undefined) {
      let extLibs = Array.isArray(this.extFormulaFunctionLibrary) ? this.extFormulaFunctionLibrary : [this.extFormulaFunctionLibrary];
      this.categoryFunctions.push(...extLibs);
    }

    this.checkJsValid = debounce(this.checkJsValid.bind(this), 500);
  },
  beforeMount() {},
  mounted() {
    import('codemirror').then(codemirror => {
      require(`codemirror/mode/javascript/javascript`);
      require(`codemirror/addon/edit/matchbrackets`);
      require(`codemirror/addon/display/placeholder`);
      this.CodeMirror = codemirror;
      this.simpleEditor = this.createEditor(
        this.$refs.editor,
        {
          height: this.height
        },
        cm => {
          let code = this.getActualCodeWithVariableMarks(this.simpleEditor);
          this.simpleEditorErrorMsg = undefined;
          this.checkJsValid(code.value, valid => {
            this.simpleEditorErrorMsg = valid ? undefined : '公式语法解析异常';
          });
          this.$set(this.bindToConfiguration, this.configKey, cloneDeep(code));
        }
      );

      if (this.bindToConfiguration && this.bindToConfiguration[this.configKey]) {
        this.restoreEditorContent(this.simpleEditor, this.bindToConfiguration[this.configKey]);
      }
    });
  },
  methods: {
    expandCollapseGroup(key) {
      let idx = this.collapsedGroupKeys.indexOf(key);
      if (idx == -1) {
        this.collapsedGroupKeys.push(key);
      } else {
        this.collapsedGroupKeys.splice(idx, 1);
      }
    },
    formatePredefineFuncArgType(arg) {
      let str = `${arg.name} : `;
      let info = [];
      if (arg.type && this.dataType[arg.type.toLowerCase()]) {
        info.push(this.dataType[arg.type.toLowerCase()] + '类型');
      }
      if (arg.optional) {
        info.push('可选');
      }
      if (arg.description) {
        info.push(arg.description);
      }
      return str + info.join('。');
    },
    formatePredefineFuncReturnType(returnType) {
      let rt = Array.isArray(returnType) ? returnType : [returnType];
      let n = rt.map(item => this.dataType[item.toLowerCase()]);
      return n.join(' | ');
    },
    onMouseenterItem(item, type) {
      if (type == 'function') {
        this.predefineItemInfo.key = item.key;
        this.predefineItemInfo.title = item.meta.name;
        this.predefineItemInfo.description = item.meta.description;
        this.predefineItemInfo.example = item.meta.example;
        this.predefineItemInfo.args = item.meta.args;
        this.predefineItemInfo.returnType = item.meta.returnType;
      }
    },
    clearEditor(editor) {
      const doc = editor.getDoc();
      doc.setValue(''); // 清空编辑器
      if (this.zoomOut) {
        this.advancedEditorErrorMsg = undefined;
      } else {
        this.simpleEditorErrorMsg = undefined;
      }
    },
    /**
     * 将带有标记的内容回填到编辑器（支持 label 变化的情况）
     * @param {Codemirror} targetEditor 目标编辑器
     * @param {Object} data 包含 { value, label, marks } 的数据
     */
    restoreEditorContent(targetEditor, data) {
      if (!targetEditor || !data || !data.value) return;

      const doc = targetEditor.getDoc();
      doc.setValue(''); // 清空编辑器

      targetEditor.operation(() => {
        // 1. 插入基础内容（优先使用 value，确保变量格式正确）
        doc.setValue(data.value);

        // 2. 如果没有 marks 或不需要标记，直接返回
        if (!data.marks || !data.marks.length) return;

        // 3. 遍历 marks，按 value 查找变量位置，并用新 label 替换
        data.marks.forEach(mark => {
          let newLabel = this.getVariableLabel(mark.value, mark.label);
          const { value } = mark;

          // 4. 构造变量的完整语法（如 ${_USER_.userName}）
          const variableSyntax =
            this.variableDelimiters && this.variableDelimiters.length === 2
              ? `${this.variableDelimiters[0]}${value}${this.variableDelimiters[1]}`
              : value;

          // 5. 在编辑器中查找变量的位置
          let foundPos = null;
          doc.eachLine(lineHandle => {
            const lineText = lineHandle.text;
            const varIndex = lineText.indexOf(variableSyntax);
            if (varIndex !== -1) {
              foundPos = {
                from: { line: lineHandle.lineNo(), ch: varIndex },
                to: { line: lineHandle.lineNo(), ch: varIndex + variableSyntax.length }
              };
            }
          });

          // 6. 如果找到变量位置，替换为新的 label 并标记
          if (foundPos) {
            doc.replaceRange(newLabel, foundPos.from, foundPos.to);
            targetEditor.markText(
              foundPos.from,
              {
                line: foundPos.from.line,
                ch: foundPos.from.ch + newLabel.length
              },
              {
                className: 'cm-predefined-' + mark.type,
                attributes: { label: newLabel, value, type: mark.type },
                atomic: true
              }
            );
          }
        });
      });
      targetEditor.execCommand('goDocEnd'); // 内置命令，直接跳转到文档末尾
      targetEditor.refresh(); // 刷新编辑器
    },
    getVariableLabel(value, label) {
      if (value.indexOf('_FORM_DATA_.') !== -1) {
        // 表单字段，可能存在字段名被修改了，所以需要从字段列表中获取字段名
        for (let o of this.fieldVarOptions) {
          if (o.value === value) {
            return o.label;
          }
        }
      }
      return label;
    },
    setEditorDoc(editor, value) {
      editor.setValue('');
      if (value) {
        if (value.label) {
          editor.operation(() => {
            editor.setValue(value.label);
            if (value.marks && value.marks.length > 0) {
              const sortedMarks = [...value.marks].sort((a, b) => b.from.ch - a.from.ch);
              for (let mark of sortedMarks) {
                // 标记为特殊变量
                editor.markText(mark.from, mark.to, {
                  className: 'cm-predefined-' + mark.type,
                  attributes: {
                    label: mark.label,
                    value: mark.value,
                    type: mark.type
                  },
                  atomic: true
                });
              }
            }
          });
        }
      }
    },
    onConfirmEditAdvancedFormula() {
      this.zoomOut = false;
      this.setEditorDoc(this.simpleEditor, cloneDeep(this.tempFormulaValue));
      this.$set(this.bindToConfiguration, this.configKey, cloneDeep(this.tempFormulaValue));
    },
    insertSimpleEditorCode(code) {
      this.insertCode(code, this.simpleEditor);
    },
    insertCode(code, editor, type) {
      const doc = editor.getDoc();
      const cursor = doc.getCursor();
      editor.operation(() => {
        if (typeof code == 'string') {
          // 插入描述文本
          doc.replaceRange(code, cursor);
          if (code == '()') {
            // 插入括号
            doc.setCursor(doc.getCursor().line, doc.getCursor().ch - 1);
          }
        } else if (typeof code == 'object') {
          // 插入描述文本
          doc.replaceRange(code.label, cursor);
          // 获取插入的文本位置
          const from = cursor;
          const to = {
            line: from.line,
            ch: from.ch + code.label.length
          };

          // 标记为特殊变量
          editor.markText(from, to, {
            className: 'cm-predefined-' + type,
            attributes: {
              label: code.label,
              value: code.value,
              type: type
            },
            atomic: true
          });
        }
      });

      editor.focus();
    },

    onSelectPredefinedItem(item, type) {
      if (type == 'variable') {
        this.insertCode(
          {
            label: item.label,
            value: item.value
          },
          this.advancedEditor,
          'variable'
        );
      } else if (type == 'function') {
        this.advancedEditor.operation(() => {
          let label = item.meta.name,
            value = ` await ${item.key}`,
            noArgs = item.meta.args == undefined || item.meta.args.length == 0;
          // if (noArgs) {
          //   label += '()';
          //   value += '()';
          // }
          this.insertCode(
            {
              label,
              value
            },
            this.advancedEditor,
            'function'
          );
          // if (!noArgs) {
          this.insertCode('()', this.advancedEditor);
          // }
        });
      }
    },
    onZoom() {
      this.zoomOut = !this.zoomOut;
      if (this.zoomOut) {
        let originalValue = cloneDeep(this.bindToConfiguration[this.configKey]);
        this.$nextTick(() => {
          this.advancedEditor = this.createEditor(
            this.$refs.zoomModalEditor,
            {
              height: '150px'
            },
            cm => {
              let code = this.getActualCodeWithVariableMarks(this.advancedEditor);
              this.advancedEditorErrorMsg = undefined;
              this.checkJsValid(code.value, valid => {
                this.advancedEditorErrorMsg = valid ? undefined : '公式语法解析异常';
              });
              this.tempFormulaValue = cloneDeep(code);
            }
          );
          if (originalValue && originalValue.value) {
            this.restoreEditorContent(this.advancedEditor, originalValue);
          }
        });
      }
    },

    checkJsValid(content, callback) {
      if (this.mode == 'javascript') {
        import('acorn').then(acorn => {
          try {
            const ast = acorn.parse(content, {
              ecmaVersion: 2022,
              sourceType: 'module'
            });
            callback(true);
          } catch (error) {
            console.error('公式解析异常: ', error);
            callback(false);
          }
        });
      }
    },

    createEditor(textarea, options, onChange) {
      let editor = this.CodeMirror.fromTextArea(textarea, {
        mode: this.mode,
        lineNumbers: false,
        indentUnit: 4,
        matchBrackets: true,
        placeholder: this.placeholder,
        // lineWrapping: false, // 禁用自动换行
        extraKeys: {
          Tab: cm => {
            if (cm.somethingSelected()) {
              cm.indentSelection('add');
            } else {
              cm.replaceSelection('    ', 'end');
            }
          },
          'Shift-Tab': cm => {
            cm.indentSelection('subtract');
          }
        }
      });

      // 监听变化
      editor.on('change', cm => {
        if (typeof onChange === 'function') {
          onChange.call(this, cm);
        }
      });
      editor.setSize(null, options.height);
      return editor;
    },

    getActualCodeWithVariableMarks(editor) {
      const doc = editor.getDoc();
      const label = doc.getValue(); // 获取完整内容（含换行符）
      let code = label;
      const lines = code.split('\n'); // 按行分割
      const markRanges = [];

      // 1. 收集所有标记
      doc.getAllMarks().forEach(mark => {
        if (!mark.find()) return;
        const m = mark.find();
        const { from, to } = m;
        const markedText = doc.getRange(from, to);
        if (markedText && mark.className.startsWith('cm-predefined-')) {
          markRanges.push({
            from,
            to,
            value: mark.attributes['value'],
            label: markedText,
            type: mark.attributes['type']
          });
        }
      });

      // 2. 按从后到前的顺序处理标记（避免位置偏移）
      markRanges.sort((a, b) => {
        if (b.from.line !== a.from.line) return b.from.line - a.from.line;
        return b.from.ch - a.from.ch;
      });

      // 3. 替换标记文本为变量语法
      markRanges.forEach(mark => {
        const variableSyntax =
          this.variableDelimiters && this.variableDelimiters.length === 2
            ? `${this.variableDelimiters[0]}${mark.value}${this.variableDelimiters[1]}`
            : mark.value;

        // 检查当前内容是否匹配标记的 label
        const currentText = doc.getRange(mark.from, mark.to);
        if (currentText === mark.label) {
          // 处理单行标记
          if (mark.from.line === mark.to.line) {
            const line = lines[mark.from.line];
            lines[mark.from.line] = line.substring(0, mark.from.ch) + variableSyntax + line.substring(mark.to.ch);
          }
          // 处理跨行标记（如变量包含换行符）
          else {
            // 第一行：替换开头部分
            lines[mark.from.line] = lines[mark.from.line].substring(0, mark.from.ch) + variableSyntax;
            // 中间行：清空
            for (let i = mark.from.line + 1; i < mark.to.line; i++) {
              lines[i] = '';
            }
            // 最后一行：替换结尾部分
            lines[mark.to.line] = lines[mark.to.line].substring(mark.to.ch);
          }
        }
      });

      // 4. 重新拼接代码
      code = lines.join('\n');

      return {
        value: code,
        label,
        marks: markRanges.map(m => ({
          from: m.from,
          to: m.to,
          label: m.label,
          value: m.value,
          type: m.type
        }))
      };
    }
  }
};
</script>
