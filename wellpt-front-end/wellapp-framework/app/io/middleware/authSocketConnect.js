module.exports = app => {
  return async (ctx, next) => {
    if (ctx.session.passport && ctx.session.passport.user) {
      ctx.socket.handshake.query._sessionid = ctx.session._externalKey;
      ctx.socket.handshake.query.userName = ctx.session.passport.user.userName;
      ctx.socket.handshake.query.photoUuid = ctx.session.passport.user.photoUuid;
      ctx.socket.handshake.query.mainJobPath = ctx.session.passport.user.mainJobPath;
      ctx.socket.emit('socketid', { socketid: ctx.socket.conn.id, userid: ctx.session.passport.user.userId });
      await next();
    } else {
      ctx.socket.disconnect(true);
    }
    return;
  };
};
