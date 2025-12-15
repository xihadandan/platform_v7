<template>
  <div>
    <a-form-model-item>
      <template slot="label">
        <span
          style="cursor: pointer"
          :class="formData.uuid ? 'ant-btn-link' : ''"
          @click="redirectDataModelDesign(formData.uuid)"
          :title="formData.uuid ? '打开数据模型' : ''"
        >
          数据模型选择
        </span>
      </template>
      <DataModelSelectModal v-model="formData.uuid" ref="dataModelSelect" :dtype="['TABLE', 'VIEW']" @change="onChangeDataModel" />
    </a-form-model-item>
    <a-form-model-item label="标题字段">
      <w-select v-model="formData.labelColumn" :options="columnIndexOptions" :replaceFields="replaceFields" />
    </a-form-model-item>
    <a-form-model-item label="子标题字段">
      <w-select v-model="formData.subTileColumn" :options="columnIndexOptions" :replaceFields="replaceFields" />
    </a-form-model-item>
    <a-form-model-item label="状态字段">
      <w-select v-model="formData.statusColumn" :options="columnIndexOptions" :replaceFields="replaceFields" />
    </a-form-model-item>
    <a-form-model-item label="状态显示条件" class="display-b" :label-col="{}" :wrapper-col="{}">
      <div v-for="(item, index) in formData.statusConditions" :key="index" class="steps-status-item">
        <w-select v-model="item.operator" :options="[{ label: '等于', value: '==' }]" :allowClear="false" />
        <a-input v-model="item.value" />
        <w-select v-model="item.status" :options="statusOptionsFormat" />
        <a-button type="link" size="small" @click="delCondition(item, index)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
      </div>
      <a-button size="small" type="link" @click="addCondition">
        <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
        添加
      </a-button>
    </a-form-model-item>
    <a-form-model-item label="描述字段">
      <w-select v-model="formData.descColumn" :options="columnIndexOptions" :replaceFields="replaceFields" />
    </a-form-model-item>
    <a-form-model-item label="页面类型">
      <a-radio-group size="small" v-model="formData.pageType">
        <a-radio-button :value="item.value" v-for="item in pageTypeOptions" :key="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="formData.pageType === pageTypeOptions[0]['value']">
      <a-form-model-item label="页面字段">
        <w-select v-model="formData.pageId" :options="columnIndexOptions" :replaceFields="replaceFields" />
      </a-form-model-item>
    </template>
    <template v-if="formData.pageType === pageTypeOptions[1]['value']">
      <a-form-model-item>
        <template slot="label">
          <span>url地址</span>
          <a-popover placement="rightTop" :title="null">
            <template slot="content">
              支持通过
              <a-tag>${ 字段 }</a-tag>
              设值到地址上
            </template>
            <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
          </a-popover>
        </template>
        <a-input v-model="formData.renderUrl" />
      </a-form-model-item>
    </template>
    <a-form-model-item label="默认过滤条件">
      <a-textarea
        placeholder="请输入过滤条件"
        v-model="formData.defaultCondition"
        :rows="4"
        :style="{
          height: 'auto'
        }"
      />
    </a-form-model-item>
  </div>
</template>

<script>
import { statusOptions } from './constant';
import WSelect from '@workflow/app/web/page/workflow-designer/component/components/w-select';

export default {
  name: 'StepDataModel',
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WSelect
  },
  data() {
    return {
      statusOptions: JSON.parse(JSON.stringify(statusOptions)),
      dataStoreOptions: [],
      columnIndexOptions: [],
      replaceFields: {
        title: 'title',
        key: 'columnIndex',
        value: 'columnIndex'
      },
      pageTypeOptions: [
        { label: '页面', value: 'page' },
        { label: 'url', value: 'url' }
      ]
    };
  },
  computed: {
    statusOptionsFormat() {
      const status = this.formData.statusConditions.map(item => item.status);

      return this.statusOptions.map(item => {
        item.disabled = false;
        if (status.includes(item.value)) {
          item.disabled = true;
        }
        return item;
      });
    }
  },
  created() {
    if (!this.formData.hasOwnProperty('statusConditions')) {
      this.$set(this.formData, 'statusConditions', []);
    }
    if (!this.formData.hasOwnProperty('pageType')) {
      this.$set(this.formData, 'pageType', 'page');
    }
  },
  mounted() {
    if (this.formData.uuid) {
      this.fetchColumns();
    }
  },
  methods: {
    redirectDataModelDesign(uuid) {
      if (uuid) {
        window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
      }
    },
    addCondition() {
      if (this.formData.statusConditions.length >= this.statusOptions.length) {
        return;
      }
      this.formData.statusConditions.push({
        operator: '==',
        value: '',
        status: ''
      });
    },
    delCondition(item, index) {
      this.formData.statusConditions.splice(index, 1);
    },
    onChangeDataModel(value, option) {
      this.columnIndexOptions = [];
      for (const key in this.formData) {
        this.formData[key] = undefined;
      }
      this.formData.uuid = value;
      this.formData.statusConditions = [];

      if (value) this.fetchColumns();
    },
    fetchColumns() {
      this.$refs.dataModelSelect.fetchDataModelColumns(this.formData.uuid).then(list => {
        if (list) {
          this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
          this.columnIndexOptions.push(...list);
        }
      });
    }
  }
};
</script>
