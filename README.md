# amazon-reviews
Repository contains POC concepts for downloading and processing Amazon Reviews Data(http://jmcauley.ucsd.edu/data/amazon/links.html)

### Prerequisities
* Install sbt
* Install Hadoop
* Install Spark

### Testing the project
To run tests, please run:
```
sbt test
```

### Building the project
To build the project, please run:
```
sbt assembly
```
It will create a fat jar inside `target` folder

### Running the project locally
To proceed with next steps, it is assumed there is running hadoop cluster at `hdfs://localhost:9000/`
### Submit Spark Jobs
Once you have the assembly jar, you can submit spark jobs using this syntax.
Other examples can be found [here](kubernetes/charts/amazon-reviews/config)

For example, to run load reviews spark job:
```
   bin/spark-submit \
     --class org.amazon.reviews.load.LoadReviews \
     --master local[*] \
     --deploy-mode client \
     --conf spark.sql.streaming.schemaInference=true \
     --conf spark.sql.streaming.checkpointLocation=hdfs://localhost:9000/data/checkpoint \
     target/scala-2.12/amazon-reviews-fat.jar \
     --sourceDir hdfs://localhost:9000/<source_directory> \
     --targetDir hdfs://localhost:9000/<target_directory>
```

### Building Docker Image
To build docker image, please run:
```
docker run -t amazon-reviews:latest .
```

### Deploy in Kubernetes
To deploy in Kubernetes, you need to install [helm](https://helm.sh/docs/intro/install/)

```
helm install amazon-reviews kubernetes/charts/amazon-reviews
```
Since the chart uses Argo workflows, these CRDs should be present in kubernetes environment.
Argo workflows can be installed from [here](https://argoproj.github.io/argo/)

You should also have running Spark and Hadoop clusters in kubernetes  environment to submit spark jobs and manage DFS

### Analyzing data
To do quick analysis of data, we can start a new zeppelin server
Zeppelin has a notebook based interface and supports a range of interpreters
More info about zeppelin can be found [here](https://zeppelin.apache.org/)

To start zeppelin server, from zeppelin home folder run: `bin/zeppelin-daemon.sh start`

Access zeppelin UI here: `http://localhost:8080/`

Link to sample [zeppelin notebook](notebooks/AmazonReviewsDemo.zpln)

### References
* Spark structured streaming concepts programming guide:
https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html
* Argo workflows was used for pipeline orchestration:
https://argoproj.github.io/argo/