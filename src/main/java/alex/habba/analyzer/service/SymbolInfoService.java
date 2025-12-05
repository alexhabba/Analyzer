package alex.habba.analyzer.service;

import alex.habba.analyzer.entity.SymbolInfo;
import alex.habba.analyzer.repository.SymbolInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SymbolInfoService {

    private final SymbolInfoRepository repository;

    public void save(SymbolInfo symbolInfo) {
        repository.save(symbolInfo);
    }

    public void saveAll(List<SymbolInfo> symbolInfos) {
        repository.saveAll(symbolInfos);
    }
}
