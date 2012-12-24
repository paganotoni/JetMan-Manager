package manager

import grails.converters.JSON
@Mixin( PortHelper )
class APIController {

  def instanceManagerService
  def portVerifierService

  def createInstance() {
    if ( !params.bucketWarName || !params.name ) {
      response.status = APIStatusCodes.INCOMPLETE_PARAMETERS.HTTPStatus
      render APIStatusCodes.INCOMPLETE_PARAMETERS.message
    } else {
      def startPort = grailsApplication.config.runner.startPort
      def endPort = grailsApplication.config.runner.endPort

      def port = findAvailablePort(startPort,endPort)
      if (!port){
        response.status = APIStatusCodes.NO_PORT_AVAILABLE.HTTPStatus
        render APIStatusCodes.NO_PORT_AVAILABLE.message
        return
      }

      instanceManagerService.createInstance(params.name, params.javaOpts, port.toString(), params.bucketWarName )

      response.status = APIStatusCodes.SUCCESS.HTTPStatus
      render APIStatusCodes.SUCCESS.message
    }
  }

  def listInstances(){
    response.status = APIStatusCodes.SUCCESS.HTTPStatus
    render APIStatusCodes.SUCCESS.message
  }

  def deleteInstance(){
    if ( !params.id && !params.name ) {
      response.status = APIStatusCodes.INCOMPLETE_PARAMETERS.HTTPStatus
      render APIStatusCodes.INCOMPLETE_PARAMETERS.message
    } else {
      instanceManagerService.deleteInstance( params.long("id"), params.name)

      response.status = APIStatusCodes.SUCCESS.HTTPStatus
      render APIStatusCodes.SUCCESS.message
    }
  }

  def updateInstance(){
    if ( !params.id && !params.name ) {
      response.status = APIStatusCodes.INCOMPLETE_PARAMETERS.HTTPStatus
      render APIStatusCodes.INCOMPLETE_PARAMETERS.message
    } else {
      instanceManagerService.updateInstance(params.long("id"), params.name, params.javaOptions, params.port, params.bucketWarName )

      response.status = APIStatusCodes.SUCCESS.HTTPStatus
      render APIStatusCodes.SUCCESS.message
    }
  }
}
