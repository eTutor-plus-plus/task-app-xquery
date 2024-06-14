package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.*;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskGroupRepository;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskDto;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import at.jku.dke.task_app.xquery.evaluation.EvaluationService;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.transform.dom.DOMSource;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides methods for managing {@link XQueryTask}s.
 */
@Service
public class XQueryTaskService extends BaseTaskInGroupService<XQueryTask, XQueryTaskGroup, ModifyXQueryTaskDto> {

    private final EvaluationService evaluationService;

    /**
     * Creates a new instance of class {@link XQueryTaskService}.
     *
     * @param repository          The task repository.
     * @param taskGroupRepository The task group repository.
     * @param evaluationService   The evaluation service.
     */
    public XQueryTaskService(XQueryTaskRepository repository, XQueryTaskGroupRepository taskGroupRepository, EvaluationService evaluationService) {
        super(repository, taskGroupRepository);
        this.evaluationService = evaluationService;
    }

    @Override
    protected XQueryTask createTask(long id, ModifyTaskDto<ModifyXQueryTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("xquery"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        var task = new XQueryTask(modifyTaskDto.additionalData().solution(), stringToList(modifyTaskDto.additionalData().sorting()));
        setPenaltyProperties(task, modifyTaskDto);
        return task;
    }

    @Override
    protected void updateTask(XQueryTask task, ModifyTaskDto<ModifyXQueryTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("xquery"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        task.setSolution(modifyTaskDto.additionalData().solution());
        task.setSorting(stringToList(modifyTaskDto.additionalData().sorting()));
        setPenaltyProperties(task, modifyTaskDto);
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(XQueryTask task, boolean create) {
        return new TaskModificationResponseDto(null, null);
    }

    @Override
    protected void afterCreate(XQueryTask task, ModifyTaskDto<ModifyXQueryTaskDto> dto) {
        // check query does not return empty result
        var queryResult = this.evaluationService.execute(task.getId(), SubmissionMode.DIAGNOSE, task.getSolution());
        if (queryResult.getRawResult().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query returns empty result for DIAGNOSE document");

        queryResult = this.evaluationService.execute(task.getId(), SubmissionMode.SUBMIT, task.getSolution());
        if (queryResult.getRawResult().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query returns empty result for SUBMIT document");

        // check grading
        var result = this.evaluationService.evaluate(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.DIAGNOSE, 3, new XQuerySubmissionDto(task.getSolution())));
        if (!result.points().equals(result.maxPoints()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, convertGradingDtoToString(result));

        // get element and attribute names
        try {
            var processor = new Processor(false);
            var doc = processor.newDocumentBuilder().build(new DOMSource(queryResult.getResultDocument()));

            var xpathResult = processor.newXPathCompiler().evaluate("distinct-values(//*/name())", doc);
            task.setSolutionElements(xpathResult.stream().map(XdmItem::getStringValue).toList());

            xpathResult = processor.newXPathCompiler().evaluate("distinct-values(//*//@*/name())", doc);
            task.setSolutionAttributes(xpathResult.stream().map(XdmItem::getStringValue).toList());

            this.repository.save(task);
        } catch (SaxonApiException ex) {
            LOG.error("Could not determine elements/attributes in solution result", ex);
        }
    }

    @Override
    protected void afterUpdate(XQueryTask task, ModifyTaskDto<ModifyXQueryTaskDto> dto) {
        this.afterCreate(task, dto);
    }

    private static void setPenaltyProperties(XQueryTask task, ModifyTaskDto<ModifyXQueryTaskDto> modifyTaskDto) {
        task.setMissingNodePenalty(modifyTaskDto.additionalData().missingNodePenalty());
        task.setMissingNodeStrategy(modifyTaskDto.additionalData().missingNodeStrategy());
        task.setSuperfluousNodePenalty(modifyTaskDto.additionalData().superfluousNodePenalty());
        task.setSuperfluousNodeStrategy(modifyTaskDto.additionalData().superfluousNodeStrategy());
        task.setDisplacedNodePenalty(modifyTaskDto.additionalData().displacedNodePenalty());
        task.setDisplacedNodeStrategy(modifyTaskDto.additionalData().displacedNodeStrategy());
        task.setMissingAttributePenalty(modifyTaskDto.additionalData().missingAttributePenalty());
        task.setMissingAttributeStrategy(modifyTaskDto.additionalData().missingAttributeStrategy());
        task.setSuperfluousAttributePenalty(modifyTaskDto.additionalData().superfluousAttributePenalty());
        task.setSuperfluousAttributeStrategy(modifyTaskDto.additionalData().superfluousAttributeStrategy());
        task.setIncorrectTextPenalty(modifyTaskDto.additionalData().incorrectTextPenalty());
        task.setIncorrectTextStrategy(modifyTaskDto.additionalData().incorrectTextStrategy());
        task.setIncorrectAttributeValuePenalty(modifyTaskDto.additionalData().incorrectAttributeValuePenalty());
        task.setIncorrectAttributeValueStrategy(modifyTaskDto.additionalData().incorrectAttributeValueStrategy());
    }

    private static List<String> stringToList(String s) {
        if (s == null)
            return List.of();
        return Arrays.stream(s.split("\n")).map(String::strip).filter(x -> !x.isBlank()).toList();
    }

    private static String convertGradingDtoToString(GradingDto grading) {
        var sb = new StringBuilder(grading.generalFeedback());
        grading.criteria().stream()
            .filter(c -> !c.passed())
            .filter(c -> !c.feedback().contains("<style>"))
            .forEach(c -> {
                sb.append("\n");
                sb.append(c.name());
                sb.append(": ");
                sb.append(c.feedback());
            });
        return sb.toString();
    }
}
