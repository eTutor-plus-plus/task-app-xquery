package at.jku.dke.task_app.xquery.evaluation;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.xquery.config.XQuerySettings;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import at.jku.dke.task_app.xquery.evaluation.analysis.AnalysisException;
import at.jku.dke.task_app.xquery.evaluation.analysis.AnalysisImpl;
import at.jku.dke.task_app.xquery.evaluation.analysis.XQResult;
import at.jku.dke.task_app.xquery.evaluation.execution.*;
import at.jku.dke.task_app.xquery.evaluation.grading.XQueryGrading;
import at.jku.dke.task_app.xquery.evaluation.report.XQueryReport;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service that evaluates submissions.
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private final XQuerySettings settings;
    private final XQueryTaskRepository taskRepository;
    private final MessageSource messageSource;

    /**
     * Creates a new instance of class {@link EvaluationServiceImpl}.
     *
     * @param settings       The XQuery settings.
     * @param taskRepository The task repository.
     * @param messageSource  The message source.
     */
    public EvaluationServiceImpl(XQuerySettings settings, XQueryTaskRepository taskRepository, MessageSource messageSource) {
        this.settings = settings;
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
        LOG.info("Evaluating input for task {} with mode {} and feedback-level {}", submission.taskId(), submission.mode(), submission.feedbackLevel());
        Locale locale = Locale.of(submission.language());
        BigDecimal points = BigDecimal.ZERO;
        List<CriterionDto> criteria = new ArrayList<>();
        String xmlDocument = switch (submission.mode()) {
            case RUN, DIAGNOSE -> task.getTaskGroup().getDiagnoseDocument();
            case SUBMIT -> task.getTaskGroup().getSubmitDocument();
        };
        XQProcessor processor = switch (this.settings.executor()) {
            case "saxon" -> //noinspection resource
                new SaxonProcessor(Path.of(this.settings.xmlDirectory()));
            case "basex" -> //noinspection resource
                new BaseXProcessor(Path.of(this.settings.xmlDirectory()));
            default -> throw new IllegalStateException("Unexpected executor: " + this.settings.executor());
        };

        // execute
        String submissionResult;
        String solutionResult;
        try (processor) {
            // execute submission
            try {
                submissionResult = processor.executeQuery(submission.submission().input(), xmlDocument);
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
                criteria.add(new CriterionDto(
                    this.messageSource.getMessage("criterium.syntax", null, locale),
                    null,
                    false,
                    ex.getMessage()));
                return new GradingDto(task.getMaxPoints(), points, this.messageSource.getMessage("syntaxError", null, locale), criteria);
            }

            // execute solution
            if (submission.mode() == SubmissionMode.RUN) {
                solutionResult = submissionResult;
            } else {
                try {
                    solutionResult = processor.executeQuery(task.getSolution(), xmlDocument);
                } catch (XQueryException ex) {
                    LOG.error("Error while executing query", ex);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not execute solution query.", ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("Could not close processor for task " + task.getId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not execute query.", ex);
        }

        // analyze, grade, feedback
        try {
            var analysis = new AnalysisImpl(new XQResult(submissionResult), new XQResult(solutionResult), task.getSorting());
            var grading = new XQueryGrading(task, analysis);
            var report = new XQueryReport(this.messageSource, locale, submission.mode(), submission.feedbackLevel(), analysis, grading);
            return new GradingDto(task.getMaxPoints(), grading.getPoints(), report.getGeneralFeedback(), report.getCriteria());
        } catch (AnalysisException ex) {
            LOG.error("Could not analyze query result for task " + task.getId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not analyze query result.", ex);
        }
    }
}
