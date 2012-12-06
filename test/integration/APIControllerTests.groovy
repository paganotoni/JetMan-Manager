import manager.APIController
import manager.APIStatusCodes
import manager.Instance
/**
 * Created with IntelliJ IDEA.
 * User: antoniopagano
 * Date: 12/1/12
 * Time: 11:18 PM 
 */
class APIControllerTests extends GroovyTestCase {

  APIController apiController

  public void setUp(){
    apiController =  new APIController();
  }

  public void testCorrectAddInstance(){
    apiController.request.parameters = [name: 'SomeName', port: "1212", javaOptions: "-Xmx512m -XX:PermSize=64M -XX:MaxPermSize=128M", bucketWarName: "sample.war"]
    apiController.createInstance()

    assert apiController.response.status == APIStatusCodes.SUCCESS.HTTPStatus
    assert apiController.instanceManagerService.instances.size() > 0
  }

  public void testIncompleteAddInstance(){
    //PORT and warURL are required
    apiController.request.parameters = [ name: 'SomeName2',javaOptions: "-Xmx512m -XX:PermSize=64M -XX:MaxPermSize=128M", bucketWarName: "sample.war"]
    apiController.createInstance()

    assert apiController.response.status == APIStatusCodes.INCOMPLETE_PARAMETERS.HTTPStatus

  }

  public void testListInstance(){
    apiController.listInstances()
    assert apiController.response.status == APIStatusCodes.SUCCESS.HTTPStatus

    apiController.request.parameters = [name: 'SomeName3',port: "1212", javaOptions: "-Xmx512m -XX:PermSize=64M -XX:MaxPermSize=128M", bucketWarName: "sample.war"]
    apiController.createInstance()

    assert apiController.response.status == APIStatusCodes.SUCCESS.HTTPStatus
    assert apiController.instanceManagerService.instances.size() > 0

    apiController.listInstances()
    assert apiController.response.json.size() > 0
  }

  public void testDeleteInstance(){
    def instance =  new Instance(name: 'SomeName23',port: "1212", bucketWarName: "www", javaOptions: "wwww", localFileName: "sss")
    instance.save()

    apiController.instanceManagerService.instances << instance

    def beforeServiceCount  = apiController.instanceManagerService.instances.size()
    def beforeDatabaseCount = Instance.count()

    apiController.request.parameters = [id: apiController.instanceManagerService.instances.last().id.toString() ]
    apiController.deleteInstance()

    assert beforeServiceCount > apiController.instanceManagerService.instances.size()
    assert Instance.count() < beforeDatabaseCount


  }



}
