<template>
  <div>
    <a-form-model-item label="数据集类型">
      <a-select v-model="widget.configuration.dataSourceType">
        <a-select-option value="static">静态JSON数据集</a-select-option>
        <a-select-option value="datasetService">数据集接口</a-select-option>
        <a-select-option value="dataModel">数据模型</a-select-option>
        <a-select-option value="dataSource">数据仓库</a-select-option>
      </a-select>
    </a-form-model-item>
    <div v-show="widget.configuration.dataSourceType === 'static'">
      <a-form-model-item label="静态数据">
        <WidgetDesignDrawer title="静态数据" :id="'WidgetBarChartStaticDatasetJsonDrawer' + widget.id" :designer="designer">
          <a-button size="small" type="link">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            设置
          </a-button>
          <template slot="content">
            <WidgetCodeEditor
              v-model="widget.configuration.datasetJson"
              width="auto"
              height="600px"
              lang="json"
              :hideError="true"
            ></WidgetCodeEditor>
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
    </div>
    <div v-show="widget.configuration.dataSourceType === 'datasetService'">
      <a-form-model-item>
        <template slot="label">
          <a-popover placement="bottomRight" :arrowPointAtCenter="true">
            <template slot="content">
              <div>
                提供继承于
                <code>com.wellsoft.pt.report.echart.support.EchartDatasetLoad</code>
                的后端服务类实例
              </div>
            </template>
            接口
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-popover>
        </template>

        <a-select
          show-search
          allow-clear
          :options="datasetServiceOptions"
          v-model="widget.configuration.datasetService"
          :filter-option="filterSelectOption"
        ></a-select>
      </a-form-model-item>
    </div>
    <div v-if="['dataModel', 'dataSource'].includes(widget.configuration.dataSourceType)">
      <a-form-model-item label="数据仓库" v-if="widget.configuration.dataSourceType === 'dataSource'">
        <DataStoreSelectModal v-model="widget.configuration.dataSourceId" :displayModal="true" @change="onChangeDataSource" />
      </a-form-model-item>
      <a-form-model-item v-else>
        <template slot="label">
          <span
            style="cursor: pointer"
            :class="widget.configuration.dataModelUuid ? 'ant-btn-link' : ''"
            @click="redirectDataModelDesign(widget.configuration.dataModelUuid)"
            :title="widget.configuration.dataModelUuid ? '打开数据模型' : ''"
          >
            数据模型
            <a-icon type="environment" v-show="widget.configuration.dataModelUuid" style="color: inherit; line-height: 1" />
          </span>
        </template>
        <DataModelSelectModal
          v-model="widget.configuration.dataModelUuid"
          :dtype="['TABLE', 'VIEW']"
          :displayModal="true"
          ref="dataModelSelect"
          @change="onDataModelChange"
        />
      </a-form-model-item>

      <a-form-model-item>
        <template slot="label">
          <a-popover title="支持编写动态SQL" placement="left" :mouseEnterDelay="0.5">
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
            </template>
            <label>
              默认查询条件
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </label>
          </a-popover>
        </template>
        <a-switch v-model="widget.configuration.enableDefaultCondition" />
      </a-form-model-item>
      <a-form-model-item :label="null" v-show="widget.configuration.enableDefaultCondition">
        <a-textarea
          placeholder="默认查询条件"
          v-model="widget.configuration.defaultCondition"
          allowClear
          :autoSize="{ minRows: 3, maxRows: 6 }"
        />
      </a-form-model-item>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { filterSelectOption } from '@framework/vue/utils/function';

export default {
  name: 'DatasetConfiguration',
  props: {
    widget: Object,
    designer: Object,
    datasetColumns: Array
  },
  components: {},
  computed: {},
  data() {
    return {
      datasetServiceOptions: [],
      dataSourceOptions: [],
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
  created() {},
  beforeMount() {
    this.fetchDatasetServiceOptions();
    if (this.widget.configuration.dataSourceType === 'dataModel') {
      this.onDataModelChange(this.widget.configuration.dataModelUuid);
    } else if (this.widget.configuration.dataSourceType === 'dataSource') {
      this.onChangeDataSource();
    }
  },
  mounted() {},
  methods: {
    redirectDataModelDesign(uuid) {
      if (uuid) {
        window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
      }
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
    filterSelectOption,
    fetchDataModelDetails(uuid) {
      return new Promise((resolve, reject) => {
        if (uuid) {
          $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
            if (data.code == 0) {
              let detail = data.data,
                columns = JSON.parse(detail.columnJson),
                columnOptions = [];
              for (let col of columns) {
                if (detail.type == 'TABLE' || (detail.type == 'VIEW' && col.hidden !== true && col.return === true)) {
                  if (col.isSysDefault) {
                    continue;
                  }
                  columnOptions.push({
                    dataIndex: col.alias || col.column,
                    title: col.title,
                    column: col
                  });
                }
              }
              resolve(columnOptions);
            }
          });
        }
      });
    },
    fetchDataSourceColumns(dataSourceId) {
      return $axios.post('/json/data/services', {
        serviceName: 'viewComponentService',
        methodName: 'getColumnsById',
        args: JSON.stringify([dataSourceId])
      });
    },
    onChangeDataSource() {
      this.datasetColumns.splice(0, this.datasetColumns.length);
      if (this.widget.configuration.dataSourceId) {
        this.fetchDataSourceColumns(this.widget.configuration.dataSourceId).then(({ data }) => {
          if (data.data) {
            data.data.forEach(column => {
              this.datasetColumns.push({
                dataIndex: column.columnIndex,
                title: column.title
              });
            });
          }
        });
      }
    },
    onDataModelChange(uuid) {
      this.datasetColumns.splice(0, this.datasetColumns.length);
      if (this.widget.configuration.dataModelUuid) {
        this.fetchDataModelDetails(this.widget.configuration.dataModelUuid).then(columnOptions => {
          this.datasetColumns.push(...columnOptions);
        });
      }
    },
    fetchDatasetServiceOptions() {
      this.datasetServiceOptions.splice(0, this.datasetServiceOptions.length);
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'reportFacadeService',
          queryMethod: 'loadEchartDatasetServiceData'
        })
        .then(({ data }) => {
          if (data.results) {
            data.results.forEach(d => {
              this.datasetServiceOptions.push({
                label: d.text,
                value: d.id
              });
            });
          }
        });
    }
  }
};
</script>
