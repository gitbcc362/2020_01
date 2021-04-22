const cassandra = require("cassandra-driver"); 
const { getActions } = require("./options");
const { setInterval } = require('timers');

const contactPoints = ["35.196.27.83"];
const protocolOptions = { port: 7154 };

const client = new cassandra.Client({contactPoints, protocolOptions, keyspace: "brinks", localDataCenter:"datacenter1"});

const options = ["select-stock", "insert-stock", "delete-stock"];

setInterval(function() {
  const type = Math.floor(Math.random() * options.length);
  getActions(options[type], client);
}, 1000);

// const query = "SELECT * FROM brinks.brinks_stock;";
// client.execute(query, []).then(result => console.log(result));
