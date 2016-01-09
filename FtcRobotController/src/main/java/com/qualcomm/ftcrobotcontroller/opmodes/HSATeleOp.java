package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class HSATeleOp extends OpMode {

	final static double BASKET_MIN_RANGE  = 0.20;
	final static double BASKET_MAX_RANGE  = 0.7;

	double basketPosition;
	double basketDelta = 0.008;

	DcMotor motorRight;
	DcMotor motorLeft;
	DcMotor motorShoulder;
	DcMotor motorElbow;
	Servo basket;
    boolean waitingForPowerSwitch = true;

	@Override
	public void init() {
		motorRight = hardwareMap.dcMotor.get("right");
		motorLeft = hardwareMap.dcMotor.get("left");
		motorLeft.setDirection(DcMotor.Direction.REVERSE);
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
	public void loop() {
		float throttle = -gamepad1.left_stick_y;
		float direction = gamepad1.left_stick_x;
        float powerRange = gamepad1.right_trigger;

        double right = powerInRange(throttle - direction, powerRange);
		double left = powerInRange(throttle + direction, powerRange);

        motorRight.setPower(right);
        motorLeft.setPower(left);
		motorShoulder.setTargetPosition(motorShoulder.getCurrentPosition() + (int) (-gamepad2.left_stick_y * 10));
		motorElbow.setTargetPosition(motorElbow.getCurrentPosition() + (int) (gamepad2.right_stick_y * 10));

        if (gamepad2.right_bumper && waitingForPowerSwitch) {
            int newPower = motorShoulder.getPower() > 0.5 ? 0 : 1;
            motorShoulder.setPower(newPower);
            motorElbow.setPower(newPower);
        }
        waitingForPowerSwitch = !gamepad2.right_bumper;

        double basketMove = gamepad2.a ? basketDelta : gamepad2.b ? -basketDelta : 0;
        basketPosition = Range.clip(basketPosition + basketMove, BASKET_MIN_RANGE, BASKET_MAX_RANGE);
		basket.setPosition(basketPosition);

//        telemetry.addData("x " + String.format("%.2f", gamepad1.left_stick_x) + " y ", String.format("%.2f", gamepad1.left_stick_y));
//        telemetry.addData("left " + String.format("%.2f", left) + " right ", String.format("%.2f", right));
//        telemetry.addData("Current shoulder " + motorShoulder.getCurrentPosition() + " elbow ", motorElbow.getCurrentPosition());
//        telemetry.addData("Target shoulder " + motorShoulder.getTargetPosition() + " elbow ", motorElbow.getTargetPosition());
		telemetry.addData("basket ", String.format("%.2f", basketPosition));
		telemetry.addData("motorShoulderPower ", String.format("%.2f", motorShoulder.getPower()));
	}

    private double powerInRange(float power, float powerRange) {
        return Range.clip((power / 4) * (4 * powerRange), -1.0, 1.0);
    }
}
