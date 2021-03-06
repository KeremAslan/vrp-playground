package app;


import common.optaplanner.basedomain.VehicleRoutingSolution;
import org.apache.commons.lang3.NotImplementedException;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import vrpproblems.ProblemType;
import vrpproblems.sintef.persistence.SintefReader;

import java.io.File;
import java.util.logging.Logger;

public class OptaplannerApp {

  /** Default logger for this class */
  private static final Logger LOG = Logger.getLogger(OptaplannerApp.class.getName());

  private ProblemType problemType;
  private VehicleRoutingSolution uninitiatedVrpProblem;
  private VehicleRoutingSolution solvedSolution;


  public OptaplannerApp(ProblemType problemType, File file) {
    this.problemType = problemType;
    init(file);
  }

  private void init(File file) {
    switch(problemType) {
      case SINTEF:
        SintefReader sintefReader = new SintefReader();
        uninitiatedVrpProblem = sintefReader.read(file);
        break;
      default:
        uninitiatedVrpProblem = null;
        LOG.warning("uninitiatedVrpProblem is not set because the problem type couldn't be recognized.");
    }
  }

  /**
   * Runs Optaplanner, stores and returns the solved solution.
   */

  public VehicleRoutingSolution run(Integer runtimeInMinutes) {
    if (uninitiatedVrpProblem == null || problemType == null)  {
      throw new NullPointerException("The variable uninitiatedVrpProblem is not yet initialised. Call OptaplannerApp.init() first to initialise a solution");
    }

    String optaplannerConfigurationFile;
    switch(problemType) {
      case SINTEF:
        optaplannerConfigurationFile = "vrpproblems/sintef/optaplanner/sintef-solver-configuration.xml";
        break;
      default:
        throw new NotImplementedException("Not implemented");
    }

    LOG.info("Starting optimisation of problem type " + problemType);
    SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource(optaplannerConfigurationFile);
    if (runtimeInMinutes != null) {
      solverFactory.getSolverConfig().getTerminationConfig().setSecondsSpentLimit(runtimeInMinutes * 60L);
    }
    Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();

    this.solvedSolution = solver.solve(uninitiatedVrpProblem);
    return solvedSolution;
  }


  public VehicleRoutingSolution getUninitiatedVrpProblem() {
    return uninitiatedVrpProblem;
  }

  public VehicleRoutingSolution getSolvedSolution() {
    return solvedSolution;
  }
}
