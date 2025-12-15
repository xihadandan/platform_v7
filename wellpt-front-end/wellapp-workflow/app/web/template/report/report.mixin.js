export default {
  methods: {
    getCurrentUserDeptId() {
      let userSystemOrgDetails = this._$USER.userSystemOrgDetails || {};
      let details = userSystemOrgDetails.details || [];
      for (let i = 0; i < details.length; i++) {
        let detail = details[i];
        if (detail.mainDept && detail.mainDept.deptId) {
          return detail.mainDept.deptId;
        }
        if (detail.mainJob && detail.mainJob.deptId) {
          return detail.mainDept.deptId;
        }

        let otherDepts = detail.otherDepts || [];
        for (let j = 0; j < otherDepts.length; j++) {
          let otherDept = otherDepts[j];
          if (otherDept.deptId) {
            return otherDept.deptId;
          }
        }
        let otherJobs = detail.otherJobs || [];
        for (let j = 0; j < otherJobs.length; j++) {
          let otherJob = otherJobs[j];
          if (otherJob.deptId) {
            return otherJob.deptId;
          }
        }
      }
      return null;
    }
  }
}