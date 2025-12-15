<template>
  <a-collapse :bordered="false" expandIconPosition="right">
    <a-collapse-panel key="paddingStyle" header="内边距">
      <template v-if="displayStyle == '2'">
        <BorderRadiusSet label="卡片" field="spacing" :target="target" hideSilder :max="50" />
        <BorderRadiusSet label="卡片内容区" field="padding" :target="target" hideSilder :max="50" />
        <BorderRadiusSet label="卡片标题栏" field="cardTitlePadding" :target="target" hideSilder :max="50" />
        <BorderRadiusSet label="卡片内容行" field="cardContentPadding" :target="target" hideSilder :max="50" />
      </template>
      <template v-if="displayStyle == '1'">
        <BorderRadiusSet label="列表" field="spacing" :target="target" hideSilder :max="50" />
        <BorderRadiusSet label="列表标题" field="listTitlePadding" :target="target" hideSilder :max="50" />
        <BorderRadiusSet label="列表小标题" field="listSubtitlePadding" :target="target" hideSilder :max="50" />
        <BorderRadiusSet label="列表备注" field="listNotePadding" :target="target" hideSilder :max="50" />
        <BorderRadiusSet label="列表右侧文本" field="listExtraPadding" :target="target" hideSilder :max="50" />
      </template>
      <template v-if="target.footerHr">
        <BorderRadiusSet label="底部" field="footerPadding" :target="target" hideSilder :max="50" />
      </template>
      <a-form-model-item label="行下按钮间距">
        <a-input-number v-model="target.itemFooterButtonGutter" :min="0" />
        px
      </a-form-model-item>
      <BorderRadiusSet label="行下按钮区" field="itemFooterButtonPadding" :target="target" hideSilder :max="50" />
    </a-collapse-panel>
    <a-collapse-panel key="marginStyle" header="外边距">
      <template v-if="displayStyle == '2'">
        <BorderRadiusSet label="卡片" field="margin" :target="target" hideSilder :min="-50" :max="50" />
        <BorderRadiusSet label="卡片标题栏" field="cardTitleMargin" :target="target" hideSilder :min="-50" :max="50" />
      </template>
      <template v-if="displayStyle == '1'">
        <BorderRadiusSet label="列表标题" field="listTitleMargin" :target="target" hideSilder :min="-50" :max="50" />
        <BorderRadiusSet label="列表小标题" field="listSubtitleMargin" :target="target" hideSilder :min="-50" :max="50" />
        <BorderRadiusSet label="列表备注" field="listNoteMargin" :target="target" hideSilder :min="-50" :max="50" />
        <BorderRadiusSet label="列表右侧文本" field="listExtraMargin" :target="target" hideSilder :min="-50" :max="50" />
      </template>
      <template v-if="target.footerHr">
        <BorderRadiusSet label="底部" field="footerMargin" :target="target" hideSilder :min="-50" :max="50" />
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="radiusStyle" header="圆角大小" v-if="displayStyle == '2'">
      <BorderRadiusSet label="圆角" :target="target" hideSilder />
    </a-collapse-panel>
    <a-collapse-panel key="fontSizeStyle" header="字体大小">
      <template v-if="displayStyle == '2'">
        <a-form-model-item label="卡片标题">
          <a-input-number v-model="target.cardTitleSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="卡片小标题">
          <a-input-number v-model="target.cardSubTitleSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="卡片右侧文本">
          <a-input-number v-model="target.cardExtraSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="卡片内容区标题">
          <a-input-number v-model="target.cardContentLabelSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="卡片内容区内容">
          <a-input-number v-model="target.cardContentValueSize" :min="12" />
          px
        </a-form-model-item>
      </template>
      <template v-if="displayStyle == '1'">
        <a-form-model-item label="列表标题">
          <a-input-number v-model="target.listTitleSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="列表小标题">
          <a-input-number v-model="target.listSubtitleSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="列表右侧文本">
          <a-input-number v-model="target.listExtraSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="列表备注">
          <a-input-number v-model="target.listNoteSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="列表底部左侧">
          <a-input-number v-model="target.listContentLabelSize" :min="12" />
          px
        </a-form-model-item>
        <a-form-model-item label="列表底部右侧">
          <a-input-number v-model="target.listContentValueSize" :min="12" />
          px
        </a-form-model-item>
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="fontWeightStyle" header="字重大小">
      <template v-if="displayStyle == '2'">
        <a-form-model-item label="卡片标题">
          <a-select :options="fontWeightOptions" v-model="target.cardTitleWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="卡片小标题">
          <a-select :options="fontWeightOptions" v-model="target.cardSubtitleWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="卡片右侧文本">
          <a-select :options="fontWeightOptions" v-model="target.cardExtraWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="卡片内容区标题">
          <a-select :options="fontWeightOptions" v-model="target.cardContentLabelWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="卡片内容区内容">
          <a-select :options="fontWeightOptions" v-model="target.cardContentValueWeight" allowClear></a-select>
        </a-form-model-item>
      </template>
      <template v-if="displayStyle == '1'">
        <a-form-model-item label="列表标题">
          <a-select :options="fontWeightOptions" v-model="target.listTitleWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="列表小标题">
          <a-select :options="fontWeightOptions" v-model="target.listSubtitleeWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="列表右侧文本">
          <a-select :options="fontWeightOptions" v-model="target.listExtraWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="列表备注">
          <a-select :options="fontWeightOptions" v-model="target.listNoteWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="列表底部左侧">
          <a-select :options="fontWeightOptions" v-model="target.listContentLabeleWeight" allowClear></a-select>
        </a-form-model-item>
        <a-form-model-item label="列表底部右侧">
          <a-select :options="fontWeightOptions" v-model="target.listContentValueWeight" allowClear></a-select>
        </a-form-model-item>
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="ColorStyle" header="颜色">
      <a-form-model-item label="标题">
        <ColorPicker v-model="target.titleTextColor" :allowClear="true" />
      </a-form-model-item>
      <a-form-model-item label="小标题">
        <ColorPicker v-model="target.subTitleColor" :allowClear="true" />
      </a-form-model-item>
      <a-form-model-item label="右侧文本">
        <ColorPicker v-model="target.rightTextColor" :allowClear="true" />
      </a-form-model-item>
      <a-form-model-item label="备注" v-if="displayStyle == '1'">
        <ColorPicker v-model="target.noteColor" :allowClear="true" />
      </a-form-model-item>
      <a-form-model-item label="左侧内容">
        <ColorPicker v-model="target.leftContentTextColor" :allowClear="true" />
      </a-form-model-item>
      <a-form-model-item label="右侧内容">
        <ColorPicker v-model="target.rightContentTextColor" :allowClear="true" />
      </a-form-model-item>
      <template v-if="displayStyle == '2'">
        <a-form-model-item label="卡片边框">
          <ColorPicker v-model="target.borderColor" :allowClear="true" />
        </a-form-model-item>
        <a-form-model-item label="卡片标题分割线">
          <ColorPicker v-model="target.cardTitleBorderColor" :allowClear="true" />
        </a-form-model-item>
      </template>
      <template v-if="displayStyle == '1'">
        <a-form-model-item label="列表分割线">
          <ColorPicker v-model="target.listItemBorderColor" :allowClear="true" />
        </a-form-model-item>
      </template>
      <template v-if="target.footerHr">
        <a-form-model-item label="底部分割线">
          <ColorPicker v-model="target.footerHrColor" :allowClear="true" />
        </a-form-model-item>
      </template>
      <a-form-model-item label="行下按钮分割线">
        <ColorPicker v-model="target.itemFooterButtonHrColor" :allowClear="true" />
      </a-form-model-item>
    </a-collapse-panel>
    <a-collapse-panel key="hrStyle" header="分隔线">
      <a-form-model-item label="底部分隔线">
        <a-switch v-model="target.footerHr" />
      </a-form-model-item>
      <a-form-model-item label="行下按钮分隔线高度">
        <a-input-number v-model="target.itemFooterButtonHrHeight" :allowClear="true" />
        px
      </a-form-model-item>
    </a-collapse-panel>
    <a-collapse-panel key="cardStyle" header="卡片" v-if="displayStyle == '2'">
      <a-form-model-item label="内容对齐方式">
        <a-radio-group v-model="target.justifyContent" button-style="solid" size="small">
          <a-radio-button value="">默认</a-radio-button>
          <a-radio-button value="space-between">两端对齐</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="标题区右侧内容宽度">
        <a-input-number v-model="target.rightWidth" :allowClear="true" />
        px
      </a-form-model-item>
      <a-form-model-item label="内容区左侧内容宽度">
        <a-input-number v-model="target.cardContentLabelWidth" :allowClear="true" />
        px
      </a-form-model-item>
    </a-collapse-panel>
    <a-collapse-panel key="listStyle" header="列表" v-if="displayStyle == '1'">
      <a-form-model-item label="列表右侧内容宽度">
        <a-input-number v-model="target.rightWidth" :allowClear="true" />
        px
      </a-form-model-item>
    </a-collapse-panel>
    <a-collapse-panel key="bgStyle" header="背景颜色">
      <BackgroundSet :widget="widget" :designer="designer" :backgroundStyle="target.backgroundStyle"></BackgroundSet>
    </a-collapse-panel>
    <a-collapse-panel key="otherStyle" header="其他设置">
      <a-form-model-item label="在表单内的最大高度">
        <a-input-number v-model="target.maxHeightInDyform" :allowClear="true" />
        px
      </a-form-model-item>
    </a-collapse-panel>
    <a-collapse-panel key="mainStyle" header="自定义样式">
      <a-form-model-item>
        <template slot="label">
          <a-tooltip placement="bottomRight">
            <template slot="title">
              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                <li>可以通过该字段修改卡片或列表内信息的样式</li>
              </ul>
            </template>
            列表样式
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>
        <a-textarea placeholder="请输入列表样式" :autosize="{ minRows: 4, maxRows: 8 }" v-model="target.mainStyle" allow-clear></a-textarea>
      </a-form-model-item>
    </a-collapse-panel>
  </a-collapse>
</template>
<style></style>
<script type="text/babel">
import BackgroundSet from '../../commons/background-set.vue';

export default {
  name: 'UniListStyleConfiguration',
  mixins: [],
  props: {
    target: Object,
    configuration: Object,
    displayStyle: {
      type: String,
      default: '1'
    }
  },
  data() {
    return {
      fontWeightOptions: [
        { label: '默认值', value: 'normal' },
        { label: '粗体', value: 'bold' },
        // { label: '更粗', value: 'bolder' },
        { label: '更细', value: 'lighter' }
      ]
    };
  },

  beforeCreate() {},
  components: {
    BackgroundSet
  },
  computed: {},
  created() {},
  methods: {},
  mounted() {}
};
</script>
