<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item label="布局风格">
            <LayoutSelect v-model="widget.configuration.layoutType" />
          </a-form-model-item>

          <a-form-model-item label="区域显示">
            <a-button-group size="small">
              <a-button :type="wHeaderConf.visible ? 'primary' : 'default'" @click="wHeaderConf.visible = !wHeaderConf.visible">
                头部
              </a-button>
              <a-button
                v-show="
                  widget.configuration.layoutType == 'siderTopMiddleBottom' ||
                  widget.configuration.layoutType == 'topMiddleSiderBottom' ||
                  widget.configuration.layoutType == 'topMiddleRightSiderBottom'
                "
                :type="wSiderConf.visible ? 'primary' : 'default'"
                @click="wSiderConf.visible = !wSiderConf.visible"
              >
                侧边
              </a-button>
              <a-button :type="wFooterConf.visible ? 'primary' : 'default'" @click="wFooterConf.visible = !wFooterConf.visible">
                底部
              </a-button>
            </a-button-group>
          </a-form-model-item>
          <!-- <a-form-model-item label="高度自动">
             <a-switch v-model="widget.configuration.content.configuration.autoHeight" />
          </a-form-model-item> -->
          <a-form-model-item label="区域固定" v-if="vShowStickOption">
            <a-button-group size="small">
              <a-button
                v-if="wHeaderConf.visible"
                :type="wHeaderConf.stickyOnTop ? 'primary' : 'default'"
                @click="wHeaderConf.stickyOnTop = !wHeaderConf.stickyOnTop"
              >
                头部
              </a-button>

              <a-button
                v-if="wFooterConf.visible"
                :type="wFooterConf.stickyOnBottom ? 'primary' : 'default'"
                @click="wFooterConf.stickyOnBottom = !wFooterConf.stickyOnBottom"
              >
                底部
              </a-button>
            </a-button-group>
          </a-form-model-item>
          <a-form-model-item label="logo">
            <ImageLibrary v-model="widget.configuration.header.configuration.logo" width="100%" :height="100" :tipVisible="true" />
          </a-form-model-item>
          <a-form-model-item label="logo位置">
            <a-radio-group size="small" v-model="widget.configuration.logoPosition" button-style="solid">
              <a-radio-button value="header">顶部区</a-radio-button>
              <a-radio-button value="sider">侧边区</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="2" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import LayoutSelect from './layout-select.vue';
import { generateId } from '@framework/vue/utils/util';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';

export default {
  name: 'WidgetLayoutConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return { layoutPositionVisible: ['header', 'sider', 'footer'] };
  },

  beforeCreate() {},
  components: { LayoutSelect, ImageLibrary },
  computed: {
    vShowStickOption() {
      return (
        this.widget.configuration.layoutType == 'topMiddleBottom' ||
        this.widget.configuration.layoutType == 'siderTopMiddleBottom' ||
        ((this.widget.configuration.layoutType == 'topMiddleSiderBottom' ||
          this.widget.configuration.layoutType == 'topMiddleRightSiderBottom') &&
          !this.widget.configuration.sider.configuration.visible)
      );
    },
    wContentConf() {
      return this.widget.configuration.content.configuration;
    },
    wFooterConf() {
      return this.widget.configuration.footer.configuration;
    },
    wSiderConf() {
      return this.widget.configuration.sider.configuration;
    },
    wHeaderConf() {
      return this.widget.configuration.header.configuration;
    }
  },
  created() {},
  methods: {
    // 生成组件定义数据保存
    getWidgetDefinitionElements(widget) {
      return [
        {
          wtype: widget.wtype,
          id: widget.id,
          title: widget.title,
          main: !!widget.main, // 是否被标记为主组件，用于主布局标记
          definitionJson: JSON.stringify(widget)
        }
      ];
    }
  },
  mounted() {},
  configuration() {
    return {
      layoutType: 'topMiddleBottom',
      logoPosition: 'header',
      header: {
        wtype: 'WidgetLayoutHeader',
        title: '顶部区',
        id: `layout-header-${generateId()}`,
        configuration: {
          logo: undefined,
          visible: true,
          backgroundColorType: 'primary-color', //   浅色 light \ 主题色 primary-color
          widgets: [],
          stickyOnTop: false // 是否置顶头部
        }
      },
      content: {
        wtype: 'WidgetLayoutContent',
        title: '内容区',
        id: `layout-content-${generateId()}`,
        configuration: {
          widgets: [],
          autoHeight: true,
          contentAsTabs: false,
          defaultTabTitle: undefined,
          defaultTabClosable: false
        }
      },
      sider: {
        wtype: 'WidgetLayoutSider',
        title: '侧边区',
        id: `layout-sider-${generateId()}`,
        configuration: {
          backgroundColorType: 'dark', // 深色 dark \ 浅色 light \ 主题色 primary-color
          darkBgColorTypeLogo: '/static/images/logo_vertical _gray.png',
          visible: true,
          collapsible: false,
          widgets: []
        }
      },
      footer: {
        wtype: 'WidgetLayoutFooter',
        title: '底部区',
        id: `layout-footer-${generateId()}`,
        configuration: {
          visible: true,
          stickyOnBottom: false,
          widgets: []
        }
      }
    };
  }
};
</script>
