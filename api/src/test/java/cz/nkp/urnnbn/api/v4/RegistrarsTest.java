package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.config.RestAssuredConfig.newConfig;
import static com.jayway.restassured.config.XmlConfig.xmlConfig;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import groovy.time.BaseDuration.From;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;




//import static com.jayway.restassured.RestAssured.*;
//import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
//import static com.jayway.restassured.config.RestAssuredConfig.*;
//import static com.jayway.restassured.config.XmlConfig.*;
//import static org.hamcrest.Matchers.*;
//import static com.jayway.restassured.path.xml.XmlPath.*;
import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
import static com.jayway.restassured.config.RestAssuredConfig.*;
import static com.jayway.restassured.config.XmlConfig.*;
import static org.hamcrest.Matchers.*;
import static com.jayway.restassured.path.xml.XmlPath.*;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.config.XmlPathConfig;
import com.jayway.restassured.response.ResponseBodyExtractionOptions;
import com.jayway.restassured.response.ValidatableResponse;

import cz.nkp.urnnbn.api.Utils;

public class RegistrarsTest {
    
    private String responseXsdString;
    private NamespaceContext nsContext;
    
    @BeforeSuite
    public void beforeSuite() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api/v4/";
        // RestAssured.authentication = basic("username", "password");
        // RestAssured.rootPath = "x.y.z";
        //TODO: v4
        responseXsdString = Utils.readXsd("http://localhost:8080/api/v3/response.xsd");
        nsContext = Utils.buildNsContext("c", "http://resolver.nkp.cz/v3/");
    }
    
    @Test
    public void getRegistrarsJson() {
        //TODO
        given().parameter("format", "json").get("/registrars")//
                .then()//
                .body("registrars", equalTo("TODO"));
    }

    @Test
    public void getRegistrarsContentType() {
        expect().contentType(ContentType.XML).when().get("/registrars");
        with().parameters("format", "xml").expect().contentType(ContentType.XML).when().get("/registrars");
        with().parameters("format", "json").expect().contentType(ContentType.JSON).when().get("/registrars");
    }

    @Test
    public void getRegistrarsXsdValid() {
        with().parameters("format", "xml").expect().body(matchesXsd(responseXsdString)).when().get("/registrars");
    }
    
    


}