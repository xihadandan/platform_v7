'use strict';

module.exports = {
  name: {
    desc: 'project name',
    filter: namePrefixFormate
  },
  description: {
    desc: 'project description'
  },
  author: {
    desc: 'project author',
    default: 'wellsoft'
  },
  keys: {
    desc: 'cookie security keys',
    default: Date.now() + '_' + random(100, 10000)
  },
  ptVersion: {
    desc: 'pt version',
    default: '70.0.0'
  }
};

function namePrefixFormate(str) {
  if (str && str.indexOf('wellapp-') === 0) {
    return str;
  }
  return 'wellapp-' + str;
}

function random(start, end) {
  return Math.floor(Math.random() * (end - start) + start);
}
