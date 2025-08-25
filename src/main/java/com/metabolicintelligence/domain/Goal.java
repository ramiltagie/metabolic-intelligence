package com.metabolicintelligence.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false, length = 32)
    private GoalType goalType;

    @Min(20)
    @Max(400)
    @Column(name = "target_weight_kg")
    private Integer targetWeightKg;

    @Min(1)
    @Max(104)
    @Column(name = "target_timeline_weeks")
    private Integer targetTimelineWeeks;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_detail_level", nullable = false, length = 32)
    private PlanDetailLevel planDetailLevel;
}
