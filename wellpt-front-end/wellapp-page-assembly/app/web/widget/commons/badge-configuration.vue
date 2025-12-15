<template>
  <div v-if="configuration.badge != undefined">
    <a-form-model-item label="徽标">
      <a-switch v-model="configuration.badge.enable" />
    </a-form-model-item>
    <div v-show="configuration.badge.enable">
      <a-form-model-item label="徽标数来源">
        <a-select
          v-model="configuration.badge.badgeSourceType"
          :options="[
            { label: '数据仓库', value: 'dataSource' },
            { label: '数据模型', value: 'dataModel' },
            { label: 'JS模块', value: 'jsFunction' }
          ]"
          allow-clear
          :getPopupContainer="getPopupContainerNearestPs()"
        />
      </a-form-model-item>

      <div v-if="configuration.badge.badgeSourceType === 'dataSource'">
        <a-form-model-item label="数据仓库查询">
          <DataStoreSelectModal v-model="configuration.badge.dataSourceId" :displayModal="true" />
        </a-form-model-item>
      </div>
      <div v-if="configuration.badge.badgeSourceType === 'jsFunction'">
        <a-form-model-item label="执行JS方法">
          <JsHookSelect :designer="designer" :widget="widget" v-model="configuration.badge.countJsFunction" />
        </a-form-model-item>
      </div>
      <div v-if="configuration.badge.badgeSourceType == 'dataModel'">
        <a-form-model-item>
          <template slot="label">
            <span
              style="cursor: pointer"
              :class="configuration.badge.dataModelUuid ? 'ant-btn-link' : ''"
              @click="redirectDataModelDesign(configuration.badge.dataModelUuid)"
              :title="configuration.badge.dataModelUuid ? '打开数据模型' : ''"
            >
              数据模型
              <a-icon type="environment" v-show="configuration.badge.dataModelUuid" style="color: inherit; line-height: 1" />
            </span>
          </template>

          <DataModelSelectModal v-model="configuration.badge.dataModelUuid" ref="dataModelSelect" :dtype="['TABLE', 'VIEW']" />
        </a-form-model-item>
      </div>
      <div v-show="configuration.badge.badgeSourceType === 'dataSource' || configuration.badge.badgeSourceType == 'dataModel'">
        <a-form-model-item>
          <template slot="label">
            <a-popover title="支持编写动态SQL" placement="left" :mouseEnterDelay="0.5" v-if="conditionTip">
              <template slot="content">
                <div>
                  <label style="font-weight: bold; line-height: 32px">一、支持使用系统内置变量</label>
                  <ol>
                    <li v-for="(item, i) in sqlVarOptions" style="margin-bottom: 8px">
                      <a-tag class="primary-color">{{ item.value }}</a-tag>
                      : {{ item.label }}
                    </li>
                  </ol>
                  <p>
                    例如: SQL 编写为
                    <a-tag>creator = :currentUserId</a-tag>
                  </p>
                </div>
                <slot name="defaultCondition"></slot>
              </template>
              <label>
                默认查询条件
                <a-icon type="info-circle" style="vertical-align: middle" />
              </label>
            </a-popover>
            <template v-else>默认查询条件</template>
          </template>
          <a-textarea v-model="configuration.badge.defaultCondition"></a-textarea>
        </a-form-model-item>
      </div>
    </div>
  </div>
</template>
<style></style>

<script type="text/babel">
import JsHookSelect from './js-hook-select.vue';
import DataModelSelect from './data-model-select.vue';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function.js';
export default {
  name: 'BadgeConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object,
    conditionTip: Boolean // 默认查询条件提示
  },
  data() {
    return {
      dataSourceOptions: [],
      jsHooks: [],
      sqlVarOptions: [
        { label: '当前用户名', value: ':currentUserName' },
        { label: '当前用户登录名', value: ':currentLoginName' },
        { label: '当前用户ID', value: ':currentUserId' },
        { label: '当前系统', value: ':currentSystem' },
        { label: '当前用户主部门ID', value: ':currentUserDepartmentId' },
        { label: '当前用户主部门名称', value: ':currentUserDepartmentName' },
        { label: '当前用户归属单位ID', value: ':currentUserUnitId' },
        { label: '系统当前时间', value: ':sysdate' }
      ]
    };
  },

  beforeCreate() {},
  components: { JsHookSelect, DataModelSelect },
  computed: {},
  created() {
    if (this.configuration.badge === undefined) {
      this.$set(this.configuration, 'badge', {
        enable: false,
        badgeSourceType: undefined,
        dataSourceId: undefined,
        dataModelUuid: undefined,
        jsFunction: undefined
      });
    }
    this.fetchJsHooksOptions();
    // this.fetchDataSourceOptions();
  },
  methods: {
    getPopupContainerNearestPs,
    redirectDataModelDesign(uuid) {
      window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
    },
    fetchJsHooksOptions() {
      if (this.widget == undefined || this.widget.configuration.jsModules == undefined || this.widget.configuration.jsModules.length == 0) {
        return;
      }
      let ids =
        typeof this.widget.configuration.jsModules === 'string'
          ? [this.widget.configuration.jsModules]
          : this.widget.configuration.jsModules;
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
    },
    fetchDataSourceOptions(value) {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            _this.dataSourceOptions = data.results;
          }
        });
    },
    filterOption(input, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
