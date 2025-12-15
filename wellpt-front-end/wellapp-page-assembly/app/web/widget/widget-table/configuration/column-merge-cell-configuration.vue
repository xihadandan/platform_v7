<template>
  <div class="column-group-configuration" v-if="widget.configuration.defaultDisplayState == 'table'">
    <a-form-model-item>
      <template slot="label">合并单元格</template>
      <WidgetDesignDrawer
        ref="drawerRef"
        :id="'widgetTableRowMergeCells' + widget.id"
        title="合并单元格设置"
        :designer="designer"
        v-model="modalVisible"
      >
        <a-button type="link" size="small">
          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          设置
        </a-button>
        <template slot="content">
          <a-form-model>
            <a-tabs default-active-key="1" class="pt-tabs">
              <a-tab-pane key="1" tab="行合并">
                <a-form-model-item label="启用">
                  <a-switch v-model="widget.configuration.mergeCell.isMergeCell" @change="changeRowMergeEnable" />
                </a-form-model-item>
                <template v-if="widget.configuration.mergeCell.isMergeCell">
                  <a-form-model-item>
                    <template slot="label">
                      <a-tooltip placement="right">
                        <template slot="title">
                          <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                            <li>排序优先：按表格的数据排序优先，当相邻行的值相同时合并行</li>
                            <li>合并优先：匹配指定列的列数据，满足合并条件的数据一起显示合并，此时表格默认的数据排序无效</li>
                          </ul>
                        </template>
                        合并模式
                        <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                      </a-tooltip>
                    </template>
                    <a-radio-group v-model="widget.configuration.mergeCell.mergeCellMode" button-style="solid" size="small">
                      <a-radio-button value="sort">排序优先</a-radio-button>
                      <a-radio-button value="merge">合并优先</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-form-model-item v-if="widget.configuration.mergeCell.mergeCellMode == 'merge'">
                    <template slot="label">
                      <a-tooltip placement="right">
                        <template slot="title">
                          <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                            <li>
                              逐级合并
                              <ul style="padding-inline-start: 16px; margin-block-end: 0px">
                                <li>按表格的数据排序的优先级，当相邻行的值相同时合并</li>
                                <li>如果该字段相邻行值相同，但是上一层级的值不同时，不进行合并</li>
                              </ul>
                            </li>
                            <li>
                              自由合并
                              <ul style="padding-inline-start: 16px; margin-block-end: 0px">
                                <li>相邻行的值相同时合并</li>
                              </ul>
                            </li>
                          </ul>
                        </template>
                        合并方式
                        <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                      </a-tooltip>
                    </template>
                    <a-radio-group v-model="widget.configuration.mergeCell.mergeCellType" button-style="solid" size="small">
                      <a-radio-button value="auto">自由合并</a-radio-button>
                      <a-radio-button value="level">逐级合并</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-table
                    rowKey="id"
                    :pagination="false"
                    bordered
                    :columns="tableColumn"
                    :data-source="widget.configuration.mergeCell.mergeCellCols"
                    :locale="locale"
                    class="pt-table widget-table-merge-table no-border"
                    style="--pt-table-td-padding: var(--w-padding-2xs)"
                  >
                    <a-tooltip placement="right" slot="titleHeaderSlot">
                      <template slot="title">
                        <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                          <li>选择需要行合并的列，相邻列值相同时会合并</li>
                          <li>合并优先时，优先级从上至下逐级下降</li>
                          <li>合并优先时，根据字段优先级从下至上，依次对数据进行排序</li>
                        </ul>
                      </template>
                      排序方式
                      <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                    </a-tooltip>
                    <span>
                      <a-icon type="smile-o" />
                      Name
                    </span>
                    <template slot="titleSlot" slot-scope="text, record">
                      <Icon
                        title="拖动排序"
                        v-if="widget.configuration.mergeCell.mergeCellMode == 'merge'"
                        type="pticon iconfont icon-ptkj-tuodong"
                        class="drag-column-handler"
                        :style="{ cursor: 'move' }"
                      ></Icon>
                      <a-select
                        :options="columnIndexOptions"
                        :style="{ width: '260px' }"
                        v-model="record.dataIndex"
                        show-search
                        :filter-option="filterOption"
                      ></a-select>
                    </template>
                    <template slot="sortRuleSlot" slot-scope="text, record">
                      <a-button type="link" @click.stop="onClickSortRule(record)">
                        {{ record.sortType === 'asc' ? '升序' : '降序' }}
                      </a-button>
                    </template>
                    <template slot="operationSlot" slot-scope="text, record, index">
                      <a-button type="link" @click="delColumn(index)" title="删除">
                        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                      </a-button>
                    </template>
                  </a-table>
                  <div>
                    <a-button type="link" icon="plus" :style="{ paddingLeft: '7px' }" @click="addColumn">新增</a-button>
                  </div>
                  <a-form-model-item label="空值合并">
                    <a-switch v-model="widget.configuration.mergeCell.mergeIfNull" />
                  </a-form-model-item>
                </template>
              </a-tab-pane>
              <a-tab-pane key="2" tab="列合并">
                <a-form-model-item label="启用">
                  <a-switch v-model="widget.configuration.mergeCell.isRowMergeCell" />
                </a-form-model-item>
                <template v-if="widget.configuration.mergeCell.isRowMergeCell">
                  <a-form-model-item>
                    <template slot="label">
                      <a-tooltip placement="right" slot="titleHeaderSlot">
                        <template slot="title">
                          <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                            <li>请设置合理的开始列索引和合并列数</li>
                            <li>参与行合并的字段，再进行列合并时容易使表格产生错位，请合理配置</li>
                            <li>存在行合并时，该单元格不进行列合并</li>
                          </ul>
                        </template>
                        列合并模式
                        <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                      </a-tooltip>
                    </template>
                    <a-radio-group v-model="widget.configuration.mergeCell.rowMergeType" button-style="solid" size="small">
                      <a-radio-button value="default">默认</a-radio-button>
                      <a-radio-button value="function">自定义代码</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <template v-if="widget.configuration.mergeCell.rowMergeType == 'default'">
                    <div
                      v-for="(record, i) in widget.configuration.mergeCell.rowMergeData"
                      :key="'rowMerge_' + i"
                      style="padding: 12px; outline: 1px solid #e8e8e8; border-radius: 4px; position: relative; margin-bottom: 12px"
                    >
                      <a-form-model-item :label-col="{ style: { width: 'calc(100% - 30px)' } }" :wrapper-col="{ style: { width: '30px' } }">
                        <template slot="label">
                          满足
                          <a-select
                            size="small"
                            :getPopupContainer="getPopupContainer()"
                            :options="[
                              { label: '全部', value: 'all' },
                              { label: '任一', value: 'any' }
                            ]"
                            style="width: 65px"
                            v-model="record.match"
                          />
                          条件时渲染
                          <a-button size="small" icon="plus-square" title="添加条件" type="link" @click="addCondition(i)">
                            添加条件
                          </a-button>
                        </template>
                        <a-button
                          size="small"
                          type="link"
                          @click="widget.configuration.mergeCell.rowMergeData.splice(i, 1)"
                          title="删除配置"
                        >
                          <Icon type="pticon iconfont icon-ptkj-shanchu" />
                        </a-button>
                      </a-form-model-item>
                      <a-form-model-item :wrapper-col="{ style: { width: '100%' } }">
                        <template v-if="record.conditions.length > 0">
                          <a-row type="flex" :key="'mergeCon_' + i + t" style="margin-bottom: 5px" v-for="(item, t) in record.conditions">
                            <a-col flex="calc(100% - 30px)">
                              <a-input-group compact>
                                <a-select
                                  :options="columnIndexOptions"
                                  :style="{ width: '260px' }"
                                  v-model="item.code"
                                  show-search
                                  :filter-option="filterOption"
                                ></a-select>
                                <a-select :options="operatorOptions" v-model="item.operator" :style="{ width: '120px' }" />
                                <a-input
                                  v-model="item.value"
                                  v-show="!['true', 'false'].includes(item.operator)"
                                  :style="{ width: 'calc(100% - 380px)' }"
                                />
                              </a-input-group>
                            </a-col>
                            <a-col flex="25px">
                              <a-button
                                type="link"
                                size="small"
                                v-if="record.conditions.length > 1"
                                @click="record.conditions.splice(t, 1)"
                                title="删除条件"
                              >
                                <Icon type="pticon iconfont icon-ptkj-shanchu" />
                              </a-button>
                            </a-col>
                          </a-row>
                        </template>
                      </a-form-model-item>
                      <a-form-model-item>
                        <template slot="label">
                          显示内容
                          <a-tooltip placement="top">
                            <template slot="title">
                              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                                <li>使用自定义代码</li>
                                <li>未填写内容时，使用开始索引对应字段内容</li>
                                <li>内容的渲染逻辑与开始索引对应字段的逻辑一致</li>
                              </ul>
                            </template>
                            <a-checkbox v-model="record.isFunction" />
                          </a-tooltip>
                        </template>
                        <a-input placeholder="请输入" v-model="record.content" allowClear v-if="!record.isFunction">
                          <template slot="addonAfter">
                            <WI18nInput :widget="widget" :designer="designer" :target="record" :code="record.id" v-model="record.content" />
                          </template>
                        </a-input>
                        <template v-else>
                          <WidgetCodeEditor
                            v-model="record.function"
                            lang="js"
                            width="100%"
                            height="200px"
                            :hideError="true"
                          ></WidgetCodeEditor>
                          <div style="margin-bottom: 4px">支持使用函数返回行样式, 入参传入的是row,index参数。</div>
                          <div
                            class="pt-tip-block"
                            @click="e => onClickCopy(e, `return '合计';`)"
                            style="margin-bottom: 4px; cursor: pointer"
                            title="复制"
                          >
                            返回示例：return '合计';
                          </div>
                        </template>
                      </a-form-model-item>
                      <a-form-model-item label="开始列索引">
                        <a-input-number v-model="record.startIndex" :min="0" :max="100" />
                      </a-form-model-item>
                      <a-form-model-item label="合并列数">
                        <a-input-number v-model="record.count" :min="1" :max="100" />
                      </a-form-model-item>
                    </div>
                    <a-button :block="true" type="link" @click="addRowMergeData">
                      <Icon type="pticon iconfont icon-ptkj-jiahao" />
                      添加列合并条件配置
                    </a-button>
                  </template>
                  <template v-if="widget.configuration.mergeCell.rowMergeType == 'function'">
                    <WidgetCodeEditor
                      v-model="widget.configuration.mergeCell.rowMergeFunction"
                      lang="js"
                      width="100%"
                      height="300px"
                      :hideError="true"
                    ></WidgetCodeEditor>
                    <div style="margin-bottom: 4px">支持使用函数返回行样式, 入参传入的是row，index，columns参数。</div>
                    <div style="margin-bottom: 4px">返回值content不填写时，使用开始索引对应字段内容。</div>
                    <div style="margin-bottom: 4px">内容的渲染逻辑与开始索引对应字段的逻辑一致</div>
                    <div
                      class="pt-tip-block"
                      @click="e => onClickCopy(e, `return [{startIndex: 0, count: 2, content: '合计'}];`)"
                      style="margin-bottom: 4px; cursor: pointer"
                      title="复制"
                    >
                      返回示例：return [{startIndex: 0, count: 2, content: '合计'}]
                    </div>
                    <div class="flex f_wrap help-tip-content-list">
                      <div v-for="(item, i) in tipParamList" style="margin-bottom: 4px; cursor: pointer">
                        {{ item.label }}
                        <a-tag @click="e => onClickCopy(e, item.value)" :title="'点击复制：' + item.value">{{ item.value }}</a-tag>
                      </div>
                    </div>
                  </template>
                </template>
              </a-tab-pane>
            </a-tabs>
          </a-form-model>
        </template>
      </WidgetDesignDrawer>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone, copyToClipboard } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';
export default {
  name: 'ColumnMergeCellConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object,
    columnIndexOptions: Array
  },
  components: {},
  computed: {},
  data() {
    return {
      draggableMounted: false,
      modalVisible: false,
      operatorOptions: [
        { value: '>', label: '大于' },
        { value: '>=', label: '大于等于' },
        { value: '<', label: '小于' },
        { value: '<=', label: '小于等于' },
        { label: '等于', value: '==' },
        { label: '不等于', value: '!=' },
        { label: '为真', value: 'true' },
        { label: '为假', value: 'false' },
        { label: '包含于', value: 'in' },
        { label: '不包含于', value: 'not in' },
        { label: '包含', value: 'contain' },
        { label: '不包含', value: 'not contain' }
      ],
      tipParamList: [
        { label: '开始列索引', value: 'startIndex' },
        { label: '合并列数 ', value: 'count' },
        { label: '显示内容 ', value: 'content' }
      ]
    };
  },
  computed: {
    tableColumn() {
      let columns = [
        {
          title: '合并列',
          dataIndex: 'title',
          width: 340,
          scopedSlots: { customRender: 'titleSlot' },
          slots: { title: 'titleHeaderSlot' }
        },
        { title: '排序方式', dataIndex: 'rule', width: 120, scopedSlots: { customRender: 'sortRuleSlot' } },
        { title: '操作', dataIndex: 'operation', scopedSlots: { customRender: 'operationSlot' } }
      ];
      if (this.widget.configuration.mergeCell.mergeCellMode == 'sort') {
        columns.splice(1, 1);
      }
      return columns;
    }
  },
  beforeCreate() {},
  mounted() {},
  methods: {
    addColumn() {
      this.widget.configuration.mergeCell.mergeCellCols.push({
        dataIndex: '',
        id: generateId(),
        sortType: 'asc'
      });
    },
    delColumn(index) {
      this.widget.configuration.mergeCell.mergeCellCols.splice(index, 1);
    },
    onClickSortRule(record) {
      record.sortType = record.sortType === 'asc' ? 'desc' : 'asc';
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    draggableMount() {
      this.tableDraggable(
        this.widget.configuration.mergeCell.mergeCellCols,
        this.$refs.drawerRef.container.querySelector('.widget-table-merge-table tbody'),
        '.drag-column-handler'
      );
    },
    addCondition(index) {
      this.widget.configuration.mergeCell.rowMergeData[index].conditions.push({
        dataIndex: '',
        id: generateId(),
        operator: '',
        value: '',
        startIndex: 0,
        count: 1
      });
    },
    addRowMergeData() {
      if (!this.widget.configuration.mergeCell.rowMergeData) {
        this.widget.configuration.mergeCell.rowMergeData = [];
      }
      this.widget.configuration.mergeCell.rowMergeData.push({
        enable: true,
        match: 'all',
        id: generateId(),
        conditions: [
          {
            code: '',
            id: generateId(),
            operator: '',
            value: ''
          }
        ],
        isFunction: false,
        content: '',
        function: ''
      });
    },
    getPopupContainer() {
      return triggerNode => triggerNode.closest('.ant-form-item');
    },
    onClickCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          // message不支持修改样式，代码编辑组件弹框widget-code-editor层级为2000，导致message提示框显示在遮罩下面
          _this.$message.success({
            content: '已复制'
          });
        }
      });
    },
    changeRowMergeEnable(value) {
      if (value && !this.draggableMounted) {
        setTimeout(() => {
          this.draggableMount();
        }, 500);
        this.draggableMounted = true;
      }
    }
  },
  watch: {
    modalVisible(v) {
      if (v && !this.draggableMounted) {
        this.changeRowMergeEnable(this.widget.configuration.mergeCell.isMergeCell);
      }
    }
  }
};
</script>
