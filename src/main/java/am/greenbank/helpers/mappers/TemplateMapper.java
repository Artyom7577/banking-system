package am.greenbank.helpers.mappers;

import am.greenbank.dtos.TemplateDto;
import am.greenbank.dtos.TransactionEntityDto;
import am.greenbank.entities.transaction.Template;
import am.greenbank.entities.transaction.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TemplateMapper {

    public TemplateDto mapSaveTransactionToSaveTransactionDto(Template template) {
        return TemplateDto
            .builder()
            .id(template.getId())
            .userId(template.getUserId())
            .description(template.getDescription())
            .amount(template.getAmount())
            .from(new TransactionEntityDto(
                    template.getFrom().getNumber(),
                    template.getFrom().getType()
                )
            )
            .to(new TransactionEntityDto(
                    template.getTo().getNumber(),
                    template.getTo().getType()
                )
            )
            .build();
    }

    public Template mapTemplateDtoToTemplate(TemplateDto templateDto) {
        return Template
            .builder()
            .id(templateDto.getId())
            .userId(templateDto.getUserId())
            .description(templateDto.getDescription())
            .amount(templateDto.getAmount())
            .from(TransactionEntity
                .builder()
                .number(templateDto.getFrom().getNumber())
                .type(templateDto.getFrom().getType())
                .build()
            )
            .to(TransactionEntity
                .builder()
                .number(templateDto.getTo().getNumber())
                .type(templateDto.getTo().getType())
                .build()
            )
            .build();
    }
}
