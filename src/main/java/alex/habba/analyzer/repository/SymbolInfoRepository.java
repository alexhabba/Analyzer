package alex.habba.analyzer.repository;

import alex.habba.analyzer.entity.SymbolInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SymbolInfoRepository extends JpaRepository<SymbolInfo, UUID> {
}
