<template>
  <div>
    <a-form-model>
      <a-form-model-item label="按钮名称">
        <a-input v-model="button.title">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :designer="designer" :code="button.id" :target="button" v-model="button.title" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="编码">
        <a-input v-model="button.code" />
      </a-form-model-item>
      <!-- <a-form-model-item label="角色可访问">
        <RoleSelect v-model="button.role" />
      </a-form-model-item> -->
      <a-form-model-item label="按钮类型">
        <a-select :options="buttonTypeOptions" v-model="button.style.type" :style="{ width: '100%' }"></a-select>
      </a-form-model-item>
      <div v-if="button.switch && button.style.type === 'switch'">
        <a-form-model-item label="选中时内容">
          <a-input v-model="button.switch.checkedText" />
        </a-form-model-item>
        <a-form-model-item label="非选中时内容">
          <a-input v-model="button.switch.UnCheckedText" />
        </a-form-model-item>
        <a-form-model-item label="默认选中">
          <a-switch v-model="button.switch.defaultChecked" />
        </a-form-model-item>
      </div>
      <!-- <a-form-model-item label="展示条件" v-show="setDisplayPosition">
        <WidgetCodeEditor @save="value => (button.visibleCondition = value)" :value="button.visibleCondition">
          <a-button icon="code">编写代码</a-button>
          <template slot="help">
            <div>入参说明：</div>
            <ul>
              <li>
                <a-tag>row</a-tag>
                : 表示表格内的行数据
              </li>
            </ul>
          </template>
        </WidgetCodeEditor>
      </a-form-model-item> -->

      <a-form-model-item label="按钮图标" v-show="button.style.type != 'switch'">
        <WidgetDesignModal
          title="选择图标"
          :zIndex="1002"
          :width="640"
          dialogClass="pt-modal widget-icon-lib-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          mask
          bodyContainer
        >
          <IconSetBadge v-model="button.style.icon" onlyIconClass />
          <template slot="content">
            <WidgetIconLib v-model="button.style.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
    </a-form-model>
    <WidgetEventHandler
      :eventModel="button.eventHandler"
      :designer="designer"
      :widget="widget"
      :rule="{ name: false, triggerSelectable: false }"
      :snippets="snippets"
    />
  </div>
</template>
<script type="text/babel">
import { buttonTypeOptions, buttonShapeOptions } from '../../commons/constant';
export default {
  name: 'ButtonConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    button: Object
  },

  data() {
    return {
      snippets: [
        {
          name: 'closeModal | 关闭弹窗',
          content: 'this.closeModal(); // 关闭弹窗' // tabTrigger: 'close', trigger: 'this.'
        }
      ],
      options: {},
      jsHooks: [],
      buttonTypeOptions,
      buttonShapeOptions
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (this.button.role == undefined) {
      this.$set(this.button, 'role', []);
    }
  },
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
  },
  watch: {
    'widget.configuration.jsModules': {
      deep: true,
      handler(v) {
        this.jsModuleChanged(v);
      }
    }
  }
};
</script>
