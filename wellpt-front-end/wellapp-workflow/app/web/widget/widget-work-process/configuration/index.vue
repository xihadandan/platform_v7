<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title"></a-input>
        </a-form-model-item>
        <a-form-model-item label="编码">
          <a-input v-model="widget.configuration.code"></a-input>
        </a-form-model-item>
        <a-form-model-item label="标题">
          <a-input v-model="widget.configuration.title">
            <template slot="addonAfter">
              <a-switch
                :checked="!widget.configuration.hideTitle"
                size="small"
                @change="widget.configuration.hideTitle = !widget.configuration.hideTitle"
                title="显示标题"
              />
              <WI18nInput
                v-show="!widget.configuration.hideTitle"
                :widget="widget"
                :designer="designer"
                code="title"
                v-model="widget.configuration.title"
              />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="标题图标">
          <WidgetDesignDrawer
            :id="'widgetCardTitleIconset' + widget.id"
            title="选择图标"
            :designer="designer"
            :width="640"
            :bodyStyle="{ height: '100%' }"
          >
            <IconSetBadge title="设置标题图标" v-model="widget.configuration.titleIcon" />
            <template slot="content">
              <WidgetIconLib v-model="widget.configuration.titleIcon" />
            </template>
          </WidgetDesignDrawer>
        </a-form-model-item>
        <a-form-model-item label="数据来源">
          <a-alert type="info" style="font-size: 11px; padding-left: 6px; padding-right: 0; text-align: left" :show-icon="false">
            <div slot="message">
              <a-icon type="info-circle" />
              工作流实例数据，需结合工作流使用
            </div>
          </a-alert>
        </a-form-model-item>
        <a-form-model-item label="展示风格">
          <a-radio-group size="small" v-model="widget.configuration.displayStyle">
            <a-radio-button value="standard">标准时间轴</a-radio-button>
            <a-radio-button value="simple">简约时间轴</a-radio-button>
            <a-radio-button value="table">表格</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="展示数据">
          <a-checkbox-group v-model="widget.configuration.displayDataTypes" name="displayDataTypes" :options="displayDataTypeOptions" />
        </a-form-model-item>

        <a-collapse :bordered="false" expandIconPosition="right">
          <a-collapse-panel v-if="widget.configuration.displayStyle == 'table'" key="columnSetting" header="列定义">
            <TableColumnsConfiguration
              :widget="widget"
              :designer="designer"
              :columnIndexOptions="tableColumnIndexOptions"
            ></TableColumnsConfiguration>
            <a-form-model-item label="序号列">
              <a-switch v-model="widget.configuration.addSerialNumber" />
            </a-form-model-item>
          </a-collapse-panel>
          <a-collapse-panel v-if="widget.configuration.displayStyle == 'table'" key="paginationSetting" header="分页设置">
            <a-form-model-item :label="null">
              <a-radio-group v-model="widget.configuration.pagination.type" button-style="solid" size="small">
                <a-radio-button value="default">默认分页</a-radio-button>
                <a-radio-button value="waterfall">瀑布式分页</a-radio-button>
                <a-radio-button value="no">不分页</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item v-if="widget.configuration.pagination.type == 'no'">
              <template slot="label">
                <a-tooltip title="为空时候显示全部数据" placement="left" :mouseEnterDelay="0.5">
                  <label>
                    只显示前n条数据
                    <a-icon type="info-circle" />
                  </label>
                </a-tooltip>

                <label style="color: #999"></label>
              </template>
              <a-input-number v-model="widget.configuration.pagination.onlyFetchCount" :min="1" />
            </a-form-model-item>

            <a-form-model-item label="默认每页条数" v-if="widget.configuration.pagination.type != 'no'">
              <a-input-number v-model="widget.configuration.pagination.pageSize" :min="1" />
            </a-form-model-item>
            <div v-show="widget.configuration.pagination.type === 'default'">
              <a-form-model-item label="显示总数/页数">
                <a-switch v-model="widget.configuration.pagination.showTotalPage" />
              </a-form-model-item>
              <a-form-model-item label="显示切换分页数">
                <a-switch v-model="widget.configuration.pagination.showSizeChanger" />
              </a-form-model-item>
              <a-form-model-item :label="null" v-show="widget.configuration.pagination.showSizeChanger">
                <a-select
                  :options="vPageSizeOptions"
                  :style="{ width: '100%', display: 'block' }"
                  mode="multiple"
                  v-model="widget.configuration.pagination.pageSizeOptions"
                  @change="onPageSizeOptionsChange"
                ></a-select>
              </a-form-model-item>
              <a-form-model-item label="显示跳页">
                <a-switch v-model="widget.configuration.pagination.showQuickJumper" />
              </a-form-model-item>
              <a-form-model-item label="页码为1隐藏分页器">
                <a-switch v-model="widget.configuration.pagination.hideOnSinglePage" />
              </a-form-model-item>
            </div>
          </a-collapse-panel>
          <a-collapse-panel key="searchSetting" header="查询设置">
            <a-form-model-item label="关键字搜索">
              <a-switch v-model="widget.configuration.search.keywordSearchEnable" />
            </a-form-model-item>
            <a-form-model-item label="关键字搜索字段" v-show="widget.configuration.search.keywordSearchEnable">
              <a-select
                v-model="widget.configuration.search.keywordSearchColumns"
                :options="searchColumnIndexOptions"
                :style="{ width: '100%' }"
                mode="multiple"
                show-search
                :filter-option="filterSelectOption"
              />
            </a-form-model-item>
          </a-collapse-panel>
          <a-collapse-panel v-if="widget.configuration.displayStyle != 'table'" key="locateSetting" header="查找定位">
            <a-form-model-item label="查找并定位到当前用户“我”的记录">
              <a-switch v-model="widget.configuration.search.locateCurrentUserRecord" />
            </a-form-model-item>
            <a-form-model-item label="查找并定位到当前环节的记录">
              <a-switch v-model="widget.configuration.search.locateCurrentTaskRecord" />
            </a-form-model-item>
          </a-collapse-panel>
        </a-collapse>

        <a-form-model-item label="排序功能">
          <a-switch v-model="widget.configuration.enabledSort" />
        </a-form-model-item>
        <a-form-model-item label="表单数据未关联流程时">
          <a-radio-group size="small" v-model="widget.configuration.unlinkDataDisplayState">
            <a-radio-button value="hidden">隐藏本组件</a-radio-button>
            <a-radio-button value="label">显示本组件</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="样式设置">
        <StyleConfiguration :widget="widget" :setWidthHeight="[false, true]" :editBlock="false" :setMarginPadding="false" />
      </a-tab-pane>
      <a-tab-pane key="3" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { filterSelectOption } from '@framework/vue/utils/function';
import { generateId } from '@framework/vue/utils/util';
import WorkProcess from '../../../page/workflow-work/component/WorkProcess.js';
const TABLE_COLUMN_INDEX_OPTIONS = [
  { label: '办理环节', value: 'taskName' },
  { label: '办理人', value: 'assignee' },
  { label: '操作', value: 'actionName' },
  { label: '办理意见', value: 'opinion' },
  { label: '意见立场名称', value: 'opinionLabel' },
  { label: '意见立场值', value: 'opinionValue' },
  { label: '操作时间', value: 'endTime' },
  { label: '附件', value: 'opinionFiles' }
];
export default {
  name: 'WidgetWorkProcessConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    let searchColumnIndexOptions = TABLE_COLUMN_INDEX_OPTIONS.filter(item => !(item.value == 'endTime' || item.value == 'opinionFiles'));
    searchColumnIndexOptions.push({ label: '附件', value: 'fileName' });
    return {
      displayDataTypeOptions: [
        { label: '办理记录', value: 'Handle' },
        { label: '抄送记录', value: 'CopyTo' },
        { label: '移交记录', value: 'HandOver' },
        { label: '跳转记录', value: 'GotoTask' }
      ],
      searchColumnIndexOptions,
      tableColumnIndexOptions: TABLE_COLUMN_INDEX_OPTIONS
    };
  },
  computed: {
    vPageSizeOptions() {
      let sizes = ['5', '10', '15', '20', '50', '100', '200', '500', '1000', '5000'];
      let options = [];
      for (let i = 0, len = sizes.length; i < len; i++) {
        options.push({
          label: sizes[i],
          value: sizes[i]
        });
      }
      return options;
    }
  },
  configuration() {
    let workProcess = new WorkProcess(null, this);
    return workProcess.getWidgetConfiguration().configuration;
  },
  methods: {
    filterSelectOption,
    onPageSizeOptionsChange() {
      this.widget.configuration.pagination.pageSizeOptions.sort((a, b) => {
        return parseInt(a) - parseInt(b);
      });
    }
  }
};
</script>

<style></style>
