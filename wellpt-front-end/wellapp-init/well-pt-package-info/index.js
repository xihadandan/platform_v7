'use strict';

console.error('should not require well-init-config');
process.exit(1);

module.exports = {
  packages: [
    'wellapp-framework',
    'wellapp-web',
    'wellapp-mobile',
    'wellapp-admin',
    'wellapp-dyform',
    'wellapp-for-prod',
    'wellapp-page-assembly',
    'wellapp-workflow'
  ]
};
