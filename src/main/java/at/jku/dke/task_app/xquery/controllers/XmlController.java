package at.jku.dke.task_app.xquery.controllers;

import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskGroupRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sqids.Sqids;

/**
 * Controller for managing {@link XQueryTaskGroup}s.
 */
@RestController
@RequestMapping({"/xml"})
@Tag(name = "XML", description = "Load XML documents")
public class XmlController {

    private final XQueryTaskGroupRepository taskGroupRepository;

    /**
     * Creates a new instance of class {@link XmlController}.
     *
     * @param taskGroupRepository The task group repository.
     */
    public XmlController(XQueryTaskGroupRepository taskGroupRepository) {
        this.taskGroupRepository = taskGroupRepository;
    }

    /**
     * Returns the diagnose document of the task group with the given ID.
     *
     * @param id The hashed ID of the task group.
     * @return The diagnose document.
     */
    @GetMapping(value = "{id}", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> getDiagnoseDocument(@PathVariable String id) {
        long taskGroupId = Sqids.builder().minLength(4).build().decode(id).getFirst();
        XQueryTaskGroup taskGroup = this.taskGroupRepository.findById(taskGroupId).orElseThrow(() -> new EntityNotFoundException("XML Document not found"));
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_XML)
            .body(taskGroup.getDiagnoseDocument());
    }

}
