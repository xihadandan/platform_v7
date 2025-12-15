'use strict';

const Service = require('wellapp-framework').Service;
const lodash = require('lodash');

class LoginService extends Service {
  async getOnlineUser() {
    const { ctx, app } = this;
    let keyword = ctx.req.body.keyword;
    let systemUnitId = ctx.user.systemUnitId;
    let { pageIndex, pageSize } = ctx.req.body.page;
    let ids = await app.redis.keys('session:id:*');
    if (ids.length === 0) {
      return {
        data: [],
        total: 0
      };
    }
    let users = await app.redis.mget(ids);
    let user = [],
      userIds = new Set(),
      allUserIds = new Set(),
      index = {};
    let superadmin = ctx.user.superAdmin;
    if (users.length > 0) {
      let i = 0;
      users.forEach(u => {
        if (u) {
          let _user = JSON.parse(u);
          // 1. 超管要看到所有的在线用户 2. 非超管只需要看到单位内的在线用户
          let superAdminOrSameUnit = superadmin || _user.systemUnitId == systemUnitId;
          if (superAdminOrSameUnit) {
            allUserIds.add(_user.userId);
          }
          // 支持姓名、账户名、职位、部门模糊查询
          if (!userIds.has(_user.userId)) {
            if (
              superAdminOrSameUnit &&
              (keyword == null ||
                keyword == '' ||
                _user.userName.indexOf(keyword) != -1 ||
                _user.loginName.indexOf(keyword) != -1 ||
                (_user.mainJobPath &&
                  (_user.mainJobPath.indexOf(keyword) != -1 ||
                    _user.mainJobName.indexOf(keyword) != -1 ||
                    _user.mainDeptName.indexOf(keyword) != -1)))
            ) {
              user.push(_user);
              userIds.add(_user.userId);
              index[_user.userId] = i++;
            }
          } else {
            // 已存在，取同账号登录时间的最大值
            let _u = user[index[_user.userId]];
            _u.loginTime = Math.max(_u.loginTime, _user.loginTime);
          }
        }
      });
    }
    user = lodash.orderBy(user, ['loginTime'], ['desc']);
    //分页
    return {
      data: pageIndex != undefined && pageSize != undefined ? lodash.slice(user, (pageIndex - 1) * pageSize, pageIndex * pageSize) : user,
      total: allUserIds.size || 0
    };
  }

  async listOnlineUser() {
    const { ctx, app } = this;
    let ids = await app.redis.keys('session:key:*');
    if (ids.length === 0) {
      return [];
    }
    let users = await app.redis.mget(ids);
    let user = [],
      userIds = new Set(),
      index = {};
    if (users.length > 0) {
      let i = 0;
      for (let u = 0, len = users.length; u < len; u++) {
        let _user = JSON.parse(users[u]);
        let underSystem = false;
        let hasUser = userIds.has(_user.userId),
          replace = hasUser && _user.loginTime > user[index[_user.userId]].loginTime;
        if (!hasUser || replace) {
          let userSystemOrgDetails = _user.userSystemOrgDetails;
          if (userSystemOrgDetails) {
            // 匹配组织节点信息模糊查询
            let details = userSystemOrgDetails.details;
            if (details.length) {
              for (let j = 0; j < details.length; j++) {
                if (details[j].system == ctx.SYSTEM_ID) {
                  underSystem = true;
                } else {
                  // 剔除非本系统关联的组织信息
                  details.splice(j--, 1);
                }
              }
            }
          }

          if (underSystem) {
            _user.online = (await app.redis.get(`session:online:${_user.loginName}`)) != undefined;
            if (!hasUser) {
              _user.loginTimeFormat = formatTimestamp(_user.loginTime);
              user.push(_user);
              userIds.add(_user.userId);
              index[_user.userId] = i++;
            } else if (replace) {
              _user.loginTimeFormat = formatTimestamp(_user.loginTime);
              user[index[_user.userId]] = _user;
            }
          }
        }
      }
    }
    return user;
  }
}

function formatTimestamp(timestamp) {
  const now = new Date();
  const date = new Date(typeof timestamp == 'string' ? parseInt(timestamp) : timestamp);

  // 计算日期差
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24));

  // 时间格式化，补零
  const formatTime = date => {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  };

  // 日期格式化
  const formatDate = date => {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${month}-${day}`;
  };

  if (diffDays === 0) {
    // 今天，只显示时间
    return formatTime(date);
  } else if (diffDays === 1) {
    // 昨天
    return `昨天 ${formatTime(date)}`;
  } else if (diffDays === 2) {
    // 前天
    return `前天 ${formatTime(date)}`;
  } else {
    // 超过前天，显示完整日期和时间
    return `${formatDate(date)} ${formatTime(date)}`;
  }
}

module.exports = LoginService;
