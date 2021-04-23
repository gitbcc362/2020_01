'use strict';

exports.MessageBuffer = class MessageBuffer {
  constructor(delimiter) {
    this.delimiter = delimiter;
    this.buffer = '';
  }

  isFinished() {
    if (this.buffer.length === 0 || this.buffer.indexOf(this.delimiter) === -1) {
      return true;
    }

    return false;
  }

  push(data) {
    this.buffer += data;
  }

  getMessage() {
    const delimiterIndex = this.buffer.indexOf(this.delimiter);

    if (delimiterIndex !== -1) {
      const message = this.buffer.slice(0, delimiterIndex);

      this.buffer = this.buffer.replace(message + this.delimiter, '');

      return JSON.parse(message);
    }

    return null;
  }

  handleData() {
    const message = this.getMessage();

    return message;
  }
};
