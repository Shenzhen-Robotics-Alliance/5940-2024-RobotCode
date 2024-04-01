package frc.robot.autonomous.modes;

import com.pathplanner.lib.path.PathPlannerPath;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.commands.StationaryShootCommand;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.swerve.Swerve;
import frc.robot.subsystems.swerve.TrajectoryFollowerCommand;

public class SixNoteAmpSide extends SequentialCommandGroup {

  public SixNoteAmpSide(
      Superstructure superstructure, Swerve swerve, Shooter shooter, Intake intake) {
    addRequirements(superstructure, swerve, shooter, intake);
    addCommands(
        new InstantCommand(
            () -> {
              PathPlannerPath path = Robot.sixNoteAmpSideA;
              if (Robot.alliance == Alliance.Red) {
                path = path.flipPath();
              }
              swerve.resetPose(path.getPreviewStartingHolonomicPose());
            }),
        new InstantCommand(
            () -> {
              shooter.requestVisionSpeaker(false);
              superstructure.requestVisionSpeaker(true, true, false);
            }),
        new TrajectoryFollowerCommand(Robot.sixNoteAmpSideA, swerve, false, () -> true)
            .beforeStarting(
                () -> {
                  intake.requestIntake();
                  superstructure.requestIntake(true);
                  superstructure.requestVisionSpeaker(true, true, false);
                })
            .deadlineWith(new RunCommand(() -> shooter.requestVisionSpeaker(false))),
        new WaitUntilCommand(() -> !superstructure.hasPiece())
            .deadlineWith(new RunCommand(() -> shooter.requestVisionSpeaker(false))),
        new TrajectoryFollowerCommand(Robot.sixNoteAmpSideB, swerve, false, () -> true)
            .beforeStarting(
                () -> {
                  intake.requestIntake();
                  superstructure.requestIntake(true);
                  superstructure.requestVisionSpeaker(true, true, false);
                })
            .deadlineWith(new RunCommand(() -> shooter.requestVisionSpeaker(false))),
        new TrajectoryFollowerCommand(Robot.sixNoteAmpSideC, swerve, false, () -> true)
            .beforeStarting(
                () -> {
                  intake.requestIntake();
                  superstructure.requestIntake(true);
                  superstructure.requestVisionSpeaker(true, true, false);
                })
            .deadlineWith(new RunCommand(() -> shooter.requestVisionSpeaker(false))),
        new TrajectoryFollowerCommand(Robot.sixNoteAmpSideD, swerve, false, () -> true)
            .beforeStarting(
                () -> {
                  intake.requestIntake();
                  superstructure.requestIntake(true);
                  superstructure.requestVisionSpeaker(true, true, false);
                })
            .deadlineWith(new RunCommand(() -> shooter.requestVisionSpeaker(false))),
        new WaitUntilCommand(() -> !superstructure.hasPiece()).withTimeout(1.0),
        new TrajectoryFollowerCommand(
                Robot.sixNoteAmpSideE, swerve, false, () -> superstructure.hasPiece())
            .beforeStarting(
                () -> {
                  intake.requestIntake();
                  superstructure.requestIntake(true);
                  superstructure.requestVisionSpeaker(true, false, false);
                })
            .deadlineWith(new RunCommand(() -> shooter.requestVisionSpeaker(false))),
        new StationaryShootCommand(swerve, superstructure, shooter),
        new TrajectoryFollowerCommand(
                Robot.sixNoteAmpSideF, swerve, false, () -> superstructure.hasPiece())
            .beforeStarting(
                () -> {
                  intake.requestIntake();
                  superstructure.requestIntake(true);
                  superstructure.requestVisionSpeaker(true, false, false);
                })
            .deadlineWith(new RunCommand(() -> shooter.requestVisionSpeaker(false))),
        new StationaryShootCommand(swerve, superstructure, shooter));
  }
}
