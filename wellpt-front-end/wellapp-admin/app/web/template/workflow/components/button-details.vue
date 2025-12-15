<template>
  <div>
    <a-form-model-item label="按钮名称" prop="title">
      <a-input v-model="button.title">
        <template slot="addonAfter">
          <WI18nInput
            :key="button.code"
            :widget="widget"
            :designer="designer"
            :code="button.code"
            :target="button"
            v-model="button.title"
          />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item v-if="!button.buildIn" label="编码" prop="code">
      <a-input v-model="button.code" />
    </a-form-model-item>
    <a-form-model-item label="排序号" prop="sortOrder">
      <a-input-number v-model="button.sortOrder" style="width: 100%" :precision="0" />
    </a-form-model-item>
    <a-form-model-item label="按钮类型">
      <a-select :options="buttonTypeOptions" v-model="button.style.type" :style="{ width: '100%' }"></a-select>
    </a-form-model-item>
    <a-form-model-item label="隐藏文本">
      <a-switch v-model="button.style.textHidden" />
    </a-form-model-item>
    <a-form-model-item label="按钮图标" v-show="button.style.type != 'switch'">
      <WidgetIconLibModal v-model="button.style.icon" :zIndex="1000" :onlyIconClass="true">
        <a-badge>
          <a-icon
            v-if="button.style.icon"
            slot="count"
            type="close-circle"
            style="color: #f5222d"
            theme="filled"
            @click.stop="button.style.icon = undefined"
            title="删除图标"
          />
          <a-button size="small" shape="round">
            {{ button.style.icon ? '' : '设置图标' }}
            <Icon :type="button.style.icon || 'setting'" />
          </a-button>
        </a-badge>
      </WidgetIconLibModal>
    </a-form-model-item>
    <PopconfirmConfiguration :configuration="button" :designer="{}" :popconfirmI18nCodePrefix="button.code">
      <template slot="popconfirmTypeHelpSlot">
        <a-popover placement="left">
          <template slot="content">移动端均按居中对话框显示</template>
          <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
        </a-popover>
      </template>
      <template slot="popconfirmHelpSlot">
        <a-popover placement="left">
          <template slot="content">
            支持通过访问流程数据变量
            <a-tag>workData</a-tag>
            <a-tag>action</a-tag>
            设值
            <br />
            例如: ${ workData.title } 获取流程数据标题
          </template>
          <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
        </a-popover>
      </template>
    </PopconfirmConfiguration>
    <a-form-model-item label="显示" class="item-lh">
      <a-radio-group size="small" v-model="button.defaultVisible" button-style="solid">
        <a-switch v-model="button.defaultVisible" />
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item class="item-lh" :label-col="{ style: { width: '100%' } }">
      <template slot="label">
        <a-checkbox v-model="button.defaultVisibleVar.enable" />
        满足条件时{{ button.defaultVisible ? '显示' : '隐藏' }}
      </template>
    </a-form-model-item>
    <a-form-model-item v-if="button.defaultVisibleVar.enable">
      <template slot="label">
        <a-popover placement="left">
          <template slot="content">
            支持通过访问流程变量
            <a-tag>workData</a-tag>
            <a-tag>workView</a-tag>
            返回条件结果
            <br />
            例如: return workView.isTodo(); 判断流程是否为待办
          </template>
          条件代码
          <a-button icon="info-circle" type="link" size="small" />
        </a-popover>
      </template>
      <WidgetCodeEditor
        @save="value => (button.defaultVisibleVar.customScript = value)"
        :value="button.defaultVisibleVar.customScript"
        :snippets="codeSnippets"
      >
        <a-button icon="code">编写代码</a-button>
      </WidgetCodeEditor>
    </a-form-model-item>
    <EventHandlerAdmin
      v-if="!button.buildIn"
      :eventModel="button.eventHandler"
      labelAlign="left"
      :label-col="{ span: 7 }"
      :wrapper-col="{ span: 17 }"
      :colon="false"
    />
    <div style="height: 50px"></div>
  </div>
</template>

<script>
import { buttonTypeOptions } from '@pageAssembly/app/web/widget/commons/constant.js';
import PopconfirmConfiguration from '@pageAssembly/app/web/widget/commons/popconfirm-configuration.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import WidgetCodeEditor from '@pageAssembly/app/web/widget/commons/widget-code-editor.vue';
import EventHandlerAdmin from '../../common/event-handler-admin.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
export default {
  props: {
    button: Object
  },
  components: { PopconfirmConfiguration, WidgetIconLibModal, WidgetCodeEditor, EventHandlerAdmin, WI18nInput },
  data() {
    return { buttonTypeOptions, codeSnippets: [] };
  }
};
</script>

<style></style>
