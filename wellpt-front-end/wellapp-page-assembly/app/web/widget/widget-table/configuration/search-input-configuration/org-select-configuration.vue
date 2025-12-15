<template>
  <div>
    <a-form-model-item label="指定组织">
      <a-select v-model="options.orgUuid" allowClear :options="orgOptions" showSearch :filterOption="filterOption" />
    </a-form-model-item>
    <a-form-model-item label="可选组织选择项">
      <a-select mode="multiple" v-model="options.orgType" :options="orgTypeOptions" />
    </a-form-model-item>
    <a-form-model-item label="默认组织选择项">
      <a-select v-model="options.defaultOrgType" :options="orgTypeOptions" />
    </a-form-model-item>
    <a-form-model-item label="可选节点类型">
      <a-select mode="tags" style="width: 100%" v-model="options.checkableTypes" :showArrow="true">
        <a-select-option v-for="(opt, i) in checkTypeOptions" :value="opt.value" :key="'checktyp_' + i">
          {{ opt.label }}
        </a-select-option>
      </a-select>
    </a-form-model-item>

    <a-form-model-item label="是否多选">
      <a-switch v-model="options.multiSelect" />
    </a-form-model-item>
    <a-form-model-item label="返回值格式">
      <a-radio-group size="small" v-model="options.isPathValue" button-style="solid">
        <a-radio-button :value="true">完整格式</a-radio-button>
        <a-radio-button :value="false">仅组织ID</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="选中节点层级显示">
      <a-switch v-model="options.titlePath" />
    </a-form-model-item>
    <a-form-model-item label="输入框风格">
      <a-select v-model="options.inputDisplayStyle" :options="displayStyleOptions" />
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import OptionSourceConfigration from './option-source-configuration.vue';
export default {
  name: 'organizationSearchConfig',
  mixins: [],
  props: {
    options: Object
  },
  data() {
    return {
      orgOptions: [],
      orgTypeOptions: [
        { label: '我的组织', value: 'MyOrg' },
        { label: '我的领导', value: 'MyLeader' },
        { label: '我的部门', value: 'MyDept' },
        { label: '我的下属', value: 'MyUnderling' }
      ],
      displayStyleOptions: [
        { label: '标签', value: 'IconLabel' },
        { label: '标签分组', value: 'GroupIconLabel' },
        { label: '纯文本(以分号;分隔)', value: 'Label' }
      ],
      checkTypeOptions: []
    };
  },

  beforeCreate() {},
  components: { OptionSourceConfigration },
  computed: {},
  created() {},
  methods: {
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    fetchOrgElementModel() {
      let _this = this;
      $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          let opt = [],
            models = {};
          for (let i = 0, len = data.data.length; i < len; i++) {
            if (data.data[i].enable) {
              models[data.data[i].id] = data.data[i];
            }
          }
          if (models.unit) {
            opt.push({ label: models.unit.name, value: 'unit' });
            delete models.unit;
          }
          if (models.dept) {
            opt.push({ label: models.dept.name, value: 'dept' });
            delete models.dept;
          }
          if (models.job) {
            opt.push({ label: models.job.name, value: 'job' });
            delete models.job;
          }
          opt.push({ label: '用户', value: 'user' });
          opt.push({ label: '群组', value: 'group' });
          opt.push({ label: '职务', value: 'duty' });
          for (let k in models) {
            opt.push({ label: models[k].name, value: k });
          }
          _this.checkTypeOptions = opt;
        }
      });
    },
    fetchOrgOptions() {
      let _this = this;
      $axios
        .get(`/proxy/api/org/organization/queryEnableOrgs`, { params: { system: null } })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            let opt = [];
            for (let i = 0, len = data.data.length; i < len; i++) {
              opt.push({
                label: data.data[i].name,
                value: data.data[i].uuid
              });
            }
            _this.orgOptions = opt;
          }
        })
        .catch(() => {
          _this.$message.error('组织服务异常');
        });
    }
  },
  mounted() {
    this.fetchOrgOptions();
    this.fetchOrgElementModel();
  }
};
</script>
