package goRest.v2.api.test;

import goRest.v2.api.responseSchemaClass.GetAllUserSchema;
import goRest.v2.api.responseSchemaClass.InvalidCreateUserSchema;
import goRest.v2.api.utils.Configuration;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

//    Note : Because PUT / PATCH is same response, i only make PUT
public class TestUser {


    @BeforeTest
    public void setUp() throws IOException {
        RestAssured.baseURI = Configuration.baseURI;
    }

    // It is made for get first user id, because in go rest i can't get user based on email i need this.
    public int getFirstUserID(){
        List<GetAllUserSchema> users  =  RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .get("users")
                .jsonPath().getList("", GetAllUserSchema.class);

        return users.get(0).getId();
    }

    @Test
    public void getUsers(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"testGetAllUsersSchema.json");
        RestAssured.given().when()
                .get("users/")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file));
    }

    @Test
    public void createUserWithNoAuth(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"testGetUserNotFoundSchema.json");
        JSONObject bodyObject = new JSONObject();
        String email = "banyu13@mailinator.com";
        String gender = "male";
        String name = "adaefrv";
        String status = "active";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        RestAssured.given()
                .header("Content-Type","application/json")
                .body(bodyObject.toString())
                .post("/users")
                .then()
                .assertThat().statusCode(401)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("message",Matchers.equalTo("Authentication failed"));

    }

    @Test
    public void createUserWithNoBody(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"failedCreateUserSchema.json");
        RequestSpecification request =  RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token);
        Response response = request.when().post("users/");

        List<InvalidCreateUserSchema> items = response.jsonPath().getList("", InvalidCreateUserSchema.class);
        response.then().assertThat().statusCode(422)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file));

        int totalFieldEmail = 0;
        int totalFieldName = 0;
        int totalFieldStatus = 0;
        int totalFieldGender = 0;
        for(InvalidCreateUserSchema item: items){
            String message = item.getMessage();
            String field = item.getField();
            System.out.println("field = "+field);
            if(field.equals("email")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldEmail++;
            }
            else if(field.equals("name")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldName++;
            }
            else if(field.equals("status")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldStatus++;
            }
            else if(field.equals("gender")){
                Assertions.assertEquals("can't be blank, can be male of female",message);
                totalFieldGender++;
            }
            else{
                Assertions.fail("The field "+field+" is not recognized");
            }
        }

        if(!(totalFieldEmail == 1 && totalFieldGender == 1 && totalFieldName == 1 && totalFieldStatus == 1)){
            Assertions.fail("The field contain more field than expected");
        }
    }

    @Test
    public void createUserWithEmptyData(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"failedCreateUserSchema.json");

        JSONObject bodyObject = new JSONObject();
        String email = "";
        String gender = "";
        String name = "";
        String status = "";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        RequestSpecification request =  RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .body(bodyObject.toString());
        Response response = request.when().post("users/");

        List<InvalidCreateUserSchema> items = response.jsonPath().getList("", InvalidCreateUserSchema.class);
        response.then().assertThat().statusCode(422)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file));

        int totalFieldEmail = 0;
        int totalFieldName = 0;
        int totalFieldStatus = 0;
        int totalFieldGender = 0;
        for(InvalidCreateUserSchema item: items){
            String message = item.getMessage();
            String field = item.getField();
            System.out.println("field = "+field);
            if(field.equals("email")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldEmail++;
            }
            else if(field.equals("name")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldName++;
            }
            else if(field.equals("status")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldStatus++;
            }
            else if(field.equals("gender")){
                Assertions.assertEquals("can't be blank, can be male of female",message);
                totalFieldGender++;
            }
            else{
                Assertions.fail("The field "+field+" is not recognized");
            }
        }

        if(!(totalFieldEmail == 1 && totalFieldGender == 1 && totalFieldName == 1 && totalFieldStatus == 1)){
            Assertions.fail("The field contain more field than expected");
        }
    }

    @Test
    public void createUserWithInvalidFormat(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"failedCreateUserSchema.json");

        JSONObject bodyObject = new JSONObject();
        String email = "!@$!%!@%";
        String gender = "!@#$!#!";
        String name = "!@$$!@$!%";
        String status = "!@$!%#";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        RequestSpecification request =  RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .body(bodyObject.toString());
        Response response = request.when().post("users/");


        List<InvalidCreateUserSchema> items = response.jsonPath().getList("", InvalidCreateUserSchema.class);
        response.then().assertThat().statusCode(422)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file));

        int totalFieldEmail = 0;
        int totalFieldStatus = 0;
        int totalFieldGender = 0;
        for(InvalidCreateUserSchema item: items){
            String message = item.getMessage();
            String field = item.getField();
            System.out.println("field = "+field);
            if(field.equals("email")){
                Assertions.assertEquals("is invalid",message);
                totalFieldEmail++;
            }
            else if(field.equals("status")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldStatus++;
            }
            else if(field.equals("gender")){
                Assertions.assertEquals("can't be blank, can be male of female",message);
                totalFieldGender++;
            }
            else{
                Assertions.fail("The field "+field+" is not recognized");
            }
        }

        if(!(totalFieldEmail == 1 && totalFieldGender == 1 && totalFieldStatus == 1)){
            Assertions.fail("The field contain more field than expected");
        }
    }

    @Test
    public void createUser(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"succesCreateUserSchema.json");
        JSONObject bodyObject = new JSONObject();
        String email = "banyu23@mailinator.com";
        String gender = "female";
        String name = "nyoman1";
        String status = "active";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        RequestSpecification request = RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .body(bodyObject.toString());

        Response response = request.when().post("users/");

        response
                .then()
                .log().all()
                .assertThat().assertThat().statusCode(201)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("name",Matchers.equalTo(name))
                .assertThat().body("gender",Matchers.equalTo(gender))
                .assertThat().body("email",Matchers.equalTo(email))
                .assertThat().body("status",Matchers.equalTo(status));
    }

    @Test
    public void putUserWithNoAuth(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"testGetUserNotFoundSchema.json");
        JSONObject bodyObject = new JSONObject();
        String email = "banyu13@mailinator.com";
        String gender = "male";
        String name = "adaefrv";
        String status = "active";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        int userID = getFirstUserID();

        RestAssured.given()
                .header("Content-Type","application/json")
                .body(bodyObject.toString())
                .put("/users/"+userID)
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("message",Matchers.equalTo("Resource not found"));
    }

    @Test
    public void putUserWithInvalidUserID(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"testGetUserNotFoundSchema.json");
        JSONObject bodyObject = new JSONObject();
        String email = "banyu13@mailinator.com";
        String gender = "male";
        String name = "adaefrv";
        String status = "active";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .body(bodyObject.toString())
                .put("/users/!@#$!@$")
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("message",Matchers.equalTo("Resource not found"));
    }

    @Test
    public void putUserWithNoBody(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"succesCreateUserSchema.json");

        int userID = getFirstUserID();

        RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .put("/users/"+userID)
                .then()
                .assertThat().statusCode(200)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("id",Matchers.equalTo(userID));
    }

    @Test
    public void putUserWithEmptyData(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"failedCreateUserSchema.json");

        JSONObject bodyObject = new JSONObject();
        String email = "";
        String gender = "";
        String name = "";
        String status = "";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        int userID = getFirstUserID();

        RequestSpecification request =  RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .body(bodyObject.toString());
        Response response = request.when().put("users/"+userID);

        List<InvalidCreateUserSchema> items = response.jsonPath().getList("", InvalidCreateUserSchema.class);
        response.then().assertThat().statusCode(422)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file));

        int totalFieldEmail = 0;
        int totalFieldName = 0;
        int totalFieldStatus = 0;
        int totalFieldGender = 0;
        for(InvalidCreateUserSchema item: items){
            String message = item.getMessage();
            String field = item.getField();
            System.out.println("field = "+field);
            if(field.equals("email")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldEmail++;
            }
            else if(field.equals("name")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldName++;
            }
            else if(field.equals("status")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldStatus++;
            }
            else if(field.equals("gender")){
                Assertions.assertEquals("can't be blank, can be male of female",message);
                totalFieldGender++;
            }
            else{
                Assertions.fail("The field "+field+" is not recognized");
            }
        }

        if(!(totalFieldEmail == 1 && totalFieldGender == 1 && totalFieldName == 1 && totalFieldStatus == 1)){
            Assertions.fail("The field contain more field than expected");
        }
    }

    @Test
    public void putUserWithInvalidFormat(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"failedCreateUserSchema.json");

        JSONObject bodyObject = new JSONObject();
        String email = "!@$!%!@%";
        String gender = "!@#$!#!";
        String name = "!@$$!@$!%";
        String status = "!@$!%#";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        int userID = getFirstUserID();

        RequestSpecification request =  RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .body(bodyObject.toString());
        Response response = request.when().put("users/"+userID);


        List<InvalidCreateUserSchema> items = response.jsonPath().getList("", InvalidCreateUserSchema.class);
        response.then().assertThat().statusCode(422)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file));

        int totalFieldEmail = 0;
        int totalFieldStatus = 0;
        int totalFieldGender = 0;
        for(InvalidCreateUserSchema item: items){
            String message = item.getMessage();
            String field = item.getField();
            System.out.println("field = "+field);
            if(field.equals("email")){
                Assertions.assertEquals("is invalid",message);
                totalFieldEmail++;
            }
            else if(field.equals("status")){
                Assertions.assertEquals("can't be blank",message);
                totalFieldStatus++;
            }
            else if(field.equals("gender")){
                Assertions.assertEquals("can't be blank, can be male of female",message);
                totalFieldGender++;
            }
            else{
                Assertions.fail("The field "+field+" is not recognized");
            }
        }

        if(!(totalFieldEmail == 1 && totalFieldGender == 1 && totalFieldStatus == 1)){
            Assertions.fail("The field contain more field than expected");
        }
    }

    @Test
    public void putUser(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"succesCreateUserSchema.json");
        JSONObject bodyObject = new JSONObject();
        String email = "banyu24@mailinator.com";
        String gender = "male";
        String name = "nyoman2";
        String status = "active";
        bodyObject.put("name",name);
        bodyObject.put("gender",gender);
        bodyObject.put("email",email);
        bodyObject.put("status",status);

        RequestSpecification request = RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .body(bodyObject.toString());

        int userID = getFirstUserID();

        Response response = request.when().put("users/"+userID);

        response
                .then()
                .log().all()
                .assertThat().assertThat().statusCode(200)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("name",Matchers.equalTo(name))
                .assertThat().body("gender",Matchers.equalTo(gender))
                .assertThat().body("email",Matchers.equalTo(email))
                .assertThat().body("status",Matchers.equalTo(status));
    }

    @Test
    public void deleteUserWithNoAuth(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"testGetUserNotFoundSchema.json");

        int userID = getFirstUserID();

        RestAssured.given()
                .header("Content-Type","application/json")
                .delete("/users/"+userID)
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("message",Matchers.equalTo("Resource not found"));
    }

    @Test
    public void deleteUserWithInvalidUserID(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"testGetUserNotFoundSchema.json");

        RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .delete("/users/!@#$!@$")
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("message",Matchers.equalTo("Resource not found"));
    }

    @Test
    public void deleteUser(){
        int userID = getFirstUserID();
        RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .delete("/users/"+userID)
                .then()
                .assertThat().statusCode(204);
    }


    @Test
    public void getUserDetailWithNoAuth(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"testGetUserNotFoundSchema.json");

        int userID = getFirstUserID();

        RestAssured.given()
                .header("Content-Type","application/json")
                .get("/users/"+userID)
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("message",Matchers.equalTo("Resource not found"));
    }

    @Test
    public void getUserWithInvalidUserID(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"testGetUserNotFoundSchema.json");

        RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .get("/users/!@#$!@$")
                .then()
                .assertThat().statusCode(404)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("message",Matchers.equalTo("Resource not found"));
    }

    @Test
    public void getUserDetail(){
        File file = new File(Configuration.baseFilePathTestDataSchema+"succesCreateUserSchema.json");

        int userID = getFirstUserID();

        RestAssured.given()
                .header("Content-Type","application/json")
                .header("Authorization","Bearer "+Configuration.token)
                .get("/users/"+userID)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file))
                .assertThat().body("id",Matchers.equalTo(userID));
    }

}
