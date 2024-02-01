package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskGroupRepository;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskRepository;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class provides methods for managing {@link XQueryTask}s.
 */
@Service
public class XQueryTaskService extends BaseTaskInGroupService<XQueryTask, XQueryTaskGroup, ModifyXQueryTaskDto> {

    /**
     * Creates a new instance of class {@link XQueryTaskService}.
     *
     * @param repository          The task repository.
     * @param taskGroupRepository The task group repository.
     */
    public XQueryTaskService(XQueryTaskRepository repository, XQueryTaskGroupRepository taskGroupRepository) {
        super(repository, taskGroupRepository);
    }

    @Override
    protected XQueryTask createTask(long id, ModifyTaskDto<ModifyXQueryTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("xquery"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        return new XQueryTask(modifyTaskDto.additionalData().solution(), modifyTaskDto.additionalData().sorting());
    }

    @Override
    protected void updateTask(XQueryTask task, ModifyTaskDto<ModifyXQueryTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("xquery"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        task.setSolution(modifyTaskDto.additionalData().solution());
        task.setSorting(modifyTaskDto.additionalData().sorting());
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(XQueryTask task, boolean create) {
        return new TaskModificationResponseDto(null, null);
    }

}
