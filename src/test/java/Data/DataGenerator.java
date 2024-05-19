package Data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private static final Faker faker = new Faker(new Locale("en"));

    private DataGenerator() {
    }

    private static void sendRequest(UserInfo user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

    }

    public static String generateName() {
        return faker.name().firstName();
    }

    public static String generatePassword() {
        return faker.internet().password();
    }

    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateUser(String status) {
            return new UserInfo(generateName(), generatePassword(), status);
        }

        public static UserInfo changeUserInfo(String name, String password, String status) {
            return new UserInfo(name, password, status);
        }

        public static UserInfo getRegisteredUser(String status) {
            UserInfo user = generateUser(status);
            sendRequest(user);
            return user;
        }

        public static void updateUserStatus(UserInfo user, String status) {
            UserInfo updateUser = changeUserInfo(user.getLogin(), user.getPassword(), status);
            sendRequest(updateUser);
        }

        public static void updateUserPassword(UserInfo user, String password) {
            UserInfo updateUser = changeUserInfo(user.getLogin(), password, user.getStatus());
            sendRequest(updateUser);
        }
    }

    @Value
    public static class UserInfo {
        String login;
        String password;
        String status;
    }
}