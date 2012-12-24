package manager

class InstancesWatcherJob {

  def instanceManagerService
  def grailsApplication

  static triggers = {
    simple repeatInterval: 40000l // execute job once in 5 seconds
  }

  def execute() {
    def instances = Instance.list()
    println "| Verifing ${instances.size()} instances at ${new Date().format('dd:MM:yyyy hh:mm:ss')}"
    def downCount = 0

    instances.each { Instance instance ->
      Instance.withTransaction {
        URL url = new URL("http://localhost:${instance.port}/")

        try {
          def result = url.getText()

          //Delete Local file if exists
          def folder = grailsApplication.config.runner.deployerFolder
          File localFile = new File("$folder/apps/${instance.localFileName}")
          if (localFile.exists()) localFile.delete()

          instance.refresh();
          instance.instanceStatus = Instance.STATUS_UP

        } catch (e) {
          downCount++

          instance.refresh();
          instance.instanceStatus = Instance.STATUS_DOWN

          println "|[${instance.logIdentifier}] Instance Down"
        } finally {
          instance.save();
        }
      }
    }


    println "| Summary: ${downCount} instances down"

  }
}
