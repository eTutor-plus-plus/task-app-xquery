package at.jku.dke.task_app.binary_search.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.binary_search.data.entities.BinarySearchTask;
import at.jku.dke.task_app.binary_search.data.entities.BinarySearchTaskGroup;
import at.jku.dke.task_app.binary_search.data.repositories.BinarySearchTaskGroupRepository;
import at.jku.dke.task_app.binary_search.data.repositories.BinarySearchTaskRepository;
import at.jku.dke.task_app.binary_search.dto.ModifyBinarySearchTaskDto;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/**
 * This class provides methods for managing {@link BinarySearchTask}s.
 */
@Service
public class BinarySearchTaskService extends BaseTaskInGroupService<BinarySearchTask, BinarySearchTaskGroup, ModifyBinarySearchTaskDto> {

    private final MessageSource messageSource;

    /**
     * Creates a new instance of class {@link BinarySearchTaskService}.
     *
     * @param repository          The task repository.
     * @param taskGroupRepository The task group repository.
     * @param messageSource       The message source.
     */
    public BinarySearchTaskService(BinarySearchTaskRepository repository, BinarySearchTaskGroupRepository taskGroupRepository, MessageSource messageSource) {
        super(repository, taskGroupRepository);
        this.messageSource = messageSource;
    }

    @Override
    protected BinarySearchTask createTask(long id, ModifyTaskDto<ModifyBinarySearchTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("binary-search"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        return new BinarySearchTask(modifyTaskDto.additionalData().solution());
    }

    @Override
    protected void updateTask(BinarySearchTask task, ModifyTaskDto<ModifyBinarySearchTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("binary-search"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        task.setSolution(modifyTaskDto.additionalData().solution());
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(BinarySearchTask task, boolean create) {
        return new TaskModificationResponseDto(
            this.messageSource.getMessage("defaultTaskDescription", null, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskDescription", null, Locale.ENGLISH)
        );
    }
}
