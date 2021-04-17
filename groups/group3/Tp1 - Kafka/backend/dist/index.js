"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
require("reflect-metadata");
const graphql_yoga_1 = require("graphql-yoga");
const type_graphql_1 = require("type-graphql");
const JobsResolver_1 = require("./resolvers/JobsResolver");
async function bootstrap() {
    const schema = await type_graphql_1.buildSchema({
        resolvers: [JobsResolver_1.default],
        emitSchemaFile: true,
    });
    const server = new graphql_yoga_1.GraphQLServer({
        schema,
    });
    server.start(() => console.log("Server is running on http://localhost:4000"));
}
bootstrap();
//# sourceMappingURL=index.js.map