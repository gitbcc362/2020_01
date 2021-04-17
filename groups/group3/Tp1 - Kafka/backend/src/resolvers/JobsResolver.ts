import 'reflect-metadata';
import { Arg, FieldResolver, Query, Resolver, Root } from "type-graphql";
import { JobInterface, jobs } from "../data";
import Job from "../schemas/Job";

@Resolver(of => Job)
export default class {
    @Query(returns => [Job], { nullable: true })
    jobs(): JobInterface[] | undefined {
        console.log(jobs)
        return jobs;
    }
    @Query(returns => [Job], { nullable: true })
    jobsCountry(@Arg("country") country:string): JobInterface[] | undefined {
        console.log(jobs)
        return jobs.filter(job => job.country == country);
    }
}