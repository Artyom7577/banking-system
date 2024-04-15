package am.greenbank.repositories.interfaces;

import am.greenbank.entities.transaction.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository {
    Template saveTemplate(Template template);

    Optional<Template> findTemplateById(String id);

    List<Template> findAllByUserId(String userId);

    void deleteById(String templateId);

    void deleteAllByUserId(String userId);
}
