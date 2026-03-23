package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.ChessCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChessCategoryRepository extends JpaRepository<ChessCategory, String> {
    List<ChessCategory> findByActiveTrueOrderBySortOrderAsc();
}
