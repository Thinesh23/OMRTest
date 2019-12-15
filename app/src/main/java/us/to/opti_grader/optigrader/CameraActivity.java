package us.to.opti_grader.optigrader;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Camera;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import us.to.opti_grader.optigrader.Common.Common;

import static android.Manifest.permission.CAMERA;

public class CameraActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String    TAG = "OCVSample::Activity";

    private int                    noOfQuestions, noOfChoices;

    private Mat                    mRgba;
    private Mat                    altframe;
    private Mat                    mIntermediateMat;
    private Mat                    mIntermediateMat2;
    private Mat                    mIntermediateMat3;

    private CameraBridgeViewBase   mOpenCvCameraView;

    private boolean                pressed;
    private boolean                start = false;
    private boolean                second = false;
    private boolean                third = false;
    private boolean                answerFilled;
    private String                 tempAnswers;
    ArrayList<String>              answer;

    MatOfPoint                     maxContour;
    MatOfPoint                     minContour;

    private ImageView btnConfirm;
    private ImageView btnRetry;
    private ImageView btnAdd;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

/* Called when the activity is first created.*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_CROSSFADE;
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.rotationAnimation = rotationAnimation;
        win.setAttributes(winParams);

        setContentView(R.layout.activity_camera);

        ActivityCompat.requestPermissions(CameraActivity.this,
                new String[] {CAMERA}, 1);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        Intent i = getIntent();
        if (i != null){
            noOfChoices = Integer.parseInt(i.getStringExtra("noOfChoices"));
            noOfQuestions = Integer.parseInt(i.getStringExtra("noOfAnswers"));
        }

        btnConfirm = (ImageView) findViewById(R.id.button_confirm);
        btnRetry = (ImageView) findViewById(R.id.button_retry);
        btnAdd = (ImageView) findViewById(R.id.button_add);

        btnAdd.setEnabled(false);

        btnConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (start && second) {
                    // If camera has been initialized and the screen has been pressed a second time (to confirm contour).
                    // Start processing image.  This code transforms, crops, and detects the answer circles.
                    pressed = true;
                    Log.i(TAG, "Screen pressed");

                    // Approximates contour with less vertexes
                    MatOfPoint2f m2f = new MatOfPoint2f();
                    maxContour.convertTo(m2f, CvType.CV_32FC2);
                    double arc = Imgproc.arcLength(m2f, true);
                    MatOfPoint2f approx = new MatOfPoint2f();
                    Imgproc.approxPolyDP(m2f, approx, arc * 0.01, true);
                    MatOfPoint contour = new MatOfPoint();
                    approx.convertTo(contour, CvType.CV_32S);

                    // Get the centroid of the image
                    Moments moment = Imgproc.moments(contour);
                    int x = (int) (moment.get_m10() / moment.get_m00());
                    int y = (int) (moment.get_m01() / moment.get_m00());

                    Point[] sortedPoints = new Point[4];

                    // Using that centroid, find the outermost points on the image's matrix.
                    double[] data;
                    int count = 0;
                    Log.i(TAG, "Screen pressed2: " + contour.rows());
                    for (int i = 0; i < contour.rows(); i++) {
                        data = contour.get(i, 0);
                        double datax = data[0];
                        double datay = data[1];
                        if (datax < x && datay < y) {
                            sortedPoints[0] = new Point(datax, datay);
                            count++;
                        } else if (datax > x && datay < y) {
                            sortedPoints[1] = new Point(datax, datay);
                            count++;
                        } else if (datax < x && datay > y) {
                            sortedPoints[2] = new Point(datax, datay);
                            count++;
                        } else if (datax > x && datay > y) {
                            sortedPoints[3] = new Point(datax, datay);
                            count++;
                        }
                    }
                    // ^ BUG HERE WHERE NOT TAKING INTO ACCOUNT STRANGE CORNER.  Can ignore for now.  Not that important

                    // Corners of material to perspective transform from
                    MatOfPoint2f src = new MatOfPoint2f(
                            sortedPoints[0],
                            sortedPoints[1],
                            sortedPoints[2],
                            sortedPoints[3]);

                    // Corners of material to perspective transform to
                    MatOfPoint2f dst = new MatOfPoint2f(
                            new Point(0, 0),
                            new Point(1100 - 1, 0),
                            new Point(0, 550 - 1),
                            new Point(1100 - 1, 550 - 1)
                    );

                    // Get transform to warp how we want
                    Mat warpMat = Imgproc.getPerspectiveTransform(src, dst);

                    // Warp image/material with transform
                    Mat destImage = new Mat();
                    Imgproc.warpPerspective(altframe, destImage, warpMat, mRgba.size());

                    // Isolate scantron answers
                    Rect scantron = new Rect(0, 0, 1920, 1080);

                    // Crop image (filter out any unnecessary data)
                    Mat cropped = new Mat(destImage, scantron);

                    // Reminder: OpenCV is in landscape!  This means if you're holding your phone upright, x axis is y and y axis is x.
                    // Coordinates (0,0) are the top right of the phone screen. (Still holding phone upright)

                    // Base material to display to users.  SCREEN-SPECIFC!  It only works with camera's initialized to the 1920*1080 resolution.
                    // Deviating from this resolution will result in an app crash bc cant place small material on bigger screen. rgb is 0,0,0 so all pixels initialized to black.
                    Mat incoming = new Mat(1080, 1920, CvType.CV_8UC4, new Scalar(0, 0, 0));

                    // In order to insert cropped image into base material, have to adjust the Region of Interest.  Sad to say it took me 4 hours to figure this out with no help from the internet forums.
                    incoming.adjustROI(0, -(1080 - scantron.height), 0, -(1920 - scantron.width));
                    cropped.copyTo(incoming);
                    incoming.adjustROI(0, 1080 - scantron.height, 0, 1920 - scantron.width);

                    // Filtering and circle detection.  The Hough Circles params are *super* delicate, so treat with care.
                    //Mat circles = new Mat();

                    //Convert color to gray
                    Imgproc.cvtColor(incoming, mIntermediateMat, 7);
                    //Imgproc.GaussianBlur(mIntermediateMat, mIntermediateMat, new Size(5, 5), 0);
                    //Imgproc.Canny(mIntermediateMat, mIntermediateMat, 75, 200);
                    //Imgproc.HoughCircles(mIntermediateMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 9, 200, 12, 5, 10);
                    //Imgproc.HoughCircles(mIntermediateMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 15, 150, 18, 15, 20);


                    //Mat thresh = new Mat(mIntermediateMat.size(), CvType.CV_8UC1);
                    //List<MatOfPoint> bubbles;
                    //Mat thresh = new Mat(mIntermediateMat.size(), CvType.CV_8UC1);
                    //Imgproc.threshold(mIntermediateMat, thresh, 150, 250, Imgproc.THRESH_BINARY_INV | Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C);
                    //Imgproc.Canny(mIntermediateMat, thresh, 75, 200);
                    //bubbles = detectContours(thresh);
                    //FilterByCircle(thresh, bubbles);
                    //filterForLargestContours(bubbles, 10);
                    //detectShapes(10, 100, bubbles);
                    Mat circles = detectCircle(mIntermediateMat, 20);
                    //Imgproc.findContours(mIntermediateMat, bubbles, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);


/*                    for (int contourInx = 0; contourInx < bubbles.size(); contourInx++) {
                        Imgproc.drawContours(incoming, bubbles, contourInx, new Scalar(57, 255, 20), 2);
                    }*/

                    List<Point> points = new ArrayList<Point>();

                    // Draw Hough Circles onto base material
                    for (int i = 0; i < circles.cols(); i++) {
                        double[] vCircle = circles.get(0, i);

                        Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                        int radius = (int) Math.round(vCircle[2]);
                        points.add(pt);

                        //Imgproc.circle(incoming, pt, radius, new Scalar(57, 255, 20), 2);
                    }

                    // Sort by y axis (when holding phone upright)
                    Collections.sort(points, new Comparator<Point>() {
                        public int compare(Point o1, Point o2) {
                            int result = 0;
                            if (o1.x > o2.x)
                                result = 1;
                            else if (o1.x < o2.x)
                                result = -1;
                            else
                                result = 0;

                            return result;
                        }
                    });

                    // Group by 5s.  Only if number of answers is divisible by 5 (will crash otherwise)
                    List<List<Point>> points_grouped = new ArrayList<List<Point>>();
                    if (points.size() == (noOfChoices * noOfQuestions)) {
                        List<Point> temp;
                        for (int i = 0; i < points.size() / noOfChoices; i++) {
                            temp = new ArrayList<>();

                            temp.add(points.get(i * noOfChoices));
                            temp.add(points.get(i * noOfChoices + 1));
                            temp.add(points.get(i * noOfChoices + 2));
                            temp.add(points.get(i * noOfChoices + 3));
                            if (noOfChoices == 5) {
                                temp.add(points.get(i * noOfChoices + 4));
                            }

                            points_grouped.add(temp);
                        }
                   }
                    else
                    {
                        second = false;
                        pressed = false;

                        Toast.makeText(getApplicationContext(),"Error: Invalid number of circles, try again." + points.size() +" "+ noOfQuestions*noOfChoices,Toast.LENGTH_LONG).show();

                        return;
                    }



                    // Sort each group by x axis to align with letters A, B, C, etc
                    for (List<Point> group : points_grouped)
                    {
                        Collections.sort(group, new Comparator<Point>()
                        {
                            public int compare(Point o1, Point o2) {
                                int result = 0;
                                if (o1.y < o2.y)
                                    result = 1;
                                else if (o1.y > o2.y)
                                    result = -1;
                                else
                                    result = 0;

                                return result;
                            }
                        });
                    }

                    // If have all the circles, make circle in the letters A and C of question 1
                    //if (points.size() % 5 == 0)
                    //{
                    //    List<Point> group = points_grouped.get(0);
                    //Imgproc.circle(incoming, group.get(0), 5, new Scalar(57, 255, 20), 2);
                    //Imgproc.circle(incoming, group.get(2), 5, new Scalar(57, 255, 20), 2);
                    //}

                    Mat thresh2 = new Mat(incoming.size(), CvType.CV_8UC1);
                    //Imgproc.threshold(mIntermediateMat, thresh2, 100, 250, Imgproc.THRESH_BINARY);
                    Imgproc.threshold(mIntermediateMat, thresh2, 180, 200, Imgproc.THRESH_OTSU);
                    //Imgproc.adaptiveThreshold(mIntermediateMat, thresh2,150, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY,5,2);

                    //Core.bitwise_and(thresh, mask, conjunction);

                    //Grading
                    //Loop of grouped points
                    int selection[] = new int[points_grouped.size()];
                    int lCol = 0, rCol = 0;
                    if (noOfQuestions == 50) {
                        lCol = 0;
                        rCol = 25;
                    } else if (noOfQuestions == 20){
                        lCol = 0;
                        rCol = 10;
                    }
                    // Mat conjunction = new Mat(circles.size(), CvType.CV_8UC1);

                    if(points_grouped.size() > 0)
                    {
                        String tempAnswersl = "";
                        String tempAnswersr = "";

                        for (int i = 0; i < points_grouped.size(); i++)
                        {
                            int mostFilled = 1000000000;
                            int selectIdx = -1;

                            for (int j = 0; j < noOfChoices; j++) {
                                Point cur = points_grouped.get(i).get(j);
                                Mat mask = new Mat(incoming.size(), CvType.CV_8UC1, Scalar.all(0));
                                Imgproc.circle(mask, cur, 8, new Scalar(57, 255, 20), 1);
                                Mat conjunction = new Mat(circles.size(), CvType.CV_8UC1);
                                Core.bitwise_and(thresh2, mask, conjunction);

                                int countWhitePixels = Core.countNonZero(conjunction);
                                if (countWhitePixels < mostFilled) {
                                    mostFilled = countWhitePixels;
                                    selectIdx = j;
                                    Log.i("OPENCV", "selectIdx = " + j + " group "+i);
                                }
                            }
                            //add selected answer to array and output image
                            if (selectIdx != -1 && i % 2 == 0)
                            {
                                selection[lCol] = selectIdx;
                                lCol++;

                                switch (selectIdx) {
                                    case 0:
                                        tempAnswersl = tempAnswersl + "A";
                                        break;
                                    case 1:
                                        tempAnswersl = tempAnswersl + "B";
                                        break;
                                    case 2:
                                        tempAnswersl = tempAnswersl + "C";
                                        break;
                                    case 3:
                                        tempAnswersl = tempAnswersl + "D";
                                        break;
                                    case 4:
                                        tempAnswersl = tempAnswersl + "E";
                                        break;
                                    default:
                                        break;
                                }

                                Imgproc.circle(incoming, points_grouped.get(i).get(selectIdx), 10, new Scalar(57, 255, 20), 2);
                            }
                            else if(selectIdx != -1 && i % 2 == 1)
                            {
                                selection[rCol] = selectIdx;
                                rCol++;

                                switch (selectIdx) {
                                    case 0:
                                        tempAnswersr = tempAnswersr + "A";
                                        break;
                                    case 1:
                                        tempAnswersr = tempAnswersr + "B";
                                        break;
                                    case 2:
                                        tempAnswersr = tempAnswersr + "C";
                                        break;
                                    case 3:
                                        tempAnswersr = tempAnswersr + "D";
                                        break;
                                    case 4:
                                        tempAnswersr = tempAnswersr + "E";
                                    default:
                                        break;
                                }

                                Imgproc.circle(incoming, points_grouped.get(i).get(selectIdx), 10, new Scalar(57, 255, 20), 2);
                            }
                        }
                        tempAnswers =  tempAnswersl + tempAnswersr;
                        String str[] = tempAnswers.split("");
                        List<String> tempo = new ArrayList<String>();
                        tempo = Arrays.asList(str);
                        answer = new ArrayList<String>(tempo.size());
                        answer.addAll(tempo);
                        Log.i("OPENCV", "Answers Left = " + tempAnswersl);
                        Log.i("OPENCV", "Answers right = " + tempAnswersr);
                        Log.i("OPENCV", "Answers full = " + answer);

                    }

                    //sort array to string of numbers
                    // Pass to global frame img variable that's returned onCameraFrame.  Shows cropped scantron with circles.
                    //mRgba = thresh2;
                    mRgba = incoming;
                    second = false;
                    btnAdd.setEnabled(true);
                    Log.i("OPENCV", "Answers Combined = " + tempAnswers);
                }
                else if (pressed == false && start)
                {
                    // First press, stops onCameraFrame updating to show user the contour.  Another press and will start processing.
                    pressed = true;
                    second = true;
                }
/*                else if (start)
                {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("tempAnswers", tempAnswers);
                    Log.i("OPENCV", "Answers = " + tempAnswers);
                    Toast.makeText(getApplicationContext(), tempAnswers, Toast.LENGTH_LONG).show();
                    finish();

                }*/
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                pressed = false;
                second = false;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scan = new Intent(CameraActivity.this, VerifyActivity.class);
                scan.putStringArrayListExtra("tempAnswers", answer);
                scan.putExtra("tempChoices", Integer.toString(noOfChoices));
                scan.putExtra("tempQuestions", Integer.toString(noOfQuestions));
                startActivity(scan);
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent (Intent intent){
        setIntent (intent);
    }

    public void detectShapes (int amount, int vertices, double accuracy, List<MatOfPoint> contours){

        detectShapes(vertices, accuracy,contours);
        if (amount < contours.size()){
            for (int i = contours.size() - 1; i >= amount; i--)
                contours.remove(i);
        }
    }

    public void detectShapes (int vertices, double accuracy, List<MatOfPoint> contours){
        detectContourByShape(contours, vertices, accuracy);
    }

    public static void detectContourByShape (List <MatOfPoint> contours, int vertices, double accuracy){

        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        for (int idx = contours.size() - 1; idx >= 0; idx--){
            MatOfPoint contourBubble = contours.get(idx);
            matOfPoint2f.fromList(contourBubble.toList());
            Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true)*accuracy, true);
            long total = approxCurve.total();

            if (total != vertices){
                contours.remove(idx);
            }
        }
    }

    public static List<MatOfPoint> detectContours (Mat threshold){
        List <MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        return detectContours(threshold, contours, hierarchy);
    }

    public static List<MatOfPoint> detectContours (Mat threshold, List<MatOfPoint> contours, Mat hierachy){
        Imgproc.findContours(threshold, contours, hierachy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }


    public static Mat detectCircle (Mat threshold, int distance){
        Mat circles = new Mat();
        Imgproc.HoughCircles(threshold, circles, Imgproc.CV_HOUGH_GRADIENT, 1, distance, 180, 13, 10, 12);
        //Imgproc.HoughCircles(threshold, circles, Imgproc.CV_HOUGH_GRADIENT, 1, distance);

        return circles;
    }

    public static void FilterByCircle (Mat threshold, List<MatOfPoint> contours){

        Mat circles = detectCircle(threshold, 12);
        for (int i = 0; i < contours.size(); i++){
            boolean foundMatch = false;
            for (int j = 0; j < circles.rows(); j++){
                double[] data2 = circles.get(j,0);
                Point center = new Point(data2[0], data2[1]);
                double distance = Imgproc.pointPolygonTest(new MatOfPoint2f(contours.get(i)), center, true);
                if (distance < 0){
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch)
                contours.remove(i);
        }
    }

    public static Point contourCenter (MatOfPoint contour){
        List<Point> pointArr;
        pointArr = contour.toList();
        int size = pointArr.size();
        Point currPoint;
        int sumX = 0;
        int sumY = 0;
        for (int y = 0; y < size; y++){
            currPoint = pointArr.get(y);
            sumX += (int) currPoint.x;
            sumY += (int) currPoint.y;
        }

        return (new Point ((int) sumX/size, (int)sumY/size));
    }

    public static Point contourCenter2 (MatOfPoint contour){
        Rect rect = Imgproc.boundingRect(contour);
        return contourCenter2(rect);
    }

    public static Point contourCenter2 (Rect rect){
        return new Point (rect.x + rect.width/2, rect.y + rect.height/2);
    }

    public static void filterByComparator (List<MatOfPoint> contours, int amount, Comparator<MatOfPoint> comparator){
        contours.sort(comparator);

        int size = amount < contours.size() && amount > 0 ? amount : contours.size();
        for (int i = contours.size() - 1; i >= size; i--)
            contours.remove(i);
    }

/*    public static void filterForSmallestContours (List<MatOfPoint> contours, int amount){
        Comparator<MatOfPoint> sizeComparer = (MatOfPoint o1, MatOfPoint o2) -> {
            if(o1.total() > o2.total()) return 1;
            else if (o2.total() > o1.total()) return -1;
            return 0;
        };
        filterByComparator(contours, amount, sizeComparer);
    }

    public static void filterForLargestContours (List<MatOfPoint> contours, int amount){
        Comparator<MatOfPoint> sizeComparer = (MatOfPoint o1, MatOfPoint o2) -> {
            if(o1.total() < o2.total()) return 1;
            else if (o2.total() < o1.total()) return -1;
            return 0;
        };
        filterByComparator(contours, amount, sizeComparer);
    }*/

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat2 = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat3 = new Mat(height, width, CvType.CV_8UC4);
        maxContour = new MatOfPoint();
        minContour = new MatOfPoint();
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mIntermediateMat.release();
        mIntermediateMat2.release();
        mIntermediateMat3.release();
        maxContour.release();
        minContour.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        if (!start)
            start = true;

        if (!pressed && start)
        {
            // This code is run after camera init and when the user has not pressed the screen
            // If the user has pressed the screen, control is turned over to onUserInteraction()

            List<MatOfPoint> contours = new ArrayList<>();

            // Save frame to global variable for onUserInteraction() processing
            altframe = inputFrame.rgba();

            // Filtering
            Imgproc.GaussianBlur(inputFrame.gray(), mIntermediateMat2, new Size(5, 5), 0);
            Imgproc.Canny(mIntermediateMat2, mIntermediateMat, 75, 200);

            // Obtain contours, looking for scantron outline
            Imgproc.findContours(mIntermediateMat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // Draw biggest contour onto the screen.  If user presses screen, they will select that
            // contour and control will be moved to onUserInteraction()
            double maxContourArea = 0;
            for (MatOfPoint contour : contours) {
                double tempContourArea = Imgproc.contourArea(contour);
                if (tempContourArea > maxContourArea) {
                    maxContour = contour;
                    maxContourArea = tempContourArea;
                }
            }
            List<MatOfPoint> temp = new ArrayList<>();

            temp.add(maxContour);

            //Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            mRgba = altframe;
            // Draw contour only if one actually exists
            if (contours.size() > 0) {
                Imgproc.drawContours(mRgba, temp, -1, new Scalar(57, 255, 20), 2);
            }
        }

        return mRgba;
    }
}
