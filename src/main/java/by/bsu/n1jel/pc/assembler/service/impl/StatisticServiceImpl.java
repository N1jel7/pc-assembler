package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.response.StatsInfoResponseDto;
import by.bsu.n1jel.pc.assembler.repository.BuildRepository;
import by.bsu.n1jel.pc.assembler.service.api.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final BuildRepository buildRepository;

    @Override
    @Transactional(readOnly = true)
    public StatsInfoResponseDto getStatistics() {
        return buildRepository.getStatistic();
    }
}
