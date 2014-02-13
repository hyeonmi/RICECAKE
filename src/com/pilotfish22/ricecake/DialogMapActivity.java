package com.pilotfish22.ricecake;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class DialogMapActivity extends MapActivity {
	Geocoder geocoder;
	EditText editAddress;
	AddressAdapter adapter;
	MapView mMapView;
	ListView listContents;
	MapController mMapController;
	Drawable marker;
	LinearLayout layout;
	Button btnSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_map);
		setTitle(R.string.title_map);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		geocoder = new Geocoder(getApplication(), Locale.getDefault());

		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		// mMapView.setSatellite(true);
		mMapView.setStreetView(true);

		layout = (LinearLayout) findViewById(R.id.layoutButton);
		listContents = (ListView) findViewById(R.id.listContents);
		editAddress = (EditText) findViewById(R.id.editAddress);

		marker = this.getResources().getDrawable(
				android.R.drawable.ic_menu_myplaces);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		// 주소 검색
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 인터넷
				ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				if (connec == null) {
					Toast.makeText(getApplicationContext(),
							R.string.toast_msg_internet, Toast.LENGTH_LONG)
							.show();
					return;
				}

				if (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
						.isConnected() == false
						&& connec.getNetworkInfo(
								ConnectivityManager.TYPE_MOBILE).isConnected() == false) {
					Toast.makeText(getApplicationContext(),
							R.string.toast_msg_internet, Toast.LENGTH_LONG)
							.show();
					return;
				}

				// Log.i("hyun", editAddress.getText().toString());
				if (!"".equals(editAddress.getText().toString())) {
					new GetGoogleMapTask().execute();
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.hint_address, Toast.LENGTH_LONG).show();
				}

			}
		});

		// 주소 선택시
		listContents.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				listContents.setVisibility(View.INVISIBLE);
				mMapView.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);

				Address address = (Address) view.getTag();
				int la = (int) (address.getLatitude() * 1000000);
				int lo = (int) (address.getLongitude() * 1000000);

				Log.i("hyun", String.valueOf(la));
				Log.i("hyun", String.valueOf(lo));

				GeoPoint geopoint = new GeoPoint(la, lo);
				mMapController = mMapView.getController();
				mMapController.animateTo(geopoint);

				mMapController.setZoom(16);
				mMapController.setCenter(geopoint);

				// 주소가 여러개인 경우 마커가 여러개 생기는 단점을 보완
				if (mMapView.getOverlays().size() > 0) {
					mMapView.getOverlays().remove(0);
				}

				placeMarker(la, lo);

			}

		});

		// 사진으로 저장
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapView.setDrawingCacheEnabled(true);
				Bitmap bm = mMapView.getDrawingCache();
				FileOutputStream f = null;
				try {
					f = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/"+Constant.TEMP_PHOTO_FILE);
					bm.compress(Bitmap.CompressFormat.PNG, 100, f);
				} catch (FileNotFoundException e) {
					Log.e("MapAct",e.getMessage());
				}finally{
					try {
						f.close();
					} catch (IOException e) {
						Log.e("MapAct",e.getMessage());
					}
					bm.recycle();
					bm=null;
					setResult(RESULT_OK);
					finish();
				}
				
				

			}
		});

		// 취소
		Button btnCancle = (Button) findViewById(R.id.btnCancle);
		btnCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	// 구글 지도 가져오기
	public class GetGoogleMapTask extends AsyncTask<String, Integer, Integer> {
		@Override
		protected void onPreExecute() {
			showDialog(1);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// super.onPostExecute(result);

			dismissDialog(1);

			if (result == 1) {
				mMapView.setVisibility(View.INVISIBLE);
				btnSave.setVisibility(View.GONE);
				listContents.setVisibility(View.VISIBLE);
				listContents.setAdapter(adapter);

			} else if (result == 0) {

				Toast.makeText(getApplicationContext(),
						R.string.toast_msg_address, Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(getApplicationContext(),
						R.string.toast_msg_system, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			return SearchLocation(editAddress.getText().toString());
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {

		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(getResources().getText(R.string.loading));
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;

	}

	/*
	 * 지도 표시
	 */
	class InterestingLocations extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Drawable marker;
		private OverlayItem myOverlayItem;

		public InterestingLocations(Drawable defaultMarker, int LatitudeE6,
				int LongitudeE6) {
			super(defaultMarker);

			this.marker = defaultMarker;
			// create locations of interest
			GeoPoint myPlace = new GeoPoint(LatitudeE6, LongitudeE6);
			myOverlayItem = new OverlayItem(myPlace, "My Place", "My Place");
			locations.add(myOverlayItem);

			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return locations.get(i);
		}

		@Override
		public int size() {
			return locations.size();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			mapView.getOverlays().remove(0);
			mMapController.animateTo(p);
			placeMarker(p.getLatitudeE6(), p.getLongitudeE6());
			return true;
		}

	}

	/*
	 * 위치 마커로 표시
	 */
	private void placeMarker(int markerLatitude, int markerLongitude) {
		Drawable marker = getResources().getDrawable(
				android.R.drawable.ic_menu_myplaces);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		mMapView.getOverlays().add(
				new InterestingLocations(marker, markerLatitude,
						markerLongitude));
	}

	/*
	 * 주소 검색
	 */
	public int SearchLocation(String search) {
		List<Address> addressList = null;
		try {
			addressList = geocoder.getFromLocationName(search, 10);
			if (addressList.size() > 0) {
				adapter = new AddressAdapter(this, addressList);
				return 1;
			} else {
				return 0;
			}
		} catch (IOException e) {
			Log.i("hyun", e.getMessage());
			return 3;
		}

	}

	/*
	 * 주소 어댑터
	 */
	class AddressAdapter extends ArrayAdapter<Address> {
		List<Address> listAddress;

		public AddressAdapter(Context context, List<Address> objects) {
			super(context, 0, objects);
			listAddress = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView text = new TextView(getApplicationContext());
			text.setText(listAddress.get(position).getAddressLine(0).toString());
			text.setTag(listAddress.get(position));
			return text;
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
