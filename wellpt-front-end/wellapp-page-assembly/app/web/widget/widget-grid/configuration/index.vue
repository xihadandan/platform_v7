<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <!-- 仅有一个tab时加上class="one-tab" -->
      <a-tabs default-active-key="1" class="one-tab">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item label="类型">
            <a-radio-group v-model="widget.configuration.rowType" button-style="solid" size="small">
              <a-radio-button value="default">默认</a-radio-button>
              <a-radio-button value="flex">弹性布局</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              高度类型
              <a-tooltip placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 8] }">
                <template slot="title">
                  <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                    <li>多列等高目前只支持栅格下的表格组件</li>
                    <li v-if="designer.terminalType == 'mobile'">多列等高，移动端使用弹性布局实现，请设置弹性布局列宽</li>
                  </ul>
                </template>
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-tooltip>
            </template>
            <a-radio-group v-model="widget.configuration.heightType" button-style="solid" size="small" @change="changeHeightType">
              <a-radio-button value="default">默认</a-radio-button>
              <a-radio-button value="define">自定义</a-radio-button>
              <a-radio-button value="colsEqualHeight">多列等高</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <StyleConfiguration
            v-if="widget.configuration.heightType == 'define'"
            :widget="widget"
            :setWidthHeight="[false, true]"
            :editBlock="false"
            :setMarginPadding="false"
          ></StyleConfiguration>
          <a-form-model-item v-if="widget.configuration.cols.length > 1">
            <template slot="label">
              <a-tooltip placement="top">
                <template slot="title">
                  <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                    <li>适用于所有栅格在一行的情况，多行栅格不适用</li>
                    <li>分隔线开启后，高度默认的情况下，栅格高度会被外层限制，最高为屏幕可视高度</li>
                    <li>每个栅格内仅有一个组件时，该组件高度100%</li>
                  </ul>
                </template>
                分隔线
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-tooltip>
            </template>
            <a-switch v-model="widget.configuration.dividingLine" />
          </a-form-model-item>
          <template v-if="widget.configuration.dividingLine && !rowDisplay">
            <a-form-model-item>
              <template slot="label">
                <a-tooltip placement="top">
                  <template slot="title">
                    <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                      <li>仅第一个和最后一个栅格可折叠</li>
                      <li>左折叠即第一个栅格折叠</li>
                      <li>右折叠即最后一个栅格折叠</li>
                      <li>弹性布局时，可折叠栅格需配置宽度</li>
                    </ul>
                  </template>
                  可折叠
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </a-tooltip>
              </template>
              <a-switch v-model="widget.configuration.collaspe" />
            </a-form-model-item>
            <template v-if="widget.configuration.collaspe">
              <a-form-model-item label="折叠方式">
                <a-button-group size="small">
                  <a-button
                    :type="widget.configuration.collaspeType.indexOf(item.value) > -1 ? 'primary' : 'default'"
                    v-for="(item, index) in collaspeOptions"
                    :key="index"
                    @click="changeCollaspeType(item)"
                  >
                    {{ item.label }}
                  </a-button>
                </a-button-group>
              </a-form-model-item>
            </template>
          </template>
          <!-- 样式有主题控制 -->
          <!-- <a-form-model-item label="列间隔">
            <a-input-number v-model="widget.configuration.gutter" />
          </a-form-model-item> -->
          <a-form-model-item v-if="designer.terminalType == 'mobile'">
            <template slot="label">
              <a-tooltip placement="top">
                <template slot="title">
                  <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                    <li>开启后，忽略列宽，每个列都按行结构展示</li>
                    <li>行结构展示开启后，高度除自定义具体高度外，其他按照内容自适应展示</li>
                  </ul>
                </template>
                行展示
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-tooltip>
            </template>
            <a-switch v-model="widget.configuration.uniConfiguration.rowDisplay" />
          </a-form-model-item>
          <div>
            <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1">
              <a-collapse-panel key="1" :header="rowDisplay ? '行设置' : (designer.terminalType == 'pc' ? '' : '移动端') + '列设置'">
                <div v-if="widget.configuration.rowType != 'flex'">
                  <template v-for="(col, cindex) in widget.configuration.cols">
                    <a-form-model-item :key="'form-item' + col.id">
                      <template slot="label">列{{ cindex + 1 }}{{ rowDisplay ? '' : '占位格数' }}</template>
                      <a-input-number v-model="col.configuration.span" v-show="!rowDisplay" />
                      <a-button size="small" type="link" class="ant-btn-dangerous" @click.stop="removeCol(cindex)" title="删除">
                        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                      </a-button>
                    </a-form-model-item>
                  </template>
                  <a-form-model-item :wrapper-col="{ style: { textAlign: 'left' } }">
                    <a-button size="small" type="link" @click.stop="addCol()" style="--w-button-padding-lr: 0">
                      <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                      添加列
                    </a-button>
                  </a-form-model-item>
                </div>
                <div v-if="widget.configuration.rowType == 'flex'">
                  <template v-for="(col, cindex) in widget.configuration.cols">
                    <a-form-model-item :label="'列' + (cindex + 1) + (rowDisplay ? '' : '宽度')" :key="'form-item' + col.id">
                      <template v-if="!rowDisplay">
                        <a-input-number v-model="col.configuration.flex" v-if="designer.terminalType == 'pc'" />
                        <a-input-number v-model="col.configuration.uniflex" v-else />
                      </template>
                      <a-button size="small" type="link" class="ant-btn-dangerous" @click.stop="removeCol(cindex)" title="删除">
                        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                      </a-button>
                    </a-form-model-item>
                  </template>
                  <a-form-model-item :wrapper-col="{ style: { textAlign: 'left' } }">
                    <a-button size="small" type="link" @click.stop="addCol()" style="--w-button-padding-lr: 0">
                      <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                      添加列
                    </a-button>
                  </a-form-model-item>
                </div>
              </a-collapse-panel>
            </a-collapse>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'WidgetGridConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    return {
      collaspeOptions: [
        { label: '左折叠', value: 'left' },
        { label: '右折叠', value: 'right' }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    collaspeType() {
      if (this.widget.configuration.cols.length == 2) {
        if (this.widget.configuration.collaspeType.length == 2) {
          this.widget.configuration.collaspeType.pop();
        }
        return 'radio';
      } else if (this.widget.configuration.cols.length > 2) {
        return 'multiple';
      }
      return 'checkbox';
    },
    // 移动端,行展示
    rowDisplay() {
      return this.designer.terminalType == 'mobile' && this.widget.configuration.uniConfiguration.rowDisplay;
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('heightType')) {
      if (this.widget.configuration.colsEqualHeight) {
        this.$set(this.widget.configuration, 'heightType', 'colsEqualHeight');
      } else {
        this.$set(this.widget.configuration, 'heightType', 'default');
      }
    }
    if (!this.widget.configuration.hasOwnProperty('dividingLine')) {
      this.$set(this.widget.configuration, 'dividingLine', false);
      this.$set(this.widget.configuration, 'collaspe', false);
      this.$set(this.widget.configuration, 'collaspeType', ['left']);
    }
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', {
        rowDisplay: true
      });
    }
  },
  methods: {
    removeCol(cindex) {
      this.widget.configuration.cols.splice(cindex, 1);
    },
    addCol() {
      this.widget.configuration.cols.push({
        wtype: 'GridCol',
        category: 'basicContainer',
        id: `grid-col-${generateId()}`,
        title: '列',
        configuration: {
          span: 12,
          widgets: []
        }
      });
    },

    getFunctionElements(wgt, EWidgets) {
      return {};
    },

    getWidgetDefinitionElements(wgt, EWidgets) {
      let wgtDefinitionElements = [
        {
          wtype: wgt.wtype,
          id: wgt.id,
          definitionJson: JSON.stringify(wgt)
        }
      ];

      return wgtDefinitionElements;
    },
    changeHeightType() {
      if (this.widget.configuration.heightType == 'colsEqualHeight') {
        this.widget.configuration.colsEqualHeight = true;
      } else {
        this.widget.configuration.colsEqualHeight = false;
      }
    },
    changeCollaspeType(item) {
      let value = item.value;
      const index = this.widget.configuration.collaspeType.indexOf(value);
      if (this.collaspeType === 'radio') {
        this.widget.configuration.collaspeType = [item.value];
      } else {
        if (index > -1) {
          // 如果已选中，则移除
          this.widget.configuration.collaspeType.splice(index, 1);
        } else {
          // 如果未选中，则添加
          this.widget.configuration.collaspeType.push(value);
        }
      }
    }
  },
  mounted() {},
  configuration() {
    return {
      rowType: 'default',
      collaspe: false,
      collaspeType: ['left'],
      heightType: 'default',
      dividingLine: false,
      colsEqualHeight: false,
      cols: [
        {
          wtype: 'GridCol',
          category: 'basicContainer',
          id: `grid-col-${generateId()}`,
          title: '列',
          configuration: {
            span: 12,
            widgets: []
          }
        },
        {
          wtype: 'GridCol',
          category: 'basicContainer',
          id: `grid-col-${generateId()}`,
          title: '列',
          configuration: {
            span: 12,
            widgets: []
          }
        }
      ],
      uniConfiguration: {
        rowDisplay: true // 单独行展示
      }
    };
  }
};
</script>
