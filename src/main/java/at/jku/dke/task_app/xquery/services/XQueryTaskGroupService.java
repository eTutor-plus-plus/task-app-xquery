package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskGroupModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskGroupService;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskGroupRepository;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskGroupDto;
import jakarta.validation.ValidationException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * This class provides methods for managing {@link XQueryTaskGroup}s.
 */
@Service
public class XQueryTaskGroupService extends BaseTaskGroupService<XQueryTaskGroup, ModifyXQueryTaskGroupDto> {

    private final MessageSource messageSource;

    /**
     * Creates a new instance of class {@link XQueryTaskGroupService}.
     *
     * @param repository    The task group repository.
     * @param messageSource The message source.
     */
    public XQueryTaskGroupService(XQueryTaskGroupRepository repository, MessageSource messageSource) {
        super(repository);
        this.messageSource = messageSource;
    }

    @Override
    protected XQueryTaskGroup createTaskGroup(long id, ModifyTaskGroupDto<ModifyXQueryTaskGroupDto> modifyTaskGroupDto) {
        if (!modifyTaskGroupDto.taskGroupType().equals("xquery"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task group type.");

        // Validate
        validateXml(modifyTaskGroupDto.additionalData());

        // Create
        return new XQueryTaskGroup(modifyTaskGroupDto.additionalData().diagnoseDocument(), modifyTaskGroupDto.additionalData().submitDocument());
    }

    @Override
    protected void updateTaskGroup(XQueryTaskGroup taskGroup, ModifyTaskGroupDto<ModifyXQueryTaskGroupDto> modifyTaskGroupDto) {
        if (!modifyTaskGroupDto.taskGroupType().equals("xquery"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task group type.");

        // Validate
        validateXml(modifyTaskGroupDto.additionalData());

        // Update
        taskGroup.setDiagnoseDocument(modifyTaskGroupDto.additionalData().diagnoseDocument());
        taskGroup.setSubmitDocument(modifyTaskGroupDto.additionalData().submitDocument());
    }

    @Override
    protected TaskGroupModificationResponseDto mapToReturnData(XQueryTaskGroup taskGroup, boolean create) {
        String id = HashIds.encode(taskGroup.getId());
        return new TaskGroupModificationResponseDto(
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{HtmlUtils.htmlEscape(taskGroup.getDiagnoseDocument()), id}, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{HtmlUtils.htmlEscape(taskGroup.getDiagnoseDocument()), id}, Locale.ENGLISH));
    }

    /**
     * Validates the XML documents and throws an exception if the documents are not valid.
     *
     * @param dto The DTO containing the documents.
     */
    private void validateXml(ModifyXQueryTaskGroupDto dto) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            try {
                builder.parse(new ByteArrayInputStream(dto.diagnoseDocument().getBytes(StandardCharsets.UTF_8)));
            } catch (SAXException | IOException ex) {
                LOG.warn("Invalid diagnose document.", ex);
                throw new ValidationException("Invalid diagnose document: " + ex.getMessage());
            }
            try {
                builder.parse(new ByteArrayInputStream(dto.submitDocument().getBytes(StandardCharsets.UTF_8)));
            } catch (SAXException | IOException ex) {
                LOG.warn("Invalid submit document.", ex);
                throw new ValidationException("Invalid submit document: " + ex.getMessage());
            }
        } catch (ParserConfigurationException ex) {
            LOG.error("Could not create document builder.", ex);
            throw new RuntimeException(ex);
        }
    }
}
