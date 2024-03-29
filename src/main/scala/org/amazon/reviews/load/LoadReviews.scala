package org.amazon.reviews.load

import org.amazon.reviews.RunSpark
import org.amazon.reviews.config.CmdConfig
import org.amazon.reviews.types.Review
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.types.TimestampType
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Spark streaming job to process reviews data
  * Assumes new data comes in a hdfs directory as separate files
  * In general, it good to have the data parittioned in a way
  * that downstream processing becomes easy
  */
object LoadReviews extends RunSpark {

  def extract(config: CmdConfig)(implicit spark: SparkSession): Unit = {
    import spark.implicits._

    logger.info("Reading reviews stream data")
    val reviews: Dataset[Review] = spark
      .readStream
      .json(s"${config.sourceDir}")
      .withColumn("reviewTimestamp", $"unixReviewTime".cast(TimestampType))
      .as[Review]
    logger.info(s"Printing reviews schema: ${reviews.printSchema}")

    logger.info("Writing reviews data in micro batch mode")
    //TODO Optimize window size
    val query: StreamingQuery = reviews
      .writeStream
      .format("parquet")
      .option("path", s"${config.targetDir}")
      .start()

    // Wait for termination signal
    query.awaitTermination()
  }
}
