<template>
  <div>
    <a-form-model-item label="数据仓库选择">
      <DataStoreSelectModal v-model="options.dataSourceId" :displayModal="true" @change="changeDataStoreId" />
    </a-form-model-item>
    <a-form-model-item label="标题字段">
      <w-select v-model="options.dataSourceLabelColumn" :options="columnIndexOptions" :replaceFields="replaceFields" />
    </a-form-model-item>
    <a-form-model-item label="子标题字段">
      <w-select v-model="options.dataSourceSubTileColumn" :options="columnIndexOptions" :replaceFields="replaceFields" />
    </a-form-model-item>
    <a-form-model-item label="状态字段">
      <w-select v-model="options.dataSourceStatusColumn" :options="columnIndexOptions" :replaceFields="replaceFields" />
    </a-form-model-item>
    <a-form-model-item label="状态显示条件" class="display-b" :label-col="{}" :wrapper-col="{}">
      <!-- 
        状态字段==0 等待
        状态字段==1 完成
      -->
      <div v-for="(item, index) in options.dataSourceStatusConditions" :key="index" class="steps-status-item">
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
      <w-select v-model="options.dataSourceDescColumn" :options="columnIndexOptions" :replaceFields="replaceFields" />
    </a-form-model-item>
    <a-form-model-item label="页面类型">
      <a-radio-group size="small" v-model="options.pageType">
        <a-radio-button :value="item.value" v-for="item in pageTypeOptions" :key="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="options.pageType === pageTypeOptions[0]['value']">
      <a-form-model-item label="页面字段">
        <w-select v-model="options.dataSourcePageId" :options="columnIndexOptions" :replaceFields="replaceFields" />
      </a-form-model-item>
    </template>
    <template v-if="options.pageType === pageTypeOptions[1]['value']">
      <a-form-model-item>
        <!-- 
        部分常量+部分变量 /workflow-designer/index?uuid=${uuid}
        全部变量 /${systemId}/${uuid}
      -->
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
        <a-input v-model="options.dataSourceRenderUrl" />
      </a-form-model-item>
    </template>
    <a-form-model-item label="默认过滤条件">
      <a-textarea
        placeholder="请输入过滤条件"
        v-model="options.defaultCondition"
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
  name: 'StepDataStore',
  props: {
    options: {
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
      const status = this.options.dataSourceStatusConditions.map(item => item.status);

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
    if (!this.options.hasOwnProperty('dataSourceStatusConditions')) {
      this.$set(this.options, 'dataSourceStatusConditions', []);
    }
    if (!this.options.hasOwnProperty('pageType')) {
      this.$set(this.options, 'pageType', 'page');
    }
    this.fetchDataSourceOptions();
    if (this.options.dataSourceId) {
      this.fetchColumns();
    }
  },
  methods: {
    addCondition() {
      if (this.options.dataSourceStatusConditions.length >= this.statusOptions.length) {
        return;
      }
      this.options.dataSourceStatusConditions.push({
        operator: '==',
        value: '',
        status: ''
      });
    },
    delCondition(item, index) {
      this.options.dataSourceStatusConditions.splice(index, 1);
    },
    changeDataStoreId(value, option) {
      this.columnIndexOptions = [];
      for (const key in this.options) {
        if (key.indexOf('dataSource') === 0) {
          this.options[key] = undefined;
        }
      }
      this.options.defaultCondition = undefined;
      this.options.dataSourceId = value;
      this.options.dataSourceStatusConditions = [];

      if (value) this.fetchColumns();
    },
    // 获取数据仓库
    fetchDataSourceOptions() {
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            this.dataStoreOptions = data.results;
          }
        });
    },
    fetchColumns() {
      this.$axios
        .post('/json/data/services', {
          serviceName: 'viewComponentService',
          methodName: 'getColumnsById',
          args: JSON.stringify([this.options.dataSourceId])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.columnIndexOptions = data.data;
          }
        });
    }
  }
};
</script>
