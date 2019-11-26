package com.example.lenovo.chreview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class BookListAdapter extends BaseAdapter {
    private Context context;
    private int itemLayoutId;
    private List<Book> books = new ArrayList<>();

    public BookListAdapter(Context context, int itemLayoutId, List<Book> books) {
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.books = books;
    }

    @Override
    public int getCount() {
        if (null != books)
            return books.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != books)
            return books.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (null != books)
            return position;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView){
            convertView = LayoutInflater.from(context).inflate(itemLayoutId,null);
            viewHolder = new ViewHolder();
            viewHolder.ivPhoto = convertView.findViewById(R.id.iv_photo);
            viewHolder.tvName = convertView.findViewById(R.id.tv_name);
            viewHolder.tvPrice = convertView.findViewById(R.id.tv_price);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //显示服务器端图片
        Book book = books.get(position);
        Glide.with(context)
                .load(Constant.BASE_IP+book.getImgPath())
                .into(viewHolder.ivPhoto);

        viewHolder.tvName.setText(book.getName());
        viewHolder.tvPrice.setText(book.getPrice()+"");
        return convertView;
    }

    private class ViewHolder{
        private ImageView ivPhoto;
        private TextView tvName;
        private TextView tvPrice;
    }
}
