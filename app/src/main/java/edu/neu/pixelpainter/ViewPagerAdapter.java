package edu.neu.pixelpainter;


import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private final List<ViewPagerItem>  viewPageItemArrayList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public ViewPagerAdapter(List<ViewPagerItem> viewPageItemArrayList) {
        this.viewPageItemArrayList = viewPageItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewPagerItem viewPagerItem =viewPageItemArrayList.get(position);
        holder.imageView.setImageResource(viewPagerItem.imageID);
        // Clear any existing color filter
        holder.imageView.clearColorFilter();
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // Set saturation to 0 to apply grayscale
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        if (viewPagerItem.gray && position != 0){
            holder.imageView.setColorFilter(filter);
        }
        holder.textView.setText(viewPagerItem.heading);
    }

    @Override
    public int getItemCount() {
        return viewPageItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.viewPageImage);
            textView = itemView.findViewById(R.id.viewPageImageText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
