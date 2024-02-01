package at.jku.dke.task_app.xquery.evaluation;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.xquery.config.XQuerySettings;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import at.jku.dke.task_app.xquery.services.XmlFileNameHelper;
import jakarta.persistence.EntityNotFoundException;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service that evaluates submissions.
 */
@Service
public class EvaluationService {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationService.class);

    private final XQuerySettings settings;
    private final XQueryTaskRepository taskRepository;
    private final Processor processor;
    private final XQueryCompiler compiler;
    private final MessageSource messageSource;

    /**
     * Creates a new instance of class {@link EvaluationService}.
     *
     * @param settings       The XQuery settings.
     * @param taskRepository The task repository.
     * @param messageSource  The message source.
     */
    public EvaluationService(XQuerySettings settings, XQueryTaskRepository taskRepository, MessageSource messageSource) {
        this.settings = settings;
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
        this.processor = new Processor(false);
        this.compiler = this.processor.newXQueryCompiler();
        this.compiler.setBaseURI(Path.of(this.settings.xmlFilesDirectory()).toAbsolutePath().toUri());
    }

    /**
     * Evaluates a input.
     *
     * @param submission The input to evaluate.
     * @return The evaluation result.
     */
    @Transactional
    public GradingDto evaluate(SubmitSubmissionDto<XQuerySubmissionDto> submission) {
        // find task
        var task = this.taskRepository.findById(submission.taskId()).orElseThrow(() -> new EntityNotFoundException("Task " + submission.taskId() + " does not exist."));

        // evaluate input
        LOG.info("Evaluating input for task {} with mode {} and feedback-level {}", submission.taskId(), submission.mode(), submission.feedbackLevel());
        Locale locale = Locale.of(submission.language());
        BigDecimal points = BigDecimal.ZERO;
        List<CriterionDto> criteria = new ArrayList<>();
        String feedback = this.messageSource.getMessage("incorrect", null, locale);
        String xmlFileName = switch (submission.mode()) {
            case RUN, DIAGNOSE -> XmlFileNameHelper.getDiagnoseFileName(task.getTaskGroup().getId());
            case SUBMIT -> XmlFileNameHelper.getSubmitFileName(task.getTaskGroup().getId());
        };

        // execute submission
        String submissionResult = null;
        try {
            submissionResult = this.executeQuery(submission.submission().input().replaceAll("'etutor.xml'", String.format("'%s'", xmlFileName)));
        } catch (SaxonApiException ex) {
            LOG.warn("Error while executing query", ex);
            criteria.add(new CriterionDto(
                this.messageSource.getMessage("criterium.syntax", null, locale),
                null,
                false,
                ex.getMessage()));
        }
        if (!criteria.isEmpty()) { // abort if submission contains syntax error
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO,
                this.messageSource.getMessage("incorrect", null, locale), criteria);
        }

        // execute solution
        String solutionResult = null;
        try {
            solutionResult = this.executeQuery(task.getSolution().replaceAll("'etutor.xml'", String.format("'%s'", xmlFileName)));
        } catch (SaxonApiException ex) {
            LOG.error("Error while executing query", ex);
            throw new RuntimeException(ex);
        }

        // analyze
        criteria.add(new CriterionDto(
            "Ausgabe",
            null,
            false,
            submissionResult));
        return new GradingDto(task.getMaxPoints(), points, feedback, criteria);
    }

    /**
     * Executes the specified query.
     *
     * @param query The query to execute.
     * @return The result of the query.
     * @throws SaxonApiException If the query compilation fails with a static error or the execution of the query fails with a dynamic error.
     */
    private String executeQuery(String query) throws SaxonApiException {
        LOG.debug("Executing query: {}", query);
        XQueryExecutable executable = this.compiler.compile(query);
        XQueryEvaluator evaluator = executable.load();
        XdmValue result = evaluator.evaluate();
        return result.toString();
    }
}
