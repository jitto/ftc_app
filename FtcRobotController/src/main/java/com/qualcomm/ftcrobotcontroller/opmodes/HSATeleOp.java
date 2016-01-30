package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class HSATeleOp extends OpMode {

    final static double BASKET_MIN_RANGE  = 0.10;
    final static double BASKET_MAX_RANGE  = 0.9;

    double servoDelta = 0.008;

    DcMotor motorRight;
    DcMotor motorLeft;
    DcMotor motorWinch;
    Servo winchAngle;
    Servo armDirection;
    Servo elbow;
    Servo basket;

    @Override
    public void init() {
        motorRight = hardwareMap.dcMotor.get("right");
        motorLeft = hardwareMap.dcMotor.get("left");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorWinch = hardwareMap.dcMotor.get("winch");
        winchAngle = hardwareMap.servo.get("winchAngle");
        armDirection = hardwareMap.servo.get("armDirection");
        elbow = hardwareMap.servo.get("elbow");
        basket = hardwareMap.servo.get("bucket");
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

        motorWinch.setPower(gamepad1.right_stick_y / 4);

        updateServoPosition(winchAngle, gamepad1.a, gamepad2.b, BASKET_MIN_RANGE, BASKET_MAX_RANGE);
        updateServoPosition(armDirection, gamepad2.dpad_left, gamepad2.dpad_right, BASKET_MIN_RANGE, BASKET_MAX_RANGE);
        updateServoPosition(elbow, gamepad2.dpad_up, gamepad2.dpad_down, BASKET_MIN_RANGE, BASKET_MAX_RANGE);
        updateServoPosition(basket, gamepad2.a, gamepad2.b, BASKET_MIN_RANGE, BASKET_MAX_RANGE);

        telemetry.addData("x " + String.format("%.2f", gamepad1.left_stick_x) + " y ", String.format("%.2f", gamepad1.left_stick_y));
        telemetry.addData("left " + String.format("%.2f", left) + " right ", String.format("%.2f", right));
//        telemetry.addData("Current shoulder " + motorShoulder.getCurrentPosition() + " elbow ", motorElbow.getCurrentPosition());
//        telemetry.addData("Target shoulder " + motorShoulder.getTargetPosition() + " elbow ", motorElbow.getTargetPosition());
//        telemetry.addData("basket ", String.format("%.2f", basketPosition));
//        telemetry.addData("motorShoulderPower ", String.format("%.2f", motorShoulder.getPower()));
    }

    private void updateServoPosition(Servo servo, boolean incrementButton, boolean decrementButton, double minRange, double maxRange) {
        double currentValue = servo.getPosition();
        double basketMove = incrementButton ? servoDelta : decrementButton ? -servoDelta : 0;
        servo.setPosition(Range.clip(currentValue + basketMove, minRange, maxRange));
    }

    private double powerInRange(float power, float powerRange) {
        return Range.clip((power / 10) * (4 * powerRange), -1.0, 1.0);
    }
}
