<template>
  <div v-if="configuration[configCode] != undefined">
    <a-form-model-item label="用户操作日志">
      <a-switch v-model="configuration[configCode].enable"></a-switch>
      <WidgetDesignModal title="内容设置" :zIndex="1000" :width="800">
        <template slot="content">
          <div>
            <a-form-model-item>
              <template slot="label">
                <span>
                  日志描述
                  <a-popover>
                    <template slot="content">
                      <div>
                        根据字段等数据, 自定义日志描述内容
                        <div>
                          1. 通过
                          <a-tag>+</a-tag>
                          号拼接字符与字段变量定义内容, 例如:

                          <span style="color: red; font-weight: bolder">
                            '删除数据:' +
                            <a-tag style="color: red; font-weight: bolder">字段</a-tag>
                          </span>
                        </div>
                        <div>
                          2. 使用​​反引号
                          <a-tag>`</a-tag>
                          与
                          <a-tag>${}</a-tag>
                          字符串​​模板定义内容, 例如:
                          <span style="color: red; font-weight: bolder">
                            `删除数据: ${
                            <a-tag style="color: red; font-weight: bolder; margin: 0px">字段</a-tag>
                            }`
                          </span>
                        </div>
                      </div>
                    </template>
                    <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                  </a-popover>
                </span>
              </template>
              <FormulaEditor
                :widget="widget"
                :bind-to-configuration="configuration[configCode]"
                configKey="description"
                :enableFormulaFunction="true"
                :supportVariableTypes="supportVariableTypes"
                :extVariableOptions="extFormulaVariableOptions"
                :extFormulaFunctionLibrary="extFormulaFunctionLibrary"
              />
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <a-popover>
                  <template slot="content">
                    <div>可以直接选择关联的字段数据来定义日志业务编码，用于快速检索业务相关的日志</div>
                  </template>
                  日志业务编码
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </a-popover>
              </template>
              <FormulaEditor
                :widget="widget"
                :bind-to-configuration="configuration[configCode]"
                configKey="businessCode"
                :enableFormulaFunction="false"
                :extVariableOptions="extFormulaVariableOptions"
              />
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <a-popover>
                  <template slot="content">
                    <div>业务数据保存于日志内</div>
                  </template>
                  日志业务数据
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </a-popover>
              </template>
              <FormulaEditor
                :widget="widget"
                :bind-to-configuration="configuration[configCode]"
                configKey="extraInfo"
                :enableFormulaFunction="false"
                :extVariableOptions="extFormulaVariableOptions"
              />
            </a-form-model-item>
          </div>
        </template>

        <a-button v-if="configuration[configCode].enable" icon="setting" type="link" size="small">内容设置</a-button>
      </WidgetDesignModal>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'BehaviorLogConfiguration',
  props: {
    configuration: Object,
    designer: Object,
    widget: Object,
    extFormulaVariableOptions: Array,
    extFormulaFunctionLibrary: Object,
    configCode: {
      type: String,
      default: 'behaviorLogConfig'
    }
  },
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    if (this.configuration && !this.configuration.hasOwnProperty(this.configCode)) {
      this.$set(this.configuration, this.configCode, { enable: false });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
