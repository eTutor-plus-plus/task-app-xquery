package at.jku.dke.task_app.xquery.data.repositories;

import at.jku.dke.etutor.task_app.data.repositories.TaskRepository;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository for entity {@link XQueryTask}.
 */
public interface XQueryTaskRepository extends TaskRepository<XQueryTask> {
    /**
     * Returns the task with the specified id including the task group eagerly loaded.
     *
     * @param id The id of the task.
     * @return The task with the specified id including the task group.
     */
    @Query("SELECT t FROM XQueryTask t LEFT JOIN FETCH t.taskGroup WHERE t.id = :id")
    Optional<XQueryTask> findByIdWithTaskGroup(Long id);
}
