<%-- 
    Document   : index
    Created on : 25.2.2011, 12:16:20
    Author     : Martin Řehánek
--%>

<%@page import="cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat"%>
<%@page import="cz.nkp.urnnbn.oaipmhprovider.tools.PropertyLoader"%>
<%@page import="cz.nkp.urnnbn.oaipmhprovider.Configuration"%>
<%@page import="java.io.FileInputStream"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>OaiMzk</title>
        <style type="text/css" media="Screen">
            table {
                width: auto;
                height: 1px;
                table-layout: auto;
                border-collapse: collapse;
                margin-left: 20px;
                /* border: 1px solid black;*/
            }
            th{
                width: 50px;
                height: 1px;
                overflow: hidden;
                visibility: visible;
                border: 1px solid black;
                padding: 5px;
                background: #21bdc6;
                text-align: right;
                vertical-align: middle;
                text-indent: 5px;
            }

            td {
                width: 50px;
                height: 1px;
                overflow: hidden;
                visibility: visible;
                /*  border: 1px solid black;*/
                padding: 5px;
                background: #21bdc6;
                text-align: right;
                vertical-align: middle;
                text-indent: 5px;
            }
        </style>
    </head>
    <body>
        <%
            PropertyLoader loader = Configuration.getPropertyLoader();
            String providerUrl = loader.loadString("provider.baseUrl");
        %>

        <h1 align="center">Welcome to implementation of OAI-PMH repository for URN:NBN Resolver system</h1>
        <div>
            <table align="left" rules="all">
                <caption>OAI-PMH requests</caption>
                <tr>
                    <th>verb</th>
                    <th>metadataPrefix</th>
                    <th>identifier</th>
                    <th>from</th>
                    <th>until</th>
                    <th>set</th>
                    <th>resumptionToken</th>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>">
                        <input type="hidden" name="verb" value="Identify"/>
                        <td>
                            <input type="submit" value="Identify"/>
                        </td>
                    </form>
                    <td/><td/><td/><td/><td/><td/>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <td><input type="submit" value="ListSets"/></td>
                        <input type="hidden" name="verb" value="ListSets"/>
                    </form>
                    <td/><td/><td/><td/><td/><td/>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>">
                        <td><input type="submit" value="ListSets"/></td>
                        <input type="hidden" name="verb" value="ListSets"/>
                        <td/><td/><td/><td/><td/>
                        <td><input type="text" name="resumptionToken" size="10" /></td>
                    </form>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <td><input type="submit" value="ListMetadataFormats"/></td>
                        <input type="hidden" name="verb" value="ListMetadataFormats"/>
                        <td/>
                        <td><input type="text" size="10" name="identifier"/></td>
                    </form>
                    <td/><td/><td/><td/>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <td><input type="submit" value="GetRecord"/></td>
                        <input type="hidden" name="verb" value="GetRecord"/>
                        <td>
                            <select name="metadataPrefix">
                                <% for (MetadataFormat format : MetadataFormat.values()) {%>
                                <option value="<%=format.toString()%>"><%=format.toString()%>
                                </option>
                                <%}
                                    ;%>
                            </select>
                        </td>
                        <td><input type="text" size="10" name="identifier"/></td>
                    </form>
                    <td/><td/><td/><td/>
                </tr>
                <tr>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <td><input type="submit" value="ListIdentifiers"/></td>
                        <input type="hidden" name="verb" value="ListIdentifiers"/>
                        <td>
                            <select name="metadataPrefix">
                                <% for (MetadataFormat format : MetadataFormat.values()) {%>
                                <option value="<%=format.toString()%>"><%=format.toString()%>
                                </option>
                                <%}
                                    ;%>
                            </select>
                        </td>
                        <td/>
                        <td><input type="text"  size="10" name="from"/></td>
                        <td><input type="text"  size="10" name="until"/></td>
                        <td><input type="text"  size="10" name="set"/></td>
                    </form>
                    <td/>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>">
                        <td><input type="submit" value="ListIdentifiers"/></td>
                        <input type="hidden" name="verb" value="ListIdentifiers"/>
                        <td/><td/><td/><td/><td/>
                        <td><input type="text"  size="10" name="resumptionToken"/></td>
                    </form>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <td><input type="submit" value="ListRecords"/></td>
                        <input type="hidden" name="verb" value="ListRecords"/>
                        <td>
                            <select name="metadataPrefix">
                                <% for (MetadataFormat format : MetadataFormat.values()) {%>
                                <option value="<%=format.toString()%>"><%=format.toString()%>
                                </option>
                                <%}
                                    ;%>
                            </select>
                        </td>
                        <td/>
                        <td><input type="text"  size="10" name="from"/></td>
                        <td><input type="text"  size="10" name="until"/></td>
                        <td><input type="text"  size="10" name="set"/></td>
                    </form>
                    <td/>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>">
                        <td><input type="submit" value="ListRecords"/></td>
                        <input type="hidden" name="verb" value="ListRecords"/>
                        <td/><td/><td/><td/><td/>
                        <td><input type="text"  size="10" name="resumptionToken"/></td>
                    </form>
                </tr>
            </table>
        </div>

        <script type="text/javascript" >
            function disableEmptyInputs(form){
                for (i=0;i<form.elements.length;i++){
                    element = form.elements[i];
                    //            console.log("index: " + i);
                    //            console.log("type: " + element.type);
                    //            console.log("name: " + element.name);
                    //            console.log("value: " + element.value);
                    if(isEmpty(element.value)){
                        element.disabled = true;
                    }
                }
                return true;
                //return false;
            }

            function isEmpty(value){
                return (value == "");
            }

            function buildIdentifier(form){
                prefix = form.elements["idPrefix"];
                base = form.elements["base"];
                sysno = form.elements["sysno"];
                identifier = prefix.value + ":" + base.value + "-" + sysno.value ;
                form.elements["identifier"].value = identifier;
                prefix.disabled = true;
                base.disabled = true;
                sysno.disabled = true;
                //console.log(identifier);
                //return false;
                return true;
            }    
        </script>
    </body>
</html>
