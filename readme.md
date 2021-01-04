<groupId>com.equifax.ews.instant.productservices</groupId>
    <artifactId>vs-billingpublisher-svc</artifactId>
	
	
	
	--project=ews-vs-prdsvs-dev-npe-476d
--region=us-east1
--runner=org.apache.beam.runners.dataflow.DataflowRunner
--serviceAccount=prdsvcs-dataflow@ews-vs-prdsvs-dev-npe-476d.iam.gserviceaccount.com
--subnetwork=https://www.googleapis.com/compute/v1/projects/ews-vs-prdsvs-dev-npe-476d/regions/us-east1/subnetworks/dataflow-us-east1-125c
--network=ews-vs-prdsvs-dev-npe-476d-internal
--stagingLocation=gs://demo-poc/BillingPublisher/staging
--gcpTempLocation=gs://demo-poc/BillingPublisher/tmp
--numWorkers=1
--maxNumWorkers=3
--workerMachineType=n1-standard-2
--filesToStage=target/vs-billingpublisher-svc-dataflow-1.0.0-SNAPSHOT.jar
--inputSubscription=projects/ews-vs-prdsvs-dev-npe-476d/subscriptions/vsi-billing-publisher-demo
--outputTopic=projects/ews-vs-prdsvs-dev-npe-476d/topics/vsi-billing-publisher-output-demo
--usePublicIps=false