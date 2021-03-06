package vrp.integration;

import common.optaplanner.basedomain.DistanceType;
import common.optaplanner.basedomain.Vehicle;
import common.optaplanner.basedomain.VehicleRoutingSolution;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import vrp.testhelpers.OptaplannerHelper;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import vrpproblems.ProblemType;
import vrpproblems.sintef.domain.SintefJob;
import vrpproblems.sintef.domain.SintefVehicleRoutingSolution;

public class SintefScoreOptaplannerIntegrationTests {

  @Test
  public void mediumScoreOfSintefProblemWithOneVehicleShouldBeOne() {
    VehicleRoutingSolution problem = OptaplannerHelper.buildOptaplannerProblem(ProblemType.SINTEF, 1, 5);
    VehicleRoutingSolution solution = OptaplannerHelper.runOptaplannerWithBasicConfiguration(ProblemType.SINTEF, problem);

    BendableLongScore score = ((SintefVehicleRoutingSolution) solution).getScore();
    Assert.assertEquals(score.getSoftScore(0), -1);
  }

  @Test
  public void softScoreOfSintefProblemWithOneVehicleAndFirstFiveJobsShouldSameAsSummingDistanceOfASingleTour() {
    VehicleRoutingSolution problem = OptaplannerHelper.buildOptaplannerProblem(ProblemType.SINTEF, 1, 5);
    VehicleRoutingSolution solution = OptaplannerHelper.runOptaplannerWithBasicConfiguration(ProblemType.SINTEF, problem);

    BendableLongScore score = ((SintefVehicleRoutingSolution) solution).getScore();

    Vehicle vehicle = solution.getVehicles().get(0);
    SintefJob job = (SintefJob) vehicle.getNextJob();
    Long totalDistance = 0L; //jo b.getDistanceFromPreviousStandstill(DistanceType.STRAIGHT_LINE_DISTANCE);
    SintefJob lastJob = null;
    while (job != null) {
      totalDistance += job.getTravelTimeInSecondsFromPreviousStandstill(DistanceType.STRAIGHT_LINE_DISTANCE);
      lastJob = job;
      job = (SintefJob) job.getNextJob();
    }
    // Close loop
    totalDistance += lastJob.getTravelTimeInSecondsTo(DistanceType.STRAIGHT_LINE_DISTANCE, vehicle);
    Assert.assertEquals(score.getSoftScore(1), -totalDistance);
  }

}
