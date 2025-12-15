import VuePageDevelopment from '@develop/VuePageDevelopment';

class TestDevelopment extends VuePageDevelopment {

  get META() {
    return {
      name: '测试脚本',
      hook: {
        getBadgeCount: '统计徽标数测试',
      }
    };
  }


  getBadgeCount() {
    return new Promise((resolve, reject) => {
      $axios.post(`/proxy/api/app/prod/query`, {
        page: {
          currentPage: 1,
          pageSize: 1

        }
      }).then(({ data }) => {
        resolve(data.data.page.totalCount);
      }).catch(error => { })

    })
  }



}

export default TestDevelopment;
