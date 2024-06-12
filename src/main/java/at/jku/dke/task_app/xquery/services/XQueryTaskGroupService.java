package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskGroupModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskGroupService;
import at.jku.dke.task_app.xquery.config.XQuerySettings;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.data.repositories.XQueryTaskGroupRepository;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskGroupDto;
import at.jku.dke.task_app.xquery.evaluation.analysis.DTDGenerator;
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
import java.nio.file.Files;
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
        String dtd = "";

        try {
            var file = Files.createTempFile("tg-dtd", ".dtd");
            Files.writeString(file, taskGroup.getDiagnoseDocument(), StandardCharsets.UTF_8);

            var gen = new DTDGenerator();
            gen.run(file.toString());
            dtd = HtmlUtils.htmlEscape(gen.printDTD());
        } catch (Exception ex) {
            LOG.error("Could not generate DTD", ex);
        }

        return new TaskGroupModificationResponseDto(
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{
                HtmlUtils.htmlEscape(taskGroup.getDiagnoseDocument()),
                this.settings.docUrl(),
                id,
                dtd
            }, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{
                HtmlUtils.htmlEscape(taskGroup.getDiagnoseDocument()),
                this.settings.docUrl(),
                id,
                dtd
            }, Locale.ENGLISH));
    }

    /**
     * Returns the public URL of the diagnose document for the specified task group.
     *
     * @param id The task group identifier.
     * @return The public URL.
     */
    public String getPublicUrl(long id) {
        return this.settings.docUrl() + HashIds.encode(id);
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
