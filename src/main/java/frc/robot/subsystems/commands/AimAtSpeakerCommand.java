package frc.robot.subsystems.commands;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.commons.TimestampedVisionUpdate;
import frc.robot.subsystems.swerve.Swerve;
import java.util.List;
import org.littletonrobotics.junction.Logger;

public class AimAtSpeakerCommand extends Command {

  // Subsystems
  private Swerve swerve;

  // Feedback controllers
  private PIDController turnPID = new PIDController(7, 0, 0);

  public AimAtSpeakerCommand(Swerve swerve) {
    this.swerve = swerve;

    addRequirements(swerve);
  }

  @Override
  public void execute() {
    double setpoint = RobotContainer.visionSupplier.robotToSpeakerAngle().getRadians();
    double measurement = swerve.getPose().getRotation().getRadians();

    double output = turnPID.calculate(measurement, setpoint);

    if (RobotContainer.aprilTagVision.seesTag4()) {
      // Add vision data
      TimestampedVisionUpdate update =
          new TimestampedVisionUpdate(
              RobotContainer.aprilTagVision.getRobotToSpeakerTag(),
              RobotContainer.aprilTagVision.getSpeakerTagTimestamp(),
              VecBuilder.fill(0.00000005, 0.0000005, 1.0E6));

      RobotContainer.swerve.addVisionData(List.of(update));
    }

    // Subsystem Requests
    swerve.requestVelocity(new ChassisSpeeds(0, 0, output), true);

    Logger.recordOutput(
        "AimAtSpeakerCommand/measurementDegrees", Units.radiansToDegrees(measurement));
    Logger.recordOutput("AimAtSpeakerCommand/setpointDegrees", Units.radiansToDegrees(setpoint));
  }
}
