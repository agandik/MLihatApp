/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mlihat.app;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.nio.ByteBuffer;
import java.util.List;
import org.mlihat.app.env.ImageUtils;
import org.mlihat.app.env.Logger;
import org.mlihat.app.tflite.Classifier.Device;
import org.mlihat.app.tflite.Classifier.Model;
import org.mlihat.app.tflite.Classifier.Recognition;

public abstract class CameraActivity extends AppCompatActivity
    implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  private LinearLayout bottomSheetLayout;
  private LinearLayout gestureLayout;
  private BottomSheetBehavior<LinearLayout> sheetBehavior;
  protected TextView recognitionTextView,
      recognition1TextView,
      recognition2TextView,
      recognitionValueTextView,
      recognition1ValueTextView,
      recognition2ValueTextView;
  protected TextView frameValueTextView,
      cropValueTextView,
      cameraResolutionTextView,
      rotationTextView,
      inferenceTimeTextView;
  protected ImageView bottomSheetArrowImageView;
  private ImageView plusImageView, minusImageView;
  private Spinner modelSpinner;
  private Spinner modelSpinner1;
  private RelativeLayout Jenis;
  private RelativeLayout Jenis1;
  private Spinner deviceSpinner;
  private TextView threadsTextView;
  private Model model = Model.BUNGA;
  private Device device = Device.CPU;
  private int numThreads = -1;
  private Spinner modelLanguage;
  private int Model_id;
  private TextView detected_search;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.tfe_ic_activity_camera);

    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }

    threadsTextView = findViewById(R.id.threads);
    plusImageView = findViewById(R.id.plus);
    minusImageView = findViewById(R.id.minus);
    modelSpinner = findViewById(R.id.model_spinner);
    modelSpinner1 = findViewById(R.id.model_spinner1);
    Jenis = findViewById(R.id.jenis);
    Jenis1 = findViewById(R.id.jenis1);
    deviceSpinner = findViewById(R.id.device_spinner);
    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    gestureLayout = findViewById(R.id.gesture_layout);
    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
    bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);
    detected_search = findViewById(R.id.detected_item);
    modelLanguage = findViewById(R.id.language_spinner);

    detected_search.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String bahasa = (String) modelLanguage.getSelectedItem().toString();
        String Model = (String) detected_search.getText().toString();
        int model_search;
        String jenis;
        if(modelSpinner.getVisibility() == View.VISIBLE){
          jenis = modelSpinner.getSelectedItem().toString();
        }
        else if (modelSpinner.getVisibility() == View.GONE){
          jenis = modelSpinner1.getSelectedItem().toString();
        }
        else {
          jenis = "";
        }

        String hasil="";

        String[] aves ={
                "Geokichla citrina",
                "Pycnonotus goiavier",
                "Chloropsis sonnerati",
                "Copsychus saularis",
                "Agapornis",
                "Copsychus malabaricus"
        };
        String[] felis_catus ={
                "",
                "",
                "Prionailurus bengalensis",
                "",
                "",
                ""
        };
        String[] canis_lupus ={
                "",
                "",
                "",
                "",
                "",
                ""
        };
        String[] latin_flower ={
                "Orchidaceae",
                "Helianthus annuus",
                "Plumeria",
                "Lavandula",
                "Rosa Eden",
                "Jasminum",
                "Hibiscus rosa-sinensis",
                "Nymphaea"
        };
        String[] dog ={
                "akita",
                "Alaskan Malamute",
                "beagle",
                "cihuahua",
                "golden retriever",
                "pomeranian"
        };
        String[] bird ={
                "Orange-headed Thrush",
                "Yellow-vented Bulbul",
                "Greater green leafbird",
                "Oriental Magpie-Robin",
                "love bird",
                "White-rumped Shama"
        };
        String[] cat ={
                "abbyssinian",
                "angora",
                "bengal",
                "persian",
                "siamese",
                "sphynx"
        };
        String[] flower ={
                "orchid",
                "sunflower",
                "frangipani ",
                "lavender",
                "rose flower",
                "jasmin",
                "hibiscus",
                "lotus"
        };

        if (detected_search.getText().toString().equals("akita") || detected_search.getText().toString().equals("anis merah") || detected_search.getText().toString().equals("abbyssinian") || detected_search.getText().toString().equals("anggrek")){
          model_search=0;
        }
        else if (detected_search.getText().toString().equals("Alaskan Malamute") || detected_search.getText().toString().equals("trucukan") || detected_search.getText().toString().equals("anggora") || detected_search.getText().toString().equals("bunga matahari")){
          model_search=1;
        }
        else if (detected_search.getText().toString().equals("beagle") || detected_search.getText().toString().equals("cucak ijo") || detected_search.getText().toString().equals("bengal") || detected_search.getText().toString().equals("kamboja")){
          model_search=2;
        }
        else if (detected_search.getText().toString().equals("cihuahua") || detected_search.getText().toString().equals( "kacer") || detected_search.getText().toString().equals("persia") || detected_search.getText().toString().equals("lavender")){
          model_search=3;
        }
        else if (detected_search.getText().toString().equals("golden retriever") || detected_search.getText().toString().equals("love bird") || detected_search.getText().toString().equals("siamese") || detected_search.getText().toString().equals("mawar")){
          model_search=4;
        }
        else if (detected_search.getText().toString().equals("pomeranian") || detected_search.getText().toString().equals("murai batu") || detected_search.getText().toString().equals("sphynx") || detected_search.getText().toString().equals("melati")){
          model_search=5;
        }
        else if (detected_search.getText().toString().equals("kembang sepatu")){
          model_search=6;
        }
        else {
          model_search=7;
        }

        if (bahasa.equals("Latin")){
            if(jenis.equals("Anjing")){
                hasil = canis_lupus[model_search];
            }
            else if(jenis.equals("Burung")){
                hasil = aves[model_search];
            }
            else if(jenis.equals("Kucing")){
                hasil = felis_catus[model_search];
            }
            else {
                hasil = latin_flower[model_search];
            }

        }
        else if(bahasa.equals("Inggris")){
            if(jenis.equals("Anjing")){
                hasil = dog[model_search]+" dog";
            }
            else if(jenis.equals("Burung")){
                hasil = bird[model_search];
            }
            else if(jenis.equals("Kucing")){
                hasil = cat[model_search]+" cat";
            }
            else {
                hasil = flower[model_search];
            }
        }
        else {
          if(jenis.equals("Anjing")){
            hasil = "anjing "+(String) detected_search.getText();
          }
          else if(jenis.equals("Burung")){
            hasil = "burung "+(String) detected_search.getText();
          }
          else if(jenis.equals("Kucing")){
            hasil = "kucing "+(String) detected_search.getText();
          }
          else {
            hasil = "bunga "+(String) detected_search.getText();
          }
        }

        if(hasil.equals("")){
          if(jenis.equals("Anjing")){
            hasil = "anjing "+(String) detected_search.getText();
          }
          else if(jenis.equals("Burung")){
            hasil = "burung "+(String) detected_search.getText();
          }
          else if(jenis.equals("Kucing")){
            hasil = "kucing "+(String) detected_search.getText();
          }
          else {
            hasil = "bunga "+(String) detected_search.getText();
          }
            Toast.makeText(CameraActivity.this,"Bahasa "+bahasa+" "+jenis+" "+detected_search.getText()+" tidak ditemukan, mencari dengan bahasa Indonesia" , Toast.LENGTH_LONG).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + hasil)));
        }
        else if(!"".equals(hasil)){
          //Toast.makeText(CameraActivity.this, String.valueOf(Pilih_model)+detected_search.getText() + String.valueOf(Pilih_model) , Toast.LENGTH_SHORT).show();
           Toast.makeText(CameraActivity.this, "Mencari "+hasil+" Pada Google search" , Toast.LENGTH_SHORT).show();
           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + hasil)));
        }
        /*else{
            Toast.makeText(CameraActivity.this,jenis , Toast.LENGTH_SHORT).show();
            //Toast.makeText(CameraActivity.this, "Mencari "+detected_search.getText()+" Pada Google search" , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + detected_search.getText())));
        }*/
      }
    });

    ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
              gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
              gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            //                int width = bottomSheetLayout.getMeasuredWidth();
            int height = gestureLayout.getMeasuredHeight();

            sheetBehavior.setPeekHeight(height);
          }
        });
    sheetBehavior.setHideable(false);

    sheetBehavior.setBottomSheetCallback(
        new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
              case BottomSheetBehavior.STATE_HIDDEN:
                break;
              case BottomSheetBehavior.STATE_EXPANDED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
                }
                break;
              case BottomSheetBehavior.STATE_COLLAPSED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                }
                break;
              case BottomSheetBehavior.STATE_DRAGGING:
                break;
              case BottomSheetBehavior.STATE_SETTLING:
                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                break;
            }
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

    recognitionTextView = findViewById(R.id.detected_item);
    recognitionValueTextView = findViewById(R.id.detected_item_value);
    recognition1TextView = findViewById(R.id.detected_item1);
    recognition1ValueTextView = findViewById(R.id.detected_item1_value);
    recognition2TextView = findViewById(R.id.detected_item2);
    recognition2ValueTextView = findViewById(R.id.detected_item2_value);
    frameValueTextView = findViewById(R.id.frame_info);
    cropValueTextView = findViewById(R.id.crop_info);
    cameraResolutionTextView = findViewById(R.id.view_info);
    rotationTextView = findViewById(R.id.rotation_info);
    inferenceTimeTextView = findViewById(R.id.inference_info);
    deviceSpinner.setOnItemSelectedListener(this);
    plusImageView.setOnClickListener(this);
    minusImageView.setOnClickListener(this);

    String pilih=getIntent().getStringExtra("Pilih");

    if(pilih.equals("hewan")){
      modelSpinner.setVisibility(View.VISIBLE);
      modelSpinner1.setVisibility(View.GONE);
      Jenis1.setVisibility(View.GONE);
      model = Model.ANJING;
      Toast.makeText(CameraActivity.this, "Hewan Peliharaan",
              Toast.LENGTH_LONG).show();
      modelSpinner.setOnItemSelectedListener(this);
    }
    else if(pilih.equals("bunga")){
      modelSpinner.setVisibility(View.GONE);
      modelSpinner1.setVisibility(View.VISIBLE);
      Jenis.setVisibility(View.GONE);
            model = Model.BUNGA;
      Toast.makeText(CameraActivity.this, "Bunga",
              Toast.LENGTH_LONG).show();
      modelSpinner1.setOnItemSelectedListener(this);
    }
    else{
      Toast.makeText(CameraActivity.this, "gagal",
              Toast.LENGTH_LONG).show();
      Intent PindahTumbuhan = new Intent(CameraActivity.this,MenuActivity.class);
      startActivity(PindahTumbuhan);
    }
    device = Device.valueOf(deviceSpinner.getSelectedItem().toString());
    numThreads = Integer.parseInt(threadsTextView.getText().toString().trim());
  }

  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

    private void Model_search(String search){
    if (search.equals("akita")||equals("anis merah")||equals("abbyssinian")||equals("anggrek")){
      Model_id=0;
    }
    else if (search.equals("Alaskan Malamute")||equals("trucukan")||equals("anggora")||equals("bunga matahari")){
      Model_id=1;
    }
    else if (search.equals("beagle")||equals("cucak ijo")||equals("bengal")||equals("kamboja")){
      Model_id=2;
    }
    else if (search.equals("cihuahua")||equals( "kacer")||equals("persia")||equals("lavender")){
      Model_id=3;
    }
    else if (search.equals("golden retriever")||equals("love bird")||equals("siamese")||equals("mawar")){
      Model_id=4;
    }
    else if (search.equals("pomeranian")||equals("murai batu")||equals("sphynx")||equals("melati")){
      Model_id=5;
    }
    else if (search.equals("kembang sepatu")){
        Model_id=6;
    }
    else if (search.equals("teratai")){
        Model_id=7;
    }
    else{

    }
  };

  /** Callback for android.hardware.Camera API */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
        new Runnable() {
          @Override
          public void run() {
            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
          }
        };

    postInferenceCallback =
        new Runnable() {
          @Override
          public void run() {
            camera.addCallbackBuffer(bytes);
            isProcessingFrame = false;
          }
        };
    processImage();
  }

  /** Callback for Camera2 API */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
          new Runnable() {
            @Override
            public void run() {
              ImageUtils.convertYUV420ToARGB8888(
                  yuvBytes[0],
                  yuvBytes[1],
                  yuvBytes[2],
                  previewWidth,
                  previewHeight,
                  yRowStride,
                  uvRowStride,
                  uvPixelStride,
                  rgbBytes);
            }
          };

      postInferenceCallback =
          new Runnable() {
            @Override
            public void run() {
              image.close();
              isProcessingFrame = false;
            }
          };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    LOGGER.d("onResume " + this);
    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    LOGGER.d("onStop " + this);
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    LOGGER.d("onDestroy " + this);
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      final int requestCode, final String[] permissions, final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      if (allPermissionsGranted(grantResults)) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private static boolean allPermissionsGranted(final int[] grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
            .show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
      CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

       
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
            (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                || isHardwareLevelSupported(
                    characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
          CameraConnectionFragment.newInstance(
              new CameraConnectionFragment.ConnectionCallback() {
                @Override
                public void onPreviewSizeChosen(final Size size, final int rotation) {
                  previewHeight = size.getHeight();
                  previewWidth = size.getWidth();
                  CameraActivity.this.onPreviewSizeChosen(size, rotation);
                }
              },
              this,
              getLayoutId(),
              getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
          new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  @UiThread
  protected void showResultsInBottomSheet(List<Recognition> results) {
    if (results != null && results.size() >= 3) {
      Recognition recognition = results.get(0);
      if (recognition != null) {
        if (recognition.getTitle() != null) recognitionTextView.setText(recognition.getTitle());
        if (recognition.getConfidence() != null)
          recognitionValueTextView.setText(
              String.format("%.2f", (100 * recognition.getConfidence())) + "%");
      }

      Recognition recognition1 = results.get(1);
      if (recognition1 != null) {
        if (recognition1.getTitle() != null) recognition1TextView.setText(recognition1.getTitle());
        if (recognition1.getConfidence() != null)
          recognition1ValueTextView.setText(
              String.format("%.2f", (100 * recognition1.getConfidence())) + "%");
      }

      Recognition recognition2 = results.get(2);
      if (recognition2 != null) {
        if (recognition2.getTitle() != null) recognition2TextView.setText(recognition2.getTitle());
        if (recognition2.getConfidence() != null)
          recognition2ValueTextView.setText(
              String.format("%.2f", (100 * recognition2.getConfidence())) + "%");
      }
    }
  }

  protected void showFrameInfo(String frameInfo) {
    frameValueTextView.setText(frameInfo);
  }

  protected void showCropInfo(String cropInfo) {
    cropValueTextView.setText(cropInfo);
  }

  protected void showCameraResolution(String cameraInfo) {
    cameraResolutionTextView.setText(cameraInfo);
  }

  protected void showRotationInfo(String rotation) {
    rotationTextView.setText(rotation);
  }

  protected void showInference(String inferenceTime) {
    inferenceTimeTextView.setText(inferenceTime);
  }

  protected Model getModel() {
    return model;
  }

  private void setModel(Model model) {
    if (this.model != model) {
      LOGGER.d("Updating  model: " + model);
      this.model = model;
      onInferenceConfigurationChanged();
    }
  }

  protected Device getDevice() {
    return device;
  }

  private void setDevice(Device device) {
    if (this.device != device) {
      LOGGER.d("Updating  device: " + device);
      this.device = device;
      final boolean threadsEnabled = device == Device.CPU;
      plusImageView.setEnabled(threadsEnabled);
      minusImageView.setEnabled(threadsEnabled);
      threadsTextView.setText(threadsEnabled ? String.valueOf(numThreads) : "N/A");
      onInferenceConfigurationChanged();
    }
  }

  protected int getNumThreads() {
    return numThreads;
  }

  private void setNumThreads(int numThreads) {
    if (this.numThreads != numThreads) {
      LOGGER.d("Updating  numThreads: " + numThreads);
      this.numThreads = numThreads;
      onInferenceConfigurationChanged();
    }
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void onInferenceConfigurationChanged();

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.plus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads >= 9) return;
      setNumThreads(++numThreads);
      threadsTextView.setText(String.valueOf(numThreads));
    } else if (v.getId() == R.id.minus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads == 1) {
        return;
      }
      setNumThreads(--numThreads);
      threadsTextView.setText(String.valueOf(numThreads));
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    if (parent == modelSpinner) {
      setModel(Model.valueOf(parent.getItemAtPosition(pos).toString().toUpperCase()));
    } else if (parent == deviceSpinner) {
      setDevice(Device.valueOf(parent.getItemAtPosition(pos).toString()));
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }
}
