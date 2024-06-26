package frc.robot.subsystems.elevatorpivot;

import org.littletonrobotics.junction.AutoLog;

/* Interface encapsulating elevator hardware */
public interface ElevatorIO {
  @AutoLog
  public static class ElevatorIOInputs {
    public double posMeters = 0.0;
    public double velMetersPerSecond = 0.0;
    public double motionMagicVelocityTarget = 0.0;
    public double motionMagicPositionTarget = 0.0;
    public double setpointMeters = 0.0;
    public double appliedVoltage = 0.0;
    public double[] currentAmps = new double[] {}; // {leader, follower}
    public double[] tempCelcius = new double[] {}; // {leader, follower}
    public double acceleration = 0.0;
  }

  /* Updates the set of loggable inputs */
  public default void updateInputs(ElevatorIOInputs inputs) {}

  /* Sets the climber to a height setpoint via motion magic */
  public default void setHeight(double heightMeters) {}

  /** Sets the climber to a specified voltage output */
  public default void setVoltage(double volts) {}

  /** Resets the encoder reading of the climber to a specified position */
  public default void resetHeight(double newHeightMeters) {}

  /* Sets the climber's neutral mode */
  public default void enableBrakeMode(boolean enable) {}

  /* Updates tunable numbers if neccesary */
  public default void updateTunableNumbers() {}
}
