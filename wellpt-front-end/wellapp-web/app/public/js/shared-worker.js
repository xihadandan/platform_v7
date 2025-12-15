const ports = []; // 存储所有连接的端口
setInterval(() => {
  let formData = new FormData();
  formData.set('reason', 'client keepalive');
  fetch('/heartbeat', {
    method: 'POST',
    credentials: 'same-origin',
    body: formData
  });
}, 1000 * 60 * 3)
// 监听新连接
self.onconnect = (e) => {
  console.log('shared-worker on connect', e)
  const port = e.ports[0];
  ports.push(port);
};
