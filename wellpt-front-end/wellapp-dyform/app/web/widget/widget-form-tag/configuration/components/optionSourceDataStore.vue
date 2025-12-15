<template>
  <!-- 备选项来源-数据仓库 -->
  <div>
    <a-form-model-item label="联动设置" :wrapper-col="{ style: { textAlign: 'right' } }" v-if="showRelate">
      <a-switch v-model="widget.configuration.optionDataAutoSet" />
    </a-form-model-item>
    <a-form-model-item v-if="widget.configuration.optionDataAutoSet" label="联动字段" class="display-b" :label-col="{}" :wrapper-col="{}">
      <select-relate-field :widget="widget" style="margin: 0" />
    </a-form-model-item>
    <a-form-model-item label="数据仓库选择" class="display-b" :label-col="{}" :wrapper-col="{}">
      <DataStoreSelectModal v-model="options.dataSourceId" :displayModal="true" @change="changeDataSourceId" />
    </a-form-model-item>
    <a-form-model-item
      v-for="(opt, i) in dataSourceFieldOptions"
      :key="i"
      :label="opt.label"
      class="display-b"
      :label-col="{}"
      :wrapper-col="{}"
    >
      <a-select
        v-model="options[opt.value]"
        show-search
        style="width: 100%"
        :filter-option="filterOption"
        :options="columnIndexOptions"
        :getPopupContainer="getPopupContainerByPs()"
        :dropdownClassName="getDropdownClassName()"
        :allowClear="opt.allowClear"
      ></a-select>
    </a-form-model-item>
    <template v-if="sortable">
      <a-form-model-item label="排序字段">
        <a-select
          v-model="options.sortField"
          show-search
          style="width: 100%"
          :filter-option="filterOption"
          :options="columnIndexOptions"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
          allow-clear
        />
      </a-form-model-item>
      <a-form-model-item label="排序方式">
        <a-radio-group v-model="options.sortType" button-style="solid" default-value="asc" size="small">
          <a-radio-button value="asc">升序</a-radio-button>
          <a-radio-button value="desc">降序</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
    <a-form-model-item class="display-b" :label-col="{}" :wrapper-col="{}">
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
              <label style="font-weight: bold; line-height: 32px">
                二、支持通过
                <a-tag class="primary-color">:FORM_DATA.字段编码</a-tag>
                使用主表字段变量用于SQL字段比较 (注意：当表单字段数据发生变更后会自动刷新表格)
              </label>
              <p>
                例如: SQL 编写为
                <a-tag>create_time > :FORM_DATA.create_time</a-tag>
              </p>
              <label style="font-weight: bold; line-height: 32px">
                三、支持通过模板标签
                <a-tag class="primary-color"><% %></a-tag>
                内使用js语法输出动态SQL
              </label>
              <p style="line-height: 28px">
                例如: 判断某个字段不为空的情况下, 拼接SQL
                <br />
                <a-tag>
                  <label style="color: red"><% if ( FORM_DATA.user_name !=null ) { %></label>
                  creator = :FORM_DATA.user_name
                  <label style="color: red"><% } else { %></label>
                  creator is null
                  <label style="color: red"><% } %></label>
                </a-tag>
              </p>
            </div>
          </template>
          <label>
            默认查询条件
            <a-icon type="info-circle" />
          </label>
        </a-popover>
      </template>
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
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';

export default {
  name: 'OptionSourceDataStore',
  props: {
    designer: Object,
    widget: Object,
    options: Object,
    dataSourceFieldOptions: {
      type: Array,
      default: () => {
        return [
          {
            label: '显示值字段',
            value: 'dataSourceLabelColumn'
          },
          {
            label: '真实值字段',
            value: 'dataSourceValueColumn'
          }
        ];
      }
    },
    showRelate: {
      type: Boolean,
      default: true
    },
    sortable: {
      type: Boolean,
      default: false
    }
  },
  components: {},
  data() {
    return {
      sqlVarOptions: [
        { label: '当前用户名', value: ':currentUserName' },
        { label: '当前用户登录名', value: ':currentLoginName' },
        { label: '当前用户ID', value: ':currentUserId' },
        { label: '当前系统', value: ':currentSystem' },
        { label: '当前用户主部门ID', value: ':currentUserDepartmentId' },
        { label: '当前用户主部门名称', value: ':currentUserDepartmentName' },
        { label: '当前用户归属单位ID', value: ':currentUserUnitId' },
        { label: '系统当前时间', value: ':sysdate' }
      ],
      dataSourceOptions: [], // 数据仓库
      columnIndexOptions: []
    };
  },
  created() {
    // this.fetchDataSourceOptions();
    this.fetchColumns();
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    // 获取数据仓库
    fetchDataSourceOptions() {
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            this.dataSourceOptions = data.results;
          }
        });
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    // 改变数据仓库
    changeDataSourceId(value) {
      this.dataSourceFieldOptions.forEach(item => {
        this.options[item.value] = null;
      });
      if (this.sortable) {
        this.options.sortField = null;
      }
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      // this.columnIndexOptions.length = 0;
      if (value) this.fetchColumns();
    },

    fetchColumns() {
      if (this.options.dataSourceId) {
        $axios
          .post('/json/data/services', {
            serviceName: 'viewComponentService',
            methodName: 'getColumnsById',
            args: JSON.stringify([this.options.dataSourceId])
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
              // this.columnIndexOptions.length = 0;
              for (let i = 0, len = data.data.length; i < len; i++) {
                this.columnIndexOptions.push({
                  value: data.data[i].columnIndex,
                  label: data.data[i].title
                });
              }
              this.$emit('columnIndexOptionChange', this.columnIndexOptions);
            }
          });
      }
    }
  }
};
</script>
