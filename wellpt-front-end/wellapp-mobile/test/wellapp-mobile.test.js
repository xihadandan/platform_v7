'use strict';

const mock = require('egg-mock');

describe('test/wellapp-mobile.test.js', () => {
  let app;
  before(() => {
    app = mock.app({
      baseDir: 'apps/wellapp-mobile-test',
    });
    return app.ready();
  });

  after(() => app.close());
  afterEach(mock.restore);

  it('should GET /', () => {
    return app.httpRequest()
      .get('/')
      .expect('hi, wellappMobile')
      .expect(200);
  });
});
