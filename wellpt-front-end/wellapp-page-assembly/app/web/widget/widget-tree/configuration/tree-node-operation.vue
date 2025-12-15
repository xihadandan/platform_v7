<template>
  <div>
    <a-form-model :colon="false" labelAlign="left" :label-col="{ span: 5 }" :wrapper-col="{ span: 18, style: { textAlign: 'right' } }">
      <a-form-model-item label="操作名称">
        <a-input v-model="operation.name">
          <template slot="addonAfter">
            <a-checkbox v-model="operation.nameHidden">隐藏名称</a-checkbox>
            <WI18nInput
              v-show="!operation.nameHidden"
              :widget="widget"
              :target="operation"
              :designer="designer"
              :code="operation.id"
              v-model="operation.name"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="节点图标">
        <WidgetDesignModal
          title="选择图标"
          :zIndex="1000"
          :width="640"
          dialogClass="pt-modal widget-icon-lib-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          mask
          bodyContainer
        >
          <IconSetBadge v-model="operation.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="operation.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>

      <DefaultVisibleConfiguration :designer="designer" :configuration="operation" v-if="widget.configuration.dataSourceId">
        <template slot="extraAutoCompleteSelectGroup">
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="appstore" />
              字段
            </span>
            <a-select-option v-for="opt in columnOptions" :key="opt.value" :title="opt.label">
              {{ opt.label }}
              <a-tag style="position: absolute; right: 0px; top: 4px" @click.stop="() => {}">
                {{ opt.value }}
              </a-tag>
            </a-select-option>
          </a-select-opt-group>
        </template>
      </DefaultVisibleConfiguration>
    </a-form-model>
    <WidgetEventHandler :eventModel="operation.eventHandler" :designer="designer" :widget="widget" />
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'TreeNodeOperation',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    operation: Object,
    columnOptions: Array
  },
  data() {
    return { jsHooks: [] };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    jsModuleChanged(v) {
      let _this = this,
        ids = [];
      for (let i = 0, len = v.length; i < len; i++) {
        ids.push(v[i].key);
      }

      $axios.post(`/web/resource/queryJavascriptByIds`, { ids }).then(({ data }) => {
        let options = [];
        data.forEach(d => {
          if (d.hooks && d.hooks.length) {
            d.hooks.forEach(h => {
              options.push({ label: h.description, value: `${d.id}.${h.key}` });
            });
          }
        });
        _this.jsHooks = options;
      });
    }
  },
  mounted() {
    if (this.widget.configuration.jsModules != undefined) {
      this.jsModuleChanged(this.widget.configuration.jsModules);
    }
  }
};
</script>
