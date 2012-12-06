package manager

class InstancesWatcherJob {

  def instanceManagerService

  static triggers = {
    simple repeatInterval: 30000l // execute job once in 5 seconds
  }

  def execute() {
    def instances = instanceManagerService.instances
    println "| Verifing ${instances.size()} instances at ${new Date().format('dd:MM:yyyy hh:mm:ss')}"
    def downCount = 0
    instances.each { Instance instance ->
      instance.refresh();
      URL url = new URL("http://localhost:${instance.port}/")

      try {
        def result = url.getText()
        instance.instanceStatus = Instance.STATUS_UP
      } catch (e) {
        downCount++
        instance.instanceStatus = Instance.STATUS_DOWN
        println "|[${instance.logIdentifier}] Instance Down"
      } finally {

        instance.save();
      }
    }

    println "| Summary: ${downCount} instances down"

  }
}
