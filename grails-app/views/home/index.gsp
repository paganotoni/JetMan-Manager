<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
  <meta name="layout" content="main">
</head>
<body>
  <table class="table table-bordered table-stripped">
    <thead>
      <tr>
        <th>Name</th>
        <th>Status</th>
        <th>Java Options</th>
        <th>Port #</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      <g:each in="${instances}" var="instance">
        <tr>

          <td>${instance.name}</td>
          <td>${instance.instanceStatus}</td>
          <td>${instance.javaOptions}</td>
          <td>${instance.port}</td>

          <td>
            <div class="btn btn-success btn-small" data-id="${instance.id}" data-remote-url="${createLink(controller: 'API', action: 'updateInstance')}">Update</div>
            <div class="btn btn-danger btn-small btn-delete-instance" data-id="${instance.id}" data-remote-url="${createLink(controller: 'API', action:'deleteInstance')}">Delete</div>
          </td>
        </tr>
      </g:each>
    </tbody>
    <tfoot>
      <tr>
        <td colspan="5">
          <div class="btn btn-primary" data-toggle="modal" data-target="#instanceModal">
            Launch Instance
          </div>
        </td>
      </tr>
    </tfoot>
  </table>

  <div class="modal hide" id="instanceModal">
    
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
      <h4>New Instance</h4>
    </div>

    <form action="">
      <div class="modal-body">
        <label for="name">Name</label>
        <input type="text" id="name" name="name">
        <label for="bucketWarName">War's name</label>
        %{--<input type="text" name="bucketWarName" id="warName">--}%
        <g:select name="bucketWarName" from="${availableWars}"/>
      </div>
      <div class="modal-footer">
        <div class="btn btn-primary add-instance-button" data-remote-url="${createLink(controller: 'API', action: 'createInstance')}">Add Instance</div>
      </div>
    </form>
  </div>
</body>
</html>