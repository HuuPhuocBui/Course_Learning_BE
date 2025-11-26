package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.BenefitRequestDTO;
import com.example.course_learning_be.dto.response.BenefitResponseDTO;
import com.example.course_learning_be.enums.Language;
import com.example.course_learning_be.service.BenefitService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/benefit")
@Validated
@Slf4j(topic = "BENEFIT-CONTROLLER")
@RequiredArgsConstructor
public class BenefitController {
  private final BenefitService benefitService;
  @PostMapping
  public ResponseEntity<?> createBenefit(@RequestBody BenefitRequestDTO requestDTO) {
    var res = benefitService.create(requestDTO);

    return ResponseEntity.ok().body(res);
  }

  @GetMapping
  public ResponseEntity<?> getAllBenefits(@RequestParam(required = false, defaultValue = "ASC") String orderBy) {
    var res = benefitService.getAll();

    return ResponseEntity.ok().body(res);
  }
  @PutMapping("/{benefitId}")
  public ResponseEntity<?> updateBenefitInfo(@PathVariable String benefitId, @Valid @RequestBody BenefitRequestDTO requestDTO) {

    BenefitResponseDTO res = benefitService.update(benefitId, requestDTO);
    return ResponseEntity.ok().body(res);

  }
  @GetMapping("/{benefitId}")
  public ResponseEntity<?> getSpecificBenefit(@PathVariable String benefitId) {

    BenefitResponseDTO<Map<Language, String>> res = benefitService.getById(benefitId);
    return ResponseEntity.ok().body(res);

  }
  @DeleteMapping("/{benefitId}")
  public ResponseEntity<?> deleteBenefit(@PathVariable String benefitId) {

    benefitService.delete(benefitId);
    return ResponseEntity.ok().body(null);

  }
}
