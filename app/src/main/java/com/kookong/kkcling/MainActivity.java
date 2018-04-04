package com.kookong.kkcling;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;

import java.util.List;

public class MainActivity extends ListActivity {
    BaseAdapter mAdapter;
    private DLNAPlayer mPlayer;
    private Device mDevice;
    private ControlPoint mControlPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new CustomAdapter(this, android.R.layout.simple_list_item_single_choice, DLNAManager.getInstance(this).getDeviceList());
        setListAdapter(mAdapter);
        mPlayer = new DLNAPlayer();
        DLNAManager.getInstance(this).setOnDeviceRefreshListener(new DLNAManager.DeviceRefreshListener() {
            @Override
            public void onDeviceRefresh() {
                if (mControlPoint == null) {
                    mControlPoint = DLNAManager.getInstance(MainActivity.this).getControlPoint();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDevice = (Device) mAdapter.getItem(position);
                mPlayer.setUp(mDevice, mControlPoint);
            }
        });
        mPlayer.addListener(new DLNAPlayer.EventListener() {
            @Override
            public void onPlay() {
                log("<-onPlay->");
            }

            @Override
            public void onGetMediaInfo(MediaInfo mediaInfo) {
                log("onGetMediaInfo:" + mediaInfo.getMediaDuration() + "," + mediaInfo.getPlayMedium() + "," + mediaInfo.getRecordMedium() + "," + mediaInfo.getCurrentURI());
            }

            @Override
            public void onPlayerError() {
                log("onPlayError");
            }

            @Override
            public void onTimelineChanged(PositionInfo positionInfo) {
                log("onTimelineChanged:" + positionInfo.getTrackDuration() + "," + positionInfo.getAbsTime() + "," + positionInfo.getRelTime());
            }

            @Override
            public void onSeekCompleted() {
                log("onSeekCompleted");
            }

            @Override
            public void onPaused() {
                log("onPaused");
            }

            @Override
            public void onMuteStatusChanged(boolean isMute) {
                log("onMuteStatusChanged:" + isMute);
            }

            @Override
            public void onVolumeChanged(int volume) {
                log("onVolumeChanged:" + volume);
            }

            @Override
            public void onStop() {
                log("onStop");
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                DLNAManager.getInstance(this).search();
                break;
            case R.id.play:
                String url = "/storage/emulated/0/test.mp4";
                mPlayer.play("香港卫视", url);
                break;
            case R.id.pause:
                mPlayer.pause();
                break;
            case R.id.stop:
                mPlayer.stop();
                break;
            case R.id.get_connection:
                mPlayer.getConnectionStatus();
                break;
            case R.id.get_media_info:
                mPlayer.getMediaInfo();
                break;
            case R.id.get_cur_pos:
                mPlayer.getCurrentPosition();
                break;
            case R.id.get_transport_info:
                mPlayer.getTransportInfo();
                break;
            case R.id.get_transport_actions:
                mPlayer.getCurrentTransportActions();
                break;
            case R.id.get_volume:
                mPlayer.getVolume();
                break;
            case R.id.set_volume:
                mPlayer.setVolume(30);
                break;
            case R.id.get_mute:
                mPlayer.getMute();
                break;
            case R.id.set_mute:
                mPlayer.setMute(true);
                break;
            default:
                break;
        }

    }

    class CustomAdapter extends ArrayAdapter<Device> {


        public CustomAdapter(Context context, int resource, List<Device> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_single_choice, parent, false);
                holder.textView = convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Device device = getItem(position);
            holder.textView.setText(device.getDetails().getFriendlyName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    private void log(String msg) {
        Log.i("==MainActivity==", msg);
    }

}
