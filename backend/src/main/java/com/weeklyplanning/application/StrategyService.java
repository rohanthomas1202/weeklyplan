package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.*;
import com.weeklyplanning.domain.entity.DefiningObjective;
import com.weeklyplanning.domain.entity.Outcome;
import com.weeklyplanning.domain.entity.RallyCry;
import com.weeklyplanning.infrastructure.repository.DefiningObjectiveRepository;
import com.weeklyplanning.infrastructure.repository.OutcomeRepository;
import com.weeklyplanning.infrastructure.repository.RallyCryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrategyService {

    private final RallyCryRepository rallyCryRepository;
    private final DefiningObjectiveRepository definingObjectiveRepository;
    private final OutcomeRepository outcomeRepository;

    public StrategyService(RallyCryRepository rallyCryRepository,
                           DefiningObjectiveRepository definingObjectiveRepository,
                           OutcomeRepository outcomeRepository) {
        this.rallyCryRepository = rallyCryRepository;
        this.definingObjectiveRepository = definingObjectiveRepository;
        this.outcomeRepository = outcomeRepository;
    }

    public List<RallyCryDto> getAllRallyCries() {
        return rallyCryRepository.findAll().stream()
                .map(rc -> new RallyCryDto(rc.getId(), rc.getTitle(), rc.getDescription()))
                .toList();
    }

    public List<DefiningObjectiveDto> getDefiningObjectives(Long rallyCryId) {
        return definingObjectiveRepository.findByRallyCryId(rallyCryId).stream()
                .map(d -> new DefiningObjectiveDto(d.getId(), d.getTitle(), d.getDescription()))
                .toList();
    }

    public List<OutcomeDto> getOutcomes(Long definingObjectiveId) {
        return outcomeRepository.findByDefiningObjectiveId(definingObjectiveId).stream()
                .map(o -> new OutcomeDto(o.getId(), o.getTitle(), o.getDescription()))
                .toList();
    }

    public List<StrategyTreeDto> getStrategyTree() {
        List<RallyCry> rallyCries = rallyCryRepository.findAll();
        return rallyCries.stream().map(rc -> {
            List<DefiningObjective> dos = definingObjectiveRepository.findByRallyCryId(rc.getId());
            List<StrategyTreeDto.DefiningObjectiveTreeNode> doNodes = dos.stream().map(d -> {
                List<Outcome> outcomes = outcomeRepository.findByDefiningObjectiveId(d.getId());
                List<OutcomeDto> outcomeDtos = outcomes.stream()
                        .map(o -> new OutcomeDto(o.getId(), o.getTitle(), o.getDescription()))
                        .toList();
                return new StrategyTreeDto.DefiningObjectiveTreeNode(
                        d.getId(), d.getTitle(), d.getDescription(), outcomeDtos);
            }).toList();
            return new StrategyTreeDto(rc.getId(), rc.getTitle(), rc.getDescription(), doNodes);
        }).toList();
    }
}
