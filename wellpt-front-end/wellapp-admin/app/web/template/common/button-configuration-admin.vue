<template>
  <div>
    <a-form-model :labelAlign="labelAlign" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-model-item v-if="!(hideParams.indexOf('text') > -1)">
        <template #label>
          <span>按钮名称</span>
          <slot name="textTips"></slot>
        </template>
        <a-input v-model="button[replaceFields.text]">
          <template slot="addonAfter" v-if="$slots.textAfter">
            <slot name="textAfter"></slot>
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="按钮编码" v-if="!(hideParams.indexOf('code') > -1)">
        <a-input v-model="button[replaceFields.code]" />
      </a-form-model-item>
      <a-form-model-item label="按钮类型">
        <a-select :options="buttonTypeOptions" v-model="button[replaceFields.style].type" :style="{ width: '100%' }"></a-select>
      </a-form-model-item>
      <div v-show="button[replaceFields.style].type != 'switch'">
        <a-form-model-item label="按钮图标">
          <WidgetIconLibModal v-model="button[replaceFields.style].icon" :zIndex="1000" :onlyIconClass="true">
            <a-badge>
              <a-icon
                v-if="!!button[replaceFields.style].icon"
                slot="count"
                type="close-circle"
                style="color: #f5222d"
                theme="filled"
                @click.stop="button[replaceFields.style].icon = undefined"
                title="删除图标"
                class="widget-icon-lib-del-icon"
              />
              <a-button size="small" shape="round">
                {{ button[replaceFields.style].icon ? '' : '设置图标' }}
                <Icon :type="button[replaceFields.style].icon || 'setting'" />
              </a-button>
            </a-badge>
          </WidgetIconLibModal>
        </a-form-model-item>
        <a-form-model-item label="按钮形状" v-if="!(hideParams.indexOf('shape') > -1)">
          <a-select
            :style="{ width: '100%' }"
            :options="buttonShapeOptions"
            v-model="button[replaceFields.style].shape"
            allowClear
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="隐藏文本" v-if="!(hideParams.indexOf('txtHidden') > -1)">
          <a-switch v-model="button[replaceFields.style].textHidden" />
        </a-form-model-item>
      </div>
      <EventHandlerAdmin v-if="hasHandler" :eventModel="handler" ref="eventHandlerModal" />
    </a-form-model>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import { buttonTypeOptions, buttonShapeOptions } from '@pageWidget/commons/constant';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import EventHandlerAdmin from './event-handler-admin.vue';
import { filter } from 'lodash';

export default {
  name: 'AdminSettingButtonConfiguration',
  inject: ['appId'],
  mixins: [],
  props: {
    button: Object,
    handler: Object,
    hideParams: {
      type: [Array, String],
      default: ''
    },
    hasHandler: {
      type: Boolean,
      default: true
    },
    labelAlign: {
      type: String,
      default: 'right'
    },
    labelCol: {
      type: Object,
      default: () => {
        return { span: 4 };
      }
    },
    wrapperCol: {
      type: Object,
      default: () => {
        return { span: 19 };
      }
    },
    replaceFields: {
      type: [Object],
      default: () => {
        return {
          text: 'text',
          code: 'code',
          style: 'style'
        };
      }
    }
  },
  data() {
    if (!this.button[this.replaceFields.style]) {
      this.button[this.replaceFields.style] = {};
    } else if (typeof this.button[this.replaceFields.style] == 'string') {
      this.button[this.replaceFields.style] = JSON.parse(this.button[this.replaceFields.style]);
    }
    return {
      options: {},
      buttonTypeOptions: filter(buttonTypeOptions, item => {
        return item.value != 'switch';
      }),
      buttonShapeOptions,
      roleOptions: [],
      newEventHandler: {
        id: undefined,
        eventParams: []
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },
      iconSelectModalVisible: false
    };
  },

  beforeCreate() {},
  components: { EventHandlerAdmin, WidgetIconLibModal },
  computed: {},
  created() {
    if (this.button.role == undefined) {
      this.$set(this.button, 'role', []);
    }
  },
  methods: {
    onConfirmOk() {
      this.newEventHandler.id = generateId();
      this.button.eventHandler.push(deepClone(this.newEventHandler));
      this.newEventHandler = deepClone(this.defaultNewEventHandler);
      this.$refs.eventHandlerModal.visible = false;
    },
    onClickIcon() {
      this.iconSelectModalVisible = true;
    }
  },
  beforeMount() {
    this.defaultNewEventHandler = deepClone(this.newEventHandler);
  },
  mounted() {}
};
</script>
