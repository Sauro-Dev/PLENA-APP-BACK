package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.PlanDto.CreatePlanDto;
import com.plenamente.sgt.domain.dto.PlanDto.ListPlanDto;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;


    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Plan> createPlan(@Valid @RequestBody CreatePlanDto createPlanDto) {
        Plan plan = planService.createPlan(createPlanDto);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ListPlanDto>> getAllPlans() {
        List<ListPlanDto> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }
}
