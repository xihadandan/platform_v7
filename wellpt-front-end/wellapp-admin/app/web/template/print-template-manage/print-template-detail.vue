<template>
  <a-form-model
    class="pt-form"
    :model="form"
    layout="vertical"
    :label-col="labelCol"
    :wrapper-col="wrapperCol"
    :rules="formRules"
    ref="form"
  >
    <a-tabs v-model="activeTab" class="pt-tabs">
      <a-tab-pane :key="1" tab="基本信息">
        <a-form-model-item label="名称" prop="name">
          <a-input v-model="form.name" allow-clear>
            <template slot="addonAfter">
              <w-i18n-input :target="form" code="name" v-model="form.name" :key="form.uuid" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="ID" prop="id">
          <a-input v-model="form.id" readOnly />
        </a-form-model-item>
        <a-form-model-item label="编号" prop="code">
          <a-input v-model="form.code" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="分类">
          <a-tree-select
            v-model="form.category"
            style="width: 100%"
            :replaceFields="{ children: 'children', title: 'name', key: 'id', value: 'id' }"
            :tree-data="allClassifyOptions"
            allow-clear
            @change="classifyChange"
          />
        </a-form-model-item>
        <a-form-model-item label="所属模块">
          <a-select v-model="form.moduleId" showSearch allow-clear option-filter-prop="title">
            <a-select-option v-for="d in allModuleOptions" :key="d.id" :title="d.text">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="模板类型">
          <a-radio-group v-model="form.templateType">
            <a-radio value="wordType" name="templateType">Word模板(只支持doc)</a-radio>
            <a-radio value="htmlType" name="templateType">HTML模板</a-radio>
            <a-radio value="wordXmlType" name="templateType">WordXml模板</a-radio>
            <a-radio value="wordXmlByCommentType" name="templateType">WordXml模板(批注)</a-radio>
            <a-radio value="wordPoiType" name="templateType">Poi-tl模板(只支持docx)</a-radio>
          </a-radio-group>
          <div class="help-block">
            <div>word模板:一般不建议使用 html模板: 功能最强大（支持freemarker的全语法），但在套打出来后，不可编辑。</div>
            <div>
              WordXml模板:功能也还可以，但没html强大，但套打出来后是可编辑的,可以再次调整内容及格式(不建议使用，建议使用下面这种方式)
            </div>
            <div>WordXml模板(批注): 功能与WordXml模板是一样，不同之处在于:freemarker脚本是写在批注中。(政府收发文建议使用)</div>
            <div>Poi-tl模板: 模版文件和嵌套的正文必须为docx格式文件</div>
            <a target="_blank" class="widget-hyperlink" href="http://wiki.well-soft.com:81/pages/viewpage.action?pageId=4292668">帮助</a>
          </div>
        </a-form-model-item>
        <a-form-model-item label="多次套打结果间隔">
          <a-radio-group v-model="form.printInterval">
            <a-radio value="paging" name="printInterval">分页</a-radio>
            <a-radio value="multi_line" name="printInterval">多行</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="行数" v-if="form.printInterval == 'multi_line'">
          <a-input v-model="form.rowNumber" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="其他选项">
          <a-checkbox v-model="form.isSaveTrace">保留正文修改痕迹</a-checkbox>
          <a-checkbox v-model="form.isReadOnly">只读标志</a-checkbox>
          <a-checkbox v-model="form.isSavePrintRecord">保存打印记录</a-checkbox>
          <a-checkbox v-model="form.isSaveSource">保存到源文档</a-checkbox>
        </a-form-model-item>
        <a-form-model-item label="文件名格式" v-if="form.isSaveSource">
          <a-input v-model="form.fileNameFormat" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="上传模板文件">
          <UploadSimpleComponent :fileIds="form.fileUuid" @change="fileChange" ref="uploadSimpleRef"></UploadSimpleComponent>
          <div class="help-block">
            <div>Word模板上传2003的版本、应用服务器安装office</div>
            <div>2000及jacob-1.17-x86.dll(32位操作系统)或jacob-1.17-x64.dll(64位操作系统)放到系统PATH目录(C:\Windows\System32)下</div>
          </div>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane :key="2" tab="模板列表">
        <div style="padding-bottom: 12px; text-align: right" class="btn_has_space">
          <a-button type="primary" @click="addTableItem">新建</a-button>
          <a-button @click="delTableItems">删除</a-button>
          <a-button @click="upTableItems">上移</a-button>
          <a-button @click="downTableItems">下移</a-button>
        </div>
        <a-table
          :data-source="form.printContents"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: tableSelectChange }"
          :pagination="false"
          :row-key="(record, index) => index"
          style="margin-bottom: 20px"
          :scroll="{ y: 'calc(100vh - 300px)' }"
          class="pt-table"
        >
          <a-table-column key="lang" data-index="lang" title="语言">
            <template slot-scope="text, record, index">
              <a-select v-model="record.lang" showSearch allow-clear style="width: 150px">
                <a-select-option v-for="(item, idx) in langsOptions" :key="index + '_' + idx" :value="idx">
                  {{ item }}
                </a-select-option>
              </a-select>
            </template>
          </a-table-column>
          <a-table-column key="remark" data-index="remark" title="备注">
            <template slot-scope="text, record">
              <a-input v-model="record.remark" allow-clear />
            </template>
          </a-table-column>
          <a-table-column key="operation" title="操作">
            <template slot-scope="text, record, index">
              <a-button @click="openModal(record, index)">设计</a-button>
            </template>
          </a-table-column>
        </a-table>
      </a-tab-pane>
    </a-tabs>
    <a-modal v-model="modalVisible" title="模板设计" @ok="handleOk" ref="contentModalRef" class="pt-modal">
      <a-form-model-item label="上传模板文件" :label-col="{ span: 6 }" :wrapper-col="{ span: 17 }">
        <UploadSimpleComponent
          :defaultFileList="editData.printTemplate"
          @change="printTemplateChange"
          ref="uploadPrintTemplateChangeRef"
        ></UploadSimpleComponent>
      </a-form-model-item>
    </a-modal>
    <a-modal v-model="htmlModalVisible" title="模板设计" @ok="handleOk" ref="contentHtmlModalRef" :width="800" class="pt-modal">
      <a-row type="flex">
        <a-col flex="200px">
          <div class="help-block" style="padding-right: 8px">
            注意：1、点击字段名称,复制字段值。 2、编辑器右击选择“删除表格”即可删除从表
          </div>
          <a-select
            v-model="editData.formUuid"
            showSearch
            :filter-option="filterFormOption"
            allow-clear
            style="width: 150px"
            @change="formChange"
          >
            <a-select-option v-for="item in allFormOptions" :key="item.id" :value="item.id">
              {{ item.text }}
            </a-select-option>
          </a-select>
          <PerfectScrollbar style="height: 500px">
            <a-tree
              :tree-data="formDefinitionTreeData"
              @select="onFormDefinitionClick"
              :replaceFields="{ children: 'children', title: 'name', key: 'id' }"
            ></a-tree>
          </PerfectScrollbar>
        </a-col>
        <a-col flex="auto">
          <QuillEditor v-model="editData.printContent" ref="quillEditor" @change="onPrintContentChange" style="width: 550px" />
        </a-col>
      </a-row>
    </a-modal>
    <div style="text-align: center; margin-bottom: 30px" v-if="!$widgetDrawerContext">
      <a-button @click="saveForm(false)" type="primary">保存</a-button>
      <a-button @click="saveForm(true)">保存新版本</a-button>
    </div>
  </a-form-model>
</template>

<script type="text/babel">
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';
import UploadSimpleComponent from '../common/upload-simple-component';
import { langsOptions } from '../common/constant';
import { each, assignIn, map, find, findIndex, sortBy, filter, cloneDeep } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'PrintTemplateDetail',
  props: {
    uuid: String,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { UploadSimpleComponent, QuillEditor, WI18nInput },
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      $evtWidget: undefined,
      $dialogWidget: undefined,
      activeTab: 1,
      form: {
        printContents: []
      },
      labelCol: { span: 4, style: 'text-align:right;padding-right:4px;' },
      wrapperCol: { span: 19 },
      rules: {
        name: [{ required: true, message: '名称必填', trigger: ['blur', 'change'] }, { validator: this.validateName }],
        id: { required: true, message: 'id必填', trigger: ['blur', 'change'] }
      },
      allModuleOptions: [],
      allClassifyOptions: [],
      fileList: [],
      selectedRowKeys: [],
      selectedRows: [],
      langsOptions,
      modalVisible: false,
      editData: {},
      eidtIndex: 0,
      printContentsUuid: [],
      htmlModalVisible: false,
      allFormOptions: [],
      formDefinitionTreeData: [],
      initFormDefinitionTreeData: [
        {
          id: 'root_zNdoes_field',
          name: '字段信息',
          children: [],
          selectable: false
        },
        {
          id: 'root_zNdoes_subform',
          name: '从表信息',
          children: [],
          selectable: false
        }
      ],
      editor: undefined,
      wTemplate: {
        $options: {
          methods: {
            initFormData: this.initFormData
          },
          META: {
            method: {
              initFormData: '新增/编辑表单'
            }
          }
        }
      },
      $widgetDrawerContext: undefined
    };
  },
  META: {
    method: {
      initFormData: '新增/编辑表单',
      saveFormMethod: '保存表单',
      saveNewVersion: '保存新版本',
      saveAndNewNextData: '保存并添加下一个'
    }
  },
  computed: {
    formRules() {
      let rules = assignIn({}, this.rules);
      return rules;
    }
  },
  watch: {},
  beforeCreate() {},
  created() {
    this.getAllModuleOptions();
    this.getAllClassifyOptions();
    this.getAllFormOptions();
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
  },
  beforeMount() {
    let _this = this;
  },
  mounted() {
    if (!this.form.uuid) {
      this.$set(this.form, 'id', 'PRT_' + generateId('SF'));
    }
    let $event = this._provided.$event;
    this.$widgetDrawerContext = this._provided && this._provided.widgetDrawerContext;
    if ($event && $event.meta) {
      this.initFormData($event);
    }
  },
  methods: {
    getPopupContainerByPs,
    // 保存并添加下一个
    saveAndNewNextData() {
      this.saveFormMethod(() => {
        this.initFormData(this._provided.$event);
      });
    },
    initFormData(data) {
      this.$refs.form.clearValidate();
      this.form.uuid = '';
      this.printContentsUuid = [];
      if (data && data.eventParams) {
        this.form.uuid = data.eventParams.uuid || '';
      }
      if (!this.form.uuid) {
        let form = {
          id: 'PRT_' + generateId('SF'),
          printContents: []
        };
        this.$set(this, 'form', form);
        if (this.$refs.uploadSimpleRef) {
          this.$refs.uploadSimpleRef.setValue();
        }
      } else {
        this.getFormData();
      }
    },
    getFormData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'printTemplateService',
          methodName: 'getBeanByUuid',
          args: JSON.stringify([this.form.uuid]),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.form = data.data;
            if (this.form.i18ns) {
              let i18n = {};
              for (let item of this.form.i18ns) {
                if (i18n[item.locale] == undefined) {
                  i18n[item.locale] = {};
                }
                i18n[item.locale][item.code] = item.content;
              }
              this.form.i18n = i18n;
            }
            if (this.$refs.uploadSimpleRef) {
              this.$refs.uploadSimpleRef.setValue(data.data.fileUuid);
            }
            _this.printContentsUuid = map(data.data.printContents, 'uuid');
          }
        });
    },
    // 模块
    getAllModuleOptions() {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'appModuleMgr',
          queryMethod: 'loadSelectData',
          params: {
            systemUnitId: this._$USER.systemUnitId
          },
          searchValue: '',
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.allModuleOptions = data.results;
          }
        });
    },
    // 分类
    getAllClassifyOptions() {
      let _this = this;
      $axios.post('/api/printTemplate/category/getTreeAllBySystemUnitIdsLikeName', { name: this.searchWord }).then(({ data }) => {
        this.loading = false;
        if (data.code == 0 && data.data) {
          this.allClassifyOptions = data.data;
        }
      });
    },
    // 所有表单选项
    getAllFormOptions() {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'formDefinitionService',
          queryMethod: '',
          searchValue: '',
          pageSize: 10000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.allFormOptions = data.results;
          }
        });
    },
    getFormDefinition() {
      let _this = this;
      $axios
        .post('/pt/dyform/definition/getFormDefinition', { formUuid: this.editData.formUuid, justDataAndDef: false })
        .then(({ data, status }) => {
          this.loading = false;
          if (status == 200) {
            if (data) {
              let treeData = cloneDeep(this.initFormDefinitionTreeData);
              each(data.fields || {}, (item, index) => {
                treeData[0].children.push({
                  id: index,
                  formId: data.id || data.outerId,
                  formUuid: data.uuid,
                  name: item.displayName,
                  children: []
                });
              });
              each(data.subforms || {}, (item, index) => {
                treeData[1].children.push({
                  id: index,
                  data: item,
                  isSubform: true,
                  formId: item.outerId || item.name,
                  formUuid: item.formUuid,
                  name: item.displayName || item.name,
                  children: []
                });
              });
              this.formDefinitionTreeData = treeData;
            }
          } else if (status == 404) {
            // 加载定义失败
            this.$message.info('表单不存在:formUuid=' + this.editData.formUuid);
          } else {
            this.$message.info('表单定义加载失败,请重试');
            throw new Error(data);
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '表单加载失败');
        });
    },
    filterFormOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    getPrintContent() {
      $axios
        .get('/html/print/template/manager/print_content_html.html', {
          dataType: 'html'
        })
        .then(({ data }) => {
          if (this.$refs.quillEditor) {
            this.$refs.quillEditor.setHtml(data);
          }
        });
    },
    onFormDefinitionClick(item, e) {
      let _this = this;
      const data = e.node.$options.propsData.dataRef;
      const text = '${dytable.' + data.formId + '.' + data.id + '}';

      // copyToClipboard(text, e.nativeEvent, function (success) {
      //   if (success) {
      //     _this.$message.success('已复制');
      //   }
      // });
      let length = this.editor.editor.selection.savedRange.index;
      this.editor.editor.insertEmbed(length, 'text', text);
      this.editor.editor.setSelection(length + text.length);
    },
    // 表单选择变化
    formChange(value, label, extr) {
      this.formDefinitionTreeData = [];
      if (value) {
        this.getFormDefinition();
      }
    },
    //分类选择变化
    classifyChange(value, label, extra) {
      this.form.categoryName = label.join(',');
    },
    fileChange(fileList) {
      if (fileList.length) {
        this.form.fileUuid = fileList[0].dbFile.fileID;
        this.form.fileName = fileList[0].dbFile.fileName || fileList[0].dbFile.filename;
      } else {
        this.form.fileUuid = '';
        this.form.fileName = '';
      }
    },
    tableSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRows = selectedRows; //选中的keys
      this.selectedRowKeys = selectedRowKeys; //选中的行
    },
    resetTableSelectKey() {
      let selectedRowKeys = [];
      each(this.selectedRows, (item, index) => {
        let hasIndex = -1;
        if (item.id) {
          hasIndex = findIndex(this.form.printContents, { id: item.id });
        } else {
          hasIndex = findIndex(this.form.printContents, { uuid: item.uuid });
        }
        if (hasIndex > -1) {
          selectedRowKeys.push(hasIndex);
        }
      });
      this.selectedRowKeys = selectedRowKeys;
    },
    addTableItem() {
      this.form.printContents.push({
        id: generateId(),
        printTemplate: {}
      });
      this.$forceUpdate();
    },
    delTableItems() {
      if (this.selectedRows.length == 0) {
        this.$message.error('请选择删除的项');
        return false;
      }
      const newData = filter(this.form.printContents, (item, index) => {
        let hasIndex = -1;
        if (item.id) {
          hasIndex = findIndex(this.selectedRows, { id: item.id });
        } else {
          hasIndex = findIndex(this.selectedRows, { uuid: item.uuid });
        }
        return hasIndex == -1;
      });
      this.form.printContents = newData;
      this.selectedRowKeys = [];
      this.selectedRows = [];
    },
    upTableItems() {
      if (this.selectedRows.length == 0) {
        this.$message.error('请选择上移的项');
        return false;
      }
      each(sortBy(this.selectedRowKeys), (item, index) => {
        if (item > 0) {
          const currentItem = this.form.printContents[item];
          this.form.printContents.splice(item, 1);
          this.form.printContents.splice(item - 1, 0, currentItem);
        }
      });
      this.resetTableSelectKey();
    },
    downTableItems() {
      if (this.selectedRows.length == 0) {
        this.$message.error('请选择下移的项');
        return false;
      }
      each(this.selectedRowKeys.reverse(), (item, index) => {
        if (item < this.form.printContents.length - 1) {
          const currentItem = this.form.printContents[item];
          this.form.printContents.splice(item + 2, 0, currentItem);
          this.form.printContents.splice(item, 1);
        }
      });
      this.resetTableSelectKey();
    },
    openModal(record, index) {
      if (this.form.templateType) {
        this.editData = JSON.parse(JSON.stringify(record));
        this.eidtIndex = index;
        if (this.form.templateType == 'htmlType') {
          this.formChange(this.editData.formUuid);
          if (!this.editData.printContent) {
            this.getPrintContent();
          }
          this.htmlModalVisible = true;
          this.$nextTick(() => {
            if (this.$refs.quillEditor) {
              this.editor = this.$refs.quillEditor;
            }
            if (this.editData.printContent) {
              this.editor.setHtml(decodeURIComponent(this.editData.printContent));
            }
          });
        } else {
          this.modalVisible = true;
          this.$nextTick(() => {
            if (this.$refs.uploadPrintTemplateChangeRef) {
              this.$refs.uploadPrintTemplateChangeRef.setValue(this.editData.fileUuid);
            }
          });
        }
      } else {
        this.$message.error('请在基本信息栏位内选择模板类型');
      }
    },
    onPrintContentChange(value) {
      this.$set(this.editData, 'printContent', value);
    },
    printTemplateChange(fileList) {
      this.editData.fileUuid = fileList.length ? fileList[0].dbFile.fileID : '';
    },
    handleOk() {
      this.form.printContents[this.eidtIndex] = JSON.parse(JSON.stringify(this.editData));
      this.modalVisible = false;
      this.htmlModalVisible = false;
    },
    saveFormMethod(callback) {
      this.saveForm(false, callback);
    },
    saveNewVersion() {
      this.saveForm(true);
    },
    saveForm(newVersion, callback) {
      console.log('保存表单');
      this.$refs.form.validate(valid => {
        if (valid) {
          this.beforeSaveReq(newVersion, callback);
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    validateName(rule, value, callback) {
      if ((value && value.indexOf('/') > -1) || value.indexOf('\\') > -1) {
        callback('打印模板名称不要有/或\\');
        return false;
      }
      callback();
    },
    beforeSaveReq(newVersion, callback) {
      let item = this.form;
      if (item.i18n) {
        let i18ns = [];
        for (let locale in item.i18n) {
          for (let key in item.i18n[locale]) {
            if (item.i18n[locale][key]) {
              i18ns.push({
                locale: locale,
                content: item.i18n[locale][key],
                code: key
              });
            }
          }
        }
        item.i18ns = i18ns;
      }
      let bean = JSON.parse(JSON.stringify(this.form));
      each(bean.printContents, (item, index) => {
        let i = this.printContentsUuid.indexOf(item.uuid);
        if (i > -1) {
          this.printContentsUuid.splice(i, 1);
        } else {
          item.uuid = null;
        }
      });
      if (!bean.systemUnitId) {
        bean.systemUnitId = this._$USER.systemUnitId;
      }
      this.saveFormData(bean, newVersion, callback);
    },
    saveFormData(bean, newVersion, callback) {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'printTemplateService',
          methodName: 'saveBean',
          args: JSON.stringify([bean, newVersion]),
          validate: false,
          argTypes: []
        })
        .then(({ data }) => {
          if (data.code == 0) {
            this.activeTab = 1;
            if (typeof callback !== 'function') {
              this.initFormData();
            }
            this.$message.success('保存成功');
            this.pageContext.emitEvent(`refetchPrintTemplateManageTableByClassifyUuid`);
            if (this.$widgetDrawerContext && typeof callback !== 'function') {
              this.$widgetDrawerContext.close();
            }
            if (typeof callback === 'function') {
              callback();
            }
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    }
  }
};
</script>
<style lang="less" scoped>
.help-block {
  display: block;
  margin-top: 5px;
  margin-bottom: 10px;
  color: #a6a6a6;
  font-size: 12px;
  line-height: var(--w-line-height);
}
</style>
