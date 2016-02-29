package com.example.baidumap;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.model.LatLng;
import com.example.baidumap.api.LBSSearch;
import com.example.baidumap.api.LBSStorage;
import com.example.baidumap.view.SettingItemViewBtn;
import com.example.baidumap.view.SettingItemViewEdit;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class UpDataActivity extends Activity implements OnClickListener {
	private static String mTAG = "UpDataActivity";

	SettingItemViewBtn PositionBtn;
	SettingItemViewEdit TitleEdit;
	Button upload_data_btn;
	ImageView updata_back;

	PositionEntity entity = new PositionEntity();
	private String myCentureLatitude;
	private String myCentureLongitude;
	private String myCentureAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		setContentView(R.layout.activity_updata);

		findView();
		initData();
	}

	/**
	 * 找控件
	 */
	private void findView() {
		PositionBtn = (SettingItemViewBtn) findViewById(R.id.myPosition);
		PositionBtn.setOnClickListener(this);

		TitleEdit = (SettingItemViewEdit) findViewById(R.id.myTitle);

		upload_data_btn = (Button) findViewById(R.id.myupload_data_btn);
		upload_data_btn.setOnClickListener(this);

		updata_back = (ImageView) findViewById(R.id.updata_back);
		updata_back.setOnClickListener(this);
	}

	/**
	 * 发起云存储请求
	 */
	private void storage() {
		LBSStorage.request(getRequestParams(),mHandler);
	}

	/**
	 * 设定云检索参数
	 * 
	 * @return
	 */
	private HashMap<String, String> getRequestParams() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			myCentureLatitude = Double.toString(PositionEntity.latitue);
			myCentureLongitude = Double.toString(PositionEntity.longitude);
			myCentureAddress = PositionEntity.address;
			
			Log.e(mTAG, myCentureLatitude+"-"+myCentureLongitude+"-"+myCentureAddress);
			
			String titleString = TitleEdit.getMyRightTextView().getText().toString();
			map.put("latitude", myCentureLatitude);
			map.put("longitude", myCentureLongitude);
			map.put("address", myCentureAddress);
			map.put("title", titleString);
			map.put("image", "null");
			map.put("zan", "0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 3:
				if (msg.obj == null) {
					Log.e(mTAG, "msg接收数据为空");
				} else {
					String result = msg.obj.toString();
					try {
						JSONObject json = new JSONObject(result);
						parser(json);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
	};

	/**
	 * 解析返回数据
	 * 
	 * @param json
	 */
	protected void parser(JSONObject json) {
		Infos infos = new Infos();
		List<Infos> list = infos.getReturnInfos();
		try {
			if (json.getInt("status") != 0) {
				Log.e(mTAG, "POST上传错误"+"status="+json.getInt("status") + "message="+json.getString("message"));
			} else {
				if (json.getString("message") == "成功") {
					Infos info = new Infos();
					info.setReturnid(json.getString("id"));
					list.add(info);
					Toast.makeText(this, "提交成功",Toast.LENGTH_SHORT).show();
				} else {
					return;
				}
			}

		} catch (Exception e) {
			Log.e(mTAG, "parser错误！");
		}
	}

	/**
	 * 初始化页面
	 */
	private void initData() {
		PositionBtn.setLeftText("确定当前位置");
		PositionBtn.setRightBitMap(R.drawable.image_more_subitem_arrow);
		TitleEdit.setLeftText("主题:");
		upload_data_btn.setText("提交");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myupload_data_btn:
			if(TitleEdit.getMyRightTextView().getText().toString() == null){
				Toast.makeText(this, "主题为空",Toast.LENGTH_SHORT).show();
			}else{
				storage();
			}
			break;
		case R.id.myPosition:
			Intent intent = new Intent();
			intent.setClass(UpDataActivity.this, UpDataLocationActivity.class);
			startActivity(intent);
			break;
		case R.id.updata_back:
			Intent intent2 = new Intent(UpDataActivity.this, MainActivity.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			setResult(1, intent2);
			finish();
			break;
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		try {
//			Bundle MarsBuddle = data.getExtras();
//			myCentureLatitude = MarsBuddle.getDouble("Latitude");
//			myCentureLongitude = MarsBuddle.getDouble("Longitude");
//			Log.e(mTAG,
//					Double.toString(myCentureLatitude) + "-"
//							+ Double.toString(myCentureLongitude));
//			
//		} catch (Exception e) {
//			Log.e(mTAG, "返回数据为空");
//		}
//	}
}
