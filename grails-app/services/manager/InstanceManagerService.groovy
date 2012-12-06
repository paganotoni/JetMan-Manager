package manager

class InstanceManagerService {

  def grailsApplication
  def aws
  List<Instance> instances = []

  public void initializeInstanceProcess( Instance instance ) {
    def options = instance.javaOptions
    def folder  = grailsApplication.config.runner.deployerFolder
    def port    = instance.port
    def warName = instance.localFileName

    def command = """java ${options} -jar $folder/lib/jetty-runner.jar --port ${port} $folder/apps/${warName}"""
    println "|[${instance.logIdentifier}] Starting instance: $command "

    ProcessBuilder builder = new ProcessBuilder( command.split(' ') )
    builder.redirectErrorStream(true)
    Process process       = builder.start()

    instance.startedDate  = new Date()
    instance.process = process

    instances << instance

    printLog(process, instance)
  }

  public void terminateInstanceProcess( Instance instance ){
    if( instance && instance.process ) instance.process.destroy()
  }

  public void createInstance( String instanceName, String javaOptions, String port, String bucketFileName ){
    javaOptions = javaOptions ?: grailsApplication.config.runner.defaultJavaOptions
    def instance =  new Instance( name:  instanceName, javaOptions: javaOptions, port: port, bucketWarName: bucketFileName )
    instance.save();

    def bucket = grailsApplication.config.runner.s3.bucket
    def path   = grailsApplication.config.runner.s3.path
    def name   = bucketFileName

    println "| [${instance.logIdentifier}] Downloading s3 File"
    def fileToDownload = aws.s3().on(bucket).get(name, path)
    def folder = grailsApplication.config.runner.deployerFolder
    def localFileName = "${UUID.randomUUID()}.war"
    def localFile = new File("$folder/apps/$localFileName")

    instance.instanceStatus = Instance.STATUS_DOWNLOADING
    instance.save();

    localFile << fileToDownload.dataInputStream

    println "| [${instance.logIdentifier}] s3 File Downloaded"

    instance.instanceStatus = Instance.STATUS_STARTING
    instance.localFileName = localFileName
    instance.save()

    initializeInstanceProcess(instance)
  }

  public void deleteInstance(Long instanceId, String name){
    def instance = instances.find{ it.id == instanceId || it.name == name }

    if( instance ){
      instance.process?.destroy()
      instances.remove(instance)
      instance.delete()
    }
  }

  public void initializeSavedInstances(){
    Instance.list().each{ instance -> initializeInstanceProcess(instance) }
  }

  public void printLog(process, instance){
    InputStream stdout = process.getInputStream ()
    BufferedReader reader = new BufferedReader (new InputStreamReader(stdout))

    Thread.start {
      String line = null
      try{
        while ((line = reader.readLine ()) != null) {
          System.out.println ("|[${instance.logIdentifier}]: " + line)
        }
      } catch(exception){
         println "|[${instance.logIdentifier}]: APPLICATION SHUTTED DOWN"
      }
    }
  }

}
