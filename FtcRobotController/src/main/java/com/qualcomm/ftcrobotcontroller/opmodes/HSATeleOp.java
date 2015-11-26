package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class HSATeleOp extends OpMode {

	final static double BASKET_MIN_RANGE  = 0.20;
	final static double BASKET_MAX_RANGE  = 0.7;

	double basketPosition;
	double basketDelta = 0.1;

	DcMotor motorRight;
	DcMotor motorLeft;
	DcMotor motorShoulder;
	DcMotor motorElbow;
	Servo basket;

	@Override
	public void init() {
		motorRight = hardwareMap.dcMotor.get("right");
		motorLeft = hardwareMap.dcMotor.get("left");
		motorLeft.setDirection(DcMotor.Direction.REVERSE);
		motorShoulder = hardwareMap.dcMotor.get("shoulder");
		motorElbow = hardwareMap.dcMotor.get("elbow");
		basket = hardwareMap.servo.get("bucket");
		basketPosition = 0.2;
	}

	@Override
	public void loop() {
		float throttle = -gamepad1.left_stick_y;
		float direction = gamepad1.left_stick_x;
		double shoulderMotorPower = powWithSign(gamepad2.left_stick_y, 2);
		double elbowMotorPower = powWithSign(gamepad2.right_stick_y, 2);

        double right = powWithSign(throttle - direction, 2);
		double left = powWithSign(throttle + direction, 2);

		motorRight.setPower(right);
		motorLeft.setPower(left);
		motorShoulder.setPower(shoulderMotorPower);
		motorElbow.setPower(elbowMotorPower);

        double basketMove = gamepad2.x ? basketDelta : gamepad2.b ? -basketDelta : 0;
        basketPosition = Range.clip(basketPosition + basketMove, BASKET_MIN_RANGE, BASKET_MAX_RANGE);
		basket.setPosition(basketPosition);

        telemetry.addData("left " + String.format("%.2f", left) + " right ", String.format("%.2f", right));
        telemetry.addData("shoulder " + String.format("%.2f", shoulderMotorPower) + " elbow ", String.format("%.2f", elbowMotorPower));
		telemetry.addData("basket ", String.format("%.2f", basketPosition));
	}

    private double powWithSign(float number, int scalePower) {
        double scaledValue = Math.pow(Range.clip(number, -1.0, 1.0), scalePower);
        return (number < 0) ? -scaledValue : scaledValue;
    }
}
