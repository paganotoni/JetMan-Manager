package manager

import grails.plugin.aws.s3.AWSS3Tools
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.s3.AmazonS3Client

class HomeController {
  def instanceManagerService
  def s3Service

  def index() {
    def availableWars = s3Service.findAvailableWars();
    def instances = Instance.list()

    [instances: instances, availableWars: availableWars ]
  }
}
