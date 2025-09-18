package com.example.mdp_group_31;

import static com.example.mdp_group_31.Home.refreshMessageReceivedNS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Arrays;

public class ControlFragment extends Fragment {
    // Bluetooth message handler for TARGET messages
    public static void handleBluetoothMessage(String message) {
        if (message != null && message.startsWith("TARGET")) {
            String[] parts = message.split(",");
            if (parts.length >= 3) {
                try {
                    int obstacleNum = Integer.parseInt(parts[1]);
                    int targetId = Integer.parseInt(parts[2]);
                    String face = (parts.length > 3) ? parts[3] : null;
                    if (gridMap != null) {
                        gridMap.displayTargetIdOnObstacle(obstacleNum, targetId, face);
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing TARGET message: " + message);
                }
            }
        }
    }
    private static final String TAG = "ControlFragment";

    SharedPreferences sharedPreferences;

    // Control Button
    ImageButton moveForwardImageBtn, turnRightImageBtn, moveBackImageBtn, turnLeftImageBtn,turnbleftImageBtn,turnbrightImageBtn;
    ImageButton exploreResetButton, fastestResetButton;
    private static long exploreTimer, fastestTimer;
    public static ToggleButton exploreButton, fastestButton;
    public static TextView exploreTimeTextView, fastestTimeTextView, robotStatusTextView;
    private static GridMap gridMap;

    // Timer
    public static Handler timerHandler = new Handler();

    //Button startSend;

    public static Runnable timerRunnableExplore = new Runnable() {
        @Override
        public void run() {
            long millisExplore = System.currentTimeMillis() - exploreTimer;
            int secondsExplore = (int) (millisExplore / 1000);
            int minutesExplore = secondsExplore / 60;
            secondsExplore = secondsExplore % 60;

            if (!Home.stopTimerFlag) {
                exploreTimeTextView.setText(String.format("%02d:%02d", minutesExplore,
                        secondsExplore));
                timerHandler.postDelayed(this, 500);
            }
        }
    };

    public static Runnable timerRunnableFastest = new Runnable() {
        @Override
        public void run() {
            long millisFastest = System.currentTimeMillis() - fastestTimer;
            int secondsFastest = (int) (millisFastest / 1000);
            int minutesFastest = secondsFastest / 60;
            secondsFastest = secondsFastest % 60;

            if (!Home.stopWk9TimerFlag) {
                fastestTimeTextView.setText(String.format("%02d:%02d", minutesFastest,
                        secondsFastest));
                timerHandler.postDelayed(this, 500);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate
        View root = inflater.inflate(R.layout.controls, container, false);

        // get shared preferences
        sharedPreferences = getActivity().getSharedPreferences("Shared Preferences",
                Context.MODE_PRIVATE);

        // variable initialization
        moveForwardImageBtn = Home.getUpBtn();
        turnRightImageBtn = Home.getRightBtn();
        moveBackImageBtn = Home.getDownBtn();
        turnLeftImageBtn = Home.getLeftBtn();
        turnbleftImageBtn = Home.getbLeftBtn();
        turnbrightImageBtn = Home.getbRightBtn();
        exploreTimeTextView = root.findViewById(R.id.exploreTimeTextView2);
        fastestTimeTextView = root.findViewById(R.id.fastestTimeTextView2);
        exploreButton = root.findViewById(R.id.exploreToggleBtn2);
        fastestButton = root.findViewById(R.id.fastestToggleBtn2);
        exploreResetButton = root.findViewById(R.id.exploreResetImageBtn2);
        fastestResetButton = root.findViewById(R.id.fastestResetImageBtn2);
        robotStatusTextView = Home.getRobotStatusTextView();
        fastestTimer = 0;
        exploreTimer = 0;
        //startSend = root.findViewById(R.id.startSend); //just added, need to test

        gridMap = Home.getGridMap();

        // Button Listener
        moveForwardImageBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        showLog("Clicked moveForwardImageBtn");
        if (gridMap.getCanDrawRobot()) {
            int[] coord = gridMap.getCurCoord();
            int col = coord[0];
            int row = coord[1];
            // Check next cell for obstacle before moving
            if (row < 19 && gridMap.isCellFree(col, row + 1)) {
                gridMap.moveOneCell("up");
                Home.refreshLabel();
                gridMap.invalidate();
                updateStatus("Robot moved forward");
                Home.printMessage("f");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot moved forward.");
            } else if (row < 19 && !gridMap.isCellFree(col, row + 1)) {
                updateStatus("Robot is blocked by obstacle!");
                Home.printMessage("obstacle");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot is blocked by obstacle!");
            } else {
                updateStatus("Robot cannot move forward, at grid edge.");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot cannot move forward, at grid edge.");
            }
        } else {
            updateStatus("Please press 'SET START POINT'");
        }
        showLog("Exiting moveForwardImageBtn");
    }
});

turnRightImageBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        showLog("Clicked turnRightImageBtn");
        if (gridMap.getCanDrawRobot()) {
            int[] coord = gridMap.getCurCoord();
            int col = coord[0];
            int row = coord[1];
            if (col < 20 && gridMap.isCellFree(col + 1, row)) {
                gridMap.moveOneCell("right");
                Home.refreshLabel();
                gridMap.invalidate();
                updateStatus("Robot turned right");
                Home.printMessage("tr");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot turned right.");
            } else if (col < 20 && !gridMap.isCellFree(col + 1, row)) {
                updateStatus("Robot is blocked by obstacle!");
                Home.printMessage("obstacle");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot is blocked by obstacle!");
            } else {
                updateStatus("Robot cannot turn right, at grid edge.");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot cannot turn right, at grid edge.");
            }
        } else {
            updateStatus("Please press 'SET START POINT'");
        }
        showLog("Exiting turnRightImageBtn");
    }
});

turnbrightImageBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (robotStatusTextView != null) {
            robotStatusTextView.setText("Robot action: turning back right");
        }
        showLog("Clicked turnbRightImageBtn");
        if (gridMap.getCanDrawRobot()) {
            gridMap.moveOneCell("backright");
            Home.refreshLabel();
            gridMap.invalidate();
            updateStatus("turning back right");
            Home.printMessage("br");
            System.out.println(Arrays.toString(gridMap.getCurCoord()));
        } else {
            updateStatus("Please press 'SET START POINT'");
        }
        showLog("Exiting turnbRightImageBtn");
    }
});

moveBackImageBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        showLog("Clicked moveBackwardImageBtn");
        if (gridMap.getCanDrawRobot()) {
            int[] coord = gridMap.getCurCoord();
            int col = coord[0];
            int row = coord[1];
            if (row > 1 && gridMap.isCellFree(col, row - 1)) {
                gridMap.moveOneCell("down");
                Home.refreshLabel();
                gridMap.invalidate();
                updateStatus("Robot moved backward");
                Home.printMessage("r");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot moved backward.");
            } else if (row > 1 && !gridMap.isCellFree(col, row - 1)) {
                updateStatus("Robot is blocked by obstacle!");
                Home.printMessage("obstacle");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot is blocked by obstacle!");
            } else {
                updateStatus("Robot cannot move backward, at grid edge.");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot cannot move backward, at grid edge.");
            }
        } else {
            updateStatus("Please press 'SET START POINT'");
        }
        showLog("Exiting moveBackwardImageBtn");
    }
});

turnLeftImageBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        showLog("Clicked turnLeftImageBtn");
        if (gridMap.getCanDrawRobot()) {
            int[] coord = gridMap.getCurCoord();
            int col = coord[0];
            int row = coord[1];
            if (col > 2 && gridMap.isCellFree(col - 1, row)) {
                gridMap.moveOneCell("left");
                Home.refreshLabel();
                gridMap.invalidate();
                updateStatus("Robot turned left");
                Home.printMessage("tl");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot turned left.");
            } else if (col > 2 && !gridMap.isCellFree(col - 1, row)) {
                updateStatus("Robot is blocked by obstacle!");
                Home.printMessage("obstacle");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot is blocked by obstacle!");
            } else {
                updateStatus("Robot cannot turn left, at grid edge.");
                if (robotStatusTextView != null) robotStatusTextView.setText("Robot cannot turn left, at grid edge.");
            }
        } else {
            updateStatus("Please press 'SET START POINT'");
        }
        showLog("Exiting turnLeftImageBtn");
    }
});

turnbleftImageBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (robotStatusTextView != null) {
            robotStatusTextView.setText("Robot action: turning back left");
        }
        showLog("Clicked turnbLeftImageBtn");
        if (gridMap.getCanDrawRobot()) {
            gridMap.moveOneCell("backleft");
            Home.refreshLabel();
            gridMap.invalidate();
            updateStatus("turning back left");
            Home.printMessage("bl");
        } else {
            updateStatus("Please press 'SET START POINT'");
        }
        showLog("Exiting turnbLeftImageBtn");
    }
});


        // Start Task 1 challenge
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked Task 1 Btn (exploreToggleBtn)");
                ToggleButton exploreToggleBtn = (ToggleButton) v;

                if (exploreToggleBtn.getText().equals("TASK 1 START")) {
                    showToast("Task 1 timer stop!");
                    robotStatusTextView.setText("Task 1 Stopped");
                    timerHandler.removeCallbacks(timerRunnableExplore);
                }
                else if (exploreToggleBtn.getText().equals("STOP")) {
                    // Get String value that represents obstacle configuration
                    String msg = gridMap.getObstacles();
                    // Send this String over via BT
                    //Home.printCoords(msg);
                    //Send BEGIN to the robot
                    Home.printMessage("BEGIN"); //send a string "BEGIN" to the RPI
                    // Start timer
                    Home.stopTimerFlag = false;
                    showToast("Task 1 timer start!");

                    robotStatusTextView.setText("Task 1 Started");
                    exploreTimer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnableExplore, 0);
                }
                else {
                    showToast("Else statement: " + exploreToggleBtn.getText());
                }
                showLog("Exiting exploreToggleBtn");
            }
        });


        //Start Task 2 Challenge Timer
        fastestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked Task 2 Btn (fastestToggleBtn)");
                ToggleButton fastestToggleBtn = (ToggleButton) v;
                if (fastestToggleBtn.getText().equals("TASK 2 START")) {
                    showToast("Task 2 timer stop!");
                    robotStatusTextView.setText("Task 2 Stopped");
                    timerHandler.removeCallbacks(timerRunnableFastest);
                }
                else if (fastestToggleBtn.getText().equals("STOP")) {
                    showToast("Task 2 timer start!");
                    Home.printMessage("BEGIN"); //send a string "BEGIN" to the RPI
                    Home.stopWk9TimerFlag = false;
                    robotStatusTextView.setText("Task 2 Started");
                    fastestTimer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnableFastest, 0);
                }
                else
                    showToast(fastestToggleBtn.getText().toString());
                showLog("Exiting fastestToggleBtn");
            }
        });

        exploreResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked exploreResetImageBtn");
                showToast("Resetting exploration time...");
                exploreTimeTextView.setText("00:00");
                robotStatusTextView.setText("Not Available");
                if(exploreButton.isChecked())
                    exploreButton.toggle();
                timerHandler.removeCallbacks(timerRunnableExplore);
                showLog("Exiting exploreResetImageBtn");
            }
        });

        fastestResetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showLog("Clicked fastestResetImgBtn");
                showToast("Resetting Fastest Time...");
                fastestTimeTextView.setText("00:00");
                robotStatusTextView.setText("Fastest Car Finished");
                if(fastestButton.isChecked()){
                    fastestButton.toggle();
                }
                timerHandler.removeCallbacks(timerRunnableFastest);
                showLog("Exiting fastestResetImgBtn");
            }
        });

        /*
        startSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showLog("Clicked startSendBtn");
                showToast("Sending BEGIN to robot...");
                exploreButton.toggle();
                if (exploreButton.getText().equals("WK8 START")) {
                    showToast("Auto Movement/ImageRecog timer stop!");
                    robotStatusTextView.setText("Auto Movement Stopped");
                    timerHandler.removeCallbacks(timerRunnableExplore);
                }
                else if (exploreButton.getText().equals("STOP")) {
                    // Get String value that represents obstacle configuration
                    String msg = gridMap.getObstacles();
                    // Send this String over via BT
                    //Home.printCoords(msg);
                    // Start timer
                    Home.stopTimerFlag = false;
                    showToast("Auto Movement/ImageRecog timer start!");

                    robotStatusTextView.setText("Auto Movement Started");
                    exploreTimer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnableExplore, 0);
                }
                //ok
                Home.printMessage("BEGIN"); //send a string "BEGIN" to the RPI
                showLog("Exiting startSend");
            }
        });
         */

        return root;
    }



    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void updateStatus(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0, 0);
        toast.show();
    }
}