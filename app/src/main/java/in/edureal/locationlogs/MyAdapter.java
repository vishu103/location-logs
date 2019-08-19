package in.edureal.locationlogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;
    private int colors;

    MyAdapter(List<ListItem> listItems, Context context, int colorsCode) {
        this.listItems = listItems;
        this.context = context;
        this.colors=colorsCode;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.single_log, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {

        if(colors==1){
            holder.logLayout.setBackgroundColor(Color.parseColor("#eb7070"));
            colors=2;
        }else if(colors==2){
            holder.logLayout.setBackgroundColor(Color.parseColor("#fec771"));
            colors=3;
        }else if(colors==3){
            holder.logLayout.setBackgroundColor(Color.parseColor("#e6e56c"));
            colors=4;
        }else{
            holder.logLayout.setBackgroundColor(Color.parseColor("#64e291"));
            colors=1;
        }

        final ListItem listItem=listItems.get(position);

        holder.logId.setText("Log ID - "+listItem.getLogId());
        holder.description.setText(listItem.getLogDate()+" @ "+listItem.getLogTime12());

        holder.logLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putInt("id",listItem.getLogId());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putFloat("latitude",(float)listItem.getLogLatitude());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putFloat("longitude",(float)listItem.getLogLongitude());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putString("logDate",listItem.getLogDate());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putString("logTime12",listItem.getLogTime12());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putString("logTime24",listItem.getLogTime24());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putString("address",listItem.getLogAddress());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putString("reason",listItem.getLogReason());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putString("mobile",listItem.getLogMobile());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putString("wifi",listItem.getLogWifi());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putInt("batteryRate",listItem.getLogBattery());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putString("climate",listItem.getLogClimate());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().putFloat("temperature",(float)listItem.getLogTemperature());
                SharedPreferenceSingleton.getInstance(context).getSpEditor().commit();
                Intent intent = new Intent (view.getContext(), SingleLog.class);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView logId;
        TextView description;
        LinearLayout logLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            logId=(TextView) itemView.findViewById(R.id.logId);
            description=(TextView) itemView.findViewById(R.id.description);
            logLayout=(LinearLayout) itemView.findViewById(R.id.logLayout);

        }
    }

}
