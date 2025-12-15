'use strict';

exports.wellappRedisSession = {
  name: '',
  forbidMultiDeviceLogin: false
};

exports.redis = {
  client: {
    port: process.env.WELLAPP_REDIS_PORT ? parseInt(process.env.REDIS_PORT) : 6379, // Redis port
    host: process.env.WELLAPP_REDIS_HOST || '192.168.0.241', // Redis host
    password: process.env.WELLAPP_REDIS_PASSWORD || 'wellsoft',
    db: process.env.WELLAPP_REDIS_DB ? parseInt(process.env.WELLAPP_REDIS_DB) : 0,
    weakDependent: true
  }
};
