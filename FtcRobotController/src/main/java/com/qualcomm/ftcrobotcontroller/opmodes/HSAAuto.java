package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

public class HSAAuto extends LinearOpMode {
    double basketPosition;

    DcMotor motorRight;
    DcMotor motorLeft;
    DcMotor motorShoulder;
    DcMotor motorElbow;
    Servo basket;

    public void initMotors() {
        motorRight = hardwareMap.dcMotor.get("right");
        motorLeft = hardwareMap.dcMotor.get("left");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        motorShoulder = hardwareMap.dcMotor.get("shoulder");
        motorElbow = hardwareMap.dcMotor.get("elbow");
        basket = hardwareMap.servo.get("bucket");
        basketPosition = 0.2;
        motorShoulder.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorElbow.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        motorShoulder.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorElbow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorShoulder.setPower(1);
        motorElbow.setPower(1);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initMotors();
        waitForStart();

        go(.5, 0.5, 50);
        turn(40);
        go(0, 0, 0);
    }

    private void turn(int degrees) throws InterruptedException {
        go(0.5, -0.5, degrees * 5);
    }

    private void go(double leftPower, double rightPower, int rightPositionDelta) throws InterruptedException {
        motorLeft.setPower(leftPower);
        motorRight.setPower(rightPower);

        int startingPosition = motorRight.getCurrentPosition();
        int targetPosition = startingPosition + rightPositionDelta;
        while (motorRight.getCurrentPosition() < targetPosition) {
            waitOneFullHardwareCycle();
        }
    }
}
