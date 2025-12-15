<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="列数">
            <a-input-number v-model="widget.configuration.numColumns" :max="4" :min="1" />
          </a-form-model-item>
          <a-form-model-item label="显示边框">
            <a-switch v-model="widget.configuration.bordered" />
          </a-form-model-item>
          <a-form-model-item label="滑动视图">
            <a-switch v-model="widget.configuration.enableSwiper" />
          </a-form-model-item>
          <a-form-model-item label="视图行数" v-if="widget.configuration.enableSwiper">
            <a-input-number v-model="widget.configuration.swiperItemRowCount" :min="1" />
          </a-form-model-item>
          <a-form-model-item label="JS模块">
            <JsModuleSelect v-model="widget.configuration.jsModules" />
          </a-form-model-item>
          <a-form-model-item label="单元格信息">
            <WidgetDesignDrawer :id="'widgetUniGridViewConfig' + widget.id" title="单元格信息" :designer="designer" width="800px">
              <a-button type="primary" size="small">开始配置</a-button>
              <template slot="content">
                <WidgetUniGridViewColumnConfiguration
                  :pageOptions="pageOptions"
                  :widget="widget"
                  :designer="designer"
                  ref="columns"
                ></WidgetUniGridViewColumnConfiguration>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1">
            <a-collapse-panel key="1" header="样式设置">
              <a-form-model-item label="结构">
                <a-radio-group size="small" v-model="widget.configuration.displayStyle">
                  <a-radio value="iconLeft_textRight">图标左、文字右</a-radio>
                  <a-radio value="textLeft_IconRight">文字左、图标右</a-radio>
                  <a-radio value="iconTop_textBottom">图标上、文字下（居中）</a-radio>
                  <a-radio value="textTop_iconBottom">文字上居左、图标下居右</a-radio>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="组合样式">
                <WidgetDesignDrawer :id="'groupConfigureSet' + widget.id" title="组合样式" :width="600" :designer="designer">
                  <a-button size="small" title="组合样式">
                    <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                  </a-button>
                  <template slot="content">
                    <WidgetUniGridViewStyleConfiguration
                      :target="widget.configuration"
                      param="groupStyle"
                      type=""
                      :isItem="false"
                    ></WidgetUniGridViewStyleConfiguration>
                  </template>
                </WidgetDesignDrawer>
              </a-form-model-item>
              <a-form-model-item label="图标样式">
                <WidgetDesignDrawer :id="'iconConfigureSet' + widget.id" title="图标样式" :width="600" :designer="designer">
                  <a-button size="small" title="图标样式">
                    <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                  </a-button>
                  <template slot="content">
                    <WidgetUniGridViewStyleConfiguration
                      :target="widget.configuration"
                      param="iconStyle"
                      :isItem="false"
                    ></WidgetUniGridViewStyleConfiguration>
                  </template>
                </WidgetDesignDrawer>
              </a-form-model-item>
              <a-form-model-item label="标题样式">
                <WidgetDesignDrawer :id="'iconConfigureSet' + widget.id" title="标题样式" :width="600" :designer="designer">
                  <a-button size="small" title="标题样式">
                    <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                  </a-button>
                  <template slot="content">
                    <WidgetUniGridViewStyleConfiguration
                      :target="widget.configuration"
                      param="titleStyle"
                      type="title"
                      :isItem="false"
                    ></WidgetUniGridViewStyleConfiguration>
                  </template>
                </WidgetDesignDrawer>
              </a-form-model-item>
              <a-form-model-item label="自定义样式">
                <a-textarea
                  placeholder="请输入自定义样式"
                  :autosize="{ minRows: 4, maxRows: 8 }"
                  v-model="widget.configuration.mainStyle"
                  allow-clear
                ></a-textarea>
              </a-form-model-item>
            </a-collapse-panel>
          </a-collapse>
        </a-form-model>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import WidgetUniGridViewColumnConfiguration from './column-configuration.vue';
import WidgetUniGridViewStyleConfiguration from './style-configuration.vue';
export default {
  name: 'WidgetUniGridViewConfiguration',
  inject: ['appId', 'subAppIds'],
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      pageOptions: []
    };
  },

  beforeCreate() {},
  components: { WidgetUniGridViewColumnConfiguration, WidgetUniGridViewStyleConfiguration },
  computed: {},
  created() {},
  methods: {
    fetchPageOptions() {
      let appIds = [].concat(this.appId || []).concat(this.subAppIds || []);
      if (appIds.length) {
        $axios
          .post(`/proxy/api/webapp/page/definition/queryLatestPageDefinitionByAppIds`, appIds)
          .then(({ data }) => {
            this.pageOptions.splice(0, this.pageOptions.length);
            if (data.data) {
              for (let d of data.data) {
                if (d.wtype == 'vUniPage') {
                  this.pageOptions.push({
                    label: `${d.name} v${d.version}`,
                    value: d.id,
                    wtype: d.wtype,
                    uuid: d.uuid
                  });
                }
              }
            }
          })
          .catch(error => {});
      }
    }
  },
  mounted() {
    this.fetchPageOptions();
  },
  configuration() {
    let columns = [];
    for (let i = 0, len = 9; i < len; i++) {
      columns.push({
        uuid: generateId(),
        text: undefined,
        hidden: false,
        eventHandler: {
          actionType: undefined,
          pageType: 'page',
          pageId: undefined,
          url: undefined,
          eventParams: []
        },
        icon: {
          src: undefined,
          color: undefined,
          fontSize: 38
        }
      });
    }
    return {
      jsModules: [],
      bordered: true,
      logoFilePath: '',
      numColumns: 3,
      enableSwiper: false,
      swiperItemRowCount: 3,
      showAsSingle: false,
      columns,
      displayStyle: 'iconTop_textBottom',
      groupStyle: {
        backgroundStyle: {
          backgroundColor: '#ffffff', // 白底
          backgroundImage: undefined,
          backgroundImageInput: undefined,
          bgImageUseInput: false,
          backgroundRepeat: undefined,
          backgroundPosition: undefined,
          backgroundSize: undefined
        },
        borderRadius: 8,
        padding: '',
        margin: '',
        size: '',
        color: '',
        borderColor: ''
      },
      iconStyle: {
        backgroundStyle: {
          backgroundColor: '#ffffff', // 白底
          backgroundImage: undefined,
          backgroundImageInput: undefined,
          bgImageUseInput: false,
          backgroundRepeat: undefined,
          backgroundPosition: undefined,
          backgroundSize: undefined
        },
        borderRadius: 8,
        padding: '',
        margin: '',
        size: '',
        color: '',
        borderColor: '',
        width: '',
        height: ''
      },
      titleStyle: {
        padding: '',
        size: '',
        color: '',
        subColor: '',
        subSize: ''
      }
    };
  }
};
</script>
