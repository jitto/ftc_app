package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class HSATeleOp extends OpMode {
    DcMotor motorRight;
    DcMotor motorLeft;
    DcMotor motorWinch;
    Servo winchAngle;
    double winchAnglePosition = 0.7;
    Servo armDirection;
    double armDirectionPosition = 0.91;
    Servo elbow;
    double elbowPosition = 0.19;
    Servo basket;
    double basketPositon = 0;


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

        winchAngle.setPosition(winchAnglePosition);
        armDirection.setPosition(armDirectionPosition);
        elbow.setPosition(elbowPosition);
        basket.setPosition(basketPositon);
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

        winchAnglePosition = updateServoPosition(winchAnglePosition, gamepad2.dpad_up, gamepad2.dpad_down, 0.001, 0, 1);
        winchAngle.setPosition(winchAnglePosition);
        armDirectionPosition = updateServoPosition(armDirectionPosition, gamepad2.dpad_right, gamepad2.dpad_left, 0.002, 0, 1);
        armDirection.setPosition(armDirectionPosition);
        elbowPosition = updateServoPosition(elbowPosition, gamepad2.x, gamepad2.y, 0.002, 0, 1);
        elbow.setPosition(elbowPosition);
        basketPositon = updateServoPosition(basketPositon, gamepad2.a, gamepad2.b, 0.01, 0, 1);
        basket.setPosition(basketPositon);

        telemetry.addData("winchAngle " + String.format("%.2f", winchAngle.getPosition()), " armDirection" + String.format("%.2f", armDirection.getPosition()));
        telemetry.addData("elbow " + String.format("%.2f", elbow.getPosition()), " basket" + String.format("%.2f", basket.getPosition()));

//        telemetry.addData("x " + String.format("%.2f", gamepad1.left_stick_x) + " y ", String.format("%.2f", gamepad1.left_stick_y));
//        telemetry.addData("left " + String.format("%.2f", left) + " right ", String.format("%.2f", right));
//        telemetry.addData("Current shoulder " + motorShoulder.getCurrentPosition() + " elbow ", motorElbow.getCurrentPosition());
//        telemetry.addData("Target shoulder " + motorShoulder.getTargetPosition() + " elbow ", motorElbow.getTargetPosition());
//        telemetry.addData("basket ", String.format("%.2f", basketPosition));
//        telemetry.addData("motorShoulderPower ", String.format("%.2f", motorShoulder.getPower()));
    }

    private double updateServoPosition(double currentValue, boolean incrementButton, boolean decrementButton, double delta, double minRange, double maxRange) {
        double basketMove = incrementButton ? delta : decrementButton ? -delta : 0;
        return Range.clip(currentValue + basketMove, minRange, maxRange);
    }

    private double powerInRange(float power, float powerRange) {
        return Range.clip((power / 10) * (4 * powerRange), -1.0, 1.0);
    }
}
