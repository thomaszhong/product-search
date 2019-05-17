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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener mListener;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onCartClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_item, viewGroup, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i) {
        //getting the product of the specified position
        Product product = productList.get(i);
        //binding the data with the viewHolder views
        Picasso.with(context).load(product.getImage()).placeholder(R.drawable.default_image).into(productViewHolder.productImage);
        Picasso.with(context).load(product.getCartId()).into(productViewHolder.cartImage);
        productViewHolder.titleText.setText(product.getShortTitle().toUpperCase());
        productViewHolder.zipCodeText.setText("Zip: " + product.getZipCode());
        productViewHolder.shippingText.setText(product.getShipping());
        productViewHolder.conditionText.setText(product.getCondition());
        String coloredPrice = "<b><font color='#6200EE'>$" + product.getPrice() + "</font></b>";
        productViewHolder.priceText.setText(Html.fromHtml(coloredPrice));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, cartImage;
        TextView titleText, zipCodeText, shippingText, conditionText, priceText;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            cartImage = itemView.findViewById(R.id.cartImage);
            titleText = itemView.findViewById(R.id.titleText);
            zipCodeText = itemView.findViewById(R.id.zipCodeText);
            shippingText = itemView.findViewById(R.id.shippingText);
            conditionText = itemView.findViewById(R.id.conditionText);
            priceText = itemView.findViewById(R.id.priceText);
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
            cartImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onCartClick(position);
                    }
                }
            });
        }
    }
}
