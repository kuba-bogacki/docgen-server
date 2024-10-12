package com.document.service.implementation;

import com.document.event.EvidenceCreateEvent;
import com.document.mapper.EvidenceMapper;
import com.document.model.Evidence;
import com.document.model.dto.EvidenceDto;
import com.document.repository.EvidenceRepository;
import com.document.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.document.util.ApplicationConstants.API_VERSION;
import static com.document.util.ApplicationConstants.PROTOCOL;
import static com.document.util.UrlBuilder.buildUrl;

@Service
@RequiredArgsConstructor
@Log4j2
class EvidenceServiceImplementation implements EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceMapper evidenceMapper;
    private final WebClient.Builder webClientBuilder;
//    private final KafkaTemplate<String, EvidenceCreateEvent> kafkaTemplate;

    @Override
    public void createEvidence(EvidenceDto evidenceDto) {
//        Boolean votingHeld = webClientBuilder.build().get()
//                .uri(buildUrl(PROTOCOL, "event-service", API_VERSION, "/event/voting-held"))
//                .retrieve()
//                .bodyToMono(Boolean.class)
//                .block();
//        if (Boolean.FALSE.equals(votingHeld)) {
//            throw new ResponseStatusException(HttpStatusCode.valueOf(403), "Couldn't save evidence");
//        }
        Evidence evidence = evidenceRepository.save(evidenceMapper.mapToEntity(evidenceDto));
//        kafkaTemplate.send("notificationTopic", new EvidenceCreateEvent(evidence.getEvidenceId()));
        log.info("Evidence has been created with id : {}", evidence.getEvidenceId());
    }

    @Override
    public EvidenceDto getEvidenceByName(String evidenceName) {
        return evidenceMapper.mapToDto(evidenceRepository.findByEvidenceName(evidenceName));
    }

    @Override
    public List<EvidenceDto> getAllEvidences() {
        return evidenceMapper.mapToDtos(evidenceRepository.findAll());
    }
}
