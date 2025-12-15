<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <!-- 仅有一个tab时加上class="one-tab",不是请移除 -->
      <a-tabs default-active-key="1" class="one-tab">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <a-popover placement="left">
                <template slot="content">把页面区域分为 n 列</template>
                <label>
                  列数
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </label>
              </a-popover>
            </template>

            <a-input-number v-model="widget.configuration.column" :min="1" :max="12" />
          </a-form-model-item>

          <a-form-model-item label="允许用户自定义排版">
            <a-switch v-model="widget.configuration.enableUserCustom" />
          </a-form-model-item>
          <a-form-model-item label="操作栏展示" v-if="widget.configuration.enableUserCustom">
            <a-radio-group v-model="widget.configuration.customBarDisplayPosition" button-style="solid" size="small">
              <a-radio-button value="top">顶部</a-radio-button>
              <a-radio-button value="rightPopover">右侧贴别浮层</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import { CELL_HEIGHT } from '../constant';
export default {
  name: 'WidgetGridstackConfiguration',
  inject: ['pageContext'],
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {},
  configuration(widget) {
    return {
      enableUserCustom: false,
      column: 2,
      customBarDisplayPosition: 'top',
      gridItems: [
        {
          title: '单元格',
          category: 'basicContainer',
          wtype: 'GridstackItem',
          id: generateId(),
          configuration: {
            style: {
              borderRadius: 0
            },
            itemPosition: {
              w: 2,
              x: 0,
              y: 0,
              h: 12
            },
            widgets: []
          }
        },
        {
          title: '单元格',
          category: 'basicContainer',
          wtype: 'GridstackItem',
          id: generateId(),
          configuration: {
            style: { borderRadius: 0 },
            itemPosition: {
              x: 0,
              y: 1,
              w: 1,
              h: 12
            },
            widgets: []
          }
        },
        {
          title: '单元格',
          category: 'basicContainer',
          wtype: 'GridstackItem',
          id: generateId(),
          configuration: {
            style: { borderRadius: 0 },
            itemPosition: {
              x: 1,
              y: 1,
              w: 1,
              h: 12
            },
            widgets: []
          }
        }
      ]
    };
  }
};
</script>
