package manager

import grails.converters.JSON

class APIController {

  def instanceManagerService
  def portVerifierService

  def createInstance() {
    if ( !params.port || !params.bucketWarName || !params.name ) {
      response.status = APIStatusCodes.INCOMPLETE_PARAMETERS.HTTPStatus
      render APIStatusCodes.INCOMPLETE_PARAMETERS.message
    } else {

      if( !portVerifierService.isPortAvailable(params.port) ){
        response.status = APIStatusCodes.PORT_TAKEN.HTTPStatus
        render APIStatusCodes.PORT_TAKEN.message
        return
      }

      instanceManagerService.createInstance(params.name, params.javaOpts, params.port, params.bucketWarName )

      response.status = APIStatusCodes.SUCCESS.HTTPStatus
      render APIStatusCodes.SUCCESS.message
    }
  }

  def listInstances(){
    response.status = APIStatusCodes.SUCCESS.HTTPStatus
    render instanceManagerService.instances as JSON
  }

  def deleteInstance(){
    if ( !params.id && !params.name ) {
      response.status = APIStatusCodes.INCOMPLETE_PARAMETERS.HTTPStatus
      render APIStatusCodes.INCOMPLETE_PARAMETERS.message
    } else {
      instanceManagerService.deleteInstance( params.long("id"), params.name)

      response.status = APIStatusCodes.SUCCESS.HTTPStatus
      render instanceManagerService.instances as JSON
    }
  }
}
