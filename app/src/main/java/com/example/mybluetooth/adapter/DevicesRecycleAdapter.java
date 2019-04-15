package com.example.mybluetooth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mybluetooth.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DevicesRecycleAdapter extends RecyclerView.Adapter<DevicesRecycleAdapter.ViewHolder> {

    private List<String> deviceList;
    private OnClickListener onClickListener;

    public DevicesRecycleAdapter(List<String> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycleview_devices, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.deviceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                onClickListener.onClick(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String deviceName = deviceList.get(position);
        holder.tv_devices.setText(deviceName);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_devices;
        private View deviceView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceView = itemView;
            tv_devices = itemView.findViewById(R.id.item_recycle_devices_name);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }
}
