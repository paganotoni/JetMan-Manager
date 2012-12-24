package manager

class InstanceManagerService {

  def grailsApplication
  def aws
  HashMap instanceProcesses = [:]

  public void initializeInstanceProcess( Instance instance ) {
    def options = instance.javaOptions
    def folder  = grailsApplication.config.runner.deployerFolder
    def port    = instance.port
    def warName = instance.localFileName
    def out     =  "--out $folder/logs/${warName}.out"

    def command = """java ${options} -jar $folder/lib/jetty-runner.jar --log $folder/logs/${warName}.log --port ${port} $folder/apps/${warName}"""
    println "|[${instance.logIdentifier}] Starting instance: $command "

    ProcessBuilder builder = new ProcessBuilder( command.split(' ') )
    builder.redirectErrorStream(true)
    Process process       = builder.start()

    instance.startedDate  = new Date()
    instance.process = process

    if(!instanceProcesses["$instance.id"]) instanceProcesses.put( instance.id, process )
    printLog(process, instance)
  }

  public void terminateInstanceProcess( Instance instance ){
    if( instance && instance.process ) instance.process.destroy()
  }

  public void createInstance( String instanceName, String javaOptions, String port, String bucketFileName ){
    javaOptions = javaOptions ?: grailsApplication.config.runner.defaultJavaOptions
    def instance =  new Instance( name:  instanceName, javaOptions: javaOptions, port: port, bucketWarName: bucketFileName )
    instance.save();

    Thread.start{
      Instance.withTransaction {
        instance                = downloadWarFile( instance );
        instance.instanceStatus = Instance.STATUS_STARTING
        instance.save()
      }

      initializeInstanceProcess(instance)
    }

  }

  public void updateInstance( Long instanceId ,String instanceName, String javaOptions, String port, String bucketFileName ){
    def instance = Instance.findByIdOrName( instanceId, instanceName )

    if( instance ){
      instance.refresh()

      findProcess(instance.id)?.destroy()

      instance.javaOptions    = javaOptions ?: instance.javaOptions
      instance.port           = port ?: instance.port
      instance.bucketWarName  = bucketFileName ?: instance.bucketWarName
      instance = instance.save();

      Thread.start{
        Instance.withTransaction {
          instance.refresh()

          instance                = downloadWarFile( instance );
          instance.instanceStatus = Instance.STATUS_STARTING
          instance.save()
        }

        initializeInstanceProcess(instance)
      }

    }
  }


  private Instance downloadWarFile( Instance instance  ){
    println "| [${instance.logIdentifier}] Downloading s3 File"

    def bucket = grailsApplication.config.runner.s3.bucket
    def path   = grailsApplication.config.runner.s3.path
    def name   = instance.bucketWarName

    def fileToDownload  = aws.s3().on(bucket).get(name, path)
    def folder          = grailsApplication.config.runner.deployerFolder
    def localFileName   = "${UUID.randomUUID()}.war"
    def localFile       = new File("$folder/apps/$localFileName")

    instance.localFileName  = localFileName
    instance.instanceStatus = Instance.STATUS_DOWNLOADING
    instance.save(flush: true);

    localFile << fileToDownload.dataInputStream
    println "| [${instance.logIdentifier}] s3 File Downloaded"

    instance.refresh()
    instance.instanceStatus = Instance.STATUS_STARTING
    instance.save();

    return instance
  }

  public void deleteInstance(Long instanceId, String name){
    def instance = Instance.findByIdOrName(instanceId, name)

    if( instance ){
      findProcess(instance.id)?.destroy()
      instanceProcesses.remove("$instance.id")
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

  public Process findProcess(def instanceId){
    return instanceProcesses.get(instanceId);
  }



}
