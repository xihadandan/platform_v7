<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item label="页面设置">
            <a-radio-group v-model="widget.configuration.pageSourceType" button-style="solid" size="small" @change="onChangeRowDataFrom">
              <a-radio-button value="constant">指定页面</a-radio-button>
              <a-radio-button value="expr">动态页面</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-if="widget.configuration.pageSourceType == 'constant'" label="选择页面">
            <a-row type="flex">
              <a-col flex="auto" :style="{ width: widget.configuration.pageId ? 'calc(100% - 80px)' : '100%' }">
                <a-select
                  allowClear
                  :filter-option="filterSelectOption"
                  :showSearch="true"
                  :style="{ width: '100%' }"
                  :options="pageOptions"
                  v-model="widget.configuration.pageId"
                  @change="(v, node) => onSelectPageChange(v, node)"
                ></a-select>
              </a-col>
              <a-col flex="70px" v-if="widget.configuration.pageId">
                <a-button type="link" @click.stop="onClickOpenDesignPage">
                  <Icon type="pticon iconfont icon-ptkj-chakanbiaodanxiangqing"></Icon>
                  查看
                </a-button>
              </a-col>
            </a-row>
          </a-form-model-item>
          <a-form-model-item v-if="widget.configuration.pageSourceType == 'expr'" label="返回页面ID">
            <FormulaEditor
              :widget="widget"
              :bind-to-configuration="widget.configuration"
              configKey="pageExpr"
              ref="formulaEditor"
              :enableFormulaFunction="false"
            />
          </a-form-model-item>
          <a-form-model-item label="错误页面自定义">
            <a-switch v-model="widget.configuration.errorCustomize" />
          </a-form-model-item>
          <template v-if="widget.configuration.errorCustomize">
            <a-form-model-item
              :label="{ '404': '404 页面', '403': '403 页面' }[errorConfig.code]"
              v-for="(errorConfig, i) in widget.configuration.errorTemplateConfig"
              :key="'errorTemplateConfig_' + i"
            >
              <a-form-model-item>
                <a-select :options="srcOptions" :style="{ width: '100%' }" v-model="errorConfig.sourceType"></a-select>
              </a-form-model-item>
              <a-form-model-item v-if="errorConfig.sourceType === 'projectCode'">
                <a-tree-select
                  :treeIcon="true"
                  v-model="errorConfig.templateName"
                  show-search
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', maxWidth: '260px' }"
                  :treeData="vTemplateTreeData"
                  allow-clear
                >
                  <template slot="titleSlot" slot-scope="scope">
                    <div>
                      {{ scope.value }}
                    </div>
                  </template>
                </a-tree-select>
              </a-form-model-item>
              <a-form-model-item v-else="errorConfig.sourceType === 'codeEditor'">
                <WidgetCodeEditor
                  lang="html"
                  width="231px"
                  title="编写vue代码"
                  :hideError="true"
                  v-model="errorConfig.template"
                ></WidgetCodeEditor>
              </a-form-model-item>
            </a-form-model-item>
          </template>
          <!-- <a-form-model-item label="是否进行权限控制">
            <a-switch v-model="widget.configuration.isPermissionControl" />
          </a-form-model-item> -->
        </a-form-model>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import { filterSelectOption } from '@framework/vue/utils/function';

export default {
  name: 'WidgetRefVpageConfiguration',
  inject: ['appId', 'subAppIds'],
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    return {
      pageOptions: [],
      pageUuidMap: {},
      typeOptions: [
        { label: 'vue', value: 'vue' },
        { label: 'html', value: 'html' }
      ],
      srcOptions: [
        { label: '代码编辑器', value: 'codeEditor' },
        { label: '项目代码', value: 'projectCode' }
      ],
      vTemplateTreeData: []
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    filterSelectOption,
    onClickOpenDesignPage() {
      let key = this.widget.configuration.pageUuid;
      if (key) {
        let page = this.pageUuidMap[key];
        window.open(`/${page.wtype == 'vUniPage' ? 'uni-' : ''}page-designer/index?uuid=${key}`, '_blank');
      }
    },
    onSelectPageChange(v, node) {
      this.widget.configuration.pageUuid = undefined;
      if (v) {
        let uuid = node.data.props.uuid;
        this.widget.configuration.pageUuid = uuid;
      }
    },
    fetchPageOptions() {
      let appIds = [].concat(this.appId || []).concat(this.subAppIds || []);
      if (appIds.length) {
        $axios
          .post(`/proxy/api/webapp/page/definition/queryLatestPageDefinitionByAppIds`, appIds)
          .then(({ data }) => {
            this.pageOptions.splice(0, this.pageOptions.length);
            this.pageUuidMap = {};
            if (data.data) {
              for (let d of data.data) {
                // if (d.wtype == 'vPage') {
                this.pageUuidMap[d.uuid] = d;
                this.pageOptions.push({
                  label: `${d.name} v${d.version}`,
                  value: d.id,
                  wtype: d.wtype,
                  uuid: d.uuid
                });
                // }
              }
            }
          })
          .catch(error => {});
      }
    },
    fetchVueTemplate() {
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
        }
      }
    }
  },
  mounted() {
    this.fetchPageOptions();
    this.fetchVueTemplate();
  },

  configuration() {
    return {
      pageId: undefined,
      isPermissionControl: true,
      pageSourceType: 'constant',
      errorCustomize: false,
      errorTemplateConfig: [
        {
          code: '404',
          sourceType: 'projectCode'
        },
        {
          code: '403',
          sourceType: 'projectCode'
        }
      ]
    };
  }
};
</script>
