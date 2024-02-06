package frc.robot.subsystems.shooter;

import static frc.robot.constants.Constants.Shooter.*;
import static frc.robot.constants.Constants.Feeder.*;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.util.Units;
import frc.robot.commons.LoggedTunableNumber;

public class ShooterIOKrakenX60 implements ShooterIO {

  /* Hardware */
  private final TalonFX left = new TalonFX(SHOOTER_LEFT_ID);
  private final TalonFX right = new TalonFX(SHOOTER_RIGHT_ID);

  /* Configurators */
  private TalonFXConfigurator leftConfigurator;
  private TalonFXConfigurator rightConfigurator;

  private CurrentLimitsConfigs shooterCurrentLimitConfigs;
  private MotorOutputConfigs leftMotorOutputConfigs;
  private MotorOutputConfigs rightMotorOutputConfigs;
  private Slot0Configs shooterSlot0Configs;

  /* Gains */
  LoggedTunableNumber shooterkS = new LoggedTunableNumber("Shooter/kS", 0.0);
  LoggedTunableNumber  shooterkV =
      new LoggedTunableNumber("Shooter/kV", (12.0 / SHOOTER_MAX_VELOCITY));
  LoggedTunableNumber  shooterkP = new LoggedTunableNumber("Shooter/kP", 0.5);
  LoggedTunableNumber  shooterkI = new LoggedTunableNumber("Shooter/kI", 0.0);
  LoggedTunableNumber shooterkD = new LoggedTunableNumber("Shooter/kD", 0.0);

  public void ShooterIOFalcon500() {
    /* Instantiate configuators */
    leftConfigurator = left.getConfigurator();
    rightConfigurator = right.getConfigurator();

    /* Create configs */

    // Current Limit configs
    shooterCurrentLimitConfigs = new CurrentLimitsConfigs();
    shooterCurrentLimitConfigs.StatorCurrentLimitEnable = true;
    shooterCurrentLimitConfigs.StatorCurrentLimit = 250.0;

    // Motor output configs
    leftMotorOutputConfigs = new MotorOutputConfigs();
    leftMotorOutputConfigs.Inverted = SHOOTER_LEFT_INVERSION;
    leftMotorOutputConfigs.PeakForwardDutyCycle = 1.0;
    leftMotorOutputConfigs.PeakReverseDutyCycle = -1.0;
    leftMotorOutputConfigs.NeutralMode = NeutralModeValue.Coast;

    rightMotorOutputConfigs = new MotorOutputConfigs();
    rightMotorOutputConfigs.Inverted = SHOOTER_RIGHT_INVERSION;
    rightMotorOutputConfigs.PeakForwardDutyCycle = 1.0;
    rightMotorOutputConfigs.PeakReverseDutyCycle = -1.0;
    rightMotorOutputConfigs.NeutralMode = NeutralModeValue.Coast;

    // Slot 0 Configs
    shooterSlot0Configs = new Slot0Configs();
    shooterSlot0Configs.kS = shooterkS.get();
    shooterSlot0Configs.kV = shooterkV.get();
    shooterSlot0Configs.kP = shooterkP.get();
    shooterSlot0Configs.kI = shooterkI.get();
    shooterSlot0Configs.kD = shooterkD.get();


    /* Apply Configurations */
    leftConfigurator.apply(shooterCurrentLimitConfigs);
    leftConfigurator.apply(leftMotorOutputConfigs);
    leftConfigurator.apply(shooterSlot0Configs);

    rightConfigurator.apply(shooterCurrentLimitConfigs);
    rightConfigurator.apply(rightMotorOutputConfigs);
    rightConfigurator.apply(shooterSlot0Configs);

    left.optimizeBusUtilization();
    right.optimizeBusUtilization();
  }

  @Override
  public void updateInputs(ShooterIOInputs inputs) {
    // Shooter left
    inputs.shooterLeftPosRad =
        Units.rotationsToRadians(left.getPosition().getValue()) / SHOOTER_LEFT_GEAR_RATIO;
    inputs.shooterLeftVelocityRpm = left.getVelocity().getValue() * 60.0;
    inputs.shooterLeftAppliedVolts = left.getMotorVoltage().getValue();
    inputs.shooterLeftTempCelcius = left.getDeviceTemp().getValue();

    // Shooter right
    inputs.shooterRightPosRad =
        Units.rotationsToRadians(right.getPosition().getValue()) / SHOOTER_RIGHT_GEAR_RATIO;
    inputs.shooterRightVelocityRpm = right.getVelocity().getValue() * 60.0;
    inputs.shooterRightAppliedVolts = right.getMotorVoltage().getValue();
    inputs.shooterRightTempCelcius = right.getDeviceTemp().getValue();

    inputs.shooterCurrentAmps =
        new double[] {
          left.getSupplyCurrent().getValue(), right.getSupplyCurrent().getValue()
        };

  }

  @Override
  public void setPercent(double percentLeft, double percentRight) {
    left.setControl(new DutyCycleOut(percentLeft));
    right.setControl(new DutyCycleOut(percentRight));
  }

  @Override
  public void setVelocity(double velocityLeft, double velocityRight) {
    if (velocityLeft > 0.0) {
      left.setControl(new VelocityVoltage(velocityLeft));
    } else {
      left.setControl(new DutyCycleOut(0.0));
    }

    if (velocityRight > 0.0) {
      right.setControl(new VelocityVoltage(velocityRight));
    } else {
      right.setControl(new DutyCycleOut(0.0));
    }
  }

  @Override
  public void setCurrentLimit(
      double currentLimit, double supplyCurrentThreshold, double supplyTimeThreshold) {
    shooterCurrentLimitConfigs.StatorCurrentLimitEnable = true;
    shooterCurrentLimitConfigs.StatorCurrentLimit = currentLimit;
    shooterCurrentLimitConfigs.SupplyCurrentThreshold = supplyCurrentThreshold;
    shooterCurrentLimitConfigs.SupplyTimeThreshold = supplyTimeThreshold;

    leftConfigurator.apply(shooterCurrentLimitConfigs);
    rightConfigurator.apply(shooterCurrentLimitConfigs);
  }

  @Override
  public void enableBrakeMode(boolean enable) {
    leftMotorOutputConfigs.NeutralMode = enable ? NeutralModeValue.Brake : NeutralModeValue.Coast;
    rightMotorOutputConfigs.NeutralMode = enable ? NeutralModeValue.Brake : NeutralModeValue.Coast;

    leftConfigurator.apply(leftMotorOutputConfigs);
    rightConfigurator.apply(rightMotorOutputConfigs);
  }

  @Override
  public void updateTunableNumbers() {
    if (shooterkS.hasChanged(0)
        || shooterkV.hasChanged(0)
        || shooterkP.hasChanged(0)
        || shooterkI.hasChanged(0)
        || shooterkD.hasChanged(0)) {
      shooterSlot0Configs.kS = shooterkS.get();
      shooterSlot0Configs.kV = shooterkV.get();
      shooterSlot0Configs.kP = shooterkP.get();
      shooterSlot0Configs.kI = shooterkI.get();
      shooterSlot0Configs.kD = shooterkD.get();

      leftConfigurator.apply(shooterSlot0Configs);
      rightConfigurator.apply(shooterSlot0Configs);
    }
  }
}