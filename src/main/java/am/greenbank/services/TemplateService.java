package am.greenbank.services;

import am.greenbank.entities.transaction.Template;
import am.greenbank.exceptions.exceptions.TemplateNotFoundException;
import am.greenbank.repositories.interfaces.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final TemplateRepository templateRepository;

    public Template save(Template template) {
        return templateRepository.saveTemplate(template);

    }

    public List<Template> getAllTemplatesByUserId(String userId) {
        List<Template> allByUserId = templateRepository.findAllByUserId(userId);

        return allByUserId;
    }

    public Template updateById(Template template, String templateId) {
        Template saved = templateRepository.findTemplateById(templateId)
            .map(templateById -> {
                    templateById.setAmount(Optional.ofNullable(template.getAmount()).orElse(templateById.getAmount()));
                    templateById.setDescription(Optional.ofNullable(template.getDescription()).orElse(templateById.getDescription()));
                    templateById.setFrom(Optional.ofNullable(template.getFrom()).orElse(templateById.getFrom()));
                    templateById.setTo(Optional.ofNullable(template.getTo()).orElse(templateById.getTo()));

                    return templateRepository.saveTemplate(templateById);
                }
            ).orElseThrow(()-> new TemplateNotFoundException("Template by provided id not found"));

        return saved;
    }

    public void deleteById(String templateId) {
        templateRepository.deleteById(templateId);
    }
}
