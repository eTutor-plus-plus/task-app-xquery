package at.jku.dke.task_app.xquery.controllers;

import at.jku.dke.etutor.task_app.auth.AuthConstants;
import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.xquery.ClientSetupExtension;
import at.jku.dke.task_app.xquery.DatabaseSetupExtension;
import at.jku.dke.task_app.xquery.data.entities.GradingStrategy;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskGroupRepository;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({DatabaseSetupExtension.class, ClientSetupExtension.class})
class TaskControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private XQueryTaskRepository repository;

    @Autowired
    private XQueryTaskGroupRepository groupRepository;

    private long taskId;
    private long taskGroupId;

    @BeforeEach
    void initDb() {
        this.repository.deleteAll();
        this.groupRepository.deleteAll();

        var group = this.groupRepository.save(new XQueryTaskGroup(1L, TaskStatus.APPROVED, "<db><solution>1</solution></db>", "<db><solution>2</solution></db>"));
        this.taskGroupId = group.getId();
        this.taskId = this.repository.save(new XQueryTask(1L, BigDecimal.TWO, TaskStatus.APPROVED, group, "//solution", List.of("//sorting", "/node"))).getId();
    }

    //#region --- GET ---
    @Test
    void getShouldReturnOk() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .accept(ContentType.JSON)
            // WHEN
            .when()
            .get("/api/task/{id}", this.taskId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("solution", equalTo("//solution"))
            .body("sorting", equalTo("//sorting\n/node"));
    }

    @Test
    void getShouldReturnNotFound() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .accept(ContentType.JSON)
            // WHEN
            .when()
            .get("/api/task/{id}", this.taskId + 1)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(404);
    }

    @Test
    void getShouldReturnForbidden() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.SUBMIT_API_KEY)
            .accept(ContentType.JSON)
            // WHEN
            .when()
            .get("/api/task/{id}", this.taskId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(403);
    }
    //#endregion

    //#region --- CREATE ---
    @Test
    void createShouldReturnCreated() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskDto<>(this.taskGroupId, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyXQueryTaskDto("//newSolution", "//newSorting")))
            // WHEN
            .when()
            .post("/api/task/{id}", this.taskId + 2)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .header("Location", containsString("/api/task/" + (this.taskId + 2)));
    }

    @Test
    void createShouldReturnBadRequestOnInvalidBody() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskDto<>(this.taskGroupId, BigDecimal.TEN, "", TaskStatus.APPROVED, new ModifyXQueryTaskDto("//newSolution", "//newSorting")))
            // WHEN
            .when()
            .post("/api/task/{id}", this.taskId + 2)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    void createShouldReturnBadRequestOnEmptyBody() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            // WHEN
            .when()
            .post("/api/task/{id}", this.taskId + 2)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    void createShouldReturnForbidden() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.SUBMIT_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskDto<>(this.taskGroupId, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyXQueryTaskDto("//newSolution", "//newSorting")))
            // WHEN
            .when()
            .post("/api/task/{id}", this.taskId + 2)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(403);
    }
    //#endregion

    //#region --- UPDATE ---
    @Test
    void updateShouldReturnOk() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskDto<>(this.taskGroupId, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyXQueryTaskDto("//newSolution", "//newSorting")))
            // WHEN
            .when()
            .put("/api/task/{id}", this.taskId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    void updateShouldReturnNotFound() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskDto<>(this.taskGroupId, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyXQueryTaskDto("//newSolution", "//newSorting")))
            // WHEN
            .when()
            .put("/api/task/{id}", this.taskId + 1)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(404);
    }

    @Test
    void updateShouldReturnBadRequestOnInvalidBody() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskDto<>(this.taskGroupId, BigDecimal.TEN, "sql", TaskStatus.APPROVED, new ModifyXQueryTaskDto("//newSolution", "//newSorting")))
            // WHEN
            .when()
            .put("/api/task/{id}", this.taskId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    void updateShouldReturnBadRequestOnEmptyBody() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            // WHEN
            .when()
            .put("/api/task/{id}", this.taskId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    void updateShouldReturnForbidden() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.SUBMIT_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskDto<>(this.taskGroupId, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyXQueryTaskDto("//newSolution", "//newSorting")))
            // WHEN
            .when()
            .put("/api/task/{id}", this.taskId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(403);
    }
    //#endregion

    //#region --- DELETE ---
    @Test
    void deleteShouldReturnNoContent() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            // WHEN
            .when()
            .delete("/api/task/{id}", this.taskId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(204);
    }

    @Test
    void deleteShouldReturnNoContentOnNotFound() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            // WHEN
            .when()
            .delete("/api/task/{id}", this.taskId + 1)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(204);
    }

    @Test
    void deleteShouldReturnForbidden() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.SUBMIT_API_KEY)
            // WHEN
            .when()
            .delete("/api/task/{id}", this.taskId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(403);
    }
    //#endregion

    @Test
    void mapToDto() {
        // Arrange
        var task = new XQueryTask("//newSolution", List.of("//newSorting"));
        task.setMissingNodePenalty(BigDecimal.ONE);
        task.setMissingNodeStrategy(GradingStrategy.KO);
        task.setSuperfluousNodePenalty(BigDecimal.TWO);
        task.setSuperfluousNodeStrategy(GradingStrategy.GROUP);
        task.setDisplacedNodePenalty(BigDecimal.ZERO);
        task.setDisplacedNodeStrategy(GradingStrategy.EACH);
        task.setIncorrectTextPenalty(BigDecimal.valueOf(100));
        task.setIncorrectTextStrategy(GradingStrategy.EACH);
        task.setMissingAttributePenalty(BigDecimal.valueOf(200));
        task.setMissingAttributeStrategy(GradingStrategy.EACH);
        task.setSuperfluousAttributePenalty(BigDecimal.valueOf(300));
        task.setSuperfluousAttributeStrategy(GradingStrategy.EACH);
        task.setIncorrectAttributeValuePenalty(BigDecimal.valueOf(400));
        task.setIncorrectAttributeValueStrategy(GradingStrategy.EACH);


        // Act
        var result = new TaskController(null).mapToDto(task);

        // Assert
        assertEquals("//newSolution", result.solution());
        assertEquals("//newSorting", result.sorting());
        assertEquals(BigDecimal.ONE, result.missingNodePenalty());
        assertEquals(GradingStrategy.KO, result.missingNodeStrategy());
        assertEquals(BigDecimal.TWO, result.superfluousNodePenalty());
        assertEquals(GradingStrategy.GROUP, result.superfluousNodeStrategy());
        assertEquals(BigDecimal.ZERO, result.displacedNodePenalty());
        assertEquals(GradingStrategy.EACH, result.displacedNodeStrategy());
        assertEquals(BigDecimal.valueOf(100), result.incorrectTextPenalty());
        assertEquals(GradingStrategy.EACH, result.incorrectTextStrategy());
        assertEquals(BigDecimal.valueOf(200), result.missingAttributePenalty());
        assertEquals(GradingStrategy.EACH, result.missingAttributeStrategy());
        assertEquals(BigDecimal.valueOf(300), result.superfluousAttributePenalty());
        assertEquals(GradingStrategy.EACH, result.superfluousAttributeStrategy());
        assertEquals(BigDecimal.valueOf(400), result.incorrectAttributeValuePenalty());
        assertEquals(GradingStrategy.EACH, result.incorrectAttributeValueStrategy());
    }

}
