"use strict";

const {
  publish,
} = require("@joaoderocha/redis-pub-sub-nodejs/headless/redis/services/pub-sub");
const {
  getNextStep,
  messageBuilder,
  roundRobinSubscribe,
  WORDCOUNT,
} = require("@joaoderocha/redis-pub-sub-nodejs/headless/utils");

const countWords = (linha) => {
  return linha.split(" ").reduce((cnt, ele) => {
    if (!cnt[ele]) {
      cnt[ele] = 1;
      return cnt;
    }
    cnt[ele] += 1;
    return cnt;
  }, {});
};

const wordCounterStep = (channel, message) => {
  const { linha, queueIndex } = message;

  const result = countWords(linha);
  const msg = messageBuilder(result, queueIndex);

  console.log(msg.linha);

  publish(getNextStep(channel), msg);
};

roundRobinSubscribe(WORDCOUNT, wordCounterStep);
