<%-- 
    Document   : index
    Created on : 25.2.2011, 12:16:20
    Author     : Martin Řehánek
--%>

<%@page import="cz.nkp.urnnbn.oaipmhprovider.conf.OaiPmhConfiguration"%>
<%@page import="cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat"%>
<%@page import="cz.nkp.urnnbn.oaipmhprovider.tools.PropertyLoader"%>
<%@page import="java.io.FileInputStream"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>CZIDLO: OAI-PMH data provider</title>
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
                /*width: 50px;*/
                height: 1px;
                overflow: hidden;
                visibility: visible;
                border: 1px solid black;
                padding: 5px;
                background: #21bdc6;
                /*text-align: right;*/
                text-align: center;
                vertical-align: middle;
                text-indent: 5px;
            }

            td {
                /*width: 50px;*/
                height: 1px;
                overflow: hidden;
                visibility: visible;
                /*  border: 1px solid black;*/
                padding: 5px;
                background: #21bdc6;
                /*text-align: right;*/
                text-align: center;
                vertical-align: middle;
                text-indent: 5px;
            }

            h1{
                margin-right: 25px;
                margin-left: 25px;
                align: left;
            }
            h2{
                margin-right: 25px;
                margin-left: 25px;
                align: left;
            }
            .descriptionText{
                max-width: 1000px;
                margin-right: 25px;
                margin-left: 25px;
            }

            .verb{
                text-align: left;
            }
        </style>
    </head>
    <body>
        <%
            OaiPmhConfiguration conf = OaiPmhConfiguration.instanceOf();
            String providerUrl = conf.getBaseUrl();
            String adminName = conf.getAdminEmail();
            if(adminName == null){
                adminName = conf.getAdminEmail();
            }
        %>

        <h1>OAI-PMH data provider</h1>
        <p class="descriptionText">
            Welcome to <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html">OAI-PMH</a> data provider of
            <a href="https://github.com/NLCR/CZIDLO/wiki/OAI-PMH-data-provider">CZIDLO (version <%=cz.nkp.urnnbn.core.Czidlo.VERSION%>)</a>.
            This particular instance,
            which is deployed at <b><%=request.getHeader("host")%></b>,
            handles language code <b><%=conf.getLanguageCode()%></b>.
            <br>
            For more information checkout the <a href ="../../web">main web interface</a> of CZIDLO or contact the administrator
            <a href="mailto:<%=conf.getAdminEmail()%>"><%=adminName%></a>.
        </p>

        <h2>Web interface</h2>
        <p class="descriptionText">
            This is a simple web interface for accessing OAI-PMH operations.
            The actual base url is <a href="<%=providerUrl%>"><%=providerUrl%></a>.
            <br>
            Every row in this table represents a single operation.
            First column always contains button with name of the operation. Clicking this button launches the operation.
            <br>
            Rest of the columns contain those parameters, that are applicable to given operation.
            <br>
            Some operations are available in more than one form and are therefore present in multiple rows.
            For example there are two versions of the operation <i>ListIdentifiers</i> - one for initial request
            and one for following requests identified by
            <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl"><i>resumtion tokens</i></a>.
        </p>


        <div></div>
        <div>
            <table align="left" rules="all">
                <!-- <caption><h2>OAI-PMH requests</h2></caption> -->
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
                        <td class="verb">
                            <input type="submit" value="Identify"/>
                        </td>
                    </form>
                    <td/><td/><td/><td/><td/><td/>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <input type="hidden" name="verb" value="ListSets"/>
                        <td class="verb">
                            <input type="submit" value="ListSets"/>
                        </td>
                    </form>
                    <td/><td/><td/><td/><td/><td/>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>">
                        <input type="hidden" name="verb" value="ListSets"/>
                        <td class="verb">
                            <input type="submit" value="ListSets"/>
                        </td>
                        <td/><td/><td/><td/><td/>
                        <td><input type="text" name="resumptionToken" size="10" /></td>
                    </form>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return buildIdentifier(this)">
                        <input type="hidden" name="verb" value="ListMetadataFormats"/>
                        <input type="hidden" name="identifier" value=""/>
                        <td class="verb">
                            <input type="submit" value="ListMetadataFormats"/>
                        </td>
                        <td/>
                        <td>
                            urn:nbn:<%=conf.getLanguageCode()%>:
                            <input type="text" size="6" name="registrarCode"/>
                            -
                            <input type="text" size="6" name="documentCode" />
                        </td>
                    </form>
                    <td/><td/><td/><td/>
                </tr>
                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <input type="hidden" name="verb" value="ListMetadataFormats"/>
                        <td class="verb">
                            <input type="submit" value="ListMetadataFormats"/>
                        </td>
                        <td/>
                        <td><input type="text" size="17" name="identifier"/></td>
                    </form>
                    <td/><td/><td/><td/>
                </tr>

                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return buildIdentifier(this)">
                        <input type="hidden" name="verb" value="GetRecord"/>
                        <input type="hidden" name="identifier" value=""/>
                        <td class="verb">
                            <input type="submit" value="GetRecord"/>
                        </td>
                        <td>
                            <select name="metadataPrefix">
                                <% for (MetadataFormat format : MetadataFormat.values()) {%>
                                <option value="<%=format.toString()%>"><%=format.toString()%>
                                </option>
                                <%}
                                    ;%>
                            </select>
                        </td>
                        <td>
                            urn:nbn:<%=conf.getLanguageCode()%>:
                            <input type="text" size="6" name="registrarCode"/>
                            -
                            <input type="text" size="6" name="documentCode" />
                        </td>
                    </form>
                    <td/><td/><td/><td/>
                </tr>

                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <input type="hidden" name="verb" value="GetRecord"/>
                        <td class="verb">
                            <input type="submit" value="GetRecord"/>
                        </td>
                        <td>
                            <select name="metadataPrefix">
                                <% for (MetadataFormat format : MetadataFormat.values()) {%>
                                <option value="<%=format.toString()%>"><%=format.toString()%>
                                </option>
                                <%}
                                    ;%>
                            </select>
                        </td>
                        <td><input type="text" size="17" name="identifier"/></td>
                    </form>
                    <td/><td/><td/><td/>
                </tr>
                <tr>
                </tr>

                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <input type="hidden" name="verb" value="ListIdentifiers"/>
                        <td class="verb">
                            <input type="submit" value="ListIdentifiers"/>
                        </td>
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
                        <input type="hidden" name="verb" value="ListIdentifiers"/>
                        <td class="verb">
                            <input type="submit" value="ListIdentifiers"/>
                        </td>
                        <td/><td/><td/><td/><td/>
                        <td><input type="text"  size="10" name="resumptionToken"/></td>
                    </form>
                </tr>

                <tr>
                    <form action ="<%=providerUrl%>" onsubmit="return disableEmptyInputs(this)">
                        <input type="hidden" name="verb" value="ListRecords"/>
                        <td class="verb">
                            <input type="submit" value="ListRecords"/>
                        </td>
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
                        <input type="hidden" name="verb" value="ListRecords"/>
                        <td class="verb">
                            <input type="submit" value="ListRecords"/>
                        </td>
                        <td/><td/><td/><td/><td/>
                        <td><input type="text"  size="10" name="resumptionToken"/></td>
                    </form>
                </tr>
            </table>
        </div>

        <script type="text/javascript" >
            function isEmpty(value){
                return (value == "");
            }

            function buildIdentifier(form){
                registrarCode = form.elements["registrarCode"];
                documentCode = form.elements["documentCode"];
                identifier = "urn:nbn:<%=conf.getLanguageCode()%>:" + registrarCode.value + "-" + documentCode.value ;
                form.elements["identifier"].value = identifier;
                registrarCode.disabled = true;
                documentCode.disabled = true;
                //console.log(identifier);
                return true;
            }
            
            function disableEmptyInputs(form){
                for (i=0; i < form.elements.length; i++){
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
            }
        </script>
    </body>
</html>
