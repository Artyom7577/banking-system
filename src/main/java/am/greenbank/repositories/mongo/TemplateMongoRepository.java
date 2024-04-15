package am.greenbank.repositories.mongo;

import am.greenbank.entities.transaction.Template;
import am.greenbank.repositories.interfaces.TemplateRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateMongoRepository extends TemplateRepository, MongoRepository<Template, String> {
    @Override
    default Template saveTemplate(Template template) {
        return save(template);
    }

    @Override
    default Optional<Template> findTemplateById(String id) {
        return findById(id);
    }

    @Override
    default void deleteById(String templateId) {
        Optional<Template> templateById = findById(templateId);

        if (templateById.isPresent()) {
            Template template = templateById.get();
            template.setDeleted(true);
            saveTemplate(template);
        }
    }

    @Override
    default void deleteAllByUserId(String userId) {
        List<Template> allByUserId = findAllByUserId(userId);
        allByUserId.forEach(template -> template.setDeleted(true));
        saveAll(allByUserId);
    }
}
