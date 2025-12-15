<template>
  <div class="theme-style-panel theme-upload-panel">
    <a-page-header title="文件">
      <div slot="subTitle">平台表单文件</div>
      <!-- 默认视图 -->
      <a-card title="默认视图" :style="vStyle">
        <div
          :class="[selectedKey == 'basicStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('basicStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" class="theme-upload-simple-descriptions">
            <a-descriptions-item>
              <template #label>
                整体样式
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
              </template>
              <theme-upload-simple :visibleTips="true" :fileList="fileList" />
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div
          :class="[selectedKey == 'popoverStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('popoverStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" class="theme-upload-simple-descriptions">
            <a-descriptions-item>
              <template #label>
                气泡显示
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('popoverStyle')" />
              </template>
              <theme-upload-simple class="theme-upload-simple" :visibleTips="true" :fileList="fileList2" :defaultVisible="true" />
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>
      <!-- <a-card
        title="高级视图"
        :style="vStyle"
        :class="[selectedKey == 'advancedStyle' ? 'border-selected' : '']"
        @click="onClickCard('advancedStyle')"
      >
        <a-descriptions layout="vertical" :colon="false" :column="1">
          <a-descriptions-item>
            <template #label>
              框体样式
              <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('advancedStyle')" />
            </template>
            <theme-upload-advanced />
          </a-descriptions-item>
        </a-descriptions>
      </a-card> -->
      <a-card
        title="高级视图-列表"
        :style="vStyle"
        :class="[selectedKey == 'advancedListStyle' ? 'border-selected' : '']"
        @click="onClickCard('advancedListStyle')"
      >
        <a-descriptions layout="vertical" :colon="false" :column="1">
          <a-descriptions-item>
            <template #label>
              列表样式
              <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('advancedListStyle')" />
            </template>
            <theme-upload-advanced :advancedFileListType="advancedViewList[0]" :fileListProp="JSON.parse(JSON.stringify(fileList))" />
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card
        title="高级视图-表格"
        :style="vStyle"
        :class="[selectedKey == 'advancedTableStyle' ? 'border-selected' : '']"
        @click="onClickCard('advancedTableStyle')"
      >
        <a-descriptions layout="vertical" :colon="false" :column="1">
          <a-descriptions-item>
            <template #label>
              表格样式
              <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('advancedTableStyle')" />
            </template>
            <theme-upload-advanced :advancedFileListType="advancedViewList[1]" :fileListProp="JSON.parse(JSON.stringify(fileList))" />
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card
        :style="vStyle"
        :class="[selectedKey == 'advancedIconStyle' ? 'border-selected' : '']"
        @click="onClickCard('advancedIconStyle')"
      >
        <template #title>
          高级视图-图标
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('advancedIconStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="1" class="theme-upload-icon-descriptions">
          <a-descriptions-item label="选项样式">
            <theme-upload-advanced :advancedFileListType="advancedViewList[2]" :fileListProp="JSON.parse(JSON.stringify(fileList))" />
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 错误提示 -->
      <a-card :class="[selectedKey == 'errorStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('errorStyle')">
        <template #title>
          错误提示
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('errorStyle')" />
        </template>
        <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="错误提示 Error">
              <a-form-model-item prop="testUpload" required class="full-line80">
                <theme-upload-simple />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item :label="null"></a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';
import { fileList } from './components/config';

export default {
  name: 'ThemeUpload',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '文件',
  category: 'formBasicComp',
  themePropConfig: {
    // 整体样式
    basicStyle: {
      wUploadTipsColor: '--w-text-color-base',
      wUploadTipsSize: '--w-font-size-base',
      wUploadTipsWeight: '--w-font-weight-regular',
      wUploadListBg: '--w-fill-color-base',
      wUploadListPadding: '--w-padding-3xs',
      wUploadListTextColor: '--w-text-color-dark',
      wUploadListTextSize: '--w-font-size-base',
      wUploadListTextWeight: '--w-font-weight-regular'
    },
    popoverStyle: {
      wUploadPopoverColor: '--w-border-color-darker',
      wUploadPopoverWidth: '--w-border-width-0',
      wUploadPopoverStyle: '--w-border-style-base',
      wUploadPopoverRadius: '--w-border-radius-2',
      wUploadPopoverTitleColor: '--w-text-color-dark',
      wUploadPopoverTitleSize: '--w-font-size-base',
      wUploadPopoverTitleWeight: '--w-font-weight-regular',
      wUploadPopoverDescColor: '--w-text-color-base',
      wUploadPopoverDescSize: '--w-font-size-base',
      wUploadPopoverDescWeight: '--w-font-weight-regular',
      wUploadPopoverBackground: '--w-fill-color-base',
      wUploadPopoverLrPadding: '--w-padding-sm'
    },
    advancedStyle: {
      wUploadAdvancedColor: '--w-border-color-base',
      wUploadAdvancedWidth: '--w-border-width-0',
      wUploadAdvancedStyle: '--w-border-style-base',
      wUploadAdvancedRadius: '--w-border-radius-base'
    },
    advancedListStyle: {
      wUploadAdvancedListColor: '--w-border-color-base',
      wUploadAdvancedListWidth: '--w-border-width-0',
      wUploadAdvancedListStyle: '--w-border-style-base',
      wUploadAdvancedListRadius: '--w-border-radius-base',
      wUploadAdvancedListBg: '--w-fill-color-base',
      wUploadAdvancedListMargin: '--w-margin-0'
    },
    advancedTableStyle: {
      wUploadAdvancedTableColor: '--w-border-color-base',
      wUploadAdvancedTableWidth: '--w-border-width-0',
      wUploadAdvancedTableStyle: '--w-border-style-base',
      wUploadAdvancedTableRadius: '--w-border-radius-1',
      wUploadAdvancedTableBg: '--w-fill-color-base',
      wUploadAdvancedTableMargin: '--w-margin-0'
    },
    advancedIconStyle: {
      wUploadAdvancedIconBg: '--w-fill-color-base',
      wUploadAdvancedIconPadding: '--w-padding-3xs'
    },
    errorStyle: {
      wUploadErrorBorderColor: '--w-danger-color',
      wUploadErrorBorderStyle: '--w-border-style-base',
      wUploadErrorBorderWidth: '--w-border-width-base',
      wUploadErrorTextColor: '--w-danger-color',
      wUploadErrorTextSize: '--w-font-size-base',
      wUploadErrorTextWeight: '--w-font-weight-regular'
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = [
          'basicStyle',
          'popoverStyle',
          'advancedStyle',
          'advancedListStyle',
          'advancedTableStyle',
          'advancedIconStyle',
          'errorStyle'
        ];
      for (let prop of keys) {
        if (this.config[prop]) {
          for (let key in this.config[prop]) {
            if (this.config[prop][key] != undefined) {
              if (key.toLowerCase().indexOf('boxshadow') != -1) {
                // 阴影样式处理
                colorVars['--' + kebabCase(key)] = this.config[prop][key].startsWith('inset ')
                  ? 'inset var(' + this.config[prop][key].split('inset ')[1] + ')'
                  : `var(${this.config[prop][key]})`;
              } else {
                colorVars['--' + kebabCase(key)] = this.config[prop][key].startsWith('--w-')
                  ? `var(${this.config[prop][key]})`
                  : this.config[prop][key];
              }
            }
          }
        }
      }

      let vars = { ...colorVars, ...this.basicStyleCssVars };
      console.log('colorVars', colorVars);
      // console.log('输入框样式变量: ', colorVars,this.basicStyleCssVars);
      return vars;
    }
  },
  data() {
    return {
      selectedKey: 'basicStyle',
      testForm: { testUpload: undefined },
      testRules: {
        testUpload: [
          {
            required: true,
            message: (
              <span class="ant-upload-error">
                <a-icon type="warning" /> 请上传文件
              </span>
            )
          }
        ]
      },
      fileList,
      fileList2: JSON.parse(JSON.stringify([fileList[0]])),
      advancedViewList: ['listView', 'tableView', 'iconView']
    };
  },
  mounted() {
    this.$emit('select', this.config.basicStyle);
    this.$refs.testForm.validate();
  },
  methods: {
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeUploadProp',
          popoverStyle: 'ThemeUploadPopover',
          advancedStyle: 'ThemeUploadAdvancedProp',
          advancedListStyle: 'ThemeUploadAdvancedList',
          advancedTableStyle: 'ThemeUploadAdvancedTable',
          advancedIconStyle: 'ThemeUploadAdvancedIcon',
          errorStyle: 'ThemeUploadError'
        };
        this.selectedKey = key;
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    }
  }
};
</script>
