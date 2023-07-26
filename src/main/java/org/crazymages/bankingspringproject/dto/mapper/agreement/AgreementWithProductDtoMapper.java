package org.crazymages.bankingspringproject.dto.mapper.agreement;

import org.crazymages.bankingspringproject.dto.AgreementDto;
import org.crazymages.bankingspringproject.entity.Agreement;
import org.crazymages.bankingspringproject.entity.enums.AgreementStatus;
import org.crazymages.bankingspringproject.exception.DataNotFoundException;
import org.crazymages.bankingspringproject.service.utils.mapper.DtoMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class AgreementWithProductDtoMapper implements DtoMapper<Agreement, AgreementDto> {
    @Override
    public AgreementDto mapEntityToDto(Agreement entity) {
        if (entity == null) {
            throw new IllegalArgumentException("account cannot be null");
        }
        return AgreementDto.builder()
                .interestRate(entity.getInterestRate() != null ? entity.getInterestRate() : BigDecimal.ZERO)
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .amount(entity.getAmount() != null ? entity.getAmount() : BigDecimal.ZERO)
                .build();
    }

    @Override
    public Agreement mapDtoToEntity(AgreementDto entityDto) {
        if (entityDto == null) {
            throw new IllegalArgumentException("account cannot be null");
        }
        return Agreement.builder()
                .interestRate(entityDto.getInterestRate())
                .status(entityDto.getStatus() != null ? AgreementStatus.valueOf(entityDto.getStatus()) : null)
                .amount(entityDto.getAmount())
                .build();
    }

    @Override
    public List<AgreementDto> getDtoList(List<Agreement> entityList) {
        return Optional.ofNullable(entityList)
                .orElseThrow(() -> new DataNotFoundException("list is null"))
                .stream()
                .filter(Objects::nonNull)
                .map(this::mapEntityToDto)
                .toList();
    }
}