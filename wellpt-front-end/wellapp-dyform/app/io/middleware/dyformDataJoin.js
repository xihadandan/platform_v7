module.exports = app => {
  function sendmsg(nsp, dataUuid, app) {
    nsp.adapter.clients([dataUuid], (err, clients) => {
      if (clients) {
        const user = [],
          userIds = new Set();
        if (app.config.redis && app.config.redis.client) {
          app.redis.smembers(`socketio:${dataUuid}`).then(members => {
            for (let i = 0, len = members.length; i < len; i++) {
              let _m = JSON.parse(members[i]);
              if (clients.indexOf(_m._socketId) != -1) {
                // 存在连接的客户端
                if (!userIds.has(_m.userId)) {
                  userIds.add(_m.userId);
                  user.push({
                    userId: _m.userId,
                    userName: _m.userName,
                    photoUuid: _m.photoUuid,
                    mainJobPath: _m.mainJobPath,
                    issued: _m.issued
                  });
                }
              } else {
                app.redis.srem(`socketio:${dataUuid}`, members[i]);
              }
            }

            // 更新在线用户列表
            nsp.to(dataUuid).emit('updateUserEditDyform', {
              user,
              userIds: Array.from(userIds)
            });
          });
        } else {
          //非集群启动方式的情况下，才可获取到正确的sockets
          let sockets = nsp.sockets;
          for (let key in sockets) {
            if (clients.indexOf(sockets[key].id) != -1) {
              if (!userIds.has(sockets[key].handshake.query.userId)) {
                userIds.add(sockets[key].handshake.query.userId);
                user.push({
                  userId: sockets[key].handshake.query.userId,
                  userName: sockets[key].handshake.query.userName,
                  photoUuid: sockets[key].handshake.query.photoUuid,
                  mainJobPath: sockets[key].handshake.query.mainJobPath,
                  issued: sockets[key].handshake.query.issued
                });
              }
            }
          }

          // 更新在线用户列表
          nsp.to(dataUuid).emit('updateUserEditDyform', {
            user,
            userIds: Array.from(userIds)
          });
        }
      }
    });
  }
  return async (ctx, next) => {
    const { app, socket, logger } = ctx;
    const nsp = app.io.of('/wellapp-dyform');
    const dataUuid = ctx.socket.handshake.query.dataUuid;
    const _id = socket.id;
    socket.join(dataUuid);
    socket.handshake.query._socketId = _id;
    socket.handshake.query.issued = socket.handshake.issued;
    if (app.config.redis && app.config.redis.client) {
      app.redis.sadd(`socketio:${dataUuid}`, JSON.stringify(socket.handshake.query));
    } else if (app.config.env !== 'local') {
      logger.error('socket.io 未配置 redis');
    }
    sendmsg(nsp, dataUuid, app);
    socket.on('disconnect', reason => {
      // 连接将要丢失
      sendmsg(nsp, dataUuid, app);
    });
    await next();
  };
};
