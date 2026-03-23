package com.weeklyplanning.api;

import com.weeklyplanning.api.dto.ChessCategoryDto;
import com.weeklyplanning.api.dto.UserDto;
import com.weeklyplanning.domain.entity.AppUser;
import com.weeklyplanning.infrastructure.config.UserContext;
import com.weeklyplanning.infrastructure.repository.AppUserRepository;
import com.weeklyplanning.infrastructure.repository.ChessCategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final AppUserRepository appUserRepository;
    private final ChessCategoryRepository chessCategoryRepository;

    public UserController(AppUserRepository appUserRepository,
                          ChessCategoryRepository chessCategoryRepository) {
        this.appUserRepository = appUserRepository;
        this.chessCategoryRepository = chessCategoryRepository;
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return appUserRepository.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getTeamId()))
                .toList();
    }

    @GetMapping("/users/me")
    public UserDto getCurrentUser() {
        AppUser user = UserContext.get();
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getTeamId());
    }

    @GetMapping("/chess-categories")
    public List<ChessCategoryDto> getChessCategories() {
        return chessCategoryRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(c -> new ChessCategoryDto(c.getCode(), c.getDisplayName(), c.getDescription(), c.getSortOrder()))
                .toList();
    }
}
