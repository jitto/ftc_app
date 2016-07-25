package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;

public class HSATeleOp extends OpMode {
    DcMotor motorRight;
    DcMotor motorLeft;
    Servo elbow;
    double elbowPosition = 0.00;
    boolean servoOn = false;

    @Override
    public void init() {
        motorRight = hardwareMap.dcMotor.get("right");
        motorLeft = hardwareMap.dcMotor.get("left");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        elbow = hardwareMap.servo.get("elbow");
        elbow.setPosition(elbowPosition);
    }

    @Override
    public void loop() {
        float throttle = -gamepad1.left_stick_y;
        float direction = gamepad1.left_stick_x;
        float powerRange = gamepad1.right_trigger;

        double right = powerInRange(throttle - direction, powerRange);
        double left = powerInRange(throttle + direction, powerRange);

        motorRight.setPower(right);
        motorLeft.setPower(left);

        servoOn = !gamepad2.x && (gamepad2.a || servoOn);

        if (servoOn) {
            elbowPosition = updateServoPosition(elbowPosition, gamepad2.right_stick_y < -0.1, gamepad2.right_stick_y > 0.1, Math.abs(gamepad2.right_stick_y) / 200, 0, 1);
            elbow.getController().pwmEnable();
            elbow.setPosition(elbowPosition);
        } else {
            elbow.getController().pwmDisable();
        }

        telemetry.addData("elbow ", String.format("%.2f", elbow.getPosition()));
        telemetry.addData("test ", elbow.getController().getPwmStatus());
    }

    private double updateServoPosition(double currentValue, boolean incrementButton, boolean decrementButton, double delta, double minRange, double maxRange) {
        double basketMove = incrementButton ? delta : decrementButton ? -delta : 0;
        return Range.clip(currentValue + basketMove, minRange, maxRange);
    }

    private double powerInRange(float power, float powerRange) {
        return Range.clip((power / 10) * (4 * powerRange), -1.0, 1.0);
    }
}
