package manager

class Instance {
  final static String STATUS_NEW          = "NEW"
  final static String STATUS_DOWNLOADING  = "DOWNLOADING"
  final static String STATUS_UP           = "UP"
  final static String STATUS_STARTING     = "STARTING"
  final static String STATUS_DOWN         = "DOWN"
  final static String STATUS_STOPPED      = "STOPPED"

  String javaOptions
  String port
  String name
  String bucketWarName
  String localFileName
  String instanceStatus     = STATUS_NEW

  Date  startedDate

  def process

  static transients = [ "process" ]

  static constraints = {
    name            unique:     true
    javaOptions     blank:      true
    port            nullable:   false
    bucketWarName   nullable:   false
    instanceStatus  nullable:   false
    startedDate     nullable:   true
  }

  public String getLogIdentifier(){
    return "$name@$port"
  }
}
