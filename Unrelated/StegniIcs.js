// Zap. št. programa v zbiralcu
var program = 2
//Št. letnika
var letnik = 3
//Št. smeri 
var smer = 1




changeHappened = function(str, id)
{
 
  xmlHttp=GetXmlHttpObject()
 
  if (str == "program")
  {
    console.log("Pofuru program")
	_program_id = id;
    var url="lib/helper.php?type=program&program_id=" + id;
    xmlHttp.onreadystatechange= function()
    {
    
      if (xmlHttp.readyState == 4)  {
        var myObject = eval('(' + xmlHttp.responseText + ')');
        var hide_branch_code = document.getElementById("hide_branch_code").value == "1"?true:false;
        
        document.getElementById("program_response").value = _program_id;
    
        document.getElementById("year").options.length = 0;
        document.getElementById("branch").options.length = 0;
        if (document.getElementById("with_groups").value == "1")
        {
          document.getElementById("group").options.length = 0;
        }
        
        document.getElementById("year_response").value = "";
    
        document.getElementById("year").options[0] = new Option("-- vsi --", "");
        document.getElementById("year").options[0].selected = true;
    
        for (i = 0; i < myObject.result[0]; ++i)
        {
          document.getElementById("year").options[i + 1] = new Option(i + 1, i + 1);
        }
    
        document.getElementById("year_response").value=myObject.result[0];
        
    
        if (myObject.result[1].length > 0)
        {
          document.getElementById("branch").options[0] = new Option("-- izberite --", "");
          document.getElementById("branch").options[0].disabled = true;
    
          for(i = 0; i < myObject.result[1].length; ++i)
          {
              if(hide_branch_code)
                  document.getElementById("branch").options[i + 1] = new Option(myObject.result[1][i].name, myObject.result[1][i].branch_id);
              else	
                  document.getElementById("branch").options[i + 1] = new Option(myObject.result[1][i].name + " - " + myObject.result[1][i].code, myObject.result[1][i].branch_id);
          }
          document.getElementById("branch_response").value = xmlHttp.responseText;
        }
        //Moj dodatek
    document.getElementById("year").selectedIndex = letnik
    console.log(document.getElementById("year").selectedIndex)
   document.getElementById("year").onchange()
      }
    }
    xmlHttp.open("GET",url,true);
    xmlHttp.send(null);

  }
  else if (str == "year")
  {
    console.log("Pofuru let")
    var url="lib/helper.php?type=year&program_id=" + document.getElementById("program").value + "&year=" + id;
    xmlHttp.onreadystatechange= function()
    {
        if (xmlHttp.readyState == 4)
        {
          var response = eval('(' + xmlHttp.responseText + ')');
          var branches = response.result[1];    
          var hide_branch_code = document.getElementById("hide_branch_code").value == "1"?true:false;
      
          document.getElementById("branch").options.length = 0;
          if (document.getElementById("with_groups").value == "1")
          {
            document.getElementById("group").options.length = 0;
          }
          
          if (branches.length > 0)
          {
            document.getElementById("branch").options[0] = new Option("-- izberite --", "");
            document.getElementById("branch").options[0].selected = true;
          }
          
          var select_first_branch = false;// avtomatsko izberi prvo skupino
          if(document.getElementById("branch_selector").value != "1") {	//Skrij skupine in grupe - ko je samo ena skupina                    
              if (branches.length == 1) {
                  var groups = response.result[2];
      
                  for(i = 0; i < groups.length; ++i)
                  {
                    document.getElementById("group").options[i + 1] = new Option(groups[i].name, groups[i].groups_id);
                    document.getElementById("group").options[i + 1].selected = true;
                  }
                  
                  hideElement('branchRow');
                  hideElement('groupsRow');
                  select_first_branch=true;
              } else { 
                  showElement('branchRow');
                  showElement('groupsRow');
              }
          }
      
          var first_branch_id;
          for(i = 0; i < branches.length; ++i)
          {
              if(hide_branch_code)
                  document.getElementById("branch").options[i + 1] = new Option(branches[i].name, branches[i].branch_id);
              else	
                  document.getElementById("branch").options[i + 1] = new Option(branches[i].name + " - " + branches[i].code, branches[i].branch_id);    
              
              if(i==0 && select_first_branch) {
                  first_branch_id = branches[0].branch_id;
                  document.getElementById("branch").options[i + 1].selected = true;
              }
          }
      
          document.getElementById("branch_response").value = xmlHttp.responseText;
          
          //	Tako se napolnijo se skupine 
          if(document.getElementById("branch_selector").value != "1" && branches.length == 1) {    
              xmlHttp=GetXmlHttpObject()	
              var url="lib/helper.php?type=branch&branch_id=" + first_branch_id;
              xmlHttp.onreadystatechange=nobranchChangeCallback;
              xmlHttp.open("GET",url,true);
              xmlHttp.send(null);
          }
          
        }
        document.getElementById("branch").selectedIndex = smer
        document.getElementById("branch").onchange()
    }
    ;
    xmlHttp.open("GET",url,true);
    xmlHttp.send(null);
    
  }
  //tu gre neki v kurc
  else if (str == "branch")
  {
      console.log("pofuru branch")
    var url="lib/helper.php?type=branch&branch_id=" + id;
    xmlHttp.onreadystatechange=branchChangeCallback;
    xmlHttp.open("GET",url,true);
    xmlHttp.send(null);
  
  }
}


document.getElementById("program").selectedIndex = program
changeHappened("program",program)

