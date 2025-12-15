class FullTextSearch {
  constructor({ pageSize = 10 } = {}) {
    this.categoryCode = '';
    this.countCategory = true;
    this.dateRange = null;
    this.dateRangeField = 'createTime';

    this.filterMap = {};
    this.fragmentSize = 150;
    this.keyword = '';
    // this.order = {
    //   direction: "DESC",
    //   property: "createTime"
    // }
    this.pagingInfo = {
      autoCount: true,
      currentPage: 1,
      first: 0,
      firstTime: '',
      pageSize,
      totalCount: 0,
      totalPages: 0
    };
    this.resultFieldMapping = {};
    this.startTime = '';
    this.endTime = '';
  }
}

export default FullTextSearch;

export const dateRangeOptions = [
  { label: '时间不限', value: 'anytime' },
  { label: '一天内', value: 'ONE_DAY' },
  { label: '一周内', value: 'ONE_WEEK' },
  { label: '一月内', value: 'ONE_MONTH' },
  { label: '一年内', value: 'ONE_YEAR' },
  { label: '自定义', value: 'CUSTOM' }
];

export const filterTimeTypeOptions = [
  { label: '创建时间', value: 'createTime' },
  { label: '修改时间', value: 'modifyTime' }
];

export const orderOptions = [
  { label: '相关度高优先', value: '' },
  { label: '最早创建优先', value: 'createTime.ASC' },
  { label: '最近创建优先', value: 'createTime.DESC' },
  { label: '最近更新优先', value: 'modifyTime.DESC' }
];
