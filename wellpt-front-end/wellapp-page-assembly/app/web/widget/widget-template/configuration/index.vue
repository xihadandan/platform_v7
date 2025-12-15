<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item label="编码">
            <a-input v-model="widget.configuration.code" />
          </a-form-model-item>
          <a-form-model-item label="模板类型">
            <a-select
              :options="typeOptions"
              :style="{ width: '100%' }"
              v-model="widget.configuration.type"
              @change="onTypeChange"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="内容来源">
            <a-select :options="srcOptions" :style="{ width: '100%' }" v-model="widget.configuration.sourceType"></a-select>
          </a-form-model-item>

          <a-form-model-item label="项目代码" v-show="widget.configuration.sourceType === 'projectCode'">
            <!-- <a-select
              :options="widget.configuration.type === 'vue' ? vueTemplateOptions : htmlTemplateOptions"
              :style="{ width: '100%' }"
              v-model="widget.configuration.templateName"
              :filter-option="filterOption"
              :showSearch="true"
              allowClear
            ></a-select> -->
            <a-tree-select
              :treeIcon="true"
              v-model="widget.configuration.templateName"
              show-search
              style="width: 100%"
              :dropdown-style="{ maxHeight: '400px', maxWidth: '260px' }"
              :treeData="vTemplateTreeData"
              allow-clear
            >
              <template slot="titleSlot" slot-scope="scope">
                <div>
                  <img :src="VueSvg" style="width: 14px; height: 14px" />
                  {{ scope.value }}
                </div>
              </template>
            </a-tree-select>
          </a-form-model-item>
          <a-form-model-item label="模板内容" v-show="widget.configuration.sourceType === 'codeEditor'">
            <WidgetDesignDrawer id="widgetTemplateCodeDrawer" title="代码" :designer="designer">
              <a-button icon="code">编写代码</a-button>
              <template slot="content">
                <a-tabs default-active-key="1" tab-position="left">
                  <a-tab-pane key="1" tab="模板">
                    <WidgetCodeEditor
                      lang="html"
                      width="500px"
                      :title="widget.configuration.type === 'vue' ? '编写vue代码' : null"
                      :hideError="widget.configuration.type === 'vue'"
                      v-model="widget.configuration.templateContent"
                    ></WidgetCodeEditor>
                  </a-tab-pane>
                  <a-tab-pane key="2" tab="数据">
                    <WidgetCodeEditor width="500px" lang="json" v-model="widget.configuration.templateData"></WidgetCodeEditor>
                  </a-tab-pane>
                  <a-tab-pane key="3" tab="方法">
                    <a-button @click="onClickAddMethod" size="small" icon="plus" type="link">添加方法</a-button>
                    <a-collapse
                      :bordered="false"
                      accordion
                      v-if="widget.configuration.templateMethods != undefined"
                      :activeKey="collapseMethodActiveKey"
                    >
                      <a-collapse-panel v-for="(method, i) in widget.configuration.templateMethods" :key="'tmethod_' + i">
                        <template slot="header">
                          <a-input
                            v-model="method.name"
                            @click.stop="() => {}"
                            size="small"
                            style="margin-left: 45px; width: 50%"
                            placeholder="请输入方法名"
                          >
                            <a-icon slot="prefix" type="code" />
                          </a-input>
                        </template>
                        <WidgetCodeEditor v-model="method.content" width="500px"></WidgetCodeEditor>
                      </a-collapse-panel>
                    </a-collapse>
                  </a-tab-pane>
                </a-tabs>
              </template>
            </WidgetDesignDrawer>
            <!-- <WidgetCodeEditor
              lang="html"
              :title="widget.configuration.type === 'vue' ? '编写vue代码' : null"
              :hideError="widget.configuration.type === 'vue'"
              @save="value => (widget.configuration.templateContent = value)"
              :value="widget.configuration.templateContent"
            >
              <a-button icon="code">编写代码</a-button>
              <template slot="extras">
                <WidgetCodeEditor lang="json" title="页面变量JSON" @save="value => saveVueTemplateVar(value)" :value="templateVarString">
                  <a-button icon="code" type="link" size="small">设置变量</a-button>
                </WidgetCodeEditor>
              </template>
            </WidgetCodeEditor> -->
          </a-form-model-item>
          <a-form-model-item
            label="JS模块"
            v-show="!(widget.configuration.sourceType === 'projectCode' && widget.configuration.type === 'vue')"
          >
            <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetTemplateDevelopment" />
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer" />
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import VueSvg from './vue.svg';

export default {
  name: 'WidgetTemplateConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      vueTemplateOptions: [],
      htmlTemplateOptions: [],
      collapseMethodActiveKey: 'tmethod_0',
      typeOptions: [
        { label: 'vue', value: 'vue' },
        { label: 'html', value: 'html' }
      ],
      srcOptions: [
        { label: '代码编辑器', value: 'codeEditor' },
        { label: '项目代码', value: 'projectCode' }
      ],
      vTemplateTreeData: [],
      VueSvg
    };
  },
  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (this.widget.configuration.templateMethods == undefined) {
      this.$set(this.widget.configuration, 'templateMethods', []);
    }
  },
  methods: {
    onClickAddMethod() {
      this.widget.configuration.templateMethods.push({ name: '', content: undefined });
      this.collapseMethodActiveKey = 'tmethod_' + (this.widget.configuration.templateMethods.length - 1);
    },

    onTypeChange() {
      this.widget.configuration.templateName = undefined;
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }
  },
  beforeMount() {
    let _this = this;
    $axios.get('/pagerender/viewTemplateQuery').then(({ data }) => {
      let options = [];
      for (let i = 0, len = data.length; i < len; i++) {
        if (data[i].endsWith('.html')) {
          options.push({ label: data[i], value: data[i], title: data[i] });
        }
      }
      _this.htmlTemplateOptions = options;
    });

    // 获取vue模板实例
    let regExp = /wellapp[\w|-]+\/app\/web\/template.+\.vue$/;
    let nodeMap = {};
    for (let key in window.Vue.options.components) {
      let comp = window.Vue.options.components[key];
      let META = comp.META;
      let label = undefined;
      if (META) {
        label = META.fileName.replace('./', '');
      } else if (comp.options && comp.options.__file && regExp.test(comp.options.__file)) {
        label = comp.options.__file.substr(comp.options.__file.lastIndexOf('/') + 1);
      }
      if (label) {
        let paths = label.split('/'),
          key = '';
        for (let i = 0, length = paths.length; i < length; i++) {
          let key = []
            .concat(paths)
            .splice(0, i + 1)
            .join('/');

          let current = {
            value: paths[i],
            label: paths[i],
            level: i + 1,
            selectable: i == length - 1,
            children: i != length - 1 ? [] : undefined
          };
          if (i == length - 1) {
            current.value = paths[i].replace('.vue', '');
            current.scopedSlots = { title: 'titleSlot' };
          }

          if (nodeMap[key] == undefined) {
            nodeMap[key] = current;
            if (i == 0) {
              this.vTemplateTreeData.push(current);
            }
          }
          if (i > 0) {
            // 拼接到父级上
            let parentKey = [].concat(paths).splice(0, i).join('/'),
              childKeys = [];
            nodeMap[parentKey].children.forEach(element => {
              childKeys.push(element.value);
            });
            if (!childKeys.includes(current.value)) {
              nodeMap[parentKey].children.push(current);
            }
          }
        }

        this.vueTemplateOptions.push({
          label,
          value: key,
          title: label
        });
      }

      // '/a/b/c'
      // if (META) {
      //   this.vueTemplateOptions.push({
      //     label: META.fileName.replace('./', ''),
      //     value: key,
      //     title: META.fileName.replace('./', '')
      //   });
      //   continue;
      // } else if (comp.options && comp.options.__file && regExp.test(comp.options.__file)) {
      //   this.vueTemplateOptions.push({
      //     label: comp.options.__file.substr(comp.options.__file.lastIndexOf('/') + 1),
      //     value: key,
      //     title: comp.options.__file.substr(comp.options.__file.lastIndexOf('/') + 1)
      //   });
      // }
    }
    console.log(this.vTemplateTreeData);
  },
  mounted() {},
  configuration() {
    return {
      title: '',
      code: undefined, //编码
      templateName: undefined,
      type: 'vue', // vue or html
      sourceType: 'projectCode',
      templateContent: null,
      templateVar: {}
    };
  }
};
</script>
