package at.jku.dke.task_app.xquery.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseSubmissionController;
import at.jku.dke.task_app.xquery.data.entities.XQuerySubmission;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import at.jku.dke.task_app.xquery.services.XQuerySubmissionService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link XQuerySubmission}s.
 */
@RestController
public class SubmissionController extends BaseSubmissionController<XQuerySubmissionDto> {
    /**
     * Creates a new instance of class {@link SubmissionController}.
     *
     * @param submissionService The input service.
     */
    public SubmissionController(XQuerySubmissionService submissionService) {
        super(submissionService);
    }
}
