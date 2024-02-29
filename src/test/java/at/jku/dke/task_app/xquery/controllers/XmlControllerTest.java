package at.jku.dke.task_app.xquery.controllers;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.xquery.ClientSetupExtension;
import at.jku.dke.task_app.xquery.DatabaseSetupExtension;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskGroupRepository;
import at.jku.dke.task_app.xquery.services.HashIds;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({DatabaseSetupExtension.class, ClientSetupExtension.class})
class XmlControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private XQueryTaskGroupRepository groupRepository;

    private long taskGroupId;

    @BeforeEach
    void initDb() {
        this.groupRepository.deleteAll();

        var group = this.groupRepository.save(new XQueryTaskGroup(1L, TaskStatus.APPROVED, "<db><solution>1</solution></db>", "<db><solution>2</solution></db>"));
        this.taskGroupId = group.getId();
    }

    @Test
    void getFacts_exists() {
        given()
            .port(port)
            // WHEN
            .when()
            .get("/xml/{id}", HashIds.encode(this.taskGroupId))
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.XML)
            .body(equalTo("<db><solution>1</solution></db>"));
    }

    @Test
    void getFacts_notExists() {
        given()
            .port(port)
            // WHEN
            .when()
            .get("/xml/{id}", HashIds.encode(this.taskGroupId + 1))
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(404);
    }

}
