

let settingPromise = null;
export function getSetting() {
  if (settingPromise) {
    return settingPromise;
  }
  settingPromise = $axios.get('/proxy/api/workflow/setting/getByKey?key=REPORT').then(({ data: result }) => {
    if (result.data && result.data.attrVal) {
      return JSON.parse(result.data.attrVal);
    }
    return {}
  }).catch(() => {
    return {}
  });
  return settingPromise;
}