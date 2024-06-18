package com.document.mapper;

import com.document.model.Evidence;
import com.document.model.dto.EvidenceDto;
import com.document.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EvidenceMapper {

    Evidence mapToEntity(EvidenceDto evidenceDto);
    EvidenceDto mapToDto(Evidence evidence);
    List<Evidence> mapToEntities(List<EvidenceDto> evidenceDto);
    List<EvidenceDto> mapToDtos(List<Evidence> evidence);
}
