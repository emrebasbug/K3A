package com.havagram.plugin;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import com.clouiotech.port.Adapt;
import com.clouiotech.pda.rfid.EPCModel;
import com.clouiotech.pda.rfid.IAsynchronousMessage;
import com.clouiotech.pda.rfid.uhf.UHF;
import com.clouiotech.pda.rfid.uhf.UHFReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import android.util.Log;
import java.util.HashMap;
import android.media.*;
import java.util.*;


/**
 * This class echoes a string called from JavaScript.
 */
public class K3A extends CordovaPlugin implements
  IAsynchronousMessage {

    private static boolean isStartPingPong = false;
    private Object hmList_Lock = new Object();
    private HashMap<String, EPCModel> hmList = new HashMap<String, EPCModel>();
    private List<RFModel> sList = new ArrayList<RFModel>();
    private String jsonString = new String();
    private int i = 0;
    private Object beep_Lock = new Object();
      ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
      ToneGenerator.MAX_VOLUME);

    public static UHF CLReader = UHFReader.getUHFInstance();
    static Boolean _UHFSTATE = false;



    @Override
    public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) throws JSONException {
      boolean rt = false;



      if (action.equals("Init")) {

        if(Init())
          callbackContext.success("Connected");
        else
          callbackContext.success("Not Connected");

        rt = true;
      }

      if (action.equals("GetSerialNumber")) {
        String message = GetSerialNumber();
        callbackContext.success(message);
        rt = true;
      }

      if (action.equals("Hello")) {
        String message = args.getString(0);
        callbackContext.success(message);
        rt = true;
      }

      if (action.equals("Stop")) {
        Stop();
        callbackContext.success("OK");
        rt = true;
      }

      if (action.equals("Read")) {
        final CallbackContext _callbackContext = callbackContext;

        cordova.getThreadPool().execute(

          new Runnable() {
            @Override
            public void run() {
              if (isStartPingPong)
                return;

              isStartPingPong = true;
              while (isStartPingPong) {
                try {
                  //CLReader.Read_EPC("3|1|3,000006"); // USERDATA
                  //CLReader.Read_EPC("3|1|2,0006"); // TID
                  CLReader.Read_EPC("3|1"); // EPC

                  //Thread.sleep(1000);

                  if (300 > 0) {
                    CLReader.Stop();

                    //JSONObject JSONObj = new JSONObject(hmList);
                    String strResult = "";
                    if(jsonString.length()>2)
                      strResult = "[" + jsonString.substring(0,jsonString.length()-1) + "]";

                    PluginResult result = new PluginResult(Status.OK,strResult) ;
                    result.setKeepCallback(true);
                    //_callbackContext.success(hmList.toString());
                    _callbackContext.sendPluginResult(result);

                    //Thread.sleep(300);
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            }
          }


        );
        return true;
      }
      return false;
    }

    private Boolean Init() {
    Boolean rt = false;
    try {
      if (_UHFSTATE == false) {
        rt = CLReader.OpenConnect(false, this);
        if (rt) {
          _UHFSTATE = true;
        }
      } else {
        rt = true;
      }
    } catch (Exception ex) {
      Log.d("debug", "UHF" + ex.getMessage());
    }
    return rt;
  }

    private String GetSerialNumber() {
		  return Adapt.getPropertiesInstance().getSN();
	  }

    @Override
    public void OutPutEPC(EPCModel model) {

    if (!isStartPingPong)
      return;
    try {
      synchronized (hmList_Lock) {
        if (hmList.containsKey(model._EPC + model._TID)) {
          EPCModel tModel = hmList.get(model._EPC + model._TID);
          tModel._TotalCount++;
        } else {
          hmList.put(model._EPC + model._TID, model);

          JSONObject obj = new JSONObject();
          obj.put("id",i+1);
          obj.put("EPC",model._EPC);
          obj.put("TID",model._TID);
          obj.put("USER",model._UserData);
          jsonString += obj.toString() + ",";
          i +=1;
        }
      }
      synchronized (beep_Lock) {
        beep_Lock.notify();
      }
    } catch (Exception ex) {
      Log.d("Debug", "Bi bak!" + ex.getMessage());
    }

  }

    private void Stop()
    {
      isStartPingPong = false;
    }

}


