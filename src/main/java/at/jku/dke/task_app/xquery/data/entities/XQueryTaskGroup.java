package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a XQuery task group.
 */
@Entity
@Table(name = "task_group")
public class XQueryTaskGroup extends BaseTaskGroup {
    @NotNull
    @Column(name = "doc_diagnose", nullable = false)
    private String diagnoseDocument;

    @NotNull
    @Column(name = "doc_submit", nullable = false)
    private String submitDocument;

    /**
     * Creates a new instance of class {@link XQueryTaskGroup}.
     */
    public XQueryTaskGroup() {
    }

    /**
     * Creates a new instance of class {@link XQueryTaskGroup}.
     *
     * @param diagnoseDocument The diagnose document.
     * @param submitDocument   The submit document.
     */
    public XQueryTaskGroup(String diagnoseDocument, String submitDocument) {
        this.diagnoseDocument = diagnoseDocument;
        this.submitDocument = submitDocument;
    }

    /**
     * Creates a new instance of class {@link XQueryTaskGroup}.
     *
     * @param status           The status.
     * @param diagnoseDocument The diagnose document.
     * @param submitDocument   The submit document.
     */
    public XQueryTaskGroup(TaskStatus status, String diagnoseDocument, String submitDocument) {
        super(status);
        this.diagnoseDocument = diagnoseDocument;
        this.submitDocument = submitDocument;
    }

    /**
     * Creates a new instance of class {@link XQueryTaskGroup}.
     *
     * @param id               The identifier.
     * @param status           The status.
     * @param diagnoseDocument The diagnose document.
     * @param submitDocument   The submit document.
     */
    public XQueryTaskGroup(Long id, TaskStatus status, String diagnoseDocument, String submitDocument) {
        super(id, status);
        this.diagnoseDocument = diagnoseDocument;
        this.submitDocument = submitDocument;
    }

    /**
     * Gets the diagnose document.
     *
     * @return The diagnose document.
     */
    public String getDiagnoseDocument() {
        return diagnoseDocument;
    }

    /**
     * Sets the diagnose document.
     *
     * @param diagnoseDocument The diagnose document.
     */
    public void setDiagnoseDocument(String diagnoseDocument) {
        this.diagnoseDocument = diagnoseDocument;
    }

    /**
     * Gets the submit document.
     *
     * @return The submit document.
     */
    public String getSubmitDocument() {
        return submitDocument;
    }

    /**
     * Sets the submit document.
     *
     * @param submitDocument The submit document.
     */
    public void setSubmitDocument(String submitDocument) {
        this.submitDocument = submitDocument;
    }
}
