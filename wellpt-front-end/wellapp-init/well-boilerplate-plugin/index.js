'use strict';

module.exports = {
  name: {
    desc: 'package name(will publish to npm as {name})',
    filter: namePrefixFormate
  },
  pluginName: {
    desc: 'plugin name will be camelcase',
    default(vars) {
      return camelcase(vars.name);
    },
    filter: camelcase,
  },
  description: {
    desc: 'plugin description',
  },
  author: {
    desc: 'plugin author',
  },
};
function namePrefixFormate(str){
  console.log(str);
  if(str && str.indexOf('wellapp-')===0){
    return str;
  }
  return 'wellapp-'+str;
}
function camelcase(str) {
  return str && str.replace(/[_.\- ]+(\w|$)/g, (m, p1) => p1.toUpperCase());
}
