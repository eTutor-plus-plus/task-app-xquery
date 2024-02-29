package at.jku.dke.task_app.xquery.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskGroupController;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskGroupDto;
import at.jku.dke.task_app.xquery.dto.XQueryTaskGroupDto;
import at.jku.dke.task_app.xquery.services.XQueryTaskGroupService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link XQueryTaskGroup}s.
 */
@RestController
public class TaskGroupController extends BaseTaskGroupController<XQueryTaskGroup, XQueryTaskGroupDto, ModifyXQueryTaskGroupDto> {

    /**
     * Creates a new instance of class {@link TaskGroupController}.
     *
     * @param taskGroupService The task group service.
     */
    public TaskGroupController(XQueryTaskGroupService taskGroupService) {
        super(taskGroupService);
    }

    @Override
    protected XQueryTaskGroupDto mapToDto(XQueryTaskGroup taskGroup) {
        return new XQueryTaskGroupDto(taskGroup.getDiagnoseDocument(), taskGroup.getSubmitDocument());
    }

}
