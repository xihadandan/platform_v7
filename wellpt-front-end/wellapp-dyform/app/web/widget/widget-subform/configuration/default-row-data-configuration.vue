<template>
  <div>
    <a-form-model-item>
      <template slot="label">
        启用默认行数据
        <a-checkbox v-model="widget.configuration.defaultRowDataConfig.enable" />
      </template>
      <WidgetDesignDrawer
        :id="'subformDefaultRowsConfig' + widget.id"
        title="默认行数据"
        :designer="designer"
        v-if="widget.configuration.defaultRowDataConfig.enable"
      >
        <a-button size="small" type="link" icon="setting">配置行数据</a-button>
        <template slot="content">
          <div class="widget-subform-config-default-row-drawer-content">
            <div style="margin-bottom: 8px">
              <div>
                <a-checkbox :checked="checkAll" :indeterminate="indeterminate" @change="onChangeCheckAll">全选</a-checkbox>
                <template v-if="selectedKeys.length > 0">
                  <a-divider type="vertical" />
                  <a-button size="small" type="link" @click="onDeleteSelected">
                    <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    删除 ({{ selectedKeys.length }})
                  </a-button>
                </template>
                <a-divider type="vertical" />
                <a-checkbox @change="onChangeDeleteCheckAll" :checked="deleteCheckAll" :indeterminate="deleteIndeterminate">
                  默认数据可删除
                </a-checkbox>
              </div>
            </div>
            <a-list :grid="{ gutter: 16, column: 1 }" :data-source="widget.configuration.defaultRowDataConfig.rows" v-if="!loading">
              <a-list-item slot="renderItem" slot-scope="item, index">
                <a-card>
                  <template slot="title">
                    <a-checkbox :checked="selectedKeys.includes(item.id)" @change="onChangeItemChecked(item)" />
                    <!-- {{ index + 1 + '. 默认数据' }} -->
                    <a-divider type="vertical" />
                    <a-checkbox v-model="item.deletable" @change="onChangeDeletable">数据可删除</a-checkbox>
                  </template>
                  <template slot="extra">
                    <div>
                      <a-button size="small" title="删除" type="link" @click="deleteItem(index)">
                        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                      </a-button>
                    </div>
                  </template>

                  <a-form-model
                    :model="item.data"
                    ref="formModal"
                    :key="item.id"
                    :label-col="{ style: { width: '120px' } }"
                    :wrapper-col="{ style: { width: '100%' } }"
                  >
                    <a-space direction="vertical" style="width: 100%">
                      <template v-for="(col, ii) in widget.configuration.columns">
                        <div :class="[col.required ? 'required-line' : '']">
                          <component
                            :is="columnWidgetMap[col.widget.configuration.code].wtype"
                            :widget="columnWidgetMap[col.widget.configuration.code]"
                            :form="dyformObj[index]"
                            @change="e => onChangeDyformComponent(e, columnWidgetMap[col.widget.configuration.code], item.data)"
                          ></component>
                        </div>
                      </template>
                    </a-space>
                  </a-form-model>
                </a-card>
              </a-list-item>

              <div slot="footer">
                <a-button block size="small" icon="plus" @click="addNewRow">新增数据</a-button>
              </div>
            </a-list>
          </div>
        </template>
      </WidgetDesignDrawer>
    </a-form-model-item>
  </div>
</template>
<style lang="less">
.widget-subform-config-default-row-drawer-content {
  .required-line {
    > .widget {
      > .ant-form-item-label {
        > label::before {
          display: inline-block;
          margin-right: 4px;
          color: #f5222d;
          font-size: 14px;
          font-family: SimSun, sans-serif;
          line-height: 1;
          content: '*';
        }
      }
    }
    content: '*';
  }
}
</style>
<script type="text/babel">
import { createDyform } from '../../../framework/vue/dyform/dyform';
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'DefaultRowDataConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  components: {},
  computed: {
    rowIds() {
      let id = [];
      for (let i = 0, len = this.widget.configuration.defaultRowDataConfig.rows.length; i < len; i++) {
        id.push(this.widget.configuration.defaultRowDataConfig.rows[i].id);
      }
      return id;
    },
    deletableCount() {
      let count = 0;
      for (let i = 0, len = this.widget.configuration.defaultRowDataConfig.rows.length; i < len; i++) {
        count += this.widget.configuration.defaultRowDataConfig.rows[i].deletable ? 1 : 0;
      }
      return count;
    }
  },
  provide() {
    return { designMode: false };
  },
  data() {
    return {
      dyformObj: [],
      selectedKeys: [],
      indeterminate: false,
      checkAll: false,
      deleteIndeterminate: false,
      deleteCheckAll: false,
      columnWidgetMap: {},
      loading: true
    };
  },
  beforeCreate() {},
  created() {
    if (this.widget.configuration.defaultRowDataConfig == undefined) {
      this.$set(this.widget.configuration, 'defaultRowDataConfig', {
        enable: false,
        rows: [] // { data : {} , deletable: true }
      });
    } else {
      this.widget.configuration.defaultRowDataConfig.rows.forEach(row => {
        let f = createDyform(this.widget.configuration.formUuid, row.id);
        f.formData = row.data;
        this.dyformObj.push(f);
      });
    }
  },
  beforeMount() {},
  mounted() {
    this.fetchLatestFormColumns().then(fieldMap => {
      for (let key in fieldMap) {
        let wgt = fieldMap[key];
        wgt.configuration.required = false;
        this.columnWidgetMap[key] = wgt;
      }
      this.loading = false;
    });
    this.onChangeDeletable();
  },
  methods: {
    fetchLatestFormColumns() {
      return new Promise((resolve, reject) => {
        this.fetchFormDefinitionVjson(this.widget.configuration.formUuid).then(definition => {
          if (definition) {
            let json = JSON.parse(definition.definitionVjson);
            let fieldMap = {};
            for (let i = 0, len = json.fields.length; i < len; i++) {
              fieldMap[json.fields[i].configuration.code] = json.fields[i];
            }
            resolve(fieldMap);
          }
        });
      });
    },
    fetchFormDefinitionVjson(formUuid) {
      return new Promise((resolve, reject) => {
        this.$tempStorage.getItem(formUuid).then(cacheData => {
          console.log(`${formUuid} -> 获取缓存数据`, cacheData);
          if (cacheData == null) {
            if (formUuid) {
              $axios
                .post('/json/data/services', {
                  serviceName: 'formDefinitionService',
                  methodName: 'getOne',
                  args: JSON.stringify([formUuid])
                })
                .then(({ data }) => {
                  if (data.code == 0 && data.data && data.data.definitionVjson) {
                    this.$tempStorage.setItem(formUuid, data.data);
                    resolve(data.data);
                  }
                });
            }
          } else {
            resolve(cacheData);
          }
        });
      });
    },
    onChangeDeletable() {
      this.$nextTick(() => {
        this.deleteIndeterminate = this.deletableCount > 0 && this.deletableCount < this.rowIds.length;
        this.deleteCheckAll = this.deletableCount > 0 && this.deletableCount == this.rowIds.length;
      });
    },
    onChangeDeleteCheckAll(e) {
      this.deleteIndeterminate = false;
      this.deleteCheckAll = e.target.checked && this.rowIds.length > 0;
      this.widget.configuration.defaultRowDataConfig.rows.forEach(r => {
        r.deletable = this.deleteCheckAll;
      });
    },
    onChangeItemChecked(record) {
      let i = this.selectedKeys.indexOf(record.id);
      if (i == -1) {
        this.selectedKeys.push(record.id);
      } else {
        this.selectedKeys.splice(i, 1);
      }
      this.changeCheckAllState();
    },
    onChangeCheckAll(e) {
      this.indeterminate = false;
      this.checkAll = e.target.checked && this.rowIds.length > 0;
      this.selectedKeys.splice(0, this.selectedKeys.length);
      if (this.checkAll) {
        this.selectedKeys.push(...this.rowIds);
      }
    },
    changeCheckAllState() {
      this.$nextTick(() => {
        this.indeterminate = this.selectedKeys.length > 0 && this.selectedKeys.length < this.rowIds.length;
        this.checkAll = this.selectedKeys.length > 0 && this.selectedKeys.length == this.rowIds.length;
      });
    },
    deleteItem(index) {
      this.dyformObj.splice(index, 1);
      this.widget.configuration.defaultRowDataConfig.rows.splice(index, 1);
      this.changeCheckAllState();
      this.onChangeDeletable();
    },

    onDeleteSelected() {
      let rows = this.widget.configuration.defaultRowDataConfig.rows;
      for (let i = 0; i < rows.length; i++) {
        if (this.selectedKeys.includes(rows[i].id)) {
          this.selectedKeys.splice(this.selectedKeys.indexOf(rows[i].id), 1);
          this.dyformObj.splice(i, 1);
          rows.splice(i--, 1);
        }
      }
      this.changeCheckAllState();
      this.onChangeDeletable();
    },
    addNewRow() {
      let row = {
        id: generateId('SF'),
        data: {},
        deletable: true
      };
      this.widget.configuration.defaultRowDataConfig.rows.push(row);
      let form = createDyform(this.widget.configuration.formUuid, row.id);
      form.formData = row.data;
      this.dyformObj.push(form);
      this.changeCheckAllState();
      this.onChangeDeletable();
    },
    onChangeDyformComponent(e, widget, formData) {
      if (widget.wtype == 'WidgetFormFileUpload' && formData[widget.configuration.code] != undefined) {
        let v = e.$vue;
        let files = [];
        v.fileList.forEach(a => {
          files.push({
            fileID: a.dbFile.fileID,
            filename: a.dbFile.filename,
            fileSize: a.dbFile.fileSize,
            createTimeStr: a.dbFile.createTimeStr
          });
        });
        formData[widget.configuration.code] = files;
      }
    }
  }
};
</script>
