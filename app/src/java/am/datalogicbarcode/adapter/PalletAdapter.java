package am.datalogicbarcode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import am.datalogicbarcode.R;
import am.datalogicbarcode.model.Pallet;

public class PalletAdapter extends ArrayAdapter<Pallet> {

    //storing all the names in the list
    private List<Pallet> palletList;

    //context object
    private Context context;

    //constructor
    public PalletAdapter(Context context, int resource, List<Pallet> pallets) {
        super(context, resource, pallets);
        this.context = context;
        this.palletList = pallets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        View listViewItem = inflater.inflate(R.layout.pallet_data_list_text, null, true);
        TextView tv_lot_no = (TextView) listViewItem.findViewById(R.id.tv_lot_no);
        TextView tv_roll_no = (TextView) listViewItem.findViewById(R.id.tv_roll_no);
        TextView tv_qty_no = (TextView) listViewItem.findViewById(R.id.tv_qty_no);
        TextView tv_article_no = (TextView) listViewItem.findViewById(R.id.tv_article_no);
//        TextView imageViewStatus = (TextView) listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current name
        Pallet pallet = palletList.get(position);
        //setting the name to textview
        tv_lot_no.setText(pallet.getLot_no());
        tv_roll_no.setText(pallet.getRoll_no());
        tv_qty_no.setText(pallet.getProd_qty());
        tv_article_no.setText(pallet.getArticle_no());
        return listViewItem;
    }
}




