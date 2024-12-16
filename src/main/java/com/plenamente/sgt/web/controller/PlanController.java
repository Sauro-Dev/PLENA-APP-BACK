package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.PlanDto.PlanDto;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
}
