'use strict';

const mock = require('egg-mock');

describe('test/wellapp-for-prod.test.js', () => {
  let app;
  before(() => {
    app = mock.app({
      baseDir: 'apps/wellapp-for-prod-test',
    });
    return app.ready();
  });

  after(() => app.close());
  afterEach(mock.restore);

  it('should GET /', () => {
    return app.httpRequest()
      .get('/')
      .expect('hi, wellappForProd')
      .expect(200);
  });
});
