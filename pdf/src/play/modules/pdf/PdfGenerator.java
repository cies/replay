package play.modules.pdf;

import play.mvc.Http;
import play.mvc.TemplateNameResolver;
import play.templates.Template;
import play.templates.TemplateLoader;

import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

@Singleton
public class PdfGenerator {
  private static final TemplateNameResolver templateNameResolver = new TemplateNameResolver();
  private static final PdfHelper helper = new PdfHelper();

  /** Renders PDF from localhost */
  public byte[] generate(PdfTemplate pdfTemplate) {
    return generate(pdfTemplate, true);
  }

  /** Renders PDF from the same domain as the request */
  public byte[] generate(PdfTemplate pdfTemplate, boolean fromLocalhost) {
    PDFDocument document = helper.createSinglePDFDocuments(pdfTemplate);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    String templateName1 = templateNameResolver.resolveTemplateName(document.template);
    Template template = TemplateLoader.load(templateName1);
    document.args.putAll(helper.templateBinding(pdfTemplate.getArguments()));
    document.content = template.render(new HashMap<>(document.args));
    helper.loadHeaderAndFooter(document, document.args);
    helper.renderPDF(document, out, Http.Request.current(), fromLocalhost);
    return out.toByteArray();
  }
}
