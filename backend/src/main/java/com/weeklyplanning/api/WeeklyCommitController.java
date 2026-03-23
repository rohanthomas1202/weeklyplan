package com.weeklyplanning.api;

import com.weeklyplanning.api.dto.CreateCommitRequest;
import com.weeklyplanning.api.dto.UpdateCommitRequest;
import com.weeklyplanning.api.dto.WeeklyCommitDto;
import com.weeklyplanning.application.WeeklyCommitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weeks/{weekId}/commits")
public class WeeklyCommitController {

    private final WeeklyCommitService weeklyCommitService;

    public WeeklyCommitController(WeeklyCommitService weeklyCommitService) {
        this.weeklyCommitService = weeklyCommitService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WeeklyCommitDto createCommit(@PathVariable Long weekId,
                                         @Valid @RequestBody CreateCommitRequest request) {
        return weeklyCommitService.createCommit(weekId, request);
    }

    @PutMapping("/{commitId}")
    public WeeklyCommitDto updateCommit(@PathVariable Long weekId,
                                         @PathVariable Long commitId,
                                         @Valid @RequestBody UpdateCommitRequest request) {
        return weeklyCommitService.updateCommit(weekId, commitId, request);
    }

    @DeleteMapping("/{commitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommit(@PathVariable Long weekId,
                              @PathVariable Long commitId) {
        weeklyCommitService.deleteCommit(weekId, commitId);
    }
}
