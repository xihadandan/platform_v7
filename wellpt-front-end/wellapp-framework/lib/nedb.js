'use strict';

const Nedb = require('nedb');


function DataStore(database) {
  this.db = new Nedb(database ? {
    filename: database,
    autoload: true,
  } : null);
}

DataStore.prototype.insert = function (doc, callback) {
  this.db.insert(doc, callback);
};


DataStore.prototype.insertSync = function (values) {
  return new Promise((resolve, reject) => {
    this.db.insert(values, (err, newDoc) => {
      if (err) {
        return reject(err);
      }
      resolve(newDoc);
    });
  });
};


DataStore.prototype.update = function (query, update, options, callback) {
  this.db.update(query, update, options, callback);
};

DataStore.prototype.updateSync = function (query, update, options, callback) {
  return new Promise((resolve, reject) => {
    this.db.update(query || {}, update || {}, options || {}, (err, numAffected) => {
      if (err) {
        return reject(err);
      }
      resolve(numAffected);
    });
  });
};

DataStore.prototype.remove = function (query, options, callback) {
  this.db.remove(query, options || {}, callback);
};

DataStore.prototype.removeSync = function (query, options, callback) {
  return new Promise((resolve, reject) => {
    this.db.remove(query || {}, options || {}, (err, numRemoved) => {
      if (err) {
        return reject(err);
      }
      resolve(numRemoved);
    });
  });
};


DataStore.prototype.find = function (query, callback) {
  this.db.find(query, callback);
};

DataStore.prototype.findSync = function (query, select) {
  return new Promise((resolve, reject) => {
    const stmt = this.db.find(query || {});
    if (this.orderby !== undefined) {
      stmt.sort(this.orderby);
    }
    if (this.offset !== undefined) {
      stmt.skip(this.offset).limit(this.limit);
    }
    if (select != undefined) {
      stmt.projection(select || {});
    }
    stmt.exec((err, docs) => {
      if (err) {
        return reject(err);
      }
      resolve(docs);
    });
  });
};

DataStore.prototype.findOne = function (query, select) {
  return new Promise((resolve, reject) => {
    const stmt = this.db.findOne(query || {});
    if (this.orderby !== undefined) {
      stmt.sort(this.orderby);
    }
    if (this.offset !== undefined) {
      stmt.skip(this.offset).limit(this.limit);
    }
    if (select != undefined) {
      stmt.projection(select || {});
    }
    stmt.exec((err, docs) => {
      if (err) {
        return reject(err);
      }
      resolve(docs);
    });
  });
};

DataStore.prototype.ensureIndex = function (options, callback) {
  this.db.ensureIndex(options, callback);
};

DataStore.prototype.findOneSync = function (query, select) {
  return new Promise((resolve, reject) => {
    const stmt = this.db.findOne(query || {});
    if (this.sort !== undefined) {
      stmt.sort(this.sort);
    }
    if (select != undefined) {
      stmt.projection(select || {});
    }
    stmt.exec((err, doc) => {
      if (err) {
        return reject(err);
      }
      resolve(doc);
    });
  });
};


module.exports = DataStore;
