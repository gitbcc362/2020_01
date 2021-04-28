'use strict';

const { MongoClient } = require('mongodb');

let client, db;

const changeStreamMap = {};

async function connect() {
  if (client && client.isConnected()) {
    return true;
  }

  client = await MongoClient.connect('mongodb://localhost',{
    useUnifiedTopology: true
  });

  db = client.db('tp-sd');

  return client.isConnected();
}

function disconnect() {
  return client && client.disconnect();
}

function getDB() {
  return db;
}

function subscribeChanges(pipeline = [], options = {}) {
  const collection = getDB().collection('ReduceStep');

  const stream = collection.watch(pipeline, options);

  changeStreamMap['ReduceStep'] = stream;

  return changeStreamMap['ReduceStep'];
}

function lockDB() {
  return new Promise((resolve) => {
    resolve(db.fsyncLock());
  });
}

function unlockDB() {
  return new Promise((resolve)=>{
    resolve(db.fsyncUnlock());
  });
}

module.exports = {
  connect,
  disconnect,
  getDB,
  subscribeChanges,
  lockDB,
  unlockDB,
};
