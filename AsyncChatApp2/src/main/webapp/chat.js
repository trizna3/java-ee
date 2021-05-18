var xmlHttp;

window.onload = function()
{  
    var input = document.getElementById("message");
    input.addEventListener('input', updateValue);
    var name = window.localStorage.getItem('name');
    document.getElementById("myName").innerHTML = name;
    sendNewRequest();
}

function sendNewRequest()
{
    if (window.ActiveXObject)
    {
        xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    else if (window.XMLHttpRequest)
    {
        xmlHttp=new XMLHttpRequest();
    }
    xmlHttp.open("POST", "GetMessagesServlet", true);
    xmlHttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xmlHttp.onreadystatechange=handleStateChange;
    var name = window.localStorage.getItem('name');
    var parameters = "&name=" + name; 
    xmlHttp.send(parameters);
}

function handleStateChange()
{
    if (xmlHttp.readyState===4)
    {
        if(xmlHttp.status===200)
        {
          var newMessage = xmlHttp.responseText;          
          document.getElementById("messages").innerHTML = newMessage;
          sendNewRequest();
        }
        else if (xmlHttp.status > 200)
        {
            sendNewRequest();
        }
        else if (xmlHttp.status !== 0)  
        {
           alert("Error loading page "+ xmlHttp.status + ":"+xmlHttp.statusText);
        }
    }
}

function updateValue(e) 
{
    var xmlHttp2;
    if (window.ActiveXObject)
    {
        xmlHttp2=new ActiveXObject("Microsoft.XMLHTTP");
    }
    else if (window.XMLHttpRequest)
    {
        xmlHttp2=new XMLHttpRequest();
    }
    var name = window.localStorage.getItem('name');

    var parameters = "&name=" + name + "&msg=" + name + ": " + e.target.value; 
    xmlHttp2.open("POST", "SendNewMessageServlet", true);
    xmlHttp2.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xmlHttp2.send(parameters);
    console.log("sending" + parameters);
}
