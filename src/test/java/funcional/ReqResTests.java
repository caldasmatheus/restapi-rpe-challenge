package funcional;

import static utils.JsonUtils.convertToJson;

import dataprovider.ReqresDataProvider;
import dto.UserCreateAndLoginDTO;
import dto.UserDTO;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import spec.RequestSpecification;
import utils.TestRule;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.github.javafaker.Faker;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

import static org.apache.http.HttpStatus.*;

public class ReqResTests extends TestRule {

    @Test
    public void testGetListUsers() {
        given()
            .spec(RequestSpecification.getSpec())
        .when()
                .get("/users")
        .then()
            .statusCode(SC_OK)
            .body("data", hasSize(greaterThan(0)))
            .body("data[0].id", notNullValue())
            .body("data[0].email", notNullValue())
            .body("data[0].first_name", notNullValue())
            .body("data[0].last_name", notNullValue());
    }

    @Test
    public void testGetListUsersWithPagination() {
        int[] idsEsperados = {7, 8, 9, 10, 11, 12};
        String[] nomesEsperados = {"Michael", "Lindsay", "Tobias", "Byron", "George", "Rachel"};

        Response response = given()
                .spec(RequestSpecification.getSpec())
                .queryParam("page", 2)
                .queryParam("per_page", 6)
            .when()
                .get("/users")
            .then()
                .statusCode(SC_OK)
                .body("page", equalTo(2))
                .body("per_page", equalTo(6))
                .body("data", hasSize(6))
                .body("data[0].id", notNullValue())
                .body("data[0].first_name", notNullValue())
                .extract().response();

        java.util.List<Integer> ids = response.jsonPath().getList("data.id");
        java.util.List<String> nomes = response.jsonPath().getList("data.first_name");

        for (int i = 0; i < idsEsperados.length; i++) {
            assertThat("ID na posição " + i, ids.get(i), equalTo(idsEsperados[i]));
            assertThat("Nome na posição " + i, nomes.get(i), equalTo(nomesEsperados[i]));
        }
    }

    @Test
    public void testGetSingleUser() {
        given()
            .spec(RequestSpecification.getSpec())
            .pathParam("userId", 2)
        .when()
            .get("/users/{userId}")
        .then()
            .statusCode(SC_OK)
            .body("data.id", equalTo(2))
            .body("data.first_name", equalTo("Janet"));
    }

    @Test
    public void testGetSingleUserNotFound() {
        int idInexistente = 999999;

        Response response = given()
                .spec(RequestSpecification.getSpec())
                .pathParam("userId", idInexistente)
            .when()
                .get("/users/{userId}")
            .then()
                .statusCode(SC_NOT_FOUND)
                .extract().response();

        assertThat("A resposta deve ser um objeto vazio {}", response.asString(), equalTo("{}"));
    }

    @Test
    public void testGetListResources() {
        int[] idsEsperados = {1, 2, 3, 4, 5, 6};
        String[] nomesEsperados = {"cerulean", "fuchsia rose", "true red", "aqua sky", "tigerlily", "blue turquoise"};

        Response response = given()
                .spec(RequestSpecification.getSpec())
            .when()
                .get("/unknown")
            .then()
                .statusCode(SC_OK)
                .body("page", equalTo(1))
                .body("per_page", equalTo(6))
                .body("data", hasSize(greaterThanOrEqualTo(6)))
                .body("data[0].id", notNullValue())
                .body("data[0].name", notNullValue())
                .extract().response();

        List<Integer> ids = response.jsonPath().getList("data.id");
        List<String> nomes = response.jsonPath().getList("data.name");

        for (int i = 0; i < idsEsperados.length; i++) {
            assertThat("ID na posição " + i, ids.get(i), equalTo(idsEsperados[i]));
            assertThat("Nome na posição " + i, nomes.get(i), equalTo(nomesEsperados[i]));
        }
    }

    @Test
    public void testGetSingleResource() {
        Response response = given()
                .spec(RequestSpecification.getSpec())
                .pathParam("resourceId", 2)
            .when()
                .get("/unknown/{resourceId}")
            .then()
                .statusCode(SC_OK)
                .body("data", notNullValue())
                .body("data.id", equalTo(2))
                .body("data.name", notNullValue())
                .body("data.year", notNullValue())
                .body("data.color", notNullValue())
                .body("data.pantone_value", notNullValue())
                .extract().response();

        assertThat("ID deve ser igual ao solicitado", response.jsonPath().getInt("data.id"), equalTo(2));
        assertThat("Nome deve estar presente", response.jsonPath().getString("data.name"), equalTo("fuchsia rose"));
        assertThat("Ano deve ser 2001", response.jsonPath().getInt("data.year"), equalTo(2001));
        assertThat("Cor deve ser #C74375", response.jsonPath().getString("data.color"), equalTo("#C74375"));
        assertThat("Pantone Value deve ser 17-2031", response.jsonPath().getString("data.pantone_value"), equalTo("17-2031"));
    }

    @Test
    public void testGetSingleResourceNotFound() {
        Response response = given()
                .spec(RequestSpecification.getSpec())
                .pathParam("resourceId", 23)
            .when()
                .get("/unknown/{resourceId}")
            .then()
                .statusCode(SC_NOT_FOUND)
                .extract().response();

        assertThat("Corpo da resposta deve ser um objeto vazio {}", response.asString(), equalTo("{}"));
    }

    @Test(dataProvider = "creatingUser", dataProviderClass = ReqresDataProvider.class)
    public void testPostCreateUser(UserDTO user) {
        String requestBody = convertToJson(user);

        Response response = given()
                .spec(RequestSpecification.getSpec())
                .body(requestBody)
            .when()
                .post("/users")
            .then()
                .statusCode(SC_CREATED)
                .body("name", equalTo(user.name()))
                .body("job", equalTo(user.job()))
                .body("id", not(emptyOrNullString()))
                .body("createdAt", not(emptyOrNullString()))
                .extract().response();

        String idCriado = response.jsonPath().getString("id");
        String createdAt = response.jsonPath().getString("createdAt");

        assertThat("ID gerado não deve ser nulo ou vazio", idCriado, not(emptyOrNullString()));
        assertThat("Campo createdAt não deve ser nulo", createdAt, notNullValue());
    }

    @Test(dataProvider = "updateUser", dataProviderClass = ReqresDataProvider.class)
    public void testPutUpdateUser(UserDTO user) {
        String requestBody = convertToJson(user);

        given()
                .spec(RequestSpecification.getSpec())
                .pathParam("userId", 2)
                .body(requestBody)
            .when()
                .put("/users/{userId}")
            .then()
                .statusCode(SC_OK)
                .body("name", equalTo(user.name()))
                .body("job", equalTo(user.job()))
                .body("updatedAt", notNullValue());
    }

    @Test
    public void testPatchUpdateUser() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String job = faker.job().title();

        String requestBody = "{\"name\":\"" + name + "\",\"job\":\"" + job + "\"}";

        Response response = given()
                .spec(RequestSpecification.getSpec())
                .pathParam("userId", 2)
                .body(requestBody)
            .when()
                .patch("/users/{userId}")
            .then()
                .statusCode(SC_OK)
                .extract().response();

        assertThat("O nome retornado deve ser o mesmo enviado", response.jsonPath().getString("name"), equalTo(name));
        assertThat("O job retornado deve ser o mesmo enviado", response.jsonPath().getString("job"), equalTo(job));
        assertThat("O campo updatedAt não deve ser nulo", response.jsonPath().getString("updatedAt"), notNullValue());
        assertThat("A data de updatedAt deve estar no formato ISO8601", response.jsonPath().getString("updatedAt"), matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z")
        );
    }

    @Test
    public void testDeleteRandomUser() {
        Response listaUsuarios = given()
                .spec(RequestSpecification.getSpec())
                .queryParam("page", 1)
            .when()
                .get("/users")
            .then()
                .statusCode(SC_OK)
                .extract().response();

        List<Integer> listaIds = listaUsuarios.jsonPath().getList("data.id");

        assertThat("Deve existir pelo menos um usuário", listaIds, is(not(empty())));

        Random rand = new Random();
        int indexAleatorio = rand.nextInt(listaIds.size());
        int idParaDeletar = listaIds.get(indexAleatorio);

        Response respostaDelete = given()
                .spec(RequestSpecification.getSpec())
                .pathParam("userId", idParaDeletar)
            .when()
                .delete("/users/{userId}")
            .then()
                .statusCode(SC_NO_CONTENT)
                .extract().response();

        assertThat("Resposta do delete deve estar vazia", respostaDelete.asString(), isEmptyString());
    }

    @Test(dataProvider = "registerSuccessful", dataProviderClass = ReqresDataProvider.class)
    public void testPostRegisterSuccessful(UserCreateAndLoginDTO registerData) {
        String requestBody = convertToJson(registerData);

        Response response = given()
                .spec(RequestSpecification.getSpec())
                .body(requestBody)
            .when()
                .post("/register")
            .then()
                .statusCode(SC_OK)
                .body("id", notNullValue())
                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .extract().response();

        String token = response.jsonPath().getString("token");
        Integer id = response.jsonPath().getInt("id");

        assertThat("Token deve estar presente", token, notNullValue());
        assertThat("ID deve ser fornecido", id, notNullValue());
    }

    @Test(dataProvider = "registerUnsuccessful", dataProviderClass = ReqresDataProvider.class)
    public void testPostRegisterUnsuccessful(UserCreateAndLoginDTO registerData) {
        String requestBody = convertToJson(registerData);

        Response response = given()
                .spec(RequestSpecification.getSpec())
                .body(requestBody)
            .when()
                .post("/register")
            .then()
                .statusCode(SC_BAD_REQUEST)
                .body("error", equalTo("Missing password"))
                .extract().response();

        String errorMsg = response.jsonPath().getString("error");
        assertThat("Mensagem de erro deve ser 'Missing password'", errorMsg, equalTo("Missing password"));
    }

    @Test(dataProvider = "loginSuccessful", dataProviderClass = ReqresDataProvider.class)
    public void testPostLoginSuccessful(UserCreateAndLoginDTO loginData) {
        String requestBody = convertToJson(loginData);

        Response response = given()
                .spec(RequestSpecification.getSpec())
                .body(requestBody)
            .when()
                .post("/login")
            .then()
                .statusCode(SC_OK)
                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .extract().response();

        String token = response.jsonPath().getString("token");
        assertThat("Token retornado deve ser 'QpwL5tke4Pnpja7X4'", token, equalTo("QpwL5tke4Pnpja7X4"));
    }

    @Test(dataProvider = "loginUnsuccessful", dataProviderClass = ReqresDataProvider.class)
    public void testPostLoginUnsuccessful(UserCreateAndLoginDTO loginData) {
        String requestBody = convertToJson(loginData);

        Response response = given()
                .spec(RequestSpecification.getSpec())
                .body(requestBody)
            .when()
                .post("/login")
            .then()
                .statusCode(SC_BAD_REQUEST)
                .body("error", equalTo("Missing password"))
                .extract().response();

        String errorMsg = response.jsonPath().getString("error");
        assertThat("Mensagem de erro deve ser 'Missing password'", errorMsg, equalTo("Missing password"));
    }

    @Test
    public void testGetDelayedResponseAndData() {
        int delaySeconds = 3;
        Response response = given()
                .spec(RequestSpecification.getSpec())
                .queryParam("delay", delaySeconds)
            .when()
                .get("/users")
            .then()
                .statusCode(SC_OK)
                .body("page", equalTo(1))
                .body("per_page", equalTo(6))
                .body("data", notNullValue())
                .body("data", hasSize(6))
                .extract()
                .response();

        long elapsedTime = response.getTimeIn(TimeUnit.SECONDS);
        assertThat("Tempo de resposta deve ser maior ou igual ao delay configurado", elapsedTime, greaterThanOrEqualTo((long) delaySeconds));

        List<Integer> ids = response.jsonPath().getList("data.id");
        List<String> firstNames = response.jsonPath().getList("data.first_name");

        int[] expectedIds = {1, 2, 3, 4, 5, 6};
        String[] expectedNames = {"George", "Janet", "Emma", "Eve", "Charles", "Tracey"};

        for (int i = 0; i < expectedIds.length; i++) {
            assertThat("ID na posição " + i, ids.get(i), equalTo(expectedIds[i]));
            assertThat("First name na posição " + i, firstNames.get(i), equalTo(expectedNames[i]));
        }
    }
}