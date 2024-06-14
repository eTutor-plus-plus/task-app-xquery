package at.jku.dke.task_app.xquery.evaluation;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import at.jku.dke.task_app.xquery.evaluation.analysis.AnalysisException;
import at.jku.dke.task_app.xquery.evaluation.analysis.AnalysisImpl;
import at.jku.dke.task_app.xquery.evaluation.analysis.XQResult;
import at.jku.dke.task_app.xquery.evaluation.execution.InvalidDocumentLoadException;
import at.jku.dke.task_app.xquery.evaluation.execution.XQProcessor;
import at.jku.dke.task_app.xquery.evaluation.execution.XQueryException;
import at.jku.dke.task_app.xquery.evaluation.grading.XQueryGrading;
import at.jku.dke.task_app.xquery.evaluation.report.XQueryReport;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service that evaluates submissions.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EvaluationServiceImpl implements EvaluationService, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private final XQProcessor processor;
    private final XQueryTaskRepository taskRepository;
    private final MessageSource messageSource;

    /**
     * Creates a new instance of class {@link EvaluationServiceImpl}.
     *
     * @param processor      The XQuery processor.
     * @param taskRepository The task repository.
     * @param messageSource  The message source.
     */
    public EvaluationServiceImpl(XQProcessor processor, XQueryTaskRepository taskRepository, MessageSource messageSource) {
        this.processor = processor;
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
    }

    /**
     * Evaluates a input.
     *
     * @param submission The input to evaluate.
     * @return The evaluation result.
     * @throws EntityNotFoundException If the task does not exist.
     * @throws RuntimeException        If an error occurs during evaluation.
     * @throws IllegalStateException   If the executor is not supported.
     */
    @Override
    @Transactional
    public GradingDto evaluate(SubmitSubmissionDto<XQuerySubmissionDto> submission) {
        // find task
        var task = this.taskRepository.findByIdWithTaskGroup(submission.taskId())
            .orElseThrow(() -> new EntityNotFoundException("Task " + submission.taskId() + " does not exist."));

        // prepare
        LOG.info("Evaluating user ({}) input for task {} with mode {} and feedback-level {}",
            submission.userId(), submission.taskId(), submission.mode(), submission.feedbackLevel());
        Locale locale = Locale.of(submission.language());
        BigDecimal points = BigDecimal.ZERO;
        List<CriterionDto> criteria = new ArrayList<>();
        String xmlDocument = switch (submission.mode()) {
            case RUN, DIAGNOSE -> task.getTaskGroup().getDiagnoseDocument();
            case SUBMIT -> task.getTaskGroup().getSubmitDocument();
        };

        // execute
        String submissionResult;
        String solutionResult;
        try {
            // execute submission
            try {
                submissionResult = this.processor.executeQuery(submission.submission().input(), xmlDocument);
            } catch (InvalidDocumentLoadException ex) {
                LOG.warn("Error while executing query because of invalid document load", ex);
                criteria.add(new CriterionDto(
                    this.messageSource.getMessage("criterium.syntax", null, locale),
                    null,
                    false,
                    this.messageSource.getMessage("invalidDocument", null, locale)));
                return new GradingDto(task.getMaxPoints(), points, this.messageSource.getMessage("syntaxError", null, locale), criteria);
            } catch (XQueryException ex) {
                LOG.warn("Error while executing query", ex);
                String msg = ex.getMessage();
                if (msg.contains("BaseXException"))
                    msg = msg.substring(msg.indexOf(',') + 1);

                criteria.add(new CriterionDto(
                    this.messageSource.getMessage("criterium.syntax", null, locale),
                    null,
                    false,
                    msg));
                return new GradingDto(task.getMaxPoints(), points, this.messageSource.getMessage("syntaxError", null, locale), criteria);
            }

            // execute solution
            if (submission.mode() == SubmissionMode.RUN) {
                solutionResult = submissionResult;
            } else {
                try {
                    solutionResult = this.processor.executeQuery(task.getSolution(), xmlDocument);
                } catch (XQueryException ex) {
                    LOG.error("Error while executing query", ex);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not execute solution query.", ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("Could not execute query for task " + task.getId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not execute query.", ex);
        }

        // analyze, grade, feedback
        try {
            var analysis = new AnalysisImpl(new XQResult(submissionResult), new XQResult(solutionResult), task);
            var grading = new XQueryGrading(task, analysis);
            var report = new XQueryReport(this.messageSource, locale, submission.mode(), submission.feedbackLevel(), analysis, grading);
            return new GradingDto(task.getMaxPoints(), grading.getPoints(), report.getGeneralFeedback(), report.getCriteria());
        } catch (AnalysisException ex) {
            LOG.error("Could not analyze query result for task " + task.getId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not analyze query result.", ex);
        }
    }

    /**
     * Executes a query for the specified task.
     *
     * @param taskId The task identifier.
     * @param mode   The execution mode.
     * @param query  The query to execute.
     * @return The query result.
     * @throws EntityNotFoundException If the task does not exist.
     * @throws RuntimeException        If an error occurs during evaluation.
     * @throws IllegalStateException   If the executor is not supported.
     */
    @Override
    @Transactional
    public XQResult execute(long taskId, SubmissionMode mode, String query) {
        // find task
        var task = this.taskRepository.findByIdWithTaskGroup(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task " + taskId + " does not exist."));

        // prepare
        LOG.info("Executing query for task {} with mode {}", taskId, mode);
        String xmlDocument = switch (mode) {
            case RUN, DIAGNOSE -> task.getTaskGroup().getDiagnoseDocument();
            case SUBMIT -> task.getTaskGroup().getSubmitDocument();
        };

        // execute
        try {
            try {
                return new XQResult(this.processor.executeQuery(query, xmlDocument));
            } catch (InvalidDocumentLoadException ex) {
                LOG.warn("Error while executing query because of invalid document load", ex);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, this.messageSource.getMessage("invalidDocument", null, Locale.ENGLISH));
            } catch (XQueryException ex) {
                LOG.warn("Error while executing query", ex);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
            }
        } catch (Exception ex) {
            LOG.error("Could not execute query for task " + task.getId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not execute query.", ex);
        }
    }

    @Override
    public void close() throws Exception {
        this.processor.close();
    }
}
