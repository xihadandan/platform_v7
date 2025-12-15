<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :children="widget.configuration.steps"
  >
    <div :class="['widget-steps', `widget-steps-${configuration.direction}`]">
      <a-steps
        v-model="current"
        :type="configuration.type"
        :direction="configuration.direction"
        :labelPlacement="configuration.labelPlacement"
        :size="configuration.size"
      >
        <template v-for="item in steps">
          <a-step
            v-if="item.style.icon"
            :key="item.id"
            :title="item.title"
            :subTitle="item.subTitle"
            :description="item.description"
            :status="item.status"
          >
            <template slot="icon">
              <Icon :type="item.style.icon" />
            </template>
          </a-step>
          <a-step
            v-else
            :key="item.id"
            :title="item.title"
            :subTitle="item.subTitle"
            :description="item.description"
            :status="item.status"
          />
        </template>
      </a-steps>
      <div class="steps-content-wrapper">
        <template v-if="optionType === this.sourceTypes[0]['value']">
          <div class="steps-content-container" v-for="(item, index) in steps" :key="item.id" v-show="index === current">
            <template v-if="item.configuration.eventHandler.pageId">
              <!-- 页面加载 -->
              <div style="pointer-events: none; cursor: not-allowed !important">
                <WidgetVpage :pageId="item.configuration.eventHandler.pageId" :key="item.configuration.eventHandler.pageId" />
              </div>
            </template>
            <template v-else>
              <!-- 组件加载 -->
              <draggable
                :list="item.configuration.widgets"
                v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
                handle=".widget-drag-handler"
                @add="e => onDragAdd(e, item.configuration.widgets)"
                :move="onDragMove"
                @paste.native="onDraggablePaste"
                :class="[
                  !item.configuration.widgets || item.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content'
                ]"
              >
                <transition-group
                  :id="'tab-container' + item.id"
                  name="fade"
                  tag="div"
                  :style="{ minHeight: '150px' }"
                  :class="['widget-tab-item', tabItemSelected(item.id) ? 'tab-selected' : '']"
                >
                  <template v-for="(wgt, w) in item.configuration.widgets">
                    <WDesignItem
                      :widget="wgt"
                      :key="wgt.id"
                      :index="w"
                      :widgetsOfParent="item.configuration.widgets"
                      :designer="designer"
                      :parent="item"
                      :dragGroup="dragGroup"
                    />
                  </template>
                </transition-group>
              </draggable>
            </template>
          </div>
        </template>
        <template v-else>
          <div class="steps-content-container" v-for="(item, index) in steps" :key="item.id" v-show="index === current">
            <template v-if="item.configuration && item.configuration.eventHandler && item.configuration.eventHandler.pageId != undefined">
              <div style="pointer-events: none; cursor: not-allowed !important">
                <WidgetVpage :pageId="item.configuration.eventHandler.pageId" :key="item.configuration.eventHandler.pageId" />
              </div>
            </template>
            <iframe v-else-if="item.configuration.url" :src="item.configuration.url" :style="{ border: 'none', width: '100%' }"></iframe>
          </div>
        </template>
      </div>
    </div>
    <a-empty
      v-if="designMode && steps.length === 0"
      :style="{
        margin: 0,
        padding: 'var(--w-padding-xs) var(--w-padding-md)',
        ...vBackground
      }"
      description="暂无数据"
    />
  </EditWrapper>
</template>

<script>
import '../css/index.less';
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import stepsMixin from '../mixins/index';
import formMixin from '@dyform/app/web/widget/mixin/form-common.mixin';
import { sourceTypes } from './constant';

export default {
  name: 'EWidgetSteps',
  mixins: [editWgtMixin, draggable, formMixin, stepsMixin],
  inject: ['vProvideStyle', 'designMode'],
  data() {
    return {
      current: 0,
      sourceTypes,
      dataSourceId: '',
      dataSourceRes: [],
      dataModelUuid: '',
      dataModelRes: [],
      steps: [],
      optionType: ''
    };
  },
  computed: {
    configuration() {
      return this.widget.configuration;
    },
    defaultEvents() {
      return [
        {
          id: 'stepNext',
          title: '下一步骤',
          codeSnippet: `
          /**
           * 下一步骤
           */
          this.pageContext.emitEvent({{事件编码}});
          `
        },
        {
          id: 'stepPrev',
          title: '上一步骤',
          codeSnippet: `
          /**
           * 上一步骤
           */
          this.pageContext.emitEvent({{事件编码}});
          `
        }
      ];
    }
  },
  watch: {
    'configuration.options': {
      deep: true,
      immediate: true,
      handler(options) {
        if (options.type !== this.optionType) {
          this.current = 0;
        }
        this.optionType = options.type;
        this.getSteps(options);
      }
    },
    'configuration.dataModel': {
      deep: true,
      handler() {
        if (this.optionType === this.sourceTypes[2]['value']) {
          this.getSteps({
            type: this.sourceTypes[2]['value']
          });
        }
      }
    }
  },
  methods: {
    getSteps(options, dataModel = this.configuration.dataModel) {
      if (options.type === this.sourceTypes[0]['value']) {
        this.steps = this.configuration.steps;
      } else if (options.type === this.sourceTypes[1]['value']) {
        if (!options.dataSourceId) {
          this.steps = [];
          return;
        }
        if (this.dataSourceId !== options.dataSourceId) {
          this.dataSourceId = options.dataSourceId;
          this.fetchDataStore(options, res => {
            this.dataSourceRes = res;
            this.formatDataStore(this.dataSourceRes, options);
          });
        } else {
          this.formatDataStore(this.dataSourceRes, options);
        }
      } else if (options.type === this.sourceTypes[2]['value']) {
        if (!dataModel.uuid) {
          this.steps = [];
          return;
        }
        if (this.dataModelUuid !== dataModel.uuid) {
          this.dataModelUuid = dataModel.uuid;
          this.fetchDataModel(dataModel, res => {
            this.dataModelRes = res;
            this.formatDataModel(this.dataModelRes, dataModel);
          });
        } else {
          this.formatDataModel(this.dataModelRes, dataModel);
        }
      }
    },
    changeStep(value) {
      this.steps.forEach((item, index) => {
        if (index < value) {
          item.status = 'finish';
        } else if (index === value) {
          item.status = 'process';
        } else {
          item.status = 'wait';
        }
      });
    },
    tabItemSelected(id) {
      if (this.designer.selectedId == id) {
        this.activeKey = id;
        return true;
      }
      return false;
    }
  }
};
</script>
