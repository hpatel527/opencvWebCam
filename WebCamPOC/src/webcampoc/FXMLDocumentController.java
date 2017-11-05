/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webcampoc;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.cvtColor;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author minidude34
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    
    //Create references between GUI Components and a local variable
    @FXML
    private Button button;
    @FXML
    private ImageView currentFrame;
    
    private ScheduledExecutorService timer; //timer for obtaining video
    private VideoCapture capture = new VideoCapture(); // opencv video object
    private boolean cameraActive = false; // upon button click -> sets camera behavior
    private static int cameraID = 0; //id for camera -> can allow for multiple cameras
    
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    
    @FXML
    protected void startCamera(ActionEvent event) throws InterruptedException // when button is clicked, this event is fired
    {
        if(!this.cameraActive)
        {
            //start video capture
            this.capture.open(cameraID);
            
            //check to see if video stream is available
            if(this.capture.isOpened())
            {
                
                this.cameraActive = true;
                
                Runnable frameGrabber = new Runnable(){
                    
                    @Override
                    public void run()
                    {
                        Mat frame = grabFrame();
                        Image imgToShow = mat2Image(frame);
                        updateImageView(currentFrame,imgToShow);
                         
                    }

                   
                };
                
                this.timer = Executors.newSingleThreadScheduledExecutor();
		this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
                // update the button text
		this.button.setText("Stop Camera");
                
            }
            else
            {
                System.err.println("Can't open camera!");
            }
        }
        else
        {
            // the camera is not active at this point
            this.cameraActive = false;
	    // update again the button content
            this.button.setText("Start Camera");
            // stop the timer
	    this.stopAcquisition();
            
        }
        
        
    }
   
    public static Image mat2Image(Mat frame)
	{
		try
		{
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		}
		catch (Exception e)
		{
			System.err.println("Cannot convert the Mat obejct: " + e);
			return null;
		}
	}
    
    private static BufferedImage matToBufferedImage(Mat original)
	{
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);
		
		if (original.channels() > 1)
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}
		else
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return image;
	}
    
    private Mat grabFrame()
	{
		// init everything
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);
				
				// if the frame is not empty, process it
				if (!frame.empty())
				{
					cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
				}
				
			}
			catch (Exception e)
			{
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition() throws InterruptedException
	{
		if (this.timer!=null && !this.timer.isShutdown())
		{
                    // stop the timer
                    this.timer.shutdown();
                    this.timer.awaitTermination(33, MILLISECONDS);
		}
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	
	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image)
	{
		view.setImage(image);
	}
 
	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed() throws InterruptedException
	{
		this.stopAcquisition();
	}
    
}
