from __future__ import print_function

from pyspark import SparkContext, SparkConf
from pyspark.mllib.linalg import DenseVector, VectorUDT
from pyspark.sql import SQLContext

from pyspark.ml.classification import MultilayerPerceptronClassifier
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
from pyspark.sql.types import StructType, StructField, StringType, DoubleType, ArrayType


def data_frame_from_file(sqlContext, file_name, fraction):
    lines = sc.textFile(file_name).sample(False, fraction)
    parts = lines.map(lambda l: map(lambda s: int(s), l.split(",")))
    samples = parts.map(lambda p: (
        float(p[0]),
        DenseVector(map(lambda el: el / 255.0, p[1:]))
    ))

    fields = [
        StructField("label", DoubleType(), True),
        StructField("features", VectorUDT(), True)
    ]
    schema = StructType(fields)

    data = sqlContext.createDataFrame(samples, schema)
    return data


if __name__ == "__main__":
    conf = SparkConf(True)
    conf.set("spark.executor.memory", "8g")

    sc = SparkContext(
        master="spark://169.254.147.148:7077",
        appName="multilayer_perceptron_classification_example",
        conf=conf
    )

    sqlContext = SQLContext(sc)

    # train = data_frame_from_file(sqlContext, "mnist_train.csv", 0.01)
    # test = data_frame_from_file(sqlContext, "mnist_test.csv", 0.01)

    train = data_frame_from_file(sqlContext, "mnist_train.csv", 1)
    test = data_frame_from_file(sqlContext, "mnist_test.csv", 1)

    # layers = [28*28, 14*14, 5*5, 10]
    layers = [28*28, 1024, 10]

    # create the trainer and set its parameters
    trainer = MultilayerPerceptronClassifier(maxIter=100, layers=layers, blockSize=128, seed=1234)
    # train the model
    model = trainer.fit(train)
    # compute precision on the test set
    result = model.transform(test)
    predictionAndLabels = result.select("prediction", "label")
    evaluator = MulticlassClassificationEvaluator(metricName="precision")
    print("Precision: " + str(evaluator.evaluate(predictionAndLabels)))

    sc.stop()