class UrlMappings {

	static mappings = {
//		"/$controller/$action?/$id?"{
//			constraints {
//				// apply constraints here
//			}
//		}

    "/API/instance"(controller: "API"){
      action = [POST: 'createInstance', GET: 'listInstances', DELETE: 'deleteInstance']
    }

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
