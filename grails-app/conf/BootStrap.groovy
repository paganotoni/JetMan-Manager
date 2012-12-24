class BootStrap {
  def grailsApplication
  def instanceManagerService

  def init = { servletContext ->
    println "| Default Java Opts: " + grailsApplication.config.runner.defaultJavaOptions
    println "| Applications Folder: " + grailsApplication.config.runner.deployerFolder

    instanceManagerService.initializeSavedInstances()
  }

  def destroy = {
    println "| Manager going down "
    instanceManagerService.instances.each {
      it.process.destroy()
    }
  }
}
