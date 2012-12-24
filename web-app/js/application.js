if (typeof jQuery !== 'undefined') {
  (function ($) {
    $('#spinner').ajaxStart(function () {
      $(this).fadeIn();
    }).ajaxStop(function () {
          $(this).fadeOut();
        });
  })(jQuery);
}

JMan = {}

JMan.initAddInstanceButtons = function () {
  $(".add-instance-button").click(function () {
    event.preventDefault();
    var instanceData = $(this).parents("form").serialize();
    var url          = $(this).attr("data-remote-url");

    JMan.remoteOperation(url, instanceData );
  });
}


//JMan.initDeleteInstanceButtons = function(){
//  $(".btn-delete-instance").click(function(){
//    var instanceId = $(this).attr("data-id");
//    var url = $(this).attr("data-remote-url");
//
//    JMan.remoteOperation(url, {
//      id: instanceId
//    })
//  });
//}

JMan.initRemoteOperationButtons = function(){
  $(".btn[data-id][data-remote-url]").click(function(){
    var instanceId = $(this).attr("data-id");
    var url        = $(this).attr("data-remote-url");

    console.log( instanceId )

    JMan.remoteOperation(url, {
      id: instanceId
    })
  });
}

JMan.remoteOperation = function(url, params){
  $.ajax({
    url: url,
    data: params ,
    success: function(data){
      window.location.reload();
    }
  })
}

$(document).ready(function(){
  JMan.initAddInstanceButtons();
  JMan.initRemoteOperationButtons();
})
