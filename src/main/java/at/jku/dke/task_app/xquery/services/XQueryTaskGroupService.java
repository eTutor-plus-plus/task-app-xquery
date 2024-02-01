package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskGroupModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskGroupService;
import at.jku.dke.task_app.xquery.config.XQuerySettings;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskGroupRepository;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskGroupDto;
import jakarta.validation.ValidationException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import org.sqids.Sqids;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;

/**
 * This class provides methods for managing {@link XQueryTaskGroup}s.
 */
@Service
public class XQueryTaskGroupService extends BaseTaskGroupService<XQueryTaskGroup, ModifyXQueryTaskGroupDto> {

    private final MessageSource messageSource;
    private final XQuerySettings settings;

    /**
     * Creates a new instance of class {@link XQueryTaskGroupService}.
     *
     * @param repository    The task group repository.
     * @param messageSource The message source.
     * @param settings      The XQuery settings.
     */
    public XQueryTaskGroupService(XQueryTaskGroupRepository repository, MessageSource messageSource, XQuerySettings settings) {
        super(repository);
        this.messageSource = messageSource;
        this.settings = settings;
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
    protected void afterCreate(XQueryTaskGroup taskGroup) {
        persistToFileSystem(taskGroup);
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
    protected void afterUpdate(XQueryTaskGroup taskGroup) {
        persistToFileSystem(taskGroup);
    }

    @Override
    protected void afterDelete(long id) {
        LOG.info("Deleting XML files for task group with id {}.", id);
        var path = XmlFileNameHelper.getDiagnoseFilePath(this.settings.xmlFilesDirectory(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            LOG.error("Could not delete diagnose document from file system. " + path, ex);
        }

        path = XmlFileNameHelper.getSubmitFilePath(this.settings.xmlFilesDirectory(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            LOG.error("Could not delete submit document from file system. " + path, ex);
        }
    }

    @Override
    protected TaskGroupModificationResponseDto mapToReturnData(XQueryTaskGroup taskGroup, boolean create) {
        String id = Sqids.builder().minLength(4).build().encode(List.of(taskGroup.getId()));
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

    /**
     * Persists the xml documents to the file system.
     *
     * @param taskGroup The task group.
     * @throws RuntimeException If an I/O error occurs.
     */
    private void persistToFileSystem(XQueryTaskGroup taskGroup) {
        File directory = Path.of(this.settings.xmlFilesDirectory()).normalize().toFile();
        if (!directory.exists()) {
            LOG.info("Creating directory for XML files: {}", directory.getAbsolutePath());
            directory.mkdirs();
        }

        try {
            var path = XmlFileNameHelper.getDiagnoseFilePath(directory.getAbsolutePath(), taskGroup.getId());
            LOG.info("Persisting diagnose document to file: {}", path);
            Files.writeString(path, taskGroup.getDiagnoseDocument(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            LOG.error("Could not persist diagnose document to file system.", ex);
            throw new RuntimeException("Could not persist diagnose document to file system.", ex);
        }

        try {
            var path = XmlFileNameHelper.getSubmitFilePath(directory.getAbsolutePath(), taskGroup.getId());
            LOG.info("Persisting submit document to file: {}", path);
            Files.writeString(path, taskGroup.getSubmitDocument(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            LOG.error("Could not persist diagnose document to file system.", ex);
            throw new RuntimeException("Could not persist diagnose document to file system.", ex);
        }
    }
}
