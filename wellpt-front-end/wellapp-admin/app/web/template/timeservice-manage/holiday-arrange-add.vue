<template>
  <div class="holiday_arrangement">
    <div class="holiday_arrangement_add flex">
      <div class="holiday_arrangement_add_left f_g_1">
        <div class="search-box">
          <a-input-search v-model="keyword" @search="getFormData" />
          <div class="tags-select">
            <a-checkable-tag
              v-for="tag in tags"
              :key="tag"
              :checked="selectedTags.indexOf(tag) > -1"
              @change="checked => tagChange(tag, checked)"
            >
              {{ tag }}
            </a-checkable-tag>
          </div>
        </div>
        <a-table
          :data-source="holidayData"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: tableSelectChange }"
          :pagination="false"
          :row-key="(record, index) => record.uuid"
          :columns="columns"
          :scroll="{ y: 400 }"
          :loading="loading"
          :bordered="false"
          class="pt-table"
        >
          <span slot="tags" slot-scope="tags">
            <a-tag v-for="(item, index) in tags" :key="index">
              {{ item }}
            </a-tag>
          </span>
        </a-table>
      </div>
      <div class="holiday_arrangement_add_right f_s_0" style="width: 300px">
        <a-card class="pt-card" :bordered="false" :title="`已选择${selectedHoliday.length}项`" style="width: 300px; height: 100%">
          <Scroll style="max-height: 520px">
            <div class="holiday-right-list">
              <div class="holiday-right-item" v-for="(item, index) in selectedHoliday" :key="index">
                <span>{{ item.name }}</span>
                <i class="iconfont icon-ptkj-dacha-xiao" @click="delItem(index)"></i>
              </div>
            </div>
          </Scroll>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script type="text/babel">
import moment from 'moment';
import { each, map, findIndex, find, sortBy, filter } from 'lodash';
export default {
  name: 'HolidayArrangeAdd',
  props: {
    holiday: {
      type: Array,
      default: () => {
        return [];
      }
    },
    autoUpdate: {
      type: Boolean,
      default: false
    },
    year: {
      type: Number,
      default: () => {
        return moment().year();
      }
    },
    baseHoliday: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  components: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      $evtWidget: undefined,
      $dialogWidget: undefined,
      holidayData: [],
      selectedRowKeys: [],
      selectedRows: [],
      columns: [
        {
          title: '名称',
          dataIndex: 'name',
          key: 'name'
        },
        {
          title: '日期',
          dataIndex: 'holidayDateName',
          key: 'holidayDateName'
        },
        {
          title: '标签',
          dataIndex: 'tags',
          key: 'tags',
          scopedSlots: { customRender: 'tags' }
        }
      ],
      keyword: '',
      tags: [],
      selectedTags: [],
      loading: false,
      allHolidayInstanceDate: {},
      wTemplate: {
        $options: {
          methods: {
            saveForm: this.saveForm
          },
          META: {
            method: {
              saveForm: '保存'
            }
          }
        }
      }
    };
  },
  META: {
    method: {
      saveForm: '保存'
    }
  },
  computed: {
    selectedHoliday() {
      this.selectedRowKeys = [];
      return map(this.holiday, item => {
        return {
          uuid: item.holidayUuid,
          name: item.holidayName,
          holidayInstanceDate: item.holidayInstanceDate
        };
      });
    }
  },
  watch: {
    holidayData: {
      deep: true,
      handler(v) {
        let hasInstantDate = filter(v, 'holidayInstanceDate');
        if (hasInstantDate.length == v.length) {
          this.loading = false;
          window.localStorage.setItem('systemData_allHolidayInstanceDate', JSON.stringify(this.allHolidayInstanceDate));
        }
      }
    }
  },
  beforeCreate() {},
  created() {
    if (window.localStorage.getItem('systemData_allHolidayInstanceDate')) {
      this.allHolidayInstanceDate = JSON.parse(window.localStorage.getItem('systemData_allHolidayInstanceDate'));
    }
    this.getFormData();
    this.getDataDictionariesByTypeCode();
  },
  beforeMount() {
    let _this = this;
  },
  mounted() {
    let $event = this._provided && this._provided.$event;
    if ($event) {
      this.$evtWidget = $event && $event.$evtWidget;
    }
    this.$dialogWidget = this._provided && this._provided.dialogContext;
  },
  methods: {
    getFormData() {
      let _this = this;
      _this.loading = true;
      $axios
        .post('/api/ts/holiday/getAllBySystemUnitIdsLikeFields', {
          keyword: this.keyword,
          tags: this.selectedTags.join(';')
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.holidayData = map(data.data, (item, index) => {
              item.tags = item.tag ? item.tag.split(';') : [];
              if (this.allHolidayInstanceDate[item.uuid]) {
                item.holidayInstanceDate = this.allHolidayInstanceDate[item.uuid];
              } else {
                this.getHolidayInstanceDate(item, index);
              }
              return item;
            });
          }
        });
      this.selectedRowKeys = map(this.selectedHoliday, 'uuid');
    },
    initSelectedRowKeys() {
      this.selectedRowKeys = map(this.selectedHoliday, 'uuid');
    },
    getDataDictionariesByTypeCode() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'dataDictionaryService',
          methodName: 'getDataDictionariesByTypeCode',
          args: JSON.stringify(['computer_rank', '']),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.tags = map(data.data, 'name');
          }
        });
    },
    tableSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys; //选中的行
      this.resetTableSelectKey();
    },
    resetTableSelectKey() {
      each(this.holidayData, tItem => {
        let hasIndex = findIndex(this.selectedHoliday, { uuid: tItem.uuid });
        let isChecked = this.selectedRowKeys.indexOf(tItem.uuid) > -1;
        // 已选择项在当前表格里
        if (hasIndex > -1) {
          if (!isChecked) {
            this.selectedHoliday.splice(hasIndex, 1);
          }
        } else if (isChecked) {
          this.selectedHoliday.push(tItem);
        }
      });
    },
    delItem(index) {
      const item = this.selectedHoliday.splice(index, 1);
      let hasIndex = this.selectedRowKeys.indexOf(item[0].uuid);
      if (hasIndex > -1) {
        this.selectedRowKeys.splice(hasIndex, 1);
      }
    },
    tagChange(tag, checked) {
      if (checked && this.selectedTags.indexOf(tag) == -1) {
        this.selectedTags.push(tag);
      } else if (!checked && this.selectedTags.indexOf(tag) > -1) {
        this.selectedTags.splice(this.selectedTags.indexOf(tag), 1);
      }
      this.getFormData();
    },
    saveForm(callback) {
      if (this.selectedHoliday.length == 0) {
        this.$message.error('请选择节假日!');
        return false;
      }
      let list = sortBy(
        map(this.selectedHoliday, item => {
          let holidayItem = {};
          let hasIndex = findIndex(this.holiday, { holidayUuid: item.uuid });
          if (hasIndex == -1) {
            holidayItem = {
              holidayUuid: item.uuid,
              holidayName: item.name,
              holidayInstanceDate: item.holidayInstanceDate,
              rangeDate: [],
              makeupDateList: [],
              year: this.year
            };
            if (this.autoUpdate) {
              let baseHoliday = find(this.baseHoliday, { holidayUuid: item.uuid });
              if (baseHoliday) {
                holidayItem = baseHoliday;
              }
            }
          } else {
            holidayItem = this.holiday[hasIndex];
          }
          return holidayItem;
        }),
        'holidayInstanceDate'
      );
      if (typeof callback == 'function') {
        callback(list);
      } else {
        return list;
      }
    },
    getHolidayInstanceDate(item, index) {
      $axios.get('/api/ts/holiday/getHolidayInstanceDate', { params: { uuid: item.uuid, year: this.year } }).then(({ data }) => {
        let date = '';
        if (data.code == 0) {
          date = data.data.indexOf(this.year) > -1 ? data.data.substr(5) : data.data;
        }
        this.allHolidayInstanceDate[item.uuid] = date;
        this.$set(this.holidayData[index], 'holidayInstanceDate', date);
      });
    }
  }
};
</script>
