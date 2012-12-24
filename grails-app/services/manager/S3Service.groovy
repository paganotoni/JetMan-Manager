package manager

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.s3.AmazonS3Client

class S3Service {
  def grailsApplication

  public def findAvailableWars() {
    def bucket = grailsApplication.config.runner.s3.bucket
    def path   = grailsApplication.config.runner.s3.path

    def credentialsFile = new File(grailsApplication.config.grails.plugin.aws.credentials.properties)
    AWSCredentials credentials = new PropertiesCredentials(credentialsFile);
    AmazonS3Client s3 = new AmazonS3Client(credentials)

    def summaries = s3.listObjects("$bucket", "$path").getObjectSummaries()
    def availableWars = summaries.collect{
      it.key.replace("$path/",'')
    }

    availableWars.remove('')
    return availableWars
  }
}
