from pyspark.sql import SparkSession
from pyspark.sql import SQLContext
from pyspark.sql import Row
from pyspark.streaming import StreamingContext

import sys, os
import requests
from requests.models import Response


def read_stopwords(file_path):
    file = open(file_path, mode='r')
    # read all lines at once
    all_of_it = file.read().strip()
    # close the file
    file.close()
    return set(map(lambda line: line.strip(), all_of_it.split('\n')))

def aggregate_tags_count(new_values, total_sum):
    return sum(new_values) + (total_sum or 0)


def get_sql_context_instance(spark_context):
    if 'sqlContextSingletonInstance' not in globals():
        globals()['sqlContextSingletonInstance'] = SQLContext(spark_context)
    return globals()['sqlContextSingletonInstance']


def process_hashtags_rdd(time, rdd):
    print("----------- %s -----------" % str(time))
    try:
        # Get spark sql singleton context from the current context
        sql_context = get_sql_context_instance(rdd.context)
        # convert the RDD to Row RDD
        row_rdd = rdd.map(lambda w: Row(hashtag=w[0], hashtag_count=w[1]))

        # create a DF from the Row RDD
        hashtags_df = sql_context.createDataFrame(row_rdd)

        # Register the dataframe as table
        hashtags_df.registerTempTable("hashtags")
        # get the top 10 hashtags from the table using SQL and print them
        hashtag_counts_df = sql_context.sql(
            "select hashtag, hashtag_count from hashtags order by hashtag_count desc limit 10")

        hashtag_counts_df.show()

        # extract the hashtags from dataframe and convert them into array
        top_tags = [str(t.hashtag) for t in hashtag_counts_df.select("hashtag").collect()]
        # extract the counts from dataframe and convert them into array
        tags_count = [p.hashtag_count for p in hashtag_counts_df.select("hashtag_count").collect()]
        # initialize and send the data through REST API
        request_data = {'label': str(top_tags), 'data': str(tags_count)}

        requests.post(f'http://{os.environ["DASHBOARD_CLIENT"]}:{os.environ["DASHBOARD_PORT"]}/updateDataHashtag', data=request_data)

    except Exception as e:
        print("Error: %s" % e)


def process_words_rdd(time, rdd):
    print("----------- %s -----------" % str(time))
    try:
        # Get spark sql singleton context from the current context
        sql_context = get_sql_context_instance(rdd.context)
        # convert the RDD to Row RDD
        row_rdd = rdd.map(lambda w: Row(word=w[0], word_count=w[1]))

        # create a DF from the Row RDD
        words_df = sql_context.createDataFrame(row_rdd)

        # Register the dataframe as table
        words_df.registerTempTable("words")
        # get the top 10 hashtags from the table using SQL and print them
        trendigs_df = sql_context.sql(
            "select word, word_count from words order by word_count desc limit 10")

        trendigs_df.show()

        # extract the hashtags from dataframe and convert them into array
        top_tags = [str(t.word) for t in trendigs_df.select("word").collect()]
        # extract the counts from dataframe and convert them into array
        tags_count = [p.word_count for p in trendigs_df.select("word_count").collect()]

        # initialize and send the data through REST API
        request_data = {'label': str(top_tags), 'data': str(tags_count)}
        requests.post(f'http://{os.environ["DASHBOARD_CLIENT"]}:{os.environ["DASHBOARD_PORT"]}/updateDataWord', data=request_data)

    except Exception as e:
        print("Error: %s" % e)


if __name__ == "__main__": 

    stopwords = read_stopwords("./stopwords.txt")

    ss = SparkSession.builder \
        .appName("Spark Structured Streaming from Twitter") \
        .getOrCreate()

    sc = ss.sparkContext

    ssc = StreamingContext(sc, 2)
    
	# setting a checkpoint to allow RDD recovery
    ssc.checkpoint(f"hdfs://{os.environ['HDFS_HOST_NAME']}:{os.environ['HDFS_HOST_PORT']}/checkpoint")

    # read data from port 9009
    dataStream = ssc.socketTextStream(os.environ["TWITTER_CLIENT"], os.environ["TWITTER_PORT"])

    # split each tweet into words
    words = dataStream.flatMap(lambda line: line.lower().split(" "))

    # filter the words to get only hashtags, then map each hashtag to be a pair of (hashtag,1)
    hashtags = words.filter(lambda w: '#' in w).map(lambda x: (x, 1))
    # adding the count of each hashtag to its last count
    tags_totals = hashtags.updateStateByKey(aggregate_tags_count)
    # do processing for each RDD generated in each interval
    tags_totals.foreachRDD(process_hashtags_rdd)


    # filter the words to get only hashtags, then map each hashtag to be a pair of (hashtag,1)
    # trending_words = words.filter(lambda w: w.strip() and w.lower() not in stopwords and '#' not in w).map(lambda x: (x, 1))
    trending_words = words.filter(lambda w: "@" in w).map(lambda x: (x, 1))
    # adding the count of each hashtag to its last count
    trending_words_totals = trending_words.updateStateByKey(aggregate_tags_count)
    # do processing for each RDD generated in each interval
    trending_words_totals.foreachRDD(process_words_rdd)


    # start the streaming computation
    ssc.start()
    # wait for the streaming to finish
    ssc.awaitTermination()

    # ss.stop()
