<template>
  <a-select
    show-search
    :filter-option="filterOption"
    :style="{ width: width }"
    :size="size"
    allowClear
    :showSearch="true"
    :mode="multiSelect ? 'multiple' : 'default'"
    v-model="selectValue"
    @change="onChange"
  >
    <a-icon slot="suffixIcon" type="loading" v-if="loading" />

    <template v-if="moreType">
      <!-- 分组展示 -->
      <template v-for="(grp, g) in options">
        <a-select-opt-group :label="grp.label" v-if="grp.children.length > 0" :key="'dt_grp_' + g">
          <a-select-option v-for="(opt, i) in grp.children" :value="opt.value" :key="opt.value" :title="opt.label">
            {{ opt.label }}
          </a-select-option>
        </a-select-opt-group>
      </template>
    </template>
    <template v-else>
      <a-select-option v-for="(opt, i) in options" :value="opt.value" :key="opt.value" :title="opt.label">{{ opt.label }}</a-select-option>
    </template>
  </a-select>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'DataModelSelect',
  mixins: [],
  props: {
    value: {
      type: [String, Array]
    },

    width: {
      type: String,
      default: '100%'
    },
    size: {
      type: String,
      default: 'default'
    },
    multiSelect: {
      type: Boolean,
      default: false
    },
    dtype: {
      type: [String, Array],
      default: 'TABLE' // TABLE \ VIEW
    }
  },
  inject: ['appId', 'subAppIds'],
  data() {
    return {
      loading: false,
      options: [],
      dataModelMap: {},
      selectValue: this.value === undefined || (!Array.isArray(this.value) && Object.keys(this.value).length == 0) ? undefined : this.value
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    moreType() {
      return Array.isArray(this.dtype) && this.dtype.length > 0;
    }
  },
  created() {},
  methods: {
    reset(type) {
      this.fetchDataModelOptions(type);
      this.selectValue = undefined;
    },
    fetchDataModelOptions(type) {
      this.dataModelMap = {};
      this.loading = true;
      let module = [];
      if (this.appId) {
        module.push(this.appId);
      }
      if (this.subAppIds) {
        module.push(...this.subAppIds);
      }
      this.$tempStorage.getCache(
        { url: '/proxy/api/dm/getDataModelsByType', type: typeof type == 'string' ? [type] : type, module },
        () => {
          return new Promise((resolve, reject) => {
            $axios.post(`/proxy/api/dm/getDataModelsByType`, { type: typeof type == 'string' ? [type] : type, module }).then(({ data }) => {
              if (data.code == 0) {
                resolve(data.data);
              }
            });
          });
        },
        models => {
          this.options.splice(0, this.options.length);
          this.loading = false;
          if (this.moreType) {
            // 分组
            this.options.push(
              {
                label: '存储对象',
                value: 'table',
                children: []
              },
              {
                label: '视图对象',
                value: 'view',
                children: []
              }
            );
          }
          if (models) {
            for (let m of models) {
              this.dataModelMap[m.uuid] = m;
              if (this.moreType) {
                let tar = this.options[m.type == 'TABLE' || m.type == 'RELATION' ? 0 : 1];
                tar.children.push({ label: m.name, value: m.uuid });
              } else {
                this.options.push({ label: m.name, value: m.uuid });
              }
            }
          }
          this.$emit('optionsReady', this.dataModelMap);
        }
      );
    },
    filterOption(input, option) {
      if (option.componentOptions.tag == 'a-select-option') {
        let { title, value } = option.componentOptions.propsData;
        return value.toLowerCase().indexOf(input.toLowerCase()) >= 0 || (title && title.toLowerCase().indexOf(input.toLowerCase()) >= 0);
      }
      return false;
    },
    onChange(value, option) {
      this.$emit('input', value);
      this.$emit('change', value, this.dataModelMap[value]);
    },

    fetchDataModelColumns(uuid) {
      return new Promise((resolve, reject) => {
        this.$tempStorage.getCache(
          `dm.getDetails:${uuid}`,
          () => {
            return new Promise((resolve, reject) => {
              $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
                if (data.code == 0) {
                  let detail = data.data,
                    columns = JSON.parse(detail.columnJson),
                    columnIndexOptions = [];
                  for (let col of columns) {
                    if (detail.type == 'TABLE' || (detail.type == 'VIEW' && col.hidden !== true && col.return === true)) {
                      columnIndexOptions.push({
                        value: col.alias || col.column,
                        label: col.title,
                        isSysDefault: col.isSysDefault
                      });
                    }
                  }
                  resolve(columnIndexOptions);
                }
              });
            });
          },
          data => {
            resolve(data);
          }
        );
      });
    }
  },
  beforeMount() {
    this.fetchDataModelOptions(this.dtype);
  },
  mounted() {}
};
</script>
