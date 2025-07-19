package com.document.mapper;

import com.document.model.Evidence;
import com.document.model.dto.EvidenceDetailsDto;
import com.document.model.dto.EvidenceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", imports = Timestamp.class)
public interface EvidenceMapper {

    @Mapping(source = "createDateTime", target = "createDateTime", dateFormat = "dd-MM-yyyy HH:mm:ss")
    EvidenceDto mapToDto(Evidence evidence);

    @Mapping(source = "createDateTime", target = "createDateTime", dateFormat = "dd-MM-yyyy HH:mm:ss")
    EvidenceDetailsDto mapToDetailDto(Evidence evidence);

    @Mapping(source = "createDateTime", target = "createDateTime", dateFormat = "dd-MM-yyyy HH:mm:ss")
    List<EvidenceDto> mapToDtos(List<Evidence> evidence);

    @Mapping(source = "createDateTime", target = "createDateTime", dateFormat = "dd-MM-yyyy HH:mm:ss")
    List<EvidenceDetailsDto> mapToDetailDtos(List<Evidence> evidence);
}
