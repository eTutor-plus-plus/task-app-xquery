package at.jku.dke.task_app.xquery.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskController;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.dto.XQueryTaskDto;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskDto;
import at.jku.dke.task_app.xquery.services.XQueryTaskService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link XQueryTask}s.
 */
@RestController
public class TaskController extends BaseTaskController<XQueryTask, XQueryTaskDto, ModifyXQueryTaskDto> {

    /**
     * Creates a new instance of class {@link TaskController}.
     *
     * @param taskService The task service.
     */
    public TaskController(XQueryTaskService taskService) {
        super(taskService);
    }

    @Override
    protected XQueryTaskDto mapToDto(XQueryTask task) {
        return new XQueryTaskDto(task.getSolution());
    }

}
