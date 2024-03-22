package at.jku.dke.task_app.xquery.evaluation;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that evaluates submissions.
 */
public interface EvaluationService {
    /**
     * Evaluates a input.
     *
     * @param submission The input to evaluate.
     * @return The evaluation result.
     * @throws EntityNotFoundException If the task does not exist.
     * @throws RuntimeException        If an error occurs during evaluation.
     * @throws IllegalStateException   If the executor is not supported.
     */
    @Transactional
    GradingDto evaluate(SubmitSubmissionDto<XQuerySubmissionDto> submission);
}
