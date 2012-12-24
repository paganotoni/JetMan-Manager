package manager

/**
 * Created with IntelliJ IDEA.
 * User: antoniopagano
 * Date: 12/1/12
 * Time: 11:23 PM 
 */
enum APIStatusCodes {

  SUCCESS( 200, "Operation Successfull" ),
  INCOMPLETE_PARAMETERS( 401, "Incomplete Parameters" ),
  PORT_TAKEN(409,"Port has been Taken"),
  NO_PORT_AVAILABLE(503, "No port available");

  final Integer HTTPStatus
  final String message

  private APIStatusCodes(Integer status, String message ){
    this.HTTPStatus = status
    this.message    = message
  }

  public int getHTTPStatus(){
    return HTTPStatus
  }

  public String getMessage(){
    return message
  }

}
