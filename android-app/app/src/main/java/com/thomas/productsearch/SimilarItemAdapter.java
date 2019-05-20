package com.thomas.productsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
Adapter used in similar items recycler view
*/
public class SimilarItemAdapter extends RecyclerView.Adapter<SimilarItemAdapter.SimilarItemViewHolder> {

    private Context context;
    private List<SimilarItem> productList;
    private OnItemClickListener mListener;

    public SimilarItemAdapter(Context context, List<SimilarItem> productList) {
        this.context = context;
        this.productList = productList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public SimilarItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_similar_item, viewGroup, false);
        return new SimilarItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarItemViewHolder similarItemViewHolder, int i) {
        //getting the product of the specified position
        SimilarItem product = productList.get(i);
        //binding the data with the viewHolder views
        Picasso.with(context).load(product.getImageURL()).placeholder(R.drawable.default_image).into(similarItemViewHolder.productImage);
        similarItemViewHolder.titleText.setText(product.getTitle());
        String shippingCost = product.getShipping();
        if (Double.parseDouble(shippingCost) == 0.0) {
            shippingCost = "Free Shipping";
        } else {
            shippingCost = "$" + shippingCost;
        }
        similarItemViewHolder.shippingText.setText(shippingCost);
        similarItemViewHolder.daysLeftText.setText(product.getDaysLeft() + " Days Left");
        String coloredPrice = "<b><font color='#6200EE'>$" + product.getPrice() + "</font></b>";
        similarItemViewHolder.priceText.setText(Html.fromHtml(coloredPrice));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class SimilarItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView titleText, shippingText, daysLeftText, priceText;

        public SimilarItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.similarProductImage);
            titleText = itemView.findViewById(R.id.similarProductTitle);
            shippingText = itemView.findViewById(R.id.similarProductShipping);
            daysLeftText = itemView.findViewById(R.id.similarProductDaysLeft);
            priceText = itemView.findViewById(R.id.similarProductPrice);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
