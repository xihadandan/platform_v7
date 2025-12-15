<template>
  <a-form-model>
    <Scroll style="height: calc(100vh - 180px); padding-right: 10px">
      <a-form-model-item label="字段">
        <a-select
          v-model="column.dataIndex"
          :style="{ width: '100%' }"
          :showSearch="true"
          :filter-option="filterSelectOption"
          @change="onChangeColumnSelect"
          v-if="widget.configuration.rowDataFrom == undefined || widget.configuration.rowDataFrom !== 'developSource'"
        >
          <a-select-option v-for="(opt, i) in columnIndexOptions" :key="'col-option-' + i" :value="opt.value">
            {{ opt.label }}
            <a-tag style="position: absolute; right: 0px; top: 4px" @click.stop="() => {}">
              {{ opt.value }}
            </a-tag>
          </a-select-option>
        </a-select>
        <a-input v-model="column.dataIndex" v-if="widget.configuration.rowDataFrom === 'developSource'" />
      </a-form-model-item>
      <a-form-model-item label="标题">
        <a-input v-model="column.title">
          <template slot="addonAfter">
            <a-checkbox v-model="column.titleHidden">隐藏标题</a-checkbox>
            <WI18nInput
              v-show="!column.titleHidden"
              :widget="widget"
              :designer="designer"
              :code="column.id"
              :target="column"
              v-model="column.title"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="显示标题在内容上" v-if="widget.configuration.columnTitleHidden == true">
        <a-switch v-model="column.titleDisplayTop" />
      </a-form-model-item>

      <a-form-model-item label="主键">
        <a-switch v-model="column.primaryKey" @change="onChangePrimaryKey" />
      </a-form-model-item>
      <a-form-model-item label="列宽">
        <a-input-number v-model="column.width" :min="0" />
      </a-form-model-item>
      <!-- <a-form-model-item label="角色可访问">
      <RoleSelect v-model="column.role" />
    </a-form-model-item> -->
      <a-form-model-item label="是否显示">
        <a-switch
          :checked="!column.hidden"
          @change="
            checked => {
              column.hidden = !checked;
            }
          "
        />
      </a-form-model-item>
      <a-form-model-item label="用户自定义显示" class="item-lh" v-if="widget.configuration.enableCustomTable && !column.hidden">
        <a-radio-group size="small" v-model="column.customVisibleType" button-style="solid">
          <a-radio-button value="chooseVisible">
            <a-tooltip placement="topLeft">
              <template slot="title">
                <span>默认情况下显示, 可通过用户自定义表格列进行隐藏</span>
              </template>
              可选显示
            </a-tooltip>
          </a-radio-button>
          <a-radio-button value="mustVisible">
            <a-tooltip placement="topLeft">
              <template slot="title">
                <span>总是显示, 不可通过用户自定义表格列进行隐藏</span>
              </template>
              必须显示
            </a-tooltip>
          </a-radio-button>
          <a-radio-button value="defaultHidden">
            <a-tooltip placement="topLeft">
              <template slot="title">
                <span>默认情况下隐藏, 可通过用户自定义表格列进行显示</span>
              </template>
              默认隐藏
            </a-tooltip>
          </a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="列排序">
        <a-switch v-model="column.sortable" />
      </a-form-model-item>
      <a-form-model-item label="列提示">
        <a-row type="flex">
          <a-col flex="50px"><a-switch v-model="column.showTip" /></a-col>
          <a-col flex="auto">
            <a-input v-model="column.tipContent" v-show="column.showTip" placeholder="请输入提示内容">
              <template slot="addonAfter">
                <WI18nInput
                  v-show="column.showTip"
                  :widget="widget"
                  :designer="designer"
                  :code="column.id + '_tipContent'"
                  :target="column"
                  v-model="column.tipContent"
                />
              </template>
            </a-input>
          </a-col>
        </a-row>
      </a-form-model-item>
      <a-form-model-item label="标题对齐" class="item-lh">
        <a-radio-group size="small" v-model="column.titleAlign" button-style="solid">
          <a-radio-button value="left"><a-icon type="align-left" /></a-radio-button>
          <a-radio-button value="center"><a-icon type="align-center" /></a-radio-button>
          <a-radio-button value="right"><a-icon type="align-right" /></a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="内容对齐" class="item-lh">
        <a-radio-group size="small" v-model="column.contentAlign" button-style="solid">
          <a-radio-button value="left"><a-icon type="align-left" /></a-radio-button>
          <a-radio-button value="center"><a-icon type="align-center" /></a-radio-button>
          <a-radio-button value="right"><a-icon type="align-right" /></a-radio-button>
        </a-radio-group>
      </a-form-model-item>

      <a-form-model-item label="自定义样式">
        <a-textarea v-model="column.customStyle" />
      </a-form-model-item>
      <a-form-model-item label="超过宽度自动省略">
        <a-switch v-model="column.ellipsis" />
      </a-form-model-item>
      <a-form-model-item label="内容为空时显示">
        <a-input v-model="column.defaultContentIfNull" allow-clear>
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :code="column.id + '_defaultContentIfNull'"
              :target="column"
              v-model="column.defaultContentIfNull"
            />
          </template>
        </a-input>
      </a-form-model-item>

      <a-form-model-item label="渲染器">
        <WidgetDesignModal title="渲染器配置" :width="640" :maxHeight="550">
          <a-button type="link">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            <template v-if="column.renderFunction.type">
              {{ getRenderFunctionName(column.renderFunction.type) }}
            </template>
            <template v-else>配置</template>
          </a-button>
          <span v-show="column.renderFunction.type" @click.stop="() => {}">
            <a-divider type="vertical" @click.stop="() => {}" />
            <a-button type="link" size="small" icon="delete" @click.stop="removeColumnRenderFunction(column)"></a-button>
          </span>

          <template slot="content">
            <a-form-model :colon="false">
              <div>
                <a-select
                  :allowClear="true"
                  :showSearch="true"
                  :filter-option="filterSelectOption"
                  v-model="column.renderFunction.type"
                  @change="(val, vnode) => onSelectChangeRenderFunc(val, vnode, column.renderFunction)"
                >
                  <a-select-opt-group>
                    <span slot="label">
                      <a-icon type="desktop" />
                      客户端渲染
                    </span>

                    <a-select-option v-for="(opt, i) in clientRenderOptions" :value="opt.value" :key="opt.value">
                      {{ opt.label }}
                    </a-select-option>
                  </a-select-opt-group>
                  <a-select-opt-group>
                    <span slot="label">
                      <a-icon type="cloud-server" />
                      服务端渲染
                    </span>

                    <a-select-option v-for="(opt, i) in serverRenderOptions" :value="opt.value" :key="opt.value">
                      {{ opt.label }}
                    </a-select-option>
                  </a-select-opt-group>
                </a-select>
              </div>
              <a-row type="flex" v-if="column.renderFunction.type">
                <template v-if="renderConfNames.includes(column.renderFunction.type + 'Config')">
                  <a-col :span="24">
                    <component
                      :is="column.renderFunction.type + 'Config'"
                      :render-type="column.renderFunction.type"
                      :options="column.renderFunction.options"
                      :column="column"
                      :widget="widget"
                      :designer="designer"
                      :columnIndexOptions="columnIndexOptions"
                      class="cellrender-config-wrapper"
                    />
                  </a-col>
                </template>
                <template v-if="cellRenderMockNames.includes(column.renderFunction.type + 'Mock')">
                  <a-col :span="24">
                    <div class="cellrender-mock-wrapper">
                      <component
                        :is="column.renderFunction.type + 'Mock'"
                        :cellRenderName="column.renderFunction.type"
                        :options="column.renderFunction.options"
                      />
                    </div>
                  </a-col>
                </template>
              </a-row>
            </a-form-model>
          </template>
        </WidgetDesignModal>
      </a-form-model-item>

      <template v-if="column.clickEvent != undefined">
        <a-form-model-item label="列点击事件">
          <a-switch v-model="column.clickEvent.enable" />
        </a-form-model-item>

        <WidgetEventHandler
          style="padding: 0px"
          v-if="column.clickEvent.enable"
          :widget="widget"
          :eventModel="column.clickEvent.eventHandler"
          :designer="designer"
          :rule="{
            name: false,
            triggerSelectable: false
          }"
        >
          <template slot="urlHelpSlot">
            <a-popover placement="rightTop" :title="null">
              <template slot="content">
                支持通过
                <a-tag>${ 字段 }</a-tag>
                设值到地址上
              </template>
              <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
            </a-popover>
          </template>
          <template slot="eventParamValueHelpSlot">
            支持通过
            <a-tag>${ 字段 }</a-tag>
            取值
          </template>
        </WidgetEventHandler>
      </template>
    </Scroll>
  </a-form-model>
</template>
<script type="text/babel">
import RenderConfiguration from './render-configuration/index';
import TableRenderMixin from '../table.renderMixin';
import CellRender from '../cell-render/index';
import { copyToClipboard } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
import CellRenderMock from './cell-render-mock';

export default {
  name: 'ColumnConfiguration',
  inject: ['pageContext'],
  props: {
    widget: Object,
    designer: Object,
    column: Object,
    columnIndexOptions: Array
  },
  data() {
    console.log(CellRender);
    let cellRenderOptions = [];
    for (let k in CellRender) {
      cellRenderOptions.push({
        label: CellRender[k].title,
        value: k
      });
    }
    return {
      cellRenderMockNames: Object.keys(CellRenderMock),
      renderConfNames: Object.keys(RenderConfiguration),
      RenderConfiguration,
      options: {},
      serverRenderOptions: [],
      clientRenderOptions: [].concat(cellRenderOptions).concat(TableRenderMixin.methods.getRenderMethodOptions())
    };
  },
  components: {
    ...CellRenderMock,
    ...RenderConfiguration
  },
  computed: {
    renderModalWidth() {
      let width = 600;
      if (this.column.renderFunction.type) {
        if (this.renderConfNames.includes(this.column.renderFunction.type + 'Config')) {
          width = 900;
        }
      }
      return width;
    },
    columnData() {
      if (this.columnIndexOptions && this.columnIndexOptions.length) {
        return this.widget.configuration.columns;
      }
      return [];
    },
    columnIndexTitleMap() {
      let map = {};
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        map[this.columnIndexOptions[i].value] = this.columnIndexOptions[i].label;
      }
      return map;
    }
  },
  created() {
    if (this.column.role == undefined) {
      this.$set(this.column, 'role', []);
    }
    if (this.column.clickEvent == undefined) {
      this.$set(this.column, 'clickEvent', {
        enable: false,
        eventHandler: {}
      });
    }
  },
  beforeMount() {
    this.getRenderFunctionOptions();
  },
  methods: {
    removeColumnRenderFunction(column) {
      this.$set(column, 'renderFunction', { type: undefined, options: {} });
    },
    getRenderFunctionName(type) {
      let options = [...this.clientRenderOptions, ...this.serverRenderOptions];
      for (let o of options) {
        if (o.value == type) {
          return o.label;
        }
      }
      return '';
    },
    filterSelectOption,
    onChangeColumnSelect(v, option) {
      if (option) {
        this.column.title = this.columnIndexTitleMap[v];
      }
    },
    onConfirmOk() {},
    onChangePrimaryKey(checked) {
      if (this.column.id != undefined && checked) {
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          if (this.widget.configuration.columns[i].id != this.column.id && this.widget.configuration.columns[i].primaryKey) {
            this.widget.configuration.columns[i].primaryKey = false;
          }
        }
      }
    },
    onSelectChangeRenderFunc(val, vnode, renderFunction) {
      this.$set(renderFunction, 'options', {});
    },

    getRenderFunctionOptions() {
      var _this = this;
      if (this.serverRenderOptions.length == 0) {
        $axios
          .post('/common/select2/query', {
            serviceName: 'viewComponentService',
            queryMethod: 'loadRendererSelectData',
            pageNo: 1,
            type: 2,
            pageSize: 10000
          })
          .then(({ data }) => {
            if (data.results && data.results.length) {
              for (let i = 0, len = data.results.length; i < len; i++) {
                if (data.results[i].id !== 'APP_FUNCTION_TYPE_NAME_RENDER' && data.results[i].id !== 'favoriteRenderer') {
                  _this.serverRenderOptions.push({
                    value: data.results[i].id,
                    label: data.results[i].text
                  });
                }
              }
              console.log('服务端渲染器: ', _this.serverRenderOptions);
            }
          });
      }
    }
  }
};
</script>

<style lang="less">
.cellrender-config-wrapper {
  > .ant-form-item {
    margin-bottom: 0;
  }
}
.cellrender-mock-wrapper {
  .ant-form-item {
    margin-bottom: 0;
  }
}
</style>
