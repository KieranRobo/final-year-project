package com.kieranrobertson.project.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kieranrobertson.project.exception.ChallengeNotFoundException;
import com.kieranrobertson.project.exception.ClassNotFoundException;
import com.kieranrobertson.project.model.APIResponse;
import com.kieranrobertson.project.model.Class;
import com.kieranrobertson.project.model.APIErrorResponse;
import com.kieranrobertson.project.model.CodingChallenge;
import com.kieranrobertson.project.service.ChallengeService;
import com.kieranrobertson.project.service.ClassService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("classes")
public class ClassController {

    @Resource
    private ClassService classService;

    @Resource
    private ChallengeService challengeService;

    @GetMapping("{id}")
    public Class getClass(@PathVariable("id") int id) {
        Optional<Class> classOptional = classService.getClass(id);
        if (!classOptional.isPresent()) {
            throw new ClassNotFoundException("Class with ID " + id + " does not exist");
        } else {
            return classOptional.get();
        }
    }

    @PostMapping
    public APIResponse newClass(@RequestBody NewClassPost newClass) {
        if (classService.findClassByClassCode(newClass.getClassCode()).isPresent()) {
            return new APIErrorResponse("ERR001", "A class with the same class code already exists.");
        }
        classService.newClass(newClass.getClassCode(), newClass.getName(), newClass.getLecturerId(), newClass.getStudentIds());
        return new APIResponse("SUCC001", "Class successfully created");
    }

    @PutMapping("{id}/assign-challenge/{challenge_id}")
    public void assignChallenge(@PathVariable("id") int classId, @PathVariable("challenge_id") int challengeId) {
        Optional<Class> theClass = classService.getClass(classId);
        Optional<CodingChallenge> codingChallenge = challengeService.getChallenge(challengeId);

        if (!theClass.isPresent()) {
            throw new ClassNotFoundException("Class with ID " + classId + " does not exist.");
        }
        if (!codingChallenge.isPresent()) {
            throw new ChallengeNotFoundException("Challenge with ID " + challengeId + " does not exist.");
        }

        classService.assignChallenge(theClass.get(), codingChallenge.get());
    }

    @Data
    @AllArgsConstructor
    private static class NewClassPost {

        @JsonProperty("class_code")
        private String classCode;

        @JsonProperty("name")
        private String name;

        @JsonProperty("lecturer_id")
        private int lecturerId;

        @JsonProperty("student_ids")
        private int[] studentIds;
    }
}
