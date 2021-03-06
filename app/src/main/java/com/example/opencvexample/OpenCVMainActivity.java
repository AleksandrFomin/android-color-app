package com.example.opencvexample;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_32FC3;
import static org.opencv.core.CvType.CV_64FC1;
import static org.opencv.core.CvType.CV_64FC3;
import static org.opencv.core.CvType.CV_8UC3;


public class OpenCVMainActivity extends Activity implements CvCameraViewListener2 {
    private static final String  TAG              = "DaltonicApp::Activity";

    
    
    private MenuItem             menuTipoCamara = null;
    private MenuItem			 menuBlancoYNegro = null;
    private MenuItem			 menuModoReconocimiento = null;
    private MenuItem			 menuSubmenuResoluciones = null;
    private boolean              tipoCamara = true;
    private boolean				 modoGrises = false;
    private boolean				 modoReconocimiento = false; //Modo de reconocimiento colores. Preciso(true) o Rango(false). por defecto empezamos en preciso(true)
    private int					 anchoCamara = 1280; //960; <--> Nexus
    private int					 altoCamara = 720;
    
    
    //C�mara
    private CameraBridgeViewBase camara;
    

    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    camara.enableView();  
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public OpenCVMainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        Log.i(TAG, "called onCreate");
        
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Intento de Fullscreen, OpenCV No quiere en galaxy nexus, s� en galaxy 4 :(
        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Screen ON Permanente

        //Brillo m�ximo permanente
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        
        
        setContentView(R.layout.opencv_main_activity);
        
        //Cambiamos el tipo de camara actual 	JAVA <--> NATIVA
//        if (tipoCamara){
//        	camara = (CameraBridgeViewBase)findViewById(R.id.camara_nativa);
//        }else{
            camara = (CameraBridgeViewBase) findViewById(R.id.camara_java);
//        }

    	camara.setVisibility(SurfaceView.VISIBLE);
    	camara.setCvCameraViewListener(this);
        camara.setMaxFrameSize(144, 176);
        camara.disableView();
        camara.enableView();
    	
    	
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (camara != null)
            camara.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initDebug();
        Log.i(TAG, "OpenCV loaded successfully");
        camara.enableView();
        camara.setCameraPermissionGranted();
    }

    public void onDestroy() {
        super.onDestroy();
        if (camara != null)
            camara.disableView();
    }
    
    
    
    //Creamos el menu y las opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        menuTipoCamara = menu.add("Cambiar Camara Nativa/Java");
        menuBlancoYNegro = menu.add("Blanco y Negro");
        menuModoReconocimiento = menu.add("Modo Preciso / Rango de Color");
        
        SubMenu subMenu = menu.addSubMenu(4, 4, 4, "Selecciona una resoluci�n");
        subMenu.add(1, 10, 1, "Alta Resoluci�n (1280x720)");
        subMenu.add(1, 11, 2, "Media Resoluci�n (960x720)");
        subMenu.add(1, 12, 3, "Baja Resoluci�n (800x480)");
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String mensajeToast = new String();
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        //Boton cambia tipo de camara
        if (item == menuTipoCamara) {
            camara.setVisibility(SurfaceView.GONE);
            tipoCamara = !tipoCamara;

//            if (tipoCamara) {
//                camara = (CameraBridgeViewBase) findViewById(R.id.camara_nativa);
//                mensajeToast = "C�mara Nativa";
//            }else{
                camara = (CameraBridgeViewBase) findViewById(R.id.camara_java);
                mensajeToast = "C�mara Java";
//            }

            
            camara.setVisibility(SurfaceView.VISIBLE);
            camara.setCvCameraViewListener(this);
            camara.enableView();
            Toast toast = Toast.makeText(this, mensajeToast, Toast.LENGTH_LONG);
            toast.show();
        }
        //Fin Tipo Camara
        
        
        //Boton pone blanco y negro - Grises
        if(item == menuBlancoYNegro){
        	if(modoGrises){
        		modoGrises = false;
        		Toast toast = Toast.makeText(this, "'Modo Grises' desactivado.\n'Modo Normal' habilitado." , Toast.LENGTH_LONG);
        		toast.show();
        	}else{
        		modoGrises = true;
        		Toast toast = Toast.makeText(this, "'Modo Normal' desactivado.\n'Modo Grises' habilitado." , Toast.LENGTH_LONG);
        		toast.show();
        	}
        }
        //Fin Modo Grises
        
        //Boton Modo Preciso / Modo Tonalidades
        if(item == menuModoReconocimiento){
        	if(modoReconocimiento){
        		modoReconocimiento = false;
        		Toast toast = Toast.makeText(this, "'Modo Preciso' desactivado.\n'Modo Tonalidades' habilitado." , Toast.LENGTH_LONG);
        		toast.show();
        	}else{
        		modoReconocimiento = true;
        		Toast toast = Toast.makeText(this, "'Modo Tonalidades' desactivado.\n'Modo Preciso' habilitado." , Toast.LENGTH_LONG);
        		toast.show();
        	}
        }
        
        
        //Submenu para cambiar el tama�o del HUD
        switch(item.getItemId()){
            case 10: //Id del men�, para combrobar que se ha pulsado
            	anchoCamara = 1280;
            	altoCamara = 720;
            	Toast toast = Toast.makeText(this, "Resoluci�n del HUD m�xima" , Toast.LENGTH_LONG);
        		toast.show();
                break;
            case 11:
            	anchoCamara = 960;
            	altoCamara = 720;
            	toast = Toast.makeText(this, "Resoluci�n del HUD media" , Toast.LENGTH_LONG);
        		toast.show();
                break;
            case 12:
            	anchoCamara = 800;
            	altoCamara = 480;
            	toast = Toast.makeText(this, "Resoluci�n del HUD m�nima" , Toast.LENGTH_LONG);
        		toast.show();
                break;

        }

        return true;
    }
    
    

    

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
        
    }
    
    
    
    public Mat onCameraFrame(CvCameraViewFrame frame) {
        Mat ImageMat = frame.rgba();
        Mat ImageMatRGB = new Mat();
        Imgproc.cvtColor(ImageMat, ImageMatRGB, Imgproc.COLOR_RGBA2RGB); //8UC3

//        return ImageMatRGB;
        //Transformation matrix for Deuteranope (a form of red/green color deficit)
        double lms2lmsd1[] = {1,0,0,0.494207,0,1.24827,0,0,1};
        Mat lms2lmsd = new Mat(3,3,CV_64FC1);
        lms2lmsd.put(0,0,lms2lmsd1);

        //Transformation matrix for Protanope (another form of red/green color deficit)
        double lms2lmsp1[] = {0,2.02344,-2.52581,0,1,0,0,0,1};
        Mat lms2lmsp = new Mat(3,3,CV_64FC1);
        lms2lmsp.put(0,0,lms2lmsp1);

        //Transformation matrix for Tritanope (a blue/yellow deficit - very rare)
        double lms2lmst1[] = {1,0,0,0,1,0,-0.395913,0.801109,0};
        Mat lms2lmst = new Mat(3,3,CV_64FC1);
        lms2lmst.put(0,0,lms2lmst1);

        //Colorspace transformation matrices
//        double rgb2lms1[] = {17.8824,43.5161,4.11935,3.45565,27.1554,3.86714,0.0299566,0.184309,1.46709};
        double rgb2lms1[] = {1.46709, 0.184309, 0.0299566, 3.86714, 27.1554, 3.45565, 4.11935, 43.5161, 17.8824};
        Mat rgb2lms = new Mat(3,3,CV_64FC1);
        rgb2lms.put(0,0,rgb2lms1);
        Mat lms2rgb = rgb2lms.inv();

        //Daltonize image correction matrix
        double err2mod1[] = {0,0,0,0.7,1,0,0.7,0,1};
        Mat err2mod = new Mat(3,3,CV_64FC1);
        err2mod.put(0,0,err2mod1);

        //CHANGE ACCORDING TO TYPE OF COLOR BLINDNESS
        Mat lms2lms_deficit = lms2lmsd;

        Mat LMS = new Mat(ImageMatRGB.rows(),ImageMatRGB.cols(), CvType.CV_64FC3);
        Mat _LMS = LMS;
        Mat _RGB = LMS;
        Mat ERR = LMS;
        int i,j;

        double actRGBVal[];
        double tempRGB[];
        Mat lmsval = new Mat(1,1,CV_64FC3);
        Mat lmsResVec = new Mat();
        Mat actRGBVec = new Mat(3,1,CV_64FC1);
        try {
            for (i = 0; i < ImageMatRGB.rows(); i++) {
                for (j = 0; j < ImageMatRGB.cols(); j++) {
                    actRGBVal = ImageMatRGB.get(i, j);
                    actRGBVec.put(0, 0, actRGBVal);
                    Core.gemm(rgb2lms, actRGBVec, 1, new Mat(), 0, lmsResVec, 0);
                    double lmsvaldata[] = {lmsResVec.get(0,0)[0],lmsResVec.get(1,0)[0],lmsResVec.get(2,0)[0]};
                    LMS.put(i,j,lmsvaldata);
                }
            }
        }
        catch (Exception e){
            Log.d("ImageHandler","Error rgb to lms conversion! " + e.getMessage());
        }


        try {
            for (i = 0; i < ImageMatRGB.rows(); i++) {
                for (j = 0; j < ImageMatRGB.cols(); j++) {
                    actRGBVal = LMS.get(i, j);
                    actRGBVec.put(0, 0, actRGBVal);
                    Core.gemm(lms2lms_deficit, actRGBVec, 1, new Mat(), 0, lmsResVec, 0);
                    double lmsvaldata[] = {lmsResVec.get(0,0)[0],lmsResVec.get(1,0)[0],lmsResVec.get(2,0)[0]};
                    _LMS.put(i,j,lmsvaldata);
                }
            }
        }
        catch (Exception e){
            Log.d("ImageHandler","Error rgb to lms conversion! " + e.getMessage());
        }

        try {
            for (i = 0; i < ImageMatRGB.rows(); i++) {
                for (j = 0; j < ImageMatRGB.cols(); j++) {
                    actRGBVal = _LMS.get(i,j);
                    actRGBVec.put(0, 0, actRGBVal);
                    Core.gemm(lms2rgb, actRGBVec, 1, new Mat(), 0, lmsResVec, 0);
                    double lmsvaldata[] = {lmsResVec.get(0,0)[0],lmsResVec.get(1,0)[0],lmsResVec.get(2,0)[0]};
                    _RGB.put(i,j,lmsvaldata);
                }
            }
        }
        catch (Exception e){
            Log.d("ImageHandler","Error rgb to lms conversion! " + e.getMessage());
        }

        Mat error = new Mat(ImageMatRGB.rows(),ImageMatRGB.cols(), CV_64FC3);

        Core.subtract(ImageMatRGB,_RGB,error,new Mat(),CV_64FC3);

        try {
            for (i = 0; i < ImageMatRGB.rows(); i++) {
                for (j = 0; j < ImageMatRGB.cols(); j++) {
                    actRGBVal = error.get(i,j);
                    actRGBVec.put(0, 0, actRGBVal);
                    Core.gemm(err2mod, actRGBVec, 1, new Mat(), 0, lmsResVec, 0);
                    double lmsvaldata[] = {lmsResVec.get(0,0)[0],lmsResVec.get(1,0)[0],lmsResVec.get(2,0)[0]};
                    ERR.put(i,j,lmsvaldata);
                }
            }
        }
        catch (Exception e){
            Log.d("ImageHandler","Error rgb to lms conversion! " + e.getMessage());
        }

        Mat dtpn = new Mat(ImageMatRGB.rows(),ImageMatRGB.cols(), CV_64FC3);

        Core.add(ERR,ImageMatRGB,dtpn,new Mat(),CV_64FC3);

        dtpn.convertTo(dtpn,CV_32FC3);
        dtpn.convertTo(dtpn,CV_8UC3,255.0);


        try {
            for (i = 0; i < ImageMatRGB.rows(); i++) {
                for (j = 0; j < ImageMatRGB.cols(); j++) {
                    actRGBVal = dtpn.get(i, j);
                    actRGBVal[0] = (actRGBVal[0] > 0) ? actRGBVal[0] : 0;
                    actRGBVal[0] = (actRGBVal[0] < 255) ? actRGBVal[0] : 255;
                    actRGBVal[1] = (actRGBVal[1] > 0) ? actRGBVal[1] : 0;
                    actRGBVal[1] = (actRGBVal[1] < 255) ? actRGBVal[1] : 255;
                    actRGBVal[2] = (actRGBVal[2] > 0) ? actRGBVal[2] : 0;
                    actRGBVal[2] = (actRGBVal[2] < 255) ? actRGBVal[2] : 255;
                    dtpn.put(i,j, actRGBVal);
                }
            }
        }
        catch (Exception e){
            Log.d("ImageHandler","Error rgb to lms conversion! " + e.getMessage());
        }

        Imgproc.cvtColor(dtpn, dtpn, Imgproc.COLOR_RGB2RGBA); //8UC3
        return dtpn;
    }


    public String getColorName(double r, double g, double b){
    	
    	String nombreColor = null;
    	
    	
    	if(modoReconocimiento){ //Modo Preciso
    		
    		
	    	//Blanco
	    	if(r > 140.0 && g > 140.0 && b > 140.0){
	    		if(r > 200.0 && g > 200.0 && b > 200.0){
	    			nombreColor = "Blanco Puro";
	    		}else{
	    			nombreColor = "Blanco";
	    		}
	    	}
	    	
	    	//Negro
	    	if(r < 50.0 && g < 50.0 && b < 50.0){
	    		nombreColor = "Negro";
	    	}
	    	
	    	//Rojo
	    	if(r > 100.0 && g < 100.0 && b < 100.0){
	    		nombreColor = "Rojo";
	    	}
	    	
	    	//Verde
	    	if(r < 100.0 && g > 100.0 && b < 100.0){
	    		nombreColor = "Verde";
	    	}
	    	
	    	//Azul
	    	if(r < 100.0 && g < 100.0 && b > 100.0){
	    		nombreColor = "Azul";
	    	}
	    	
	    	//Amarillo 
	    	if(r > 180.0 && r < 230.0 && g > 200.0 && g < 230.0 && b < 30.0){
	    		nombreColor = "Amarillo";
	    	}
	    	
	    	//Cyan
	    	if(r < 10.0 && g > 200.0 && g < 230.0 && b > 230.0 && b < 240.0){
	    		nombreColor = "Cyan";
	    	}
	    	
	    	//Magenta
	    	if(r > 200.0 && r < 220.0 && g > 30.0 && g < 50.0 && b > 220.0 && b < 240.0){
	    		nombreColor = "Magenta";
	    	}
	    	
    	}else{ //Modo Rangos de Colores
	    	
	    	// Calculamos a partir del Hue, en vez del valor... As� tomamos rangos
	    	// http://en.wikipedia.org/wiki/Hue
	    	
	    	//Rojo
	    	if(r >= g && g >= b){
	    		nombreColor = "Tono Rojo";
	    	}
	    	
	    	//Amarillo
	    	if(g > r && r >= b){
	    		nombreColor = "Tono Amarillo";
	    	}
	    	
	    	//Verde
	    	if(g >= b && b > r){
	    		nombreColor = "Tono Verde";
	    	}
	    	
	    	//Cyan
	    	if(b > g && g > r){
	    		nombreColor = "Tono Cyan";
	    	}
	    	
	    	//Azul
	    	if(b > r && r >= g){
	    		nombreColor = "Tono Azul";
	    	}
	    	
	    	//Magenta
	    	if(r >= b && b > g){
	    		nombreColor = "Tono Magenta";
	    	}
	    	
	    	//Negro
	    	if(r < 10.0 && g < 10.0 && b < 10.0){
	    		nombreColor = "Tono Negro";
	    	}
	    	
	    	//Blanco
	    	if(r > 140.0 && g > 140.0 && b > 140.0){
	    		if(r > 200.0 && g > 200.0 && b > 200.0){
	    			nombreColor = "Blanco Puro";
	    		}else{
	    			nombreColor = "Tono Blanco";
	    		}
	    	}
	    	
    	}
    	
	   return nombreColor;
   }
    
 
}