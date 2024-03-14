package at.jku.dke.task_app.xquery.evaluation.report;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.task_app.xquery.evaluation.analysis.Analysis;
import at.jku.dke.task_app.xquery.evaluation.grading.GradingEntry;
import at.jku.dke.task_app.xquery.evaluation.grading.XQueryGrading;
import org.codelibs.jhighlight.renderer.XhtmlRendererFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Class for generating a report for the XQuery evaluation.
 */
public class XQueryReport {
    private final MessageSource messageSource;
    private final Locale locale;
    private final SubmissionMode mode;
    private final int feedbackLevel;
    private final Analysis analysis;
    private final XQueryGrading grading;

    /**
     * Creates a new instance of class {@link XQueryReport}.
     *
     * @param messageSource The message source.
     * @param locale        The locale.
     * @param mode          The submission mode.
     * @param feedbackLevel The feedback level.
     * @param analysis      The analysis.
     * @param grading       The grading.
     */
    public XQueryReport(MessageSource messageSource, Locale locale, SubmissionMode mode, int feedbackLevel, Analysis analysis, XQueryGrading grading) {
        if (feedbackLevel < 0 || feedbackLevel > 3)
            throw new IllegalArgumentException("feedbackLevel must be between 0 and 3");

        this.messageSource = messageSource;
        this.locale = locale;
        this.mode = mode;
        this.feedbackLevel = mode == SubmissionMode.SUBMIT ? Math.max(1, feedbackLevel) : feedbackLevel;
        this.analysis = analysis;
        this.grading = grading;
    }

    /**
     * Gets the general feedback.
     *
     * @return The general feedback.
     */
    public String getGeneralFeedback() {
        if (this.mode == SubmissionMode.RUN) // we assume here that the syntax is valid because otherwise, the evaluation service will abort earlier
            return this.messageSource.getMessage("noSyntaxError", null, this.locale);

        return this.analysis.isCorrect() ?
            this.messageSource.getMessage(this.mode == SubmissionMode.SUBMIT ? "correct" : "possiblyCorrect", null, this.locale) :
            this.messageSource.getMessage("incorrect", null, this.locale);
    }

    /**
     * Gets the detailed feedback.
     *
     * @return The detailed feedback.
     */
    public List<CriterionDto> getCriteria() {
        var criteria = new ArrayList<CriterionDto>();

        // Syntax always valid because if syntax is invalid, this method will not be called.
        criteria.add(new CriterionDto(
            this.messageSource.getMessage("criterium.syntax", null, locale),
            null,
            true,
            this.messageSource.getMessage("criterium.syntax.valid", null, locale)));

        // schema
        if (this.mode != SubmissionMode.RUN && this.feedbackLevel > 0 && !this.analysis.isSchemaValid()) {
            criteria.add(new CriterionDto(
                this.messageSource.getMessage("criterium.schema", null, locale),
                null,
                true,
                this.messageSource.getMessage("criterium.schema.invalid", null, locale)));
        }

        // Semantics
        this.createCriterion("missingNode", GradingEntry.MISSING_NODE, this.analysis::getMissingNodes).ifPresent(criteria::add);
        this.createCriterion("superfluousNode", GradingEntry.SUPERFLUOUS_NODE, this.analysis::getSuperfluousNodes).ifPresent(criteria::add);
        this.createCriterion("displacedNode", GradingEntry.DISPLACED_NODE, this.analysis::getDisplacedNodes).ifPresent(criteria::add);
        this.createCriterion("incorrectText", GradingEntry.INCORRECT_TEXT, this.analysis::getIncorrectTextValues).ifPresent(criteria::add);
        this.createCriterion("missingAttribute", GradingEntry.MISSING_ATTRIBUTE, this.analysis::getMissingAttributes).ifPresent(criteria::add);
        this.createCriterion("superfluousAttribute", GradingEntry.SUPERFLUOUS_ATTRIBUTE, this.analysis::getSuperfluousAttributes).ifPresent(criteria::add);
        this.createCriterion("incorrectValue", GradingEntry.INCORRECT_ATTRIBUTE_VALUE, this.analysis::getIncorrectAttributeValues).ifPresent(criteria::add);

        // Query result
        if (this.mode != SubmissionMode.SUBMIT) {
            criteria.add(new CriterionDto(
                this.messageSource.getMessage("criterium.result", null, locale),
                null,
                analysis.isCorrect(),
                highlightCode(this.analysis.getSubmissionResult().getRawResult())
            ));
        }

        return criteria;
    }

    private Optional<CriterionDto> createCriterion(String translationKey, String errorCategory, Supplier<List<?>> listSupplier) {
        if (this.mode == SubmissionMode.RUN)
            return Optional.empty();
        if (listSupplier.get().isEmpty())
            return Optional.empty();

        return switch (this.feedbackLevel) {
            case 0 -> // no feedback
                Optional.empty();
            case 1 -> // little feedback
                Optional.of(new CriterionDto(
                    this.messageSource.getMessage("criterium." + translationKey, null, locale),
                    this.mode == SubmissionMode.SUBMIT ? this.grading.getDetails(errorCategory).map(e -> e.minusPoints().negate()).orElse(null) : null,
                    false,
                    this.messageSource.getMessage("criterium." + translationKey + ".noCount", null, locale)
                ));
            case 2 -> // some feedback
                Optional.of(new CriterionDto(
                    this.messageSource.getMessage("criterium." + translationKey, null, locale),
                    this.grading.getDetails(errorCategory).map(e -> e.minusPoints().negate()).orElse(null),
                    false,
                    this.messageSource.getMessage("criterium." + translationKey + ".count", new Object[]{listSupplier.get().size()}, locale)
                ));
            case 3 -> // much feedback
                Optional.of(new CriterionDto(
                    this.messageSource.getMessage("criterium." + translationKey, null, locale),
                    this.grading.getDetails(errorCategory).map(e -> e.minusPoints().negate()).orElse(null),
                    false,
                    this.messageSource.getMessage("criterium." + translationKey + ".details", new Object[]{this.createDetailsString(listSupplier.get())}, locale)
                ));
            default -> Optional.empty();
        };
    }

    private String createDetailsString(List<?> list) {
        StringBuilder sb = new StringBuilder("<ul>");
        for (var node : list) {
            sb.append("<li><pre>")
                .append(HtmlUtils.htmlEscape(node.toString()))
                .append("</pre></li>");
        }
        return sb.append("</ul>").toString();
    }

    private static String highlightCode(String code) {
        String css = """
            <style>
            .xml {font-family: monospace;}
            .xml_tag_symbols {color: #000000;}
            .xml_tag_name {color: #008080;}
            .xml_plain {color: #000000;}
            .xml_attribute_value {color: #a31515;}
            </style>
            """;
        try {
            return css + "<div class=\"xml\">" + XhtmlRendererFactory.getRenderer(XhtmlRendererFactory.XML).highlight("result.xml", code, "UTF-8", true) + "</div>";
        } catch (IOException e) {
            return "<pre>" + HtmlUtils.htmlEscape(code) + "</pre>";
        }
    }

}
