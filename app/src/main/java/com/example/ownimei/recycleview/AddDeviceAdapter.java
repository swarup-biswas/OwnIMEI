package com.example.ownimei.recycleview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ownimei.R;
import com.example.ownimei.activity.UserProfile;
import com.example.ownimei.pojo.AddDeviceModel;

import java.util.ArrayList;

public class AddDeviceAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<AddDeviceModel> adddeviceModelList;
    private ViewHolder.LongPressInterface mLongPressInterface;



    public AddDeviceAdapter(ArrayList<AddDeviceModel> adddeviceModelList, ViewHolder.LongPressInterface mLongPressInterface) {

        this.adddeviceModelList = adddeviceModelList;
        this.mLongPressInterface = mLongPressInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_device_add, viewGroup, false);
        return new ViewHolder(view, mLongPressInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.getVhDeviceName().setText("" + adddeviceModelList.get(i).getSelectDevice());
        viewHolder.getVhModelName().setText("" + adddeviceModelList.get(i).getDeviceName());
        viewHolder.getVhIMEIoneName().setText("" + adddeviceModelList.get(i).getPhoneImeiOne());
        viewHolder.getVhIMEItwoName().setText("" + adddeviceModelList.get(i).getPhoneImeiTwo());
        viewHolder.getVhMacName().setText("" + adddeviceModelList.get(i).getMac());
        viewHolder.getVhPurchaseDate().setText("" + adddeviceModelList.get(i).getPurchaseDate());
        viewHolder.getVhStatusName().setText("" + adddeviceModelList.get(i).getStatus());

        //Visibility start
        if (adddeviceModelList.get(i).getSelectDevice().equals("Laptop")) {

            viewHolder.getiOne().setVisibility(View.GONE);
            viewHolder.getiTwo().setVisibility(View.GONE);

        } else if (adddeviceModelList.get(i).getSelectDevice().equals("Phone")) {

            viewHolder.getMac().setVisibility(View.GONE);
            if (adddeviceModelList.get(i).getPhoneImeiTwo().isEmpty()) {
                viewHolder.getiTwo().setVisibility(View.GONE);
            }
        }

        //Visibility end

        viewHolder.getTotalLinearView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLongPressInterface.onPressInterface(i);
            }
        });

        viewHolder.getTotalLinearView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mLongPressInterface.longPressInterface(i);

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return adddeviceModelList.size();
    }
}
