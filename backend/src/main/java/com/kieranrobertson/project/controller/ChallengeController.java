package com.kieranrobertson.project.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kieranrobertson.project.exception.ChallengeNotFoundException;
import com.kieranrobertson.project.exception.CodeCompileException;
import com.kieranrobertson.project.model.CodingChallenge;
import com.kieranrobertson.project.model.SubmissionAttempt;
import com.kieranrobertson.project.model.TestCase;
import com.kieranrobertson.project.model.TestResult;
import com.kieranrobertson.project.service.ChallengeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("challenges")
public class ChallengeController {

    @Resource
    private ChallengeService challengeService;

    @GetMapping("{id}")
    public CodingChallenge allChallenges(@PathVariable("id") int id) {
        Optional<CodingChallenge> codingChallenge = challengeService.getChallenge(id);
        if (codingChallenge.isPresent()) {
            return codingChallenge.get();
        }
        throw new ChallengeNotFoundException("Challenge with ID " + id + " could not be found.");
    }

    @GetMapping
    public List<CodingChallenge> allChallenges() {
        return challengeService.getAllChallenges();
    }

    @PostMapping
    public void newChallenge(@RequestBody CodingChallenge challenge) {
        challengeService.newChallenge(challenge);
    }


    @PostMapping("{id}/attempt")
    public Set<TestCaseResult> attemptChallenge(@PathVariable("id") int id, @RequestBody ChallengeAttempt attempt) {
        Optional<CodingChallenge> codingChallengeCheck = challengeService.getChallenge(id);
        if (!codingChallengeCheck.isPresent()) {
            throw new ChallengeNotFoundException("Challenge with ID " + id + " could not be found.");
        }

        CodingChallenge codingChallenge = codingChallengeCheck.get();
        if (!codingChallenge.doesCompile(attempt.getCode(), attempt.getLanguage())) {
            // TODO: Add compile exception to message.
            throw new CodeCompileException("Code failed to compile.");
        }

        // TODO: move all this out into service and improve logic substantially.
        int testsPassed = 0;
        Set<TestCaseResult> testCaseResults = new TreeSet<>();
        Map<TestCase, TestResult> runResults = codingChallenge.submitAttempt(attempt.getCode(), attempt.getLanguage());
        for (TestCase testCase : runResults.keySet()) {
            Object actualResult = runResults.get(testCase).getResult();
            if (actualResult.equals(testCase.getExpectedResult())) {
                testCaseResults.add(new TestCaseResult(testCase.getId(), true, actualResult.toString()));
                testsPassed++;
            } else {
                testCaseResults.add(new TestCaseResult(testCase.getId(), false, actualResult.toString()));
            }
        }

        // -1 is a submission from a lecturer
        if (attempt.getStudentId() != -1) {
            challengeService.saveSubmissionAttempt(attempt.getStudentId(), id, testsPassed, attempt.getCode());
        }

        return testCaseResults;
    }

    @GetMapping("{chalId}/submissions/{classId}")
    public List<SubmissionAttempt> getChallengeAttempts(@PathVariable("chalId") int chalId, @PathVariable("classId") int classId) {
        return challengeService.getSubmissionsFromClass(chalId, classId);
    }

    @Data
    private static class ChallengeAttempt {
        @JsonProperty("student_id")
        private int studentId;

        @JsonProperty("language")
        private CodingChallenge.ProgrammingLanguage language;

        @JsonProperty("code")
        private String code;
    }

    @Data
    @AllArgsConstructor
    private static class TestCaseResult implements Comparable<TestCaseResult>{
        private Integer id;
        private boolean success;
        private String output;

        /**
         * Used to sort test cases results by ID, for consistent output.
         */
        @Override
        public int compareTo(TestCaseResult testCaseResult) {
            return this.getId().compareTo(testCaseResult.getId());
        }
    }


}
