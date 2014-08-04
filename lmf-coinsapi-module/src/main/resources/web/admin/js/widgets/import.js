/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function Importer(id,host) {

    var LMF = new MarmottaClient(host);

    var loader =$("<img style='position: relative;top: 4px;margin-left: 10px;' src='../public/img/loader/ajax-loader_small.gif'>");

    var container = $("#"+id);

    var style = $("<style type='text/css'>.td_title{font-weight:bold;width:100px}</style>")

    var step1 = $("<div></div>");
    var step2 = $("<div></div>");
    var step3 = $("<div></div>");
    var step4 = $("<div></div>");
    var button = $("<div style='margin-top:20px'></div>");

    var metadata_types;
    var contexts;
    var example_context;

    function init() {

        $.getJSON("../../import/types",function(data) {
            metadata_types = data;
        });

        $.getJSON("../../context/list",function(data) {
            loader.hide();
            contexts = data;
        });
        
        $.getJSON("../../config/data/kiwi.host",function(data) {
            example_context = data["kiwi.host"] + "context/name";
        });        
        
        container.empty();
        container.append(style);
        container.append($("<h1></h1>").append("<span>Import</span>").append(loader));
        container.append(step1);
        container.append(step2);
        container.append(step3);
        container.append(step4);
        container.append(button);

        step1.append("<h2>1. Select input source-type:</h2>");
        step1.append($("<a class='import_type'></a>").text("File").click(function(){
          button.empty();
          step4.empty();
          step3.empty();
          step2.empty();
          step2.append("<h2>2. Select file:</h2>");
          step2.append("<form enctype='multipart/form-data' action='../../coinsapi/upload' method='post'>\n<input id='file' name='file' type='file' />\n<input type='submit' value='Submit' />\n</form>");
       }));
       step1.append("<span>|</span>");
       step1.append($("<a class='import_type' ></a>").text("URL").click(function(){
          button.empty();
          step4.empty()
          step3.empty();
          step2.empty();
          step2.append("<h2>2. Define url:</h2>");
          var input = $("<input type='text' style='width: 300px'>");
          step2.append(input);
          step2.append($("<button></button>").text("ok").click(function(){
              step3.empty();
              if(input.val()==undefined || input.val()=="") alert("Define an URL first!");
              else if(!isUrl(input.val())) alert("URL is not valid!")
              else predefine(input,"url");
          }));
       }));
    }

    function predefine(input_field,source_type) {
        var source_relation;
        var source_filetype;
        var source_filetype_input;
        var context;
        var context_input;
        var context_type="default";

        var url = $("<input type='text' style='width: 300px'>");

        function waitForMetadataTypes() {
            step3.append("<h2>3. Import (..loading)</h2>");
            if(metadata_types==undefined) setTimeout(waitForMetadataTypes,1000);
            else writeTable()
        }
        waitForMetadataTypes();

        function writeTable() {
            step4.empty();
            button.empty();
            step3.empty().append("<h2>3. Import</h2>");

            var b= $("<button  style='font-weight:bold'></button>").text("Import!").click(function(){
                context = context_type=="default"?undefined:context_input.val();
                context = context==null?context=null:context;
                var _url=undefined;
                if(context!=null && !isUrl(context)) {
                    alert("context must be an url!"); return;
                }
                if(source_type=="file") {
                   upload(input_field,context);
                } else {
                  external(input_field,context);
                }
            });
            button.append(b);
        }
    }

    function isUrl(s) {
	    var regexp = /(file|ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/
	    return regexp.test(s);
    }

    function upload(source_filetype_input,context) {
    	var form = document.createElement("form");
    	var input = document.createElement("input");
    	
    	loader.show();
    	
    	form.action = "../coinsapi/upload";
    	form.method = "post";
    	form.enctype = "multipart/form-data";

        input.name = "file";
    	input.id = "file";
    	input.type = "file";
    	input.value = source_filetype_input.get(0).files[0];

    	form.appendChild(input);
    	document.body.appendChild(form);
    	form.submit();
    	
    	loader.hide();
    }

    function external(source_filetype_input,context) {
      loader.show();
      LMF.importClient.uploadFromUrl(source_filetype_input.val(),source_filetype,context,function(){
          alert("upload is running; you can control the running import tasks on $LMF/core/admin/tasks.html");
          loader.hide();
      },function(error){
          alert(error.name+": "+error.message);
          loader.hide();
      })
    }

    function resource(source_type,source_filetype_input,source_relation,source_filetype,context,content)  {
        if(source_type!="file") {
            alert("import content from url is not implemented yet");return;
        }
            if(!source_filetype) {
                alert("mimetype must be defined");return;
            }
            loader.show();
            LMF.resourceClient.createResource(content,function(data){
                LMF.resourceClient.updateResourceContent(data,source_filetype_input.get(0).files[0],source_filetype,function(){
                    alert("set content of "+data);
                    loader.hide();
                },function(error){
                    loader.hide();
                    alert(error.name+": "+error.message);
                })
            },function(error){
                alert(error.name+": "+error.message);
                loader.hide();
            });
    }

    init();
}
