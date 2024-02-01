package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.etutor.task_app.services.BaseSubmissionService;
import at.jku.dke.task_app.xquery.data.entities.XQuerySubmission;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.data.repositories.XQuerySubmissionRepository;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import at.jku.dke.task_app.xquery.evaluation.EvaluationService;
import org.springframework.stereotype.Service;

/**
 * This class provides methods for managing {@link XQuerySubmission}s.
 */
@Service
public class XQuerySubmissionService extends BaseSubmissionService<XQueryTask, XQuerySubmission, XQuerySubmissionDto> {

    private final EvaluationService evaluationService;

    /**
     * Creates a new instance of class {@link XQuerySubmissionService}.
     *
     * @param submissionRepository The input repository.
     * @param taskRepository       The task repository.
     * @param evaluationService    The evaluation service.
     */
    public XQuerySubmissionService(XQuerySubmissionRepository submissionRepository, XQueryTaskRepository taskRepository, EvaluationService evaluationService) {
        super(submissionRepository, taskRepository);
        this.evaluationService = evaluationService;
    }

    @Override
    protected XQuerySubmission createSubmissionEntity(SubmitSubmissionDto<XQuerySubmissionDto> submitSubmissionDto) {
        return new XQuerySubmission(submitSubmissionDto.submission().input());
    }

    @Override
    protected GradingDto evaluate(SubmitSubmissionDto<XQuerySubmissionDto> submitSubmissionDto) {
        return this.evaluationService.evaluate(submitSubmissionDto);
    }

    @Override
    protected XQuerySubmissionDto mapSubmissionToSubmissionData(XQuerySubmission submission) {
        return new XQuerySubmissionDto(submission.getSubmission());
    }

}
