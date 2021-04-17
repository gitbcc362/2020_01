import { Field, ObjectType } from "type-graphql";

@ObjectType()
export default class Job {
    @Field()
    title: string;

    @Field()
    url: string;

    @Field()
    country: string;
}