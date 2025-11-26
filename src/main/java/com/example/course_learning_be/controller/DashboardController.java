package com.example.course_learning_be.controller;

import com.example.course_learning_be.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Validated
@Slf4j(topic = "CURRICULUM-CONTROLLER")
@RequiredArgsConstructor
public class DashboardController {
  private final DashboardService dashboardService;
  @GetMapping("/first-part")
  public ResponseEntity<?> getFirstPart() {

    var res = dashboardService.getFirstPart();
    return ResponseEntity.ok().body(res);
  }
  @GetMapping("/fifth-part")
  public ResponseEntity<?> getFifthPart() {

    var res = dashboardService.getFifthPart();
    return ResponseEntity.ok().body(res);
  }
  @GetMapping("/sixth-part")
  public ResponseEntity<?> getSixthPart() {

    var res = dashboardService.getSixthPart();
    return ResponseEntity.ok().body(res);
  }
}
